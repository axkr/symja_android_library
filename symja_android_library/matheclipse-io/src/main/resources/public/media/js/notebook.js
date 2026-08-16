/**
    Symja: a general-purpose computer algebra system

    Open and save *.ipynb notebooks on the local disk.

    Opening a notebook follows what Jupyter does: the cells are shown, but nothing is
    evaluated - the engine session stays as it was, and the user decides when to run the
    notebook with "Run all". Saved outputs are deliberately *not* restored, because an
    output is raw HTML which createLine() hands to Prototype's update(), which executes
    any <script> in it; a notebook from somebody else would then be running code in this
    page, with the evaluation engine of this server one request away.
**/

/** nbformat version written by saveNotebook(). 4.5 requires an "id" on every cell. */
var NBFORMAT = 4;
var NBFORMAT_MINOR = 5;

/** File name of the notebook that was opened last, used as the default when saving. */
var notebookFileName = null;

var notebookFileInput = null;
var notebookCellId = 0;

// ---------------------------------------------------------------- syntax variant

/** Either 'mma' (Mathematica syntax) or 'relaxed' (Symja syntax), set by the page. */
function symjaSyntax() {
	return (typeof SYMJA_SYNTAX == 'undefined') ? 'relaxed' : SYMJA_SYNTAX;
}

function symjaSyntaxName(syntax) {
	return syntax == 'mma' ? "Mathematica syntax" : "Symja syntax (relaxed syntax)";
}

function notebookMetadata() {
	if (symjaSyntax() == 'mma') {
		return {
			'kernelspec': {
				'display_name': "Symja (Mathematica syntax)",
				'language': 'Wolfram Language',
				'name': 'symja-mma'
			},
			'language_info': {
				'name': 'Wolfram Language',
				'file_extension': '.wl',
				'mimetype': 'application/vnd.wolfram.wl',
				'codemirror_mode': 'mathematica',
				'pygments_lexer': 'mathematica'
			},
			'symja': {'syntax': 'mma', 'generator': "Symja notebook interface"}
		};
	}
	return {
		'kernelspec': {
			'display_name': "Symja",
			'language': 'symja',
			'name': 'symja'
		},
		'language_info': {
			'name': 'symja',
			'file_extension': '.m',
			'mimetype': 'text/x-symja',
			'codemirror_mode': 'mathematica',
			'pygments_lexer': 'mathematica'
		},
		'symja': {'syntax': 'relaxed', 'generator': "Symja notebook interface"}
	};
}

// ---------------------------------------------------------------- small helpers

