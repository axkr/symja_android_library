package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;

/**
 * Polynomials over the field <code>GF(p)</code> of a prime <code>p</code>, the coefficient domain
 * of the <code>Modulus -&gt; p</code> option: the conversion between Symja expressions and
 * <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomials, and the JAS engines which
 * factor them and compute their greatest common divisors.
 *
 * <p>
 * The coefficients are machine words where the prime allows it and big integers where it
 * doesn't. JAS's <code>ModLong</code> multiplies two residues in a <code>long</code>, which is
 * only right for a modulus up to {@link Integer#MAX_VALUE}; beyond that the product overflows
 * without a word. {@link #of(IInteger, IAST)} therefore chooses <code>ModInteger</code> for a
 * larger prime, and the code which uses this class is written once for both.
 *
 * @param <C> the type of the coefficients, <code>ModLong</code> or <code>ModInteger</code>
 */
public final class JASModular<C extends GcdRingElem<C> & Modular> {

  private final ModularRingFactory<C> fRingFactory;

  private final GenPolynomialRing<C> fPolyFactory;

  private final IAST fVariables;

  private JASModular(IAST varList, ModularRingFactory<C> ringFactory, TermOrder termOrder) {
    this.fRingFactory = ringFactory;
    this.fVariables = varList;
    String[] vars = new String[varList.argSize()];
    for (int i = 0; i < varList.argSize(); i++) {
      vars[i] = varList.get(i + 1).toString();
    }
    this.fPolyFactory = new GenPolynomialRing<C>(ringFactory, varList.argSize(), termOrder, vars);
  }

  /**
   * The polynomials in the variables <code>varList</code> over <code>GF(modulus)</code>.
   *
   * @param modulus a prime number; see {@link #isPrimeModulus(IExpr)}
   */
  public static JASModular<?> of(IInteger modulus, IAST varList) {
    return of(modulus, varList, TermOrderByName.Lexicographic);
  }

  public static JASModular<?> of(IInteger modulus, IAST varList, TermOrder termOrder) {
    return create(modulus, varList, termOrder, true);
  }

  /**
   * The polynomials in the variables <code>varList</code> over the ring of the integers modulo
   * <code>modulus</code>, which is a field only if the modulus is prime: for reducing the
   * coefficients of a polynomial, where any positive modulus has a meaning.
   */
  public static JASModular<?> ofRing(IInteger modulus, IAST varList) {
    return create(modulus, varList, TermOrderByName.Lexicographic, modulus.isProbablePrime());
  }

  private static JASModular<?> create(IInteger modulus, IAST varList, TermOrder termOrder,
      boolean isField) {
    // WMA: Modulus -> -5 is Modulus -> 5
    BigInteger m = modulus.toBigNumerator().abs();
    if (m.bitLength() < 32) {
      return new JASModular<>(varList, new ModLongRing(m.longValue(), isField), termOrder);
    }
    return new JASModular<>(varList, new ModIntegerRing(m, isField), termOrder);
  }

  /** The polynomial of the expression, with its coefficients reduced, as an expression again. */
  public IExpr reduce(IExpr exprPoly) throws JASConversionException {
    return poly2Expr(expr2JAS(exprPoly));
  }

  /**
   * Whether <code>option</code> is a prime number, the only modulus which gives a field. A
   * negative modulus stands for its absolute value, as in WMA.
   */
  public static boolean isPrimeModulus(IExpr option) {
    return option.isInteger() && ((IInteger) option).abs().isProbablePrime();
  }

  /**
   * Print the message
   * <code>The value of the option `1` should be a prime number or zero.</code> if the value of
   * the <code>Modulus</code> option is neither.
   *
   * @param symbol the function the message is printed for
   * @param option the value of the option
   * @return <code>true</code> if the message was printed, and the function has no result
   */
  public static boolean isModpMessage(IBuiltInSymbol symbol, IExpr option) {
    if (option.isZero() || isPrimeModulus(option)) {
      return false;
    }
    // The value of the option `1` should be a prime number or zero.
    Errors.printMessage(symbol, "modp", F.List(F.Rule(S.Modulus, option)));
    return true;
  }

