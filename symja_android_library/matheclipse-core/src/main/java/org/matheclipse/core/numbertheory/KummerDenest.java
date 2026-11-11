package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Denest <code>r^(1/n)</code> for a positive number <code>r</code> of a field
 * <code>K = Q(p1^(1/M1), ..., pk^(1/Mk))</code> of real radicals of primes: if
 * <code>r^(1/n) = c^(1/n)*A/B</code> with a positive rational <code>c</code> and
 * <code>A, B</code> in <code>K</code>, the nested radical is written that way, like
 * <code>(2^(1/3)-1)^(1/3) == (1 - 2^(1/3) + 2^(2/3))/3^(2/3)</code>.
 *
 * <p>
 * The candidates <code>c</code> are products of small primes with exponents below <code>n</code>,
 * those whose norm condition <code>N(r) = c^d*N(A/B)^n</code> can hold. <code>A</code> and
 * <code>B</code> are an integer relation, found by lattice reduction, of the numbers
 * <code>v*b_j</code> and <code>b_j</code> for the monomial basis <code>b_j</code> of
 * <code>K</code> and <code>v = (r/c)^(1/n)</code>. The relation is then proved exactly:
 * <code>c*A^n == r*B^n</code> by polynomial arithmetic modulo <code>t_i^Mi - p_i</code>.
 */
public final class KummerDenest {

  /** The largest degree of the field of the radicals. */
  private static final int MAX_DEGREE = 27;

  /** The largest root index n. */
  private static final int MAX_ROOT_INDEX = 12;

  /** The decimal digits of the numbers in the lattice. */
  private static final int DIGITS = 60;

  /** The small primes of the candidates c, besides the primes of r. */
  private static final int[] SMALL_PRIMES = {2, 3, 5, 7};

  /** The largest number of candidates c which are tried. */
  private static final int MAX_CANDIDATES = 400;

  private KummerDenest() {}

