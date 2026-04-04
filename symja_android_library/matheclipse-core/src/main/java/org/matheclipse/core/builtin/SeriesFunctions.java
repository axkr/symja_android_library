package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.reflection.system.rulesets.SeriesCoefficientRules;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.QuotientTaylorFunction;

public class SeriesFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      if (ToggleFeature.SERIES) {
        S.ComposeSeries.setEvaluator(new ComposeSeries());
        S.InverseSeries.setEvaluator(new InverseSeries());
        S.PadeApproximant.setEvaluator(new PadeApproximant());
        S.Residue.setEvaluator(new Residue());
        S.Series.setEvaluator(new Series());
        S.SeriesCoefficient.setEvaluator(new SeriesCoefficient());
        S.SeriesData.setEvaluator(new SeriesData());
      }
    }
  }

  private static final class PadeApproximant extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg2().isList3()) {
        IExpr function = ast.arg1();
        IAST list = (IAST) ast.arg2();
        IExpr x = list.arg1();
        IExpr x0 = list.arg2();

        if (list.arg3().isList2()) {
          try {
            IAST order = (IAST) list.arg3();
            final int m = order.arg1().toIntDefault();
            if (F.isNotPresent(m)) {
              return F.NIL;
            }
            final int n = order.arg2().toIntDefault();
            if (F.isNotPresent(n)) {
              return F.NIL;
            }
            if (function.isTimes()) {
              Optional<IExpr[]> numeratorDenominatorParts =
                  AlgebraUtil.fractionalParts(function, false);
              if (numeratorDenominatorParts.isPresent()) {
                return quotientTaylorFunction(numeratorDenominatorParts.get(), x, x0, m, n);
              }
            }

            return taylorFunction(function, x, x0, m, n);
          } catch (ArithmeticException aex) {
            Errors.printRuntimeException(S.PadeApproximant, aex, engine);
          } catch (JASConversionException jce) {
            // could not use JAS library here
          }
        }

      }
      return F.NIL;
    }

    private static IExpr quotientTaylorFunction(IExpr[] numeratorDenominatorParts, IExpr x,
        IExpr x0, final int m, final int n) {

      UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<BigRational>(BigRational.ZERO);
      JASConvert<BigRational> jas = new JASConvert<BigRational>(x.makeList(), BigRational.ZERO);
      BigRational bf = null;
      if (x0.isRational()) {
        bf = ((IRational) x0).toBigRational();
      }
      if (bf == null) {
        return F.NIL;
      }
      GenPolynomial<BigRational> numerator = jas.expr2JAS(numeratorDenominatorParts[0], false);
      if (numerator == null) {
        return F.NIL;
      }
      GenPolynomial<BigRational> denominator = jas.expr2JAS(numeratorDenominatorParts[1], false);
      if (denominator == null) {
        return F.NIL;
      }
      GenPolynomialRing<BigRational> pr = fac.polyRing();
      QuotientRing<BigRational> qr = new QuotientRing<BigRational>(pr);
      Quotient<BigRational> p = new Quotient<BigRational>(qr, numerator, denominator);
      TaylorFunction<BigRational> TF = new QuotientTaylorFunction<BigRational>(p);
      Quotient<BigRational> approximantOfPade =
          PolyUfdUtil.<BigRational>approximantOfPade(fac, TF, bf, m, n);
      IAST numeratorExpr = jas.rationalPoly2Expr(approximantOfPade.num, false);
      IAST denominatorExpr = jas.rationalPoly2Expr(approximantOfPade.den, false);
      return org.matheclipse.core.expression.F.Divide(numeratorExpr, denominatorExpr);
    }


    private static IExpr taylorFunction(IExpr function, IExpr x, IExpr bf, int m, int n) {
      List<IExpr> varList = Lists.newArrayList(x);
      IASTAppendable list = F.ListAlloc(varList);
      if (bf.isRational()) {
        try {
          BigRational expansionPoint = ((IRational) bf).toBigRational();
          JASConvert<BigRational> jas = new JASConvert<BigRational>(list, BigRational.ZERO);
          GenPolynomial<BigRational> numerator = jas.expr2JAS(function, false);
          if (numerator != null) {
            TaylorFunction<BigRational> TF = new PolynomialTaylorFunction<BigRational>(numerator);
            UnivPowerSeriesRing<BigRational> fac =
                new UnivPowerSeriesRing<BigRational>(BigRational.ZERO);
            Quotient<BigRational> approximantOfPade =
                PolyUfdUtil.<BigRational>approximantOfPade(fac, TF, expansionPoint, m, n);
            IExpr numeratorExpr = jas.rationalPoly2Expr(approximantOfPade.num, false);
            IExpr denominatorExpr = jas.rationalPoly2Expr(approximantOfPade.den, false);
            return org.matheclipse.core.expression.F.Divide(numeratorExpr, denominatorExpr);
          }
        } catch (JASConversionException jce) {
          //
        }
      }
      JASIExpr jas = new JASIExpr(varList, true);
      ExprPolynomialRing ring = new ExprPolynomialRing(list);
      ExprPolynomial poly = ring.create(function);
      GenPolynomial<IExpr> numerator = jas.expr2IExprJAS(poly);
      TaylorFunction<IExpr> TF = new PolynomialTaylorFunction<IExpr>(numerator);
      UnivPowerSeriesRing<IExpr> fac = new UnivPowerSeriesRing<IExpr>(ExprRingFactory.CONST);
      Quotient<IExpr> approximantOfPade = PolyUfdUtil.<IExpr>approximantOfPade(fac, TF, bf, m, n);
      IExpr numeratorExpr = jas.exprPoly2Expr(approximantOfPade.num);
      IExpr denominatorExpr = jas.exprPoly2Expr(approximantOfPade.den);
      return org.matheclipse.core.expression.F.Divide(numeratorExpr, denominatorExpr);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }


  /**
   *
   *
   * <pre>
   * ComposeSeries(series1, series2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * substitute <code>series2</code> into <code>series1</code>
   *
   * </blockquote>
   *
   * <pre>
   * ComposeSeries(series1, series2, series3)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return multiple series composed.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)
   * x^2+3*x^3+O(x)^4
   * </pre>
   */
  private static final class ComposeSeries extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() > 2) {
        if (ast.arg1() instanceof ASTSeriesData) {
          ASTSeriesData result = (ASTSeriesData) ast.arg1();
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i) instanceof ASTSeriesData) {
              ASTSeriesData s2 = (ASTSeriesData) ast.get(i);
              result = result.compose(s2);
              if (result == null) {
                return F.NIL;
              }
            }
          }
          return result;
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   *
   *
   * <pre>
   * InverseSeries(series)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the inverse series.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; InverseSeries(Series(Sin(x), {x, 0, 7}))
   * x+x^3/6+3/40*x^5+5/112*x^7+O(x)^8
   * </pre>
   */
  /**
   * <pre>
   * InverseSeries(series)
   * </pre>
   * 
   * <blockquote>
   * <p>
   * return the inverse series.
   * </p>
   * </blockquote> *
   * 
   * <pre>
   * InverseSeries(f(x), x)
   * </pre>
   * 
   * <blockquote>
   * <p>
   * generates the inverse power series of the function f(x) using the Lagrange-Bürmann formula.
   * </p>
   * </blockquote>
   */
  private static final class InverseSeries extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      // 1. Invertierung eines bereits generierten ASTSeriesData Objekts
      if (ast.arg1() instanceof ASTSeriesData) {
        ASTSeriesData ps = (ASTSeriesData) ast.arg1();
        IExpr variable = ps.expansionVariable();
        if (ast.isAST2()) {
          variable = ast.arg2();
        }
        IExpr temp = ps.reversion(variable, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      // 2. Direkte analytische Invertierung einer Funktion f(x) via Lagrange-Bürmann
      else if (ast.isAST2() && ast.arg2().isSymbol()) {
        IExpr f = ast.arg1();
        IExpr x = ast.arg2();

        // prüfen, ob f(0) == 0 gilt
        IExpr f0 = engine.evaluate(F.subst(f, x, F.C0));
        if (f0.isZero()) {
          int order = 6;
          IASTAppendable coeffs = F.ListAlloc(order);

          for (int i = 1; i < order; i++) {
            IExpr c = ASTSeriesData.lagrangeBurmannCoefficient(f, x, F.C0, F.ZZ(i), engine);
            if (c.isPresent()) {
              coeffs.append(c);
            } else {
              return F.NIL;
            }
          }

          // Konstruiert exakt ein SeriesData Objekt bis zur Ordnung O(x^6)
          return new ASTSeriesData(x, F.C0, coeffs, 1, order, 1);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }
  }

  private static final class Residue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && (ast.arg2().isList2())) {
        IExpr function = ast.arg1();
        IAST list = (IAST) ast.arg2();

        IExpr x = list.arg1();
        IExpr x0 = list.arg2();
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return Errors.printMessage(S.Residue, "ivar", F.List(x), engine);
        }

        // Handle Residue at Infinity
        // Res(f(x), {x, Infinity}) = -Res(1/x^2 * f(1/x), {x, 0})
        if (x0.isDirectedInfinity()) {
          IExpr subFunc = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
          IExpr newFunc = engine.evaluate(F.Times(F.CN1, F.Power(x, F.CN2), subFunc));
          return engine.evaluate(F.Residue(newFunc, F.List(x, F.C0)));
        }

        // Fast-Path: Try direct evaluation via our optimized SeriesCoefficient
        IExpr coeff = engine.evaluate(F.SeriesCoefficient(function, F.List(x, x0, F.CN1)));
        if (coeff.isPresent() && !coeff.isAST(S.SeriesCoefficient)) {
          return coeff;
        }

        // Robust Fallback: Compute the Laurent series explicitly up to O((x-x0)^0)
        // and extract the precise -1 coefficient using ASTSeriesData properties.
        IExpr series = engine.evaluate(F.Series(function, F.List(x, x0, F.C0)));
        if (series instanceof ASTSeriesData) {
          ASTSeriesData seriesData = (ASTSeriesData) series;
          return engine.evaluate(F.Together(seriesData.coefficient(-1)));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   *
   * <pre>
   * Series(expr, {x, x0, n})
   * </pre>
   *
   * <blockquote>
   * <p>
   * create a power series of <code>expr</code> up to order <code>(x- x0)^n</code> at the point
   * <code>x = x0</code> </blockquote>
   */
  private static final class Series extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2) {
        IExpr currentExpr = ast.arg1();

        // Process iterators from LEFT to RIGHT to build the SeriesData hierarchy correctly!
        // Series(f, {x, x0, nx}, {y, y0, ny}) -> expands x first, then y.
        // This mathematically ensures x is the outer-most boundary.
        for (int i = 2; i <= ast.argSize(); i++) {
          IExpr iterator = ast.get(i);

          if (iterator.isList3()) {
            IAST list = (IAST) iterator;
            IExpr x = list.arg1();
            IExpr x0 = list.arg2();
            if (!x.isVariable()) {
              return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
            }
            final int n = list.arg3().toIntDefault();
            if (F.isNotPresent(n)) {
              return F.NIL;
            }
            if (currentExpr.isFree(x)) {
              continue; // Optimization: if free of x, series in x is the expression itself
            }

            // Handle ASTSeriesData multi-variate mapping natively
            if (currentExpr instanceof ASTSeriesData) {
              ASTSeriesData outerSeries = (ASTSeriesData) currentExpr;
              if (!outerSeries.expansionVariable().equals(x)) {
                // Multivariate: map the new series iterator over the coefficients
                IAST coefficients = outerSeries.arg3();
                IASTAppendable newCoeffs = F.ListAlloc(coefficients.argSize());
                for (int j = 1; j <= coefficients.argSize(); j++) {
                  IExpr coeffSeries = engine.evaluate(F.Series(coefficients.get(j), iterator));
                  newCoeffs.append(coeffSeries);
                }
                currentExpr = new ASTSeriesData(outerSeries.expansionVariable(),
                    outerSeries.expansionPoint(), newCoeffs, outerSeries.minExponent(),
                    outerSeries.truncateOrder(), outerSeries.puiseuxDenominator());
                continue;
              } else {
                // Re-expanding in the same variable
                if (outerSeries.expansionPoint().equals(x0)) {
                  int targetTruncate = n * outerSeries.puiseuxDenominator() + 1;
                  if (targetTruncate >= outerSeries.truncateOrder()) {
                    continue; // Cannot increase precision of existing series
                  } else {
                    int newNMin = Math.min(outerSeries.minExponent(), targetTruncate);
                    int capacity = targetTruncate - newNMin;
                    IASTAppendable newList = F.ListAlloc(capacity < 0 ? 1 : capacity + 1);
                    for (int j = newNMin; j < targetTruncate; j++) {
                      newList.append(outerSeries.coefficient(j));
                    }
                    currentExpr = new ASTSeriesData(x, x0, newList, newNMin, targetTruncate,
                        outerSeries.puiseuxDenominator());
                    continue;
                  }
                } else {
                  // Expanding at a different point! Flatten it first.
                  currentExpr = outerSeries.normal(false);
                }
              }
            }

            boolean isInfinity = x0.isDirectedInfinity();
            int direction = 0;
            if (x0.equals(F.CInfinity)) {
              direction = -1;
            } else if (x0.equals(F.CNInfinity)) {
              direction = 1;
            }

            IExpr seriesX0 = x0;
            IExpr seriesFunction = currentExpr;

            if (isInfinity) {
              seriesX0 = F.C0;
              seriesFunction = engine.evaluate(F.subst(currentExpr, x, F.Power(x, F.CN1)));
            }

            ASTSeriesData series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, n,
                direction, engine);

            if (series != null) {
              if (isInfinity) {
                currentExpr = new ASTSeriesData(x, x0, series.arg3(), series.minExponent(),
                    series.truncateOrder(), series.puiseuxDenominator());
              } else {
                currentExpr = series;
              }
            } else {
              // --- NEW FALLBACK HEURISTIC ---
              if (currentExpr.isAST() && x.isPresent()) {
                IAST astFunc = (IAST) currentExpr;
                IASTAppendable resultAST = F.ast(astFunc.head(), astFunc.argSize());
                boolean changed = false;
                for (int j = 1; j <= astFunc.argSize(); j++) {
                  IExpr arg = astFunc.get(j);
                  if (!arg.isFree(x)) {
                    IExpr argSeries = engine.evaluate(F.Series(arg, iterator));
                    if (argSeries.isPresent() && !argSeries.equals(arg)) {
                      resultAST.append(argSeries);
                      changed = true;
                    } else {
                      resultAST.append(arg);
                    }
                  } else {
                    resultAST.append(arg);
                  }
                }
                if (changed) {
                  currentExpr = engine.evaluate(resultAST);
                  continue;
                }
              }
              return F.NIL;
            }
          } else if (iterator.isRuleAST()) {
            IAST rule = (IAST) iterator;
            IExpr x = rule.arg1();
            IExpr x0 = rule.arg2();
            if (!x.isVariable()) {
              return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
            }
            if (currentExpr.isFree(x)) {
              continue;
            }

            if (currentExpr instanceof ASTSeriesData) {
              ASTSeriesData outerSeries = (ASTSeriesData) currentExpr;
              if (!outerSeries.expansionVariable().equals(x)) {
                IAST coefficients = outerSeries.arg3();
                IASTAppendable newCoeffs = F.ListAlloc(coefficients.argSize());
                for (int j = 1; j <= coefficients.argSize(); j++) {
                  IExpr coeffSeries = engine.evaluate(F.Series(coefficients.get(j), iterator));
                  newCoeffs.append(coeffSeries);
                }
                currentExpr = new ASTSeriesData(outerSeries.expansionVariable(),
                    outerSeries.expansionPoint(), newCoeffs, outerSeries.minExponent(),
                    outerSeries.truncateOrder(), outerSeries.puiseuxDenominator());
                continue;
              } else {
                currentExpr = outerSeries.normal(false);
              }
            }

            boolean isInfinity = x0.isInfinity() || x0.isNegativeInfinity();
            int direction = 0;
            if (x0.equals(F.CInfinity)) {
              direction = -1;
            } else if (x0.equals(F.CNInfinity)) {
              direction = 1;
            }

            IExpr seriesX0 = x0;
            IExpr seriesFunction = currentExpr;

            if (isInfinity) {
              seriesX0 = F.C0;
              seriesFunction = engine.evaluate(F.subst(currentExpr, x, F.Power(x, F.CN1)));
            }

            int currentN = 1;
            ASTSeriesData series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0,
                currentN, direction, engine);

            int probeLimit = 30;
            while (series != null && currentN < probeLimit) {
              boolean foundNonZero = false;
              if (!series.isOrder()) {
                for (int j = series.minExponent(); j < series.truncateOrder(); j++) {
                  if (!series.coefficient(j).isZero()) {
                    foundNonZero = true;
                    break;
                  }
                }
              }
              if (foundNonZero) {
                break;
              }
              currentN += 4;
              series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, currentN,
                  direction, engine);
            }

            if (series != null && !series.isOrder()) {
              int leadIndex = Integer.MAX_VALUE;
              for (int j = series.minExponent(); j < series.truncateOrder(); j++) {
                if (!series.coefficient(j).isZero()) {
                  leadIndex = j;
                  break;
                }
              }
              if (leadIndex != Integer.MAX_VALUE) {
                int nextIndex = -1;
                if (isInfinity || leadIndex < 0) {
                  for (int j = leadIndex + 1; j < series.truncateOrder(); j++) {
                    if (!series.coefficient(j).isZero()) {
                      nextIndex = j;
                      break;
                    }
                  }
                  if (nextIndex == -1) {
                    nextIndex = series.truncateOrder();
                  }
                }
                if (nextIndex == -1) {
                  nextIndex = leadIndex + series.puiseuxDenominator();
                }

                IASTAppendable coeffs = F.ListAlloc(2);
                coeffs.append(series.coefficient(leadIndex));

                if (isInfinity) {
                  currentExpr = new ASTSeriesData(x, x0, coeffs, leadIndex, nextIndex,
                      series.puiseuxDenominator());
                } else {
                  currentExpr = new ASTSeriesData(x, seriesX0, coeffs, leadIndex, nextIndex,
                      series.puiseuxDenominator());
                }
              } else {
                return F.NIL;
              }
            } else {
              if (currentExpr.isAST() && x.isPresent()) {
                IAST astFunc = (IAST) currentExpr;
                IASTAppendable resultAST = F.ast(astFunc.head(), astFunc.argSize());
                boolean changed = false;
                for (int j = 1; j <= astFunc.argSize(); j++) {
                  IExpr arg = astFunc.get(j);
                  if (!arg.isFree(x)) {
                    IExpr argSeries = engine.evaluate(F.Series(arg, iterator));
                    if (argSeries.isPresent() && !argSeries.equals(arg)) {
                      resultAST.append(argSeries);
                      changed = true;
                    } else {
                      resultAST.append(arg);
                    }
                  } else {
                    resultAST.append(arg);
                  }
                }
                if (changed) {
                  currentExpr = engine.evaluate(resultAST);
                  continue;
                }
              }
              return F.NIL;
            }
          } else {
            return F.NIL;
          }
        }
        return currentExpr;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }
  /**
   *
   *
   * <pre>
   * SeriesCoefficient(expr, {x, x0, n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the coefficient of <code>(x- x0)^n</code> at the point <code>x = x0</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SeriesCoefficient(Sin(x),{x,f+g,n})
   * Piecewise({{Sin(f+g+1/2*n*Pi)/n!,n&gt;=0}},0)
   * </pre>
   */
  public static final class SeriesCoefficient extends AbstractFunctionEvaluator {
    private static com.google.common.base.Supplier<Matcher> MATCHER1;

    private static Matcher matcher1() {
      return MATCHER1.get();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // SeriesCoefficient(expr, {x, 0, nx}, {y, 0, ny})
      // Evaluates sequentially from left to right.
      if (ast.argSize() > 2) {
        IExpr currentExpr = ast.arg1();
        for (int i = 2; i <= ast.argSize(); i++) {
          currentExpr = engine.evaluate(F.SeriesCoefficient(currentExpr, ast.get(i)));
          if (!currentExpr.isPresent()) {
            return F.NIL;
          }
        }
        return currentExpr;
      }

      if (ast.isAST2()) {
        if (ast.arg1() instanceof ASTSeriesData) {
          ASTSeriesData series = (ASTSeriesData) ast.arg1();

          // Case 1: SeriesCoefficient(series, n)
          if (ast.arg2().isInteger()) {
            int n = ast.arg2().toIntDefault();
            if (n >= 0) {
              int order = series.truncateOrder();
              if (order > n) {
                return series.coefficient(n);
              } else {
                return S.Indeterminate;
              }
            }
            return F.NIL;
          }

          // Case 2: SeriesCoefficient(series, {x, x0, n})
          if (ast.arg2().isList3()) {
            IAST list = (IAST) ast.arg2();
            IExpr x = list.arg1();
            IExpr x0 = list.arg2();
            IExpr nExpr = list.arg3();

            // Extract the coefficient if the variable and expansion point match
            if (series.expansionVariable().equals(x) && series.expansionPoint().equals(x0)) {
              if (nExpr.isInteger()) {
                int n = nExpr.toIntDefault();
                int order = series.truncateOrder();
                if (order > n) {
                  return series.coefficient(n);
                } else {
                  return S.Indeterminate;
                }
              }
            }
            return F.NIL;
          }
        }
        if (ast.arg1() instanceof ASTSeriesData && ast.arg2().isInteger()) {
          ASTSeriesData series = (ASTSeriesData) ast.arg1();
          int n = ast.arg2().toIntDefault();
          if (n >= 0) {
            int order = series.truncateOrder();
            if (order > n) {
              return series.coefficient(n);
            } else {
              return S.Indeterminate;
            }
          }
          return F.NIL;
        }
        if (ast.arg2().isList3() && !(ast.arg1() instanceof ASTSeriesData)) {
          IExpr matched = matcher1().apply(ast);
          if (matched.isPresent()) {
            return matched;
          }
          IExpr function = ast.arg1();

          IAST list = (IAST) ast.arg2();
          IExpr x = list.arg1();
          IExpr x0 = list.arg2();

          IExpr n = list.arg3();
          IExpr functionCoefficient = F.NIL;

          if (function.isAST()) {
            functionCoefficient = specialFunctionCoefficient((IAST) function, x, x0, n);
            if (functionCoefficient.isPresent()
                && functionCoefficient.isFree(f -> f.isIndeterminate(), true)) {
              return functionCoefficient;
            }
          }
          functionCoefficient = functionCoefficient(ast, function, x, x0, n, engine);
          if (functionCoefficient.isPresent()
              && functionCoefficient.isFree(f -> f.isIndeterminate(), true)) {
            return functionCoefficient;
          }
          // return matcher1().apply(ast);
        }
      }

      return F.NIL;
    }

    private static IExpr functionCoefficient(final IAST ast, IExpr function, IExpr x, IExpr x0,
        IExpr n, EvalEngine engine) {

      // Map Infinity evaluations to 0 by inverting x
      if (x0.isDirectedInfinity()) {
        // Wrap in F.Together to normalize nested fractions like 1/(1 - 1/x) -> x/(x - 1)
        IExpr subFunc = engine.evaluate(F.Together(F.subst(function, x, F.Power(x, F.CN1))));
        return functionCoefficient(ast, subFunc, x, F.C0, n, engine);
      }

      // Rational Function Expansion Fast-Path ---
      IExpr rationalCoeff = rationalSeriesCoefficient(function, x, x0, n, engine);
      if (rationalCoeff.isPresent()) {
        return rationalCoeff;
      }

      if (n.isReal()) {
        if (n.isFraction() && !((IFraction) n).denominator().isOne()) {
          return F.C0;
        }
        if (!n.isInteger()) {
          return F.NIL;
        }
      }
      if (function.isFree(x)) {
        if (n.isZero()) {
          return function;
        }
        return F.Piecewise(F.list(F.list(function, F.Equal(n, F.C0))), F.C0);
      }
      IExpr temp = polynomialSeriesCoefficient(function, x, x0, n, ast, engine);
      if (temp.isPresent()) {
        return temp;
      }
      // Lagrange-Bürmann Inversion Fast-Path
      if (function.isAST(S.InverseSeries)) {
        IExpr f = function.first();
        IExpr innerX = x;
        if (function.argSize() >= 2) {
          innerX = function.second();
        }
        IExpr lbCoeff = ASTSeriesData.lagrangeBurmannCoefficient(f, innerX, x0, n, engine);
        if (lbCoeff.isPresent()) {
          return lbCoeff;
        }
      } else if (function.isAST1() && function.head().isAST(S.InverseFunction)) {
        // SeriesCoefficient(InverseFunction(f)[x], {x, 0, n})
        IExpr fExpr = function.head().first();
        // Restrict to valid mathematical function heads to match Mathematica
        if (fExpr.isSymbol() || fExpr.isAST(S.Function)) {
          IExpr f = engine.evaluate(F.unaryAST1(fExpr, x));
          IExpr lbCoeff = ASTSeriesData.lagrangeBurmannCoefficient(f, x, x0, n, engine);
          if (lbCoeff.isPresent()) {
            return lbCoeff;
          }
        } else {
          return F.NIL;
        }
      }
      // Composition of Series Fast-Path (Faà di Bruno) ---
      IExpr compositeCoeff = compositeSeriesCoefficient(function, x, x0, n, engine);
      if (compositeCoeff.isPresent()) {
        return engine.evaluate(F.Expand(compositeCoeff));
      }
      if (n.isReal()) {
        if (n.isFraction() && !((IFraction) n).denominator().isOne()) {
          return F.C0;
        }
        if (!n.isInteger()) {
          return F.NIL;
        }
      }
      if (function.isFree(x)) {
        if (n.isZero()) {
          return function;
        }
        return F.Piecewise(F.list(F.list(function, F.Equal(n, F.C0))), F.C0);
      }
      if (function.isPower()) {
        IExpr b = function.base();
        IExpr exponent = function.exponent();
        if (b.equals(x)) {
          if (exponent.isNumber()) {
            // x^exp
            INumber exp = (INumber) exponent;
            if (exp.isInteger()) {
              if (x0.isZero()) {
                return F.Piecewise(F.list(F.list(F.C1, F.Equal(n, exp))), F.C0);
              }
              return F.Piecewise(
                  F.list(F.list(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
                      F.LessEqual(F.C0, n, exp))),
                  F.C0);
            }
          }
          if (!x0.isZero() && exponent.isFree(x)) {
            IExpr exp = exponent;
            return F.Piecewise(
                F.list(F.list(F.Times(F.Power(x0, F.Plus(exp, n.negate())), F.Binomial(exp, n)),
                    F.GreaterEqual(n, F.C0))),
                F.C0);
          }
        }
        if (b.isFree(x)) {
          IExpr[] linear = exponent.linear(x);
          if (linear != null) {
            if (x0.isZero()) {
              // b^(a+c*x)
              IExpr a = linear[0];
              IExpr c = linear[1];
              return
              // [$ Piecewise({{(b^a*(c*Log(b))^n)/n!, n >= 0}}, 0) $]
              F.Piecewise(F.list(F.list(F.Times(F.Power(b, a), F.Power(F.Factorial(n), F.CN1),
                  F.Power(F.Times(c, F.Log(b)), n)), F.GreaterEqual(n, F.C0))), F.C0); // $$;
            } else if (linear[0].isZero() && linear[1].isOne()) {
              // b^x with b is free of x

              return F.Piecewise(F.list(F.list(
                  F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
                  F.GreaterEqual(n, F.C0))), F.C0);
            }
          }
        } else if (b.equals(exponent) && x0.isZero()) {
          // x^x
          if (exponent.equals(x)) {
            // x^x or b^x with b is free of x

            return F.Piecewise(F.list(F.list(
                F.Times(F.Power(b, x0), F.Power(F.Factorial(n), F.CN1), F.Power(F.Log(b), n)),
                F.GreaterEqual(n, F.C0))), F.C0);
          }
        }
      }

      if (x0.isReal()) {
        final int x0Value = x0.toIntDefault();
        if (x0Value != 0) {
          return taylorCoefficient(function, x, x0, n, engine);
        }
        x0 = F.ZZ(x0Value);
      }

      return taylorCoefficient(function, x, x0, n, engine);
    }

    /**
     * Resolves the general series coefficients of simple rational functions using Partial Fraction
     * Decomposition and shifted Geometric Series boundary formulas, preventing O(n!) catastrophic
     * derivative evaluation.
     */
    private static IExpr rationalSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      // Simple heuristic check: Abort early if not a fraction
      IExpr den = engine.evaluate(F.Denominator(function));
      if (den.isOne() || den.isFree(x)) {
        return F.NIL;
      }

      // Decompose into simple fractions
      IExpr apart = engine.evaluate(F.Apart(function, x));

      if (apart.isPlus()) {
        IAST apartPlus = (IAST) apart;
        Map<IExpr, IASTAppendable> condMap = new HashMap<IExpr, IASTAppendable>();
        boolean canMerge = true;
        IASTAppendable fallbackSum = F.PlusAlloc(apartPlus.argSize());

        for (int i = 1; i <= apartPlus.argSize(); i++) {
          IExpr termCoeff = rationalTermCoefficient(apartPlus.get(i), x, x0, n, engine);
          if (!termCoeff.isPresent()) {
            return F.NIL; // Abort cleanly if any term isn't a simple rational
          }
          fallbackSum.append(termCoeff);

          // Try to extract Piecewise({{val, cond}}, 0)
          if (canMerge && termCoeff.isAST(S.Piecewise, 3)) {
            IAST pw = (IAST) termCoeff;
            if (pw.arg1().isList1() && pw.arg1().first().isList2() && pw.arg2().isZero()) {
              IAST rule = (IAST) pw.arg1().first();
              IExpr val = rule.arg1();
              IExpr cond = rule.arg2();

              IASTAppendable sumForCond = condMap.get(cond);
              if (sumForCond == null) {
                sumForCond = F.PlusAlloc();
                condMap.put(cond, sumForCond);
              }
              sumForCond.append(val);
            } else {
              canMerge = false; // Fallback if a term has complex/nested piecewise rules
            }
          } else {
            canMerge = false;
          }
        }

        if (canMerge) {
          IASTAppendable mergedRules = F.ListAlloc(condMap.size());
          for (Map.Entry<IExpr, IASTAppendable> entry : condMap.entrySet()) {
            IExpr cond = entry.getKey();
            // oneIdentity1() naturally converts Plus(x) to x, or Plus(x, y) remains Plus(x, y)
            IExpr mergedVal = engine.evaluate(entry.getValue().oneIdentity1());
            mergedRules.append(F.List(mergedVal, cond));
          }
          return F.Piecewise(mergedRules, F.C0);
        }

        return engine.evaluate(fallbackSum);
      } else {
        return rationalTermCoefficient(apart, x, x0, n, engine);
      }
    }

    private static IExpr rationalTermCoefficient(IExpr term, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      IExpr c = F.C1;
      IExpr core = term;

      // Separate coefficient C from core p(x)^m
      if (term.isTimes()) {
        IAST times = (IAST) term;
        IASTAppendable cTimes = F.TimesAlloc(times.argSize());
        IASTAppendable coreTimes = F.TimesAlloc(times.argSize());
        for (int i = 1; i <= times.argSize(); i++) {
          IExpr arg = times.get(i);
          if (arg.isFree(x)) {
            cTimes.append(arg);
          } else {
            coreTimes.append(arg);
          }
        }
        c = cTimes.oneIdentity1();
        core = coreTimes.oneIdentity1();
      }

      if (core.isPower()) {
        IExpr base = core.base();
        IExpr exp = core.exponent();

        if (exp.isInteger() && ((IInteger) exp).isNegative()) {
          IExpr[] linear = base.linear(x);
          if (linear != null) {
            IExpr a = linear[0];
            IExpr b = linear[1];

            // Shift expansion point: a + b(x + x0) = (a + b*x0) + b*x
            IExpr aPrime = engine.evaluate(F.Plus(a, F.Times(b, x0)));
            IExpr bPrime = b;

            // Handles native Laurent series limits for expansions right AT the pole
            if (aPrime.isZero()) {
              IExpr coeff = engine.evaluate(F.Times(c, F.Power(bPrime, exp)));
              return F.Piecewise(F.list(F.list(coeff, F.Equal(n, exp))), F.C0);
            }

            // Use grouping (-a)^(-m-n) for symbolic variables, but standard (-b/a)^n for pure
            // numbers
            boolean useGroupedBase = !aPrime.isNumber();
            IExpr negRatio = engine.evaluate(F.Divide(F.Negate(bPrime), aPrime));

            IExpr coeff;
            if (exp.isMinusOne()) {
              if (useGroupedBase) {
                // Exact Geometric Series Fallback: exp = -1 -> c * (-a')^(-1-n) * (b')^n *
                // (-1)^(-1)
                IExpr signShift = F.CN1; // (-1)^(-1) = -1
                IExpr baseA = F.Power(F.Negate(aPrime), F.Subtract(exp, n));
                IExpr baseB = F.Power(bPrime, n);
                coeff = engine.evaluate(F.Together(F.Times(c, signShift, baseA, baseB)));
              } else {
                coeff = engine.evaluate(F.Times(c, F.Power(aPrime, F.CN1), F.Power(negRatio, n)));
              }
            } else {
              int m = exp.negate().toIntDefault();
              // Expand higher-order poles into explicit polynomials (safeguard against extreme m)
              if (m > 1 && m < 1000) {
                // Expand Binomial(-m, n) = (-1)^n * (n+1)*...*(n+m-1) / (m-1)!
                IASTAppendable num = F.TimesAlloc(m);
                for (int i = 1; i < m; i++) {
                  num.append(F.Plus(n, F.ZZ(i)));
                }
                IExpr den = engine.evaluate(F.Factorial(F.ZZ(m - 1)));
                IExpr binomialExpanded = engine.evaluate(F.Divide(F.ExpandAll(num), den));

                if (useGroupedBase) {
                  // Merge exactly to MMA format: c * (-a')^(exp-n) * (b')^n * (-1)^exp * P(n)
                  IExpr signShift = F.Power(F.CN1, exp);
                  IExpr baseA = F.Power(F.Negate(aPrime), F.Subtract(exp, n));
                  IExpr baseB = F.Power(bPrime, n);

                  coeff = engine
                      .evaluate(F.Together(F.Times(c, signShift, baseA, baseB, binomialExpanded)));
                } else {
                  coeff = engine.evaluate(F.Together(
                      F.Times(c, F.Power(aPrime, exp), F.Power(negRatio, n), binomialExpanded)));
                }
              } else {
                // General Formula Fallback
                coeff = engine.evaluate(F.Times(c, F.Power(aPrime, exp), F.Binomial(exp, n),
                    F.Power(F.Divide(bPrime, aPrime), n)));
              }
            }
            return F.Piecewise(F.list(F.list(coeff, F.GreaterEqual(n, F.C0))), F.C0);

            // IExpr coeff;
            // if (exp.isMinusOne()) {
            // // Exact Geometric Series Fallback: exp = -1 -> c * aPrime^(-1) * (-bPrime /
            // aPrime)^n
            // IExpr negRatio = engine.evaluate(F.Divide(F.Negate(bPrime), aPrime));
            // coeff = engine.evaluate(F.Times(c, F.Power(aPrime, F.CN1), F.Power(negRatio, n)));
            // } else {
            // // General Formula: C * (a')^exp * Binomial(exp, n) * (b' / a')^n
            // coeff = engine.evaluate(F.Times(c, F.Power(aPrime, exp), F.Binomial(exp, n),
            // F.Power(F.Divide(bPrime, aPrime), n)));
            // }
            // return F.Piecewise(F.list(F.list(coeff, F.GreaterEqual(n, F.C0))), F.C0);
          }
        }
      } else if (core.isFree(x)) {
        return F.Piecewise(F.list(F.list(engine.evaluate(F.Times(c, core)), F.Equal(n, F.C0))),
            F.C0);
      }

      return F.NIL;
    }

    /**
     * Computes the SeriesCoefficient of a composed function f(g(x)) utilizing Faà di Bruno's
     * formula and Bell Polynomials.
     */
    private static IExpr compositeSeriesCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      if (!n.isInteger() || !n.isPositive()) {
        return F.NIL;
      }
      int nInt = n.toIntDefault();

      if (function.isAST1()) {
        IExpr fHead = function.head();
        IExpr gExpr = function.first();

        if (gExpr.isFree(x)) {
          return F.NIL;
        }

        IExpr g0 = engine.evaluate(F.subst(gExpr, x, x0));

        // Extract inner derivatives g_1 ... g_n
        IASTAppendable gDerivs = F.ListAlloc(nInt);
        IExpr currentGDeriv = gExpr;
        for (int i = 1; i <= nInt; i++) {
          currentGDeriv = engine.evaluate(F.D(currentGDeriv, x));
          IExpr gDerivAtX0 = engine.evaluate(F.subst(currentGDeriv, x, x0));
          gDerivs.append(gDerivAtX0);
        }

        IASTAppendable sum = F.PlusAlloc(nInt);
        ISymbol yDum = F.Dummy("y");
        IExpr currentFDeriv = F.unaryAST1(fHead, yDum);

        // Global DP Cache for the entire Faà di Bruno sequence ---
        IExpr[][] bellCache = new IExpr[nInt + 1][nInt + 1];

        // Faà di Bruno iteration Sum
        for (int k = 1; k <= nInt; k++) {
          currentFDeriv = engine.evaluate(F.D(currentFDeriv, yDum));
          IExpr fDerivAtG0 = engine.evaluate(F.subst(currentFDeriv, yDum, g0));

          // Directly invoke the recursive BellY with persistent cache.
          // Because BellY ignores indices > (n - k + 1) internally, we can safely
          // pass the entire gDerivs list without manually truncating it!
          IExpr bellY = org.matheclipse.core.builtin.PolynomialFunctions.bellY(nInt, k, gDerivs,
              (IAST) function, engine, bellCache);

          sum.append(F.Times(fDerivAtG0, bellY));
        }

        return engine.evaluate(F.Times(F.Power(F.Factorial(n), F.CN1), sum));
      }
      return F.NIL;
    }


    private static IExpr taylorCoefficient(IExpr function, IExpr x, IExpr x0, IExpr n,
        EvalEngine engine) {
      final int degree = n.toIntDefault();
      if (degree < 0) {
        return F.NIL;
      }

      if (degree == 0) {
        return function.subs(x, x0);
        // F.ReplaceAll(function, F.Rule(x, x0));
      }
      IExpr derivedFunction = S.D.of(engine, function, F.list(x, n));
      IExpr substituted = derivedFunction.subs(x, x0);

      IExpr rawCoefficient = engine.evaluate(F.Times(F.Power(F.Factorial(n), F.CN1), substituted));
      return engine.evaluate(F.Together(F.ExpandAll(rawCoefficient)));
    }

    /**
     * Fast-path closed-form series coefficients for known special functions.
     */
    private static IExpr specialFunctionCoefficient(IAST function, IExpr x, IExpr x0, IExpr n) {
      // if (function.isAST1()) {
      // IExpr head = function.head();
      // IExpr arg = function.first();
      //
      // Match f(x)
      // if (arg.equals(x) && head.isBuiltInSymbol()) {
      // switch (((IBuiltInSymbol) head).ordinal()) {
      // case ID.Exp:
      // return F.Times(F.Exp(x0), F.Power(F.Factorial(n), F.CN1));
      //
      // case ID.Sin:
      // return F.Times(F.Sin(F.Plus(x0, F.Times(n, S.Pi, F.C1D2))),
      // F.Power(F.Factorial(n), F.CN1));
      //
      // case ID.Cos:
      // return F.Times(F.Cos(F.Plus(x0, F.Times(n, S.Pi, F.C1D2))),
      // F.Power(F.Factorial(n), F.CN1));
      //
      // case ID.Sinh:
      // if (x0.isZero()) {
      // return F.Times(F.Subtract(F.C1, F.Power(F.CN1, n)),
      // F.Power(F.Times(F.C2, F.Factorial(n)), F.CN1));
      // }
      // break;
      //
      // case ID.Cosh:
      // if (x0.isZero()) {
      // return F.Times(F.Plus(F.C1, F.Power(F.CN1, n)),
      // F.Power(F.Times(F.C2, F.Factorial(n)), F.CN1));
      // }
      // break;
      // }
      // }
      // } else if (function.isAST2()) {
      // IExpr head = function.head();
      // IExpr arg1 = function.first();
      // IExpr arg2 = function.second();
      //
      // // Match f(k, x)
      // if (arg2.equals(x) && head.isBuiltInSymbol()) {
      // switch (((IBuiltInSymbol) head).ordinal()) {
      // case ID.ChebyshevU:
      // return F.Times(F.Power(F.C2, n),
      // F.GegenbauerC(F.Subtract(arg1, n), F.Plus(n, F.C1), x0));
      //
      // case ID.BesselJ:
      // if (x0.isZero()) {
      // IExpr k = F.Divide(F.Subtract(n, arg1), F.C2);
      // IExpr coeff = F.Times(F.Power(F.CN1, k),
      // F.Power(
      // F.Times(F.Power(F.C2, n), F.Factorial(k), F.Gamma(F.Plus(k, arg1, F.C1))),
      // F.CN1));
      // return F.Piecewise(
      // F.list(F.list(coeff, F.And(F.Equal(F.Mod(F.Subtract(n, arg1), F.C2), F.C0),
      // F.GreaterEqual(k, F.C0)))),
      // F.C0);
      // }
      // break;
      // }
      // }
      // } else
      if (function.isAST3()) {
        IExpr head = function.head();
        IExpr arg1 = function.first();
        IExpr arg2 = function.second();
        IExpr arg3 = function.arg3();

        // Match HypergeometricPFQ({a...}, {b...}, x)
        if (arg3.equals(x) && head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.HypergeometricPFQ:
              if (x0.isZero() && arg1.isList() && arg2.isList()) {
                IAST aList = (IAST) arg1;
                IAST bList = (IAST) arg2;

                if (n.isInteger()) {
                  int nInt = n.toIntDefault();
                  if (nInt < 0) {
                    return F.C0;
                  }

                  // Integer n
                  IASTAppendable num = F.TimesAlloc(aList.argSize() * nInt + 1);
                  IASTAppendable den = F.TimesAlloc(bList.argSize() * nInt + 2);

                  for (int i = 1; i <= aList.argSize(); i++) {
                    IExpr a = aList.get(i);
                    for (int j = 0; j < nInt; j++) {
                      num.append(F.Plus(a, F.ZZ(j)));
                    }
                  }

                  for (int i = 1; i <= bList.argSize(); i++) {
                    IExpr b = bList.get(i);
                    for (int j = 0; j < nInt; j++) {
                      den.append(F.Plus(b, F.ZZ(j)));
                    }
                  }
                  den.append(F.Factorial(n));

                  return F.Divide(num.oneIdentity1(), den.oneIdentity1());

                } else {
                  // symbolic n
                  IASTAppendable num = F.TimesAlloc(aList.argSize() + bList.argSize() + 1);
                  IASTAppendable den = F.TimesAlloc(aList.argSize() + bList.argSize() + 2);

                  // (a)_n = Gamma(a + n) / Gamma(a)
                  for (int i = 1; i <= aList.argSize(); i++) {
                    IExpr a = aList.get(i);
                    num.append(F.Gamma(F.Plus(a, n)));
                    den.append(F.Gamma(a));
                  }

                  // 1 / (b)_n = Gamma(b) / Gamma(b + n)
                  for (int i = 1; i <= bList.argSize(); i++) {
                    IExpr b = bList.get(i);
                    num.append(F.Gamma(b));
                    den.append(F.Gamma(F.Plus(b, n)));
                  }
                  den.append(F.Factorial(n));

                  IExpr coeff = F.Divide(num.oneIdentity1(), den.oneIdentity1());
                  return F.Piecewise(F.list(F.list(coeff, F.GreaterEqual(n, F.C0))), F.C0);
                }
              }
              break;
          }
        }
      }
      return F.NIL;
    }


    /**
     * @param univariatePolynomial
     * @param x
     * @param x0
     * @param n
     * @param seriesTemplate
     * @param engine
     */
    public static IExpr polynomialSeriesCoefficient(IExpr univariatePolynomial, IExpr x, IExpr x0,
        IExpr n, final IAST seriesTemplate, EvalEngine engine) {
      try {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        IASTAppendable rest = F.ListAlloc(4);
        ExprPolynomialRing.create(univariatePolynomial, x, coefficientMap, rest);
        IASTAppendable coefficientPlus = F.PlusAlloc(2);

        if (coefficientMap.size() > 0) {

          // FAST-PATH FOR x0 == 0:
          // Avoid the 0^(-n) Infinity bug entirely. For expansions around 0,
          // the coefficient for x^n is strictly the coefficient mapped to n.
          if (x0.isZero()) {
            IExpr coeff = coefficientMap.get(n);
            if (coeff != null) {
              coefficientPlus.append(coeff);
            } else if (!n.isNumber()) {
              // If n is symbolic, construct a Piecewise function for the coefficients
              IASTAppendable rules = F.ListAlloc(coefficientMap.size());
              for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
                rules.append(F.list(entry.getValue(), F.Equal(n, entry.getKey())));
              }
              coefficientPlus.append(F.Piecewise(rules, F.C0));
            }
            // Note: If n is a number and not in the map, it appends nothing, which correctly
            // evaluates to 0 via oneIdentity0() below.
          } else {

            // LOGIC FOR x0 != 0: Generalized binomial Taylor shift
            IExpr defaultValue = F.C0;
            IASTAppendable rules = F.ListAlloc(2);
            IASTAppendable plus = F.PlusAlloc(coefficientMap.size());
            IAST comparator = F.GreaterEqual(n, F.C0);

            for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
              final IExpr exp = entry.getKey();
              if (exp.isZero()) {
                continue;
              }

              IExpr coefficient = entry.getValue();
              if (coefficient.isZero()) {
                continue;
              }

              IAST powerPart = F.Power(x0, exp);
              comparator = F.Greater(n, F.C0);
              IAST bin;
              int k = exp.toIntDefault();

              if (F.isPresent(k)) {
                if (k < 0) {
                  x0 = x0.negate();
                  int nk = -k;
                  IASTAppendable binomial = F.TimesAlloc(nk + 1);
                  for (int i = 1; i < nk; i++) {
                    binomial.append(F.Plus(n, F.ZZ(i)));
                  }
                  binomial.append(F.Power(F.Factorial(F.ZZ(nk - 1)), -1));
                  bin = binomial;
                  comparator = F.GreaterEqual(n, F.C0);
                } else {
                  comparator = F.LessEqual(F.C0, n, exp);
                  bin = F.Binomial(exp, n);
                }
              } else {
                bin = F.Binomial(exp, n);
              }

              if (coefficient.isOne()) {
                plus.append(F.Times(powerPart, bin));
              } else {
                plus.append(F.Times(coefficient, powerPart, bin));
              }
            }

            IExpr temp = engine.evaluate(plus);
            if (!temp.isZero()) {
              rules.append(
                  F.list(engine.evaluate(F.Times(F.Power(x0, n.negate()), plus)), comparator));
            }

            if (comparator.isAST(S.Greater)) {
              plus = F.PlusAlloc(coefficientMap.size());
              for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
                IExpr exp = entry.getKey();
                IExpr coefficient = entry.getValue();
                if (coefficient.isZero()) {
                  continue;
                }
                if (coefficient.isOne()) {
                  plus.append(F.Times(F.Power(x0, exp)));
                } else {
                  plus.append(F.Times(coefficient, F.Power(x0, exp)));
                }
              }
              rules.append(F.list(engine.evaluate(plus), F.Equal(n, F.C0)));
            }
            coefficientPlus.append(F.Piecewise(rules, defaultValue));
          }
        } else {
          if (!univariatePolynomial.isPlus()) {
            return F.NIL;
          }
        }

        for (int i = 1; i < rest.size(); i++) {
          IASTMutable term = seriesTemplate.copy();
          term.set(1, rest.get(i));
          coefficientPlus.append(term);
        }

        return coefficientPlus.oneIdentity0();

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.SeriesCoefficient, rex, engine);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // Allow 2 or more arguments for nested / multi-variate extractions
      return IFunctionEvaluator.ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      MATCHER1 = Suppliers.memoize(SeriesCoefficientRules::init1);
    }
  }


  /**
   *
   *
   * <pre>
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * internal structure of a power series at the point <code>x = x0</code> the <code>coeff</code> -i
   * are coefficients of the power series.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)
   * Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)
   * </pre>
   */
  private static final class SeriesData extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int denominator = 1;
      if (ast.size() == 6 || ast.size() == 7) {
        if (ast.arg1().isNumber()) {
          // Attempt to evaluate a series at the number `1`. Returning Indeterminate.
          Errors.printMessage(S.SeriesData, "ssdn", F.List(), engine);
          return S.Indeterminate;
        }
        IExpr x = ast.arg1();
        IExpr x0 = ast.arg2();

        if (ast.arg3().isVector() < 0 || !ast.arg3().isAST()) {
          return F.NIL;
        }
        IAST coefficients = (IAST) ast.arg3();
        final int nMin = ast.arg4().toIntDefault();
        if (F.isNotPresent(nMin)) {
          return F.NIL;
        }
        final int truncate = ast.arg5().toIntDefault();
        if (F.isNotPresent(truncate)) {
          return F.NIL;
        }
        if (ast.size() == 7) {
          denominator = ast.get(6).toIntDefault();
        }
        return new ASTSeriesData(x, x0, coefficients, nMin, truncate, denominator);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SeriesFunctions() {}
}

