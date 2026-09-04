package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

public class Root extends AbstractFunctionEvaluator {

  /**
   * Extra digits carried while Newton-refining a root in {@link Apcomplex} arithmetic. Evaluating
   * the polynomial close to one of its roots cancels leading digits, so the working precision has
   * to exceed the requested precision.
   */
  private static final long GUARD_DIGITS = 20L;

  /**
   * Maximum number of Newton steps used to refine a machine-precision root approximation to the
   * requested precision. Newton's method doubles the number of correct digits per step, so a simple
   * root needs about {@code log2(precision/16)} steps. Multiple (or badly clustered) roots converge
   * only linearly and are cut off by this limit.
   */
  private static final int MAX_NEWTON_ITERATIONS = 32;

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    // Root[{f, c}] — near-float approximation form. Represents a root of f[x]==0 near x = c
    // (used for transcendental equations whose roots cannot be expressed in closed algebraic form).
    if (ast.isAST1() && ast.arg1().isList2()) {
      IAST list = (IAST) ast.arg1();
      IExpr f = list.first();
      IExpr c = list.second();

      // Validate: f must be a pure Function (or otherwise applicable) and c must be numeric.
      if (!c.isNumber() && !c.isNumericFunction(true)) {
        return Errors.printMessage(S.Root, "rapp", F.List(c), engine);
      }

      // No exact solution. Refine c via FindRoot when either
      // (a) the evaluator is in numeric mode (e.g. inside N[...]), or
      // (b) c itself is an inexact number (machine- or arbitrary-precision float).
      //
      // For case (b) — auto-evaluation of Root[{f, c}] with an inexact c — verify the
      // input c is genuinely close to the refined root. Emits "Root::invrt"
      // and leaves the expression unevaluated when the deviation exceeds a precision-
      // dependent tolerance (e.g. Root[{2#-Tan[#]&, 4.60421}] is rejected because
      // |4.60421 - 4.6042167...| ≈ 7e-6 while Root[{...&, 4.604216}] is accepted).
      //
      // For case (a) — explicit N[Root[{f, c}]] — the user is asking for numeric
      // refinement, so skip the precision check and return whatever FindRoot converges
      // to regardless of how rough the initial seed was.
      if (engine.isNumericMode()) {
        IExpr refinedNumeric = refineNumerically(f, c, engine);
        if (refinedNumeric.isPresent() && refinedNumeric.isNumber() && c.isNumber()) {
          if (!engine.isNumericMode()) {
            double cVal = c.evalfNaN();
            double rVal = refinedNumeric.evalfNaN();
            // if either isn't numeric: fall through – keep numeric refinement result
            if (!Double.isNaN(cVal) && !Double.isNaN(rVal)) {
              // Tolerance: ~1e-5 relative. Matches behavior on the
              // 4.60421 vs 4.604216 boundary for machine-precision Num inputs.
              double tol = Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, Math.abs(cVal));
              if (Math.abs(rVal - cVal) > tol) {
                // c is not close enough to any root of f → emit Root::invrt, stay inert.
                return Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
              }
            }
          }
          return refinedNumeric;
        }
        // refineNumerically failed (e.g. FindRoot did not converge from this seed) →
        // treat as "not equal to a root" and stay inert (only emit message in the
        // auto-evaluation path; under N[...] we just leave the expression unevaluated).
        if (!engine.isNumericMode()) {
          Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
        }
        return F.NIL;
      }

      // Symbolic mode with an exact c: refine once via FindRoot so the inert
      // Root[{f, c}] carries a precise numerical seed. Avoid looping by only returning
      // a new expression when the refined value differs from c.
      IExpr refined = refineNumerically(f, c, engine);
      if (refined.isPresent() && refined.isNumber() && c.isNumber()) {
        double cVal = c.evalfNaN();
        double rVal = refined.evalfNaN();
        // if either isn't numeric: ignore – keep inert
        if (!Double.isNaN(cVal) && !Double.isNaN(rVal)) {
          if (Math.abs(rVal - cVal) > Config.SPECIAL_FUNCTIONS_TOLERANCE
              * Math.max(1.0, Math.abs(cVal))) {
            return F.Root(F.list(f, refined));
          }
        }
      }

