<#macro mainLayout active bodyClass>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="robots" content="noindex, nofollow">

    <title>${msg("accountManagementTitle")}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico">
    <#if properties.stylesCommon?has_content>
        <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script type="text/javascript" src="${url.resourcesPath}/${script}"></script>
        </#list>
    </#if>
</head>
<body class="admin-console user ${bodyClass}">
        
    <nav class="navbar navbar-expand-md fixed-top bg-dark">
        <div class="container-fluid">
            <div class="upper-logo"><img src="${url.resourcesPath}/img/p1-logo-tall.png" /></div>
            <h3 class="mb-0"><a>${realm.displayName}</a></h3>
            <div class="collapse navbar-collapse flex-column align-items-start ml-lg-2 ml-0" id="navbarCollapse">
                <ul class="navbar-nav mb-auto mt-0 ml-auto">
                    <li class="nav-item <#if active=='account'>active</#if>"><a class="nav-link" href="${url.accountUrl}">${msg("account")}</a></li>
                    <#if features.passwordUpdateSupported><li class="nav-item <#if active=='password'>active</#if>"><a class="nav-link" href="${url.passwordUrl}">${msg("password")}</a></li></#if>
                    <li class="nav-item <#if active=='totp'>active</#if>"><a class="nav-link" href="${url.totpUrl}">${msg("authenticator")}</a></li>
                    <#if features.identityFederation><li class="nav-item <#if active=='social'>active</#if>"><a class="nav-link" href="${url.socialUrl}">${msg("federatedIdentity")}</a></li></#if>
                    <li class="nav-item <#if active=='sessions'>active</#if>"><a class="nav-link" href="${url.sessionsUrl}">${msg("sessions")}</a></li>
                    <#if features.log><li class="nav-item <#if active=='log'>active</#if>"><a class="nav-link" href="${url.logUrl}">${msg("log")}</a></li></#if>
                    <li class="nav-item"><span class="nav-link">|</span></li>
                    <li class="nav-item"><a class="nav-link" href="${url.logoutUrl}">${msg("doSignOut")}</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row justify-content-center">
            <div class="col-xl-6 col-lg-8 col-md-12">
                <div class="card">
                    <div class="card-body">
                        <#if message?has_content>
                            <div class="alert alert-${message.type}">
                                <#if message.type=='success' ><span class="pficon pficon-ok"></span></#if>
                                <#if message.type=='error' ><span class="pficon pficon-error-circle-o"></span></#if>
                                <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                            </div>
                        </#if>

                        <#nested "content">
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
</#macro>