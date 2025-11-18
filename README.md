<!-- Warning: Do not manually edit this file. See notes on gluon + helm-docs at the end of this file for more information. -->
# keycloak

![Version: 7.1.4-bb.3](https://img.shields.io/badge/Version-7.1.4--bb.3-informational?style=flat-square) ![AppVersion: 26.4.2](https://img.shields.io/badge/AppVersion-26.4.2-informational?style=flat-square) ![Maintenance Track: bb_integrated](https://img.shields.io/badge/Maintenance_Track-bb_integrated-green?style=flat-square)

Keycloak.X - Open Source Identity and Access Management for Modern Applications and Services

## Upstream References

- <https://www.keycloak.org/>
- <https://github.com/codecentric/helm-charts>
- <https://github.com/keycloak/keycloak/tree/main/quarkus/container>
- <https://github.com/bitnami/charts/tree/main/bitnami/postgresql>

## Upstream Release Notes

- [Find upstream chart's release notes and CHANGELOG here](https://github.com/codecentric/helm-charts/releases)
- [Find upstream keycloak project's release notes and CHANGELOG here](https://www.keycloak.org/docs/latest/release_notes/index.html)

## Learn More

- [Application Overview](docs/overview.md)
- [Other Documentation](docs/)

## Pre-Requisites

- Kubernetes Cluster deployed
- Kubernetes config installed in `~/.kube/config`
- Helm installed

Install Helm

https://helm.sh/docs/intro/install/

## Deployment

- Clone down the repository
- cd into directory

```bash
helm install keycloak chart/
```

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| domain | string | `"dev.bigbang.mil"` | The base domain for all Big Bang components. Keycloak will be available at keycloak.%domain% |
| istio.enabled | bool | `false` | Enable or disable Istio |
| istio.hardened.enabled | bool | `false` | Enable or disable istio's hardened mode |
| istio.hardened.customAuthorizationPolicies | list | `[]` | Custom authorization policies to be applied to the keycloak namespace |
| istio.hardened.outboundTrafficPolicyMode | string | `"REGISTRY_ONLY"` | Specify the Istio outbound traffic policy mode |
| istio.hardened.customServiceEntries | list | `[]` | Custom service entries to be applied to the keycloak namespace |
| istio.mtls.mode | string | `"STRICT"` | PERMISSIVE = Allow both plain text and mutual TLS traffic |
| istio.keycloak.enabled | bool | `false` | Enable or disable the istio virtual service for keycloak |
| istio.keycloak.annotations | object | `{}` | Additional annotations to be added to the istio virtual service |
| istio.keycloak.labels | object | `{}` | Additional labels to be added to the istio virtual service |
| istio.keycloak.gateways | list | `["istio-gateway/passthrough-ingressgateway"]` | Specify the istio gateways to be used for keycloak |
| istio.keycloak.hosts | list | `["keycloak.{{ .Values.domain }}"]` | Specify the hostnames from which keycloak will be accessible |
| networkPolicies.enabled | bool | `true` | Enable or disable the bundled network policies |
| networkPolicies.ingressLabels | object | `{"app":"istio-ingressgateway","istio":"ingressgateway"}` | Configures labelSelectors for network policies allowing ingress from istio gateways |
| networkPolicies.ingress | object | `{"to":{"keycloak":{"from":{"definition":{"gateway":true}}}}}` | Configures additional network policies beyond the ones bundled with the chart, using the bb-common shorthand |
| networkPolicies.egress.from.*.to.definition.kubeAPI | bool | `true` |  |
| networkPolicies.additionalPolicies | list | `[]` |  |
| bbtests.enabled | bool | `false` | Enables the Big Bang test hooks |
| bbtests.image | string | `"registry1.dso.mil/ironbank/big-bang/base:2.1.0"` |  |
| bbtests.cypress.artifacts | bool | `true` |  |
| bbtests.cypress.envs.cypress_url | string | `"http://keycloak-keycloak-http.keycloak.svc.cluster.local"` |  |
| bbtests.cypress.envs.cypress_username | string | `"admin"` |  |
| bbtests.cypress.envs.cypress_password | string | `"password"` |  |
| bbtests.cypress.envs.cypress_tnr_username | string | `"cypress"` |  |
| bbtests.cypress.envs.cypress_tnr_password | string | `"tnr_w!G33ZyAt@C8"` |  |
| bbtests.cypress.envs.tnr_username | string | `"cypress"` |  |
| bbtests.cypress.envs.tnr_password | string | `"tnr_w!G33ZyAt@C8"` |  |
| bbtests.cypress.envs.tnr_firstName | string | `"Cypress"` |  |
| bbtests.cypress.envs.tnr_lastName | string | `"TNR"` |  |
| bbtests.cypress.envs.tnr_email | string | `"cypress@tnr.mil"` |  |
| bbtests.scripts.envs.HEADLESS_SERVICE | string | `"keycloak-keycloak-headless.keycloak.svc.cluster.local"` |  |
| bbtests.scripts.envs.PORT | string | `"7800"` |  |
| bbtests.scripts.envs.TIMEOUT | string | `"10"` |  |
| upstream.fullnameOverride | string | `"keycloak-keycloak"` |  |
| upstream.nameOverride | string | `"keycloak"` |  |
| upstream.podAnnotations."traffic.sidecar.istio.io/excludeInboundPorts" | string | `"9000"` |  |
| upstream.podAnnotations."proxy.istio.io/config" | string | `"proxyMetadata:\n  ISTIO_META_DNS_CAPTURE: \"true\"\n"` |  |
| upstream.replicas | int | `1` |  |
| upstream.image.repository | string | `"registry1.dso.mil/ironbank/opensource/keycloak/keycloak"` | The Keycloak image repository |
| upstream.image.tag | string | `"26.4.2"` |  |
| upstream.podSecurityContext | object | `{"fsGroup":2000,"runAsGroup":2000,"runAsNonRoot":true,"runAsUser":2000}` | SecurityContext for the entire Pod. Every container running in the Pod will inherit this SecurityContext. This might be relevant when other components of the environment inject additional containers into running Pods (service meshes are the most prominent example for this) |
| upstream.securityContext | object | `{"capabilities":{"drop":["ALL"]},"runAsGroup":2000,"runAsNonRoot":true,"runAsUser":2000}` | SecurityContext for the Keycloak container |
| upstream.args | list | `["start"]` | Overrides the default args for the Keycloak container **arg: "start" needs to be set for the container to start up properly** |
| upstream.extraEnvFrom | string | `"- secretRef:\n    name: '{{ include \"keycloak.fullname\" . }}-env'\n"` | Additional environment variables for Keycloak mapped from Secret or ConfigMap |
| upstream.resources | object | `{"limits":{"memory":"1Gi"},"requests":{"cpu":"1","memory":"1Gi"}}` | Pod resource requests and limits |
| upstream.secrets | object | `{"env":{"stringData":{"JAVA_OPTS_APPEND":"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless","JAVA_TOOL_OPTIONS":"-Dcom.redhat.fips=false","KC_HOSTNAME":"keycloak.dev.bigbang.mil","KEYCLOAK_ADMIN":"admin","KEYCLOAK_ADMIN_PASSWORD":"password"}}}` | Configuration for secrets that should be created The secrets can also be independently created separate from this helm chart. for example with a gitops tool like flux with a kustomize overlay. NOTE: Secret values can be templated |
| upstream.secrets.env | object | `{"stringData":{"JAVA_OPTS_APPEND":"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless","JAVA_TOOL_OPTIONS":"-Dcom.redhat.fips=false","KC_HOSTNAME":"keycloak.dev.bigbang.mil","KEYCLOAK_ADMIN":"admin","KEYCLOAK_ADMIN_PASSWORD":"password"}}` | Environmental variables |
| upstream.secrets.env.stringData.JAVA_TOOL_OPTIONS | string | `"-Dcom.redhat.fips=false"` | https://docs.redhat.com/en/documentation/red_hat_build_of_openjdk/11/html-single/configuring_red_hat_build_of_openjdk_11_on_rhel_with_fips/index |
| upstream.secrets.env.stringData.KEYCLOAK_ADMIN | string | `"admin"` | default admin credentials. Override them for production deployments |
| upstream.secrets.env.stringData.JAVA_OPTS_APPEND | string | `"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless"` | https://www.keycloak.org/server/caching |
| upstream.dbchecker.enabled | bool | `false` | If `true`, the dbchecker init container is enabled; this is incompatible with Big Bang and so is disabled by default. |
| upstream.database | object | `{"database":"keycloak","hostname":"keycloak-keycloak-postgresql","password":"keycloak","port":5432,"username":"keycloak","vendor":"postgres"}` | Configures the database connection; can be configured here and/or via environment variables with `upstream.secrets.env` |
| upstream.database.hostname | string | `"keycloak-keycloak-postgresql"` | you will need to change the hostname to match : %fullnameOverride%-postgresql |
| postgresql.enabled | bool | `true` | If `true`, the Postgresql dependency is enabled |
| postgresql.image.registry | string | `"registry1.dso.mil"` |  |
| postgresql.image.repository | string | `"ironbank/bitnami/postgres"` |  |
| postgresql.image.tag | string | `"17.4.0"` |  |
| postgresql.global.security.allowInsecureImages | bool | `true` | Allow registry1.dso.mil in lieu of the default bitnami registry |
| postgresql.global.postgresql.auth.username | string | `"keycloak"` | PostgreSQL User to create |
| postgresql.global.postgresql.auth.password | string | `"keycloak"` | PostgreSQL Password for the new user |
| postgresql.global.postgresql.auth.database | string | `"keycloak"` | PostgreSQL Database to create |

## Contributing

Please see the [contributing guide](./CONTRIBUTING.md) if you are interested in contributing.

---

_This file is programatically generated using `helm-docs` and some BigBang-specific templates. The `gluon` repository has [instructions for regenerating package READMEs](https://repo1.dso.mil/big-bang/product/packages/gluon/-/blob/master/docs/bb-package-readme.md)._

