package org.matheclipse.core.polynomials;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.JASQuotient;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.polynomials.longexponent.ExprTermOrder;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.gb.EGroebnerBaseSeq;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gbufd.GroebnerBasePartial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;

/**
 * The Groebner basis computations behind the <code>GroebnerBasis</code> function, one for each
 * coefficient domain: the field of rational functions in the symbols which are not variables, a
 * finite field <code>GF(p)</code>, and the integers. The elimination ideal has its own entry
 * point.
 *
 * <p>
 * Each of them converts the expressions into JAS polynomials, runs a JAS Buchberger algorithm and
 * converts the basis back; none of them evaluates arguments or reads options.
 */
public class GroebnerBasisJAS {

  private GroebnerBasisJAS() {}

  /**
   * The Groebner basis over the field the coefficients live in: the rational functions of the
   * symbols which are not variables, or the rational numbers when there are none.
   *
   * @return <code>F.NIL</code> if no valid expression can be returned
   */
  public static IAST basis(IAST listOfPolynomials, IAST listOfVariables, TermOrder termOrder) {
    IAST parameters = JASQuotient.parametersOf(listOfPolynomials, listOfVariables);
    if (parameters.argSize() > 0) {
      IAST quotientBasis = quotientBasis(listOfPolynomials, listOfVariables, parameters, termOrder);
      if (quotientBasis.isPresent()) {
        return quotientBasis;
      }
    }
    return exprPolynomialBasis(listOfPolynomials, listOfVariables, termOrder);
  }

