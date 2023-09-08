package org.matheclipse.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.PodDefaultsRules;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Suppliers;

public class DocumentationPod implements IPod {

  static com.google.common.base.Supplier<Map<IExpr, IAST>> LAZY_DEFAULTS_SUPPLIER =
      new com.google.common.base.Supplier<Map<IExpr, IAST>>() {

        @Override
        public Map<IExpr, IAST> get() {
          HashMap<IExpr, IAST> defaultParameters = new HashMap<IExpr, IAST>();
          for (int i = 1; i < PodDefaultsRules.RULES.size(); i++) {
            IExpr arg = PodDefaultsRules.RULES.get(i);
            if (arg.isAST(S.SetDelayed, 3)) {
              defaultParameters.put(arg.first(), (IAST) arg.second());
            } else if (arg.isAST(S.Set, 3)) {
              defaultParameters.put(arg.first(), (IAST) arg.second());
            }
          }
          return defaultParameters;
        }
      };

  private static com.google.common.base.Supplier<Map<IExpr, IAST>> LAZY_DEFAULTS =
      Suppliers.memoize(LAZY_DEFAULTS_SUPPLIER);

  private static Map<IExpr, IAST> getMap() {
    return LAZY_DEFAULTS.get();
  }

  ISymbol symbol;
  IAST parameters;

  public DocumentationPod(ISymbol symbol) {
    this.symbol = symbol; // F.symbol(word, Context.SYSTEM_CONTEXT_NAME, null, EvalEngine.get());
    this.parameters = getMap().get(symbol);
  }

  @Override
  public short podType() {
    return DOCUMENTATION;
  }

  @Override
  public String keyWord() {
    return symbol.toString();
  }

  @Override
  public int addJSON(ArrayNode podsArray, int formats, EvalEngine engine) {
    StringBuilder buf = new StringBuilder();
    if (Documentation.getMarkdown(buf, keyWord())) {
      return addDocumentationPod(this, podsArray, buf, formats);
    }
    return 0;
  }

  protected static int addDocumentationPod(DocumentationPod pod, ArrayNode podsArray,
      StringBuilder buf, int formats) {
    int numpods = 0;
    if (pod.parameters != null) {
      IAST plotFunction = F.unaryAST1(pod.symbol, F.Times(S.a, S.x));
      if (pod.parameters.arg5() == S.Complexes) {
        // print real and imaginary part separately
        plotFunction = F.List(F.Re(plotFunction), F.Im(plotFunction));
      }
      IExpr plot2D = F.Manipulate(F.Plot(plotFunction, //
          F.List(S.x, pod.parameters.arg1(), pod.parameters.arg2()), //
          F.Rule(S.PlotRange, //
              F.List(pod.parameters.arg3(), pod.parameters.arg4()))), //
          F.List(S.a, F.C1, F.C10));
      EvalEngine engine = EvalEngine.get();
      IExpr podOut = engine.evaluate(plot2D);
      if (podOut.isAST(S.JSFormData, 3)) {
        int form = Pods.internFormat(Pods.SYMJA, podOut.second().toString());
        Pods.addPod(podsArray, plot2D, podOut, podOut.first().toString(),
            StringFunctions.inputForm(plot2D), "Plot", "Plotter", form, engine);
        ++numpods;
      }
    }

    ArrayNode temp = Pods.JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = Pods.JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", "Documentation");
    subpodsResult.put("scanner", "help");
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);
    ObjectNode node = Pods.JSON_OBJECT_MAPPER.createObjectNode();
    // if ((formats & HTML) != 0x00) {
    temp.add(node);
    // node.put("html", generateHTMLString(buf.toString()));
    node.put("markdown", buf.toString());
    if ((formats & Pods.HTML) != 0x00) {
      node.put("html", generateHTMLString(buf.toString()));
    }
    return ++numpods;
  }

  private static String generateHTMLString(final String markdownStr) {
    Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
    Parser parser = Parser.builder().extensions(EXTENSIONS).build();
    Node document = parser.parse(markdownStr);
    HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
    return renderer.render(document);
  }
}
