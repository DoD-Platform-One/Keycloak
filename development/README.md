# Platform One SSO development environment
The Platform One custom Keycloak plugin code is now hosted at [https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin). Git clone the that repo on your workstation. See documentation in that repo for how to compile and build the plugin jar. 

# Local Development
This development environment is intended for local development of the Platform One custom Keycloak plugin. Docker compose is used to start a local dev/test environment quickly and easily. This local dev environment is not ideal for end-to-end SSO testing. For end-to-end SSO testing deploy Keycloak with the plugin in Kubernetes along with the apps that are needed for SSO client testing. A Big Bang dev values override is in this repo at ./docs/assets/config/example/keycloak-bigbang-dev-values.yaml

1. To try out this docker compose environmnet you will need to download a current version of the P1 Keycloak plugin from the [P1 Keycloak Plugin package registry](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/packages) and place the jar file at `./plugin/p1-keycloak-plugin-X.X.X.jar`. Insure that the volume in the docker-compose.yaml matches the name of the file in the `./plugin/` directory. Then skip down several steps to `Start the local docker compose...` where the docker compose commands are.
1. For new plugin development continue here.
1. Update the ./certs files as needed. See the [./certs/README.md](./certs/README.md).
1. Build the plugin jar. More documentation, if needed, is in the [plugin repo](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/tree/main/docs). Your code changes must pass the linting and unit tests to successfully build.
1. Create a symbolic link for `./plugin/p1-keycloak-plugin-X.X.X.jar` to the location on your workstation where you built a new version of the plugin. The plugin jar version must match what is in the docker-compose.yaml volume. Example:
    ```bash
    ln -s $HOME/path-to-plugin-repo/keycloak-p1-auth-plugin/build/libs/p1-keycloak-plugin-X.X.X.jar p1-keycloak-plugin-X.X.X.jar
    ```
1. Start the local docker compose development environment. Typical commands for starting, using, and stopping the local docker compose environment.
    ```bash
    docker compose up -d
    docker logs --follow keycloak
    docker exec -it keycloak /bin/bash
    docker compose down
    ```
1. There is no need to edit /etc/hosts. The wildcard `*.bigbang.dev` cert points to localhost.
1. In a web browser go to `https://keycloak.bigbang.dev:8443/auth/admin`
1. Login with Username: `admin` and Password: `password`
1. Routing redirect for account page is `https://keycloak.bigbang.dev:8443`
1. Routing redirect for registration page is `https://keycloak.bigbang.dev:8443/register`
1. For live theme development, login to the admin console. Navigate to the baby-yoda realm. Navigate to the `Realm settings`. On the `Themes` tab, in the drop-downs, select "p1-sso-live-dev" for Login, Account, and Admin themes. Click the save button. The browser will immediately show any theme changes made in the files at `development/theme-live-dev/theme/p1-sso-live-dev/`.

# Custom Theme Development
Reference the [Keycloak documentation](https://www.keycloak.org/docs/latest/server_development/#deploying-themes) for creating a custom theme. When developing a custom theme you should start with the upstream [keycloak base files](https://github.com/keycloak/keycloak/tree/main/themes/src/main/resources/theme/base). If you want small changes to the Platform One theme then start with those theme files. If you are starting with the Keycloak upstream base theme files it is best to use the code for the release tag that matches the version of Keycloak that you are targeting. You can also compare the upstream base files with the P1 [custom sso theme](https://repo1.dso.mil/big-bang/apps/product-tools/keycloak-p1-auth-plugin/-/tree/main/p1-keycloak-plugin/src/main/resources/theme/p1-sso). 
1. Use the docker compose dev environment as described in above section. The docker-compose.yaml demonstrates two ways to inject a custom theme into Keycloak.
    1. Drop theme files into the Keycloak container at `/opt/keycloak/themes/`
    1. Drop a theme jar into the Keycloak container at `/opt/keycloak/providers/`
1. Copy your theme files into `./theme-live-dev/theme/p1-sso-live-dev/`. The docker-compose.yaml volume mounts these theme files into the Keycloak container. Login to the Admin Console and change the theme for the realm to "p1-sso-live-dev" as described in the previous section. Theme changes you make will immediately show in the browser.
1. Continue editing and testing until you are satisfied with your changes.
1. Delete existing theme files from `./theme-custom/theme/custom-theme` and copy in your live-dev theme files
    ```bash
    rm -r ./theme-custom/theme/custom-theme/*
    cp -r ./theme-live-dev/theme/p1-sso-live-dev/* ./theme-custom/theme/custom-theme/
    ```
1. If you want a different theme name other than "custom-theme" you need to make changes in two places. The theme directory name and the name in `keycloak-themes.json` must match. 
    1. Edit `./theme-custom/META-INF/keycloak-themes.json` and change the `name:` key value. Example `"name": "my-theme-name"` 
    1. Rename the `./theme-custom/theme/custom-theme` directory to the theme name you want. Example: `./theme-custom/theme/my-theme-name`
1. Build a theme jar. Change directory to the `./theme-custom` directory
    ```bash
    cd ./theme-custom/
    jar cvf custom-theme.jar .
    ```
1. Restart the docker compose environment from the `./development` directory
    ```bash
    cd ./development/
    docker compose down
    docker compose up -d
    ```
1. The custom theme will now be available in the Admin Console as described previously. Select the custom theme and save.
1. Retest the custom theme that was deployed from the custom theme jar.


# Custom Theme Deployment In Kubernetes
Now that you have a custom theme developed here are the steps for deploying the theme in Kubernetes. The only way to inject a custom theme into a Kubernetes deployment of Keycloak is to use the custom theme jar. The theme files are too big to fit in the 1 Mb limit for ConfigMap or Secret. Create an image with the jar inside and use a k8s init-container to inject the jar inside the Keycloak pod container. The steps and configuration are the same as how the P1 plugin jar is deployed with Keycloak. Reference the full example [Big Bang production values](https://repo1.dso.mil/big-bang/bigbang/-/blob/master/docs/assets/configs/example/keycloak-prod-values.yaml).
1. Build an image with the custom theme jar inside. Change the steps to use an image repository that you have access to. Reference the `./DockerFile` in this directory. Example:
    ```bash
    cd ./development
    docker build -t your-registry-domain/path-to-image/custom-theme:X.X.X .
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
