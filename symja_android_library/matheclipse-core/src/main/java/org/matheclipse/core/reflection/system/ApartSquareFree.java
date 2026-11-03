package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASModular;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigRational;
import edu.jas.arith.Modular;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.Power;

/**
 * <code>ApartSquareFree(expr)</code> separates a rational expression along the factors its
 * denominator is written with: these bases are not factored any further, so that
 * <code>1/(x^2-1)</code> stays and <code>1/((x-1)*(x+2)^2)</code> is split. With
 * <code>Modulus-&gt;p</code> the bases are the square-free parts of the denominator over
 * <code>GF(p)</code>.
 */
public class ApartSquareFree extends AbstractFunctionOptionEvaluator {

  /**
   * The polynomial part of <code>numerator / Product(base^exponent)</code> and a term
   * <code>c/base^k</code> for each base and each power up to its exponent, with
   * <code>deg(c)&lt;deg(base)</code>.
   *
   * @param numerator the numerator
   * @param bases the pairwise coprime factors of the denominator with their exponents
   * @param toExpr converts a polynomial into an expression
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the bases are not coprime or the degree is too large
   */
  private static <C extends GcdRingElem<C>> IExpr partialFractions(GenPolynomial<C> numerator,
      Map<GenPolynomial<C>, Long> bases, Function<GenPolynomial<C>, IExpr> toExpr,
      EvalEngine engine) {
    GenPolynomial<C> denominator = numerator.ring.getONE();
    for (Map.Entry<GenPolynomial<C>, Long> entry : bases.entrySet()) {
      if (entry.getKey().degree() * entry.getValue() > Config.MAX_POLYNOMIAL_DEGREE) {
        return F.NIL;
      }
      denominator = denominator.multiply(Power.positivePower(entry.getKey(), entry.getValue()));
    }
    GenPolynomial<C>[] division = numerator.quotientRemainder(denominator);
    IASTAppendable terms = F.PlusAlloc(bases.size() + 1);
    terms.append(toExpr.apply(division[0]));
    if (division[1].isZERO()) {
      return engine.evaluate(terms);
    }
    try {
      for (Map.Entry<GenPolynomial<C>, Long> entry : bases.entrySet()) {
        engine.checkTimeLimit();
        GenPolynomial<C> base = entry.getKey();
        GenPolynomial<C> power = Power.positivePower(base, entry.getValue());
        GenPolynomial<C> rest = denominator.divide(power).modInverse(power);
        GenPolynomial<C> part = division[1].multiply(rest).remainder(power);
        // part is the sum over c(k)*base^(exponent-k), which divides into c(k)/base^k
        for (long k = entry.getValue(); !part.isZERO(); k--) {
          GenPolynomial<C>[] digits = part.quotientRemainder(base);
          if (!digits[1].isZERO()) {
            terms.append(F.Divide(toExpr.apply(digits[1]), F.Power(toExpr.apply(base), F.ZZ(k))));
          }
          part = digits[0];
        }
      }
    } catch (NotInvertibleException nie) {
      return F.NIL;
    }
    return engine.evaluate(terms);
  }

  /**
   * The partial fractions of a rational expression in one variable over <code>GF(p)</code>.
   *
   * @param expr the expression
   * @param modulus the value of the <code>Modulus</code> option
   * @param squareFree the denominator is split into its square-free parts only and not into its
   *        irreducible factors
   * @param head the function which prints a message for an invalid modulus
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the expression has no such form
   */
  public static IExpr apartModulus(IExpr expr, IExpr modulus, boolean squareFree,
      IBuiltInSymbol head, EvalEngine engine) {
    if (JASModular.isModpMessage(head, modulus)) {
      return F.NIL;
    }
    VariablesSet variables = new VariablesSet(expr);
    if (!variables.isSize(1)) {
      return F.NIL;
    }
    IExpr together = engine.evaluate(F.Together(expr));
    Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(together, false);
    IExpr numerator = parts.isPresent() ? parts.get()[0] : together;
    IExpr denominator = parts.isPresent() ? parts.get()[1] : F.C1;
    try {
      return apartModulus(JASModular.of((IInteger) modulus, variables.getVarList()), numerator,
          denominator, squareFree, engine);
    } catch (JASConversionException | ArithmeticException e) {
      return F.NIL;
    }
  }

