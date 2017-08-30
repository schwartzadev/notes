<head><link rel="stylesheet" type="text/css" href="/styles.css"></head>
<form id="note-factory" method="post" action="/update-note">
    <label for="title">Title:</label>
    <#if n.title??>
        <input name="title" id="title" type="text" value="${n.getTitle()}">
    <#else>
        <input name="title" id="title" type="text">
    </#if>
    <label for="textarea">Body *</label>
    <textarea id="textarea" rows="20" name="body" required>${n.getBody()}</textarea>
    <label class="colorText" for="colorchoice">Custom hex color:</label>
    <input type="text" name="color" id="colorchoice" maxlength="6" value="${n.getColor()}">
    <input type="hidden" name="id" value="${n.getId()}">
    <button class="submit" >Submit</button>
</form>