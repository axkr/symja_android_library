/**
    Symja: a general-purpose computer algebra system

    Live Dynamic cells.

    A Manipulate is one widget: its controls and the body they drive are the same object, and
    everything about it stays inside its own cell. A Dynamic is not. The two halves of

        In[1] := x = 0.5; Slider[Dynamic[x]]
        In[2] := Dynamic[x]

    are separate cells that know nothing about each other; what relates them is that both
    mention x, and x is one symbol of one session on the server.

    So a cell here does not own its state. It posts what the user did to /ajax/dynamic/, and the
    answer names every cell of the page that has to be redrawn - which is regularly not the one
    that posted. Each cell listed in the answer is replaced, and the rest are left alone.
**/

/** Milliseconds a burst of moves on one control is collected into a single write. */
var DYNAMIC_DEBOUNCE = 120;

/** Every live cell on the page, by its server side id. */
var dynamicCells = {};

/** The session's write count as the page last saw it, so a cell can catch up after a gap. */
var dynamicGeneration = 0;

function createDynamic(spec) {
	var cell = {
		id: spec.id,
		controls: spec.controls || [],
		widgets: {},
		bindings: {},
		// the panel builders in manipulate.js read these, so a cell looks enough like a widget
		// for the very same code to build a slider, a picker or a colour well for it
		options: {continuousAction: true, animationRate: 1.0, animationDirection: 1},
		playing: false,
		timer: null,
		pending: 0,
		applied: 0,
		scheduled: false,
		inflight: false,
		refreshTimer: null
	};
	dynamicCells[cell.id] = cell;
	if (spec.generation != null)
		dynamicGeneration = spec.generation;

	cell.dom = $E('div', {'class': 'dynamiccell'});
	cell.output = $E('div', {'class': 'dynamicoutput'});
	cell.dom.appendChild(cell.output);

	showDynamicBody(cell, spec.body);

	// UpdateInterval -> t asks to be looked at again on a timer whether or not anything the
	// cell mentions has changed - a clock, a counter, something outside the session's symbols
	if (spec.updateInterval > 0) {
		cell.refreshTimer = window.setInterval(function() {
			postDynamic(cell, {refresh: 1});
		}, Math.max(100, Math.round(spec.updateInterval * 1000)));
	}

	return cell.dom;
}

// ---------------------------------------------------------------- rendering

/**
 * Put a cell's rendering on screen, then give it back the controls it draws.
 *
 * The new content is built first and swapped in whole, so the cell never goes blank between two
 * renderings.
 */
function showDynamicBody(cell, body) {
	if (!body || !body.results)
		return;
	var replacement = $E('div', {'class': 'dynamicoutput'});
	body.results.each(function(result) {
		if (result.out) {
			result.out.each(function(out) {
				var li = $E('div', {'class': out.message ? 'dynamicmessage' : 'dynamicprint'});
				if (out.message)
					li.appendChild($T(out.prefix + ': '));
				li.appendChild(createLine(out.text, out.format));
				replacement.appendChild(li);
			});
		}
		if (result.result != null && result.result !== '')
			replacement.appendChild(createLine(result.result, result.format));
	});
	cell.dom.replaceChild(replacement, cell.output);
	cell.output = replacement;
	wireDynamicControls(cell);
	wireDynamicButtons(cell);
}

/**
 * Build the widget for every control the cell draws.
 *
 * A control already on screen is moved into the new rendering rather than rebuilt: a cell is
 * redrawn on every change, and rebuilding a slider under the pointer would drop the drag that
 * is moving it. Only its value is brought up to date - and it is the server's value, which for
 * a Dynamic with a setter need not be the one that was sent.
 */
