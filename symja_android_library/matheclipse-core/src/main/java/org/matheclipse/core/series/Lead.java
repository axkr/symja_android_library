package org.matheclipse.core.series;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The leading term of an expression at <code>t -&gt; 0+</code>, in the form
 * <code>coefficient * t^exponent</code>.
 *
 * <p>
 * This is the Symja analogue of SymPy's <code>(c0, e0)</code> pair returned by
 * <code>Expr.leadterm</code>. Every limit problem is normalized to the single direction
 * <code>t -&gt; 0+</code> before a {@code Lead} is computed (see
 * {@link LeadTerm#normalize(IExpr, ISymbol, IExpr, int)}), so no direction flag is carried here.
 *
 * <p>
 * Invariants, relied upon by every caller:
 * <ul>
 * <li>{@link #coefficient()} is free of <code>t</code>. It may contain the <code>logx</code> dummy
 * standing for <code>Log(t)</code>, which is <em>not</em> t-free mathematically but is treated as an
 * opaque symbol so that the exponent lattice stays one-dimensional.</li>
 * <li>{@link #coefficient()} is not the literal zero. A vanishing leading term carries no
 * information, so the producers return {@code null} instead.</li>
 * <li>{@link #exponent()} is any t-free real. It is <em>not</em> restricted to rationals: an
 * irrational exponent such as <code>1 - Log(5)/Log(3)</code> is representable here, which is what
 * the rational-only Puiseux lattice of {@code ASTSeriesData} cannot do.</li>
 * </ul>
 */
public final class Lead {

  private final IExpr coefficient;

  private final IExpr exponent;

  private final IExpr logx;

  public Lead(IExpr coefficient, IExpr exponent, IExpr logx) {
    this.coefficient = coefficient;
    this.exponent = exponent;
    this.logx = logx;
  }

  /** The coefficient <code>c</code> in <code>c * t^e</code>; free of <code>t</code>, never zero. */
  public IExpr coefficient() {
    return coefficient;
  }

  /** The exponent <code>e</code> in <code>c * t^e</code>; free of <code>t</code>, any real. */
  public IExpr exponent() {
    return exponent;
  }

  /** The dummy symbol standing for <code>Log(t)</code> inside {@link #coefficient()}. */
  public IExpr logx() {
    return logx;
  }

  /** Whether the coefficient mentions the {@code logx} dummy. */
  public boolean coefficientHasLogx() {
    return logx.isPresent() && !coefficient.isFree(logx);
  }

  /**
   * The sign of the exponent: <code>-1</code>, <code>0</code>, <code>1</code>, or
   * {@link LeadTerm#UNDECIDABLE} when no ordering against zero can be established.
   */
  public int exponentSign(EvalEngine engine) {
    return LeadTerm.compareExponents(exponent, F.C0, engine);
  }

  /**
   * Rebuild the leading term as an expression in <code>t</code>, substituting <code>Log(t)</code>
   * back for the {@code logx} dummy.
   */
  public IExpr toExpr(ISymbol t, EvalEngine engine) {
    IExpr c = coefficientHasLogx() ? F.subst(coefficient, logx, F.Log(t)) : coefficient;
    if (exponent.isZero()) {
      return engine.evaluate(c);
    }
    return engine.evaluate(F.Times(c, F.Power(t, exponent)));
  }

  @Override
  public String toString() {
    return "Lead(" + coefficient + ", " + exponent + ")";
  }
}
