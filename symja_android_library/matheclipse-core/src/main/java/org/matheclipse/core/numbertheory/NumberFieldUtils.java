package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.AlgebraicNumberUtils;
import io.github.mangara.diophantine.XYPair;
import io.github.mangara.diophantine.utils.ContinuedFraction;

/**
 * Utilities for algebraic number fields <code>Q(theta)</code>.
 *
 * <p>
 * The norm, the trace and the signature are available for every degree, because they only depend on
 * the minimal polynomial. The invariants which need the ring of integers (discriminant, integral
 * basis, fundamental unit, regulator, class number) are implemented for quadratic fields, and for
 * higher degrees only when the polynomial discriminant is squarefree, in which case
 * <code>Z[theta]</code> is already the maximal order.
 */
public final class NumberFieldUtils {

  /** The quadratic field <code>Q(Sqrt(d))</code> with <code>d</code> squarefree. */
  public static final class QuadraticField {
    /** The squarefree kernel, so the field is <code>Q(Sqrt(d))</code>. */
    public final IInteger d;

    /** The discriminant of the field: <code>d</code> or <code>4*d</code>. */
    public final IInteger discriminant;

    private QuadraticField(IInteger d, IInteger discriminant) {
      this.d = d;
      this.discriminant = discriminant;
    }

    public boolean isReal() {
      return d.isPositive();
    }
  }

  private NumberFieldUtils() {}

