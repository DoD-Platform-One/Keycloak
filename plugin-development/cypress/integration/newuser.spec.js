describe('Test new user actions', () => {
    // TO DO - does this need to be beforeEach or does the auth context persist?
    before(() => {
        // TO DO - use a request, don't use the UI for each login
        // authenticate as admin
        // const username = 'admin'
        // const password = 'pass'

        // cy.request({
        //     method: 'POST',
        //     url: 'http://localhost:8080/auth/realms/master/protocol/openid-connect/token',
        //     body: {
        //         username: username,
        //         password: password
        //     },
        //     form: true
        // })

        cy.visit('/auth/admin')

        cy.url().should('include', '/auth/realms')

        // enter creds
        cy.get('input[name=username]').type('admin').should('have.value', 'admin')
        cy.get('input[name=password]').type('pass').should('have.value', 'pass')

        // test using MFA Log In
        cy.get('#kc-login').click()
    })

    it('Creates a new user', () => {
        cy.visit('/auth/admin/master/console/#/realms/baby-yoda/users')
        cy.get('#createUser').click()

        cy.get('input[name=username]').type('testuser')
        cy.get('input[name=email]').type('test@sample.com')
        cy.get('input[name=firstName]').type('First')
        cy.get('input[name=lastName]').type('Last')

        let user_actions = ['Configure OTP']

        // click the "Required User Actions" dropdown to make the options appear
        cy.get('#s2id_reqActions').click()

        // TO DO - actions: configure otp, terms and conditions, update password, update profile, verify email

        cy.get('.select2-results-dept-0').each((item) => {
            item.click()
        })

        // cy.contains("Configure OTP").then((option) => {
        //     cy.wrap(option).click({force: true});  // force b/c this select is hidden
        // })

        // cy.contains("Update Password").then((option) => {
        //     cy.wrap(option).click({force: true});  // this select is hidden
        //     // option[0].click()
        // })

    })
})