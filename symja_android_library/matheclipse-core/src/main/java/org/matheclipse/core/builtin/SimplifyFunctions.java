package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.Log;
import static org.matheclipse.core.expression.S.x;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherTimes;
import org.matheclipse.core.patternmatching.hash.HashedPatternRules;
import org.matheclipse.core.patternmatching.hash.HashedPatternRulesTimes;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.core.visit.VisitorExpr;

public class SimplifyFunctions {
  private static HashedOrderlessMatcherTimes TIMES_ORDERLESS_MATCHER;

  private static HashedOrderlessMatcherTimes initTimesHashMatcher() {
    HashedOrderlessMatcherTimes timesMatcher = new HashedOrderlessMatcherTimes();
    // Abs(z)*Sign(z) == z
    timesMatcher.defineHashRule(new HashedPatternRulesTimes( //
        F.Abs(x_), //
        F.Sign(x_), //
        F.x));
    return timesMatcher;
  }

  public static class SimplifiedResult {
    IExpr result;

    long minCounter;

    final Function<IExpr, Long> complexityFunction;

    public SimplifiedResult(IExpr result, IExpr minExpr, Function<IExpr, Long> complexityFunction) {
      this.result = result;
      this.complexityFunction = complexityFunction;
      this.minCounter = complexityFunction.apply(minExpr);
    }

    public SimplifiedResult(IExpr minExpr, Function<IExpr, Long> complexityFunction) {
      this(minExpr, minExpr, complexityFunction);
    }

    public boolean checkLessEqual(IExpr expr) {
      long counter = complexityFunction.apply(expr);
      if (counter <= this.minCounter) {
        this.minCounter = counter;
        this.result = expr;
        return true;
      }
      return false;
    }

    public boolean checkLess(IExpr expr) {
      long counter = complexityFunction.apply(expr);
      if (counter < this.minCounter) {
        this.minCounter = counter;
        this.result = expr;
        return true;
      }
      return false;
    }

    public IExpr getResult() {
      return result;
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FullSimplify.setEvaluator(new FullSimplify());
      S.Simplify.setEvaluator(new Simplify());
    }
  }

  /**
   *
   *
   * <pre>
   * Simplify(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * simplifies <code>expr</code>
   *
   * </blockquote>
   *
   * <pre>
   * Simplify(expr, option1, option2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * simplify <code>expr</code> with some additional options set
   *
   * </blockquote>
   *
   * <ul>
   * <li>Assumptions - use assumptions to simplify the expression
   * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Simplify(1/2*(2*x+2))
   * x+1
   *
   * &gt;&gt; Simplify(2*Sin(x)^2 + 2*Cos(x)^2)
   * 2
   *
   * &gt;&gt; Simplify(x)
   * x
   *
   * &gt;&gt; Simplify(f(x))
   * f(x)
   *
   * &gt;&gt; Simplify(a*x^2+b*x^2)
   * (a+b)*x^2
   * </pre>
   *
   * <p>
   * Simplify with an assumption:
   *
   * <pre>
   * &gt;&gt; Simplify(Sqrt(x^2), Assumptions -&gt; x&gt;0)
   * x
   * </pre>
   *
   * <p>
   * For <code>Assumptions</code> you can define the assumption directly as second argument:
   *
   * <pre>
   * &gt;&gt; Simplify(Sqrt(x^2), x&gt;0)
   * x
   * </pre>
   *
   * <pre>
   * ```
   * &gt;&gt; Simplify(Abs(x), x&lt;0)
   * Abs(x)
   * </pre>
   *
   * <p>
   * With this &ldquo;complexity function&rdquo; the <code>Abs</code> expression gets a
   * &ldquo;heavier weight&rdquo;.
   *
   * <pre>
   * &gt;&gt; complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)
   *
   * &gt;&gt; Simplify(Abs(x), x&lt;0, ComplexityFunction-&gt;complexity)
   * -x
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FullSimplify.md">FullSimplify</a>
   */
  private static class Simplify extends AbstractFunctionEvaluator {
    private static HashedOrderlessMatcherPlus PLUS_ORDERLESS_MATCHER =
        new HashedOrderlessMatcherPlus();

    static {
      // Cosh(x)+Sinh(x) -> Exp(x)
      PLUS_ORDERLESS_MATCHER.defineHashRule(new HashedPatternRules( //
          F.Cosh(x_), //
          F.Sinh(x_), //
          F.Exp(x), //
          false, //
          null, true));
    }

    private static class IsBasicExpressionVisitor extends AbstractVisitorBoolean {
      public IsBasicExpressionVisitor() {
        super();
      }

      @Override
      public boolean visit(IAST ast) {
        if (ast.isTimes() || ast.isPlus()) {
          // check the arguments
          return ast.forAll(x -> x.accept(this));
        }
        if (ast.isPowerInteger()) {
          // check the arguments
          return ast.base().accept(this);
        }
        return false;
      }

      @Override
      public boolean visit(IComplex element) {
        return true;
      }

      @Override
      public boolean visit(IComplexNum element) {
        return true;
      }

      @Override
      public boolean visit(IFraction element) {
        return true;
      }

      @Override
      public boolean visit(IInteger element) {
        return true;
      }

      @Override
      public boolean visit(INum element) {
        return true;
      }

      @Override
      public boolean visit(ISymbol symbol) {
        return true;
      }
    }



    private static class SimplifyVisitor extends VisitorExpr {
      final IsBasicExpressionVisitor isBasicAST = new IsBasicExpressionVisitor();
      /**
       * This function is used to determine the “weight” of an expression. For example by counting
       * the leafs of an expression with the <code>IExpr#leafCountSimplify()</code> method.
       */
      final Function<IExpr, Long> fComplexityFunction;

      /** If <code>true</code> we are in full simplify mode (i.e. function FullSimplify) */
      final boolean fFullSimplify;

      final boolean fNoApart;

      /** The current evlaution engine */
      final EvalEngine fEngine;

      public SimplifyVisitor(Function<IExpr, Long> complexityFunction, boolean fullSimplify,
          EvalEngine engine, boolean noApart) {
        super();
        fEngine = engine;
        fComplexityFunction = complexityFunction;
        fFullSimplify = fullSimplify;
        fNoApart = noApart;
      }

