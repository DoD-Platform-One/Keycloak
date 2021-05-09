package dod.p1.keycloak.authentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import dod.p1.keycloak.common.CommonConfig;

/**
 * Simple {@link Authenticator} that checks of a user is member of a given {@link GroupModel Group}.
 */
public class RequireGroupAuthenticator implements Authenticator {

    private static final Logger logger = LogManager.getLogger(RequireGroupAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();

        AuthenticationSessionModel authenticationSession = context.getAuthenticationSession();
        ClientModel client = authenticationSession.getClient();
        String clientId = client.getClientId();
        String logPrefix = "P1_GROUP_PROTECTION_AUTHENTICATE_" + authenticationSession.getParentSession().getId();

        if (user != null) {
            logger.info(logPrefix + " user " + user.getId() + " / " + user.getUsername());
        } else {
            logger.warn(logPrefix + " invalid user");
        }
        logger.info(logPrefix + " client " + clientId + " / " + client.getName());

        // Match the pattern "test_b4e4ae70-5b78-47ff-ad5c-7ebf3c10e452_app"
        // where "test" is the short name and "b4e4ae70-5b78-47ff-ad5c-7ebf3c10e452" is the group id
        String clientIdPatternMatch = "^[a-z0-9-]+_([0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})_[a-z0-9-]+$";
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
                logger.warn(logPrefix + " invalid group {}" + groupId);
                context.failure(AuthenticationFlowError.CLIENT_DISABLED);
            } else {
                // Check if the user is a member of the specified group
                if (isMemberOfGroup(realm, user, group, logPrefix)) {
                    logger.info(logPrefix + " matched authorized group");
                    success(context, user);
                } else {
                    logger.warn(logPrefix + " failed authorized group match");
                    context.failure(AuthenticationFlowError.INVALID_CLIENT_SESSION);
                }
            }
        } else {
            if (CommonConfig.getInstance(realm).getIgnoredGroupProtectionClients().contains(clientId)) {
                logger.info(logPrefix + " matched authorized ignored group protect client");
                success(context, user);
            } else {
                logger.warn(logPrefix + " failed ignored group protect client test");
                context.failure(AuthenticationFlowError.CLIENT_DISABLED);
            }
        }

    }

    private void success(AuthenticationFlowContext context, UserModel user) {
        RealmModel realm = context.getRealm();
        // Reset X509 attribute per login event
        user.setSingleAttribute(CommonConfig.getInstance(realm).getUserActive509Attribute(), "");
        user.addRequiredAction("terms_and_conditions");
        context.success();
    }

    private boolean isMemberOfGroup(RealmModel realm, UserModel user, GroupModel group, String logPrefix) {
        // No on likes null pointers
        if (realm == null || user == null || group == null) {
            logger.warn(logPrefix + " realm, group or user null");
            return false;
        }

        String groupList = user.getGroupsStream()
                .map(GroupModel::getId)
                .collect(Collectors.joining(","));

        logger.info(logPrefix + " user groups {} " + groupList);

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
