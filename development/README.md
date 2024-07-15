# Platform One SSO development environment
The Platform One custom Keycloak plugin code is now hosted at https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin. Git clone the that repo on your workstation. See [documentation](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md) in that repo for how to compile and build the plugin jar. 

# Local Dev/Testing

You'll find that the below instructions are very similar to [the procedure that Party Bus uses](https://confluence.il4.dso.mil/pages/viewpage.action?spaceKey=P1CNAPSSO&title=Keycloak).

These steps are all performed in the keycloak repo.

This development environment is intended for local testing of the Platform One custom Keycloak plugin and developement/testing of custom themes. Docker compose is used to start a local dev/test environment quickly and easily. This local dev environment is not ideal for end-to-end SSO testing. For end-to-end SSO testing deploy Keycloak with the plugin in Kubernetes along with the apps that are needed for SSO client testing. Please see [DEVELOPMENT_MAINTENANCE.md](https://repo1.dso.mil/big-bang/product/packages/keycloak/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md) in this repo for deploy and integration testing instructions.

1. A current version of the P1 Keycloak plugin jar is required locally for the docker compose deploy. You can either:
    - Download it from the [P1 Keycloak Plugin package registry](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/packages).
    - Or build a new version of the plugin jar. More documentation, if needed, is in the [plugin repo](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/blob/main/docs/DEVELOPMENT_MAINTENANCE.md).
1. Either:
    - Place the jar file at `development/plugin/p1-keycloak-plugin-X.X.X.jar`.
    - Or create a symbolic link at `development/plugin/p1-keycloak-plugin-X.X.X.jar` pointing to the location on your workstation where you built or downloaded the plugin. 

      Example:
        ```bash
        cd development
        ln -s /absolute-path-to-plugin-repo/keycloak-p1-auth-plugin/build/libs/p1-keycloak-plugin-X.X.X.jar plugin/p1-keycloak-plugin-X.X.X.jar
        ```
1. Ensure that the volume in `development/docker-compose.yaml` matches the name of the file in the `development/plugin` directory.
1. Update the `development/certs` files as needed. See the [development/certs/README.md](./certs/README.md).

1. Start the local docker compose development environment. Typical commands for starting, using, and stopping the local docker compose environment. On Mac, it is recommended to use [Rancher Desktop](https://rancherdesktop.io/) or [Colima](https://github.com/abiosoft/colima) versus the resource heavy Docker Desktop.
    ```bash
    docker compose up -d
    docker logs --follow keycloak
    docker exec -it keycloak /bin/bash
    docker compose down
    ```
1. It may be necessary to edit `/etc/hosts` to comment out or remove any existing `keycloak.dev.bigbang.mil` entries and add it onto the end of the localhost entry e.g. `127.0.0.1      localhost keycloak.dev.bigbang.mil`
1. In a web browser go to `https://keycloak.dev.bigbang.mil:8443/auth/admin`
1. Login with Username: `admin` and Password: `password`
1. Routing redirect for account page is `https://keycloak.dev.bigbang.mil:8443`
1. Routing redirect for registration page is `https://keycloak.dev.bigbang.mil:8443/register`
1. For live theme development, login to the admin console. Navigate to the baby-yoda realm. Navigate to the `Realm settings`. On the `Themes` tab, in the drop-downs, select "p1-sso-live-dev" for Login, Account, and Admin themes. Click the save button. The browser will immediately show any theme changes made in the files at `development/theme-live-dev/theme/p1-sso-live-dev/`.

# Custom Theme Development
Reference the [Keycloak documentation](https://www.keycloak.org/docs/latest/server_development/#deploying-themes) for creating a custom theme. When developing a custom theme you should start with the upstream [keycloak base files](https://github.com/keycloak/keycloak/tree/main/themes/src/main/resources/theme/base). If you want small changes to the Platform One theme then start with those theme files. If you are starting with the Keycloak upstream base theme files it is best to use the code for the release tag that matches the version of Keycloak that you are targeting. You can also compare the upstream base files with the P1 [custom sso theme](https://repo1.dso.mil/big-bang/product/plugins/keycloak-p1-auth-plugin/-/tree/main/p1-keycloak-plugin/src/main/resources/theme/p1-sso). 
1. Use the docker compose dev environment as described in above section. The docker-compose.yaml demonstrates two ways to inject a custom theme into Keycloak.
    1. Drop theme files into the Keycloak container at `/opt/keycloak/themes/`
    1. Drop a theme jar into the Keycloak container at `/opt/keycloak/providers/`
1. Copy your theme files into `development/theme-live-dev/theme/p1-sso-live-dev/`. The docker-compose.yaml volume mounts these theme files into the Keycloak container. Login to the Admin Console and change the theme for the realm to "p1-sso-live-dev" as described in the previous section. Theme changes you make will immediately show in the browser.
1. Continue editing and testing until you are satisfied with your changes.
1. Delete existing theme files from `development/theme-custom/theme/custom-theme` and copy in your live-dev theme files
    ```bash
    rm -r ./theme-custom/theme/custom-theme/*
    cp -r ./theme-live-dev/theme/p1-sso-live-dev/* ./theme-custom/theme/custom-theme/
    ```
1. If you want a different theme name other than "custom-theme" you need to make changes in two places. The theme directory name and the name in `keycloak-themes.json` must match. 
    1. Edit `development/theme-custom/META-INF/keycloak-themes.json` and change the `name:` key value. Example `"name": "my-theme-name"` 
    1. Rename the `development/theme-custom/theme/custom-theme` directory to the theme name you want. Example: `development/theme-custom/theme/my-theme-name`
1. Build a theme jar. Change directory to the `./theme-custom` directory
    ```bash
    cd ./theme-custom/
    jar cvf custom-theme.jar .
    ```
1. Restart the docker compose environment from the `development` directory
    ```bash
    cd development
    docker compose down
    docker compose up -d
    ```
1. The custom theme will now be available in the Admin Console as described previously. Select the custom theme and save.
1. Re-test the custom theme that was deployed from the custom theme jar.


# Custom Theme Deployment In Kubernetes
Now that you have a custom theme developed here are the steps for deploying the theme in Kubernetes. The only way to inject a custom theme into a Kubernetes deployment of Keycloak is to use the custom theme jar. The theme files are too big to fit in the 1 Mb limit for ConfigMap or Secret. Create an image with the jar inside and use a k8s init-container to inject the jar inside the Keycloak pod container. The steps and configuration are the same as how the P1 plugin jar is deployed with Keycloak. Reference the full example [Big Bang production values](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/configs/example/keycloak-prod-values.yaml).
1. Build an image with the custom theme jar inside. Change the steps to use an image repository that you have access to. Reference the `./DockerFile` in this directory.

    Example:

    ```bash
    cd development
    docker build --platform linux/amd64 -t your-registry-domain/path-to-image/custom-theme:X.X.X .
    ```
1. Verify the contents of the image. Example:
    ```bash
    docker run -it --rm your-registry-domain/path-to-image/custom-theme:X.X.X /bin/bash
    ls -l
    ```
1. Push the image to your image repository. Example:
    ```bash
    docker push your-registry-domain/path-to-image/custom-theme:X.X.X
    ```
1.  The Keycloak helm chart supports extra initContainers, volumes, and volumeMounts. Configure the Keycloak helm chart override values to include an init container, volume, and volumemount for the custom plugin jar. The configuration utilizes the k8s emptyDir volume which is shared between all containers in a pod to copy the jar into the Keycloak container. Reference the example [Big Bang production values](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/configs/example/keycloak-prod-values.yaml) in the Big Bang repo for the complete example of injecting a custom theme jar.
    ```yaml
    extraInitContainers: |-
      - name: custom-theme
        image: registry.dso.mil/big-bang/product/packages/keycloak/custom-theme:1.0.0
        imagePullPolicy: Always
        command:
        - sh
        - -c
        - |
          cp /app/custom-theme.jar /init
          ls -l /init
        volumeMounts:
        - name: custom-theme
          mountPath: "/init"
        securityContext:
          capabilities:
            drop:
              - ALL
    extraVolumes: |-
      - name: custom-theme
        emptyDir: {}
    extraVolumeMounts: |-
      - name: custom-theme
        mountPath: /opt/keycloak/providers/custom-theme.jar
        subPath: custom-theme.jar
    ```
1. In the Keycloak Admin Console navigate to your realm settings and select the custom theme and save.


# How to change realm name
1. OPTIONAL: a custom theme to remove P1 branding and any html references to "baby-yoda" in the theme. This is optional and not required for a realm name change to be functional.
1. Log into the Admin console select baby-yoda realm and in the Realm Settings change the "Realm ID". Or, export the baby-yoda realm and make any necessary edits the json to change the name. Then import the new realm.
1. The Platform One Keycloak plugin includes a custom Quarkus extension developed by P1 to handle custom routing and redirects. It is configured with quarkus.properties. Change the quarkus.properties to modify the path of the custom routing and redirects. You can do this with chart value override configuration by creating a secret with the quarkus.properties combined with a volume, and volumemount. Reference the example [Big Bang production values](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/configs/example/keycloak-prod-values.yaml) in the Big Bang repo for the complete example of setting quarkus.properties.
```yaml
secrets:
  quarkusproperties:
    stringData:
      quarkus.properties: |-
        quarkus.http.non-application-root-path=/
        quarkus.kc-routing-redirects.urls./=/auth/realms/your-realm-name/account
        quarkus.kc-routing-redirects.urls./auth=/auth/realms/your-realm-name/account
        quarkus.kc-routing-redirects.urls./register=/auth/realms/your-realm-name/protocol/openid-connect/registrations?client_id=account&response_type=code
        quarkus.kc-routing-redirects.path-prefixes./oauth/authorize=/auth/realms/your-realm-name/protocol/openid-connect/auth
        quarkus.kc-routing-redirects.path-filters./api/v4/user=/auth/realms/your-realm-name/protocol/openid-connect/userinfo
        quarkus.kc-routing-redirects.path-filters./oauth/token=/auth/realms/your-realm-name/protocol/openid-connect/token
extraVolumes: |-
  - name: quarkusproperties
    secret:
      secretName: {{ include "keycloak.fullname" . }}-quarkusproperties
      defaultMode: 0777
extraVolumeMounts: |-
  - name: quarkusproperties
    mountPath: /opt/keycloak/conf/quarkus.properties
    subPath: quarkus.properties
```
1. Any existing client applications will have to change their IDP configuration to use the new "/your-realm-name/" URL path. If you rename your exiting realm you will not need to migrate any users or groups to a new realm.
