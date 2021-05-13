<#import "template.ftl" as layout>
<@layout.mainLayout active='password' bodyClass='password'; section>

    <div class="row">
        <div class="col-md-10">
            <h2>${msg("changePasswordHtmlTitle")}</h2>
        </div>
    </div>

    <form action="${url.passwordUrl}" class="form-horizontal" method="post">
        <input type="text" id="username" name="username" value="${(account.username!'')}" autocomplete="username" readonly="readonly" style="display:none;">

        <#if password.passwordSet>
            <div class="form-group">
                <label for="password" class="control-label">${msg("password")}</label>
                <input type="password" class="form-control" id="password" name="password" autofocus autocomplete="current-password">
            </div>
        </#if>

        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">

        <div class="form-group">
            <label for="password-new" class="control-label">${msg("passwordNew")}</label>
            <input type="password" class="form-control" id="password-new" name="password-new" autocomplete="new-password">
        </div>

        <div class="form-group">
            <label for="password-confirm" class="control-label" class="two-lines">${msg("passwordConfirm")}</label>
            <input type="password" class="form-control" id="password-confirm" name="password-confirm" autocomplete="new-password">
        </div>

        <div class="form-group">
            <div id="kc-form-buttons" class="text-right submit">
                <button type="submit" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="submitAction" value="Save">${msg("doSave")}</button>
            </div>
        </div>
    </form>

</@layout.mainLayout>
