package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.ExpandAll;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.k;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.SumRules;

/**
 *
 *
 * <pre>
 * Sum(expr, {i, imin, imax})
 * </pre>
 *
 * <blockquote>
 *
 * <p>evaluates the discrete sum of <code>expr</code> with <code>i</code> ranging from <code>imin
 * </code> to <code>imax</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Sum(expr, {i, imin, imax, di})
 * </pre>
 *
 * <blockquote>
 *
 * <p><code>i</code> ranges from <code>imin</code> to <code>imax</code> in steps of <code>di</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Sum(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
 * </pre>
 *
 * <blockquote>
 *
 * <blockquote>
 *
 * <p>evaluates <code>expr</code> as a multiple sum, with <code>{i, ...}, {j, ...}, ...</code> being
 * in outermost-to-innermost order.
 *
 * </blockquote>
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Sum(k, {k, 1, 10})
 * 55
 * </pre>
 *
 * <p>Double sum:<br>
 *
 * <pre>
 * &gt;&gt; Sum(i * j, {i, 1, 10}, {j, 1, 10})
 * 3025
 * </pre>
 *
 * <p>Symbolic sums are evaluated:
 *
 * <pre>
 * &gt;&gt; Sum(k, {k, 1, n})
 * 1/2*n*(1+n)
 *
 * &gt;&gt; Sum(k, {k, n, 2*n})
 * 3/2*n*(1+n)
 *
 * &gt;&gt; Sum(k, {k, I, I + 1})
 * 1+I*2
 *
 * &gt;&gt; Sum(1 / k ^ 2, {k, 1, n})
 * HarmonicNumber(n, 2)
 * </pre>
 *
 * <p>Verify algebraic identities:<br>
 *
 * <pre>
 * &gt;&gt; Simplify(Sum(x ^ 2, {x, 1, y}) - y * (y + 1) * (2 * y + 1) / 6)
 * 0
 * </pre>
 *
 * <p>Infinite sums:<br>
 *
 * <pre>
 * &gt;&gt; Sum(1 / 2 ^ i, {i, 1, Infinity})
 * 1
 *
 * &gt;&gt; Sum(1 / k ^ 2, {k, 1, Infinity})
 * Pi^2/6
 *
 * &gt;&gt; Sum(x^k*Sum(y^l,{l,0,4}),{k,0,4}))
 * 1+y+y^2+y^3+y^4+x*(1+y+y^2+y^3+y^4)+(1+y+y^2+y^3+y^4)*x^2+(1+y+y^2+y^3+y^4)*x^3+(1+y+y^2+y^3+y^4)*x^4
 *
 * &gt;&gt; Sum(2^(-i), {i, 1, Infinity})
 * 1
 *
 * &gt;&gt; Sum(i / Log(i), {i, 1, Infinity})
 * Sum(i/Log(i),{i,1,Infinity})
 *
 * &gt;&gt; Sum(Cos(Pi i), {i, 1, Infinity})
 * Sum(Cos(i*Pi),{i,1,Infinity})
 * </pre>
 */
public class Sum extends ListFunctions.Table implements SumRules {
  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public IAST getRuleAST() {
    IASTAppendable rules = F.ListAlloc(RULES1.size() + RULES2.size());
    rules.appendArgs(RULES1);
    rules.appendArgs(RULES2);
    return rules;
  }

