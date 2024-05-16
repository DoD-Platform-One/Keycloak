# How to update the Keycloak Package chart
Big Bang makes modifications to the upstream Codecentric helm chart. The upstream Keycloak image from IronBank is used in conjuction with a custom Keycloak plugin. The custom P1 plugin contains custom authz and authn code, custom self-registration theme, and automatic joining of groups based on certian registration information like the use of a DoD CAC. The plugin code is hosted at [https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin). Platform One custom plugin artifact image is hosted in Iron Bank at registry1.dso.mil/ironbank/opensource/keycloak/keycloak:X.X.X  

*Note: Be aware that there are currently two versions of Keycloak. One is the legacy version that uses Wildfly for the application server. The other version is the new one using Quarkus. Big Bang has migrated to the new Quarkus version. The images in Iron Bank have tag without the "legacy" `X.X.X`.*

1. **Read Release Notes:** from the upstream [Keycloak documentation](https://www.keycloak.org/docs/latest/release_notes/index.html). Carefully review the release notes for the new Keycloak version to understand any breaking changes or required manual upgrade steps.

1. **Identify Chart Changes:** Perform a diff between the current and [upstream](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak) charts to identify significant changes.

1. **Merge Chart Updates:** Manually merge the new chart updates into the existing Keycloak package code, paying close attention to Big Bang-specific modifications. *See the [Modifications made to upstream chart section](#modifications-made-to-upstream-chart) below.*

1. **Update Chart Files:** Update the chart/Kptfile with the new chart version and commit hash.

1. **Update CHANGELOG.md:** Add an entry describing the upgrade, including the new Keycloak and chart versions.

1. **Update Chart.yaml:** Update the chart/Chart.yaml file with the appropriate versions for Keycloak, dependencies, and annotations.
    ```yaml
    version: XX.X.X-bb.X
    appVersion: XX.X.X-legacy
    dependencies:
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
1. **Update README.md:** Update the file following the [gluon library script](https://repo1.dso.mil/big-bang/apps/library-charts/gluon/-/blob/master/docs/bb-package-readme.md) guidelines noting any additional chart changes you make during development testing.

1. **Run Helm Dependency Update:** Execute helm dependency update ./chart to update the chart archives and generate a new requirements.lock file. Commit both the archives and the lock file.
    ```bash
    helm dependency update ./chart
    ```
1. **Build and Publish Plugin Image:** Follow the instructions in the P1 plugin repository to build and publish a new plugin image to the IronBank container registry. Use a test label for the image initially.

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

1. **Publish Official Plugin Image:** Publish the official plugin image to IronBank, following the provided [instructions](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/blob/main/docs/deployment.md).

1. **Update BigBang MR:** Update the `tests/test-values.yaml` file in the BigBang MR with the new plugin init-container tag, using either the test image or the official IronBank image.


# Testing with custom P1 plugin

1. **Create k8s Dev Environment:** Use the Big Bang k3d-dev.sh script with the -a option to create a k3d cluster with metalLB and a passthrough gateway for Keycloak.

1. **Deploy Big Bang:** Deploy Big Bang with Istio-operator, Istio, Gitlab, Sonarqube, and Mattermost enabled. Ensure SSO is enabled for Gitlab and Sonarqube, but not for Mattermost initially. Use the provided example Big Bang values override file and modify the plugin image label to use your test image.


    ```bash
    helm upgrade -i bigbang ./chart \
        -n bigbang --create-namespace \
        -f ../overrides/keycloak-bigbang-values.yaml \
        -f ../overrides/registry-values.yaml \
        -f ./chart/ingress-certs.yaml
    ```

1. **Register Test Users:** Create two test users in Keycloak, one with a CAC and one without (username/password/OTP). *Note: You may create these test users through the [admin UI](https://keycloak.dev.bigbang.mil/auth/admin/master/console/), but must also create them in the baby-yoda realm and add them to the IL2 Users group.* 


1. **Test Admin Console:** Access the Keycloak admin console using the default credentials (admin/password) and verify functionality.

1. **Test End-to-End SSO:** Test SSO with Gitlab and Sonarqube for both CAC and non-CAC users. Update the `sso.saml.metadata` value in your override file for Sonarqube testing.

1. **Test Mattermost:** Create a user in Mattermost with username/password and then attempt to log in using the Gitlab OIDC link.
    - Browse to chat.dev.bigbang.mil and create a user with username/password. This option should be available if `addons.mattermost.sso.enable_sign_up_with_email` is enabled. This test simply validates that normal authentication works when SSO is not forced.
    - After you have logged in, log out and attempt to create an SSO user using the Gitlab OIDC link. Once you finish authenticating, you should return to mattermost as expected and be logged in as your keycloak user.

1. **Test Custom User Forms:** Verify the functionality of the custom user forms for account management.
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/password
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/totp
    - https://keycloak.dev.bigbang.mil/register

*Note: Occasionally the DoD certificate authorities will need to be updated. Follow the instructions at `/scripts/certs/README.md` and copy the new `truststore.jks` to [./chart/resources/dev](../chart/resources/dev/) and to [./development/certs](../development/certs) . You may need to edit `/scripts/certs/dod_cas_to_pem.sh` to update to the most recent published certs but it usually points to the latest archive.*


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
- `{{- toYaml $.Values.postgesql.containerSecurityContext | nindent 12 }}`
