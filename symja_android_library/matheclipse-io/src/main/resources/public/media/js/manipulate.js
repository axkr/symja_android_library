/**
    Symja: a general-purpose computer algebra system

    The interactive Manipulate widget.

    The body of a Manipulate stays on the server: this file builds the controls from the
    specification the server sent, and every change posts the current control values to
    /ajax/manipulate/ and puts the answer on screen through createLine() - the very same
    renderer that shows a plain result. That is what lets the body be anything at all: a
    plot, a piece of 3D geometry, a matrix, a symbolic expression.
**/

/** Milliseconds a burst of slider moves is collected into one evaluation. */
var MANIPULATE_DEBOUNCE = 120;

/** All widgets on the page, by their server side id. */
var manipulateWidgets = {};

function createManipulate(spec) {
	var widget = {
		id: spec.id,
		controls: spec.controls || [],
		options: spec.options || {},
		bindings: {},
		// generation counters: a burst of moves is coalesced into a single evaluation
		pending: 0,
		applied: 0,
		scheduled: false,
		inflight: false,
		playing: false,
		timer: null
	};
	manipulateWidgets[widget.id] = widget;

	widget.controls.each(function(control) {
		if (control.name)
			widget.bindings[control.name] = control.value;
	});

	var dom = $E('div', {'class': 'manipulate'});
	widget.dom = dom;
	widget.output = $E('div', {'class': 'manipulateoutput'});
	widget.panel = $E('div', {'class': 'manipulatepanel'});

	if (!widget.options.paneled)
		widget.panel.addClassName('nopanel');
	if (widget.options.appearanceNone)
		widget.panel.hide();

	buildManipulateControls(widget);

	var placement = (widget.options.controlPlacement || 'top').toLowerCase();
	dom.addClassName('placement-' + placement);
	if (placement == 'bottom' || placement == 'right') {
		dom.appendChild(widget.output);
		dom.appendChild(widget.panel);
	} else {
		dom.appendChild(widget.panel);
		dom.appendChild(widget.output);
	}

	showManipulateBody(widget, spec.body);

	if (widget.options.animated && widget.options.animationRunning)
		startManipulateAnimation(widget);

	return dom;
}

// ---------------------------------------------------------------- controls

function buildManipulateControls(widget) {
	widget.controls.each(function(control, index) {
		var row = buildManipulateRow(widget, control, index);
		if (row)
			widget.panel.appendChild(row);
	});
}

function buildManipulateRow(widget, control, index) {
	if (control.kind == 'delimiter')
		return $E('div', {'class': 'manipulatedelimiter'});
	if (control.kind == 'heading')
		return $E('div', {'class': 'manipulateheading'}, $T(control.label));
	if (control.kind == 'button') {
		var button = $E('input', {'type': 'button', 'class': 'manipulatebutton',
			'value': control.label});
		button.observe('click', function() {
			postManipulate(widget, index);
		});
		return $E('div', {'class': 'manipulaterow'}, button);
	}

	var row = $E('div', {'class': 'manipulaterow'});
	row.appendChild($E('span', {'class': 'manipulatelabel'}, $T(control.label)));
	var widgetElement = buildManipulateWidget(widget, control);
	if (!widgetElement)
		return null;
	row.appendChild(widgetElement);
	control.row = row;
	return row;
}

function buildManipulateWidget(widget, control) {
	switch (control.kind) {
	case 'slider':
		return buildSlider(widget, control);
	case 'trigger':
		return buildTrigger(widget, control);
	case 'discrete':
		return buildDiscrete(widget, control);
	case 'checkbox':
		return buildCheckbox(widget, control);
	case 'interval':
		return buildInterval(widget, control);
	case 'slider2d':
		return buildSlider2D(widget, control);
	case 'locator':
		return buildLocator(widget, control);
	case 'color':
		return buildColor(widget, control);
	case 'inputfield':
		return buildInputField(widget, control);
	}
	return null;
}

/** A range input plus the read-out of its current value. */
function buildSlider(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var input = $E('input', {'type': 'range', 'class': 'manipulateslider',
		'min': control.min, 'max': control.max, 'step': control.step});
	input.value = control.value;
	var readout = $E('span', {'class': 'manipulatevalue'}, $T(formatManipulateNumber(control.value)));
	control.input = input;
	control.readout = readout;

	input.observe('input', function() {
		var value = parseFloat(input.value);
		widget.bindings[control.name] = value;
		readout.setText(formatManipulateNumber(value));
		// ContinuousAction -> False waits for the mouse button to come up again
		if (widget.options.continuousAction)
			requestManipulate(widget, control.name);
	});
	input.observe('change', function() {
		widget.bindings[control.name] = parseFloat(input.value);
		requestManipulate(widget, control.name);
	});

	box.appendChild(input);
	box.appendChild(readout);
	return box;
}