function wireDynamicControls(cell) {
	var descriptors = cell.controls || [];
	cell.output.select('span.symjacontrol').each(function(span) {
		var index = parseInt(span.readAttribute('data-control'), 10);
		var descriptor = descriptors[index];
		if (descriptor == null)
			return;
		var control = cell.widgets[index];
		if (control && manipulateSameShape(control, descriptor)) {
			setManipulateControlValue(cell, control, descriptor.value);
		} else {
			control = Object.clone(descriptor);
			control.dynamicIndex = index;
			cell.bindings[control.name] = control.value;
			// the panel builders read and write cell.bindings and call manipulateChanged when
			// the user moves something, which is why a cell carries the same two fields a
			// Manipulate widget does
			control.element = buildManipulateWidget(cell, control);
			cell.widgets[index] = control;
		}
		if (control.element) {
			span.appendChild(control.element);
			if (control.readOnly)
				control.element.select('input, select, button').each(function(input) {
					input.disabled = true;
				});
		}
	});
}

/** Make the Button elements of a cell clickable; their code never leaves the server. */
function wireDynamicButtons(cell) {
	cell.output.select('span.symjabutton').each(function(button) {
		var action = button.readAttribute('data-action');
		if (action == null)
			return;
		button.addClassName('active');
		button.observe('click', function() {
			postDynamic(cell, {action: parseInt(action, 10)});
		});
	});
}

// ---------------------------------------------------------------- updating

/**
 * A control of a live cell was moved. Only the newest position is worth sending, so a burst of
 * moves collapses into one write.
 */
function requestDynamic(cell, control) {
	cell.pending++;
	if (cell.scheduled || cell.inflight)
		return;
	cell.scheduled = true;
	window.setTimeout(function() {
		cell.scheduled = false;
		if (cell.applied != cell.pending)
			postDynamic(cell, {
				control: control.dynamicIndex,
				value: Object.toJSON(cell.bindings[control.name])
			});
	}, DYNAMIC_DEBOUNCE);
}

function postDynamic(cell, parameters) {
	cell.inflight = true;
	cell.applied = cell.pending;
	parameters = Object.extend({id: cell.id, poll: dynamicGeneration}, parameters || {});

	new Ajax.Request('/ajax/dynamic/', {
		method: 'post',
		parameters: parameters,
		onSuccess: function(transport) {
			var response;
			try {
				response = transport.responseText.evalJSON();
			} catch (e) {
				cell.inflight = false;
				return;
			}
			applyDynamicResponse(response);
			cell.inflight = false;
			// a move that arrived while this request was on its way
			if (cell.applied != cell.pending)
				postDynamic(cell, parameters);
		},
		onFailure: function() {
			cell.inflight = false;
		}
	});
}

/**
 * Redraw the cells the server says have changed. This is where a write in one cell reaches the
 * other cells of the page: the answer names them, and each one is replaced where it stands.
 */
function applyDynamicResponse(response) {
	if (response.generation != null)
		dynamicGeneration = response.generation;
	if (!response.cells)
		return;
	$H(response.cells).each(function(pair) {
		var cell = dynamicCells[pair.key];
		if (!cell || pair.value == null)
			return;
		cell.controls = pair.value.controls || [];
		showDynamicBody(cell, pair.value.body);
	});
}

/**
 * Release a cell whose output the user deleted: the server drops it, and the timer it may own
 * is stopped here.
 */
function disposeDynamic(id) {
	var cell = dynamicCells[id];
	if (!cell)
		return;
	if (cell.refreshTimer) {
		window.clearInterval(cell.refreshTimer);
		cell.refreshTimer = null;
	}
	stopManipulateAnimation(cell);
	delete dynamicCells[id];
	new Ajax.Request('/ajax/dynamic/', {
		method: 'post',
		parameters: {id: id, dispose: 1}
	});
}

/** Release every live cell inside an element that is about to be removed. */
function disposeDynamicsIn(element) {
	if (!element || typeof element.select != 'function')
		return;
	element.select('div.dynamiccell').each(function(dom) {
		$H(dynamicCells).each(function(pair) {
			if (pair.value.dom == dom)
				disposeDynamic(pair.key);
		});
	});
}
