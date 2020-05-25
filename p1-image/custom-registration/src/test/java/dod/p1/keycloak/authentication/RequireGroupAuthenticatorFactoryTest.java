package dod.p1.keycloak.authentication;

import org.junit.Before;
import org.junit.Test;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RequireGroupAuthenticatorFactoryTest {

    public static final String EXPECTED_ID = "p1-group-restriction";
    public static final String EXPECTED_NAME = "Platform One Group Authentication Validation";

    private RequireGroupAuthenticatorFactory subjectUnderTest;

    @Before
    public void setup() {
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
        AuthenticationExecutionModel.Requirement actionChoices = Arrays.stream(actualRequirementChoices).findFirst().orElse(null);
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
