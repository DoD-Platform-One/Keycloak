# Modifications made to upstream chart
## chart/values.yaml
- disable all internal services other than postgres
- add BigBang additional values at bottom of values.yaml
- add IronBank hardened image
- add default argument of "-b 0.0.0.0" to bind to localhost

##  chart/charts/*.tgz
- run ```helm dependency update``` and commit the downloaded archives
## chart/requirements.yaml
- change all external dependency links to point to the local file system

## chart/templates/bigbang/*
- add istio virtual service
- add NetworkPolicies to restrict traffic
# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [10.1.0-bb.0] - 2021-04-01
- initial realase app version 12.0.4

