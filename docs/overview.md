# Keycloak

This repository contains a Helm chart to deploy a customizable Keycloak for single sign-on (SSO) with Identity and Access Management. Platform One provides a [custom keycloak plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin) that adds additional customization for authentication, authorization, self-registration, and auto-joining of groups.

## Prerequisites

The following items are required before deploying KeyCloak from this repository:

- A running Kubernetes cluster
- [Helm](https://helm.sh/docs/intro/install/)

## Quickstart

To quickly evaluate Keycloak with the custom Platform One plugin in a local development environment follow the [docker-compose instructions](./development/README.md).
