<head>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <script
            src="https://code.jquery.com/jquery-3.2.1.min.js"
            integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
            crossorigin="anonymous">
    </script>
    <script src="/scripts.js"></script>
</head>
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
<script>ctrlEnterTextarea();</script>
