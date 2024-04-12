#!/bin/bash

URL=https://dl.dod.cyber.mil/wp-content/uploads/pki-pke/zip/unclass-dod_approved_external_pkis_trust_chains.zip

if ! [ $# == 0 ]; then
  # optional argument handling
  if [[ "$1" == "version" ]]; then
    echo "1.0.0 by Zac Williamson"
    exit 0
  elif [[ "$1" == "url" ]]; then
      echo "Source Zip file $URL"
      exit 0
  elif [ "$1" == '-h' ] ; then
    echo -e "  Download DoD CAs from cyber.mil, Unzip, create pem bundle, removed Expired, Emails, SWs"
    echo -e "  dod_cas_to_pem <dest_pem_file>"
    echo -e "  options:"
    echo -e "           version - show version"
    echo -e "           url     - show DoD zip file url"
    echo -e "           -u      - specify url to DoD CA Zip File"
    echo -e "  Example: dod_cas_to_pem ./dod_cas.pem"
  else
    if [ ! -z $2 ]; then
      if [ $2 == "-u" ]; then
        URL=$3
      fi
    fi
    mkdir /tmp/dod_cas
    curl -sS $URL > /tmp/dod_cas/dod_cas.zip
    env --chdir=/tmp/dod_cas -S jar -xf /tmp/dod_cas/dod_cas.zip

    find /tmp/dod_cas/ -name '*.cer' -print0 |
    while IFS= read -r -d '' line; do
        #if [[ "$line" == *"1-CFS_PIVI_CA2"* ]]; then
        #  echo "Found file. $line"
        #fi
        if openssl x509 -inform der -in "$line" -noout 2>/dev/null; then
          echo "Found in Der format: $line Converting to PEM"
          openssl x509 -inform der -in "$line" -out "$line"
        fi
        if ! sed -n '1{/^-----BEGIN CERTIFICATE-----/!q1;}' "$line" ; then
          echo "Found garbage in file $line Removing garbage";
          sed -i -n '/^-----BEGIN CERTIFICATE-----$/,$p' "$line"
          #sed -i -ne '
          #  /-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p
          #  /-END CERTIFICATE-/q '
          #  "$line"
        fi
    done

    find /tmp/dod_cas -type f  -iname '*.cer' -a ! -regex '\(.*EMAIL.*\|.*SW.*\)' -printf "\n%f\n" -exec cat {} \; > /tmp/dod_cas/cas.pem
    #sed -i 's/\r//g' /tmp/dod_cas/cas.pem
    # ./cert_tree.py /tmp/dod_cas/cas.pem 1>/dev/null -r 2> /tmp/dod_cas/cas_updated.pem
    cp /tmp/dod_cas/cas.pem $1
    # cert_tree.py $1
    rm -rf /tmp/dod_cas

  fi
else
  echo -e " Run with -h for help menu"
fi
