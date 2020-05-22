FROM gradle:jdk11 AS build

WORKDIR /usr/src/gradle

COPY custom-registration /usr/src/gradle

RUN gradle build

FROM quay.io/keycloak/keycloak:10.0.1 AS keycloak

COPY baby-yoda.json /opt/baby-yoda.json
COPY custom-theme /opt/custom-theme
COPY --from=build /usr/src/gradle/build/libs/keycloak-registration-validation-1.2.jar /opt/jboss/keycloak/standalone/deployments/p1.jar

USER root

RUN mv /opt/jboss/keycloak/themes/base /opt/jboss/keycloak/themes/base.bak && \
    mv /opt/custom-theme /opt/jboss/keycloak/themes/base && \
    mv /opt/jboss/keycloak/themes/base.bak/admin /opt/jboss/keycloak/themes/base/admin && \
    rm -fr /opt/jboss/keycloak/themes/base.bak

USER jboss