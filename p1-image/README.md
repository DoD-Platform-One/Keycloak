To build / test this image, you can use the following command from within this directory:

`docker rm p1-keycloak;DOCKER_BUILDKIT=1 docker build -t p1-keycloak:1.0.0 . && docker run -p 8080:8080 -e DB_VENDOR=h2 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=pass -e _JAVA_OPTIONS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Dkeycloak.profile=preview" -e KEYCLOAK_LOGLEVEL=DEBUG --name p1-keycloak p1-keycloak:1.0.0`

To rebuild/hotreload the p1 custom jar from the cutom-registration directory:

`mvn install && docker cp target/keycloak-registration-validation-1.2.jar p1-keycloak:/opt/jboss/keycloak/standalone/deployments/p1.jar`

Once complete navigate to `http://127.0.0.1:8080/auth/admin` and enter Username: admin and password: pass.  You'll need to add a realm (Master -> Add Realm) and import the baby-yoda.json file.

To create a test case see [Manually Create a Test Case](../docs/create-a-test-case.md)