      // Try exact algebraic resolution via Solve (should succeed for algebraic f).
      IExpr exact = ToRadicals.rootToRadicals(ast, engine);
      if (exact.isPresent()) {
        return exact;
      }

      // Stay inert: Root[{f, c}] is a valid symbolic placeholder for a transcendental root.
      return F.NIL;
    }
    // Root[f, k] or Root[f, k, n] (n in {0,1}): auto-expand only for polynomials of
    // degree <= 2 . For higher degrees the user must call ToRadicals[Root[f, k]] explicitly.
    //
    // The 3-argument form Root[f, k, 0] is canonical input form (real-root ordering);
    // Root[f, k, 1] selects an alternate complex ordering. Symja currently only
    // implements the real-first ordering, so both forms are treated equivalently to Root[f, k].
    if ((ast.isAST2() || ast.isAST3()) && ast.arg2().isInteger()) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isFunction() && !arg1.isList()) {
        // Root[poly, k] with a plain polynomial expression instead of a pure function of Slot1:
        // rewrite it, so that Root[x^2-2, 1] and Root[#^2-2 &, 1] denote the same object. Only a
        // single variable identifies the polynomial unambiguously - Root[x^2+y, 1] stays
        // unevaluated because it doesn't say which of x and y the root belongs to.
        VariablesSet variables = new VariablesSet(arg1);
        if (variables.size() == 1) {
          return ast.setAtCopy(1, F.Function(F.subst(arg1, variables.firstVariable(), F.Slot1)));
        }
        return F.NIL;
      }
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (!arg3.equals(F.C0) && !arg3.equals(F.C1)) {
          // Normalize any invalid third argument to 0 (behavior:
          // Root[f, k, 2] → Root[f, k, 0]). The returned expression will be
          // re-evaluated and handled by the n==0 branch below.
          return F.ternaryAST3(S.Root, ast.arg1(), ast.arg2(), F.C0);
        }
      }
      IExpr radical = ToRadicals.rootToRadicals(ast, engine, 2);
      if (radical.isPresent()) {
        return radical;
      }
      // In numeric mode (e.g. inside N[...]) there is no reason to insist on a closed form: return
      // the k-th root of the polynomial as a number. This is the only option for polynomials which
      // are not solvable in radicals at all (e.g. the quintic 1 + 2*#1 + #1^5).
      if (engine.isNumericMode()) {
        IExpr numericRoot = numericRoot(ast, engine);
        if (numericRoot.isPresent()) {
          return numericRoot;
        }
      }
      // The Root object stays inert: normalize it to the canonical input form Root[f, k, 0].
      IExpr canonical = canonicalRootObject(ast, engine);
      if (canonical.isPresent()) {
        return canonical;
      }
    }
    // Root(f, k) stays unevaluated
    // Use ToRadicals(Root(f, k)) to expand to radical form.
    return F.NIL;
  }

  /**
   * Normalize an inert {@code Root[f, k]} object to the canonical input form {@code Root[f, k, 0]},
   * with the polynomial body of {@code f} in canonical term order.
   *
   * <p>
   * {@code Function} is {@link ISymbol#HOLDALL}, so the body of {@code f} is never evaluated and
   * keeps whatever term order the parser happened to produce: {@code Root[#1^5+2*#1+1 &, 2]} and
   * {@code Root[1+2*#1+#1^5 &, 2]} would otherwise be two structurally different objects for the
   * same algebraic number.
   *
   * @param ast the {@code Root[f, k]} or {@code Root[f, k, n]} expression
   * @param engine the evaluation engine
   * @return the canonical {@code Root[f, k, n]} object, or {@link F#NIL} if {@code f} is not a
   *         polynomial in {@link F#Slot1}, if {@code k} doesn't select one of its roots, or if
   *         {@code ast} is already canonical
   */
  private static IExpr canonicalRootObject(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    if (!function.isFunction()) {
      return F.NIL;
    }
    IExpr[] coefficients = polynomialCoefficients(function.first());
    if (coefficients == null) {
      return F.NIL;
    }
    final int degree = coefficients.length - 1;
    final int k = ast.arg2().toMachineInt();
    if (k < 1 || k > degree) {
      // k doesn't name one of the roots: keep the object as the user wrote it, so that the out of
      // range index stays visible instead of being dressed up as a canonical Root object.
      return F.NIL;
    }
    // Build the polynomial over a dummy variable and evaluate it there: evaluating an expression
    // which contains Slot1 would substitute the slot if this Root object is nested in the body of
    // another pure function.
    ISymbol x = F.Dummy("x");
    IASTAppendable plus = F.PlusAlloc(degree + 1);
    for (int i = 0; i <= degree; i++) {
      if (!coefficients[i].isZero()) {
        plus.append(i == 0 ? coefficients[i] : F.Times(coefficients[i], F.Power(x, F.ZZ(i))));
      }
    }
    IExpr body = F.subst(engine.evaluate(plus), x, F.Slot1);
    IExpr n = ast.isAST3() ? ast.arg3() : F.C0;
    IAST result = F.ternaryAST3(S.Root, F.Function(body), ast.arg2(), n);
    return result.equals(ast) ? F.NIL : result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // The root index k and the ordering flag n are positions in a list of roots, not numbers to be
    // approximated: without NHoldRest, N[Root[f, k, n]] would rewrite them to 1.0 / 0.0 and the
    // integer check in evaluate(IAST, EvalEngine) would no longer match.
    newSymbol.setAttributes(Attribute.NHOLDREST);
  }

  /**
   * Numerically determine the {@code k}-th root of the polynomial {@code Root[f, k]} or
   * {@code Root[f, k, n]}.
   *
   * <p>
   * All roots are computed at machine precision with Laguerre's method and are then ordered by the
   * {@code Root} k-indexing convention (real roots first in ascending order, then the complex roots
   * by ascending real part and ascending imaginary part). If the engine asks for more than machine
   * precision, the selected root is additionally refined with Newton's method in {@link Apcomplex}
   * arithmetic.
   *
   * @param ast the {@code Root[f, k]} or {@code Root[f, k, n]} expression
   * @param engine the evaluation engine
   * @return the numeric value of the {@code k}-th root, or {@link F#NIL} if it cannot be determined
   *         (non-polynomial or non-numeric {@code f}, degree out of range, {@code k} out of range)
   */
  private static IExpr numericRoot(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    if (!function.isFunction()) {
      return F.NIL;
    }
    final int k = ast.arg2().toMachineInt();
    if (k < 1) {
      return F.NIL;
    }
    IExpr[] coefficients = polynomialCoefficients(function.first());
    if (coefficients == null) {
      return F.NIL;
    }
    final int degree = coefficients.length - 1;
    if (k > degree) {
      // there is no k-th root: stay inert, like ToRadicals does for the same situation
      return F.NIL;
    }

    double[] doubleCoefficients = new double[degree + 1];
    for (int i = 0; i <= degree; i++) {
      IReal real = coefficients[i].evalReal();
      if (real == null) {
        // symbolic or non-real coefficient
        return F.NIL;
      }
      doubleCoefficients[i] = real.doubleValue();
    }

    org.hipparchus.complex.Complex[] complexRoots;
    try {
      complexRoots = RootsFunctions.allComplexRootsLaguerre(doubleCoefficients);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (complexRoots == null || complexRoots.length != degree) {
      return F.NIL;
    }

    IExpr[] numericRoots = new IExpr[degree];
    for (int i = 0; i < degree; i++) {
      // Chop with the same tolerance which sortRootsByMmaOrder() uses to tell real and complex
      // roots
      // apart, otherwise a root could be sorted as complex but printed as real (or vice versa).
      numericRoots[i] =
          F.chopExpr(F.complexNum(complexRoots[i].getReal(), complexRoots[i].getImaginary()),
              Config.DEFAULT_CHOP_DELTA);
    }
    numericRoots = ToRadicals.sortRootsByMmaOrder(numericRoots);

    IExpr result = numericRoots[k - 1];
    if (engine.isArbitraryMode()) {
      IExpr refined = refineToPrecision(coefficients, result,
          minimumSeparation(numericRoots, k - 1), engine.getNumericPrecision(), engine);
      if (refined.isPresent()) {
        return refined;
      }
    }
    return result;
  }

  /**
   * Determine the distance from {@code roots[index]} to its nearest neighbour.
   *
   * @param roots the numeric roots of a polynomial
   * @param index the index of the root to measure from
   * @return the distance to the nearest other root, or {@link Double#MAX_VALUE} if there is no
   *         other root or the distance cannot be determined
   */
  private static double minimumSeparation(IExpr[] roots, int index) {
    double separation = Double.MAX_VALUE;
    try {
      org.hipparchus.complex.Complex selected = roots[index].evalfc();
      for (int i = 0; i < roots.length; i++) {
        if (i != index) {
          double distance = selected.subtract(roots[i].evalfc()).norm();
          if (distance < separation) {
            separation = distance;
          }
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.MAX_VALUE;
    }
    return separation;
  }

  /**
   * Extract the exact coefficients of a univariate polynomial in {@link F#Slot1}.
   *
   * @param expr the body of the pure function of a {@code Root[f, k]} expression
   * @return the coefficients indexed by their exponent ({@code result[i]} belongs to
   *         {@code Slot1^i}), or <code>null</code> if {@code expr} is not a polynomial in
   *         {@code Slot1} of degree {@code >= 1}
   */
  private static IExpr[] polynomialCoefficients(IExpr expr) {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, F.list(F.Slot1));
      ExprPolynomial polynomial = ring.create(expr, false, true, false);
      final long varDegree = polynomial.degree(0);
      if (varDegree < 1 || varDegree > Config.MAX_POLYNOMIAL_DEGREE) {
        return null;
      }
      IExpr[] coefficients = new IExpr[(int) varDegree + 1];
      Arrays.fill(coefficients, F.C0);
      for (ExprMonomial monomial : polynomial) {
        coefficients[(int) monomial.exponent().getVal(0)] = monomial.coefficient();
      }
      return coefficients;
    } catch (JASConversionException | ArithmeticException ex) {
      return null;
    }
  }

  /**
   * Refine a machine-precision root approximation to {@code precision} digits with Newton's method
   * in {@link Apcomplex} arithmetic.
   *
   * <p>
   * The iteration is carried out with {@link #GUARD_DIGITS} extra digits, because evaluating the
   * polynomial near one of its roots cancels as many leading digits as the approximation already
   * has correct.
   *
   * @param coefficients the exact polynomial coefficients indexed by their exponent
   * @param approximation the machine-precision approximation of the root to refine
   * @param separation the distance from {@code approximation} to the nearest other root. Newton's
   *        method is only guaranteed to stay at the selected root while it moves less than half of
   *        this distance - otherwise the refined value would belong to a different index {@code k}.
   * @param precision the requested precision in decimal digits
   * @param engine the evaluation engine
   * @return the refined root, or {@link F#NIL} if the iteration did not converge or left the
   *         neighbourhood of the selected root (e.g. for a multiple root, where the derivative
   *         vanishes and Newton's method fails)
   */
  private static IExpr refineToPrecision(IExpr[] coefficients, IExpr approximation,
      double separation, long precision, EvalEngine engine) {
    if (!(approximation instanceof INumber)) {
      return F.NIL;
    }
    final int degree = coefficients.length - 1;
    final long workingPrecision = precision + GUARD_DIGITS;
    try {
      Apcomplex[] apcomplexCoefficients = new Apcomplex[degree + 1];
      for (int i = 0; i <= degree; i++) {
        Apcomplex coefficient = toApcomplex(coefficients[i], workingPrecision, engine);
        if (coefficient == null) {
          return F.NIL;
        }
        apcomplexCoefficients[i] = coefficient;
      }

      final Apcomplex seed = ((INumber) approximation).apcomplexValue().precision(workingPrecision);
      Apcomplex x = seed;
      // relative tolerance 10^-(precision+1)
      final Apfloat tolerance =
          ApfloatMath.scale(new Apfloat(1L, workingPrecision), -(precision + 1));
      for (int iteration = 0; iteration < MAX_NEWTON_ITERATIONS; iteration++) {
        // Horner's scheme, evaluating the polynomial and its derivative in one sweep
        Apcomplex p = apcomplexCoefficients[degree];
        Apcomplex dp = Apcomplex.ZERO;
        for (int i = degree - 1; i >= 0; i--) {
          dp = dp.multiply(x).add(p);
          p = p.multiply(x).add(apcomplexCoefficients[i]);
        }
        if (dp.real().signum() == 0 && dp.imag().signum() == 0) {
          // the derivative vanishes: multiple root, Newton's method is not applicable
          return F.NIL;
        }
        Apcomplex delta = p.divide(dp);
        x = x.subtract(delta);
        Apfloat absoluteDelta = ApcomplexMath.abs(delta);
        Apfloat absoluteX = ApcomplexMath.abs(x);
        if (absoluteDelta.signum() == 0 //
            || (absoluteX.signum() != 0
                && absoluteDelta.compareTo(absoluteX.multiply(tolerance)) < 0)) {
          if (ApcomplexMath.abs(x.subtract(seed)).doubleValue() > separation / 2.0) {
            // Newton left the neighbourhood of the selected root and converged somewhere else
            return F.NIL;
          }
          x = x.precision(precision);
          return x.imag().signum() == 0 ? F.num(x.real()) : F.complexNum(x);
        }
      }
    } catch (ArithmeticException | org.apfloat.ApfloatRuntimeException ex) {
      // fall through and keep the machine-precision approximation
    }
    return F.NIL;
  }

  /**
   * Convert an exact polynomial coefficient into an {@link Apcomplex} with the given precision.
   *
   * @param coefficient the exact coefficient
   * @param precision the precision in decimal digits
   * @param engine the evaluation engine
   * @return the converted coefficient, or <code>null</code> if it has no numeric value
   */
  private static Apcomplex toApcomplex(IExpr coefficient, long precision, EvalEngine engine) {
    if (coefficient instanceof IRational) {
      IRational rational = (IRational) coefficient;
      Apfloat numerator = new Apfloat(rational.toBigNumerator(), precision);
      Apfloat denominator = new Apfloat(rational.toBigDenominator(), precision);
      return new Apcomplex(numerator.divide(denominator));
    }
    // constants like Pi or algebraic numbers like Sqrt(2). The guard digits can push the working
    // precision past the limit which N() accepts, so ask for as much as N() can deliver.
    IExpr numeric =
        engine.evaluate(F.N(coefficient, Math.min(precision, Config.MAX_PRECISION_APFLOAT)));
    return (numeric instanceof INumber) ? ((INumber) numeric).apcomplexValue() : null;
  }

  /**
   * Refine the numerical approximation {@code c} of a root of {@code f[x] == 0} by calling
   * {@code FindRoot[f[x] == 0, {x, c}]}.
   *
   * @param f a pure {@code Function} (or any expression applicable to a single argument)
   * @param c a numeric starting value for the root
   * @param engine the evaluation engine
   * @return the refined numeric root value, or {@link F#NIL} if {@code FindRoot} fails
   */
  private static IExpr refineNumerically(IExpr f, IExpr c, EvalEngine engine) {
    if (!c.isNumber() && !c.isNumericFunction(true)) {
      return F.NIL;
    }
    try {

      ISymbol x = F.Dummy("x");
      IASTMutable unaryAST1 = F.unaryAST1(f, x);
      if (!engine.isNumericMode()) {
        IExpr result = engine.evaluate(F.subst(unaryAST1, x, c));
        if (!result.isPossibleZero(true)) {
          return Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
        }
        return F.NIL;
      }
      IExpr eq = F.Equal(unaryAST1, F.C0);

      IExpr findRootResult = engine.evaluate(F.FindRoot(eq, F.list(x, c)));
      // FindRoot returns {x -> value}
      if (findRootResult.isList() && findRootResult.size() == 2) {
        IExpr rule = findRootResult.first();
        if (rule.isRuleAST()) {
          IExpr value = rule.second();
          if (value.isNumber()) {
            return value;
          }
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }
}