/** A slider with its own play/pause button. */
function buildTrigger(widget, control) {
	var box = buildSlider(widget, control);
	var play = $E('a', {'class': 'manipulateplay', 'href': 'javascript:;', 'title': "Play"},
		$E('i', {'class': 'fa fa-play'}));
	play.observe('click', function() {
		if (widget.playing)
			stopManipulateAnimation(widget);
		else
			startManipulateAnimation(widget);
		refreshManipulatePlayButtons(widget);
	});
	control.playButton = play;
	box.insertBefore(play, box.firstChild);
	return box;
}

function buildDiscrete(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var labels = control.labels || [];
	var appearance = (control.appearance || '').toLowerCase();
	// a short list of choices is a row of buttons, a long one a drop down. PopupMenu and
	// SetterBar ask for one or the other whatever the length.
	var useSetterBar = appearance == 'setterbar' || appearance == 'radiobuttonbar'
		|| appearance == 'togglerbar' || (appearance != 'popupmenu' && labels.length <= 6);

	if (useSetterBar) {
		var buttons = [];
		labels.each(function(text, index) {
			var button = $E('input', {'type': 'button', 'class': 'manipulatesetter',
				'value': text});
			if (index == control.value)
				button.addClassName('selected');
			button.observe('click', function() {
				widget.bindings[control.name] = index;
				buttons.each(function(other, i) {
					other.setClassName('selected', i == index);
				});
				control.value = index;
				requestManipulate(widget, control.name);
			});
			buttons.push(button);
			box.appendChild(button);
		});
		control.buttons = buttons;
	} else {
		var select = $E('select', {'class': 'manipulateselect'});
		labels.each(function(text, index) {
			var option = $E('option', {'value': index}, $T(text));
			select.appendChild(option);
		});
		select.selectedIndex = control.value;
		select.observe('change', function() {
			widget.bindings[control.name] = select.selectedIndex;
			control.value = select.selectedIndex;
			requestManipulate(widget, control.name);
		});
		control.input = select;
		box.appendChild(select);
	}
	return box;
}

function buildCheckbox(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var input = $E('input', {'type': 'checkbox', 'class': 'manipulatecheckbox'});
	input.checked = control.value ? true : false;
	input.observe('change', function() {
		widget.bindings[control.name] = input.checked;
		requestManipulate(widget, control.name);
	});
	control.input = input;
	box.appendChild(input);
	return box;
}

/** Two range inputs that cannot cross, bound to a {low, high} pair. */
function buildInterval(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var low = $E('input', {'type': 'range', 'class': 'manipulateslider',
		'min': control.min, 'max': control.max, 'step': control.step});
	var high = $E('input', {'type': 'range', 'class': 'manipulateslider',
		'min': control.min, 'max': control.max, 'step': control.step});
	low.value = control.value[0];
	high.value = control.value[1];
	var readout = $E('span', {'class': 'manipulatevalue'},
		$T(formatManipulateNumber(control.value[0]) + ' .. '
			+ formatManipulateNumber(control.value[1])));

	function update() {
		var a = parseFloat(low.value);
		var b = parseFloat(high.value);
		if (a > b) {
			// keep the ends in order rather than letting them swap silently
			a = Math.min(a, b);
			low.value = a;
		}
		widget.bindings[control.name] = [a, b];
		readout.setText(formatManipulateNumber(a) + ' .. ' + formatManipulateNumber(b));
		requestManipulate(widget, control.name);
	}
	low.observe('input', update);
	high.observe('input', update);

	box.appendChild(low);
	box.appendChild(high);
	box.appendChild(readout);
	return box;
}

