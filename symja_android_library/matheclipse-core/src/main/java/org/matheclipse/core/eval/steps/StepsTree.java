package org.matheclipse.core.eval.steps;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Reads the expression which {@link org.matheclipse.core.expression.S#TraceForm} evaluates to.
 *
 * <p>
 * The built form is
 *
 * <pre>
 * TraceForm(HoldForm(result), {step, ...})
 * step := {HoldForm(input), HoldForm(result), {headSymbol, "RuleKey", hintArg...}, {subStep...}}
 * </pre>
 *
 * It stays in the expression tree the way <code>TableForm</code> does, so every output factory can
 * render it and a user can take it apart with <code>Part</code>, <code>Cases</code> or
 * <code>Flatten</code>. Both the input and the result of a step are wrapped in
 * {@link org.matheclipse.core.expression.S#HoldForm} so that taking the tree apart never evaluates
 * them again.
 *
 * <p>
 * <code>headSymbol::RuleKey</code> is the key of the human readable description in
 * <code>i18n/en.json</code>, see {@link StepDescription}.
 */
public final class StepsTree {

  /** The rule key of the marker step which says that sub-steps were dropped. */
  public static final String TRUNCATED_KEY = "Truncated";

  /** The rule key used when a step carries no more specific one. */
  public static final String REWRITE_RULE_KEY = "RewriteRule";

  private StepsTree() {}

  /** Is this the built form <code>TraceForm(HoldForm(result), {step...})</code>? */
  public static boolean isTraceForm(IExpr expr) {
    if (expr.isAST(S.TraceForm, 3)) {
      IAST ast = (IAST) expr;
      return ast.arg2().isList();
    }
    return false;
  }

  /** The result of the traced evaluation, unwrapped from its <code>HoldForm</code>. */
  public static IExpr traceResult(IAST traceForm) {
    IExpr arg1 = traceForm.arg1();
    return arg1.isAST(S.HoldForm, 2) ? arg1.first() : arg1;
  }

  /** The top level steps. */
  public static IAST steps(IAST traceForm) {
    return (IAST) traceForm.arg2();
  }

  /** Is this a well formed step? */
  public static boolean isStep(IExpr expr) {
    if (expr.isList() && ((IAST) expr).argSize() == 4) {
      IAST step = (IAST) expr;
      return step.arg3().isList() && step.arg4().isList();
    }
    return false;
  }

  /** The expression the step started from, unwrapped from its <code>HoldForm</code>. */
  public static IExpr input(IAST step) {
    IExpr arg1 = step.arg1();
    return arg1.isAST(S.HoldForm, 2) ? arg1.first() : arg1;
  }

  /** The expression the step produced, unwrapped from its <code>HoldForm</code>. */
  public static IExpr result(IAST step) {
    IExpr arg2 = step.arg2();
    return arg2.isAST(S.HoldForm, 2) ? arg2.first() : arg2;
  }

  /** <code>{headSymbol, "RuleKey", hintArg...}</code>. */
  public static IAST hints(IAST step) {
    return (IAST) step.arg3();
  }

  /** The steps caused by this step. */
  public static IAST subSteps(IAST step) {
    return (IAST) step.arg4();
  }

  /** The symbol the step belongs to, or {@link S#Null} for a malformed hint list. */
  public static ISymbol symbol(IAST step) {
    IAST hints = hints(step);
    IExpr first = hints.isEmpty() ? F.NIL : hints.first();
    return first.isSymbol() ? (ISymbol) first : S.Null;
  }

  /** The rule key of the step, or <code>""</code> for a malformed hint list. */
  public static String ruleKey(IAST step) {
    IAST hints = hints(step);
    return hints.argSize() >= 2 ? hints.arg2().toString() : "";
  }

  /**
   * The key of the description of this step in <code>i18n/en.json</code>, for example
   * <code>D::ChainRule</code>.
   */
  public static String descriptionKey(IAST step) {
    return symbol(step).toString() + "::" + ruleKey(step);
  }

  /**
   * Is this step an annotation rather than a rewrite - something which happened <i>to</i> the
   * expression, described in words, with the expression itself unchanged?
   *
   * @see org.matheclipse.core.eval.EvalEngine#addTraceInfoStep(int, IExpr, IAST)
   */
  public static boolean isInfoStep(IAST step) {
    return input(step).equals(result(step));
  }

  /** Is this the marker step which says that further sub-steps were dropped? */
  public static boolean isTruncated(IAST step) {
    return symbol(step) == S.TraceForm && TRUNCATED_KEY.equals(ruleKey(step));
  }
}
