<#include "header.ftl">
<#include "*/index.html">
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
            <a href="${pair.getEndpointName()}/${note.id}"><img class="icon" src="./img/${pair.getIconName()}.svg"></a>
            </#list>
        </div>
    </div>
</#list>