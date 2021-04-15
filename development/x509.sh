#!/bin/bash

function autogenerate_keystores() {
  # Keystore infix notation as used in templates to keystore name mapping
  declare -A KEYSTORES=( ["https"]="HTTPS" )

  local KEYSTORES_STORAGE="${JBOSS_HOME}/standalone/configuration/keystores"
  if [ ! -d "${KEYSTORES_STORAGE}" ]; then
    mkdir -p "${KEYSTORES_STORAGE}"
  fi

  # Auto-generate the HTTPS keystore if volumes for OpenShift's
  # serving x509 certificate secrets service were properly mounted
  for KEYSTORE_TYPE in "${!KEYSTORES[@]}"; do

    local X509_KEYSTORE_DIR="/etc/x509/${KEYSTORE_TYPE}"
    local X509_CRT="tls.crt"
    local X509_KEY="tls.key"
    local NAME="keycloak-${KEYSTORE_TYPE}-key"
    local PASSWORD=$(openssl rand -base64 32 2>/dev/null)
    local JKS_KEYSTORE_FILE="${KEYSTORE_TYPE}-keystore.jks"
    local PKCS12_KEYSTORE_FILE="${KEYSTORE_TYPE}-keystore.pk12"

    if [ -d "${X509_KEYSTORE_DIR}" ]; then

      echo "Creating ${KEYSTORES[$KEYSTORE_TYPE]} x509 certificate secrets.."

      openssl pkcs12 -export \
      -name "${NAME}" \
      -inkey "${X509_KEYSTORE_DIR}/${X509_KEY}" \
      -in "${X509_KEYSTORE_DIR}/${X509_CRT}" \
      -out "${KEYSTORES_STORAGE}/${PKCS12_KEYSTORE_FILE}" \
      -password pass:"${PASSWORD}" >& /dev/null

      keytool -importkeystore -noprompt \
      -srcalias "${NAME}" -destalias "${NAME}" \
      -srckeystore "${KEYSTORES_STORAGE}/${PKCS12_KEYSTORE_FILE}" \
      -srcstoretype pkcs12 \
      -destkeystore "${KEYSTORES_STORAGE}/${JKS_KEYSTORE_FILE}" \
      -storepass "${PASSWORD}" -srcstorepass "${PASSWORD}" >& /dev/null

      if [ -f "${KEYSTORES_STORAGE}/${JKS_KEYSTORE_FILE}" ]; then
        echo "${KEYSTORES[$KEYSTORE_TYPE]} keystore successfully created at: ${KEYSTORES_STORAGE}/${JKS_KEYSTORE_FILE}"
      fi

      echo "set keycloak_tls_keystore_password=${PASSWORD}" >> "$JBOSS_HOME/bin/.jbossclirc"
      echo "set keycloak_tls_keystore_file=${KEYSTORES_STORAGE}/${JKS_KEYSTORE_FILE}" >> "$JBOSS_HOME/bin/.jbossclirc"
      echo "set configuration_file=standalone.xml" >> "$JBOSS_HOME/bin/.jbossclirc"
      $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-keystore.cli
      sed -i '$ d' "$JBOSS_HOME/bin/.jbossclirc"
      echo "set configuration_file=standalone-ha.xml" >> "$JBOSS_HOME/bin/.jbossclirc"
      $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-keystore.cli
      sed -i '$ d' "$JBOSS_HOME/bin/.jbossclirc"
    fi

  done

  # Auto-generate the Keycloak truststore if X509_CA_BUNDLE was provided
  local -r X509_CRT_DELIMITER="/-----BEGIN CERTIFICATE-----/"
  local JKS_TRUSTSTORE_FILE="truststore.jks"
  local JKS_TRUSTSTORE_PATH="${KEYSTORES_STORAGE}/${JKS_TRUSTSTORE_FILE}"
  local PASSWORD=$(openssl rand -base64 32 2>/dev/null)
  local TEMPORARY_CERTIFICATE="temporary_ca.crt"
  #base64 -d ${KEYCLOAK_CA_BUNDLE} > ${X509_CA_BUNDLE}
  #echo "${KEYCLOAK_CA_BUNDLE}" > ${X509_CA_BUNDLE}
  if [ -n "${X509_CA_BUNDLE}" ]; then
    pushd /tmp >& /dev/null
    echo "Creating Keycloak truststore.."
    # We use cat here, so that users could specify multiple CA Bundles using space or even wildcard:
    # X509_CA_BUNDLE=/var/run/secrets/kubernetes.io/serviceaccount/*.crt
    # Note, that there is no quotes here, that's intentional. Once can use spaces in the $X509_CA_BUNDLE like this:
    # X509_CA_BUNDLE=/ca.crt /ca2.crt
    cat ${X509_CA_BUNDLE} > ${TEMPORARY_CERTIFICATE}
    csplit -s -z -f crt- "${TEMPORARY_CERTIFICATE}" "${X509_CRT_DELIMITER}" '{*}'
    for CERT_FILE in crt-*; do
      keytool -import -noprompt -keystore "${JKS_TRUSTSTORE_PATH}" -file "${CERT_FILE}" \
      -storepass "${PASSWORD}" -alias "service-${CERT_FILE}" >& /dev/null
    done

    if [ -f "${JKS_TRUSTSTORE_PATH}" ]; then
      echo "Keycloak truststore successfully created at: ${JKS_TRUSTSTORE_PATH}"
    fi

    echo "set keycloak_tls_truststore_password=${PASSWORD}" >> "$JBOSS_HOME/bin/.jbossclirc"
    echo "set keycloak_tls_truststore_file=${KEYSTORES_STORAGE}/${JKS_TRUSTSTORE_FILE}" >> "$JBOSS_HOME/bin/.jbossclirc"
    echo "set configuration_file=standalone.xml" >> "$JBOSS_HOME/bin/.jbossclirc"
    $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-truststore.cli
    sed -i '$ d' "$JBOSS_HOME/bin/.jbossclirc"
    echo "set configuration_file=standalone-ha.xml" >> "$JBOSS_HOME/bin/.jbossclirc"
    $JBOSS_HOME/bin/jboss-cli.sh --file=/opt/jboss/tools/cli/x509-truststore.cli
    sed -i '$ d' "$JBOSS_HOME/bin/.jbossclirc"

    popd >& /dev/null
  fi
}

autogenerate_keystores