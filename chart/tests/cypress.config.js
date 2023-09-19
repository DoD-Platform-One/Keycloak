const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    env: {
      baseUrl: "https://keycloak.bigbang.dev"
    },
    video: true,
    screenshot: true,
    supportFile: false,
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
})