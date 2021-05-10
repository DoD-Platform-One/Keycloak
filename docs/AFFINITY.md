# Node Affinity & Anti-Affinity with KeyCloak

Affinity is exposed through values options for this package. If you want to schedule your pods to deploy on specific nodes you can do that through the `nodeSelector` value and as needed the `affinity` value. Additional info is provided below as well to help in configuring this.

It is good to have a basic knowledge of node affinity and available options to you before customizing in this way - the upstream kubernetes documentation [has a good walkthrough of this](https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#affinity-and-anti-affinity).

## Values for NodeSelector

The `nodeSelector` value at the top level can be set to do basic node selection for deployments. See the below example on how to schedule pods to only nodes with the label `node-type` equal to `keycloak`:

```yaml
nodeSelector:
  node-type: keycloak
```

## Values for Affinity

Preferably, the `affinity` value at the top level can be used to specify affinity. The format to include follows what you'd specify at a pod/deployment level. See the example below for scheduling the operator pods only to nodes with the label `node-type` equal to `keycloak`:

```yaml
affinity: |
  nodeAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      nodeSelectorTerms:
      - matchExpressions:
        - key: node-type
          operator: In
          values:
          - keycloak
```

## Values for Anti-Affinity

The `affinity` value at the top level can be set in the same way to schedule pods using anti-affinity. See the below example on how to schedule pods to not be present on the nodes that already have pods with the `dont-schedule-with: keycloak` label:

```yaml
affinity: |
  podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
      - topologyKey: "kubernetes.io/hostname"
        labelSelector:
          matchLabels:
            dont-schedule-with: keycloak
```
