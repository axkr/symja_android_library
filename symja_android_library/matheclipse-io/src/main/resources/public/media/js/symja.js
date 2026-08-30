var deleting;
var blurredElement;

var movedItem;

var clickedQuery;

var lastFocus = null;

function getLetterWidth(element) {
	var letter = $E('span', $T('m'));
	letter.setStyle({
		fontFamily: element.getStyle('font-family'),
		fontSize: element.getStyle('font-size')
	});
	var parent = $$('body')[0];
	parent.appendChild(letter);
	var width = letter.getWidth();
	parent.removeChild(letter);
	delete letter;
	return width;
}

function refreshInputSize(textarea) {
	var letterWidth = getLetterWidth(textarea);
	var width = textarea.getWidth() - 15;
	var lines = textarea.value.split('\n');
	var lineCount = 0;
	for (var index = 0; index < lines.length; ++index) {
		var line = lines[index];
		lineCount += Math.ceil(1.0 * (line.length + 1) * letterWidth / width);
	}
	textarea.rows = lineCount;
}

function refreshInputSizes() {
	$$('textarea.request').each(function(textarea) {
		refreshInputSize(textarea);
	});
	
	$$('#queries ul').each(function(ul) {
		afterProcessResult(ul, 'Rerender');		
	});
}

function inputChange(event) {
	refreshInputSize(this);
}

function isEmpty(textarea) {
	return textarea.value.strip() == '' && !textarea.submitted;
}

function prepareText(text) {
	if (text == '') {
		text = String.fromCharCode(160);
	}
	return text;
	
	/*
	// Place &shy; between every two characters.
	// Problem: Copy & paste yields weird results!
	var result = '';
	for (var index = 0; index < text.length; ++index) {
		result += text.charAt(index);
		if (index < text.length - 1)
			result += String.fromCharCode(173); // &shy;
	}
	return result;
	*/
}

function createLine(value, format) {
	if (format == 'manipulate') {
		// handled by setResult, which has the whole result object; never reached
		return $E('div');
	} else if (format == 'mathml') {
		// every browser this page supports renders MathML itself, so the markup only has to
		// be put into the document
		var dom = $E('div', {'class': 'mathmlresult'});
		dom.updateDOM(value);
		return dom;
	} else if (format == 'latex') {
		// KaTeX renders synchronously, so the result is complete when this returns
		return symjaRenderTeX(value, false);
	} else if (format == 'code') {
		var pre = $E('pre', {'class': 'codemessage'});
		pre.appendChild($T(value));
		return pre;
	} else if (format == 'text') {
		var lines = value.split('\n');
		var p = $E('p');
		for (var index = 0; index < lines.length; ++index) {
			p.appendChild($T(prepareText(lines[index])));
			if (index < lines.length - 1)
				p.appendChild($E('br'));
		}
		return p;
	} else if (value.startsWith('<svg')) {
		var dom = document.createElement('div');
		// The svg carries its own size and a viewBox, and asks to be scaled with
		// max-width:100%; height:auto. Giving the wrapper a fixed box fought that: a plot
		// taller than the box was simply cut off, which is what happened to every square
		// one - MatrixPlot, ArrayPlot, DensityPlot - while a wide one such as Plot fitted
		// inside it and looked fine. A max width and no height lets the graphic size itself.
		dom.setAttribute('style', 'max-width: 600px; margin: 0; padding: 0');
		dom.update(value);
		return dom;
	} else if (value.startsWith('<img')) {
		var dom = document.createElement('div');
		// A raster carries max-width:100%; height:auto of its own, so it only needs a wrapper
		// that can narrow with the output column. It used to be delivered inside an iframe,
		// whose fixed height made anything taller scroll within its own little window.
		dom.setAttribute('style', 'max-width: 600px; margin: 0; padding: 0');
		dom.update(value);
		return dom;
	} else if (value.startsWith('<iframe')) {
		var dom = document.createElement('div'); 
		dom.setAttribute('id', 'mathcell');
		// an iframe needs a height of its own, but it must still be allowed to narrow with the
		// output column rather than running off the side of it
		dom.setAttribute('style', 'width: 600px; max-width: 100%; height: 440px; margin: 0; padding: 0');
		dom.update(value); 
		return dom;
	} else if (value.startsWith('<div data-type="webgl"')) {
        var dom = document.createElement('div');
        // The script inside 'value' will be executed by Prototype.js here
        dom.update(value); 
        return dom;
	} else {
		var dom = document.createElement('div');
		dom.updateDOM(value);
		return dom;
	}
}