  private static <C extends GcdRingElem<C> & Modular> IExpr apartModulus(JASModular<C> jas,
      IExpr numerator, IExpr denominator, boolean squareFree, EvalEngine engine) {
    GenPolynomial<C> n = jas.expr2JAS(numerator);
    GenPolynomial<C> d = jas.expr2JAS(denominator);
    if (d.isZERO()) {
      return F.NIL;
    }
    GenPolynomial<C> gcd = jas.gcd().gcd(n, d);
    n = n.divide(gcd).multiply(d.divide(gcd).leadingBaseCoefficient().inverse());
    d = d.divide(gcd).monic();
    Map<GenPolynomial<C>, Long> bases = new LinkedHashMap<GenPolynomial<C>, Long>();
    if (!d.isConstant()) {
      for (Map.Entry<GenPolynomial<C>, Long> entry : jas.factors(d, squareFree).entrySet()) {
        if (!entry.getKey().isConstant()) {
          bases.put(entry.getKey(), entry.getValue());
        }
      }
    }
    return partialFractions(n, bases, jas::poly2Expr, engine);
  }

  /** The bases of the factors of a denominator and their exponents, constants are left out. */
  private static List<IExpr[]> writtenFactors(IExpr denominator) {
    List<IExpr[]> factors = new ArrayList<IExpr[]>();
    for (IExpr factor : denominator.isTimes() ? (IAST) denominator : F.List(denominator)) {
      if (factor.isNumber()) {
        continue;
      }
      if (factor.isPower() && factor.exponent().isInteger() && factor.exponent().isPositive()) {
        factors.add(new IExpr[] {factor.base(), factor.exponent()});
      } else {
        factors.add(new IExpr[] {factor, F.C1});
      }
    }
    return factors;
  }

  private static IExpr apartWrittenFactors(IExpr expr, IExpr variable, EvalEngine engine) {
    Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(expr, false);
    if (parts.isEmpty()) {
      return expr;
    }
    JASConvert<BigRational> jas = new JASConvert<BigRational>(F.List(variable), BigRational.ZERO);
    try {
      GenPolynomial<BigRational> numerator = jas.expr2JAS(parts.get()[0], false);
      // the numbers of the denominator are moved up into the numerator
      GenPolynomial<BigRational> product = jas.expr2JAS(parts.get()[1], false);
      if (numerator == null || product == null) {
        return expr;
      }
      Map<GenPolynomial<BigRational>, Long> bases =
          new LinkedHashMap<GenPolynomial<BigRational>, Long>();
      for (IExpr[] factor : writtenFactors(parts.get()[1])) {
        GenPolynomial<BigRational> base = jas.expr2JAS(factor[0], false);
        if (base == null) {
          return expr;
        }
        if (!base.isConstant()) {
          // x-1 and 1-x are the same base
          base = base.leadingBaseCoefficient().signum() < 0 ? base.negate() : base;
          bases.merge(base, factor[1].toLongDefault(), Long::sum);
        }
      }
      if (bases.isEmpty()) {
        return expr;
      }
      GenPolynomial<BigRational> written = product.ring.getONE();
      for (Map.Entry<GenPolynomial<BigRational>, Long> entry : bases.entrySet()) {
        written = written.multiply(Power.positivePower(entry.getKey(), entry.getValue()));
      }
      numerator = numerator.divide(product.divide(written));
      return partialFractions(numerator, bases, p -> jas.rationalPoly2Expr(p, false), engine)
          .orElse(expr);
    } catch (JASConversionException | ArithmeticException e) {
      return expr;
    }
  }

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr expr = ast.arg1();
    if (!options[0].isZero()) {
      return apartModulus(expr, options[0], true, S.ApartSquareFree, engine);
    }
    if (expr.isPlus()) {
      expr = engine.evaluate(F.Together(expr));
    }
    if (argSize == 2) {
      return ast.arg2().isVariable() ? apartWrittenFactors(expr, ast.arg2(), engine) : F.NIL;
    }
    VariablesSet variables = new VariablesSet(expr);
    return variables.isSize(1) ? apartWrittenFactors(expr, variables.getVarList().arg1(), engine)
        : expr;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
    setOptions(newSymbol, S.Modulus, F.C0);
  }
}
