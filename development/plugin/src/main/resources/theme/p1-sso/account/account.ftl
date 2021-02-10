<#import "template.ftl" as layout>
<@layout.mainLayout active='account' bodyClass='user'; section>

    <div class="row">
        <div class="col-md-10">
            <h2>${msg("editAccountHtmlTitle")}</h2>
        </div>
        <div class="col-md-2 subtitle">
            <span class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</span>
        </div>
    </div>

    <form action="${url.accountUrl}" class="form-horizontal" method="post">

        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">

        <#if !realm.registrationEmailAsUsername>
            <div class="form-group ${messagesPerField.printIfExists('username','has-error')}">
                <div class="col-sm-2 col-md-2">
                    <label for="username"
                           class="control-label">${msg("username")}</label> <#if realm.editUsernameAllowed><span
                            class="required">*</span></#if>
                </div>

                <div class="col-sm-10 col-md-10">
                    <input type="text" class="form-control" id="username" name="username"
                           <#if !realm.editUsernameAllowed>disabled="disabled"</#if> value="${(account.username!'')}"/>
                </div>
            </div>
        </#if>

        <div class="form-group ${messagesPerField.printIfExists('email','has-error')}">
            <div class="col-sm-2 col-md-2">
                <label for="email" class="control-label">${msg("email")}</label> <span class="required">*</span>
            </div>

            <div class="col-sm-10 col-md-10">
                <input type="text" class="form-control" id="email" name="email" autofocus
                       value="${(account.email!'')}"/>
            </div>
        </div>

        <div class="form-group ${messagesPerField.printIfExists('firstName','has-error')}">
            <div class="col-sm-2 col-md-2">
                <label for="firstName" class="control-label">${msg("firstName")}</label> <span class="required">*</span>
            </div>

            <div class="col-sm-10 col-md-10">
                <input type="text" class="form-control" id="firstName" name="firstName"
                       value="${(account.firstName!'')}"/>
            </div>
        </div>

        <div class="form-group ${messagesPerField.printIfExists('lastName','has-error')}">
            <div class="col-sm-2 col-md-2">
                <label for="lastName" class="control-label">${msg("lastName")}</label> <span class="required">*</span>
            </div>

            <div class="col-sm-10 col-md-10">
                <input type="text" class="form-control" id="lastName" name="lastName" value="${(account.lastName!'')}"/>
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-2 col-md-2">
                <label class="control-label">Affliation & Rank</label>
            </div>

            <div class="col-sm-10 col-md-10">
                <div class="col-sm-8 ${messagesPerField.printIfExists('rank',properties.kcFormGroupErrorClass!)}"
                     style="padding:0">
                    <select id="user.attributes.affiliation" name="user.attributes.affiliation" class="form-control"
                            value="${(account.attributes.affiliation!'')}">
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
                </div>

                <div class="col-sm-4 ${messagesPerField.printIfExists('rank',properties.kcFormGroupErrorClass!)}"
                     style="padding-right:0">
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
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-2 col-md-2">
                <label for="user.attributes.organization" class="control-label">Unit, Organization or Company
                    Name</label>
            </div>

            <div class="col-sm-10 col-md-10">
                <input type="text" class="form-control" id="user.attributes.organization"
                       name="user.attributes.organization" value="${(account.attributes.organization!'')}"/>
            </div>
        </div>

        <div class="form-group">
            <div id="kc-form-buttons" class="col-md-offset-2 col-md-10 submit">
                <div class="">
                    <#if url.referrerURI??><a
                        href="${url.referrerURI}">${kcSanitize(msg("backToApplication")?no_esc)}</a></#if>
                    <button type="submit"
                            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                            name="submitAction" value="Save">${msg("doSave")}</button>
                    <button type="submit"
                            class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}"
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
