JKS_TRUSTSTORE="truststore.jks"
DEST_TRUSTSTORE="truststore.pfx"
TRUSTSTORE_PASSWORD="password"
keytool -importkeystore -srckeystore ${JKS_TRUSTSTORE} \
            -srcstoretype JKS \
            -destkeystore ${DEST_TRUSTSTORE} \
            -deststoretype PKCS12 \
            -storepass "${TRUSTSTORE_PASSWORD}" >& /dev/null
