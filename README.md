# Keycloak (Customized)

This repository contains a Helm chart to deploy a customizable Keycloak for single sign-on (SSO) with Identity and Access Management.  It extends the [open-source Keycloak](https://www.keycloak.org/) with a plugin that adds additional customization for group authentication, registration, and themes.

## Prerequisites

The following items are required before deploying KeyCloak from this repository:

- A running Kubernetes cluster
- [Helm](https://helm.sh/docs/intro/install/)

## Quickstart

To get Keycloak running quickly, we recommend using the same configuration as our test environment.

Deploy Keycloak using the test configuration, but disable Istio:

```bash
# Deploy keycloak
helm upgrade -i -n keycloak --create-namespace -f ./tests/test-values.yml --set istio.enabled=false keycloak ./chart

# Get name of running pod
export POD_NAME=$(kubectl get pods --namespace keycloak -l "app.kubernetes.io/name=keycloak,app.kubernetes.io/instance=keycloak" -o name)

# Port forward pod to localhost
kubectl --namespace keycloak port-forward "$POD_NAME" 8080
```

Now, you can access keycloak registration through the endpoint of `http://localhost:8080` which will attempt to authenticate you through SSO.  In addition, you can access `http://localhost:8080/auth/admin` to reach the admin login screen.  Login using the user `admin` and password `password`.

## Customization

To customize the keycloak deployment, read the [customization documentation](./docs/configuration.md)

## Custom Plugin Development

The plugin that allows for additional customization is located in the [development directory](./development).  See the [readme.me](./development/README.md) there for further documentation.
