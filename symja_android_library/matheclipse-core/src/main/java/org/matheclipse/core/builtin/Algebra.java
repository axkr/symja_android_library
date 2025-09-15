package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Arg;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.Assumptions;
import static org.matheclipse.core.expression.S.E;
import static org.matheclipse.core.expression.S.I;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.Power;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.JASIExpr;
import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.SimplifyUtil;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.DefaultDict;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.reflection.system.Solve.SolveData;
import org.matheclipse.core.visit.VisitorExpr;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.poly.TermOrderByName;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.RingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

public class Algebra {
  private static final Logger LOGGER = LogManager.getLogger(Algebra.class);

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Apart.setEvaluator(new Apart());
      S.Cancel.setEvaluator(new Cancel());
      S.Collect.setEvaluator(new Collect());
      S.Denominator.setEvaluator(new Denominator());
      S.Distribute.setEvaluator(new Distribute());
      S.Expand.setEvaluator(new Expand());
      S.ExpandAll.setEvaluator(new ExpandAll());
      S.ExpandDenominator.setEvaluator(new ExpandDenominator());
      S.ExpandNumerator.setEvaluator(new ExpandNumerator());
      S.Factor.setEvaluator(new Factor());
      S.FactorSquareFree.setEvaluator(new FactorSquareFree());
      S.FactorSquareFreeList.setEvaluator(new FactorSquareFreeList());
      S.FactorTerms.setEvaluator(new FactorTerms());
      S.FactorTermsList.setEvaluator(new FactorTermsList());
      S.Numerator.setEvaluator(new Numerator());

      S.PolynomialExtendedGCD.setEvaluator(new PolynomialExtendedGCD());
      S.PolynomialGCD.setEvaluator(new PolynomialGCD());
      S.PolynomialLCM.setEvaluator(new PolynomialLCM());
      S.PolynomialQ.setEvaluator(new PolynomialQ());
      S.PolynomialQuotient.setEvaluator(new PolynomialQuotient());
      S.PolynomialQuotientRemainder.setEvaluator(new PolynomialQuotientRemainder());
      S.PolynomialRemainder.setEvaluator(new PolynomialRemainder());

