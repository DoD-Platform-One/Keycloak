import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    env: {
      url: "https://keycloak.bigbang.dev"
    },
    supportFile: false,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
});