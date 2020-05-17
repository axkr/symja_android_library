package org.matheclipse.api;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Deque;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.SyntaxError;

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

				// exchange.getResponseSender().send(createJSONJavaScript(inExpr.toString()));
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode messageJSON = mapper.createObjectNode();

				ObjectNode queryresult = mapper.createObjectNode();
				messageJSON.putPOJO("queryresult", queryresult);

				IExpr inExpr = F.Null;
				IExpr outExpr = F.Null;
				EvalEngine engine = EvalEngine.get();
				boolean error = false;
				int numpods = 0;
				queryresult.put("success", "false");
				queryresult.put("error", "false");
				queryresult.put("numpods", numpods);
				queryresult.put("version", "0.1");
				try {
					ArrayNode podsArray = mapper.createArrayNode();
					engine.setPackageMode(false);
					inExpr = engine.parse(inputStr);
					if (inExpr.isNumber()) {
						outExpr = inExpr;
						if (outExpr.isInteger()) {
							IInteger n = (IInteger) outExpr;
							IExpr podOut = F.BaseForm.of(engine, inExpr, F.C2);
							addPod(podsArray, inExpr, podOut, "Binary form", "Integer", formats, mapper, engine);
							numpods++;
							podOut = F.FactorInteger.of(engine, inExpr);
							addPod(podsArray, inExpr, podOut, "Prime factorization", "Integer", formats, mapper,
									engine);
							numpods++;
							podOut = F.Mod.of(engine, inExpr, F.Range(F.C2, F.C9));
							addPod(podsArray, inExpr, podOut, "Residues modulo small integers", "Integer", formats,
									mapper, engine);
							numpods++;

							addIntegerPropertiesPod(podsArray, (IInteger) inExpr, podOut, "Properties", "Integer",
									formats, mapper, engine);
							numpods++;

							queryresult.putPOJO("pods", podsArray);
							queryresult.put("success", "true");
							queryresult.put("error", error ? "true" : "false");
							queryresult.put("numpods", numpods);
							final String jsonStr = messageJSON.toString();
							// System.out.println(jsonStr);
							exchange.getResponseSender().send(jsonStr);
							return;
						}
					} else {
						if (inExpr.isAST(F.D, 3)) {
							outExpr = engine.evaluate(inExpr);
							IExpr podOut = outExpr;
							addPod(podsArray, inExpr, podOut, "Derivative", "Derivative", formats, mapper, engine);
							numpods++;
							podOut = F.TrigToExp.of(engine, outExpr);
							if (!F.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
								addPod(podsArray, inExpr, podOut, "Alternate form", "Simplification", formats, mapper,
										engine);
								numpods++;
							}
						} else {
							outExpr = engine.evaluate(inExpr);
							if (outExpr != F.Null) {
								IExpr podOut = outExpr;
								addPod(podsArray, inExpr, podOut, "Input", "Identity", formats, mapper, engine);
								numpods++;
							}
						}

						queryresult.putPOJO("pods", podsArray);
						queryresult.put("success", "true");
						queryresult.put("error", error ? "true" : "false");
						queryresult.put("numpods", numpods);
						final String jsonStr = messageJSON.toString();
						// System.out.println(jsonStr);
						exchange.getResponseSender().send(jsonStr);
						return;
					}
				} catch (SyntaxError se) {
					se.printStackTrace();
					error = false;
					outExpr = F.$Aborted;
				} catch (RuntimeException rex) {
					rex.printStackTrace();
					error = true;
					outExpr = F.$Aborted;
				}
				queryresult.put("error", error ? "true" : "false");
				final String jsonStr = messageJSON.toString();
				// System.out.println(jsonStr);
				exchange.getResponseSender().send(jsonStr);
			}

		}).build();
		server.start();
		System.out.println("started");
	}

	private static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title, String scanner,
			String[] formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		ObjectNode node = mapper.createObjectNode();
		temp.add(node);

		for (int i = 0; i < formats.length; i++) {
			createJSONFormat(node, engine, outExpr, formats[i]);
		}
	}

	private static void addIntegerPropertiesPod(ArrayNode podsArray, IInteger inExpr, IExpr outExpr, String title,
			String scanner, String[] formats, ObjectMapper mapper, EvalEngine engine) {
		ArrayNode temp = mapper.createArrayNode();
		int numsubpods = 0;
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", title);
		subpodsResult.put("scanner", scanner);
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", numsubpods);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);

		try {
			if (inExpr.isEven()) {
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				for (int i = 0; i < formats.length; i++) {
					createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an even number.", formats[i]);
				}
				numsubpods++;
			} else {
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				for (int i = 0; i < formats.length; i++) {
					createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an odd number.", formats[i]);
				}
				numsubpods++;
			}
			if (inExpr.isProbablePrime()) {
				IExpr primePi = F.PrimePi.of(engine, inExpr);
				if (primePi.isInteger()) {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					for (int i = 0; i < formats.length; i++) {
						createJSONFormat(node, engine, F.NIL,
								inExpr.toString() + " the " + primePi.toString() + "th prime number.", formats[i]);
					}
					numsubpods++;
				} else {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					for (int i = 0; i < formats.length; i++) {
						createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is a prime number.", formats[i]);
					}
					numsubpods++;
				}
			}
		} finally {
			subpodsResult.put("numsubpods", numsubpods);
		}
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

	private static ObjectNode createJSONErrorString(String str) {
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

	private static String createJSONJavaScript(String script) throws IOException {
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
		StringWriter stw = new StringWriter();
		if (format.equals(PLAIN)) {
			stw.append(outExpr.toString());
			json.put(format, stw.toString());
		} else if (format.equals(MATHML)) {
			MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
			if (!mathUtil.toMathML(outExpr, stw, true)) {
				// return createJSONErrorString("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
			} else {
				json.put(format, stw.toString());
			}
		}
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, String plainText,
			String format) {
		StringWriter stw = new StringWriter();
		if (format.equals(PLAIN)) {
			json.put(format, plainText);
		} else if (format.equals(MATHML)) {
			if (outExpr.isPresent()) {
				MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
				if (!mathUtil.toMathML(outExpr, stw, true)) {
					// return createJSONErrorString("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
				} else {
					json.put(format, stw.toString());
				}
			}
		}

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
