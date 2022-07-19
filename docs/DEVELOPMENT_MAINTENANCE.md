# How to update the Keycloak Package chart
BigBang makes modifications to the upstream Codecentric helm chart and also builds a custom image using the Iron Bank hardened image. The custom image contains a custom authentication plugin. Eventually the Platform One custom plugin code at ./development will be moved to an Iron Bank plugin pipeline. For now it remains in this repo.
1. Read release notes from upstream [Keycloak documentation](https://www.keycloak.org/docs/latest/release_notes/index.html). Be aware of changes that are included in the upgrade. Take note of any manual upgrade steps that customers might need to perform, if any.
1. Be aware that there are currently two versions of Keycloak. One is the legacy version that uses Wildfly for the application server. The other version is the new one using Quarkus. Big Bang for now will remain with the legacy version. The images in Iron Bank have tag with ```X.X.X-legacy```.
1. Do diff of [upstream chart](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak) between old and new release tags to become aware of any significant chart changes.
1. Create a development branch and merge request from the Keycloak issue.
1. Merge/Sync the new helm chart with the existing Keycloak package code. A graphical diff application like [Meld](https://meldmerge.org/) is useful. Reference the "Modifications made to upstream chart" section below. Be careful not to overwrite Big Bang Package changes that need to be kept.
1. Run a helm dependency command to update the chart/charts/*.tgz archives and create a new requirements.lock file. You will commit the tar archives along with the requirements.lock that was generated.
    ```
    export HELM_EXPERIMENTAL_OCI=1
    helm dependency update ./chart
    ```
1. Update /CHANGELOG.md with an entry for "upgrade Keycloak to app version X.X.X-legacy chart version X.X.X-bb.X". Or, whatever description is appropriate.
1. Update /chart/Chart.yaml to the appropriate versions.
    ```yaml
    version: XX.X.X-bb.X
    appVersion: XX.X.X-legacy
    dependencies:
      - name: gluon
        version: "X.X.X"
    annotations:
      bigbang.dev/applicationVersions: |
        - Keycloak: XX.X.X-legacy
        - PlatformOne Plugin: X.X.X
    ```
1. Update the /README.md following the [gluon library script](https://repo1.dso.mil/platform-one/big-bang/apps/library-charts/gluon/-/blob/master/docs/bb-package-readme.md)
1. Update the /development/Earthfile. This is an [Earthly](https://earthly.dev/) config file. Earthly combines bash script and dockerfile together to make repeatable builds easy. Update PLUGIN_VERSION if the custom plugin code has changed. Update KEYCLOAK_VERSION to the new Iron Bank image tag. Update BIGBANG_VERSION as needed. These variables will be combined to create a composite custom image tag.
1. Update the keycloak library dependencies in the ./development/plugin/build.gradle to match the new version of Keycloak. This library update might cause build errors. You might have to fix code in `src/main/**.java` and `src/test/**.java` to get a build to complete without errors.
1. Build new image using [Earthly](https://earthly.dev/) by following the /development/README.md. If you are not doing Keycloak Java plugin development there is no need to install build tools on your workstation. A remote build server is recommended (EC2 instance) so that your workstation is not cluttered with build tools and artifacts. On the remote build server install [Earthly](https://earthly.dev/get-earthly). Git clone the [Keyclok Package repository](https://repo1.dso.mil/platform-one/big-bang/apps/security-tools/keycloak) in the home directory on the remote build server and then change to the development directory. Switch to your development branch.
    ```bash
    git clone https://repo1.dso.mil/platform-one/big-bang/apps/security-tools/keycloak.git
    cd keycloak/development
    git checkout your-development-branch-name
    ```
    login to registry1 with your development pull credentials.
    ```bash
    docker login registry1.dso.mil
    ```
    Install Earthly on the remote build server.
    Run the Earthly build
    ```bash
    earthly +build-image
    ```
    If the build complets successfully the image will be saved to the remote build server.
    ```bash
    docker image list
    ```
    Verify that the image tag is unique in the [repo1 registry](https://repo1.dso.mil/platform-one/big-bang/apps/security-tools/keycloak/container_registry/3340). There are operational images in this image registry. Be 100% certian that you will not overwrite an existing tag.  
    Login to Repo1 registry with your access token and push the image.
    ```bash
    docker login registry.dso.mil
    docker push registry.dso.mil/platform-one/big-bang/apps/security-tools/keycloak/keycloak-ib:X.X.X-X.X.X-X
    ```
1. Update the image tag in /chart/values.yaml to match the new tag you pushed to the registry.
1. Use a development environment to deploy and test Keycloak end-to-end SSO.


# Testing new Keycloak version
1. Create a k8s dev environment. One option is to use the Big Bang [k3d-dev.sh](https://repo1.dso.mil/platform-one/big-bang/bigbang/-/blob/master/docs/assets/scripts/developer/k3d-dev.sh) with the ```-m``` for metalLB so that k3d can support multiple ingress gateways. The following steps assume you are using the script.
1. Follow all of the instructions at the end of the script to ssh to the EC2 instance with application-level port forwarding. Keep this ssh session for the remainder of the testing. An `example` of the instructions to be followed are below.      
  ```
  SAVE THE FOLLOWING INSTRUCTIONS INTO A TEMPORARY TEXT DOCUMENT SO THAT YOU DON'T LOOSE THEM
  NOTE: The EC2 instance will automatically terminate at 08:00 UTC unless you delete the cron job

  ssh to instance:
  ssh -i ~/.ssh/user.name-dev.pem ubuntu@X.X.X.X

  To access apps from browser start ssh with application-level port forwarding:
  ssh -i ~/.ssh/user.name-dev.pem ubuntu@X.X.X.X -D 127.0.0.1:12345

  To use kubectl from your local workstation you must set the KUBECONFIG environment variable:
  export KUBECONFIG=~/.kube/user.name-dev-config   

  Do not edit /etc/hosts on your local workstation.
  To access apps from a browser edit /etc/hosts on the EC2 instance. Sample /etc/host entries have already been added there.
  Manually add more hostnames as needed.
  
  The IPs to use come from the istio-system services of type LOADBALANCER EXTERNAL-IP that are created when Istio is deployed.
  You must use Firefox browser with with manual SOCKs v5 proxy configuration to localhost with port 12345.
  Also ensure 'Proxy DNS when using SOCKS v5' is checked.
  Or, with other browsers like Chrome you could use a browser plugin like foxyproxy to do the same thing as Firefox.   
  ```

1. You will need to edit the /etc/hosts on the EC2 instance. Make it look like this
    ```bash
    ## begin bigbang.dev section
    172.20.1.240 keycloak.bigbang.dev
    172.20.1.241 gitlab.bigbang.dev sonarqube.bigbang.dev
    ## end bigbang.dev section
    ```
1. For end-to-end SSO testing there needs to be DNS for Keycloak. In a k3d dev environment there is no DNS so you must do a dev hack and edit the configmap "coredns-xxxxxxxx". Under NodeHosts add a host for keycloak.bigbang.dev.    
```
kubectl get cm -n kube-system   
kubectl edit cm coredns -n kube-system   
```

The IP for keycloak in a k3d environment created by the dev script will be 172.20.1.240. Like this
    ```yaml
      NodeHosts:|                                                                                    
        <nil> host.k3d.internal
        172.20.0.2 k3d-k3s-default-agent-0   
        172.20.0.5 k3d-k3s-default-agent-1
        172.20.0.4 k3d-k3s-default-agent-2
        172.20.0.3 k3d-k3s-default-server-0
        172.20.0.6 k3d-k3s-default-serverlb
        172.20.1.240 keycloak.bigbang.dev
    ```
1. Restart the coredns pod so that it picks up the new configmap.
```
kubectl get pods -A   
kubectl delete pod <coredns pod> -n kube-system
```

1. Deploy Big Bang with only istio-operator, istio, gitlab, and sonarqube enabled. Need to test both OIDC and SAML end-to-end SSO. Gitlab uses OIDC and Sonarqube uses SAML. Deploy BigBang using the following example helm command
    ```
    helm upgrade -i bigbang ./chart -n bigbang --create-namespace -f ../overrides/my-bb-override-values.yaml -f ../overrides/registry-values.yaml -f ./chart/ingress-certs.yaml
    ```
    and these example values overrides (be sure to update the keycloak branch in the overrides below)
    ```
    domain: bigbang.dev

    flux:
      interval: 1m
      rollback:
        cleanupOnFail: false

    networkPolicies:
      enabled: true

    clusterAuditor:
      enabled: false

    gatekeeper:
      enabled: false

    kyverno:
      enabled: false

    kyvernopolicies:
      enabled: false

    istiooperator:
      enabled: true

    istio:
      enabled: true
      ingressGateways:
        public-ingressgateway:
          type: "LoadBalancer"
        passthrough-ingressgateway:
          type: "LoadBalancer"
      gateways:
        public:
          ingressGateway: "public-ingressgateway"
          hosts:
          - "*.{{ .Values.domain }}"
        passthrough:
          ingressGateway: "passthrough-ingressgateway"
          hosts:
          - "*.{{ .Values.domain }}"
          tls:
            mode: "PASSTHROUGH"

    jaeger:
      enabled: false

    kiali:
      enabled: false

    logging:
      enabled: false

    eckoperator:
      enabled: false

    fluentbit:
      enabled: false

    monitoring:
      enabled: false

    twistlock:
      enabled: false

    # Gloabl SSO parameters
    sso:
      oidc:
        host: keycloak.bigbang.dev
        realm: baby-yoda

    addons:

      keycloak:
        enabled: true
        git:
          tag: null
          branch: "your-dev-branch-name-here"
        ingress:
          gateway: "passthrough"
        values:
          replicas: 1
          secrets:
            env:
              stringData:
                CUSTOM_REGISTRATION_CONFIG: /opt/jboss/keycloak/customreg.yaml
                KEYCLOAK_IMPORT: /opt/jboss/keycloak/realm.json
                X509_CA_BUNDLE: /etc/x509/https/cas.pem
                # KEYCLOAK_LOGLEVEL: DEBUG
                # WILDFLY_LOGLEVEL: DEBUG
            certauthority:
              stringData:
                cas.pem: '{{ .Files.Get "resources/dev/dod_cas.pem" }}'
            customreg:
              stringData:
                customreg.yaml: '{{ .Files.Get "resources/dev/baby-yoda.yaml" }}'
            realm:
              stringData:
                realm.json: '{{ .Files.Get "resources/dev/baby-yoda.json" }}'
          extraVolumes: |-              
            - name: certauthority
              secret:
                secretName: {{ include "keycloak.fullname" . }}-certauthority
            - name: customreg
              secret:
                secretName: {{ include "keycloak.fullname" . }}-customreg
            - name: realm
              secret:
                secretName: {{ include "keycloak.fullname" . }}-realm
          extraVolumeMounts: |-
            - name: certauthority
              mountPath: /etc/x509/https/cas.pem
              subPath: cas.pem
              readOnly: true
            - name: customreg
              mountPath: /opt/jboss/keycloak/customreg.yaml
              subPath: customreg.yaml
              readOnly: true
            - name: realm
              mountPath: /opt/jboss/keycloak/realm.json
              subPath: realm.json
              readOnly: true

      gitlab:
        enabled: true
        ingress:
          gateway: "public"
        hostnames:
          gitlab: gitlab
          registry: registry
        sso:
          enabled: true
          label: "Platform One SSO"
          client_id: "dev_00eb8904-5b88-4c68-ad67-cec0d2e07aa6_gitlab"
          client_secret: ""
        values:
          gitlab:
            webservice:
              minReplicas: 1
              maxReplicas: 1
            gitlab-shell:
              minReplicas: 1
              maxReplicas: 1
            sidekiq:
              minReplicas: 1
              maxReplicas: 1
          registry:
            hpa:
              minReplicas: 1
              maxReplicas: 1
          global:
            appConfig:
              defaultCanCreateGroup: true

      sonarqube:
        enabled: true
        ingress:
          gateway: "public"
        sso:
          enabled: true
          client_id: "dev_00eb8904-5b88-4c68-ad67-cec0d2e07aa6_saml-sonarqube"
          label: "keycloak sso"
          # this is the Keycloak realm cert. Get it froum the Keycloak admin console
          certificate: "single-line-string-keycloak-realm-cert"
          login: login
          name: name
          email: email
          group: group
    ```
1. Sonarqube needs an extra configuration step for SSO to work because it uses SAML. The values override ```addons.sonarqube.sso.certificate``` needs to be updated with the Keycloak realm certificate. When Keycloak finishes installing login to the admin console [Keycloak](https://keycloak.bigbang.dev/auth/admin) with default credentials ```admin/password```. Navigate to Realm Settings >> Keys. On the RS256 row click on the ```Certificate``` button and copy the certificate text as a single line string and paste it into your ```addons.sonarqube.sso.certificate``` value. Run another ```helm upgrade``` command and watch for Sonarqube to update.
1. Use Firefox browser with SOCKS v5 manual proxy configured so that we are running Firefox as if it was running on the EC2 instance. This is described in more detail in the development environment addendum [Multi Ingress-gateway Support with MetalLB and K3D](https://repo1.dso.mil/platform-one/big-bang/bigbang/-/blob/master/docs/developer/development-environment.md)
1. In the Firefox browser load ```https://keycloak.bigbang.dev``` and register a test user. You should register yourself with CAC and also a non-CAC test.user with just user and password with OTP. Both flows need to be tested.
1. Then go back to ```https://keycloak.bigbang.dev/auth/admin``` and login to the admin console with the default credentials ```admin/password```
1. Navigate to users, click "View all users" button and edit the test users that you created. Set "Email Verified" ON. Remove the verify email "Required User Actions". Click "Save" button.
1. Test end-to-end SSO with Gitlab and Sonarqube with your CAC user and the other test user.
1. Test the custom user forms to make sure all the fields are working
    - https://keycloak.bigbang.dev/auth/realms/baby-yoda/account/
    - https://keycloak.bigbang.dev/auth/realms/baby-yoda/account/password
    - https://keycloak.bigbang.dev/auth/realms/baby-yoda/account/totp
    - https://keycloak.bigbang.dev/register
1. Occasionally the DoD certificate authorities will need to be updated. Follow the instructions at ```/scripts/certs/README.md``` and copy the new ```dod_cas.pem``` to ```chart/resources/dev```. You might have to edit the ```/scripts/certs/dod_cas_to_pem.sh``` to update to the most recent published certs.

# Modifications made to upstream chart
This is a high-level list of modifications that Big Bang has made to the upstream helm chart. You can use this as as cross-check to make sure that no modifications were lost during the upgrade process.

## chart/values.yaml
- disable all internal services other than postgres
- add BigBang additional values at bottom of values.yaml
- add IronBank hardened image
- add default argument of "-b 0.0.0.0" to bind to localhost
- Add IronBank postgresql12 image for dev/CI development/testing
- and other miscellaneous change.  Diff with previous version to find all changes
- modify pgchecker image to ironbank postgres image

##  chart/charts/*.tgz
- run ```helm dependency update``` and commit the downloaded archives
- also commit the requirements.lock file so that air-gap deployments don't try to check for updates

## chart/Chart.lock
- Chart.lock is updated during ```helm dependency update``` with the gluon library & postgresql dependency

## chart/templates/StatefulSet.yaml
- add extraVolumesBigBang (lines 196-189)
- add extraVolumeMountsBigBang (lines 146-148)
- modify pgchecker initContainer (lines 54-64)

## chart/templates/bigbang/*
- add istio virtual service
- add NetworkPolicies to restrict traffic

## chart/resources/
- add /dev directory to hold the the baby-yoda configuration files
- add the DoD certificate bundle pem file

## chart/tests
- add directory with cypress test files

## chart/templates/tests  (this is separate from the upstream templates/test directory)
- add helm template to add support for the helm test library

## chart/Chart.yaml
- update the chart version with the bigbang ```-bb.#```
- update app version when not the same as the original chart
- add gluon library dependency
- Update postgresql dependency for local source
- add annotations for release automation

## chart/Kptfile
- file created when kpt was used to download the upstream chart

## chart/scripts/keycloak.cli
- delete this upstream file.  Don't want to encourage anyone to override the startup script.

## chart/deps/postgresql
- Upstream bitnami postgresql chart - modified for Iron Bank Postgresql 12.9 runtime.
- Update security context for user:group 26:26
