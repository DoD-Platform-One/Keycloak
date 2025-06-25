# Keycloak Configuration

## Ingress

The helm chart is setup to integrate with [Istio](https://istio.io). Istio must
be deployed to your cluster before enabling the integration. To create an istio
`VirtualService` for keycloak, pass the following values into the Keycloak
chart:

```yaml
domain: <your_domain>
istio:
  enabled: true
  keycloak:
    enabled: true
```

This will create an endpoint at https://keycloak.<your_domain>. You will need to
[configure TLS certificates](#configuring-tls) to access this endpoint.

In order for Keycloak to properly perform x509 client authentication (necessary
for CAC usage), keycloak must terminate TLS. To configure this with the default
Big Bang gateways, pass the following values into the Big Bang chart:

```yaml
istio:
  values:
    extraServers:
      - port:
          name: https-keycloak
          protocol: TLS
          number: 8443
        hosts:
          - keycloak.<your_domain>
        tls:
          mode: PASSTHROUGH
```

> 🛈NOTE
>
> For Big Bang 3.x, this configuration is unnecessary. The passthrough gateway
> is created for you by default.

## Admin User

The administrative user's credentials are pulled from a secret named
`credentials` created by the helm chart. To override the default username and
password, pass the following values into the Keycloak chart:

```yaml
upstream:
  secrets:
    env:
      stringData:
        KEYCLOAK_ADMIN: "your_admin_username"
        KEYCLOAK_ADMIN_PASSWORD: "your_admin_password"
```

The helm chart will automatically create a secret with your credentials and set
the
[appropriate environment variables](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#creating-a-keycloak-admin-user)
for the user to be created.

## Configuring TLS

In order for Keycloak to properly terminate TLS, it must be configured with a
serving certificate and private key.

### Creating the TLS Secrets

`Secret` resources can be created easily by passing them as values to
`upstream.secrets`. Each key under `secrets` will be templated out to separate
`Secret` resources named `{{ include "keycloak.fullname" }}-<key>`

```yaml
upstream:
  secrets:
    tlscert:
      stringData:
        tls.crt: |
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
    tlskey:
      stringData:
        tls.key: |
          -----BEGIN PRIVATE KEY-----
          ...
          -----END PRIVATE KEY-----
```

> ⚠ WARNING
>
> TLS private keys are sensitive and should be safeguarded. If deploying Big
> Bang via GitOps patterns, make sure you're encrypting these secrets in your
> version control system.
> [Big Bang recommends using SOPS for this purpose.](https://docs-bigbang.dso.mil/2.53.0/docs/understanding-bigbang/concepts/encryption/)

### Mounting the Secrets

```yaml
upstream:
  extraVolumes: |-
    - name: tlscert
      secret:
        secretName: {{ include "keycloak.fullname" . }}-tlscert
    - name: tlskey
      secret:
        secretName: {{ include "keycloak.fullname" . }}-tlskey

  extraVolumeMounts: |-
    - name: tlscert
      mountPath: /opt/keycloak/conf/tls.crt
      subPath: tls.crt
      readOnly: true
    - name: tlskey
      mountPath: /opt/keycloak/conf/tls.key
      subPath: tls.key
      readOnly: true
```

> ⚠ WARNING
>
> Because `extraVolumes` and `extraVolumeMounts` are templated string literals,
> only the last instance of them that are specified when performing a
> `helm install` or `helm upgrade` are actually applied to the chart. Make sure
> your final values overlay includes all of the `extraVolumes` and
> `extraVolumeMounts` your configuration requires.

### Configuring Keycloak for HTTPS

```yaml
upstream:
  extraEnv: |-
    - name: KC_HTTPS_CERTIFICATE_FILE
      value: /opt/keycloak/conf/tls.crt
    - name: KC_HTTPS_CERTIFICATE_KEY_FILE
      value: /opt/keycloak/conf/tls.key
```

> 🛈NOTE
>
> The note above about `extraVolumes` and `extraVolumeMounts` applies to
> `extraEnv` as well.

## Configuring Keycloak's x509 Trust Store

For Keycloak to correctly handle x509 client authentication, it must be
configured to trust the certificate authorities the client certificates are
signed with.

### DOD Certificate Authorities

If deploying Keycloak with
[the registry1 image](https://registry1.dso.mil/harbor/projects/3/repositories/opensource%2Fkeycloak%2Fkeycloak),
the DOD certificate authority certificates are already present in the
container's trust store at `/etc/ssl/certs`. You can configure Keycloak to trust
these certificates with
[the `KC_TRUSTSTORE_PATHS` environment variable](https://www.keycloak.org/server/keycloak-truststore).

```yaml
upstream:
  extraEnv:
    - name: KC_TRUSTSTORE_PATHS
      value: /etc/ssl/certs/
```

If you expect all x509 client certificates your instance will encounter will
come from DOD sources (like CACs), this is the only configuration you need to
establish the trust necessary for those certificates to be validated.

### Custom Certificate Authorities

Keycloak can be configured to trust any arbitrary certificate authorities by
placing their certificates in the default `/opt/keycloak/conf/truststores/`
directory.

```yaml
upstream:
  secrets:
    cacertificates:
      stringData:
        # Add all the CA certificates you want to trust here
        ca-certificates.pem: |
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
          -----BEGIN CERTIFICATE-----
          ...
          -----END CERTIFICATE-----
  extraVolumes: |-
    - name: cacertificates
      secret:
        secretName: {{ include "keycloak.fullname" . }}-cacertificates
  extraVolumeMounts: |-
    - name: cacertificates
      mountPath: /opt/keycloak/conf/truststores/
      subPath: ca-certificates.pem
```

## External Services

If your Keycloak instance needs access to external services (for LDAP, SMTP, or
an external database), these can be configured easily.

```yaml
networkPolicies:
  ldap: # can be anything; will be included in the generated network policy's name
    cidrs:
      - <ip>/32 # or maybe a subnet CIDR for a database subnet in your cloud provider
    ports:
      - protocol: TCP # optional; will be assumed TCP unless specified
        port: 636
```

## Databases

This chart includes a postgres database via a dependency on the upstream bitnami
postgres standalone chart. This bundled database is _not_ suitable for
production environments and is only made available as a dependency for
non-production and development environments.

By default, this chart is configured for this development-like setup. When using
this chart in production, you should configure an external database.

### Database Configuration

Configuring an external database is quite straightforward.

```yaml
postgresql:
  # Disable the bundled PostgreSQL chart
  enabled: false

networkPolicies:
  enabled: true
  external:
    database:
      cidrs:
        # Add your database CIDRs here
        - <your db subnet cidr>
        - <your db subnet cidr>
        - <your db subnet cidr>
      ports:
        - port: <your db port>

upstream:
  database:
    vendor: <your db vendor> # postgres, mariadb, etc.
    hostname: <your db hostname>
    port: <your db port>
    database: <your db name>
    username: <your db username>

    # You can specify the password directly:
    password: <your db password>

    # Or you can reference an existing secret:
    existingSecret: my-database-password-secret
    existingSecretKey: password

    # This is useful when paired with a secret management solution like External Secrets Operator
```

In lieu of configuring the database via the upstream chart's `database`
abstraction, you can opt instead to configure the database connection via
environment variables.

```yaml
upstream:
  database: null # disable the upstream chart's abstraction
  secrets:
    env:
      # For all database config options, see: https://www.keycloak.org/server/db
      stringData:
        KC_DB_USERNAME: <your db password>
        KC_DB_PASSWORD: <your db password>
        KC_DB: <your db implementation>
        KC_DB_URL_HOST: <your db url>
        KC_DB_URL_PORT: <your db port> # remember to quote it; environment variables must be a string
        KC_DB_URL_DATABASE: <your db name>
```

You can also combine the two if you want to configure additional database
options.

```yaml
upstream:
  database:
    vendor: <your db vendor> # postgres, mariadb, etc.
    hostname: <your db hostname>
    port: <your db port>
    database: <your db name>
    username: <your db username>

    existingSecret: my-database-password-secret
    existingSecretKey: password

  secrets:
    env:
      # For all database config options, see: https://www.keycloak.org/server/db
      stringData:
        KC_DB_POOL_MAX_SIZE: 1024
```

> 🛈NOTE
>
> The environment variable values are templated, so you can use helm templating
> like `{{ default "true" .Values.enabled }}`

## Monitoring

This chart has been preconfigured to allow the Big Bang monitoring stack to
scrape metrics. To enable this integration, apply these values to the Keycloak
chart.

```yaml
monitoring:
  enabled: true
upstream:
  serviceMonitor:
    enabled: true
```

> 🛈NOTE
>
> This feature is currently not working correctly with Istio. For updates, keep
> an eye on
> [this issue](https://repo1.dso.mil/big-bang/product/packages/keycloak/-/issues/206).

<-- TODO: Remove the above note once #206 is closed -->

## Additional Documentation

- [Keycloak Configuration](https://www.keycloak.org/server/all-config)
- [Keycloak Helm Chart Settings](https://github.com/codecentric/helm-charts/tree/master/charts/keycloakx#readme)
- [Keycloak Documentation](https://www.keycloak.org/documentationmerged)
