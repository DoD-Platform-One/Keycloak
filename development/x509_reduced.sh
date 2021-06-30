#!/bin/bash

function autogenerate_keystores() {
      cat /opt/jboss/keycloak/standalone/configuration/.jbossclirc >> /opt/jboss/keycloak/bin/.jbossclirc
      $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-keystore.cli
      $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-truststore.cli
}

autogenerate_keystores