      S.PowerExpand.setEvaluator(new PowerExpand());
      S.Root.setEvaluator(new Root());
      S.Together.setEvaluator(new Together());
      S.ToRadicals.setEvaluator(new ToRadicals());
      S.Variables.setEvaluator(new Variables());
    }
  }

  /**
   *
   *
   * <h2>Apart</h2>
   *
   * <pre>
   * <code>Apart(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * rewrites <code>expr</code> as a sum of individual fractions.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Apart(expr, var)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * treats <code>var</code> as main variable.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Apart((x-1)/(x^2+x))
   * 2/(x+1)-1/x
   *
   * &gt;&gt; Apart(1 / (x^2 + 5x + 6))
   * 1/(2+x)+1/(-3-x)
   * </code>
   * </pre>
   *
   * <p>
   * When several variables are involved, the results can be different depending on the main
   * variable:
   *
   * <pre>
   * <code>&gt;&gt; Apart(1 / (x^2 - y^2), x)
   * -1 / (2 y (x + y)) + 1 / (2 y (x - y))
   * &gt;&gt; Apart(1 / (x^2 - y^2), y)
   * 1 / (2 x (x + y)) + 1 / (2 x (x - y))
   * </code>
   * </pre>
   *
   * <p>
   * 'Apart' is 'Listable':
   *
   * <pre>
   * <code>&gt;&gt; Apart({1 / (x^2 + 5x + 6)})
   * {1/(2+x)+1/(-3-x)}
   * </code>
   * </pre>
   *
   * <p>
   * But it does not touch other expressions:
   *
   * <pre>
   * <code>&gt;&gt; Sin(1 / (x ^ 2 - y ^ 2)) // Apart
   * Sin(1/(x^2-y^2))
   * </code>
   * </pre>
   */
  private static class Apart extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }

      VariablesSet eVar = new VariablesSet(arg1);
      if (eVar.isSize(0)) {
        if (arg1.isTimes() && arg1.isNumericFunction()
            && arg1.leafCount() < Config.MAX_SIMPLIFY_APART_LEAFCOUNT) {
          return SimplifyUtil.simplifyStep(arg1, arg1, true, true, engine);
        }
        return F.evalExpandAll(arg1, engine);
      }
      IAST variableList = null;
      if (ast.isAST2()) {
        variableList = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (variableList.isNIL()) {
          return F.NIL;
        }
      } else {
        if (!eVar.isSize(1)) {
          // partial fraction only possible for univariate polynomials
          return F.evalExpandAll(arg1, engine);
        }
        variableList = eVar.getVarList();
      }

      if (variableList.size() == 2 && (arg1.isTimes() || arg1.isPower())) {
        Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, false);
        if (parts.isPresent()) {
          IExpr variable = variableList.arg1();
          IExpr temp = partsApart(parts.get(), variable, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        return arg1;
      }
      return F.evalExpandAll(arg1, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <h2>Cancel</h2>
   *
   * <pre>
   * <code>Cancel(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * cancels out common factors in numerators and denominators.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Cancel(x / x ^ 2)
   * 1/x
   * </code>
   * </pre>
   *
   * <p>
   * 'Cancel' threads over sums:
   *
   * <pre>
   * <code>&gt;&gt; Cancel(x / x ^ 2 + y / y ^ 2)
   * 1/x+1/y
   *
   * &gt;&gt; Cancel(f(x) / x + x * f(x) / x ^ 2)
   * (2*f(x))/x
   * </code>
   * </pre>
   */
  public static class Cancel extends AbstractFunctionEvaluator {

    private static IExpr cancelPowerTimes(IExpr powerTimesAST, EvalEngine engine)
        throws JASConversionException {
      Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(powerTimesAST, false);
      if (parts.isPresent()) {
        IExpr p00 = parts.get()[0];
        IExpr p01 = F.C1;
        IExpr p10 = parts.get()[1];
        IExpr p11 = F.C1;
        // VariablesSet eVar = new VariablesSet(powerTimesAST);
        // IASTAppendable variables = eVar.getVarList();
        if (p00.isPlus()) {
          IAST numParts = p00.partitionPlus(x -> AlgebraUtil.isPolynomial(x), F.C0, F.C1, S.List);
          if (numParts.isPresent() && !numParts.arg1().isOne()) {
            p00 = numParts.arg1();
            p01 = numParts.arg2();
          }
        }

        if (p10.isPlus()) {
          IAST denParts = p10.partitionPlus(x -> AlgebraUtil.isPolynomial(x), F.C0, F.C1, S.List);
          if (denParts.isPresent() && !denParts.arg1().isOne()) {
            p10 = denParts.arg1();
            p11 = denParts.arg2();
          }
        }
        if (!p10.isOne()) {
          Optional<IExpr[]> result = AlgebraUtil.cancelGCD(p00, p10);
          if (result.isPresent()) {
            IExpr[] elements = result.get();
            IExpr commonFactor = elements[0];
            IExpr numeratorPolynomial = elements[1];
            IExpr denominatorPolynomial = elements[2];
            // (commonFactor * numeratorPolynomial * p01) / (denominatorPolynomial * p11)
            return engine.evaluate(F.Times(commonFactor, numeratorPolynomial, p01,
                F.Power(F.Times(denominatorPolynomial, p11), F.CN1)));
          }
        }
      }
      return F.NIL;
    }

    // private static IExpr[] cancelQuotientRemainder(final IExpr arg1, IExpr arg2, IExpr variable)
    // {
    // IExpr[] result = new IExpr[2];
    // try {
    //
    // JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
    // GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
    // GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
    // if (poly1.degree() > poly2.degree()) {
    // GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
    // if (!divRem[1].isZERO()) {
    // return null;
    // }
    // result[0] = jas.rationalPoly2Expr(divRem[0], false);
    // result[1] = F.C1;
    // } else {
    // GenPolynomial<BigRational>[] divRem = poly2.quotientRemainder(poly1);
    // if (!divRem[1].isZERO()) {
    // return null;
    // }
    // result[0] = F.C1;
    // result[1] = jas.rationalPoly2Expr(divRem[0], false);
    // }
    // return result;
    // } catch (JASConversionException e1) {
    // try {
    // JASIExpr jas = new JASIExpr(variable, ExprRingFactory.CONST);
    // GenPolynomial<IExpr> poly1 = jas.expr2IExprJAS(arg1);
    // GenPolynomial<IExpr> poly2 = jas.expr2IExprJAS(arg2);
    // if (poly1.degree() > poly2.degree()) {
    // GenPolynomial<IExpr>[] divRem = poly1.quotientRemainder(poly2);
    // if (!divRem[1].isZERO()) {
    // return null;
    // }
    // result[0] = jas.exprPoly2Expr(divRem[0], variable);
    // result[1] = F.C1;
    // } else {
    // GenPolynomial<IExpr>[] divRem = poly2.quotientRemainder(poly1);
    // if (!divRem[1].isZERO()) {
    // return null;
    // }
    // result[0] = F.C1;
    // result[1] = jas.exprPoly2Expr(divRem[0]);
    // }
    // return result;
    // } catch (JASConversionException e) {
    // LOGGER.debug("Cancel.cancelQuotientRemainder() failed", e);
    // }
    // }
    // return null;
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST1() && arg1.isAtom()) {
        return arg1;
      }
      IExpr temp = CompareUtil.threadPlusLogicEquationOperators(arg1, ast, 1);
      if (temp.isPresent()) {
        return temp;
      }
      temp = cancelFractionPowers(engine, arg1);
      if (temp.isPresent()) {
        return temp;
      }
      return cancelNIL(arg1, engine).orElse(arg1);
      // if (temp.isPresent()) {
      // return temp;
      // }
      // return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private IExpr cancelFractionPowers(EvalEngine engine, IExpr arg1) {
      IExpr temp;
      Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, false);
      if (parts.isPresent()) {
        IExpr numerator = parts.get()[0];
        IExpr denominator = parts.get()[1];
        // use long values see: https://lgtm.com/rules/7900075/
        long numerExponent = 1L;
        long denominatorExponent = 1L;
        if (numerator.isPowerInteger() || denominator.isPowerInteger()) {
          if (numerator.isPower()) {
            numerExponent = numerator.exponent().toIntDefault();
            numerator = numerator.base();
          }
          if (denominator.isPower()) {
            denominatorExponent = denominator.exponent().toIntDefault();
            denominator = denominator.base();
          }
          if (numerExponent > 0 && denominatorExponent > 0) {
            temp = cancelNIL(F.Times(numerator, F.Power(denominator, -1)), engine);
            if (temp.isPresent()) {
              if (numerExponent > denominatorExponent) {
                long exp = numerExponent - denominatorExponent;
                // result^denomExponent * numer^exp
                return F.Times(F.Power(temp, denominatorExponent), F.Power(numerator, exp));
              } else if (numerExponent < denominatorExponent) {
                long exp = denominatorExponent - numerExponent;
                // result^numerExponent / denom^exp
                return F.Times(F.Power(temp, numerExponent), F.Power(denominator, -1 * exp));
              }
              return F.Power(temp, numerExponent);
            }
          }
        }
      }
      return F.NIL;
    }

    /**
     * @param arg1
     * @param engine
     * @return {@link F#NIL} if no evaluations was possible
     */
    private IExpr cancelNIL(IExpr arg1, EvalEngine engine) {
      try {
        if (arg1.isTimes() || arg1.isPower()) {
          IExpr result = AlgebraUtil.cancelFractionalParts(arg1);
          if (result.isPresent()) {
            return result;
          }
        }
        IExpr expandedArg1 = F.evalExpandAll(arg1, engine);

        if (expandedArg1.isPlus()) {
          return expandedArg1.mapThread(F.Cancel(F.Slot1), 1);
        } else if (expandedArg1.isTimes() || expandedArg1.isPower()) {
          IExpr result = cancelPowerTimes(expandedArg1, engine);
          if (result.isPresent()) {
            return result;
          }
        }

      } catch (JASConversionException jce) {
        LOGGER.debug("Cancle failed", jce);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * <code>Collect(expr, variable)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * collect subexpressions in <code>expr</code> which belong to the same <code>variable</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>Collect(expr, variable, head)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * collect subexpressions in <code>expr</code> which belong to the same <code>variable</code> and
   * apply <code>head</code> on these subexpressions.
   *
   * </blockquote>
   *
   * <p>
   * Collect additive terms of an expression.
   *
   * <p>
   * This function collects additive terms of an expression with respect to a list of expression up
   * to powers with rational exponents. By the term symbol here are meant arbitrary expressions,
   * which can contain powers, products, sums etc. In other words symbol is a pattern which will be
   * searched for in the expression's terms.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Collect(a*x^2 + b*x^2 + a*x - b*x + c, x)
   * c+(a-b)*x+(a+b)*x^2
   *
   * &gt;&gt; Collect(a*Exp(2*x) + b*Exp(2*x), Exp(2*x))
   * (a+b)*E^(2*x)
   *
   * &gt;&gt; Collect(x^2 + y*x^2 + x*y + y + a*y, {x, y})
   * (1+a)*y+x*y+x^2*(1+y)
   * </code>
   * </pre>
   */
  private static class Collect extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        IAST temp = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
        if (temp.isPresent()) {
          return temp;
        }
        IExpr arg3Head = null;
        arg1 = F.expandAll(arg1, true, true);
        final IExpr arg2 = engine.evalPattern(ast.arg2());
        if (!arg2.isList()) {
          if (ast.isAST3()) {
            arg3Head = ast.arg3();
          }
          return collectSingleVariableRecursive(arg1, arg2, null, 1, arg3Head, engine);
        }
        IAST list = (IAST) arg2;
        if (list.size() > 1) {
          if (ast.isAST3()) {
            arg3Head = ast.arg3();
          }
          return collectSingleVariableRecursive(arg1, list.arg1(), (IAST) arg2, 2, arg3Head,
              engine);
        }
        return arg1;
      } catch (Exception e) {
        Errors.rethrowsInterruptException(e);
        LOGGER.debug("Collect.evaluate() failed", e);
      }
      return F.NIL;
    }

    private static IExpr getRest(IPatternMatcher matcher, IPatternSequence blankNullRest,
        IExpr defaultValue) {
      IExpr rest = matcher.getPatternMap().getValue(blankNullRest);
      if (rest != null) {
        return rest;
      }
      return defaultValue;
    }

    /**
     * Collect terms in <code>expr</code> containing the same power expressions of <code>x</code>.
     *
     * @param expr
     * @param x the current variable from the list of variables which should be collected
     * @param listOfVariables list of variables which should be collected or <code>null</code> if no
     *        list is available
     * @param listPosition position of the next variable in the list after <code>x</code> which
     *        should be collected recursively
     * @param head the head which should be applied to each coefficient or <code>null</code> if no
     *        head should be applied
     * @param engine the evaluation engine
     * @return
     */
    private IExpr collectSingleVariableRecursive(IExpr expr, IExpr x, final IAST listOfVariables,
        final int listPosition, IExpr head, EvalEngine engine) {
      if (expr.isAST()) {
        DefaultDict<IASTAppendable> defaultdict =
            new DefaultDict<IASTAppendable>(() -> F.PlusAlloc(8));
        IAST poly = (IAST) expr;
        IASTAppendable rest = F.PlusAlloc(poly.size());

        if (x.isTimes()) {
          // append a BlankNullSequence[] to match the parts of an Orderless expression into a
          // "rest" variable
          IPatternSequence blankNullRest =
              F.$ps(F.Dummy("§rest§" + EvalEngine.incModuleCounter()), true);
          IASTAppendable newLHS = ((IAST) x).appendClone(blankNullRest);
          // newLHS.append(blankNullRest);

          final IPatternMatcher matcher = engine.evalPatternMatcher(newLHS);

          collectTimesToMap(x, poly, matcher, defaultdict, rest, blankNullRest);

        } else {
          final IPatternMatcher matcher = engine.evalPatternMatcher(x);

          collectToMap(x, poly, matcher, defaultdict, rest);
        }

        if (listOfVariables != null && listPosition < listOfVariables.size()) {
          // collect next pattern in sub-expressions
          IASTAppendable result = F.PlusAlloc(defaultdict.size() + 1);
          if (rest.size() > 1) {
            result.append(collectSingleVariableRecursive(rest.oneIdentity0(),
                listOfVariables.get(listPosition), listOfVariables, listPosition + 1, head,
                engine));
          }
          result.append(defaultdict.getMap(), (key, value) -> {
            IExpr temp = collectSingleVariableRecursive(((IASTAppendable) value).oneIdentity0(),
                listOfVariables.get(listPosition), listOfVariables, listPosition + 1, head, engine);
            return F.Times(key, temp);
          });
          return result;
        }
        if (head != null) {
          IASTMutable simplifyAST = F.unaryAST1(head, null);
          rest.forEach((arg, i) -> {
            simplifyAST.set(1, arg);
            rest.set(i, engine.evaluate(simplifyAST));
          });
          rest.append(defaultdict.getMap(), (key, value) -> {
            simplifyAST.set(1, value);
            IExpr coefficient = engine.evaluate(simplifyAST);
            if (coefficient.isPlus()) {
              return F.Times(key).appendOneIdentity((IAST) coefficient);
            } else {
              return key.times(coefficient);
            }
          });
        } else {
          rest.append(defaultdict.getMap(), (key, value) -> {
            IASTAppendable times = F.TimesAlloc(2);
            times.append(key);
            times.appendOneIdentity((IASTAppendable) value);
            return times;
          });
        }
        return rest.oneIdentity0();
      }
      return expr;
    }

    public void collectTimesToMap(final IExpr key, IExpr expr, IPatternMatcher matcher,
        DefaultDict<IASTAppendable> defaultdict, IASTAppendable rest,
        IPatternSequence blankNullRest) {
      if (expr.isFree(matcher, false)) {
        rest.append(expr);
        return;
      } else if (matcher.test(expr)) {
        addPowerFactor(expr, getRest(matcher, blankNullRest, F.C1), defaultdict);
        return;
      } else if (blankNullRest == null && isPowerMatched(expr, matcher)) {
        addPowerFactor(expr, F.C1, defaultdict);
        return;
      } else if (expr.isPlus()) {
        IAST plusAST = (IAST) expr;
        IASTAppendable clone = plusAST.copyAppendable();
        int i = 1;
        while (i < clone.size()) {
          if (collectTimesToMapPlus(key, clone.get(i), matcher, defaultdict, blankNullRest)) {
            clone.remove(i);
          } else {
            i++;
          }
        }
        if (clone.size() > 1) {
          rest.appendOneIdentity(clone);
        }
        return;
      } else if (blankNullRest == null && expr.isTimes()) {
        IAST timesAST = (IAST) expr;
        if (timesAST.exists((x, i) -> {
          if (matcher.test(x) || isPowerMatched(x, matcher)) {
            IASTAppendable clone = timesAST.copyAppendable();
            clone.remove(i);
            addOneIdentityPowerFactor(x, clone, defaultdict);
            return true;
          }
          return false;
        }, 1)) {
          return;
        }
        rest.append(expr);
        return;
      }
      rest.append(expr);
    }

    public boolean collectTimesToMapPlus(final IExpr key, IExpr expr, IPatternMatcher matcher,
        DefaultDict<IASTAppendable> defaultdict, IPatternSequence blankNullRest) {
      if (expr.isFree(matcher, false)) {
        return false;
      } else if (matcher.test(expr)) {
        addPowerFactor(key, getRest(matcher, blankNullRest, F.C0), defaultdict);
        return true;
      }
      return false;
    }

    public void collectToMap(final IExpr key, IExpr expr, IPatternMatcher matcher,
        DefaultDict<IASTAppendable> defaultdict, IASTAppendable rest) {
      if (expr.isFree(matcher, false)) {
        rest.append(expr);
        return;
      } else if (matcher.test(expr)) {
        addPowerFactor(expr, F.C1, defaultdict);
        return;
      } else if (isPowerMatched(expr, matcher)) {
        addPowerFactor(expr, F.C1, defaultdict);
        return;
      } else if (expr.isPlus()) {
        IAST plusAST = (IAST) expr;
        IASTAppendable clone = plusAST.copyAppendable();
        int i = 1;
        while (i < clone.size()) {
          if (collectToMapPlus(key, clone.get(i), matcher, defaultdict)) {
            clone.remove(i);
          } else {
            i++;
          }
        }
        if (clone.size() > 1) {
          rest.appendOneIdentity(clone);
        }
        return;
      } else if (expr.isTimes()) {
        IAST timesAST = (IAST) expr;
        if (timesAST.exists((x, i) -> {
          if (matcher.test(x) || isPowerMatched(x, matcher)) {
            IASTAppendable clone = timesAST.copyAppendable();
            clone.remove(i);
            addOneIdentityPowerFactor(x, clone, defaultdict);
            return true;
          }
          return false;
        }, 1)) {
          return;
        }
        rest.append(expr);
        return;
      }
      rest.append(expr);
    }

    public boolean collectToMapPlus(final IExpr key, IExpr expr, IPatternMatcher matcher,
        DefaultDict<IASTAppendable> defaultdict) {
      if (expr.isFree(matcher, false)) {
        return false;
      } else if (matcher.test(expr)) {
        addPowerFactor(expr, F.C1, defaultdict);
        return true;
      } else if (isPowerMatched(expr, matcher)) {
        addPowerFactor(expr, F.C1, defaultdict);
        return true;
      } else if (expr.isTimes()) {
        IAST timesAST = (IAST) expr;
        return timesAST.exists((x, i) -> {
          if (matcher.test(x) || isPowerMatched(x, matcher)) {
            IAST clone = timesAST.splice(i);
            addOneIdentityPowerFactor(x, clone, defaultdict);
            return true;
          }
          return false;
        }, 1);
      }

      return false;
    }

    public void addOneIdentityPowerFactor(IExpr key, IAST subAST,
        DefaultDict<IASTAppendable> defaultdict) {
      IASTAppendable resultList = defaultdict.getValue(key);
      resultList.appendOneIdentity(subAST);
    }

    public void addPowerFactor(IExpr key, IExpr value, DefaultDict<IASTAppendable> defaultdict) {
      IASTAppendable resultList = defaultdict.getValue(key);
      resultList.append(value);
    }

    public boolean isPowerMatched(IExpr poly, IPatternMatcher matcher) {
      return poly.isPower() && poly.exponent().isNumber() && matcher.test(poly.base());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <h2>Denominator</h2>
   *
   * <pre>
   * <code>Denominator(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the denominator in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Denominator(a / b)
   * b
   * &gt;&gt; Denominator(2 / 3)
   * 3
   * &gt;&gt; Denominator(a + b)
   * 1
   * </code>
   * </pre>
   */
  private static class Denominator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean trig = false;
      if (ast.isAST2()) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        if (options.isInvalidPosition(1)) {
          return options.printNonopt(ast, 1, engine);
        }
        trig = options.isTrue(S.Trig);
      }

      return fractionalPartsRational(ast.arg1(), trig);
    }

    public static IExpr fractionalPartsRational(final IExpr expr, boolean trig) {
      if (expr.isRational()) {
        return ((IRational) expr).denominator();
      }
      Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(expr, trig);
      if (parts.isPresent()) {
        return parts.get()[1];
      }
      return F.C1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Trig, S.False)));
    }


  }

  /**
   *
   *
   * <pre>
   * Distribute(f(x1, x2, x3,...))
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * distributes <code>f</code> over <code>Plus</code> appearing in any of the <code>xi</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Distributive_property">Wikipedia - Distributive
   * property</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Distribute((a+b)*(x+y+z))
   * a*x+a*y+a*z+b*x+b*y+B*z
   * </pre>
   */
  public static final class Distribute extends AbstractFunctionEvaluator {



    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr head = ast.getArg(2, S.Plus);
      if (ast.isAST3()) {
        if (!arg1.head().equals(ast.arg3())) {
          return arg1;
        }
      }

      if (arg1.isAST() && ast.argSize() > 0) {
        return AlgebraUtil.distribute(ast, head);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5;
    }
  }

  /**
   *
   *
   * <pre>
   * Expand(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * expands out positive rational powers and products of sums in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Expand((x + y) ^ 3)
   * x^3+3*x^2*y+3*x*y^2+y^3
   *
   * &gt;&gt; Expand((a + b) (a + c + d))
   * a^2+a*b+a*c+b*c+a*d+b*d
   *
   * &gt;&gt; Expand((a + b) (a + c + d) (e + f) + e a a)
   * 2*a^2*e+a*b*e+a*c*e+b*c*e+a*d*e+b*d*e+a^2*f+a*b*f+a*c*f+b*c*f+a*d*f+b*d*f
   *
   * &gt;&gt; Expand((a + b) ^ 2 * (c + d))
   * a^2*c+2*a*b*c+b^2*c+a^2*d+2*a*b*d+b^2*d
   *
   * &gt;&gt; Expand((x + y) ^ 2 + x y)
   * x^2+3*x*y+y^2
   *
   * &gt;&gt; Expand(((a + b) (c + d)) ^ 2 + b (1 + a))
   * a^2*c^2+2*a*b*c^2+b^2*c^2+2*a^2*c*d+4*a*b*c*d+2*b^2*c*d+a^2*d^2+2*a*b*d^2+b^2*d^2+b(1+a)
   * </pre>
   *
   * <p>
   * <code>Expand</code> expands out rational powers by expanding the <code>Floor()</code> part of
   * the rational powers number:
   *
   * <pre>
   * &gt;&gt; Expand((x + 3)^(5/2)+(x + 1)^(3/2))
   * Sqrt(1+x)+x*Sqrt(1+x)+9*Sqrt(3+x)+6*x*Sqrt(3+x)+x^2*Sqrt(3+x)
   * </pre>
   *
   * <p>
   * <code>Expand</code> expands items in lists and rules:<br>
   *
   * <pre>
   * &gt;&gt; Expand({4 (x + y), 2 (x + y) -&gt; 4 (x + y)})
   * {4*x+4*y,2*(x+y)-&gt;4*(x+y)}
   * </pre>
   *
   * <p>
   * <code>Expand</code> does not change any other expression.<br>
   *
   * <pre>
   * &gt;&gt; Expand(Sin(x*(1 + y)))
   * Sin(x*(1+y))
   *
   * &gt;&gt; a*(b*(c+d)+e) // Expand
   * a*b*c+a*b*d+a*e
   *
   * &gt;&gt; (y^2)^(1/2)/(2x+2y)//Expand
   * Sqrt(y^2)/(2*x+2*y)
   *
   * &gt;&gt; 2(3+2x)^2/(5+x^2+3x)^3 // Expand
   * 18/(5+3*x+x^2)^3+(24*x)/(5+3*x+x^2)^3+(8*x^2)/(5+3*x+x^2)^3
   * </pre>
   */
  public static class Expand extends AbstractFunctionOptionEvaluator {

    

    // private static class NumberPartition {
    // IASTAppendable expandedResult;
    // int m;
    // int n;
    // int[] parts;
    //
    // /**
    // * Cached {@link S#Power} calculations for each part of the {@link S#Plus} AST.
    // * <p>
    // * If <code>x</code> is an argument of the {@link S#Plus} AST at position <code>i</code>, then
    // * the <code>cachedPowers[i - 1] = {x^1, x^2, x^3,....,x^n}</code> will be calculated and
    // * stored in the cache.
    // */
    // final IASTAppendable[] cachedPowers;
    //
    // public NumberPartition(IAST plusAST, int n, IASTAppendable expandedResult) {
    // this.expandedResult = expandedResult;
    // this.n = n;
    // this.m = plusAST.argSize();
    // this.parts = new int[m];
    // // cache all {@link S#Power} calculations for each part of the {@link S#Plus} AST:
    // this.cachedPowers = new IASTAppendable[m];
    // for (int i = 1; i < plusAST.size(); i++) {
    // IExpr arg = plusAST.get(i);
    // cachedPowers[i - 1] = F.ListAlloc(n + 1);
    // for (int j = 0; j < n; j++) {
    // // x^1, x^2, x^3,....,x^n
    // this.cachedPowers[i - 1].append(arg.pow(j + 1));
    // }
    // }
    // }
    //
    // private void addFactor(int[] j) {
    // final KPermutationsIterable perm = new KPermutationsIterable(j, m, m);
    // IInteger multinomial = NumberTheory.multinomial(j, n);
    // IExpr temp;
    // TimesOp timesOp = new TimesOp(32);
    // for (int[] indices : perm) {
    // if (!multinomial.isOne()) {
    // timesOp.append(multinomial);
    // }
    // for (int k = 0; k < m; k++) {
    // if (indices[k] != 0) {
    // temp = cachedPowers[k].get(indices[k]);
    // if (temp.equals(F.C1)) {// keep numeric 1.0 values here
    // continue;
    // }
    // timesOp.append(temp);
    // }
    // }
    // expandedResult.append(timesOp.getProduct());
    // timesOp.clear();
    // }
    // }
    //
    // public void partition() {
    // partition(n, n, 0);
    // }
    //
    // private void partition(int n, int max, int currentIndex) {
    // if (n == 0) {
    // addFactor(parts);
    // return;
    // }
    // if (currentIndex >= m) {
    // return;
    // }
    // int old;
    // old = parts[currentIndex];
    // int min = Math.min(max, n);
    //
    // for (int i = min; i >= 1; i--) {
    // parts[currentIndex] = i;
    // partition(n - i, i, currentIndex + 1);
    // }
    // parts[currentIndex] = old;
    // }
    // }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }
      if (arg1.isAST()) {
        IAST ast1 = (IAST) arg1;
        Predicate<IExpr> matcher = null;
        if (ast.size() > 2) {
          matcher = Predicates.toFreeQ(ast.arg2());
        }
        return AlgebraUtil.expand(ast1, matcher, false, true, true).orElse(ast1);
      }

      return ast.arg1();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Trig, S.Modulus};
      IExpr[] optionValues = new IExpr[] {S.False, F.C0};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }

  /**
   *
   *
   * <h2>ExpandAll</h2>
   *
   * <pre>
   * <code>ExpandAll(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * expands out all positive integer powers and products of sums in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ExpandAll((a + b) ^ 2 / (c + d)^2)
   * (a^2+2*a*b+b^2)/(c^2+2*c*d+d^2)
   * </code>
   * </pre>
   *
   * <p>
   * <code>ExpandAll</code> descends into sub expressions
   *
   * <pre>
   * <code>&gt;&gt; ExpandAll((a + Sin(x*(1 + y)))^2)
   * a^2+Sin(x+x*y)^2+2*a*Sin(x+x*y)
   * </code>
   * </pre>
   *
   * <p>
   * <code>ExpandAll</code> also expands heads
   *
   * <pre>
   * <code>&gt;&gt; ExpandAll(((1 + x)(1 + y))[x])
   * (1+x+y+x*y)[x]
   * </code>
   * </pre>
   */
  public static class ExpandAll extends AbstractFunctionOptionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }
      Predicate<IExpr> matcher = null;
      if (ast.size() > 2) {
        matcher = Predicates.toFreeQ(ast.arg2());
      }
      if (arg1.isAST()) {
        return AlgebraUtil.expandAll((IAST) arg1, matcher, true, true, false, engine).orElse(arg1);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Trig, S.Modulus};
      IExpr[] optionValues = new IExpr[] {S.False, F.C0};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }

  private static class ExpandDenominator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }
      if (arg1.isAST()) {
        if (arg1.isPlus()) {
          return ((IAST) arg1).mapThread(x -> F.ExpandDenominator(x));
        }
        Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, false);
        if (parts.isPresent() && parts.get()[1].isAST()) {
          IExpr denominator = engine.evaluate(F.Expand(parts.get()[1]));
          return F.Divide(parts.get()[0], denominator);
        }
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  private static class ExpandNumerator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }
      if (arg1.isAST()) {
        if (arg1.isPlus()) {
          return ((IAST) arg1).mapThread(x -> F.ExpandNumerator(x));
        }
        Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg1, false);
        if (parts.isPresent() && parts.get()[0].isAST()) {
          IExpr numerator = engine.evaluate(F.Expand(parts.get()[0]));
          return F.Divide(numerator, parts.get()[1]);
        }
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  /**
   *
   *
   * <h2>Factor</h2>
   *
   * <pre>
   * <code>Factor(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * factors the polynomial expression <code>expr</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Factor(1+2*x+x^2, x)
   * (1+x)^2
   * </code>
   * </pre>
   *
   * <pre>
   * <code>```
   * &gt;&gt; Factor(x^4-1, GaussianIntegers-&gt;True)
   * (x-1)*(x+1)*(x-I)*(x+I)
   * </code>
   * </pre>
   */
  public static class Factor extends AbstractFunctionEvaluator {

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Extension, S.None), F.Rule(S.GaussianIntegers, S.False),
              F.Rule(S.Modulus, F.C0)));
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST list = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (list.isPresent()) {
        return list;
      }

      IExpr result = engine.getCache(ast);
      if (result != null) {
        if (result.isPresent()) {
          return result;
        }
        return arg1;
      }
      VariablesSet eVar = new VariablesSet(arg1);
      List<IExpr> varList = eVar.getVarList().copyTo();
      if (ast.isAST1()) {
        return factor(ast, arg1, eVar, false, true, engine);
      }

      try {
        if (ast.isAST2()) {
          IExpr temp = factorWithOption(ast, arg1, varList, false, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        IExpr temp = factorExpr(ast, arg1, eVar, false, true, engine);
        engine.putCache(ast, temp);
        if (temp.isPresent()) {
          return temp;
        }
      } catch (JASConversionException e) {
        LOGGER.debug("Factor.evaluate() failed", e);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private static IExpr factor(IAST ast, IExpr arg1, VariablesSet eVar, boolean squareFree,
        boolean withHomogenization, EvalEngine engine) {
      IExpr expr = arg1;
      if (!arg1.isTimes() && !arg1.isPower()) {
        expr = S.Together.of(engine, arg1);
        if (expr.isAST()) {
          IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) expr, true, engine);
          if (!parts[1].isOne()) {
            try {
              IExpr numerator = factorExpr(F.Factor(parts[0]), parts[0], eVar, squareFree,
                  withHomogenization, engine);
              IExpr denominator = factorExpr(F.Factor(parts[1]), parts[1], eVar, squareFree,
                  withHomogenization, engine);
              if (numerator.isPresent() && denominator.isPresent()) {
                IExpr temp = engine.evaluate(F.Divide(numerator, denominator));
                engine.putCache(ast, temp);
                return temp;
              }
              engine.putCache(ast, F.NIL);
            } catch (JASConversionException e) {
              //
            }
            return arg1;
          }
        }
      }
      try {
        IExpr temp = factorExpr(ast, expr, eVar, squareFree, withHomogenization, engine);
        engine.putCache(ast, temp);
        if (temp.isPresent()) {
          return temp;
        }
      } catch (JASConversionException e) {
        LOGGER.debug("Factor.evaluate() failed", e);
      }
      return arg1;
    }

    protected static IExpr factorExpr(final IAST ast, IExpr expr, VariablesSet eVar,
        boolean squareFree, boolean withHomogenization, EvalEngine engine) {
      if (expr.isAST()) {
        IExpr temp;
        // if (expr.isPower()&&expr.base().isPlus()) {
        // temp = factorExpr(ast, expr.base(), varList);
        // temp = F.Power(temp, expr.exponent());
        // } else
        if (expr.isPower()) {
          IExpr p =
              factorExpr((IAST) expr, expr.base(), eVar, squareFree, withHomogenization, engine);
          if (p.isPresent() && !p.equals(expr.base())) {
            return F.Power(p, expr.exponent());
          }
          return expr;
        } else if (expr.isTimes()) {
          temp = ((IAST) expr).map(x -> {
            if (x.isPlus()) {
              return factorExpr(ast, x, eVar, squareFree, withHomogenization, engine);
            }
            if (x.isPower() && x.base().isPlus()) {
              IExpr p = factorExpr(ast, x.base(), eVar, squareFree, withHomogenization, engine);
              if (p.isPresent() && !p.equals(x.base())) {
                return F.Power(p, x.exponent());
              }
            }
            return F.NIL;
          }, 1);
          return temp;
        } else {
          return factor((IAST) expr, eVar, squareFree, withHomogenization, engine);
        }
      }
      return expr;
    }

    private static IExpr factor(IAST expr, VariablesSet eVar, boolean squareFree,
        boolean withHomogenization, EvalEngine engine) throws JASConversionException {
      if (expr.leafCount() > Config.MAX_FACTOR_LEAFCOUNT) {
        return expr;
      }

      // use TermOrderByName.INVLEX here!
      // See https://github.com/kredel/java-algebra-system/issues/8
      Object[] objects = null;
      JASConvert<BigRational> jas = new JASConvert<BigRational>(eVar.getArrayList(),
          BigRational.ZERO, TermOrderByName.INVLEX);
      try {
        GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
        if (polyRat == null) {
          if (!squareFree && withHomogenization) {
            return factorWithPolynomialHomogenization(expr, eVar, engine);
          }
          return F.NIL;
        }
        if (polyRat.length() <= 1) {
          return expr;
        }
        objects = jas.factorTerms(polyRat);
      } catch (JASConversionException e) {
        if (!squareFree && withHomogenization) {
          return factorWithPolynomialHomogenization(expr, eVar, engine);
        }
        return F.NIL;
      }

      if (objects != null) {

        SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map = null;
        try {
          GenPolynomial<edu.jas.arith.BigInteger> poly =
              (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
          if (eVar.size() == 1) {
            IExpr temp = heuristicXP2XPOne(poly, expr, eVar.getArrayList().get(0), engine);
            if (temp.isPresent()) {
              return temp;
            }
          }

          FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
              FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
          if (squareFree) {
            map = factorAbstract.squarefreeFactors(poly); // factors(poly);
          } else {
            map = factorAbstract.factors(poly);
          }
          // } catch (TimeExceededException texex) {
          // LOGGER.debug("Factor.factor() time limit exceeded", texex);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          LOGGER.debug("Factor.factor() failed", rex);
          return expr;
        }
        if (map != null) {
          IASTAppendable result = F.TimesAlloc(map.size() + 1);
          java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
          java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
          IRational f = F.C1;
          if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
            f = F.fraction(gcd, lcm).normalize();
          }
          for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map
              .entrySet()) {
            if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
              continue;
            }
            IExpr base = jas.integerPoly2Expr(entry.getKey());
            if (entry.getValue() == 1L) {
              if (f.isMinusOne() && base.isPlus()) {
                base = ((IAST) base).map(x -> x.negate(), 1);
                f = F.C1;
              }
              result.append(base);
            } else {
              result.append(F.Power(base, F.ZZ(entry.getValue())));
            }
          }
          if (!f.isOne()) {
            result.append(f);
          }
          return engine.evaluate(result);
        }
      }
      return F.NIL;
    }

    public static IExpr evaluateSolve(IExpr expr, SolveData solveData, EvalEngine engine) {
      VariablesSet eVar = new VariablesSet(expr);
      if (eVar.size() == 1 && !expr.isTimes() && !expr.isPower()) {
        IExpr variable = eVar.getArrayList().get(0);
        expr = S.Together.of(engine, expr);
        if (expr.isAST()) {
          IExpr[] parts = AlgebraUtil.numeratorDenominator((IAST) expr, true, engine);
          try {
            IExpr numerator =
                factorExprSolve(F.Factor(parts[0]), parts[0], variable, solveData, engine);
            if (numerator.isList() && !parts[1].isOne()) {
              // IExpr denominator = factorExprSolve(F.Factor(parts[1]), parts[1], variable,
              // engine);
              // cross check for zero-values in denominator
              IExpr denominator = parts[1];
              IAST list = (IAST) numerator;
              return list.removePositionsAtCopy(
                  x -> crossCheckDivByZero(x, variable, denominator, engine));
            }
            return numerator;
          } catch (JASConversionException e) {
            LOGGER.debug("Factor.evaluate() JAS conversion failed", e);
          }

        }
      }

      return F.NIL;
    }

    private static boolean crossCheckDivByZero(IExpr x, IExpr variable, IExpr denominator,
        EvalEngine engine) {
      IAST expr = F.PossibleZeroQ(F.ReplaceAll(denominator, F.Rule(variable, x)));
      boolean isPossibleZeroQ = engine.evalTrue(expr);

      return isPossibleZeroQ;
    }

    protected static IExpr factorExprSolve(final IAST ast, IExpr expr, IExpr variable,
        SolveData solveData, EvalEngine engine) {
      if (expr.isAST()) {
        if (expr.isTimes()) {
          IASTAppendable result = F.ListAlloc();
          ((IAST) expr).forEach(x -> {
            if (x.isPlus()) {
              IExpr subList = factorExprSolve(ast, x, variable, solveData, engine);
              if (subList.isList()) {
                result.appendArgs((IAST) subList);
              }
            }
          });
          if (result.size() > 1) {
            return result;
          }
          return F.NIL;
        } else {
          return factorWithPolynomialHomogenizationSolve((IAST) expr, variable, solveData, engine);
        }
      }
      return expr;
    }

    private static IExpr factorWithPolynomialHomogenizationSolve(IAST expr, IExpr variable,
        SolveData solveData, EvalEngine engine) {
      if (expr.leafCount() > Config.MAX_FACTOR_LEAFCOUNT) {
        return expr;
      }
      boolean gaussianIntegers = !expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);
      IASTAppendable originalVarList = F.ListAlloc(2);
      originalVarList.append(variable);
      VariablesSet variablesSet = new VariablesSet();
      variablesSet.add(variable);
      PolynomialHomogenization substitutions = new PolynomialHomogenization(variablesSet, engine);
      IExpr subsPolynomial = substitutions.replaceForward(expr);
      Set<ISymbol> varSet = substitutions.substitutedVariablesSet();
      List<IExpr> arrayList = new ArrayList<IExpr>(1);
      arrayList.add(variable);
      arrayList.addAll(varSet);
      IExpr factorization =
          factorComplex(subsPolynomial, arrayList, S.Times, gaussianIntegers, engine);
      if (factorization.isNIL() || factorization.size() == 2) {
        VariablesSet newVariables = new VariablesSet(subsPolynomial);
        if (newVariables.size() == 1) {
          IAST resultList = QuarticSolver.solve(subsPolynomial, newVariables.firstVariable());
          if (resultList.size() > 0) {
            return solveEquationList(resultList, originalVarList, substitutions, varSet, engine);
          }
          resultList = RootsFunctions.findRoots(subsPolynomial, newVariables.getVarList());
          if (resultList.size() > 0) {
            return solveEquationList(resultList, originalVarList, substitutions, varSet, engine);
          }
        }
        return F.NIL;
      }
      return solveEquationRecursive(factorization, originalVarList, substitutions, varSet,
          solveData, engine);

    }

    private static IAST solveEquationRecursive(IExpr factorization, IASTAppendable originalVarList,
        PolynomialHomogenization substitutions, Set<ISymbol> varSet, SolveData solveData,
        EvalEngine engine) {
      IASTAppendable resultList = F.NIL;
      if (factorization.isTimes() && factorization.size() > 1 && varSet.size() == 1) {
        // System.out.println(factorization);
        IAST varList = F.ListAlloc(varSet);
        IAST timesAST = (IAST) factorization;
        resultList = F.ListAlloc(factorization.size());
        for (int i = 1; i < timesAST.size(); i++) {
          IExpr factor = timesAST.get(i);
          IAST subList = RootsFunctions.rootsOfExprPolynomial(factor, varList, true);
          if (subList.isPresent()) {
            for (int j = 1; j < subList.size(); j++) {
              IAST solveFunction = F.Solve(
                  F.Equal(F.Subtract(substitutions.replaceBackward(varList.arg1()), subList.get(j)),
                      F.C0), //
                  originalVarList.arg1(), //
                  F.Rule(S.GenerateConditions,
                      solveData.isGenerateConditions() ? S.True : S.False));
              IExpr result = engine.evaluate(solveFunction);
              if (result.isListOfLists()) {
                IAST listOfRulesToValuesList =
                    listOfRulesToValuesList((IAST) result, originalVarList.arg1());
                if (listOfRulesToValuesList.isPresent()) {
                  resultList.appendArgs(listOfRulesToValuesList);
                }
              }
            }

          }
        }
      }
      return resultList;
    }

    private static IAST solveEquationList(IExpr factorization, IASTAppendable originalVarList,
        PolynomialHomogenization substitutions, Set<ISymbol> varSet, EvalEngine engine) {
      if (factorization.isList() && factorization.size() > 1 && varSet.size() == 1) {
        IASTAppendable resultList = F.NIL;
        IAST varList = F.ListAlloc(varSet);
        IAST listOfRules = (IAST) factorization;

        for (int i = 1; i < listOfRules.size(); i++) {
          IExpr factor = listOfRules.get(i);
          IExpr replaceBackward =
              substitutions.replaceDenominatorBackwardLCM((ISymbol) varList.arg1(), factor);
          if (replaceBackward.isPresent()) {
            if (resultList.isNIL()) {
              resultList = F.ListAlloc(listOfRules.argSize());
            }
            resultList.append(replaceBackward);
          }
        }
        return resultList;
      }
      return F.NIL;
    }

    public static IAST listOfRulesToValuesList(IAST listOfRules, IExpr variable) {
      IASTAppendable solveValues =
          F.mapList(listOfRules, possibleList1 -> getVariableValue(possibleList1, variable));
      if (solveValues.size() > 1) {
        return solveValues;
      }
      return F.NIL;
    }

    private static IExpr getVariableValue(IExpr possibleList1, IExpr variable) {
      if (possibleList1.isList1() && possibleList1.first().isRuleAST()
          && possibleList1.first().first().equals(variable)) {
        return possibleList1.first().second();
      }
      return F.NIL;
    }

    /**
     * Polynomials of the form <code>x^(2*p) + x^p + 1</code> have exactly two factors for all
     * primes <code>p != 3</code>. One is <code>x^2 + x + 1</code>, and its cofactor is a polynomial
     * whose coefficients are all <code>1, 0, or −1</code>.
     *
     * @param poly
     * @param expr
     * @param variable
     * @param engine
     * @return
     */
    private static IExpr heuristicXP2XPOne(GenPolynomial<edu.jas.arith.BigInteger> poly, IAST expr,
        IExpr variable, EvalEngine engine) {
      if (poly.length() == 3 && poly.ring.tord == TermOrderByName.INVLEX && poly.ring.nvar == 1) {
        edu.jas.arith.BigInteger a = edu.jas.arith.BigInteger.ZERO;
        edu.jas.arith.BigInteger b = edu.jas.arith.BigInteger.ZERO;
        edu.jas.arith.BigInteger c = edu.jas.arith.BigInteger.ZERO;
        edu.jas.arith.BigInteger one = edu.jas.arith.BigInteger.ONE;
        long expA = 0;
        long p = 0;
        long expC = 0;
        int i = 0;
        for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
          final edu.jas.arith.BigInteger coeff = monomial.coefficient();
          long lExp = monomial.exponent().getVal(0);
          i++;
          if (i == 1) {
            a = coeff;
            expA = lExp;
          } else if (i == 2) {
            b = coeff;
            p = lExp;
          } else if (i == 3) {
            c = coeff;
            expC = lExp;
          }
        }
        if (a.equals(one) && b.equals(one) && c.equals(one)) {
          if (expC == 0L && (p != 3L) && (expA == p * 2)
              && java.math.BigInteger.valueOf(p).isProbablePrime(32)) {
            // polynomials of the form x^(2*p) + x^p + 1 have exactly two factors for
            // all primes p != 3. One is x^2 + x + 1, and its cofactor is a polynomial whose
            // coefficients are all 1, 0, or −1.
            IExpr x = variable;
            IExpr p1 = F.Plus(F.Power(x, F.C2), x, F.C1);
            IExpr p2 = engine.evaluate(F.PolynomialQuotient(expr, p1, x));
            return F.Times(p1, p2);
          }
        }
      }
      return F.NIL;
    }

    private static IExpr factorWithPolynomialHomogenization(IAST expr, VariablesSet eVar,
        EvalEngine engine) {
      boolean gaussianIntegers = !expr.isFree(x -> x.isComplex() || x.isComplexNumeric(), false);
      PolynomialHomogenization substitutions = new PolynomialHomogenization(eVar, engine);
      IExpr subsPolynomial = substitutions.replaceForward(expr);
      // System.out.println(subsPolynomial.toString());
      // System.out.println(substitutions.substitutedVariables());
      if (substitutions.size() == 0) {
        return factorComplex(expr, eVar.getArrayList(), S.Times, gaussianIntegers, engine);
      }
      if (subsPolynomial.isAST()) {
        // System.out.println(subsPolynomial);
        Set<ISymbol> varSet = substitutions.substitutedVariablesSet();
        eVar.addAll(varSet);
        IExpr factorization =
            factorComplex(subsPolynomial, eVar.getArrayList(), S.Times, gaussianIntegers, engine);
        if (factorization.isPresent()) {
          return substitutions.replaceBackward(factorization);
        }
      }
      return expr;
    }

    /**
     * Factor the <code>expr</code> with the option given in <code>ast</code>.
     *
     * @param ast
     * @param expr
     * @param varList
     * @param factorSquareFree
     * @return
     * @throws JASConversionException
     */
    public static IExpr factorWithOption(final IAST ast, IExpr expr, List<IExpr> varList,
        boolean factorSquareFree, final EvalEngine engine) throws JASConversionException {
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      IExpr option = options.getOption(S.Modulus);
      if (option.isInteger() && !option.isZero()) {
        return factorModulus(expr, varList, factorSquareFree, option);
      }
      if (!factorSquareFree) {
        option = options.getOption(S.Extension);
        if (option.isImaginaryUnit()) {
          // Exptension->I is like gaussian integers
          return factorComplex(expr, varList, S.Times, false, true, engine);
        }

        option = options.getOption(S.GaussianIntegers);
        if (option.isTrue()) {
          return factorComplex(expr, varList, S.Times, false, true, engine);
        }
      }
      return F.NIL; // no evaluation
    }
  }

  /**
   *
   *
   * <h2>FactorSquareFree</h2>
   *
   * <pre>
   * <code>FactorSquareFree(polynomial)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * factor the polynomial expression <code>polynomial</code> square free.
   *
   * </blockquote>
   */
  private static class FactorSquareFree extends Factor {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST list = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (list.isPresent()) {
        return list;
      }
      VariablesSet eVar = new VariablesSet(arg1);
      IExpr result = engine.getCache(ast);
      if (result != null) {
        if (result.isPresent()) {
          return result;
        }
        return arg1;
      }
      try {
        IExpr expr = F.evalExpandAll(arg1, engine);
        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // List<IExpr> varList = r;
        List<IExpr> varList = eVar.getVarList().copyTo();

        if (ast.isAST2()) {
          IExpr temp = factorWithOption(ast, expr, varList, true, engine);
          if (temp.isPresent()) {
            return temp;
          }
        } else if (expr.isAST()) {
          IExpr temp = factorExpr((IAST) expr, (IAST) expr, eVar, true, true, engine);
          engine.putCache(ast, temp);
          if (temp.isPresent()) {
            return temp;
          }
        }
        return ast.arg1();

      } catch (JASConversionException jce) {
        // toInt() conversion failed
        LOGGER.debug("FactorSquareFree.evaluate() failed", jce);
      }
      return ast.arg1();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <h2>FactorSquareFreeList</h2>
   *
   * <pre>
   * <code>FactorSquareFreeList(polynomial)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the square free factors of the polynomial expression <code>polynomial</code>.
   *
   * </blockquote>
   */
  private static class FactorSquareFreeList extends Factor {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      VariablesSet eVar = new VariablesSet(ast.arg1());
      try {
        IExpr expr = F.evalExpandAll(ast.arg1(), engine);
        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // List<IExpr> varList = r;
        List<IExpr> varList = eVar.getVarList().copyTo();

        // if (ast.isAST2()) {
        // return factorWithOption(ast, expr, varList, true);
        // }
        return factorList(expr, varList, true);

      } catch (JASConversionException jce) {
        // toInt() conversion failed
        LOGGER.debug("FactorSquareFreeList.evaluate() failed", jce);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    private static IExpr factorList(IExpr expr, List<IExpr> varList, boolean factorSquareFree)
        throws JASConversionException {
      if (!expr.isAST()) {
        if (expr.isNumber()) {
          return F.list(F.list(expr, F.C1));
        }
        return F.list(F.list(F.C1, F.C1), F.list(expr, F.C1));
      }
      JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
      GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, false);
      if (polyRat == null) {
        return F.list(expr);
      }
      Object[] objects = jas.factorTerms(polyRat);
      java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
      java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
      SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
      try {
        GenPolynomial<edu.jas.arith.BigInteger> poly =
            (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
        FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
            FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);

        if (factorSquareFree) {
          map = factorAbstract.squarefreeFactors(poly);
        } else {
          map = factorAbstract.factors(poly);
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // JAS may throw RuntimeExceptions
        return F.list(expr);
      }
      IASTAppendable result = F.ListAlloc(map.size() + 1);
      if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
        result.append(F.list(F.fraction(gcd, lcm), F.C1));
      }
      for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
        if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
          continue;
        }
        result.append(F.list(jas.integerPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
      }
      return result;
    }
  }

  /**
   *
   *
   * <h2>FactorTerms</h2>
   *
   * <pre>
   * <code>FactorTerms(poly)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pulls out any overall numerical factor in <code>poly</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; FactorTerms(3+3/4*x^3+12/17*x^2, x)
   * 3/68*(17*x^3+16*x^2+68)
   * </code>
   * </pre>
   */
  static class FactorTerms extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr temp = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (temp.isPresent()) {
        return temp;
      }
      VariablesSet eVar = null;
      IAST variableList = F.NIL;
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg2.isSymbol()) {
          ISymbol variable = (ISymbol) arg2;
          variableList = F.list(variable);
        } else if (arg2.isList()) {
          variableList = (IAST) arg2;
        }
      }

      IExpr expanded = F.evalExpandAll(arg1, engine);
      if (expanded.isPlus()) {
        temp = AlgebraUtil.factorTermsPlus((IAST) expanded, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      if (variableList.isNIL()) {
        eVar = new VariablesSet(arg1);
        // if (!eVar.isSize(1)) {
        // // FactorTerms currently only possible for univariate polynomials
        // return arg1;
        // }
        variableList = eVar.getVarList();
      }


      // if (variableList.isNIL() || variableList.size() != 2) {
      // // FactorTerms only possible for univariate polynomials
      // return arg1;
      // }
      List<IExpr> varList = variableList.copyTo();
      IExpr expr = expanded; // F.evalExpandAll(arg1, engine);
      try {
        JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
        GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
        if (poly == null) {
          return arg1;
        }
        Object[] objects = jas.factorTerms(poly);
        java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
        java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
        if (lcm.signum() == 0) {
          // no changes
          return expr;
        }
        GenPolynomial<edu.jas.arith.BigInteger> iPoly =
            (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
        IASTAppendable result = F.TimesAlloc(2);
        result.append(F.fraction(gcd, lcm));
        result.append(jas.integerPoly2Expr(iPoly));
        return result;
      } catch (JASConversionException e1) {
        // try {
        // if (variableList.isAST1()) {
        // IAST list = PolynomialFunctions.rootsOfExprPolynomial(expr, variableList, true);
        // if (list.isList()) {
        // IExpr x = variableList.arg1();
        // IASTAppendable result = F.TimesAlloc(list.size());
        // list.forEach(arg -> result.append(F.Plus(x, arg)));
        // // for (int i = 1; i < list.size(); i++) {
        // // result.append(F.Plus(x, list.get(i)));
        // // }
        // return result;
        // }
        // }
        // } catch (JASConversionException e2) {
        // LOGGER.debug("FactorTerms.evaluate() failed", e2);
        // }

      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  static class FactorTermsList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (!arg1.isFree(S.List)) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.List(arg1), engine);
      }
      VariablesSet eVar = null;
      IAST variableList = F.NIL;
      if (ast.isAST2()) {
        if (ast.arg2().isSymbol()) {
          ISymbol variable = (ISymbol) ast.arg2();
          variableList = F.list(variable);
        } else if (ast.arg2().isList()) {
          variableList = (IAST) ast.arg2();
        }
      }
      IExpr expanded = F.evalExpandAll(arg1, engine);
      if (expanded.isPlus()) {
        Optional<IRational> gcd = AlgebraUtil.factorTermsGCD((IAST) expanded, engine);
        if (gcd.isPresent()) {
          IRational rationalGCD = gcd.get();
          return F.List(rationalGCD, F.Expand(F.Times(rationalGCD.inverse(), expanded)))
              .eval(engine);
        }
      }
      if (variableList.isNIL()) {
        eVar = new VariablesSet(arg1);
        if (!eVar.isSize(1)) {
          // FactorTerms only possible for univariate polynomials
          if (eVar.isSize(0)) {
            if (arg1.isTimes() && arg1.first().isNumber()) {
              return F.List(arg1.first(), arg1.rest()).eval(engine);
            }
          }
          return F.List(F.C1, arg1);
        }
        variableList = eVar.getVarList();
      }

      // if (variableList.isNIL() || variableList.size() != 2) {
      // // FactorTerms only possible for univariate polynomials
      // return F.NIL;
      // }
      List<IExpr> varList = variableList.copyTo();
      IExpr expr = expanded;
      try {
        JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
        GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
        if (poly == null) {
          return F.List(F.C1, arg1);
        }
        Object[] objects = jas.factorTerms(poly);
        java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
        java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
        if (lcm.signum() == 0) {
          // no changes
          return expr;
        }
        GenPolynomial<edu.jas.arith.BigInteger> iPoly =
            (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
        return F.List(F.fraction(gcd, lcm), jas.integerPoly2Expr(iPoly));
      } catch (JASConversionException e1) {
      }
      return F.List(F.C1, arg1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <h2>Numerator</h2>
   *
   * <pre>
   * <code>Numerator(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the numerator in <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Numerator(a / b)
   * a
   * &gt;&gt; Numerator(2 / 3)
   * 2
   * &gt;&gt; Numerator(a + b)
   * a + b
   * </code>
   * </pre>
   */
  private static class Numerator extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean trig = false;
      if (ast.isAST2()) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        if (options.isInvalidPosition(1)) {
          return options.printNonopt(ast, 1, engine);
        }
        trig = options.isTrue(S.Trig);
      }

      IExpr arg = ast.arg1();
      if (arg.isRational()) {
        return ((IRational) arg).numerator();
      }
      Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(arg, trig);
      if (parts.isPresent()) {
        return parts.get()[0];
      }
      return arg;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Trig, S.False)));
    }


  }

  /**
   *
   *
   * <h2>PolynomialExtendedGCD</h2>
   *
   * <pre>
   * <code>PolynomialExtendedGCD(p, q, x)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the extended GCD ('greatest common divisor') of the univariate polynomials <code>p
   * </code> and <code>q</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialExtendedGCD(p, q, x, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the extended GCD ('greatest common divisor') of the univariate polynomials <code>p
   * </code> and <code>q</code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href=
   * "https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Polynomial_extended_Euclidean_algorithm">Wikipedia:
   * Polynomial extended Euclidean algorithm</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PolynomialExtendedGCD(x^8 + x^4 + x^3 + x + 1, x^6 + x^4 + x + 1, x, Modulus-&gt;2)
   * {1,{1+x^2+x^3+x^4+x^5,x+x^3+x^6+x^7}}
   * </code>
   * </pre>
   */
  private static class PolynomialExtendedGCD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST variables = VariablesSet.getAlgebraicVariables(ast.arg3());
      if (variables.size() != 2) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(ast.arg3()), engine);
      }
      IExpr expr1 = F.evalExpandAll(ast.arg1(), engine);
      IExpr expr2 = F.evalExpandAll(ast.arg2(), engine);
      if (!expr1.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(expr1), engine);
      }
      if (!expr2.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(expr2), engine);
      }

      if (ast.size() == 5) {
        // List<IExpr> varList = r;
        List<IExpr> varList = variables.copyTo();
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 4, engine);
        IExpr option = options.getOption(S.Modulus);
        if (option.isInteger() && !option.isZero()) {
          try {
            // found "Modulus" option => use ModIntegerRing
            ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
            JASModInteger jas = new JASModInteger(varList, modIntegerRing);
            GenPolynomial<ModLong> poly1 = jas.expr2JAS(expr1);
            GenPolynomial<ModLong> poly2 = jas.expr2JAS(expr2);
            GenPolynomial<ModLong>[] result = poly1.egcd(poly2);
            IASTAppendable list = F.ListAlloc(2);
            list.append(jas.modLongPoly2Expr(result[0]));
            IASTAppendable subList = F.ListAlloc(2);
            subList.append(jas.modLongPoly2Expr(result[1]));
            subList.append(jas.modLongPoly2Expr(result[2]));
            list.append(subList);
            return list;
          } catch (ArithmeticException aex) {
            LOGGER.log(engine.getLogLevel(), S.PolynomialExtendedGCD, aex);
          } catch (JASConversionException e) {
            LOGGER.debug("PolynomialExtendedGCD.evaluate() failed", e);
          }
          return F.NIL;
        }
      }
      try {
        List<IExpr> varList = variables.copyTo();
        IExpr variable = varList.get(0);
        if (expr1.isFree(variable) || expr2.isFree(variable)) {
          IASTAppendable list = F.ListAlloc(2);
          list.append(F.C1);
          IASTAppendable subList = F.ListAlloc(2);
          subList.append(F.C0);
          subList.append(F.Power(expr2, F.CN1));
          list.append(subList);
          return list;
        }
        if (!expr1.isPolynomial(variables) && !expr2.isPolynomial(variables)) {
          IASTAppendable list = F.ListAlloc(2);
          list.append(expr2);
          IASTAppendable subList = F.ListAlloc(2);
          subList.append(F.C0);
          subList.append(F.C1);
          list.append(subList);
          return list;
        }

        JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
        GenPolynomial<BigRational> poly1 = jas.expr2JAS(expr1, false);
        if (poly1 == null) {
          return polynomialExtendedExpr(expr1, expr2, variables, engine);
        }
        GenPolynomial<BigRational> poly2 = jas.expr2JAS(expr2, false);
        if (poly2 == null) {
          return polynomialExtendedExpr(expr1, expr2, variables, engine);
        }
        GenPolynomial<BigRational>[] result = poly1.egcd(poly2);
        IASTAppendable list = F.ListAlloc(2);
        list.append(jas.rationalPoly2Expr(result[0], true));
        IASTAppendable subList = F.ListAlloc(2);
        subList.append(jas.rationalPoly2Expr(result[1], true));
        subList.append(jas.rationalPoly2Expr(result[2], true));
        list.append(subList);
        return list;
      } catch (JASConversionException e0) {
        return polynomialExtendedExpr(expr1, expr2, variables, engine);
      }

    }

    private static IExpr polynomialExtendedExpr(IExpr expr1, IExpr expr2, IAST variables,
        EvalEngine engine) {
      try {
        ExprPolynomialRing ring = new ExprPolynomialRing(variables);
        ExprPolynomial poly1 = ring.create(expr1);
        ExprPolynomial poly2 = ring.create(expr2);
        ExprPolynomial[] result = poly1.egcd(poly2);
        if (result != null) {
          IASTAppendable list = F.ListAlloc(2);
          list.append(result[0].getExpr());
          IASTAppendable subList = F.ListAlloc(2);
          subList.append(S.Together.of(engine, result[1].getExpr()));
          subList.append(S.Together.of(engine, result[2].getExpr()));
          list.append(subList);
          return list;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("PolynomialExtendedGCD.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Modulus, F.C0)));
    }
  }

  /**
   *
   *
   * <h2>PolynomialGCD</h2>
   *
   * <pre>
   * <code>PolynomialGCD(p, q)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the GCD ('greatest common divisor') of the polynomials <code>p</code> and <code>q
   * </code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialGCD(p, q, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the GCD ('greatest common divisor') of the polynomials <code>p</code> and <code>q
   * </code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   */
  private static class PolynomialGCD extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.isAST0()) {
        return F.NIL;
      }
      if (checkPolyStruct(ast, engine)) {
        if (ast.isAST1()) {
          IExpr arg1 = ast.arg1();
          if (arg1.isNegativeResult()) {
            return arg1.negate();
          }
          return arg1;
        }
        VariablesSet eVar = new VariablesSet();
        eVar.addVarList(ast, 1);

        IExpr expr = F.evalExpandAll(ast.arg1(), engine);
        if (ast.size() > 3 && ast.last().isRuleAST()) {
          return gcdWithOption(ast, expr, eVar, engine);
        }
        try {
          List<IExpr> varList = eVar.getVarList().copyTo();
          JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
          GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
          if (poly == null) {
            IExpr result = polynomialGCDExpr(ast, eVar, engine);
            if (result.isPresent()) {
              return result;
            }
          } else {
            GenPolynomial<BigRational> temp;
            GreatestCommonDivisorAbstract<BigRational> factory =
                GCDFactory.getImplementation(BigRational.ZERO, true);

            // TODO https://github.com/kredel/java-algebra-system/issues/15
            // JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
            // GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
            // GenPolynomial<BigRational> temp;
            // GreatestCommonDivisorAbstract<BigRational> factory =
            // GCDFactory.getImplementation(BigRational.ZERO);
            for (int i = 2; i < ast.size(); i++) {
              expr = F.evalExpandAll(ast.get(i), engine);
              temp = jas.expr2JAS(expr, false);
              if (temp == null) {
                throw JASConversionException.FAILED;
              }
              poly = factory.gcd(poly, temp);
            }
            // TODO https://github.com/kredel/java-algebra-system/issues/15
            return jas.rationalPoly2Expr(poly, false);
            // return jas.integerPoly2Expr(poly.monic());
          }
        } catch (ArithmeticException aex) {
          LOGGER.log(engine.getLogLevel(), S.PolynomialGCD, aex);
          return F.NIL;
        } catch (ClassCastException | JASConversionException e) {
          IExpr result = polynomialGCDExpr(ast, eVar, engine);
          if (result.isPresent()) {
            return result;
          }
        }
        IAST list = ast.setAtCopy(0, S.List);
        Optional<IExpr[]> result = AlgebraUtil.InternalFindCommonFactorPlus.findCommonFactors(list, false);
        if (result.isPresent()) {
          return result.get()[0];
        }
        return F.C1;
      }
      return F.NIL;
    }

    private static IExpr polynomialGCDExpr(final IAST ast, VariablesSet eVar, EvalEngine engine) {
      IExpr expr;
      try {
        if (eVar.size() == 0) {
          return F.NIL;
        }
        IAST vars = eVar.getVarList();
        expr = F.evalExpandAll(ast.arg1(), engine);

        ExprPolynomialRing ring = new ExprPolynomialRing(vars);
        ExprPolynomial p1 = ring.create(expr);
        ExprPolynomial p2;
        for (int i = 2; i < ast.size(); i++) {
          expr = F.evalExpandAll(ast.get(i), engine);
          p2 = ring.create(expr);
          p1 = p1.gcd(p2);
        }
        return p1.getExpr();

        // ExprPolynomialRing ring = new ExprPolynomialRing(vars);
        // ExprPolynomial pol1 = ring.create(expr);
        // ExprPolynomial pol2;
        // // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // List<IExpr> varList = eVar.getVarList().copyTo();
        // JASIExpr jas = new JASIExpr(varList, true);
        // GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
        // GenPolynomial<IExpr> p2;
        //
        // GreatestCommonDivisor<IExpr> factory =
        // GCDFactory.getImplementation(ExprRingFactory.CONST);
        // for (int i = 2; i < ast.size(); i++) {
        // expr = F.evalExpandAll(ast.get(i), engine);
        // p2 = jas.expr2IExprJAS(expr);
        // p1 = factory.gcd(p1, p2);
        // }
        // return jas.exprPoly2Expr(p1);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("PolynomialGCD.evaluate() failed", rex);
      }
      return F.NIL;
    }

    private IExpr gcdWithOption(final IAST ast, IExpr expr, VariablesSet eVar,
        final EvalEngine engine) {
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
      IExpr option = options.getOption(S.Modulus);
      if (option.isInteger() && !option.isZero()) {
        return modulusGCD(ast, expr, eVar, option);
      }
      return F.NIL;
    }

    private IExpr modulusGCD(final IAST ast, IExpr expr, VariablesSet eVar, IExpr option) {
      try {
        // found "Modulus" option => use ModIntegerRing
        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        // ModIntegerRing modIntegerRing =
        // JASConvert.option2ModIntegerRing((IReal) option);
        // JASConvert<ModInteger> jas = new
        // JASConvert<ModInteger>(r.toList(), modIntegerRing);
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
        JASModInteger jas = new JASModInteger(eVar.getArrayList(), modIntegerRing);
        GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
        GenPolynomial<ModLong> temp;
        GreatestCommonDivisorAbstract<ModLong> factory =
            GCDFactory.getImplementation(modIntegerRing);

        for (int i = 2; i < ast.argSize(); i++) {
          final IExpr arg = ast.get(i);
          eVar = new VariablesSet(arg);
          if (!eVar.isSize(1)) {
            // gcd only possible for univariate polynomials
            return F.NIL;
          }
          expr = F.evalExpandAll(arg);
          temp = jas.expr2JAS(expr);
          poly = factory.gcd(poly, temp);
        }
        return Algebra.factorModulus(jas, modIntegerRing, poly, false);
      } catch (JASConversionException e) {
        LOGGER.debug("PolynomialGCD.modulusGCD() failed", e);
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Modulus, F.C0)));
    }
  }

  /**
   *
   *
   * <h2>PolynomialLCM</h2>
   *
   * <pre>
   * <code>PolynomialLCM(p, q)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the LCM ('least common multiple') of the polynomials <code>p</code> and <code>q
   * </code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialLCM(p, q, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the LCM ('least common multiple') of the polynomials <code>p</code> and <code>q
   * </code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   */
  private static class PolynomialLCM extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.NIL;
      }

      if (checkPolyStruct(ast, engine)) {
        if (ast.isAST1()) {
          IExpr arg1 = ast.arg1();
          if (arg1.isNegativeResult()) {
            return arg1.negate();
          }
          return arg1;
        }

        VariablesSet eVar = new VariablesSet();
        eVar.addVarList(ast, 1);

        // ASTRange r = new ASTRange(eVar.getVarList(), 1);
        IExpr expr = F.evalExpandAll(ast.arg1(), engine);
        if (ast.size() > 3) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
          IExpr option = options.getOption(S.Modulus);
          if (option.isInteger() && !option.isZero()) {
            try {
              // found "Modulus" option => use ModIntegerRing
              List<IExpr> varList = eVar.getVarList().copyTo();
              ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
              JASModInteger jas = new JASModInteger(varList, modIntegerRing);
              GenPolynomial<ModLong> poly = jas.expr2JAS(expr);
              GenPolynomial<ModLong> temp;
              GreatestCommonDivisorAbstract<ModLong> factory =
                  GCDFactory.getImplementation(modIntegerRing);
              for (int i = 2; i < ast.argSize(); i++) {
                expr = F.evalExpandAll(ast.get(i), engine);
                temp = jas.expr2JAS(expr);
                poly = factory.lcm(poly, temp);
              }

              return Algebra.factorModulus(jas, modIntegerRing, poly.monic(), false);
              // return jas.modLongPoly2Expr(poly.monic());

            } catch (ArithmeticException aex) {
              LOGGER.log(engine.getLogLevel(), S.PolynomialLCM, aex);
              return F.NIL;
            } catch (JASConversionException e) {
              try {
                if (eVar.size() == 0) {
                  return F.NIL;
                }
                expr = F.evalExpandAll(ast.arg1(), engine);
                IAST vars = eVar.getVarList();
                ExprPolynomialRing ring = new ExprPolynomialRing(vars);
                ExprPolynomial pol1 = ring.create(expr);
                // ASTRange r = new ASTRange(eVar.getVarList(), 1);
                List<IExpr> varList = eVar.getVarList().copyTo();
                JASIExpr jas = new JASIExpr(varList, true);
                GenPolynomial<IExpr> p1 = jas.expr2IExprJAS(pol1);
                GenPolynomial<IExpr> p2;

                GreatestCommonDivisor<IExpr> factory =
                    GCDFactory.getImplementation(ExprRingFactory.CONST);
                for (int i = 2; i < ast.size(); i++) {
                  expr = F.evalExpandAll(ast.get(i), engine);
                  p2 = jas.expr2IExprJAS(expr);
                  p1 = factory.lcm(p1, p2);
                }
                return jas.exprPoly2Expr(p1);
              } catch (RuntimeException rex) {
                Errors.rethrowsInterruptException(rex);
                LOGGER.debug("PolynomialLCM.evaluate() failed", rex);
              }
            }
            return F.NIL;
          }
        }
        try {
          List<IExpr> varList = eVar.getVarList().copyTo();
          JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
          GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
          if (poly == null) {
            IExpr result = polynomialLCMExpr(ast);
            if (result.isPresent()) {
              return result;
            }
            return ast.setAtCopy(0, S.Times);
          }
          GenPolynomial<BigRational> gcd;
          boolean evaled = false;
          GenPolynomial<BigRational> temp;
          GreatestCommonDivisorAbstract<BigRational> factory =
              GCDFactory.getImplementation(BigRational.ZERO, true);
          for (int i = 2; i < ast.size(); i++) {
            expr = F.evalExpandAll(ast.get(i), engine);
            temp = jas.expr2JAS(expr, false);
            if (temp == null) {
              IExpr result = polynomialLCMExpr(ast);
              if (result.isPresent()) {
                return result;
              }
              return ast.setAtCopy(0, S.Times);
            }
            if (!evaled) {
              gcd = factory.gcd(poly, temp);
              if (!gcd.isONE()) {
                evaled = true;
              }
            }
            poly = factory.lcm(poly, temp);
          }
          if (evaled) {
            return jas.rationalPoly2Expr(poly, false);
          }
        } catch (ClassCastException | JASConversionException e) {
          LOGGER.debug("PolynomialLCM.evaluate() failed", e);
          IExpr result = polynomialLCMExpr(ast);
          if (result.isPresent()) {
            return result;
          }
        }
        return ast.setAtCopy(0, S.Times);
      }
      return F.NIL;
    }

    private static IExpr polynomialLCMExpr(final IAST ast) {
      IAST list = ast.setAtCopy(0, S.List);
      Optional<IExpr[]> result = AlgebraUtil.InternalFindCommonFactorPlus.findCommonFactors(list, true);
      if (result.isPresent()) {
        return F.Times(result.get()[0], ((IAST) result.get()[1]).setAtCopy(0, S.Times));
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      setOptions(newSymbol, //
          F.list(F.Rule(S.Modulus, F.C0)));
    }
  }

  /**
   *
   *
   * <h2>PolynomialQ</h2>
   *
   * <pre>
   * <code>PolynomialQ(p, x)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return <code>True</code> if <code>p</code> is a polynomial for the variable <code>x</code>.
   * Return <code>False</code> in all other cases.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialQ(p, {x, y, ...})
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return <code>True</code> if <code>p</code> is a polynomial for the variables <code>x, y, ...
   * </code> defined in the list. Return <code>False</code> in all other cases.
   *
   * </blockquote>
   */
  private static class PolynomialQ extends AbstractFunctionEvaluator
      implements BiPredicate<IExpr, IExpr> {

    /**
     * Returns <code>True</code> if the given expression is a polynomial object; <code>False</code>
     * otherwise
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST variablesList = F.NIL;
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        // mimic S.Variables
        IAST temp = VariablesSet.getAlgebraicVariables(arg1);
        if (temp.isList()) {
          variablesList = temp;
        } else {
          return F.NIL;
        }

        if (variablesList.size() == 0) {
          return S.True;
        }
      } else {
        variablesList = ast.arg2().makeList();
        Set<IExpr> fVariablesSet = new HashSet<IExpr>();
        VariablesSet.addAlgebraicVariables(fVariablesSet, variablesList);
        for (int i = 1; i < variablesList.size(); i++) {
          IExpr variable = variablesList.get(i);
          if (variable.isPlus() || variable.isTimes()) {
            // `1` is not a valid variable.
            Errors.printMessage(S.General, "ivar", F.List(variable));
            return F.NIL;
          }
        }
        // if (!fVariablesSet.contains(variable)) {
        // // `1` is not a valid variable.
        // Errors.printMessage(S.General, "ivar", F.List(variable));
        // return F.NIL;
        // }
      }
      IExpr cached = engine.getCache(ast);
      if (cached != null) {
        return cached;
      }
      IAST subst = substituteVariablesInPolynomial(arg1, variablesList, "§PolynomialQ", false);
      if (subst.isPresent()) {
        IExpr result = F.booleSymbol(subst.arg1().isPolynomial((IAST) subst.arg2()));
        engine.putCache(ast, result);
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean test(final IExpr firstArg, final IExpr secondArg) {
      return firstArg.isPolynomial(secondArg.makeList());
    }
  }

  /**
   *
   *
   * <h2>PolynomialQuotient</h2>
   *
   * <pre>
   * <code>PolynomialQuotient(p, q, x)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the polynomial quotient of the polynomials <code>p</code> and <code>q</code> for the
   * variable <code>x</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialQuotient(p, q, x, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the polynomial quotient of the polynomials <code>p</code> and <code>q</code> for the
   * variable <code>x</code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   */
  private static class PolynomialQuotient extends PolynomialQuotientRemainder {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {

      IExpr variable;
      if (ast.arg3().isAST()) {
        variable = ast.arg3();
      } else {
        variable = Validate.checkIsVariable(ast, 3, engine);
        if (variable.isNIL()) {
          return F.NIL;
        }
      }
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      try {
        // IExpr denom1 = S.Denominator.of(engine, arg1);
        // IExpr denom2 = S.Denominator.of(engine, arg2);
        // if (!denom1.isOne() || !denom2.isOne()) {
        // IExpr numer1 = S.Numerator.of(engine, arg1);
        // IExpr numer2 = S.Numerator.of(engine, arg2);
        // arg1 = F.ExpandAll.of(engine, F.Times(numer1, denom2));
        // arg2 = F.ExpandAll.of(engine, F.Times(denom1, numer2));
        // } else {
        arg1 = F.ExpandAll.of(engine, arg1);
        arg2 = F.ExpandAll.of(engine, arg2);
        // }

        if (arg1.isZero() || arg2.isZero()) {
          return F.NIL;
        }
        if (!arg1.isPolynomialStruct()) {
          // `1` is not a polynomial.
          return Errors.printMessage(ast.topHead(), "poly", F.list(arg1), engine);
        }
        if (!arg2.isPolynomialStruct()) {
          // `1` is not a polynomial.
          return Errors.printMessage(ast.topHead(), "poly", F.list(arg2), engine);
        }
        IExpr option = options[0];
        if (isModpMessage(S.PolynomialQuotient, option)) {
          return F.NIL;
        }
        if (!option.isZero()) {
          Optional<IExpr[]> result = quotientRemainderModInteger(arg1, arg2, variable, option);
          if (result.isPresent()) {
            return result.get()[0];
          }
          return F.NIL;
        }
        Optional<IExpr[]> result = quotientRemainder(arg1, arg2, variable);
        if (result.isPresent()) {
          return result.get()[0];
        }
      } catch (ArithmeticException aex) {
        // division by zero
        LOGGER.log(engine.getLogLevel(), S.PolynomialQuotient, aex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Modulus};
      IExpr[] optionValues = new IExpr[] {F.C0};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }

  /**
   *
   *
   * <h2>PolynomialQuotientRemainder</h2>
   *
   * <pre>
   * <code>PolynomialQuotientRemainder(p, q, x)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list with the polynomial quotient and remainder of the polynomials <code>p</code> and
   * <code>q</code> for the variable <code>x</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialQuotientRemainder(p, q, x, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns list with the polynomial quotient and remainder of the polynomials <code>p</code> and
   * <code>q</code> for the variable <code>x</code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   */
  private static class PolynomialQuotientRemainder extends AbstractFunctionOptionEvaluator {

    public static Optional<IExpr[]> quotientRemainder(final IExpr arg1, IExpr arg2,
        IExpr variable) {
      if (arg1.isFree(variable) && //
          arg2.isFree(variable)) {
        return Optional.of(new IExpr[] { //
            F.Divide(arg1, arg2), //
            F.C0});
      }
      try {
        JASConvert<BigRational> jas = new JASConvert<BigRational>(variable, BigRational.ZERO);
        GenPolynomial<BigRational> poly1 = jas.expr2JAS(arg1, false);
        if (poly1 == null) {
          return polynomialQuotientRemainderExpr(arg1, arg2, variable);
        } else {
          GenPolynomial<BigRational> poly2 = jas.expr2JAS(arg2, false);
          if (poly2 == null) {
            return polynomialQuotientRemainderExpr(arg1, arg2, variable);
          } else {
            GenPolynomial<BigRational>[] divRem = poly1.quotientRemainder(poly2);
            return Optional.of(new IExpr[] { //
                jas.rationalPoly2Expr(divRem[0], false), //
                jas.rationalPoly2Expr(divRem[1], false)});
          }
        }
      } catch (JASConversionException e1) {
        return polynomialQuotientRemainderExpr(arg1, arg2, variable);
      }
    }

    private static Optional<IExpr[]> polynomialQuotientRemainderExpr(final IExpr arg1, IExpr arg2,
        IExpr variable) {
      try {
        ExprPolynomialRing ring = new ExprPolynomialRing(F.list(variable));
        ExprPolynomial poly1 = ring.create(arg1);
        ExprPolynomial poly2 = ring.create(arg2);
        ExprPolynomial[] divRem = poly1.quotientRemainder(poly2);
        if (divRem == null) {
          return Optional.empty();
        }
        return Optional.of(new IExpr[] { //
            divRem[0].getExpr(), //
            divRem[1].getExpr()});
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // rex.printStackTrace();
        LOGGER.debug("PolynomialQuotientRemainder.quotientRemainder() failed", rex);
      }
      return Optional.empty();
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr temp = engine.getCache(ast);
      if (temp != null) {
        return temp;
      }
      IExpr variable;
      if (ast.arg3().isAST()) {
        variable = ast.arg3();
      } else {
        variable = Validate.checkIsVariable(ast, 3, engine);
        if (variable.isNIL()) {
          return F.NIL;
        }
      }
      // IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
      // IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (!arg1.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(arg1), engine);
      }
      if (!arg2.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(arg2), engine);
      }

      try {
        // IExpr denom1 = S.Denominator.of(engine, arg1);
        // IExpr denom2 = S.Denominator.of(engine, arg2);
        // if (!denom1.isOne() || !denom2.isOne()) {
        // IExpr numer1 = S.Numerator.of(engine, arg1);
        // IExpr numer2 = S.Numerator.of(engine, arg2);
        // arg1 = F.ExpandAll.of(engine,numer1);// F.Times(numer1, denom2));
        // arg2 = F.ExpandAll.of(engine,numer2);// F.Times(denom1, numer2));
        // } else {
        arg1 = F.ExpandAll.of(engine, arg1);
        arg2 = F.ExpandAll.of(engine, arg2);
        // }
        if (arg2.isZero()) {
          return F.NIL;
        }
        IExpr result = F.NIL;
        IExpr option = options[0];
        if (isModpMessage(S.PolynomialQuotientRemainder, option)) {
          return F.NIL;
        }
        if (!option.isZero()) {
          Optional<IExpr[]> quotientRemainderModInteger =
              quotientRemainderModInteger(arg1, arg2, variable, option);
          if (quotientRemainderModInteger.isPresent()) {
            IExpr[] elements = quotientRemainderModInteger.get();
            result = F.list(elements[0], elements[1]);
            engine.putCache(ast, result);
            return result;
          }
          return F.NIL;
        }
        Optional<IExpr[]> quotientRemainder = quotientRemainder(arg1, arg2, variable);
        if (quotientRemainder.isPresent()) {
          result = F.list(quotientRemainder.get()[0], quotientRemainder.get()[1]);
        }
        engine.putCache(ast, result);
        return result;
      } catch (ArithmeticException aex) {
        // division by zero
        LOGGER.log(engine.getLogLevel(), S.PolynomialQuotientRemainder, aex);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("PolynomialQuotientRemainder.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    public Optional<IExpr[]> quotientRemainderModInteger(IExpr arg1, IExpr arg2, IExpr variable,
        IExpr option) {
      try {
        // found "Modulus" option => use ModIntegerRing
        ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
        JASModInteger jas = new JASModInteger(variable, modIntegerRing);
        GenPolynomial<ModLong> poly1 = jas.expr2JAS(arg1);
        GenPolynomial<ModLong> poly2 = jas.expr2JAS(arg2);
        if (poly2.isZERO()) {
          return Optional.empty();
        }
        GenPolynomial<ModLong>[] divRem = poly1.quotientRemainder(poly2);
        IExpr[] result = new IExpr[2];
        result[0] = jas.modLongPoly2Expr(divRem[0]);
        result[1] = jas.modLongPoly2Expr(divRem[1]);
        return Optional.of(result);
      } catch (JASConversionException e) {
        LOGGER.debug("PolynomialQuotientRemainder.quotientRemainderModInteger() failed", e);
      }
      return Optional.empty();
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Modulus};
      IExpr[] optionValues = new IExpr[] {F.C0};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }

  /**
   *
   *
   * <h2>PolynomialQuotient</h2>
   *
   * <pre>
   * <code>PolynomialQuotient(p, q, x)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the polynomial remainder of the polynomials <code>p</code> and <code>q</code> for the
   * variable <code>x</code>.
   *
   * </blockquote>
   *
   * <pre>
   * <code>PolynomialQuotient(p, q, x, Modulus -&gt; prime)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the polynomial remainder of the polynomials <code>p</code> and <code>q</code> for the
   * variable <code>x</code> modulus the <code>prime</code> integer.
   *
   * </blockquote>
   */
  private static class PolynomialRemainder extends PolynomialQuotientRemainder {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr variable;
      if (ast.arg3().isAST()) {
        variable = ast.arg3();
      } else {
        variable = Validate.checkIsVariable(ast, 3, engine);
        if (variable.isNIL()) {
          return F.NIL;
        }
      }
      IExpr arg1 = F.evalExpandAll(ast.arg1(), engine);
      IExpr arg2 = F.evalExpandAll(ast.arg2(), engine);
      if (arg2.isZero()) {
        return F.NIL;
      }
      if (!arg1.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(arg1), engine);
      }
      if (!arg2.isPolynomialStruct()) {
        // `1` is not a polynomial.
        return Errors.printMessage(ast.topHead(), "poly", F.list(arg2), engine);
      }
      try {
        IExpr option = options[0];
        if (isModpMessage(S.PolynomialRemainder, option)) {
          return F.NIL;
        }
        if (!option.isZero()) {
          Optional<IExpr[]> result = quotientRemainderModInteger(arg1, arg2, variable, option);
          if (result.isPresent()) {
            return result.get()[1];
          }
          return F.NIL;
        }
        Optional<IExpr[]> result = quotientRemainder(arg1, arg2, variable);
        if (result.isPresent()) {
          return result.get()[1];
        }

      } catch (

      ArithmeticException aex) {
        // division by zero
        LOGGER.log(engine.getLogLevel(), S.PolynomialRemainder, aex);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("PolynomialRemainder.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.Modulus};
      IExpr[] optionValues = new IExpr[] {F.C0};
      setOptions(newSymbol, optionKeys, optionValues);
    }
  }


  /**
   *
   *
   * <h2>PowerExpand</h2>
   *
   * <pre>
   * <code>PowerExpand(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * expands out powers of the form <code>(x^y)^z</code> and <code>(x*y)^z</code> in <code>expr
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; PowerExpand((a ^ b) ^ c)
   * a^(b*c)
   *
   * &gt;&gt; PowerExpand((a * b) ^ c)
   * a^c*b^c
   * </code>
   * </pre>
   *
   * <p>
   * <code>PowerExpand</code> is not correct without certain assumptions:
   *
   * <pre>
   * <code>&gt;&gt; PowerExpand((x ^ 2) ^ (1/2))
   * x
   * </code>
   * </pre>
   */
  private static class PowerExpand extends AbstractFunctionEvaluator {

    private static class PowerExpandVisitor extends VisitorExpr {
      final boolean assumptions;

      public PowerExpandVisitor(boolean assumptions) {
        super();
        this.assumptions = assumptions;
      }

      /** {@inheritDoc} */
      @Override
      public IExpr visit2(IExpr head, IExpr arg1) {
        boolean evaled = false;
        // IExpr x1 = arg1;
        IExpr result = arg1.accept(this);
        if (result.isPresent()) {
          evaled = true;
          arg1 = result;
        }
        if (head.equals(S.Log)) {
          if (arg1.isRational()) {
            return powerExpandLogRational((IRational) arg1);
          }
          if (arg1.isPower()) {
            // Log[x_ ^ y_] :> y * Log(x)
            IAST logResult = Times(arg1.exponent(), powerExpand(Log(arg1.base()), assumptions));
            if (assumptions) {
              IAST floorResult = Floor(Divide(Subtract(Pi, Im(logResult)), F.C2Pi));
              IAST timesResult = Times(C2, I, Pi, floorResult);
              return Plus(logResult, timesResult);
            }
            return logResult;
          }
          if (arg1.isTimes()) {
            IAST timesAST = (IAST) arg1;
            // Log[x_ * y_ * z_] :> Log(x)+Log(y)+Log(z)
            IAST logResult = timesAST.setAtCopy(0, S.Plus);
            logResult = logResult.mapThread(F.Log(F.Slot1), 1);
            return powerExpand(logResult, assumptions);
          }
        } else if (head.equals(S.ProductLog)) {
          if (arg1.isTimes2()) {
            // ProductLog[x_ * Exp[x_]] :> x
            IExpr a1 = arg1.first();
            IExpr a2 = arg1.second();
            if (a2.isExp() && a2.second().equals(a1)) {
              return a1;
            }
            if (a1.isExp() && a1.second().equals(a2)) {
              return a2;
            }
          }
        }
        if (evaled) {
          return F.unaryAST1(head, arg1);
        }
        return F.NIL;
      }

      /** {@inheritDoc} */
      @Override
      public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
        boolean evaled = false;
        IExpr x1 = arg1;
        IExpr result = arg1.accept(this);
        if (result.isPresent()) {
          evaled = true;
          x1 = result;
        }
        IExpr x2 = arg2;
        result = arg2.accept(this);
        if (result.isPresent()) {
          evaled = true;
          x2 = result;
        }
        if (head.equals(Power)) {
          if (x1.isTimes()) {
            // Power[x_ * y_, z_] :> x^z * y^z
            IAST timesAST = (IAST) x1;
            IASTMutable timesResult = x1.mapThread(Power(F.Slot1, x2), 1);
            if (assumptions) {
              IASTAppendable plusResult = F.PlusAlloc(timesAST.size());
              plusResult.append(C1D2);
              plusResult.appendArgs(timesAST.size(),
                  i -> Negate(Divide(Arg(timesAST.get(i)), F.C2Pi)));
              IAST expResult = Power(E, Times(C2, I, Pi, x2, Floor(plusResult)));
              if (!(timesResult instanceof IASTAppendable)) {
                timesResult = timesResult.copyAppendable();
              }
              ((IASTAppendable) timesResult).append(expResult);
              return timesResult;
            }
            return timesResult;
          }
          if (x1.isPower()) {
            return power(x1, x2);
          }
        }
        if (evaled) {
          return F.binaryAST2(head, x1, x2);
        }
        return F.NIL;
      }

      private IExpr power(IExpr x1, IExpr z) {
        // Power[x_ ^ y_, z_] :> x ^(y*z)
        IExpr base = x1.base();
        IExpr exponent = x1.exponent();
        IAST powerResult = Power(base, Times(exponent, z));
        if (assumptions) {
          IAST floorResult = Floor(Divide(Subtract(Pi, Im(Times(exponent, Log(base)))), F.C2Pi));
          IAST expResult = Power(E, Times(C2, I, Pi, z, floorResult));
          IAST timesResult = Times(powerResult, expResult);
          return timesResult;
        }
        return powerResult;
      }
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST tempAST = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (tempAST.isPresent()) {
        return tempAST;
      }
      if (arg1.isAST()) {
        boolean assumptions = false;
        if (ast.isAST2()) {
          final OptionArgs options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
          IExpr option = options.getOption(Assumptions);
          if (option.isTrue()) {
            // found "Assumptions -> True"
            assumptions = true;
          }
        }

        return powerExpand((IAST) arg1, assumptions);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    /** {@inheritDoc} */
    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.Automatic)));
    }
  }


  private static class ToRadicals extends AbstractFunctionEvaluator {



    private static class ToRadicalsVisitor extends VisitorExpr {
      IAST replacement;

      private ToRadicalsVisitor(IAST replacement) {
        this.replacement = replacement;
      }

      // D[a_+b_+c_,x_] -> D[a,x]+D[b,x]+D[c,x]
      // return listArg1.mapThread(F.D(F.Null, x), 1);
      @Override
      public IExpr visit(IASTMutable ast) {
        if (!ast.isAST(S.Root)) {
          return ast.mapThread(replacement, 1);
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() >= 2) {
        IExpr arg1 = ast.arg1();
        IExpr temp = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
        if (temp.isPresent()) {
          return temp;
        }
        if (arg1.isAST()) {
          ToRadicalsVisitor visitor = new ToRadicalsVisitor(ast);
          temp = arg1.accept(visitor);
          if (temp.isPresent()) {
            return temp;
          }
          temp = rootToRadicals((IAST) arg1, engine);
          if (temp.isPresent()) {
            return temp;
          }
        }
        return arg1;
      }
      return F.NIL;
    }


  }


  private static class Root extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      return rootToRadicals(ast, engine);
    }
  }


  /**
   *
   *
   * <h2>Together</h2>
   *
   * <pre>
   * <code>Together(expr)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * writes sums of fractions in <code>expr</code> together.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt;&gt; Together(a/b+x/y)
   * (a*y+b*x)*b^(-1)*y^(-1)
   *
   * &gt;&gt; Together(a / c + b / c)
   * (a+b)/c
   * </code>
   * </pre>
   *
   * <p>
   * <code>Together</code> operates on lists:
   *
   * <pre>
   * <code>&gt;&gt; Together({x / (y+1) + x / (y+1)^2})
   * {x (2 + y) / (1 + y) ^ 2}
   * </code>
   * </pre>
   *
   * <p>
   * But it does not touch other functions:
   *
   * <pre>
   * <code>&gt;&gt; Together(f(a / c + b / c))
   * f(a/c+b/c)
   * &gt;&gt; f(x)/x+f(x)/x^2//Together
   * f(x)/x^2+f(x)/x
   * </code>
   * </pre>
   */
  public static class Together extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST list = CompareUtil.threadListLogicEquationOperators(arg1, ast, 1);
      if (list.isPresent()) {
        return list;
      }
      if (arg1.isAST()) {
        return AlgebraUtil.togetherExpr(arg1, engine);
      }
      return arg1;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }



    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }


  /**
   *
   *
   * <h2>Variables</h2>
   *
   * <pre>
   * <code>Variables[expr]
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives a list of the variables that appear in the polynomial <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Variables(a x^2 + b x + c)
   * {a,b,c,x}
   *
   * &gt;&gt; Variables({a + b x, c y^2 + x/2})
   * {a,b,c,x,y}
   *
   * &gt;&gt; Variables(x + Sin(y))
   * {x,Sin(y)}
   * </code>
   * </pre>
   */
  private static class Variables extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return VariablesSet.getAlgebraicVariables(ast.arg1());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}

  }

  private static boolean checkPolyStruct(final IAST ast, EvalEngine engine) {
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (i == ast.argSize()) {
        if (arg.isRuleAST()) {
          // an option rule is used as last arg
          continue;
        }
      }
      if (!arg.isPolynomialStruct()) {
        // `1` is not a polynomial.
        Errors.printMessage(ast.topHead(), "poly", F.list(arg), engine);
        return false;
      }
    }
    return true;
  }

  /**
   * Print message <code>Value of option `1` should be a prime number or zero.</code>, if option is
   * not zero or prime.
   * 
   * @param option
   * @return <code>true</code> if the &quot;modp&quot; message was printed
   */
  private static boolean isModpMessage(IBuiltInSymbol symbol, IExpr option) {
    if (!option.isInteger()) {
      // Value of option `1` should be a prime number or zero.
      Errors.printMessage(S.PolynomialQuotientRemainder, "modp", F.List(F.Rule(S.Modulus, option)));
      return true;
    } else {
      IInteger optionInteger = (IInteger) option;
      if (!optionInteger.isZero() && !optionInteger.isProbablePrime()) {
        // Value of option `1` should be a prime number or zero.
        Errors.printMessage(symbol, "modp", F.List(F.Rule(S.Modulus, option)));
        return true;
      }
    }
    return false;
  }

  /**
   * Expand an expression <code>Log(numerator/denominator)</code>, where
   * <code>numerator/denominator</code> is of type {@link IRational}.
   * 
   * @param rationalNumber
   * @return {@link F#NIL} if an expansion of <code>Log(numerator/denominator)</code> isn't
   *         possible.
   */
  private static IExpr powerExpandLogRational(IRational rationalNumber) {
    IASTAppendable factors = rationalNumber.factorInteger();
    IAST pair = (IAST) factors.first();
    if (pair.second().isOne()) {
      if (factors.argSize() == 1) {
        // no expansion possible
        return F.NIL;
      }
    }

    int startPosition = 1;
    IASTAppendable logIntExpanded = F.PlusAlloc(factors.size());
    if (pair.first().isMinusOne() && pair.second().isOne()) {
      // negative number
      startPosition++;
      logIntExpanded.append(F.Times(F.CI, S.Pi));
    }

    for (int i = startPosition; i < factors.size(); i++) {
      pair = (IAST) factors.get(i);
      IExpr base = pair.arg1();
      IExpr exponent = pair.arg2();
      if (exponent.isOne()) {
        logIntExpanded.append(F.Log(base));
      } else {
        // for denominator part of a rational number the exponent will be negative
        logIntExpanded.append(F.Times(exponent, F.Log(base)));
      }
    }
    return logIntExpanded;
  }

  /**
   * Factor the <code>expr</code> in the domain of GaussianIntegers.
   *
   * @param expr the (polynomial) expression which should be factored
   * @param varList the list of variables
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param gaussianIntegers if <code>true</code> use Gaussian integers
   * @param engine the evaluation engine
   * @return factorization result AST (typically with head <code>F.Times</code> or <code>
   *     F.List</code>)
   * @throws JASConversionException
   */
  public static IExpr factorComplex(IExpr expr, List<IExpr> varList, ISymbol head,
      boolean gaussianIntegers, EvalEngine engine) {
    return factorComplex(expr, varList, head, false, gaussianIntegers, engine);
  }

  /**
   * Factor the <code>expr</code> in the domain of GaussianIntegers.
   *
   * @param expr the (polynomial) expression which should be factored
   * @param varList the list of variables
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param numeric2Rational transform numerical values to symbolic rational numbers
   * @param gaussianIntegers if <code>true</code> use Gaussian integers
   * @param engine
   * @return factorization result AST (typically with head <code>F.Times</code> or <code>
   *     F.List</code>)
   * @throws JASConversionException
   */
  private static IExpr factorComplex(IExpr expr, List<IExpr> varList, ISymbol head,
      boolean numeric2Rational, boolean gaussianIntegers, EvalEngine engine) {
    try {
      if (gaussianIntegers) {
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
        JASConvert<Complex<BigRational>> jas = new JASConvert<Complex<BigRational>>(varList, cfac);
        GenPolynomial<Complex<BigRational>> polyRat = jas.expr2JAS(expr, numeric2Rational);
        if (polyRat == null) {
          return expr;
        }
        return factorComplex(polyRat, jas, head, cfac, expr).eval(engine);
      } else {
        JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
        GenPolynomial<BigRational> polyRat = jas.expr2JAS(expr, numeric2Rational);
        if (polyRat == null) {
          return expr;
        }
        return factorRational(polyRat, jas, head);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.debug("Algebra.factorComplex() failed", rex);
    }
    return expr;
  }

  public static IExpr factor(IExpr arg1, EvalEngine engine) {
    VariablesSet eVar = new VariablesSet(arg1);
    return Factor.factor(F.Factor(arg1), arg1, eVar, false, false, engine);
  }

  /**
   * @param polynomial the complex-rational polynomial which should be factored
   * @param jas
   * @param head the head of the factorization result AST (typically <code>F.Times</code> or <code>
   *     F.List</code>)
   * @param cfac
   * @param original the original expression
   * @return
   */
  private static IExpr factorComplex(GenPolynomial<Complex<BigRational>> polynomial,
      JASConvert<? extends RingElem<?>> jas, ISymbol head, ComplexRing<BigRational> cfac,
      IExpr original) {
    if (polynomial.degree() > Config.MAX_POLYNOMIAL_DEGREE) {
      // Exponent ist out of bounds for function `1`.
      return Errors.printMessage(S.Factor, "lrgexp", F.List(S.Factor));
    }
    FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
    SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(polynomial);

    IASTAppendable result = F.ast(head, map.size());
    for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
      if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
        continue;
      }
      final IExpr key = jas.complexPoly2Expr(entry.getKey());
      if (entry.getValue().equals(1L) && map.size() <= 2
          && (key.equals(F.CNI) || key.equals(F.CI))) {
        // hack: factoring -I and I out of an expression should give no new factorized expression
        return original;
      }
      result.append(F.Power(jas.complexPoly2Expr(entry.getKey()), F.ZZ(entry.getValue())));
    }
    return result;
  }

  private static IAST factorModulus(IExpr expr, List<IExpr> varList, boolean factorSquareFree,
      IExpr option) throws JASConversionException {
    try {
      // found "Modulus" option => use ModIntegerRing
      ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IReal) option);
      JASModInteger jas = new JASModInteger(varList, modIntegerRing);
      GenPolynomial<ModLong> poly = jas.expr2JAS(expr);

      return factorModulus(jas, modIntegerRing, poly, factorSquareFree);
    } catch (ArithmeticException ae) {
      // toInt() conversion failed
      LOGGER.debug("Algebra.factorModulus() failed", ae);
    }
    return F.NIL;
  }

  /**
   * @param jas
   * @param modIntegerRing
   * @param poly
   * @param factorSquareFree
   * @return {@link F#NIL} if evaluation is impossible.
   */
  public static IAST factorModulus(JASModInteger jas, ModLongRing modIntegerRing,
      GenPolynomial<ModLong> poly, boolean factorSquareFree) {
    SortedMap<GenPolynomial<ModLong>, Long> map;
    try {
      FactorAbstract<ModLong> factorAbstract = FactorFactory.getImplementation(modIntegerRing);
      if (factorSquareFree) {
        map = factorAbstract.squarefreeFactors(poly);
      } else {
        map = factorAbstract.factors(poly);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // JAS may throw RuntimeExceptions
      return F.NIL;
    }
    IASTAppendable result = F.TimesAlloc(map.size());
    for (SortedMap.Entry<GenPolynomial<ModLong>, Long> entry : map.entrySet()) {
      final GenPolynomial<ModLong> singleFactor = entry.getKey();
      final Long val = entry.getValue();
      result.append(F.Power(jas.modLongPoly2Expr(singleFactor), F.ZZ(val)));
    }
    return result;
  }

  public static IAST factorRational(GenPolynomial<BigRational> polyRat, JASConvert<BigRational> jas,
      ISymbol head) {
    if (polyRat.degree() > Config.MAX_POLYNOMIAL_DEGREE) {
      // Exponent ist out of bounds for function `1`.
      return Errors.printMessage(S.Factor, "lrgexp", F.List(S.Factor));
    }
    Object[] objects = jas.factorTerms(polyRat);
    GenPolynomial<edu.jas.arith.BigInteger> poly =
        (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
    FactorAbstract<edu.jas.arith.BigInteger> factorAbstract =
        FactorFactory.getImplementation(edu.jas.arith.BigInteger.ONE);
    SortedMap<GenPolynomial<edu.jas.arith.BigInteger>, Long> map;
    map = factorAbstract.factors(poly);
    // if (map.size() == 1 && original != null) {
    // return F.unaryAST1(head, original);
    // }
    IASTAppendable result = F.ast(head, map.size() + 1);
    java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
    java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
    if (!gcd.equals(java.math.BigInteger.ONE) || !lcm.equals(java.math.BigInteger.ONE)) {
      result.append(F.fraction(gcd, lcm));
    }
    for (SortedMap.Entry<GenPolynomial<edu.jas.arith.BigInteger>, Long> entry : map.entrySet()) {
      final GenPolynomial<BigInteger> key = entry.getKey();
      final Long value = entry.getValue();
      if (key.isONE() && value.equals(1L)) {
        continue;
      }
      if (value == 1L) {
        result.append(jas.integerPoly2Expr(key));
      } else {
        result.append(F.Power(jas.integerPoly2Expr(key), F.ZZ(value)));
      }
    }
    return result;
  }

  /**
   * If possible returns an AST with head {@link S#Plus}, which contains the partial fraction
   * decomposition of the numerator and denominator parts.
   *
   * @param parts numerator and denominator parts
   * @param variable
   * @param engine
   * @return an AST with head {@link S#Plus}, which contains the partial fraction decomposition of
   *         the numerator and denominator parts. Otherwise return {@link F#NIL}
   */
  public static IExpr partsApart(IExpr[] parts, IExpr variable, EvalEngine engine) {
    IExpr temp =
        partialFractionDecompositionRational(new PartialFractionGenerator(), parts, variable);
    if (temp.isPresent()) {
      return temp;
    }
    temp = S.Factor.of(parts[1]);
    if (temp.isTimes()) {
      return partialFractionDecomposition(parts[0], temp, variable, 0, engine);
    }
    return F.NIL;
  }

  /**
   * Expand out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   * 
   * @param ast
   * @param assumptions
   * @return <code>F.NIL</code> if no evaluation was necessary
   */
  public static IExpr powerExpand(final IAST ast, boolean assumptions) {
    return ast.accept(new PowerExpand.PowerExpandVisitor(assumptions)).orElse(ast);
  }


  /**
   * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition
   * of the numerator and denominator parts.
   *
   * @param pf partial fraction generator
   * @param parts
   * @param variable a variable
   * @return {@link F#NIL} if the partial fraction decomposition wasn't constructed
   */
  public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf,
      IExpr[] parts, IExpr variable) {
    return partialFractionDecompositionRational(pf, parts, F.list(variable));
  }

  /**
   * Create a (recursive) partial fraction decomposition of the expression <code>
   * numerator / Times( ... )</code> for the given <code>variable</code>
   *
   * @param numerator the numerator of the fraction expression
   * @param denominatorTimes the <code>Times( ... )</code> expression of the denominator of the
   *        fraction expression
   * @param variable
   * @param count the recursion level
   * @param engine
   * @return the partial fraction decomposition is possible
   */
  public static IExpr partialFractionDecomposition(IExpr numerator, IExpr denominatorTimes,
      IExpr variable, int count, EvalEngine engine) {
    if (!denominatorTimes.isTimes()) {
      return S.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
    }

    // denominator is Times() here:
    IExpr first = denominatorTimes.first();
    IExpr rest = denominatorTimes.rest().oneIdentity1();
    if (first.isFree(variable)) {
      return S.Times.of(engine, F.Power(first, -1),
          partialFractionDecomposition(numerator, rest, variable, count + 1, engine));
    } else {
      IExpr v1 = S.Expand.of(engine, first);
      IExpr v2 = S.Expand.of(engine, rest);
      IExpr peGCD = S.PolynomialExtendedGCD.of(engine, v1, v2, variable);
      if (peGCD.isList() && peGCD.second().isList()) {
        IAST s = (IAST) peGCD.second();
        IExpr A = s.arg1();
        IExpr B = s.arg2();
        // IExpr u1 = F.PolynomialRemainder.of(engine, F.Expand(F.Times(B, numerator)), v1,
        // variable);
        // IExpr u2 = F.PolynomialRemainder.of(engine, F.Expand(F.Times(A, numerator)), v2,
        // variable);
        // return F.Plus.of(engine, F.Times(u1, F.Power(first, -1)),
        // partialFractionDecomposition(u2, rest, variable, count + 1, engine));

        IExpr u1 =
            S.PolynomialRemainder.ofNIL(engine, F.Expand(F.Times(B, numerator)), v1, variable);
        if (u1.isPresent()) {
          IExpr u2 =
              S.PolynomialRemainder.ofNIL(engine, F.Expand(F.Times(A, numerator)), v2, variable);
          if (u2.isPresent()) {
            return S.Plus.of(engine, F.Times(u1, F.Power(first, -1)),
                partialFractionDecomposition(u2, rest, variable, count + 1, engine));
          }
        }
        if (count == 0) {
          return F.NIL;
        }
        return S.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
      }
    }
    if (count == 0) {
      return F.NIL;
    }
    return S.Times.of(engine, numerator, F.Power(denominatorTimes, -1));
  }

  /**
   * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition
   * of the numerator and denominator parts.
   *
   * @param pf partial fraction generator
   * @param parts
   * @param variableList a list of variable
   * @return {@link F#NIL} if the partial fraction decomposition wasn't constructed
   */
  public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf,
      IExpr[] parts, IAST variableList) {
    try {
      IExpr exprNumerator = F.evalExpandAll(parts[0]);
      IExpr exprDenominator = F.evalExpandAll(parts[1]);
      // ASTRange r = new ASTRange(variableList, 1);
      // List<IExpr> varList = r;
      List<IExpr> varList = variableList.copyTo();

      JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
      GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
      if (numerator == null) {
        return F.NIL;
      }
      GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
      if (denominator == null) {
        return F.NIL;
      }
      // get factors
      FactorAbstract<BigRational> factorAbstract =
          FactorFactory.getImplementation(BigRational.ZERO);
      SortedMap<GenPolynomial<BigRational>, Long> sfactors =
          factorAbstract.baseFactors(denominator);

      List<GenPolynomial<BigRational>> D =
          new ArrayList<GenPolynomial<BigRational>>(sfactors.keySet());

      SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.getImplementation(BigRational.ZERO);
      List<List<GenPolynomial<BigRational>>> Ai = sqf.basePartialFraction(numerator, sfactors);
      // returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
      // A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

      if (Ai.size() > 0) {
        // IAST result = F.Plus();
        pf.allocPlus(Ai.size() * 2);
        pf.setJAS(jas);
        if (!Ai.get(0).get(0).isZERO()) {
          pf.addNonFractionalPart(Ai.get(0).get(0));
        }
        for (int i = 1; i < Ai.size(); i++) {
          final List<GenPolynomial<BigRational>> list = Ai.get(i);
          int j = 0;
          for (GenPolynomial<BigRational> genPolynomial : list) {
            if (!genPolynomial.isZERO()) {
              final GenPolynomial<BigRational> Di_1 = D.get(i - 1);
              pf.addSinglePartialFraction(genPolynomial, Di_1, j);
            }
            j++;
          }
        }
        return pf.getResult();
      }
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // JAS may throw JASConversionException and RuntimeExceptions
      LOGGER.debug("Algebra.partialFractionDecompositionRational() failed", e);
    }
    return F.NIL;
  }

  public static ASTSeriesData polynomialTaylorSeries(IExpr[] parts, IExpr x, IExpr x0, int n,
      int expDenominator) {
    try {
      IExpr exprNumerator = F.evalExpandAll(parts[0]);
      IExpr exprDenominator = F.evalExpandAll(parts[1]);

      final UnivPowerSeries<BigRational> ps = quotientPS(exprNumerator, exprDenominator, x);
      if (ps != null) {
        ASTSeriesData seriesData = new ASTSeriesData(x, x0, 0, n + expDenominator, expDenominator);
        // reversed order seems to be a bit faster
        for (int i = n; i >= 0; i--) {
          BigRational coefficient = ps.coefficient(i);
          seriesData.setCoeff(i, F.fraction(coefficient.numerator(), coefficient.denominator()));
        }
        return seriesData;
      }
    } catch (RuntimeException e) {
      Errors.rethrowsInterruptException(e);
      // JAS may throw JASConversionException and RuntimeExceptions
      LOGGER.debug("Algebra.polynomialTaylorSeries() failed", e);
    }
    return null;
  }

  public static UnivPowerSeries<BigRational> quotientPS(IExpr exprNumerator, IExpr exprDenominator,
      IExpr x) {
    JASConvert<BigRational> jas = new JASConvert<BigRational>(x, BigRational.ZERO);
    GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
    if (numerator == null) {
      return null;
    }
    final UnivPowerSeries<BigRational> ps;
    BigRational cfac = BigRational.ONE;
    UnivPowerSeriesRing<BigRational> fac = new UnivPowerSeriesRing<BigRational>(cfac);
    TaylorFunction<BigRational> FN = new PolynomialTaylorFunction<BigRational>(numerator);
    if (exprNumerator.isOne()) {
      GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
      if (denominator == null) {
        return null;
      }
      TaylorFunction<BigRational> FD = new PolynomialTaylorFunction<BigRational>(denominator);
      UnivPowerSeries<BigRational> psD = fac.seriesOfTaylor(FD, BigRational.ZERO);
      ps = psD.inverse();
    } else {
      if (exprDenominator.isOne()) {
        ps = fac.seriesOfTaylor(FN, BigRational.ZERO);
      } else {
        GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);
        if (denominator == null) {
          return null;
        }
        TaylorFunction<BigRational> FD = new PolynomialTaylorFunction<BigRational>(denominator);
        UnivPowerSeries<BigRational> psN = fac.seriesOfTaylor(FN, BigRational.ZERO);
        UnivPowerSeries<BigRational> psD = fac.seriesOfTaylor(FD, BigRational.ZERO);
        ps = psN.divide(psD);
      }
    }
    return ps;
  }

  /**
   * If {@link IAST} structures are available in the {@code variableList} create dummy variables and
   * replace these expressions in polyExpr.
   *
   * @param polyExpr
   * @param variablesList a list of variables, which aren't necessarily symbols
   * @param dummyStr
   * @param polynomialQTest test if the method {@link IExpr#isVariable(boolean)} returns
   *        <code>true</code>; if not return {@link F#NIL}
   * @return <code>F.List(polyExpr, substitutedVariableList)</code> or {@link F#NIL}, if the
   *         variables are invalid and <code>isVariableTest==true</code>
   */
  public static IAST substituteVariablesInPolynomial(IExpr polyExpr, IAST variablesList,
      String dummyStr, boolean polynomialQTest) {
    IASTAppendable substitutedVariableList = F.ListAlloc(variablesList.size());
    for (int i = 1; i < variablesList.size(); i++) {
      final IExpr variable = variablesList.get(i);
      if (!polynomialQTest || variable.isVariable(polynomialQTest)) {
        if (variable.isAST() && !variable.isPower()) {
          ISymbol dummy = F.Dummy(dummyStr + i);
          polyExpr = F.subst(polyExpr, F.Rule(variable, dummy));
          substitutedVariableList.append(dummy);
        } else {
          substitutedVariableList.append(variable);
        }
      } else {
        // `1` is not a valid variable.
        Errors.printMessage(S.General, "ivar", F.List(variable));
        return F.NIL;
      }

    }
    return F.list(polyExpr, substitutedVariableList);
  }

  /**
   * Root of a polynomial: <code>a + b*Slot1</code>.
   *
   * @param a coefficient a of the polynomial
   * @param b coefficient b of the polynomial
   * @param nthRoot <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
   * @return
   */
  private static IAST root1(IExpr a, IExpr b, int nthRoot) {
    if (nthRoot != 1) {
      return F.NIL;
    }
    return Times(F.CN1, a, Power(b, -1));
  }

  /**
   * Root of a polynomial: <code>a + b*Slot1 + c*Slot1^2</code>.
   *
   * @param a coefficient a of the polynomial
   * @param b coefficient b of the polynomial
   * @param c coefficient c of the polynomial
   * @param nthRoot <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
   * @return
   */
  private static IAST root2(IExpr a, IExpr b, IExpr c, int nthRoot) {
    if (nthRoot < 1 || nthRoot > 3) {
      return F.NIL;
    }
    IExpr k = F.ZZ(nthRoot);
    return Plus(
        Times(C1D2, Power(F.CN1, k),
            F.Sqrt(Times(Plus(F.Sqr(b), Times(F.CN4, a, c)), Power(c, -2)))),
        Times(F.CN1D2, b, Power(c, -1)));
  }

  /**
   * Root of a polynomial: <code>a + b*Slot1 + c*Slot1^2 + d*Slot1^3</code>.
   *
   * @param a coefficient a of the polynomial
   * @param b coefficient b of the polynomial
   * @param c coefficient c of the polynomial
   * @param d coefficient d of the polynomial
   * @param nthRoot <code>1 <= nthRoot <= 3</code> otherwise return F.NIL;
   * @return
   */
  private static IAST root3(IExpr a, IExpr b, IExpr c, IExpr d, int nthRoot) {
    if (nthRoot < 1 || nthRoot > 3) {
      return F.NIL;
    }
    // System.out.println(F.List(a, b, c, d));
    IExpr k = F.ZZ(nthRoot);

    // r = 3*b*d - c^2
    IExpr r = Plus(Negate(F.Sqr(c)), Times(F.C3, b, d));
    // q = 9*b*c*d - 2*c^3 - 27*a*d^2
    IExpr q = Plus(Times(F.CN2, Power(c, 3)), Times(F.C9, b, c, d), Times(F.ZZ(-27), a, F.Sqr(d)));
    // p = (q + Sqrt(q^2 + 4 r^3))^(1/3)
    IExpr p = Power(Plus(q, F.Sqrt(Plus(F.Sqr(q), Times(F.C4, Power(r, 3))))), F.C1D3);
    // -(c/(3*d)) + (E^((2*I*Pi*(k - 1))/3)*p)/(3*2^(1/3)*d) -
    // (2^(1/3)*r)/(E^((2*I*Pi*(k - 1))/3)*(3*p*d))
    return Plus(Times(F.CN1D3, c, Power(d, -1)),
        Times(F.CN1D3, Power(S.E, Times(F.CC(0L, 1L, -2L, 3L), Plus(F.CN1, k), S.Pi)), Power(p, -1),
            r, Power(C2, F.C1D3), Power(d, -1)),
        Times(F.C1D3, Power(C2, F.CN1D3),
            Power(S.E, Times(F.CC(0L, 1L, 2L, 3L), Plus(F.CN1, k), S.Pi)), Power(d, -1), p));
  }

  /**
   * Root of a polynomial <code>a + b*Slot1 + c*Slot1^2 + d*Slot1^3 + e*Slot1^4</code>
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param nthRoot <code>1 <= nthRoot <= 4</code> otherwise return F.NIL;
   * @return
   */
  private static IAST root4(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e, int nthRoot) {
    if (nthRoot < 1 || nthRoot > 4) {
      return F.NIL;
    }
    IExpr k = F.ZZ(nthRoot);

    // t = Sqrt(-4*(c^2 - 3*b*d + 12*a*e)^3 + (2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2
    // + b^2*e))^2)
    IExpr t = F.Sqrt(
        Plus(Times(F.CN4, Power(Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12), a, e)), 3)),
            F.Sqr(Plus(Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
                Times(F.ZZ(27), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e))),
                Times(C2, Power(c, 3))))));
    // s = (t + 2*c^3 - 9*c*(b*d + 8*a*e) + 27*(a*d^2 + b^2*e))^(1/3)
    IExpr s =
        Power(Plus(Times(C2, Power(c, 3)), t, Times(F.CN9, c, Plus(Times(b, d), Times(F.C8, a, e))),
            Times(F.ZZ(27), Plus(Times(a, F.Sqr(d)), Times(F.Sqr(b), e)))), F.C1D3);

    // eps1 = (1/2)*Sqrt((2^(1/3)*(c^2 - 3*b*d + 12*a*e))/ (3*s*e) + (3*d^2 +
    // 2*2^(2/3)*s*e - 8*c*e)/ (12 e^2))
    IExpr eps1 = Times(C1D2,
        F.Sqrt(Plus(
            Times(F.QQ(1L, 12L),
                Plus(Times(F.C3, F.Sqr(d)), Times(F.CN8, c, e),
                    Times(C2, e, s, Power(C2, F.QQ(2L, 3L)))),
                Power(e, -2)),
            Times(F.C1D3, Plus(F.Sqr(c), Times(F.CN3, b, d), Times(F.ZZ(12), a, e)),
                Power(C2, F.C1D3), Power(e, -1), Power(s, -1)))));

    // u = -((2^(1/3)*s^2 + 2*c^2 - 6*b*d + 24*a*e)/ (2^(2/3)*s*e)) + 8*eps1^2
    IExpr u = Plus(Times(F.C8, F.Sqr(eps1)),
        Times(F.CN1,
            Plus(Times(C2, F.Sqr(c)), Times(F.CN6, b, d), Times(F.ZZ(24), a, e),
                Times(Power(C2, F.C1D3), F.Sqr(s))),
            Power(C2, F.QQ(-2L, 3L)), Power(e, -1), Power(s, -1)));

    // v = (d^3 - 4*c*d*e + 8*b*e^2)/ (8*e^3*eps1)
    IExpr v =
        Times(F.QQ(1L, 8L), Plus(Power(d, 3), Times(F.CN4, c, d, e), Times(F.C8, b, F.Sqr(e))),
            Power(e, -3), Power(eps1, -1));

    // eps2 = (1/2)*Sqrt(u + v)
    IExpr eps2 = Times(C1D2, F.Sqrt(Plus(u, v)));

    // eps3 = (1/2)*Sqrt(u - v)

    IExpr eps3 = Times(C1D2, F.Sqrt(Plus(u, Negate(v))));

    // -(d/(4*e)) + (2*Floor((k - 1)/2) - 1)*eps1 + (-1)^k*(1 - UnitStep(k -
    // 3))*eps2 - (-1)^k*(UnitStep(2 - k)
    // - 1)*eps3
    return Plus(Times(eps1, Plus(F.CN1, Times(C2, Floor(Times(C1D2, Plus(F.CN1, k)))))),
        Times(eps2, Plus(F.C1, Negate(F.UnitStep(Plus(F.CN3, k)))), Power(F.CN1, k)),
        Times(eps3, Plus(F.CN1, F.UnitStep(Plus(C2, Negate(k)))), Power(F.CN1, Plus(F.C1, k))),
        Times(F.CN1D4, d, Power(e, -1)));
  }

  private static IExpr rootToRadicals(final IAST ast, EvalEngine engine) {
    if (ast.size() == 3 && ast.arg2().isInteger()) {
      IExpr expr = ast.arg1();
      if (expr.isFunction()) {
        expr = expr.first();
        try {
          int k = ast.arg2().toIntDefault();
          if (k < 0) {
            return F.NIL;
          }
          final IAST variables = F.list(F.Slot1);
          ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, variables);
          ExprPolynomial polynomial = ring.create(expr, false, true, false);

          final long varDegree = polynomial.degree(0);
          if (polynomial.isConstant()) {
            return F.CEmptyList;
          }
          IExpr a;
          IExpr b;
          IExpr c;
          IExpr d;
          IExpr e;
          if (varDegree >= 1 && varDegree <= 4) {
            a = C0;
            b = C0;
            c = C0;
            d = C0;
            e = C0;
            for (ExprMonomial monomial : polynomial) {
              final IExpr coeff = monomial.coefficient();
              long lExp = monomial.exponent().getVal(0);
              if (lExp == 4) {
                e = coeff;
              } else if (lExp == 3) {
                d = coeff;
              } else if (lExp == 2) {
                c = coeff;
              } else if (lExp == 1) {
                b = coeff;
              } else if (lExp == 0) {
                a = coeff;
              } else {
                throw new ArithmeticException("Root::Unexpected exponent value: " + lExp);
              }
            }
            IAST result = F.NIL;
            if (varDegree == 1) {
              result = root1(a, b, k);
            } else if (varDegree == 2) {
              result = root2(a, b, c, k);
            } else if (varDegree == 3) {
              result = root3(a, b, c, d, k);
            } else {
              result = root4(a, b, c, d, e, k);
            }
            if (result.isPresent()) {
              return engine.evaluate(result);
            }
          }
        } catch (JASConversionException e2) {
          LOGGER.debug("ToRadicals.rootToRadicals() failed", e2);
        }
      }
    }
    return F.NIL;
  }

  public static void initialize() {
    Initializer.init();
  }

  private Algebra() {}
}
