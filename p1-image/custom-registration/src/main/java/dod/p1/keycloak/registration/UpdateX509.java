package dod.p1.keycloak.registration;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.security.cert.X509Certificate;

import static dod.p1.keycloak.common.CommonConfig.*;
import static dod.p1.keycloak.registration.X509Tools.*;
import static org.keycloak.services.x509.DefaultClientCertificateLookup.JAVAX_SERVLET_REQUEST_X509_CERTIFICATE;

public class UpdateX509 implements RequiredActionProvider, RequiredActionFactory {

    private static final String PROVIDER_ID = "UPDATE_X509";
    private static final String IGNORE_X509 = "IGNORE_X509";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        String ignore = context.getAuthenticationSession().getAuthNote(IGNORE_X509);
        String cacUsername = getCACUsername(context);
        if (cacUsername == null || ignore != null && ignore.equals("true")) {
            return;
        }

        X509Certificate[] certAttribute = (X509Certificate[]) context.getHttpRequest().getAttribute(JAVAX_SERVLET_REQUEST_X509_CERTIFICATE);
        String identity = (String) getX509IdentityFromCertChain(certAttribute, context.getRealm());
        context.getUser().setSingleAttribute(ACTIVE_CAC_USER_ATTRIBUTE, identity);

        if (!isCACRegistered(context)) {
            context.getUser().addRequiredAction(PROVIDER_ID);
        }

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("username", context.getUser() != null ? context.getUser().getUsername() : "unknown user");
        formData.add("subjectDN", getCACUsername(context));
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

        String username = getCACUsername(context);
        if (username != null) {
            context.getUser().setSingleAttribute(X509_USER_ATTRIBUTE, username);

            // Add the user to the IL2/4/5 groups
            GroupModel il2Group = context.getRealm().getGroupById(IL2_GROUP_ID);
            GroupModel il4Group = context.getRealm().getGroupById(IL4_GROUP_ID);
            GroupModel il5Group = context.getRealm().getGroupById(IL5_GROUP_ID);

            context.getUser().joinGroup(il2Group);
            context.getUser().joinGroup(il4Group);
            context.getUser().joinGroup(il5Group);
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