      private IExpr eval(IExpr a) {
        return fEngine.evaluate(a);
      }

      private IExpr tryExpandTransformation(IAST plusAST, IExpr test) {
        IExpr result = F.NIL;
        long minCounter = fComplexityFunction.apply(plusAST);
        IExpr temp;
        long count;

        try {
          temp = F.evalExpand(test);
          count = fComplexityFunction.apply(temp);
          if (count < minCounter) {
            result = temp;
          }
        } catch (RuntimeException rex) {
          //
        }

        return result;
      }

      private IExpr tryTransformations(IExpr expr) {
        if (!expr.isAST()) {
          return F.NIL;
        }
        try {
          // try ExpandAll, Together, Apart, Factor to reduce the expression
          SimplifiedResult simplifiedResult = new SimplifiedResult(expr, fComplexityFunction);
          IExpr temp;
          long expandAllCounter = 0;
          if (expr.isTimes()) {
            temp = tryTimesLog((IAST) expr);
            if (temp.isPresent()) {
              simplifiedResult.checkLessEqual(temp);
            }
          } else if (expr.isPlus()) {
            temp = Algebra.factorTermsPlus((IAST) expr, EvalEngine.get());
            if (temp.isPresent()) {
              simplifiedResult.checkLessEqual(temp);
            }

            Optional<IExpr[]> commonFactors =
                Algebra.InternalFindCommonFactorPlus.findCommonFactors((IAST) expr, true);
            if (commonFactors.isPresent()) {
              temp = eval(F.Times(commonFactors.get()[0], commonFactors.get()[1]));
              simplifiedResult.checkLessEqual(temp);
            }

            if (simplifiedResult.result.isPlus()) {
              temp = tryPlusLog((IAST) simplifiedResult.result);
            } else {
              temp = tryPlusLog((IAST) expr);
            }
            if (temp.isPresent()) {
              temp = eval(temp);
              simplifiedResult.checkLessEqual(temp);
            }
            // } else if (expr.isExp() && expr.second().isTimes()) {
            // IAST times = (IAST) expr.second();
            // IExpr i = Times.of(times, F.CNI, F.Power(F.Pi, F.CN1));
            // if (i.isRational()) {
            // IRational rat = (IRational) i;
            // if (rat.isGT(F.C1) || rat.isLE(F.CN1)) {
            // IInteger t = rat.trunc();
            // t = t.add(t.mod(F.C2));
            // // exp(I*(i - t)*Pi)
            // return F.Exp.of(F.Times(F.CI, F.Pi, F.Subtract(i, t)));
            // } else {
            // IRational t1 = rat.multiply(F.C6).normalize();
            // IRational t2 = rat.multiply(F.C4).normalize();
            // if (t1.isInteger() || t2.isInteger()) {
            // // Cos(- I*times) + I*Sin(- I*times)
            // return F.Plus.of(F.Cos(F.Times(F.CNI, times)),
            // F.Times(F.CI, F.Sin(F.Times(F.CNI, times))));
            // }
            // }
            // }
          }

          if (simplifiedResult.result.isAST()) {
            expr = simplifiedResult.result;
          }

          try {
            temp = F.evalExpandAll(expr);
            expandAllCounter = fComplexityFunction.apply(temp);
            simplifiedResult.checkLess(temp);
          } catch (RuntimeException rex) {
            //
          }

          if (simplifiedResult.result.isAST()) {
            expr = simplifiedResult.result;
          }

          if (((IAST) expr).hasTrigonometricFunction()) {

            try {
              temp = eval(F.TrigExpand(expr));
              simplifiedResult.checkLess(temp);
            } catch (ValidateException ve) {
              //
            }

            try {
              temp = eval(F.TrigToExp(expr));
              if (!simplifiedResult.checkLess(temp)) {
                if (fFullSimplify) {
                  temp = eval(F.Factor(temp));
                  simplifiedResult.checkLess(temp);
                }
              }
            } catch (ValidateException ve) {
              //
            }

            try {
              temp = eval(F.TrigReduce(expr));
              simplifiedResult.checkLess(temp);
            } catch (ValidateException ve) {
              //
            }
          }

          try {
            temp = eval(F.ExpToTrig(expr));
            simplifiedResult.checkLess(temp);
          } catch (ValidateException ve) {
            //
          }

          try {
            IExpr together = expr;
            if (simplifiedResult.minCounter < Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
              together = eval(F.Together(expr));
              simplifiedResult.checkLess(together);
            }

            if (fFullSimplify) {
              if (together.isTimes()) {
                IExpr[] parts =
                    Algebra.numeratorDenominator((IAST) together, true, EvalEngine.get());
                IExpr numerator = parts[0];
                IExpr denominator = parts[1];
                // common factors in numerator, denominator may be canceled here, so check if we
                // have
                // a new minimal expression
                IExpr divide = F.Divide(parts[0], parts[1]);
                simplifiedResult.checkLess(divide);

                if (!numerator.isOne() && //
                    !denominator.isOne()) {
                  tryPolynomialQuotientRemainder(numerator, denominator, simplifiedResult);
                }
              }
            }

          } catch (ValidateException wat) {
            //
          }

          try {
            // TODO: Factor is not fast enough for large expressions!
            // Maybe restricting factoring to smaller expressions is necessary here
            temp = F.NIL;
            if (fFullSimplify && expandAllCounter < 50) { // Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
              temp = eval(F.Factor(expr));
              simplifiedResult.checkLess(temp);
            }
            // if (fFullSimplify
            // && (minCounter >= Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT || !temp.equals(expr))) {
            // if (expandAllCounter < (Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT / 2) && !fFullSimplify)
            // {
            // temp = eval(F.Factor(expr));
            // count = fComplexityFunction.apply(temp);
            // if (count < minCounter) {
            // minCounter = count;
            // result = temp;
            // }
            // } else
            if (expandAllCounter < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
              temp = eval(F.FactorSquareFree(expr));
              simplifiedResult.checkLess(temp);
            }

          } catch (ValidateException ve) {
            //
          }

          try {
            if (!fNoApart //
                && simplifiedResult.minCounter < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
              temp = eval(F.Apart(expr));
              simplifiedResult.checkLess(temp);
            }
          } catch (ValidateException ve) {
            //
          }
          return simplifiedResult.result;
        } catch (LimitException aele) {
          //
        }
        return F.NIL;
      }

