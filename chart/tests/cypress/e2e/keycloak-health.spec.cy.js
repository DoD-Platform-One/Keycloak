describe("Keycloak", function () {
  describe("The admin console", function () {
    it("should be accessible", function () {
      cy.env(["url"]).then(({ url }) => {
        cy.visit(url + "/auth/admin");
      });
    });
    it("should render the header", function () {
      cy.get('div[id="kc-header-wrapper"]').should("be.visible");
    });
    it("should allow login", function () {
      cy.env(["username", "password"]).then(({ username, password }) => {
        cy.get('input[id="username"]')
          .type(username)
          .should("have.value", username);

        cy.get('input[id="password"]')
          .type(password)
          .should("have.value", password);
      });

      cy.get("form").submit();

      cy.location("pathname", { timeout: 10000 }).should(
        "eq",
        "/auth/admin/master/console/",
      );

      cy.title().should("eq", "Keycloak Administration Console");
    });
    it("should allow enabling the test user", function () {
      cy.env(["url", "keycloak_test_enable", "tnr_username", "tnr_password"]).then(
        ({ url, keycloak_test_enable, tnr_username, tnr_password }) => {
          cy.visit(url + "/auth/admin/master/console/#/baby-yoda/users");
          cy.wait(5000);
          cy.get("body").should("contain", "Cypress");

          if (keycloak_test_enable) {
            cy.visit(url + "/auth/realms/baby-yoda/account");
            cy.wait(2000);

            cy.performKeycloakLogin(tnr_username, tnr_password);
            cy.wait(500);
          }
        },
      );
    });
  });
});
