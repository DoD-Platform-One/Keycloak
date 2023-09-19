# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---
## [18.4.3-bb.5]
### Updated
- Updated gluon to 0.4.0 to 0.4.1
- Updated Cypress tests to accomodate cypress 13.X+
- Added chart/resources/dev/baby-yoda-bb-ci.json to enable SSO testing in the pipeline
- Improved chart/templates/bigbang/create-ci-cypress-user-hook.yaml with additional attributes



## [18.4.3-bb.4]
### Updated
- Fixed a broken link in the docs

## [18.4.3-bb.2] - 2023-08-09
### Updated
- Update securityContext for postgres to run as non-root

## [18.4.3-bb.2] - 2022-06-29
### Updated
- Update bitnami/postgresql version 15.2.0 -> 15.3.0
- Update postgresql-exporter version to 0.12.0 -> 0.12.1
- Update postgresql12 version to 12.14 -> 12.15
- Update gluon version 0.3.2 -> 0.4.0
- Update uib8-micro version 8.7 -> 8.8

## [18.4.3-bb.1] - 2023-06-27
### Updated
- Added support for LDAP egress

---
## [18.4.3-bb.0] - 2022-05-23
### Updated
- Update Keycloak version to 21.1.1
- Update bitnami postgres exporter to 0.12.0

## [18.4.0-bb.3] - 2023-05-17
### Updated
- Update chat/values.yaml hostname key to domain
- Updated docs, changing hostname to domain

## [18.4.0-bb.2] - 2022-03-30
### Updated
- Update helm.sh/images postgresql ironbank image to 12.14
- Update bitnami postgres version to 15.2.0
- Update Keycloak version to 21.0.2
- new plugin version 3.2.0

## [18.4.0-bb.1] - 2022-02-27
### Updated
- new plugin version 3.1.0

## [18.4.0-bb.0] - 2022-01-24
### Updated
- Update helm chart to 18.4.0
- Update Keycloak version to 20.0.3

## [18.3.0-bb.2] - 2022-01-17
### Changed
- Update gluon to new registry1 location + latest version (0.3.2)

## [18.3.0-bb.1] - 2023-01-11
### Changed
- Fix PeerAuthentication exception policy for infinispan/jgroups communication

## [18.3.0-bb.0] - 2022-12-30
### Updated
- Update helm chart to 18.3.0
- Upgrade Keycloak image from version 18.0.1-legacy to version 20.0.2
- Update Java truststore to DoD trusted certificate authorities version 9.5
### Changed
- Migration to new Quarkus deployment architecture

## [18.2.1-bb.6] - 2022-12-12
### Added
- Added keycloak-primary-app-exception for JPGROUPS

## [18.2.1-bb.5] - 2022-10-28
### Added
- Added ServiceMonitor support for Istio mTLS

## [18.2.1-bb.4] - 2022-09-22
### Fixed
- Added capabilities drop ALL
- Updated Gluon to `0.3.1`

## [18.2.1-bb.3] - 2022-08-10
### Fixed
- Fixed metrics mTLS issue

## [18.2.1-bb.2] - 2022-08-05
### Fixed
- Fixed CI mTLS issue by injecting create-ci-cypress-user job
- Updated conditionals for PeerAuthentications to be stricter and less prone to edge cases

## [18.2.1-bb.1] - 2022-08-01
### Added
- Default Istio `PeerAuthentication` for mTLS
- Set mTLS exceptions for postgresql

## [18.2.1-bb.0] - 2022-07-19
### Updated
- Update chart to latest 18.2.1
- Upgrade Keycloak image from version 18.0.1-legacy to version 18.0.2-legacy

## [18.1.1-bb.6] - 2022-06-28
### Updated
- Updated bb base image to 2.0.0
- Updated gluon to 0.2.10
- Removed websecurity disable from cypress

## [18.1.1-bb.5] - 2022-06-27
### Updated
- Updated pgchecker initContainer to use IronBank postgres image instead of busybox
- Moved base image out of `create-ci-cypress-user-hook.yaml` and into bbtest values

## [18.1.1-bb.4] - 2022-06-24
### Updated
- Fix app version in Chart.yaml

## [18.1.1-bb.3] - 2022-06-21
### Updated
- upgrade Keycloak to app version 18.0.1 chart version 18.1.1
- Update postgresql dependency chart big-bang base image to 1.18.0

## [18.1.1-bb.2] - 2022-06-16
### Updated
- Update postgresql image and initContainer image

## [18.1.1-bb.1] - 2022-06-03
### Added
- Added network policies to support istio sidecar injection

## [18.1.1-bb.0] - 2022-05-27
### Updated
- upgrade Keycloak to app version 18.0.0-legacy chart version 18.1.1-bb.0

## [18.0.0-bb.4] - 2022-04-26
### Changed
- Custom P1 plugin changed to allow underscores in client names
- Move MODIFICATIONS.md to /docs/PACKAGE_UPDATES.md and add more upgrade documentation
### Updated
- Updated DoD certificate authorities pem file

## [18.0.0-bb.3] - 2022-04-18
### Added
- Added oscal-component

## [18.0.0-bb.2] - 2022-04-18
### Added
- Added values to the values.yaml file for using an ironbank approved image for postgresql.enabled set to true.
- Added postgresql dependency chart source under `/charts/deps` directory

## [18.0.0-bb.1] - 2022-04-15
### Changed
- Changed the bigbang.dev/applicationVersions to point to upstream version instead of tagged version

### Added
- Added PlatformOne Plugin to bigbang.dev/applicationVersions annotation

## [18.0.0-bb.0] - 2022-04-13
### Updated
- upgrade Keycloak to app version 17.0.1-legacy chart version 18.0.0-bb.0

## [17.0.1-bb.4] - 2022-03-29
### Added
- Added create-ci-cypress-user-hook.yaml, creates a cypress user using Keycloak REST API when run in CI testing.

## [17.0.1-bb.3] - 2022-03-25
### Added
- Added baby-yoda-ci.json, create a baby-yoda realm w/ MFA disabled for CI cypress testing

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
