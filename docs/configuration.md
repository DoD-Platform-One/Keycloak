# Keycloak Configuration

## Ingress

The helm chart is setup to integrate with [Istio](https://istio.io).  Istio must be deployed to your cluster before enabling the integration.  To create the istio endpoint, set the following in your `values.yaml`

```yaml
hostname: <your_domain_name.com>
istio:
  enabled: true
  keycloak:
    enabled: true
```

This will create an endpoint at https://keycloak.<your_domain_name>.com.  You will need to [setup TLS certificates](#certificates) to access this endpoint.

> Keycloak requires that Istio be setup as passthrough.  To do this, set the following in Istio's `values.yaml`:
>
> ```yaml
> tls:
>   mode: PASSTHROUGH
> ```

## Admin user

The administrative user's credentials are pulled from a secret named `credentials` created by the helm chart.  To override the default username and password, set the following in your `values.yaml`:

```yaml
secrets:
  credentials:
    stringData:
      adminuser: your_admin_username
      password: your_admin_password
```

The helm chart will automatically create a secret with your credentials and set the [appropriate environmental variables](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#creating-a-keycloak-admin-user) for the user to be created.

## Certificates

Certificates for Certificate Authorities and TLS are pulled from a secret named `certificates`, created by the helm chart.  To override the development certificates, set the following in your `values.yaml`.  The values should be a key

```yaml
secrets:
  certificates:
    stringData:
      tls.crt: |-
        <TLS CRT string>
      tls.key: |-
        <TLS Key string>
      cas.pem: |-
        <Certificate Authorities String>
```

The secrets will be volume mounted at `/etc/x509/https`, where [Keycloak will look for certificates to install](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md#setting-up-tlsssl).

## Database

By default, the helm chart uses an internal PostgreSQL database.  To point to an external database, use the [Keycloak container documentation](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#database-setup).  We recommend you use `extraEnvFrom` and `secrets` so you do not need to manually merge `extraEnv` with your new values.  Example of helm chart values:

```yaml
postgresql:
  # Disable PostgreSQL dependency
  enabled: false

extraEnvFrom: |
  - secretRef:
      name: '{{ include "keycloak.fullname" . }}-db'

secrets:
  db:
    stringData:
      DB_USER: "myDBUser"
      DB_PASSWORD: "myDBPassword"
      DB_VENDOR: postgres
      DB_ADDR: mypostgres.bigbang.dev
      DB_PORT: "5432"
      DB_DATABASE: mydb
```

> This setting is a single string.  So, by overriding it, you will eliminate upstream settings for `extraEnv`.  Be careful with your settings.
> This setting is templatized, so you can use helm templating like `{{ default "true" .Values.enabled }}`

## High Availability (HA) / Clustering

The helm chart is already setup to support [Keycloak clustering](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md#setting-up-tlsssl) using DNS Ping for service disovery.  To enable this, update `replicas` to be > 1 in your `values.yaml`:

```yaml
replicas: 2
```

## Custom Registration

Platform One has included a plugin in the Keycloak docker image to help customize new user registrations.  The configuration for this plugin can be set by updating `customreg` in `values.yaml`:

```yaml
secrets:
  customreg:
    stringData:
      customreg.yaml: |-
        <configuration in yaml form>
```

The helm chart will create a secret named `customreg` containing the configuration.  This gets volume mounted in the pod as a file and an environmental variable is set to tell the plugin to use the configuration.

## Custom Realm

Setting up a custom realm using a `.json` file can be done using a volume mount of a file and an environmental variable.  However, it is **NOT** recommended that this be done in production because it will reset the credentials in Keycloak every time a deployment happens.  If you need to set this for development or testing purposes, see the [official documentation](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#setting-a-custom-realm) and the [test-values.yml](../tests/test-values.yml) for a working example.

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

## Additional Documentation

- [Keycloak Container Settings](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md)
- [Keycloak Helm Chart Settings](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#readme)
- [Keycloak Documentation](https://www.keycloak.org/documentation)