function togglePass() {
    var box = document.getElementById('showPass');
    if (box.checked) {
        document.getElementById('passInput').type = 'text';
    } else {
        document.getElementById('passInput').type = 'password';
    }
}

function checkRegister() {
    function failedTest(error) {
        message.innerHTML = error;
        message.textContent = error;
        ok = false;
    }

    var user = document.getElementById("username");
    var pass1 = document.getElementById("first-pass");
    var pass2 = document.getElementById("second-pass");
    var message = document.getElementById("message");
    var ok = true;
    if (pass1.value != pass2.value) {
        pass1.style.borderColor = "#E34234";
        pass2.style.borderColor = "#E34234";
        failedTest("passwords don't match");
    } else {
        if (pass1.value.length < 6) {
            failedTest('your password must contain at least 6 characters');
        }
        if (pass1.value.length >= 255) {
            failedTest('your password must contain less than 255 characters');
        }
        if (user.value.length >= 255) {
            failedTest('your username must contain less than 255 characters');
        }
        if (user.value.length < 4) {
            failedTest('your username must contain at least 4 characters');
        }
    }
    return ok;
}

function modalScripts() {
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
        document.getElementById("textarea").focus();
    }
    close.onclick = function() {
        modal.style.display = "none";
    }
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
}

function deleteMe(id) {
	var url = 'delete/' + id;
	$.ajax({
		url: url,
		success: function(result) {
			console.log('deleted ' + id);
			$('#' + id).hide(120);
		},
		error: function() {
            alert("an error occurred while trying to delete a note");
        }
	});
}