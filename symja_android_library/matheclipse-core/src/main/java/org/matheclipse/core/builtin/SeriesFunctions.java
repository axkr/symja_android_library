package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
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

      S.Limit.setEvaluator(new Limit());
      if (ToggleFeature.SERIES) {
        S.ComposeSeries.setEvaluator(new ComposeSeries());
        S.InverseSeries.setEvaluator(new InverseSeries());
        S.Normal.setEvaluator(new Normal());
        S.PadeApproximant.setEvaluator(new PadeApproximant());
        S.Series.setEvaluator(new Series());
        S.SeriesCoefficient.setEvaluator(new SeriesCoefficient());
        S.SeriesData.setEvaluator(new SeriesData());
      }
    }
  }

  /**
   *
   *
   * <pre>
   * Limit(expr, x -&gt; x0)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the limit of <code>expr</code> as <code>x</code> approaches <code>x0</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Limit(7+Sin(x)/x, x-&gt;Infinity)
   * 7
   * </pre>
   */
  private static final class Limit extends AbstractFunctionOptionEvaluator {

    /** Direction of limit computation */
    private static enum Direction {
      /** Compute the limit approaching from larger real values. */
      FROM_ABOVE(-1),

      /** Compute the limit approaching from larger or smaller real values automatically. */
      TWO_SIDED(0),

      /** Compute the limit approaching from smaller real values. */
      FROM_BELOW(1);

      private int direction;

      /**
       * Convert the direction <code>FROM_ABOVE, TWO_SIDED, FROM_BELOW</code> to the corresponding
       * value <code>-1, 0, 1</code>
       *
       * @return
       */
      int toInt() {
        return direction;
      }

      private Direction(int direction) {
        this.direction = direction;
      }
    }

    /** Representing the data for the current limit. */
    private static class LimitData {
      private final ISymbol variable;

      private final IExpr limitValue;

      /** The rule <code>variable->limitValue</code>. */
      private final IAST rule;

      private Direction direction;

      public LimitData(ISymbol variable, IExpr limitValue, IAST rule, Direction direction) {
        this.variable = variable;
        this.limitValue = limitValue;
        this.rule = rule;
        this.direction = direction;
      }

      /**
       * Get the optional direction value. Default is DIRECTION_TWO_SIDED = 0.
       *
       * @return
       */
      public Direction direction() {
        return direction;
      }

      /**
       * Get the limit value of the limit definition <code>variable->limitValue</code>
       *
       * @return
       */
      public IExpr limitValue() {
        return limitValue;
      }

      public IAST rule() {
        return rule;
      }

      /**
       * Get the <code>variable</code> of the limit definition <code>variable->limitValue</code>
       *
       * @return
       */
      public ISymbol variable() {
        return variable;
      }

      /**
       * Create a new <code>F.Limit( arg1, ... )</code> expression from this <code>LimitData</code>
       * object
       *
       * @param arg1 the first argument of the Limit expression
       * @return a new <code>F.Limit( arg1, ... )</code> expression
       */
      public IExpr limit(IExpr arg1) {
        return evalLimitQuiet(arg1, this);
      }

      /**
       * Map a <code>F.Limit( arg1, ... )</code> expression at each argument of the given <code>ast
       * </code>.
       *
       * @param ast
       * @return
       */
      public IAST mapLimit(final IAST ast) {
        // return ast.mapThread(limit(null), 1);
        IASTMutable result = ast.copy();
        boolean isIndeterminate = false;
        boolean isLimit = false;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = evalLimitQuiet(ast.get(i), this);
          if (!temp.isFree(S.Limit)) {
            isLimit = true;
          } else if (temp.isIndeterminate()) {
            isIndeterminate = true;
          }
          result.set(i, temp);
        }
        if (isLimit && isIndeterminate) {
          return F.NIL;
        }
        if (isIndeterminate && limitValue.isZero() && ast.isTimes()) {
          return squeezeTheorem(ast).orElse(result);
        }
        return result;
      }

      /**
       * Try the squeeze theorem: <a href="https://en.wikipedia.org/wiki/Squeeze_theorem">Wikipedia
       * - Squeeze theorem</a>. It is assumed that {@link #limitValue} equals <code>0</code>.
       * <p>
       * Example:
       * 
       * <pre>
       * Limit(x * Sin(1 / x), x -> 0)
       * </pre>
       * 
       * @param ast
       * @param defaultResult
       * @return <code>F.Times(F.C0)</code> or {@link F#NIL} if squeeze theorem was not applicable.
       */
      private IAST squeezeTheorem(final IAST ast) {
        IASTAppendable[] cosSinFilter = ast.filter(x -> determineCosSinCases(x));
        if (cosSinFilter != null //
            && cosSinFilter[0].argSize() > 0 //
            && cosSinFilter[1].argSize() > 0 //
            && !cosSinFilter[0].isOne() //
            && !cosSinFilter[1].isOne()) {
          IExpr temp = evalLimitQuiet(F.Abs(cosSinFilter[1]), this);
          if (temp.isZero()) {
            temp = evalLimitQuiet(F.Abs(cosSinFilter[0]), this);
            if (temp.isIndeterminate()) {
              return F.Times(F.C0);
            }
          }
        }
        return F.NIL;
      }

      /**
       * Examples:
       * 
       * <pre>
       * Limit(x * Sin(1 / x), x -> 0)
       * </pre>
       * 
       * <pre>
       * Limit(x ^ 2 * Sin(1 / x) ^ 3, x -> 0)
       * </pre>
       * 
       * @param x
       * @return
       */
      private boolean determineCosSinCases(IExpr x) {
        if (x.isPower()) {
          int exponent = x.exponent().toIntDefault();
          if (exponent > 0) {
            return determineCosSinCases(x.base());
          }
        }
        return (x.isSin() || x.isCos()) && !x.isFree(variable);
      }
    }

    private static IExpr evalLimitQuiet(final IExpr expr, LimitData data) {
      if (expr.isNumber()) {
        return expr;
      }
      EvalEngine engine = EvalEngine.get();
      boolean quiet = engine.isQuietMode();
      try {
        IExpr direction =
            data.direction() == Direction.TWO_SIDED ? S.Reals : F.ZZ(data.direction().toInt());
        return F.Limit(expr, data.rule(), F.Rule(S.Direction, direction)).eval(engine);
      } finally {
        engine.setQuietMode(quiet);
      }
    }

    /**
     * Evaluate the limit for the given limit data.
     *
     * @param expr
     * @param data the limits data definition
     * @param engine
     * @return {@link S#NIL} if no limit could be found
     */
    private static IExpr evalLimit(final IExpr expr, LimitData data, EvalEngine engine) {
      IExpr expression = expr;
      final IExpr limitValue = data.limitValue();

      // Android-changed: do not use shared EvalEngine
      if (engine == null) {
        engine = EvalEngine.get();
      }
      IExpr result = engine.evalQuiet(expression);
      if (result.isNumericFunction(true)) {
        return result;
      }
      if (!result.isIndeterminate()) {
        expression = result;
      }
      if (result.isFree(data.variable(), true)) {
        // Limit[a_,sym->lim] -> a
        return expression;
      }
      if (result.equals(data.variable())) {
        // Limit[x_,x_->lim] -> lim
        return limitValue;
      }

      if (limitValue.isNumericFunction(true) && expression.isFree(x -> x == S.Piecewise, true)) {
        IExpr temp = evalReplaceAll(expression, data, engine);
        if (temp.isPresent()) {
          return temp;
        }
      } else if ((limitValue.isInfinity() || limitValue.isNegativeInfinity()) && expression.isAST()
          && expression.size() > 1) {
        if (limitValue.isInfinity() || limitValue.isNegativeInfinity()) {
          IExpr temp = evalReplaceAll(expression, data, engine);
          if (temp.isNumericFunction(true)) {
            return temp;
          }
          if (expression.isNumericFunction(data.variable()) && expression.size() > 1
              && !expression.isPlusTimesPower()) {
            temp = limitNumericFunctionArgs((IAST) expression, data, engine);
            if (temp.isPresent()) {
              return temp;
            }
          }
          // if (expression.isPower() && !expression.first().isE()) {
          // temp = F.Times(expression.second(), F.Log(expression.first()));
          // temp = evalLimit(temp, data, engine);
          // if (temp.isPresent()) {
          // return temp;
          // }
          // }
        }
        IExpr temp = limitInfinityZero((IAST) expression, data, (IAST) limitValue);
        if (temp.isPresent()) {
          return temp;
        }
      }

      if (expression.isAST()) {
        if (!limitValue.isNumericFunction(true) && limitValue.isFree(S.DirectedInfinity)
            && limitValue.isFree(data.variable())) {
          // example Limit(E^(3*x), x->a) ==> E^(3*a)
          return expr.replaceAll(data.rule()).orElse(expr);
        }
        final IAST ast = (IAST) expression;
        if (ast.isPlus()) {
          return plusLimit(ast, data, engine);
        } else if (ast.isTimes()) {
          return timesLimit(ast, data, engine);
        } else if (ast.isPower() && !ast.base().isPositive() && !ast.exponent().isPositive()) {
          return powerLimit(ast, data, engine);
        } else if (ast.isAST(S.Piecewise, 3)) {
          return piecewiseLimit(ast, data, engine);
        } else if (ast.argSize() > 0 && ast.isNumericFunctionAST()) {
          IASTMutable copy = ast.copy();
          IExpr temp = F.NIL;
          boolean indeterminate = false;
          for (int i = 1; i < ast.size(); i++) {
            temp = data.limit(ast.get(i));
            if (temp.isPresent()) {
              if (temp.isIndeterminate()) {
                if (data.direction != Direction.TWO_SIDED) {
                  return S.Indeterminate;
                }
                indeterminate = true;
              }
              copy.set(i, temp);
            } else {
              copy.set(i, S.Indeterminate);
              indeterminate = true;
            }
          }
          if (!indeterminate) {
            temp = engine.evalQuiet(copy);
            if (temp.isPresent() && !temp.isIndeterminate()) {
              return temp;
            }
          }
          if (data.direction == Direction.TWO_SIDED && indeterminate) {
            return evalLimitTwoSided(copy, ast, data, engine);
          }
          return S.Indeterminate;
        }
      }

      return F.NIL;
    }

    /**
     * <p>
     * Evaluate the limit of a function by evaluating the separate directions
     * <code>({@link Direction#FROM_BELOW}, {@link Direction#FROM_ABOVE}</code> for the arguments
     * and comparing the function evaluation result for equality.
     * 
     * @param astLimitEvaluated the limit evaluation which can contain {@link S#Indeterminate} as
     *        arguments
     * @param astOriginal the original ast with all non {@link S#Indeterminate} arguments
     * @param limitTwoSided containing the <code>{@link Direction#TWO_SIDED}</code> data for limit
     *        determining
     * @param engine
     * @return {@link S#Indeterminate} if no limit can be found
     */
    private static IExpr evalLimitTwoSided(IASTMutable astLimitEvaluated, final IAST astOriginal,
        LimitData limitTwoSided, EvalEngine engine) {
      IASTMutable copy1 = astOriginal.copy();
      IASTMutable copy2 = astOriginal.copy();
      for (int i = 1; i < astOriginal.size(); i++) {
        IExpr arg = astLimitEvaluated.get(i);
        if (arg.isIndeterminate()) {
          arg = astOriginal.get(i);
          LimitData limitBelow = new LimitData(limitTwoSided.variable, limitTwoSided.limitValue,
              limitTwoSided.rule, Direction.FROM_BELOW);
          IExpr belowValue = limitBelow.limit(arg);
          if (belowValue.isPresent() && !belowValue.isIndeterminate()) {
            LimitData limitAbove = new LimitData(limitTwoSided.variable, limitTwoSided.limitValue,
                limitTwoSided.rule, Direction.FROM_ABOVE);
            IExpr aboveValue = limitAbove.limit(arg);
            if (aboveValue.isPresent() && !aboveValue.isIndeterminate()) {
              copy1.set(i, belowValue);
              copy2.set(i, aboveValue);
              continue;
            }
          }
          return S.Indeterminate;
        } else {
          copy1.set(i, astLimitEvaluated.get(i));
          copy2.set(i, astLimitEvaluated.get(i));
        }
      }
      IExpr f1 = engine.evalQuiet(copy1);
      IExpr f2 = engine.evalQuiet(copy2);
      if (f1.equals(f2) && !f1.isIndeterminate() && f1.isPresent() && f1.isFree(S.Interval)) {
        return f1;

      }
      return S.Indeterminate;
    }

    /**
     * <p>
     * Evaluate the limit of a single argument function by evaluating the separate directions
     * (FROM_BELOW, FROM_ABOVE) and comparing the result for equality.
     * 
     * <p>
     * TODO create similar mechanism for functions with multiple arguments.
     * 
     * @param functionArg1 a function with one argument
     * @param data
     * @param engine
     * @return {@link S#Indeterminate} if no limit can be found
     */
    // private static IExpr evalSplitTwoSided(final IAST functionArg1, LimitData data,
    // EvalEngine engine) {
    // LimitData copy =
    // new LimitData(data.variable, data.limitValue, data.rule, Direction.FROM_BELOW);
    // IExpr belowValue = copy.limit(functionArg1.arg1());
    // if (belowValue.isPresent() && !belowValue.isIndeterminate()) {
    // copy = new LimitData(data.variable, data.limitValue, data.rule, Direction.FROM_ABOVE);
    // IExpr aboveValue = copy.limit(functionArg1.arg1());
    // if (aboveValue.isPresent() && !aboveValue.isIndeterminate()) {
    // IExpr f1 = engine.evalQuiet(F.unaryAST1(functionArg1.head(), belowValue));
    // IExpr f2 = engine.evalQuiet(F.unaryAST1(functionArg1.head(), aboveValue));
    // if (f1.equals(f2) && !f1.isIndeterminate() && f1.isPresent() && f1.isFree(S.Interval)) {
    // return f1;
    // }
    // }
    // }
    // return S.Indeterminate;
    // }

    /**
     * Evaluate the limits of the arguments of the <code>function</code> and evaluate the <code>
     * function</code> with these new arguments if available.
     *
     * @param function
     * @param data the data for the limit
     * @param engine
     * @return {@link F#NIL} if evaluation wasn't successful
     */
    private static IExpr limitNumericFunctionArgs(IAST function, LimitData data,
        EvalEngine engine) {
      IASTMutable functionLimitArgs = F.NIL;
      for (int i = 1; i < function.size(); i++) {
        IExpr arg = function.get(i);
        if (!arg.isFree(data.variable())) {
          IExpr temp = evalLimitQuiet(arg, data);
          if (temp.isPresent() && temp.isFree(data.variable()) && temp.isNumericFunction(true)) {
            if (functionLimitArgs.isNIL()) {
              functionLimitArgs = function.copy();
            }
            functionLimitArgs.set(i, temp);
          }
        }
      }
      if (functionLimitArgs.isPresent()) {
        IExpr temp = engine.evaluate(functionLimitArgs);
        if (!temp.isIndeterminate() && !temp.isComplexInfinity()) {
          return temp;
        }
      }
      return F.NIL;
    }

    private static IExpr evalReplaceAll(IExpr expression, LimitData data, EvalEngine engine) {
      IExpr result = expression.replaceAll(data.rule());
      if (result.isPresent()) {
        result = engine.evalQuiet(result);
        if (result.isNumericFunction(true) || result.isInfinity() || result.isNegativeInfinity()) {
          return result;
        }
      }
      return F.NIL;
    }

    /**
     * Solve for example:<br>
     * <code>Limit(Gamma(1/t),t->Infinity) ==> Infinity</code> <br>
     * <code>Limit(Gamma(1/t),t->-Infinity)  ==> -Infinity</code>
     *
     * @param ast
     * @param data
     * @param limitValue <code>Infinity</code> or <code>-Infinity</code>
     * @return
     */
    private static IExpr limitInfinityZero(IAST ast, LimitData data, final IAST limitValue) {
      Direction direction = limitValue.isNegativeInfinity() ? //
          Direction.FROM_BELOW //
          : Direction.FROM_ABOVE;
      Direction dataDirection = data.direction();
      if (dataDirection == Direction.TWO_SIDED || dataDirection == direction) {
        int variableArgPosition = -1;
        for (int i = 1; i < ast.size(); i++) {
          if (!ast.get(i).isFree(data.variable())) {
            if (variableArgPosition == -1) {
              variableArgPosition = i;
            } else {
              // more than 1 argument contains the variable
              return F.NIL;
            }
          }
        }
        if (variableArgPosition > 0) {
          IExpr arg1 = evalLimitQuiet(ast.get(variableArgPosition), data);
          if (arg1.isZero()) {

            LimitData tempData =
                new LimitData(data.variable(), F.C0, F.Rule(data.variable(), F.C0), direction);
            return evalLimitQuiet(ast.setAtCopy(variableArgPosition, data.variable()), tempData);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Try L'hospitales rule. See <a href="http://en.wikipedia.org/wiki/L%27H%C3%B4pital%27s_rule">
     * Wikipedia L'Hôpital's rule</a>
     *
     * @param numerator
     * @param denominator
     * @param data the limits data definition
     * @param engine
     * @return
     */
    private static IExpr lHospitalesRule(IExpr numerator, IExpr denominator, LimitData data,
        EvalEngine engine) {
      final ISymbol x = data.variable();
      int recursionLimit = engine.getRecursionLimit();
      try {
        if (recursionLimit <= 0 || recursionLimit > Config.LIMIT_LHOSPITAL_RECURSION_LIMIT) {
          // set recursion limit for using l'Hospitales rule
          engine.setRecursionLimit(Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
        }
        if (data.limitValue.isInfinity() || data.limitValue.isNegativeInfinity()) {
          if (!numerator.isPower() && denominator.isPower()
              && denominator.exponent().equals(F.C1D2)) {
            // github #115: numerator / Sqrt( denominator.base() )
            IFraction frac = (IFraction) denominator.exponent();
            if (frac.numerator().isOne()) {
              IInteger exp = frac.denominator(); // == 2
              IExpr expr = engine.evalQuiet(F.Times(F.D(F.Power(numerator, exp), x),
                  F.Power(F.D(denominator.base(), x), F.CN1)));
              if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
                expr = engine.evalQuiet(F.Simplify(expr));
              }
              expr = evalLimit(expr, data, engine);
              if (expr.isNumber()) {
                // Sqrt( expr )
                return F.Power(expr, frac);
              }
            }
          }
        }
        if (numerator.isPowerFraction()) {
          return lHospitalesRuleWithNumeratorRoot((IAST) numerator, denominator, data, engine);
        }
        IExpr expr =
            engine.evalQuiet(F.Times(F.D(numerator, x), F.Power(F.D(denominator, x), F.CN1)));
        if (expr.isTimes() && expr.leafCount() < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
          expr = engine.evalQuiet(F.Simplify(expr));
        }
        if (expr.isFree(v -> v.equals(S.D) || v.equals(S.Derivative), true)) {
          engine.incRecursionCounter();
          return evalLimit(expr, data, engine);
        }
      } catch (RecursionLimitExceeded rle) {
        engine.setRecursionLimit(recursionLimit);
      } finally {
        engine.setRecursionLimit(recursionLimit);
      }
      return F.NIL;
    }

    /**
     * The <code>numerator</code> is of the form <code>base ^ (n/root)</code>. L'hospital rule is
     * tried for <code>{base ^ n, denominator ^ root}</code> and the result returned as <code>
     * result ^ (1/root)</code>.
     *
     * @param numerator is of the form <code>Power(base,n/root)</code>
     * @param denominator
     * @param data
     * @param engine
     * @return
     */
    private static IExpr lHospitalesRuleWithNumeratorRoot(IAST numerator, IExpr denominator,
        LimitData data, EvalEngine engine) {
      // see github #230
      final ISymbol x = data.variable();
      final IFraction exponentFraction = (IFraction) numerator.exponent();
      final IInteger n = exponentFraction.numerator();
      final IInteger root = exponentFraction.denominator();
      final IExpr newNumerator = engine.evalQuiet(F.Power(numerator.base(), n));
      final IExpr newDenominator = engine.evalQuiet(F.Power(denominator, root));
      final IExpr expr =
          engine.evalQuiet(F.Times(F.D(newNumerator, x), F.Power(F.D(newDenominator, x), F.CN1)));
      final IExpr temp = evalLimit(expr, data, engine);
      if (temp.isPresent()) {
        return F.Power(temp, F.QQ(F.C1, root));
      }
      return F.NIL;
    }

    /**
     * See: <a href="http://en.wikibooks.org/wiki/Calculus/Infinite_Limits">Limits at Infinity of
     * Rational Functions</a>
     *
     * @param numeratorPoly
     * @param denominatorPoly
     * @param symbol the variable for which to approach to the limit
     * @param limit the limit value
     * @param data the limit expression which the variable should approach to
     * @return
     */
    private static IExpr limitsInfinityOfRationalFunctions(ExprPolynomial numeratorPoly,
        ExprPolynomial denominatorPoly, ISymbol symbol, IExpr limit, LimitData data) {
      long numDegree = numeratorPoly.degree();
      long denomDegree = denominatorPoly.degree();
      if (numDegree > denomDegree) {
        // If the numerator has the highest term, then the fraction is
        // called "top-heavy". If, when you divide the numerator
        // by the denominator the resulting exponent on the variable is
        // even, then the limit (at both \infty and -\infty) is
        // \infty. If it is odd, then the limit at \infty is \infty, and the
        // limit at -\infty is -\infty.
        long oddDegree = (numDegree + denomDegree) % 2;
        if (oddDegree == 1) {
          return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
              denominatorPoly.leadingBaseCoefficient()), limit));
        } else {
          return data.limit(F.Times(F.Divide(numeratorPoly.leadingBaseCoefficient(),
              denominatorPoly.leadingBaseCoefficient()), F.CInfinity));
        }
      } else if (numDegree < denomDegree) {
        // If the denominator has the highest term, then the fraction is
        // called "bottom-heavy" and the limit (at both \infty
        // and -\infty) is zero.
        return F.C0;
      }
      // If the exponent of the highest term in the numerator matches the
      // exponent of the highest term in the denominator,
      // the limit (at both \infty and -\infty) is the ratio of the
      // coefficients of the highest terms.
      return F.Divide(numeratorPoly.leadingBaseCoefficient(),
          denominatorPoly.leadingBaseCoefficient());
    }

    // private static IExpr mapLimit(final IAST ast, LimitData data) {
    // return ast.mapThread(data.limit(null), 1);
    // }

    /**
     * Try l'Hospitales rule for numerator and denominator expression.
     *
     * @param numerator
     * @param denominator
     * @param data the limit data definition
     * @param engine
     * @return <code>F.NIL</code> if no limit was found
     */
    private static IExpr numeratorDenominatorLimit(IExpr numerator, IExpr denominator,
        LimitData data, EvalEngine engine) {
      IExpr numValue;
      IExpr denValue;
      // final IExpr limitValue = data.limitValue();
      // IAST rule = data.getRule();
      if (denominator.isOne() && numerator.isTimes()) {
        // Limit[a_*b_*c_,sym->lim] ->
        // Limit[a,sym->lim]*Limit[b,sym->lim]*Limit[c,sym->lim]
        return data.mapLimit((IAST) numerator);
      }
      if (!denominator.isNumber() || denominator.isZero()) {
        int recursionLimit = engine.getRecursionLimit();
        try {
          if (recursionLimit <= 0 || recursionLimit > Config.LIMIT_LHOSPITAL_RECURSION_LIMIT) {
            // set recursion limit for using l'Hospitales rule
            engine.setRecursionLimit(Config.LIMIT_LHOSPITAL_RECURSION_LIMIT);
          }
          IExpr result = F.NIL;
          // ISymbol x = data.variable();
          denValue = evalLimitQuiet(denominator, data);
          // denValue = engine.evalModuleDummySymbol(denominator, x, limitValue, true);
          if (denValue.isIndeterminate()) {
            return F.NIL;
          } else if (denValue.isZero()) {
            numValue = evalLimitQuiet(numerator, data);
            // numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
            if (numValue.isZero()) {
              return lHospitalesRule(numerator, denominator, data, engine);
            }
            return F.NIL;
          } else if (denValue.isInfinity()) {
            numValue = evalLimitQuiet(numerator, data);
            // numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
            if (numValue.isInfinity()) {
              return lHospitalesRule(numerator, denominator, data, engine);
            } else if (numValue.isNegativeInfinity()) {
              numerator = engine.evaluate(numerator.negate());
              numValue = evalLimitQuiet(numerator, data);
              // numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
              if (numValue.isInfinity()) {
                result = lHospitalesRule(numerator, denominator, data, engine);
                if (result.isPresent()) {
                  return result.negate();
                }
              }
            }
            return F.NIL;
          } else if (denValue.isNegativeInfinity()) {
            denominator = engine.evaluate(denominator.negate());
            denValue = evalLimitQuiet(denominator, data);
            // denValue = engine.evalModuleDummySymbol(denominator, x, limitValue, true);
            if (denValue.isInfinity()) {
              numValue = evalLimitQuiet(numerator, data);
              // numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
              if (numValue.isInfinity()) {
                result = lHospitalesRule(numerator, denominator, data, engine);
                if (result.isPresent()) {
                  // negate because denominator.negate()
                  return result.negate();
                }
              } else if (numValue.isNegativeInfinity()) {
                numerator = engine.evaluate(numerator.negate());
                numValue = evalLimitQuiet(numerator, data);
                // numValue = engine.evalModuleDummySymbol(numerator, x, limitValue, true);
                if (numValue.isInfinity()) {
                  // tried both cases numerator.negate() and denominator.negate()
                  return lHospitalesRule(numerator, denominator, data, engine);
                }
              }
            }
            return F.NIL;
          }
        } catch (RecursionLimitExceeded rle) {
          engine.setRecursionLimit(recursionLimit);
        } finally {
          engine.setRecursionLimit(recursionLimit);
        }
      }
      return F.NIL;
      // return F.Times(data.limit(numerator), F.Power(data.limit(denominator), F.CN1));
    }

    private static IExpr piecewiseLimit(final IAST piecwiseAST, LimitData data, EvalEngine engine) {
      IExpr limit = data.limitValue();
      IExpr variable = data.variable();
      if (limit.isReal()) {
        int[] piecewiseDimension = piecwiseAST.isPiecewise();
        if (piecewiseDimension != null && piecewiseDimension[0] > 0) {
          IAST matrixOfValueConditionPairs = (IAST) piecwiseAST.first();
          IExpr defaultPiecewiseValue = piecwiseAST.second();
          IExpr limitFromBelow = F.NIL;
          IExpr limitFromAbove = F.NIL;
          for (int i = 0; i < piecewiseDimension[0]; i++) {
            IAST row = matrixOfValueConditionPairs.getAST(i + 1);
            IExpr arg1Result = row.arg1();
            IExpr arg2Comparison = row.arg2();

            IExpr tempComparison = arg2Comparison;
            if (data.direction == Direction.FROM_BELOW //
                || data.direction == Direction.TWO_SIDED) {
              if (arg2Comparison.isAST(S.Less, 3) && arg2Comparison.first().equals(variable)
                  && arg2Comparison.second().equals(limit)) {
                tempComparison = ((IAST) arg2Comparison).setAtCopy(0, S.LessEqual);
              }
              IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
              if (temp.isTrue()) {
                temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
                if (limitFromBelow.isPresent() && !limitFromBelow.equals(temp)) {
                  return S.Indeterminate;
                }
                limitFromBelow = temp;
              } else if (!temp.isFalse()) {
                return F.NIL;
              }
            }

            tempComparison = arg2Comparison;
            if (data.direction == Direction.FROM_ABOVE //
                || data.direction == Direction.TWO_SIDED) {
              if (arg2Comparison.isAST(S.Greater, 3) && arg2Comparison.first().equals(variable)
                  && arg2Comparison.second().equals(limit)) {
                tempComparison = ((IAST) arg2Comparison).setAtCopy(0, S.GreaterEqual);
              }
              IExpr temp = engine.evaluate(F.xreplace(tempComparison, variable, limit));
              if (temp.isTrue()) {
                temp = engine.evaluate(F.xreplace(arg1Result, variable, limit));
                if (limitFromAbove.isPresent() && !limitFromAbove.equals(temp)) {
                  return S.Indeterminate;
                }
                limitFromAbove = temp;
              } else if (!temp.isFalse()) {
                return F.NIL;
              }

            }
          }

          if (data.direction == Direction.FROM_BELOW) {
            if (limitFromBelow.isPresent()) {
              return limitFromBelow;
            }
            return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
          }
          if (data.direction == Direction.FROM_ABOVE) {
            if (limitFromAbove.isPresent()) {
              return limitFromAbove;
            }
            return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
          }
          if (data.direction == Direction.TWO_SIDED) {
            if (limitFromBelow.isPresent() && limitFromBelow.equals(limitFromAbove)) {
              return limitFromBelow;
            }
            if (limitFromBelow.isNIL() && limitFromAbove.isNIL()) {
              return engine.evaluate(F.xreplace(defaultPiecewiseValue, variable, limit));
            }
            return S.Indeterminate;
          }
        }
      }
      return F.NIL;
    }

    private static IExpr plusLimit(final IAST plusAST, LimitData data, EvalEngine engine) {
      // Limit[a_+b_+c_,sym->lim] ->
      // Limit[a,sym->lim]+Limit[b,sym->lim]+Limit[c,sym->lim]
      // IAST rule = data.getRule();
      IExpr limit = data.limitValue();
      if (limit.isInfinity() || limit.isNegativeInfinity()) {
        ISymbol symbol = data.variable();
        try {
          ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
          ExprPolynomial poly = ring.create(plusAST);
          IExpr coeff = poly.leadingBaseCoefficient();
          long oddDegree = poly.degree() % 2;
          if (oddDegree == 1) {
            return evalLimitQuiet(F.Times(coeff, limit), data);
          }
          return evalLimitQuiet(F.Times(coeff, F.CInfinity), data);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
      }
      IAST mapLimit = data.mapLimit(plusAST);
      if (mapLimit.isPresent()) {
        if (mapLimit.isFree(x -> x.equals(S.Limit), true)) {
          IExpr temp = F.eval(mapLimit);
          if (temp.isIndeterminate() && plusAST.isPlus()) {
            int indexOf = plusAST.indexOf(x -> isSqrtExpression(x));
            if (indexOf > 0) {
              temp = timesConjugateLHospital(plusAST, indexOf, data);
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
        }
        return mapLimit;
      }
      return F.NIL;
    }

    /**
     * Test if <code>y</code> matches pattern <code>Sqrt(_)</code> or
     * <code>Times(f1,...,Sqrt(_),...,fn)</code>
     * 
     * @param y
     * @return
     */
    private static boolean isSqrtExpression(IExpr y) {
      if (y.isTimes()) {
        return ((IAST) y).exists(x -> x.isSqrt());
      }
      return y.isSqrt();
    }

    private static IExpr timesConjugateLHospital(final IAST plusAST, int indexOf, LimitData data) {
      IExpr factor1 = plusAST.removeAtCopy(indexOf).oneIdentity0();
      IExpr factor2 = plusAST.get(indexOf);
      IExpr numerator = F.evalExpand(F.Subtract(F.Sqr(factor1), F.Sqr(factor2)));
      IExpr denominator = F.eval(F.Subtract(factor1, factor2));
      // IASTMutable timesConjugate = F.Times(numerator, F.Power(denominator, F.CN1));
      return numeratorDenominatorLimit(numerator, denominator, data, EvalEngine.get());
      // temp = evalLimitQuiet(timesConjugate, data);

    }

    private static IExpr powerLimit(final IAST powerAST, LimitData data, EvalEngine engine) {
      IExpr base = powerAST.arg1();
      IExpr exponent = powerAST.arg2();
      if (exponent.equals(data.variable())) {
        if (!base.isZero()) {
          if (data.limitValue().isZero()) {
            return F.C1;
          }
          if (base.isFree(data.variable())) {
            boolean isInfinityLimit = data.limitValue().isInfinity();
            if (isInfinityLimit || data.limitValue().isNegativeInfinity()) {
              if (F.Log(base).isNumericFunction(true)) {
                if (F.Log(base).greater(F.C0).isTrue()) {
                  return isInfinityLimit ? F.CInfinity : F.C0;
                }
              } else if (base.isNumericFunction(s -> s.isSymbol() ? "" : null)) {
                return F.ConditionalExpression(isInfinityLimit ? F.CInfinity : F.C0,
                    F.Greater(F.Log(base), F.C0));
              }
            }
          }
        }
        if (base.isRealResult()) {
          IExpr temp = evalReplaceAll(powerAST, data, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      if (exponent.isFree(data.variable())) {
        final IExpr temp = evalLimitQuiet(base, data);
        if (temp.isPresent()) {
          if (temp.isZero()) {
            if (!exponent.isNumericFunction(true)) {
              // ConditionalExpression(0, exponent > 0)
              return F.ConditionalExpression(F.C0, F.Greater(exponent, F.C0));
            }
          } else {
            if (temp.isFree(data.variable())) {
              // ConditionalExpression(0, exponent > 0)
              return F.Power(temp, exponent);
            }
          }
        }
        if (base.isTimes()) {
          IAST isFreeResult =
              base.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
          if (!isFreeResult.arg2().isOne()) {
            return F.Times(F.Power(isFreeResult.arg1(), exponent),
                data.limit(F.Power(isFreeResult.arg2(), exponent)));
          }
        }
      }
      if (exponent.isNumericFunction(true)) {
        // Limit[a_^exp_,sym->lim] -> Limit[a,sym->lim]^exp
        // IExpr temp = F.evalQuiet(F.Limit(arg1.arg1(), rule));?
        IExpr temp = evalLimitQuiet(base, data);
        if (temp.isNumericFunction(true)) {
          if (temp.isZero()) {
            if (exponent.isPositive()) {
              // 0 ^ (positve exponent)
              return F.C0;
            }
            if (exponent.isNegative()) {
              // 0 ^ (negative exponent)
              if (exponent.isInteger()) {
                IInteger n = (IInteger) exponent;
                if (n.isEven()) {
                  return F.CInfinity;
                }
                if (data.direction() == Direction.TWO_SIDED) {
                  return S.Indeterminate;
                } else if (data.direction() == Direction.FROM_BELOW) {
                  return F.CNInfinity;
                } else if (data.direction() == Direction.FROM_ABOVE) {
                  return F.CInfinity;
                }
              } else if (exponent.isFraction()) {
                if (data.direction() == Direction.TWO_SIDED) {
                  return S.Indeterminate;
                } else if (data.direction() == Direction.FROM_ABOVE) {
                  return F.CInfinity;
                }
              }
            }
            return F.NIL;
          }
          return F.Power(temp, exponent);
        }
        if (temp.isNIL()) {
          temp = base;
        }
        if (exponent.isInteger()) {
          IInteger n = (IInteger) exponent;
          if (temp.isInfinity()) {
            if (n.isPositive()) {
              return temp;
            } else if (n.isNegative()) {
              return F.C0;
            }
            return F.NIL;
          } else if (temp.isNegativeInfinity()) {
            if (n.isPositive()) {
              if (n.isEven()) {
                return F.CInfinity;
              } else {
                return F.CNInfinity;
              }
            } else if (n.isNegative()) {
              return F.C0;
            }
            return F.NIL;
          } else if (temp.isIndeterminate() || temp.isAST(S.Limit)) {
            return F.NIL;
          }
          if (n.isPositive()) {
            return F.Power(temp, n);
          } else if (n.isNegative() && n.isEven()) {
            return F.Power(temp, n);
          }
        }
      }
      return F.NIL;
    }

    /**
     * Try a substitution. <code>y = 1/x</code>. As <code>|x|</code> approaches <code>Infinity
     * </code> or <code>-Infinity</code>, <code>y</code> approaches <code>0</code>.
     *
     * @param arg1
     * @param data (the datas limit must be Infinity or -Infinity)
     * @param engine
     * @return <code>F.NIL</code> if the substitution didn't succeed.
     */
    private static IExpr substituteInfinity(final IAST arg1, LimitData data, EvalEngine engine) {
      ISymbol x = data.variable();
      IExpr y = F.Power(x, F.CN1); // substituting by 1/x
      IExpr temp = F.evalQuiet(F.subst(arg1, x, y));
      if (temp.isTimes()) {
        Optional<IExpr[]> parts =
            Algebra.fractionalPartsTimesPower((IAST) temp, false, false, true, true, true, true);
        if (parts.isPresent()) {
          if (!parts.get()[1].isOne()) { // denominator != 1
            LimitData ndData = new LimitData(x, F.C0, F.Rule(x, F.C0), data.direction());
            temp = numeratorDenominatorLimit(parts.get()[0], parts.get()[1], ndData, engine);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
      }
      return F.NIL;
    }

    private static IExpr timesLimit(final IAST timesAST, LimitData data, EvalEngine engine) {
      IAST isFreeResult =
          timesAST.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
      if (!isFreeResult.arg1().isOne()) {
        IExpr freeOfVariable = isFreeResult.arg1();
        IExpr limit = data.limit(isFreeResult.arg2());
        if (limit.isInfinity() || limit.isNegativeInfinity()) {
          if (engine.evaluate(F.Greater(freeOfVariable, F.C0)).isTrue()) {
            return limit.isInfinity() ? F.CInfinity : F.CNInfinity;
          }
          if (engine.evaluate(F.Less(freeOfVariable, F.C0)).isTrue()) {
            return limit.isInfinity() ? F.CNInfinity : F.CInfinity;
          }
        }

        return F.Times(freeOfVariable, limit);
      }
      Optional<IExpr[]> parts =
          Algebra.fractionalPartsTimesPower(timesAST, false, false, true, true, true, true);
      if (parts.isEmpty()) {
        IAST[] timesPolyFiltered = timesAST.filter(x -> x.isPolynomial(data.variable));
        if (timesPolyFiltered[0].size() > 1 && timesPolyFiltered[1].size() > 1) {
          IExpr first = engine.evaluate(data.limit(timesPolyFiltered[0].oneIdentity1()));
          if (first.isIndeterminate()) {
            return S.Indeterminate;
          }
          IExpr second = engine.evaluate(data.limit(timesPolyFiltered[1].oneIdentity1()));
          if (second.isIndeterminate()) {
            return S.Indeterminate;
          }
          if (first.isReal() || second.isReal()) {
            IExpr temp = engine.evaluate(F.Times(first, second));
            if (!temp.isIndeterminate()) {
              return temp;
            }
            if (data.limitValue().isZero()) {
              // Try reciprocal of symbol and approach to +/- Infinity
              IExpr newTimes =
                  timesAST.replaceAll(F.Rule(data.variable, F.Power(data.variable, F.CN1)));
              if (newTimes.isPresent()) {
                IAST infinityExpr =
                    (data.direction == Direction.FROM_BELOW) ? F.CNInfinity : F.CInfinity;
                LimitData copy = new LimitData(data.variable, infinityExpr,
                    F.Rule(data.variable, infinityExpr), data.direction);
                temp = engine.evaluate(copy.limit(newTimes));
                if (!temp.isIndeterminate()) {
                  return temp;
                }
              }
            }
          }
        }
      } else {

        IExpr numerator = parts.get()[0];
        IExpr denominator = parts.get()[1];
        IExpr limit = data.limitValue();
        ISymbol symbol = data.variable();
        if (limit.isInfinity() || limit.isNegativeInfinity()) {
          try {
            ExprPolynomialRing ring = new ExprPolynomialRing(symbol);
            ExprPolynomial denominatorPoly = ring.create(denominator);
            ExprPolynomial numeratorPoly = ring.create(numerator);
            return limitsInfinityOfRationalFunctions(numeratorPoly, denominatorPoly, symbol, limit,
                data);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
          }
        }

        IExpr plusResult = Algebra.partsApart(parts.get(), symbol, engine);
        // Algebra.partialFractionDecompositionRational(new PartialFractionGenerator(),
        // parts,symbol);
        if (plusResult.isPlus()) {
          return data.mapLimit((IAST) plusResult);
        }

        if (denominator.isOne()) {
          if (limit.isInfinity() || limit.isNegativeInfinity()) {
            IExpr temp = substituteInfinity(timesAST, data, engine);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
        IExpr temp = numeratorDenominatorLimit(numerator, denominator, data, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      return data.mapLimit(timesAST);
    }

    // private static IExpr logLimit(final IAST logAST, LimitData data, EvalEngine engine) {
    // if (logAST.isAST2() && !logAST.isFree(data.variable())) {
    // return F.NIL;
    // }
    // IExpr firstArg = logAST.arg1();
    // if (firstArg.isPower() && firstArg.exponent().isFree(data.variable())) {
    // IAST arg1 = logAST.setAtCopy(1, firstArg.base());
    // return F.Times(firstArg.exponent(), data.limit(arg1));
    // } else if (firstArg.isTimes()) {
    // IAST isFreeResult =
    // firstArg.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
    // if (!isFreeResult.arg1().isOne()) {
    // IAST arg1 = logAST.setAtCopy(1, isFreeResult.arg1());
    // IAST arg2 = logAST.setAtCopy(1, isFreeResult.arg2());
    // return F.Plus(arg1, data.limit(arg2));
    // }
    // }
    // return F.NIL;
    // }

    // private static IExpr logLimit(final IAST logAST, LimitData data, EvalEngine engine) {
    // if (logAST.isAST2() && !logAST.isFree(data.variable())) {
    // return F.NIL;
    // }
    // IExpr firstArg = logAST.arg1();
    // if (firstArg.isPower() && firstArg.exponent().isFree(data.variable())) {
    // IAST arg1 = logAST.setAtCopy(1, firstArg.base());
    // return F.Times(firstArg.exponent(), data.limit(arg1));
    // } else if (firstArg.isTimes()) {
    // IAST isFreeResult =
    // firstArg.partitionTimes(x -> x.isFree(data.variable(), true), F.C1, F.C1, S.List);
    // if (!isFreeResult.arg1().isOne()) {
    // IAST arg1 = logAST.setAtCopy(1, isFreeResult.arg1());
    // IAST arg2 = logAST.setAtCopy(1, isFreeResult.arg2());
    // return F.Plus(arg1, data.limit(arg2));
    // }
    // }
    // return F.NIL;
    // }

    /**
     * Limit of a function. See <a href="http://en.wikipedia.org/wiki/List_of_limits">List of
     * Limits</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
        final EvalEngine engine, IAST originalAST) {

      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (!arg2.isRuleAST()) {
        // Limit specification `1` is not of the form x->x0.
        return Errors.printMessage(S.Limit, "lim", F.List(arg2), engine);
      }
      IAST rule = (IAST) arg2;
      if (!(rule.arg1().isSymbol())) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.Limit, "ivar", F.List(arg2), engine);
      }
      if (arg1.isList()) {
        return arg1.mapThread(ast, 1);
      }
      boolean numericMode = engine.isNumericMode();
      try {
        engine.setNumericMode(false);
        Direction direction = Direction.TWO_SIDED; // no direction as default

        // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        IExpr directionOption = option[0];
        if (directionOption.isPresent()) {
          if (directionOption.isOne() || directionOption.isString("FromBelow")) {
            direction = Direction.FROM_BELOW;
          } else if (directionOption.isMinusOne() || directionOption.isString("FromAbove")) {
            direction = Direction.FROM_ABOVE;
          } else if (directionOption.equals(S.Automatic) || directionOption.equals(S.Reals)
              || directionOption.isString("TwoSided")) {
            direction = Direction.TWO_SIDED;
          } else {
            // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided
            // or a list of these.
            return Errors.printMessage(S.Limit, "ldir", F.List(ast.arg3()), engine);
          }
        } else {
          // Value of `1` should be a number, Reals, Complexes, FromAbove, FromBelow, TwoSided or
          // a list of these.
          return Errors.printMessage(S.Limit, "ldir", F.List(S.Null), engine);
        }

        IExpr assumptionOption = option[1];
        IExpr generateConditionOption = option[2];
        IAssumptions oldAssumptions = engine.getAssumptions();
        try {
          IExpr assumptionExpr = OptionArgs.determineAssumptions(assumptionOption);
          if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
            IAssumptions assumptions =
                org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
            if (assumptions != null) {
              engine.setAssumptions(assumptions);
            }
          }

          if (direction == Direction.TWO_SIDED) {
            IExpr temp = S.Limit.evalDownRule(engine, F.Limit(arg1, arg2));
            if (temp.isPresent()) {
              return temp;
            }
          }

          ISymbol symbol = (ISymbol) rule.arg1();
          IExpr limit = rule.arg2();
          LimitData data = new LimitData(symbol, limit, rule, direction);
          return evalLimit(arg1, data, engine);
        } finally {
          engine.setAssumptions(oldAssumptions);
        }
      } finally {
        engine.setNumericMode(numericMode);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDALL);
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.Direction, S.Assumptions, S.GenerateConditions}, //
          new IExpr[] {S.Reals, S.$Assumptions, S.Automatic});
      super.setUp(newSymbol);
    }
  }


  /**
   *
   *
   * <pre>
   * Normal(series)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * converts a <code>series</code> expression into a standard expression.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))
   * 1/x-x-4*x^2-17*x^3-88*x^4-549*x^5
   * </pre>
   */
  private static final class Normal extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST heads = F.CEmptyList;
      if (ast.isAST2()) {
        heads = ast.arg2().makeList();
      }
      final IExpr arg1 = ast.arg1();
      return F.subst(arg1, normal(heads));
    }

    private Function<IExpr, IExpr> normal(final IAST heads) {
      return x -> {
        final int size = heads.size();
        if (size == 1) {
          return x.normal(true);
        }
        final IExpr head = x.head();
        if (heads.exists(y -> y.equals(head))) {
          return x.normal(true);
        }
        return F.NIL;
      };
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
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
            if (m == Integer.MIN_VALUE) {
              return F.NIL;
            }
            final int n = order.arg2().toIntDefault();
            if (n == Integer.MIN_VALUE) {
              return F.NIL;
            }
            if (function.isTimes()) {
              Optional<IExpr[]> numeratorDenominatorParts =
                  Algebra.fractionalParts(function, false);
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

    /**
     * 
     * @param numeratorDenominatorParts
     * @param x
     * @param x0
     * @param m
     * @param n
     * @return
     * @deprecated contains bug
     */
    @Deprecated
    private static IExpr quotientTaylorFunctionExpr(IExpr[] numeratorDenominatorParts, IExpr x,
        IExpr x0, final int m, final int n) {
      List<IExpr> varList = Lists.newArrayList(x);
      IASTAppendable list = F.ListAlloc(varList);
      JASIExpr jas = new JASIExpr(varList, true);
      ExprPolynomialRing ring = new ExprPolynomialRing(list);
      ExprPolynomial num = ring.create(numeratorDenominatorParts[0]);
      ExprPolynomial den = ring.create(numeratorDenominatorParts[1]);
      GenPolynomial<IExpr> numerator = jas.expr2IExprJAS(num);
      GenPolynomial<IExpr> denominator = jas.expr2IExprJAS(den);
      UnivPowerSeriesRing<IExpr> fac = new UnivPowerSeriesRing<IExpr>(ExprRingFactory.CONST);
      GenPolynomialRing<IExpr> pr = fac.polyRing();
      QuotientRing<IExpr> qr = new QuotientRing<IExpr>(pr);
      Quotient<IExpr> p = new Quotient<IExpr>(qr, numerator, denominator);
      TaylorFunction<IExpr> TF = new QuotientTaylorFunction<IExpr>(p);
      Quotient<IExpr> approximantOfPade = PolyUfdUtil.<IExpr>approximantOfPade(fac, TF, x0, m, n);
      IExpr numeratorExpr = jas.exprPoly2Expr(approximantOfPade.num);
      IExpr denominatorExpr = jas.exprPoly2Expr(approximantOfPade.den);
      return org.matheclipse.core.expression.F.Divide(numeratorExpr, denominatorExpr);
    }

    private static IExpr quotientTaylorFunction(IExpr[] numeratorDenominatorParts, IExpr x,
        IExpr x0, final int m, final int n) {

      UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<BigRational>(BigRational.ZERO);
      JASConvert<BigRational> jas = new JASConvert<BigRational>(x, BigRational.ZERO);
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
      if (ast.isAST1() && (ast.arg1() instanceof ASTSeriesData)) {

        ASTSeriesData ps = (ASTSeriesData) ast.arg1();
        ps = ps.reversion();
        if (ps != null) {
          return ps;
        }
      }
      return F.NIL;
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
      if (ast.isAST2() && (ast.arg2().isVector() == 3)) {

        IExpr function = ast.arg1();

        IAST list = (IAST) ast.arg2();
        IExpr x = list.arg1();
        IExpr x0 = list.arg2();
        if (!x.isVariable()) {
          // `1` is not a valid variable.
          return Errors.printMessage(S.General, "ivar", F.List(x), engine);
        }
        final int n = list.arg3().toIntDefault();
        if (n == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (function.isFree(x)) {
          return function;
        }
        ASTSeriesData series = seriesDataRecursive(function, x, x0, n, engine);
        if (series != null) {
          return series;
        }
      }
      return F.NIL;
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
  private static final class SeriesCoefficient extends AbstractFunctionEvaluator {
    private static Supplier<Matcher> MATCHER1;

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
            int order = series.order();
            if (order > n) {
              return series.coefficient(n);
            } else {
              return S.Indeterminate;
            }
          }
          return F.NIL;
        }
        if (ast.arg2().isList3() && !(ast.arg1() instanceof ASTSeriesData)) {
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
          return matcher1().apply(ast);
        }
      }

      return F.NIL;
    }

    private static IExpr functionCoefficient(final IAST ast, IExpr function, IExpr x, IExpr x0,
        IExpr n, EvalEngine engine) {
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
            }
            if (linear[0].isZero() && linear[1].isOne()) {
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
        final int lowerLimit = x0.toIntDefault();
        if (lowerLimit != 0) {
          // TODO support other cases than 0
          return F.NIL;
        }
        x0 = F.ZZ(lowerLimit);
      }

      final int degree = n.toIntDefault();
      if (degree < 0) {
        return F.NIL;
      }

      if (degree == 0) {
        return F.ReplaceAll(function, F.Rule(x, x0));
      }
      IExpr derivedFunction = S.D.of(engine, function, F.list(x, n));
      return engine.evaluate(F.Together(
          F.Times(F.Power(F.Factorial(n), F.CN1), F.ReplaceAll(derivedFunction, F.Rule(x, x0)))));
    }

    /**
     * @param univariatePolynomial
     * @param x
     * @param x0
     * @param n
     * @param seriesTemplate
     * @param engine
     * @return
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
            if (k != Integer.MIN_VALUE) {
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
        if (nMin == Integer.MIN_VALUE) {
          return F.NIL;
        }
        final int truncate = ast.arg5().toIntDefault();
        if (truncate == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (ast.size() == 7) {
          denominator = ast.get(6).toIntDefault();
          if (!ToggleFeature.SERIES_DENOMINATOR && denominator != 1) {
            // ToggleFeature `1` is disabled.
            return Errors.printMessage(ast.topHead(), "toggle",
                F.list(F.stringx("SERIES_DENOMINATOR")), engine);
          }
        }
        return new ASTSeriesData(x, x0, coefficients, nMin, truncate, denominator);
      }
      return F.NIL;
    }

  }


  /**
   * Create an <code>ASTSeriesData</code> object from the given <code>function</code> expression.
   *
   * @param function the function which should be generated as a power series
   * @param x the variable
   * @param x0 the point to do the power expansion for
   * @param n the order of the expansion
   * @param engine the evaluation engine
   * @return the series or <code>null</code> if no series is found
   */
  @Nullable
  public static ASTSeriesData seriesDataRecursive(final IExpr function, IExpr x, IExpr x0,
      final int n, EvalEngine engine) {
    final int denominator = 1;
    if (function.isFree(x) || function.equals(x)) {
      Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
      IASTAppendable rest = F.PlusAlloc(4);
      return polynomialSeries(function, x, x0, n, coefficientMap, rest);
    }

    if (function.isPlus()) {
      ASTSeriesData temp = plusSeriesData((IAST) function, x, x0, n, engine);
      if (temp != null) {
        return temp;
      }
    } else if (function.isTimes()) {
      Optional<IExpr[]> numeratorDenominatorParts = Algebra.fractionalParts(function, false);
      if (numeratorDenominatorParts.isPresent()) {
        ASTSeriesData sd =
            Algebra.polynomialTaylorSeries(numeratorDenominatorParts.get(), x, x0, n, denominator);
        if (sd != null) {
          return sd;
        }
      }
      ASTSeriesData temp = timesSeriesData((IAST) function, x, x0, n, engine);
      if (temp != null) {
        return temp;
      }
    }
    ASTSeriesData sd = simpleSeries(function, x, x0, n, denominator, engine);
    if (sd != null) {
      return sd;
    }

    if (function.isPower()) {
      ASTSeriesData temp = powerSeriesData(function, x, x0, n, engine);
      if (temp != null) {
        return temp;
      }
    } else if (function.isLog() && function.first().equals(x) && x0.isZero() && n >= 0) {
      return new ASTSeriesData(x, x0, F.list(function), 0, n + 1, 1);
    }
    return null;
  }

  /**
   * Try to find a series with the steps:
   *
   * <ol>
   * <li><a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/SeriesCoefficient.md">SeriesCoefficient()</a>.
   * <li><a href="https://en.wikipedia.org/wiki/Taylor_series">Wikipedia - Taylor's formula</a>
   * </ol>
   *
   * @param function the function which should be generated as a power series
   * @param x the variable
   * @param x0 the point to do the power expansion for
   * @param n the order of the expansion
   * @param denominator
   * @param engine the evaluation engine
   * @return the series or <code>null</code> if no series is found
   */
  private static ASTSeriesData simpleSeries(final IExpr function, IExpr x, IExpr x0, final int n,
      final int denominator, EvalEngine engine) {
    VariablesSet varSet = new VariablesSet(function);
    varSet.add(x);
    varSet.addVarList(x0);
    ASTSeriesData sd = seriesCoefficient(function, x, x0, n, denominator, varSet, engine);
    if (sd != null) {
      return sd;
    }
    return taylorSeries(function, x, x0, n, denominator, varSet, engine);
  }

  /**
   * Try to find a series with function {@link SeriesCoefficient}
   *
   * @param function the function which should be generated as a power series
   * @param x the variable
   * @param x0 the point to do the power expansion for
   * @param n the order of the expansion
   * @param denominator
   * @param varSet the variables of the function (including x)
   * @param engine the evaluation engine
   * @return the <code>SeriesCoefficient()</code> series or <code>null</code> if the function is not
   *         numeric w.r.t the varSet
   */
  private static ASTSeriesData seriesCoefficient(final IExpr function, IExpr x, IExpr x0,
      final int n, final int denominator, VariablesSet varSet, EvalEngine engine) {
    ISymbol power = F.Dummy("$$$n");
    IExpr temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, power)));
    if (temp.isNumericFunction(varSet)) {
      int end = n;
      if (n < 0) {
        end = 0;
      }
      ASTSeriesData ps = new ASTSeriesData(x, x0, end + 1, end + denominator, denominator);
      for (int i = 0; i <= end; i++) {
        ps.setCoeff(i, engine.evalQuiet(F.subst(temp, F.Rule(power, F.ZZ(i)))));
      }
      return ps;
    } else {
      int end = n;
      if (n < 0) {
        end = 0;
      }
      temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, F.C0)));
      if (temp.isNumericFunction(varSet)) {
        boolean evaled = true;
        ASTSeriesData ps = new ASTSeriesData(x, x0, end + 1, end + denominator, denominator);
        ps.setCoeff(0, temp);
        for (int i = 1; i <= end; i++) {
          temp = engine.evalQuiet(F.SeriesCoefficient(function, F.list(x, x0, F.ZZ(i))));
          if (temp.isNumericFunction(varSet)) {
            ps.setCoeff(i, temp);
          } else {
            evaled = false;
            break;
          }
        }
        if (evaled) {
          return ps;
        }
      }
    }
    return null;
  }

  /**
   * Create a series with <a href="https://en.wikipedia.org/wiki/Taylor_series">Wikipedia - Taylor's
   * formula</a>.
   *
   * @param function the function which should be generated as a power series
   * @param x the variable
   * @param x0 the point to do the power expansion for
   * @param n the order of the expansion
   * @param denominator
   * @param varSet the variables of the function (including x)
   * @param engine the evaluation engine
   * @return the Taylor series or <code>null</code> if the function is not numeric w.r.t the varSet
   */
  private static ASTSeriesData taylorSeries(final IExpr function, IExpr x, IExpr x0, final int n,
      int denominator, VariablesSet varSet, EvalEngine engine) {
    ASTSeriesData ps = new ASTSeriesData(x, x0, 0, n + denominator, denominator);
    IExpr derivedFunction = function;
    for (int i = 0; i <= n; i++) {
      IExpr functionPart = engine.evalQuiet(F.ReplaceAll(derivedFunction, F.Rule(x, x0)));
      if (functionPart.isIndeterminate()) {
        functionPart = engine.evalQuiet(F.Limit(derivedFunction, F.Rule(x, x0)));
        if (!functionPart.isNumericFunction(varSet)) {
          return null;
        }
      }
      IExpr coefficient =
          engine.evalQuiet(F.Times(F.Power(NumberTheory.factorial(i), F.CN1), functionPart));
      if (coefficient.isIndeterminate() || coefficient.isComplexInfinity()) {
        return null;
      }
      ps.setCoeff(i, coefficient);
      derivedFunction = engine.evalQuiet(F.D(derivedFunction, x));
    }
    return ps;
  }

  private static ASTSeriesData timesSeriesData(IAST timesAST, IExpr x, IExpr x0, final int n,
      EvalEngine engine) {
    IASTAppendable rest = F.PlusAlloc(4);
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
    rest = F.TimesAlloc(4);
    coefficientMap = ExprPolynomialRing.createTimes(timesAST, x, coefficientMap, rest);
    int shift = 0;
    IExpr coefficient = F.C1;
    if (coefficientMap.size() == 1) {
      shift = coefficientMap.keySet().iterator().next().toIntDefault(0);
      if (shift != 0) {
        timesAST = rest;
        coefficient = coefficientMap.values().iterator().next();
      }
    }

    IExpr arg;
    // IInteger ni = F.ZZ(n + Math.abs(shift));
    int ni = n + Math.abs(shift);
    ASTSeriesData series;
    if (timesAST.isEmpty()) {
      ASTSeriesData temp = seriesFromMap(x, x0, n, coefficientMap, rest);
      if (temp != null && rest.isEmpty()) {
        return temp;
      }
      if (rest.isEmpty()) {
        return null;
      }
      timesAST = rest;
      if (temp != null) {
        arg = seriesDataRecursive(timesAST.arg1(), x, x0, ni, engine);
        if (arg != null) {
          series = temp.timesPS((ASTSeriesData) arg);
        } else {
          return null;
        }
      } else {
        arg = seriesDataRecursive(timesAST.arg1(), x, x0, ni, engine);
        if (arg != null) {
          series = (ASTSeriesData) arg;
        } else {
          return null;
        }
      }
    } else {
      arg = seriesDataRecursive(timesAST.arg1(), x, x0, ni, engine);
      if (arg != null) {
        series = (ASTSeriesData) arg;
      } else {
        return null;
      }
    }
    if (timesAST.size() != 1) {
      for (int i = 2; i < timesAST.size(); i++) {
        IExpr timesArg = timesAST.get(i);
        if (timesArg.isPower()) {
          int exp = timesArg.exponent().toIntDefault();
          if (exp != Integer.MIN_VALUE) {
            if (exp == -1) {
              // arg1.divide(arg2.base())
              arg = seriesDataRecursive(timesArg.base(), x, x0, ni, engine);
              if (arg != null) {
                series = series.dividePS((ASTSeriesData) arg);
                continue;
              }
              return null;
            }
          }
        }

        arg = seriesDataRecursive(timesArg, x, x0, ni, engine);
        if (arg != null) {
          series = series.timesPS((ASTSeriesData) arg);
          continue;
        }
        return null;
      }
      if (shift != 0) {
        series = series.shift(shift, coefficient, n + 1);
      }
      return series;
    }
    return null;
  }

  private static ASTSeriesData plusSeriesData(final IAST plusAST, IExpr x, IExpr x0, final int n,
      EvalEngine engine) {
    Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
    IASTAppendable rest = F.PlusAlloc(4);

    ASTSeriesData temp = polynomialSeries(plusAST, x, x0, n, coefficientMap, rest);
    if (temp != null) {
      if (rest.isEmpty()) {
        return temp;
      }
    }
    ASTSeriesData series = null;
    ASTSeriesData arg;
    int start = 1;
    if (temp != null) {
      series = temp;
    } else {
      arg = seriesDataRecursive(rest.arg1(), x, x0, n, engine);
      series = arg;
      start = 2;
    }
    if (series != null) {
      for (int i = start; i < rest.size(); i++) {
        arg = seriesDataRecursive(rest.get(i), x, x0, n, engine);
        series = series.plusPS(arg);
      }
      if (series != null) {
        return series;
      }
    }
    return null;
  }

  private static ASTSeriesData powerSeriesData(final IExpr powerAST, IExpr x, IExpr x0, final int n,
      EvalEngine engine) {
    IExpr base = powerAST.base();
    IExpr exponent = powerAST.exponent();
    if (base.isFree(x)) {
      if (exponent.isPower() && exponent.base().equals(x) && exponent.exponent().isRational()) {
        IRational rat = (IRational) exponent.exponent();
        if (rat.isPositive()) {
          int numerator = rat.numerator().toIntDefault();
          int denominator = rat.denominator().toIntDefault();
          if (denominator != Integer.MIN_VALUE) {
            ASTSeriesData temp =
                seriesDataRecursive(F.Power(base, x), x, x0, n * denominator, engine);
            if (temp != null) {
              ASTSeriesData series = temp;
              if (numerator != 1) {
                series = series.shiftTimes(numerator, F.C1, series.order());
              }
              series.setDenominator(denominator);
              return series;
            }
          }
        }
      }
    } else if (!(base instanceof ASTSeriesData)) {
      Map<IExpr, IExpr> coefficientMap = new HashMap<IExpr, IExpr>();
      IASTAppendable rest = F.PlusAlloc(4);
      ASTSeriesData temp = polynomialSeries(powerAST, x, x0, n, coefficientMap, rest);
      if (temp != null) {
        return temp;
      }
    }
    int exp = exponent.toIntDefault();
    if (exp != Integer.MIN_VALUE) {
      ASTSeriesData series = seriesDataRecursive(base, x, x0, n, engine);
      if (series != null) {
        return series.powerSeries(exp);
      }
    }
    return null;
  }

  private static ASTSeriesData polynomialSeries(final IExpr function, IExpr x, IExpr x0,
      final int n, Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    ExprPolynomialRing.create(function, x, coefficientMap, rest);
    if (coefficientMap.size() > 0) {
      return seriesFromMap(x, x0, n, coefficientMap, rest);
    }
    return null;
  }

  private static ASTSeriesData seriesFromMap(IExpr x, IExpr x0, final int n,
      Map<IExpr, IExpr> coefficientMap, IASTAppendable rest) {
    ASTSeriesData series = new ASTSeriesData(x, x0, 0, n + 1, 1);
    boolean evaled = false;
    for (Map.Entry<IExpr, IExpr> entry : coefficientMap.entrySet()) {
      IExpr coefficient = entry.getValue();
      if (coefficient.isZero()) {
        continue;
      }
      IExpr exp = entry.getKey();
      int exponent = exp.toIntDefault();
      if (exponent == Integer.MIN_VALUE) {
        rest.append(F.Times(coefficient, F.Power(x, exp)));
      } else {
        series.setCoeff(exponent, coefficient);
        evaled = true;
      }
    }
    if (evaled) {
      return series;
    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private SeriesFunctions() {}
}