/** A square pad; clicking or dragging in it sets the {x, y} pair. */
function buildSlider2D(widget, control) {
	var size = 120;
	var box = $E('span', {'class': 'manipulatecontrol'});
	var pad = $E('div', {'class': 'manipulatepad',
		'style': 'width: ' + size + 'px; height: ' + size + 'px'});
	var dot = $E('div', {'class': 'manipulatedot'});
	pad.appendChild(dot);
	var readout = $E('span', {'class': 'manipulatevalue'});

	function place() {
		var value = widget.bindings[control.name];
		var fx = (value[0] - control.min) / (control.max - control.min || 1);
		var fy = (value[1] - control.minY) / (control.maxY - control.minY || 1);
		dot.setStyle({left: (fx * size) + 'px', top: ((1 - fy) * size) + 'px'});
		readout.setText('{' + formatManipulateNumber(value[0]) + ', '
			+ formatManipulateNumber(value[1]) + '}');
	}

	function pick(event) {
		var offset = pad.cumulativeOffset();
		var fx = Math.max(0, Math.min(1, (Event.pointerX(event) - offset.left) / size));
		var fy = Math.max(0, Math.min(1, (Event.pointerY(event) - offset.top) / size));
		widget.bindings[control.name] = [
			control.min + fx * (control.max - control.min),
			control.minY + (1 - fy) * (control.maxY - control.minY)
		];
		place();
		requestManipulate(widget, control.name);
	}

	var dragging = false;
	pad.observe('mousedown', function(event) {
		dragging = true;
		pick(event);
		event.stop();
	});
	pad.observe('mousemove', function(event) {
		if (dragging)
			pick(event);
	});
	$(document).observe('mouseup', function() {
		dragging = false;
	});

	box.appendChild(pad);
	box.appendChild(readout);
	place();
	return box;
}

/** One X/Y pair of sliders per draggable point. */
function buildLocator(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol manipulatelocator'});
	var points = widget.bindings[control.name] || [];
	points.each(function(point, index) {
		var x = $E('input', {'type': 'range', 'class': 'manipulateslider',
			'min': control.min, 'max': control.max, 'step': (control.max - control.min) / 100});
		var y = $E('input', {'type': 'range', 'class': 'manipulateslider',
			'min': control.minY, 'max': control.maxY,
			'step': (control.maxY - control.minY) / 100});
		x.value = point[0];
		y.value = point[1];
		function update() {
			widget.bindings[control.name][index] = [parseFloat(x.value), parseFloat(y.value)];
			requestManipulate(widget, control.name);
		}
		x.observe('input', update);
		y.observe('input', update);
		box.appendChild($E('span', {'class': 'manipulatepoint'}, x, y));
	});
	return box;
}

function buildColor(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var input = $E('input', {'type': 'color', 'class': 'manipulatecolor'});
	input.value = /^#[0-9a-fA-F]{6}$/.test(control.value) ? control.value : '#3366cc';
	widget.bindings[control.name] = input.value;
	input.observe('change', function() {
		widget.bindings[control.name] = input.value;
		requestManipulate(widget, control.name);
	});
	control.input = input;
	box.appendChild(input);
	return box;
}

function buildInputField(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var input = $E('input', {'type': 'text', 'class': 'manipulateinput'});
	input.value = control.value == null ? '' : control.value;
	input.observe('change', function() {
		widget.bindings[control.name] = input.value;
		requestManipulate(widget, control.name);
	});
	control.input = input;
	box.appendChild(input);
	return box;
}

function formatManipulateNumber(value) {
	if (typeof value != 'number')
		return '' + value;
	if (value == Math.round(value) && Math.abs(value) < 1e15)
		return '' + Math.round(value);
	return '' + (Math.round(value * 1e6) / 1e6);
}

// ---------------------------------------------------------------- evaluation

/**
 * Note a control change and re-evaluate, at most one request at a time. Evaluating on every
 * mouse move of a slider would queue up requests the user has already moved past, so a burst
 * is collected and only the newest state is sent.
 */
function requestManipulate(widget, changedName) {
	if (!manipulateIsTracked(widget, changedName))
		return;
	widget.pending++;
	if (widget.scheduled || widget.inflight)
		return;
	widget.scheduled = true;
	window.setTimeout(function() {
		widget.scheduled = false;
		if (widget.applied != widget.pending)
			postManipulate(widget, -1);
	}, MANIPULATE_DEBOUNCE);
}

/** TrackedSymbols :> {...}: only the listed variables re-run the body. */
function manipulateIsTracked(widget, name) {
	var tracked = widget.options.trackedSymbols;
	if (!tracked || !name)
		return true;
	return tracked.indexOf(name) >= 0;
}

