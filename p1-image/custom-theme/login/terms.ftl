<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("termsTitle")}
    <#elseif section = "form">
    <div id="kc-terms-text">
        <div>
            <h1>You are accessing a U.S. Government (USG) Information System (IS) that is provided for USG-authorized use only.</h1>
            <h2>By using  this IS (which includes any device attached to this IS), you consent to the following conditions:</h2>
            <ul>
                <li>The USG routinely intercepts and monitors communications on this IS for purposes including, but not limited to, penetration testing, COMSEC monitoring, network operations and defense, personnel misconduct (PM), law enforcement (LE), and counterintelligence (CI) investigations.</li>
                <li>At any time, the USG may inspect and seize data stored on this IS.</li>
                <li>Communications using, or data stored on, this IS are not private, are subject to routine monitoring, interception, and search, and may be disclosed or used for any USG authorized purpose.</li>
                <li>This IS includes security measures (e.g., authentication and access controls) to protect USG interests--not for your personal benefit or privacy.</li>
                <li>NOTICE: There is the potential that information presented and exported from the Platform One contains FOUO or Controlled Unclassified Information (CUI). It is the responsibility of all users to ensure information extracted from Platform One is appropriately marked and properly safeguarded. If you are not sure of the safeguards necessary for the information, contact your functional lead or Information Security Officer.</li>
                <li>Notwithstanding the above, using this IS does not constitute consent to PM, LE or CI investigative searching or monitoring of the content of privileged communications, or work product, related to personal representation or services by attorneys, psychotherapists, or clergy, and their assistants. Such communications and work product are private and confidential. See <a class="linkInline" href="https://www.my.af.mil/afp/netstorage/login_page_files_cloud_one/dod-user-agreement.html" target="_blank">User Agreement</a> for details.</li>
            </ul>
        </div>
    </div>
    <form class="form-actions" action="${url.loginAction}" method="POST">
        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="accept" id="kc-accept" type="submit" value="${msg("doAccept")}"/>
        <input class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" name="cancel" id="kc-decline" type="submit" value="${msg("doDecline")}"/>
    </form>
    <div class="clearfix"></div>
    </#if>
</@layout.registrationLayout>
