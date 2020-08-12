package dod.p1.keycloak.registration;

import dod.p1.keycloak.common.CommonConfig;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.authenticators.x509.AbstractX509ClientCertificateAuthenticator;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.authentication.authenticators.x509.X509ClientCertificateAuthenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.x509.X509ClientCertificateLookup;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

public class X509Tools {

    private static boolean isX509Registered(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {
        String username = getX509Username(session, httpRequest, realm);
        if (username != null) {
            List<UserModel> users = session.users().searchForUserByUserAttribute(CommonConfig.getInstance(realm).getUserIdentityAttribute(), username, realm);
            return users != null && users.size() > 0;
        }
        return false;
    }

    public static boolean isX509Registered(FormContext context) {
        return isX509Registered(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    public static boolean isX509Registered(RequiredActionContext context) {
        return isX509Registered(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    private static String getX509Username(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {
        Object identity = getX509Identity(session, httpRequest, realm);
        if (identity != null && !identity.toString().isEmpty()) {
            return identity.toString();
        }
        return null;
    }

    public static String getX509Username(FormContext context) {
        return getX509Username(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    public static String getX509Username(RequiredActionContext context) {
        return getX509Username(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    public static Object getX509IdentityFromCertChain(X509Certificate[] certs, RealmModel realm) {
        if (certs == null || certs.length == 0) {
            return null;
        }

        if (realm.getAuthenticatorConfigs() != null) {
            for (AuthenticatorConfigModel config : realm.getAuthenticatorConfigs()) {
                X509ClientCertificateAuthenticator authenticator = new X509ClientCertificateAuthenticator();
                if (config.getConfig().containsKey(AbstractX509ClientCertificateAuthenticator.CUSTOM_ATTRIBUTE_NAME)) {
                    X509AuthenticatorConfigModel model = new X509AuthenticatorConfigModel(config);
                    return authenticator.getUserIdentityExtractor(model).extractUserIdentity(certs);
                }
            }
        }

        return null;
    }

    private static Object getX509Identity(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {

        try {
            if (session == null || httpRequest == null || realm == null) {
                return null;
            }

            X509ClientCertificateLookup provider = session.getProvider(X509ClientCertificateLookup.class);
            if (provider == null) {
                return null;
            }

            X509Certificate[] certs = provider.getCertificateChain(httpRequest);

            return getX509IdentityFromCertChain(certs, realm);
        } catch (GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
