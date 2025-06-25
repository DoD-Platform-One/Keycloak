# Keycloak Package Chart Maintenance

This guide covers updating, testing and maintaining the Big Bang Keycloak
package.

## Overview

Big Bang's Keycloak implementation:

- Wraps the upstream codecentric keycloakx chart
- Bundles bitnami postgres as a dependency
- Includes the custom
  [p1-auth-plugin](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin)
  for DoD-specific auth requirements

## Update Process

1. **Research**: Review
   [Keycloak release notes](https://www.keycloak.org/docs/latest/release_notes/index.html)
   for breaking changes

2. **Update Dependencies**:
   ```sh
   helm dependency update ./chart
   ```

3. **Update Chart Files**:
   - **Chart.yaml**: Update version numbers and annotations
     ```yaml
     name: keycloak
     version: XX.X.X-bb.X
     appVersion: XX.X.X
     dependencies:
       - name: postgresql
         version: 16.6.7
         repository: oci://registry-1.docker.io/bitnamicharts
         condition: postgresql.enabled
       - name: gluon
         version: "X.X.X"
     # Update annotations section accordingly
     ```
   - **CHANGELOG.md**: Document changes
   - **README.md**: Update using
     [gluon library script](https://repo1.dso.mil/big-bang/apps/library-charts/gluon/-/blob/master/docs/bb-package-readme.md)

4. **Plugin Management**:
   - Build/publish the
     [p1-auth-plugin](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md)
     (use test label initially)
   - After testing, create an official release tag
   - Publish to IronBank following
     [these instructions](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md#publish-plugin-image-to-iron-bank)

5. **Testing**: Test both clean install and upgrade scenarios

6. **BigBang MR**: Update with new plugin init-container tag

## Testing Environment Setup

### Prerequisites

```sh
# Set credentials (can be added to ~/.bashrc)
export REGISTRY_USERNAME='<your_username>'
export REGISTRY_PASSWORD='<your_password>'

# Path to BigBang repo (no trailing slash)
export BIGBANG_REPO_DIR=~/repos/bigbang
```

### Cluster Setup

1. **Create dev cluster**:
   ```sh
   "${BIGBANG_REPO_DIR}"/docs/assets/scripts/developer/k3d-dev.sh -a
   ```

2. **Configure kubectl**:
   ```sh
   export KUBECONFIG=~/.kube/<your_kubeconfig_file>
   ```

3. **Deploy Flux**:
   ```sh
   "${BIGBANG_REPO_DIR}"/scripts/install_flux.sh -u "${REGISTRY_USERNAME}" -p "${REGISTRY_PASSWORD}"
   ```

### Deploy BigBang

```sh
helm upgrade -i bigbang ${BIGBANG_REPO_DIR}/chart \
  -n bigbang \
  --create-namespace \
  --set registryCredentials.username=${REGISTRY_USERNAME} \
  --set registryCredentials.password=${REGISTRY_PASSWORD} \
  -f https://repo1.dso.mil/big-bang/bigbang/-/raw/master/tests/test-values.yaml \
  -f https://repo1.dso.mil/big-bang/bigbang/-/raw/master/chart/ingress-certs.yaml \
  -f docs/dev-overrides/enable-sso.yaml
```

This deploys Keycloak, Authservice, Istio stack, and several applications with
SSO enabled.

## Configuration

### Override File Options

1. **Branch/Tag Testing**:
   ```yaml
   addons:
     keycloak:
       git:
         tag: null
         branch: "renovate/ironbank" # Example branch
   ```

2. **Plugin Image**:
   ```yaml
   addons:
     keycloak:
       values:
         upstream:
           extraInitContainers: |-
             - name: plugin
               image: registry1.dso.mil/ironbank/big-bang/p1-keycloak-plugin:3.5.7
   ```
   Alternatively, you can modify `enable-sso.yaml` to specify the plugin you're
   testing.

3. **SSO SAML Metadata**:
   ```yaml
   sso:
     saml:
       # curl -fsSL https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/protocol/saml/descriptor for this
       metadata: ""
   ```

## Test Cases

1. **Admin Console**: https://keycloak.dev.bigbang.mil/auth/admin/
   (admin/password)

2. **User Management**:
   - Create test users (CAC and non-CAC)
   - For non-CAC: use incognito window or disconnect CAC reader
   - Set email as verified via admin console
   - Note: A `cypress` user is created automatically in test deployments

3. **SSO Integration**:
   - Test with Grafana (https://grafana.dev.bigbang.mil)
   - Test with Mattermost (https://chat.dev.bigbang.mil)
   - Test with Sonarqube (https://sonarqube.dev.bigbang.mil)

4. **Custom Forms**:
   - Account: https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/
   - Password:
     https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/password
   - TOTP: https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/totp
   - Registration: https://keycloak.dev.bigbang.mil/register

5. **Integration Testing**: Follow
   [Big Bang testing guide](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/developer/test-package-against-bb.md)
