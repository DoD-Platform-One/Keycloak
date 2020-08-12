package dod.p1.keycloak.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class YAMLConfig {

    private YAMLConfigX509 x509;

    private List<String> groupProtectionIgnoreClients;
    private List<String> noEmailMatchAutoJoinGroup;

    private List<YAMLConfigEmailAutoJoin> emailMatchAutoJoinGroup;

}
