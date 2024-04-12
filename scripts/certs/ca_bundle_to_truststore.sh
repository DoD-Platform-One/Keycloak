#!/bin/bash

MAIN_DIR=`pwd`
X509_CRT_DELIMITER="/-----BEGIN CERTIFICATE-----/"
JKS_TRUSTSTORE_FILE="truststore.jks"
JKS_TRUSTSTORE_PATH="${MAIN_DIR}/${JKS_TRUSTSTORE_FILE}"
TRUSTSTORE_PASSWORD="password"
TEMPORARY_CERTIFICATE="temporary_ca.crt"
X509_CA_BUNDLE="${MAIN_DIR}/dod_cas.pem"

pushd /tmp >& /dev/null

echo "Creating truststore ${JKS_TRUSTSTORE_PATH}"
cat "${X509_CA_BUNDLE}" > ${TEMPORARY_CERTIFICATE}
csplit -s -z -f crt- "${TEMPORARY_CERTIFICATE}" "${X509_CRT_DELIMITER}" '{*}'
for CERT_FILE in crt-*; do
keytool -import -noprompt -keystore "${JKS_TRUSTSTORE_PATH}" -file "${CERT_FILE}" \
-storepass "${TRUSTSTORE_PASSWORD}" -alias "service-${CERT_FILE}" >& /dev/null
done
if [ -f "${JKS_TRUSTSTORE_PATH}" ]; then
echo "Truststore successfully created at: ${JKS_TRUSTSTORE_PATH}"
else
echo "ERROR: Creating truststore: ${JKS_TRUSTSTORE_PATH}"
exit
fi

popd >& /dev/null

echo "Validating truststore"
keytool -list -keystore $JKS_TRUSTSTORE_PATH -storepass "${TRUSTSTORE_PASSWORD}" >& /dev/null
if [ $? == 0 ]; then
echo "Truststore validated"
else
echo "ERROR: Reading truststore"
exit
fi

echo "Cleaning up files"
rm -rf /tmp/crt-*
