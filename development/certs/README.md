# This directory contains files used for development testing

## tls.cert and tls.key
The *.bigbang.dev wildcard cert that points to localhost. This is a LetsEncrypt cert that expires after 90 days. The current cert and key can be found at [Big Bang repository](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/chart/ingress-certs.yaml).

## truststore.jks
The truststore prevents bad actors from using fake certificates to authenticate with Keycloak. The truststore binary file created from the the scripts located in this code repository at `/scripts/certs/`. For more info see the readme file in that directory. The truststore was created from the DoD version 9.5 certs downloaded from [public.cyber.mil](https://public.cyber.mil/pki-pke/pkipke-document-library/). 
