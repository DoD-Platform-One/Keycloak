# This directory contains files used for development testing
The files must be located here so that they can be accessed by the Keycloak helm chart.

## baby-yoda.json and baby-yoda.yaml
These files contain realm configuration that can be loaded on startup for development testing. See the [keycloak-dev-values.yaml](https://repo1.dso.mil/platform-one/big-bang/bigbang/-/blob/master/docs/example_configs/keycloak-dev-values.yaml) example. 

## dod_cas.pem
This file contains the DoD approved certificate authorities that Keycloak will trust. This file was created using the helper scripts located in this repository at ```/scripts/certs/```. For more info see the readme file in that directory.