  /**
   * The monic minimal polynomial of <code>theta</code> over the rationals, as the list of its
   * coefficients in ascending order of the exponent.
   *
   * @param theta an explicit algebraic number
   * @param engine the evaluation engine
   * @return <code>null</code> if the minimal polynomial couldn't be determined or if it doesn't
   *         have rational coefficients
   */
  public static IRational[] minimalPolynomialCoefficients(IExpr theta, EvalEngine engine) {
    if (!AlgebraicNumberUtils.isExplicitAlgebraicNumber(theta)) {
      return null;
    }
    if (theta.isRational()) {
      // x - theta
      return new IRational[] {((IRational) theta).negate(), F.C1};
    }
    ISymbol x = F.Dummy("x");
    IExpr polynomial;
    if (theta.isAST(S.Root, 3) || theta.isAST(S.Root, 4)) {
      // MinimalPolynomial doesn't handle Root() objects, but the defining polynomial is right
      // there; only its irreducible factor which vanishes at the root is needed
      polynomial = rootObjectPolynomial((IAST) theta, x, engine);
      if (polynomial == null) {
        return null;
      }
    } else {
      polynomial = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, theta, x));
      if (polynomial.isAST(S.MinimalPolynomial) || polynomial.isNIL()) {
        return null;
      }
    }
    IExpr coefficients = engine.evaluate(F.CoefficientList(polynomial, x));
    if (!coefficients.isList() || coefficients.size() < 3) {
      return null;
    }
    IAST list = (IAST) coefficients;
    IRational[] result = new IRational[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      if (!list.get(i).isRational()) {
        return null;
      }
      result[i - 1] = (IRational) list.get(i);
    }
    IRational leading = result[result.length - 1];
    if (leading.isZero()) {
      return null;
    }
    if (!leading.isOne()) {
      for (int i = 0; i < result.length; i++) {
        result[i] = (IRational) result[i].divide(leading);
      }
    }
    return result;
  }

  /**
   * The irreducible factor of the defining polynomial of a <code>Root[f, k]</code> object which
   * vanishes at that root, expressed in the variable <code>x</code>.
   *
   * @return <code>null</code> if it couldn't be determined
   */
  private static IExpr rootObjectPolynomial(IAST root, ISymbol x, EvalEngine engine) {
    IExpr function = root.arg1();
    if (!function.isFunction()) {
      return null;
    }
    IExpr polynomial = engine.evaluate(F.subst(function.first(), F.Slot1, x));
    IExpr factors = engine.evaluate(F.FactorList(polynomial));
    if (!factors.isList()) {
      return polynomial;
    }
    IExpr value = engine.evaluate(F.N(root));
    if (!value.isNumber()) {
      return polynomial;
    }
    IAST list = (IAST) factors;
    IExpr best = F.NIL;
    IExpr bestResidual = F.NIL;
    for (int i = 1; i < list.size(); i++) {
      IExpr entry = list.get(i);
      if (!entry.isList2() || entry.first().isFree(x)) {
        continue;
      }
      IExpr factor = entry.first();
      IExpr residual = engine.evaluate(F.Abs(F.subst(factor, x, value)));
      if (!residual.isReal()) {
        continue;
      }
      if (bestResidual.isNIL() || ((IReal) residual).isLT((IReal) bestResidual)) {
        bestResidual = residual;
        best = factor;
      }
    }
    return best.isPresent() ? best : polynomial;
  }

  /**
   * The norm of <code>theta</code> in <code>Q(theta)</code>, i.e. <code>(-1)^n</code> times the
   * constant coefficient of its monic minimal polynomial.
   *
   * @return {@link F#NIL} if the minimal polynomial couldn't be determined
   */
  public static IExpr norm(IExpr theta, EvalEngine engine) {
    IRational[] coefficients = minimalPolynomialCoefficients(theta, engine);
    if (coefficients == null) {
      return F.NIL;
    }
    int degree = coefficients.length - 1;
    IRational constant = coefficients[0];
    return degree % 2 == 0 ? constant : constant.negate();
  }

  /**
   * The trace of <code>theta</code> in <code>Q(theta)</code>, i.e. the negated second highest
   * coefficient of its monic minimal polynomial.
   *
   * @return {@link F#NIL} if the minimal polynomial couldn't be determined
   */
  public static IExpr trace(IExpr theta, EvalEngine engine) {
    IRational[] coefficients = minimalPolynomialCoefficients(theta, engine);
    if (coefficients == null) {
      return F.NIL;
    }
    return coefficients[coefficients.length - 2].negate();
  }

  /**
   * The quadratic field generated by <code>theta</code>.
   *
   * @return <code>null</code> if <code>theta</code> doesn't generate a quadratic field
   */
  public static QuadraticField quadraticField(IExpr theta, EvalEngine engine) {
    IRational[] coefficients = minimalPolynomialCoefficients(theta, engine);
    if (coefficients == null || coefficients.length != 3) {
      return null;
    }
    // x^2 + b*x + c
    IRational b = coefficients[1];
    IRational c = coefficients[0];
    IExpr discriminant = b.multiply(b).subtract(c.multiply(F.C4));
    if (!discriminant.isRational()) {
      return null;
    }
    IRational rational = (IRational) discriminant;
    // clear the denominator with a square factor, which doesn't change the field
    IInteger scaled = rational.numerator().multiply(rational.denominator());
    IInteger d = squarefreeKernel(scaled);
    if (d == null || d.isZero() || d.isOne()) {
      return null;
    }
    IInteger fundamental = d.mod(F.C4).isOne() ? d : d.multiply(F.C4);
    return new QuadraticField(d, fundamental);
  }

  /**
   * The squarefree kernel of an integer, i.e. the product of the primes which occur with an odd
   * exponent, carrying the sign of the input.
   *
   * @return <code>null</code> if the number couldn't be factored
   */
  public static IInteger squarefreeKernel(IInteger n) {
    if (n.isZero()) {
      return F.C0;
    }
    boolean negative = n.isNegative();
    IInteger value = negative ? n.negate() : n;
    IInteger result = F.C1;
    IAST factors = value.factorInteger();
    for (int i = 1; i < factors.size(); i++) {
      IExpr entry = factors.get(i);
      if (!entry.isList2() || !entry.first().isInteger() || !entry.second().isInteger()) {
        return null;
      }
      IInteger prime = (IInteger) entry.first();
      int exponent = entry.second().toIntDefault();
      if (exponent < 0) {
        return null;
      }
      if (exponent % 2 == 1) {
        result = result.multiply(prime);
      }
    }
    return negative ? result.negate() : result;
  }

  /**
   * The number of distinct real roots of a squarefree univariate polynomial, computed exactly with
   * a Sturm sequence.
   *
   * @param coefficients the coefficients of a monic polynomial in ascending order of the exponent
   * @param engine the evaluation engine
   * @return the number of real roots, or <code>-1</code> if the Sturm sequence couldn't be built
   */
  public static int realRootCount(IRational[] coefficients, EvalEngine engine) {
    final int degree = coefficients.length - 1;
    if (degree < 1) {
      return 0;
    }
    if (degree == 1) {
      return 1;
    }
    ISymbol x = F.Dummy("x");
    IExpr polynomial = polynomial(coefficients, x);
    IExpr derivative = engine.evaluate(F.D(polynomial, x));

    // the signs of the leading coefficients and the degrees are all that is needed to evaluate the
    // Sturm sequence at plus and minus infinity
    int signChangesAtPlusInfinity = 0;
    int signChangesAtMinusInfinity = 0;
    int previousPlus = 0;
    int previousMinus = 0;

    IExpr current = polynomial;
    IExpr next = derivative;
    for (int step = 0; step <= degree + 1; step++) {
      int[] limits = limitSigns(current, x, engine);
      if (limits == null) {
        return -1;
      }
      if (limits[0] != 0) {
        if (previousMinus != 0 && previousMinus != limits[0]) {
          signChangesAtMinusInfinity++;
        }
        previousMinus = limits[0];
      }
      if (limits[1] != 0) {
        if (previousPlus != 0 && previousPlus != limits[1]) {
          signChangesAtPlusInfinity++;
        }
        previousPlus = limits[1];
      }
      if (next.isZero()) {
        break;
      }
      IExpr remainder = engine.evaluate(F.PolynomialRemainder(current, next, x));
      current = next;
      next = engine.evaluate(F.Negate(remainder));
      if (next.isZero()) {
        // the last non-zero element of the sequence has already been processed above in the next
        // iteration, so handle it here
        int[] last = limitSigns(current, x, engine);
        if (last == null) {
          return -1;
        }
        if (last[0] != 0 && previousMinus != 0 && previousMinus != last[0]) {
          signChangesAtMinusInfinity++;
        }
        if (last[1] != 0 && previousPlus != 0 && previousPlus != last[1]) {
          signChangesAtPlusInfinity++;
        }
        break;
      }
    }
    return signChangesAtMinusInfinity - signChangesAtPlusInfinity;
  }

  /**
   * The signs of a polynomial at minus infinity and at plus infinity.
   *
   * @return an array <code>{signAtMinusInfinity, signAtPlusInfinity}</code>, or <code>null</code>
   *         if the leading coefficient isn't a non-zero rational number
   */
  private static int[] limitSigns(IExpr polynomial, ISymbol x, EvalEngine engine) {
    if (polynomial.isZero()) {
      return new int[] {0, 0};
    }
    IExpr degree = engine.evaluate(F.Exponent(polynomial, x));
    int n = degree.toIntDefault();
    if (n < 0) {
      return null;
    }
    IExpr leading = engine.evaluate(F.Coefficient(polynomial, x, F.ZZ(n)));
    if (!leading.isRational()) {
      return null;
    }
    int sign = ((IRational) leading).complexSign();
    if (sign == 0) {
      return null;
    }
    return new int[] {n % 2 == 0 ? sign : -sign, sign};
  }

  /** Build the polynomial with the given coefficients in ascending order of the exponent. */
  public static IExpr polynomial(IRational[] coefficients, ISymbol x) {
    IASTAppendable result = F.PlusAlloc(coefficients.length);
    for (int i = 0; i < coefficients.length; i++) {
      if (!coefficients[i].isZero()) {
        result.append(i == 0 ? coefficients[i] : F.Times(coefficients[i], F.Power(x, F.ZZ(i))));
      }
    }
    return result.oneIdentity0();
  }

  /**
   * The fundamental unit of a real quadratic field, i.e. the smallest unit greater than
   * <code>1</code>.
   *
   * <p>
   * It is <code>(x + y*Sqrt(D))/2</code> for the least positive solution of
   * <code>x^2 - D*y^2 = &plusmn;4</code>, which is found from the continued fraction expansion of
   * <code>(1+Sqrt(D))/2</code> respectively <code>Sqrt(d)</code>.
   *
   * @return {@link F#NIL} for an imaginary quadratic field
   */
  public static IExpr fundamentalUnit(QuadraticField field, EvalEngine engine) {
    if (!field.isReal()) {
      return F.NIL;
    }
    XYPair solution = leastPellFourSolution(field);
    if (solution == null) {
      return F.NIL;
    }
    // (x + y*Sqrt(D))/2 rewritten over Sqrt(d)
    IInteger x = F.ZZ(solution.x);
    IInteger y = F.ZZ(solution.y);
    IExpr root = F.Sqrt(field.d);
    if (field.discriminant.equals(field.d)) {
      return engine.evaluate(F.Times(F.C1D2, F.Plus(x, F.Times(y, root))));
    }
    // Sqrt(D) == 2*Sqrt(d), so (x + y*2*Sqrt(d))/2 == x/2 + y*Sqrt(d)
    return engine.evaluate(F.Plus(F.Times(F.C1D2, x), F.Times(y, root)));
  }

  /**
   * The least positive solution of <code>x^2 - D*y^2 = &plusmn;4</code> for the discriminant
   * <code>D</code> of a real quadratic field.
   */
  private static XYPair leastPellFourSolution(QuadraticField field) {
    try {
      BigInteger discriminant = field.discriminant.toBigNumerator();
      if (field.discriminant.equals(field.d)) {
        // D = d = 1 mod 4: the convergents of (1 + Sqrt(D))/2
        ContinuedFraction cf =
            ContinuedFraction.ofExpression(BigInteger.ONE, discriminant, BigInteger.TWO);
        XYPair convergent = cf.convergent(cf.getPeriod() - 1);
        BigInteger x = BigInteger.TWO.multiply(convergent.x).subtract(convergent.y);
        return new XYPair(x, convergent.y);
      }
      // D = 4*d: the convergents of Sqrt(d) give p^2 - d*q^2 = +-1, so x = 2*p and y = q
      ContinuedFraction cf = ContinuedFraction.ofRoot(field.d.toBigNumerator());
      XYPair convergent = cf.convergent(cf.getPeriod() - 1);
      return new XYPair(BigInteger.TWO.multiply(convergent.x), convergent.y);
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /**
   * The norm of the fundamental unit, which is <code>1</code> or <code>-1</code>.
   *
   * @return <code>0</code> if it couldn't be determined
   */
  public static int fundamentalUnitNorm(QuadraticField field) {
    XYPair solution = leastPellFourSolution(field);
    if (solution == null) {
      return 0;
    }
    // (x^2 - D*y^2)/4
    BigInteger discriminant = field.discriminant.toBigNumerator();
    BigInteger value = solution.x.multiply(solution.x)
        .subtract(discriminant.multiply(solution.y).multiply(solution.y));
    return value.signum();
  }

  /**
   * The class number of a quadratic field, computed by counting the classes of primitive reduced
   * binary quadratic forms of the field discriminant.
   *
   * @return {@link F#NIL} if it couldn't be determined
   */
  public static IExpr classNumber(QuadraticField field) {
    BigInteger discriminant = field.discriminant.toBigNumerator();
    if (discriminant.signum() < 0) {
      return F.ZZ(imaginaryClassNumber(discriminant));
    }
    long narrow = narrowRealClassNumber(discriminant);
    if (narrow <= 0) {
      return F.NIL;
    }
    int unitNorm = fundamentalUnitNorm(field);
    if (unitNorm == 0) {
      return F.NIL;
    }
    // the narrow class number equals the class number exactly when a unit of norm -1 exists
    return F.ZZ(unitNorm < 0 ? narrow : narrow / 2);
  }

  /**
   * Count the reduced positive definite primitive forms <code>(a,b,c)</code> with
   * <code>b^2 - 4*a*c == D</code>, <code>|b| &lt;= a &lt;= c</code> and <code>b &gt;= 0</code> when
   * <code>|b| == a</code> or <code>a == c</code>.
   */
  private static long imaginaryClassNumber(BigInteger discriminant) {
    long count = 0;
    BigInteger absDiscriminant = discriminant.negate();
    // b^2 <= |D|/3
    BigInteger limit = absDiscriminant.divide(BigInteger.valueOf(3)).sqrt();
    int parity = discriminant.testBit(0) ? 1 : 0;
    for (BigInteger b = BigInteger.valueOf(parity); b.compareTo(limit) <= 0; b =
        b.add(BigInteger.TWO)) {
      BigInteger product = b.multiply(b).subtract(discriminant).divide(BigInteger.valueOf(4));
      if (product.signum() <= 0) {
        continue;
      }
      // enumerate the divisors a of a*c == product with b <= a <= sqrt(product)
      for (BigInteger a = b.max(BigInteger.ONE); a.multiply(a).compareTo(product) <= 0; a =
          a.add(BigInteger.ONE)) {
        if (!product.mod(a).equals(BigInteger.ZERO)) {
          continue;
        }
        BigInteger c = product.divide(a);
        if (!a.gcd(b).gcd(c).equals(BigInteger.ONE)) {
          continue;
        }
        count++;
        // (a,-b,c) is a second class unless the form is ambiguous
        if (b.signum() != 0 && !b.equals(a) && !a.equals(c)) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * The narrow class number of a real quadratic field: the number of cycles of primitive reduced
   * indefinite forms of discriminant <code>D</code> under the reduction operator rho.
   */
  private static long narrowRealClassNumber(BigInteger discriminant) {
    long d;
    try {
      d = discriminant.longValueExact();
    } catch (ArithmeticException aex) {
      return -1;
    }
    // the largest integer whose square is below the discriminant; the discriminant of a real
    // quadratic field is never a perfect square, so sqrt(d) is irrational
    final long root = BigInteger.valueOf(d).sqrt().longValueExact();

    java.util.List<long[]> forms = new java.util.ArrayList<>();
    for (long b = (d % 2 == 0) ? 2 : 1; b * b < d; b += 2) {
      long product = (b * b - d) / 4; // == a*c, always negative
      long absProduct = -product;
      for (long absA = 1; absA <= absProduct; absA++) {
        if (absProduct % absA != 0) {
          continue;
        }
        long absC = absProduct / absA;
        if (gcd(gcd(absA, b), absC) != 1) {
          continue;
        }
        for (int sign = -1; sign <= 1; sign += 2) {
          long a = sign * absA;
          long c = -sign * absC; // keeps a*c == product
          if (isReducedIndefinite(a, b, d)) {
            forms.add(new long[] {a, b, c});
          }
        }
      }
    }
    if (forms.isEmpty()) {
      return -1;
    }

    // rho permutes the reduced forms, so the number of classes is the number of its orbits
    java.util.Set<java.util.List<Long>> seen = new java.util.HashSet<>();
    long cycles = 0;
    for (long[] form : forms) {
      if (seen.contains(key(form))) {
        continue;
      }
      cycles++;
      long[] current = form;
      for (int guard = 0; guard <= forms.size(); guard++) {
        if (!seen.add(key(current))) {
          break;
        }
        current = rho(current, d, root);
        if (current == null) {
          return -1;
        }
      }
    }
    return cycles;
  }

  private static java.util.List<Long> key(long[] form) {
    return java.util.Arrays.asList(form[0], form[1], form[2]);
  }

  /**
   * Test whether an indefinite form is reduced, i.e. whether
   * <code>|sqrt(D) - 2|a|| &lt; b &lt; sqrt(D)</code>. Because <code>sqrt(D)</code> is irrational
   * the comparisons can be squared without worrying about equality.
   */
  private static boolean isReducedIndefinite(long a, long b, long d) {
    if (b <= 0 || b * b >= d) {
      return false;
    }
    long twoAbsA = 2 * Math.abs(a);
    // 2|a| - b < sqrt(D)
    if (twoAbsA > b && (twoAbsA - b) * (twoAbsA - b) > d) {
      return false;
    }
    // sqrt(D) < 2|a| + b
    return (twoAbsA + b) * (twoAbsA + b) > d;
  }

  /**
   * The reduction operator rho on indefinite forms: <code>(a,b,c) -&gt; (c, r, (r^2-D)/(4c))</code>
   * where <code>r</code> is congruent to <code>-b</code> modulo <code>2c</code> and lies in the
   * window which makes the image reduced.
   */
  private static long[] rho(long[] form, long d, long root) {
    long b = form[1];
    long c = form[2];
    if (c == 0) {
      return null;
    }
    long absC = Math.abs(c);
    long modulus = 2 * absC;
    // the window is (upper - modulus, upper]
    long upper = absC > root ? absC : root;
    long r = upper - Math.floorMod(upper + b, modulus);
    long numerator = r * r - d;
    if (numerator % (4 * c) != 0) {
      return null;
    }
    return new long[] {c, r, numerator / (4 * c)};
  }

  private static long gcd(long a, long b) {
    while (b != 0) {
      long t = a % b;
      a = b;
      b = t;
    }
    return Math.abs(a);
  }
}
