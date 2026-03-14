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
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
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
  private static final class InverseSeries extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
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
          return Errors.printMessage(S.General, "ivar", F.List(x), engine);
        }
        // TODO add implementation
      }
      return F.NIL;
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
   * Series(expr, {x, x0, n})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * create a power series of <code>expr</code> up to order <code>(x- x0)^n</code> at the point
   * <code>x = x0</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Series(f(x),{x,a,3})
   * f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4
   * </pre>
   */
  private static final class Series extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        IExpr function = ast.arg1();
        IExpr x = F.NIL;

        if (ast.arg2().isVector() == 3) {
          // Existing logic for Series(expr, {x, x0, n})
          IAST list = (IAST) ast.arg2();
          x = list.arg1();
          IExpr x0 = list.arg2();
          if (!x.isVariable()) {
            return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
          }
          final int n = list.arg3().toIntDefault();
          if (F.isNotPresent(n)) {
            return F.NIL;
          }
          if (function.isFree(x)) {
            return function;
          }

          boolean isInfinity = x0.isDirectedInfinity();
          int direction = 0;
          if (x0.equals(F.CInfinity)) {
            direction = -1;
          } else if (x0.equals(F.CNInfinity)) {
            direction = 1;
          }

          IExpr seriesX0 = x0;
          IExpr seriesFunction = function;

          if (isInfinity) {
            seriesX0 = F.C0;
            seriesFunction = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
          }

          ASTSeriesData series =
              ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0, n, direction, engine);
          if (series != null) {
            if (isInfinity) {
              // Repackage the series computed at 0 with the original Infinity expansion point
              return new ASTSeriesData(x, x0, series.arg3(), series.minExponent(),
                  series.truncateOrder(), series.puiseuxDenominator());
            }
            return series;
          }
        } else if (ast.arg2().isRuleAST()) {
          // New logic for Series(expr, x -> x0) to generate the leading term
          IAST rule = (IAST) ast.arg2();
          x = rule.arg1();
          IExpr x0 = rule.arg2();
          if (!x.isVariable()) {
            return Errors.printMessage(S.Series, "ivar", F.List(x), engine);
          }
          if (function.isFree(x)) {
            return function;
          }

          boolean isInfinity = x0.isInfinity() || x0.isNegativeInfinity();
          int direction = 0;
          if (x0.equals(F.CInfinity)) {
            direction = -1;
          } else if (x0.equals(F.CNInfinity)) {
            direction = 1;
          }

          IExpr seriesX0 = x0;
          IExpr seriesFunction = function;

          if (isInfinity) {
            seriesX0 = F.C0;
            seriesFunction = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
          }

          // FIX: Default expansion order changed to 1. This prevents over-calculating
          // truncation bounds for single simple poles, allowing rigorous matching of `2`.
          int currentN = 1;
          ASTSeriesData series = ASTSeriesData.seriesDataRecursive(seriesFunction, x, seriesX0,
              currentN, direction, engine);

          // Force probe deeper if the series evaluates entirely to O(x^n) zeroes
          int probeLimit = 30;
          while (series != null && currentN < probeLimit) {
            boolean foundNonZero = false;
            if (!series.isOrder()) {
              for (int i = series.minExponent(); i < series.truncateOrder(); i++) {
                if (!series.coefficient(i).isZero()) {
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
            // Locate the first mathematically non-zero term
            for (int i = series.minExponent(); i < series.truncateOrder(); i++) {
              if (!series.coefficient(i).isZero()) {
                leadIndex = i;
                break;
              }
            }
            if (leadIndex != Integer.MAX_VALUE) {
              int nextIndex = -1;

              // For Laurent series (poles) or expansions at Infinity,
              // dynamically scan for the true next non-zero term to establish a rigorous asymptotic
              // boundary.
              if (isInfinity || leadIndex < 0) {
                for (int i = leadIndex + 1; i < series.truncateOrder(); i++) {
                  if (!series.coefficient(i).isZero()) {
                    nextIndex = i;
                    break;
                  }
                }

                // If no non-zero terms exist before the evaluated truncation bound,
                // the strict Big-O boundary is exactly the truncation limit itself.
                if (nextIndex == -1) {
                  nextIndex = series.truncateOrder();
                }
              }

              // Fallback for regular Taylor series (x -> x0) where the mathematical
              // boundary safely decays to the next incremental precision step.
              if (nextIndex == -1) {
                nextIndex = leadIndex + series.puiseuxDenominator();
              }

              IASTAppendable coeffs = F.ListAlloc(2);
              coeffs.append(series.coefficient(leadIndex));

              if (isInfinity) {
                return new ASTSeriesData(x, x0, coeffs, leadIndex, nextIndex,
                    series.puiseuxDenominator());
              }
              return new ASTSeriesData(x, seriesX0, coeffs, leadIndex, nextIndex,
                  series.puiseuxDenominator());
            }
          }
        }

        // --- NEW FALLBACK HEURISTIC ---
        // For functions with essential singularities (e.g. Exp(x) as x -> Infinity),
        // they cannot be mathematically mapped to a standard Puiseux SeriesData object.
        // We bypass the series generator by pushing the series request recursively into the AST
        // arguments.
        if (function.isAST() && x.isPresent()) {
          IAST astFunc = (IAST) function;
          IASTAppendable resultAST = F.ast(astFunc.head(), astFunc.argSize());
          boolean changed = false;
          for (int i = 1; i <= astFunc.argSize(); i++) {
            IExpr arg = astFunc.get(i);
            if (!arg.isFree(x)) {
              IExpr argSeries = engine.evaluate(F.Series(arg, ast.arg2()));
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
            // When evaluated, functions like Exp(SeriesData) auto-convert to E^SeriesData natively
            return engine.evaluate(resultAST);
          }
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
      // IExpr result = matcher1().apply(ast);
      // if (result.isPresent()) {
      // return result;
      // }

      if (ast.isAST2()) {
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
          IExpr functionCoefficient = functionCoefficient(ast, function, x, x0, n, engine);
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
        IExpr subFunc = engine.evaluate(F.subst(function, x, F.Power(x, F.CN1)));
        return functionCoefficient(ast, subFunc, x, F.C0, n, engine);
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
      temp = polynomialSeriesCoefficient(function, x, x0, n, ast, engine);
      if (temp.isPresent()) {
        return temp;
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
      return engine.evaluate(F.Together(F.Times(F.Power(F.Factorial(n), F.CN1), substituted)));
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
        // if (!x0.isZero()) {
        Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
        IASTAppendable rest = F.ListAlloc(4);
        ExprPolynomialRing.create(univariatePolynomial, x, coefficientMap, rest);
        IASTAppendable coefficientPlus = F.PlusAlloc(2);
        if (coefficientMap.size() > 0) {
          IExpr defaultValue = F.C0;
          IASTAppendable rules = F.ListAlloc(2);
          IASTAppendable plus = F.PlusAlloc(coefficientMap.size());
          IAST comparator = F.GreaterEqual(n, F.C0);
          for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
            final IExpr exp = entry.getKey();
            if (exp.isZero()) {
              continue;
            }
            if (exp.isNegative() && x0.isZero()) {
              if (exp.equals(n)) {
                defaultValue = F.C1;
              }
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
                // powerPart = F.Power(x0.negate(), exp);
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
                // binomial = F.TimesAlloc(k);
                // for (int i = 0; i < k; i++) {
                // binomial.append(F.Subtract(n, F.ZZ(i)));
                // }
                // binomial.append(F.Power(F.Factorial(F.ZZ(k)), -1));
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
        // }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.SeriesCoefficient, rex, engine);
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

