# Platform One SSO

Platform One Keycloak customizations.

# Local Development

1. Build and Run: `./build.sh`
    ### Console output, like this, indicates the server is up and running
    ```
    Keycloak 11.0.1 (WildFly Core 11.1.1.Final) started in 20825ms - Started 809 of 1116 services (721 services are lazy, passive or on-demand)
    ```

2. Navigate Browser to `https://keycloak.bigbang.dev/auth/admin`
3. Login with Username: `admin` and Password: `pass`
4. Happy Debugging!
5. Inline re-builds: `./build.sh update`

## Groups and Users and Such

Keycloak just doesn't really solve our problem without some [customization](plugin/src/main/java/dod/p1/keycloak/authentication/RequireGroupAuthenticator.java).

TLDR;
```java
public class RequireGroupAuthenticator implements Authenticator {
    
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        //...We do things here...
    }
}
```

First, a keycloak admin creates groups and users.   
Second, when a logged in user attempts to access code.il2.dso.mil (for example) the custom authenticate method is called under the hood.  

This custom logic grants/denies access via the context.

We define UUIDs for IL2, IL4, IL5. [CommonConfig.java](plugin/src/main/java/dod/p1/keycloak/common/CommonConfig.java)
```java
    public static final String IL2_GROUP_ID = "00eb8904-5b88-4c68-ad67-cec0d2e07aa6";
    public static final String IL4_GROUP_ID = "191f836b-ec50-4819-ba10-1afaa5b99600";
    public static final String IL5_GROUP_ID = "be8d20b3-8cd6-4d7e-9c98-5bb918f53c5c";
```
### Group Structuring

Assumptions:
- Explicit allow, least privilege

| Group Name | Description | Example |
| --- | --- | --- |
| Impact Level 2 Authorized | Default Group for IL2 users | Brand new user that can login to Client Software and see no data |
| Impact Level 4 Authorized | Default Group for IL4 users | Brand new user that can login to Client Software and see no data |
| Impact Level 5 Authorized | Default Group for IL5 users | Brand new user that can login to Client Software and see no data |
| {Optional Hierarchy}/{Team}-{Qualifier} | Organizational Hierarchy down to a team level | DoD/PlatformOne/IronBank/VAT-PartyBus |
| MissionApps/{SoftwareNode}/{Product} | Hierarchy for end users | AirForce/AirCombatCommand/67th/OpsSupport/ProductX |

Qualifiers
- PartyBus: Users in this group have full access to CI/CD tools, Collaboration Tools, and intent of C-ATO for the specified group.
- ToeDipper: Users in this group have full access to CI/CD tools, but no intent of a C-ATO.
- Collaborators: Users in this group have full access to Collaboration tools (Mattermost, Jira, Confluence, Jitsi) for their team.

## Creating a test Case

To create a test case see [Manually Create a Test Case](../docs/create-a-test-case.md)




