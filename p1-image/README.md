To build / test this image, you can use the following command from within this directory:

`DOCKER_BUILDKIT=1 docker build -t p1-keycloak:1.0.0 . && docker run -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=pass p1-keycloak:1.0.0`

Once complete navigate to `http://127.0.0.1:8080/auth/admin/master/console/` and enter Username: admin and password: pass.