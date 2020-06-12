package dod.p1.keycloak.authentication;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dod.p1.keycloak.common.CommonConfig.VALID_BUILTIN_CLIENTS;

/**
 * Simple {@link Authenticator} that checks of a user is member of a given {@link GroupModel Group}.
 */
public class RequireGroupAuthenticator implements Authenticator {

    private final String clientIdPatternMatch = "^[a-z0-9-]+_([0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})_[a-z0-9-]+$";

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();

        AuthenticationSessionModel authenticationSession = context.getAuthenticationSession();
        ClientModel client = authenticationSession.getClient();
        String clientId = client.getClientId();

        // Match the pattern "test_b4e4ae70-5b78-47ff-ad5c-7ebf3c10e452_app"
        // where "test" is the short name and "b4e4ae70-5b78-47ff-ad5c-7ebf3c10e452" is the group id
        Pattern pattern = Pattern.compile(clientIdPatternMatch);
        Matcher matcher = pattern.matcher(clientId);

        // Check for a valid match
        if (matcher.find() && matcher.groupCount() == 1) {
            String groupId = matcher.group(1);

            GroupModel group = null;

            if (realm != null) {
                group = realm.getGroupById(groupId);
            }

            // Must be an valid environment name
            if (groupId == null || group == null) {
                context.failure(AuthenticationFlowError.CLIENT_DISABLED);
            } else {
                // Check if the user is a member of the specified group
                if (isMemberOfGroup(realm, user, group)) {
                    context.success();
                } else {
                    context.failure(AuthenticationFlowError.INVALID_CLIENT_SESSION);
                }
            }
        } else {
            if (VALID_BUILTIN_CLIENTS.contains(clientId)) {
                context.success();
            } else {
                context.failure(AuthenticationFlowError.CLIENT_DISABLED);
            }
        }

    }

    private boolean isMemberOfGroup(RealmModel realm, UserModel user, GroupModel group) {
        // No on likes null pointers
        if (realm == null || user == null || group == null) {
            return false;
        }

        return user.isMemberOf(group);
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
    }


    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
    }

    @Override
    public void close() {
    }
}