  /**
   * <code>base^(1/n)</code> denested.
   *
   * @param base a positive number with real radicals of positive rationals
   * @param n the root index
   * @return {@link F#NIL} if the root can't be denested this way
   */
  public static IExpr denest(IExpr base, int n, EvalEngine engine) {
    if (n < 2 || n > MAX_ROOT_INDEX || !base.isFree(x -> x.isSymbol() && !x.isConstantAttribute(),
        false)) {
      return F.NIL;
    }
    // N(expr, digits) sets the significant figures of the output: they are the caller's
    final int significantFigures = engine.getSignificantFigures();
    try {
      double value = base.evalf();
      if (!(value > 0.0) || Double.isInfinite(value)) {
        return F.NIL;
      }
      IExpr together = engine.evaluate(F.Together(base));
      IExpr numerator = engine.evaluate(F.Numerator(together));
      IExpr denominator = engine.evaluate(F.Denominator(together));
      // prime -> root index M of its radicals
      Map<IInteger, Integer> rootIndex = new TreeMap<>();
      if (!collectRadicals(numerator, rootIndex) || !collectRadicals(denominator, rootIndex)
          || rootIndex.isEmpty()) {
        return F.NIL;
      }
      int degree = 1;
      for (int m : rootIndex.values()) {
        degree *= m;
        if (degree > MAX_DEGREE) {
          return F.NIL;
        }
      }
      Field field = new Field(rootIndex, engine);
      IExpr rA = field.toPolynomial(numerator);
      IExpr rB = field.toPolynomial(denominator);
      if (rA.isNIL() || rB.isNIL()) {
        return F.NIL;
      }
      // the sign of a resultant depends on the degrees: only |N(r)| is compared
      IExpr normValue = engine.evaluate(F.Abs(F.Divide(field.norm(rA), field.norm(rB))));
      if (!normValue.isRational() || normValue.isZero()) {
        return F.NIL;
      }
      IRational norm = (IRational) normValue;
      List<IRational> candidates = candidates(norm, degree, n, rootIndex);
      List<IExpr> basis = new ArrayList<>();
      for (IExpr b : field.basis()) {
        basis.add(engine.evaluate(F.N(b, F.ZZ(2 * DIGITS))));
      }
      IExpr baseValue = engine.evaluate(F.N(base, F.ZZ(2 * DIGITS)));
      // first A/B with a rational B for all c (the smaller lattice), then B in the field
      for (boolean rationalB : new boolean[] {true, false}) {
        for (IRational c : candidates) {
          engine.checkTimeLimit();
          IExpr v = engine.evaluate(F.Power(F.Divide(baseValue, c), F.QQ(1, n)));
          IExpr found = relation(v, c, n, basis, field, rA, rB, rationalB, engine);
          if (found.isPresent()) {
            return found;
          }
        }
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setSignificantFigures(significantFigures);
    }
  }

  /**
   * <code>c^(1/n)*A/B</code> if an integer relation <code>B*v - A == 0</code>,
   * <code>v = (r/c)^(1/n)</code>, is found and proved.
   *
   * @param rationalB search <code>v*B - A == 0</code> with an integer <code>B</code>, else with
   *        <code>B</code> in the field
   */
  private static IExpr relation(IExpr v, IRational c, int n, List<IExpr> basis, Field field,
      IExpr rA, IExpr rB, boolean rationalB, EvalEngine engine) {
    int d = basis.size();
    // the numbers v*b_j (only v*1 for an integer B), then b_j
    int bCount = rationalB ? 1 : d;
    int size = bCount + d;
    BigInteger[][] rows = new BigInteger[size][size + 1];
    IExpr[] numbers = new IExpr[size];
    IExpr scale = F.Power(F.C10, F.ZZ(DIGITS));
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        rows[i][j] = i == j ? BigInteger.ONE : BigInteger.ZERO;
      }
      IExpr number = engine.evaluate(i < bCount ? F.Times(v, basis.get(i)) : basis.get(i - bCount));
      numbers[i] = number;
      IExpr scaled = engine.evaluate(F.Round(F.Times(scale, number)));
      if (!scaled.isInteger()) {
        return F.NIL;
      }
      rows[i][size] = ((IInteger) scaled).toBigNumerator();
    }
    BigInteger[][] reduced = LLL.reduce(rows);
    for (BigInteger[] row : reduced) {
      // the relation with the numbers of twice the digits of the lattice: a true one vanishes to
      // those digits, a short vector of the lattice only to about DIGITS
      IASTAppendable residual = F.PlusAlloc(size);
      for (int j = 0; j < size; j++) {
        residual.append(F.Times(F.ZZ(row[j]), numbers[j]));
      }
      if (!engine.evalTrue(F.Less(F.Abs(residual), F.Power(F.C10, F.ZZ(-(3 * DIGITS) / 2))))) {
        continue;
      }
      IASTAppendable a = F.PlusAlloc(d);
      IASTAppendable b = F.PlusAlloc(bCount);
      for (int j = 0; j < bCount; j++) {
        b.append(F.Times(F.ZZ(row[j]), field.monomial(j)));
      }
      for (int j = 0; j < d; j++) {
        a.append(F.Times(F.ZZ(row[bCount + j].negate()), field.monomial(j)));
      }
      IExpr polyA = engine.evaluate(a);
      IExpr polyB = engine.evaluate(b);
      if (polyB.isZero() || polyA.isZero()) {
        continue;
      }
      // c*A^n*rB == rA*B^n
      IExpr difference = F.Subtract(F.Times(c, F.Power(polyA, F.ZZ(n)), rB),
          F.Times(rA, F.Power(polyB, F.ZZ(n))));
      if (field.reduce(difference).isZero()) {
        IExpr quotient =
            engine.evaluate(F.Divide(field.toRadicals(polyA), field.toRadicals(polyB)));
        IExpr result = engine.evaluate(F.Times(F.Power(c, F.QQ(1, n)), quotient));
        if (rationalB) {
          result = engine.evaluate(F.Expand(result));
        }
        // the principal root: the relation was found numerically for the positive value
        double check = result.evalf();
        double expected = v.evalf() * Math.pow(c.doubleValue(), 1.0 / n);
        if (Math.abs(check - expected) <= 1.0e-8 * Math.max(1.0, Math.abs(expected))) {
          return result;
        }
      }
      engine.checkTimeLimit();
    }
    return F.NIL;
  }

  /**
   * The rationals <code>c = Product(q^e)</code>, <code>0 <= e < n</code>, with
   * <code>norm/c^degree</code> an <code>n</code>-th power, the smallest first.
   */
  private static List<IRational> candidates(IRational norm, int degree, int n,
      Map<IInteger, Integer> rootIndex) {
    java.util.Set<BigInteger> primes = new java.util.TreeSet<>();
    for (int p : SMALL_PRIMES) {
      primes.add(BigInteger.valueOf(p));
    }
    for (IInteger p : rootIndex.keySet()) {
      primes.add(p.toBigNumerator());
    }
    for (IInteger part : new IInteger[] {norm.numerator(), norm.denominator()}) {
      for (IExpr factor : part.factorInteger()) {
        BigInteger p = ((IInteger) factor.first()).toBigNumerator();
        if (p.compareTo(BigInteger.ONE) > 0 && p.bitLength() < 32) {
          primes.add(p);
        }
      }
    }
    List<BigInteger> primeList = new ArrayList<>(primes);
    List<BigInteger> products = new ArrayList<>();
    products.add(BigInteger.ONE);
    for (BigInteger p : primeList) {
      List<BigInteger> next = new ArrayList<>();
      for (BigInteger product : products) {
        BigInteger power = BigInteger.ONE;
        for (int e = 0; e < n; e++) {
          next.add(product.multiply(power));
          power = power.multiply(p);
        }
      }
      products = next;
      if (products.size() > 50 * MAX_CANDIDATES) {
        break;
      }
    }
    products.sort(null);
    List<IRational> result = new ArrayList<>();
    for (BigInteger product : products) {
      IRational c = F.ZZ(product);
      // norm / c^degree must be an n-th power of a rational
      IRational quotient = norm.divideBy(c.powerRational(degree));
      if (isPerfectPower(quotient.numerator(), n) && isPerfectPower(quotient.denominator(), n)) {
        result.add(c);
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
      }
    }
    return result;
  }

  private static boolean isPerfectPower(IInteger x, int n) {
    BigInteger value = x.toBigNumerator().abs();
    if (value.signum() == 0) {
      return true;
    }
    BigInteger root = nthRoot(value, n);
    return root.pow(n).equals(value);
  }

  /** The integer part of the n-th root of a positive number. */
  private static BigInteger nthRoot(BigInteger value, int n) {
    BigInteger low = BigInteger.ZERO;
    BigInteger high = BigInteger.ONE.shiftLeft(value.bitLength() / n + 1);
    while (low.compareTo(high) < 0) {
      BigInteger mid = low.add(high).add(BigInteger.ONE).shiftRight(1);
      if (mid.pow(n).compareTo(value) <= 0) {
        low = mid;
      } else {
        high = mid.subtract(BigInteger.ONE);
      }
    }
    return low;
  }

  /**
   * Collect the primes and root indices of the radicals <code>b^e</code> (positive rational
   * <code>b</code>, rational <code>e</code>) of <code>expr</code>, a polynomial expression in such
   * radicals with rational coefficients.
   *
   * @return <code>false</code> for any other subexpression
   */
  private static boolean collectRadicals(IExpr expr, Map<IInteger, Integer> rootIndex) {
    if (expr.isRational()) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      for (IExpr arg : (IAST) expr) {
        if (!collectRadicals(arg, rootIndex)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isPower()) {
      IExpr b = expr.base();
      IExpr e = expr.exponent();
      if (e.isInteger() && e.isPositive()) {
        return collectRadicals(b, rootIndex);
      }
      if (b.isRational() && b.isPositive() && e.isRational()) {
        IRational exponent = (IRational) e;
        for (IInteger part : new IInteger[] {((IRational) b).numerator(),
            ((IRational) b).denominator()}) {
          for (IExpr factor : part.factorInteger()) {
            IInteger p = (IInteger) factor.first();
            if (p.isOne() || p.isMinusOne()) {
              continue;
            }
            if (p.toBigNumerator().bitLength() > 31) {
              return false;
            }
            IRational pe = exponent.multiply((IInteger) factor.second());
            int m = pe.denominator().toIntDefault();
            if (m <= 0) {
              return false;
            }
            if (m > 1) {
              Integer old = rootIndex.get(p);
              rootIndex.put(p, old == null ? m : lcm(old, m));
            }
          }
        }
        return true;
      }
    }
    return false;
  }

  private static int lcm(int a, int b) {
    return a / java.math.BigInteger.valueOf(a).gcd(java.math.BigInteger.valueOf(b)).intValue() * b;
  }

  /** Arithmetic in the field of radicals: polynomials in t_i modulo t_i^Mi - p_i. */
  private static final class Field {
    final IInteger[] primes;
    final int[] indices;
    final ISymbol[] variables;
    final EvalEngine engine;

    Field(Map<IInteger, Integer> rootIndex, EvalEngine engine) {
      this.engine = engine;
      int k = rootIndex.size();
      primes = new IInteger[k];
      indices = new int[k];
      variables = new ISymbol[k];
      int i = 0;
      for (Map.Entry<IInteger, Integer> entry : rootIndex.entrySet()) {
        primes[i] = entry.getKey();
        indices[i] = entry.getValue();
        variables[i] = F.Dummy("t" + i);
        i++;
      }
    }

    /** The monomial basis t^a, 0 <= a_i < M_i, as numbers. */
    List<IExpr> basis() {
      List<IExpr> result = new ArrayList<>();
      for (int j = 0; j < size(); j++) {
        result.add(engine.evaluate(toRadicals(monomial(j))));
      }
      return result;
    }

    int size() {
      int d = 1;
      for (int m : indices) {
        d *= m;
      }
      return d;
    }

    /** The j-th monomial of the basis, in the variables t_i. */
    IExpr monomial(int j) {
      IASTAppendable times = F.TimesAlloc(primes.length);
      for (int i = 0; i < primes.length; i++) {
        int a = j % indices[i];
        j /= indices[i];
        times.append(F.Power(variables[i], F.ZZ(a)));
      }
      return engine.evaluate(times);
    }

    /** The radicals b^e of expr written with the variables t_i. */
    IExpr toPolynomial(IExpr expr) {
      IExpr replaced = expr.replaceAll(x -> {
        if (x.isPower() && x.base().isRational() && x.base().isPositive()
            && x.exponent().isRational() && !x.exponent().isInteger()) {
          return radical((IRational) x.base(), (IRational) x.exponent());
        }
        return F.NIL;
      });
      return reduce(replaced.orElse(expr));
    }

    private IExpr radical(IRational b, IRational e) {
      IASTAppendable times = F.TimesAlloc(4);
      for (IInteger part : new IInteger[] {b.numerator(), b.denominator()}) {
        boolean inverse = part == b.denominator();
        for (IExpr factor : part.factorInteger()) {
          IInteger p = (IInteger) factor.first();
          if (p.isOne()) {
            continue;
          }
          IRational pe = e.multiply((IInteger) factor.second());
          if (inverse) {
            pe = pe.negate();
          }
          int i = indexOf(p);
          if (i < 0) {
            times.append(F.Power(p, pe));
            continue;
          }
          // p^(N/M) = p^floor(N/M) * t^(N mod M)
          IInteger scaled = (IInteger) pe.multiply(indices[i]);
          IInteger m = F.ZZ(indices[i]);
          IInteger quotient = (IInteger) scaled.divideBy(m).floorFraction();
          IInteger remainder = scaled.subtract(quotient.multiply(m));
          times.append(F.Power(p, quotient));
          times.append(F.Power(variables[i], remainder));
        }
      }
      return times;
    }

    private int indexOf(IInteger p) {
      for (int i = 0; i < primes.length; i++) {
        if (primes[i].equals(p)) {
          return i;
        }
      }
      return -1;
    }

    /** expr expanded and reduced modulo t_i^Mi - p_i. */
    IExpr reduce(IExpr expr) {
      IExpr result = engine.evaluate(F.Expand(expr));
      for (int i = 0; i < primes.length; i++) {
        result = engine.evaluate(F.PolynomialRemainder(result,
            F.Subtract(F.Power(variables[i], F.ZZ(indices[i])), primes[i]), variables[i]));
      }
      return engine.evaluate(F.Expand(result));
    }

    /** The norm of the field element x: the resultants with t_i^Mi - p_i. */
    IExpr norm(IExpr x) {
      IExpr result = x;
      for (int i = 0; i < primes.length; i++) {
        result = engine.evaluate(F.Resultant(result,
            F.Subtract(F.Power(variables[i], F.ZZ(indices[i])), primes[i]), variables[i]));
      }
      return result;
    }

    IExpr toRadicals(IExpr x) {
      IASTAppendable rules = F.ListAlloc(primes.length);
      for (int i = 0; i < primes.length; i++) {
        rules.append(F.Rule(variables[i], F.Power(primes[i], F.QQ(1, indices[i]))));
      }
      return engine.evaluate(F.ReplaceAll(x, rules));
    }
  }
}
