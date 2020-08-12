describe('Login via MFA', () => {
    it('Logs the admin user in to Keycloak', () => {
        cy.visit('auth/admin')

        cy.url().should('include', '/auth/realms')

        // enter creds
        cy.get('input[name=username]').type('admin').should('have.value', 'admin')
        cy.get('input[name=password]').type('pass').should('have.value', 'pass')

        // test using MFA Log In
        cy.get('#kc-login').click()

        // assert that admin is logged into Baby Yoda realm
        cy.url().should('include', 'realms/baby-yoda')
    })

    it('Asserts that a non-logged-in user cannot access the console', () => {
        cy.visit('/auth/admin/master/console')

    })
})