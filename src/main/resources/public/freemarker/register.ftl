<head><link rel="stylesheet" type="text/css" href="/styles.css"></head>
<h1>Sign up!</h1>
<div class="login">
    <form action="/signup" method="post">
        <br>
        <label><b>Username</b></label>
        <br>
        <input type="text" placeholder="Enter Username" name="username" required>
        <br>
        <label><b>Password</b></label>
        <br>
        <input type="password" placeholder="Enter Password" name="pwd" required>
        <br>
        <button type="submit">Sign me up</button>
        <br>
        <input type="checkbox" checked="checked" name="remember"> Remember me
    </form>
</div>