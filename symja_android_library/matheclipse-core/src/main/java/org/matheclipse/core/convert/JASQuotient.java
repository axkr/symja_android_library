package org.matheclipse.core.convert;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;

/**
 * Convert <code>IExpr</code> polynomials whose coefficients contain symbols other than the
 * variables into JAS polynomials over the field <code>Q(parameters)</code> of rational functions in
 * those symbols, and back.
 *
 * <p>
 * The alternative is to compute with {@link IExpr} coefficients, where every coefficient operation
 * is a full evaluation and no intermediate coefficient is normalized: they grow as nested
 * unsimplified fractions, and a coefficient which cancels only over a common denominator is not
 * recognized as zero. A {@link Quotient} keeps every coefficient as a normalized
 * numerator/denominator pair over a real field, which makes the computation both faster and exact.
 *
 * @see JASConvert
 * @see JASIExpr
 */
public class JASQuotient {

  private final IAST variables;

  /** Converts the coefficients, which are polynomials in the parameters. */
  private final JASConvert<BigRational> parameterJAS;

  private final QuotientRing<BigRational> coefficientRing;

  private final GenPolynomialRing<Quotient<BigRational>> polyRing;

  /**
   * @param variables the variables of the polynomials
   * @param parameters the symbols the coefficients are rational functions of
   */
  public JASQuotient(IAST variables, IAST parameters) {
    this(variables, parameters, null);
  }

  /**
   * @param variables the variables of the polynomials
   * @param parameters the symbols the coefficients are rational functions of
   * @param termOrder the term order of the polynomial ring, or <code>null</code> for the default
   */
  public JASQuotient(IAST variables, IAST parameters, TermOrder termOrder) {
    this.variables = variables;
    this.parameterJAS = new JASConvert<BigRational>(parameters, BigRational.ZERO);
    this.coefficientRing =
        new QuotientRing<BigRational>(parameterJAS.getPolynomialRingFactory());
    String[] variableNames = new String[variables.argSize()];
    for (int i = 1; i <= variables.argSize(); i++) {
      variableNames[i - 1] = variables.get(i).toString();
    }
    this.polyRing = termOrder == null
        ? new GenPolynomialRing<Quotient<BigRational>>(coefficientRing, variableNames.length,
            variableNames)
        : new GenPolynomialRing<Quotient<BigRational>>(coefficientRing, variableNames.length,
            termOrder, variableNames);
  }

  /**
   * The symbols of <code>expression</code> which are not variables, in the order
   * {@link VariablesSet} lists them.
   */
  public static IAST parametersOf(IExpr expression, IAST variables) {
    IAST symbols = new VariablesSet(expression).getVarList();
    IASTAppendable parameters = F.ListAlloc(symbols.argSize());
    for (int i = 1; i <= symbols.argSize(); i++) {
      if (variables.indexOf(symbols.get(i)) <= 0) {
        parameters.append(symbols.get(i));
      }
    }
    return parameters;
  }

  public GenPolynomialRing<Quotient<BigRational>> getPolynomialRing() {
    return polyRing;
  }

  public QuotientRing<BigRational> getCoefficientRing() {
    return coefficientRing;
  }

  /**
   * Convert a polynomial with {@link IExpr} coefficients into one whose coefficients are rational
   * functions of the parameters.
   *
   * @return <code>null</code> if a coefficient is not a rational function of the parameters
   */
  public GenPolynomial<Quotient<BigRational>> expr2JAS(ExprPolynomial polynomial) {
    GenPolynomial<Quotient<BigRational>> result = polyRing.getZERO();
    for (ExprMonomial monomial : polynomial) {
      Quotient<BigRational> coefficient = expr2Quotient(monomial.coefficient());
      if (coefficient == null) {
        return null;
      }
      result = result.sum(coefficient, ExpVector.create(monomial.exponent().getVal()));
    }
    return result;
  }

  /**
   * Convert a coefficient into a normalized quotient of polynomials in the parameters.
   *
   * @return <code>null</code> if it is not a rational function of them
   */
  public Quotient<BigRational> expr2Quotient(IExpr coefficient) {
    EvalEngine engine = EvalEngine.get();
    IExpr together = engine.evaluate(F.Together(coefficient));
    try {
      GenPolynomial<BigRational> numerator =
          parameterJAS.expr2JAS(engine.evaluate(F.Numerator(together)), false);
      GenPolynomial<BigRational> denominator =
          parameterJAS.expr2JAS(engine.evaluate(F.Denominator(together)), false);
      if (numerator == null || denominator == null || denominator.isZERO()) {
        return null;
      }
      return new Quotient<BigRational>(coefficientRing, numerator, denominator);
    } catch (JASConversionException jce) {
      return null;
    }
  }

  /** Convert a polynomial with rational function coefficients back into an {@link IExpr}. */
  public IExpr quotientPoly2Expr(GenPolynomial<Quotient<BigRational>> polynomial) {
    if (polynomial.isZERO()) {
      return F.C0;
    }
    EvalEngine engine = EvalEngine.get();
    IASTAppendable sum = F.PlusAlloc(polynomial.length());
    for (Monomial<Quotient<BigRational>> monomial : polynomial) {
      IASTAppendable term = F.TimesAlloc(variables.argSize() + 1);
      term.append(quotient2Expr(monomial.coefficient(), engine));
      ExpVector exponents = monomial.exponent();
      int length = exponents.length();
      for (int i = 1; i <= variables.argSize(); i++) {
        long exponent = exponents.getVal(length - i);
        if (exponent != 0L) {
          term.append(F.Power(variables.get(i), F.ZZ(exponent)));
        }
      }
      sum.append(term.oneIdentity1());
    }
    return engine.evaluate(sum.oneIdentity0());
  }

  /** A coefficient as the expression it came from. */
  private IExpr quotient2Expr(Quotient<BigRational> coefficient, EvalEngine engine) {
    IExpr numerator = parameterJAS.rationalPoly2Expr(coefficient.num, false);
    if (coefficient.den.isONE()) {
      return numerator;
    }
    IExpr result = F.Divide(numerator, parameterJAS.rationalPoly2Expr(coefficient.den, false));
    if (coefficient.den.length() == 1) {
      // A single denominator term divides every summand of the numerator without combining them,
      // so expanding keeps the shape the IExpr coefficient division produced: `-1/a^2+b/a` instead
      // of `(-1+a*b)/a^2`. For a denominator with more than one term expanding would only
      // distribute the same denominator over all summands.
      return engine.evaluate(F.Expand(result));
    }
    return result;
  }
}
