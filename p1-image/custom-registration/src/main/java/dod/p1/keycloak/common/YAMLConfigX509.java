package dod.p1.keycloak.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class YAMLConfigX509 {

    private String userIdentityAttribute;
    private String userActive509Attribute;
    private List<String> autoJoinGroup;
    private List<String> requiredCertificatePolicies;

}
