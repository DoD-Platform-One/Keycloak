package dod.p1.keycloak.registration;

import org.junit.Assert;
import org.junit.Test;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class RegistrationProtectionProviderTest {

    @Test
    public void testSessionPropertyIsSet() {
        final String SESSION_FIELD = "session";
        KeycloakSession session = mock(KeycloakSession.class);
        AuthenticationManager.AuthResult mockAuthResult = mock(AuthenticationManager.AuthResult.class);
        AppAuthManager mockAppAuthManager = PowerMockito.mock(AppAuthManager.class);
        KeycloakSession actualSession = null;

        Field field;
        try {
            when(mockAppAuthManager, "authenticateIdentityCookie", ArgumentMatchers.any(KeycloakSession.class), ArgumentMatchers.any(mockAppAuthManager.getClass())).thenReturn(mockAuthResult);
            RegistrationProtectionProvider subjectUnderTest = new RegistrationProtectionProvider(session);
            field = RegistrationProtectionProvider.class.getDeclaredField(SESSION_FIELD);
            field.setAccessible(true);
            actualSession = (KeycloakSession) field.get(subjectUnderTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(session, actualSession);
    }

    @Test
    public void testCreateRegistrationProtection() throws Exception {
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        AuthenticationManager.AuthResult mockAuthResult = mock(AuthenticationManager.AuthResult.class);
        AppAuthManager mockAppAuthManager = PowerMockito.mock(AppAuthManager.class);
        when(mockAppAuthManager, "authenticateIdentityCookie", ArgumentMatchers.any(KeycloakSession.class), ArgumentMatchers.any(mockAppAuthManager.getClass())).thenReturn(mockAuthResult);

        RegistrationProtectionProvider subjectUnderTest = new RegistrationProtectionProvider(session, mockAppAuthManager);
        Object actualCreatedObject = subjectUnderTest.getResource();

        Assert.assertEquals(RegistrationProtection.class, actualCreatedObject.getClass());
    }
}
