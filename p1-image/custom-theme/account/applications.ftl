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
                <td>${msg("availableRoles")}</td>
              </tr>
            </thead>

            <tbody>
              <#list applications.applications as application>
                <tr>
                    <td>
                        <#if application.effectiveUrl?has_content><a href="${application.effectiveUrl}"></#if>
                            <#if application.client.name?has_content>${advancedMsg(application.client.name)}<#else>${application.client.clientId}</#if>
                        <#if application.effectiveUrl?has_content></a></#if>
                    </td>

                    <td>
                        <#list application.realmRolesAvailable as role>
                            <#if role.description??>${advancedMsg(role.description)}<#else>${advancedMsg(role.name)}</#if>
                            <#if role_has_next>, </#if>
                        </#list>
                        <#list application.resourceRolesAvailable?keys as resource>
                            <#if application.realmRolesAvailable?has_content>, </#if>
                            <#list application.resourceRolesAvailable[resource] as clientRole>
                                <#if clientRole.roleDescription??>${advancedMsg(clientRole.roleDescription)}<#else>${advancedMsg(clientRole.roleName)}</#if>
                                ${msg("inResource")} <strong><#if clientRole.clientName??>${advancedMsg(clientRole.clientName)}<#else>${clientRole.clientId}</#if></strong>
                                <#if clientRole_has_next>, </#if>
                            </#list>
                        </#list>
                    </td>
                </tr>
              </#list>
            </tbody>
        </table>
    </form>

</@layout.mainLayout>