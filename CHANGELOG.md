# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---
## [17.0.1-bb.2] - 2022-03-10
### Updated
- Updated development realm config with Vault client

## [17.0.1-bb.1] - 2022-02-17
### Updated
- Updated gluon subchart to latest version 0.2.6

## [17.0.1-bb.0] - 2022-02-02
### Changed
- upgrade Keycloak to app version 16.1.1 chart version 17.0.1

## [16.0.6-bb.3] - 2022-01-31
### Changed
- moved test values

## [16.0.6-bb.2] - 2022-01-31
### Updated
- Update Chart.yaml to follow new standardization for release automation
- Added renovate check to update new standardization

## [16.0.6-bb.1] - 2022-01-27
### Changed
- fix problem on FIPS enabled nodes 

## [16.0.6-bb.0] - 2022-01-24
### Changed
- upgrade to Keycloak app version 16.1.0 chart version 16.0.6
- the x509.sh script will conditionally skip building the java keystore if it already exists
- the Java JDK version is changed from JDK8 to JDK11

## [11.0.1-bb.9] - 2021-10-21
### Changed
- add development realm with clients for testing and CI pipeline purposes

## [11.0.1-bb.8] - 2021-10-06
### Changed
- Updated Helm Tests

## [11.0.1-bb.7] - 2021-09-24
### Fixed
- fix for trash bin in custom plugin code

## [11.0.1-bb.6] - 2021-09-16
### Fixed
- modify networkPolicy for smtp egress

## [11.0.1-bb.5] - 2021-09-16
### Added
- add networkPolicy for smtp egress
### Fixed
- fix yaml syntax in values

## [11.0.1-bb.4] - 2021-09-13
### Changed
- plugin code change for email

## [11.0.1-bb.3] - 2021-09-10
### Fixed
- custom plugin code fix for email to whitelist check

## [11.0.1-bb.2] - 2021-08-12
### Changed
- added requests and limits to postgresql pod to satisfy ratio violations
- added requests and limits to CI test-values to satisfy ratio violations

## [11.0.1-bb.1] - 2021-07-22
### Changed
- allow DNS networkpolicie allow for port 5353

## [11.0.1-bb.0] - 2021-06-30
### Changed
- upgrade to keycloak app version 14.0.0 chart version 11.0.1
### Fixed
- includes fix for usercertificate attribute
- cleanup networkpolicies

## [11.0.0-bb.5] - 2021-06-14
### Changed
- set resource request and limit for CPU and memory to comply with BigBang charter

## [11.0.0-bb.4] - 2021-06-10
### Added
- modify upstream chart to add custom volumes and volumemounts for BigBang integration

## [11.0.0-bb.3] - 2021-06-09
### Fixed
- new custom image with various UI fixes

## [11.0.0-bb.2] - 2021-06-08
### Changed
- remove configuration from deploying by default
- DoD CA certs no longer loaded by default
- refactor how ENV variables are configured in the values.yaml
- document recommended way to configure

## [11.0.0-bb.1] - 2021-05-26
### Added
- Added additional network policies to be controlled through the bigbang chart

## [11.0.0-bb.0] - 2021-05-14
### Added
- initial realase with app version 13.0.0 helm chart version 11.0.0
