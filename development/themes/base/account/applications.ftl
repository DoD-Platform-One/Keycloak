<#import "template.ftl" as layout>
<@layout.mainLayout active='applications' bodyClass='applications'; section>

    <div class="row">
        <div class="col-md-10">
            <h2>This page intentionally left blank</h2>
        </div>
    </div>

    <form action="${url.applicationsUrl}" method="post">
        <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">
        <input type="hidden" id="referrer" name="referrer" value="${stateChecker}">
    </form>

</@layout.mainLayout>