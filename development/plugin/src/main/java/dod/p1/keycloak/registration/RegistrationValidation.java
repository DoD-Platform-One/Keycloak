package dod.p1.keycloak.registration;

import static dod.p1.keycloak.common.CommonConfig.getInstance;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.authentication.forms.RegistrationProfile;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import dod.p1.keycloak.common.CommonConfig;

public class RegistrationValidation extends RegistrationProfile {

    public static final String PROVIDER_ID = "registration-validation-action";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED };

    private static void bindRequiredActions(UserModel user, String x509Username) {
        // Default actions for all users
        user.addRequiredAction(UserModel.RequiredAction.VERIFY_EMAIL);

        // Make GS-15 Matt and the Cyber Humans happy
        user.addRequiredAction("terms_and_conditions");

        if (x509Username == null) {
            // This user must configure MFA for their login
            user.addRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP);
        }
    }

    private static void processX509UserAttribute(RealmModel realm, UserModel user, String x509Username) {
        if (x509Username != null) {
            // Bind the X509 attribute to the user
            user.setSingleAttribute(getInstance(realm).getUserIdentityAttribute(), x509Username);
        }
    }

    private static void joinValidUserToGroups(FormContext context, UserModel user, String x509Username) {
        String email = user.getEmail().toLowerCase();
        RealmModel realm = context.getRealm();
        CommonConfig config = getInstance(realm);

        long domainMatchCount = config.getEmailMatchAutoJoinGroup()
                .filter(collection -> collection.getDomains().stream().anyMatch(email::endsWith)).count();

        if (x509Username != null) {
            // User is a X509 user - Has a CAC
            config.logger.info(" user " + user.getId() + " / " + user.getUsername() + " found with X509: " + x509Username);
            config.getAutoJoinGroupX509().forEach(user::joinGroup);
        } else {
          if (domainMatchCount != 0) {
            // User is not a X509 user but is in the whitelist

            config.logger.info(" user " + user.getUsername() + " / " + email + ": Email found in whitelist");

            //Below Works but without logging
            /*config.getEmailMatchAutoJoinGroup()
                    .filter(collection -> collection.getDomains().stream().anyMatch(email::endsWith))
                    .forEach(collection -> collection.getGroupModels().forEach(user::joinGroup));
            */
            config.getEmailMatchAutoJoinGroup()
                  .filter(collection -> collection.getDomains().stream().anyMatch(email::endsWith))
                  .forEach(match -> {
                    config.logger.info("Adding user " + user.getUsername() + " to group(s): " + match.getGroups());
                    match.getGroupModels().forEach(group_match -> {
                      user.joinGroup(group_match);
                      });
                  });

        } else {
            // User is not a X509 user or in whitelist
            config.logger.info(" user " + user.getUsername() + " / " + email + ": Email Not found in whitelist");
            config.getNoEmailMatchAutoJoinGroup().forEach(user::joinGroup);
            user.setSingleAttribute("public-registrant", "true");
          }
        }
    }

    /**
     * Add a custom user attribute (mattermostid) to enable direct mattermost <>
     * keycloak auth on mattermost teams edition
     *
     * @param formData The user registration form data
     */
    private static void generateUniqueStringIdForMattermost(MultivaluedMap<String, String> formData, UserModel user) {
        String email = formData.getFirst(Validation.FIELD_EMAIL);

        byte[] encodedEmail;
        int emailByteTotal = 0;
        Date today = new Date();

        encodedEmail = email.getBytes(StandardCharsets.US_ASCII);
        for (byte b : encodedEmail) {
            emailByteTotal += b;
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("yyDHmsS");

        user.setSingleAttribute("mattermostid", formatDate.format(today) + emailByteTotal);
    }

    @Override
    public void success(FormContext context) {
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String x509Username = X509Tools.getX509Username(context);

        generateUniqueStringIdForMattermost(formData, user);
        joinValidUserToGroups(context, user, x509Username);
        processX509UserAttribute(realm, user, x509Username);
        bindRequiredActions(user, x509Username);
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        String x509Username = X509Tools.getX509Username(context);
        if (x509Username != null) {
            form.setAttribute("cacIdentity", x509Username);
        }
    }

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
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public void validate(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        List<FormMessage> errors = new ArrayList<>();
        String username = formData.getFirst(Validation.FIELD_USERNAME);
        String email = formData.getFirst(Validation.FIELD_EMAIL);

        String eventError = Errors.INVALID_REGISTRATION;

        String location = formData.getFirst("user.attributes.location");
        if (Validation.isBlank(location) || !location.equals("42")) {
            errors.add(new FormMessage("Bot-like activity detected, try disabling auto form filling"));
        }

        if (Validation.isBlank(username)) {
            errors.add(new FormMessage(Validation.FIELD_USERNAME, Messages.MISSING_USERNAME));
        }

        // Username validation based on Mattermost requirements.
        if (!Validation.isBlank(username)) {
            if (!username.matches("[A-Za-z0-9-_.]+")) {
                errors.add(new FormMessage(Validation.FIELD_USERNAME,
                        "Username can only contain alphanumeric, underscore, hyphen and period characters."));
            }

            if (!Character.isLetter(username.charAt(0))) {
                errors.add(new FormMessage(Validation.FIELD_USERNAME, "Username must begin with a letter."));
            }

            if (username.length() < 3 || username.length() > 22) {
                errors.add(new FormMessage(Validation.FIELD_USERNAME, "Username must be between 3 to 22 characters."));
            }
        }

        if (Validation.isBlank(formData.getFirst(RegistrationPage.FIELD_FIRST_NAME))) {
            errors.add(new FormMessage(RegistrationPage.FIELD_FIRST_NAME, Messages.MISSING_FIRST_NAME));
        }

        if (Validation.isBlank(formData.getFirst(RegistrationPage.FIELD_LAST_NAME))) {
            errors.add(new FormMessage(RegistrationPage.FIELD_LAST_NAME, Messages.MISSING_LAST_NAME));
        }

        if (Validation.isBlank(formData.getFirst("user.attributes.affiliation"))) {
            errors.add(new FormMessage("user.attributes.affiliation", "Please specify your organization affiliation."));
        }

        if (Validation.isBlank(formData.getFirst("user.attributes.rank"))) {
            errors.add(new FormMessage("user.attributes.rank", "Please specify your rank or choose n/a."));
        }

        if (Validation.isBlank(formData.getFirst("user.attributes.organization"))) {
            errors.add(new FormMessage("user.attributes.organization", "Please specify your organization."));
        }

        if (X509Tools.getX509Username(context) != null) {
            if (X509Tools.isX509Registered(context)) {
                // X509 auth, invite code not required
                errors.add(new FormMessage(null, "Sorry, this CAC seems to already be registered."));
                context.error(Errors.INVALID_REGISTRATION);
                context.validationError(formData, errors);
            }
        }

        if (Validation.isBlank(email) || !Validation.isEmailValid(email)) {
            context.getEvent().detail(Details.EMAIL, email);
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL,
                    "Please check your email address, it seems to be invalid"));
        }

        if (context.getSession().users().getUserByEmail(context.getRealm(), email) != null) {
            eventError = Errors.EMAIL_IN_USE;
            formData.remove("email");
            context.getEvent().detail("email", email);
            errors.add(new FormMessage("email", Messages.EMAIL_EXISTS));
        }

        if (errors.size() > 0) {
            context.error(eventError);
            context.validationError(formData, errors);
        } else {
            context.success();
        }

    }

}
