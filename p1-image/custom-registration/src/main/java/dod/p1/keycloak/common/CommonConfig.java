package dod.p1.keycloak.common;

import java.util.Arrays;
import java.util.List;

public final class CommonConfig {


    public static final String IL2_ENV_NAME = "il2";
    public static final String IL4_ENV_NAME = "il4";
    public static final String IL5_ENV_NAME = "il5";
    public static final String TEST_ENV_NAME = "test";

    public static final String IL2_GROUP_ID = "00eb8904-5b88-4c68-ad67-cec0d2e07aa6";
    public static final String IL4_GROUP_ID = "191f836b-ec50-4819-ba10-1afaa5b99600";
    public static final String IL5_GROUP_ID = "be8d20b3-8cd6-4d7e-9c98-5bb918f53c5c";
    public static final String TEST_GROUP_ID = "741e5b7b-6918-4179-9d88-a147b4f31d0b";

    public static final List<String> VALID_ENV_NAMES = Arrays.asList(
            IL2_ENV_NAME,
            IL4_ENV_NAME,
            IL5_ENV_NAME,
            TEST_ENV_NAME
    );

    public static final List<String> VALID_BUILTIN_CLIENTS = Arrays.asList(
            "account",
            "account-console",
            "broker",
            "security-admin-console"
    );

    public static String getGroupIdByEnvironment(String groupId) {
        switch (groupId) {
            case IL2_ENV_NAME:
                return IL2_GROUP_ID;
            case IL4_ENV_NAME:
                return IL4_GROUP_ID;
            case IL5_ENV_NAME:
                return IL5_GROUP_ID;
            case TEST_ENV_NAME:
                return TEST_GROUP_ID;
            default:
                return null;
        }
    }
}