function escapeHTML(text) {
	return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

/**
 * Split a cell source into the array of lines that the ipynb format uses: every line but
 * the last keeps its trailing newline.
 */
function sourceLines(text) {
	if (text == null || text == '')
		return [];
	var lines = text.split('\n');
	var result = [];
	for (var index = 0; index < lines.length; ++index)
		result.push(index < lines.length - 1 ? lines[index] + '\n' : lines[index]);
	if (result[result.length - 1] == '')
		result.pop();
	return result;
}

/** The ipynb format allows a cell source to be a string or an array of strings. */
function cellSource(cell) {
	var source = cell.source;
	if (source == null)
		return '';
	if (Object.isArray(source))
		return source.join('');
	return '' + source;
}

function newCellId() {
	return 'symja-' + (notebookCellId++) + '-' +
		Math.floor(Math.random() * 0x10000).toString(16);
}

/**
 * A plain text form of a result. The server sends the ordinary OutputForm of the result
 * alongside the LaTeX, so nothing has to be reconstructed here.
 */
function plainTextOf(result, value, format) {
	if (result && result.plaintext)
		return result.plaintext;
	if (format == 'latex' || format == 'text' || format == 'code')
		return value == null ? '' : value;
	if (value == null)
		return '';
	if (value.startsWith('<svg') || value.startsWith('<img') ||
			value.startsWith('<iframe') || value.startsWith('<div data-type="webgl"'))
		return '-Graphics-';
	// a fallback only: a result normally carries its OutputForm in result.plaintext
	return value.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').strip();
}

function notebookQueries() {
	return $('queries');
}

// ---------------------------------------------------------------- notice bar

function showNotebookNotice(text) {
	hideNotebookNotice();
	var notice = $E('div', {'id': 'notebookNotice'},
		$E('a', {'class': 'close', 'href': 'javascript:hideNotebookNotice()', 'title': "Dismiss"},
			$T(String.fromCharCode(215))),
		$T(text));
	$('queriesContainer').insertBefore(notice, notebookQueries());
}

function hideNotebookNotice() {
	var notice = $('notebookNotice');
	if (notice)
		notice.deleteElement();
}

// ---------------------------------------------------------------- text cells

/**
 * Create a Markdown or raw cell. It shows its rendered form and turns into a source
 * textarea when clicked; Shift+Return or leaving the textarea renders it again.
 */
function createTextCell(before, source, cellType, metadata) {
	var li, ul, textarea, view, moveHandle, deleteHandle;
	// Items need id in order for Sortable.onUpdate to work.
	li = $E('li', {'id': 'query_' + queryIndex++, 'class': 'query textcell'},
		ul = $E('ul', {'class': 'query'},
			$E('li', {'class': 'request'},
				view = $E('div', {'class': 'textview'}),
				// starts hidden, so that a cell being rendered does not show its source first
				textarea = $E('textarea',
					{'class': 'textsource', 'spellcheck': 'false', 'style': 'display: none'})
			)
		),
		moveHandle = $E('span', {'class': 'move'}),
		deleteHandle = $E('span', {'class': 'delete', 'title': "Delete"}, $T(String.fromCharCode(215)))
	);
	li.isTextCell = true;
	li.cellType = cellType == 'raw' ? 'raw' : 'markdown';
	li.cellMetadata = metadata || {};
	li.textarea = textarea;
	li.ul = ul;
	li.view = view;
	textarea.li = li;
	textarea.ul = ul;
	textarea.value = source || '';
	view.li = li;
	moveHandle.li = li;
	deleteHandle.li = li;

	if (before)
		notebookQueries().insertBefore(li, before);
	else
		notebookQueries().appendChild(li);

	view.observe('click', function(event) {
		// let a link inside the rendered Markdown be followed instead of starting an edit
		var element = Event.element(event);
		if (element && element.tagName == 'A')
			return;
		editTextCell(li);
	});
	textarea.observe('blur', function() {
		renderTextCell(li);
	});
	textarea.observe('keydown', function(event) {
		var isReturn = event.keyCode == Event.KEY_RETURN || event.key == 'Enter';
		if (isReturn && (event.shiftKey || event.location == 3)) {
			event.stop();
			renderTextCell(li);
		}
	});
	deleteHandle.observe('click', deleteClick.bindAsEventListener(deleteHandle));
	deleteHandle.observe('mousedown', deleteMouseDown.bindAsEventListener(deleteHandle));
	moveHandle.observe('mousedown', moveMouseDown.bindAsEventListener(moveHandle));
	moveHandle.observe('mouseup', moveMouseUp.bindAsEventListener(moveHandle));
	li.observe('mousedown', queryMouseDown.bindAsEventListener(li));

	return li;
}

function refreshTextCellSize(textarea) {
	var lines = textarea.value.split('\n').length;
	textarea.rows = Math.max(3, lines + 1);
}

function editTextCell(li) {
	li.view.hide();
	li.textarea.show();
	refreshTextCellSize(li.textarea);
	li.addClassName('editing');
	window.setTimeout(function() {
		li.textarea.focus();
	}, 10);
}

/** Show the rendered form of a text cell. Markdown is rendered by the server. */
function renderTextCell(li, onDone) {
	li.removeClassName('editing');
	li.textarea.hide();
	li.view.show();
	var source = li.textarea.value;
	if (source.strip() == '') {
		li.view.update('<p class="placeholder">Text cell &ndash; click to edit</p>');
		if (onDone)
			onDone();
		return;
	}
	if (li.cellType == 'raw') {
		li.view.update('<pre>' + escapeHTML(source) + '</pre>');
		if (onDone)
			onDone();
		return;
	}
	renderMarkdown([source], function(html) {
		setTextCellHTML(li, html[0]);
		if (onDone)
			onDone();
	});
}

function setTextCellHTML(li, html) {
	li.view.update(html == null ? '' : html);
	// a Markdown cell may contain math
	symjaRenderMathIn(li.view);
}

/**
 * Render Markdown sources to HTML on the server, which escapes raw HTML and sanitizes
 * link URLs. On failure the sources are shown verbatim rather than as unchecked markup.
 */
function renderMarkdown(sources, onSuccess) {
	function fallback() {
		onSuccess(sources.map(function(source) {
			return '<pre>' + escapeHTML(source) + '</pre>';
		}));
	}
	new Ajax.Request('/ajax/notebook/', {
		method: 'post',
		parameters: {
			markdown: Object.toJSON(sources)
		},
		onSuccess: function(transport) {
			var response;
			try {
				response = transport.responseText.evalJSON();
			} catch (e) {
				fallback();
				return;
			}
			if (response && response.html)
				onSuccess(response.html);
			else
				fallback();
		},
		onFailure: fallback
	});
}

/** Append a new Markdown cell and start editing it. */
function insertTextCell() {
	if (!notebookQueries())
		return;
	if ($('welcomeContainer'))
		$('welcomeContainer').hide();
	var li = createTextCell(null, '', 'markdown');
	createSortable();
	editTextCell(li);
}

// ---------------------------------------------------------------- writing a notebook

/**
 * The mime bundle for one value. A LaTeX result is stored as "text/latex", which Jupyter
 * and nbviewer render themselves; the HTML forms are the graphics, which stay "text/html".
 */
function outputData(result, value, format) {
	var data = {};
	if (format == 'latex') {
		data['text/latex'] = sourceLines('$$' + value + '$$');
	} else if (format != 'text' && format != 'code') {
		// MathML and the graphics snippets are both HTML as far as a notebook is concerned
		data['text/html'] = sourceLines(value);
	}
	data['text/plain'] = sourceLines(plainTextOf(result, value, format));
	return data;
}

function resultsToOutputs(results, executionCount) {
	var outputs = [];
	results.each(function(result) {
		if (result.out) {
			result.out.each(function(out) {
				if (out.message) {
					var prefix = out.prefix ? out.prefix + ': ' : '';
					outputs.push({
						'output_type': 'stream',
						'name': 'stderr',
						'text': sourceLines(prefix + plainTextOf(null, out.text, out.format))
					});
				} else {
					outputs.push({
						'output_type': 'display_data',
						'metadata': {},
						'data': outputData(null, out.text, out.format)
					});
				}
			});
		}
		if (result.result != null && result.result !== '') {
			outputs.push({
				'output_type': 'execute_result',
				'execution_count': executionCount,
				'metadata': {},
				'data': outputData(result, result.result, result.format)
			});
		}
	});
	return outputs;
}

function buildNotebook() {
	var cells = [];
	var executionCount = 0;
	notebookQueries().childElements().each(function(li) {
		if (li.isTextCell) {
			var cell = {
				'id': newCellId(),
				'cell_type': li.cellType,
				'metadata': li.cellMetadata || {},
				'source': sourceLines(li.textarea.value)
			};
			// a raw cell has neither outputs nor an execution count in nbformat 4
			cells.push(cell);
			return;
		}
		var textarea = li.select('textarea.request')[0];
		if (!textarea)
			return;
		var value = textarea.value;
		if (value.strip() == '' && !textarea.submitted)
			return; // the trailing empty input is not a cell
		var count = null;
		var outputs = [];
		if (textarea.results) {
			count = ++executionCount;
			outputs = resultsToOutputs(textarea.results, count);
		}
		cells.push({
			'id': newCellId(),
			'cell_type': 'code',
			'execution_count': count,
			'metadata': {},
			'source': sourceLines(value),
			'outputs': outputs
		});
	});
	return {
		'cells': cells,
		'metadata': notebookMetadata(),
		'nbformat': NBFORMAT,
		'nbformat_minor': NBFORMAT_MINOR
	};
}

function showSaveNotebook() {
	if (!notebookQueries())
		return;
	// a text cell that is being edited must contribute its current source
	notebookQueries().childElements().each(function(li) {
		if (li.isTextCell && li.hasClassName('editing'))
			renderTextCell(li);
	});
	var input = $('id_name');
	if (input && !input.value.strip())
		input.value = notebookFileName || 'symja.ipynb';
	showPopup($('save'));
}

function cancelSaveNotebook() {
	hidePopup();
}

function saveNotebook() {
	var name = $('id_name').value.strip();
	if (name == '')
		name = 'symja.ipynb';
	if (!name.endsWith('.ipynb'))
		name += '.ipynb';
	hidePopup();
	notebookFileName = name;
	downloadNotebook(name, JSON.stringify(buildNotebook(), null, 1));
}

function downloadNotebook(name, text) {
	var blob = new Blob([text], {type: 'application/x-ipynb+json'});
	var url = URL.createObjectURL(blob);
	var link = document.createElement('a');
	link.href = url;
	link.download = name;
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
	window.setTimeout(function() {
		URL.revokeObjectURL(url);
	}, 10000);
}

// ---------------------------------------------------------------- reading a notebook

function openNotebook() {
	if (!notebookQueries())
		return;
	if (!notebookFileInput) {
		notebookFileInput = $E('input', {
			'type': 'file',
			'id': 'notebookFile',
			'accept': '.ipynb,application/x-ipynb+json',
			'style': 'display: none'
		});
		document.body.appendChild(notebookFileInput);
		notebookFileInput.observe('change', notebookFileChosen);
	}
	// so that picking the same file twice in a row fires the change event again
	notebookFileInput.value = '';
	notebookFileInput.click();
}

function notebookFileChosen(event) {
	var file = notebookFileInput.files[0];
	if (!file)
		return;
	var reader = new FileReader();
	reader.onload = function() {
		var notebook;
		try {
			notebook = JSON.parse(reader.result);
		} catch (e) {
			window.alert("'" + file.name + "' is not a valid notebook: " + e.message);
			return;
		}
		loadNotebook(notebook, file.name);
	};
	reader.onerror = function() {
		window.alert("Cannot read '" + file.name + "'.");
	};
	reader.readAsText(file);
}

/**
 * Show a notebook in the page. Nothing is evaluated - see the note at the top of this file.
 *
 * @param onDone called with true if cells were inserted, false if the user cancelled
 */
function loadNotebook(notebook, fileName, onDone) {
	function done(loaded) {
		if (onDone)
			onDone(loaded);
	}
	if (!notebook || !Object.isArray(notebook.cells)) {
		window.alert("'" + fileName + "' has no cells - it does not look like a Jupyter notebook.");
		done(false);
		return;
	}
	if (notebook.nbformat && notebook.nbformat != NBFORMAT) {
		window.alert("'" + fileName + "' uses nbformat " + notebook.nbformat +
			"; this page reads nbformat " + NBFORMAT + ". Some cells may not be shown correctly.");
	}

	var syntax = notebook.metadata && notebook.metadata.symja ?
		notebook.metadata.symja.syntax : null;
	if (syntax && syntax != symjaSyntax()) {
		showDialog("Different input syntax",
			"'" + fileName + "' was saved with " + symjaSyntaxName(syntax) + ", but this page uses " +
			symjaSyntaxName(symjaSyntax()) + ". Its inputs may not parse here.",
			"Open anyway", "Cancel",
			function() {
				insertNotebook(notebook, fileName, onDone);
			},
			function() {
				done(false);
			});
		return;
	}
	insertNotebook(notebook, fileName, onDone);
}

function insertNotebook(notebook, fileName, onDone) {
	notebookFileName = fileName;
	hideNotebookNotice();
	notebookQueries().deleteChildNodes();
	if ($('welcomeContainer'))
		$('welcomeContainer').hide();
	lastFocus = null;

	var codeCells = 0;
	var droppedOutputs = 0;
	var skipped = 0;
	var pendingCells = [];
	var pendingSources = [];

	notebook.cells.each(function(cell) {
		var source = cellSource(cell);
		if (cell.cell_type == 'code') {
			var li = createQuery(null, true, true);
			li.textarea.value = source;
			li.addClassName('notrun');
			codeCells++;
			if (cell.outputs && cell.outputs.length > 0)
				droppedOutputs++;
		} else if (cell.cell_type == 'markdown' || cell.cell_type == 'raw') {
			var textLi = createTextCell(null, source, cell.cell_type, cell.metadata);
			if (textLi.cellType == 'markdown') {
				pendingCells.push(textLi);
				pendingSources.push(source);
			} else {
				renderTextCell(textLi);
			}
		} else {
			skipped++;
		}
	});

	if (notebookQueries().childElements().length == 0)
		createQuery(null, true, true);

	createSortable();
	refreshInputSizes();

	if (pendingSources.length > 0) {
		renderMarkdown(pendingSources, function(html) {
			for (var index = 0; index < pendingCells.length; ++index) {
				var li = pendingCells[index];
				if (pendingSources[index].strip() == '')
					li.view.update('<p class="placeholder">Text cell &ndash; click to edit</p>');
				else
					setTextCellHTML(li, html[index]);
			}
		});
	}

	var notice = "Opened '" + fileName + "' with " + codeCells +
		(codeCells == 1 ? " code cell" : " code cells") + ".";
	if (droppedOutputs > 0)
		notice += " Saved outputs were not restored.";
	notice += " Nothing has been evaluated yet - press Run all to evaluate the notebook in this session.";
	if (skipped > 0)
		notice += " " + skipped + " cell(s) of an unknown type were skipped.";
	showNotebookNotice(notice);

	if (onDone)
		onDone(true);
}

/**
 * Load the notebook that was named with the -notebook command line argument, if any.
 *
 * @param onDone called with true if a notebook was shown, false otherwise
 */
function loadStartupNotebook(onDone) {
	new Ajax.Request('/ajax/notebook/', {
		method: 'get',
		onSuccess: function(transport) {
			var response;
			try {
				response = transport.responseText.evalJSON();
			} catch (e) {
				onDone(false);
				return;
			}
			if (response && response.error)
				window.alert(response.error);
			if (response && response.notebook)
				loadNotebook(response.notebook, response.name || 'notebook.ipynb', onDone);
			else
				onDone(false);
		},
		onFailure: function() {
			onDone(false);
		}
	});
}

// ---------------------------------------------------------------- run all

var runningAll = false;

/**
 * Evaluate every code cell from top to bottom. This has to be sequential: the engine keeps
 * its definitions per session, so a later cell may depend on an earlier one.
 */
function runAllCells() {
	if (runningAll || !notebookQueries())
		return;
	var textareas = [];
	notebookQueries().childElements().each(function(li) {
		if (li.isTextCell)
			return;
		var textarea = li.select('textarea.request')[0];
		if (textarea && textarea.value.strip() != '')
			textareas.push(textarea);
	});
	if (textareas.length == 0)
		return;

	hideNotebookNotice();
	runningAll = true;
	$('logo').addClassName('working');

	function step(index) {
		if (index >= textareas.length) {
			runningAll = false;
			$('logo').removeClassName('working');
			return;
		}
		submitQuery(textareas[index], function() {
			step(index + 1);
		});
	}
	step(0);
}
