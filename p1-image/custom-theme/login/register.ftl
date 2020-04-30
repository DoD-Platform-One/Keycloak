<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">
        <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('firstName',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="firstName" class="${properties.kcLabelClass!}">${msg("firstName")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="firstName" class="${properties.kcInputClass!}" name="firstName" value="${(register.formData.firstName!'')}" />
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('lastName',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="lastName" class="${properties.kcLabelClass!}">${msg("lastName")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="lastName" class="${properties.kcInputClass!}" name="lastName" value="${(register.formData.lastName!'')}" />
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} ${messagesPerField.printIfExists('email',properties.kcFormGroupErrorClass!)}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="email" class="${properties.kcInputClass!}" name="email" value="${(register.formData.email!'')}" autocomplete="email" />
                </div>
            </div>

            <br>

            <div class="row no-gutters" style="margin-left:-20px;margin-right:-20px">

                <div class="col-sm-6 ${messagesPerField.printIfExists('rank',properties.kcFormGroupErrorClass!)}">
                    <label for="user.attributes.affiliation">Affiliation</label>
                    <select id="user.attributes.affiliation" name="user.attributes.affiliation" class="form-control">
                        <option></option>
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

                <div class="col-sm-6 ${messagesPerField.printIfExists('rank',properties.kcFormGroupErrorClass!)}">
                    <label for="user.attributes.rank">Pay Grade</label>
                    <select id="user.attributes.rank" name="user.attributes.rank" class="form-control">
                        <option></option>
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
            <hr>

            <#if passwordRequired??>
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
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                </div>
            </div>
            </#if>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <span><a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doRegister")}"/>
                </div>
            </div>

            <input class="form-control" id="user.attributes.mattermostid" name="user.attributes.mattermostid" type="hidden" />
        </form>
    </#if>
</@layout.registrationLayout>

<script>
    const mmid = document.getElementById('user.attributes.mattermostid');
    mmid.value =  Date.now() + Math.floor(1000 + Math.random() * 9000);
</script>