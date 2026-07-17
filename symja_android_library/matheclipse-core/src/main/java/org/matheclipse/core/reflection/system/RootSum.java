package org.matheclipse.core.reflection.system;

import java.util.Optional;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>RootSum(f, form)</code> represents the sum of <code>form(r)</code> over all roots
 * <code>r</code> of the polynomial <code>f(r)</code>.
 *
 * <p>
 * When <code>form</code> is a rational function this evaluator reduces the sum to an explicit
 * rational function. The value is computed as a <em>trace over the roots</em> rather than by
 * building the polynomial resultant, because the resultant carries the free (external) variable
 * through modular polynomial arithmetic in the root variable, which makes Symja's general evaluator
 * blow up on non-trivial inputs.
 *
 * <p>
 * Two cases are handled:
 * <ul>
 * <li>The summand is parameter free, i.e. <code>A(r)/B(r)</code> with numeric coefficients. Then
 * <code>Sum_i A(r_i)/B(r_i) = Sum_k c_k*p_k</code>, where <code>c_k</code> are the coefficients of
 * <code>A*B^-1 mod f</code> and <code>p_k = Sum_i r_i^k</code> are the Newton power sums of the
 * roots.
 * <li>The summand is <code>A(r)/(C(r)*(v - r))</code> with a single external variable <code>v</code>
 * and <code>A</code>, <code>C</code> free of <code>v</code>. This is exactly the shape produced by
 * differentiating a <code>Log(v - r)</code> antiderivative. Using
 * <code>1/(v - r_i) == S(v, r_i)/f(v)</code> with <code>S(v, r) = (f(v) - f(r))/(v - r)</code>, the
 * external variable is kept out of the root arithmetic and only reappears in the final assembly.
 * </ul>
 */