      /**
       * Try <code>F.PolynomialQuotientRemainder(numerator, denominator, variable)</code> for
       * differnt variables and numerator, denominator combinations.
       *
       * @param numerator
       * @param denominator
       * @param sResult
       */
      private void tryPolynomialQuotientRemainder(IExpr numerator, IExpr denominator,
          SimplifiedResult sResult) {
        IExpr temp;
        VariablesSet variables = new VariablesSet(numerator);
        variables.addVarList(denominator);
        List<IExpr> vars = variables.getArrayList();
        boolean evaled = false;
        for (int i = 0; i < vars.size(); i++) {
          temp = EvalEngine.get()
              .evaluate(F.PolynomialQuotientRemainder(numerator, denominator, vars.get(i)));
          if (temp.isAST(S.List, 3) && //
              temp.second().isZero()) {
            // the remainder is 0 here:
            IExpr arg1 = temp.first();
            if (sResult.checkLess(arg1)) {
              evaled = true;
              break;
            }
          }
        }
        if (!evaled) {
          for (int i = 0; i < vars.size(); i++) {
            temp = EvalEngine.get()
                .evaluate(F.PolynomialQuotientRemainder(denominator, numerator, vars.get(i)));
            if (temp.isAST(S.List, 3) && //
                temp.second().isZero()) {
              // the remainder is 0 here:
              IExpr arg1 = temp.first().reciprocal();
              if (sResult.checkLess(arg1)) {
                break;
              }
            }
          }
        }
      }

