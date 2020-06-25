To build / test this image, you can use the following command from within this directory:

`docker rm p1-keycloak;DOCKER_BUILDKIT=1 docker build -f Dockerfile.dev -t p1-keycloak:dev-latest . && docker run -p 8443:8443 --name p1-keycloak p1-keycloak:dev-latest`

To rebuild/hotreload the p1 custom jar from the cutom-registration directory:

`./gradlew build && docker cp build/libs/keycloak-registration-validation-1.2.jar p1-keycloak:/opt/jboss/keycloak/standalone/deployments/p1.jar`
<br><br><br>
Once complete navigate to `http://127.0.0.1:8080/auth/admin` and enter Username: admin and password: pass.  You'll need to add a realm (Master -> Add Realm) and import the baby-yoda.json file.

To create a test case see [Manually Create a Test Case](../docs/create-a-test-case.md)
