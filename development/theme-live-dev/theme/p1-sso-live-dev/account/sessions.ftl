<#import "template.ftl" as layout>
<@layout.mainLayout active='sessions' bodyClass='sessions'; section>

    <div class="row">
        <div class="col-md-10">
            <h2>${msg("sessionsHtmlTitle")}</h2>
        </div>
    </div>

    <br>

    <table class="table table-striped table-bordered">
        <thead>
            <tr>
                <td class="w-50">Valid Range / Last Access</td>
                <td class="w-50">Application ID(s)</td>
            </tr>
        </thead>

        <tbody>
            <#list sessions.sessions as session>
                <tr>
                    <td>
                        ${session.started?datetime} - <br> 
                        ${session.expires?datetime} <br>
                        <br>
                        <span class="font-weight-bold">${session.lastAccess?datetime}</span>
                    </td>
                    <td>
                        <#list session.clients as client>
                            ${client}<br/>
                        </#list>
                    </td>
                </tr>
            </#list>
        </tbody>

    </table>

    <form action="${url.sessionsUrl}" method="post">
        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">
        <button id="logout-all-sessions" class="btn btn-default">${msg("doLogOutAllSessions")}</button>
    </form>

</@layout.mainLayout>