/**
 * Nothing left to do once the lines are in the document: KaTeX has already rendered them
 * by the time createLine() returned. This used to drive the MathJax typesetting queue.
 */
function afterProcessResult(ul, command) {
}

function setResult(ul, results) {
	results.each(function(result) {
		var resultUl = $E('ul', {'class': 'out'});
		result.out.each(function(out) {
			var li = $E('li', {'class': (out.message ? 'message' : 'print')});
			if (out.message)
				li.appendChild($T(out.prefix + ': '));
			li.appendChild(createLine(out.text, out.format));
			resultUl.appendChild(li);
		});
		if (result.format == 'manipulate' && result.manipulate) {
			var li = $E('li', {'class': 'result'}, createManipulate(result.manipulate));
			resultUl.appendChild(li);
		} else if (result.format == 'dynamic' && result.dynamic) {
			var li = $E('li', {'class': 'result'}, createDynamic(result.dynamic));
			resultUl.appendChild(li);
		} else if (result.result != null) {
			var li = $E('li', {'class': 'result'}, createLine(result.result, result.format));
			resultUl.appendChild(li);
		}
		ul.appendChild($E('li', {'class': 'out'}, resultUl));
	});
	afterProcessResult(ul);
}

function submitQuery(textarea, onfinish) {
	$('welcomeContainer').fade({duration: 0.5});
	
	textarea.li.addClassName('loading');
	// the cell was loaded from a notebook and is being evaluated now, so it is no longer
	// showing a request that this session has not seen
	textarea.li.removeClassName('notrun');
	new Ajax.Request('/ajax/query/', {
		method: 'post',
		parameters: {
			query: textarea.value
		},
		onSuccess: function(transport) {
			textarea.ul.select('li[class!=request][class!=submitbutton]').invoke('deleteElement');
			if (!transport.responseText) {
				// A fatal Java error has occured, e.g. on 4.4329408320439^43214234345
				// ("Fatal Java error: mp_reallocate failure")
				// -> print overflow message
				transport.responseText = '{"results": [{"out": [{"prefix": "General::noserver", "message": true, "tag": "noserver", "symbol": "General", "text": "<math><mrow><mtext>No server running.</mtext></mrow></math>"}]}]}';
			}
			var response = transport.responseText.evalJSON();
			setResult(textarea.ul, response.results);
			textarea.submitted = true;
			textarea.results = response.results;
			var next = textarea.li.nextSibling;
			if (next)
				next.textarea.focus();
			else
				createQuery();
		},
		onFailure: function(transport) {
			textarea.ul.select('li[class!=request]').invoke('deleteElement');
			var li = $E('li', {'class': 'serverError'}, $T("Sorry, an error occurred while processing your request!"));
			textarea.ul.appendChild(li);					
			textarea.submitted = true;
		},
		onComplete: function() {
			textarea.li.removeClassName('loading');
			if (onfinish)
				onfinish();
		}
	});
}

function getSelection() {
	// TODO
}

function keyDown(event) {
	var element = Event.element(event);
	if (element && element.tagName == 'TEXTAREA' && $(element).hasClassName('textsource'))
		// a Markdown or raw cell brings its own key handling
		return;
	var textarea = lastFocus;
	if (!textarea)
		return;
	refreshInputSize(textarea);
	
	if (event.keyCode == Event.KEY_RETURN && (event.shiftKey || event.location == 3)) {
		if (!Prototype.Browser.IE)
			event.stop();
		
		var query = textarea.value.strip();
		if (query) {
			submitQuery(textarea);
		}
	} else if (event.keyCode == Event.KEY_UP) {
		if (textarea.selectionStart == 0 && textarea.selectionEnd == 0) {
			if (isEmpty(textarea)) {
				if (textarea.li.previousSibling)
					textarea.li.previousSibling.textarea.focus();
			} else
				createQuery(textarea.li);
		}
	} else if (event.keyCode == Event.KEY_DOWN) {
		if (textarea.selectionStart == textarea.value.length && textarea.selectionEnd == textarea.selectionStart) {
			if (isEmpty(textarea)) {
				if (textarea.li.nextSibling)
					textarea.li.nextSibling.textarea.focus();
			} else
				createQuery(textarea.li.nextSibling);
		}
	} // else
	//	if (isGlobalKey(event))
	//		event.stop();
}

