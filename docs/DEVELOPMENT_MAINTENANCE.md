# How to update the Keycloak Package chart
Big Bang makes modifications to the upstream Codecentric helm chart. The upstream Keycloak image from IronBank is used in conjuction with a custom Keycloak plugin. The custom P1 plugin contains custom authz and authn code, custom self-registration theme, and automatic joining of groups based on certian registration information like the use of a DoD CAC. The plugin code is hosted at [https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin). Platform One custom plugin artifact image is hosted in Iron Bank at registry1.dso.mil/ironbank/opensource/keycloak/keycloak:X.X.X

*Note: Be aware that there are currently two versions of Keycloak. One is the legacy version that uses Wildfly for the application server. The other version is the new one using Quarkus. Big Bang has migrated to the new Quarkus version. The images in Iron Bank have tag without the "legacy" `X.X.X`.*

1. **Read Release Notes:** from the upstream [Keycloak documentation](https://www.keycloak.org/docs/latest/release_notes/index.html). Carefully review the release notes for the new Keycloak version to understand any breaking changes or required manual upgrade steps.

1. **Run kpt update:** Identify latest upstream chart version for our target keycloak version: https://github.com/codecentric/helm-charts/tree/master/charts/keycloakx
    ```
    kpt pkg update chart@keycloakx-<version> --strategy alpha-git-patch
    ```
    Note that the upstream chart is named `keycloakx`, but we leave our chart named `keycloak` to align with current umbrella config.

1. **Update Chart.yaml:** Update the chart/Chart.yaml file with the appropriate versions for Keycloak, dependencies, and annotations.
    ```yaml
    name: keycloak
    version: XX.X.X-bb.X
    appVersion: XX.X.X
    dependencies:
      - name: postgresql
        version: XX.X.XX
        repository: file://./deps/postgresql
        condition: postgresql.enabled
      - name: gluon
        version: "X.X.X"
    annotations:
      bigbang.dev/applicationVersions: |
        - Keycloak: XX.X.X
      helm.sh/images: |
        - name: keycloak
        image: registry1.dso.mil/ironbank/opensource/keycloak/keycloak:XX.X.X
        - name: postgreslXX
        condition: postgresql.enabled
        image: registry1.dso.mil/ironbank/opensource/postgres/postgresqlXX:XX.XX
        - name: base
        condition: bbtests.enabled
        image: registry1.dso.mil/ironbank/big-bang/base:X.X.X
    ```

1. **Update CHANGELOG.md:** Add an entry describing the upgrade, including the new Keycloak, p1-keycloak-plugin and chart versions.

1. **Update README.md:** Update the file following the [gluon library script](https://repo1.dso.mil/big-bang/apps/library-charts/gluon/-/blob/master/docs/bb-package-readme.md) guidelines noting any additional chart changes you make during development testing.

1. **Run Helm Dependency Update:** Execute `helm dependency update ./chart` to update the chart archives and generate a new requirements.lock file. Commit both the archives and the lock file.
    ```bash
    helm dependency update ./chart
    ```
1. **Build and Publish Plugin Image:** Follow the [instructions](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md) in the P1 plugin repository to build and publish a new plugin image to the IronBank container registry. Use a test label for the image initially.

1. **Test Keycloak and Plugin:** Conduct thorough testing of Keycloak, the custom plugin, and end-to-end SSO using a full k8s deployment. Refer to the [Testing with custom P1 plugin](#testing-with-custom-p1-plugin) section for detailed instructions.
    - Test the admin console
    - Test the custom user forms
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/password
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/totp
        - https://keycloak.dev.bigbang.mil:8443/register
    - Test registering a user with a CAC verifying that the user is automatically added to the IL2 group.
    - Test logging in from the account page.
    - Test registering a regular user with username and password.
    - Test login at the account page with OTP.

1. **Update Documentation:** After successful testing, update all documentation with the new Keycloak and plugin versions.
1. **Create Release Tag:** Create an official semver release tag in the P1 plugin repository and monitor the mirrored Party Bus IL2 pipeline for successful completion.

1. **Publish Official Plugin Image:** Publish the official plugin image to IronBank, following the provided [instructions](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md#publish-plugin-image-to-iron-bank).

1. **Update BigBang MR:** Update the `tests/test-values.yaml` file in the BigBang MR with the new plugin init-container tag, using either the test image or the official IronBank image.


# Testing Keycloak with custom P1 plugin

You should perform these steps on both a clean install and an upgrade from BB master.

## Update overrides

Update `docs/dev-overrides/keycloak-testing` as needed:

### Branch/Tag Config

If you'd like to install from a specific branch or tag, then the code block under keycloak needs to be uncommented and used to target your changes.

For example, this would target the `renovate/ironbank` branch.

```yaml
addons:
  keycloak:
  # Add git branch or tag to test against a specific branch or tag instead of the current umbrella tag.
  # Tag takes precedence and must therefore be set to null if you wish to reference a branch
  git:
    tag: null
    branch: "renovate/ironbank"
```

### Plugin image version

Update to reference the appropriate plugin image version. This might be an ironbank image if doing final testing, or a bigbang-staging image for development testing.

```yaml
addons:
  keycloak:
    values:
      extraInitContainers: |-
        - name: plugin
          image: registry1.dso.mil/ironbank/big-bang/p1-keycloak-plugin:3.5.0
          ...
```

### sso.saml.metadata for Sonarqube
```yaml
sso:
  saml:
    # Required for Sonarqube (or other SAML apps) SSO to work, must update after keycloak is deployed and run a helm upgrade
    # Fill this in with the result from `curl https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/protocol/saml/descriptor ; echo`
    metadata: ''
```

## Cluster setup

⚠️ Always make sure your local bigbang repo is current before deploying.

1. Export your Ironbank/Harbor credentials (this can be done in your `~/.bashrc` or `~/.zshrc` file if desired). These specific variables are expected by the `k3d-dev.sh` script when deploying metallb, and are referenced in other commands for consistency:

```sh
export REGISTRY_USERNAME='<your_username>'
export REGISTRY_PASSWORD='<your_password>'
```

1. Export the path to your local bigbang repo (without a trailing `/`):

⚠️ Note that wrapping your file path in quotes when exporting will break expansion of `~`.

```sh
export BIGBANG_REPO_DIR=<absolute_path_to_local_bigbang_repo>
```

e.g.

```sh
export BIGBANG_REPO_DIR=~/repos/bigbang
```

1. Run the [k3d_dev.sh](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/scripts/developer/k3d-dev.sh) script to deploy a dev cluster (`-a` flag required to create a k3d cluster with metalLB and a passthrough gateway for Keycloak):

    ```sh
    "${BIGBANG_REPO_DIR}"/docs/assets/scripts/developer/k3d-dev.sh -a
    ```

1. Export your kubeconfig:

    ```sh
    export KUBECONFIG=~/.kube/<your_kubeconfig_file>
    ```

    e.g.

    ```sh
    export KUBECONFIG=~/.kube/Sam.Sarnowski-dev-config
    ```

1. [Deploy flux to your cluster](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/scripts/install_flux.sh):

```sh
"${BIGBANG_REPO_DIR}"/scripts/install_flux.sh -u "${REGISTRY_USERNAME}" -p "${REGISTRY_PASSWORD}"
```

## Deploy Bigbang

From the root of this repo, run the following deploy command:

  ```sh
  helm upgrade -i bigbang ${BIGBANG_REPO_DIR}/chart/ -n bigbang --create-namespace \
  --set registryCredentials.username=${REGISTRY_USERNAME} --set registryCredentials.password=${REGISTRY_PASSWORD} \
  -f https://repo1.dso.mil/big-bang/bigbang/-/raw/master/tests/test-values.yaml \
  -f https://repo1.dso.mil/big-bang/bigbang/-/raw/master/chart/ingress-certs.yaml \
  -f docs/dev-overrides/minimal.yaml \
  -f docs/dev-overrides/keycloak-testing.yaml
  ```

This will deploy the following apps for testing:

- Keycloak, Authservice, Istio, Istio Operator
- Monitoring including Grafana and Mattermost plus dependencies (OIDC), Sonarqube (SAML), all with SSO enabled

## Testing

1. **Test Admin Console:** Access the Keycloak admin console at https://keycloak.dev.bigbang.mil/auth/admin/ using the default credentials (admin/password) and verify functionality.

    - Note that this will log you into the `master` realm, *not* the `baby-yoda` realm.

1. **Register Test Users:** Naviagte to https://keycloak.dev.bigbang.mil to create two test users in Keycloak, one with a CAC and one without (username/password/OTP).

    - While creating and testing the non-CAC user, ensure there is no CAC supplied to the browser (either use an incognito window/new browser instance or disconnect the CAC/CAC Reader).

    - You may create these test users through the admin console to bypass the registration flow, but must create them in the `baby-yoda` realm, set credentails and add them to the `Impact Level 2 Authorized` group. CAC registered users will automatically be added to the IL2 group.

    - Regardless of creation mechanism, you will need to set email as verified via the admin console for both test users.

    - If deploying using the instructions outlined in the [Deploy Bigbang](#deploy-bigbang) section above, a `cypress` user will automatically be created in keycloak. This can be used as an alternative to manually creating a non-CAC user. The password can be found under `monitoring.values.bbtests.cypress.envs.cypress_tnr_password` in the Big Bang [test-values.yaml](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/tests/test-values.yaml?) file.

1. **Test End-to-End SSO:** Test SSO with Grafana (https://grafana.dev.bigbang.mil), Mattermost (https://chat.dev.bigbang.mil) and Sonarqube (https://sonarqube.dev.bigbang.mil) for both CAC and non-CAC users. Update the `sso.saml.metadata` value in your override file and re-run helm upgrade command for Sonarqube testing as noted previously.

1. **Test Custom Plugin User Forms:** Verify the functionality of the custom user forms for account management.
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/password
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/totp
    - https://keycloak.dev.bigbang.mil/register

1. Once you've confirmed that the package tests above pass, also test your branch against Big Bang per the steps in this [document](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/developer/test-package-against-bb.md).

*Note: Occasionally the DoD certificate authorities will need to be updated. Follow the instructions at `scripts/certs/README.md` and copy the new `truststore.jks` to `chart/resources/dev` and to `development/certs`. You may need to edit `scripts/certs/dod_cas_to_pem.sh` to update to the most recent published certs but it usually points to the latest archive.*

## Files That Require Integration Testing

Some things aren't tested by the package pipeline, but are tested by the BigBang pipeline. These need to be tested independently if updated:

- SSO
- ./chart/templates/bigbang/create-ci-cypress-user-hook.yaml
- ./chart/templates/bigbang/istio/authorizationPolicies/ingressgateway-authz-policy.yaml
- ./chart/templates/bigbang/istio/authorizationPolicies/keycloak-postgres-policy.yaml
- ./chart/templates/bigbang/istio/authorizationPolicies/template.yaml
- ./chart/templates/bigbang/keycloak-rolebinding-openshift-scc.yaml
- ./chart/templates/bigbang/network-attachment-definition.yaml
- ./chart/templates/bigbang/network-policies/additional-networkpolicies.yaml
- ./chart/templates/bigbang/network-policies/egress-default-deny-all.yaml
- ./chart/templates/bigbang/network-policies/egress-dns-http-https.yaml
- ./chart/templates/bigbang/network-policies/egress-helm-test.yaml
- ./chart/templates/bigbang/network-policies/egress-in-namespace.yaml
- ./chart/templates/bigbang/network-policies/egress-istiod.yaml
- ./chart/templates/bigbang/network-policies/egress-ldap.yaml
- ./chart/templates/bigbang/network-policies/egress-postgres.yaml
- ./chart/templates/bigbang/network-policies/egress-smtp.yaml
- ./chart/templates/bigbang/network-policies/egress-tempo.yaml
- ./chart/templates/bigbang/network-policies/ingress-allow-https.yaml
- ./chart/templates/bigbang/network-policies/ingress-default-deny-all.yaml
- ./chart/templates/bigbang/network-policies/ingress-in-namespace.yaml
- ./chart/templates/bigbang/network-policies/ingress-internal-postgres.yaml
- ./chart/templates/bigbang/network-policies/ingress-istio.yaml
- ./chart/templates/bigbang/network-policies/ingress-jgroups.yaml
- ./chart/templates/bigbang/network-policies/ingress-monitoring-sidecar.yaml
- ./chart/templates/bigbang/network-policies/ingress-monitoring.yaml
- ./chart/templates/bigbang/peer-authentication/ispn-ha-exception.yaml
- ./chart/templates/bigbang/peer-authentication/peer-authentication.yaml
- ./chart/templates/bigbang/peer-authentication/postgresException.yaml
- ./chart/templates/bigbang/service-entries/serviceEntry.yaml
- ./chart/templates/bigbang/sidecar/sidecar.yaml
- ./chart/templates/bigbang/virtualservice.yaml

## Instructions for Integration Testing

See the [Big Bang Doc](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/developer/test-package-against-bb.md?ref_type=heads)


# Modifications made to upstream chart
This is a high-level list of modifications that Big Bang has made to the upstream helm chart. You can use this as as cross-check to make sure that no modifications were lost during the upgrade process.

## chart/values.yaml
- disable all internal services other than postgres
- add BigBang additional values at bottom of values.yaml
- add IronBank hardened image
- Add IronBank postgresql12 image for dev/CI development/testing
- and other miscellaneous change.  Diff with previous version to find all changes
- modify pgchecker image to ironbank postgres image

##  chart/charts/*.tgz
- run `helm dependency update` and commit the downloaded archives
- also commit the requirements.lock file so that air-gap deployments don't try to check for updates

## chart/Chart.lock
- Chart.lock is updated during `helm dependency update` with the gluon library & postgresql dependency

## chart/templates/secrets.yaml
- Modified to support templating of the data key so that the keycloak chart can be used to create secret for the truststore.jks binary.

## chart/templates/service-http.yaml
- Quarkus migration: remove http-management port 9990 yaml block. Removed 7 lines near line 50

## chart/templates/servicemonitor.yaml
- line 1 changed "wildfly" to "metrics"
- added support for Istio mTLS in the endpoints lines 37-43

## chart/templates/StatefulSet.yaml
- add extraVolumesBigBang (lines 196-189)
- add extraVolumeMountsBigBang (lines 146-148)
- modify pgchecker initContainer (lines 54-64)
- Quarkus migration: change database environment variable names (lines 89-99)
- Quarkus migration: remove http-management port 9990 yaml block. Removed 3 lines near line 118

## chart/templates/bigbang/*
- add istio virtual service
- add NetworkPolicies to restrict traffic

## chart/resources/
- add /dev directory to hold the the baby-yoda configuration files
- add the DoD certificate bundle pem file

## chart/tests
- add directory with cypress test files

## chart/templates/tests  (this is separate from the upstream templates/test directory)
- add helm template to add support for the helm test library

## chart/Chart.yaml
- update the chart version with the bigbang `-bb.#`
- update app version when not the same as the original chart
- add gluon library dependency
- Update postgresql dependency for local source
- add annotations for release automation

## chart/deps/postgresql
- Upstream bitnami postgresql chart - modified for Iron Bank Postgresql 12.15 runtime.
- Update security context for user:group 26:26

## chart/deps/postgresql/templates/statefulset.yaml
- commented out existing `-if -else` securityContext and replaced with
- `{{- toYaml $.Values.postgresql.containerSecurityContext | nindent 12 }}`
- Update to use `tpl` for `.Values.primary.podLabels`

## chart/deps/postgresql/templates/statefulset-readreplicas.yaml
- Update to use `tpl` for `.Values.readReplicas.podLabels`

## charts/.helmignore
- Added /resources/dev/truststore.pfx to alleviate helm deployment secret size limits
