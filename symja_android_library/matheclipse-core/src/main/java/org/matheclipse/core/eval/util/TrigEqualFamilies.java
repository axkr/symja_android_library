package org.matheclipse.core.eval.util;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The solutions of <code>a*Sin(A) - a*Sin(B) == 0</code> or <code>a*Cos(A) - a*Cos(B) == 0</code>
 * with <code>A</code>, <code>B</code> linear in the variable: <code>A - B == 2*Pi*C(k)</code> or
 * <code>A + B == Pi + 2*Pi*C(k)</code> (<code>A + B == 2*Pi*C(k)</code> for <code>Cos</code>).
 * <code>Sin(A) == Cos(B)</code> is <code>Sin(A) == Sin(Pi/2 - B)</code>. Each equation is linear in
 * the variable; one in which the variable cancels holds for every value or for none.
 */
public final class TrigEqualFamilies {

  /** The integer parameter <code>C(k)</code> of the families. */
  public final IAST parameter;

  /** The values of the variable, each a family in {@link #parameter}. */
  public final List<IExpr> roots;

  /** Whether the equation holds for every value of the variable. */
  public final boolean everyValue;

  private TrigEqualFamilies(IAST parameter, List<IExpr> roots, boolean everyValue) {
    this.parameter = parameter;
    this.roots = roots;
    this.everyValue = everyValue;
  }

  /**
   * The families of solutions of <code>f == 0</code>.
   *
   * @return <code>null</code> if <code>f</code> isn't a difference of two such terms, or if it
   *         can't be decided whether an equation without the variable holds
   */
  public static TrigEqualFamilies of(IExpr f, IExpr variable, EvalEngine engine) {
    if (!f.isPlus() || f.argSize() != 2) {
      return null;
    }
    IExpr[] first = sinCosTerm(f.first(), variable);
    IExpr[] second = sinCosTerm(f.second(), variable);
    if (first == null || second == null
        || !engine.evaluate(F.Plus(first[0], second[0])).isZero()) {
      return null;
    }
    IExpr a = first[2];
    IExpr b = second[2];
    boolean sin = first[1] == S.Sin;
    if (first[1] != second[1]) {
      // the Cos term as a Sin term
      if (first[1] == S.Cos) {
        a = F.Subtract(F.CPiHalf, a);
      } else {
        b = F.Subtract(F.CPiHalf, b);
      }
      sin = true;
    }
    IAST k = F.C(engine.incConstantCounter());
    IExpr period = F.Times(F.C2Pi, k);
    IExpr[][] families = {{F.Subtract(a, b), period},
        {F.Plus(a, b), sin ? F.Plus(S.Pi, period) : period}};
    List<IExpr> roots = new ArrayList<IExpr>(2);
    for (IExpr[] family : families) {
      IExpr[] linear = engine.evaluate(family[0]).linear(variable);
      if (linear == null) {
        return null;
      }
      if (linear[1].isZero()) {
        // the variable cancels: the family holds for every value or for none
        IExpr turns = engine.evaluate(
            F.Divide(F.Subtract(linear[0], F.Subtract(family[1], period)), F.C2Pi));
        if (turns.isInteger()) {
          return new TrigEqualFamilies(k, roots, true);
        }
        double value = turns.isNumericFunction() ? turns.evalf() : Double.NaN;
        if (Double.isNaN(value) || Math.abs(value - Math.rint(value)) < 1.0e-10) {
          // a symbolic offset, or one which can't be told from a whole number of turns
          return null;
        }
        continue;
      }
      IExpr root =
          engine.evaluate(F.Expand(F.Divide(F.Subtract(family[1], linear[0]), linear[1])));
      IExpr[] inK = root.linear(k);
      if (inK != null && inK[1].isNegative()) {
        // C(k) runs over all integers, so -C(k) is written as C(k)
        root = engine.evaluate(F.Subtract(inK[0], F.Times(inK[1], k)));
      }
      roots.add(root);
    }
    return new TrigEqualFamilies(k, roots, false);
  }

  /**
   * <code>{amplitude, head, argument}</code> of a term <code>amplitude*Sin(argument)</code> or
   * <code>amplitude*Cos(argument)</code> with an amplitude free of the variable and an argument
   * linear in it.
   *
   * @return <code>null</code> if the term isn't of that form
   */
  private static IExpr[] sinCosTerm(IExpr term, IExpr variable) {
    IExpr amplitude = F.C1;
    IExpr function = term;
    if (term.isTimes()) {
      IAST times = (IAST) term;
      int position = times.indexOf(x -> !x.isFree(variable));
      if (position < 0 || times.exists(x -> !x.isFree(variable), position + 1)) {
        return null;
      }
      function = times.get(position);
      amplitude = times.removeAtCopy(position).oneIdentity1();
    }
    if (!(function.isAST(S.Sin, 2) || function.isAST(S.Cos, 2))) {
      return null;
    }
    IExpr[] linear = function.first().linear(variable);
    if (linear == null || linear[1].isZero()) {
      return null;
    }
    return new IExpr[] {amplitude, function.head(), function.first()};
  }
}
