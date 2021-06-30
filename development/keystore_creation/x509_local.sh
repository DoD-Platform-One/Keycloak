#!/bin/bash

# run from within keystore subfolder

function autogenerate_keystores() {

  local MAIN_DIR=`pwd`
  local X509_KEYSTORE_DIR="${MAIN_DIR}/source_files"

  local KEYSTORES_STORAGE="${MAIN_DIR}/keystores"
  if [ ! -d "${KEYSTORES_STORAGE}" ]; then
    mkdir -p "${KEYSTORES_STORAGE}"
  fi

  local X509_CRT="tls.crt"
  local X509_KEY="tls-enc.key"
  local NAME="keycloak-https-key"
  local PASSWORD=$(openssl rand -base64 32 2>/dev/null)
  local JKS_KEYSTORE_FILE="https-keystore.jks"
  local PKCS12_KEYSTORE_FILE="https-keystore.pk12"
  local JBOSSCLIRC="jbossclirc"

  # Auto-generate the Keycloak truststore if X509_CA_BUNDLE was provided
  local -r X509_CRT_DELIMITER="/-----BEGIN CERTIFICATE-----/"
  local JKS_TRUSTSTORE_FILE="truststore.jks"
  local JKS_TRUSTSTORE_PATH="${MAIN_DIR}/${JKS_TRUSTSTORE_FILE}"
  local PASSWORD=$(openssl rand -base64 32 2>/dev/null)
  local TEMPORARY_CERTIFICATE="temporary_ca.crt"
  local X509_CA_BUNDLE="cas.pem"


  echo "Removing old files.."
  rm $JBOSSCLIRC

  echo "Creating x509 certificate secrets.."

  openssl pkcs12 -export \
  -name "${NAME}" \
  -inkey "${X509_KEYSTORE_DIR}/${X509_KEY}" \
  -in "${X509_KEYSTORE_DIR}/${X509_CRT}" \
  -out "${MAIN_DIR}/${PKCS12_KEYSTORE_FILE}" \
  -password pass:"${PASSWORD}" >& /dev/null

  keytool -importkeystore -noprompt \
  -srcalias "${NAME}" -destalias "${NAME}" \
  -srckeystore "${MAIN_DIR}/${PKCS12_KEYSTORE_FILE}" \
  -srcstoretype pkcs12 \
  -destkeystore "${MAIN_DIR}/${JKS_KEYSTORE_FILE}" \
  -storepass "${PASSWORD}" -srcstorepass "${PASSWORD}" >& /dev/null

  if [ -f "${MAIN_DIR}/${JKS_KEYSTORE_FILE}" ]; then
    echo "keystore successfully created at: ${MAIN_DIR}/${JKS_KEYSTORE_FILE}"
  fi

  echo "set keycloak_tls_keystore_password=${PASSWORD}" >> "${MAIN_DIR}/$JBOSSCLIRC"
  echo "set keycloak_tls_keystore_file=/opt/jboss/keycloak/standalone/configuration/keystores/https-keystore.jks" >> "${MAIN_DIR}/$JBOSSCLIRC"

  if [ -n "${X509_CA_BUNDLE}" ]; then
    pushd /tmp >& /dev/null
    echo "Creating Keycloak truststore.."
    # We use cat here, so that users could specify multiple CA Bundles using space or even wildcard:
    # X509_CA_BUNDLE=/var/run/secrets/kubernetes.io/serviceaccount/*.crt
    # Note, that there is no quotes here, that's intentional. Once can use spaces in the $X509_CA_BUNDLE like this:
    # X509_CA_BUNDLE=/ca.crt /ca2.crt
    cat ${X509_KEYSTORE_DIR}/${X509_CA_BUNDLE} > ${TEMPORARY_CERTIFICATE}
    csplit -s -z -f crt- "${TEMPORARY_CERTIFICATE}" "${X509_CRT_DELIMITER}" '{*}'
    for CERT_FILE in crt-*; do
      keytool -import -noprompt -keystore "${JKS_TRUSTSTORE_PATH}" -file "${CERT_FILE}" \
      -storepass "${PASSWORD}" -alias "service-${CERT_FILE}" >& /dev/null
    done

    if [ -f "${JKS_TRUSTSTORE_PATH}" ]; then
      echo "Keycloak truststore successfully created at: ${JKS_TRUSTSTORE_PATH}"
    fi

    echo "set keycloak_tls_truststore_password=${PASSWORD}" >> "${MAIN_DIR}/$JBOSSCLIRC"
    echo "set keycloak_tls_truststore_file=/opt/jboss/keycloak/standalone/configuration/keystores/truststore.jks" >> "${MAIN_DIR}/$JBOSSCLIRC"
    echo "set configuration_file=standalone-ha.xml" >> "${MAIN_DIR}/$JBOSSCLIRC"

    popd >& /dev/null
  fi

  #keytool -list -v -keystore ./truststore.jks
  echo "Creating Configmap for $JKS_TRUSTSTORE_FILE VolumeMount to /opt/jboss/keycloak/standalone/configuration/keystores/$JKS_TRUSTSTORE_FILE"
  kubectl create configmap "$JKS_TRUSTSTORE_FILE" --from-file=$JKS_TRUSTSTORE_FILE -n keycloak --dry-run=client -o yaml > "${KEYSTORES_STORAGE}/${JKS_TRUSTSTORE_FILE}-configmap-enc.yaml"

  echo "Creating Configmap for $JKS_KEYSTORE_FILE VolumeMount to /opt/jboss/keycloak/standalone/configuration/keystores/$JKS_KEYSTORE_FILE"
  kubectl create configmap "$JKS_KEYSTORE_FILE" --from-file=$JKS_KEYSTORE_FILE -n keycloak --dry-run=client -o yaml > "${KEYSTORES_STORAGE}/${JKS_KEYSTORE_FILE}-configmap-enc.yaml"

  echo "Creating Configmap for $PKCS12_KEYSTORE_FILE VolumeMount to /opt/jboss/keycloak/standalone/configuration/keystores/$PKCS12_KEYSTORE_FILE"
  kubectl create configmap "$PKCS12_KEYSTORE_FILE" --from-file=$PKCS12_KEYSTORE_FILE -n keycloak --dry-run=client -o yaml > "${KEYSTORES_STORAGE}/${PKCS12_KEYSTORE_FILE}-configmap-enc.yaml"
  #kubectl create secret generic "$PKCS12_KEYSTORE_FILE" --from-file=$PKCS12_KEYSTORE_FILE -n keycloak --dry-run=client -o yaml > "${PKCS12_KEYSTORE_FILE}-secret-enc.yaml"

  echo "Creating secret for $JBOSSCLIRC VolumeMount to /opt/jboss/keycloak/bin/.$JBOSSCLIRC"
  kubectl create secret generic "$JBOSSCLIRC" --from-file=$JBOSSCLIRC -n keycloak --dry-run=client -o yaml > "${KEYSTORES_STORAGE}/${JBOSSCLIRC}-secret-enc.yaml"

  echo "Removing files $JKS_TRUSTSTORE_FILE $JKS_KEYSTORE_FILE $PKCS12_KEYSTORE_FILE $JBOSSCLIRC"
  #rm $JKS_TRUSTSTORE_FILE $JKS_KEYSTORE_FILE $PKCS12_KEYSTORE_FILE $JBOSSCLIRC
}

autogenerate_keystores