function deleteMouseDown(event) {
	if (event.isLeftClick())
		deleting = true;
}

function deleteClick(event) {
	// a Manipulate or a live Dynamic in this cell has state on the server; let it go before
	// the cell does
	if (typeof disposeManipulatesIn == 'function')
		disposeManipulatesIn(this.li);
	if (typeof disposeDynamicsIn == 'function')
		disposeDynamicsIn(this.li);
	if (lastFocus == this.li.textarea)
		lastFocus = null;
	this.li.deleteElement();
	deleting = false;
	if (blurredElement) {
		blurredElement.focus();
		blurredElement = null;
	}
	if ($('queries').childElements().length == 0)
		createQuery();
}

function moveMouseDown(event) {
	movedItem = this.li;
	movedItem.addClassName('moving');
}

function moveMouseUp(event) {
	if (movedItem) {
		movedItem.removeClassName('moving');
		movedItem.textarea.focus();
		movedItem = null;
	}
}

function onFocus(event) {
	var textarea = this;
	textarea.li.addClassName('focused');
	lastFocus = textarea;
}

function onBlur(event) {
	var textarea = this;
	blurredElement = textarea;
	if (!deleting && textarea.li != movedItem && isEmpty(textarea) && $('queries').childElements().length > 1) {
		textarea.li.hide();
		if (textarea == lastFocus)
			lastFocus = null;
		window.setTimeout(function() {
			textarea.li.deleteElement();
		}, 10);
	}
	textarea.li.removeClassName('focused');
}

function createSortable() {
	Position.includeScrollOffsets = true;
  Sortable.create('queries', {
    handle: 'move',
    scroll: 'document',
    scrollSensitivity: 1	// otherwise strange flying-away of item at top
  });
}

var queryIndex = 0;

function createQuery(before, noFocus, updatingAll) {
	var ul, textarea, moveHandle, deleteHandle, submitButton;
	// Items need id in order for Sortable.onUpdate to work.
	var li = $E('li', {'id': 'query_' + queryIndex++, 'class': 'query'},
		ul = $E('ul', {'class': 'query'},
			$E('li', {'class': 'request'},
			    textarea = $E('textarea', {'class': 'request', 'spellcheck': 'false'}),
				$E('span', {'class': 'submitbutton', 'title': "Submit [Shift+Return]"},
							submitButton = $E('span', $T('='))
				)
			)
		),
		moveHandle = $E('span', {'class': 'move'}),
		deleteHandle = $E('span', {'class': 'delete', 'title': "Delete"}, $T(String.fromCharCode(215)))
	);
	textarea.rows = 1;
	textarea.ul = ul;
	textarea.li = li;
	textarea.submitted = false;
	moveHandle.li = li;
	deleteHandle.li = li;
	li.textarea = textarea;
	li.ul = ul;
	if (before)
		$('queries').insertBefore(li, before);
	else
		$('queries').appendChild(li);	
	if (!updatingAll)
		refreshInputSize(textarea);
	new Form.Element.Observer(textarea, 0.2, inputChange.bindAsEventListener(textarea));
	textarea.observe('focus', onFocus.bindAsEventListener(textarea));
	textarea.observe('blur', onBlur.bindAsEventListener(textarea));
	li.observe('mousedown', queryMouseDown.bindAsEventListener(li));
	deleteHandle.observe('click', deleteClick.bindAsEventListener(deleteHandle));
	deleteHandle.observe('mousedown', deleteMouseDown.bindAsEventListener(deleteHandle));
	moveHandle.observe('mousedown', moveMouseDown.bindAsEventListener(moveHandle));
	moveHandle.observe('mouseup', moveMouseUp.bindAsEventListener(moveHandle));
	$(document).observe('mouseup', moveMouseUp.bindAsEventListener($(document)));
	submitButton.observe('mousedown', function() {
		if (textarea.value.strip())
			submitQuery(textarea);
		else
			window.setTimeout(function() {
				textarea.focus();
			}, 10);
	});
	if (!updatingAll) {
		createSortable();
		// calling directly fails in Safari on document loading
		//window.setTimeout(createSortable, 10);
	}
	// Immediately setting focus doesn't work in IE.
	if (!noFocus)
		window.setTimeout(function() {
			textarea.focus();
		}, 10);
	return li;
}

