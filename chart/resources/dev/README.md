# This directory contains files used for development testing
The files must be located here so that they can be accessed by the Keycloak helm chart. They are for dev/test/demo/CI. They are also a working baseline for production deployments.

## baby-yoda.json
This is a Keycloak realm export that can be imported to a Keycloak installation for dev/test/demo/CI. It can be imported on startup with the `--import-realm` argument. See the [keycloak-bigbang-values.yaml](../../../docs/assets/config/example/keycloak-bigbang-values.yaml) example.

## baby-yoda.yaml
This is a configuration file to support the [Platform One custom Keycloak plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin). It can be configured on startup with the CUSTOM_REGISTRATION_CONFIG environment variable. See the [keycloak-bigbang-values.yaml](../../../docs/assets/config/example/keycloak-bigbang-values.yaml) example.

## baby-yoda-ci.json
A keycloak realm configured for CI pipelines without OTP and email verification.

## truststore.jks
 The truststore prevents bad actors from using fake certificates to authenticate with Keycloak. The truststore is pre-built and put into a k8s secret combined with a volume/volumemount to inject the file into the Keycloak container. The truststore binary file was created from the the scripts located in this code repository at `/scripts/certs/`. For more info see the readme file in that directory. The truststore was created from the DoD version 9.5 certs downloaded from [public.cyber.mil](https://public.cyber.mil/pki-pke/pkipke-document-library/).

## quarkus.properties
Contains quarkus properties that cannot be set with Keycloak environment variables. These quarkus properties are for the custom quarkus extension that is deployed with the Platform One Keycloak plugin. The quarkus extension handles custom routing and redirects.