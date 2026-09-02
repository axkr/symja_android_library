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
		// the controls the body itself drew, as the server last described them, and the live
		// widgets built for them - kept by position so a re-rendered body can hand a control
		// that is still under the pointer back to the user rather than a fresh one
		bodyControls: spec.bodyControls || [],
		bodyWidgets: {},
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

	if (spec.enabled)
		applyManipulateEnabled(widget, spec.enabled);
	if (spec.visible)
		applyManipulateVisible(widget, spec.visible);
	if (spec.displays)
		applyManipulateDisplays(widget, spec.displays);

	showManipulateBody(widget, spec.body);

	if (widget.options.animated && widget.options.animationRunning)
		startManipulateAnimation(widget);

	return dom;
}

// ---------------------------------------------------------------- controls

function buildManipulateControls(widget) {
	widget.controls.each(function(control, index) {
		var row = buildManipulateRow(widget, control, index);
		if (row) {
			// every kind of row remembers its element, not only the ones with a widget in them:
			// a PaneSelector hides whole rows, headings and separators along with the controls
			control.row = row;
			widget.panel.appendChild(row);
		}
	});
}

function buildManipulateRow(widget, control, index) {
	if (control.kind == 'delimiter')
		return $E('div', {'class': 'manipulatedelimiter'});
	if (control.kind == 'heading')
		return $E('div', {'class': 'manipulateheading'}, $T(control.label));
	if (control.kind == 'display') {
		// the server sends this row's rendering with every frame; the element is only the
		// place it goes
		var display = $E('div', {'class': 'manipulatedisplay'});
		control.displayElement = display;
		return display;
	}
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
	control.widgetElement = widgetElement;
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
	case 'multi':
		return buildMulti(widget, control);
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
	case 'file':
		return buildFileNameSetter(widget, control);
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
			manipulateChanged(widget, control);
	});
	input.observe('change', function() {
		widget.bindings[control.name] = parseFloat(input.value);
		manipulateChanged(widget, control);
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
				manipulateChanged(widget, control);
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
			manipulateChanged(widget, control);
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
		manipulateChanged(widget, control);
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
		manipulateChanged(widget, control);
	}
	low.observe('input', update);
	high.observe('input', update);

	// how a value the server sends back is put into this control (see setManipulateControlValue)
	control.apply = function(value) {
		low.value = value[0];
		high.value = value[1];
		readout.setText(formatManipulateNumber(value[0]) + ' .. '
			+ formatManipulateNumber(value[1]));
	};

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
		manipulateChanged(widget, control);
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

	control.apply = place;

	box.appendChild(pad);
	box.appendChild(readout);
	place();
	return box;
}

/** One X/Y pair of sliders per draggable point. */
function buildLocator(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol manipulatelocator'});
	var points = widget.bindings[control.name] || [];
	var rows = [];
	points.each(function(point, index) {
		var x = $E('input', {'type': 'range', 'class': 'manipulateslider',
			'min': control.min, 'max': control.max, 'step': (control.max - control.min) / 100});
		var y = $E('input', {'type': 'range', 'class': 'manipulateslider',
			'min': control.minY, 'max': control.maxY,
			'step': (control.maxY - control.minY) / 100});
		x.value = point[0];
		y.value = point[1];
		var readout = $E('span', {'class': 'manipulatevalue'},
			$T('{' + formatManipulateNumber(point[0]) + ', '
				+ formatManipulateNumber(point[1]) + '}'));
		function update() {
			var px = parseFloat(x.value);
			var py = parseFloat(y.value);
			widget.bindings[control.name][index] = [px, py];
			readout.setText('{' + formatManipulateNumber(px) + ', '
				+ formatManipulateNumber(py) + '}');
			manipulateChanged(widget, control);
		}
		x.observe('input', update);
		y.observe('input', update);
		rows.push({x: x, y: y, readout: readout});
		box.appendChild($E('span', {'class': 'manipulatepoint'}, x, y, readout));
	});

	// a locator's position is regularly not the one that was sent - a setter may round or clamp
	// it, and a pane keeps its points inside the picture - so the sliders follow the answer
	control.apply = function(value) {
		rows.each(function(row, index) {
			var point = value[index];
			if (!point)
				return;
			row.x.value = point[0];
			row.y.value = point[1];
			row.readout.setText('{' + formatManipulateNumber(point[0]) + ', '
				+ formatManipulateNumber(point[1]) + '}');
		});
	};
	return box;
}

function buildColor(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var input = $E('input', {'type': 'color', 'class': 'manipulatecolor'});
	input.value = /^#[0-9a-fA-F]{6}$/.test(control.value) ? control.value : '#3366cc';
	widget.bindings[control.name] = input.value;
	input.observe('change', function() {
		widget.bindings[control.name] = input.value;
		manipulateChanged(widget, control);
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
		manipulateChanged(widget, control);
	});
	control.input = input;
	box.appendChild(input);
	return box;
}

