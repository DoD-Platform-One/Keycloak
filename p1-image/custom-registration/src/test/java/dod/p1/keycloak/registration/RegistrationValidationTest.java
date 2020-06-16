package dod.p1.keycloak.registration;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.common.ClientConnection;
import org.keycloak.component.ComponentModel;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.*;
import org.keycloak.models.cache.UserCache;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.Provider;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.storage.federated.UserFederatedStorageProvider;
import org.keycloak.vault.VaultTranscriber;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistrationValidationTest {

    public ValidationContext setupVariables(String[] errorEvent, List<FormMessage> errors, MultivaluedMap<String, String> multivaluedMap,
                                            AuthenticatorConfigModel configModel) {
        return new ValidationContext() {
            final RealmModel realmModel = mock(RealmModel.class);

            @Override
            public void validationError(MultivaluedMap<String, String> multivaluedMap, List<FormMessage> list) {
                errors.addAll(list);
            }

            @Override
            public void error(String s) {
                errorEvent[0] = s;
            }

            @Override
            public void success() {

            }

            @Override
            public void excludeOtherErrors() {

            }

            @Override
            public EventBuilder getEvent() {
                return mock(EventBuilder.class);
            }

            @Override
            public EventBuilder newEvent() {
                return null;
            }

            @Override
            public AuthenticationExecutionModel getExecution() {
                return null;
            }

            @Override
            public UserModel getUser() {
                return null;
            }

            @Override
            public void setUser(UserModel userModel) {

            }

            @Override
            public RealmModel getRealm() {
                return realmModel;
            }

            @Override
            public AuthenticationSessionModel getAuthenticationSession() {
                return mock(AuthenticationSessionModel.class);
            }

            @Override
            public ClientConnection getConnection() {
                return mock(ClientConnection.class);
            }

            @Override
            public UriInfo getUriInfo() {
                return mock(UriInfo.class);
            }

            @Override
            public KeycloakSession getSession() {
                return new KeycloakSession() {
                    @Override
                    public KeycloakContext getContext() {
                        return null;
                    }

                    @Override
                    public KeycloakTransactionManager getTransactionManager() {
                        return null;
                    }

                    @Override
                    public <T extends Provider> T getProvider(Class<T> aClass) {
                        return null;
                    }

                    @Override
                    public <T extends Provider> T getProvider(Class<T> aClass, String s) {
                        return null;
                    }

                    @Override
                    public <T extends Provider> T getProvider(Class<T> aClass, ComponentModel componentModel) {
                        return null;
                    }

                    @Override
                    public <T extends Provider> Set<String> listProviderIds(Class<T> aClass) {
                        return null;
                    }

                    @Override
                    public <T extends Provider> Set<T> getAllProviders(Class<T> aClass) {
                        return null;
                    }

                    @Override
                    public Class<? extends Provider> getProviderClass(String s) {
                        return null;
                    }

                    @Override
                    public Object getAttribute(String s) {
                        return null;
                    }

                    @Override
                    public <T> T getAttribute(String s, Class<T> aClass) {
                        return null;
                    }

                    @Override
                    public Object removeAttribute(String s) {
                        return null;
                    }

                    @Override
                    public void setAttribute(String s, Object o) {

                    }

                    @Override
                    public void enlistForClose(Provider provider) {

                    }

                    @Override
                    public KeycloakSessionFactory getKeycloakSessionFactory() {
                        return null;
                    }

                    @Override
                    public RealmProvider realms() {
                        return null;
                    }

                    @Override
                    public UserSessionProvider sessions() {
                        return null;
                    }

                    @Override
                    public AuthenticationSessionProvider authenticationSessions() {
                        return null;
                    }

                    @Override
                    public void close() {

                    }

                    @Override
                    public UserCache userCache() {
                        return null;
                    }

                    @Override
                    public UserProvider users() {
                        UserProvider userProvider = mock(UserProvider.class);
                        when(userProvider.getUserByEmail("test@ss.usafa.edu", realmModel)).thenReturn(mock(UserModel.class));
                        return userProvider;
                    }

                    @Override
                    public ClientProvider clientStorageManager() {
                        return null;
                    }

                    @Override
                    public UserProvider userStorageManager() {
                        return null;
                    }

                    @Override
                    public UserCredentialManager userCredentialManager() {
                        return null;
                    }

                    @Override
                    public UserProvider userLocalStorage() {
                        return null;
                    }

                    @Override
                    public RealmProvider realmLocalStorage() {
                        return null;
                    }

                    @Override
                    public ClientProvider clientLocalStorage() {
                        return null;
                    }

                    @Override
                    public UserFederatedStorageProvider userFederatedStorage() {
                        return null;
                    }

                    @Override
                    public KeyManager keys() {
                        return null;
                    }

                    @Override
                    public ThemeManager theme() {
                        return null;
                    }

                    @Override
                    public TokenManager tokens() {
                        return null;
                    }

                    @Override
                    public VaultTranscriber vault() {
                        return null;
                    }
                };
            }

            @Override
            public HttpRequest getHttpRequest() {
                return new HttpRequest() {
                    @Override
                    public HttpHeaders getHttpHeaders() {
                        return null;
                    }

                    @Override
                    public MultivaluedMap<String, String> getMutableHeaders() {
                        return null;
                    }

                    @Override
                    public InputStream getInputStream() {
                        return null;
                    }

                    @Override
                    public void setInputStream(InputStream inputStream) {

                    }

                    @Override
                    public ResteasyUriInfo getUri() {
                        return null;
                    }

                    @Override
                    public String getHttpMethod() {
                        return null;
                    }

                    @Override
                    public void setHttpMethod(String s) {

                    }

                    @Override
                    public void setRequestUri(URI uri) throws IllegalStateException {

                    }

                    @Override
                    public void setRequestUri(URI uri, URI uri1) throws IllegalStateException {

                    }

                    @Override
                    public MultivaluedMap<String, String> getFormParameters() {
                        return null;
                    }

                    @Override
                    public MultivaluedMap<String, String> getDecodedFormParameters() {
                        return multivaluedMap;
                    }

                    @Override
                    public Object getAttribute(String s) {
                        return null;
                    }

                    @Override
                    public void setAttribute(String s, Object o) {

                    }

                    @Override
                    public void removeAttribute(String s) {

                    }

                    @Override
                    public Enumeration<String> getAttributeNames() {
                        return null;
                    }

                    @Override
                    public ResteasyAsynchronousContext getAsyncContext() {
                        return null;
                    }

                    @Override
                    public boolean isInitial() {
                        return false;
                    }

                    @Override
                    public void forward(String s) {

                    }

                    @Override
                    public boolean wasForwarded() {
                        return false;
                    }
                };
            }

            @Override
            public AuthenticatorConfigModel getAuthenticatorConfig() {
                return configModel;
            }
        };
    }

    @Test
    public void testInvalidFields() {
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        AuthenticatorConfigModel configModel = new AuthenticatorConfigModel();
        ValidationContext context = setupVariables(errorEvent, errors, valueMap, configModel);
        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        Set<String> errorFields = errors.stream().map(FormMessage::getField).collect(Collectors.toSet());
        Assert.assertTrue(errorFields.contains("firstName"));
        Assert.assertTrue(errorFields.contains("lastName"));
        Assert.assertTrue(errorFields.contains("username"));
        Assert.assertTrue(errorFields.contains("user.attributes.affiliation"));
        Assert.assertTrue(errorFields.contains("user.attributes.rank"));
        Assert.assertTrue(errorFields.contains("user.attributes.organization"));
        Assert.assertTrue(errorFields.contains("email"));
        Assert.assertEquals(8, errors.size());
        Set<String> errorMessages = errors.stream().map(FormMessage::getMessage).collect(Collectors.toSet());
        Assert.assertTrue(errorMessages.contains("Invalid or expired registration code."));
    }

    @Test
    public void testInvalidInviteCode() {
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("email", "test@af.mil");
        valueMap.putSingle("invite", "invitecode");

        AuthenticatorConfigModel configModel = new AuthenticatorConfigModel();
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("inviteSecret", "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N");
        configMap.put("inviteSecretDays", "2");
        configModel.setConfig(configMap);
        ValidationContext context = setupVariables(errorEvent, errors, valueMap, configModel);
        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        FormMessage invalidInviteError = errors.get(0);
        Assert.assertTrue(invalidInviteError.getField().isEmpty());
        Assert.assertEquals("Invalid or expired registration code.", invalidInviteError.getMessage());
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testValidInviteCode() {
        String inviteSecret = "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N";
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("email", "test@af.mil");
        String inviteDigest = RegistrationValidation.getInviteDigest(0, inviteSecret);
        String invitedUrlEncoded = URLEncoder.encode(inviteDigest, StandardCharsets.UTF_8);
        String invitedUrlDecoded = URLDecoder.decode(invitedUrlEncoded, StandardCharsets.UTF_8);
        valueMap.putSingle("invite", invitedUrlDecoded);

        AuthenticatorConfigModel configModel = new AuthenticatorConfigModel();
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("inviteSecret", inviteSecret);
        configMap.put("inviteSecretDays", "2");
        configModel.setConfig(configMap);
        ValidationContext context = setupVariables(errorEvent, errors, valueMap, configModel);
        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertNull(errorEvent[0]);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testExpiredInviteCode() {
        String inviteSecret = "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N";
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("email", "test@af.mil");
        String inviteDigest = RegistrationValidation.getInviteDigest(20, inviteSecret);
        String invitedUrlEncoded = URLEncoder.encode(inviteDigest, StandardCharsets.UTF_8);
        String invitedUrlDecoded = URLDecoder.decode(invitedUrlEncoded, StandardCharsets.UTF_8);
        valueMap.putSingle("invite", invitedUrlDecoded);

        AuthenticatorConfigModel configModel = new AuthenticatorConfigModel();
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("inviteSecret", inviteSecret);
        configMap.put("inviteSecretDays", "2");
        configModel.setConfig(configMap);
        ValidationContext context = setupVariables(errorEvent, errors, valueMap, configModel);
        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        FormMessage invalidInviteError = errors.get(0);
        Assert.assertTrue(invalidInviteError.getField().isEmpty());
        Assert.assertEquals("Invalid or expired registration code.", invalidInviteError.getMessage());
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void testEmailValidation() {
        String inviteSecret = "wFLZTdbSqBLO2gb2AjtFNc8aM76iAaSHK7F55JLJajeOblBnaThajQLrtcYB90N";
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("email", "test@gmail.com");
        String inviteDigest = RegistrationValidation.getInviteDigest(0, inviteSecret);
        String invitedUrlEncoded = URLEncoder.encode(inviteDigest, StandardCharsets.UTF_8);
        String invitedUrlDecoded = URLDecoder.decode(invitedUrlEncoded, StandardCharsets.UTF_8);
        valueMap.putSingle("invite", invitedUrlDecoded);

        AuthenticatorConfigModel configModel = new AuthenticatorConfigModel();
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put("inviteSecret", inviteSecret);
        configMap.put("inviteSecretDays", "2");
        configMap.put("il2ApprovedDomains", "unicorns.com##trex.scary");
        configMap.put("il4ApprovedDomains", "mil##gov##usafa.edu##afit.edu");
        configModel.setConfig(configMap);
        ValidationContext context = setupVariables(errorEvent, errors, valueMap, configModel);

        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(errors.get(0).getField(), "email");

        // test an email address already in use
        valueMap.putSingle("email", "test@ss.usafa.edu");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap, configModel);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(Errors.EMAIL_IN_USE, errorEvent[0]);
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(RegistrationPage.FIELD_EMAIL, errors.get(0).getField());

        //test valid IL2 email with custom domains
        valueMap.putSingle("email", "rando@supercool.unicorns.com");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap, configModel);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertNull(errorEvent[0]);
        Assert.assertEquals(0, errors.size());

        //test invalid IL2 email with custom domains
        valueMap.putSingle("email", "rando@supercoolunicorns.com");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap, configModel);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(errors.get(0).getField(), "email");

        //test valid IL4 email with custom domains
        valueMap.putSingle("email", "test22@ss.usafa.edu");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap, configModel);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertNull(errorEvent[0]);
        Assert.assertEquals(0, errors.size());

        //test invalid IL4 email with custom domains
        valueMap.putSingle("email", "test22@mil");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap, configModel);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(errorEvent[0], Errors.INVALID_REGISTRATION);
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(errors.get(0).getField(), "email");
    }


}
