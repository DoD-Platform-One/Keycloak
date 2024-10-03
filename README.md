<!-- Warning: Do not manually edit this file. See notes on gluon + helm-docs at the end of this file for more information. -->
# keycloak

![Version: 2.5.1-bb.0](https://img.shields.io/badge/Version-2.5.1--bb.0-informational?style=flat-square) ![AppVersion: 25.0.6](https://img.shields.io/badge/AppVersion-25.0.6-informational?style=flat-square)

Keycloak.X - Open Source Identity and Access Management for Modern Applications and Services

## Upstream References
* <https://www.keycloak.org/>

* <https://github.com/codecentric/helm-charts>
* <https://github.com/keycloak/keycloak/tree/main/quarkus/container>
* <https://github.com/bitnami/charts/tree/master/bitnami/postgresql>

### Upstream Release Notes

* [Find upstream chart's release notes and CHANGELOG here](https://www.keycloak.org/docs/latest/release_notes/index.html)

## Learn More
* [Application Overview](docs/overview.md)
* [Other Documentation](docs/)

## Pre-Requisites

* Kubernetes Cluster deployed
* Kubernetes config installed in `~/.kube/config`
* Helm installed

Install Helm

https://helm.sh/docs/intro/install/

## Deployment

* Clone down the repository
* cd into directory
```bash
helm install keycloak chart/
```

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| fullnameOverride | string | `""` |  |
| nameOverride | string | `""` |  |
| replicas | int | `1` |  |
| image.repository | string | `"registry1.dso.mil/ironbank/opensource/keycloak/keycloak"` |  |
| image.tag | string | `"25.0.6"` |  |
| image.digest | string | `""` |  |
| image.pullPolicy | string | `"IfNotPresent"` |  |
| imagePullSecrets[0].name | string | `"private-registry"` |  |
| hostAliases | list | `[]` |  |
| enableServiceLinks | bool | `true` |  |
| podManagementPolicy | string | `"Parallel"` |  |
| updateStrategy | string | `"RollingUpdate"` |  |
| restartPolicy | string | `"Always"` |  |
| serviceAccount.create | bool | `true` |  |
| serviceAccount.allowReadPods | bool | `false` |  |
| serviceAccount.name | string | `""` |  |
| serviceAccount.annotations | object | `{}` |  |
| serviceAccount.labels | object | `{}` |  |
| serviceAccount.imagePullSecrets | list | `[]` |  |
| serviceAccount.automountServiceAccountToken | bool | `true` |  |
| rbac.create | bool | `false` |  |
| rbac.rules | list | `[]` |  |
| podSecurityContext.fsGroup | int | `2000` |  |
| podSecurityContext.runAsUser | int | `2000` |  |
| podSecurityContext.runAsGroup | int | `2000` |  |
| podSecurityContext.runAsNonRoot | bool | `true` |  |
| securityContext.runAsUser | int | `2000` |  |
| securityContext.runAsGroup | int | `2000` |  |
| securityContext.runAsNonRoot | bool | `true` |  |
| securityContext.capabilities.drop[0] | string | `"ALL"` |  |
| extraInitContainers | string | `""` |  |
| skipInitContainers | bool | `false` |  |
| extraContainers | string | `""` |  |
| lifecycleHooks | string | `""` |  |
| terminationGracePeriodSeconds | int | `60` |  |
| clusterDomain | string | `"cluster.local"` |  |
| command | list | `[]` |  |
| args[0] | string | `"start"` |  |
| extraEnv | string | `""` |  |
| extraEnvFrom | string | `"- secretRef:\n    name: '{{ include \"keycloak.fullname\" . }}-env'\n"` |  |
| priorityClassName | string | `""` |  |
| affinity | string | `"podAntiAffinity:\n  requiredDuringSchedulingIgnoredDuringExecution:\n    - labelSelector:\n        matchLabels:\n          {{- include \"keycloak.selectorLabels\" . \| nindent 10 }}\n        matchExpressions:\n          - key: app.kubernetes.io/component\n            operator: NotIn\n            values:\n              - test\n      topologyKey: kubernetes.io/hostname\n  preferredDuringSchedulingIgnoredDuringExecution:\n    - weight: 100\n      podAffinityTerm:\n        labelSelector:\n          matchLabels:\n            {{- include \"keycloak.selectorLabels\" . \| nindent 12 }}\n          matchExpressions:\n            - key: app.kubernetes.io/component\n              operator: NotIn\n              values:\n                - test\n        topologyKey: topology.kubernetes.io/zone\n"` |  |
| topologySpreadConstraints | string | `nil` |  |
| nodeSelector | object | `{}` |  |
| tolerations | list | `[]` |  |
| podLabels | object | `{}` |  |
| podAnnotations | object | `{}` |  |
| livenessProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\n  scheme: HTTP\nfailureThreshold: 15\ntimeoutSeconds: 2\nperiodSeconds: 15\ninitialDelaySeconds: 0\n"` |  |
| readinessProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\n  scheme: HTTP\nfailureThreshold: 15\ntimeoutSeconds: 2\ninitialDelaySeconds: 10\n"` |  |
| startupProbe | string | `"httpGet:\n  path: /auth/realms/master\n  port: http\ninitialDelaySeconds: 90\ntimeoutSeconds: 2\nfailureThreshold: 60\nperiodSeconds: 5\n"` |  |
| resources.requests.cpu | string | `"1"` |  |
| resources.requests.memory | string | `"1Gi"` |  |
| resources.limits.cpu | string | `"1"` |  |
| resources.limits.memory | string | `"1Gi"` |  |
| extraVolumes | string | `""` |  |
| extraVolumesBigBang | object | `{}` |  |
| extraVolumeMounts | string | `""` |  |
| extraVolumeMountsBigBang | object | `{}` |  |
| extraPorts | list | `[]` |  |
| podDisruptionBudget | object | `{}` |  |
| statefulsetAnnotations | object | `{}` |  |
| statefulsetLabels | object | `{}` |  |
| secrets.env.stringData.JAVA_TOOL_OPTIONS | string | `"-Dcom.redhat.fips=false"` |  |
| secrets.env.stringData.KEYCLOAK_ADMIN | string | `"admin"` |  |
| secrets.env.stringData.KEYCLOAK_ADMIN_PASSWORD | string | `"password"` |  |
| secrets.env.stringData.JAVA_OPTS_APPEND | string | `"-Djgroups.dns.query={{ include \"keycloak.fullname\" . }}-headless"` |  |
| service.annotations | object | `{}` |  |
| service.labels | object | `{}` |  |
| service.type | string | `"ClusterIP"` |  |
| service.loadBalancerIP | string | `""` |  |
| service.httpPort | int | `80` |  |
| service.httpNodePort | string | `nil` |  |
| service.httpsPort | int | `8443` |  |
| service.httpsNodePort | string | `nil` |  |
| service.extraPorts | list | `[]` |  |
| service.loadBalancerSourceRanges | list | `[]` |  |
| service.externalTrafficPolicy | string | `"Cluster"` |  |
| service.sessionAffinity | string | `""` |  |
| service.sessionAffinityConfig | object | `{}` |  |
| serviceHeadless.annotations | object | `{}` |  |
| ingress.enabled | bool | `false` |  |
| ingress.ingressClassName | string | `""` |  |
| ingress.servicePort | string | `"http"` |  |
| ingress.annotations | object | `{}` |  |
| ingress.labels | object | `{}` |  |
| ingress.rules[0].host | string | `"{{ .Release.Name }}.keycloak.example.com"` |  |
| ingress.rules[0].paths[0].path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/"` |  |
| ingress.rules[0].paths[0].pathType | string | `"Prefix"` |  |
| ingress.console.enabled | bool | `false` |  |
| ingress.console.ingressClassName | string | `""` |  |
| ingress.console.annotations | object | `{}` |  |
| ingress.console.rules[0].host | string | `"{{ .Release.Name }}.keycloak.example.com"` |  |
| ingress.console.rules[0].paths[0].path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/admin"` |  |
| ingress.console.rules[0].paths[0].pathType | string | `"Prefix"` |  |
| ingress.console.tls | list | `[]` |  |
| networkPolicy.enabled | bool | `false` |  |
| networkPolicy.labels | object | `{}` |  |
| networkPolicy.extraFrom | list | `[]` |  |
| networkPolicy.egress | list | `[]` |  |
| route.enabled | bool | `false` |  |
| route.path | string | `"/"` |  |
| route.annotations | object | `{}` |  |
| route.labels | object | `{}` |  |
| route.host | string | `""` |  |
| route.tls.enabled | bool | `true` |  |
| route.tls.insecureEdgeTerminationPolicy | string | `"Redirect"` |  |
| route.tls.termination | string | `"edge"` |  |
| dbchecker.enabled | bool | `true` |  |
| dbchecker.image.repository | string | `"registry1.dso.mil/ironbank/opensource/postgres/postgresql12"` |  |
| dbchecker.image.tag | string | `"12.20"` |  |
| dbchecker.image.pullPolicy | string | `"IfNotPresent"` |  |
| dbchecker.securityContext.allowPrivilegeEscalation | bool | `false` |  |
| dbchecker.securityContext.runAsUser | int | `1000` |  |
| dbchecker.securityContext.runAsGroup | int | `1000` |  |
| dbchecker.securityContext.runAsNonRoot | bool | `true` |  |
| dbchecker.securityContext.capabilities.drop[0] | string | `"ALL"` |  |
| dbchecker.resources.requests.cpu | string | `"250m"` |  |
| dbchecker.resources.requests.memory | string | `"256Mi"` |  |
| dbchecker.resources.limits.cpu | string | `"250m"` |  |
| dbchecker.resources.limits.memory | string | `"256Mi"` |  |
| postgresql.enabled | bool | `true` |  |
| postgresql.postgresqlUsername | string | `"keycloak"` |  |
| postgresql.postgresqlPassword | string | `"keycloak"` |  |
| postgresql.postgresqlDatabase | string | `"keycloak"` |  |
| postgresql.networkPolicy.enabled | bool | `false` |  |
| postgresql.global.imagePullSecrets[0] | string | `"private-registry"` |  |
| postgresql.image.registry | string | `"registry1.dso.mil"` |  |
| postgresql.image.repository | string | `"ironbank/opensource/postgres/postgresql12"` |  |
| postgresql.image.tag | string | `"12.20"` |  |
| postgresql.securityContext.enabled | bool | `true` |  |
| postgresql.securityContext.fsGroup | int | `26` |  |
| postgresql.securityContext.runAsUser | int | `1000` |  |
| postgresql.securityContext.runAsGroup | int | `1000` |  |
| postgresql.containerSecurityContext.enabled | bool | `true` |  |
| postgresql.containerSecurityContext.runAsUser | int | `26` |  |
| postgresql.containerSecurityContext.capabilities.drop[0] | string | `"ALL"` |  |
| postgresql.resources.requests.cpu | string | `"250m"` |  |
| postgresql.resources.requests.memory | string | `"256Mi"` |  |
| postgresql.resources.limits.cpu | string | `"250m"` |  |
| postgresql.resources.limits.memory | string | `"256Mi"` |  |
| database.existingSecret | string | `""` |  |
| database.existingSecretKey | string | `""` |  |
| database.vendor | string | `nil` |  |
| database.hostname | string | `nil` |  |
| database.port | string | `nil` |  |
| database.database | string | `nil` |  |
| database.username | string | `nil` |  |
| database.password | string | `nil` |  |
| cache.stack | string | `"default"` |  |
| proxy.enabled | bool | `true` |  |
| proxy.mode | string | `"forwarded"` |  |
| proxy.http.enabled | bool | `true` |  |
| metrics.enabled | bool | `true` |  |
| health.enabled | bool | `true` |  |
| http.relativePath | string | `"/auth"` |  |
| http.internalPort | string | `"http-internal"` |  |
| http.internalScheme | string | `"HTTP"` |  |
| serviceMonitor.enabled | bool | `false` |  |
| serviceMonitor.namespace | string | `""` |  |
| serviceMonitor.namespaceSelector | object | `{}` |  |
| serviceMonitor.annotations | object | `{}` |  |
| serviceMonitor.labels | object | `{}` |  |
| serviceMonitor.interval | string | `"10s"` |  |
| serviceMonitor.scrapeTimeout | string | `"10s"` |  |
| serviceMonitor.path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/metrics"` |  |
| serviceMonitor.port | string | `"{{ .Values.http.internalPort }}"` |  |
| serviceMonitor.scheme | string | `""` |  |
| serviceMonitor.tlsConfig | object | `{}` |  |
| extraServiceMonitor.enabled | bool | `false` |  |
| extraServiceMonitor.namespace | string | `""` |  |
| extraServiceMonitor.namespaceSelector | object | `{}` |  |
| extraServiceMonitor.annotations | object | `{}` |  |
| extraServiceMonitor.labels | object | `{}` |  |
| extraServiceMonitor.interval | string | `"10s"` |  |
| extraServiceMonitor.scrapeTimeout | string | `"10s"` |  |
| extraServiceMonitor.path | string | `"{{ tpl .Values.http.relativePath $ \| trimSuffix \"/\" }}/metrics"` |  |
| extraServiceMonitor.port | string | `"{{ .Values.http.internalPort }}"` |  |
| extraServiceMonitor.scheme | string | `""` |  |
| extraServiceMonitor.tlsConfig | object | `{}` |  |
| prometheusRule.enabled | bool | `false` |  |
| prometheusRule.namespace | string | `""` |  |
| prometheusRule.annotations | object | `{}` |  |
| prometheusRule.labels | object | `{}` |  |
| prometheusRule.rules | list | `[]` |  |
| autoscaling.enabled | bool | `false` |  |
| autoscaling.labels | object | `{}` |  |
| autoscaling.minReplicas | int | `3` |  |
| autoscaling.maxReplicas | int | `10` |  |
| autoscaling.metrics[0].type | string | `"Resource"` |  |
| autoscaling.metrics[0].resource.name | string | `"cpu"` |  |
| autoscaling.metrics[0].resource.target.type | string | `"Utilization"` |  |
| autoscaling.metrics[0].resource.target.averageUtilization | int | `80` |  |
| autoscaling.behavior.scaleDown.stabilizationWindowSeconds | int | `300` |  |
| autoscaling.behavior.scaleDown.policies[0].type | string | `"Pods"` |  |
| autoscaling.behavior.scaleDown.policies[0].value | int | `1` |  |
| autoscaling.behavior.scaleDown.policies[0].periodSeconds | int | `300` |  |
| test.enabled | bool | `false` |  |
| test.image.repository | string | `"docker.io/seleniarm/standalone-chromium"` |  |
| test.image.tag | string | `"117.0"` |  |
| test.image.pullPolicy | string | `"IfNotPresent"` |  |
| test.podSecurityContext.fsGroup | int | `1000` |  |
| test.securityContext.runAsUser | int | `1000` |  |
| test.securityContext.runAsNonRoot | bool | `true` |  |
| test.deletionPolicy | string | `"before-hook-creation"` |  |
| domain | string | `"dev.bigbang.mil"` |  |
| istio.enabled | bool | `false` |  |
| istio.hardened.enabled | bool | `false` |  |
| istio.hardened.customAuthorizationPolicies | list | `[]` |  |
| istio.hardened.outboundTrafficPolicyMode | string | `"REGISTRY_ONLY"` |  |
| istio.hardened.customServiceEntries | list | `[]` |  |
| istio.injection | string | `"disabled"` |  |
| istio.mtls.mode | string | `"STRICT"` | STRICT = Allow only mutual TLS traffic, PERMISSIVE = Allow both plain text and mutual TLS traffic |
| istio.keycloak.enabled | bool | `false` |  |
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