/**
 * FileNameSetter[Dynamic[f]]: a Browse button whose value is a file name.
 *
 * On a local kernel that name is a path on the machine the kernel runs on. Here it cannot be: the
 * kernel is at the other end of an HTTP connection and reads inside a directory of its own, so the
 * file is carried across first - the same POST /ajax/upload/ the toolbar button uses - and the
 * value handed to the variable is the name it was stored under. Import(f) then means the same
 * thing it would mean locally.
 */
function buildFileNameSetter(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var dialog = control.dialog || 'Open';
	var button = $E('input', {'type': 'button', 'class': 'manipulatesetter',
		'value': dialog == 'Directory' ? 'Choose folder\u2026' : 'Browse\u2026'});
	var readout = $E('span', {'class': 'manipulatevalue'},
		$T(control.value == null ? '' : control.value));
	var input = $E('input', {'type': 'file', 'style': 'display: none'});
	if (dialog == 'OpenList')
		input.multiple = true;

	control.input = input;
	control.readout = readout;

	// a Save or Directory dialog has nothing to upload - a browser cannot offer either - so the
	// button asks for a name instead and the value is whatever the user types
	function askForName() {
		var name = window.prompt(dialog == 'Directory' ?
			"Folder name inside this session's directory:" :
			"File name to write inside this session's directory:", control.value || '');
		if (name == null)
			return;
		widget.bindings[control.name] = name;
		readout.setText(name);
		manipulateChanged(widget, control);
	}

	function uploadChosen() {
		var files = input.files;
		if (!files || files.length == 0)
			return;
		var names = [];
		var pending = files.length;
		readout.setText("uploading\u2026");
		$A(files).each(function(file) {
			var form = new FormData();
			form.append('file', file);
			var request = new XMLHttpRequest();
			request.open('POST', '/ajax/upload/');
			request.onload = function() {
				var result;
				try {
					result = JSON.parse(request.responseText);
				} catch (e) {
					result = {'error': request.responseText};
				}
				if (result.error) {
					readout.setText(result.error);
				} else {
					names.push(result.name);
				}
				if (--pending == 0)
					uploadsDone(names);
			};
			request.onerror = function() {
				readout.setText("upload failed");
				if (--pending == 0)
					uploadsDone(names);
			};
			request.send(form);
		});
	}

	function uploadsDone(names) {
		if (names.length == 0)
			return;
		// the variable holds one name, or the list of them for OpenList - the same shape the
		// server writes back through ManipulateSession.valueOf
		var value = dialog == 'OpenList' ? names.join(',') : names[0];
		widget.bindings[control.name] = value;
		readout.setText(value);
		manipulateChanged(widget, control);
	}

	input.observe('change', uploadChosen);
	button.observe('click', function() {
		if (dialog == 'Save' || dialog == 'Directory') {
			askForName();
			return;
		}
		// so that picking the same file twice in a row fires the change event again
		input.value = '';
		input.click();
	});

	box.appendChild(button);
	box.appendChild(readout);
	box.appendChild(input);
	return box;
}

/**
 * A row of independent switches, for TogglerBar and CheckboxBar. Unlike the single choice of a
 * setter bar, the value here is the list of positions that are on, so clicking one adds or
 * removes it and leaves the rest alone.
 */
function buildMulti(widget, control) {
	var box = $E('span', {'class': 'manipulatecontrol'});
	var labels = control.labels || [];
	var buttons = [];
	labels.each(function(text, index) {
		var button = $E('input', {'type': 'button', 'class': 'manipulatesetter', 'value': text});
		button.observe('click', function() {
			var chosen = (widget.bindings[control.name] || []).slice();
			var at = chosen.indexOf(index);
			if (at >= 0)
				chosen.splice(at, 1);
			else
				chosen.push(index);
			widget.bindings[control.name] = chosen;
			control.value = chosen;
			refreshMultiButtons(control);
			manipulateChanged(widget, control);
		});
		buttons.push(button);
		box.appendChild(button);
	});
	control.buttons = buttons;
	refreshMultiButtons(control);
	return box;
}