      @Override
      public IExpr visit(IASTMutable ast) {
        SimplifiedResult sResult = new SimplifiedResult(F.NIL, ast, fComplexityFunction);

        IExpr temp = visitAST(ast);
        if (temp.isPresent()) {
          temp = eval(temp);
          if (sResult.checkLessEqual(temp)) {
            if (temp.isAST()) {
              ast = (IASTMutable) temp;
              // result = temp;
            } else {
              return temp;
            }
          }
          // long count = fComplexityFunction.apply(temp);
          // if (count <= minCounter[0]) {
          // minCounter[0] = count;
          // if (temp.isAST()) {
          // ast = (IASTMutable) temp;
          // result = temp;
          // } else {
          // return temp;
          // }
          // }
        }
        if (ast.isPower()) {
          temp = visitPower(ast, sResult);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (ast.isTimes()) {
          temp = visitTimes(ast, sResult);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (ast.isPlus()) {
          temp = visitPlus(ast, sResult);
          if (temp.isPresent()) {
            return temp;
          }
        }

        temp = sResult.result;
        if (temp.isPresent()) {
          if (temp.isAST()) {
            ast = (IASTMutable) temp;
          } else {
            return temp;
          }
        }
        temp = F.evalExpandAll(ast);
        sResult.checkLess(temp);

        functionExpand(ast, sResult);
        return sResult.result;
      }

      private IExpr visitPower(IASTMutable powerAST, SimplifiedResult sResult) {

        if (powerAST.isPowerReciprocal() && powerAST.base().isPlus()
            && powerAST.base().size() == 3) {
          // example 1/(5+Sqrt(17)) => 1/8*(5-Sqrt(17))
          IAST plus1 = (IAST) powerAST.base();
          IAST plus2 = plus1.setAtCopy(2, plus1.arg2().negate());
          // example (5+Sqrt(17)) * (5-Sqrt(17))
          IExpr expr = eval(F.Expand(F.Times(plus1, plus2)));
          if (expr.isNumber() && !expr.isZero()) {
            IExpr powerSimplified = S.Times.of(expr.inverse(), plus2);
            if (sResult.checkLess(powerSimplified)) {
              return powerSimplified;
            }
          }
        } else if (powerAST.base().isE() && powerAST.exponent().isPlus()) {
          // E^(a*Log(f)+b+Log(g)) ==> E^(b) * f^a * g
          IAST plusAST = (IAST) powerAST.exponent();
          IASTAppendable plusResult = F.NIL;
          IASTAppendable logFactor = F.NIL;
          for (int i = 1; i < plusAST.size(); i++) {
            IExpr plusArg = plusAST.get(i);
            if (plusArg.isTimes()) {
              IAST timesAST = (IAST) plusArg;
              int indx1 = timesAST.indexOf(x -> x.isLog());
              if (indx1 > 0) {
                int indx2 = timesAST.indexOf(x -> x.isLog(), indx1 + 1);
                if (indx2 < 0) {
                  if (plusResult.isNIL()) {
                    plusResult = plusAST.copyUntil(plusAST.size() - 1, i);
                    logFactor = F.TimesAlloc(10);
                  }
                  logFactor
                      .append(F.Power(timesAST.get(indx1).first(), timesAST.removeAtCopy(indx1)));
                  continue;
                }
              }
            } else if (plusArg.isLog()) {
              if (plusResult.isNIL()) {
                plusResult = plusAST.copyUntil(plusAST.size() - 1, i);
                logFactor = F.TimesAlloc(10);
              }
              logFactor.append(plusArg.first());
              continue;
            }
            if (plusResult.isPresent()) {
              plusResult.append(plusArg);
            }
          }

          if (plusResult.isPresent()) {
            logFactor.append(F.Power(S.E, plusResult));
            IExpr temp = eval(logFactor);
            sResult.checkLessEqual(temp);
          }
        }
        return F.NIL;
      }

      private IExpr visitTimes(IASTMutable timesAST, SimplifiedResult sResult) {
        final IExpr denominator = eval(F.Denominator(timesAST));
        if (!denominator.isNumber()) {
          final IExpr numerator = eval(F.Numerator(timesAST));
          if (numerator.isAST(S.RealAbs, 2) && numerator.first().equals(denominator)
              && denominator.isPlus() && denominator.first().isRational()) {
            IRational n = (IRational) denominator.first();
            IExpr rest = denominator.rest().oneIdentity0();
            // Piecewise({{-1,rest<(-n)}},1)
            return F.Piecewise(F.list(F.list(F.CN1, F.Less(rest, n.negate()))), F.C1);
          } else if (denominator.isAST(S.RealAbs, 2) && denominator.first().equals(numerator)
              && numerator.isPlus() && numerator.first().isRational()) {
            IRational n = (IRational) numerator.first();
            IExpr rest = numerator.rest().oneIdentity0();
            // Piecewise({{-1,rest<(-n)}},1)
            return F.Piecewise(F.list(F.list(F.CN1, F.Less(rest, n.negate()))), F.C1);
          }
          if (fFullSimplify || numerator.isTimes() || denominator.isTimes()) {
            IExpr numer = F.evalExpandAll(numerator);
            IExpr denom = F.evalExpandAll(denominator);
            if (S.PossibleZeroQ.ofQ(F.Subtract(numer, denom))) {
              return F.C1;
            }
          }
        }

        IExpr temp = reduceNumberFactor(timesAST);
        if (temp.isPresent()) {
          sResult.result = temp;
          sResult.minCounter = fComplexityFunction.apply(temp);
        }

        temp = reduceConjugateFactors(timesAST, sResult);
        if (temp.isPresent()) {
          return temp;
        }

        temp = tryTransformations(sResult.result.orElse(timesAST));
        if (temp.isPresent()) {
          sResult.result = temp;
        }
        temp = sResult.result.orElse(timesAST);
        sResult.minCounter = fComplexityFunction.apply(temp);
        functionExpand(temp, sResult); // minCounter[0], result);
        return F.NIL;
      }

      /**
       * Try reducing for {@link S#Plus} expressions in the denominators of the {@link S#Times}
       * function by creating a conjugate expression and use the rule
       * <code>(a+b)*(a-b) == a^2 - b^2</code>
       * 
       * @param timesAST
       * @param sResult
       */
      private IExpr reduceConjugateFactors(IASTMutable timesAST, SimplifiedResult sResult) {
        IExpr temp;
        IASTAppendable newTimes = F.NIL;
        int i = 1;
        int lastIndex = -1;
        INumber numberFactors = F.C1;
        IExpr exprFactors = F.C1;
        while (i < timesAST.size()) {
          IExpr timesArg = timesAST.get(i);
          if (timesArg.isPower()) {
            IExpr base = timesArg.base();
            IExpr exponent = timesArg.exponent();
            if (timesArg.isPowerReciprocal()) {
              if (base.isPlus() && base.argSize() >= 2 && base.argSize() <= 10) {
                // try multiplying the conjugate
                // example plusDenominator(5+Sqrt(17)) => plusConjugate(5-Sqrt(17))
                IAST plusDenominator = (IAST) base;
                // search for prefered Sqrt() expressions
                int index = plusDenominator.lastIndexOf(x -> (x.isSqrt() && x.first().isNumber()) //
                    || (x.isTimes() && x.last().isSqrt() && x.last().first().isNumber()));
                if (index == -1) {
                  index = plusDenominator.lastIndexOf(x -> x.isSqrt() //
                      || (x.isTimes() && x.last().isSqrt()));
                  if (index == -1) {
                    // fall back to default
                    index = plusDenominator.argSize();
                  }
                }
                IAST plusConjugate =
                    plusDenominator.setAtCopy(index, plusDenominator.get(index).negate());
                // example (5+Sqrt(17)) * (5-Sqrt(17))
                IExpr newDenominator = eval(F.Expand(F.Times(plusDenominator, plusConjugate)));
                if (!newDenominator.isZero()
                    && newDenominator.leafCount() < plusDenominator.leafCount()) {
                  IExpr inversedDenominator = newDenominator.inverse();
                  if (inversedDenominator.isNumber()) {
                    numberFactors = numberFactors.times((INumber) inversedDenominator);
                  } else {
                    exprFactors = exprFactors.times(inversedDenominator);
                  }
                  // replace the reciprocal Power in the timesAST[i] with the plusConjugate
                  if (newTimes.isPresent()) {
                    newTimes.set(i, plusConjugate);
                  } else {
                    newTimes = timesAST.setAtClone(i, plusConjugate);
                  }
                  i++;
                  continue; // while
                }
              }
            }
            if ((i + 1 < timesAST.size())
                && ((fFullSimplify && base.isAST()) || (base.isPlus() && base.first().isReal()))) {
              IExpr rhs = timesAST.get(i + 1);
              if (rhs.isPower() && rhs.exponent().equals(exponent) //
                  && ((fFullSimplify && rhs.base().isAST())
                      || (rhs.base().isPlus() && rhs.base().first().equals(base.first())))) {
                if (fFullSimplify) {
                  IAST test = F.Times(base, rhs.base());
                  long minCounter = fComplexityFunction.apply(test);
                  temp = simplifyStep(test, F.NIL, fComplexityFunction, minCounter, fFullSimplify,
                      false, fEngine);
                  if (temp.isPresent()) {
                    IAST powerSimplified = F.Power(temp, rhs.exponent());
                    if (newTimes.isPresent()) {
                      newTimes.set(i, powerSimplified);
                      newTimes.remove(i + 1);
                    } else {
                      newTimes = timesAST.setAtClone(i, powerSimplified);
                      newTimes.remove(i + 1);
                    }
                    i++;
                    continue; // while
                  }
                } else {
                  IExpr lhsRest = base.rest();
                  IExpr rhsRest = rhs.base().rest();
                  IExpr zeroCandidate = eval(F.Plus(lhsRest, rhsRest));
                  if (zeroCandidate.isZero()) {
                    // found something like: (2-rest)^(z) * (2+rest)^(z) ==> (4-rest^2)^(z)
                    IAST powerSimplified = F.Power(
                        F.Subtract(F.Sqr(rhs.base().first()), F.Sqr(lhsRest)), rhs.exponent());
                    if (newTimes.isPresent()) {
                      newTimes.set(i, powerSimplified);
                      newTimes.remove(i + 1);
                    } else {
                      newTimes = timesAST.setAtClone(i, powerSimplified);
                      newTimes.remove(i + 1);
                    }
                    i++;
                    continue; // while
                  }
                }
              }
            }
          }

          if (timesArg.isPlus()) {
            IExpr negExpr =
                AbstractFunctionEvaluator.getNormalizedNegativeExpression(timesArg.first());
            if (negExpr.isPresent()) {
              // try avoiding negative expressions in the first arg of Plus()
              if (lastIndex < 0) {
                lastIndex = i;
              } else {

                if (newTimes.isNIL()) {
                  newTimes = timesAST.copyAppendable();
                }
                newTimes.set(lastIndex, timesAST.get(lastIndex).negate());
                newTimes.set(i, timesArg.negate());
                lastIndex = -1;
                i++;
                continue; // while
              }
            }
          }
          i++;
        }
        if (newTimes.isPresent()) {
          sResult.result = timesAST;
          try {
            if (exprFactors.isOne()) {
              temp = eval(newTimes);
              IExpr temp2 = numberFactors.times(temp);
              if (sResult.checkLessEqual(temp2)) {
                if (temp2.isAtom()) {
                  return temp2;
                }
              }
            } else {
              temp = F.Times(numberFactors, exprFactors, newTimes);
            }
            temp = eval(F.Expand(temp));
            temp = numberFactors.times(temp);
            if (sResult.checkLess(temp)) {
              if (temp.isAtom()) {
                return temp;
              }
            }
            if (temp.isTimes()) {
              temp = eval(F.Expand(temp));
              if (sResult.checkLess(temp)) {
                if (temp.isAtom()) {
                  return temp;
                }
              }
            }

          } catch (RuntimeException rex) {
            Errors.printMessage(fFullSimplify ? S.FullSimplify : S.Simplify, rex, EvalEngine.get());
          }
        }
        return F.NIL;
      }

      private IExpr visitPlus(IASTMutable plusAST, SimplifiedResult sResult) {
        IExpr temp = tryArg1IsOnePlus(plusAST, sResult);
        if (temp.isPresent()) {
          return temp;
        }
        temp = sResult.result;
        if (temp.isPlus()) {
          plusAST = (IASTMutable) sResult.result;
        }

        IASTAppendable basicPlus = F.PlusAlloc(plusAST.size());
        IASTAppendable restPlus = F.PlusAlloc(plusAST.size());
        plusAST.forEach(x -> {
          if (x.accept(isBasicAST)) {
            basicPlus.append(x);
          } else {
            restPlus.append(x);
          }
        });
        if (basicPlus.size() > 1) {
          temp = tryTransformations(basicPlus.oneIdentity0());
          if (temp.isPresent()) {
            if (!restPlus.isAST0()) {
              temp = eval(F.Plus(temp, restPlus));
            }
            if (!temp.isPlus()) {
              return temp;
            }
            if (sResult.checkLess(temp)) {
              temp = sResult.result;
              if (temp.isPlus()) {
                plusAST = (IASTMutable) sResult.result;
              }
            }
          }
        }

        temp = tryTransformations(plusAST);
        if (temp.isPresent()) {
          if (sResult.checkLessEqual(temp)) {
            temp = sResult.result;
            if (temp.isPlus()) {
              plusAST = (IASTMutable) sResult.result;
            } else {
              return temp;
            }
          }
        }

        if (fFullSimplify) {
          HashedOrderlessMatcher hashRuleMap = PLUS_ORDERLESS_MATCHER;
          if (hashRuleMap != null) {
            plusAST.setEvalFlags(plusAST.getEvalFlags() ^ IAST.IS_HASH_EVALED);
            temp = hashRuleMap.evaluateRepeated(plusAST, fEngine);
            if (temp.isPresent()) {
              return eval(temp);
            }
          }
          functionExpand(plusAST, sResult);
        }

        return sResult.result;
      }

      /** No special function expression was found in the args of the expression */
      private static final int UNDEFINED = -1;

      /** A trigonometric or hyperbolic function <code>trig(x)^2</code> was found. */
      private static final int SQR_ARG = 1;

      /** A trigonometric or hyperbolic function <code>-trig(x)^2</code> was found. */
      private static final int NEGATIVE_SQR_ARG = 2;

      /**
       * Check if <code>plusAST</code> has the form <code>+/- 1 + ... + ... </code>. Try to find a
       * trigonometric or hyperbolic function <code>+/- trig(x)^2</code> in the rest of the <code>
       * plusAST</code> and simplify if possible.
       *
       * @param plusAST
       * @return <code>F.NIL</code> if no simplification was found
       */
      private IExpr tryArg1IsOnePlus(IASTMutable plusAST, SimplifiedResult sResult) {
        IExpr plusArg1 = plusAST.arg1();
        if (plusArg1.isOne() || plusArg1.isMinusOne()) {
          int iterIndx = 2;
          while (iterIndx > 0) {
            int[] indx = plusASTIndexOf(plusAST, iterIndx);
            if (indx[0] > 0) {
              IExpr transformResult = F.NIL;
              boolean negate = false;
              if (indx[1] == SQR_ARG) {
                IAST power = (IAST) plusAST.get(indx[0]);

                IAST trigFunction = (IAST) power.base();
                int id = trigFunction.headID();
                IExpr arg1 = trigFunction.arg1();
                if (plusArg1.isOne()) {
                  // 1+...
                  switch (id) {
                    case ID.Cot:
                      // 1+Cot(x)^2....
                      transformResult = F.Csc(arg1);
                      break;
                    case ID.Csch:
                      // 1+Csch(x)^2....
                      transformResult = F.Coth(arg1);
                      break;
                    case ID.Sinh:
                      // 1+Sinh(x)^2....
                      transformResult = F.Cosh(arg1);
                      break;
                    case ID.Tan:
                      // 1+Tan(x)^2....
                      transformResult = F.Sec(arg1);
                      break;
                    default:
                  }
                } else {
                  // -1+....
                  switch (id) {
                    case ID.Cos:
                      // -1+Cos(x)^2....
                      transformResult = F.Sin(arg1);
                      negate = true;
                      break;
                    case ID.Csc:
                      // -1+Csc(x)^2....
                      transformResult = F.Cot(arg1);
                      break;
                    case ID.Cosh:
                      // -1+Cosh(x)^2....
                      transformResult = F.Sinh(arg1);
                      break;
                    case ID.Coth:
                      // -1+Coth(x)^2....
                      transformResult = F.Csch(arg1);
                      break;
                    case ID.Sec:
                      // -1+Sec(x)^2....
                      transformResult = F.Tan(arg1);
                      break;
                    case ID.Sech:
                      // -1+Sech(x)^2....
                      transformResult = F.Tanh(arg1);
                      negate = true;
                      break;
                    case ID.Sin:
                      // -1+Sin(x)^2....
                      transformResult = F.Cos(arg1);
                      negate = true;
                      break;
                    case ID.Tanh:
                      // -1+Tanh(x)^2....
                      transformResult = F.Sech(arg1);
                      negate = true;
                      break;
                    default:
                  }
                }
              } else if (indx[1] == NEGATIVE_SQR_ARG) {
                IAST power = (IAST) plusAST.get(indx[0]).second();
                IAST trigFunction = (IAST) power.base();
                int id = trigFunction.headID();
                IExpr arg1 = trigFunction.arg1();
                if (plusArg1.isOne()) {
                  // 1 - ...
                  switch (id) {
                    case ID.Cos:
                      // 1-Cos(x)^2....
                      transformResult = F.Sin(arg1);
                      break;
                    case ID.Cosh:
                      // 1-Cosh(x)^2....
                      transformResult = F.Sinh(arg1);
                      negate = true;
                      break;
                    case ID.Coth:
                      // 1-Coth(x)^2....
                      transformResult = F.Csch(arg1);
                      negate = true;
                      break;
                    case ID.Csc:
                      // 1-Csc(x)^2....
                      transformResult = F.Cot(arg1);
                      negate = true;
                      break;
                    case ID.Sec:
                      // 1-Sec(x)^2....
                      transformResult = F.Tan(arg1);
                      negate = true;
                      break;
                    case ID.Sech:
                      // 1-Sech(x)^2....
                      transformResult = F.Tanh(arg1);
                      break;
                    case ID.Sin:
                      // 1-Sin(x)^2....
                      transformResult = F.Cos(arg1);
                      break;
                    case ID.Tanh:
                      // 1-Tanh(x)^2....
                      transformResult = F.Sech(arg1);
                      break;
                    default:
                  }
                } else {
                  // -1 - ...
                  switch (id) {
                    case ID.Cot:
                      // -1-Cot(x)^2....
                      transformResult = F.Csc(arg1);
                      negate = true;
                      break;
                    case ID.Csch:
                      // -1-Csch(x)^2....
                      transformResult = F.Coth(arg1);
                      negate = true;
                      break;
                    case ID.Tan:
                      // -1-Tan(x)^2....
                      transformResult = F.Sec(arg1);
                      negate = true;
                      break;
                    case ID.Sinh:
                      // -1-Sinh(x)^2....
                      transformResult = F.Cosh(arg1);
                      negate = true;
                      break;
                    default:
                      break;
                  }
                }
              }

              if (transformResult.isPresent()) {
                // remove -1 or +1 from first position
                IASTMutable result = plusAST.removeAtCopy(1);
                if (negate) {
                  result.set(indx[0] - 1, F.Power(transformResult, F.C2).negate());
                } else {
                  result.set(indx[0] - 1, F.Power(transformResult, F.C2));
                }
                IExpr temp = result.oneIdentity0();
                if (temp.isPlus()) {
                  sResult.checkLess(temp);
                  return F.NIL;
                }
                return temp;
              }
              iterIndx = indx[0] + 1;
              continue;
            }
            return F.NIL;
          }
        }
        return F.NIL;
      }

      /**
       * Return a value > 0 at index <code>0</code>, if a power trig- or hyperbolicfunction was
       * found. The determined type <code>SQR_ARG,NEGATIVE_SQR_ARG</code> of the expression at index
       * <code>1</code>.
       *
       * @param plusAST the <code>Plus( ... )</code> expression
       * @param fromPosition start searching at this index inclusive.
       * @return a value > 0 at index <code>0</code>, if a power trig- or hyperbolicfunction was
       *         found. The type of expression found at index <code>1</code>.
       */
      private static int[] plusASTIndexOf(IASTMutable plusAST, int fromPosition) {
        for (int i = fromPosition; i < plusAST.size(); i++) {
          IExpr x = plusAST.get(i);
          if (x.isPower() && x.exponent().isNumEqualInteger(F.C2) && x.base().size() == 2 && //
              (x.base().isTrigFunction() || x.base().isHyperbolicFunction())) {
            return new int[] {i, SQR_ARG};
          } else if (x.isAST(S.Times, 3) && x.first().isMinusOne() && x.second().isPower() && //
              x.second().exponent().isNumEqualInteger(F.C2) && x.second().base().size() == 2 && //
              (x.second().base().isTrigFunction() || x.second().base().isHyperbolicFunction())) {
            return new int[] {i, NEGATIVE_SQR_ARG};
          }
        }
        return new int[] {-1, UNDEFINED};
      }

      /**
       * Simplify <code>Log(x)+Log(y)+p*Log(z)</code> if x, y, z are real numbers and p is an
       * integer number
       *
       * @param plusAST
       * @return
       */
      private static IExpr tryPlusLog(IAST plusAST) {
        if (plusAST.size() > 2) {
          IASTAppendable logPlus = F.PlusAlloc(plusAST.size());
          IExpr a1 = F.NIL;
          boolean evaled = false;
          for (int i = 1; i < plusAST.size(); i++) {
            IExpr a2 = plusAST.get(i);
            IExpr arg = F.NIL;
            if (a2.isAST(S.Times, 3) && a2.first().isInteger() && //
                a2.second().isLog() && a2.second().first().isReal()) {
              arg = S.Power.of(a2.second().first(), a2.first());
            } else if (a2.isLog() && a2.first().isReal()) {
              arg = a2.first();
            }
            if (arg.isReal()) {
              if (a1.isPresent()) {
                a1 = a1.multiply(arg);
                evaled = true;
              } else {
                a1 = arg;
              }
              continue;
            }
            logPlus.append(a2);
          }
          if (evaled) {
            if (logPlus.isEmpty()) {
              return Log.of(a1);
            } else {
              logPlus.append(Log(a1));
              return logPlus;
            }
          }
        }
        return F.NIL;
      }

      private static IExpr tryTimesLog(IAST timesAST) {
        if (timesAST.size() > 2 && timesAST.first().isInteger() && !timesAST.first().isMinusOne()) {

          for (int i = 2; i < timesAST.size(); i++) {
            IExpr temp = timesAST.get(i);
            if (temp.isLog() && temp.first().isReal()) {
              IAST result =
                  timesAST.splice(i, 1, F.Log(S.Power.of(temp.first(), timesAST.first())));
              return result.splice(1).oneIdentity0();
            }
          }
        }
        return F.NIL;
      }

      private void functionExpand(IExpr expr, SimplifiedResult sResult) { // long minCounter, IExpr
                                                                          // result) {
        if (expr.isBooleanFunction()) {
          try {
            expr = eval(F.BooleanMinimize(expr));
            sResult.checkLess(expr);
            return;
          } catch (RuntimeException rex) {
            //
          }
        } else if (fFullSimplify) {
          if (expr.isAST(S.Arg, 2)) {
            try {
              IExpr re = expr.first().re();
              IExpr im = expr.first().im();
              IExpr temp = argReXImY(re, im, fEngine);
              sResult.checkLess(temp);
            } catch (RuntimeException rex) {
              //
            }
          } else if (expr.isTimes()) {
            try {
              HashedOrderlessMatcher hashRuleMap = TIMES_ORDERLESS_MATCHER;
              if (hashRuleMap != null) {
                IAST temp = TIMES_ORDERLESS_MATCHER.evaluateRepeatedNoCache((IAST) expr, fEngine);
                if (temp.isPresent()) {
                  sResult.checkLess(temp);
                }
              }
            } catch (RuntimeException rex) {
              //
            }
          }
          try {
            expr = eval(F.FunctionExpand(expr));
            sResult.checkLess(expr);
          } catch (RuntimeException rex) {
            //
          }
        } else {
          if (expr.isLog() //
              || (expr.isPower() && expr.first().isAbs())) {
            try {
              expr = eval(F.FunctionExpand(expr));
              sResult.checkLessEqual(expr);
            } catch (RuntimeException rex) {
              //
            }
          }
        }
      }

      private IExpr reduceNumberFactor(IASTMutable timesAST) {
        IExpr temp;
        IASTAppendable basicTimes = F.TimesAlloc(timesAST.size());
        IASTAppendable restTimes = F.TimesAlloc(timesAST.size());
        INumber number = null;
        IExpr arg1 = timesAST.arg1();

        if (arg1.isNumber()) {
          if (!arg1.isZero()) {
            number = (INumber) arg1;
          }
        } else if (arg1.isPlus()) { // && arg1.first().isNumber()) {
          long minCounter = fComplexityFunction.apply(arg1);
          IExpr imPart = AbstractFunctionEvaluator.getComplexExpr(arg1.first(), F.CI);
          if (imPart.isPresent()) {
            IExpr negativeAST = eval(F.Distribute(F.Times(F.CI, arg1)));
            long count = fComplexityFunction.apply(negativeAST);
            if (count <= minCounter) {
              return eval(F.Times(negativeAST, F.Distribute(F.Times(F.CNI, timesAST.rest()))));
            }
          } else {
            IExpr negativeAST = eval(F.Distribute(F.Times(F.CN1, arg1)));
            long count = fComplexityFunction.apply(negativeAST);
            if (count <= minCounter) {
              IASTAppendable result = F.TimesAlloc(timesAST.size());
              result.append(F.CN1);
              result.append(negativeAST);
              result.appendAll(timesAST, 2, timesAST.size());
              return result;
            }
          }
        }
        IExpr reduced = F.NIL;
        for (int i = 1; i < timesAST.size(); i++) {
          temp = timesAST.get(i);
          if (temp.accept(isBasicAST)) {
            if (i != 1 && number != null) {
              if (temp.isPlus()) {
                // <number> * Plus[.....]
                reduced = tryExpand(timesAST, (IAST) temp, number, i, false);
              } else if (temp.isPowerReciprocal() && temp.base().isPlus()) {
                // <number> * Power[Plus[...], -1 ]
                reduced = tryExpand(timesAST, (IAST) temp.base(), number.inverse(), i, true);
              }
              if (reduced.isPresent()) {
                return reduced;
              }
            }
            basicTimes.append(temp);
          } else {
            restTimes.append(temp);
          }
        }

        if (basicTimes.size() > 1) {
          temp = tryTransformations(basicTimes.oneIdentity0());
          if (temp.isPresent()) {
            if (restTimes.isAST0()) {
              return temp;
            }
            return F.Times(temp, restTimes);
          }
        }
        return F.NIL;
      }

      private IExpr tryExpand(IAST timesAST, IAST plusAST, INumber arg1, int i,
          boolean isPowerReciprocal) {
        IExpr expandedAst = tryExpandTransformation(plusAST, F.Times(arg1, plusAST));
        if (expandedAst.isPresent()) {
          IASTAppendable result = F.TimesAlloc(timesAST.size());
          // ast.range(2, ast.size()).toList(result.args());
          result.appendAll(timesAST, 2, timesAST.size());
          if (isPowerReciprocal) {
            result.set(i - 1, F.Power(expandedAst, F.CN1));
          } else {
            result.set(i - 1, expandedAst);
          }
          return result;
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAtom() && ast.isAST1()) {
        return arg1;
      }
      if (arg1.isAST()) {
        IAST list1 = (IAST) arg1;
        int headID = list1.headID();
        switch (headID) {
          case ID.List:
            return list1.mapThread(ast, 1);
          case ID.Rule:
            if (list1.size() == 3) {
              return F.Rule(ast.setAtClone(1, list1.arg1()), ast.setAtClone(1, list1.arg2()));
            }
            break;
          case ID.Equal:
          case ID.Unequal:
          case ID.Greater:
          case ID.GreaterEqual:
          case ID.Less:
          case ID.LessEqual:
            if (list1.size() == 3 && !list1.arg2().isZero()) {
              IExpr sub = ast.setAtClone(1, F.Subtract(list1.arg1(), list1.arg2()));
              return F.binaryAST2(list1.head(), sub, F.C0);
            }
            break;
        }
      }

      // note: this should also cache FullSimplify calls
      IExpr defaultResult = engine.getCache(ast);
      if (defaultResult != null) {
        return defaultResult;
      }

      IExpr complexityFunctionHead = F.NIL;
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        complexityFunctionHead = options.getOptionAutomatic(S.ComplexityFunction);
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);

      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        Function<IExpr, Long> complexityFunction =
            createComplexityFunction(complexityFunctionHead, engine);
        long minCounter = complexityFunction.apply(arg1);
        defaultResult = arg1;
        long count = 0L;
        if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
          IAssumptions assumptions =
              org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
            arg1 = AssumptionFunctions.refineAssumptions(arg1, assumptions, engine);
            count = complexityFunction.apply(arg1);
            if (count <= minCounter) {
              minCounter = count;
              defaultResult = arg1;
            }
          }
        }

