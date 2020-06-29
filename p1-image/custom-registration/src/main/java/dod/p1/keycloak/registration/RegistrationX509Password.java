package dod.p1.keycloak.registration;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPassword;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

import static dod.p1.keycloak.registration.X509Tools.getCACUsername;

public class RegistrationX509Password extends RegistrationPassword {
    private static final Logger logger = Logger.getLogger(RegistrationX509Password.class);
    public static final String PROVIDER_ID = "registration-x509-password-action";

    @Override
    public String getHelpText() {
        return "Disables password registration if CAC authentication is possible.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void validate(ValidationContext context) {
        if (getCACUsername(context) == null) {
            super.validate(context);
            return;
        }

        context.success();
    }

    @Override
    public void success(FormContext context) {
        if (getCACUsername(context) == null) {
            super.success(context);
        }
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        if (getCACUsername(context) == null) {
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
    public void close() {
    }

    @Override
    public String getDisplayType() {
        return "Platform One X509 Password Validation";
    }

    @Override
    public String getReferenceCategory() {
        return PasswordCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED
    };

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

}
