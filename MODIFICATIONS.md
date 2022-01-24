# Modifications made to upstream chart
## chart/values.yaml
- disable all internal services other than postgres
- add BigBang additional values at bottom of values.yaml
- add IronBank hardened image
- add default argument of "-b 0.0.0.0" to bind to localhost
- and other miscellaneous change.  Diff with previous version to find all changes

##  chart/charts/*.tgz
- run ```helm dependency update``` and commit the downloaded archives
- also commit the requirements.lock file so that air-gap deployments don't try to check for updates

## chart/Chart.lock
- Chart.lock is updated during ```helm dependency update``` with the gluon library

## chart/templates/StatefulSet.yaml
- add extraVolumesBigBang (lines 196-189)
- add extraVolumeMountsBigBang (lines 146-148)

## chart/templates/bigbang/*
- add istio virtual service
- add NetworkPolicies to restrict traffic

## chart/resources/
- add /dev directory to hold the the baby-yoda configuration files
- add the DoD certificate bundle pem file

## chart/tests
- add directory with cypress test files

## chart/templates/tests  (this is separate from the upstream templates/test directory)
- add helm template to add support for the helm test library

## chart/Chart.yaml
- update the chart version with the bigbang ```-bb.#```
- update app version when not the same as the original chart
- add gluon library dependency

## chart/Kptfile
- file created when kpt was used to dowload the upstream chart

## chart/scripts/keycloak.cli
- delete this uptream file.  Don't want to encourage anyone to override the startup script.  
