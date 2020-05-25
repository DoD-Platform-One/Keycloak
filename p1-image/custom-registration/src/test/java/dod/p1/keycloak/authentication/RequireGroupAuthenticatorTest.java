package dod.p1.keycloak.authentication;

import org.junit.Before;
import org.junit.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RequireGroupAuthenticatorTest {

    private RequireGroupAuthenticator subject;
    private AuthenticationFlowContext context;
    private RealmModel realm;
    private UserModel user;
    private GroupModel group;
    private AuthenticationSessionModel authenticationSession;
    private ClientModel client;

    @Before
    public void setup() {
        subject = new RequireGroupAuthenticator();

        context = mock(AuthenticationFlowContext.class);
        realm = mock(RealmModel.class);
        user = mock(UserModel.class);
        group = mock(GroupModel.class);
        authenticationSession = mock(AuthenticationSessionModel.class);
        client = mock(ClientModel.class);

        when(context.getRealm()).thenReturn(realm);
        when(context.getUser()).thenReturn(user);
        when(context.getAuthenticationSession()).thenReturn(authenticationSession);
        when(authenticationSession.getClient()).thenReturn(client);
        when(realm.getGroupById(anyString())).thenReturn(group);
    }

    @Test
    public void testShouldRejectUnknownClients() {
        when(client.getClientId()).thenReturn("random-bad-client");
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.CLIENT_DISABLED);
    }

    @Test
    public void testShouldPermitBuiltinClient() {
        when(client.getClientId()).thenReturn("account");
        subject.authenticate(context);
        verify(context).success();
    }

    @Test
    public void testShouldRejectClientsWithWrongCase() {
        when(client.getClientId()).thenReturn("group-protect-IL2-thingy");
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.CLIENT_DISABLED);
    }

    @Test
    public void testShouldRejectClientsWithUnknownEnvironments() {
        when(client.getClientId()).thenReturn("group-protect-il99-test");
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.CLIENT_DISABLED);
    }

    @Test
    public void testShouldRejectValidClientWithInvalidRealm() {
        when(client.getClientId()).thenReturn("group-protect-il5-test");
        // This user is not authorized
        when(context.getRealm()).thenReturn(null);
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.INVALID_CLIENT_SESSION);
    }

    @Test
    public void testShouldRejectValidClientWithInvalidUser() {
        when(client.getClientId()).thenReturn("group-protect-il4-test");
        // This user is not authorized
        when(context.getUser()).thenReturn(null);
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.INVALID_CLIENT_SESSION);
    }

    @Test
    public void testShouldRejectValidClientWithUserNotInGroup() {
        when(client.getClientId()).thenReturn("group-protect-il2-test");
        // This user is not authorized
        when(user.isMemberOf(any(GroupModel.class))).thenReturn(false);
        subject.authenticate(context);
        verify(context).failure(AuthenticationFlowError.INVALID_CLIENT_SESSION);
    }

    @Test
    public void testShouldAcceptValidClientWithUserInTheGroup() {
        when(client.getClientId()).thenReturn("group-protect-il2-test");
        // This user IS authorized
        when(user.isMemberOf(any(GroupModel.class))).thenReturn(true);
        subject.authenticate(context);
        verify(context).success();
    }

    @Test
    public void testOverrides() {
        subject.action(null);
        subject.setRequiredActions(null, null, null);
        subject.close();
        assertFalse(subject.requiresUser());
        assertTrue(subject.configuredFor(null, null, null));
    }

}