  /**
   * The Groebner basis of polynomials whose coefficients are rational functions of
   * <code>parameters</code>, computed in the field those functions form, or {@link F#NIL} if they
   * are not rational functions of them.
   *
   * <p>
   * A parameter is not a number, and arithmetic on it as an expression does not always reach zero:
   * <code>GroebnerBasis({a*x^2 + 5*x - 1, 2*x + 3*x*y + y^2}, {x, y})</code> answered
   * <code>{1}</code>, the basis of the whole ring, because a coefficient which cancels only after
   * the fractions are put over a common denominator was taken for a nonzero leading term. In
   * <code>Q(a)[x, y]</code> the same computation is exact.
   */
  private static IAST quotientBasis(IAST listOfPolynomials, IAST listOfVariables, IAST parameters,
      TermOrder termOrder) {
    EvalEngine engine = EvalEngine.get();
    try {
      JASQuotient jas = new JASQuotient(listOfVariables, parameters, termOrder);
      ExprPolynomialRing exprRing =
          new ExprPolynomialRing(listOfVariables, new ExprTermOrder(termOrder.getEvord()));

      List<GenPolynomial<Quotient<BigRational>>> polyList =
          new ArrayList<GenPolynomial<Quotient<BigRational>>>(listOfPolynomials.argSize());
      for (int i = 1; i <= listOfPolynomials.argSize(); i++) {
        IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
        GenPolynomial<Quotient<BigRational>> poly =
            jas.expr2JAS(exprRing.create(expr, false, true, true));
        if (poly == null) {
          return F.NIL;
        }
        if (!poly.isZERO()) {
          polyList.add(poly);
        }
      }
      if (polyList.isEmpty()) {
        return F.NIL;
      }

      GroebnerBaseSeq<Quotient<BigRational>> gb = new GroebnerBaseSeq<Quotient<BigRational>>();
      List<GenPolynomial<Quotient<BigRational>>> basis = gb.GB(polyList);
      IASTAppendable resultList = F.ListAlloc(basis.size());
      for (int i = 0; i < basis.size(); i++) {
        // the basis is written without denominators, as the rational one is
        IExpr poly = engine
            .evaluate(F.Expand(F.Numerator(F.Together(jas.quotientPoly2Expr(basis.get(i))))));
        if (poly.isZero()) {
          return F.NIL;
        }
        resultList.append(normalizeBasisElement(poly, engine));
      }
      return (IAST) engine.evaluate(F.Sort(resultList));
    } catch (JASConversionException | ClassCastException | ArithmeticException ex) {
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** The element with its numeric factors dropped. */
  private static IExpr normalizeBasisElement(IExpr poly, EvalEngine engine) {
    IExpr result = engine.evaluate(F.Factor(poly));
    if (result.isTimes()) {
      IASTAppendable rest = F.TimesAlloc(result.argSize());
      for (int i = 1; i <= ((IAST) result).argSize(); i++) {
        IExpr factor = ((IAST) result).get(i);
        if (!factor.isNumber()) {
          rest.append(factor);
        }
      }
      if (rest.argSize() > 0) {
        result = engine.evaluate(F.Expand(rest.oneIdentity1()));
      }
    }
    return result;
  }

  /**
   * @param listOfPolynomials a list of polynomials
   * @param listOfVariables a list of variable symbols
   * @param termOrder the term order
   * @return <code>F.NIL</code> if no valid expression can be returned
   */
  private static IAST exprPolynomialBasis(IAST listOfPolynomials, IAST listOfVariables,
      TermOrder termOrder) {
    String[] pvars = new String[listOfVariables.argSize()];
    for (int i = 1; i < listOfVariables.size(); i++) {
      if (!listOfVariables.get(i).isSymbol()) {
        return F.NIL;
      }
      pvars[i - 1] = listOfVariables.get(i).toString();
    }
    List<IExpr> varList = listOfVariables.asArgsList();

    List<GenPolynomial<IExpr>> polyList =
        new ArrayList<GenPolynomial<IExpr>>(listOfPolynomials.argSize());

    JASIExpr jas = new JASIExpr(varList, ExprRingFactory.CONST_FIELD, termOrder, false);
    ExprPolynomialRing ring =
        new ExprPolynomialRing(listOfVariables, new ExprTermOrder(termOrder.getEvord()));

    IASTAppendable rest = F.ListAlloc(listOfPolynomials.argSize());

    for (int i = 1; i < listOfPolynomials.size(); i++) {
      IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
      try {
        GenPolynomial<IExpr> poly = jas.expr2IExprJAS(ring.create(expr, false, true, true));
        if (poly == null) {
          rest.append(expr);
        } else {
          polyList.add(poly);
        }
      } catch (RuntimeException e) {
        rest.append(expr);
      }
    }

    if (polyList.size() == 0) {
      // nothing was a polynomial in the given variables: stay unevaluated instead of handing
      // back the input as if it were a basis
      return F.NIL;
    }

    GroebnerBasePartial<IExpr> gbp = new GroebnerBasePartial<IExpr>();
    OptimizedPolynomialList<IExpr> opl = gbp.partialGB(polyList, pvars);
    List<GenPolynomial<IExpr>> list = OrderedPolynomialList.sort(opl.list);

    IASTAppendable resultList = F.ListAlloc(list.size() + rest.argSize());

    for (int i = 0; i < list.size(); i++) {
      GenPolynomial<IExpr> p = list.get(i);

      java.math.BigInteger lcm = java.math.BigInteger.ONE;
      boolean allRationals = true;

      // 1. Find the LCM of all denominators to clear fractions
      for (Monomial<IExpr> monomial : p) {
        IExpr coeff = monomial.coefficient();
        if (coeff instanceof IFraction) {
          java.math.BigInteger denom = ((IFraction) coeff).toBigDenominator();
          java.math.BigInteger gcdVal = lcm.gcd(denom);
          lcm = lcm.multiply(denom.divide(gcdVal));
        } else if (!(coeff instanceof IInteger)) {
          allRationals = false;
          break;
        }
      }

      // 2. Perform normalization ONLY if all coefficients are standard rationals/integers
      if (allRationals) {
        // Clear fractional denominators
        if (!lcm.equals(java.math.BigInteger.ONE)) {
          p = p.multiply(F.ZZ(lcm));
        }

        // Find GCD to make the integer polynomial primitive
        java.math.BigInteger gcd = java.math.BigInteger.ZERO;
        for (Monomial<IExpr> monomial : p) {
          IExpr coeff = monomial.coefficient();
          if (coeff instanceof IInteger) {
            gcd = gcd.gcd(((IInteger) coeff).toBigNumerator());
          } else if (coeff instanceof IFraction) {
            gcd = gcd.gcd(((IFraction) coeff).toBigNumerator());
          }
        }

        // Divide by the GCD
        if (!gcd.equals(java.math.BigInteger.ONE) && !gcd.equals(java.math.BigInteger.ZERO)) {
          p = p.multiply(F.fraction(java.math.BigInteger.ONE, gcd));
        }

        // Ensure the leading coefficient is strictly positive
        if (p.leadingBaseCoefficient().signum() < 0) {
          p = p.multiply(F.CN1);
        }
      }

      // Convert the normalized Groebner Basis back to IExpr format
      resultList.append(jas.exprPoly2Expr(p));
    }

    // Append the passed-through non-polynomials
    resultList.appendArgs(rest);

    return resultList;
  }

  /**
   * Compute the Groebner basis over the finite field <code>GF(p)</code> instead of over the
   * rational numbers.
   *
   * <p>
   * Reducing a rational Groebner basis modulo <code>p</code> afterwards would not do: over
   * <code>GF(2)</code> the ideal generated by <code>{x^2+y^2-1, x-y}</code> is the whole ring
   * (basis <code>{1}</code>) although it is a proper ideal over the rationals, so the basis has
   * to be computed in <code>GF(p)[variables]</code> from the start.
   *
   * @param listOfPolynomials a list of polynomials
   * @param listOfVariables a list of variable symbols
   * @param termOrder the term order
   * @param modLongRing the coefficient field <code>GF(p)</code>
   * @return <code>F.NIL</code> if no valid expression can be returned
   */
  public static IAST modularBasis(IAST listOfPolynomials, IAST listOfVariables,
      TermOrder termOrder, ModLongRing modLongRing) {
    String[] pvars = new String[listOfVariables.argSize()];
    for (int i = 1; i < listOfVariables.size(); i++) {
      if (!listOfVariables.get(i).isSymbol()) {
        return F.NIL;
      }
      pvars[i - 1] = listOfVariables.get(i).toString();
    }

    JASModInteger jas = new JASModInteger(listOfVariables, modLongRing, termOrder);
    List<GenPolynomial<ModLong>> polyList =
        new ArrayList<GenPolynomial<ModLong>>(listOfPolynomials.argSize());

    // collect non-polynomial expressions to pass them through, like the rational case does
    IASTAppendable rest = F.ListAlloc(listOfPolynomials.argSize());

    for (int i = 1; i < listOfPolynomials.size(); i++) {
      IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
      try {
        GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
        if (poly == null) {
          rest.append(expr);
        } else {
          polyList.add(poly);
        }
      } catch (JASConversionException e) {
        rest.append(expr);
      }
    }

    if (polyList.size() == 0) {
      // nothing was a polynomial in the given variables: stay unevaluated
      return F.NIL;
    }

    List<GenPolynomial<ModLong>> list;
    try {
      GroebnerBasePartial<ModLong> gbp = new GroebnerBasePartial<ModLong>();
      OptimizedPolynomialList<ModLong> opl = gbp.partialGB(polyList, pvars);
      list = OrderedPolynomialList.sort(opl.list);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }

    IASTAppendable resultList = F.ListAlloc(list.size() + rest.argSize());
    for (int i = 0; i < list.size(); i++) {
      GenPolynomial<ModLong> p = list.get(i);
      if (p.isZERO()) {
        // a generator which vanishes modulo p contributes nothing to the ideal
        continue;
      }
      ModLong lc = p.leadingBaseCoefficient();
      if (!lc.isONE()) {
        // GF(p) is a field, so every basis element can be normalized to a monic one
        p = p.multiply(lc.inverse());
      }
      resultList.append(jas.modLongPoly2Expr(p));
    }

    // append the non-polynomials to the final basis
    resultList.appendArgs(rest);

    return resultList;
  }

  /**
   * The Groebner basis of the ideal <code>listOfPolynomials</code> generates, with the variables
   * of <code>eliminatedVariables</code> eliminated: only the basis elements free of them are
   * returned, which is the basis of the elimination ideal in the other variables.
   */
  public static IAST eliminationBasis(IAST listOfPolynomials, IAST listOfVariables,
      IAST eliminatedVariables, TermOrder termOrder) {
    IASTAppendable allVariables = F.ListAlloc(listOfVariables.argSize()
        + eliminatedVariables.argSize());
    allVariables.appendArgs(listOfVariables);
    allVariables.appendArgs(eliminatedVariables);
    String[] pvars = new String[listOfVariables.argSize()];
    for (int i = 1; i <= listOfVariables.argSize(); i++) {
      pvars[i - 1] = listOfVariables.get(i).toString();
    }
    String[] evars = new String[eliminatedVariables.argSize()];
    for (int i = 1; i <= eliminatedVariables.argSize(); i++) {
      evars[i - 1] = eliminatedVariables.get(i).toString();
    }

    JASIExpr jas =
        new JASIExpr(allVariables.asArgsList(), ExprRingFactory.CONST_FIELD, termOrder, false);
    ExprPolynomialRing ring =
        new ExprPolynomialRing(allVariables, new ExprTermOrder(termOrder.getEvord()));
    List<GenPolynomial<IExpr>> polyList =
        new ArrayList<GenPolynomial<IExpr>>(listOfPolynomials.argSize());
    for (int i = 1; i <= listOfPolynomials.argSize(); i++) {
      IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
      try {
        GenPolynomial<IExpr> poly = jas.expr2IExprJAS(ring.create(expr, false, true, true));
        if (poly == null) {
          return F.NIL;
        }
        polyList.add(poly);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
    }

    GroebnerBasePartial<IExpr> gbp = new GroebnerBasePartial<IExpr>();
    OptimizedPolynomialList<IExpr> opl = gbp.elimPartialGB(polyList, evars, pvars);
    List<GenPolynomial<IExpr>> list = OrderedPolynomialList.sort(opl.list);
    IASTAppendable resultList = F.ListAlloc(list.size());
    for (int i = 0; i < list.size(); i++) {
      IExpr poly = jas.exprPoly2Expr(list.get(i));
      if (poly.isFree(x -> eliminatedVariables.indexOf(x) > 0, true)) {
        resultList.append(F.evalExpandAll(poly));
      }
    }
    return resultList;
  }

  /**
   * The strong Groebner basis over the integers, or {@link F#NIL} if a coefficient is not one.
   */
  public static IAST integerBasis(IAST listOfPolynomials, IAST listOfVariables,
      TermOrder termOrder) {
    JASConvert<edu.jas.arith.BigInteger> jas = new JASConvert<edu.jas.arith.BigInteger>(
        listOfVariables, edu.jas.arith.BigInteger.ONE, termOrder);
    List<GenPolynomial<edu.jas.arith.BigInteger>> polyList =
        new ArrayList<GenPolynomial<edu.jas.arith.BigInteger>>(listOfPolynomials.argSize());
    for (int i = 1; i <= listOfPolynomials.argSize(); i++) {
      IExpr expr = F.evalExpandAll(listOfPolynomials.get(i));
      try {
        GenPolynomial<edu.jas.arith.BigInteger> poly = jas.expr2JAS(expr, false);
        if (poly == null) {
          return F.NIL;
        }
        polyList.add(poly);
      } catch (JASConversionException jce) {
        return F.NIL;
      }
    }
    EGroebnerBaseSeq<edu.jas.arith.BigInteger> gb =
        new EGroebnerBaseSeq<edu.jas.arith.BigInteger>();
    List<GenPolynomial<edu.jas.arith.BigInteger>> basis = gb.GB(polyList);
    basis = OrderedPolynomialList.sort(basis);
    IASTAppendable resultList = F.ListAlloc(basis.size());
    for (int i = 0; i < basis.size(); i++) {
      GenPolynomial<edu.jas.arith.BigInteger> poly = basis.get(i);
      if (poly.isZERO()) {
        continue;
      }
      if (poly.leadingBaseCoefficient().signum() < 0) {
        poly = poly.negate();
      }
      resultList.append(jas.integerPoly2Expr(poly));
    }
    return resultList;
  }

  /**
   * The JAS term order a <code>MonomialOrder</code> option asks for: one of the named orders, as
   * a symbol or as the string of the same name, or a matrix of weight vectors. <code>null</code>
   * if it is none of them.
   */
  public static TermOrder termOrder(IExpr orderSpec, TermOrder defaultOrder) {
    if (orderSpec.isString()) {
      return JASIExpr.monomialOrder(F.symbol(orderSpec.toString()), null);
    }
    if (orderSpec.isListOfLists()) {
      // A matrix of weight vectors is an order for reading a polynomial off, but not one
      // Buchberger's algorithm terminates under here, so it is declined rather than hung on.
      return null;
    }
    return orderSpec.isSymbol() ? JASIExpr.monomialOrder(orderSpec, null) : defaultOrder;
  }
}
