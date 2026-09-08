function setupExamples() { 
    $('.example-group div.contents').each(function() {
        var contents = $(this);   
        contents.hide();  
    });

    $('.example-group').click(function(e) {
        var header = $(e.target);
        var contents = header.siblings('div.contents');

        contents.stop(false, true).slideToggle(500, function() {
            createCookie(header.html(), contents.is(':visible'), 365);
        });
        header.toggleClass('shown');
        header.siblings('i').toggleClass('shown');
	header.siblings('h3').toggleClass('shown');
    });
}

$(function () {
    $("#id_i").keypress(function (e) {
        var code = (e.keyCode ? e.keyCode : e.which);
        //alert(code);
        if (code == 13&& e.shiftKey) {
            $("#submit").trigger('click');
            return true;
        }
    });
});
$("#calc").submit(
  function(event) {
	  event.preventDefault();
	  var post_url = $(this).attr("action");
	  var form_data = $(this).serialize();
	  $("#main").hide(); 
	  $("body").addClass("loading"); 
	  $.getJSON(post_url, form_data, 
        function(data) {
		  $("body").removeClass("loading"); 
		  var queryresult = data.queryresult;
		  var output = ' <div class="result_card">';
		  for ( var i in queryresult.pods) {
		    var pod = queryresult.pods[i];
		    output += `<div class="card_title">${pod.title}</div>`;
		    for ( var j in pod.subpods) {
			  var subpod = pod.subpods[j];
			  if (subpod.sinput){ 
				output += `<div class="cell_input"><pre>${subpod.sinput}</pre></div>`;
			  }
			  $.each(subpod,function(k,v) {
				if (k == 'plaintext') {
				  output += `<div class="cell_output"><pre>${v}</pre></div>\n`;
			    } else if (k == 'markdown') {
                  v=md.makeHtml(v);			
				  output += `<div class="cell_output" data-card-name="function_docs" data-variable="None">${v}</div>`;
				} else if (k == 'html') {
			      v=$("<div>").html(v).text();			
				  output += `<div class="cell_output" data-card-name="function_docs" data-variable="None">${v}</div>`;
				} else if (k == 'latex') {
				  // the pod carries bare TeX, so it needs delimiters before KaTeX can find it
			      output += `<div class="cell_output"><div>\\[${v}\\]</div></div>`;
			    } else if (k == 'jsxgraph') {
				  output += `<div class="cell_output"><div id="jsxgraph" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'mathcell') {
				  output += `<div class="cell_output"><div id="mathcell" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'plotly') {
				  output += `<div class="cell_output"><div id="plotly" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'visjs') {
					  output += `<div class="cell_output"><div id="visjs" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'mathml') {
				  output += `<div class="cell_output"><div>${v}</div></div>`;
				} else if (k == 'steps') {
				  output += `<div class="cell_output">${stepsHtml(v)}</div>`;
		        }
			  });
		    }
		  }
		  output += ' </div>'; 
		  $('#root').html(output);
		  // KaTeX does not watch the document, so everything just inserted has to be handed to it
		  symjaRenderPodMathIn(document.getElementById('root'));
        }
	  );
  }
);

/**
 * Render every formula of a finished pod list.
 *
 * A pod has math from three sources: the latex pods and the rewrites of a derivation are wrapped
 * in \\[ ... \\] and \\( ... \\) here, and the rule descriptions in i18n/en.json are written
 * with $ ... $. All three are accepted, and only here - a lone dollar sign elsewhere on a page is
 * usually a currency and not the start of a formula.
 */
function symjaRenderPodMathIn(element) {
	if (typeof renderMathInElement == 'undefined' || !element)
		return;
	try {
		renderMathInElement(element, {
			delimiters: [
				{left: '\\[', right: '\\]', display: true},
				{left: '\\(', right: '\\)', display: false},
				{left: '$', right: '$', display: false}
			],
			macros: SYMJA_KATEX_MACROS,
			throwOnError: false,
			errorColor: '#cc0000',
			strict: 'ignore',
			trust: false,
			ignoredTags: ['script', 'noscript', 'style', 'textarea', 'pre', 'code', 'option']
		});
	} catch (e) {
		// leave the source visible
	}
}

/**
 * The derivation a TraceForm() or TraceDialog() result carries: every step is a section the reader
 * opens and closes, and a step which was caused by another one sits inside it.
 *
 * The formulas travel as TeX; symjaRenderPodMathIn() typesets them once the HTML is in place.
 */
function stepsHtml(steps) {
	if (!steps)
		return '';
	var html = '<div class="steps">';
	if (steps.expression)
		html += '<div class="stepresult">\\[' + steps.expression + '\\]</div>';
	html += stepListHtml(steps.steps, 1);
	return html + '</div>';
}

function stepListHtml(steps, level) {
	if (!steps || !steps.length)
		return '';
	var html = '<div class="steplist">';
	for (var index = 0; index < steps.length; ++index) {
		var step = steps[index];
		html += '<details class="step"' + (level <= 1 ? ' open' : '') + '>';
		html += '<summary>' + escapeStepText(step.step || step.stepKey || '') + '</summary>';
		if (step.prevExpression && step.expression)
			html += '<div class="steprewrite">\\(' + step.prevExpression
				+ ' \\longrightarrow ' + step.expression + '\\)</div>';
		html += stepListHtml(step.subSteps, level + 1);
		html += '</details>';
	}
	return html + '</div>';
}

/** A sentence is prose from the rule set, so its angle brackets must not become markup. */
function escapeStepText(text) {
	return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
