package dod.p1.keycloak.registration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Instant.class, RegistrationValidation.class})
public class RegistrationProtectionTest {

    @Before
    // Fixed the time for time-sensitive tests
    public void initTimeMocks() {
        long epochSeconds = 1589569200000L;
        Instant testInstant = Instant.ofEpochMilli(epochSeconds);

        PowerMockito.mockStatic(Instant.class);
        PowerMockito.when(Instant.now()).thenReturn(testInstant);
    }

    @Test
    public void testSessionPropertyIsSet() {
        final String SESSION_FIELD = "session";
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        AuthenticationManager.AuthResult mockAuthResult = mock(AuthenticationManager.AuthResult.class);
        AppAuthManager mockAppAuthManager = mock(AppAuthManager.class);
        KeycloakSession actualSession = null;

        try {
            when(mockAppAuthManager, "authenticateIdentityCookie", ArgumentMatchers.eq(session), ArgumentMatchers.eq(mockAppAuthManager)).thenReturn(mockAuthResult);
            RegistrationProtection subjectUnderTest = new RegistrationProtection(session, mockAppAuthManager);
            Field field = RegistrationProtection.class.getDeclaredField(SESSION_FIELD);
            field.setAccessible(true);
            actualSession = (KeycloakSession) field.get(subjectUnderTest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(session, actualSession);
    }

    @Test
    public void testValidInviteCode() {
        String realmName = "master-yoda";
        String inviteSecret = "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N";
        String inviteSecretDays = "5";

        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        RealmModel realmModel = mock(RealmModel.class);
        when(context.getRealm()).thenReturn(realmModel);
        List<AuthenticatorConfigModel> configModels = new ArrayList<>();
        AuthenticatorConfigModel authConfigModel = mock(AuthenticatorConfigModel.class);
        configModels.add(authConfigModel);
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("inviteSecret", inviteSecret);
        configMap.put("inviteSecretDays", inviteSecretDays);
        when(authConfigModel.getConfig()).thenReturn(configMap);
        when(realmModel.getAuthenticatorConfigs()).thenReturn(configModels);
        when(realmModel.getName()).thenReturn(realmName);

        AppAuthManager authManager = mock(AppAuthManager.class);
        AuthenticationManager.AuthResult authResult = mock(AuthenticationManager.AuthResult.class);
        when(authManager.authenticateIdentityCookie(session, realmModel)).thenReturn(authResult);
        RegistrationProtection protection = new RegistrationProtection(session, authManager);
        RegistrationProtection.InviteCode inviteCode = protection.get();

        Assert.assertEquals(true, inviteCode.success);
        Assert.assertEquals(Integer.parseInt(inviteSecretDays), inviteCode.days.intValue());

        Assert.assertEquals(
                "/auth/realms/master-yoda/protocol/openid-connect/registrations?client_id=account&response_type=code&invite=crDqh1eTTQP3wnGhBDy2rmZprN3vSz1GjGdx2OItNSo%3D",
                inviteCode.link);

    }

    @Test
    public void testInvalidInviteCode() {
        String realmName = "master-yoda";
        String inviteSecret = "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N";
        String inviteSecretDays = "5";
        //mock session
        KeycloakSession session = mock(KeycloakSession.class);
        KeycloakContext context = mock(KeycloakContext.class);
        when(session.getContext()).thenReturn(context);
        RealmModel realmModel = mock(RealmModel.class);
        when(context.getRealm()).thenReturn(realmModel);
        List<AuthenticatorConfigModel> configModels = new ArrayList<>();
        //missing "inviteSecret" in config map
        AuthenticatorConfigModel authConfigModel = mock(AuthenticatorConfigModel.class);
        configModels.add(authConfigModel);
        HashMap<String, String> configMap = new HashMap<>();
        when(authConfigModel.getConfig()).thenReturn(configMap);
        when(realmModel.getAuthenticatorConfigs()).thenReturn(configModels);
        when(realmModel.getName()).thenReturn(realmName);

        AppAuthManager authManager = mock(AppAuthManager.class);
        AuthenticationManager.AuthResult authResult = mock(AuthenticationManager.AuthResult.class);
        when(authManager.authenticateIdentityCookie(session, realmModel)).thenReturn(authResult);
        RegistrationProtection protection = new RegistrationProtection(session, authManager);
        RegistrationProtection.InviteCode inviteCode = protection.get();

        Assert.assertEquals(false, inviteCode.success);
        Assert.assertNull(inviteCode.days);
        Assert.assertEquals("", inviteCode.link);

    }
}
