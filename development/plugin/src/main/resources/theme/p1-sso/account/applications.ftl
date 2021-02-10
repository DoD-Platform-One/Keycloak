<#import "template.ftl" as layout>
<@layout.mainLayout active='applications' bodyClass='applications'; section>

    <div class="row">
        <div class="col-md-10">
            <h2>${msg("applicationsHtmlTitle")}</h2>
        </div>
    </div>

    <form action="${url.applicationsUrl}" method="post">
        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">
        <input type="hidden" id="referrer" name="referrer" value="${stateChecker}">

        <table class="table table-striped table-bordered">
            <thead>
            <tr>
                <td>${msg("application")}</td>
            </tr>
            </thead>

            <tbody>
            <#list applications.applications as application>
                <tr>
                    <td>
                        <#if application.client.name?has_content>${advancedMsg(application.client.name)}<#else>${application.client.clientId}</#if>
                        <#--  <#if application.effectiveUrl?has_content><a href="${application.effectiveUrl}"></#if>
                        <#if application.effectiveUrl?has_content></a></#if>  -->
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </form>

</@layout.mainLayout>