var mouseDownEvent = null;

function documentMouseDown(event) {
	if (event.isLeftClick()) {
		if (clickedQuery) {
			clickedQuery = null;
			mouseDownEvent = null;
			return;
		}
		event.stop(); // strangely, doesn't work otherwise
		mouseDownEvent = event;
	}
}

function documentClick(event) {
	// In Firefox, mousedown also fires when user clicks scrollbars.
	// -> listen to click
	event = mouseDownEvent;
	if (!event)
		return;
	var first = $('queries').childElements()[0];
	if ($('queries').childElements().length == 1 && first && !first.isTextCell &&
			isEmpty(first.textarea)) {
		first.textarea.focus();
		return;
	}
	var offset = $('document').cumulativeOffset();
	var y = event.pointerY() - offset.top + $('document').scrollTop;
	var element = null;
	$('queries').childElements().each(function(li) {
		var offset = li.positionedOffset(); // margin-top: 10px
		if (offset.top + 20 > y) {
			element = li;
			throw $break;
		}
	});
	createQuery(element);
}

function queryMouseDown(event) {
	clickedQuery = this;
}

function focusLast() {
	if (lastFocus)
		lastFocus.focus();
	else
		createQuery();
}

function isGlobalKey(event) {
	if (event.ctrlKey) {
		switch(event.keyCode) {
		case 68:
		case 67:
		case 83:
		case 79:
			return true;
		}
	}
	return false;
}

function globalKeyUp(event) {
	if (!popup && event.ctrlKey) {
		switch (event.keyCode) {
		case 68: // D
			$('search').select();
			event.stop();
			break;
		case 67: // C
			focusLast();
			event.stop();
			break;
		case 83: // S
			showSave();
			break;
		case 79: // O
			showOpen();
			break;
		}
	}
}

function domLoaded() {
	// KaTeX needs no startup configuration; see symja_katex.js for the options

	if ($('welcomeBrowser'))
		if (!(Prototype.Browser.WebKit || Prototype.Browser.MobileSafari || Prototype.Browser.Gecko))
			$('welcomeBrowser').show();
	
	$$('body')[0].observe('resize', refreshInputSizes);
	
	if ($('queriesContainer')) {
		$('queriesContainer').appendChild($E('ul', {'id': 'queries'}));
		
		$('document').observe('mousedown', documentMouseDown.bindAsEventListener($('document')));
		$('document').observe('click', documentClick.bindAsEventListener($('document')));
		
		$(document).observe('keydown', keyDown.bindAsEventListener());
		if (Prototype.Browser.IE) {
			document.body.addEventListener('keydown', function(event) {
				if (event.keyCode == Event.KEY_RETURN && event.shiftKey) {
					event.stopPropagation();
					event.preventDefault();
					keyDown(event);
				}
			}, true);
		}
		if (Prototype.Browser.Opera || Prototype.Browser.IE) {
			// Opera needs another hook so it doesn't insert newlines after Shift+Return
			$(document).observe('keypress', function(event) {
				if (event.keyCode == Event.KEY_RETURN && event.shiftKey)
					event.stop();
			}.bindAsEventListener());
		}
		
//		$(document).observe('keyup', globalKeyUp.bindAsEventListener($('document')));
		
		if (!loadLink()) {
			if (typeof loadStartupNotebook == 'function')
				// a notebook named with the -notebook argument is shown, never evaluated
				loadStartupNotebook(function(loaded) {
					if (!loaded)
						createQuery();
				});
			else
				createQuery();
		}
	}
}

$(document).observe('dom:loaded', domLoaded);
// Konqueror won't fire dom:loaded, so we still need body.onload.

window.onresize = refreshInputSizes;
