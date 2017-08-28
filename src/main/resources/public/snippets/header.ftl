<head><link rel="stylesheet" type="text/css" href="/styles.css"></head>
<div class="topnav">
    <b><a id="popupTrigger">Add note</a></b>
    <a href="index.html">View notes</a>
    <a href="archived.html">View archived notes</a>
    <a href="/logout" style="float:right;">Sign Out (${user.getUsername()})</a>
</div>

<div id="myModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <span class="close">&times;</span>
            <h3>Add a note...</h3>
        </div>
        <div class="modal-body">
            <form method="post" action="/make-note">
                <div>
                    <label for="title">Title:</label>
                    <input name="title" id="title" type="text">
                </div>
                <div>
                    <label for="textarea">Body *</label>
                    <textarea id="textarea" name="body" rows="3" required></textarea>
                </div>
                <hr>
                <div class="colorButtons inline">
                    <label class="colorText">Choose color:</label>
                    <input type="button" value=" " style="background-color: #70d5d8;" onClick="document.getElementById('colorchoice').value='70d5d8'">
                    <input type="button" value=" " style="background-color: #8dffcd;" onClick="document.getElementById('colorchoice').value='8dffcd'">
                    <input type="button" value=" " style="background-color: #ebbab9;" onClick="document.getElementById('colorchoice').value='ebbab9'">
                    <input type="button" value=" " style="background-color: #eda6dd;" onClick="document.getElementById('colorchoice').value='eda6dd'">
                    <input type="button" value=" " style="background-color: #c09bd8;" onClick="document.getElementById('colorchoice').value='c09bd8'">
                    <input type="button" value=" " style="background-color: #9f97f4;" onClick="document.getElementById('colorchoice').value='9f97f4'">
                    <input type="button" value=" " style="background-color: #a4def9;" onClick="document.getElementById('colorchoice').value='a4def9'">
                </div>
                <div>
                    <label for="colorchoice">Custom hex color:</label>
                    <input class="inline" style="display: inline;" type="text" name="color" id="colorchoice" maxlength="6">
                </div>
                <br>
                <button class="submit" >Submit</button>
            </form>
        </div>
    </div>
</div>

<script>
var modal = document.getElementById('myModal');
var btn = document.getElementById('popupTrigger');
var close = document.getElementsByClassName('close')[0];
document.addEventListener("keydown", escKeyDown, false);

function escKeyDown(e) {
  if(e.keyCode==27) {
    modal.style.display = "none";
  }
}

btn.onclick = function() {
    modal.style.display = "block";
}
close.onclick = function() {
    modal.style.display = "none";
}
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
</script>