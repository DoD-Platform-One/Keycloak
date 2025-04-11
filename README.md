<!-- Warning: Do not manually edit this file. See notes on gluon + helm-docs at the end of this file for more information. -->
# keycloak

![Version: 7.0.1-bb.0](https://img.shields.io/badge/Version-7.0.1--bb.0-informational?style=flat-square) ![AppVersion: 26.1.4](https://img.shields.io/badge/AppVersion-26.1.4-informational?style=flat-square) ![Maintenance Track: bb_integrated](https://img.shields.io/badge/Maintenance_Track-bb_integrated-green?style=flat-square)

Keycloak.X - Open Source Identity and Access Management for Modern Applications and Services

## Upstream References

- <https://www.keycloak.org/>
- <https://github.com/codecentric/helm-charts>
- <https://github.com/keycloak/keycloak/tree/main/quarkus/container>
- <https://github.com/bitnami/charts/tree/master/bitnami/postgresql>

## Upstream Release Notes

- [Find upstream chart's release notes and CHANGELOG here](https://www.keycloak.org/docs/latest/release_notes/index.html)

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
| fullnameOverride | string | `""` | Optionally override the fully qualified name |
| nameOverride | string | `""` | Optionally override the name |
| replicas | int | `1` | The number of replicas to create (has no effect if autoscaling enabled) |
| image.repository | string | `"registry1.dso.mil/ironbank/opensource/keycloak/keycloak"` | The Keycloak image repository |
| image.tag | string | `"26.1.4"` |  |
| image.digest | string | `""` |  |
| image.pullPolicy | string | `"IfNotPresent"` | The Keycloak image pull policy |
| imagePullSecrets | list | `[{"name":"private-registry"}]` | Image pull secrets for the Pod |
| hostAliases | list | `[]` | Mapping between IPs and hostnames that will be injected as entries in the Pod's hosts files |
| enableServiceLinks | bool | `true` | Indicates whether information about services should be injected into Pod's environment variables, matching the syntax of Docker links |
| podManagementPolicy | string | `"Parallel"` | Pod management policy. One of `Parallel` or `OrderedReady` |
| updateStrategy | string | `"RollingUpdate"` | StatefulSet's update strategy |
| restartPolicy | string | `"Always"` | Pod restart policy. One of `Always`, `OnFailure`, or `Never` |
| serviceAccount.create | bool | `true` | Specifies whether a ServiceAccount should be created |
| serviceAccount.allowReadPods | bool | `false` | Specifies whether the ServiceAccount can get and list pods |
| serviceAccount.name | string | `""` | The name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| serviceAccount.annotations | object | `{}` | Additional annotations for the ServiceAccount |
| serviceAccount.labels | object | `{}` | Additional labels for the ServiceAccount |
| serviceAccount.imagePullSecrets | list | `[]` | Image pull secrets that are attached to the ServiceAccount |
| serviceAccount.automountServiceAccountToken | bool | `true` | Automount API credentials for the Service Account |
| rbac.create | bool | `false` |  |
| rbac.rules | list | `[]` |  |
| podSecurityContext | object | `{"fsGroup":2000,"runAsGroup":2000,"runAsNonRoot":true,"runAsUser":2000}` | SecurityContext for the entire Pod. Every container running in the Pod will inherit this SecurityContext. This might be relevant when other components of the environment inject additional containers into running Pods (service meshes are the most prominent example for this) |
| securityContext | object | `{"capabilities":{"drop":["ALL"]},"runAsGroup":2000,"runAsNonRoot":true,"runAsUser":2000}` | SecurityContext for the Keycloak container |
| extraInitContainers | string | `""` | Additional init containers, e. g. for providing custom themes |
| skipInitContainers | bool | `false` | When using service meshes which rely on a sidecar, it may be necessary to skip init containers altogether, since the sidecar doesn't start until the init containers are done, and the sidecar may be required for network access. For example, Istio in strict mTLS mode prevents the dbchecker init container from ever completing |
| extraContainers | string | `""` | Additional sidecar containers, e. g. for a database proxy, such as Google's cloudsql-proxy |
| lifecycleHooks | string | `""` | Lifecycle hooks for the Keycloak container |
| terminationGracePeriodSeconds | int | `60` | Termination grace period in seconds for Keycloak shutdown. Clusters with a large cache might need to extend this to give Infinispan more time to rebalance |
| clusterDomain | string | `"cluster.local"` | The internal Kubernetes cluster domain |
| command | list | `[]` | Overrides the default entrypoint of the Keycloak container |
| args | list | `["start"]` | Overrides the default args for the Keycloak container **arg: "start" needs to be set for the container to start up properly** |
| extraEnv | string | `""` | Additional environment variables for Keycloak Any environment variables defined directly in the statefulset should be set with the appropriate values rather than set here, which will potentially produce duplicates and helm upgrade errors https://www.keycloak.org/server/all-config |
| extraEnvFrom | string | `"- secretRef:\n    name: '{{ include \"keycloak.fullname\" . }}-env'\n"` | Additional environment variables for Keycloak mapped from Secret or ConfigMap |
| priorityClassName | string | `""` | Pod priority class name |
| affinity | string | `"podAntiAffinity:\n  requiredDuringSchedulingIgnoredDuringExecution:\n    - labelSelector:\n        matchLabels:\n          {{- include \"keycloak.selectorLabels\" . \| nindent 10 }}\n        matchExpressions:\n          - key: app.kubernetes.io/component\n            operator: NotIn\n            values:\n              - test\n      topologyKey: kubernetes.io/hostname\n  preferredDuringSchedulingIgnoredDuringExecution:\n    - weight: 100\n      podAffinityTerm:\n        labelSelector:\n          matchLabels:\n            {{- include \"keycloak.selectorLabels\" . \| nindent 12 }}\n          matchExpressions:\n            - key: app.kubernetes.io/component\n              operator: NotIn\n              values:\n                - test\n        topologyKey: topology.kubernetes.io/zone\n"` | Pod affinity |
| topologySpreadConstraints | string | `nil` | Topology spread constraints template |
| nodeSelector | object | `{}` | Node labels for Pod assignment |
| tolerations | list | `[]` | Node taints to tolerate |
| podLabels | object | `{}` | Additional Pod labels |
| podAnnotations | object | `{}` | Additional Pod annotations |
| livenessProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\n  scheme: HTTP\nfailureThreshold: 15\ntimeoutSeconds: 2\nperiodSeconds: 15\ninitialDelaySeconds: 0\n"` | Liveness probe configuration |
| readinessProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\n  scheme: HTTP\nfailureThreshold: 15\ntimeoutSeconds: 2\ninitialDelaySeconds: 10\n"` | Readiness probe configuration |
| startupProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\ninitialDelaySeconds: 90\ntimeoutSeconds: 2\nfailureThreshold: 60\nperiodSeconds: 5\n"` | Startup probe configuration |
| resources | object | `{"limits":{"memory":"1Gi"},"requests":{"cpu":"1","memory":"1Gi"}}` | Pod resource requests and limits |
| extraVolumes | string | `""` | Add additional volumes, e. g. for custom themes |
| extraVolumesBigBang | object | `{}` | This values key is reserved for integration with BigBang chart |
| extraVolumeMounts | string | `""` | Add additional volumes mounts, e. g. for custom themes |
| extraVolumeMountsBigBang | object | `{}` | This values key is reserved for integration with BigBang chart |
| extraPorts | list | `[]` | Add additional ports, e. g. for admin console or exposing JGroups ports |
| podDisruptionBudget | object | `{}` | Pod disruption budget |
| statefulsetAnnotations | object | `{}` | Annotations for the StatefulSet |
| statefulsetLabels | object | `{}` | Additional labels for the StatefulSet |
| secrets | object | `{"env":{"stringData":{"JAVA_OPTS_APPEND":"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless","JAVA_TOOL_OPTIONS":"-Dcom.redhat.fips=false","KEYCLOAK_ADMIN":"admin","KEYCLOAK_ADMIN_PASSWORD":"password"}}}` | Configuration for secrets that should be created The secrets can also be independently created separate from this helm chart. for example with a gitops tool like flux with a kustomize overlay. |
| secrets.env | object | `{"stringData":{"JAVA_OPTS_APPEND":"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless","JAVA_TOOL_OPTIONS":"-Dcom.redhat.fips=false","KEYCLOAK_ADMIN":"admin","KEYCLOAK_ADMIN_PASSWORD":"password"}}` | Environmental variables |
| secrets.env.stringData.JAVA_TOOL_OPTIONS | string | `"-Dcom.redhat.fips=false"` | https://access.redhat.com/documentation/en-us/openjdk/11/html-single/configuring_openjdk_11_on_rhel_with_fips/index |
| secrets.env.stringData.KEYCLOAK_ADMIN | string | `"admin"` | default admin credentials. Override them for production deployments |
| secrets.env.stringData.JAVA_OPTS_APPEND | string | `"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless"` | https://www.keycloak.org/server/caching |
| service.annotations | object | `{}` | Annotations for HTTP service |
| service.labels | object | `{}` | Additional labels for headless and HTTP Services |
| service.type | string | `"ClusterIP"` | The Service type |
| service.loadBalancerIP | string | `""` | Optional IP for the load balancer. Used for services of type LoadBalancer only |
| service.httpPort | int | `80` | The http Service port |
| service.httpNodePort | string | `nil` | The HTTP Service node port if type is NodePort |
| service.httpsPort | int | `8443` | The HTTPS Service port |
| service.httpsNodePort | string | `nil` | The HTTPS Service node port if type is NodePort |
| service.extraPorts | list | `[]` | Additional Service ports, e. g. for custom admin console |
| service.loadBalancerSourceRanges | list | `[]` | When using Service type LoadBalancer, you can restrict source ranges allowed to connect to the LoadBalancer, e. g. will result in Security Groups (or equivalent) with inbound source ranges allowed to connect |
| service.externalTrafficPolicy | string | `"Cluster"` | When using Service type LoadBalancer, you can preserve the source IP seen in the container by changing the default (Cluster) to be Local. See https://kubernetes.io/docs/tasks/access-application-cluster/create-external-load-balancer/#preserving-the-client-source-ip |
| service.sessionAffinity | string | `""` | Session affinity See https://kubernetes.io/docs/concepts/services-networking/service/#proxy-mode-userspace |
| service.sessionAffinityConfig | object | `{}` | Session affinity config |
| serviceHeadless.annotations | object | `{}` | Annotations for headless service |
| ingress.enabled | bool | `false` | If `true`, an Ingress is created |
| ingress.ingressClassName | string | `""` | The name of the Ingress Class associated with this ingress |
| ingress.servicePort | string | `"http"` | The Service port targeted by the Ingress |
| ingress.annotations | object | `{}` | Ingress annotations |
| ingress.labels | object | `{}` | Additional Ingress labels |
| ingress.rules | list | `[{"host":"{{ .Release.Name }}.keycloak.example.com","paths":[{"path":"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/","pathType":"Prefix"}]}]` | List of rules for the Ingress |
| ingress.rules[0] | object | `{"host":"{{ .Release.Name }}.keycloak.example.com","paths":[{"path":"{{ tpl .Values.http.relativePath $ \\| trimSuffix \"/\" }}/","pathType":"Prefix"}]}` | Ingress hostname |
| ingress.rules[0].paths | list | `[{"path":"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/","pathType":"Prefix"}]` | Paths for the host |
| ingress.console | object | `{"annotations":{},"enabled":false,"ingressClassName":"","rules":[{"host":"{{ .Release.Name }}.keycloak.example.com","paths":[{"path":"{{ tpl .Values.http.relativePath $ \\| trimSuffix \"/\" }}/admin","pathType":"Prefix"}]}],"tls":[]}` | ingress for console only (/auth/admin) |
| ingress.console.enabled | bool | `false` | If `true`, an Ingress is created for console path only |
| ingress.console.ingressClassName | string | `""` | The name of Ingress Class associated with the console ingress only |
| ingress.console.annotations | object | `{}` | Ingress annotations for console ingress only Useful to set nginx.ingress.kubernetes.io/whitelist-source-range particularly |
| ingress.console.rules[0].host | string | `"{{ .Release.Name }}.keycloak.example.com"` | Ingress host |
| ingress.console.rules[0].paths | list | `[{"path":"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/admin","pathType":"Prefix"}]` | Paths for the host |
| ingress.console.tls | list | `[]` | Console TLS configuration |
| networkPolicy | object | `{"egress":[],"enabled":false,"extraFrom":[],"labels":{}}` | Network policy configuration https://kubernetes.io/docs/concepts/services-networking/network-policies/ |
| networkPolicy.enabled | bool | `false` | If true, the Network policies are deployed |
| networkPolicy.labels | object | `{}` | Additional Network policy labels |
| networkPolicy.extraFrom | list | `[]` | Define all other external allowed source See https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.25/#networkpolicypeer-v1-networking-k8s-io |
| networkPolicy.egress | list | `[]` | Define egress networkpolicies for the Keycloak pods (external database for example) See https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.25/#networkpolicyegressrule-v1-networking-k8s-io |
| route.enabled | bool | `false` | If `true`, an OpenShift Route is created |
| route.path | string | `"/"` | Path for the Route |
| route.annotations | object | `{}` | Route annotations |
| route.labels | object | `{}` | Additional Route labels |
| route.host | string | `""` | Host name for the Route |
| route.tls | object | `{"enabled":true,"insecureEdgeTerminationPolicy":"Redirect","termination":"edge"}` | TLS configuration |
| route.tls.enabled | bool | `true` | If `true`, TLS is enabled for the Route |
| route.tls.insecureEdgeTerminationPolicy | string | `"Redirect"` | Insecure edge termination policy of the Route. Can be `None`, `Redirect`, or `Allow` |
| route.tls.termination | string | `"edge"` | TLS termination of the route. Can be `edge`, `passthrough`, or `reencrypt` |
| dbchecker.enabled | bool | `true` | If `true`, the dbchecker init container is enabled |
| dbchecker.image.repository | string | `"registry1.dso.mil/ironbank/opensource/postgres/postgresql-alpine"` | Docker image used to check Postgresql readiness at startup |
| dbchecker.image.tag | string | `"15.10"` | Image tag for the pgchecker image |
| dbchecker.image.pullPolicy | string | `"IfNotPresent"` | Image pull policy for the dbchecker image |
| dbchecker.securityContext | object | `{"allowPrivilegeEscalation":false,"capabilities":{"drop":["ALL"]},"runAsGroup":1000,"runAsNonRoot":true,"runAsUser":1000}` | SecurityContext for the dbchecker container |
| dbchecker.resources | object | `{"limits":{"memory":"256Mi"},"requests":{"cpu":"250m","memory":"256Mi"}}` | Resource requests and limits for the dbchecker container |
| postgresql.enabled | bool | `true` | If `true`, the Postgresql dependency is enabled |
| postgresql.auth.username | string | `"keycloak"` | PostgreSQL User to create |
| postgresql.auth.password | string | `"keycloak"` | PostgreSQL Password for the new user |
| postgresql.auth.database | string | `"keycloak"` | PostgreSQL Database to create |
| postgresql.networkPolicy | object | `{"enabled":false}` | PostgreSQL network policy configuration |
| postgresql.global.imagePullSecrets[0] | string | `"private-registry"` |  |
| postgresql.image.registry | string | `"registry1.dso.mil"` |  |
| postgresql.image.repository | string | `"ironbank/opensource/postgres/postgresql-alpine"` |  |
| postgresql.image.tag | string | `"15.10"` |  |
| postgresql.service.port | int | `5432` |  |
| postgresql.primary.podSecurityContext.enabled | bool | `true` |  |
| postgresql.primary.podSecurityContext.fsGroup | int | `1001` |  |
| postgresql.primary.containerSecurityContext.enabled | bool | `true` |  |
| postgresql.primary.containerSecurityContext.runAsUser | int | `1001` |  |
| postgresql.primary.containerSecurityContext.runAsGroup | int | `1001` |  |
| postgresql.primary.containerSecurityContext.runAsNonRoot | bool | `true` |  |
| postgresql.primary.containerSecurityContext.allowPrivilegeEscalation | bool | `false` |  |
| postgresql.primary.containerSecurityContext.seccompProfile.type | string | `"RuntimeDefault"` |  |
| postgresql.primary.containerSecurityContext.capabilities.drop[0] | string | `"ALL"` |  |
| database | object | `{"database":null,"existingSecret":"","existingSecretKey":"","hostname":null,"password":null,"port":null,"username":null,"vendor":null}` | If the database is not managed by this chart, you can use these keys to configure the connection |
| database.existingSecret | string | `""` | name of the existing secret containing the database password |
| database.existingSecretKey | string | `""` | key in the existing secret containing the database password |
| database.vendor | string | `nil` | E.g. dev-file, dev-mem, mariadb, mssql, mysql, oracle or postgres |
| database.hostname | string | `nil` | The database host |
| database.port | string | `nil` | The database port |
| database.database | string | `nil` | The database name |
| database.username | string | `nil` | The database username |
| database.password | string | `nil` | The database password (ignored if existingSecret is set) |
| cache.stack | string | `"default"` | Use "custom" to disable automatic cache configuration |
| proxy.enabled | bool | `true` |  |
| proxy.mode | string | `"forwarded"` |  |
| proxy.http.enabled | bool | `true` |  |
| metrics.enabled | bool | `true` |  |
| health.enabled | bool | `true` |  |
| http.relativePath | string | `"/auth"` | For backwards compatibility reasons we set this to the value used by previous Keycloak versions. |
| http.internalPort | string | `"http-internal"` |  |
| http.internalScheme | string | `"HTTP"` |  |
| serviceMonitor.enabled | bool | `false` | If `true`, a ServiceMonitor resource for the prometheus-operator is created |
| serviceMonitor.namespace | string | `""` | Optionally sets a target namespace in which to deploy the ServiceMonitor resource |
| serviceMonitor.namespaceSelector | object | `{}` | Optionally sets a namespace for the ServiceMonitor |
| serviceMonitor.annotations | object | `{}` | Annotations for the ServiceMonitor |
| serviceMonitor.labels | object | `{}` | Additional labels for the ServiceMonitor |
| serviceMonitor.interval | string | `"10s"` | Interval at which Prometheus scrapes metrics |
| serviceMonitor.scrapeTimeout | string | `"10s"` | Timeout for scraping |
| serviceMonitor.path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/metrics"` | The path at which metrics are served |
| serviceMonitor.port | string | `"{{ .Values.http.internalPort }}"` | The Service port at which metrics are served |
| serviceMonitor.scheme | string | `""` |  |
| serviceMonitor.tlsConfig | object | `{}` |  |
| extraServiceMonitor.enabled | bool | `false` | If `true`, a ServiceMonitor resource for the prometheus-operator is created |
| extraServiceMonitor.namespace | string | `""` | Optionally sets a target namespace in which to deploy the ServiceMonitor resource |
| extraServiceMonitor.namespaceSelector | object | `{}` | Optionally sets a namespace for the ServiceMonitor |
| extraServiceMonitor.annotations | object | `{}` | Annotations for the ServiceMonitor |
| extraServiceMonitor.labels | object | `{}` | Additional labels for the ServiceMonitor |
| extraServiceMonitor.interval | string | `"10s"` | Interval at which Prometheus scrapes metrics |
| extraServiceMonitor.scrapeTimeout | string | `"10s"` | Timeout for scraping |
| extraServiceMonitor.path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/metrics"` | The path at which metrics are served |
| extraServiceMonitor.port | string | `"{{ .Values.http.internalPort }}"` | The Service port at which metrics are served |
| extraServiceMonitor.scheme | string | `""` |  |
| extraServiceMonitor.tlsConfig | object | `{}` |  |
| prometheusRule.enabled | bool | `false` | If `true`, a PrometheusRule resource for the prometheus-operator is created |
| prometheusRule.namespace | string | `""` | Optionally sets a target namespace in which to deploy the ServiceMonitor resource |
| prometheusRule.annotations | object | `{}` | Annotations for the PrometheusRule |
| prometheusRule.labels | object | `{}` | Additional labels for the PrometheusRule |
| prometheusRule.rules | list | `[]` | List of rules for Prometheus |
| autoscaling.enabled | bool | `false` | If `true`, an autoscaling/v2 HorizontalPodAutoscaler resource is created (requires Kubernetes 1.23 or above) Autoscaling seems to be most reliable when using KUBE_PING service discovery (see README for details) This disables the `replicas` field in the StatefulSet |
| autoscaling.labels | object | `{}` | Additional HorizontalPodAutoscaler labels |
| autoscaling.minReplicas | int | `3` | The minimum and maximum number of replicas for the Keycloak StatefulSet |
| autoscaling.maxReplicas | int | `10` |  |
| autoscaling.metrics | list | `[{"resource":{"name":"cpu","target":{"averageUtilization":80,"type":"Utilization"}},"type":"Resource"}]` | The metrics to use for scaling |
| autoscaling.behavior | object | `{"scaleDown":{"policies":[{"periodSeconds":300,"type":"Pods","value":1}],"stabilizationWindowSeconds":300}}` | The scaling policy to use. This will scale up quickly but only scale down a single Pod per 5 minutes. This is important because caches are usually only replicated to 2 Pods and if one of those Pods is terminated this will give the cluster time to recover. |
| test.enabled | bool | `false` | If `true`, test resources are created |
| test.image.repository | string | `"docker.io/seleniarm/standalone-chromium"` | The image for the test Pod |
| test.image.tag | string | `"117.0"` | The tag for the test Pod image |
| test.image.pullPolicy | string | `"IfNotPresent"` | The image pull policy for the test Pod image |
| test.podSecurityContext | object | `{"fsGroup":1000}` | SecurityContext for the entire test Pod |
| test.securityContext | object | `{"runAsNonRoot":true,"runAsUser":1000}` | SecurityContext for the test container |
| test.deletionPolicy | string | `"before-hook-creation"` | See https://helm.sh/docs/topics/charts_hooks/#hook-deletion-policies |
| domain | string | `"dev.bigbang.mil"` | Your FQDN will be ${ .Values.subdomain }.${ .Values.domain } |
| istio.enabled | bool | `false` | Toggle istio integration |
| istio.hardened | object | `{"customAuthorizationPolicies":[],"customServiceEntries":[],"enabled":false,"outboundTrafficPolicyMode":"REGISTRY_ONLY"}` | Toggle istio hardening |
| istio.injection | string | `"disabled"` |  |
| istio.mtls.mode | string | `"STRICT"` | PERMISSIVE = Allow both plain text and mutual TLS traffic |
| istio.keycloak.enabled | bool | `false` | Toggle vs creation |
| istio.keycloak.annotations | object | `{}` |  |
| istio.keycloak.labels | object | `{}` |  |
| istio.keycloak.gateways[0] | string | `"istio-system/main"` |  |
| istio.keycloak.hosts[0] | string | `"keycloak.{{ .Values.domain }}"` |  |
| monitoring.enabled | bool | `false` |  |
| networkPolicies.enabled | bool | `false` |  |
| networkPolicies.ingressLabels.app | string | `"istio-ingressgateway"` |  |
| networkPolicies.ingressLabels.istio | string | `"ingressgateway"` |  |
| networkPolicies.smtpPort | int | `587` |  |
| networkPolicies.ldap.enabled | bool | `false` |  |
| networkPolicies.ldap.cidr | string | `"X.X.X.X/X"` |  |
| networkPolicies.ldap.port | int | `636` |  |
| networkPolicies.additionalPolicies | list | `[]` |  |
| openshift | bool | `false` |  |
| bbtests.enabled | bool | `false` |  |
| bbtests.image | string | `"registry1.dso.mil/ironbank/big-bang/base:2.1.0"` |  |
| bbtests.cypress.artifacts | bool | `true` |  |
| bbtests.cypress.envs.cypress_url | string | `"http://keycloak-http.keycloak.svc.cluster.local"` |  |
| bbtests.cypress.envs.cypress_username | string | `"admin"` |  |
| bbtests.cypress.envs.cypress_password | string | `"password"` |  |
| bbtests.cypress.envs.cypress_tnr_username | string | `"cypress"` |  |
| bbtests.cypress.envs.cypress_tnr_password | string | `"tnr_w!G33ZyAt@C8"` |  |
| bbtests.cypress.envs.tnr_username | string | `"cypress"` |  |
| bbtests.cypress.envs.tnr_password | string | `"tnr_w!G33ZyAt@C8"` |  |
| bbtests.cypress.envs.tnr_firstName | string | `"Cypress"` |  |
| bbtests.cypress.envs.tnr_lastName | string | `"TNR"` |  |
| bbtests.cypress.envs.tnr_email | string | `"cypress@tnr.mil"` |  |

## Contributing

Please see the [contributing guide](./CONTRIBUTING.md) if you are interested in contributing.

---

_This file is programatically generated using `helm-docs` and some BigBang-specific templates. The `gluon` repository has [instructions for regenerating package READMEs](https://repo1.dso.mil/big-bang/product/packages/gluon/-/blob/master/docs/bb-package-readme.md)._

