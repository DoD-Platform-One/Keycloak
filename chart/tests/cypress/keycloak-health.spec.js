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
})
