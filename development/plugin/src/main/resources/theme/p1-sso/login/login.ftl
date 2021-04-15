<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "form">
        <#if realm.password>
            <form onsubmit="login.disabled=true;return true;" action="${url.loginAction}" method="post">
                
                <div class="form-group">
                    <label class="form-label ${properties.kcLabelClass!}" for="username">
                        <#if !realm.loginWithEmailAllowed>${msg("username")}
                        <#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}
                        <#else>${msg("email")}</#if>
                    </label>
                    <input tabindex="1" id="username" class="form-control ${properties.kcInputClass!}" name="username"
                            value="${(login.username!'')}" type="text" autofocus autocomplete="off"/>
                </div>

                <div class="form-group">
                    <label for="password" class="form-label ${properties.kcLabelClass!}">${msg("password")}</label>
                    <input tabindex="2" id="password" class="form-control ${properties.kcInputClass!}" name="password"
                            type="password" autocomplete="off"/>
                </div>

                <div class="form-group text-right">
                    <#if realm.resetPasswordAllowed>
                        <a tabindex="5" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>
                    </#if>
                </div>

                <div id="form-buttons" class="form-group">
                    <input type="hidden" id="id-hidden-input" name="credentialId"
                            <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                    <input tabindex="4"
                            class="btn btn-primary btn-block"
                            name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                </div>

            </form>
        </#if>

        <div class="footer-text">
            No account? <a href="/register">Click here</a> to register now.<br>
            Need additional help? <a href="https://sso-info.il2.dso.mil/" target="_blank">Click here</a> or <a
                    id="helpdesk" href="mailto:help@dso.mil">email us</a>
        </div>
    </#if>

</@layout.registrationLayout>

<script>
    const feedback = document.getElementsByClassName('kc-feedback-text')[0];
    if (feedback && feedback.innerHTML.indexOf('X509 certificate') > -1 && feedback.innerHTML.indexOf('Invalid user') > -1) {
        feedback.parentElement.outerHTML = [
            '<div class="alert alert-info" id="cac-info">',
            '<h2>New DoD PKI Detected</h2>',
            '<div style="line-height: 2rem;">If you do not have an account yet, <a href="/register">click to register</a> now.  Otherwise, please login with your username/password to associate this CAC with your existing account.',
            '</div></div>'
        ].join('');
    }
</script>