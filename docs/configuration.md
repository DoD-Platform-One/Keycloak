# Keycloak Configuration

## Ingress

The helm chart is setup to integrate with [Istio](https://istio.io).  Istio must be deployed to your cluster before enabling the integration.  To create the istio endpoint, set the following in your `values.yaml`

```yaml
domain: <your_domain_name.com>
istio:
  enabled: true
  keycloak:
    enabled: true
```

This will create an endpoint at https://keycloak.<your_domain_name>.com.  You will need to [setup TLS certificates](#certificates) to access this endpoint.

> Keycloak requires that Istio be setup as passthrough.  To do this, set the following in Istio's `values.yaml`:
>
> ```yaml
> extraServers:
> - port:
>     name: https-keycloak
>     protocol: TLS
>     number: 8443
>   hosts:
>     - keycloak.dev.bigbang.mil
>   tls:
>     mode: PASSTHROUGH
> ```

> If you also need Istio to terminate TLS for other apps, like logging, security, or monitoring tools, in the same cluster as Keycloak, you will need to do one of the following:
>
> - Create certificates for Istio TLS termination and Keycloak TLS that do not overlap
> - Use different ingress ports for Keycloak and Istio TLS termination apps
> - Provide two proxys (e.g. load balancers) with different IPs into the cluster, one for keycloak, and one for TLS terminated apps.
>
> The reason this is needed is because [browsers reuse connections that have the same IP:Port and a valid certificate](https://httpwg.org/specs/rfc7540.html#rfc.section.9.1.1).  [Istio should attempt to detect and return a 421 response](https://github.com/istio/istio/issues/13589) to not reuse the connection, but it doesn't (yet), which results in a [data leak vulnerability](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-11767) to the wrong application, and typically a 404 error.  This happens when you go to one of the TLS terminated apps first, then attempt to connect to keycloak second.

## Admin user

The administrative user's credentials are pulled from a secret named `credentials` created by the helm chart.  To override the default username and password, set the following in your `values.yaml`:

```yaml
secrets:
  env:
    stringData:
      KEYCLOAK_ADMIN: "your_admin_username"
      KEYCLOAK_ADMIN_PASSWORD: "your_admin_password"
```

The helm chart will automatically create a secret with your credentials and set the [appropriate environmental variables](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#creating-a-keycloak-admin-user) for the user to be created.

## Certificates

TLS certificates and Certificate Authorities truststore can be injected by creating secrets containing the values and volume mounting them into the pod. Thare are two ways to create the secrets. They can be created using gitops tools like a flux kustomize overlay for example. Another way to create the secrets is to use the keycloak helm chart. The following shows you how this would be done in the `values.yaml`:

```yaml
secrets:
  truststore:
    data:
      truststore.jks: |-
        <base64 encoded binary truststore.jks>
  tlscert:
    stringData:
      tls.crt: |
        <TLS CRT string>
  tlskey:
    stringData:
      tls.key: |
        <TLS Key string>

# NOTE: If you have other volumes you must include them together with this setting
extraVolumes: |-
  - name: tlscert
    secret:
      secretName: {{ include "keycloak.fullname" . }}-tlscert
  - name: tlskey
    secret:
      secretName: {{ include "keycloak.fullname" . }}-tlskey
  - name: truststore
    secret:
      secretName: {{ include "keycloak.fullname" . }}-truststore

# NOTE: If you have other volume mounts you must include them together with this setting
extraVolumeMounts: |-
  - name: tlscert
    mountPath: /opt/keycloak/conf/tls.crt
    subPath: tls.crt
    readOnly: true
  - name: tlskey
    mountPath: /opt/keycloak/conf/tls.key
    subPath: tls.key
    readOnly: true
  - name: truststore
    mountPath: /opt/keycloak/conf/truststore.jks
    subPath: truststore.jks
    readOnly: true
```

Keycloak is informed where the files are located be setting environment variables.
```yaml
extraEnv: |-
  - name: KC_HTTPS_CERTIFICATE_FILE
    value: /opt/keycloak/conf/tls.crt
  - name: KC_HTTPS_CERTIFICATE_KEY_FILE
    value: /opt/keycloak/conf/tls.key
  - name: KC_HTTPS_TRUST_STORE_FILE
    value: /opt/keycloak/conf/truststore.jks
```

Keycloak supports a custom location for the trust store and permits enablement/disablement of the baby yoda domain. You can find a more in depth explanation of these values and their usage [here](https://repo1.dso.mil/big-bang/product/packages/keycloak/-/issues/138#note_2038936).
```yaml
secrets:
  env:
    stringData:
      KC_SPI_BABY_YODA_OCSP_ENABLED: "true"
      KC_SPI_TRUSTSTORE_FILE_FILE: "/opt/keycloak/conf/truststore.jks"
      KC_SPI_TRUSTSTORE_FILE_PASSWORD: "password"
```
_Note: OCSP [is been phased out](https://www.thesslstore.com/blog/ocsp-vs-crl-what-each-is-why-browsers-prefer-one-over-the-other/#h-ocsp-vs-crl-why-the-industry-is-shifting-away-from-ocsp) and [is no longer supported in >80% of web browsers](https://www.chromium.org/Home/chromium-security/crlsets) today with CRL's being the replacement. CRL's are the certificate revocation mechanism required by the W3C for all browser vendors. Firefox is one of the few web browsers that still uses OCSP for now._

### Using System Truststore

The HTTPS Truststore was deprecated in Keycloak 25.0 and an eventual plan to shift existing Truststores (housing certs related to validating CAC credentials) before support is fully removed. The Truststore is utilized to validate TLS traffic where Keycloak is a client (not server, which is handled by the `tls.key`, `tls.crt` files. This validation enables keycloak to trust upstream CA validation authorities)

[Deployment of the System Truststore](https://www.keycloak.org/server/keycloak-truststore) can be completed by providing certificates in either .pfx, .pem, or .p12 form. A basic script to transition from .jks to .pfx is [provided](../scripts/certs/jks_to_pfx.sh).

Once the certificates are prepared, they can be included in any Keycloak overrides under the `extraEnv`, `secrets.truststore`, `extraVolumes` and `extraVolumeMounts` keys as below:

```
extraVolumes: |-
  ...
  - name: truststore
    secret:
      secretName: {{ include "keycloak.fullname" . }}-truststore
  ...
extraVolumeMounts: |-
  ...
  - name: truststore
    mountPath: /opt/keycloak/conf/truststore.pfx
    subPath: truststore.pfx
  ...
secrets:
  ...
  truststore:
    data:
      truststore.pfx: '{{ .Files.Get "SYSTEM/PATH/TO/truststore.pfx" | b64enc }}'
extraEnv: |-
  ...
  - name: KC_TRUSTSTORE_PATHS
    value: /opt/keycloak/conf/truststore.pfx
  ...
```

#### Upgrade Concerns
  - If transitioning from the HTTPS Truststore to the System Truststore, the Keycloak Statefulset will likely need to be deleted during migration.

### Configuring Linux (Ubuntu) for CAC / Cert Testing
- [All-in-one script](https://github.com/jdjaxon/linux_cac) to install CAC middleware and import DoD certificates to Chrome & Firefox
- Other CAC resources: 
  - [PKI/PKE Document Library](https://public.cyber.mil/pki-pke/pkipke-document-library/?_dl_facet_pkipke_topics=trust-store-management)
  - [CAC FAQ](https://militarycac.org/linux.htm)

## Database

By default, the helm chart uses an internal PostgreSQL database.  To point to an external database set environment variables.  Example of helm chart values:

```yaml
postgresql:
  # Disable PostgreSQL dependency
  enabled: false

secrets:
  env:
    stringData:
      KC_DB_USERNAME: "keycloakDBuser"
      KC_DB__PASSWORD: "keycloakDBpassword"
      KC_DB: postgres
      KC_DB_URL_HOST: postgres.dev.bigbang.mil
      KC_DB_URL_PORT: "5432"
      KC_DB_URL_DATABASE: keycloakDBname
```

> The values are templatized, so you can use helm templating like `{{ default "true" .Values.enabled }}`

This will create a secret named `keycloak-env` which is then added by reference for the keycloak pod's environment.

## High Availability (HA) / Clustering

The helm chart supports high availabilty. Also see [Keycloak documentation for distributed caching](https://www.keycloak.org/server/caching). To enable this, update `replicas` to be > 1 in your `values.yaml`:

```yaml
replicas: 2
```

## Custom Registration

Platform One has a custom Keycloak plugin in the to customize new user registrations. The configuration for this plugin can be set by volume mounting a configuration file into the pod.  The following configuration in `values.yaml` shows you how to do this:

```yaml
secrets:
  env:
    stringData:
      # Let Keycloak know to use the custom registration configuration
      CUSTOM_REGISTRATION_CONFIG: /opt/jboss/keycloak/customreg.yaml
  customreg:
    stringData:
      customreg.yaml: |-
        <configuration in yaml form>

# NOTE: If you have other volumes you must include them together with this setting
extraVolumes: |-
  - name: customreg
    secret:
      secretName: {{ include "keycloak.fullname" . }}-customreg

# NOTE: If you have other volume mounts you must include them together with this setting
extraVolumeMounts: |-
  - name: customreg
    mountPath: /opt/jboss/keycloak/customreg.yaml
    subPath: customreg.yaml
    readOnly: true
```

The helm chart will create a secret named `keycloak-customreg` containing the configuration.  This gets volume mounted in the pod as a file.  An environmental variable is set to tell the plugin to use the configuration.

## Custom Realm

Keycloak has the ability to import a custom realm file using a volume mount of a file and an environmental variable. However, it is **NOT** recommended for operational/production environments. If you need to set this for development or testing purposes, see the [helm chart documentation](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#setting-a-custom-realm), the [Keycloak official documentaion](https://www.keycloak.org/server/importExport) and the [test-values.yaml](../tests/test-values.yaml) for a working example.

## Environmental Variables

By default, the helm chart sets several environmental variables to support Istio, HA/Clustering, and Monitoring. See [Keycloak configuration](https://www.keycloak.org/server/all-config) If you need to add additional environmental variables (or modify existing ones in `.Values.secrets.env`), use the following:

```yaml
secrets:
  env:
    stringData:
    # The values are templatized.  You can use helm templating like `{{ default "true" .Values.enabled }}`
      MY_ENV_VAR: MY_VALUE_FOR_MY_ENV_VAR
```

> Do not change `extraEnvFrom` in `values.yaml` without also including the secret `keycloak-env`.  Otherwise, you will lose the default environmental variables.

You can also set `extraEnv` in `values.yaml`.  But beware that this is a single string, not a map.

## Monitoring

The helm chart is setup to perform monitoring of metrics using Prometheus.  This can be enabled by setting the following in `values.yaml`:

```yaml
serviceMonitor:
  enabled: true
```

In addition, if integrating with BigBang's Prometheus, you should set the following to deploy a NetworkPolicy allowing access from Prometheus to Keycloak's metrics endpoint.

```yaml
monitoring:
  enabled: true
```

### LDAP Egress

If you are planning to utilize an external LDAP with Keycloak, you'll need to set up permissions to Egress.  This can be enabled by setting the following:

```yaml
networkPolicies:
  ldap:
    enabled: true
    cidr: X.X.X.X/X
    port: 636
```

## Additional Documentation

- [Keycloak Configuration](https://www.keycloak.org/server/all-config)
- [Keycloak Helm Chart Settings](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#readme)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
