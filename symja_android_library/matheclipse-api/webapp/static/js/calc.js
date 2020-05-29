$("#calc").submit(
  function(event) {
	  event.preventDefault();
	  var post_url = $(this).attr("action");
	  var form_data = $(this).serialize();
	  $.getJSON(post_url, form_data, 
        function(data) {
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
				  output += `<div class="cell_output_plain" data-card-name="function_docs" data-variable="None"><pre>${v}</pre></div>`;
				} else if (k == 'html') {
				  output += `<div class="cell_output_plain" data-card-name="function_docs" data-variable="None">${v}</div>`;
				} else if (k == 'latex') {
			      output += `<div class="cell_output"><div>${v}</div></div>`;
			    } else if (k == 'jsxgraph') {
				  output += `<div class="cell_output"><div id="jsxgraph" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'mathcell') {
				  output += `<div class="cell_output"><div id="mathcell" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'plotly') {
				  output += `<div class="cell_output"><div id="plotly" style="width: 600px; height: 440px; margin: 0; padding: 0">${v}</div></div>\n`;
				} else if (k == 'mathml') {
				  output += `<div class="cell_output"><div>${v}</div></div>`;
		        }
			  });
		    }
		  }
		  output += ' </div>';
		  document.getElementById("root").innerHTML = output;
        }
	  );
  }
);
 