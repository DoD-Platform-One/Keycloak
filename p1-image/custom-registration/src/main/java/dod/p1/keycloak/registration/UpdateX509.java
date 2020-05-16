package dod.p1.keycloak.registration;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.authentication.authenticators.x509.AbstractX509ClientCertificateAuthenticator;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.authentication.authenticators.x509.X509ClientCertificateAuthenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserModel;
import org.keycloak.services.x509.X509ClientCertificateLookup;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

public class UpdateX509 implements RequiredActionProvider, RequiredActionFactory {

    private static final Logger logger = Logger.getLogger(UpdateX509.class);
    private static final String PROVIDER_ID = "UPDATE_X509";
    private static final String IGNORE_X509 = "IGNORE_X509";

    /**
     * Called every time a user authenticates.  This checks to see if this required action should be triggered.
     * The implementation of this method is responsible for setting the required action on the UserModel.
     *
     * @param context
     */
    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        String ignore = context.getAuthenticationSession().getAuthNote(IGNORE_X509);
        if (ignore != null && ignore.equals("true")) {
            return;
        }

        Object identity = getX509Identity(context);
        if (identity != null) {
            String attribute = getX509Attribute(context);
            String id = identity.toString();
            String userAttribute = context.getUser().getFirstAttribute(attribute);

            if (!id.isEmpty() && !id.equals(userAttribute)) {
                List<UserModel> users = context.getSession().users().searchForUserByUserAttribute(attribute, id, context.getRealm());
                if (users.size() >= 1) {
                    logger.error("X509 already bound to an existing account: " + id);
                    return;
                }
                context.getUser().addRequiredAction(PROVIDER_ID);
            }
        }
    }

    /**
     * If the user has a required action set, this method will be the initial call to obtain what to display to the
     * user's browser.
     *
     * @param context
     */
    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
        formData.add("username", context.getUser() != null ? context.getUser().getUsername() : "unknown user");
        formData.add("subjectDN", getX509Identity(context).toString());
        formData.add("isUserEnabled", "true");
        context.form().setFormData(formData);

        Response challenge = context.form().createX509ConfirmPage();
        context.challenge(challenge);
    }

    /**
     * Called when a required action has form input you want to process.
     *
     * @param context
     */
    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.getAuthenticationSession().setAuthNote(IGNORE_X509, "true");
            context.success();
            return;
        }

        String attribute = getX509Attribute(context);
        Object identity = getX509Identity(context);
        if (attribute != null && identity != null) {
            context.getUser().setSingleAttribute(attribute, identity.toString());
        }
        context.success();
    }

    /**
     * Display text used in admin console to reference this required action
     *
     * @return
     */
    @Override
    public String getDisplayText() {
        return "Update X509";
    }

    /**
     * Flag indicating whether the execution of the required action by the same circumstances
     * (e.g. by one and the same action token) should only be permitted once.
     *
     * @return
     */
    @Override
    public boolean isOneTimeAction() {
        return true;
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    /**
     * Only called once when the factory is first created.  This config is pulled from keycloak_server.json
     *
     * @param config
     */
    @Override
    public void init(Config.Scope config) {
    }

    /**
     * Called after all provider factories have been initialized
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    /**
     * This is called when the server shuts down.
     */
    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Returns the Custom Attribute from the X509 Authenticator for mapping user
     * identities.
     *
     * @param context
     * @return custom attribute name
     */
    private String getX509Attribute(RequiredActionContext context) {
        if (context.getRealm().getAuthenticatorConfigs() != null) {
            for (AuthenticatorConfigModel config : context.getRealm().getAuthenticatorConfigs()) {
                X509ClientCertificateAuthenticator authenticator = new X509ClientCertificateAuthenticator();
                if (config.getConfig().containsKey(AbstractX509ClientCertificateAuthenticator.CUSTOM_ATTRIBUTE_NAME)) {
                    return config.getConfig().get(AbstractX509ClientCertificateAuthenticator.CUSTOM_ATTRIBUTE_NAME);
                }
            }
        }
        return null;
    }

    /**
     * Returns the user from the X509 identity extractor.
     *
     * @param context
     * @return user identity object
     */
    private Object getX509Identity(RequiredActionContext context) {
        try {
            X509ClientCertificateLookup provider = context.getSession().getProvider(X509ClientCertificateLookup.class);
            if (provider == null) {
                return null;
            }

            X509Certificate[] certs = provider.getCertificateChain(context.getHttpRequest());
            if (certs == null || certs.length == 0) {
                return null;
            }
            context.getHttpRequest().getHttpHeaders().getRequestHeader("x-forwarded-client-cert").get(0);

            if (context.getRealm().getAuthenticatorConfigs() != null) {
                for (AuthenticatorConfigModel config : context.getRealm().getAuthenticatorConfigs()) {
                    X509ClientCertificateAuthenticator authenticator = new X509ClientCertificateAuthenticator();
                    if (config.getConfig().containsKey(AbstractX509ClientCertificateAuthenticator.CUSTOM_ATTRIBUTE_NAME)) {
                        X509AuthenticatorConfigModel model = new X509AuthenticatorConfigModel(config);
                        return authenticator.getUserIdentityExtractor(model).extractUserIdentity(certs);
                    }
                }
            }
        } catch (GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
