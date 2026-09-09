package org.matheclipse.core.eval.steps;

import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Turns the hints of one recorded step into the sentence which explains it.
 *
 * <p>
 * The sentence comes from <code>i18n/en.json</code>, keyed by
 * <code>headSymbol::RuleKey</code> - for example <code>D::ChainRule</code>. Its
 * <code>`1`</code>, <code>`2`</code>, <code>`3`</code>... placeholders are filled with
 *
 * <ul>
 * <li><code>`1`</code> the result of the step,
 * <li><code>`2`</code> the expression the step started from,
 * <li><code>`3`</code> and up the further hint arguments, a string verbatim and anything else
 * rendered as mathematics.
 * </ul>
 *
 * <p>
 * How a mathematical expression is rendered and how it is delimited is the caller's choice, so the
 * same descriptions serve the plain text form, the TeX form, the MathML form and the JSON the
 * servlets send: pass <code>"$"</code>/<code>"$"</code> for a TeX array,
 * <code>"\\("</code>/<code>"\\)"</code> for HTML which KaTeX or MathJax typesets in place, and
 * <code>""</code>/<code>""</code> for plain text.
 */
public final class StepDescription {

  /** The rule key an integration step of the Rubi rule set carries. */
  private static final String RUBI_RULE_KEY = "Integrate::RubiRule";

  /** The sentence for a Rubi rule whose condition the rule set names. */
  private static final String RUBI_RULE_DESCRIPTION_KEY = "Integrate::RubiRuleDescription";

  /** The sentence for a Rubi rule which the rule set shows without a condition. */
  private static final String RUBI_RULE_REWRITE_KEY = "Integrate::RubiRuleRewrite";

  /** Renders an expression as plain <code>OutputForm</code> text. */
  public static final Function<IExpr, String> PLAIN_TEXT = IExpr::toString;

  private StepDescription() {}

  /** Renders an expression as TeX, without delimiters. */
  public static Function<IExpr, String> texRenderer() {
    final TeXFormFactory factory = new TeXFormFactory(true, -1, -1, " \\times ");
    return expr -> {
      if (expr == null) {
        return "";
      }
      try {
        StringBuilder out = new StringBuilder();
        if (factory.convert(out, expr, 0)) {
          return out.toString();
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      return expr.toString();
    };
  }

  /**
   * The sentence which explains one step.
   *
   * @param step one step of {@link StepsTree}
   * @param mathRenderer renders an expression, for example {@link #texRenderer()}
   * @param open put in front of every rendered expression, for example <code>"$"</code>
   * @param close put behind every rendered expression
   * @return <code>""</code> if no description is known for this step
   */
  public static String of(IAST step, Function<IExpr, String> mathRenderer, String open,
      String close) {
    if (StepsTree.isTruncated(step)) {
      // the key in `i18n/en.json` is the qualified one, `TraceForm::Truncated`; looking the bare
      // rule key up returned nothing and the derivation simply stopped without saying so
      return template(StepsTree.descriptionKey(step));
    }
    final IAST hints = StepsTree.hints(step);
    final String key = StepsTree.descriptionKey(step);
    IAST rubiHints = rubiRuleHints(key, hints);
    if (rubiHints.isPresent()) {
      // the rule set says what this rule does: use the sentence which has room for it
      String detailKey = rubiHints.last().toString().isEmpty() //
          ? RUBI_RULE_REWRITE_KEY
          : RUBI_RULE_DESCRIPTION_KEY;
      String detailTemplate = template(detailKey);
      if (!detailTemplate.isEmpty()) {
        return render(detailTemplate, StepsTree.input(step), StepsTree.result(step), rubiHints,
            mathRenderer, open, close);
      }
    }
    String descriptionTemplate = template(key);
    if (descriptionTemplate.isEmpty()) {
      // no sentence for this rule: fall back to the generic one for a rewrite
      descriptionTemplate = template(StepsTree.REWRITE_RULE_KEY);
      if (descriptionTemplate.isEmpty()) {
        return "";
      }
    }
    return render(descriptionTemplate, StepsTree.input(step), StepsTree.result(step), hints,
        mathRenderer, open, close);
  }

  /**
   * The hints of a Rubi integration step, with what the rule set says the rule does appended: the
   * general shape of the integral it matches at <code>`6`</code>, what it rewrites it to at
   * <code>`7`</code>, and the condition it applies under at <code>`8`</code>.
   *
   * @return {@link F#NIL} unless this is an integration rule the rule set describes
   */
  private static IAST rubiRuleHints(String descriptionKey, IAST hints) {
    if (!RUBI_RULE_KEY.equals(descriptionKey) || hints.argSize() < 3) {
      return F.NIL;
    }
    int ruleNumber = hints.arg3().toIntDefault(-1);
    if (ruleNumber < 0) {
      return F.NIL;
    }
    RubiStepDescriptions.Description description = RubiStepDescriptions.get(ruleNumber);
    if (description == null) {
      return F.NIL;
    }
    IExpr before = description.before();
    IExpr after = description.after();
    IASTAppendable extended = hints.copyAppendable(3);
    while (extended.argSize() < 5) {
      // the appended result of the rewrite is missing when the step was only recorded, not run
      extended.append(F.CEmptyString);
    }
    extended.append(before.isPresent() ? before : F.$str(description.beforeText()));
    extended.append(after.isPresent() ? after : F.$str(description.afterText()));
    extended.append(F.$str(description.condition()));
    return extended;
  }

  /** The raw template for a description key, or <code>""</code> if there is none. */
  public static String template(String descriptionKey) {
    RuleDescription descriptions = LocaleMap.get("en");
    if (descriptions == null) {
      return "";
    }
    String template = descriptions.get(descriptionKey);
    return template == null ? "" : template;
  }

  /**
   * Fill the placeholders of a description template.
   *
   * @param descriptionTemplate the template with its <code>`1`</code>... placeholders
   * @param input the expression the step started from
   * @param result the expression the step produced
   * @param hints <code>{headSymbol, "RuleKey", hintArg...}</code>
   */
  public static String render(String descriptionTemplate, IExpr input, IExpr result, IAST hints,
      Function<IExpr, String> mathRenderer, String open, String close) {
    final int argSize = Math.max(hints.argSize(), 2);
    String[] parameters = new String[argSize];
    parameters[0] = math(result, mathRenderer, open, close);
    parameters[1] = math(input, mathRenderer, open, close);
    for (int i = 3; i < hints.size(); i++) {
      IExpr arg = hints.get(i);
      // a string is a word of the sentence, not a formula
      parameters[i - 1] = arg.isString() ? arg.toString() : math(arg, mathRenderer, open, close);
    }
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i] == null) {
        parameters[i] = "";
      }
    }
    return Errors.templateRender(descriptionTemplate, parameters);
  }

  private static String math(IExpr expr, Function<IExpr, String> mathRenderer, String open,
      String close) {
    if (expr == null || !expr.isPresent()) {
      return "";
    }
    return open + mathRenderer.apply(expr) + close;
  }
}
