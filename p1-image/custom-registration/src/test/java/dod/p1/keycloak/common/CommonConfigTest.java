package dod.p1.keycloak.common;

import org.junit.Test;

import java.util.List;

import static dod.p1.keycloak.common.CommonConfig.getGroupIdByEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CommonConfigTest {

    public static final String EXPECTED_IL2_ENV_NAME = "il2";
    public static final String EXPECTED_IL4_ENV_NAME = "il4";
    public static final String EXPECTED_IL5_ENV_NAME = "il5";
    public static final String EXPECTED_TEST_ENV_NAME = "test";

    public static final String EXPECTED_IL2_GROUP_ID = "00eb8904-5b88-4c68-ad67-cec0d2e07aa6";
    public static final String EXPECTED_IL4_GROUP_ID = "191f836b-ec50-4819-ba10-1afaa5b99600";
    public static final String EXPECTED_IL5_GROUP_ID = "be8d20b3-8cd6-4d7e-9c98-5bb918f53c5c";
    public static final String EXPECTED_TEST_GROUP_ID = "741e5b7b-6918-4179-9d88-a147b4f31d0b";

    @Test
    public void testShouldDefineEnvironmentNames() {
        assertEquals(CommonConfig.IL2_ENV_NAME, EXPECTED_IL2_ENV_NAME);
        assertEquals(CommonConfig.IL4_ENV_NAME, EXPECTED_IL4_ENV_NAME);
        assertEquals(CommonConfig.IL5_ENV_NAME, EXPECTED_IL5_ENV_NAME);
        assertEquals(CommonConfig.TEST_ENV_NAME, EXPECTED_TEST_ENV_NAME);
    }

    @Test
    public void testShouldDefineGroupIds() {
        assertEquals(CommonConfig.IL2_GROUP_ID, EXPECTED_IL2_GROUP_ID);
        assertEquals(CommonConfig.IL4_GROUP_ID, EXPECTED_IL4_GROUP_ID);
        assertEquals(CommonConfig.IL5_GROUP_ID, EXPECTED_IL5_GROUP_ID);
        assertEquals(CommonConfig.TEST_GROUP_ID, EXPECTED_TEST_GROUP_ID);
    }

    @Test
    public void testShouldDefineEnvironmentsAsAList() {
        List<String> actualEnvList = CommonConfig.VALID_ENV_NAMES;
        assertEquals(actualEnvList.get(0), EXPECTED_IL2_ENV_NAME);
        assertEquals(actualEnvList.get(1), EXPECTED_IL4_ENV_NAME);
        assertEquals(actualEnvList.get(2), EXPECTED_IL5_ENV_NAME);
        assertEquals(actualEnvList.get(3), EXPECTED_TEST_ENV_NAME);
    }

    @Test
    public void testShouldDefineValidClientList() {
        List<String> actualClientList = CommonConfig.VALID_BUILTIN_CLIENTS;
        assertEquals(actualClientList.get(0), "account");
        assertEquals(actualClientList.get(1), "account-console");
        assertEquals(actualClientList.get(2), "broker");
        assertEquals(actualClientList.get(3), "security-admin-console");
    }

    @Test
    public void testShouldReturnTheCorrectGroupIdByEnvironment() {
        assertEquals(getGroupIdByEnvironment(EXPECTED_IL2_ENV_NAME), EXPECTED_IL2_GROUP_ID);
        assertEquals(getGroupIdByEnvironment(EXPECTED_IL4_ENV_NAME), EXPECTED_IL4_GROUP_ID);
        assertEquals(getGroupIdByEnvironment(EXPECTED_IL5_ENV_NAME), EXPECTED_IL5_GROUP_ID);
        assertEquals(getGroupIdByEnvironment(EXPECTED_TEST_ENV_NAME), EXPECTED_TEST_GROUP_ID);
        assertNull(getGroupIdByEnvironment("bad-env"));
    }
}

