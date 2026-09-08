package org.matheclipse.core.eval.steps;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Turns an expression a rule set recorded into the expression a reader should see.
 *
 * <p>
 * A step is recorded from the right-hand side of the rule which fired, <i>before</i> the engine
 * evaluates it, so the Rubi integration rules record their own working:
 *
 * <pre>
 * -Rubi`simp(Rubi`dist(1/(1*(1-3)),Integrate((0+1*x)^(1-3)*Cos(0+1*x),x),x),x)
 *     + Rubi`simp(((0+1*x)^(1-3)*Sin(0+1*x))/(1*(1-3)),x)
 * </pre>
 *
 * which stands for
 *
 * <pre>
 * Integrate(Cos(x)/x^2,x)/2 - Sin(x)/(2*x^2)
 * </pre>
 *
 * <p>
 * Two things are in the way. The helpers carry no mathematics - <code>Simp[u,x]</code> is
 * <code>u</code> tidied up, <code>Dist[u,v,x]</code> is <code>u*v</code>, and <code>§sin</code> is
 * an inert <code>Sin</code> the rules use so that the trigonometric simplifiers keep out of their
 * way while they match - so they are removed by rewriting, not by evaluating. And the arithmetic is
 * left as the substitution produced it, so it has to be worked out; but simply evaluating would
 * integrate every integral in sight and hand back the answer instead of the step, because none of
 * these helpers holds its arguments. So every integral is held while everything around it is
 * evaluated, which is exactly the intermediate state a derivation wants to show.
 */
public final class StepDisplay {

  /** The inert heads the rules match on, and what they stand for. */
  private static final String[] INERT_TRIG =
      {"§sin", "§cos", "§tan", "§cot", "§sec", "§csc"};

  private static final ISymbol[] REAL_TRIG = {S.Sin, S.Cos, S.Tan, S.Cot, S.Sec, S.Csc};

  private StepDisplay() {}

  /**
   * The readable form of a recorded expression.
   *
   * <p>
   * Both sides of a step have to be put through this together as soon as either of them needs it -
   * ask {@link #carriesInternals(IExpr)} once for the pair. The arithmetic is worked out here too,
   * so running only one side leaves the two written differently, and a step which changed nothing
   * no longer looks like one.
   *
   * @param expr as the rule set recorded it
   * @return the mathematics it stands for, or <code>expr</code> unchanged if it cannot be worked
   *         out
   */
  public static IExpr normalize(IExpr expr) {
    if (expr == null || !expr.isPresent()) {
      return expr;
    }
    try {
      EvalEngine engine = EvalEngine.get();
      // with the collection switched off: tidying up a step is not itself a step
      return unhold(engine.evalTraceless(holdIntegrals(strip(expr), engine)));
    } catch (RuntimeException rex) {
      // an expression which cannot be tidied up is still better shown as it stands
      return expr;
    }
  }

  /**
   * Take off the guards {@link #holdIntegrals} put on. They were only there to stop the arithmetic
   * pass from working the integrals out; what is handed back is ordinary mathematics, and the step
   * tree holds it itself.
   */
  private static IExpr unhold(IExpr expr) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.HoldForm, 2) && ast.arg1().isAST(S.Integrate, 3)) {
      return unhold(ast.arg1());
    }
    IASTAppendable copy = F.ast(ast.head(), ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      copy.append(unhold(ast.get(i)));
    }
    return copy;
  }

  /**
   * Does this expression carry anything only the rule set understands? The test is cheap, so that a
   * derivation of something which never went near the integrator pays nothing.
   */
  public static boolean carriesInternals(IExpr expr) {
    return !expr.isFree(part -> {
      if (!part.isSymbol()) {
        return false;
      }
      ISymbol symbol = (ISymbol) part;
      return symbol.getContext() == Context.RUBI || symbol.getSymbolName().startsWith("§");
    }, true);
  }

  /**
   * Rewrite the rule set's helpers away, without evaluating anything.
   *
   * @see <a href=
   *      "https://github.com/RuleBasedIntegration/Rubi">Rubi</a>'s <code>Simp</code>,
   *      <code>Dist</code>, <code>DeactivateTrig</code> and <code>ActivateTrig</code>
   */
  private static IExpr strip(IExpr expr) {
    if (expr.isSymbol()) {
      String name = ((ISymbol) expr).getSymbolName();
      for (int i = 0; i < INERT_TRIG.length; i++) {
        if (INERT_TRIG[i].equals(name)) {
          return REAL_TRIG[i];
        }
      }
      return expr;
    }
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    IASTAppendable stripped = F.ast(strip(ast.head()), ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      stripped.append(strip(ast.get(i)));
    }
    IExpr head = stripped.head();
    if (!head.isSymbol() || ((ISymbol) head).getContext() != Context.RUBI) {
      return stripped;
    }
    String name = ((ISymbol) head).getSymbolName();
    if (name.equalsIgnoreCase("simp") && (stripped.isAST1() || stripped.isAST2())) {
      // Simp[u] and Simp[u,x] are u, tidied up
      return stripped.arg1();
    }
    if (name.equalsIgnoreCase("dist") && stripped.isAST3()) {
      // Dist[u,v,x] is the product u*v, written apart so a step can show the factor
      return F.Times(stripped.arg1(), stripped.arg2());
    }
    if (name.equalsIgnoreCase("deactivatetrig") && stripped.isAST2()) {
      return stripped.arg1();
    }
    if (name.equalsIgnoreCase("subst") && stripped.isAST3()) {
      // Subst[u,x,v] substitutes v for x in u. Unlike the helpers above this one carries
      // mathematics, so it is rewritten into ordinary replacement notation rather than removed -
      // and held, because a step is the substitution still to be made. Carrying it out here would
      // put Cos(x) inside an integral taken with respect to x and read as nonsense.
      return F.HoldForm(F.ReplaceAll(stripped.arg1(), F.Rule(stripped.arg2(), stripped.arg3())));
    }
    return stripped;
  }

  /**
   * Hold every integral, and tidy up its integrand, so that working out the arithmetic around it
   * does not work out the integral itself.
   */
  private static IExpr holdIntegrals(IExpr expr, EvalEngine engine) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Integrate, 3)) {
      IExpr integrand = engine.evalTraceless(holdIntegrals(ast.arg1(), engine));
      return F.HoldForm(F.Integrate(integrand, ast.arg2()));
    }
    if (ast.isAST(S.HoldForm, 2) && ast.arg1().isAST(S.ReplaceAll, 3)) {
      // a substitution still to be made. The replacement itself is held - carrying it out would
      // reach inside the integral it is waiting for - so its two halves are tidied up separately.
      IAST replace = (IAST) ast.arg1();
      IExpr target = holdIntegrals(replace.arg1(), engine);
      IExpr rule = replace.arg2();
      if (rule.isRule()) {
        rule = F.Rule(rule.first(), engine.evalTraceless(rule.second()));
      }
      return F.HoldForm(F.ReplaceAll(target, rule));
    }
    IASTAppendable held = F.ast(ast.head(), ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      held.append(holdIntegrals(ast.get(i), engine));
    }
    return held;
  }
}
