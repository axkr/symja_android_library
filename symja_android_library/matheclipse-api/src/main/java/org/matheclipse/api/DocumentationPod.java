package org.matheclipse.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.PodDefaultsRules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Suppliers;

public class DocumentationPod implements IPod, PodDefaultsRules {

	private static Supplier<Map<IExpr, IAST>> LAZY_DEFAULTS = Suppliers.memoize(DocumentationPod::init);

	private static Map<IExpr, IAST> init() {
		HashMap<IExpr, IAST> defaultParameters = new HashMap<IExpr, IAST>();
		for (int i = 1; i < RULES.size(); i++) {
			IExpr arg = RULES.get(i);
			if (arg.isAST(F.SetDelayed, 3)) {
				defaultParameters.put(arg.first(), (IAST) arg.second());
			} else if (arg.isAST(F.Set, 3)) {
				defaultParameters.put(arg.first(), (IAST) arg.second());
			}
		}
		return defaultParameters;
	}

	private static Map<IExpr, IAST> getMap() {
		return LAZY_DEFAULTS.get();
	}

	ISymbol symbol;
	IAST parameters;

	public DocumentationPod(ISymbol symbol) {
		this.symbol = symbol;// F.symbol(word, Context.SYSTEM_CONTEXT_NAME, null, EvalEngine.get());
		this.parameters = getMap().get(symbol);
	}

	public short podType() {
		return DOCUMENTATION;
	}

	public String keyWord() {
		return symbol.toString();
	}

	public int addJSON(ObjectMapper mapper, ArrayNode podsArray, int formats, EvalEngine engine) {
		StringBuilder buf = new StringBuilder();
		if (Documentation.getMarkdown(buf, keyWord())) {
			return addDocumentationPod(this, mapper, podsArray, buf, formats);
		}
		return 0;
	}

	protected static int addDocumentationPod(DocumentationPod pod, ObjectMapper mapper, ArrayNode podsArray,
			StringBuilder buf, int formats) {
		int numpods = 0;
		if (pod.parameters != null) {
			IExpr plot2D = F.Manipulate(F.Plot(F.unaryAST1(pod.symbol, F.Times(F.a, F.x)), //
					F.List(F.x, pod.parameters.arg1(), pod.parameters.arg2()), //
					F.Rule(F.PlotRange, //
							F.List(pod.parameters.arg3(), pod.parameters.arg4()))), //
					F.List(F.a, F.C1, F.C10));
			EvalEngine engine = EvalEngine.get();
			IExpr podOut = engine.evaluate(plot2D);
			if (podOut.isAST(F.JSFormData, 3)) {
				int form = Pods.internFormat(0, podOut.second().toString());
				Pods.addPod(podsArray, plot2D, podOut, podOut.first().toString(), "Plot", "Plotter", form, mapper,
						engine);
				numpods++;
			}
		}

		ArrayNode temp = mapper.createArrayNode();
		ObjectNode subpodsResult = mapper.createObjectNode();
		subpodsResult.put("title", "documentation");
		subpodsResult.put("scanner", "help");
		subpodsResult.put("error", "false");
		subpodsResult.put("numsubpods", 1);
		subpodsResult.putPOJO("subpods", temp);
		podsArray.add(subpodsResult);
		ObjectNode node = mapper.createObjectNode();
		// if ((formats & HTML) != 0x00) {
		temp.add(node);
		// node.put("html", generateHTMLString(buf.toString()));
		node.put("markdown", buf.toString());
		if ((formats & Pods.HTML) != 0x00) {
			node.put("html", generateHTMLString(buf.toString()));
		}
		return numpods++;
	}

	private static String generateHTMLString(final String markdownStr) {
		Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
		Parser parser = Parser.builder().extensions(EXTENSIONS).build();
		Node document = parser.parse(markdownStr);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
		return renderer.render(document);
	}
}
