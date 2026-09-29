package org.matheclipse.core.reflection.system;

import org.matheclipse.core.interfaces.Attribute;
import static org.matheclipse.core.expression.F.C0;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

// Add new class after the Root class (after line 3261):
public class ToRadicals extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    // If the argument itself is Root[f, k] or Root[f, k, n], expand it directly
    // (any degree 1..4)
    if (arg1.isAST(S.Root, 3) || arg1.isAST(S.Root, 4)) {
      IExpr result = ToRadicals.rootToRadicals((IAST) arg1, engine, 4);
      return result.orElse(arg1);
    }
    // Walk the expression tree and replace any Root[f, k]/Root[f, k, n] subexpressions.
    // If no Root is present (or none can be expanded), return arg1 unchanged so that
    // ToRadicals acts as identity for non-Root inputs.
    IExpr result = arg1.replaceAll(x -> {
      if (x.isAST(S.Root, 3) || x.isAST(S.Root, 4)) {
        IExpr radical = ToRadicals.rootToRadicals((IAST) x, engine, 4);
        if (radical.isPresent()) {
          return radical;
        }
      }
      return F.NIL;
    });
    return result.orElse(arg1);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
  }

  private static IExpr rootNearFloatNumber(EvalEngine engine, IExpr f, IExpr c) {
    double targetC = c.evalfNaN();
    if (Double.isNaN(targetC)) {
      // Root approximation `1` is not a number.
      return Errors.printMessage(S.Root, "rapp", F.List(c));
    }

    // Represents the root of the general equation f(x) == 0 near x = c
    ISymbol x = F.Dummy("x");
    IAST eq = F.Equal(F.unaryAST1(f, x), F.C0);

    try {
      // Attempt to find exact symbolic solutions using Solve(eq, x)
      double cmin = targetC - Config.DEFAULT_CHOP_DELTA;
      double cmax = targetC + Config.DEFAULT_CHOP_DELTA;
      IAST solve = F.Solve(F.List(eq, F.LessEqual(x, F.Rationalize(F.num(cmax), F.C0)),
          F.GreaterEqual(x, F.Rationalize(F.num(cmin), F.C0))), x);
      IExpr solveResult = engine.evaluate(solve);

      if (solveResult.isList()) {
        IAST list = (IAST) solveResult;
        IExpr bestExactRoot = F.NIL;
        double minDiff = Double.MAX_VALUE;

        // Iterate through the solutions to find the one closest to 'c'
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr ruleList = list.get(i);
          if (ruleList.isList1() && ruleList.first().isRuleAST()) {
            IExpr exactVal = ruleList.first().second();
            double val = exactVal.evalfNaN();
            if (Double.isNaN(val)) {
              // Skip if the exact value cannot be evaluated to a double
              continue;
            }
            double diff = Math.abs(val - targetC);

            // Define a reasonable threshold for "near" x = c, e.g., 1e-6
            if (diff < minDiff && diff < 1e-6) {
              minDiff = diff;
              bestExactRoot = exactVal;
            }
          }
        }

        if (bestExactRoot.isPresent()) {
          return bestExactRoot;
        }
      }

    } catch (ArgumentTypeException e) {
    }
    // If no exact root is found close enough to 'c', leave the Root object unevaluated
    return F.NIL;
  }

  public static IExpr rootToRadicals(final IAST ast, EvalEngine engine) {
    return rootToRadicals(ast, engine, 4);
  }

  /**
   * Convert a {@code Root[f, k]} expression to its radical form.
   *
   * @param ast the {@code Root[...]} expression
   * @param engine the evaluation engine
   * @param maxDegree maximum polynomial degree to expand. Pass {@code 2} for auto-evaluation of
   *        {@code Root} (quadratics only), or {@code 4} to also expand cubics and quartics (used by
   *        {@code ToRadicals}).
   * @return the radical form, or {@link F#NIL} if the polynomial cannot be expanded (e.g. degree
   *         exceeds {@code maxDegree}).
   */
  public static IExpr rootToRadicals(final IAST ast, EvalEngine engine, int maxDegree) {
    if (ast.isAST1() && ast.arg1().isList2()) {
      IExpr f = ast.arg1().first();
      IExpr c = ast.arg1().second();
      return rootNearFloatNumber(engine, f, c);
    }

    // Accept both Root[f, k] and Root[f, k, n] (n in {0,1}). Symja implements only the
    // real-first ordering, so the trailing argument is ignored here.
    if ((ast.size() == 3 || ast.size() == 4) && ast.arg2().isInteger()) {
      IExpr expr = ast.arg1();
      if (expr.isFunction()) {
        expr = expr.first();
        try {
          int k = ast.arg2().toMachineInt();
          if (k < 1) {
            return F.NIL;
          }
          final IAST variables = F.list(F.Slot1);
          ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables);
          ExprPolynomial polynomial = ring.create(expr, false, true, false);

          final long varDegree = polynomial.degree(0);
          if (polynomial.isConstant()) {
            return F.CEmptyList;
          }
          // A reducible polynomial is resolved factor by factor: the roots of an irreducible
          // factor of degree <= maxDegree have a radical form even when the product doesn't -
          // e.g. #^4-1 factorizes into (-1+#)*(1+#)*(1+#^2) and has the four explicit roots
          // -1, 1, -I, I, while Ferrari's formula applied to the quartic as a whole returns a
          // tower of nested radicals.
          IExpr fromFactors = rootFromFactors(expr, (int) varDegree, k, maxDegree, engine);
          if (fromFactors.isPresent()) {
            return fromFactors;
          }

          if (varDegree >= 1 && varDegree <= maxDegree) {
            // Compute all radical roots (degree many), then sort numerically to
            // align with NRoots / k-indexing (Re ascending, Im ascending).
            int degree = (int) varDegree;
            // Evaluating the closed form is speculative as long as the Solve fallback below can
            // still take over: a degenerate formula divides by zero on its way to returning
            // nothing, and that isn't the user's problem.
            final boolean quietMode = engine.isQuietMode();
            IExpr[] radicalRoots = null;
            if (maxDegree >= 4 && degree == 4) {
              // cyclotomic, biquadratic and (anti)palindromic quartics have roots in nested
              // square roots, which WMA prints - (-1)^(3/4), Sqrt(5+2*Sqrt(6)) - where Ferrari's
              // formula builds cube roots of complex numbers
              radicalRoots = structuredQuarticRoots(expr, engine);
            }
            if (radicalRoots == null) {
              try {
                engine.setQuietMode(quietMode || maxDegree >= 4);
                radicalRoots = radicalRoots(polynomial, degree, engine);
              } finally {
                engine.setQuietMode(quietMode);
              }
            }
            // stablySorted gives a new array ordered by the validated values - which the machine
            // values of the symbolic Re/Im mustn't reorder again - the same array for symbolic
            // roots, and null for roots which aren't stable
            boolean sorted = false;
            if (maxDegree >= 4 && degree >= 3 && radicalRoots != null) {
              IExpr[] unsorted = radicalRoots;
              radicalRoots = stablySorted(unsorted, engine);
              sorted = radicalRoots != null && radicalRoots != unsorted;
            }
            if (radicalRoots == null && maxDegree >= 4) {
              // The closed formulas degenerate for this polynomial - Ferrari's formula divides by
              // zero for #^4-2, for instance - or name a root only up to rounding. Solve knows the
              // special cases (binomials among them), so fall back to it before giving up.
              radicalRoots = solveToRadicals(expr, degree, engine);
              if (radicalRoots != null && degree >= 3) {
                IExpr[] unsorted = radicalRoots;
                radicalRoots = stablySorted(unsorted, engine);
                sorted = radicalRoots != null && radicalRoots != unsorted;
              }
            }
            if (radicalRoots == null) {
              return F.NIL;
            }

            if (!sorted) {
              // Sort by (real-first, then Re asc, then Im asc) to match k-indexing.
              radicalRoots = sortRootsByMmaOrder(radicalRoots);
            }

            if (k < 1 || k > degree) {
              return F.NIL;
            }
            return radicalRoots[k - 1];
          } else if (varDegree > maxDegree && maxDegree >= 4) {
            // Degree exceeds the Cardano/Ferrari formulas (degree 1..4). For solvable
            // polynomials (e.g. binomials like #^5 - 2) fall back to Solve, which can return
            // exact radical solutions. This branch is gated to ToRadicals callers (maxDegree>=4);
            // plain Root auto-evaluation (maxDegree==2) never reaches here.
            IExpr[] radicalRoots = solveToRadicals(expr, (int) varDegree, engine);
            if (radicalRoots == null) {
              return F.NIL;
            }
            // Sort by (real-first, then Re asc, then Im asc) to match k-indexing.
            radicalRoots = sortRootsByMmaOrder(radicalRoots);
            if (k < 1 || k > radicalRoots.length) {
              return F.NIL;
            }
            return radicalRoots[k - 1];
          }
        } catch (JASConversionException e2) {
          //
        }
      }
    }
    return F.NIL;
  }

  /**
   * The roots of a quartic whose structure gives them as nested square roots, or <code>null</code>:
   * <ul>
   * <li>a multiple of <code>Cyclotomic(n, #)</code> - the roots of unity <code>(-1)^(2*j/n)</code>
   * <li>a biquadratic <code>e*#^4+c*#^2+a</code> - <code>+/-Sqrt(t)</code> for the roots
   * <code>t</code> of <code>e*t^2+c*t+a</code>
   * <li>a palindromic <code>e*#^4+d*#^3+c*#^2+d*#+e</code> - a quadratic in <code>y=x+1/x</code>,
   * and an antipalindromic <code>e*#^4+d*#^3+c*#^2-d*#+e</code> - a quadratic in
   * <code>y=x-1/x</code>
   * </ul>
   *
   * @param body the quartic in {@link F#Slot1}
   */
  private static IExpr[] structuredQuarticRoots(IExpr body, EvalEngine engine) {
    IExpr coefficients = engine.evaluate(F.CoefficientList(body, F.Slot1));
    if (!coefficients.isList() || coefficients.argSize() != 5
        || !((IAST) coefficients).forAll(IExpr::isRational)) {
      return null;
    }
    IAST list = (IAST) coefficients;
    IExpr a = list.arg1();
    IExpr b = list.arg2();
    IExpr c = list.arg3();
    IExpr d = list.arg4();
    IExpr e = list.arg5();

    for (int n : new int[] {5, 8, 10, 12}) {
      // the cyclotomic polynomials of degree 4
      IExpr cyclotomic = engine.evaluate(F.Expand(F.Times(e, F.binaryAST2(S.Cyclotomic, F.ZZ(n),
          F.Slot1))));
      if (engine.evaluate(F.Expand(F.Subtract(body, cyclotomic))).isZero()) {
        IExpr[] roots = new IExpr[4];
        int k = 0;
        for (int j = 1; j < n; j++) {
          if (java.math.BigInteger.valueOf(j).gcd(java.math.BigInteger.valueOf(n)).intValue() == 1) {
            roots[k++] = engine.evaluate(F.Power(F.CN1, F.QQ(2 * j, n)));
          }
        }
        return roots;
      }
    }

    if (b.isZero() && d.isZero()) {
      IExpr[] roots = new IExpr[4];
      IExpr discriminant = sqrtOf(F.Subtract(F.Sqr(c), F.Times(F.C4, e, a)), engine);
      IExpr t1 = F.Divide(F.Plus(F.Negate(c), discriminant), F.Times(F.C2, e));
      IExpr t2 = F.Divide(F.Subtract(F.Negate(c), discriminant), F.Times(F.C2, e));
      IExpr s1 = sqrtOf(t1, engine);
      IExpr s2 = sqrtOf(t2, engine);
      roots[0] = s1;
      roots[1] = engine.evaluate(F.Negate(s1));
      roots[2] = s2;
      roots[3] = engine.evaluate(F.Negate(s2));
      return roots;
    }

    final boolean palindromic = a.equals(e) && b.equals(d);
    final boolean antipalindromic = a.equals(e) && engine.evaluate(F.Plus(b, d)).isZero();
    if (palindromic || antipalindromic) {
      // e*(x^2 + 1/x^2) + d*(x +/- 1/x) + c == 0, and x^2 + 1/x^2 == y^2 -/+ 2
      final IExpr sign = palindromic ? F.CN2 : F.C2;
      IExpr yDiscriminant = sqrtOf(
          F.Subtract(F.Sqr(d), F.Times(F.C4, e, F.Plus(c, F.Times(sign, e)))), engine);
      IExpr[] ys = {F.Divide(F.Plus(F.Negate(d), yDiscriminant), F.Times(F.C2, e)),
          F.Divide(F.Subtract(F.Negate(d), yDiscriminant), F.Times(F.C2, e))};
      IExpr[] roots = new IExpr[4];
      int k = 0;
      for (IExpr y : ys) {
        y = engine.evaluate(F.Expand(y));
        // x^2 - y*x + 1 == 0, or x^2 - y*x - 1 == 0
        IExpr xDiscriminant = sqrtOf(F.Plus(F.Sqr(y), palindromic ? F.CN4 : F.C4), engine);
        roots[k++] = engine.evaluate(F.Times(F.C1D2, F.Plus(y, xDiscriminant)));
        roots[k++] = engine.evaluate(F.Times(F.C1D2, F.Subtract(y, xDiscriminant)));
      }
      return roots;
    }
    return null;
  }

  /**
   * The principal square root of a real algebraic <code>radicand</code> in WMA's form: the
   * radicand expanded with its content factored out, and <code>I*Sqrt(-r)</code> for a negative one
   * - <code>Sqrt(5+2*Sqrt(6))</code>, <code>I*Sqrt(2*(-1+Sqrt(5)))</code>.
   */
  private static IExpr sqrtOf(IExpr radicand, EvalEngine engine) {
    IExpr r = engine.evaluate(F.Expand(radicand));
    if (r.isRealResult() && engine.evaluate(F.Less(r, F.C0)).isTrue()) {
      return engine.evaluate(
          F.Times(F.CI, F.Sqrt(engine.evaluate(F.FactorTerms(engine.evaluate(F.Negate(r)))))));
    }
    return engine.evaluate(F.Sqrt(engine.evaluate(F.FactorTerms(r))));
  }

  /**
   * The roots, in the {@code Root} order of their values, if every value is stable - the same at
   * machine precision, 30 and 60 digits - or <code>null</code>.
   *
   * <p>
   * Ferrari's formula for <code>#^4+1</code> builds a radicand which is exactly <code>-2</code> as
   * a sum of cube roots of complex numbers. On the branch cut of the square root its value is a
   * matter of rounding - machine precision says <code>-0.707+0.707*I</code>, 30 digits
   * <code>-0.707-0.707*I</code> - so it names no particular root, and ordering it by one of those
   * values returned the conjugate root.
   */
  private static IExpr[] stablySorted(IExpr[] roots, EvalEngine engine) {
    final int n = roots.length;
    for (IExpr root : roots) {
      if (!root.isNumericFunction()) {
        // symbolic coefficients: no values to order by, the formula's own order is kept
        return roots;
      }
    }
    double[] re = new double[n];
    double[] im = new double[n];
    try {
      for (int i = 0; i < n; i++) {
        IExpr machine = engine.evalN(roots[i]);
        IExpr value30 = engine.evalN(roots[i], 30);
        IExpr value60 = engine.evalN(roots[i], 60);
        if (!machine.isNumber() || !value30.isNumber() || !value60.isNumber()) {
          return null;
        }
        INumber v60 = (INumber) value60;
        re[i] = v60.reDoubleValue();
        im[i] = v60.imDoubleValue();
        double scale = Math.max(1.0, Math.hypot(re[i], im[i]));
        for (IExpr value : new IExpr[] {machine, value30}) {
          INumber v = (INumber) value;
          if (Math.hypot(v.reDoubleValue() - re[i], v.imDoubleValue() - im[i]) > 1.0e-9 * scale) {
            return null;
          }
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
    return sortByValues(roots, re, im);
  }

  /**
   * Compute all roots of a polynomial of degree 1..4 in radical form with the Cardano/Ferrari
   * formulas.
   *
   * @param polynomial the polynomial in {@link F#Slot1}
   * @param degree the degree of {@code polynomial} (1..4)
   * @param engine the evaluation engine
   * @return the {@code degree} roots in the natural k-indexing of the formulas (unsorted), or
   *         <code>null</code> if one of them has no radical form
   */
  private static IExpr[] radicalRoots(ExprPolynomial polynomial, int degree, EvalEngine engine) {
    IExpr a = C0;
    IExpr b = C0;
    IExpr c2 = C0;
    IExpr d = C0;
    IExpr e = C0;
    for (ExprMonomial monomial : polynomial) {
      final IExpr coeff = monomial.coefficient();
      long lExp = monomial.exponent().getVal(0);
      if (lExp == 4) {
        e = coeff;
      } else if (lExp == 3) {
        d = coeff;
      } else if (lExp == 2) {
        c2 = coeff;
      } else if (lExp == 1) {
        b = coeff;
      } else if (lExp == 0) {
        a = coeff;
      } else {
        throw new ArithmeticException("Root::Unexpected exponent value: " + lExp);
      }
    }

    IExpr[] radicalRoots = new IExpr[degree];
    for (int i = 1; i <= degree; i++) {
      IAST ri;
      if (degree == 1) {
        ri = Algebra.root1(a, b, i);
      } else if (degree == 2) {
        ri = Algebra.root2(a, b, c2, i);
      } else if (degree == 3) {
        ri = Algebra.root3(a, b, c2, d, i);
      } else {
        ri = Algebra.root4(a, b, c2, d, e, i);
      }
      if (!ri.isPresent()) {
        return null;
      }
      IExpr root = engine.evaluate(ri);
      if (!root.isFree(S.Indeterminate) || !root.isFree(S.ComplexInfinity)
          || !root.isFree(S.DirectedInfinity)) {
        // A coefficient the formula divides by is zero for this polynomial, so the closed form
        // collapsed instead of naming the root.
        return null;
      }
      radicalRoots[i - 1] = root;
    }
    return radicalRoots;
  }

  /**
   * Determine the {@code k}-th root of a <em>reducible</em> polynomial by expanding each of its
   * irreducible factors separately.
   *
   * <p>
   * {@code Root} indexes the roots of the whole polynomial, so the roots of all factors - each one
   * repeated as often as the multiplicity of its factor - are merged and sorted together before
   * {@code k} selects one of them.
   *
   * <p>
   * The method gives up (and leaves the caller with its usual handling) unless <em>every</em>
   * irreducible factor has degree {@code <= maxDegree}: a factor which has no radical form of its
   * own can't take part in the merged ordering, because it has no exact value to sort by.
   *
   * @param body the polynomial expression in {@link F#Slot1} (the body of the pure function)
   * @param degree the degree of {@code body} in {@link F#Slot1}
   * @param k the one-based index of the wanted root
   * @param maxDegree maximum degree of an irreducible factor which may be expanded
   * @param engine the evaluation engine
   * @return the {@code k}-th root in radical form, or {@link F#NIL} if {@code body} is irreducible,
   *         if one of its factors is too big to expand, or if the factorization is unusable
   */
  private static IExpr rootFromFactors(IExpr body, int degree, int k, int maxDegree,
      EvalEngine engine) {
    if (degree < 2 || k < 1 || k > degree) {
      return F.NIL;
    }
    ISymbol x = F.Dummy("x");
    IExpr factorList = engine.evaluate(F.FactorList(F.subst(body, F.Slot1, x)));
    if (!factorList.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) factorList;
    // Collect the irreducible factors first. An irreducible polynomial gains nothing here and is
    // left to the caller - determining that before any root is computed keeps the messages of a
    // degenerate closed form out of the caller's own attempt.
    List<IExpr> factors = new ArrayList<IExpr>();
    List<Integer> multiplicities = new ArrayList<Integer>();
    int numberOfFactors = 0;
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr entry = list.get(i);
      if (!entry.isList2()) {
        return F.NIL;
      }
      IExpr factor = entry.first();
      if (factor.isFree(x)) {
        // the numerical content of the polynomial doesn't contribute a root
        continue;
      }
      int multiplicity = entry.second().toIntDefault();
      if (multiplicity < 1) {
        return F.NIL;
      }
      factors.add(factor);
      multiplicities.add(multiplicity);
      numberOfFactors += multiplicity;
    }
    if (numberOfFactors < 2) {
      return F.NIL;
    }

    ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, F.list(F.Slot1));
    List<IExpr> roots = new ArrayList<IExpr>(degree);
    for (int i = 0; i < factors.size(); i++) {
      ExprPolynomial factorPolynomial;
      try {
        factorPolynomial = ring.create(F.subst(factors.get(i), x, F.Slot1), false, true, false);
      } catch (JASConversionException jce) {
        return F.NIL;
      }
      final int factorDegree = (int) factorPolynomial.degree(0);
      if (factorDegree < 1 || factorDegree > maxDegree) {
        return F.NIL;
      }
      IExpr[] factorRoots = radicalRoots(factorPolynomial, factorDegree, engine);
      if (factorRoots == null) {
        return F.NIL;
      }
      for (int m = 0; m < multiplicities.get(i); m++) {
        for (int r = 0; r < factorRoots.length; r++) {
          roots.add(factorRoots[r]);
        }
      }
    }
    if (roots.size() != degree) {
      // the factors don't account for every root of the polynomial
      return F.NIL;
    }
    IExpr[] allRoots = sortRootsByMmaOrder(roots.toArray(new IExpr[roots.size()]));
    return allRoots[k - 1];
  }

  /**
   * Sort the given radical roots by {@code Root} k-indexing convention:
   * <ol>
   * <li>real roots first, ordered by ascending value</li>
   * <li>complex roots ordered by ascending real part, then ascending imaginary part</li>
   * </ol>
   * If any root cannot be evaluated to a numeric value (e.g. symbolic coefficients), the original
   * order is returned unchanged so that the natural Cardano/Ferrari k-indexing is preserved.
   *
   * <p>
   * Shared with {@link Root}, which uses the same ordering for the numerically evaluated roots of
   * {@code N[Root[f, k]]}. Both call sites must agree, otherwise {@code N[Root[f, k]]} and
   * {@code N[ToRadicals[Root[f, k]]]} would select different roots.
   *
   * @param roots the radical roots to sort
   * @return a new sorted array, or the original array if numeric sorting is not possible
   */
  static IExpr[] sortRootsByMmaOrder(IExpr[] roots) {
    final int n = roots.length;
    final double[] reVals = new double[n];
    final double[] imVals = new double[n];
    for (int i = 0; i < n; i++) {
      try {
        reVals[i] = roots[i].re().evalfNaN();
        imVals[i] = roots[i].im().evalfNaN();
        if (Double.isNaN(reVals[i]) || Double.isNaN(imVals[i])) {
          return roots;
        }
      } catch (RuntimeException ex) {
        return roots;
      }
    }
    return sortByValues(roots, reVals, imVals);
  }

  /** Sort the roots by the {@code Root} k-indexing convention of their given values. */
  private static IExpr[] sortByValues(IExpr[] roots, final double[] reVals,
      final double[] imVals) {
    final int n = roots.length;
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      order[i] = i;
    }
    final double imTol = 1e-10;
    final double reTol = 1e-10;
    java.util.Arrays.sort(order, (xi, yi) -> {
      boolean xReal = Math.abs(imVals[xi]) < imTol;
      boolean yReal = Math.abs(imVals[yi]) < imTol;
      if (xReal && !yReal) {
        return -1;
      }
      if (!xReal && yReal) {
        return 1;
      }
      if (Math.abs(reVals[xi] - reVals[yi]) > reTol) {
        return Double.compare(reVals[xi], reVals[yi]);
      }
      return Double.compare(imVals[xi], imVals[yi]);
    });
    IExpr[] sorted = new IExpr[n];
    for (int i = 0; i < n; i++) {
      sorted[i] = roots[order[i]];
    }
    return sorted;
  }

  /**
   * Fall back to {@code Solve} to obtain the radical solutions of {@code body == 0} for polynomials
   * whose degree exceeds the closed-form Cardano/Ferrari formulas (degree 1..4). This handles
   * solvable polynomials such as binomials (e.g. {@code #^5 - 2}).
   *
   * <p>
   * Also used for degrees the formulas do cover but degenerate on: Ferrari's formula divides by
   * zero for {@code #^4-2}, while {@code Solve} recognizes the binomial and returns
   * {@code -2^(1/4)}.
   *
   * @param body the polynomial expression in {@link F#Slot1} (the body of the pure function)
   * @param degree the polynomial degree (expected number of solutions)
   * @param engine the evaluation engine
   * @return an array of {@code degree} exact radical solutions, or {@code null} if {@code Solve}
   *         could not return exactly {@code degree} explicit radical solutions
   */
  private static IExpr[] solveToRadicals(IExpr body, int degree, EvalEngine engine) {
    ISymbol x = F.Dummy("x");
    IExpr eqBody = body.replaceAll(F.Rule(F.Slot1, x)).orElse(body);
    IExpr solveResult = engine.evaluate(F.Solve(F.Equal(eqBody, C0), x));
    if (!solveResult.isListOfRules(false) && !solveResult.isList()) {
      return null;
    }
    IAST list = (IAST) solveResult;
    if (list.argSize() != degree) {
      return null;
    }
    IExpr[] solutions = new IExpr[degree];
    for (int i = 1; i <= degree; i++) {
      IExpr ruleList = list.get(i);
      if (!ruleList.isList1() || !ruleList.first().isRuleAST()) {
        return null;
      }
      IExpr value = ruleList.first().second();
      // Reject solutions that are not exact radical forms: still contain Root, reference the
      // solve variable (unsolved), or are inexact (numeric-only) results.
      if (!value.isFree(S.Root) || !value.isFree(x) || value.isInexactNumber()) {
        return null;
      }
      solutions[i - 1] = engine.evaluate(value);
    }
    return solutions;
  }
}
