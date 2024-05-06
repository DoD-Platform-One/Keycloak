# How to update the Keycloak Package chart
Big Bang makes modifications to the upstream Codecentric helm chart. The upstream Keycloak image from IronBank is used in conjuction with a custom Keycloak plugin. The custom P1 plugin contains custom authz and authn code, custom self-registration theme, and automatic joining of groups based on certian registration information like the use of a DoD CAC. The plugin code is hosted at [https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin). Platform One custom plugin artifact image is hosted in Iron Bank at registry1.dso.mil/ironbank/opensource/keycloak/keycloak:X.X.X.
1. Read release notes from upstream [Keycloak documentation](https://www.keycloak.org/docs/latest/release_notes/index.html). Be aware of changes that are included in the upgrade. Take note of any manual upgrade steps that customers might need to perform, if any.
1. Be aware that there are currently two versions of Keycloak. One is the legacy version that uses Wildfly for the application server. The other version is the new one using Quarkus. Big Bang has migrated to the new Quarkus version. The images in Iron Bank have tag without the "legacy" `X.X.X`.
1. Create a development branch and merge request from the Keycloak issue if one was not already created by the renovate tool.
1. Do diff of [upstream chart](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak) between old and new release tags to become aware of any significant chart changes.
1. Merge/Sync the new helm chart with the existing Keycloak package code. A graphical diff application like [Meld](https://meldmerge.org/) is useful. Don't use kpt to update. You can use Meld to sync changes directory by directory, file by dile, line by line. Reference the "Modifications made to upstream chart" section below. Be careful not to overwrite Big Bang Package changes that need to be kept.
1. When you are done manually syncing the helm chart update the chart/Kptfile manually with the new version and commit hash.
1. Update /CHANGELOG.md with an entry for "upgrade Keycloak to app version X.X.X chart version X.X.X-bb.X". Or, whatever description is appropriate.
1. Update /chart/Chart.yaml to the appropriate versions.
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
1. Update the /README.md following the [gluon library script](https://repo1.dso.mil/big-bang/apps/library-charts/gluon/-/blob/master/docs/bb-package-readme.md). You should update the README again at the end to pick up any additional chart changes you make during development testing.
1. Run a helm dependency command to update the chart/charts/*.tgz archives and create a new requirements.lock file. You will commit the tar archives along with the requirements.lock that was generated.
    ```bash
    helm dependency update ./chart
    ```
1. A custom Keycloak image is no longer used. Instead a plugin image is created and volume mounted into k8s. This Keycloak package chart uses the IronBank image.
1. If you are upgrading the Keycloak version follow instructions in the [P1 plugin repo](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md) to upgrade the custom P1 plugin. Follow the instructions there for building a plugin jar for testing. Recommended to use the free community version of Intellij IDEA instead of Visual Studio Code. Intellij IDEA has much better support for Java development. You can run unit tests with coverage and build from IDE.
1. Test the plugin changes locally using the [docker-compose local dev environment](../development/README.md). Temporarily point the development/docker-compose.yaml to the new plugin jar built during the previous step. 
    1. Test the admin console
    1. Test the custom user forms
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/password
        - https://keycloak.dev.bigbang.mil:8443/auth/realms/baby-yoda/account/totp
        - https://keycloak.dev.bigbang.mil:8443/register
    1. Test registering a user with CAC. Verify that the user is automatically added to IL2 group. Test login from the account page.
    1. Test registering a regular user with username and password. Test login at the account page with OTP.
1. After you are satisfied with the testing in the docker compose environment, put a copy of the new plugin jar at development/plugin/p1-keycloak-plugin-X.X.X.jar. Then permanently point the docker-compose.yaml to that jar file so that the docker compose environment will work out of the box for anyone who clones this repo.
1. Build and publish a plugin image to the [P1 Keycloak Plugin container registry](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/container_registry/) using the instructions in the [P1 plugin repo](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/blob/main/docs/deployment.md). The image label should have "test" in the name like this `test-X.X.X`. 
1. Use a full k8s deployment to test Keycloak, the custom plugin, and end-to-end SSO. See next section.
1. After all testing search all the documentation and update any old Keycloak and plugin version numbers.
1. Make an official semver release tag in the p1 plugin repo and monitor the mirrored [Party Bus IL2 pipeline](https://code.il2.dso.mil/big-bang/keycloak/keycloak-p1-auth-plugin/-/pipelines) to make sure it passes. To test the Party Bus pipeline with your development branch cut a release candidate tag from the branch like this X.X.X-rc.X. You can verify that the new plugin code will pass before merging the MR.
1. After all testing locally and k8s testing with a BigBang deployment has been completed publish an official plugin image in [IronBank](https://repo1.dso.mil/dsop/big-bang/p1-keycloak-plugin). The order that things should happen with BigBang MRs, BigBang release, and publishing plugin image in IronBank is still unclear. Brief instructions for IronBank pipeline maintenance:
    1. The PartyBus IL2 MDO pipeline publishes the p1-keycloak-plugin-X.X.X.jar artifact back to the [P1 Keycloak Package Registry](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/packages). If PartyBus IL2 MDO pipeline is failing for reasons outside of our control, worst case, we can [manualy publish](https://docs.gitlab.com/ee/user/packages/generic_packages/) to the plugin package registry. The BigBang MR will need the `tests/test-values.yaml` updated to use the new plugin init-container tag. The test values can use the image that was created in Big Bang Keycloak plugin container registry](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/container_registry/). There is no hard requirement to use the IronBank plugin image for the test values. 
    1. At the [IronBank repo](https://repo1.dso.mil/dsop/big-bang/p1-keycloak-plugin) create a new issue with template "application update"
    1. Create a branch and MR from the issue.
    1. update hardening_manifest.yaml
        - the tag version
        - the label org.opencontainers.image.version
        - resources.url
        - resources.validation.value (use sha256sum to get the hash value of the jar file)
    1. commit and push code changes
    1. Verify that pipline passes
    1. Complete checkbox items in the issue. You will need to request [VAT](https://vat.dso.mil/) "Vendor Contributor" access if there are any new findings that need justification.
    1. Mark the MR as ready and apply label "Hardening:Review" to the MR and the issue.
    1. Monitor the issue to make sure it keeps moving with the Container Hardening Team(CHT).
1. Don't forget, the BigBang MR will need the `tests/test-values.yaml` updated to use the new plugin init-container tag. Can be either the test image or the image published in IronBank
    - registry.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/init-container:test-X.X.X
    - registry1.dso.mil/ironbank/big-bang/p1-keycloak-plugin:X.X.X

# Testing new Keycloak version with custom P1 plugin.
1. Create a k8s dev environment. One option is to use the Big Bang [k3d-dev.sh](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/scripts/developer/k3d-dev.sh) script with the `-a` option. This will enable  metalLB and create a separate passthrough gateway, which is necessary for Keycloak. The following steps assume you are using the script.
1. Follow all of the instructions at the end of the script to connect to and use the remote k3d cluster. 
1. Deploy Big Bang with istio-operator, Istio, Gitlab, Sonarqube, and Mattermost enabled. SSO should be enabled for Gitlab and Sonarqube, but NOT for Mattermost at this point (you'll enable it for Mattermost later). This is so that you can test both OIDC and SAML end-to-end SSO. Gitlab uses OIDC and Sonarqube uses SAML. Mattermost is included in the end-to-end testing because it has some unique SSO configuration that should be tested. Deploy BigBang using the following example helm command and the provided [example big bang values override](./docs/assets/config/example/keycloak-bigbang-dev-values.yaml). Change the plugin image label to use your test plugin image. Hint: search for "X.X.X" in the example values file.
    ```bash
    helm upgrade -i bigbang ./chart -n bigbang --create-namespace -f ../overrides/keycloak-bigbang-values.yaml -f ../overrides/registry-values.yaml -f ./chart/ingress-certs.yaml
    ```
1. For more help getting these setup with all of the needed values.yaml changes check [authservice](https://repo1.dso.mil/big-bang/product/packages/authservice/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md).
1. In a browser load `https://keycloak.dev.bigbang.mil` and register two test users, one with your CAC, one without a CAC with just a username and password with OTP. Both flows need to be tested. (Note: You can also create these test users through the [Keycloak admin UI](https://keycloak.dev.bigbang.mil/auth/admin/master/console/), but if you do so you must create them in the baby-yoda realm and add them to the IL2 Users group). 
1. Then go back to `https://keycloak.dev.bigbang.mil/auth/admin` and login to the admin console with the default credentials `admin/password`
1. Navigate to users, click "View all users" button and edit the test users that you created. Set "Email Verified" to "Yes." In "Required User Actions" remove "Verify Email." Click the "Save" button.
1. Test end-to-end SSO with Gitlab with both your CAC user and non-CAC user by browsing to gitlab.dev.bigbang.mil in an incognito window and confirming that you can log on through SSO as both users. 
1. Before testing sonarqube, you'll need to update the sso.saml.metadata value in your override. There are two ways to find the correct value:
    - Through the Keycloak admin console. Log on to the [admin console](https://keycloak.dev.bigbang.mil/auth/admin/master/console/), then click on "Realm settings" in the left-hand column, then in the Endpoints section, click on the "SAML 2.0 Identity Provider Metadata" link. This will open another tab, containing the SAML metadata value you need. View source here to place the value in one continuous line (ctrl-u will do this on Chrome and Firefox). Then copy the value to your clipboard. 
    - From a bash prompt, run ```curl https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/protocol/saml/descriptor``` and copy the output to your clipboard. If you are on Microsoft Windows running WSL, you may need to update your WSL /etc/hosts file with the correct IP for the Keycloak passthrough gateway (you should already have such an entry in your Windows host file if you're able to connect to keycloak.dev.bigbang.mil in a browser).
1. Once you have the value copied to your clipboard, open your override in an editor and delete the old sso.saml.metadata value. Paste in your new value. IMPORTANT: *** Be sure the value is on the same line as the metadata: key, not one line below it. ***
1. Run the same helm update command you used initially to deploy Big Bang once again so that Kubernetes reads the new SAML metadata value. 
1. Test end-to-end SSO with Sonarqube with both your CAC user and non-CAC user by browsing to sonarqube.dev.bigbang.mil in an incognito window and confirming that you can log on through SSO as both users. 
1. Test Mattermost. To do this:
    - Browse to chat.dev.bigbang.mil and create a user with username/password. This option should be available if `addons.mattermost.sso.enable_sign_up_with_email` is enabled. This test simply validates that normal authentication works when SSO is not forced.
    - Once logged in, log out and attempt to create an SSO user using the Gitlab OIDC link. Once you finish authenticating, you should return to mattermost as expected and be logged in as your keycloak user
1. Test the custom user forms to make sure all the fields are working
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/password
    - https://keycloak.dev.bigbang.mil/auth/realms/baby-yoda/account/totp
    - https://keycloak.dev.bigbang.mil/register

Note: Occasionally the DoD certificate authorities will need to be updated. Follow the instructions at `/scripts/certs/README.md` and copy the new `truststore.jks` to [./chart/resources/dev](../chart/resources/dev/) and to [./development/certs/](../development/certs/) . You might have to edit the `/scripts/certs/dod_cas_to_pem.sh` to update to the most recent published certs but it usually points to the latest archive.


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
