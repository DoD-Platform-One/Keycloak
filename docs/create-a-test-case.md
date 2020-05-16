# Manually Creating a Test Case

1. Navigate to keycloack, Select Adminstration Console

![Keycloack welcome](img/welcome.png)

2. Login as an administrator username: admin password: pass

![Keycloak Login](img/login.png)

3. Make sure you're in the baby-yoda realm

![Baby Yoda Realm](img/realm-baby-yoda.png)

4. Under Manage, select Users

![Mangage, then users](img/users.png)

5. Add a test user, Select User Required Actions that include
  - Configure OTP
  - Terms and Conditions
  - Update Password
  - Update Profile
  - Verify Email
  Then, click Save

![Add a test user](img/add-test-user.png)

6. Now impersonate the user

![Click impersonate user](img/impersonate-user.png)

7. The invite link will be at the bottom of the page

![User, applications menu, invite link at bottom of page](img/invite-link.png)

8. Copy the invite link, and paste it into the browser, navigate to the invite page.  The registration page should be displayed.

![navigate to the registration page](img/navigate-to-registration.png)
