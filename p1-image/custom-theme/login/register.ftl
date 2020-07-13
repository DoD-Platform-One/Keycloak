<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">
        <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
            
            <#if cacIdentity??>
                <div class="alert alert-info" id="cac-info">
                    <h2>DoD CAC User Registration</h2>
                    <h4>${cacIdentity}</h4>
                </div>
            <#else>
                <div class="alert alert-warning" id="sad-panda" style="display:none">
                    <span class="${properties.kcFeedbackWarningIcon!}"></span>
                    <span style="font-weight: bold;">Sorry, you don't seem to be using a DoD CAC or registration invite link.</span>  You can self-register here if you use your CAC, otherwise you will need to get an invite link first.  Please contact your team admin or <a id="helpdesk" href="">email us</a>.  For more details, please <a href="https://sso-info.il2.dsop.io/" target="_blank">click here</a>.
                </div>
            </#if>
            
            <div class="row no-gutters" style="margin-left:-20px;margin-right:-20px">

                <div class="col-sm-6 ${messagesPerField.printIfExists('firstName',properties.kcFormGroupErrorClass!)}">
                    <div>
                        <label for="firstName" class="${properties.kcLabelClass!}">${msg("firstName")}</label>
                    </div>
                    <div>
                        <input type="text" id="firstName" class="${properties.kcInputClass!}" name="firstName" value="${(register.formData.firstName!'')}" />
                    </div>
                </div>

                <div class="col-sm-6 ${messagesPerField.printIfExists('lastName',properties.kcFormGroupErrorClass!)}">
                    <div>
                        <label for="lastName" class="${properties.kcLabelClass!}">${msg("lastName")}</label>
                    </div>
                    <div>
                        <input type="text" id="lastName" class="${properties.kcInputClass!}" name="lastName" value="${(register.formData.lastName!'')}" />
                    </div>
                </div>

            </div>

            <br>
            
            <div class="row no-gutters" style="margin-left:-20px;margin-right:-20px">

                <div class="col-sm-6 ${messagesPerField.printIfExists('user.attributes.affiliation',properties.kcFormGroupErrorClass!)}">
                    <label for="user.attributes.affiliation">Affiliation</label>
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
                </div>

                <div class="col-sm-6 ${messagesPerField.printIfExists('user.attributes.rank',properties.kcFormGroupErrorClass!)}">
                    <label for="user.attributes.rank">Pay Grade</label>
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

            <br>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('user.attributes.organization',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="user.attributes.organization" class="${properties.kcLabelClass!}">Unit, Organization or Company Name</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="user.attributes.organization" class="${properties.kcInputClass!}" name="user.attributes.organization" value="${(register.formData['user.attributes.organization']!'')}" autocomplete="company" />
                </div>
            </div>

            <#if !realm.registrationEmailAsUsername>
                <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('username',properties.kcFormGroupErrorClass!)}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="username" class="${properties.kcLabelClass!}">${msg("username")}</label>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="text" id="username" class="${properties.kcInputClass!}" name="username" value="${(register.formData.username!'')}" autocomplete="username" />
                    </div>
                </div>
            </#if>
            
            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('email',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="email" class="${properties.kcInputClass!}" name="email" value="${(register.formData.email!'')}" autocomplete="email" />
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('notes',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="user.attributes.notes" class="${properties.kcLabelClass!}">Access Request Notes</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <textarea id="user.attributes.notes" class="${properties.kcInputClass!}" name="user.attributes.notes"></textarea>
                </div>
            </div>

            <#if !cacIdentity??>
                <hr>

                <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('password',properties.kcFormGroupErrorClass!)}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="password" id="password" class="${properties.kcInputClass!}" name="password" autocomplete="new-password"/>
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('password-confirm',properties.kcFormGroupErrorClass!)}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="password-confirm" class="${properties.kcLabelClass!}">${msg("passwordConfirm")}</label>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="password" id="password-confirm" class="${properties.kcInputClass!}" name="password-confirm" />
                    </div>
                </div>
            </#if>

            <#if recaptchaRequired??>
                <div class="form-group">
                    <div class="${properties.kcInputWrapperClass!}">
                        <div class="g-recaptcha" data-theme="dark" data-size="normal" data-sitekey="${recaptchaSiteKey}"></div>
                    </div>
                </div>
            </#if>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doRegister")}"/>
                </div>
            </div>

            <input class="form-control" id="invite" name="invite" type="hidden" />
        </form>
    </#if>
</@layout.registrationLayout>

<script>
    // Try to find the invite code in the URL param or session storage
    const inviteInput = document.getElementById('invite');
    const urlParams = new URLSearchParams(window.location.search);
    const inviteCode = urlParams.get('invite') || sessionStorage.getItem('invite-code');

    // Update session storage so keycloak doesn't ruin our day on error redirections
    sessionStorage.setItem('invite-code', inviteCode);
    inviteInput.value  = inviteCode || 'Missing invite code';

    document.getElementById('user.attributes.affiliation').value = "${(register.formData['user.attributes.affiliation']!'')}";
    document.getElementById('user.attributes.rank').value = "${(register.formData['user.attributes.rank']!'')}";

    if (!inviteCode || inviteCode === 'none') {
        const sadPanda = document.getElementById('sad-panda');
        sadPanda.style.display = 'block';

        const helpdeskLink = document.getElementById('helpdesk');
        helpdeskLink.setAttribute('href', [
            'mailto',
            ':',
            'DOD_p1chat_Admin', 
            '@', 
            'afwerx.af.mil', 
            '?', 
            'subject', 
            '=', 
            'new-account'].join('')
            );
    }
</script>