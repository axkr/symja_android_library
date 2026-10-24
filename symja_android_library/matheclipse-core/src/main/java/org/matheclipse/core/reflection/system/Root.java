package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.external.apfloat.ApcomplexPolynomialMath;
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

  /**
   * How far, relative to <code>|c|</code>, the start value <code>c</code> of
   * <code>Root({f, c})</code> may be from the root: <code>2*#-Tan(#)&amp;</code> has the root
   * <code>4.6042167...</code>, which <code>4.604216</code> stands for and <code>4.60421</code>
   * doesn't; <code>0.739085</code> stands for the root <code>0.73908513...</code> of
   * <code>Cos(#)-#&amp;</code>, <code>0.619061</code> not for <code>0.61906128...</code> of
   * <code>E^#-3*#&amp;</code>.
   */
  private static final double START_VALUE_TOLERANCE = 3.0e-7;

  /**
   * The values of the <code>Root({f, c})</code> which were accepted, so that an inert one isn't
   * refined and searched for a closed form at every evaluation: the closed form, or
   * {@link S#Null} for an inert one. The least recently used entries go first.
   */
  private static final java.util.Map<IAST, IExpr> ACCEPTED =
      java.util.Collections.synchronizedMap(new java.util.LinkedHashMap<IAST, IExpr>(64, 0.75f,
          true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(java.util.Map.Entry<IAST, IExpr> eldest) {
          return size() > 256;
        }
      });

  /**
   * The root of <code>f</code> near <code>c</code> at machine precision, if <code>c</code> is
   * close enough to it: within a few units of its seventh digit, as a root printed with six digits
   * is. Otherwise <code>Root::invrt</code> is printed.
   *
   * @return {@link F#NIL} if <code>c</code> is not equal to a root
   */
  private static IExpr certifiedRoot(IExpr f, IExpr c, EvalEngine engine) {
    IExpr root = refineNumerically(f, c, 0, engine);
    double cVal = c.evalfNaN();
    double rVal = root.isNumber() ? root.evalfNaN() : Double.NaN;
    if (Double.isNaN(cVal) || Double.isNaN(rVal) || Math.abs(rVal - cVal) > START_VALUE_TOLERANCE
        * (cVal == 0.0 ? 1.0 : Math.abs(cVal))) {
      // `1` is not equal to a root of `2`.
      Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
      return F.NIL;
    }
    return root;
  }

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

      // a Root({f, c}) which was accepted before: the same closed form or inert again
      IExpr known = engine.isNumericMode() ? null : ACCEPTED.get(ast);
      if (known != null) {
        return known == S.Null ? F.NIL : known;
      }

      // N(Root({f, c}), p) asks for the numerical root: c is a start value for it, which isn't
      // certified, at the working precision
      if (engine.isNumericMode()) {
        return refineNumerically(f, c, engine.isArbitraryMode() ? engine.getNumericPrecision() : 0,
            engine);
      }

      IExpr root = certifiedRoot(f, c, engine);
      if (root.isNIL()) {
        return F.NIL;
      }
      final double rVal = root.evalfNaN();

      // a closed form for an algebraic f, which has to be the root near c
      IExpr exact = engine.withQuietMode(() -> ToRadicals.rootToRadicals(ast, engine));
      if (exact.isPresent()) {
        double eVal = engine.evalQuiet(F.N(exact)).evalfNaN();
        if (Double.isNaN(eVal) || Math.abs(eVal - rVal) > 1.0e-8 * Math.max(1.0, Math.abs(rVal))) {
          exact = F.NIL;
        }
      }
      ACCEPTED.put(ast, exact.isPresent() ? exact : S.Null);
      // Stay inert without a closed form: Root[{f, c}] is a valid symbolic placeholder for a
      // transcendental root.
      return exact;
    }
    // Root[f, k] or Root[f, k, n] (n in {0,1}): auto-expand only for polynomials of
    // degree <= 2 . For higher degrees the user must call ToRadicals[Root[f, k]] explicitly.
    //
    // The 3-argument form Root[f, k, 0] is canonical input form (real-root ordering);
    // Root[f, k, 1] selects an alternate complex ordering. Symja currently only
    // implements the real-first ordering, so both forms are treated equivalently to Root[f, k].
    if ((ast.isAST2() || ast.isAST3()) && ast.arg2().isInteger()) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Function, 3)) {
        // Root[Function[y, poly], k] and Root[Function[{y}, poly], k] name the same root as
        // Root[poly /. y -> #1 &, k]: the code below reads the body of a slot function, and took
        // the parameter list {y} for the polynomial
        IExpr parameters = arg1.first();
        IExpr parameter = parameters.isList1() ? parameters.first() : parameters;
        if (!parameter.isSymbol()) {
          return F.NIL;
        }
        return ast.setAtCopy(1, F.Function(F.subst(arg1.second(), parameter, F.Slot1)));
      }
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
      // a polynomial of a high degree is not searched for factors with roots in radicals: the
      // factorization takes much longer than the root is worth
      IExpr radical = isHighDegreePolynomial(ast) ? F.NIL
          : ToRadicals.rootToRadicals(ast, engine, 2);
      if (radical.isPresent()) {
        return radical;
      }
      IExpr repeated = rootOfRepeatedFactor(ast, engine);
      if (repeated.isPresent()) {
        return repeated;
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

    // a real root is known from the exact isolation alone, without the numeric root finder
    double[] real = exactRealRoots(coefficients);
    if (real != null && k <= real.length) {
      IExpr result = F.num(real[k - 1]);
      if (engine.isArbitraryMode()) {
        double separation = Double.MAX_VALUE;
        if (k > 1) {
          separation = Math.min(separation, real[k - 1] - real[k - 2]);
        }
        if (k < real.length) {
          separation = Math.min(separation, real[k] - real[k - 1]);
        }
        IExpr refined = refineToPrecision(coefficients, result, separation,
            engine.getNumericPrecision(), engine);
        if (refined.isPresent()) {
          return refined;
        }
      }
      return result;
    }

    IExpr[] numericRoots = orderedNumericRoots(coefficients);
    if (numericRoots == null) {
      return F.NIL;
    }

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

  private static boolean isHighDegreePolynomial(IAST root) {
    if (!root.arg1().isFunction()) {
      return false;
    }
    IExpr[] coefficients = polynomialCoefficients(root.arg1().first());
    return coefficients != null && RootsFunctions.isHighDegreeGeneric(coefficients);
  }

  /** The exactly isolated real roots of the polynomials which were evaluated last. */
  private static final java.util.Map<java.util.List<IExpr>, double[]> REAL_ROOTS_CACHE =
      java.util.Collections.synchronizedMap(
          new java.util.LinkedHashMap<java.util.List<IExpr>, double[]>(32, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(
                java.util.Map.Entry<java.util.List<IExpr>, double[]> eldest) {
              return size() > 64;
            }
          });

  private static final double[] NO_ISOLATION = new double[0];

  /**
   * The real roots of a polynomial in ascending order, from an exact isolation: how many roots
   * are real is then no question of a tolerance on the imaginary part of a numeric root, which
   * goes wrong at a high degree. A machine number coefficient counts as the rational number it is.
   *
   * @return {@code null} if a coefficient is no rational or machine number, or the polynomial has
   *         a multiple root
   */
  private static double[] exactRealRoots(IExpr[] coefficients) {
    java.util.List<IExpr> key = java.util.Arrays.asList(coefficients.clone());
    double[] cached = REAL_ROOTS_CACHE.get(key);
    if (cached != null) {
      return cached == NO_ISOLATION ? null : cached;
    }
    double[] real = null;
    final int degree = coefficients.length - 1;
    java.math.BigInteger[] numerators = new java.math.BigInteger[degree + 1];
    java.math.BigInteger[] denominators = new java.math.BigInteger[degree + 1];
    boolean rational = true;
    for (int i = 0; i <= degree && rational; i++) {
      IExpr coefficient = coefficients[i];
      if (coefficient.isRational()) {
        org.matheclipse.core.interfaces.IRational r =
            (org.matheclipse.core.interfaces.IRational) coefficient;
        numerators[i] = r.toBigNumerator();
        denominators[i] = r.toBigDenominator();
      } else if (coefficient instanceof org.matheclipse.core.interfaces.INum
          && !(coefficient instanceof org.matheclipse.core.expression.ApfloatNum)
          && Double.isFinite(coefficient.evalfNaN())) {
        java.math.BigDecimal exact = new java.math.BigDecimal(coefficient.evalfNaN());
        if (exact.scale() > 0) {
          numerators[i] = exact.unscaledValue();
          denominators[i] = java.math.BigInteger.TEN.pow(exact.scale());
        } else {
          numerators[i] = exact.toBigIntegerExact();
          denominators[i] = java.math.BigInteger.ONE;
        }
      } else {
        rational = false;
      }
    }
    if (rational) {
      java.math.BigInteger denominator = java.math.BigInteger.ONE;
      for (java.math.BigInteger d : denominators) {
        denominator = denominator.multiply(d).divide(denominator.gcd(d));
      }
      java.math.BigInteger[] integers = new java.math.BigInteger[degree + 1];
      for (int i = 0; i <= degree; i++) {
        integers[i] = numerators[i].multiply(denominator.divide(denominators[i]));
      }
      real = org.matheclipse.core.polynomials.RealRootIsolation.realRoots(integers);
    }
    REAL_ROOTS_CACHE.put(key, real == null ? NO_ISOLATION : real);
    return real;
  }

  /** The ordered machine precision roots of the polynomials which were evaluated last. */
  private static final java.util.Map<java.util.List<IExpr>, IExpr[]> ROOTS_CACHE =
      java.util.Collections.synchronizedMap(
          new java.util.LinkedHashMap<java.util.List<IExpr>, IExpr[]>(32, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(
                java.util.Map.Entry<java.util.List<IExpr>, IExpr[]> eldest) {
              return size() > 64;
            }
          });

  private static final IExpr[] NO_ROOTS = new IExpr[0];

  /**
   * All roots of the polynomial at machine precision in the order of the {@code Root} index: the
   * real roots ascending, then the complex roots. The values of a polynomial are kept, because
   * {@code Root[f, 1]}, ..., {@code Root[f, n]} are usually evaluated one after the other.
   *
   * @param coefficients the coefficient of {@code x^i} at index {@code i}
   * @return {@code null} if a coefficient is no real number or the roots cannot be determined
   */
  private static IExpr[] orderedNumericRoots(IExpr[] coefficients) {
    java.util.List<IExpr> key = java.util.Arrays.asList(coefficients.clone());
    IExpr[] cached = ROOTS_CACHE.get(key);
    if (cached != null) {
      return cached == NO_ROOTS ? null : cached;
    }
    final int degree = coefficients.length - 1;
    double[] doubleCoefficients = new double[degree + 1];
    for (int i = 0; i <= degree; i++) {
      IReal real = coefficients[i].evalReal();
      if (real == null) {
        // symbolic or non-real coefficient
        return null;
      }
      doubleCoefficients[i] = real.doubleValue();
    }

    org.hipparchus.complex.Complex[] complexRoots;
    try {
      complexRoots = RootsFunctions.allComplexRootsLaguerre(doubleCoefficients);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      complexRoots = null;
    }
    if (complexRoots == null || complexRoots.length != degree) {
      // remembered too: the next index of the same polynomial would only fail again
      ROOTS_CACHE.put(key, NO_ROOTS);
      return null;
    }

    IExpr[] numericRoots = exactlyClassifiedRoots(coefficients, complexRoots);
    if (numericRoots == null) {
      numericRoots = new IExpr[degree];
      for (int i = 0; i < degree; i++) {
        // Chop with the same tolerance which sortRootsByMmaOrder() uses to tell real and complex
        // roots apart, otherwise a root could be sorted as complex but printed as real (or vice
        // versa).
        numericRoots[i] =
            F.chopExpr(F.complexNum(complexRoots[i].getReal(), complexRoots[i].getImaginary()),
                Config.DEFAULT_CHOP_DELTA);
      }
      numericRoots = ToRadicals.sortRootsByMmaOrder(numericRoots);
    }
    ROOTS_CACHE.put(key, numericRoots);
    return numericRoots;
  }

  /**
   * The roots of a polynomial with rational coefficients, with the real roots taken from an exact
   * isolation: how many roots are real is then no question of a tolerance on the imaginary part of
   * a numeric root, which goes wrong at a high degree. The numeric roots which lie nearest to the
   * real roots are replaced by them; the others are the complex roots.
   *
   * @return {@code null} if a coefficient is not rational or the polynomial has a multiple root
   */
  private static IExpr[] exactlyClassifiedRoots(IExpr[] coefficients,
      org.hipparchus.complex.Complex[] numeric) {
    final int degree = coefficients.length - 1;
    double[] real = exactRealRoots(coefficients);
    if (real == null || real.length > degree) {
      return null;
    }
    boolean[] used = new boolean[degree];
    for (double value : real) {
      int nearest = -1;
      double distance = Double.MAX_VALUE;
      for (int i = 0; i < degree; i++) {
        if (!used[i]) {
          double d = Math.hypot(numeric[i].getReal() - value, numeric[i].getImaginary());
          if (d < distance) {
            distance = d;
            nearest = i;
          }
        }
      }
      if (nearest < 0) {
        return null;
      }
      used[nearest] = true;
    }
    java.util.List<org.hipparchus.complex.Complex> complex =
        new java.util.ArrayList<org.hipparchus.complex.Complex>(degree - real.length);
    for (int i = 0; i < degree; i++) {
      if (!used[i]) {
        complex.add(numeric[i]);
      }
    }
    // the complex roots by ascending real part, then ascending imaginary part
    complex.sort((a, b) -> Math.abs(a.getReal() - b.getReal()) > 1e-10
        ? Double.compare(a.getReal(), b.getReal())
        : Double.compare(a.getImaginary(), b.getImaginary()));
    IExpr[] roots = new IExpr[degree];
    int index = 0;
    for (double value : real) {
      roots[index++] = F.num(value);
    }
    for (org.hipparchus.complex.Complex root : complex) {
      // a real part which is only noise is dropped; the imaginary part of a complex root stays
      double re = Math.abs(root.getReal()) < Config.DEFAULT_CHOP_DELTA ? 0.0 : root.getReal();
      roots[index++] = F.complexNum(re, root.getImaginary());
    }
    return roots;
  }

  /**
   * The {@code k}-th root of a polynomial with a repeated factor, as the root of its square free
   * part it is: {@code Root[(#-2)^2*(#+1)&, 3]} is {@code Root[-2+#&, 1]}, which is {@code 2}. The
   * roots are counted with their multiplicities, in the order of the {@code Root} index, and the
   * root is named by the square free factor it belongs to and its index among that factor's roots.
   *
   * @return {@link F#NIL} if {@code f} has no rational coefficients, is square free, or the roots
   *         of different factors cannot be told apart numerically
   */
  private static IExpr rootOfRepeatedFactor(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    if (!function.isFunction() || !function.first().isFree(x -> x.isInexactNumber(), false)) {
      return F.NIL;
    }
    final int k = ast.arg2().toIntDefault();
    if (k < 1) {
      return F.NIL;
    }
    IExpr body = function.first();
    IExpr[] coefficients = polynomialCoefficients(body);
    if (coefficients == null) {
      // a power of a sum, which is only a polynomial once it is expanded
      coefficients = polynomialCoefficients(engine.evaluate(F.Expand(body)));
      if (coefficients == null) {
        return F.NIL;
      }
    }
    for (IExpr coefficient : coefficients) {
      if (!coefficient.isRational()) {
        return F.NIL;
      }
    }
    if (k > coefficients.length - 1) {
      return F.NIL;
    }
    ISymbol x = F.Dummy("x");
    IExpr list = engine.evalQuiet(F.FactorSquareFreeList(F.subst(body, F.Slot1, x)));
    if (!list.isList()) {
      return F.NIL;
    }
    java.util.List<IExpr> factors = new java.util.ArrayList<IExpr>();
    java.util.List<IExpr> values = new java.util.ArrayList<IExpr>();
    java.util.List<int[]> owners = new java.util.ArrayList<int[]>();
    boolean repeated = false;
    for (IExpr entry : (IAST) list) {
      if (!entry.isList2() || entry.first().isFree(x)) {
        continue;
      }
      int multiplicity = entry.second().toIntDefault();
      if (multiplicity < 1) {
        return F.NIL;
      }
      repeated |= multiplicity > 1;
      IExpr factorBody = F.subst(entry.first(), x, F.Slot1);
      IExpr[] factorCoefficients = polynomialCoefficients(factorBody);
      if (factorCoefficients == null) {
        return F.NIL;
      }
      IExpr[] roots = orderedNumericRoots(factorCoefficients);
      if (roots == null) {
        return F.NIL;
      }
      int factorIndex = factors.size();
      factors.add(factorBody);
      for (int m = 0; m < multiplicity; m++) {
        for (int j = 0; j < roots.length; j++) {
          values.add(roots[j]);
          owners.add(new int[] {factorIndex, j + 1});
        }
      }
    }
    if (!repeated || values.size() != coefficients.length - 1) {
      return F.NIL;
    }
    // the square free factors are coprime, so roots of different factors which agree numerically
    // are too close to be ordered
    final int n = values.size();
    org.hipparchus.complex.Complex[] complex = new org.hipparchus.complex.Complex[n];
    for (int i = 0; i < n; i++) {
      complex[i] = values.get(i).evalfc();
    }
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (owners.get(i)[0] != owners.get(j)[0]
            && complex[i].subtract(complex[j]).norm() < 1.0e-10) {
          return F.NIL;
        }
      }
    }
    IExpr[] sorted = ToRadicals.sortRootsByMmaOrder(values.toArray(new IExpr[n]));
    IExpr selected = sorted[k - 1];
    // the same value object is shared by every copy of a multiple root
    for (int i = 0; i < n; i++) {
      if (values.get(i) == selected) {
        int[] owner = owners.get(i);
        return F.binaryAST2(S.Root, F.Function(factors.get(owner[0])), F.ZZ(owner[1]));
      }
    }
    return F.NIL;
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
  public static IExpr[] polynomialCoefficients(IExpr expr) {
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

      Apcomplex x = ApcomplexPolynomialMath.root(apcomplexCoefficients,
          ((INumber) approximation).apcomplexValue(), separation, precision);
      return x.imag().signum() == 0 ? F.num(x.real()) : F.complexNum(x);
    } catch (ArithmeticException | org.apfloat.ApfloatRuntimeException ex) {
      // no convergence, a multiple root, or the iteration left the neighbourhood of the selected
      // root: keep the machine-precision approximation
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
   * {@code FindRoot[f[x] == 0, {x, c}]}, quietly.
   *
   * @param f a pure {@code Function} (or any expression applicable to a single argument)
   * @param c a numeric starting value for the root
   * @param precision the working precision in digits, or {@code 0} for machine precision
   * @param engine the evaluation engine
   * @return the refined numeric root value, or {@link F#NIL} if {@code FindRoot} fails
   */
  private static IExpr refineNumerically(IExpr f, IExpr c, long precision, EvalEngine engine) {
    if (!c.isNumber() && !c.isNumericFunction(true)) {
      return F.NIL;
    }
    try {
      ISymbol x = F.Dummy("x");
      IExpr eq = F.Equal(F.unaryAST1(f, x), F.C0);
      IAST findRoot = precision > 0
          ? F.FindRoot(eq, F.list(x, c), F.Rule(S.WorkingPrecision, F.ZZ(precision)))
          : F.FindRoot(eq, F.list(x, c));
      IExpr findRootResult = engine.evalQuiet(findRoot);
      // FindRoot returns {x -> value}
      if (findRootResult.isList() && findRootResult.size() == 2) {
        IExpr rule = findRootResult.first();
        if (rule.isRuleAST() && rule.second().isNumber()) {
          return rule.second();
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }
}
