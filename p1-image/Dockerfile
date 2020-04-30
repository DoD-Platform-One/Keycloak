FROM quay.io/keycloak/keycloak:9.0.3

COPY custom-theme /opt/custom-theme

USER root

RUN mv /opt/jboss/keycloak/themes/base /opt/jboss/keycloak/themes/base.bak && \
    mv /opt/custom-theme /opt/jboss/keycloak/themes/base && \
    mv /opt/jboss/keycloak/themes/base.bak/admin /opt/jboss/keycloak/themes/base/admin && \
    rm -fr /opt/jboss/keycloak/themes/base.bak

USER jboss