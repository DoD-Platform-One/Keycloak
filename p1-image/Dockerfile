FROM maven:3-openjdk AS build

WORKDIR /usr/src/maven

COPY custom-registration /usr/src/maven

RUN mvn clean install

FROM quay.io/keycloak/keycloak:10.0.1 AS keycloak

COPY custom-theme /opt/custom-theme

COPY --from=build /usr/src/maven/target/keycloak-registration-validation-1.2.jar /opt/jboss/keycloak/standalone/deployments/p1.jar

USER root

RUN mv /opt/jboss/keycloak/themes/base /opt/jboss/keycloak/themes/base.bak && \
    mv /opt/custom-theme /opt/jboss/keycloak/themes/base && \
    mv /opt/jboss/keycloak/themes/base.bak/admin /opt/jboss/keycloak/themes/base/admin && \
    rm -fr /opt/jboss/keycloak/themes/base.bak

USER jboss