function postManipulate(widget, buttonIndex) {
	widget.inflight = true;
	widget.applied = widget.pending;
	var parameters = {
		id: widget.id,
		bindings: Object.toJSON(widget.bindings)
	};
	if (buttonIndex >= 0)
		parameters.button = buttonIndex;

	new Ajax.Request('/ajax/manipulate/', {
		method: 'post',
		parameters: parameters,
		onSuccess: function(transport) {
			var response;
			try {
				response = transport.responseText.evalJSON();
			} catch (e) {
				widget.inflight = false;
				return;
			}
			if (response.bindings)
				applyManipulateBindings(widget, response.bindings);
			showManipulateResponse(widget, response);
			widget.inflight = false;
			// a change that arrived while this request was on its way
			if (widget.applied != widget.pending)
				requestManipulate(widget, null);
		},
		onFailure: function() {
			widget.inflight = false;
		}
	});
}

/** Move the widgets a Button action wrote to, so the panel follows what the action did. */
function applyManipulateBindings(widget, bindings) {
	widget.controls.each(function(control) {
		if (!control.name || bindings[control.name] === undefined)
			return;
		var value = bindings[control.name];
		widget.bindings[control.name] = value;
		control.value = value;
		if (control.kind == 'slider' || control.kind == 'trigger') {
			if (control.input)
				control.input.value = value;
			if (control.readout)
				control.readout.setText(formatManipulateNumber(value));
		} else if (control.kind == 'checkbox' && control.input) {
			control.input.checked = value ? true : false;
		} else if (control.kind == 'discrete') {
			if (control.input)
				control.input.selectedIndex = value;
			if (control.buttons)
				control.buttons.each(function(button, i) {
					button.setClassName('selected', i == value);
				});
		}
	});
}

function showManipulateResponse(widget, response) {
	if (!response.results || response.results.length == 0)
		return;
	showManipulateBody(widget, response);
}

/**
 * Put the rendering of the body on screen. The new content is built first and only swapped in
 * once it is ready, so the previous graphic stays visible instead of the cell going blank
 * between two frames.
 */
function showManipulateBody(widget, body) {
	if (!body || !body.results)
		return;
	var replacement = $E('div', {'class': 'manipulateoutput'});
	body.results.each(function(result) {
		if (result.out) {
			result.out.each(function(out) {
				var li = $E('div', {'class': out.message ? 'manipulatemessage' : 'manipulateprint'});
				if (out.message)
					li.appendChild($T(out.prefix + ': '));
				li.appendChild(createLine(out.text, out.format));
				replacement.appendChild(li);
			});
		}
		if (result.result != null && result.result !== '')
			replacement.appendChild(createLine(result.result, result.format));
	});
	widget.dom.replaceChild(replacement, widget.output);
	widget.output = replacement;
}

// ---------------------------------------------------------------- animation

function startManipulateAnimation(widget) {
	if (widget.playing)
		return;
	var control = manipulateAnimationControl(widget);
	if (!control)
		return;
	widget.playing = true;
	var rate = widget.options.animationRate || 1.0;
	var interval = Math.max(40, Math.round(1000 / (rate * 20)));
	widget.timer = window.setInterval(function() {
		advanceManipulateAnimation(widget, control);
	}, interval);
	refreshManipulatePlayButtons(widget);
}

function stopManipulateAnimation(widget) {
	widget.playing = false;
	if (widget.timer) {
		window.clearInterval(widget.timer);
		widget.timer = null;
	}
	refreshManipulatePlayButtons(widget);
}

function manipulateAnimationControl(widget) {
	var name = widget.options.animationVariable;
	var found = null;
	widget.controls.each(function(control) {
		if (found)
			return;
		if (control.kind != 'slider' && control.kind != 'trigger')
			return;
		if (!name || control.name == name)
			found = control;
	});
	return found;
}

function advanceManipulateAnimation(widget, control) {
	var step = control.step * (widget.options.animationDirection < 0 ? -1 : 1);
	var value = widget.bindings[control.name] + step;
	if (value > control.max + control.step * 1e-6)
		value = control.min;
	else if (value < control.min - control.step * 1e-6)
		value = control.max;
	widget.bindings[control.name] = value;
	control.value = value;
	if (control.input)
		control.input.value = value;
	if (control.readout)
		control.readout.setText(formatManipulateNumber(value));
	requestManipulate(widget, control.name);
}

function refreshManipulatePlayButtons(widget) {
	widget.controls.each(function(control) {
		if (!control.playButton)
			return;
		var icon = control.playButton.select('i')[0];
		icon.setClassName('fa-play', !widget.playing);
		icon.setClassName('fa-pause', widget.playing);
	});
}