  public Sum() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      arg1 = F.expand(arg1, false, false, false);
    }
    if (arg1.isPlus()) {
      // IAST sum = ast.setAtCopy(1, null);
      return ((IAST) arg1).mapThread(ast, 1);
    }
    if (ast.size() > 2) {
      try {
        IAST list;
        if (ast.last().isList()) {
          list = (IAST) ast.last();
        } else {
          list = F.List(ast.last());
        }
        if (list.isAST1()) {
          // indefinite sum case
          IExpr variable = list.arg1();
          if (ast.arg1().isFree(variable) && variable.isVariable()) {
            return indefiniteSum(ast, variable);
          }
        }

        VariablesSet variablesSet = determineIteratorExprVariables(ast);
        IAST varList = variablesSet.getVarList();
        IIterator<IExpr> iterator = null;
        IExpr argN = ast.last();
        // try {
        IExpr temp = evaluateTableThrow(ast, Plus(), Plus(), engine);
        if (temp.isPresent()) {
          return temp;
        }

        if (argN.isList()) {

          argN = evalBlockWithoutReap(argN, varList);
          if (argN.isList()) {
            iterator = Iterator.create((IAST) argN, ast.argSize(), engine);
          } else {
            if (argN.isReal()) {
              iterator = Iterator.create(F.List(argN), ast.argSize(), engine);
            } else {
              // Non-list iterator `1` at position `2` does not evaluate to a real numeric value.
              return IOFunctions.printMessage(
                  ast.topHead(), "nliter", F.List(argN, F.ZZ(ast.size() - 1)), engine);
            }
          }

          // if (iterator.isSetIterator() || iterator.isNumericFunction()) {
          // IAST resultList = Plus();
          // temp = evaluateLast(ast.arg1(), iterator, resultList, C0);
          // if (temp.isPresent() && !temp.equals(resultList)) {
          // if (ast.isAST2()) {
          // return temp;
          // } else {
          // IAST result = ast.clone();
          // result.remove(ast.argSize());
          // result.set(1, temp);
          // return result;
          // }
          // }
          // }

        }
        // } catch (final ValidateException ve) {
        // LOGGER.debug("Sum.evaluate() failed", ve);
        // // see level specification
        // return engine.printMessage(ve.getMessage(ast.topHead()));
        // }

        // arg1 = evalBlockExpandWithoutReap(ast.arg1(), varList);
        if (arg1.isTimes()) {
          if (variablesSet.size() > 0) {
            temp = collectConstantFactors(ast, (IAST) arg1, variablesSet);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }

        if (iterator != null) {
          if (arg1.isZero()) {
            // Sum(0, {k, n, m})
            return F.C0;
          }
          if (iterator.isValidVariable() && iterator.getUpperLimit().isInfinity()) {
            if (arg1.isPositiveResult() && arg1.isIntegerResult()) {
              // Sum(n, {k, a, Infinity}) ;n is positive integer
              return F.CInfinity;
            }
            if (arg1.isNegativeResult() && arg1.isIntegerResult()) {
              // Sum(n, {k, a, Infinity}) ;n is negative integer
              return F.CNInfinity;
            }
          }

          if (iterator.isValidVariable() && iterator.isNumericFunction()) {
            IAST resultList = Plus();
            temp = evaluateLast(ast.arg1(), iterator, resultList, F.C0);
            if (!temp.isPresent() || temp.equals(resultList)) {
              return F.NIL;
            }
            if (ast.isAST2()) {
              return temp;
            } else {
              IASTAppendable result = ast.removeAtClone(ast.argSize());
              result.set(1, temp);
              return result;
            }
          }

          if (iterator.isValidVariable() && !iterator.isNumericFunction()) {
            if (iterator.getStep().isOne()) {
              if (iterator.getUpperLimit().isDirectedInfinity()) {
                temp = definiteSumInfinity(arg1, iterator, (IAST) argN, engine);
              } else {
                temp = definiteSum(arg1, iterator, (IAST) argN, engine);
              }
              if (temp.isPresent()) {
                if (ast.isAST2()) {
                  return temp;
                }
                IASTAppendable result = ast.removeAtClone(ast.argSize());
                result.set(1, temp);
                return result;
              }
            }
          }

        } else if (argN.isSymbol()) {
          temp = indefiniteSum(arg1, (ISymbol) argN);
          if (temp.isPresent()) {
            if (ast.isAST2()) {
              return temp;
            } else {
              IASTAppendable result = ast.removeAtClone(ast.argSize());
              result.set(1, temp);
              return result;
            }
          }
        }
      } catch (ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ve);
      }
    }
    return F.NIL;
  }

  /**
   * Create a new Sum() by removing last iterator or return result of indefinite sum case for Sum(a,
   * x)
   *
   * @param ast
   * @param variable the iterator variable
   * @return
   */
  private static IExpr indefiniteSum(final IAST ast, IExpr variable) {
    IExpr result = F.Times(ast.arg1(), variable);
    int argSize = ast.argSize();
    if (argSize == 2) {
      return result;
    }
    IASTAppendable newSum = ast.removeAtClone(argSize);
    newSum.set(1, result);
    return newSum;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  private IExpr collectConstantFactors(final IAST ast, IAST prod, VariablesSet variablesSet) {
    IASTAppendable filterAST = F.TimesAlloc(16);
    IASTAppendable restAST = F.TimesAlloc(16);
    prod.filter(filterAST, restAST, VariablesSet.isFree(variablesSet));
    if (filterAST.size() > 1) {
      IASTMutable reducedSum = ast.setAtCopy(1, restAST.oneIdentity1());
      return F.Times(filterAST.oneIdentity0(), reducedSum);
    }
    return F.NIL;
  }

  /**
   * Evaluate the definite sum: <code>Sum[arg1, {var, from, to}]</code>
   *
   * @param expr the first argument of the <code>Sum[]</code> function.
   * @param iterator the current iterator definition for which the Sum should be evaluated
   * @param list constructed as <code>{Symbol: var, Integer: from, Symbol: to}</code>
   * @param engine the evaluation engine
   * @return
   */
  private IExpr definiteSum(
      final IExpr expr, final IIterator<IExpr> iterator, IAST list, EvalEngine engine) {
    final ISymbol var = iterator.getVariable();
    final IExpr from = iterator.getLowerLimit();
    final IExpr to = iterator.getUpperLimit();

    if (expr.isFree(var, true)) {
      if (from.isOne()) {
        return F.Times(to, expr);
      }
      if (from.isZero()) {
        return F.Times(Plus(to, C1), expr);
      }
      if (!S.Greater.ofQ(engine, C1, from) && !S.Greater.ofQ(engine, from, to)) {
        return F.Times(Plus(C1, F.Negate(from), to), expr);
      }
    } else {
      if (expr.isTimes()) {
        // Sum[ Times[a,b,c,...], {var, from, to} ]
        IASTAppendable filterCollector = F.TimesAlloc(16);
        IASTAppendable restCollector = F.TimesAlloc(16);
        ((IAST) expr)
            .filter(
                filterCollector,
                restCollector,
                new Predicate<IExpr>() {
                  @Override
                  public boolean test(IExpr input) {
                    return input.isFree(var, true);
                  }
                });
        if (filterCollector.size() > 1) {
          IExpr temp = engine.evalQuiet(F.Sum(restCollector.oneIdentity1(), list));
          if (temp.isFreeAST(S.Sum)) {
            filterCollector.append(temp);
            return filterCollector;
          }
        }
      }

      if (expr.equals(var)) {
        if ((from.isVariable() && !from.equals(var)) || (to.isVariable() && !to.equals(var))) {
          // Sum(i, {i, from, to})
          return Times(C1D2, Plus(Subtract(to, from), C1), Plus(from, to));
        }
      }

      if (!engine.evalTrue(F.Greater(C0, from)) && !engine.evalTrue(F.Greater(from, to))) {
        IExpr temp = F.NIL;
        if (expr.isPower()) {
          temp = sumPower((IAST) expr, var, from, to);
        } else if (expr.equals(var)) {
          temp = sumPowerFormula(from, to, F.C1);
        }
        if (temp.isPresent()) {
          return temp;
        }
      }

      if (expr.isPower()
          && !engine.evalTrue(F.Greater(C1, from))
          && !engine.evalTrue(F.Greater(from, to))) {
        IAST powAST = (IAST) expr;
        if (powAST.equalsAt(1, var) && powAST.arg2().isFree(var) && to.isFree(var)) {
          if (from.isOne()) {
            // i^(k),{i,1,n}) ==> HarmonicNumber(n,-k)
            return F.HarmonicNumber(to, powAST.arg2().negate());
          }
          // i^k,{i,n,m} ==> HurwitzZeta(-k, n)-HurwitzZeta(-k,1+m)
          return F.Subtract(
              F.HurwitzZeta(F.Negate(powAST.arg2()), from),
              F.HurwitzZeta(F.Negate(powAST.arg2()), F.Plus(1, to)));
        }
      }

      try {
        IAST resultList = Plus();
        IExpr temp = evaluateLast(expr, iterator, resultList, F.NIL);
        if (temp.isPresent() && !temp.equals(resultList)) {
          return temp;
        }
      } catch (RecursionLimitExceeded rle) {
        LOGGER.log(engine.getLogLevel(), "Recursionlimit exceeded");
        return F.NIL;
      }
      // try {
      // iterator.setUp();
      // if (iterator.hasNext()) {
      // java.util.List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
      // iterList.add(iterator);
      // final TableGenerator generator = new TableGenerator(iterList, F.Plus(),
      // new UnaryArrayFunction(engine, arg1), F.NIL);
      // IExpr tableResult = generator.table();
      // if (tableResult.isPresent()) {
      // return tableResult;
      // }
      // }
      // } catch (RecursionLimitExceeded rle) {
      // engine.printMessage("Sum: Recursionlimit exceeded");
      // return F.NIL;
      // } catch (RuntimeException rex) {
      // LOGGER.debug("Sum.definiteSum() failed", rex);
      // }finally {
      // iterator.tearDown();
      // }
    }
    if (from.isPositive()) {
      IExpr temp1 = engine.evalQuiet(F.Sum(expr, F.List(var, C0, from.minus(F.C1))));
      if (!temp1.isComplexInfinity() && temp1.isFreeAST(S.Sum)) {
        IExpr temp2 = engine.evalQuietNull(F.Sum(expr, F.List(var, C0, to)));
        if (temp2.isPresent() && !temp2.isComplexInfinity()) {
          return F.Subtract(temp2, temp1);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Evaluate the definite sum: <code>Sum[arg1, {var, from, Infinity}]</code>
   *
   * @param expr the first argument of the <code>Sum[]</code> function.
   * @param iterator the current iterator definition for which the Sum should be evaluated
   * @param list constructed as <code>{Symbol: var, Integer: from, Symbol: to}</code>
   * @param engine the evaluation engine
   * @return
   */
  private IExpr definiteSumInfinity(
      final IExpr expr, final IIterator<IExpr> iterator, IAST list, EvalEngine engine) {
    final ISymbol var = iterator.getVariable();
    final IExpr from = iterator.getLowerLimit();
    final IExpr to = iterator.getUpperLimit();

    if (expr.isZero()) {
      return F.C0;
    }
    if (from.isInteger() && !from.isOne()) {
      IExpr subSum = engine.evaluateNIL(F.Sum(expr, F.List(var, C1, to)));
      if (subSum.isPresent()) {
        if (S.Less.ofQ(engine, from, C1)) {
          return F.Plus(F.Sum(expr, F.List(var, from, C0)), subSum);
        }
        if (S.Greater.ofQ(engine, from, C1)) {
          return F.Subtract(subSum, F.Sum(expr, F.List(var, C1, from.minus(F.C1))));
        }
      }
    }
    return F.NIL;
  }

  /**
   * Evaluate the indefinite sum: <code>Sum[arg1, var]</code>
   *
   * @param arg1
   * @param var
   * @return
   */
  private IExpr indefiniteSum(IExpr arg1, final ISymbol var) {
    if (arg1.isTimes()) {
      // Sum[ Times[a,b,c,...], var ]
      IASTAppendable filterCollector = F.TimesAlloc(16);
      IASTAppendable restCollector = F.TimesAlloc(16);
      ((IAST) arg1)
          .filter(
              filterCollector,
              restCollector,
              new Predicate<IExpr>() {
                @Override
                public boolean test(IExpr input) {
                  return input.isFree(var, true);
                }
              });
      if (filterCollector.size() > 1) {
        if (restCollector.isAST1()) {
          filterCollector.append(F.Sum(restCollector.arg1(), var));
        } else {
          filterCollector.append(F.Sum(restCollector, var));
        }
        return filterCollector;
      }
    } else if (arg1.isPower()) {
      return sumPower((IAST) arg1, var, F.C1, var);
    } else if (arg1.equals(var)) {
      return sumPowerFormula(F.C1, var, F.C1);
    }
    return F.NIL;
  }

  /**
   * See <a href=
   * "http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions"> Wikipedia -
   * Summation#Some_summations_of_polynomial_expressions</a>.
   *
   * @param powAST an AST of the form <code>Power[var, i_Integer]</code>
   * @param var
   * @param from TODO
   * @param to
   * @return
   */
  public IExpr sumPower(final IAST powAST, final ISymbol var, IExpr from, final IExpr to) {
    if (powAST.equalsAt(1, var) && powAST.arg2().isInteger()) {
      IInteger p = (IInteger) powAST.arg2();
      if (p.isPositive()) {
        return sumPowerFormula(from, to, p);
      }
    }
    return F.NIL;
  }

  /**
   * See <a href=
   * "http://en.wikipedia.org/wiki/Summation#Some_summations_of_polynomial_expressions"> Wikipedia -
   * Summation#Some_summations_of_polynomial_expressions</a>.
   *
   * @param from TODO
   * @param to
   * @param p
   * @return
   */
  public IExpr sumPowerFormula(IExpr from, final IExpr to, IInteger p) {
    // TODO optimize if BernoulliB==0 for odd k != 1
    // Sum[var ^ p, var] :=
    // (var+1)^(p+1)/(p+1) +
    // Sum[(var+1)^(p-k+1)*Binomial[p,k]*BernoulliB[k]*(p-k+1)^(-1),
    // {k,1,p}]
    IExpr term1 = F.NIL;
    if (!from.isOne()) {
      IExpr fromMinusOne = F.Plus(F.CN1, from);
      if (p.isOne()) {
        term1 = Times(C1D2, fromMinusOne, Plus(C1, fromMinusOne));
      } else {
        term1 =
            F.eval(
                ExpandAll(
                    Plus(
                        Times(Power(Plus(fromMinusOne, C1), Plus(p, C1)), Power(Plus(p, C1), CN1)),
                        Sum(
                            Times(
                                Times(
                                    Times(
                                        Power(
                                            Plus(fromMinusOne, C1),
                                            Plus(Plus(p, Times(CN1, k)), C1)),
                                        Binomial(p, k)),
                                    BernoulliB(k)),
                                Power(Plus(Plus(p, Times(CN1, k)), C1), CN1)),
                            List(k, C1, p)))));
      }
    }
    IExpr term2;
    if (p.isOne()) {
      term2 = Times(C1D2, to, Plus(C1, to));
    } else {
      term2 =
          F.eval(
              ExpandAll(
                  Plus(
                      Times(Power(Plus(to, C1), Plus(p, C1)), Power(Plus(p, C1), CN1)),
                      Sum(
                          Times(
                              Times(
                                  Times(
                                      Power(Plus(to, C1), Plus(Plus(p, Times(CN1, k)), C1)),
                                      Binomial(p, k)),
                                  BernoulliB(k)),
                              Power(Plus(Plus(p, Times(CN1, k)), C1), CN1)),
                          List(k, C1, p)))));
    }
    return term1.isPresent() ? Subtract(term2, term1) : term2;
  }

  /** Evaluate built-in rules and define Attributes for a function. */
  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.DELAYED_RULE_EVALUATION);
    if (getRuleAST() != null) {
      // don't call EvalEngine#addRules() here!
      // the rules should add themselves
      // EvalEngine.get().addRules(ruleList);
    }
  }
}
