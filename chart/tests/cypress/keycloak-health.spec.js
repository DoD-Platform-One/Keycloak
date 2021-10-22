function userID_Generate() {
  var text = "";
  var alphabet = "abcdefghijklmnopqrstuvwxyz";

  for (var i = 0; i < 5; i++)
    text += alphabet.charAt(Math.floor(Math.random() * alphabet.length));

  return text;
}
describe('Keycloak Healthcheck', function () {
  it('Check if Keycloak admin console is accessible', function() {
    Cypress.config('chromeWebSecurity',false);
    cy.visit(Cypress.env('url') + '/auth/admin')
    cy.get('div[id="kc-header-wrapper"]').should('be.visible')
  })
  it('Check if Keycloak registration page loads', function() {
    Cypress.config('chromeWebSecurity',false);
    cy.visit(Cypress.env('url') + '/register')
    cy.title().should('include', 'Log in to DoD Platform One')
  })
  it('Check Keycloak unauthenticated user get login page', function() {
    Cypress.config('chromeWebSecurity',false);
    cy.visit(Cypress.env('url'))
    cy.get('input[id="username"]').should('be.visible')
  })
  it('Login, create a user, add a user to a group', function() {
    // Fill in Creds
    cy.visit(Cypress.env('url') + '/auth/admin')
    // Fill in Username
      cy.get('input[id="username"]')
      .type(Cypress.env('username'))
      .should('have.value', Cypress.env('username'));
    
    // Fill in Username
    cy.get('input[id="password"]')
      .type(Cypress.env('password'))
      .should('have.value', Cypress.env('password'));
    
    // Locate and submit the form
    cy.get('form').submit();

    // Verify the app redirected you to the console
    cy.location('pathname', { timeout: 10000 }).should('eq', '/auth/admin/master/console/');

    // verify the page title is "Keycloak Admin Console"
    cy.title().should('eq', 'Keycloak Admin Console');

    // Go to Realm page
    cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms')
      cy.contains('baby-yoda').click()

    // Verify the app redirected to baby-yoda realm
    cy.location('href', { timeout: 10000 }).should('contain', '/auth/admin/master/console/#/realms/baby-yoda')

    // don't need to check count of clients. The test will break when new clients are added.
    // // Check for clients in  baby-yoda realm
    // cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms/baby-yoda/clients')
    //   cy.wait(1000)
    //   cy.get('.datatable').find('tbody').find('tr').should('have.length', 7); // also includes last tr which is class ng-hide

    // Create a non-admin user
    cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/create/user/baby-yoda')
    
    // random generated username
    let randusername = userID_Generate();
    // Fill in user data
    cy.get('input[id="username"]')
      .type(randusername)
      .should('have.value', randusername);

    // Locate and submit the form
    // cy.get('button[type="submit"],[class="btn-primary"]').click();
    cy.get('[data-ng-show="create && access.manageUsers"] > .btn-primary').click()
      cy.wait(2000)

    // Add user to group
    cy.get('[ng-class="{active: path[4] == \'groups\'}"] > .ng-binding').click()
    cy.get('[tree-id="tree"] > :nth-child(1) > :nth-child(1) > .ng-binding').click()
    cy.get('button[id="joinGroup"]').click();

    // Verify user in group
    cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms/baby-yoda/groups')
    cy.get('[data-ng-repeat="node in groupList"] > :nth-child(3) > :nth-child(1) > :nth-child(1) > .ng-binding').dblclick()
    cy.get('[ng-class="{active: path[4] == \'members\'}"] > .ng-binding').click()
    cy.contains(randusername)
  })
})
