package dod.p1.keycloak.authentication;

import dod.p1.keycloak.common.CommonConfig;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dod.p1.keycloak.common.CommonConfig.VALID_BUILTIN_CLIENTS;
import static dod.p1.keycloak.common.CommonConfig.VALID_ENV_NAMES;

/**
 * Simple {@link Authenticator} that checks of a user is member of a given {@link GroupModel Group}.
 */
public class RequireGroupAuthenticator implements Authenticator {

    private final String clientIdPatternMatch = "^group-protect-(\\w+)-[\\w-]+$";

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();

        AuthenticationSessionModel authenticationSession = context.getAuthenticationSession();
        ClientModel client = authenticationSession.getClient();
        String clientId = client.getClientId();

        // Match the pattern "group-protect-il2-mattermost" where "il2" is the environment name
        Pattern pattern = Pattern.compile(clientIdPatternMatch);
        Matcher matcher = pattern.matcher(clientId);

        // Check for a valid match
        if (matcher.find() && matcher.groupCount() == 1) {
            String environmentRestriction = matcher.group(1);

            // Must be an valid environment name
            if (!VALID_ENV_NAMES.contains(environmentRestriction)) {
                context.failure(AuthenticationFlowError.CLIENT_DISABLED);
            } else {
                String groupId = CommonConfig.getGroupIdByEnvironment(environmentRestriction);

                // Check if the user is a member of the specified group
                if (isMemberOfGroup(realm, user, groupId)) {
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

    private boolean isMemberOfGroup(RealmModel realm, UserModel user, String groupId) {
        // No on likes null pointers
        if (realm == null || user == null || groupId == null) {
            return false;
        }

        GroupModel group = realm.getGroupById(groupId);
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
