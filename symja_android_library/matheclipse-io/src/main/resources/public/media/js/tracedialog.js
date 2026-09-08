/**
    Symja: a general-purpose computer algebra system

    Stepping through an evaluation: one step is shown at a time and the evaluation waits on the
    server until the reader says to go on.

    Each button press is one request to /ajax/tracedialog/. It lets the waiting evaluation carry
    on and comes back with the step it reaches next, or - once there is nothing left to do - with
    the whole derivation, which is then shown the way TraceForm shows it.
**/

/** The dialogs on the page, by the id the server knows them under. */
var traceDialogs = {};

/**
 * The panel for one evaluation which is being stepped through.
 *
 * @param result the whole result object, whose "dialog" is the first step. It is kept so that a
 *        derivation which is stepped to the end can replace it: what a notebook saves is then the
 *        finished derivation and not the step it started at.
 */
function createTraceDialog(result) {
	var spec = result.dialog;
	var dialog = {id: spec.id, result: result};
	dialog.dom = document.createElement('div');
	dialog.dom.className = 'tracedialog';

	dialog.stepBox = document.createElement('div');
	dialog.stepBox.className = 'tracedialogstep';
	dialog.dom.appendChild(dialog.stepBox);

	dialog.dom.appendChild(createTraceDialogButtons(dialog));
	dialog.dom.appendChild(createTraceDialogAsk(dialog));

	dialog.answers = document.createElement('div');
	dialog.answers.className = 'tracedialoganswers';
	dialog.dom.appendChild(dialog.answers);

	traceDialogs[spec.id] = dialog;
	showTraceDialogStep(dialog, spec);
	return dialog.dom;
}

function createTraceDialogButtons(dialog) {
	var buttons = document.createElement('div');
	buttons.className = 'tracedialogbuttons';
	[['Next step', 'continue'], ['Run to the end', 'finish'], ['Stop', 'abort']]
		.each(function(pair) {
			var button = document.createElement('button');
			button.appendChild(document.createTextNode(pair[0]));
			button.observe('click', function() {
				postTraceDialog(dialog, pair[1], null);
			});
			buttons.appendChild(button);
		});
	dialog.buttons = buttons;
	return buttons;
}

/** The field in which the reader asks what something comes to where the evaluation stands. */
function createTraceDialogAsk(dialog) {
	var ask = document.createElement('div');
	ask.className = 'tracedialogask';
	var input = document.createElement('input');
	input.setAttribute('type', 'text');
	input.setAttribute('placeholder', 'evaluate here, for example x');
	input.observe('keydown', function(event) {
		if (event.keyCode == Event.KEY_RETURN) {
			event.stop();
			if (input.value.strip().length)
				postTraceDialog(dialog, 'eval', input.value);
		}
	});
	ask.appendChild(input);
	dialog.input = input;
	return ask;
}

/** Show the step the evaluation is waiting at. */
function showTraceDialogStep(dialog, spec) {
	dialog.stepBox.update('');
	if (spec.finished)
		return;
	var where = document.createElement('div');
	where.className = 'tracedialogwhere';
	where.appendChild(document.createTextNode(
		'Step ' + spec.number + (spec.level > 1 ? ', inside step ' + (spec.level - 1) : '')));
	dialog.stepBox.appendChild(where);

	var sentence = document.createElement('div');
	sentence.className = 'tracedialogsentence';
	sentence.appendChild(document.createTextNode(spec.step || spec.stepKey || ''));
	renderStepMathIn(sentence);
	dialog.stepBox.appendChild(sentence);

	if (spec.prevExpression && spec.expression) {
		var rewrite = document.createElement('div');
		rewrite.className = 'steprewrite';
		rewrite.appendChild(symjaRenderTeX(spec.prevExpression, false));
		var arrow = document.createElement('span');
		arrow.className = 'steparrow';
		arrow.appendChild(document.createTextNode('⟶'));
		rewrite.appendChild(arrow);
		rewrite.appendChild(symjaRenderTeX(spec.expression, false));
		dialog.stepBox.appendChild(rewrite);
	}
}

/** What the reader asked about, shown under the step. */
function showTraceDialogAnswer(dialog, tex) {
	var answer = document.createElement('div');
	answer.className = 'tracedialoganswer';
	answer.appendChild(document.createTextNode(dialog.input.value + '  =  '));
	answer.appendChild(symjaRenderTeX(tex, false));
	dialog.answers.appendChild(answer);
	dialog.input.value = '';
}

/** The evaluation is over: the panel is replaced by the derivation it produced. */
function finishTraceDialog(dialog, spec) {
	delete traceDialogs[dialog.id];
	// from here on this cell holds an ordinary derivation, which is what gets saved to a notebook
	var stored = dialog.result;
	if (stored) {
		stored.format = 'steps';
		stored.steps = spec.steps;
		stored.result = spec.result;
		stored.latex = spec.latex;
		stored.plaintext = spec.plaintext;
		delete stored.dialog;
	}
	var steps = spec.steps ? createSteps(spec.steps) : document.createElement('div');
	dialog.dom.parentNode.replaceChild(steps, dialog.dom);
}

function postTraceDialog(dialog, action, expression) {
	dialog.buttons.childElements().each(function(button) { button.disabled = true; });
	var parameters = {id: dialog.id, action: action};
	if (expression != null)
		parameters.expr = expression;
	new Ajax.Request('/ajax/tracedialog/', {
		method: 'post',
		parameters: parameters,
		onSuccess: function(transport) {
			dialog.buttons.childElements().each(function(button) { button.disabled = false; });
			var response;
			try {
				response = transport.responseText.evalJSON();
			} catch (e) {
				return;
			}
			if (!response.results || !response.results.length)
				return;
			var result = response.results[0];
			if (result.format != 'tracedialog' || !result.dialog) {
				// an error or an expired dialog: show it where the step was
				dialog.stepBox.update('');
				dialog.stepBox.appendChild(createLine(
					result.result != null ? result.result : '', result.format));
				return;
			}
			var spec = result.dialog;
			if (spec.answer) {
				showTraceDialogAnswer(dialog, spec.answer);
				return;
			}
			if (spec.finished) {
				finishTraceDialog(dialog, spec);
				return;
			}
			showTraceDialogStep(dialog, spec);
		},
		onFailure: function() {
			dialog.buttons.childElements().each(function(button) { button.disabled = false; });
		}
	});
}
