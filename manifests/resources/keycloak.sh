#!/usr/bin/env bash

set -o errexit
set -o nounset

# SAD! @todo can we bind to a real IP here?  127.0.0.1 only works with Istio, but hard-breaks in prod for client token retrieval
exec /opt/jboss/tools/docker-entrypoint.sh -b 0.0.0.0 -c standalone-ha.xml