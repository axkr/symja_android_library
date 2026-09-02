package org.matheclipse.core.reflection.system;

import java.util.Optional;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
 * <li>The summand is <code>A(r)/(C(r)*(v - r))</code> with a single external variable
 * <code>v</code> and <code>A</code>, <code>C</code> free of <code>v</code>. This is exactly the
 * shape produced by differentiating a <code>Log(v - r)</code> antiderivative. Using
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
    int degree = degreeExpr.toMachineInt();
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
      if (degree <= 2) {
        // The roots of a linear or quadratic polynomial are plain radicals - and Root(f, k) itself
        // auto-expands exactly up to degree 2 (see Root#evaluate) - so the sum is written out.
        // RootSum(#^2-#+a&, Sin(#)&) evaluates to Sin(1/2*(1-Sqrt(1-4*a))) +
        // Sin(1/2*(1+Sqrt(1-4*a))). Note it returns that plain sum over
        // the roots, not a Simplify'd product form, so no simplification is applied here.
        IExpr expanded = expandOverRoots(ast, false, engine);
        if (expanded.isPresent()) {
          return expanded;
        }
      }
      // A non-rational summand such as Log(x + #) cannot be reduced to a rational function of the
      // coefficients of f, so the expression stays inert - RootSum is exactly the canonical closed
      // form for the integral of a rational function (Rothstein-Trager), and expanding it into the
      // explicit roots defeats its purpose: the radicals only exist for degree <= 4, they are far
      // larger than the RootSum, and Sum_i Log(x + r_i) is not equal to Log(Product_i (x + r_i)) on
      // the principal branch anyway. Use Normal(RootSum(...)) to expand over the roots on demand.
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

    // Fallback for all parameter-dependent rational summands:
    // Try the general trace over roots algorithm.
    IExpr result = rootSumConstant(pMonic, ax, bx, r, powerSums, degree, engine);
    if (result.isPresent()) {
      return result;
    }

    // Keep the expression inert for forms we cannot reduce to a rational function;
    return F.NIL;
  }

  /**
   * Expand <code>RootSum(f, form)</code> into the explicit sum <code>Sum_i form(r_i)</code> over
   * the roots <code>r_i</code> of <code>f</code>.
   *
   * <p>
   * This is what <code>Normal(RootSum(f, form))</code> does. It is deliberately <em>not</em> part
   * of the automatic evaluation: a summand which reduces to a rational function is handled exactly
   * by {@link #evaluate(IAST, EvalEngine)}, and for every other summand the explicit form only
   * exists when the roots are expressible in radicals - i.e. for degree <code>&lt;= 4</code>.
   *
   * @param rootSumAST a <code>RootSum(f, form)</code> expression
   * @param engine the evaluation engine
   * @return the expanded sum, or {@link F#NIL} if the roots cannot be written explicitly
   */
  public static IExpr expandOverRoots(IAST rootSumAST, EvalEngine engine) {
    return expandOverRoots(rootSumAST, true, engine);
  }

  /**
   * @param simplify if <code>true</code> try to condense the explicit sum with
   *        {@link S#FullSimplify}. The automatic degree <code>&lt;= 2</code> expansion passes
   *        <code>false</code>, because the expected answer there is the plain sum over the roots.
   * @see #expandOverRoots(IAST, EvalEngine)
   */
  public static IExpr expandOverRoots(IAST rootSumAST, boolean simplify, EvalEngine engine) {
    if (rootSumAST.argSize() != 2) {
      return F.NIL;
    }
    IExpr f = rootSumAST.arg1();
    IExpr form = rootSumAST.arg2();
    ISymbol r = F.Dummy("r");
    IExpr px = engine.evaluate(F.ExpandAll(engine.evaluate(F.unaryAST1(f, r))));
    if (!px.isPolynomial(F.list(r))) {
      return F.NIL;
    }
    IExpr degreeExpr = engine.evaluate(F.Exponent(px, r));
    if (degreeExpr.toMachineInt() <= 0) {
      return F.NIL;
    }
    IASTMutable rootsAST = RootsFunctions.rootsOfExprPolynomial(px, F.List(r), false, true);
    if (rootsAST == null || !rootsAST.isPresent() || !rootsAST.isList()) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(rootsAST.size());
    for (int i = 1; i < rootsAST.size(); i++) {
      // Together condenses a root like 1/2-Sqrt(1-4*a)/2 into 1/2*(1-Sqrt(1-4*a)). The root is
      // printed inside the summand, so this is what makes RootSum(#^2-#+a&, Sin(#)&) come out as
      // Sin(1/2*(1-Sqrt(1-4*a)))+Sin(1/2*(1+Sqrt(1-4*a))).
      IExpr root = engine.evaluate(F.Together(rootsAST.get(i)));
      sum.append(engine.evaluate(F.unaryAST1(form, root)));
    }
    IExpr summed = engine.evaluate(sum);
    if (!simplify) {
      return summed;
    }
    // FullSimplify is only cosmetic here - `summed` is already the complete explicit sum over the
    // roots. On a sum of solvable radicals (e.g. the four (-1)^(1/4) roots of x^4+1) it can recurse
    // until the Java stack overflows, so treat any failure as "leave it unsimplified" rather than
    // letting it abort the whole evaluation.
    try {
      IExpr simplified = engine.evaluate(F.FullSimplify(summed));
      if (simplified.isPresent()) {
        return simplified;
      }
    } catch (StackOverflowError soe) {
      // fall through to the un-simplified sum
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
    }
    return summed;
  }

  @Override
  public IExpr numericEval(final IAST ast, EvalEngine engine) {
    // Ensure the expected number of arguments are present.
    if (ast.argSize() != 2) {
      return F.NIL;
    }

    IExpr f = ast.arg1();
    IExpr form = ast.arg2();

    // 1. Create a dummy variable for the polynomial to avoid naming collisions[cite: 1].
    ISymbol r = F.Dummy("r");

    // 2. Evaluate the pure function 'f' with the dummy variable 'r' to extract the polynomial[cite:
    // 1].
    IExpr px = engine.evaluate(F.ExpandAll(engine.evaluate(F.unaryAST1(f, r))));
    // if (!px.isPolynomial(F.list(r))) {
    // return F.NIL;
    // }

    // 3. Delegate to RootsFunctions to find the complex roots of the evaluated polynomial[cite: 3].
    // RootsFunctions.complexRoots expects an IAST of variables (size 2, representing List[r])[cite:
    // 3].
    IAST variables = F.List(r);
    IAST rootsList = RootsFunctions.complexRoots(px, variables, engine);

    // 4. Verify that the root finding was successful and returned a list[cite: 3].
    if (rootsList == null || !rootsList.isList()) {
      return F.NIL;
    }

    // 5. Accumulate the sum of form(root) for each discovered numerical root[cite: 1].
    IASTAppendable sum = F.PlusAlloc(rootsList.size());
    for (int i = 1; i < rootsList.size(); i++) {
      IExpr rootValue = rootsList.get(i);

      // Apply the summand 'form' function to the current root value[cite: 1].
      IExpr summand = engine.evaluate(F.unaryAST1(form, rootValue));
      sum.append(summand);
    }

    // 6. Return the fully evaluated numerical sum[cite: 1].
    return engine.evaluate(sum);
  }

  /**
   * Newton power sums <code>p_k = Sum_i r_i^k</code>, for <code>k = 0..degree-1</code>, of the
   * roots of the monic polynomial <code>pMonic</code>.
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
    IExpr[] coefficients = new IExpr[degree];
    for (int k = 0; k < degree; k++) {
      coefficients[k] = engine.evaluate(F.Coefficient(reduced, r, F.ZZ(k)));
    }
    return trace(coefficients, powerSums, degree, engine);
  }

  /**
   * <code>Sum_k coefficient_k*p_k</code> for a polynomial <code>Sum_k coefficient_k*r^k</code> of
   * degree less than <code>degree</code> which is already reduced modulo the root polynomial.
   *
   * @see #traceOverRoots(IExpr, IExpr, ISymbol, IExpr[], int, EvalEngine)
   */
  private static IExpr trace(IExpr[] coefficients, IExpr[] powerSums, int degree,
      EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(degree);
    for (int k = 0; k < degree; k++) {
      if (!coefficients[k].isZero()) {
        sum.append(F.Times(coefficients[k], powerSums[k]));
      }
    }
    return engine.evaluate(sum);
  }

  /**
   * The modular inverse <code>b(r)^-1 mod pMonic(r)</code>, or {@link F#NIL} if <code>b</code> and
   * <code>pMonic</code> are not coprime (i.e. a root of the denominator is a root of the
   * polynomial).
   */
  private static IExpr modularInverse(IExpr b, IExpr pMonic, ISymbol r, EvalEngine engine) {
    if (b.isFree(r)) {
      // b is a unit (constant) with respect to r.
      return b.isZero() ? S.ComplexInfinity : engine.evaluate(F.Power(b, F.CN1));
    }
    IExpr extendedGCD = S.PolynomialExtendedGCD.of(engine, b, pMonic, r);
    if (!extendedGCD.isList() || extendedGCD.size() != 3) {
      return F.NIL; // Evaluation failure
    }
    IExpr gcd = ((IAST) extendedGCD).arg1();
    IExpr cofactors = ((IAST) extendedGCD).arg2();
    if (!gcd.isFree(r) || gcd.isZero() || !cofactors.isList() || cofactors.size() < 2) {
      // A gcd depending on r has positive degree, so b is not invertible modulo pMonic.
      return S.ComplexInfinity; // Pole indicator
    }
    IExpr s = ((IAST) cofactors).arg1(); // s*b + t*pMonic == gcd
    return engine.evaluate(F.Cancel(F.Divide(s, gcd)));
  }

  /**
   * <code>true</code> if <code>expr</code> contains a symbol other than the bound root variable
   * <code>r</code>, i.e. if its coefficients carry free parameters.
   */
  private static boolean hasParameters(IExpr expr, ISymbol r, EvalEngine engine) {
    IExpr variables = engine.evaluate(F.Variables(expr));
    if (variables.isList()) {
      IAST variableList = (IAST) variables;
      for (int i = 1; i < variableList.size(); i++) {
        if (!variableList.get(i).equals(r)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * The coefficients <code>h_0, ..., h_(degree-1)</code> of the polynomial <code>h(r)</code> with
   * <code>h(r)*b(r) == a(r) (mod pMonic(r))</code>, i.e. of the summand <code>a/b</code> rewritten
   * as a polynomial in the root variable.
   *
   * <p>
   * They are found by solving a linear system in the basis <code>1, r, ..., r^(degree-1)</code>
   * instead of by running the extended Euclidean algorithm: {@link S#PolynomialExtendedGCD} does
   * not evaluate as soon as the coefficients contain free symbols -
   * <code>PolynomialExtendedGCD(r^3-2*r+c, r^5-a*r+b, r)</code> stays unevaluated - while the
   * multiplication matrix of <code>b</code> modulo <code>pMonic</code> needs nothing but polynomial
   * arithmetic and one solve over the field of rational functions in the parameters.
   *
   * <p>
   * The coefficients are returned instead of the assembled polynomial <code>h</code> because for a
   * parametrized polynomial each of them is already a large quotient of multivariate polynomials;
   * reading them back off <code>h</code> with {@link S#Coefficient} would put them over a common
   * denominator first and blow past {@code Config.MAX_AST_SIZE}.
   *
   * @return <code>null</code> if the system has no unique solution, which is exactly the case where
   *         <code>b</code> and <code>pMonic</code> are not coprime
   */
  private static IExpr[] modularQuotientCoefficients(IExpr ax, IExpr bx, IExpr pMonic, ISymbol r,
      int degree, EvalEngine engine) {
    // matrix[k][j] is the coefficient of r^k in b(r)*r^j reduced modulo pMonic, so that column j
    // is the image of the basis element r^j under multiplication by b.
    IExpr[][] matrix = new IExpr[degree][degree];
    for (int j = 0; j < degree; j++) {
      IExpr reduced = engine
          .evaluate(F.PolynomialRemainder(F.Expand(F.Times(bx, F.Power(r, F.ZZ(j)))), pMonic, r));
      for (int k = 0; k < degree; k++) {
        matrix[k][j] = engine.evaluate(F.Coefficient(reduced, r, F.ZZ(k)));
      }
    }
    IASTAppendable rows = F.ListAlloc(degree);
    for (int k = 0; k < degree; k++) {
      IASTAppendable row = F.ListAlloc(degree);
      for (int j = 0; j < degree; j++) {
        row.append(matrix[k][j]);
      }
      rows.append(row);
    }

    IExpr aReduced = engine.evaluate(F.PolynomialRemainder(ax, pMonic, r));
    IASTAppendable rightHandSide = F.ListAlloc(degree);
    for (int k = 0; k < degree; k++) {
      rightHandSide.append(engine.evaluate(F.Coefficient(aReduced, r, F.ZZ(k))));
    }

    IExpr solution = engine.evaluate(F.LinearSolve(rows, rightHandSide));
    if (!solution.isList() || solution.size() != degree + 1) {
      return null;
    }
    IAST solutionList = (IAST) solution;
    IExpr[] coefficients = new IExpr[degree];
    for (int j = 0; j < degree; j++) {
      coefficients[j] = solutionList.get(j + 1);
    }
    return coefficients;
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
      if (hasParameters(pMonic, r, engine) || hasParameters(ax, r, engine)
          || hasParameters(bx, r, engine)) {
        // Over a parametrized coefficient domain B^-1 mod pMonic is a quotient of multivariate
        // polynomials; multiplying it by A and reducing the product modulo pMonic again exceeds
        // Config.MAX_AST_SIZE long before anything cancels. Solve for the coefficients of
        // A*B^-1 mod pMonic in one step instead.
        IExpr[] coefficients = modularQuotientCoefficients(ax, bx, pMonic, r, degree, engine);
        if (coefficients != null) {
          return engine.evaluate(F.Together(trace(coefficients, powerSums, degree, engine)));
        }
      }
      IExpr inverse = modularInverse(bx, pMonic, r, engine);
      if (inverse.isNIL()) {
        // PolynomialExtendedGCD failed to evaluate symbolically.
        return F.NIL;
      }
      if (inverse == S.ComplexInfinity) {
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

    // S(v, r) = (pMonic(v) - pMonic(r))/(v - r); at a root r_i this gives 1/(v - r_i) ==
    // S/pMonic(v).
    IExpr pv = engine.evaluate(F.subst(pMonic, e -> e.equals(r) ? v : F.NIL));
    IExpr sVR = engine.evaluate(F.Cancel(F.Divide(F.Subtract(pv, pMonic), F.Subtract(v, r))));

    // numerator = Sum_m T_m*v^m with T_m = trace(aHat*s_m), s_m = Coefficient(S, v, m).
    IASTAppendable numerator = F.PlusAlloc(degree);
    for (int m = 0; m < degree; m++) {
      IExpr sm = engine.evaluate(F.Coefficient(sVR, v, F.ZZ(m)));
      if (sm.isZero()) {
        continue;
      }
      IExpr tm =
          traceOverRoots(engine.evaluate(F.Times(aHat, sm)), pMonic, r, powerSums, degree, engine);
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
