package dod.p1.keycloak.registration;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

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
