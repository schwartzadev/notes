<head><link rel="stylesheet" type="text/css" href="/styles.css"></head>
<script src="/scripts.js"></script>
<h1>Sign up!</h1>
<div class="login">
    <form action="/signup" method="post" onsubmit="return checkRegister()">
        <br>
        <label><b>Username</b></label>
        <br>
        <input type="text" placeholder="Enter Username" name="username" required>
        <br>
        <label><b>Password</b></label>
        <br>
        <input type="password" placeholder="Enter Password" name="pwd" id="first-pass" required>
        <br>
        <label><b>Confirm Password</b></label>
        <br>
        <input type="password" placeholder="Enter Password" name="pwd" id="second-pass" required>
        <br>
        <span id="message"></span>
        <br>
        <input type="submit" value="Sign me up">
        <br>
        <input type="checkbox" checked="checked" name="remember"> Remember me
    </form>
</div>
<br>
<div class="center">
    <a class="loginprompt" href="/login">Already have an account?</a>
</div>
