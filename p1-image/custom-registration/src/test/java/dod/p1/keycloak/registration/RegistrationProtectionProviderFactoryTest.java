package dod.p1.keycloak.registration;

import org.junit.Assert;
import org.junit.Test;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import static org.mockito.Mockito.mock;

public class RegistrationProtectionProviderFactoryTest {
    public static final String EXPECTED_ID = "generate-invite-link";

    @Test
    public void testShouldCreateExpectedEndpoint() {
        RegistrationProtectionProviderFactory subjectUnderTest = new RegistrationProtectionProviderFactory();
        String actualEndpoint = subjectUnderTest.getId();

        Assert.assertEquals(EXPECTED_ID, actualEndpoint);
    }

    @Test
    public void testsCreateRegistrationProtectionProvider() {
        KeycloakSession mockSession = mock(KeycloakSession.class);
        RegistrationProtectionProviderFactory subjectUnderTest = new RegistrationProtectionProviderFactory();
        RealmResourceProvider actualProvider = subjectUnderTest.create(mockSession);

        Assert.assertEquals(RegistrationProtectionProvider.class, actualProvider.getClass());
    }
}
