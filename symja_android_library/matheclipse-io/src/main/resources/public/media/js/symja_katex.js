/**
    Symja: a general-purpose computer algebra system

    One place for the KaTeX settings, shared by the result cells, the documentation pages
    and the Markdown cells of a notebook.
**/

/**
 * Commands that TeXFormFactory emits but KaTeX does not know. Defining them here keeps the
 * fix in one place instead of spreading it over the converters in matheclipse-core.
 */
var SYMJA_KATEX_MACROS = {
	// the imaginary unit, from the Complex converter
	'\\imag': '\\mathrm{i}',
	// omicron has no command in TeX because it is just an "o"
	'\\omicron': 'o',
	'\\degree': '^\\circ',
	// the inverse and inverse hyperbolic functions have no commands of their own,
	// neither in LaTeX nor in KaTeX
	'\\arccot': '\\operatorname{arccot}',
	'\\arcsec': '\\operatorname{arcsec}',
	'\\arccsc': '\\operatorname{arccsc}',
	'\\arcsinh': '\\operatorname{arcsinh}',
	'\\arccosh': '\\operatorname{arccosh}',
	'\\arctanh': '\\operatorname{arctanh}',
	'\\arccoth': '\\operatorname{arccoth}',
	'\\arcsech': '\\operatorname{arcsech}',
	'\\arccsch': '\\operatorname{arccsch}',
	'\\sech': '\\operatorname{sech}',
	'\\csch': '\\operatorname{csch}'
};

var SYMJA_KATEX_OPTIONS = {
	macros: SYMJA_KATEX_MACROS,
	// a formula that does not parse is shown as its source in red rather than
	// taking down the rest of the page
	throwOnError: false,
	errorColor: '#cc0000',
	strict: 'ignore',
	trust: false
};

/** The delimiters AJAXDocServlet emits, and that Markdown cells may use. */
var SYMJA_KATEX_DELIMITERS = [
	{left: '\\[', right: '\\]', display: true},
	{left: '\\(', right: '\\)', display: false},
	{left: '$$', right: '$$', display: true}
];

function symjaKatexOptions(displayMode) {
	return {
		macros: SYMJA_KATEX_MACROS,
		throwOnError: SYMJA_KATEX_OPTIONS.throwOnError,
		errorColor: SYMJA_KATEX_OPTIONS.errorColor,
		strict: SYMJA_KATEX_OPTIONS.strict,
		trust: false,
		displayMode: displayMode ? true : false
	};
}

/**
 * Render one LaTeX string into a new element.
 *
 * @param tex the LaTeX source, without delimiters
 * @param displayMode <code>true</code> for centered display math
 */
function symjaRenderTeX(tex, displayMode) {
	var element = document.createElement('span');
	element.className = 'symjamath';
	if (typeof katex == 'undefined') {
		// KaTeX did not load - show the source rather than an empty cell
		element.appendChild(document.createTextNode(tex));
		return element;
	}
	try {
		katex.render(tex, element, symjaKatexOptions(displayMode));
	} catch (e) {
		element.appendChild(document.createTextNode(tex));
	}
	return element;
}

/**
 * Render every formula inside an element that is written in the delimiters above. Used for
 * documentation pages and Markdown cells, where the math sits inside prose.
 */
function symjaRenderMathIn(element) {
	if (typeof renderMathInElement == 'undefined' || !element)
		return;
	try {
		renderMathInElement(element, {
			delimiters: SYMJA_KATEX_DELIMITERS,
			macros: SYMJA_KATEX_MACROS,
			throwOnError: SYMJA_KATEX_OPTIONS.throwOnError,
			errorColor: SYMJA_KATEX_OPTIONS.errorColor,
			strict: SYMJA_KATEX_OPTIONS.strict,
			trust: false,
			// Symja code samples live in <pre>/<code> and must not be read as formulas
			ignoredTags: ['script', 'noscript', 'style', 'textarea', 'pre', 'code', 'option']
		});
	} catch (e) {
		// leave the source visible
	}
}
