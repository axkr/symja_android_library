package org.matheclipse.core.eval.steps;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.steps.output.JSONStep;
import org.matheclipse.core.eval.steps.output.JSONStepsTemplate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class TraceInfoJSON {
  private final RuleDescription descriptionTemplates;
  private final TeXFormFactory texUtil;

  public TraceInfoJSON(RuleDescription descriptionTemplates) {
    this.descriptionTemplates = descriptionTemplates;
    // int exponentFigures = engine.getSignificantFigures() - 1;
    // int significantFigures = engine.getSignificantFigures() + 1;
    this.texUtil = new TeXFormFactory(true, -1, -1, " \\times ");
  }

  public JSONStepsTemplate toJSONStepsListRecursive(Predicate<ISymbol> symbolFilter, int level,
      @Nonnull TraceInfo traceInfo) {
    JSONStepsTemplate stepsArray = new JSONStepsTemplate();

    JSONStep stepNode = null;

    if (traceInfo.info.argSize() > 1) { // {input, result, hints}
      final IExpr input = traceInfo.info.arg1();
      IExpr result = traceInfo.info.arg2();

      final IAST info = (IAST) traceInfo.info.arg3();
      final ISymbol builtinSymbol = (ISymbol) info.first();

      boolean isIgnore = false;
      if (symbolFilter != null && !symbolFilter.test(builtinSymbol)) {
        isIgnore = true;
      }

      if (result.isAST(S.HoldForm)) {
        IExpr holdFormArg1 = result.first();
        if (holdFormArg1 == S.Null) {
          isIgnore = true;
        }
        if (holdFormArg1.isCondition()) {
          // the `if-condition` in `Condition(rhs-result, if-condition)` is evaluated to true
          // here, otherwise no entry will be written in the trace.
          // For output only display `rhs-result`.
          // See: PatternMatcherAndEvaluator#checkRHSCondition()
          holdFormArg1 = holdFormArg1.first();
        }
        result = holdFormArg1;
      }

      if (!isIgnore) {
        stepNode = renderStepTemplateToJSON(input, result, info, builtinSymbol);
        stepsArray.add(stepNode);
      }

    }

    // Include sub-steps
    if (traceInfo.fTraceInfos.size() > 0) {

      if (stepNode == null) {
        final String inputTex = toTeX(traceInfo.input);
        final String resultTex = toTeX(traceInfo.result);
        stepNode = new JSONStep();
        {
          final String descriptionKey = "Evaluate";
          String stepDescriptionTemplate = determineStepDescriptionTemplate(descriptionKey);
          if (stepDescriptionTemplate.length() > 0) {
            stepDescriptionTemplate = renderStepDescriptionTemplate(stepDescriptionTemplate,
                resultTex, inputTex, F.List(F.Evaluate, F.$str(descriptionKey)));
          }
          stepNode.setStepKey(descriptionKey);
          stepNode.setStep(stepDescriptionTemplate);
        }
        stepNode.setExpression(resultTex);
        stepNode.setPrevExpression(inputTex);

        stepsArray.add(stepNode);
      }

      for (TraceInfo subTrace : traceInfo.fTraceInfos) {
        JSONStepsTemplate temp = toJSONStepsListRecursive(symbolFilter, level + 1, subTrace);
        if (temp.size() > 0) {
          stepNode.addSubSteps(temp.getSteps());
        }
      }
    }


    return stepsArray;
  }

  /**
   * Render one single JSON node for the mathsteps API from the associated template.
   *
   * @param input
   * @param result
   * @param additionalInformation
   * @param associatedSymbol
   * @return
   */
  private JSONStep renderStepTemplateToJSON(final IExpr input, final IExpr result,
      final IAST additionalInformation, final ISymbol associatedSymbol) {
    final String ruleKey = additionalInformation.second().toString();
    final String descriptionKey = associatedSymbol.toString() + "::" + ruleKey;
    String stepDescriptionTemplate = determineStepDescriptionTemplate(descriptionKey);

    final String prevExpressionTeX = toTeX(input);
    final String expressionTeX = toTeX(result);
    if (stepDescriptionTemplate.length() > 0) {
      stepDescriptionTemplate = renderStepDescriptionTemplate(stepDescriptionTemplate,
          expressionTeX, prevExpressionTeX, additionalInformation);
    }

    final JSONStep node = new JSONStep();
    node.setExpression(expressionTeX);
    node.setPrevExpression(prevExpressionTeX);
    node.setStepKey(descriptionKey);
    node.setStep(stepDescriptionTemplate);
    return node;
  }

  /**
   * Determine the template for rendering the steps description.
   *
   * @param descriptionKey the key for the first access to the {@link RuleDescription} map
   * @return <code>""</code> if no value was found for the keys
   */
  private String determineStepDescriptionTemplate(final String descriptionKey) {
    // first try
    String stepDescriptionTemplate = descriptionTemplates.get(descriptionKey);
    if (stepDescriptionTemplate == null) {
      return "";
    }
    return stepDescriptionTemplate;
  }

  /**
   * Replace the number parameters (<code>`1`, `2`, `3`...</code>) in the <code>
   * stepDescriptionTemplate</code> <a href="https://github.com/PebbleTemplates/pebble">Pebble
   * template</a> at index
   *
   * <ul>
   * <li><code>1</code> with the string <code>expressionAsTeX</code>.
   * <li><code>2</code> with the string <code>prevExpressionAsTeX</code>.
   * <li><code>i = 3 .. n</code> with the expresspon <code>infoParameters.get(i)</code> converted to
   * a TeX expression, with the exception that {@link IStringX} expressions aren't converted to TeX.
   * </ul>
   *
   * @param stepDescriptionTemplate
   * @param expressionAsTeX
   * @param prevExpressionAsTeX
   * @param infoParameters
   * @return
   */
  private String renderStepDescriptionTemplate(final String stepDescriptionTemplate,
      final String expressionAsTeX, final String prevExpressionAsTeX, final IAST infoParameters) {
    String[] templateParameters = new String[infoParameters.size() - 1];

    templateParameters[0] = expressionAsTeX;
    templateParameters[1] = prevExpressionAsTeX;
    for (int i = 3; i < infoParameters.size(); i++) {
      IExpr arg = infoParameters.get(i);
      if (arg.isString()) {
        templateParameters[i - 1] = arg.toString();
      } else {
        templateParameters[i - 1] = toTeX(arg);
      }
    }

    return Errors.templateRender(stepDescriptionTemplate, templateParameters);
  }

  /**
   * Convert the expression to a TeX string.
   *
   * @param expr
   * @return
   */
  public String toTeX(IExpr expr) {
    if (expr == null) {
      return "";
    }
    try {
      final StringBuilder out = new StringBuilder();
      out.append("$");
      if (!texUtil.convert(out, expr, 0)) {
        // fall back
        return expr.toString();
      }
      out.append("$");
      return out.toString();
    } catch (final RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    // fall back
    return expr.toString();
  }
}
