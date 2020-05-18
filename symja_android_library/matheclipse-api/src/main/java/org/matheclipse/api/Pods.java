package org.matheclipse.api;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.SyntaxError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Pods {
	// output formats
	public static final String HTML_STR = "html";
	public static final String PLAIN_STR = "plaintext";
	public static final String SYMJA_STR = "sinput";
	public static final String MATHML_STR = "mathml";
	public static final String LATEX_STR = "latex";
	public static final String MARKDOWN_STR = "markdown";
	public static final String MATHCELL_STR = "mathcell";

	public static final int HTML = 0x0001;
	public static final int PLAIN = 0x0002;
	public static final int SYMJA = 0x0004;
	public static final int MATHML = 0x0008;
	public static final int LATEX = 0x0010;
	public static final int MARKDOWN = 0x0020;
	public static final int MATHCELL = 0x0040;

	// output
	public static final String JSON = "JSON";

	static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title, String scanner, int formats,
			ObjectMapper mapper, EvalEngine engine) {
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
		createJSONFormat(node, engine, outExpr, formats);
	}

	static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String text, String title, String scanner,
			int formats, ObjectMapper mapper, EvalEngine engine) {
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
		createJSONFormat(node, engine, outExpr, text, formats);
	}

	static void integerPropertiesPod(ArrayNode podsArray, IInteger inExpr, IExpr outExpr, String title, String scanner,
			int formats, ObjectMapper mapper, EvalEngine engine) {
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
				createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an even number.", PLAIN);

				numsubpods++;
			} else {
				ObjectNode node = mapper.createObjectNode();
				temp.add(node);
				createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an odd number.", PLAIN);

				numsubpods++;
			}
			if (inExpr.isProbablePrime()) {
				IExpr primePi = F.PrimePi.of(engine, inExpr);
				if (primePi.isInteger()) {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					createJSONFormat(node, engine, F.NIL,
							inExpr.toString() + " the " + primePi.toString() + "th prime number.", PLAIN);
					numsubpods++;
				} else {
					ObjectNode node = mapper.createObjectNode();
					temp.add(node);
					createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is a prime number.", PLAIN);
					numsubpods++;
				}
			}
		} finally {
			subpodsResult.put("numsubpods", numsubpods);
		}
	}

	public static int internFormat(String[] formats) {
		int intern = 0;
		for (String str : formats) {
			if (str.equals(HTML_STR)) {
				intern |= HTML;
			} else if (str.equals(PLAIN_STR)) {
				intern |= PLAIN;
			} else if (str.equals(SYMJA_STR)) {
				intern |= SYMJA;
			} else if (str.equals(MATHML_STR)) {
				intern |= MATHML;
			} else if (str.equals(LATEX_STR)) {
				intern |= LATEX;
			} else if (str.equals(MARKDOWN_STR)) {
				intern |= MARKDOWN;
			}
		}
		return intern;
	}

	public static ObjectNode createResult(String inputStr, String[] formatStrs) {
		int formats = internFormat(formatStrs);

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

		inputStr = inputStr.trim();
		if (inputStr.length() > 0) {
			try {
				ArrayNode podsArray = mapper.createArrayNode();
				engine.setPackageMode(false);
				inExpr = engine.parse(inputStr);
				if (inExpr.isNumber()) {
					outExpr = inExpr;
					if (outExpr.isInteger()) {
						numpods = integerPods(podsArray, inExpr, outExpr, formats, mapper, engine);
						resultStatistics(queryresult, error, numpods, podsArray);
						return messageJSON;
					}
				} else {
					if (inExpr.isSymbol() || inExpr.isString()) {
						StringBuilder buf = new StringBuilder();
						Documentation.printDocumentation(buf, inExpr.toString());
						if (buf.length() > 0) {
							ArrayNode temp = mapper.createArrayNode();
							ObjectNode subpodsResult = mapper.createObjectNode();
							subpodsResult.put("title", "documentation");
							subpodsResult.put("scanner", "help");
							subpodsResult.put("error", "false");
							subpodsResult.put("numsubpods", 1);
							subpodsResult.putPOJO("subpods", temp);
							podsArray.add(subpodsResult);

							ObjectNode node = mapper.createObjectNode();
							if ((formats & HTML) != 0x00) {
								temp.add(node);
								node.put("html", generateHTMLString(buf.toString()));
								numpods++;
							} else {
								temp.add(node);
								node.put("markdown", buf.toString());
								numpods++;
							}

							resultStatistics(queryresult, error, numpods, podsArray);
							return messageJSON;
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
							resultStatistics(queryresult, error, numpods, podsArray);
							return messageJSON;
						} else {
							outExpr = engine.evaluate(inExpr);
							if (outExpr.isAST(F.JSFormData, 3)) {
								IExpr podOut = outExpr;
								addPod(podsArray, inExpr, podOut, outExpr.first().toString(), "Function", "Plotter",
										MATHCELL, mapper, engine);
								numpods++;
							} else {
								IExpr podOut = outExpr;
								addPod(podsArray, inExpr, podOut, "Input", "Identity", formats, mapper, engine);
								numpods++;
							}

							resultStatistics(queryresult, error, numpods, podsArray);
							return messageJSON;
						}
					}
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
		}
		queryresult.put("error", error ? "true" : "false");
		return messageJSON;
	}

	private static int integerPods(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, int formats, ObjectMapper mapper,
			EvalEngine engine) {
		int numpods = 0;
		IInteger n = (IInteger) outExpr;
		IExpr podOut = F.BaseForm.of(engine, inExpr, F.C2);
		addPod(podsArray, inExpr, podOut, "Binary form", "Integer", formats, mapper, engine);
		numpods++;

		podOut = F.FactorInteger.of(engine, inExpr);
		addPod(podsArray, inExpr, podOut, "Prime factorization", "Integer", formats, mapper, engine);
		numpods++;

		podOut = F.Mod.of(engine, inExpr, F.Range(F.C2, F.C9));
		addPod(podsArray, inExpr, podOut, "Residues modulo small integers", "Integer", formats, mapper, engine);
		numpods++;

		integerPropertiesPod(podsArray, (IInteger) inExpr, podOut, "Properties", "Integer", formats, mapper, engine);
		numpods++;

		if (n.isPositive() && n.isLT(F.ZZ(100))) {
			podOut = F.Union.of(engine, F.PowerMod(F.Range(F.C0, F.QQ(n, F.C2)), F.C2, n));
			addPod(podsArray, inExpr, podOut, "Quadratic residues modulo " + n.toString(), "Integer", formats, mapper,
					engine);
			numpods++;

			if (n.isProbablePrime()) {
				podOut = F.Select.of(engine, F.Range(n.add(F.CN1)),
						F.Function(F.Equal(F.MultiplicativeOrder(F.Slot1, n), F.EulerPhi(n))));
				addPod(podsArray, inExpr, podOut, "Primitive roots modulo " + n.toString(), "Integer", formats, mapper,
						engine);
				numpods++;
			}
		}
		return numpods;
	}

	private static void resultStatistics(ObjectNode queryresult, boolean error, int numpods, ArrayNode podsArray) {
		queryresult.putPOJO("pods", podsArray);
		queryresult.put("success", "true");
		queryresult.put("error", error ? "true" : "false");
		queryresult.put("numpods", numpods);
	}

	static ObjectNode createJSONErrorString(String str) {
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

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, int formats) {
		createJSONFormat(json, engine, outExpr, null, formats);
	}

	private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr, String plainText,
			int formats) {

		if ((formats & HTML) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(HTML_STR, plainText);
			}
		}

		if ((formats & PLAIN) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(PLAIN_STR, plainText);
			} else {
				StringWriter stw = new StringWriter();
				stw.append(outExpr.toString());
				json.put(PLAIN_STR, stw.toString());
			}
		}
		if ((formats & SYMJA) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(SYMJA_STR, plainText);
			}
		}
		if ((formats & MATHML) != 0x00) {
			StringWriter stw = new StringWriter();
			MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
			if (!mathUtil.toMathML(outExpr, stw, true)) {
				// return createJSONErrorString("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
			} else {
				json.put(MATHML_STR, stw.toString());
			}
		}
		if ((formats & LATEX) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(MARKDOWN_STR, plainText);
			} else {

			}
		}
		if ((formats & MARKDOWN) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(MARKDOWN_STR, plainText);
			} else {

			}
		}
		if ((formats & MATHCELL) != 0x00) {
			if (plainText != null && plainText.length() > 0) {
				json.put(MATHCELL_STR, plainText);
			} else {

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

	private static String generateHTMLString(final String markdownStr) {
		Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
		Parser parser = Parser.builder().extensions(EXTENSIONS).build();
		Node document = parser.parse(markdownStr);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
		return renderer.render(document); // "<p>This is <em>Sparta</em></p>\n"
	}
}
