# Helper scripts for CA certs
The DoD approved certificate authority archives distributed by [DoD Cyber Exchange](https://public.cyber.mil/pki-pke/pkipke-document-library/) contain certs that are not recommended for use with a Keycloak deployment. For example: sofware certs, email certs, certs uploaded with the wrong format, expired certs, etc.   
These scripts help research and clean the certificates for a secure Keycloak deployment.

## cert_tree.py
This script prints a nice graphical tree of the certs in a pem file and also shows which certs are expired.
usage:
```
./cert_check.pl multi-cert-file.pem
```

## cert_check.pl
This script helps research muli-cert pem files. It does the equivalent of an ```openssl x509 -noout -text``` command, except with a multi-cert file.
Usage:
```
./cert_check.pl multi-cert-file.pem
```

## dod_cas_to_pem.sh
This script creates a single clean pem file using the archive downloaded from [DoD Cyber Exchange](https://public.cyber.mil/pki-pke/pkipke-document-library/). Garbage is removed from the archive files. Certs in der format are converted to pem format. Software and email certs are removed because they are not needed by Keycloak. This script is maintained by Zac Williamson who works on the CNAP team at Platform One.
Usage:
```
./dod_cas_to_pem.sh dod_cas_including_expired_certs.pem
```

## ca_bundle_to_truststore.sh
This script creates a java truststore.jks from the DoD certificate authorities for use with Keycloak. Creation of the dod_cas.pem is a prerequisite.
Usage:
```
docker run -it --rm --entrypoint=/bin/bash -v $(pwd):/truststore registry1.dso.mil/ironbank/opensource/keycloak/keycloak:20.0.2
cd /truststore
./ca_bundle_to_truststore.sh
exit
```

## Create a clean DoD CA pem bundle file without expired certs
1. Create the clean pem file which will include expired certs
    ```
    ./dod_cas_to_pem.sh dod_cas_including_expired_certs.pem
    ```
2. Create a final pem file without the expired certs. Previously we used cert_tree.py to remove expired certs but that process is currently broken because the script is filtering out the External CAs with duplicate CNs from the CA bundle. Instead, manually remove remove any expired certs from from the CA bundle.
    View the pem file created created in previous step and take note of the identity of the expired certs. There are usually about 4 of them. For example DoD Root CA "CA-49".
    ```
    ./cert_tree.py dod_cas_including_expired_certs.pem
    ```
    Make a final copy of the CA bundle file.
    ```
    cp dod_cas_including_expired_certs.pem dod_cas.pem
    ```
    Edit the final bundle file and manually search for and remove the expire certs
    ```
    vi dod_cas.pem
    ```
    View the final pem file created above and observe that expired certs are not included
    ```
    ./cert_tree.py dod_cas.pem
    ```
    Do a diff between the two pem files as a sanity check to make sure that there are no unintentional edits. 

## Create a java truststore.jks
After creating the dod_cas.pem file create a java truststore.jks
```
docker run -it --rm --entrypoint=/bin/bash -v $(pwd):/truststore registry1.dso.mil/ironbank/opensource/keycloak/keycloak:20.0.2
cd /truststore
./ca_bundle_to_truststore.sh
exit
```