        IExpr temp = arg1.replaceAll(F.list( //
            F.Rule(S.GoldenAngle, //
                F.Times(F.Subtract(F.C3, F.CSqrt5), S.Pi)), //
            F.Rule(S.GoldenRatio, //
                F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))), //
            F.Rule(S.Degree, //
                F.Divide(S.Pi, F.ZZ(180))) //
        ));
        if (temp.isPresent()) {
          arg1 = temp;
        }

        temp = simplifyStep(arg1, defaultResult, complexityFunction, minCounter,
            isFullSimplifyMode(), false, engine);
        engine.putCache(ast, temp);
        return temp;

      } catch (ArithmeticException e) {
        //
      } finally {
        engine.setAssumptions(oldAssumptions);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.$Assumptions), //
              F.Rule(S.ComplexityFunction, S.Automatic)));
    }

    public boolean isFullSimplifyMode() {
      return false;
    }

  }

  /**
   *
   *
   * <pre>
   * FullSimplify(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * works like <code>Simplify</code> but additionally tries some <code>FunctionExpand</code> rule
   * transformations to simplify <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * FullSimplify(expr, option1, option2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * full simplifies <code>expr</code> with some additional options set
   *
   * </blockquote>
   *
   * <ul>
   * <li>Assumptions - use assumptions to simplify the expression
   * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))
   * True
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Simplify.md">Simplify</a>
   */
  private static class FullSimplify extends Simplify {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return super.evaluate(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean isFullSimplifyMode() {
      return true;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.$Assumptions), //
              F.Rule(S.ComplexityFunction, S.Automatic)));
      TIMES_ORDERLESS_MATCHER = initTimesHashMatcher();
    }
  }

  /**
   * Creata the complexity function which determines the &quot;more simplified&quot; expression.
   *
   * @param complexityFunctionHead
   * @param engine
   * @return
   */
  public static Function<IExpr, Long> createComplexityFunction(IExpr complexityFunctionHead,
      EvalEngine engine) {
    Function<IExpr, Long> complexityFunction = x -> x.leafCountSimplify();
    if (complexityFunctionHead.isPresent()) {
      final IExpr head = complexityFunctionHead;
      complexityFunction = x -> {
        IExpr temp = engine.evaluate(F.unaryAST1(head, x));
        if (temp.isInteger() && !temp.isNegative()) {
          return ((IInteger) temp).toLong();
        }
        return Long.MAX_VALUE;
      };
    }
    return complexityFunction;
  }

  public static IExpr simplifyStep(IExpr arg1, IExpr defaultResult, boolean fullSimplify,
      boolean noApart, EvalEngine engine) {
    Function<IExpr, Long> complexityFunction = createComplexityFunction(F.NIL, engine);
    long minCounter = complexityFunction.apply(arg1);
    return simplifyStep(arg1, defaultResult, complexityFunction, minCounter, fullSimplify, noApart,
        engine);
  }

  private static IExpr simplifyStep(IExpr arg1, IExpr defaultResult,
      Function<IExpr, Long> complexityFunction, long minCounter, boolean fullSimplify,
      boolean noApart, EvalEngine engine) {
    long count;
    IExpr temp;
    temp = arg1
        .accept(new Simplify.SimplifyVisitor(complexityFunction, fullSimplify, engine, noApart));
    while (temp.isPresent()) {
      count = complexityFunction.apply(temp);
      if (count == minCounter) {
        return temp;
      }
      if (count < minCounter) {
        minCounter = count;
        defaultResult = temp;
        temp = defaultResult.accept(
            new Simplify.SimplifyVisitor(complexityFunction, fullSimplify, engine, noApart));
      } else {
        return defaultResult;
      }
    }
    return defaultResult;
  }

  /**
   * Return <code>Arg(x+I*y)</code>. If possible, simplify <code>Arg(Re(z)+I*Im(z))</code> to
   * <code>Arg(z)</code>, or simplify <code>Arg(factor * (Re(z)+I*Im(z)))</code> to
   * <code>Arg( (+/- 1) * z)</code>.
   * 
   * @param realPart the real part
   * @param imaginaryPart the imaginary part
   * @return
   */
  public static IExpr argReXImY(IExpr realPart, IExpr imaginaryPart, EvalEngine engine) {
    // TODO: add this rule to FullSimplify
    IExpr factorTerms = engine.evaluate(F.FactorTerms(F.Plus(realPart, imaginaryPart)));
    boolean negativeFactor = false;
    if (factorTerms.isTimes2() && factorTerms.first().isReal()) {
      // a factor could be determined
      IExpr factor = factorTerms.first();
      if (factor.isNegative()) {
        negativeFactor = true;
      }
      realPart = engine.evaluate(F.Divide(realPart, factor));
      imaginaryPart = engine.evaluate(F.Divide(imaginaryPart, factor));
    }

    final IExpr arg;
    if (realPart.isAST(S.Re, 2) && realPart.first().isSymbol() //
        && imaginaryPart.isAST(S.Im, 2) //
        && realPart.first().equals(imaginaryPart.first())) {
      ISymbol symbol = (ISymbol) realPart.first();
      arg = F.Arg(negativeFactor ? F.Times(F.CN1, symbol) : symbol);
    } else {
      final IExpr z;
      if (negativeFactor) {
        z = F.Plus(F.Times(F.CN1, realPart), F.Times(F.CNI, imaginaryPart));
      } else {
        z = F.Plus(realPart, F.Times(F.CI, imaginaryPart));
      }
      arg = F.Arg(z);
    }
    return arg;
  }

  public static void initialize() {
    Initializer.init();
  }

  private SimplifyFunctions() {}
}
