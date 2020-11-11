#!/bin/bash

function update_java() {
    pushd plugin
    ./gradlew build
    mkdir -p build/docker
    cp build/libs/keycloak-registration-validation-1.2-all.jar build/docker/p1.jar
    popd
}

function build() {
    docker kill p1-keycloak
    docker rm p1-keycloak

    DOCKER_BUILDKIT=1 docker build -f Dockerfile.dev -t p1-keycloak:dev-latest . 

    docker run -p 443:8443 -p 5005:5005 \
    --name p1-keycloak \
    -v $PWD/themes:/opt/jboss/keycloak/themes \
    -v $PWD/plugin/build/docker:/opt/jboss/keycloak/standalone/deployments \
    p1-keycloak:dev-latest
}


if [[ $1 == "update" ]]; then
    update_java
else
    update_java
    build
fi
