/**
    Symja: a general-purpose computer algebra system

    The derivation a TraceForm() result carries: every step is a section the reader opens and
    closes, and a step which was caused by another one sits inside it.

    The server sends the steps as a tree of their own rather than as one finished formula, so a
    long derivation can be read a level at a time. Each node carries

      stepKey        the rule which was applied, for example "D::ChainRule"
      step           the sentence which explains it, with its formulas in \( ... \)
      prevExpression the expression the step started from, as TeX
      expression     the expression it produced, as TeX
      truncated      true for the marker which says that deeper steps are not shown
      subSteps       the steps this one caused
**/

/**
 * Down to which level a section starts out open.
 *
 * A derivation nests: for an integral the step which does the work sits three or four levels in,
 * so opening only the outermost one would hide the whole thing behind clicks.
 */
var STEPS_OPEN_TO_LEVEL = 2;

/**
 * The whole derivation: the result, then the steps which lead to it.
 *
 * @param steps the "steps" object of the result
 */
function createSteps(steps) {
	var container = document.createElement('div');
	container.className = 'steps';
	if (steps.expression)
		container.appendChild(symjaRenderTeX(steps.expression, true));
	var list = createStepList(steps.steps, 1);
	if (list)
		container.appendChild(list);
	return container;
}

function createStepList(steps, level) {
	if (!steps || !steps.length)
		return null;
	var list = document.createElement('div');
	list.className = 'steplist';
	for (var index = 0; index < steps.length; ++index)
		list.appendChild(createStep(steps[index], level));
	return list;
}

function createStep(step, level) {
	var details = document.createElement('details');
	details.className = 'step';
	// the outer levels are the overview, so they start out open; deeper ones are the detail
	if (level <= STEPS_OPEN_TO_LEVEL)
		details.setAttribute('open', 'open');

	var summary = document.createElement('summary');
	// a step with neither a rewrite nor sub-steps is a note, not a section: it must not offer a
	// disclosure triangle which opens onto nothing
	var isLeaf = !(step.prevExpression && step.expression) && !(step.subSteps && step.subSteps.length);
	summary.className = step.truncated ? 'steptruncated' : (isLeaf ? 'stepinfo' : 'stepsummary');
	summary.appendChild(document.createTextNode(step.step || step.stepKey || ''));
	renderStepMathIn(summary);
	details.appendChild(summary);

	if (step.prevExpression && step.expression) {
		var rewrite = document.createElement('div');
		rewrite.className = 'steprewrite';
		rewrite.appendChild(symjaRenderTeX(step.prevExpression, false));
		var arrow = document.createElement('span');
		arrow.className = 'steparrow';
		// U+27F6, a long rightwards arrow
		arrow.appendChild(document.createTextNode('⟶'));
		rewrite.appendChild(arrow);
		rewrite.appendChild(symjaRenderTeX(step.expression, false));
		details.appendChild(rewrite);
	}

	var subList = createStepList(step.subSteps, level + 1);
	if (subList)
		details.appendChild(subList);
	return details;
}

/**
 * Render the formulas of a step's sentence.
 *
 * A sentence has math from two sources: the rule descriptions in i18n/en.json are written with
 * $ ... $, and the expressions substituted into them arrive in \( ... \). Both are accepted
 * here, and only here - $ stays out of the delimiters used for documentation pages and Markdown
 * cells, where a lone dollar sign is usually a currency and not the start of a formula.
 */
function renderStepMathIn(element) {
	if (typeof renderMathInElement == 'undefined' || !element)
		return;
	try {
		renderMathInElement(element, {
			delimiters: [
				{left: '\\(', right: '\\)', display: false},
				{left: '$', right: '$', display: false}
			],
			macros: SYMJA_KATEX_MACROS,
			throwOnError: false,
			errorColor: '#cc0000',
			strict: 'ignore',
			trust: false
		});
	} catch (e) {
		// leave the source visible
	}
}
