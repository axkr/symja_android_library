package org.matheclipse.core.polynomials;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.numbertheory.AlgebraicNumberField;
import edu.jas.arith.BigRational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorSubres;

/**
 * The GCD of two polynomials whose coefficients are algebraic numbers written with radicals, such
 * as <code>3-2*Sqrt(2)</code> or <code>I*Sqrt(6)</code>, computed over the number field those
 * radicals generate.
 *
 * <p>
 * Over the ring of unevaluated <code>IExpr</code> coefficients the same GCD has no normal form for
 * its coefficients: every product is a full evaluation, every quotient a nested fraction such as
 * <code>1/((-1-Sqrt(6))*(1/5-Sqrt(6)/5))</code>, and both grow at every pseudo-remainder step. In a
 * number field each coefficient is a vector of rationals, so the GCD is ordinary exact arithmetic
 * and the quotients come back rationalised.
 *
 * <p>
 * A coefficient is walked through <code>Plus</code>, <code>Times</code> and integer
 * <code>Power</code>; every other leaf must be a rational, a Gaussian rational, a radical of an
 * exact number or an <code>AlgebraicNumber</code> object (an <i>atom</i>). The field is the one
 * {@link AlgebraicNumberField#commonField(IAST, EvalEngine)} finds for the atoms. Quotient
 * coefficients are written back in a basis of products of the atoms, so a result over
 * <code>Q(Sqrt(2),Sqrt(3))</code> uses <code>1, Sqrt(2), Sqrt(3), Sqrt(6)</code> rather than
 * powers of a primitive element.
 */
public final class AlgebraicCoefficientGCD {

  /**
   * Upper bound for the product of the atoms' degrees. Finding a common field is a factorisation
   * over the field built so far, and its cost rises steeply with the degree: <code>I, Sqrt(2),
   * Sqrt(3)</code> (degree 8) takes under a second, <code>I, 2^(1/3), Sqrt(3)</code> (degree 12)
   * did not finish in ten minutes.
   */
  private static final int MAX_FIELD_DEGREE = 8;

  /** A coefficient raised to a larger integer power is not converted. */
  private static final int MAX_EXPONENT = 1024;

  private static final int CACHE_SIZE = 64;

  /** The two cancelled polynomials, or {@link #COPRIME}. */
  public static final class Quotients {
    public final GenPolynomial<IExpr> numerator;
    public final GenPolynomial<IExpr> denominator;

    private Quotients(GenPolynomial<IExpr> numerator, GenPolynomial<IExpr> denominator) {
      this.numerator = numerator;
      this.denominator = denominator;
    }
  }

  /** The GCD is <code>1</code>: there is nothing to cancel. */
  public static final Quotients COPRIME = new Quotients(null, null);

  /** A number field together with the atoms it was built for and the basis to write back in. */
  private static final class CoefficientField {
    final AlgebraicNumberField field;

    final Map<IExpr, AlgebraicNumber<BigRational>> atoms;

    /** The basis monomials, evaluated - e.g. <code>Sqrt(6)</code>, not <code>Sqrt(2)*Sqrt(3)</code>. */
    final IExpr[] basis;

    /** Maps power-basis coordinates of an element to its coordinates in {@link #basis}. */
    final BigRational[][] toBasis;

    /** Primes <code>p</code> and simple roots <code>r</code> of the minimal polynomial mod p. */
    private long[][] primeRoots;

    CoefficientField(AlgebraicNumberField field, Map<IExpr, AlgebraicNumber<BigRational>> atoms,
        IExpr[] basis, BigRational[][] toBasis) {
      this.field = field;
      this.atoms = atoms;
      this.basis = basis;
      this.toBasis = toBasis;
    }

