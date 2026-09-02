/*
 * Carrying a file to the kernel and back.
 *
 * The kernel here answers HTTP: it runs in a directory of its own per browser session and cannot
 * reach the file system this page is displayed on. So Import("data.csv") cannot mean a file on your
 * disk - the bytes have to be carried across first. uploadFile() does the carrying, and the cell it
 * then writes names the file the way a local notebook would.
 *
 * The other direction is a plain link: ExportForm(expr, "CSV") makes the query servlet write the
 * bytes into the same directory and return an <a download> to /ajax/download/.
 */

var uploadFileInput = null;

/** Ask for a file, post it to the session's directory, and write the Import cell for it. */
function uploadFile() {
	if (!uploadFileInput) {
		uploadFileInput = $E('input', {
			'type': 'file',
			'id': 'uploadFile',
			'style': 'display: none'
		});
		document.body.appendChild(uploadFileInput);
		uploadFileInput.observe('change', uploadFileChosen);
	}
	// so that picking the same file twice in a row fires the change event again
	uploadFileInput.value = '';
	uploadFileInput.click();
}

function uploadFileChosen() {
	var file = uploadFileInput.files[0];
	if (!file)
		return;

	var form = new FormData();
	form.append('file', file);

	var request = new XMLHttpRequest();
	request.open('POST', '/ajax/upload/');
	request.onload = function() {
		var result;
		try {
			result = JSON.parse(request.responseText);
		} catch (e) {
			window.alert("Upload failed: " + request.responseText);
			return;
		}
		if (result.error) {
			window.alert(result.error);
			return;
		}
		uploadDone(result.name);
	};
	request.onerror = function() {
		window.alert("Cannot upload '" + file.name + "'.");
	};
	// FormData sets its own multipart boundary, so no Content-Type is set here
	request.send(form);
}

/**
 * Put the input that reads the uploaded file into a cell, ready to run. Nothing is evaluated: the
 * user sees what will be read before reading it.
 */
function uploadDone(name) {
	if ($('welcomeContainer'))
		$('welcomeContainer').hide();
	var li = createQuery();
	if (li && li.textarea) {
		li.textarea.value = importCall(name);
		refreshInputSize(li.textarea);
		li.textarea.focus();
	}
}

/** The function that reads this file, chosen by its extension. */
function importCall(name) {
	var dot = name.lastIndexOf('.');
	var extension = dot > 0 ? name.substring(dot + 1).toLowerCase() : '';
	if (extension == 'csv' || extension == 'tsv' || extension == 'xlsx' || extension == 'xls')
		return 'SemanticImport("' + name + '")';
	return 'Import("' + name + '")';
}
