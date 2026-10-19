package org.matheclipse.core.builtin;

import java.math.BigInteger;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.DirichletCharacterTable;
import org.matheclipse.core.numerics.functions.ComplexGamma;
import org.matheclipse.core.numerics.functions.ZetaJS;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/** <code>DirichletCharacter(k, j, n)</code> and the Dirichlet L-function <code>DirichletL(k, j, s)</code>. */
public final class DirichletFunctions {

  /** Largest conductor of the exact values at <code>s &lt;= 0</code>. */
  private static final long EXACT_FMAX = 1L << 14;

  /** Largest <code>1 - s</code> of the exact values at <code>s &lt;= 0</code>. */
  private static final int EXACT_MMAX = 400;

  /** Largest <code>LCM(order, f)</code> of the exact values at <code>s = 1</code>. */
  private static final long S1_NMAX = 4096;

  /** Largest conductor of the numerical sum over Hurwitz zeta values. */
  private static final long NUMERIC_FMAX = 4096;

  private static final String INVL =
      "Argument `1` at position 2 in `2` should be a positive integer less than or equal to `3`.";

  private static class Initializer {
    private static void init() {
      S.DirichletCharacter.setEvaluator(new DirichletCharacter());
      S.DirichletL.setEvaluator(new DirichletL());
    }
  }

  /**
   * The modulus and index of <code>head(k, j, ...)</code>, or <code>null</code>. A number which
   * isn't a positive integer gets <code>intp</code>, an index above <code>EulerPhi(k)</code> gets
   * <code>invl</code>; a symbolic argument gets no message.
   */
  private static DirichletCharacterTable labels(IAST ast, EvalEngine engine) {
    long[] kj = new long[2];
    for (int i = 1; i <= 2; i++) {
      IExpr arg = ast.get(i);
      if (arg.isInteger()) {
        IInteger integer = (IInteger) arg;
        if (integer.isPositive()) {
          BigInteger big = integer.toBigNumerator();
          // positive, so below 2^63 with at most 63 bits
          if (big.bitLength() <= 63) {
            kj[i - 1] = big.longValue();
            continue;
          }
          if (i == 1) {
            return null; // a modulus beyond 2^63 stays unevaluated
          }
          kj[i - 1] = Long.MAX_VALUE; // larger than every EulerPhi(k)
          continue;
        }
      }
      if (arg.isNumber()) {
        Errors.printMessage(ast.topHead(), "intp", F.list(ast, F.ZZ(i)), engine);
      }
      return null;
    }
    DirichletCharacterTable table = DirichletCharacterTable.of(kj[0], kj[1]);
    if (table == null) {
      // the factorization for the message only, when there is one to give
      long phi = DirichletCharacterTable.eulerPhi(kj[0]);
      if (kj[1] > phi) {
        Errors.printMessage(ast.topHead(), "invl", F.list(ast.arg2(), ast, F.ZZ(phi)), engine);
      }
    }
    return table;
  }

