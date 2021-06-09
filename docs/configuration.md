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
> extraServers:
> - port:
>     name: https-keycloak
>     protocol: TLS
>     number: 8443
>   hosts:
>     - keycloak.bigbang.dev
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
  credentials:
    stringData:
      adminuser: your_admin_username
      password: your_admin_password
```

The helm chart will automatically create a secret with your credentials and set the [appropriate environmental variables](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#creating-a-keycloak-admin-user) for the user to be created.

## Certificates

TLS certificates and Certificate Authorities can be used by creating secrets containing the values and volume mounting them into the pod. Thare are two ways to create the secrets. They can be created using gitops tools like a flux kustomize overlay for example. Another way to create the secrets is to use the keycloak helm chart. The following shows you how this would be done in the `values.yaml`:

```yaml
secrets:
  env:
    stringData:
      # Tell Keycloak to use this CA file
      X509_CA_BUNDLE: /etc/x509/https/cas.pem
  certauthority:
    stringData:
      cas.pem: |
        <Certificate Authorities String>
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
  - name: certauthority
    secret:
      secretName: {{ include "keycloak.fullname" . }}-certauthority
  - name: tlscert
    secret:
      secretName: {{ include "keycloak.fullname" . }}-tlscert
  - name: tlskey
    secret:
      secretName: {{ include "keycloak.fullname" . }}-tlskey

# NOTE: If you have other volume mounts you must include them together with this setting
extraVolumeMounts: |-
  - name: certauthority
    mountPath: /etc/x509/https/cas.pem
    subPath: cas.pem
    readOnly: true
  - name: tlscert
    mountPath: /etc/x509/https/tls.crt
    subPath: tls.crt
    readOnly: true
  - name: tlskey
    mountPath: /etc/x509/https/tls.key
    subPath: tls.key
    readOnly: true
```

Each secret above will be volume mounted at `/etc/x509/https`, where [Keycloak will look for certificates to install](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md#setting-up-tlsssl).

## Database

By default, the helm chart uses an internal PostgreSQL database.  To point to an external database, use the [Keycloak container documentation](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#database-setup).  Example of helm chart values:

```yaml
postgresql:
  # Disable PostgreSQL dependency
  enabled: false

secrets:
  env:
    stringData:
      DB_USER: "myDBUser"
      DB_PASSWORD: "myDBPassword"
      DB_VENDOR: postgres
      DB_ADDR: mypostgres.bigbang.dev
      DB_PORT: "5432"
      DB_DATABASE: mydb
```

> The values are templatized, so you can use helm templating like `{{ default "true" .Values.enabled }}`

This will create a secret named `keycloak-env` which is then added by reference for the keycloak pod's environment.

## High Availability (HA) / Clustering

The helm chart is already setup to support [Keycloak clustering](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md#setting-up-tlsssl) using DNS Ping for service disovery.  To enable this, update `replicas` to be > 1 in your `values.yaml`:

```yaml
replicas: 2
```

## Custom Registration

Platform One has included a plugin in the Keycloak docker image to help customize new user registrations.  The configuration for this plugin can be set by volume mounting a configuration file into the pod.  The following configuration in `values.yaml` shows you how to do this:

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

Setting up a custom realm using a `.json` file can be done using a volume mount of a file and an environmental variable.  However, it is **NOT** recommended because it has the potential to override existing realm settings.  If you need to set this for development or testing purposes, see the [official documentation](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#setting-a-custom-realm) and the [test-values.yml](../tests/test-values.yml) for a working example.

## Environmental Variables

By default, the helm chart sets several environmental variables to support Istio, HA/Clustering, and Monitoring.  If you need to add additional environmental variables (or modify existing ones in `.Values.secrets.env`), use the following:

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

## Additional Documentation

- [Keycloak Container Settings](https://github.com/keycloak/keycloak-containers/blob/master/server/README.md)
- [Keycloak Helm Chart Settings](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#readme)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
