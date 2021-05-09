FROM registry1.dso.mil/ironbank/opensource/gradle/gradle-jdk11 AS build

# IB work-around
USER gradle

COPY plugin /home/gradle/project
RUN gradle build 

FROM registry1.dso.mil/ironbank/opensource/keycloak/keycloak:13.0.0 AS keycloak

COPY --from=build /home/gradle/project/build/libs/platform-one-sso-1.3-all.jar /opt/jboss/keycloak/standalone/deployments/p1.jar

USER keycloak