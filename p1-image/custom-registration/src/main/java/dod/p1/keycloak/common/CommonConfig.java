package dod.p1.keycloak.common;

import java.util.Arrays;
import java.util.List;

public final class CommonConfig {

    public static final String IL2_GROUP_ID = "00eb8904-5b88-4c68-ad67-cec0d2e07aa6";
    public static final String IL4_GROUP_ID = "191f836b-ec50-4819-ba10-1afaa5b99600";
    public static final String IL5_GROUP_ID = "be8d20b3-8cd6-4d7e-9c98-5bb918f53c5c";

    public static final String X509_USER_ATTRIBUTE = "usercertificate";

    public static final List<String> VALID_BUILTIN_CLIENTS = Arrays.asList(
            "account",
            "account-console",
            "broker",
            "security-admin-console"
    );

}
