# Context

The custom authorization plugin for keycloak, controls access to resources behind keycloak clients. The clients (via custom code) are associated through group UUIDs. The group structure and membership must be agreed upon for 'soft' implementation and declarative implementation.

## Group Structure

```
Organization Parent Group
└──optional-hierarchy-product-team
   └── collaborator
   └── developer
```

Examples
```
# Example 1
Platform-One/
    Products/
       Valkyrie/
          collaborator
          developer
# Example 2
SpaceCAMP/
    Genesis/
        collaborator
        developer

# Example 3
USMC/
    Marine Coders/
        collaborator
        developer
```

Implications and Rules:
- A user is a member of either collaborator or developer.
- the developer group indicates access to full development tool suite, in addition to collaborator tools. The "full developer suite" would include any deployed tool that is integrated with SSO and needed for product development. Today that includes, Mattermost, Jira, Confluence, gitlab, sonarqube, fortify, and argo. Future implementation would include monitoring and logging solutions.
- the collaborator group indicates access to deployed applications and Mattermost/Jira/Confluence
- On a case by case basis, product teams may require an additional group/sub-group to identify roles. i.e. Platform-One/Products/Valkyrie/roles/ADMIN

## Considerations and Use Cases

1. Declarative implementation - it is ideal to define a structure that can be easily defined in declarative yaml, like the realm.yaml configuration. This is consistent with other aspects of the SSO implementation.

2. The declarative implementation can also support license restrictions. For example, Organization Disney can purchase 2500 collaborator seats and 30000 developer seats. The declarative configuration would allow/restrict according to these rules.

3. The group structure is mostly focused on the Day 2 operations and related to "Party Bus" operations. While other use cases exist, this ADR is not intended to solve their unique problems.

4. In the future, we'd like to enable "self management". A team could possibly add a role (group) named 'manager' or equivalent to support.

5. A user should not have access if they are not also in the respective environment group. (i.e. IL2 Authorized and P1/Product/Unicorn/developer would both be required for a user to have access to code.il2.dso.mil/p1/product/unicorn)
