describe('Test custom registration', () => {
    it('Tests the new user sign-up form with valid data', () => {
        cy.visit('/auth/realms/baby-yoda/protocol/openid-connect/registrations?client_id=account&response_type=code&invite=54r3GvqpIgkPHNgcoVLK4nuEWmaepVn%2FByhHVj7XdOc%3D')

        cy.get('input[name=firstName]').type('First').should('have.value', 'First')
        cy.get('input[name=lastName]').type('Last').should('have.value', 'Last')
        cy.get('input[name=user\\.attributes\\.organization]').type('unit').should('have.value', 'unit')
        cy.get('input[name=username]').type('username').should('have.value', 'username')
        cy.get('input[name=email]').type('email@test.mil').should('have.value', 'email@test.mil')
        cy.get('textarea[name=user\\.attributes\\.notes]').type('notes for access request').should('have.value', 'notes for access request')

        cy.get('select[name=user\\.attributes\\.affiliation]').select('US Air Force')
        cy.get('select[name=user\\.attributes\\.rank]').select('E-1')

        cy.get('input[name=password]').type('Password123$%')
        cy.get('input[name=password-confirm]').type('Password123$%')

        cy.get('#kc-form-buttons').children().click()

        // TO DO - OTP. Consider: https://github.com/NoriSte/cypress-otp
    })

    it('Tests the new user sign-up form with valid data', () => {
        cy.visit('/auth/realms/baby-yoda/protocol/openid-connect/registrations?client_id=account&response_type=code')

        cy.get('input[name=firstName]').type('First').should('have.value', 'First')
        cy.get('input[name=lastName]').type('Last').should('have.value', 'Last')
        cy.get('input[name=user\\.attributes\\.organization]').type('unit').should('have.value', 'unit')
        cy.get('input[name=username]').type('username').should('have.value', 'username')
        cy.get('input[name=email]').type('email@test.mil').should('have.value', 'email@test.mil')
        cy.get('textarea[name=user\\.attributes\\.notes]').type('notes for access request').should('have.value', 'notes for access request')

        cy.get('select[name=user\\.attributes\\.affiliation]').select('US Air Force')
        cy.get('select[name=user\\.attributes\\.rank]').select('E-1')

        cy.get('input[name=password]').type('Password123$%')
        cy.get('input[name=password-confirm]').type('Password123$%')

        cy.get('#kc-form-buttons').children().click()

        // TO DO - OTP. Consider: https://github.com/NoriSte/cypress-otp
    })
})