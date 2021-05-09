package dod.p1.keycloak.registration;

import static dod.p1.keycloak.common.CommonConfig.getInstance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.authenticators.x509.AbstractX509ClientCertificateAuthenticator;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.authentication.authenticators.x509.X509ClientCertificateAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.x509.X509ClientCertificateLookup;
import org.keycloak.sessions.AuthenticationSessionModel;

import dod.p1.keycloak.common.CommonConfig;

public class X509Tools {

    private static final Logger logger = LogManager.getLogger(X509Tools.class);
    private static final String CERTIFICATE_POLICY_OID = "2.5.29.32";

    private static String getLogPrefix(AuthenticationSessionModel authenticationSession, String suffix) {
        return "P1_X509_TOOLS_" + suffix + "_" + authenticationSession.getParentSession().getId();
    }

    private static boolean isX509Registered(KeycloakSession session, HttpRequest httpRequest, RealmModel realm) {
        String logPrefix = getLogPrefix(session.getContext().getAuthenticationSession(), "IS_X509_REGISTERED");
        String username = getX509Username(session, httpRequest, realm);
        logger.info(logPrefix + " X509 ID: " + username);
        if (username != null) {
            Stream<UserModel> users = session.users().searchForUserByUserAttributeStream(realm,
                    CommonConfig.getInstance(realm).getUserIdentityAttribute(), username);
            return users != null && users.count() > 0;
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

    public static String getCertificatePolicyId(X509Certificate cert, int certificatePolicyPos, int policyIdentifierPos)
            throws IOException {
        byte[] extPolicyBytes = cert.getExtensionValue(CERTIFICATE_POLICY_OID);
        if (extPolicyBytes == null) {
            return null;
        }

        DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(extPolicyBytes))
                .readObject());
        ASN1Sequence seq = (ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject();

        if (seq.size() <= (certificatePolicyPos)) {
            return null;
        }

        CertificatePolicies certificatePolicies = new CertificatePolicies(
                PolicyInformation.getInstance(seq.getObjectAt(certificatePolicyPos)));
        if (certificatePolicies.getPolicyInformation().length <= policyIdentifierPos) {
            return null;
        }

        PolicyInformation[] policyInformation = certificatePolicies.getPolicyInformation();
        return policyInformation[policyIdentifierPos].getPolicyIdentifier().getId();
    }

    public static Object getX509IdentityFromCertChain(X509Certificate[] certs, RealmModel realm,
            AuthenticationSessionModel authenticationSession) {

        String logPrefix = getLogPrefix(authenticationSession, "GET_X509_IDENTITY_FROM_CHAIN");

        if (certs == null || certs.length == 0) {
            logger.info(logPrefix + " no valid certs found");
            return null;
        }

        boolean hasValidPolicy = false;

        int index = 0;
        // Only check up to 10 cert policies, DoD only uses 1-2 policies
        while (!hasValidPolicy && index < 10) {
            try {
                String certificatePolicyId = getCertificatePolicyId(certs[0], index, 0);
                if (certificatePolicyId == null) {
                    break;
                }
                logger.info(logPrefix + " checking cert policy " + certificatePolicyId);
                hasValidPolicy = getInstance(realm).getRequiredCertificatePolicies()
                        .anyMatch(s -> s.equals(certificatePolicyId));
                index++;
            } catch (Exception ignored) {
                logger.warn(logPrefix + " error parsing cert policies");
                // abort checks
                index = 20;
            }
        }

        if (!hasValidPolicy) {
            logger.warn(logPrefix + " no valid cert policies found");
            return null;
        }

        if (realm.getAuthenticatorConfigsStream().count() > 0) {
            return realm.getAuthenticatorConfigsStream().filter(config -> {
                return config.getConfig().containsKey(AbstractX509ClientCertificateAuthenticator.CUSTOM_ATTRIBUTE_NAME);
            }).map(config -> {
                X509ClientCertificateAuthenticator authenticator = new X509ClientCertificateAuthenticator();
                X509AuthenticatorConfigModel model = new X509AuthenticatorConfigModel(config);
                return authenticator.getUserIdentityExtractor(model).extractUserIdentity(certs);
            }).findFirst().orElse(null);
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

            AuthenticationSessionModel authenticationSession = session.getContext().getAuthenticationSession();

            return getX509IdentityFromCertChain(certs, realm, authenticationSession);
        } catch (GeneralSecurityException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
