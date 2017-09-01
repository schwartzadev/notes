<head><link rel="stylesheet" type="text/css" href="/styles.css"></head>
<h1>Log in</h1>
<div class="login">
    <form action="/sign-in" method="post">
        <br>
        <label><b>Username</b></label>
        <br>
        <input type="text" placeholder="Enter Username" name="username" required>
        <br>
        <label><b>Password</b></label>
        <br>
        <input id="passInput" type="password" placeholder="Enter Password" name="pwd" required style="float: left;">
        <input type="checkbox" id="showPass" onclick="togglePass()">
        <label> Show Password</label>
        <div style="clear:both;">&nbsp;</div>
        <button type="submit">Let's go</button>
        <br>
        <input type="checkbox" checked="checked" name="remember"><label> Remember me</label>
    </form>
</div>
<br>
<div class="center">
    <a class="loginprompt" href="/register">Don't have an account?</a>
</div>
<script src="/scripts.js"></script>
