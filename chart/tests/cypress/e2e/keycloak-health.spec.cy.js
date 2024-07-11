describe('Keycloak Healthcheck', function () {
  it('Check if Keycloak admin console is accessible', function() {
    cy.visit(Cypress.env('url') + '/auth/admin')
    cy.get('div[id="kc-header-wrapper"]').should('be.visible')
  })

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
    cy.title().should('eq', 'Keycloak Administration Console');

    cy.visit(Cypress.env('url') + '/auth/admin/master/console/#/baby-yoda/users');
    cy.wait(2000);
    cy.get('body').should('contain', 'Cypress');

    if (Cypress.env('keycloak_test_enable')) {
      cy.visit(Cypress.env('url') + '/auth/realms/baby-yoda/account')
      cy.wait(2000)

      cy.performKeycloakLogin(Cypress.env('tnr_username'), Cypress.env('tnr_password'))
      cy.wait(500)
    }
  })
})
