function togglePass() {
    var box = document.getElementById('showPass');
    if (box.checked) {
        document.getElementById('passInput').type = 'text';
    } else {
        document.getElementById('passInput').type = 'password';
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