package dod.p1.keycloak.registration;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RegistrationProtection {

    private final AuthenticationManager.AuthResult auth;
    private final KeycloakSession session;
    private final AppAuthManager appAuthManager;

    public RegistrationProtection(KeycloakSession session, AppAuthManager authManager) {
        this.session = session;
        this.appAuthManager = authManager;
        this.auth = resolveAuthentication(session);
    }

    // Used by RegistrationProtectionTest::testValidInviteCode
    public RegistrationProtection(KeycloakSession session) {
        this(session, new AppAuthManager());
    }

    private AuthenticationManager.AuthResult resolveAuthentication(KeycloakSession keycloakSession) {
        RealmModel realm = keycloakSession.getContext().getRealm();
        return appAuthManager.authenticateIdentityCookie(keycloakSession, realm);
    }

    @GET
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public InviteCode get() {

        if (auth == null) {
            Response.status(Response.Status.BAD_REQUEST).build();
            return new InviteCode();
        }

        AuthenticatorConfigModel authConfig = session.getContext().getRealm().getAuthenticatorConfigs()
                .stream()
                .filter(config -> config.getConfig().get("inviteSecret") != null)
                .findFirst()
                .orElse(null);

        if (authConfig == null) {
            Response.status(Response.Status.BAD_REQUEST).build();
            return new InviteCode();
        }

        InviteCode code = new InviteCode();

        String inviteSecret = authConfig.getConfig().get("inviteSecret");
        String inviteDigest = RegistrationValidation.getInviteDigest(0, inviteSecret);
        String invitedUrlEncoded = URLEncoder.encode(inviteDigest, StandardCharsets.UTF_8);

        code.success = true;
        code.days = Integer.parseInt(authConfig.getConfig().get("inviteSecretDays"));
        code.link = "/register?invite=" + invitedUrlEncoded;

        return code;
    }

    public static class InviteCode {
        public Boolean success = false;
        public Integer days;
        public String link = "";
    }

}
