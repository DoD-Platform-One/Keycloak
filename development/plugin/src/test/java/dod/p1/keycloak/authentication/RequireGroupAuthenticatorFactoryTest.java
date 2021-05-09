package dod.p1.keycloak.authentication;

import static dod.p1.keycloak.utils.Utils.setupFileMocks;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;

import dod.p1.keycloak.common.CommonConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Yaml.class, FileInputStream.class, File.class, CommonConfig.class })
public class RequireGroupAuthenticatorFactoryTest {

    public static final String EXPECTED_ID = "p1-group-restriction";
    public static final String EXPECTED_NAME = "Platform One Group Authentication Validation";

    private RequireGroupAuthenticatorFactory subjectUnderTest;

    @Before
    public void setup() throws Exception {
        setupFileMocks();

        subjectUnderTest = new RequireGroupAuthenticatorFactory();
    }

    @Test
    public void testShouldCreateExpectedEndpoint() {
        String actualEndpoint = subjectUnderTest.getId();
        assertEquals(EXPECTED_ID, actualEndpoint);
    }

    @Test
    public void testShouldCreateAuthenticatorProvider() {
        KeycloakSession mockSession = mock(KeycloakSession.class);
        Authenticator actualProvider = subjectUnderTest.create(mockSession);
        assertEquals(RequireGroupAuthenticator.class, actualProvider.getClass());
    }

    @Test
    public void testShouldNameTheModuleProperly() {
        String actualName = subjectUnderTest.getDisplayType();
        assertEquals(EXPECTED_NAME, actualName);
    }

    @Test
    public void testShouldForceAuthenticatorAsRequired() {
        AuthenticationExecutionModel.Requirement[] actualRequirementChoices = subjectUnderTest.getRequirementChoices();
        AuthenticationExecutionModel.Requirement actionChoices = Arrays.stream(actualRequirementChoices).findFirst()
                .orElse(null);
        assertEquals(actualRequirementChoices.length, 1);
        assertEquals(actionChoices, AuthenticationExecutionModel.Requirement.REQUIRED);
    }

    @Test
    public void testShouldSetupOverrides() {
        // Void overrides
        subjectUnderTest.init(null);
        subjectUnderTest.postInit(null);
        subjectUnderTest.close();

        assertNull(subjectUnderTest.getReferenceCategory());
        assertFalse(subjectUnderTest.isConfigurable());
        assertFalse(subjectUnderTest.isUserSetupAllowed());
        assertNull(subjectUnderTest.getHelpText());
        assertNull(subjectUnderTest.getConfigProperties());

    }
}
