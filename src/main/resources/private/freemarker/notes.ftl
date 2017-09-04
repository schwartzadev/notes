<#include "*/snippets/header.ftl">
<#include "*/snippets/index.html">
<h1 class="pagetitle">all notes</h1>
<div class="container">
    <#list notes as note>
        <div class="note" id="${note.getId()}" style="background-color:${note.getColor()};">
            <#if note.title??>
            <h2 class="title">${note.getTitle()}</h2>
            <div class="content hastitle">
                ${note.getHtml()}        </div>
            <#else>
            <div class="content larger">
                ${note.getHtml()}        </div>
            </#if>
            <div class="toolbar">
                <#list iconlist as pair>
                    <#--<#if pair.getEndpointName() = "edit">-->
                        <#--<a id="edit-trigger-${note.getId()}" href="${pair.getEndpointName()}/${note.id}"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>-->
                    <#if pair.getEndpointName() = "delete">
                        <#--<a href="${pair.getEndpointName()}/${note.id}" onclick="deleteMe(this, ${note.id}"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>-->
                        <a title="delete" onclick="deleteMe(${note.id})"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>
                    <#elseif pair.getEndpointName() = "share">
                        <#if note.getTitle()??>
                            <a title="share" href="mailto:?to=&body=${note.getEncodedBody()}&subject=A+Note%3A+${note.getEncodedTitle()}" target="_blank"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>
                        <#else>
                            <a title="share" href="mailto:?to=&body=${note.getEncodedBody()}&subject=A+Note+For+You" target="_blank"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>
                        </#if>
                    <#else>
                        <a title="${pair.getEndpointName()}" href="${pair.getEndpointName()}/${note.id}"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>
                    </#if>
                </#list>
            </div>
        </div>
    </#list>
</div>
<#include "*/snippets/footer.html">