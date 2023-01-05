<#import "template.ftl" as layout>
<@layout.mainLayout active='account' bodyClass='user'; section>

    <div class="row">
        <div class="col-md-12">
            <h2>${msg("editAccountHtmlTitle")}</h2>
        </div>
    </div>


    <form action="${url.accountUrl}" class="form-horizontal" method="post">

        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">

        <div class="row">

            <div class="col-lg-6 form-group ${messagesPerField.printIfExists('firstName','has-error')}">
                <label for="firstName" class="form-label">${msg("firstName")}</label>
                <input type="text" id="firstName" class="form-control" name="firstName"
                        value="${(account.firstName!'')}"/>
                <#if messagesPerField.existsError('firstName')>
                    <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('firstName'))?no_esc}</span>
                </#if>
            </div>

            <div class="col-lg-6 form-group ${messagesPerField.printIfExists('lastName','has-error')}">
                <label for="lastName" class="form-label">${msg("lastName")}</label>
                <input type="text" id="lastName" class="form-control" name="lastName"
                        value="${(account.lastName!'')}"/>
                <#if messagesPerField.existsError('lastName')>
                    <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('lastName'))?no_esc}</span>
                </#if>
            </div>

        </div>

        <div class="row">

            <div class="col-lg-6 form-group ${messagesPerField.printIfExists('user.attributes.affiliation','has-error')}">
                <label for="user.attributes.affiliation" class="form-label">Affiliation</label>
                <select id="user.attributes.affiliation" name="user.attributes.affiliation" class="form-control">
                    <option selected disabled hidden>Select your org</option>
                    <optgroup label="US Government">
                        <option>US Air Force</option>
                        <option>US Air Force Reserve</option>
                        <option>US Air National Guard</option>
                        <option>US Army</option>
                        <option>US Army Reserve</option>
                        <option>US Army National Guard</option>
                        <option>US Coast Guard</option>
                        <option>US Coast Guard Reserve</option>
                        <option>US Marine Corps</option>
                        <option>US Marine Corps Reserve</option>
                        <option>US Navy</option>
                        <option>US Navy Reserve</option>
                        <option>US Space Force</option>
                        <option>Dept of Defense</option>
                        <option>Federal Government</option>
                        <option>Other</option>
                    </optgroup>

                    <optgroup label="Contractor">
                        <option>A&AS</option>
                        <option>Contractor</option>
                        <option>FFRDC</option>
                        <option>Other</option>
                    </optgroup>
                </select>
                <#if messagesPerField.existsError('user.attributes.affiliation')>
                    <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('user.attributes.affiliation'))?no_esc}</span>
                </#if>
            </div>

            <div class="col-lg-6 form-group ${messagesPerField.printIfExists('user.attributes.rank','has-error')}">
                <label for="user.attributes.rank" class="form-label">Pay Grade</label>
                <select id="user.attributes.rank" name="user.attributes.rank" class="form-control">
                    <option selected disabled hidden>Select your rank</option>
                    <optgroup label="Enlisted">
                        <option>E-1</option>
                        <option>E-2</option>
                        <option>E-3</option>
                        <option>E-4</option>
                        <option>E-5</option>
                        <option>E-6</option>
                        <option>E-7</option>
                        <option>E-8</option>
                        <option>E-9</option>
                    </optgroup>

                    <optgroup label="Warrant Officer">
                        <option>W-1</option>
                        <option>W-2</option>
                        <option>W-3</option>
                        <option>W-4</option>
                        <option>W-5</option>
                    </optgroup>

                    <optgroup label="Officer">
                        <option>O-1</option>
                        <option>O-2</option>
                        <option>O-3</option>
                        <option>O-4</option>
                        <option>O-5</option>
                        <option>O-6</option>
                        <option>O-7</option>
                        <option>O-8</option>
                        <option>O-9</option>
                        <option>O-10</option>
                    </optgroup>

                    <optgroup label="Civil Service">
                        <option>GS-1</option>
                        <option>GS-2</option>
                        <option>GS-3</option>
                        <option>GS-4</option>
                        <option>GS-5</option>
                        <option>GS-6</option>
                        <option>GS-7</option>
                        <option>GS-8</option>
                        <option>GS-9</option>
                        <option>GS-10</option>
                        <option>GS-11</option>
                        <option>GS-12</option>
                        <option>GS-13</option>
                        <option>GS-14</option>
                        <option>GS-15</option>
                        <option>SES</option>
                    </optgroup>
                    <option>N/A</option>
                </select>
                <#if messagesPerField.existsError('user.attributes.rank')>
                    <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('user.attributes.rank'))?no_esc}</span>
                </#if>
            </div>

        </div>

        <div class="form-group ${messagesPerField.printIfExists('user.attributes.organization','has-error')}">
            <label for="user.attributes.organization" class="form-label">Unit, Organization or Company Name</label>
            <input id="user.attributes.organization" class="form-control" name="user.attributes.organization" type="text"
                    value="${(account.attributes.organization!'')}" />
            <#if messagesPerField.existsError('user.attributes.organization')>
                <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('user.attributes.organization'))?no_esc}</span>
            </#if>
        </div>

        <#if !realm.registrationEmailAsUsername>
            <div class="form-group ${messagesPerField.printIfExists('username','has-error')}">
                <label for="username" class="form-label">${msg("username")}</label>
                <input id="username" class="form-control" name="username" type="text"
                            <#if !realm.editUsernameAllowed>disabled="disabled"</#if> value="${(account.username!'')}"/>
                <#if messagesPerField.existsError('username')>
                    <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('username'))?no_esc}</span>
                </#if>
            </div>
        </#if>

        <div class="form-group ${messagesPerField.printIfExists('email','has-error')}">
            <label for="email" class="form-label">${msg("email")}</label>
            <input id="email" class="form-control" name="email" type="text"
                    value="${(account.email!'')}" />
            <#if messagesPerField.existsError('email')>
                <span class="message-details" aria-live="polite">${kcSanitize(messagesPerField.get('email'))?no_esc}</span>
            </#if>
        </div>

        <div class="form-group">
            <div class="submit">
                <div class="">
                    <#if url.referrerURI??><a
                        href="${url.referrerURI}">${kcSanitize(msg("backToApplication")?no_esc)}</a></#if>
                    <button type="submit"
                            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!} btn-block"
                            name="submitAction" value="Save">${msg("doSave")}</button>
                    <button type="submit"
                            class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!} btn-block"
                            name="submitAction" value="Cancel">${msg("doCancel")}</button>
                </div>
            </div>
        </div>
    </form>

</@layout.mainLayout>

<script>
    document.getElementById('user.attributes.affiliation').value = "${(account.attributes.affiliation!'')}";
    document.getElementById('user.attributes.rank').value = "${(account.attributes.rank!'')}";
</script>
