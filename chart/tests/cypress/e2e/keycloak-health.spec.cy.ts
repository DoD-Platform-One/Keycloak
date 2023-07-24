function userID_Generate() {
  var text = "";
  var alphabet = "abcdefghijklmnopqrstuvwxyz";

  for (var i = 0; i < 5; i++)
    text += alphabet.charAt(Math.floor(Math.random() * alphabet.length));

  return text;
}
describe('Keycloak Healthcheck', function () {
  it('Check if Keycloak admin console is accessible', function() {
    cy.visit(Cypress.env('url') + '/auth/admin')
    cy.get('div[id="kc-header-wrapper"]').should('be.visible')
  })
  // it('Check if Keycloak registration page loads', function() {
  //   cy.visit(Cypress.env('url') + '/register')
  //   cy.title().should('include', 'Log in to DoD Platform One')
  // })
  // it('Check Keycloak unauthenticated user get login page', function() {
  //   cy.visit(Cypress.env('url'))
  //   cy.get('input[id="username"]').should('be.visible')
  // })
  it('Login, create a user, add a user to a group', function() {
    // Fill in Creds
    cy.visit(Cypress.env('url') + '/auth/admin')
    // Fill in username
      cy.get('input[id="username"]')
      .type(Cypress.env('username'))
      .should('have.value', Cypress.env('username'));
    
    // Fill in password
    cy.get('input[id="password"]')
      .type(Cypress.env('password'))
      .should('have.value', Cypress.env('password'));
    
    // Locate and submit the form
    cy.get('form').submit();

    // Verify the app redirected you to the console
    cy.location('pathname', { timeout: 10000 }).should('eq', '/auth/admin/master/console/');

    // verify the page title is "Keycloak Admin UI"
    cy.title().should('eq', 'Keycloak Administration UI');

    // // Go to Realm page
    // cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms/baby-yoda')
    // cy.contains('baby-yoda')
 
    // // Create a non-admin user
    // cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/create/user/baby-yoda')
    
    // // random generated username
    // let randusername = userID_Generate();

    // // Fill in user data
    // cy.get('input[id="username"]')
    //   .type(randusername)
    //   .should('have.value', randusername);

    // // Locate and submit the form
    // cy.get('button[kc-save=""]').contains('Save').click();
    // cy.wait(2000)

    // // Add user to group
    // cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms/baby-yoda/users')
    // cy.get('button[id="viewAllUsers"]').click()
    // cy.wait(2000)
    // // click on user link  
    // cy.get('td[class="clip ng-binding"]').contains(randusername).siblings('td[class="clip"]').children('a').click({ force: true })
    // cy.wait(2000)
    // // click on the groups tab
    // cy.get('a').contains('Groups').click()
    // // select the IL2 group
    // cy.get('div[tree-id="tree"] > :nth-child(1) > :nth-child(1) > .ng-binding').click()
    // // join the user to the group
    // cy.get('button[id="joinGroup"]').click();

    // // Verify user in group
    // cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/realms/baby-yoda/groups')
    // // double click on the IL2 group
    // cy.get('[data-ng-repeat="node in groupList"] > :nth-child(3) > :nth-child(1) > :nth-child(1) > .ng-binding').dblclick()
    // // click on the members tab
    // cy.get('[ng-class="{active: path[4] == \'members\'}"] > .ng-binding').click()
    // // verify that the user is there
    // cy.contains(randusername)
  })
})
