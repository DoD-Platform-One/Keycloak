#!/bin/bash

function build() {
    docker kill p1-keycloak
    docker rm p1-keycloak

    DOCKER_BUILDKIT=1 docker build -f Dockerfile.dev -t p1-keycloak:dev-latest ../

    docker run -p 8443:8443 -p 5005:5005 \
    --name p1-keycloak \
    -v $PWD/plugin/build/docker:/opt/jboss/keycloak/standalone/deployments \
    -v $PWD/plugin/src/main/resources/theme/p1-sso:/opt/jboss/keycloak/themes/p1-sso-live-dev \
    p1-keycloak:dev-latest
}

case "$1" in

    update)
        earthly +build-local
        ;;

    release)
        earthly +build-image
        ;;

    *)
        earthly +build-local
        build
        ;;

esac