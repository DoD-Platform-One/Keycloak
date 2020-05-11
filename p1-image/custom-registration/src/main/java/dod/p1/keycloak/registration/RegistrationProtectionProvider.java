package dod.p1.keycloak.registration;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

public class RegistrationProtectionProvider implements RealmResourceProvider {

    private KeycloakSession session;

    public RegistrationProtectionProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new RegistrationProtection(session);
    }

    @Override
    public void close() {
    }

}
