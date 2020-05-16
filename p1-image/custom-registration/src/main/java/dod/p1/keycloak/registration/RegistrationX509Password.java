package dod.p1.keycloak.registration;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.authenticators.x509.AbstractX509ClientCertificateAuthenticator;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.authentication.authenticators.x509.X509ClientCertificateAuthenticator;
import org.keycloak.authentication.forms.RegistrationPassword;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.x509.X509ClientCertificateLookup;

import javax.ws.rs.core.MultivaluedMap;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class RegistrationX509Password extends RegistrationPassword {
    public static final String PROVIDER_ID = "registration-x509-password-action";
    private static final Logger logger = Logger.getLogger(RegistrationX509Password.class);
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public String getHelpText() {
        return "Disables password registration if x509 authentication is possible.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void validate(ValidationContext context) {

        if (!isX509Registration(context)) {
            super.validate(context);
            return;
        }

        if (isX509Exists(context)) {
            MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
            List<FormMessage> errors = new ArrayList<>();
            errors.add(new FormMessage(null, "Existing X509 account found."));
            context.error(Errors.INVALID_REGISTRATION);
            context.validationError(formData, errors);
            return;
        }

        context.success();
    }

    @Override
    public void success(FormContext context) {
        if (!isX509Registration(context)) {
            super.success(context);
            return;
        }

        String attribute = getX509Attribute(context);
        Object identity = getX509Identity(context);
        if (attribute != null && identity != null) {
            context.getUser().setSingleAttribute(attribute, identity.toString());
        }
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        if (!isX509Registration(context)) {
            form.setAttribute("passwordRequired", true);
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getDisplayType() {
        return "X509 Password Validation";
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Returns whether or not an X509 certificate is already bound to an
     * existing user account.
     *
     * @param context
     * @return true if x509 already bound
     */
    private boolean isX509Exists(FormContext context) {
        String attribute = getX509Attribute(context);
        Object identity = getX509Identity(context);
        if (attribute != null && identity != null) {
            List<UserModel> users = context.getSession().users().searchForUserByUserAttribute(attribute, identity.toString(), context.getRealm());
            return users != null && users.size() >= 1;
        }
        return false;
    }

    /**
     * Returns whether or not a valid X509 certificate was detected.
     *
     * @param context
     * @return true for valid certificates
     */
    private boolean isX509Registration(FormContext context) {
        Object identity = getX509Identity(context);
        return identity != null && !identity.toString().isEmpty();
    }

    /**
     * Returns the Custom Attribute from the X509 Authenticator for mapping user
     * identities.
     *
     * @param context
     * @return custom attribute name
     */
    private String getX509Attribute(FormContext context) {
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
    private Object getX509Identity(FormContext context) {
        try {
            X509ClientCertificateLookup provider = context.getSession().getProvider(X509ClientCertificateLookup.class);
            if (provider == null) {
                return null;
            }

            X509Certificate[] certs = provider.getCertificateChain(context.getHttpRequest());
            if (certs == null || certs.length == 0) {
                return null;
            }

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