function refreshMultiButtons(control) {
	var chosen = control.value || [];
	(control.buttons || []).each(function(button, index) {
		button.setClassName('selected', chosen.indexOf(index) >= 0);
	});
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
 * A control the user moved, whichever kind it is.
 *
 * <p>A control of the panel changes one of the widget's own variables, so the whole binding set
 * goes back and the body is rendered again. A control the body itself drew was written
 * <code>Slider[Dynamic[x]]</code> and points wherever <code>x</code> is - which may be a variable
 * of this widget, and may just as well be a symbol of the session - so it sends its position and
 * its value instead, and the server decides where the write lands.
 */
function manipulateChanged(widget, control) {
	if (control.dynamicIndex != null)
		requestDynamic(widget, control);
	else if (control.bodyIndex != null)
		requestBodyControl(widget, control);
	else
		requestManipulate(widget, control.name);
}

/**
 * The controls that are actually on screen. A Manipulate builds its panel once and keeps the
 * live objects in its control list; a live Dynamic cell rebuilds its rendering on every change
 * and keeps them by position instead, because the list the server sends is only a description.
 */
function manipulateLiveControls(widget) {
	if (!widget.widgets)
		return widget.controls;
	var live = [];
	$H(widget.widgets).each(function(pair) {
		live.push(pair.value);
	});
	return live;
}

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

/**
 * The same coalescing for a control the body drew. Only the newest position of the control is
 * worth sending, so a burst of moves collapses into one write the way a panel control's does.
 */
function requestBodyControl(widget, control) {
	widget.pending++;
	if (widget.scheduled || widget.inflight)
		return;
	widget.scheduled = true;
	window.setTimeout(function() {
		widget.scheduled = false;
		if (widget.applied != widget.pending)
			postManipulate(widget, -1, -1, control);
	}, MANIPULATE_DEBOUNCE);
}

function postManipulate(widget, buttonIndex, bodyButtonIndex, bodyControl) {
	widget.inflight = true;
	widget.applied = widget.pending;
	var parameters = {
		id: widget.id,
		bindings: Object.toJSON(widget.bindings)
	};
	if (buttonIndex >= 0)
		parameters.button = buttonIndex;
	if (bodyButtonIndex != null && bodyButtonIndex >= 0)
		parameters.bodyButton = bodyButtonIndex;
	if (bodyControl != null) {
		parameters.bodyControl = bodyControl.bodyIndex;
		parameters.bodyValue = Object.toJSON(widget.bindings[bodyControl.name]);
	}

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
			if (response.enabled)
				applyManipulateEnabled(widget, response.enabled);
			if (response.visible)
				applyManipulateVisible(widget, response.visible);
			if (response.displays)
				applyManipulateDisplays(widget, response.displays);
			widget.bodyControls = response.bodyControls || [];
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
		setManipulateControlValue(widget, control, bindings[control.name]);
	});
}

/**
 * Put a value into a control that is already on screen, without rebuilding it.
 *
 * <p>The value the server answers with is the one that was actually stored, which need not be
 * the one that was sent: a Dynamic with a setter may clamp it, round it or refuse it. Moving the
 * widget to it is what keeps the control showing the truth rather than drifting away from it.
 */
function setManipulateControlValue(widget, control, value) {
	if (value === undefined)
		return;
	widget.bindings[control.name] = value;
	control.value = value;
	// the controls whose widget is more than one element keep a closure that writes into it
	if (control.apply) {
		control.apply(value);
		return;
	}
	switch (control.kind) {
	case 'slider':
	case 'trigger':
		if (control.input)
			control.input.value = value;
		if (control.readout)
			control.readout.setText(formatManipulateNumber(value));
		return;
	case 'checkbox':
		if (control.input)
			control.input.checked = value ? true : false;
		return;
	case 'discrete':
		if (control.input)
			control.input.selectedIndex = value;
		if (control.buttons)
			control.buttons.each(function(button, i) {
				button.setClassName('selected', i == value);
			});
		return;
	case 'multi':
		refreshMultiButtons(control);
		return;
	case 'color':
	case 'inputfield':
		if (control.input)
			control.input.value = value == null ? '' : value;
		return;
	case 'file':
		// the file input itself cannot be written to, so only the read-out follows the value
		if (control.readout)
			control.readout.setText(value == null ? '' : value);
		return;
	}
}

/**
 * Enabled -> cond: grey out the controls whose condition is currently False. The server
 * resolves the conditions against the live control values, so one control can switch
 * another one off.
 */
function applyManipulateEnabled(widget, flags) {
	widget.controls.each(function(control, index) {
		var on = flags[index] !== false;
		if (control.enabled === on)
			return;
		control.enabled = on;
		if (control.row)
			control.row.setClassName('disabled', !on);
		if (control.widgetElement)
			control.widgetElement.select('input, select, button').each(function(input) {
				input.disabled = !on;
			});
	});
}

/**
 * Show the rows of the PaneSelector pane the selector is on and hide the rest.
 *
 * The hidden rows stay in the panel rather than being removed: their variables are still bound,
 * so the body can go on using them, and the panel does not have to be rebuilt every time the
 * selector moves.
 */
