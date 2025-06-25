describe('Smoke test', function() {
    it('should hit login page', function() {
        expect(true).to.equal(true)
        cy.visit('https://127.0.0.1:8443/auth/admin')
    })
})