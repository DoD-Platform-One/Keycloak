package dod.p1.keycloak.registration;

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

import static dod.p1.keycloak.common.CommonConfig.X509_USER_ATTRIBUTE;

public class X509Tools {

    private static boolean isCACRegistered(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {
        String username = getCACUsername(session, httpRequest, realm);
        if (username != null) {
            List<UserModel> users = session.users().searchForUserByUserAttribute(X509_USER_ATTRIBUTE, username, realm);
            return users != null && users.size() > 0;
        }
        return false;
    }

    public static boolean isCACRegistered(FormContext context) {
        return isCACRegistered(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    public static boolean isCACRegistered(RequiredActionContext context) {
        return isCACRegistered(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    private static String getCACUsername(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {
        Object identity = getX509Identity(session, httpRequest, realm);
        if (identity != null && !identity.toString().isEmpty()) {
            return identity.toString();
        }
        return null;
    }

    public static String getCACUsername(FormContext context) {
        return getCACUsername(context.getSession(), context.getHttpRequest(), context.getRealm());
    }

    public static String getCACUsername(RequiredActionContext context) {
        return getCACUsername(context.getSession(), context.getHttpRequest(), context.getRealm());
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
        } catch (GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