  /**
   * <code>DirichletCharacter(k, j, n)</code>: the Dirichlet character <code>chi_(k,j)</code> at the
   * integer <code>n</code>.
   */
  private static final class DirichletCharacter extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg3().isNumber()) {
        // a symbolic argument: nothing to evaluate, and no message about labels which N may
        // have made inexact - N(DirichletCharacter(7, 2, x))
        return F.NIL;
      }
      DirichletCharacterTable table = labels(ast, engine);
      if (table == null) {
        return F.NIL;
      }
      IExpr n = ast.arg3();
      if (!n.isInteger()) {
        if (n.isNumber()) {
          return Errors.printMessage(S.DirichletCharacter, "int", F.list(ast, F.C3), engine);
        }
        return F.NIL;
      }
      long k = table.modulus();
      long residue = ((IInteger) n).toBigNumerator().mod(BigInteger.valueOf(k)).longValue();
      IExpr value = table.value(residue);
      return value == null ? F.NIL : value;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDALL);
      newSymbol.putMessage(IPatternMatcher.SET, "invl", F.stringx(INVL));
      super.setUp(newSymbol);
    }
  }

  /**
   * <code>DirichletL(k, j, s)</code>: the Dirichlet L-function of the character
   * <code>DirichletCharacter(k, j, n)</code>.
   *
   * <p>
   * Exact values at the integers <code>s &lt;= 0</code> (generalized Bernoulli numbers) and at
   * <code>s = 1</code> for a primitive non-principal character; numerical values from
   * <code>f^(-s) * Sum(chi*(a)*HurwitzZeta(s, a/f), {a, f}) * Product(1-chi*(p)*p^(-s))</code>
   * over the primitive character <code>chi*</code> of conductor <code>f</code> and the primes
   * dividing <code>k</code> but not <code>f</code>.
   */
  private static final class DirichletL extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      DirichletCharacterTable table = labels(ast, engine);
      if (table == null) {
        return F.NIL;
      }
      IExpr s = ast.arg3();
      if (table.modulus() == 1) {
        if (s.isInteger() || s.isInexactNumber()) {
          return F.Zeta(s);
        }
        if (!engine.isNumericMode() || !s.isNumericFunction()) {
          return F.NIL;
        }
      }
      if (s.isInexactNumber()) {
        return numericValue(table, s, engine);
      }
      if (s.isInteger()) {
        IExpr value = exactValue(table, (IInteger) s, engine);
        if (value != null) {
          // in numeric mode the exact value is evaluated numerically afterwards
          return value;
        }
      }
      if (engine.isNumericMode() && s.isNumericFunction()) {
        // N leaves the arguments as they are: the value at the numerical s
        IExpr numeric = engine.isArbitraryMode() ? engine.evalN(s, engine.getNumericPrecision())
            : engine.evalN(s);
        if (numeric.isInexactNumber()) {
          return table.modulus() == 1 ? F.Zeta(numeric) : numericValue(table, numeric, engine);
        }
      }
      return F.NIL;
    }

    /**
     * The exact value at an integer <code>s</code>: at <code>s &lt;= 0</code> from the generalized
     * Bernoulli numbers, at <code>s == 1</code> for a primitive character which isn't principal.
     *
     * @return <code>null</code> if there is none
     */
    private static IExpr exactValue(DirichletCharacterTable table, IInteger s,
        EvalEngine engine) {
      if (!s.isPositive()) {
        int m = 1 - s.toIntDefault(Integer.MIN_VALUE + 1);
        if (m >= 1 && m <= EXACT_MMAX) {
          return exactNonPositive(table, m, engine);
        }
      } else if (s.isOne() && !table.isPrincipal() && table.conductor() == table.modulus()) {
        return exactAtOne(table, engine);
      }
      return null;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDALL);
      newSymbol.putMessage(IPatternMatcher.SET, "invl", F.stringx(INVL));
      super.setUp(newSymbol);
    }
  }

  // ----------------------------------------------------------------------------------------
  // numerical values
  // ----------------------------------------------------------------------------------------

  /**
   * <code>L(s, chi)</code> for an inexact <code>s</code>, at its precision.
   *
   * <p>
   * The terms of the Hurwitz sum have poles at <code>s = 1</code> which cancel for a non-principal
   * character, so near <code>s = 1</code> the sum loses about <code>-Log10(|s-1|)</code> digits:
   * there it is evaluated with that many guard digits. At <code>s = 1</code> itself the closed form
   * is used.
   */
  private static IExpr numericValue(DirichletCharacterTable table, IExpr s, EvalEngine engine) {
    if (s instanceof ApfloatNum) {
      // an integer s <= 0 at a precision above machine: the exact value, which may be a trivial
      // zero the Hurwitz sum could only approach through cancellation. Integer means the exact
      // value of s, not its rounding to a double
      org.apfloat.Apfloat apfloat = ((ApfloatNum) s).apfloatValue();
      double d = apfloat.doubleValue();
      if (apfloat.frac().signum() == 0 && d <= 0.0 && d > -EXACT_MMAX) {
        IExpr exact = exactNonPositive(table, 1 - (int) d, engine);
        if (exact != null) {
          IExpr value = exactN(table, s, exact, precisionDigits(s, engine), engine);
          return value.isZero() ? F.C0 : value;
        }
      }
    }
    IExpr sMinus1 = engine.evaluate(F.Subtract(s, F.C1));
    if (sMinus1.isZero()) {
      if (table.isPrincipal()) {
        return F.CComplexInfinity;
      }
      IExpr closed = exactAtOne(table, engine);
      if (closed == null) {
        return F.NIL;
      }
      IExpr value = exactN(table, s, closed, precisionDigits(s, engine) + 5, engine);
      return rounded(value, s, precisionDigits(s, engine), table.isReal() && s.isReal(), engine);
    }
    if (table.conductor() > NUMERIC_FMAX) {
      return F.NIL;
    }
    double distance = sMinus1.abs().evalf();
    long digits = precisionDigits(s, engine);
    if (distance < 0.1) {
      // guard digits against the cancelling poles
      long guard = 5 + (long) Math.ceil(-Math.log10(Math.max(distance, 1.0e-300)));
      IExpr value = exactN(table, s, null, digits + guard, engine);
      return rounded(value, s, digits, table.isReal() && s.isReal(), engine);
    }
    if (s.isMachineNumber()) {
      IExpr value = machineValue(table, s.evalfc(), table.isReal() && s.isReal());
      if (value.isPresent()) {
        return value;
      }
    }
    IExpr value = exactN(table, s, hurwitzSum(table, s), digits + 5, engine);
    return rounded(value, s, digits, table.isReal() && s.isReal(), engine);
  }

  /**
   * A value computed with guard digits, rounded to the precision of <code>s</code>: a machine
   * number for a machine number <code>s</code>.
   */
  private static IExpr rounded(IExpr value, IExpr s, long digits, boolean realResult,
      EvalEngine engine) {
    if (!value.isNumber()) {
      return F.NIL;
    }
    if (s.isMachineNumber()) {
      if (realResult) {
        return F.num(value.evalf());
      }
      return F.complexNum(value.evalfc());
    }
    return engine.evaluate(F.N(value, F.ZZ(digits)));
  }

  /**
   * <code>L(s, chi)</code> in machine arithmetic: the Hurwitz sum with the machine Hurwitz zeta
   * function for <code>Re(s) &gt;= 1/2</code>, and the functional equation of the primitive
   * character for <code>Re(s) &lt; 1/2</code>,
   * <code>L(s, chi) = eps * (f/Pi)^(1/2-s) * Gamma((1-s+d)/2)/Gamma((s+d)/2) * L(1-s, Conjugate(chi))</code>,
   * with the parity <code>d</code> and the root number <code>eps = tau(chi)/(I^d*Sqrt(f))</code>;
   * the poles of <code>Gamma((s+d)/2)</code> are the trivial zeros.
   *
   * @param realResult a real character at a real <code>s</code>: return a real number
   * @return {@link F#NIL} if the principal character's zeta value has no machine value here
   */
  private static IExpr machineValue(DirichletCharacterTable table, Complex s,
      boolean realResult) {
    final long f = table.conductor();
    final long order = table.order();
    Complex value;
    if (f == 1) {
      if (s.getReal() < 0.5) {
        return F.NIL; // the zeta function of the principal character
      }
      value = ZetaJS.hurwitzZeta(s, Complex.ONE);
    } else if (s.getReal() >= 0.5) {
      value = primitiveSum(table, s, false);
    } else {
      final int d = table.parity();
      final Complex b = s.add(d).multiply(0.5);
      if (b.getImaginary() == 0.0 && b.getReal() <= 0.0 && Math.rint(b.getReal()) == b.getReal()) {
        return realResult ? F.CD0 : F.complexNum(0.0, 0.0); // a trivial zero
      }
      final Complex oneMinusS = Complex.ONE.subtract(s);
      Complex logA = ComplexGamma.logGamma(oneMinusS.add(d).multiply(0.5));
      Complex logB = ComplexGamma.logGamma(b);
      if (logA == null || logB == null) {
        return F.NIL;
      }
      Complex gammaRatio = logA.subtract(logB).exp();
      Complex power = new Complex(f / Math.PI).pow(new Complex(0.5).subtract(s));
      value = rootNumber(table).multiply(power).multiply(gammaRatio)
          .multiply(primitiveSum(table, oneMinusS, true));
    }
    long[] primes = table.eulerPrimes();
    long[] exponents = table.eulerExponents();
    for (int i = 0; i < primes.length; i++) {
      value = value.multiply(Complex.ONE.subtract(
          rootOfUnity(exponents[i], order).multiply(new Complex(primes[i]).pow(s.negate()))));
    }
    if (!Double.isFinite(value.getReal()) || !Double.isFinite(value.getImaginary())) {
      return F.NIL;
    }
    if (realResult) {
      return F.num(value.getReal());
    }
    return F.complexNum(value);
  }

  /**
   * <code>L(s, chi*) = f^(-s) * Sum(chi*(a)*HurwitzZeta(s, a/f), {a, f-1})</code> of the primitive
   * character, or of its conjugate.
   */
  private static Complex primitiveSum(DirichletCharacterTable table, Complex s,
      boolean conjugate) {
    final long f = table.conductor();
    final long order = table.order();
    int[] star = table.starExponents();
    Complex sum = Complex.ZERO;
    for (int a = 1; a < f; a++) {
      if (star[a] < 0) {
        continue;
      }
      sum = sum.add(rootOfUnity(conjugate ? -star[a] : star[a], order)
          .multiply(ZetaJS.hurwitzZeta(s, new Complex((double) a / f))));
    }
    return sum.multiply(new Complex(f).pow(s.negate()));
  }

  /**
   * The root number <code>tau(chi*)/(I^d*Sqrt(f))</code> of the primitive character, with the
   * Gauss sum <code>tau(chi*) = Sum(chi*(a)*Exp(2*Pi*I*a/f), {a, f-1})</code> summed from exact
   * phases; <code>1</code> for a real character.
   */
  private static Complex rootNumber(DirichletCharacterTable table) {
    if (table.isReal()) {
      return Complex.ONE;
    }
    final long f = table.conductor();
    final long order = table.order();
    final long lcm = order / DirichletCharacterTable.gcd(order, f) * f;
    int[] star = table.starExponents();
    double sr = 0.0;
    double si = 0.0;
    for (long a = 1; a < f; a++) {
      if (star[(int) a] < 0) {
        continue;
      }
      long numerator = (star[(int) a] * (lcm / order) + a * (lcm / f)) % lcm;
      Complex term = rootOfUnity(numerator, lcm);
      sr += term.getReal();
      si += term.getImaginary();
    }
    if (table.parity() == 1) {
      // divide by I
      double t = sr;
      sr = si;
      si = -t;
    }
    double m = Math.hypot(sr, si);
    return new Complex(sr / m, si / m);
  }

  /** <code>Exp(2*Pi*I*e/order)</code> in machine arithmetic, exact on the axes. */
  private static Complex rootOfUnity(long e, long order) {
    e = Math.floorMod(e, order);
    if ((4 * e) % order == 0) {
      switch ((int) (4 * e / order)) {
        case 0:
          return Complex.ONE;
        case 1:
          return Complex.I;
        case 2:
          return Complex.MINUS_ONE;
        default:
          return Complex.MINUS_I;
      }
    }
    double numerator = e;
    if (2 * e > order) {
      numerator -= order; // an angle in (-Pi, Pi]
    }
    final double angle = 2.0 * Math.PI * numerator / order;
    return new Complex(Math.cos(angle), Math.sin(angle));
  }

  /**
   * The Hurwitz sum at the exact rational value of the inexact <code>s</code>, or the exact
   * <code>closedForm</code> if it isn't <code>null</code>, evaluated to <code>digits</code>.
   * Evaluated outside of a numeric evaluation in progress: inside one, <code>Rationalize</code>
   * would give the number back and <code>N</code> would evaluate its argument at machine precision
   * first.
   */
  private static IExpr exactN(DirichletCharacterTable table, IExpr s, IExpr closedForm,
      long digits, EvalEngine engine) {
    final boolean numericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(false);
      IExpr expr = closedForm;
      if (expr == null) {
        IExpr exact = engine.evaluate(F.Rationalize(s, F.C0));
        expr = hurwitzSum(table, exact);
      }
      return engine.evaluate(F.N(expr, F.ZZ(digits)));
    } finally {
      engine.setNumericMode(numericMode);
    }
  }

  /** The decimal precision of an inexact number, machine precision as 16. */
  private static long precisionDigits(IExpr s, EvalEngine engine) {
    if (s.isMachineNumber()) {
      return 16;
    }
    IExpr precision = engine.evaluate(F.unaryAST1(S.Precision, s));
    if (precision.isNumber()) {
      long digits = (long) Math.floor(precision.evalf());
      return Math.max(digits, 16);
    }
    return 16;
  }

  /** <code>f^(-s) * Sum(chi*(a)*HurwitzZeta(s, a/f)) * Product(1-chi*(p)*p^(-s))</code>, unevaluated. */
  private static IExpr hurwitzSum(DirichletCharacterTable table, IExpr s) {
    final long f = table.conductor();
    final long order = table.order();
    IASTAppendable product = F.TimesAlloc(4 + table.eulerPrimes().length);
    if (f == 1) {
      product.append(F.Zeta(s));
    } else {
      int[] star = table.starExponents();
      IASTAppendable sum = F.PlusAlloc((int) f);
      for (int a = 1; a < f; a++) {
        if (star[a] < 0) {
          continue;
        }
        sum.append(F.Times(DirichletCharacterTable.rootOfUnity(star[a], order),
            F.HurwitzZeta(s, F.QQ(a, f))));
      }
      product.append(F.Power(F.ZZ(f), F.Negate(s)));
      product.append(sum);
    }
    long[] primes = table.eulerPrimes();
    long[] exponents = table.eulerExponents();
    for (int i = 0; i < primes.length; i++) {
      product.append(F.Subtract(F.C1, F.Times(
          DirichletCharacterTable.rootOfUnity(exponents[i], order),
          F.Power(F.ZZ(primes[i]), F.Negate(s)))));
    }
    return product;
  }

  // ----------------------------------------------------------------------------------------
  // exact values in the cyclotomic field Q(zeta_n), as vectors in Q[x]/(x^n - 1)
  // ----------------------------------------------------------------------------------------

  /** <code>c[0] + c[1]*zeta_n + ... + c[n-1]*zeta_n^(n-1)</code>, <code>zeta_n = Exp(2*Pi*I/n)</code>. */
  private static final class Cyclotomic {
    final int n;
    final IRational[] c;

    Cyclotomic(int n) {
      this.n = n;
      this.c = new IRational[n];
      java.util.Arrays.fill(c, F.C0);
    }

    void add(int index, IRational value) {
      c[index] = c[index].add(value);
    }

    Cyclotomic times(Cyclotomic other) {
      Cyclotomic result = new Cyclotomic(n);
      for (int i = 0; i < n; i++) {
        if (c[i].isZero()) {
          continue;
        }
        for (int j = 0; j < n; j++) {
          if (other.c[j].isZero()) {
            continue;
          }
          result.add((i + j) % n, c[i].multiply(other.c[j]));
        }
      }
      return result;
    }

    void scale(IRational factor) {
      for (int i = 0; i < n; i++) {
        c[i] = c[i].multiply(factor);
      }
    }

    /**
     * Reduce modulo the cyclotomic polynomial <code>Phi_n</code>: a canonical vector. Left as it is
     * (still the same number) if <code>Phi_n</code> is not available.
     */
    void reduce(EvalEngine engine) {
      if (n <= 1) {
        return;
      }
      BigInteger[] phi = cyclotomicPolynomial(n, engine);
      if (phi == null) {
        return;
      }
      int degree = phi.length - 1;
      for (int i = n - 1; i >= degree; i--) {
        if (c[i].isZero()) {
          continue;
        }
        IRational top = c[i];
        for (int k = 0; k < degree; k++) {
          if (phi[k].signum() != 0) {
            c[i - degree + k] = c[i - degree + k].subtract(top.multiply(F.ZZ(phi[k])));
          }
        }
        c[i] = F.C0;
      }
    }

    /** The evaluated sum of the rational multiples of the roots of unity. */
    IExpr render(EvalEngine engine) {
      reduce(engine);
      IASTAppendable sum = F.PlusAlloc(n);
      for (int m = 0; m < n; m++) {
        if (!c[m].isZero()) {
          sum.append(F.Times(c[m], DirichletCharacterTable.rootOfUnity(m, n)));
        }
      }
      if (sum.argSize() == 0) {
        return F.C0;
      }
      return engine.evaluate(F.Expand(sum.oneIdentity0()));
    }
  }

  /**
   * The coefficients of the cyclotomic polynomial <code>Phi_n</code>, lowest first, from the
   * <code>Cyclotomic</code> builtin, or <code>null</code>.
   */
  private static BigInteger[] cyclotomicPolynomial(int n, EvalEngine engine) {
    IExpr x = F.Dummy("x");
    IExpr coefficients =
        engine.evaluate(F.CoefficientList(F.binaryAST2(S.Cyclotomic, F.ZZ(n), x), x));
    if (!coefficients.isList() || !((IAST) coefficients).forAll(IExpr::isInteger)) {
      return null;
    }
    IAST list = (IAST) coefficients;
    BigInteger[] result = new BigInteger[list.argSize()];
    for (int i = 1; i <= list.argSize(); i++) {
      result[i - 1] = ((IInteger) list.get(i)).toBigNumerator();
    }
    return result;
  }

  /**
   * <code>L(1-m, chi) = -B(m, chi*)/m * Product(1 - chi*(p)*p^(m-1))</code> for <code>m &gt;= 1</code>,
   * with the generalized Bernoulli numbers
   * <code>B(m, chi) = Sum(Binomial(m,i)*BernoulliB(i)*f^(i-1)*Sum(chi(a)*a^(m-i), {a, f}), {i, 0, m})</code>
   * of the primitive character modulo its conductor <code>f</code>.
   */
  private static IExpr exactNonPositive(DirichletCharacterTable table, int m, EvalEngine engine) {
    final boolean numericMode = engine.isNumericMode();
    try {
      // exact arithmetic, also inside N: numeric roots of unity would cancel to noise
      engine.setNumericMode(false);
      return exactNonPositiveValue(table, m, engine);
    } finally {
      engine.setNumericMode(numericMode);
    }
  }

  private static IExpr exactNonPositiveValue(DirichletCharacterTable table, int m,
      EvalEngine engine) {
    final long f = table.conductor();
    final long order = table.order();
    if (f > EXACT_FMAX || order > Integer.MAX_VALUE) {
      return null;
    }
    final int o = (int) order;
    int[] star = table.starExponents();
    // the power sums P_t = Sum(chi(a)*a^t, {a, f}), t = 0..m
    Cyclotomic[] powerSums = new Cyclotomic[m + 1];
    for (int t = 0; t <= m; t++) {
      powerSums[t] = new Cyclotomic(o);
    }
    for (long a = 1; a <= f; a++) {
      int e = star[(int) (a % f)];
      if (e < 0) {
        continue;
      }
      BigInteger power = BigInteger.ONE;
      BigInteger base = BigInteger.valueOf(a);
      for (int t = 0; t <= m; t++) {
        powerSums[t].add(e, F.ZZ(power));
        power = power.multiply(base);
      }
    }
    Cyclotomic acc = new Cyclotomic(o);
    BigInteger fBig = BigInteger.valueOf(f);
    for (int i = 0; i <= m; i++) {
      IRational b = AbstractFractionSym.bernoulliNumber(i);
      if (b == null) {
        return null;
      }
      if (b.isZero()) {
        continue;
      }
      IRational coefficient = F.ZZ(binomial(m, i)).multiply(b);
      coefficient = i == 0 ? coefficient.multiply(F.QQ(BigInteger.ONE, fBig))
          : coefficient.multiply(F.ZZ(fBig.pow(i - 1)));
      Cyclotomic source = powerSums[m - i];
      for (int r = 0; r < o; r++) {
        if (!source.c[r].isZero()) {
          acc.add(r, coefficient.multiply(source.c[r]));
        }
      }
    }
    acc.scale(F.QQ(-1, m));
    long[] primes = table.eulerPrimes();
    long[] exponents = table.eulerExponents();
    for (int i = 0; i < primes.length; i++) {
      Cyclotomic factor = new Cyclotomic(o);
      factor.add(0, F.C1);
      factor.add((int) (exponents[i] % o),
          F.ZZ(BigInteger.valueOf(primes[i]).pow(m - 1)).negate());
      acc = acc.times(factor);
    }
    return acc.render(engine);
  }

  private static BigInteger binomial(int n, int k) {
    BigInteger result = BigInteger.ONE;
    for (int i = 1; i <= k; i++) {
      result = result.multiply(BigInteger.valueOf(n - k + i)).divide(BigInteger.valueOf(i));
    }
    return result;
  }

  /**
   * <code>L(1, chi)</code> for a non-principal character: the closed forms of the primitive
   * character <code>chi*</code> modulo <code>f</code>, times <code>Product(1 - chi*(p)/p)</code>,
   * <ul>
   * <li>odd: <code>Pi*I*tau(chi*)/f^2 * Sum(Conjugate(chi*(a))*a, {a, f-1})</code>,</li>
   * <li>even: <code>-2*tau(chi*)/f * Sum(Conjugate(chi*(a))*Log(Sin(Pi*a/f)), {a, (f-1)/2})</code>,</li>
   * </ul>
   * with the Gauss sum <code>tau(chi*) = Sum(chi*(a)*Exp(2*Pi*I*a/f), {a, f-1})</code>, which is
   * <code>Sqrt(f)</code> or <code>I*Sqrt(f)</code> for a real character.
   */
  private static IExpr exactAtOne(DirichletCharacterTable table, EvalEngine engine) {
    final boolean numericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(false);
      return exactAtOneValue(table, engine);
    } finally {
      engine.setNumericMode(numericMode);
    }
  }

  private static IExpr exactAtOneValue(DirichletCharacterTable table, EvalEngine engine) {
    final long f = table.conductor();
    final long order = table.order();
    if (table.isPrincipal() || f > NUMERIC_FMAX) {
      return null;
    }
    int[] star = table.starExponents();
    long[] primes = table.eulerPrimes();
    long[] exponents = table.eulerExponents();
    if (table.isReal()) {
      IRational euler = F.C1;
      for (int i = 0; i < primes.length; i++) {
        long sign = (exponents[i] % order == 0) ? 1 : -1;
        euler = euler.multiply(F.C1.subtract(F.QQ(sign, primes[i])));
      }
      IExpr sqrtF = F.Power(F.ZZ(f), F.CN1D2);
      if (table.parity() == 1) {
        long sum = 0;
        for (int a = 1; a < f; a++) {
          if (star[a] >= 0) {
            sum += star[a] == 0 ? a : -a;
          }
        }
        return engine.evaluate(F.Times(F.QQ(-sum, f), S.Pi, sqrtF, euler));
      }
      IASTAppendable sum = F.PlusAlloc((int) f);
      for (int a = 1; 2 * a < f; a++) {
        if (star[a] < 0) {
          continue;
        }
        IExpr log = F.Log(F.Sin(F.Times(F.QQ(a, f), S.Pi)));
        sum.append(star[a] == 0 ? log : F.Negate(log));
      }
      return engine.evaluate(F.Times(F.CN2, sqrtF, sum.oneIdentity0(), euler));
    }
    final long lcm = order / DirichletCharacterTable.gcd(order, f) * f;
    if (lcm > S1_NMAX) {
      return null;
    }
    final int n = (int) lcm;
    Cyclotomic tau = new Cyclotomic(n);
    for (int a = 1; a < f; a++) {
      if (star[a] < 0) {
        continue;
      }
      int index = (int) ((star[a] * (lcm / order) + a * (lcm / f)) % lcm);
      tau.add(index, F.C1);
    }
    for (int i = 0; i < primes.length; i++) {
      Cyclotomic factor = new Cyclotomic(n);
      factor.add(0, F.C1);
      factor.add((int) ((exponents[i] % order) * (lcm / order) % lcm), F.QQ(-1, primes[i]));
      tau = tau.times(factor);
    }
    if (table.parity() == 1) {
      Cyclotomic sum = new Cyclotomic(n);
      for (int a = 1; a < f; a++) {
        if (star[a] < 0) {
          continue;
        }
        int conjugate = (int) (((order - star[a]) % order) * (lcm / order) % lcm);
        sum.add(conjugate, F.ZZ(a));
      }
      IExpr algebraic = tau.times(sum).render(engine);
      return engine.evaluate(F.Times(F.CI, F.QQ(1, f * f), S.Pi, algebraic));
    }
    IExpr algebraic = tau.render(engine);
    IASTAppendable sum = F.PlusAlloc((int) f);
    for (int a = 1; 2 * a < f; a++) {
      if (star[a] < 0) {
        continue;
      }
      IExpr conjugate = DirichletCharacterTable.rootOfUnity((order - star[a]) % order, order);
      sum.append(F.Times(conjugate, F.Log(F.Sin(F.Times(F.QQ(a, f), S.Pi)))));
    }
    return engine.evaluate(F.Times(F.QQ(-2, f), algebraic, sum.oneIdentity0()));
  }

  public static void initialize() {
    Initializer.init();
  }

  private DirichletFunctions() {}
}
