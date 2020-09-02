package dod.p1.keycloak.registration;

import dod.p1.keycloak.common.CommonConfig;
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
import org.keycloak.common.util.OCSPUtils;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.x509.X509ClientCertificateLookup;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Stream;

import static dod.p1.keycloak.common.CommonConfig.getInstance;
import static sun.security.provider.certpath.OCSP.getResponderURI;

public class X509Tools {

    private static final String CERTIFICATE_POLICY_OID = "2.5.29.32";

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

    public static OCSPUtils.OCSPRevocationStatus check(X509Certificate cert,
                                                       X509Certificate issuerCert)
            throws IOException, CertPathValidatorException, CertificateException {
        URI responderURI = null;

        X509CertImpl certImpl = X509CertImpl.toImpl(cert);
        responderURI = getResponderURI(certImpl);
        if (responderURI == null) {
            throw new CertPathValidatorException
                    ("No OCSP Responder URI in certificate");
        }
        return OCSPUtils.check(cert, issuerCert, responderURI, cert, null);
    }


    public static String getCertificatePolicyId(X509Certificate cert, int certificatePolicyPos, int policyIdentifierPos)
            throws IOException {
        byte[] extPolicyBytes = cert.getExtensionValue(CERTIFICATE_POLICY_OID);
        if (extPolicyBytes == null) {
            return null;
        }

        DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(extPolicyBytes)).readObject());
        ASN1Sequence seq = (ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject();

        if (seq.size() <= (certificatePolicyPos)) {
            return null;
        }

        CertificatePolicies certificatePolicies = new CertificatePolicies(PolicyInformation.getInstance(seq.getObjectAt(certificatePolicyPos)));
        if (certificatePolicies.getPolicyInformation().length <= policyIdentifierPos) {
            return null;
        }

        PolicyInformation[] policyInformation = certificatePolicies.getPolicyInformation();
        return policyInformation[policyIdentifierPos].getPolicyIdentifier().getId();
    }

    public static Object getX509IdentityFromCertChain(X509Certificate[] certs, RealmModel realm) {
        if (certs == null || certs.length == 0) {
            return null;
        }

        boolean hasValidPolicy = false;
        Stream<String> requiredCertificatePolicies = getInstance(realm).getRequiredCertificatePolicies();

        int index = 0;
        // Only check up to 10 cert policies, DoD only uses 1-2 policies
        while (!hasValidPolicy && index < 10) {
            try {
                String certificatePolicyId = getCertificatePolicyId(certs[0], index, 0);
                hasValidPolicy = requiredCertificatePolicies.anyMatch(s -> s.equals(certificatePolicyId));
                index++;
            } catch (IOException ignored) {
                // abort checks
                index = 20;
            }
        }

        if (!hasValidPolicy) {
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
