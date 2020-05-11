package dod.p1.keycloak.registration;

import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.authentication.forms.RegistrationProfile;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RegistrationValidation extends RegistrationProfile implements FormAction {

    public static final String PROVIDER_ID = "registration-validation-action";

    @Override
    public String getDisplayType() {
        return "Platform One Registration Validation";
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Restrict user registration to specific top-level-domains.  Important: be user the use the format \".mil\"";
    }

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();

    private static HashMap<String, String> INVITE_CACHE = new HashMap<>();

    static {
        // Add the domain list configuration
        ProviderConfigProperty domainProperty;
        domainProperty = new ProviderConfigProperty();
        domainProperty.setName("validDomains");
        domainProperty.setLabel("Authorized Email Domains");
        domainProperty.setType(ProviderConfigProperty.MULTIVALUED_STRING_TYPE);
        domainProperty.setDefaultValue(".mil");
        domainProperty.setHelpText("List email domains authorized to register");
        CONFIG_PROPERTIES.add(domainProperty);

        // Add the invited secret configuration
        ProviderConfigProperty inviteProperty;
        inviteProperty = new ProviderConfigProperty();
        inviteProperty.setName("inviteSecret");
        inviteProperty.setLabel("Invite Secret");
        inviteProperty.setType(ProviderConfigProperty.PASSWORD);
        inviteProperty.setDefaultValue("replace_me_C1k0cFGcXztrClL3qc6ARSdImz8ZhDNjTUhr4dxEKO3ZbYCJ0MshBHGnOb1mRgC");
        inviteProperty.setHelpText("This is the secret used to generate invite links, use a strong generator such as openssl for this.");
        CONFIG_PROPERTIES.add(inviteProperty);

        // Add invite secret maximum days limit
        ProviderConfigProperty inviteDaysProperty;
        inviteDaysProperty = new ProviderConfigProperty();
        inviteDaysProperty.setName("inviteSecretDays");
        inviteDaysProperty.setLabel("Invite link days");
        inviteDaysProperty.setHelpText("The number of days invite links are valid for");
        inviteDaysProperty.setType(ProviderConfigProperty.LIST_TYPE);
        inviteDaysProperty.setDefaultValue("5");
        List<String> daysOptions = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        inviteDaysProperty.setOptions(daysOptions);
        CONFIG_PROPERTIES.add(inviteDaysProperty);
    }

    static String getInviteDigest(int dayLookBack, String inviteSecret) {
        Instant instant = Instant.now();
        Instant instantOffset = instant.minus(dayLookBack, ChronoUnit.DAYS);
        Date day = Date.from(instantOffset);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyD");
        String dayKey = formatDate.format(day);

        if (!INVITE_CACHE.containsKey(dayKey)) {
            MessageDigest digest = null;

            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            }

            String combinedSecret = dayKey + ":" + inviteSecret;
            byte[] hash = digest.digest(combinedSecret.getBytes(StandardCharsets.UTF_8));
            String computedInvite = Base64.getEncoder().encodeToString(hash);
            INVITE_CACHE.put(dayKey, computedInvite);
        }

        return INVITE_CACHE.get(dayKey);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public void validate(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        AuthenticatorConfigModel authenticatorConfig = context.getAuthenticatorConfig();

        List<FormMessage> errors = new ArrayList<>();
        String inviteCode = formData.getFirst("invite");
        String email = formData.getFirst(Validation.FIELD_EMAIL);

        formData.forEach((key, val) -> {
            System.out.println(key + ": " + val);
        });

        String eventError = Errors.INVALID_REGISTRATION;

        if (Validation.isBlank(formData.getFirst("firstName"))) {
            errors.add(new FormMessage("firstName", Messages.MISSING_FIRST_NAME));
        }

        if (Validation.isBlank(formData.getFirst("lastName"))) {
            errors.add(new FormMessage("lastName", Messages.MISSING_LAST_NAME));
        }


        if (!isValidInviteCode(authenticatorConfig, inviteCode)) {
            context.getEvent().detail("invite", inviteCode);
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, "Invalid or expired registration code."));
        }

        if (!isValidEmailAddress(authenticatorConfig, email)) {
            context.getEvent().detail(Details.EMAIL, email);
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, "Please check your email address, it seems to not be an approved domain."));
        }

        if (context.getSession().users().getUserByEmail(email, context.getRealm()) != null) {
            eventError = Errors.EMAIL_IN_USE;
            formData.remove("email");
            context.getEvent().detail("email", email);
            errors.add(new FormMessage("email", Messages.EMAIL_EXISTS));
        }

        if (errors.size() > 0) {
            context.error(eventError);
            context.validationError(formData, errors);
        } else {
            handleSuccess(context, formData);
        }

    }

    private void handleSuccess(ValidationContext context, MultivaluedMap<String, String> formData) {
        String email = formData.getFirst(Validation.FIELD_EMAIL);
        String generatedUniqueId = generateUniqueId(email);

        formData.add("user.attributes.mattermostid", generatedUniqueId);
        context.success();
    }

    private String generateUniqueId(String email) {
        byte[] encodedEmail;
        int emailByteTotal = 0;
        Date today = new Date();

        encodedEmail = email.getBytes(StandardCharsets.US_ASCII);
        for (byte b : encodedEmail) {
            System.out.println(b);
            emailByteTotal += b;
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("yyDHmsS");

        return formatDate.format(today) + emailByteTotal;
    }

    private boolean isValidInviteCode(AuthenticatorConfigModel authenticatorConfig, String inviteCode) {
        // No code provided, abort
        if (inviteCode == null) {
            return false;
        }

        // Load the configured secret
        String inviteSecret = authenticatorConfig.getConfig().get("inviteSecret");
        int inviteSecretDays = Integer.parseInt(authenticatorConfig.getConfig().get("inviteSecretDays")) + 1;

        for (int dayOffset = 0; dayOffset < inviteSecretDays; dayOffset++) {
            String inviteDigest = getInviteDigest(dayOffset, inviteSecret);
            if (inviteCode.equals(inviteDigest)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidEmailAddress(AuthenticatorConfigModel authenticatorConfig, String email) {
        String[] domains = authenticatorConfig.getConfig().getOrDefault("validDomains", ".mil").split("##");

        if (Validation.isBlank(email) || !Validation.isEmailValid(email)) {
            return false;
        }

        for (String domain : domains) {
            if (email.endsWith(domain)) {
                return true;
            }
        }

        return false;
    }

}