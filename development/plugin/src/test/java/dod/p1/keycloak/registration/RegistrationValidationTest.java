package dod.p1.keycloak.registration;

import static dod.p1.keycloak.utils.Utils.setupFileMocks;
import static dod.p1.keycloak.utils.Utils.setupX509Mocks;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.common.ClientConnection;
import org.keycloak.component.ComponentModel;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.ClientScopeProvider;
import org.keycloak.models.GroupProvider;
import org.keycloak.models.KeyManager;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleProvider;
import org.keycloak.models.ThemeManager;
import org.keycloak.models.TokenManager;
import org.keycloak.models.UserCredentialManager;
import org.keycloak.models.UserLoginFailureProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.UserSessionProvider;
import org.keycloak.models.cache.UserCache;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.Provider;
import org.keycloak.services.clientpolicy.ClientPolicyManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.storage.federated.UserFederatedStorageProvider;
import org.keycloak.vault.VaultTranscriber;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;

import dod.p1.keycloak.common.CommonConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Yaml.class, FileInputStream.class, File.class, CommonConfig.class, X509Tools.class })
public class RegistrationValidationTest {

    @Before
    public void setup() throws Exception {
        setupX509Mocks();
        setupFileMocks();
    }

    public ValidationContext setupVariables(String[] errorEvent, List<FormMessage> errors,
            MultivaluedMap<String, String> multivaluedMap) {
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
                    public RoleProvider roleLocalStorage() {
                        return null;
                    }

                    @Override
                    public GroupProvider groupLocalStorage() {
                        return null;
                    }

                    @Override
                    public GroupProvider groups() {
                        return null;
                    }

                    @Override
                    public GroupProvider groupStorageManager() {
                        return null;
                    }

                    @Override
                    public RoleProvider roleStorageManager() {
                        return null;
                    }

                    @Override
                    public RoleProvider roles() {
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
                    public <T extends Provider> T getComponentProvider(Class<T> clazz, String componentId) {
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
                    public void invalidate(InvalidableObjectType type, Object... ids) {

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
                    public ClientProvider clients() {
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
                        when(userProvider.getUserByEmail(realmModel, "test@ss.usafa.edu"))
                                .thenReturn(mock(UserModel.class));
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
                    public ClientScopeProvider clientScopeLocalStorage() {
                        return null;
                    }

                    @Override
                    public ClientScopeProvider clientScopeStorageManager() {
                        return null;
                    }

                    @Override
                    public ClientScopeProvider clientScopes() {
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

                    @Override
                    public ClientPolicyManager clientPolicy() {
                        return null;
                    }

                    @Override
                    public UserLoginFailureProvider loginFailures() {
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
                return null;
            }

        };
    }

    @Test
    public void testInvalidFields() {
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        ValidationContext context = setupVariables(errorEvent, errors, valueMap);
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
    }

    @Test
    public void testEmailValidation() {
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("user.attributes.location", "42");
        valueMap.putSingle("email", "test@gmail.com");

        ValidationContext context = setupVariables(errorEvent, errors, valueMap);

        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(0, errors.size());

        // test an email address already in use
        valueMap.putSingle("email", "test@ss.usafa.edu");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(Errors.EMAIL_IN_USE, errorEvent[0]);
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(RegistrationPage.FIELD_EMAIL, errors.get(0).getField());

    }

    @Test
    public void testGroupAutoJoinByEmail() {
        String[] errorEvent = new String[1];
        List<FormMessage> errors = new ArrayList<>();
        MultivaluedMapImpl<String, String> valueMap = new MultivaluedMapImpl<>();
        valueMap.putSingle("firstName", "Jone");
        valueMap.putSingle("lastName", "Doe");
        valueMap.putSingle("username", "tester");
        valueMap.putSingle("user.attributes.affiliation", "AF");
        valueMap.putSingle("user.attributes.rank", "E2");
        valueMap.putSingle("user.attributes.organization", "Com");
        valueMap.putSingle("user.attributes.location", "42");
        valueMap.putSingle("email", "test@gmail.com");

        ValidationContext context = setupVariables(errorEvent, errors, valueMap);

        RegistrationValidation validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(0, errors.size());

        // test valid IL2 email with custom domains
        valueMap.putSingle("email", "rando@supercool.unicorns.com");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertNull(errorEvent[0]);
        Assert.assertEquals(0, errors.size());

        // test valid IL4 email with custom domains
        valueMap.putSingle("email", "test22@ss.usafa.edu");
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertNull(errorEvent[0]);
        Assert.assertEquals(0, errors.size());

        // Test existing x509 registration
        errorEvent = new String[1];
        errors = new ArrayList<>();
        context = setupVariables(errorEvent, errors, valueMap);

        PowerMockito.when(X509Tools.isX509Registered(any(FormContext.class))).thenReturn(true);

        validation = new RegistrationValidation();
        validation.validate(context);
        Assert.assertEquals(Errors.INVALID_REGISTRATION, errorEvent[0]);
    }

    @Test
    public void testSuccess() {
    }

    @Test
    public void testBuildPage() {
        RegistrationValidation subject = new RegistrationValidation();
        FormContext context = mock(FormContext.class);
        LoginFormsProvider form = mock(LoginFormsProvider.class);
        subject.buildPage(context, form);

        verify(form).setAttribute("cacIdentity", "thing");
    }

    @Test
    public void testGetDisplayType() {
        RegistrationValidation subject = new RegistrationValidation();
        Assert.assertEquals(subject.getDisplayType(), "Platform One Registration Validation");
    }

    @Test
    public void testGetId() {
        RegistrationValidation subject = new RegistrationValidation();
        Assert.assertEquals(subject.getId(), "registration-validation-action");
    }

    @Test
    public void testIsConfigurable() {
        RegistrationValidation subject = new RegistrationValidation();
        Assert.assertFalse(subject.isConfigurable());
    }

    @Test
    public void testGetRequirementChoices() {
        RegistrationValidation subject = new RegistrationValidation();
        AuthenticationExecutionModel.Requirement[] expected = { AuthenticationExecutionModel.Requirement.REQUIRED };
        Assert.assertEquals(subject.getRequirementChoices(), expected);
    }

}
