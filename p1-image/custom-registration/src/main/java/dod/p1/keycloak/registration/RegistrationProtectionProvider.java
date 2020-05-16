package dod.p1.keycloak.registration;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.resource.RealmResourceProvider;

public class RegistrationProtectionProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    private final AppAuthManager appAuthManager;

    public RegistrationProtectionProvider(KeycloakSession session, AppAuthManager appAuthManager) {
        this.session = session;
        this.appAuthManager = appAuthManager;
    }

    public RegistrationProtectionProvider(KeycloakSession session) {
        this.session = session;
        this.appAuthManager = new AppAuthManager();
    }

    @Override
    public Object getResource() {
        return new RegistrationProtection(session, appAuthManager);
    }

    @Override
    public void close() {
    }
}
