package dod.p1.keycloak.registration;

import static dod.p1.keycloak.common.CommonConfig.getInstance;
import static dod.p1.keycloak.registration.X509Tools.getX509IdentityFromCertChain;
import static dod.p1.keycloak.registration.X509Tools.getX509Username;
import static dod.p1.keycloak.registration.X509Tools.isX509Registered;
import static org.keycloak.services.x509.DefaultClientCertificateLookup.JAVAX_SERVLET_REQUEST_X509_CERTIFICATE;

import java.security.cert.X509Certificate;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

public class UpdateX509 implements RequiredActionProvider, RequiredActionFactory {

    private static final String PROVIDER_ID = "UPDATE_X509";
    private static final String IGNORE_X509 = "IGNORE_X509";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        String ignore = context.getAuthenticationSession().getAuthNote(IGNORE_X509);
        String x509Username = getX509Username(context);
        if (x509Username == null || ignore != null && ignore.equals("true")) {
            return;
        }

        RealmModel realm = context.getRealm();
        AuthenticationSessionModel authenticationSession = context.getAuthenticationSession();

        X509Certificate[] certAttribute = (X509Certificate[]) context.getHttpRequest()
                .getAttribute(JAVAX_SERVLET_REQUEST_X509_CERTIFICATE);
        String identity = (String) getX509IdentityFromCertChain(certAttribute, realm, authenticationSession);
        context.getUser().setSingleAttribute(getInstance(realm).getUserActive509Attribute(), identity);

        if (!isX509Registered(context)) {
            context.getUser().addRequiredAction(PROVIDER_ID);
        }

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("username", context.getUser() != null ? context.getUser().getUsername() : "unknown user");
        formData.add("subjectDN", getX509Username(context));
        formData.add("isUserEnabled", "true");
        context.form().setFormData(formData);

        Response challenge = context.form().createX509ConfirmPage();
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.getAuthenticationSession().setAuthNote(IGNORE_X509, "true");
            context.success();
            return;
        }

        String username = getX509Username(context);
        RealmModel realm = context.getRealm();
        if (username != null) {
            UserModel user = context.getUser();
            user.setSingleAttribute(getInstance(realm).getUserIdentityAttribute(), username);
            getInstance(realm).getAutoJoinGroupX509().forEach(user::joinGroup);
        }
        context.success();
    }

    @Override
    public String getDisplayText() {
        return "Update X509";
    }

    @Override
    public boolean isOneTimeAction() {
        return true;
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