  public ModularRingFactory<C> getRingFactory() {
    return fRingFactory;
  }

  public GenPolynomialRing<C> getPolynomialRingFactory() {
    return fPolyFactory;
  }

  /** The engine which factors polynomials over this field. */
  @SuppressWarnings("unchecked")
  public FactorAbstract<C> factorization() {
    // the engines JAS chooses for the ring itself; the one it chooses for a ring it only knows
    // as a RingFactory is another one
    if (fRingFactory instanceof ModLongRing) {
      return (FactorAbstract<C>) (FactorAbstract<?>) FactorFactory.getImplementation((ModLongRing) fRingFactory);
    }
    return (FactorAbstract<C>) (FactorAbstract<?>) FactorFactory.getImplementation((ModIntegerRing) fRingFactory);
  }

  /**
   * The factors of a polynomial and their multiplicities; a leading coefficient other than 1 is
   * a factor of its own.
   *
   * <p>
   * JAS's own decomposition into square-free parts takes the characteristic as a
   * <code>long</code>. For a prime beyond that the decomposition is done here: such a prime is
   * larger than the degree of any polynomial, so a derivative vanishes for a constant only and the
   * decomposition is the one of characteristic 0.
   *
   * @param squareFreeOnly only split into the square-free parts
   * @throws UnsupportedOperationException for a polynomial in several variables over a prime
   *         which is no <code>long</code>
   */
  public SortedMap<GenPolynomial<C>, Long> factors(GenPolynomial<C> poly, boolean squareFreeOnly) {
    FactorAbstract<C> engine = factorization();
    if (fRingFactory.getIntegerModul().getVal().bitLength() < 64) {
      return squareFreeOnly ? engine.squarefreeFactors(poly) : engine.factors(poly);
    }
    if (fPolyFactory.nvar != 1) {
      throw new UnsupportedOperationException("multivariate polynomial over a large prime");
    }
    SortedMap<GenPolynomial<C>, Long> result = new TreeMap<GenPolynomial<C>, Long>();
    if (poly.isZERO() || poly.isConstant()) {
      result.put(poly, 1L);
      return result;
    }
    C leading = poly.leadingBaseCoefficient();
    if (!leading.isONE()) {
      result.put(fPolyFactory.getONE().multiply(leading), 1L);
      poly = poly.monic();
    }
    GreatestCommonDivisorAbstract<C> gcd = gcd();
    // Yun's algorithm: a(i) is the product of the factors of multiplicity i
    GenPolynomial<C> derivative = PolyUtil.<C>baseDerivative(poly);
    GenPolynomial<C> a = gcd.baseGcd(poly, derivative).monic();
    GenPolynomial<C> b = poly.divide(a);
    GenPolynomial<C> d = derivative.divide(a).subtract(PolyUtil.<C>baseDerivative(b));
    for (long multiplicity = 1L; !b.isConstant(); multiplicity++) {
      a = gcd.baseGcd(b, d).monic();
      b = b.divide(a);
      d = d.divide(a).subtract(PolyUtil.<C>baseDerivative(b));
      if (a.isConstant()) {
        continue;
      }
      if (squareFreeOnly) {
        result.put(a, multiplicity);
      } else {
        for (GenPolynomial<C> factor : engine.baseFactorsSquarefree(a)) {
          result.put(factor.monic(), multiplicity);
        }
      }
    }
    return result;
  }

  /** The engine which computes greatest common divisors over this field. */
  @SuppressWarnings("unchecked")
  public GreatestCommonDivisorAbstract<C> gcd() {
    if (fRingFactory instanceof ModLongRing) {
      return (GreatestCommonDivisorAbstract<C>) (GreatestCommonDivisorAbstract<?>) GCDFactory
          .getImplementation((ModLongRing) fRingFactory);
    }
    return (GreatestCommonDivisorAbstract<C>) (GreatestCommonDivisorAbstract<?>) GCDFactory
        .getImplementation((ModIntegerRing) fRingFactory);
  }

