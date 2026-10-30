package org.matheclipse.core.polynomials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import edu.jas.arith.BigRational;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrderByName;

/**
 * The lexicographic Groebner basis of a zero-dimensional ideal with rational coefficients, from its
 * degree reverse lexicographic basis (Faugere, Gianni, Lazard, Mora).
 *
 * <p>
 * Buchberger's algorithm in the lexicographic order lets the coefficients grow out of all proportion
 * on the way to a basis which is not large at all. Here the graded basis is computed first, which is
 * cheap, and the quotient ring is a vector space of finite dimension in which multiplying by a
 * variable is linear. Walking through the monomials in the lexicographic order, each one is either
 * independent of the ones before it, or the linear relation is the next basis element. That is done
 * modulo primes, the coefficients come from the Chinese remainder theorem and rational
 * reconstruction.
 *
 * <p>
 * The primes only propose the basis. It is certified exactly: every element reduces to 0 by the
 * graded basis, so it lies in the ideal, and its leading monomials leave as many standard monomials
 * as the quotient ring has dimensions, so nothing is missing.
 */
public final class FGLMConversion {

  /** More standard monomials than this are left to the algorithm of Buchberger. */
  private static final int MAX_DIMENSION = 2000;

  /** Primes which give another shape than the first one before the conversion gives up. */
  private static final int MAX_UNLUCKY_PRIMES = 8;

  /** More primes than this are not tried. */
  private static final int MAX_PRIMES = 20000;

  private FGLMConversion() {}

  /** A monomial as exponents in the order of the variables, compared lexicographically. */
  private static final class Monomial2 implements Comparable<Monomial2> {
    final int[] exponents;

    Monomial2(int[] exponents) {
      this.exponents = exponents;
    }

    Monomial2 times(int variable) {
      int[] product = exponents.clone();
      product[variable]++;
      return new Monomial2(product);
    }

