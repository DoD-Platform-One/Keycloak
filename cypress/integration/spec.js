describe('fake test', function() {
  it('always passes', function() {
  	expect(true).to.equal(true)
  	cy.visit('http://127.0.0.1:8080/auth/admin')
  })
})