    /**
     * Proves that <code>q1</code> and <code>q2</code> are coprime from a modular image, or returns
     * <code>false</code> if the image does not decide it.
     *
     * <p>
     * The image sends the generator to a simple root <code>r</code> of its minimal polynomial
     * modulo a prime <code>p</code>. Localised at that prime the coefficient ring is a discrete
     * valuation ring, so a GCD <code>h</code> of <code>q1</code> and <code>q2</code> can be scaled
     * to be primitive there and its image divides both images. Each variable is restricted to a
     * random line <code>x_i = a_i + t*b_i</code>. If both images keep their total degree, the
     * leading form of <code>h</code> - a factor of theirs - does not vanish on the line either, so
     * the image of <code>h</code> has the total degree of <code>h</code>. A constant GCD of the
     * images therefore proves that <code>h</code> is constant.
     */
    @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
        justification = "seeded for a deterministic answer; drawn from in a loop")
    boolean isCoprime(GenPolynomial<AlgebraicNumber<BigRational>> q1,
        GenPolynomial<AlgebraicNumber<BigRational>> q2) {
      long[][] roots = primeRoots();
      java.util.Random random = new java.util.Random(q1.length() * 31L + q2.length());
      for (long[] pr : roots) {
        long p = pr[0];
        int nvar = q1.ring.nvar;
        long[] a = new long[nvar];
        long[] b = new long[nvar];
        for (int i = 0; i < nvar; i++) {
          a[i] = 1 + (long) (random.nextDouble() * (p - 1));
          b[i] = 1 + (long) (random.nextDouble() * (p - 1));
        }
        long[] f = ModularImage.onLine(q1, pr, a, b, field.degree);
        long[] g = ModularImage.onLine(q2, pr, a, b, field.degree);
        if (f == null || g == null || ModularImage.degree(f) != q1.totalDegree()
            || ModularImage.degree(g) != q2.totalDegree()) {
          // a denominator divisible by p, or an unlucky line: try the next prime
          continue;
        }
        return ModularImage.degree(ModularImage.gcd(f, g, p)) == 0;
      }
      return false;
    }