    boolean isMultipleOf(Monomial2 other) {
      for (int i = 0; i < exponents.length; i++) {
        if (exponents[i] < other.exponents[i]) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int compareTo(Monomial2 other) {
      for (int i = 0; i < exponents.length; i++) {
        if (exponents[i] != other.exponents[i]) {
          return exponents[i] < other.exponents[i] ? -1 : 1;
        }
      }
      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof Monomial2 && Arrays.equals(exponents, ((Monomial2) obj).exponents);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(exponents);
    }
  }

  /** The lexicographic basis modulo one prime. */
  private static final class ModularBasis {
    final List<Monomial2> leading = new ArrayList<Monomial2>();
    final List<Monomial2> standard = new ArrayList<Monomial2>();
    /** For every element the coefficients of its tail, over {@link #standard}. */
    final List<long[]> tails = new ArrayList<long[]>();

    boolean sameShape(ModularBasis other) {
      return leading.equals(other.leading) && standard.equals(other.standard);
    }
  }

  /** The multiplication by the variables in the quotient ring, as tables which fit every prime. */
  private static final class Quotient {
    int variables;
    /** The monomials of the graded basis, without the leading one, per element. */
    List<int[]> tailIndices = new ArrayList<int[]>();
    List<BigRational[]> tailCoefficients = new ArrayList<BigRational[]>();
    /** <code>standard[k]*x[i]</code>: the index of a standard monomial, or <code>~border</code>. */
    int[][] next;
    /** The element a border monomial is the leading monomial of, or -1. */
    int[] borderElement;
    /** The variable and the border monomial a derived border monomial is the product of. */
    int[] borderVariable;
    int[] borderPredecessor;
    int dimension;
  }

  /**
   * @param listOfPolynomials polynomials with rational coefficients
   * @param listOfVariables the variables, the first one the largest
   * @return the reduced basis, or {@link F#NIL} if the ideal isn't zero-dimensional or the
   *         conversion can't be done
   */
  public static IAST lexicographicBasis(IAST listOfPolynomials, IAST listOfVariables) {
    final EvalEngine engine = EvalEngine.get();
    try {
      JASConvert<BigRational> jas = new JASConvert<BigRational>(listOfVariables, BigRational.ZERO,
          TermOrderByName.DegreeReverseLexicographic);
      List<GenPolynomial<BigRational>> polynomials =
          new ArrayList<GenPolynomial<BigRational>>(listOfPolynomials.argSize());
      for (int i = 1; i < listOfPolynomials.size(); i++) {
        GenPolynomial<BigRational> polynomial =
            jas.expr2JAS(F.evalExpandAll(listOfPolynomials.get(i), engine), false);
        if (polynomial == null) {
          return F.NIL;
        }
        if (!polynomial.isZERO()) {
          polynomials.add(polynomial);
        }
      }
      if (polynomials.isEmpty()) {
        return F.NIL;
      }
      List<GenPolynomial<BigRational>> graded =
          TimeLimitedReduction.<BigRational>groebnerBase().GB(polynomials);
      if (graded.isEmpty() || graded.get(0).isONE()) {
        return F.NIL;
      }
      int n = listOfVariables.argSize();
      int[] jasIndex = jasIndices(jas, listOfVariables, n);
      if (jasIndex == null) {
        return F.NIL;
      }
      Quotient quotient = quotient(graded, jas.getPolynomialRingFactory(), n, jasIndex);
      if (quotient == null) {
        return F.NIL;
      }
      return liftedBasis(graded, jas, quotient, jasIndex, listOfVariables, engine);
    } catch (JASConversionException | ClassCastException | ArithmeticException ex) {
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** The position in an exponent vector of the library for every variable. */
  private static int[] jasIndices(JASConvert<BigRational> jas, IAST variables, int n) {
    int[] index = new int[n];
    for (int i = 0; i < n; i++) {
      GenPolynomial<BigRational> variable = jas.expr2JAS(variables.get(i + 1), false);
      if (variable == null) {
        return null;
      }
      ExpVector exponents = variable.leadingExpVector();
      index[i] = -1;
      for (int j = 0; j < n; j++) {
        if (exponents.getVal(j) == 1L) {
          index[i] = j;
        }
      }
      if (index[i] < 0) {
        return null;
      }
    }
    return index;
  }

  private static Monomial2 monomial(ExpVector exponents, int[] jasIndex) {
    int[] result = new int[jasIndex.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = (int) exponents.getVal(jasIndex[i]);
    }
    return new Monomial2(result);
  }

  private static ExpVector expVector(Monomial2 monomial, int[] jasIndex) {
    long[] exponents = new long[jasIndex.length];
    for (int i = 0; i < exponents.length; i++) {
      exponents[jasIndex[i]] = monomial.exponents[i];
    }
    return ExpVector.create(exponents);
  }

  /**
   * The standard monomials of the graded basis and the tables of the multiplication.
   *
   * @return <code>null</code> if there are infinitely many, or too many
   */
  private static Quotient quotient(List<GenPolynomial<BigRational>> graded,
      GenPolynomialRing<BigRational> ring, int n, int[] jasIndex) {
    List<Monomial2> leading = new ArrayList<Monomial2>(graded.size());
    boolean[] purePower = new boolean[n];
    for (GenPolynomial<BigRational> element : graded) {
      Monomial2 lead = monomial(element.leadingExpVector(), jasIndex);
      leading.add(lead);
      int nonZero = 0;
      int variable = -1;
      for (int i = 0; i < n; i++) {
        if (lead.exponents[i] != 0) {
          nonZero++;
          variable = i;
        }
      }
      if (nonZero == 1) {
        purePower[variable] = true;
      }
    }
    for (boolean pure : purePower) {
      if (!pure) {
        return null;
      }
    }
    Map<Monomial2, Integer> standardIndex = new HashMap<Monomial2, Integer>();
    List<Monomial2> standard = new ArrayList<Monomial2>();
    Monomial2 one = new Monomial2(new int[n]);
    standard.add(one);
    standardIndex.put(one, 0);
    for (int k = 0; k < standard.size(); k++) {
      for (int i = 0; i < n; i++) {
        Monomial2 product = standard.get(k).times(i);
        if (!standardIndex.containsKey(product) && !isDivisible(product, leading)) {
          if (standard.size() >= MAX_DIMENSION) {
            return null;
          }
          standardIndex.put(product, standard.size());
          standard.add(product);
        }
      }
    }
    Quotient quotient = new Quotient();
    quotient.variables = n;
    quotient.dimension = standard.size();
    for (GenPolynomial<BigRational> element : graded) {
      int length = element.length() - 1;
      int[] indices = new int[length];
      BigRational[] coefficients = new BigRational[length];
      int position = 0;
      ExpVector lead = element.leadingExpVector();
      for (Monomial<BigRational> term : element) {
        if (term.exponent().equals(lead)) {
          continue;
        }
        Integer index = standardIndex.get(monomial(term.exponent(), jasIndex));
        if (index == null) {
          // the tail of a reduced basis has standard monomials only
          return null;
        }
        indices[position] = index;
        coefficients[position++] = term.coefficient();
      }
      quotient.tailIndices.add(indices);
      quotient.tailCoefficients.add(coefficients);
    }

    Comparator<ExpVector> ascending = ring.tord.getAscendComparator();
    TreeMap<ExpVector, Monomial2> border = new TreeMap<ExpVector, Monomial2>(ascending);
    for (Monomial2 monomial : standard) {
      for (int i = 0; i < n; i++) {
        Monomial2 product = monomial.times(i);
        if (!standardIndex.containsKey(product)) {
          border.put(expVector(product, jasIndex), product);
        }
      }
    }
    Map<Monomial2, Integer> borderIndex = new HashMap<Monomial2, Integer>();
    int size = border.size();
    quotient.borderElement = new int[size];
    quotient.borderVariable = new int[size];
    quotient.borderPredecessor = new int[size];
    int id = 0;
    for (Monomial2 monomial : border.values()) {
      borderIndex.put(monomial, id);
      int element = leading.indexOf(monomial);
      quotient.borderElement[id] = element;
      if (element < 0) {
        boolean found = false;
        for (int j = 0; j < n && !found; j++) {
          if (monomial.exponents[j] > 0) {
            int[] exponents = monomial.exponents.clone();
            exponents[j]--;
            Integer predecessor = borderIndex.get(new Monomial2(exponents));
            if (predecessor != null) {
              quotient.borderVariable[id] = j;
              quotient.borderPredecessor[id] = predecessor;
              found = true;
            }
          }
        }
        if (!found) {
          return null;
        }
      }
      id++;
    }
    quotient.next = new int[standard.size()][n];
    for (int k = 0; k < standard.size(); k++) {
      for (int i = 0; i < n; i++) {
        Monomial2 product = standard.get(k).times(i);
        Integer index = standardIndex.get(product);
        quotient.next[k][i] = index != null ? index : ~borderIndex.get(product);
      }
    }
    return quotient;
  }

  private static boolean isDivisible(Monomial2 monomial, List<Monomial2> leading) {
    for (Monomial2 lead : leading) {
      if (monomial.isMultipleOf(lead)) {
        return true;
      }
    }
    return false;
  }

  /** <code>a/b</code> modulo <code>p</code>, or -1 if <code>p</code> divides <code>b</code>. */
  private static long residue(BigRational number, long p, BigInteger prime) {
    long denominator = number.denominator().mod(prime).longValue();
    if (denominator == 0L) {
      return -1L;
    }
    return number.numerator().mod(prime).longValue() * UnivariateModularGCD.inverse(denominator, p) % p;
  }

  /** Multiply a vector of the quotient ring by a variable. */
  private static long[] multiply(long[] vector, int variable, Quotient quotient, long[][] border,
      long p) {
    int d = quotient.dimension;
    long[] result = new long[d];
    for (int k = 0; k < d; k++) {
      long c = vector[k];
      if (c == 0L) {
        continue;
      }
      int target = quotient.next[k][variable];
      if (target >= 0) {
        result[target] = (result[target] + c) % p;
      } else {
        long[] values = border[~target];
        for (int l = 0; l < d; l++) {
          if (values[l] != 0L) {
            result[l] = (result[l] + c * values[l]) % p;
          }
        }
      }
    }
    return result;
  }

  /** The vectors of the border monomials modulo <code>p</code>, or <code>null</code>. */
  private static long[][] borderVectors(Quotient quotient, long p) {
    BigInteger prime = BigInteger.valueOf(p);
    int size = quotient.borderElement.length;
    long[][] border = new long[size][];
    for (int id = 0; id < size; id++) {
      int element = quotient.borderElement[id];
      if (element >= 0) {
        long[] vector = new long[quotient.dimension];
        int[] indices = quotient.tailIndices.get(element);
        BigRational[] coefficients = quotient.tailCoefficients.get(element);
        for (int i = 0; i < indices.length; i++) {
          long value = residue(coefficients[i], p, prime);
          if (value < 0L) {
            return null;
          }
          vector[indices[i]] = value == 0L ? 0L : p - value;
        }
        border[id] = vector;
      } else {
        border[id] = multiply(border[quotient.borderPredecessor[id]], quotient.borderVariable[id],
            quotient, border, p);
      }
    }
    return border;
  }

  /** One conversion modulo <code>p</code>, or <code>null</code> for a prime which can't be used. */
  private static ModularBasis modularBasis(Quotient quotient, long p, EvalEngine engine) {
    long[][] border = borderVectors(quotient, p);
    if (border == null) {
      return null;
    }
    int d = quotient.dimension;
    int n = quotient.variables;
    ModularBasis basis = new ModularBasis();
    List<long[]> vectors = new ArrayList<long[]>(d);
    // the echelon form: a row with its pivot column, and the combination of the vectors giving it
    List<long[]> rows = new ArrayList<long[]>(d);
    List<long[]> combinations = new ArrayList<long[]>(d);
    int[] pivots = new int[d];

    TreeMap<Monomial2, int[]> candidates = new TreeMap<Monomial2, int[]>();
    long[] oneVector = new long[d];
    oneVector[0] = 1L;
    candidates.put(new Monomial2(new int[n]), null);
    while (!candidates.isEmpty()) {
      engine.checkTimeLimit();
      Map.Entry<Monomial2, int[]> entry = candidates.pollFirstEntry();
      Monomial2 monomial = entry.getKey();
      if (isDivisible(monomial, basis.leading)) {
        continue;
      }
      int[] origin = entry.getValue();
      long[] vector =
          origin == null ? oneVector : multiply(vectors.get(origin[0]), origin[1], quotient, border, p);
      long[] rest = vector.clone();
      long[] combination = new long[d + 1];
      for (int r = 0; r < rows.size(); r++) {
        long c = rest[pivots[r]];
        if (c == 0L) {
          continue;
        }
        long negated = p - c;
        long[] row = rows.get(r);
        for (int l = 0; l < d; l++) {
          rest[l] = (rest[l] + negated * row[l]) % p;
        }
        long[] rowCombination = combinations.get(r);
        for (int l = 0; l < d; l++) {
          combination[l] = (combination[l] + negated * rowCombination[l]) % p;
        }
      }
      int pivot = -1;
      for (int l = 0; l < d && pivot < 0; l++) {
        if (rest[l] != 0L) {
          pivot = l;
        }
      }
      if (pivot < 0) {
        // the monomial plus the combination is 0 in the quotient ring
        basis.leading.add(monomial);
        basis.tails.add(combination);
        continue;
      }
      int index = vectors.size();
      if (index >= d) {
        // more independent vectors than dimensions
        return null;
      }
      combination[index] = 1L;
      long scale = UnivariateModularGCD.inverse(rest[pivot], p);
      for (int l = 0; l < d; l++) {
        rest[l] = rest[l] * scale % p;
        combination[l] = combination[l] * scale % p;
      }
      vectors.add(vector);
      basis.standard.add(monomial);
      pivots[rows.size()] = pivot;
      rows.add(rest);
      combinations.add(combination);
      for (int i = 0; i < n; i++) {
        Monomial2 product = monomial.times(i);
        if (!candidates.containsKey(product)) {
          candidates.put(product, new int[] {index, i});
        }
      }
    }
    return basis.standard.size() == d ? basis : null;
  }

  /**
   * The rational number with numerator and denominator below the root of the half of
   * <code>modulus</code>, or <code>null</code>.
   */
  private static BigRational reconstruct(BigInteger value, BigInteger modulus, BigInteger bound) {
    if (value.signum() == 0) {
      return BigRational.ZERO;
    }
    BigInteger r0 = modulus;
    BigInteger r1 = value;
    BigInteger t0 = BigInteger.ZERO;
    BigInteger t1 = BigInteger.ONE;
    while (r1.compareTo(bound) > 0) {
      BigInteger[] quotientRemainder = r0.divideAndRemainder(r1);
      r0 = r1;
      r1 = quotientRemainder[1];
      BigInteger t = t0.subtract(quotientRemainder[0].multiply(t1));
      t0 = t1;
      t1 = t;
    }
    if (t1.signum() == 0 || t1.abs().compareTo(bound) > 0 || !r1.gcd(t1).equals(BigInteger.ONE)) {
      return null;
    }
    return t1.signum() < 0 ? rational(r1.negate(), t1.negate()) : rational(r1, t1);
  }

  private static BigRational rational(BigInteger numerator, BigInteger denominator) {
    return new BigRational(new edu.jas.arith.BigInteger(numerator),
        new edu.jas.arith.BigInteger(denominator));
  }

  /** The tails of the basis as rational numbers, or <code>null</code> if they don't exist yet. */
  private static BigRational[][] reconstructAll(BigInteger[][] residues, BigInteger modulus) {
    BigInteger bound = modulus.shiftRight(1).sqrt();
    BigRational[][] result = new BigRational[residues.length][];
    for (int i = 0; i < residues.length; i++) {
      result[i] = new BigRational[residues[i].length];
      for (int j = 0; j < residues[i].length; j++) {
        result[i][j] = reconstruct(residues[i][j], modulus, bound);
        if (result[i][j] == null) {
          return null;
        }
      }
    }
    return result;
  }

  private static boolean agrees(BigRational[][] rationals, ModularBasis basis, long p) {
    BigInteger prime = BigInteger.valueOf(p);
    for (int i = 0; i < rationals.length; i++) {
      long[] tail = basis.tails.get(i);
      for (int j = 0; j < rationals[i].length; j++) {
        long value = residue(rationals[i][j], p, prime);
        if (value < 0L || value != tail[j]) {
          return false;
        }
      }
    }
    return true;
  }


  /** Lift the modular bases to the rational numbers and certify the result. */
  private static IAST liftedBasis(List<GenPolynomial<BigRational>> graded,
      JASConvert<BigRational> jas, Quotient quotient, int[] jasIndex, IAST variables,
      EvalEngine engine) {
    long p = (1L << 31) + 1L;
    ModularBasis reference = null;
    BigInteger[][] residues = null;
    BigInteger modulus = BigInteger.ONE;
    int unlucky = 0;
    int used = 0;
    int nextAttempt = 1;
    for (int attempt = 0; attempt < MAX_PRIMES; attempt++) {
      engine.checkTimeLimit();
      p = UnivariateModularGCD.previousPrime(p);
      ModularBasis basis = modularBasis(quotient, p, engine);
      if (basis == null) {
        continue;
      }
      if (reference == null) {
        reference = basis;
        residues = new BigInteger[basis.tails.size()][basis.standard.size()];
        for (BigInteger[] row : residues) {
          Arrays.fill(row, BigInteger.ZERO);
        }
      } else if (!reference.sameShape(basis)) {
        if (++unlucky > MAX_UNLUCKY_PRIMES) {
          return F.NIL;
        }
        continue;
      }
      BigInteger prime = BigInteger.valueOf(p);
      BigInteger inverse = modulus.mod(prime).modInverse(prime);
      for (int i = 0; i < residues.length; i++) {
        long[] tail = basis.tails.get(i);
        for (int j = 0; j < residues[i].length; j++) {
          BigInteger difference = BigInteger.valueOf(tail[j]).subtract(residues[i][j]);
          residues[i][j] = residues[i][j]
              .add(modulus.multiply(difference.multiply(inverse).mod(prime)));
        }
      }
      modulus = modulus.multiply(prime);
      used++;
      if (used < nextAttempt) {
        continue;
      }
      nextAttempt = used + Math.max(1, used / 4);
      BigRational[][] rationals = reconstructAll(residues, modulus);
      if (rationals == null) {
        continue;
      }
      p = UnivariateModularGCD.previousPrime(p);
      ModularBasis control = modularBasis(quotient, p, engine);
      if (control != null
          && (!control.sameShape(reference) || !agrees(rationals, control, p))) {
        continue;
      }
      IAST result = certified(graded, jas, reference, rationals, jasIndex, variables, engine);
      if (result.isPresent()) {
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * The basis with the proposed coefficients, if every element reduces to 0: then the elements are
   * in the ideal, and since their leading monomials leave exactly the dimension of the quotient ring
   * as standard monomials, they are its lexicographic basis.
   */
  private static IAST certified(List<GenPolynomial<BigRational>> graded,
      JASConvert<BigRational> jas, ModularBasis basis, BigRational[][] rationals, int[] jasIndex,
      IAST variables, EvalEngine engine) {
    GenPolynomialRing<BigRational> ring = jas.getPolynomialRingFactory();
    ReductionSeq<BigRational> reduction = new ReductionSeq<BigRational>();
    IASTAppendable result = F.ListAlloc(rationals.length);
    for (int i = 0; i < rationals.length; i++) {
      engine.checkTimeLimit();
      GenPolynomial<BigRational> element =
          ring.getZERO().sum(BigRational.ONE, expVector(basis.leading.get(i), jasIndex));
      for (int j = 0; j < rationals[i].length; j++) {
        if (!rationals[i][j].isZERO()) {
          element = element.sum(rationals[i][j], expVector(basis.standard.get(j), jasIndex));
        }
      }
      if (!reduction.normalform(graded, element).isZERO()) {
        return F.NIL;
      }
      result.append(asExpression(basis.leading.get(i), basis.standard, rationals[i], variables));
    }
    return (IAST) engine.evaluate(result);
  }

  /** The element with integer coefficients without a common factor. */
  private static IExpr asExpression(Monomial2 leading, List<Monomial2> standard,
      BigRational[] coefficients, IAST variables) {
    BigInteger denominator = BigInteger.ONE;
    for (BigRational coefficient : coefficients) {
      BigInteger other = coefficient.denominator();
      denominator = denominator.multiply(other.divide(denominator.gcd(other)));
    }
    BigInteger content = denominator;
    BigInteger[] integers = new BigInteger[coefficients.length];
    for (int j = 0; j < coefficients.length; j++) {
      integers[j] = coefficients[j].numerator()
          .multiply(denominator.divide(coefficients[j].denominator()));
      content = content.gcd(integers[j]);
    }
    IASTAppendable sum = F.PlusAlloc(coefficients.length + 1);
    sum.append(term(denominator.divide(content), leading, variables));
    for (int j = 0; j < coefficients.length; j++) {
      if (integers[j].signum() != 0) {
        sum.append(term(integers[j].divide(content), standard.get(j), variables));
      }
    }
    return sum;
  }

  private static IExpr term(BigInteger coefficient, Monomial2 monomial, IAST variables) {
    IASTAppendable times = F.TimesAlloc(monomial.exponents.length + 1);
    times.append(F.ZZ(coefficient));
    for (int i = 0; i < monomial.exponents.length; i++) {
      if (monomial.exponents[i] != 0) {
        times.append(F.Power(variables.get(i + 1), F.ZZ(monomial.exponents[i])));
      }
    }
    return times;
  }
}
