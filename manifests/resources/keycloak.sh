#!/usr/bin/env bash

set -o errexit
set -o nounset

exec /opt/jboss/tools/docker-entrypoint.sh -b 127.0.0.1 -c standalone-ha.xml