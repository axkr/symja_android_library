package org.matheclipse.core.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apfloat.Apfloat;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionExpand;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.PartitionNumbers;
import org.matheclipse.core.numbertheory.Primality;
import org.matheclipse.core.sympy.ntheory.ResidueNTheory;

/**
 * Integer sequences and related number theory functions: <code>ThueMorse, RudinShapiro,
 * NumberExpand, NumberCompose, FiniteAbelianGroupCount, ChampernowneNumber, NorlundB,
 * PowerModList, QBinomial, QFactorial, DirichletConvolve</code>.
 */
public final class IntegerSequenceFunctions {

  /** Largest <code>n</code> of <code>NorlundB(n, ...)</code>. */
  private static final int NORLUND_MAX = 1000;

  /** Largest <code>n</code> of an explicit <code>QFactorial</code> or <code>QBinomial</code>. */
  private static final int Q_MAX = 2000;

  /**
   * Message <code>nnintprm</code> for a number argument which is not an integer; a symbolic
   * argument stays unevaluated silently.
   */
  private static @AllowNIL IExpr nonNegativeIntegerExpected(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isNumber()) {
      // Parameter `1` at position `2` in `3` is expected to be a non-negative integer.
      return Errors.printMessage(ast.topHead(), "nnintprm", F.list(arg1, F.C1, ast), engine);
    }
    return F.NIL;
  }

  private static class Initializer {
    private static void init() {
      S.ThueMorse.setEvaluator(new ThueMorse());
      S.RudinShapiro.setEvaluator(new RudinShapiro());
      S.NumberExpand.setEvaluator(new NumberExpand());
      S.NumberCompose.setEvaluator(new NumberCompose());
      S.FiniteAbelianGroupCount.setEvaluator(new FiniteAbelianGroupCount());
      S.ChampernowneNumber.setEvaluator(new ChampernowneNumber());
      S.NorlundB.setEvaluator(new NorlundB());
      S.PowerModList.setEvaluator(new PowerModList());
      S.QFactorial.setEvaluator(new QFactorial());
      S.QBinomial.setEvaluator(new QBinomial());
      S.DirichletConvolve.setEvaluator(new DirichletConvolve());
    }
  }

  /**
   * <code>ThueMorse(n)</code> - the parity of the number of ones in the binary digits of
   * <code>n</code>.
   */
  private static final class ThueMorse extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        // the sequence is extended to negative n by ThueMorse(-n) == ThueMorse(n)
        BigInteger n = ((IInteger) arg1).toBigNumerator().abs();
        return (n.bitCount() & 1) == 0 ? F.C0 : F.C1;
      }
      return nonNegativeIntegerExpected(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>RudinShapiro(n)</code> - <code>(-1)^k</code> where <code>k</code> is the number of
   * (possibly overlapping) blocks <code>11</code> in the binary digits of <code>n</code>.
   */
  private static final class RudinShapiro extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        BigInteger n = ((IInteger) arg1).toBigNumerator();
        if (n.signum() < 0) {
          return F.NIL;
        }
        int blocks = n.and(n.shiftRight(1)).bitCount();
        return (blocks & 1) == 0 ? F.C1 : F.CN1;
      }
      return nonNegativeIntegerExpected(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>NumberExpand(n, b, len)</code> - the list of the digits of the integer <code>n</code> in
   * base <code>b</code> multiplied by their place values, most significant first.
   */
  private static final class NumberExpand extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isInteger()) {
        if (arg1.isFraction() && ast.isAST1()) {
          return F.list(arg1);
        }
        if (arg1 instanceof org.matheclipse.core.interfaces.INum && !arg1.isZero()
            && (ast.isAST1() || (ast.isAST2() && ast.arg2().isInteger() && ast.arg2().equals(F.C10)))) {
          return machineRealExpand(((org.matheclipse.core.interfaces.INum) arg1).doubleValue());
        }
        return F.NIL;
      }
      BigInteger base = BigInteger.TEN;
      if (ast.argSize() >= 2) {
        IExpr arg2 = ast.arg2();
        if (!arg2.isInteger() || ((IInteger) arg2).toBigNumerator().compareTo(BigInteger.ONE) <= 0) {
          // Base `1` is not an integer greater than 1.
          return Errors.printMessage(ast.topHead(), "ibase", F.list(arg2, F.C1), engine);
        }
        base = ((IInteger) arg2).toBigNumerator();
      }
      int length = -1;
      if (ast.argSize() == 3) {
        length = ast.arg3().toIntDefault();
        if (length < 0) {
          return Errors.printMessage(ast.topHead(), "intnm", F.list(ast, F.C3), engine);
        }
      }
      BigInteger n = ((IInteger) arg1).toBigNumerator();
      int sign = n.signum();
      n = n.abs();
      java.util.ArrayList<BigInteger> digits = new java.util.ArrayList<>();
      while (n.signum() > 0) {
        BigInteger[] qr = n.divideAndRemainder(base);
        digits.add(qr[1]);
        n = qr[0];
      }
      if (digits.isEmpty()) {
        digits.add(BigInteger.ZERO);
      }
      // the terms start at the leading digit; a length beyond the units place appends the zero
      // terms of the (empty) fractional part
      int size = length >= 0 ? length : digits.size();
      IASTAppendable result = F.ListAlloc(size);
      for (int i = 0; i < size; i++) {
        int k = digits.size() - 1 - i;
        if (k < 0) {
          result.append(F.C0);
          continue;
        }
        BigInteger term = digits.get(k).multiply(base.pow(k));
        result.append(F.ZZ(sign < 0 ? term.negate() : term));
      }
      return result;
    }

    /**
     * The 15 exact decimal place-value terms of a machine real, followed by the machine-real
     * remainder.
     */
    private static IExpr machineRealExpand(double x) {
      final int terms = 15;
      BigDecimal value = new BigDecimal(Double.toString(Math.abs(x)))
          .round(new MathContext(terms, java.math.RoundingMode.HALF_EVEN));
      // exponent of the leading digit
      int leading = value.precision() - value.scale() - 1;
      BigInteger unscaled = value.unscaledValue();
      int lowest = -value.scale();
      IASTAppendable result = F.ListAlloc(terms + 1);
      BigDecimal sum = BigDecimal.ZERO;
      for (int i = 0; i < terms; i++) {
        int position = leading - i;
        BigInteger digit = BigInteger.ZERO;
        if (position >= lowest) {
          digit = unscaled.divide(BigInteger.TEN.pow(position - lowest)).mod(BigInteger.TEN);
        }
        BigDecimal term = new BigDecimal(digit).scaleByPowerOfTen(position);
        if (x < 0) {
          term = term.negate();
        }
        sum = sum.add(term);
        if (position >= 0) {
          result.append(F.ZZ(term.toBigIntegerExact()));
        } else {
          result.append(F.QQ(F.ZZ(term.unscaledValue()), F.ZZ(BigInteger.TEN.pow(term.scale()))));
        }
      }
      result.append(F.num(x - sum.doubleValue()));
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /** <code>NumberCompose({c1, c2, ...}, {u1, u2, ...})</code> - <code>c1*u1 + c2*u2 + ...</code> */
  private static final class NumberCompose extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isList() && arg2.isList()) {
        IAST coefficients = (IAST) arg1;
        IAST units = (IAST) arg2;
        if (coefficients.size() > units.size()) {
          // List `1` of coefficients cannot be longer than list `2` of units.
          return Errors.printMessage(ast.topHead(), "ulen", F.list(arg1, arg2), engine);
        }
        if (!isNonincreasingPositive(units, engine)) {
          // `1` is not a list of nonincreasing positive numbers.
          return Errors.printMessage(ast.topHead(), "psv", F.list(arg2), engine);
        }
        IASTAppendable sum = F.PlusAlloc(coefficients.argSize());
        for (int i = 1; i < coefficients.size(); i++) {
          sum.append(F.Times(coefficients.get(i), units.get(i)));
        }
        return sum.oneIdentity0();
      }
      return F.NIL;
    }

    /** The units must be finite positive real numbers, each at most the one before it. */
    private static boolean isNonincreasingPositive(IAST units, EvalEngine engine) {
      if (units.isEmpty()) {
        return false;
      }
      IExpr previous = F.NIL;
      for (int i = 1; i < units.size(); i++) {
        IExpr unit = units.get(i);
        if (!unit.isRealResult() || !unit.isPositiveResult() || unit.isInfinity()) {
          return false;
        }
        if (previous.isPresent() && !engine.evalTrue(F.LessEqual(unit, previous))) {
          return false;
        }
        previous = unit;
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>FiniteAbelianGroupCount(n)</code> - the number of abelian groups of order
   * <code>n</code>, the product of <code>PartitionsP(e)</code> over the exponents <code>e</code> of
   * the prime factorization of <code>n</code>.
   */
  private static final class FiniteAbelianGroupCount extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        BigInteger n = ((IInteger) arg1).toBigNumerator();
        if (n.signum() == 0) {
          return F.C0;
        }
        if (n.signum() < 0) {
          // Positive integer expected at position `2` in `1`.
          return Errors.printMessage(ast.topHead(), "intp", F.list(ast, F.C1), engine);
        }
        BigInteger count = BigInteger.ONE;
        if (!n.equals(BigInteger.ONE)) {
          SortedMap<BigInteger, Integer> factors = new Primality().factorInteger(n);
          for (Map.Entry<BigInteger, Integer> entry : factors.entrySet()) {
            count = count.multiply(PartitionNumbers.partitionsP(entry.getValue()));
          }
        }
        return F.ZZ(count);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>ChampernowneNumber(b)</code> - the constant <code>0.123456789101112...</code> formed by
   * concatenating the positive integers in base <code>b</code>.
   */
  private static final class ChampernowneNumber extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public @AllowNIL IExpr numericEval(final IAST ast, final EvalEngine engine) {
      BigInteger base = BigInteger.TEN;
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (!arg1.isInteger()
            || ((IInteger) arg1).toBigNumerator().compareTo(BigInteger.ONE) <= 0) {
          return F.NIL;
        }
        base = ((IInteger) arg1).toBigNumerator();
        if (base.bitLength() > 31) {
          return F.NIL;
        }
      }
      long decimalDigits = engine.isArbitraryMode() ? engine.getNumericPrecision() : 20L;
      double digitsPerDecimal = Math.log(10.0) / Math.log(base.doubleValue());
      long needed = (long) Math.ceil(decimalDigits * digitsPerDecimal) + 4;
      BigInteger numerator = BigInteger.ZERO;
      long length = 0;
      BigInteger k = BigInteger.ONE;
      BigInteger power = base; // the smallest power of the base greater than k
      int digitsOfK = 1;
      while (length < needed) {
        if (k.compareTo(power) >= 0) {
          power = power.multiply(base);
          digitsOfK++;
        }
        numerator = numerator.multiply(base.pow(digitsOfK)).add(k);
        length += digitsOfK;
        k = k.add(BigInteger.ONE);
        if ((length & 0x3FF) == 0) {
          engine.checkTimeLimit();
        }
      }
      BigInteger denominator = base.pow((int) length);
      if (engine.isArbitraryMode()) {
        long precision = engine.getNumericPrecision();
        return F.num(new Apfloat(numerator, precision).divide(new Apfloat(denominator, precision)));
      }
      BigDecimal value =
          new BigDecimal(numerator).divide(new BigDecimal(denominator), MathContext.DECIMAL64);
      return F.num(value.doubleValue());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // N must not turn the base into a real number
      newSymbol.setAttributes(Attribute.NHOLDALL, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>NorlundB(n, a)</code> and <code>NorlundB(n, a, x)</code> - the generalized Bernoulli
   * numbers and polynomials, the coefficients of <code>(t/(E^t-1))^a*E^(x*t)</code> times
   * <code>n!</code>.
   */
  private static final class NorlundB extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isInteger()) {
        return F.NIL;
      }
      int n = arg1.toIntDefault();
      if (n < 0) {
        return F.NIL;
      }
      if (n > NORLUND_MAX) {
        return F.NIL;
      }
      IExpr a = ast.arg2();
      IExpr[] numbers = norlundNumbers(n, a, engine);
      if (ast.isAST2()) {
        return numbers[n];
      }
      IExpr x = ast.arg3();
      IASTAppendable sum = F.PlusAlloc(n + 1);
      for (int k = 0; k <= n; k++) {
        sum.append(F.Times(F.Binomial(F.ZZ(n), F.ZZ(k)), numbers[k], F.Power(x, F.ZZ(n - k))));
      }
      return engine.evaluate(F.Expand(sum));
    }

    /**
     * <code>B(k, a)</code> for <code>0 &lt;= k &lt;= n</code> from <code>G' = a*L'*G</code> with
     * <code>G = (t/(E^t-1))^a</code> and <code>L = Log(t/(E^t-1)) = -t/2 - Sum(B(k)*t^k/(k*k!),
     * {k,2,Infinity})</code>.
     */
    private static IExpr[] norlundNumbers(int n, IExpr a, EvalEngine engine) {
      // k * l(k), the coefficients of t^(k-1) in L'
      IExpr[] kl = new IExpr[n + 1];
      for (int k = 1; k <= n; k++) {
        if (k == 1) {
          kl[k] = F.CN1D2;
        } else {
          kl[k] = engine.evaluate(F.Times(F.CN1, F.BernoulliB(F.ZZ(k)),
              F.Power(F.Factorial(F.ZZ(k)), F.CN1)));
        }
      }
      // g(m), the coefficients of t^m in G
      IExpr[] g = new IExpr[n + 1];
      g[0] = F.C1;
      for (int m = 1; m <= n; m++) {
        IASTAppendable sum = F.PlusAlloc(m);
        for (int k = 1; k <= m; k++) {
          if (!kl[k].isZero()) {
            sum.append(F.Times(kl[k], g[m - k]));
          }
        }
        g[m] = engine.evaluate(F.Expand(F.Times(F.QQ(1, m), a, sum.oneIdentity0())));
        engine.checkTimeLimit();
      }
      IExpr[] result = new IExpr[n + 1];
      for (int m = 0; m <= n; m++) {
        result[m] = engine.evaluate(F.Expand(F.Times(F.Factorial(F.ZZ(m)), g[m])));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>PowerModList(a, s/r, m)</code> - all <code>0 &lt;= x &lt; m</code> with <code>x^r ==
   * a^s (mod m)</code>.
   */
  private static final class PowerModList extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr a = ast.arg1();
      IExpr b = ast.arg2();
      IExpr m = ast.arg3();
      if (!a.isInteger() || !b.isRational() || !m.isInteger()) {
        return F.NIL;
      }
      BigInteger modulus = ((IInteger) m).toBigNumerator();
      if (modulus.signum() <= 0) {
        return F.NIL;
      }
      BigInteger base = ((IInteger) a).toBigNumerator().mod(modulus);
      BigInteger s = ((org.matheclipse.core.interfaces.IRational) b).toBigNumerator();
      BigInteger r = ((org.matheclipse.core.interfaces.IRational) b).toBigDenominator();
      BigInteger target;
      if (s.signum() < 0) {
        if (!base.gcd(modulus).equals(BigInteger.ONE)) {
          // `1` is not invertible modulo `2`.
          return Errors.printMessage(S.PowerMod, "ninv", F.list(a, m), engine);
        }
        target = base.modInverse(modulus).modPow(s.negate(), modulus);
      } else {
        target = base.modPow(s, modulus);
      }
      if (r.equals(BigInteger.ONE)) {
        return F.list(F.ZZ(target));
      }
      List<BigInteger> roots = ResidueNTheory.allNthRootsMod(target, r, modulus);
      if (roots == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(roots.size());
      for (BigInteger root : roots) {
        result.append(F.ZZ(root));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>QFactorial(n, q)</code> - the q-analog of the factorial,
   * <code>QPochhammer(q, q, n)/(1-q)^n</code>.
   */
  private static final class QFactorial extends AbstractFunctionEvaluator
      implements IFunctionExpand {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr q = ast.arg2();
      if (arg1.isZero()) {
        return F.C1;
      }
      if (!q.isNumber()) {
        // a symbolic q stays unevaluated
        return F.NIL;
      }
      if (arg1.isInteger()) {
        int n = arg1.toIntDefault();
        if (n < 0 || n > Q_MAX) {
          return F.NIL;
        }
        return engine.evaluate(qFactorial(n, q));
      }
      if (arg1.isNumber() && (arg1.isInexactNumber() || q.isInexactNumber())) {
        // non-integer n: the q-gamma form inside of the unit disk
        IExpr absQ = engine.evaluate(F.Abs(q));
        if (absQ.isReal() && ((org.matheclipse.core.interfaces.IReal) absQ).isLT(F.C1)) {
          return functionExpand(ast, engine);
        }
      }
      return F.NIL;
    }

    /** <code>(1-q)^(-n)*QPochhammer(q, q)/QPochhammer(q^(n+1), q)</code> */
    @Override
    public @AllowNIL IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr q = ast.arg2();
      return F.Times(F.Power(F.Subtract(F.C1, q), F.Negate(n)), F.binaryAST2(S.QPochhammer, q, q),
          F.Power(qPochhammerTail(n, q), F.CN1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /** <code>[1]_q*[2]_q*...*[n]_q</code> with the q-integers <code>[k]_q = 1+q+...+q^(k-1)</code>. */
  private static IExpr qFactorial(int n, IExpr q) {
    IASTAppendable product = F.TimesAlloc(n);
    for (int k = 2; k <= n; k++) {
      product.append(qInteger(k, q));
    }
    return product.oneIdentity1();
  }

  /** <code>QPochhammer(q^(n+1), q)</code> */
  private static IExpr qPochhammerTail(IExpr n, IExpr q) {
    return F.binaryAST2(S.QPochhammer, F.Power(q, F.Plus(F.C1, n)), q);
  }

  private static IExpr qInteger(int k, IExpr q) {
    IASTAppendable sum = F.PlusAlloc(k);
    sum.append(F.C1);
    for (int j = 1; j < k; j++) {
      sum.append(F.Power(q, F.ZZ(j)));
    }
    return sum;
  }

  /**
   * <code>QBinomial(n, m, q)</code> - the Gaussian binomial coefficient
   * <code>QPochhammer(q, q, n)/(QPochhammer(q, q, m)*QPochhammer(q, q, n-m))</code>.
   */
  private static final class QBinomial extends AbstractFunctionEvaluator
      implements IFunctionExpand {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isInteger() && arg2.isInteger()) {
        int n = arg1.toIntDefault();
        int m = arg2.toIntDefault();
        if (n < 0 || n > Q_MAX || m == Integer.MIN_VALUE) {
          return F.NIL;
        }
        IExpr q = ast.arg3();
        if (m == 0) {
          return F.C1;
        }
        if (!q.isNumber()) {
          // a symbolic q stays unevaluated
          return F.NIL;
        }
        if (m < 0 || m > n) {
          return F.C0;
        }
        // q-Pascal rule C(i, j) = C(i-1, j-1) + q^j*C(i-1, j); no division, so roots of unity
        // q are fine
        int k = Math.min(m, n - m);
        IExpr[] row = new IExpr[k + 1];
        row[0] = F.C1;
        for (int j = 1; j <= k; j++) {
          row[j] = F.C0;
        }
        for (int i = 1; i <= n; i++) {
          for (int j = Math.min(i, k); j >= 1; j--) {
            row[j] = engine.evaluate(F.Plus(row[j - 1], F.Times(F.Power(q, F.ZZ(j)), row[j])));
          }
          engine.checkTimeLimit();
        }
        return row[k];
      }
      return F.NIL;
    }

    /**
     * <code>QPochhammer(q^(m+1), q)*QPochhammer(q^(n-m+1), q)/(QPochhammer(q, q)*
     * QPochhammer(q^(n+1), q))</code>
     */
    @Override
    public @AllowNIL IExpr functionExpand(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr m = ast.arg2();
      IExpr q = ast.arg3();
      return F.Times(qPochhammerTail(m, q), qPochhammerTail(F.Subtract(n, m), q),
          F.Power(F.Times(F.binaryAST2(S.QPochhammer, q, q), qPochhammerTail(n, q)), F.CN1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>DirichletConvolve(f, g, n, m)</code> - <code>Sum(f(d)*g(m/d), d divides m)</code> where
   * <code>f</code> and <code>g</code> are expressions in <code>n</code>.
   */
  private static final class DirichletConvolve extends AbstractFunctionEvaluator {
    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr f = ast.arg1();
      IExpr g = ast.arg2();
      IExpr n = ast.arg3();
      IExpr m = ast.arg4();
      if (!n.isVariable()) {
        return F.NIL;
      }
      if (!m.isNumber()) {
        return divisorSigmaForm(f, g, n, m);
      }
      if (!m.isInteger()) {
        return F.NIL;
      }
      IInteger mInteger = (IInteger) m;
      if (!mInteger.isPositive()) {
        return F.NIL;
      }
      IExpr divisors = engine.evaluate(F.Divisors(mInteger));
      if (!divisors.isList()) {
        return F.NIL;
      }
      IAST list = (IAST) divisors;
      IASTAppendable sum = F.PlusAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IInteger d = (IInteger) list.get(i);
        IInteger e = mInteger.div(d);
        sum.append(F.Times(F.subst(f, n, d), F.subst(g, n, e)));
      }
      return sum.oneIdentity0();
    }

    /**
     * Symbolic <code>m</code>: <code>c*n^k</code> convolved with a constant <code>d</code> is
     * <code>c*d*DivisorSigma(k, m)</code>.
     */
    private static @AllowNIL IExpr divisorSigmaForm(IExpr f, IExpr g, IExpr n, IExpr m) {
      if (g.isFree(n)) {
        IExpr k = powerOfVariable(f, n);
        if (k.isPresent()) {
          return F.Times(g, f.isFree(n) ? f : F.C1, F.DivisorSigma(k, m));
        }
      }
      if (f.isFree(n)) {
        IExpr k = powerOfVariable(g, n);
        if (k.isPresent()) {
          return F.Times(f, g.isFree(n) ? g : F.C1, F.DivisorSigma(k, m));
        }
      }
      return F.NIL;
    }

    /** <code>k</code> if <code>expr</code> is free of <code>n</code> (k=0), <code>n</code> or <code>n^k</code>. */
    private static @AllowNIL IExpr powerOfVariable(IExpr expr, IExpr n) {
      if (expr.isFree(n)) {
        return F.C0;
      }
      if (expr.equals(n)) {
        return F.C1;
      }
      if (expr.isPower() && expr.base().equals(n) && expr.exponent().isFree(n)) {
        return expr.exponent();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private IntegerSequenceFunctions() {}
}
