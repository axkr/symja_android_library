package org.matheclipse.core.convert;

import edu.jas.arith.MachinePrime;
import java.math.BigInteger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

/**
 * Convert <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> objects from and to Symja objects.
 *
 * @deprecated the coefficients are <code>ModLong</code>, which is only right for a modulus up to
 *             {@link Integer#MAX_VALUE}; use {@link JASModular}, which chooses the coefficient
 *             type by the modulus
 */
@Deprecated
public class JASModInteger {
  private final ModLongRing fRingFactory;
  private final TermOrder fTermOrder;
  private final GenPolynomialRing<ModLong> fPolyFactory;

  private final IAST fVariables;

  public JASModInteger(IAST varList, ModLongRing ringFactory) {
    this(varList, ringFactory, TermOrderByName.Lexicographic);
  }

  public JASModInteger(final IAST varList, ModLongRing ringFactory, TermOrder termOrder) {
    this.fRingFactory = ringFactory;
    this.fVariables = varList;
    String[] vars = new String[fVariables.argSize()];
    for (int i = 0; i < fVariables.argSize(); i++) {
      vars[i] = fVariables.get(i + 1).toString();
    }
    this.fTermOrder = termOrder;
    this.fPolyFactory =
        new GenPolynomialRing<ModLong>(fRingFactory, fVariables.argSize(), fTermOrder, vars);
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial
   *
   * @param exprPoly
   * @return
   * @throws JASConversionException
   */
  public GenPolynomial<ModLong> expr2JAS(final IExpr exprPoly) throws JASConversionException {
    try {
      return expr2Poly(exprPoly, false);
    } catch (Exception ae) {
      if (Config.SHOW_STACKTRACE) {
        ae.printStackTrace();
      }
      Errors.rethrowsInterruptException(ae);
      throw JASConversionException.FAILED;
    }
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial. <code>INum</code> double values are internally converted to IFractions and converte
   * into the pokynomial structure.
   *
   * @param exprPoly
   * @return
   * @throws JASConversionException
   */
  public GenPolynomial<ModLong> numericExpr2JAS(final IExpr exprPoly)
      throws JASConversionException {
    try {
      return numericExpr2Poly(exprPoly);
    } catch (Exception ae) {
      Errors.rethrowsInterruptException(ae);
      throw JASConversionException.FAILED;
    }
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial. Only symbolic numbers are converted (i.e. no <code>INum</code> or <code>IComplexNum
   * </code> values are converted into the polynomial structure)
   *
   * @param exprPoly
   * @return
   * @throws JASConversionException
   */
  public GenPolynomial<ModLong> expr2IExprJAS(final IExpr exprPoly) throws JASConversionException {
    try {
      return expr2IExprPoly(exprPoly);
    } catch (Exception ae) {
      Errors.rethrowsInterruptException(ae);
      throw JASConversionException.FAILED;
    }
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial. <code>INum</code> values are internally converted to IFractions and <code>expr2Poly
   * </code> was called for the expression
   *
   * @param exprPoly
   * @return
   * @throws ArithmeticException
   * @throws ClassCastException
   */
  private GenPolynomial<ModLong> numericExpr2Poly(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    return expr2Poly(exprPoly, true);
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial
   *
   * @param exprPoly
   * @param numeric2Rational if <code>true</code>, <code>INum</code> double values are converted to
   *        <code>BigRational</code> internally
   * @return
   * @throws ArithmeticException
   * @throws ClassCastException
   */
  private GenPolynomial<ModLong> expr2Poly(final IExpr exprPoly, boolean numeric2Rational)
      throws ArithmeticException, ClassCastException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isPlus() || ast.isTimes()) {
        return JASConvert.foldPlusTimes(ast, fPolyFactory, x -> expr2Poly(x, numeric2Rational));
      } else if (ast.isPower()) {
        GenPolynomial<ModLong> monomial = powerMonomial(ast);
        if (monomial != null) {
          return monomial;
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      int i = variableIndex(exprPoly);
      if (i >= 0) {
        return monomial(i, 1L);
      }
      // class cast exception
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory
          .fromInteger((java.math.BigInteger) exprPoly.asType(java.math.BigInteger.class));
    } else if (exprPoly instanceof IFraction) {
      // GF(p) is a field, so a rational coefficient has a value here as well
      return fraction2Poly((IFraction) exprPoly);
    }
    throw new ClassCastException(exprPoly.toString());
  }

  /** The position of <code>expr</code> in the variables list, <code>-1</code> if it isn't one. */
  private int variableIndex(IExpr expr) {
    for (int i = 0; i < fVariables.argSize(); i++) {
      if (fVariables.get(i + 1).equals(expr)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * The monomial <code>v^exponent</code> of the variable <code>v</code> at position
   * <code>i</code> of the variables list.
   */
  private GenPolynomial<ModLong> monomial(int i, long exponent) {
    // JAS stores variables in reverse order in the ExpVector: position 0 corresponds to
    // the last variable in the user list. Map the user-facing index i to the JAS-internal
    // position so the write round-trips correctly with monomialToExpr (which uses
    // leer.varIndex(i) to reverse the position back to a user index).
    int jasIndex = fVariables.argSize() - 1 - i;
    ExpVector e = ExpVector.create(fVariables.argSize(), jasIndex, exponent);
    return fPolyFactory.valueOf(e);
  }

  /**
   * The monomial for a power of one of the variables, <code>null</code> if the base isn't a
   * variable.
   *
   * @throws ArithmeticException if the exponent isn't a non-negative integer
   */
  private GenPolynomial<ModLong> powerMonomial(final IAST power) {
    int i = variableIndex(power.base());
    if (i < 0) {
      return null;
    }
    int exponent = power.exponent().toIntDefault();
    if (exponent < 0) {
      throw new ArithmeticException(
          "JASModInteger:expr2Poly - invalid exponent: " + power.exponent().toString());
    }
    return monomial(i, exponent);
  }

  /**
   * Convert a rational number into the constant polynomial holding its value in
   * <code>GF(p)</code>: the numerator times the modular inverse of the denominator.
   *
   * @param exprPoly the rational number
   * @return the constant polynomial
   * @throws ArithmeticException if the denominator is a multiple of the modulus and therefore not
   *         invertible
   */
  private GenPolynomial<ModLong> fraction2Poly(final IFraction exprPoly) {
    BigInteger n = exprPoly.toBigNumerator();
    BigInteger d = exprPoly.toBigDenominator();
    ModLong numerator = fRingFactory.fromInteger(n);
    ModLong denominator = fRingFactory.fromInteger(d);
    if (denominator.isZERO()) {
      throw new ArithmeticException(
          "JASModInteger:fraction2Poly - denominator is a multiple of the modulus: " + exprPoly);
    }
    return new GenPolynomial<ModLong>(fPolyFactory, numerator.divide(denominator));
  }

  private GenPolynomial<ModLong> expr2IExprPoly(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isPlus() || ast.isTimes()) {
        return JASConvert.foldPlusTimes(ast, fPolyFactory, x -> expr2IExprPoly(x));
      } else if (ast.isPower()) {
        GenPolynomial<ModLong> monomial = powerMonomial(ast);
        if (monomial != null) {
          return monomial;
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      int i = variableIndex(exprPoly);
      if (i >= 0) {
        return monomial(i, 1L);
      }
      return new GenPolynomial(fPolyFactory, exprPoly);
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory
          .fromInteger((java.math.BigInteger) exprPoly.asType(java.math.BigInteger.class));
    } else if (exprPoly instanceof IFraction) {
      return fraction2Poly((IFraction) exprPoly);
    }
    if (exprPoly.isFree(t -> fVariables.contains(t), true)) {
      return new GenPolynomial(fPolyFactory, exprPoly);
    } else {
      for (int i = 0; i < fVariables.argSize(); i++) {
        if (fVariables.get(i + 1).equals(exprPoly)) {
          int jasIndex = fVariables.argSize() - 1 - i;
          ExpVector e = ExpVector.create(fVariables.argSize(), jasIndex, 1L);
          return fPolyFactory.getONE().multiply(e);
        }
      }
    }
    throw new ClassCastException(exprPoly.toString());
  }

  /** @return the fPolyFactory */
  public GenPolynomialRing<ModLong> getPolynomialRingFactory() {
    return fPolyFactory;
  }

  public IExpr modLongPoly2Expr(final GenPolynomial<ModLong> poly)
      throws ArithmeticException, ClassCastException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<ModLong> monomial : poly) {
      ModLong coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IInteger coeffValue = F.ZZ(coeff.getVal());
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomialToExpr(coeffValue, exp, monomTimes);
      result.append(monomTimes.oneIdentity1());
    }
    return result.oneIdentity0();
  }

  public boolean monomialToExpr(IInteger coeff, ExpVector exp, IASTAppendable monomTimes) {
    if (!coeff.isOne()) {
      monomTimes.append(coeff);
    }
    return JASConvert.appendExpVector(exp, fPolyFactory.evzero, fVariables, monomTimes);
  }

  /**
   * @throws ArithmeticException if the modulus is larger than {@link Integer#MAX_VALUE}: the
   *         product of two residues overflows a <code>long</code> then
   */
  public static ModLongRing option2ModLongRing(IReal option) throws ArithmeticException {
    // maybe throw ArithmeticException
    long longValue = option.toLong();
    if (longValue > Integer.MAX_VALUE) {
      throw new ArithmeticException("modulus too large for ModLong: " + longValue);
    }
    final BigInteger value = BigInteger.valueOf(longValue);
    return new ModLongRing(longValue, MachinePrime.isProbablePrime(value, 32));
  }
}