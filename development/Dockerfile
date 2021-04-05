FROM registry1.dso.mil/ironbank/opensource/gradle/gradle-jdk11 AS build

# IB work-around
USER root

COPY plugin /opt/gradle
RUN gradle build 

FROM registry1.dso.mil/ironbank/opensource/keycloak/keycloak:11.0.1 AS keycloak

COPY --from=build /opt/gradle/build/libs/keycloak-registration-validation-1.3-all.jar /opt/jboss/keycloak/standalone/deployments/p1.jar

USER keycloak