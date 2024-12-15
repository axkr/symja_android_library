package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
import edu.jas.arith.BigRational;
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
 */
public class JASModInteger {
  private final ModLongRing fRingFactory;
  private final TermOrder fTermOrder;
  private final GenPolynomialRing<ModLong> fPolyFactory;

  private final List<? extends IExpr> fVariables;

  public JASModInteger(IExpr variable, ModLongRing ringFactory) {
    List<IExpr> varList = new ArrayList<IExpr>();
    varList.add(variable);
    this.fRingFactory = ringFactory;
    this.fVariables = varList;
    String[] vars = new String[fVariables.size()];
    for (int i = 0; i < fVariables.size(); i++) {
      vars[i] = fVariables.get(i).toString();
    }
    this.fTermOrder = TermOrderByName.Lexicographic;
    this.fPolyFactory =
        new GenPolynomialRing<ModLong>(fRingFactory, fVariables.size(), fTermOrder, vars);
  }

  public JASModInteger(final List<? extends IExpr> variablesList, ModLongRing ringFactory) {
    this(variablesList, ringFactory, TermOrderByName.Lexicographic);
  }

  public JASModInteger(final List<? extends IExpr> variablesList, ModLongRing ringFactory,
      TermOrder termOrder) {
    this.fRingFactory = ringFactory;
    this.fVariables = variablesList;
    String[] vars = new String[fVariables.size()];
    for (int i = 0; i < fVariables.size(); i++) {
      vars[i] = fVariables.get(i).toString();
    }
    this.fTermOrder = termOrder;
    this.fPolyFactory =
        new GenPolynomialRing<ModLong>(fRingFactory, fVariables.size(), fTermOrder, vars);
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
      GenPolynomial<ModLong> result = fPolyFactory.getZERO();
      GenPolynomial<ModLong> p = fPolyFactory.getZERO();
      if (ast.isPlus()) {
        IExpr expr = ast.arg1();
        result = expr2Poly(expr, numeric2Rational);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2Poly(expr, numeric2Rational);
          result = result.sum(p);
        }
        return result;
      } else if (ast.isTimes()) {
        IExpr expr = ast.arg1();
        result = expr2Poly(expr, numeric2Rational);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2Poly(expr, numeric2Rational);
          result = result.multiply(p);
        }
        return result;
      } else if (ast.isPower()) {
        final IExpr base = ast.base();
        for (int i = 0; i < fVariables.size(); i++) {
          if (fVariables.get(i).equals(base)) {
            int exponent = ast.exponent().toIntDefault();
            if (exponent < 0) {
              throw new ArithmeticException(
                  "JASConvert:expr2Poly - invalid exponent: " + ast.exponent().toString());
            }
            ExpVector e = ExpVector.create(fVariables.size(), i, exponent);
            return fPolyFactory.valueOf(e);
          }
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      for (int i = 0; i < fVariables.size(); i++) {
        if (fVariables.get(i).equals(exprPoly)) {
          ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
          return fPolyFactory.getONE().multiply(e);
        }
      }
      // class cast exception
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory.fromInteger(
          (java.math.BigInteger) exprPoly.asType(java.math.BigInteger.class));
    }
    throw new ClassCastException(exprPoly.toString());
  }

  private GenPolynomial<ModLong> fraction2Poly(final IFraction exprPoly) {
    BigInteger n = exprPoly.toBigNumerator(); // .toJavaBigInteger();
    BigInteger d = exprPoly.toBigDenominator(); // .toJavaBigInteger();
    BigRational nr = new BigRational(n);
    BigRational dr = new BigRational(d);
    BigRational r = nr.divide(dr);
    return new GenPolynomial(fPolyFactory, r);
  }

  private GenPolynomial<ModLong> expr2IExprPoly(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      GenPolynomial<ModLong> result = fPolyFactory.getZERO();
      GenPolynomial<ModLong> p = fPolyFactory.getZERO();
      if (ast.isPlus()) {
        IExpr expr = ast.arg1();
        result = expr2IExprPoly(expr);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2IExprPoly(expr);
          result = result.sum(p);
        }
        return result;
      } else if (ast.isTimes()) {
        IExpr expr = ast.arg1();
        result = expr2IExprPoly(expr);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2IExprPoly(expr);
          result = result.multiply(p);
        }
        return result;
      } else if (ast.isPower()) {
        final IExpr base = ast.base();
        for (int i = 0; i < fVariables.size(); i++) {
          if (fVariables.get(i).equals(base)) {
            int exponent = ast.exponent().toIntDefault();
            if (exponent < 0) {
              throw new ArithmeticException(
                  "JASConvert:expr2Poly - invalid exponent: " + ast.exponent().toString());
            }
            ExpVector e = ExpVector.create(fVariables.size(), i, exponent);
            return fPolyFactory.getONE().multiply(e);
          }
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      for (int i = 0; i < fVariables.size(); i++) {
        if (fVariables.get(i).equals(exprPoly)) {
          ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
          return fPolyFactory.getONE().multiply(e);
        }
      }
      return new GenPolynomial(fPolyFactory, exprPoly);
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory.fromInteger(
          (java.math.BigInteger) exprPoly.asType(java.math.BigInteger.class));
    } else if (exprPoly instanceof IFraction) {
      return fraction2Poly((IFraction) exprPoly);
    }
    if (exprPoly.isFree(t -> fVariables.contains(t), true)) {
      return new GenPolynomial(fPolyFactory, exprPoly);
    } else {
      for (int i = 0; i < fVariables.size(); i++) {
        if (fVariables.get(i).equals(exprPoly)) {
          ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
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
    long lExp;
    ExpVector leer = fPolyFactory.evzero;
    if (!coeff.isOne()) {
      monomTimes.append(coeff);
    }
    for (int i = 0; i < exp.length(); i++) {
      lExp = exp.getVal(i);
      if (lExp != 0) {
        int ix = leer.varIndex(i);
        if (ix >= 0) {
          if (lExp == 1L) {
            monomTimes.append(fVariables.get(ix));
          } else {
            monomTimes.append(F.Power(fVariables.get(ix), F.ZZ(lExp)));
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  public static ModLongRing option2ModLongRing(IReal option) throws ArithmeticException {
    // maybe throw ArithmeticException
    long longValue = option.toLong();
    final BigInteger value = BigInteger.valueOf(longValue);
    return new ModLongRing(longValue, value.isProbablePrime(32));
  }
}
