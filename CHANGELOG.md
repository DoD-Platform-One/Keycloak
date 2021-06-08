# Modifications made to upstream chart
## chart/values.yaml
- disable all internal services other than postgres
- add BigBang additional values at bottom of values.yaml
- add IronBank hardened image
- add default argument of "-b 0.0.0.0" to bind to localhost
- and other miscellaneous change.  Diff with previous version to find all changes

##  chart/charts/*.tgz
- run ```helm dependency update``` and commit the downloaded archives
- no need to commit the requirements.lock file

## chart/requirements.yaml
- add BigBang test library
- change all external dependency links to point to the local file system

## chart/templates/bigbang/*
- add istio virtual service
- add NetworkPolicies to restrict traffic

## chart/resources/
- add /dev directory to hold the the baby-yoda configuration files
- add the DoD certificate bundle pem file

## chart/templates/bigbang
- add network policy yaml
- add vertualservice yaml

## chart/tests
- add directory with cypress test files

## chart/templates/tests  (this is separate from the upstream templates/test directory)
- add helm template to add support for the helm test library

## chart/Chart.yaml
- update the version with the bigbang ```-bb.#``` 

## chart/Kptfile
- file created when kpt was used to dowload the upstream chart

## chart/scripts/keycloak.cli
- delete this uptream file.  Don't want to encourage anyone to override the startup script.  


&nbsp;  
&nbsp;    

# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [11.0.0-bb.1] - 2021-05-26
### Added
- Added additional network policies to be controlled through the bigbang chart

## [11.0.0-bb.0] - 2021-05-14
- initial realase with app version 13.0.0 helm chart version 11.0.0

