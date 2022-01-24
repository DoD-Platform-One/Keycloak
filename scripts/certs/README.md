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
This script creates a single clean pem file using the archive downloaded from [DoD Cyber Exchange](https://public.cyber.mil/pki-pke/pkipke-document-library/). Garbage is removed from the achive files. Certs in dir format are converted to pem format. Software and email certs are removed because they are not needed by Keycloak. This script is maintained by Zac Williamson who works on the CNAP team at Platform One.
Usage:
```
./dod_cas_to_pem.sh dod_cas_including_expired_certs.pem
```

## Create a clean DoD CA pem file without expired certs
1. Create the clean pem file which will include expired certs
    ```
    ./dod_cas_to_pem.sh dod_cas_including_expired_certs.pem
    ```
    View the pem file created above and observe that there are expired certs included
    ```
    ./cert_tree.py dod_cas_including_expired_certs.pem
    ```
2. Create a final pem file without the expired certs
    ```
    ./cert_tree.py dod_cas_including_expired_certs.pem 1>/dev/null -r 2> dod_cas.pem
    ```
    View the final pem file created above and observe that expired certs are not included
    ```
    ./cert_tree.py dod_cas.pem
    ```
