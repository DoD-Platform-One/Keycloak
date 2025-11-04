describe("Keycloak", function () {
  describe("The admin console", function () {
    it("should be accessible", function () {
      cy.visit(Cypress.env("url") + "/auth/admin");
    });
    it("should render the header", function () {
      cy.get('div[id="kc-header-wrapper"]').should("be.visible");
    });
    it("should allow login", function () {
      cy.get('input[id="username"]')
        .type(Cypress.env("username"))
        .should("have.value", Cypress.env("username"));

      cy.get('input[id="password"]')
        .type(Cypress.env("password"))
        .should("have.value", Cypress.env("password"));

      cy.get("form").submit();

      cy.location("pathname", { timeout: 10000 }).should(
        "eq",
        "/auth/admin/master/console/",
      );

      cy.title().should("eq", "Keycloak Administration Console");
    });
    it("should allow enabling the test user", function () {
      cy.visit(
        Cypress.env("url") + "/auth/admin/master/console/#/baby-yoda/users",
      );
      cy.wait(5000);
      cy.get("body").should("contain", "Cypress");

      if (Cypress.env("keycloak_test_enable")) {
        cy.visit(Cypress.env("url") + "/auth/realms/baby-yoda/account");
        cy.wait(2000);

        cy.performKeycloakLogin(
          Cypress.env("tnr_username"),
          Cypress.env("tnr_password"),
        );
        cy.wait(500);
      }
    });
  });
});