public class RootSum extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr f = ast.arg1();
    IExpr form = ast.arg2();

    // Dummy variable for the roots to avoid naming collisions with the summand.
    ISymbol r = F.Dummy("r");

    // The polynomial whose roots are summed over.
    IExpr px = engine.evaluate(F.ExpandAll(engine.evaluate(F.unaryAST1(f, r))));
    if (!px.isPolynomial(F.list(r))) {
      return F.NIL;
    }
    IExpr degreeExpr = engine.evaluate(F.Exponent(px, r));
    if (!degreeExpr.isInteger()) {
      return F.NIL;
    }
    int degree = degreeExpr.toIntDefault();
    if (degree < 0) {
      return F.NIL;
    }
    if (degree == 0) {
      // A non-zero constant polynomial has no roots, so the sum is empty.
      return F.C0;
    }

    // Write the summand form(r) as a single fraction A(r)/B(r).
    IExpr qx = engine.evaluate(F.Together(engine.evaluate(F.unaryAST1(form, r))));
    IExpr ax;
    IExpr bx;
    Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(qx, false);
    if (parts.isPresent()) {
      ax = engine.evaluate(F.ExpandAll(parts.get()[0]));
      bx = engine.evaluate(F.ExpandAll(parts.get()[1]));
    } else {
      ax = engine.evaluate(F.ExpandAll(qx));
      bx = F.C1;
    }
    if (!ax.isPolynomial(F.list(r)) || !bx.isPolynomial(F.list(r))) {
      return F.NIL;
    }

    // Make the root polynomial monic; roots (and hence their power sums) are unchanged.
    IExpr leadingCoefficient = engine.evaluate(F.Coefficient(px, r, F.ZZ(degree)));
    IExpr pMonic = engine.evaluate(F.ExpandAll(F.Times(F.Power(leadingCoefficient, F.CN1), px)));
    IExpr[] powerSums = powerSums(pMonic, r, degree, engine);

    // Free variables of the summand other than the bound root variable r.
    IExpr singleExternal = F.NIL;
    int externalCount = 0;
    IExpr summandVars = engine.evaluate(F.Variables(qx));
    if (summandVars.isList()) {
      IAST varList = (IAST) summandVars;
      for (int i = 1; i < varList.size(); i++) {
        IExpr var = varList.get(i);
        if (!var.equals(r)) {
          externalCount++;
          singleExternal = var;
        }
      }
    }

    if (externalCount == 0) {
      // Parameter free rational summand: the sum is a rational number.
      return rootSumConstant(pMonic, ax, bx, r, powerSums, degree, engine);
    }
    if (externalCount == 1) {
      IExpr result = rootSumResidue(pMonic, ax, bx, r, singleExternal, powerSums, degree, engine);
      if (result.isPresent()) {
        return result;
      }
    }

    // Keep the expression inert for forms we cannot reduce to a rational function; this lets
    // Normal[...] and N[...] manipulate it later.
    return F.NIL;
  }

  /**
   * Newton power sums <code>p_k = Sum_i r_i^k</code>, for <code>k = 0..degree-1</code>, of the roots
   * of the monic polynomial <code>pMonic</code>.
   */
  private static IExpr[] powerSums(IExpr pMonic, ISymbol r, int degree, EvalEngine engine) {
    // coefficient[j] is the coefficient of r^j, for j < degree (the coefficient of r^degree is 1).
    IExpr[] coefficient = new IExpr[degree];
    for (int j = 0; j < degree; j++) {
      coefficient[j] = engine.evaluate(F.Coefficient(pMonic, r, F.ZZ(j)));
    }
    IExpr[] p = new IExpr[degree];
    p[0] = F.ZZ(degree);
    for (int k = 1; k < degree; k++) {
      // Newton's identity: p_k = -( k*a_{d-k} + Sum_{i=1}^{k-1} a_{d-i}*p_{k-i} ).
      IASTAppendable sum = F.PlusAlloc(k + 1);
      sum.append(F.Times(F.ZZ(k), coefficient[degree - k]));
      for (int i = 1; i <= k - 1; i++) {
        sum.append(F.Times(coefficient[degree - i], p[k - i]));
      }
      p[k] = engine.evaluate(F.Negate(sum));
    }
    return p;
  }

  /**
   * <code>Sum_i h(r_i)</code> over the roots of <code>pMonic</code>, where <code>h</code> is a
   * polynomial in <code>r</code> whose coefficients are free of the external variable. Uses
   * <code>Sum_i h(r_i) = Sum_k coefficient_k(h mod pMonic)*p_k</code>.
   */
  private static IExpr traceOverRoots(IExpr h, IExpr pMonic, ISymbol r, IExpr[] powerSums,
      int degree, EvalEngine engine) {
    IExpr reduced = engine.evaluate(F.PolynomialRemainder(h, pMonic, r));
    IASTAppendable sum = F.PlusAlloc(degree);
    for (int k = 0; k < degree; k++) {
      IExpr coefficient = engine.evaluate(F.Coefficient(reduced, r, F.ZZ(k)));
      if (!coefficient.isZero()) {
        sum.append(F.Times(coefficient, powerSums[k]));
      }
    }
    return engine.evaluate(sum);
  }

  /**
   * The modular inverse <code>b(r)^-1 mod pMonic(r)</code>, or {@link F#NIL} if <code>b</code> and
   * <code>pMonic</code> are not coprime (i.e. a root of the denominator is a root of the polynomial).
   */
  private static IExpr modularInverse(IExpr b, IExpr pMonic, ISymbol r, EvalEngine engine) {
    IExpr extendedGCD = S.PolynomialExtendedGCD.of(engine, b, pMonic, r);
    if (!extendedGCD.isList() || extendedGCD.size() != 3) {
      return F.NIL;
    }
    IExpr gcd = ((IAST) extendedGCD).arg1();
    IExpr cofactors = ((IAST) extendedGCD).arg2();
    if (!gcd.isFree(r) || gcd.isZero() || !cofactors.isList() || cofactors.size() < 2) {
      // A gcd depending on r has positive degree, so b is not invertible modulo pMonic.
      return F.NIL;
    }
    IExpr s = ((IAST) cofactors).arg1(); // s*b + t*pMonic == gcd
    return engine.evaluate(F.Cancel(F.Divide(s, gcd)));
  }

  /**
   * Evaluate <code>Sum_i A(r_i)/B(r_i)</code> for a summand whose coefficients are free of any
   * external variable. The result is a rational number.
   */
  private static IExpr rootSumConstant(IExpr pMonic, IExpr ax, IExpr bx, ISymbol r,
      IExpr[] powerSums, int degree, EvalEngine engine) {
    IExpr h;
    if (bx.isFree(r)) {
      if (bx.isZero()) {
        return S.ComplexInfinity;
      }
      h = engine.evaluate(F.Divide(ax, bx));
    } else {
      IExpr inverse = modularInverse(bx, pMonic, r, engine);
      if (inverse.isNIL()) {
        // A root of the denominator coincides with a root of the polynomial: pole.
        return S.ComplexInfinity;
      }
      h = engine.evaluate(F.Times(ax, inverse));
    }
    return engine.evaluate(F.Together(traceOverRoots(h, pMonic, r, powerSums, degree, engine)));
  }

  /**
   * Evaluate <code>Sum_i A(r_i)/(C(r_i)*(v - r_i))</code> for a summand whose denominator contains
   * the linear factor <code>(v - r)</code> in the single external variable <code>v</code>. Returns
   * {@link F#NIL} when the summand does not have this shape.
   */
  private static IExpr rootSumResidue(IExpr pMonic, IExpr ax, IExpr bx, ISymbol r, IExpr v,
      IExpr[] powerSums, int degree, EvalEngine engine) {
    // Split off the (v - r) factor of the denominator.
    IExpr quotientRemainder =
        engine.evaluate(F.PolynomialQuotientRemainder(bx, F.Subtract(v, r), r));
    if (!quotientRemainder.isList() || quotientRemainder.size() != 3) {
      return F.NIL;
    }
    IExpr remainder = ((IAST) quotientRemainder).arg2();
    if (!remainder.isZero()) {
      return F.NIL;
    }
    IExpr cx = ((IAST) quotientRemainder).arg1(); // bx == cx*(v - r)
    if (!cx.isFree(v) || !ax.isFree(v)) {
      return F.NIL;
    }

    // Invert C(r) modulo pMonic(r); this arithmetic is free of the external variable.
    IExpr cInverse = modularInverse(cx, pMonic, r, engine);
    if (cInverse.isNIL()) {
      return F.NIL;
    }
    IExpr aHat = engine.evaluate(F.PolynomialRemainder(F.Times(ax, cInverse), pMonic, r));

    // S(v, r) = (pMonic(v) - pMonic(r))/(v - r); at a root r_i this gives 1/(v - r_i) == S/pMonic(v).
    IExpr pv = engine.evaluate(F.subst(pMonic, e -> e.equals(r) ? v : F.NIL));
    IExpr sVR = engine.evaluate(F.Cancel(F.Divide(F.Subtract(pv, pMonic), F.Subtract(v, r))));

    // numerator = Sum_m T_m*v^m with T_m = trace(aHat*s_m), s_m = Coefficient(S, v, m).
    IASTAppendable numerator = F.PlusAlloc(degree);
    for (int m = 0; m < degree; m++) {
      IExpr sm = engine.evaluate(F.Coefficient(sVR, v, F.ZZ(m)));
      if (sm.isZero()) {
        continue;
      }
      IExpr tm = traceOverRoots(engine.evaluate(F.Times(aHat, sm)), pMonic, r, powerSums, degree,
          engine);
      if (!tm.isZero()) {
        numerator.append(F.Times(tm, F.Power(v, F.ZZ(m))));
      }
    }
    return engine.evaluate(F.Together(F.Divide(numerator, pv)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