function applyManipulateVisible(widget, flags) {
	widget.controls.each(function(control, index) {
		var on = flags[index] !== false;
		if (control.visible === on)
			return;
		control.visible = on;
		var row = control.row || control.displayElement;
		if (!row)
			return;
		if (on)
			row.show();
		else
			row.hide();
	});
}

/**
 * Put the fresh rendering of every read-out row on screen. These are the rows written with a
 * Dynamic - Manipulate[..., Row[{"moves: ", Dynamic[moves]}]] - and they follow the frame the
 * same way the body does, through the very same renderer.
 */
function applyManipulateDisplays(widget, displays) {
	$H(displays).each(function(pair) {
		var control = widget.controls[parseInt(pair.key, 10)];
		if (!control || !control.displayElement)
			return;
		var rendering = pair.value;
		if (rendering == null || !rendering.results)
			return;
		var replacement = $E('div', {'class': 'manipulatedisplay'});
		rendering.results.each(function(result) {
			if (result.result != null && result.result !== '')
				replacement.appendChild(createLine(result.result, result.format));
		});
		if (control.visible === false)
			replacement.hide();
		control.displayElement.replace(replacement);
		control.displayElement = replacement;
		control.row = replacement;
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
	wireManipulateBodyButtons(widget);
	wireManipulateBodyControls(widget);
}

/**
 * Put the controls the body drew - Slider[Dynamic[x]] and its relatives - into the rendering.
 *
 * <p>The rendering only marks where each one goes; the widget itself is built here, from the
 * description the server sent, by exactly the same builders the control panel uses.
 *
 * <p>A control that was already on screen is moved into the new rendering rather than rebuilt.
 * The body is re-rendered on every frame, and a slider that was replaced under the pointer would
 * lose the drag that is moving it - so the element survives the frame and only its value is
 * brought up to date.
 */
function wireManipulateBodyControls(widget) {
	var descriptors = widget.bodyControls || [];
	widget.output.select('span.symjacontrol').each(function(span) {
		var index = parseInt(span.readAttribute('data-control'), 10);
		var descriptor = descriptors[index];
		if (descriptor == null)
			return;
		var control = widget.bodyWidgets[index];
		if (control && manipulateSameShape(control, descriptor)) {
			setManipulateControlValue(widget, control, descriptor.value);
		} else {
			control = Object.clone(descriptor);
			control.bodyIndex = index;
			widget.bindings[control.name] = control.value;
			control.element = buildManipulateWidget(widget, control);
			widget.bodyWidgets[index] = control;
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

/** Whether a control already on screen is still the one the server is describing. */
function manipulateSameShape(control, descriptor) {
	if (control.kind != descriptor.kind)
		return false;
	var was = control.labels ? control.labels.length : 0;
	var now = descriptor.labels ? descriptor.labels.length : 0;
	return was == now && control.min === descriptor.min && control.max === descriptor.max;
}

/**
 * Make the Button elements the body produced clickable. Their code stays on the server; the
 * position in this rendering is all the browser holds, and pressing one runs that action
 * against the live control values and re-renders.
 */
function wireManipulateBodyButtons(widget) {
	widget.output.select('span.symjabutton').each(function(button) {
		var action = button.readAttribute('data-action');
		if (action == null)
			return;
		button.addClassName('active');
		button.observe('click', function() {
			postManipulate(widget, -1, parseInt(action, 10));
		});
	});
}

/**
 * Release a widget whose cell the user deleted: the server drops it and runs its
 * Deinitialization code, and any animation timer it owns is stopped here.
 */
function disposeManipulate(id) {
	var widget = manipulateWidgets[id];
	if (!widget)
		return;
	stopManipulateAnimation(widget);
	delete manipulateWidgets[id];
	new Ajax.Request('/ajax/manipulate/', {
		method: 'post',
		parameters: {id: id, dispose: 1}
	});
}

/** Release every widget inside an element that is about to be removed. */
function disposeManipulatesIn(element) {
	if (!element || typeof element.select != 'function')
		return;
	element.select('div.manipulate').each(function(dom) {
		$H(manipulateWidgets).each(function(pair) {
			if (pair.value.dom == dom)
				disposeManipulate(pair.key);
		});
	});
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
	manipulateLiveControls(widget).each(function(control) {
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
	manipulateChanged(widget, control);
}

function refreshManipulatePlayButtons(widget) {
	manipulateLiveControls(widget).each(function(control) {
		if (!control.playButton)
			return;
		var icon = control.playButton.select('i')[0];
		icon.setClassName('fa-play', !widget.playing);
		icon.setClassName('fa-pause', widget.playing);
	});
}
