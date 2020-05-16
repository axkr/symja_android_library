package org.matheclipse.api;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class SymjaServer {
	// output formats
	public static final String PLAIN = "plaintext";
	public static final String SYMJA = "sinput";
	public static final String MATHML = "mathml";
	public static final String LATEX = "latex";
	public static final String MARKDOWN = "markdown";

	// output
	public static final String JSON = "JSON";

	public static void main(final String[] args) {
		ToggleFeature.COMPILE = false;
		Config.UNPROTECT_ALLOWED = false;
		Config.USE_MANIPULATE_JS = true;
		Config.JAS_NO_THREADS = false;
		// Config.THREAD_FACTORY = com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
		Config.MATHML_TRIG_LOWERCASE = false;
		Config.MAX_AST_SIZE = ((int) Short.MAX_VALUE) * 8;
		Config.MAX_OUTPUT_SIZE = Short.MAX_VALUE;
		Config.MAX_BIT_LENGTH = ((int) Short.MAX_VALUE) * 8;
		EvalEngine.get().setPackageMode(true);
		F.initSymbols(null, null, false);// new SymbolObserver(), false);

		Undertow server = Undertow.builder().addHttpListener(8080, "localhost").setHandler(new HttpHandler() {
			@Override
			public void handleRequest(final HttpServerExchange exchange) throws Exception {
				HeaderMap responseHeaders = exchange.getResponseHeaders();
				responseHeaders.put(new HttpString("Access-Control-Allow-Origin"), "*");
				responseHeaders.put(Headers.CONTENT_TYPE, "application/json");

				Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
				String inputStr = getParam(queryParameters, "input", "i", "");
				String[] formats = getParams(queryParameters, "format", "f", PLAIN);

				final StringWriter outWriter = new StringWriter();
				// WriterOutputStream wouts = new WriterOutputStream(outWriter);
				final StringWriter errorWriter = new StringWriter();
				// WriterOutputStream werrors = new WriterOutputStream(errorWriter);

				// exchange.getResponseSender().send(createJSONJavaScript(inExpr.toString()));
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode messageJSON = mapper.createObjectNode();

				ObjectNode queryresult = mapper.createObjectNode();
				messageJSON.putPOJO("queryresult", queryresult);

				ArrayNode podsArray = mapper.createArrayNode();
				queryresult.putPOJO("pods", podsArray);

				ArrayNode temp = mapper.createArrayNode();
				ObjectNode subpodsResult = mapper.createObjectNode();
				subpodsResult.putPOJO("subpods", temp);
				podsArray.add(subpodsResult);

				IExpr inExpr = F.Null;
				EvalEngine engine = EvalEngine.get();
				try {
					engine.setPackageMode(false);
					// ExprParser parser = new ExprParser(engine, true);
					inExpr = engine.evaluate(inputStr);
				} catch (RuntimeException rex) {
					inExpr = F.$Aborted;
				}
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				for (int i = 0; i < formats.length; i++) {
					createJSONFormat(node, engine, inExpr, formats[i]);
				}
				final String jsonStr = messageJSON.toString();
				// System.out.println(jsonStr);
				exchange.getResponseSender().send(jsonStr);
			}

		}).build();
		server.start();
		System.out.println("started");
	}

	private static String getParam(Map<String, Deque<String>> queryParameters, String longParameter,
			String shortParameter, String defaultStr) {
		Deque<String> d = queryParameters.get(shortParameter);
		if (d != null && !d.isEmpty()) {
			return d.getFirst();
		}
		d = queryParameters.get(longParameter);
		if (d != null && !d.isEmpty()) {
			return d.getFirst();
		}
		return defaultStr;
	}

	private static String[] getParams(Map<String, Deque<String>> queryParameters, String longParameter,
			String shortParameter, String defaultStr) {
		Deque<String> d = queryParameters.get(shortParameter);
		if (d != null && !d.isEmpty()) {
			String[] result = d.toArray(new String[d.size()]);
			if (result.length == 1) {
				String[] split = result[0].split(",");
				return split;
			}
			return result;
		}
		d = queryParameters.get(longParameter);
		if (d != null && !d.isEmpty()) {
			String[] result = d.toArray(new String[d.size()]);
			if (result.length == 1) {
				String[] split = result[0].split(",");
				return split;
			}
			return result;
		}
		return new String[] { defaultStr };
	}

	public static ObjectNode createJSONErrorString(String str) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode outJSON = mapper.createObjectNode();
		outJSON.put("prefix", "Error");
		outJSON.put("message", Boolean.TRUE);
		outJSON.put("tag", "syntax");
		outJSON.put("symbol", "General");
		outJSON.put("text", "<math><mrow><mtext>" + str + "</mtext></mrow></math>");

		ObjectNode resultsJSON = mapper.createObjectNode();
		resultsJSON.putNull("line");
		resultsJSON.putNull("result");

		ArrayNode temp = mapper.createArrayNode();
		temp.add(outJSON);
		resultsJSON.putPOJO("out", temp);

		temp = mapper.createArrayNode();
		temp.add(resultsJSON);
		ObjectNode json = mapper.createObjectNode();
		json.putPOJO("results", temp);
		return json;
	}

	public static String createJSONJavaScript(String script) throws IOException {
		script = StringEscapeUtils.escapeHtml4(script);
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode resultsJSON = mapper.createObjectNode();
		resultsJSON.put("line", new Integer(21));
		resultsJSON.put("result", script);

		ArrayNode temp = mapper.createArrayNode();
		resultsJSON.putPOJO("out", temp);

		temp = mapper.createArrayNode();
		temp.add(resultsJSON);
		ObjectNode json = mapper.createObjectNode();
		json.putPOJO("pods", temp);

		return json.toString();
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, String format) {
		// DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		StringWriter stw = new StringWriter();
		if (format.equals(PLAIN)) {
			stw.append(outExpr.toString());
		} else if (format.equals(MATHML)) {
			MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
			if (!mathUtil.toMathML(outExpr, stw, true)) {
				// return createJSONErrorString("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
			}

		}
		json.put(format, stw.toString());
	}

	private static ObjectNode createJSONResult(EvalEngine engine, IExpr outExpr, StringWriter outWriter,
			StringWriter errorWriter, String format) {
		ObjectMapper mapper = new ObjectMapper();
		// DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		StringWriter stw = new StringWriter();
		// DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
		if (format.equals(PLAIN)) {
			stw.append(outExpr.toString());
		} else if (format.equals(MATHML)) {
			MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
			if (!mathUtil.toMathML(outExpr, stw, true)) {
				return createJSONErrorString("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
			}
		}

		ObjectNode resultsJSON = mapper.createObjectNode();
		resultsJSON.put("line", new Integer(21));
		resultsJSON.put("result", stw.toString());
		ArrayNode temp = mapper.createArrayNode();
		String message = errorWriter.toString();
		if (message.length() > 0) {
			// "out": [{
			// "prefix": "Power::infy",
			// "message": true,
			// "tag": "infy",
			// "symbol": "Power",
			// "text": "Infinite expression 1 / 0 encountered."}]}]}
			ObjectNode messageJSON = mapper.createObjectNode();
			messageJSON.put("prefix", "Error");
			messageJSON.put("message", Boolean.TRUE);
			messageJSON.put("tag", "evaluation");
			messageJSON.put("symbol", "General");
			messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
			temp.add(messageJSON);
		}

		message = outWriter.toString();
		if (message.length() > 0) {
			ObjectNode messageJSON = mapper.createObjectNode();
			messageJSON.put("prefix", "Output");
			messageJSON.put("message", Boolean.TRUE);
			messageJSON.put("tag", "evaluation");
			messageJSON.put("symbol", "General");
			messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
			temp.add(messageJSON);
		}
		resultsJSON.putPOJO("out", temp);

		temp = mapper.createArrayNode();
		temp.add(resultsJSON);
		ObjectNode json = mapper.createObjectNode();
		json.putPOJO("results", temp);
		return json;
	}
}