  /**
   * Convert the expression into a JAS polynomial.
   *
   * @throws JASConversionException if the expression is no polynomial in the variables with
   *         rational coefficients, or a denominator is a multiple of the modulus
   */
  public GenPolynomial<C> expr2JAS(final IExpr exprPoly) throws JASConversionException {
    try {
      return expr2Poly(exprPoly);
    } catch (Exception ae) {
      if (Config.SHOW_STACKTRACE) {
        ae.printStackTrace();
      }
      Errors.rethrowsInterruptException(ae);
      throw JASConversionException.FAILED;
    }
  }

  private GenPolynomial<C> variable(int i, long exponent) {
    // JAS stores the variables in reverse order in the ExpVector: position 0 is the last variable
    // of the list. poly2Expr reverses the position back with varIndex().
    int jasIndex = fVariables.argSize() - 1 - i;
    return fPolyFactory.valueOf(ExpVector.create(fVariables.argSize(), jasIndex, exponent));
  }

  private GenPolynomial<C> expr2Poly(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isPlus()) {
        GenPolynomial<C> result = expr2Poly(ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.sum(expr2Poly(ast.get(i)));
        }
        return result;
      } else if (ast.isTimes()) {
        GenPolynomial<C> result = expr2Poly(ast.arg1());
        for (int i = 2; i < ast.size(); i++) {
          result = result.multiply(expr2Poly(ast.get(i)));
        }
        return result;
      } else if (ast.isPower()) {
        final IExpr base = ast.base();
        for (int i = 0; i < fVariables.argSize(); i++) {
          if (fVariables.get(i + 1).equals(base)) {
            int exponent = ast.exponent().toIntDefault();
            if (exponent < 0) {
              throw new ArithmeticException(
                  "JASModular:expr2Poly - invalid exponent: " + ast.exponent().toString());
            }
            return variable(i, exponent);
          }
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      for (int i = 0; i < fVariables.argSize(); i++) {
        if (fVariables.get(i + 1).equals(exprPoly)) {
          return variable(i, 1L);
        }
      }
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory.fromInteger(((IInteger) exprPoly).toBigNumerator());
    } else if (exprPoly instanceof IFraction) {
      // GF(p) is a field, so a rational coefficient has a value here as well: the numerator times
      // the inverse of the denominator
      IFraction fraction = (IFraction) exprPoly;
      C numerator = fRingFactory.fromInteger(fraction.toBigNumerator());
      C denominator = fRingFactory.fromInteger(fraction.toBigDenominator());
      if (denominator.isZERO()) {
        throw new ArithmeticException(
            "JASModular:expr2Poly - denominator is a multiple of the modulus: " + exprPoly);
      }
      return new GenPolynomial<C>(fPolyFactory, numerator.divide(denominator));
    }
    throw new ClassCastException(exprPoly.toString());
  }

  /** Convert a JAS polynomial into an expression; the coefficients are <code>0 ... p-1</code>. */
  public IExpr poly2Expr(final GenPolynomial<C> poly) {
    if (poly.length() == 0) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<C> monomial : poly) {
      result.append(monomial(monomial).oneIdentity1());
    }
    return result.oneIdentity0();
  }

  /** The monomials of the polynomial of the expression, each one as a product. */
  public IAST monomialList(IExpr exprPoly) throws JASConversionException {
    GenPolynomial<C> poly = expr2JAS(exprPoly);
    IASTAppendable list = F.ListAlloc(poly.length());
    for (Monomial<C> monomial : poly) {
      list.append(monomial(monomial));
    }
    return list;
  }

  private IASTAppendable monomial(Monomial<C> monomial) {
    IInteger coeff = F.ZZ(monomial.coefficient().getInteger().getVal());
    ExpVector exp = monomial.exponent();
    ExpVector leer = fPolyFactory.evzero;
    IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
    if (!coeff.isOne()) {
      monomTimes.append(coeff);
    }
    for (int i = 0; i < exp.length(); i++) {
      long lExp = exp.getVal(i);
      if (lExp != 0) {
        int ix = leer.varIndex(i);
        if (ix >= 0) {
          IExpr variable = fVariables.get(ix + 1);
          monomTimes.append(lExp == 1L ? variable : F.Power(variable, F.ZZ(lExp)));
        }
      }
    }
    return monomTimes;
  }
}
