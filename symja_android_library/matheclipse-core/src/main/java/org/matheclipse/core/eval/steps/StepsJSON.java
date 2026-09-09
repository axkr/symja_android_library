package org.matheclipse.core.eval.steps;

import java.util.function.Function;
import org.matheclipse.core.eval.steps.output.JSONStep;
import org.matheclipse.core.eval.steps.output.JSONStepsTemplate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Renders the steps which {@link org.matheclipse.core.expression.S#TraceForm} collected as JSON,
 * for a client which lays them out itself.
 *
 * <p>
 * Every expression travels as TeX and every description as a sentence whose formulas are wrapped in
 * <code>\(</code>...<code>\)</code>, which is what both KaTeX (in the notebook) and MathJax (on the
 * pods page) typeset in place.
 */
public final class StepsJSON {

  /** The delimiters KaTeX' auto-render and MathJax' tex2jax both recognise for inline math. */
  public static final String OPEN_MATH = "\\(";

  public static final String CLOSE_MATH = "\\)";

  private StepsJSON() {}

  /**
   * The steps of a built <code>TraceForm(...)</code> as the data transfer objects, for a caller
   * which serializes them itself.
   *
   * @param traceForm the built <code>TraceForm(HoldForm(result), {step...})</code>
   */
  public static JSONStepsTemplate toTemplate(IAST traceForm) {
    final Function<IExpr, String> tex = StepDescription.texRenderer();
    JSONStepsTemplate template = new JSONStepsTemplate();
    IAST steps = StepsTree.steps(traceForm);
    for (int i = 1; i < steps.size(); i++) {
      IExpr step = steps.get(i);
      if (StepsTree.isStep(step)) {
        template.add(toStep((IAST) step, tex));
      }
    }
    return template;
  }

  private static JSONStep toStep(IAST step, Function<IExpr, String> tex) {
    JSONStep node = new JSONStep();
    node.setStepKey(StepsTree.descriptionKey(step));
    node.setStep(StepDescription.of(step, tex, OPEN_MATH, CLOSE_MATH));
    if (!StepsTree.isTruncated(step) && !StepsTree.isInfoStep(step)) {
      // a marker and an annotation have no rewrite to show, only a sentence - the same rule
      // `toJSONArray` follows
      node.setPrevExpression(tex.apply(StepsTree.input(step)));
      node.setExpression(tex.apply(StepsTree.result(step)));
    }
    IAST subSteps = StepsTree.subSteps(step);
    for (int i = 1; i < subSteps.size(); i++) {
      IExpr subStep = subSteps.get(i);
      if (StepsTree.isStep(subStep)) {
        node.addSubSteps(java.util.Collections.singletonList(toStep((IAST) subStep, tex)));
      }
    }
    return node;
  }

  /**
   * The steps of a built <code>TraceForm(...)</code> as a Jackson tree, ready to be put under the
   * <code>"steps"</code> key of a servlet answer.
   *
   * <pre>
   * {"expression": "&lt;TeX of the result&gt;",
   *  "steps": [{"stepKey": "D::ChainRule", "step": "Apply the chain rule ...",
   *             "prevExpression": "&lt;TeX&gt;", "expression": "&lt;TeX&gt;",
   *             "truncated": false, "subSteps": [...]}]}
   * </pre>
   *
   * @param mapper the object mapper the caller builds its answer with
   * @param traceForm the built <code>TraceForm(HoldForm(result), {step...})</code>
   */
  public static ObjectNode toJSON(ObjectMapper mapper, IAST traceForm) {
    final Function<IExpr, String> tex = StepDescription.texRenderer();
    ObjectNode json = mapper.createObjectNode();
    json.put("expression", tex.apply(StepsTree.traceResult(traceForm)));
    json.putPOJO("steps", toJSONArray(mapper, StepsTree.steps(traceForm), tex));
    return json;
  }

  private static ArrayNode toJSONArray(ObjectMapper mapper, IAST steps,
      Function<IExpr, String> tex) {
    ArrayNode array = mapper.createArrayNode();
    for (int i = 1; i < steps.size(); i++) {
      IExpr step = steps.get(i);
      if (!StepsTree.isStep(step)) {
        continue;
      }
      IAST stepAST = (IAST) step;
      ObjectNode node = mapper.createObjectNode();
      node.put("stepKey", StepsTree.descriptionKey(stepAST));
      node.put("step", StepDescription.of(stepAST, tex, OPEN_MATH, CLOSE_MATH));
      node.put("truncated", StepsTree.isTruncated(stepAST));
      if (!StepsTree.isTruncated(stepAST) && !StepsTree.isInfoStep(stepAST)) {
        node.put("prevExpression", tex.apply(StepsTree.input(stepAST)));
        node.put("expression", tex.apply(StepsTree.result(stepAST)));
      }
      IAST subSteps = StepsTree.subSteps(stepAST);
      if (subSteps.size() > 1) {
        node.putPOJO("subSteps", toJSONArray(mapper, subSteps, tex));
      }
      array.add(node);
    }
    return array;
  }

  /**
   * The steps as indented plain text, one line per step: the sentence, then
   * <code>input -&gt; result</code>.
   */
  public static String toPlainText(IAST traceForm) {
    StringBuilder buf = new StringBuilder();
    appendPlainText(buf, StepsTree.steps(traceForm), 0);
    return buf.toString();
  }

  private static void appendPlainText(StringBuilder buf, IAST steps, int indent) {
    for (int i = 1; i < steps.size(); i++) {
      IExpr step = steps.get(i);
      if (!StepsTree.isStep(step)) {
        continue;
      }
      IAST stepAST = (IAST) step;
      String description =
          StepDescription.of(stepAST, StepDescription.PLAIN_TEXT, "", "");
      if (!description.isEmpty()) {
        indent(buf, indent).append(description).append('\n');
      }
      if (!StepsTree.isTruncated(stepAST) && !StepsTree.isInfoStep(stepAST)) {
        indent(buf, indent).append(StepsTree.input(stepAST).toString()).append(" -> ")
            .append(StepsTree.result(stepAST).toString()).append('\n');
      }
      appendPlainText(buf, StepsTree.subSteps(stepAST), indent + 1);
    }
  }

  private static StringBuilder indent(StringBuilder buf, int indent) {
    for (int i = 0; i < indent; i++) {
      buf.append("  ");
    }
    return buf;
  }
}