    private synchronized long[][] primeRoots() {
      if (primeRoots == null) {
        primeRoots = ModularImage.primeRoots(field.minimalPolynomial, 3);
      }
      return primeRoots;
    }
  }

  /** Marks an atom set for which no usable field was found, so it is not searched again. */
  private static final CoefficientField NO_FIELD = new CoefficientField(null, null, null, null);

  /** Keyed by the sorted list of atoms; finding a common field is the expensive step. */
  private static final Map<IAST, CoefficientField> CACHE =
      Collections.synchronizedMap(new LinkedHashMap<IAST, CoefficientField>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<IAST, CoefficientField> eldest) {
          return size() > CACHE_SIZE;
        }
      });

  private AlgebraicCoefficientGCD() {}

  /**
   * Cancel the GCD of <code>p1</code> and <code>p2</code> over the number field generated by the
   * radicals in their coefficients.
   *
   * @return <code>null</code> if a coefficient is not of the supported form or no field was found
   *         (the caller should fall back to another method), {@link #COPRIME} if the GCD is
   *         <code>1</code>, otherwise <code>p1/gcd</code> and <code>p2/gcd</code> over the ring of
   *         <code>p1</code>
   */
  public static Quotients cancel(GenPolynomial<IExpr> p1, GenPolynomial<IExpr> p2,
      EvalEngine engine) {
    Set<IExpr> atomSet = new TreeSet<IExpr>();
    if (!collectAtoms(p1, atomSet) || !collectAtoms(p2, atomSet)) {
      return null;
    }
    if (atomSet.isEmpty() || degreeBound(atomSet) > MAX_FIELD_DEGREE) {
      return null;
    }
    CoefficientField coefficients = coefficientField(atomSet, engine);
    if (coefficients == null) {
      return null;
    }
    GenPolynomialRing<AlgebraicNumber<BigRational>> ring =
        new GenPolynomialRing<AlgebraicNumber<BigRational>>(coefficients.field.ring,
            p1.ring.nvar, p1.ring.tord);
    GenPolynomial<AlgebraicNumber<BigRational>> q1 = toField(p1, ring, coefficients);
    GenPolynomial<AlgebraicNumber<BigRational>> q2 = toField(p2, ring, coefficients);
    if (q1 == null || q2 == null || q1.isZERO() || q2.isZERO()) {
      return null;
    }
    // Most calls end in "coprime", and proving that exactly can take seconds even in the field
    // (52x41 terms of total degree 10 in five variables: 8 s). A modular image proves it in
    // milliseconds; only when it fails is the GCD computed.
    boolean coprime = q1.isConstant() || q2.isConstant() || coefficients.isCoprime(q1, q2);
    if (!coprime) {
      // GCDFactory would pick the Euclidean GreatestCommonDivisorSimple for a field, whose
      // multivariate remainders swell; the subresultant sequence keeps them in check.
      GreatestCommonDivisor<AlgebraicNumber<BigRational>> gcdEngine =
          new GreatestCommonDivisorSubres<AlgebraicNumber<BigRational>>();
      GenPolynomial<AlgebraicNumber<BigRational>> gcd = gcdEngine.gcd(q1, q2);
      coprime = gcd.isConstant();
      if (!coprime) {
        q1 = q1.divide(gcd);
        q2 = q2.divide(gcd);
        // The quotients are determined only up to a unit of the field, and the GCD's scaling can
        // leave one on both sides: (3+2*Sqrt(2))*(1+Sqrt(2)+x)^2 / ((3+2*Sqrt(2))*(2+x)).
        AlgebraicNumber<BigRational> unit = simplestUnit(q1, q2, coefficients);
        if (!unit.isONE()) {
          AlgebraicNumber<BigRational> inverse = unit.inverse();
          q1 = q1.multiply(inverse);
          q2 = q2.multiply(inverse);
        }
      }
    }
    // Over a field every non-zero constant is a unit, so the GCD does not see a common rational
    // factor such as the 2 in (2*Sqrt(3)+2*x)/(4*y). Cancel it separately, as the IExpr GCD does
    // through the rational content of its coefficients - and, like it, only when every coefficient
    // is a single term: the IExpr content of a sum such as 4-4*Sqrt(2) is 1. Reading 4 there turns
    // coprime fractions into expanded rewrites, which doubled the size of an exact LinearSolve.
    Map<ExpVector, BigRational[]> c1 = basisCoordinates(q1, coefficients);
    Map<ExpVector, BigRational[]> c2 = basisCoordinates(q2, coefficients);
    // Likewise a basis element such as Sqrt(3) that every coefficient on both sides is a rational
    // multiple of: (Sqrt(3)+Sqrt(3)*x^2)/(2*Sqrt(3)*x) is (1+x^2)/(2*x).
    int common = commonBasisElement(c1, c2);
    if (common > 0) {
      moveToOne(c1, common);
      moveToOne(c2, common);
    }
    BigRational content = rationalContent(c1.values(), null);
    if (content == null || !content.isONE()) {
      content = rationalContent(c2.values(), content);
    }
    boolean contentIsOne = content == null || content.isONE();
    if (coprime && contentIsOne && common <= 0) {
      return COPRIME;
    }
    BigRational scale = contentIsOne ? BigRational.ONE : content.inverse();
    GenPolynomial<IExpr> numerator = fromBasis(c1, scale, p1.ring, coefficients, engine);
    GenPolynomial<IExpr> denominator = fromBasis(c2, scale, p1.ring, coefficients, engine);
    return new Quotients(numerator, denominator);
  }

  /**
   * The GCD of <code>p1</code> and <code>p2</code> over the number field generated by the radicals,
   * <code>AlgebraicNumber</code> and <code>Root</code> objects in their coefficients, as
   * <code>PolynomialGCD(p1, p2, Extension-&gt;Automatic)</code> gives it: the coefficients are
   * written in the basis of products of these generators, scaled to coprime integers, with the
   * first coordinate of the lowest coefficient positive -
   * <code>256+625*x+309*c+21716*c^2+45904*c^3+183616*c^4</code>, <code>Sqrt(2)-x</code> (WMA).
   *
   * @return <code>null</code> if a coefficient is not of the supported form or no field was found
   */
  public static GenPolynomial<IExpr> gcd(GenPolynomial<IExpr> p1, GenPolynomial<IExpr> p2,
      EvalEngine engine) {
    Set<IExpr> atomSet = new TreeSet<IExpr>();
    if (!collectAtoms(p1, atomSet, true) || !collectAtoms(p2, atomSet, true)) {
      return null;
    }
    if (atomSet.isEmpty() || degreeBound(atomSet) > MAX_FIELD_DEGREE) {
      return null;
    }
    CoefficientField coefficients = coefficientField(atomSet, engine);
    if (coefficients == null) {
      return null;
    }
    GenPolynomialRing<AlgebraicNumber<BigRational>> ring =
        new GenPolynomialRing<AlgebraicNumber<BigRational>>(coefficients.field.ring,
            p1.ring.nvar, p1.ring.tord);
    GenPolynomial<AlgebraicNumber<BigRational>> q1 = toField(p1, ring, coefficients);
    GenPolynomial<AlgebraicNumber<BigRational>> q2 = toField(p2, ring, coefficients);
    if (q1 == null || q2 == null) {
      return null;
    }
    GenPolynomial<AlgebraicNumber<BigRational>> gcd =
        new GreatestCommonDivisorSubres<AlgebraicNumber<BigRational>>().gcd(q1, q2);
    if (gcd.isZERO()) {
      return new GenPolynomial<IExpr>(p1.ring);
    }
    gcd = gcd.monic();
    Map<ExpVector, BigRational[]> coordinates = basisCoordinates(gcd, coefficients);
    // coprime integer coordinates: times the LCM of the denominators, over the GCD of the results
    java.math.BigInteger lcm = java.math.BigInteger.ONE;
    for (BigRational[] vector : coordinates.values()) {
      for (BigRational a : vector) {
        if (!a.isZERO()) {
          lcm = lcm.divide(lcm.gcd(a.den)).multiply(a.den);
        }
      }
    }
    java.math.BigInteger content = java.math.BigInteger.ZERO;
    for (BigRational[] vector : coordinates.values()) {
      for (BigRational a : vector) {
        if (!a.isZERO()) {
          content = content.gcd(a.num.multiply(lcm.divide(a.den)));
        }
      }
    }
    if (content.signum() == 0) {
      content = java.math.BigInteger.ONE;
    }
    BigRational scale = BigRational.reduction(lcm, content);
    // the sign: the first non-zero coordinate of the lowest term positive
    BigRational[] lowest = null;
    for (BigRational[] vector : coordinates.values()) {
      lowest = vector;
    }
    if (lowest != null) {
      for (BigRational a : lowest) {
        if (!a.isZERO()) {
          if (a.signum() < 0) {
            scale = scale.negate();
          }
          break;
        }
      }
    }
    return fromBasis(coordinates, scale, p1.ring, coefficients, engine);
  }

  /** Coefficients tried as the unit to divide out, at most. */
  private static final int MAX_UNIT_CANDIDATES = 16;

  /**
   * The coefficient of <code>q1</code> or <code>q2</code> whose division leaves the fewest terms in
   * the basis of atom products, then the fewest fractions, the smallest numerators and the fewest
   * negative signs - <code>(Sqrt(3)+x)</code> rather than <code>(3+Sqrt(3)*x)</code> or
   * <code>(-Sqrt(3)-x)</code>. <code>1</code> if none is simpler than now.
   */
  private static AlgebraicNumber<BigRational> simplestUnit(
      GenPolynomial<AlgebraicNumber<BigRational>> q1,
      GenPolynomial<AlgebraicNumber<BigRational>> q2, CoefficientField coefficients) {
    AlgebraicNumber<BigRational> best = coefficients.field.ring.getONE();
    long[] bestWeight = weight(q1, q2, coefficients);
    Set<AlgebraicNumber<BigRational>> tried = new HashSet<AlgebraicNumber<BigRational>>();
    tried.add(best);
    for (GenPolynomial<AlgebraicNumber<BigRational>> q : java.util.Arrays.asList(q2, q1)) {
      for (AlgebraicNumber<BigRational> c : q.getMap().values()) {
        if (tried.size() > MAX_UNIT_CANDIDATES) {
          return best;
        }
        if (!tried.add(c)) {
          continue;
        }
        AlgebraicNumber<BigRational> inverse = c.inverse();
        long[] w = weight(q1.multiply(inverse), q2.multiply(inverse), coefficients);
        if (isLighter(w, bestWeight)) {
          bestWeight = w;
          best = c;
        }
      }
    }
    return best;
  }

  private static boolean isLighter(long[] w, long[] than) {
    for (int i = 0; i < w.length; i++) {
      if (w[i] != than[i]) {
        return w[i] < than[i];
      }
    }
    return false;
  }

  /**
   * For all coefficients of both polynomials in the basis of atom products: the number of non-zero
   * coordinates, how many of them are not integers, the total bit length of their numerators and
   * how many of them are negative.
   */
  private static long[] weight(GenPolynomial<AlgebraicNumber<BigRational>> q1,
      GenPolynomial<AlgebraicNumber<BigRational>> q2, CoefficientField coefficients) {
    long[] weight = new long[4];
    for (GenPolynomial<AlgebraicNumber<BigRational>> q : java.util.Arrays.asList(q1, q2)) {
      for (BigRational[] vector : basisCoordinates(q, coefficients).values()) {
        for (BigRational a : vector) {
          if (!a.isZERO()) {
            weight[0]++;
            if (!a.den.equals(java.math.BigInteger.ONE)) {
              weight[1]++;
            }
            weight[2] += a.num.abs().bitLength();
            if (a.signum() < 0) {
              weight[3]++;
            }
          }
        }
      }
    }
    return weight;
  }

  /**
   * The index of the one basis element that every coordinate vector of both polynomials is
   * supported on, or <code>-1</code>.
   */
  private static int commonBasisElement(Map<ExpVector, BigRational[]> c1,
      Map<ExpVector, BigRational[]> c2) {
    int common = -1;
    for (Map<ExpVector, BigRational[]> c : java.util.Arrays.asList(c1, c2)) {
      for (BigRational[] vector : c.values()) {
        for (int i = 0; i < vector.length; i++) {
          if (!vector[i].isZERO()) {
            if (common >= 0 && common != i) {
              return -1;
            }
            common = i;
          }
        }
      }
    }
    return common;
  }

  /**
   * Divide every coefficient by basis element <code>k</code>, which is its only support. Element 0
   * is <code>1</code>: the basis search starts from the empty product.
   */
  private static void moveToOne(Map<ExpVector, BigRational[]> coordinates, int k) {
    for (BigRational[] vector : coordinates.values()) {
      vector[0] = vector[k];
      vector[k] = BigRational.ZERO;
    }
  }

  /**
   * The positive rational GCD of <code>content</code> and every non-zero coordinate: the GCD of the
   * numerators over the LCM of the denominators. <code>1</code> as soon as one coefficient has more
   * than one non-zero coordinate.
   */
  private static BigRational rationalContent(Iterable<BigRational[]> coordinates,
      BigRational content) {
    java.math.BigInteger num = content == null ? java.math.BigInteger.ZERO : content.num;
    java.math.BigInteger den = content == null ? java.math.BigInteger.ONE : content.den;
    for (BigRational[] vector : coordinates) {
      int terms = 0;
      for (BigRational a : vector) {
        if (!a.isZERO()) {
          if (++terms > 1) {
            return BigRational.ONE;
          }
          num = num.gcd(a.num);
          den = den.divide(den.gcd(a.den)).multiply(a.den);
        }
      }
    }
    return num.signum() == 0 ? null : BigRational.reduction(num, den);
  }

  private static boolean collectAtoms(GenPolynomial<IExpr> p, Set<IExpr> atoms) {
    return collectAtoms(p, atoms, false);
  }

  /** @param allowRoot whether a <code>Root(f, k)</code> object is an atom as well */
  private static boolean collectAtoms(GenPolynomial<IExpr> p, Set<IExpr> atoms,
      boolean allowRoot) {
    for (IExpr c : p.getMap().values()) {
      if (!collectAtoms(c, atoms, allowRoot)) {
        return false;
      }
    }
    return true;
  }

  private static boolean collectAtoms(IExpr c, Set<IExpr> atoms, boolean allowRoot) {
    if (c.isRational()) {
      return true;
    }
    if (allowRoot && c.isAST(S.Root) && c.argSize() >= 2) {
      atoms.add(c);
      return true;
    }
    if (c instanceof IComplex) {
      atoms.add(F.CI);
      return true;
    }
    if (c.isPlus() || c.isTimes()) {
      for (int i = 1; i < c.size(); i++) {
        if (!collectAtoms(c.getAt(i), atoms, allowRoot)) {
          return false;
        }
      }
      return true;
    }
    if (c.isPower()) {
      IExpr base = c.base();
      IExpr exponent = c.exponent();
      if (exponent.isInteger()) {
        return collectAtoms(base, atoms, allowRoot);
      }
      if (exponent.isFraction() && (base.isRational() || base instanceof IComplex)) {
        atoms.add(radicalAtom(base, (IRational) exponent));
        return true;
      }
      return false;
    }
    if (AlgebraicNumberField.isObject(c)) {
      atoms.add(c);
      return true;
    }
    return false;
  }

  /**
   * The atom of the radical <code>base^exponent</code>: <code>base^(1/q)</code> for an exponent
   * <code>p/q</code>, so that <code>Sqrt(2)</code>, <code>1/Sqrt(2)</code> and <code>2^(3/2)</code>
   * share one atom and do not enlarge the field search.
   */
  private static IExpr radicalAtom(IExpr base, IRational exponent) {
    return F.Power(base, F.fraction(F.C1, exponent.denominator()));
  }

  /** The product of an upper bound of each atom's degree over the rationals. */
  private static long degreeBound(Set<IExpr> atoms) {
    long bound = 1;
    for (IExpr atom : atoms) {
      long degree;
      if (atom == F.CI) {
        degree = 2;
      } else if (atom.isPower()) {
        degree = ((IRational) atom.exponent()).denominator().toLongDefault();
        if (degree <= 0) {
          return Long.MAX_VALUE;
        }
        if (atom.base() instanceof IComplex) {
          degree *= 2;
        }
      } else if (atom.isAST(S.Root)) {
        IRational[] coefficients = org.matheclipse.core.numbertheory.NumberFieldUtils
            .minimalPolynomialCoefficients(atom, EvalEngine.get());
        if (coefficients == null) {
          return Long.MAX_VALUE;
        }
        degree = coefficients.length - 1;
      } else {
        // an AlgebraicNumber object: the length of its coefficient list is its field's degree
        degree = atom.second().argSize();
      }
      bound *= degree;
      if (bound > MAX_FIELD_DEGREE) {
        return bound;
      }
    }
    return bound;
  }

  private static CoefficientField coefficientField(Set<IExpr> atomSet, EvalEngine engine) {
    IAST key = F.ListAlloc(atomSet);
    CoefficientField cached = CACHE.get(key);
    if (cached == null) {
      cached = createField(key, engine);
      CACHE.put(key, cached);
    }
    return cached == NO_FIELD ? null : cached;
  }

  private static CoefficientField createField(IAST atomList, EvalEngine engine) {
    AlgebraicNumberField field = AlgebraicNumberField.commonField(atomList, engine);
    if (field == null) {
      return NO_FIELD;
    }
    int n = field.degree;
    Map<IExpr, AlgebraicNumber<BigRational>> atoms =
        new HashMap<IExpr, AlgebraicNumber<BigRational>>();
    for (int i = 1; i < atomList.size(); i++) {
      AlgebraicNumber<BigRational> element = field.express(atomList.get(i), engine);
      if (element == null) {
        return NO_FIELD;
      }
      atoms.put(atomList.get(i), element);
    }

    // Breadth first over products of the atoms, keeping every product that is independent of the
    // ones kept so far. Only a kept product is extended: a dependent one lies in their span, and so
    // does its product with an atom. The span of the kept products is therefore closed under
    // multiplication by every atom, and so it is the whole field.
    int m = atomList.argSize();
    List<IExpr> basis = new ArrayList<IExpr>();
    List<BigRational[]> columns = new ArrayList<BigRational[]>();
    List<BigRational[]> echelon = new ArrayList<BigRational[]>();
    List<Integer> pivots = new ArrayList<Integer>();
    ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
    Set<String> seen = new HashSet<String>();
    queue.add(new int[m]);
    while (!queue.isEmpty() && basis.size() < n) {
      int[] exponents = queue.poll();
      if (!seen.add(java.util.Arrays.toString(exponents))) {
        continue;
      }
      AlgebraicNumber<BigRational> element = field.ring.getONE();
      IASTAppendable monomial = F.TimesAlloc(m);
      for (int j = 0; j < m; j++) {
        if (exponents[j] > 0) {
          element = element.multiply(atoms.get(atomList.get(j + 1)).power(exponents[j]));
          monomial.append(F.Power(atomList.get(j + 1), F.ZZ(exponents[j])));
        }
      }
      BigRational[] vector = coordinates(element, n);
      if (!reduce(vector.clone(), echelon, pivots)) {
        continue;
      }
      basis.add(engine.evaluate(monomial.oneIdentity1()));
      columns.add(vector);
      for (int j = 0; j < m; j++) {
        int[] next = exponents.clone();
        next[j]++;
        queue.add(next);
      }
    }
    if (basis.size() != n) {
      return NO_FIELD;
    }
    BigRational[][] toBasis = inverse(columns, n);
    if (toBasis == null) {
      return NO_FIELD;
    }
    return new CoefficientField(field, atoms, basis.toArray(new IExpr[n]), toBasis);
  }

  /**
   * Reduce <code>vector</code> by the echelon rows and, if something is left, add it as a new row.
   *
   * @return <code>true</code> if <code>vector</code> was independent of the rows
   */
  private static boolean reduce(BigRational[] vector, List<BigRational[]> echelon,
      List<Integer> pivots) {
    for (int r = 0; r < echelon.size(); r++) {
      int p = pivots.get(r);
      if (!vector[p].isZERO()) {
        BigRational[] row = echelon.get(r);
        BigRational factor = vector[p].divide(row[p]);
        for (int k = 0; k < vector.length; k++) {
          vector[k] = vector[k].subtract(factor.multiply(row[k]));
        }
      }
    }
    for (int k = 0; k < vector.length; k++) {
      if (!vector[k].isZERO()) {
        echelon.add(vector);
        pivots.add(k);
        return true;
      }
    }
    return false;
  }

  /** The inverse of the matrix whose columns are <code>columns</code>, or <code>null</code>. */
  private static BigRational[][] inverse(List<BigRational[]> columns, int n) {
    BigRational[][] a = new BigRational[n][2 * n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        a[i][j] = columns.get(j)[i];
        a[i][n + j] = i == j ? BigRational.ONE : BigRational.ZERO;
      }
    }
    for (int col = 0; col < n; col++) {
      int pivot = col;
      while (pivot < n && a[pivot][col].isZERO()) {
        pivot++;
      }
      if (pivot == n) {
        return null;
      }
      BigRational[] swap = a[pivot];
      a[pivot] = a[col];
      a[col] = swap;
      BigRational scale = a[col][col].inverse();
      for (int k = 0; k < 2 * n; k++) {
        a[col][k] = a[col][k].multiply(scale);
      }
      for (int i = 0; i < n; i++) {
        if (i != col && !a[i][col].isZERO()) {
          BigRational factor = a[i][col];
          for (int k = 0; k < 2 * n; k++) {
            a[i][k] = a[i][k].subtract(factor.multiply(a[col][k]));
          }
        }
      }
    }
    BigRational[][] result = new BigRational[n][n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(a[i], n, result[i], 0, n);
    }
    return result;
  }

  private static BigRational[] coordinates(AlgebraicNumber<BigRational> element, int n) {
    BigRational[] result = new BigRational[n];
    GenPolynomial<BigRational> val = element.val;
    for (int i = 0; i < n; i++) {
      result[i] = val.coefficient(ExpVector.create(1, 0, i));
    }
    return result;
  }

  private static GenPolynomial<AlgebraicNumber<BigRational>> toField(GenPolynomial<IExpr> p,
      GenPolynomialRing<AlgebraicNumber<BigRational>> ring, CoefficientField coefficients) {
    GenPolynomial<AlgebraicNumber<BigRational>> result = ring.getZERO().copy();
    for (Map.Entry<ExpVector, IExpr> monomial : p.getMap().entrySet()) {
      AlgebraicNumber<BigRational> c = toField(monomial.getValue(), coefficients);
      if (c == null) {
        return null;
      }
      if (!c.isZERO()) {
        result.doPutToMap(monomial.getKey(), c);
      }
    }
    return result;
  }

  private static AlgebraicNumber<BigRational> toField(IExpr c, CoefficientField coefficients) {
    AlgebraicNumberField field = coefficients.field;
    if (c.isRational()) {
      return field.fromRational((IRational) c);
    }
    if (c instanceof IComplex) {
      IComplex z = (IComplex) c;
      AlgebraicNumber<BigRational> i = coefficients.atoms.get(F.CI);
      return field.fromRational(z.re()).sum(field.fromRational(z.im()).multiply(i));
    }
    if (c.isPlus()) {
      AlgebraicNumber<BigRational> sum = field.ring.getZERO();
      for (int k = 1; k < c.size(); k++) {
        AlgebraicNumber<BigRational> term = toField(c.getAt(k), coefficients);
        if (term == null) {
          return null;
        }
        sum = sum.sum(term);
      }
      return sum;
    }
    if (c.isTimes()) {
      AlgebraicNumber<BigRational> product = field.ring.getONE();
      for (int k = 1; k < c.size(); k++) {
        AlgebraicNumber<BigRational> factor = toField(c.getAt(k), coefficients);
        if (factor == null) {
          return null;
        }
        product = product.multiply(factor);
      }
      return product;
    }
    if (c.isPower() && c.exponent().isInteger()) {
      AlgebraicNumber<BigRational> base = toField(c.base(), coefficients);
      int n = ((IInteger) c.exponent()).toIntDefault(Integer.MIN_VALUE);
      if (base == null || n == Integer.MIN_VALUE || Math.abs(n) > MAX_EXPONENT) {
        return null;
      }
      if (n < 0) {
        if (base.isZERO()) {
          return null;
        }
        return base.inverse().power(-n);
      }
      return base.power(n);
    }
    if (c.isPower() && c.exponent().isFraction()) {
      IRational exponent = (IRational) c.exponent();
      AlgebraicNumber<BigRational> root =
          coefficients.atoms.get(radicalAtom(c.base(), exponent));
      int p = exponent.numerator().toIntDefault(Integer.MIN_VALUE);
      if (root == null || p == Integer.MIN_VALUE || Math.abs(p) > MAX_EXPONENT) {
        return null;
      }
      return p < 0 ? root.inverse().power(-p) : root.power(p);
    }
    return coefficients.atoms.get(c);
  }

  /** The coordinates of every coefficient of <code>p</code> in the basis of atom products. */
  private static Map<ExpVector, BigRational[]> basisCoordinates(
      GenPolynomial<AlgebraicNumber<BigRational>> p, CoefficientField coefficients) {
    Map<ExpVector, BigRational[]> result = new LinkedHashMap<ExpVector, BigRational[]>();
    int n = coefficients.basis.length;
    for (Map.Entry<ExpVector, AlgebraicNumber<BigRational>> monomial : p.getMap().entrySet()) {
      BigRational[] v = coordinates(monomial.getValue(), n);
      BigRational[] a = new BigRational[n];
      for (int i = 0; i < n; i++) {
        a[i] = BigRational.ZERO;
        for (int j = 0; j < n; j++) {
          if (!v[j].isZERO()) {
            a[i] = a[i].sum(coefficients.toBasis[i][j].multiply(v[j]));
          }
        }
      }
      result.put(monomial.getKey(), a);
    }
    return result;
  }

  /** The polynomial with the given basis coordinates, each multiplied by <code>scale</code>. */
  private static GenPolynomial<IExpr> fromBasis(Map<ExpVector, BigRational[]> coordinates,
      BigRational scale, GenPolynomialRing<IExpr> ring, CoefficientField coefficients,
      EvalEngine engine) {
    GenPolynomial<IExpr> result = new GenPolynomial<IExpr>(ring);
    for (Map.Entry<ExpVector, BigRational[]> monomial : coordinates.entrySet()) {
      BigRational[] a = monomial.getValue();
      IASTAppendable sum = F.PlusAlloc(a.length);
      for (int i = 0; i < a.length; i++) {
        if (!a[i].isZERO()) {
          BigRational c = a[i].multiply(scale);
          sum.append(F.Times(F.fraction(c.num, c.den), coefficients.basis[i]));
        }
      }
      result.doPutToMap(monomial.getKey(), engine.evaluate(sum.oneIdentity0()));
    }
    return result;
  }
}
