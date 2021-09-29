package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.ListFunctions;
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
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ProductRules;

/**
 *
 *
 * <pre>
 * Product(expr, {i, imin, imax})
 * </pre>
 *
 * <blockquote>
 *
 * <p>evaluates the discrete product of <code>expr</code> with <code>i</code> ranging from <code>
 * imin</code> to <code>imax</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Product(expr, {i, imin, imax, di})
 * </pre>
 *
 * <blockquote>
 *
 * <p><code>i</code> ranges from <code>imin</code> to <code>imax</code> in steps of <code>di</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Product(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
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
 * &gt;&gt; Product(k, {k, 1, 10})
 * 3628800
 *
 * &gt;&gt; 10!
 * 3628800
 *
 * &gt;&gt; Product(x^k, {k, 2, 20, 2})
 * x^110
 *
 * &gt;&gt; Product(2 ^ i, {i, 1, n})
 * 2^(1/2*n*(1+n))
 * </pre>
 *
 * <p>Symbolic products involving the factorial are evaluated:
 *
 * <pre>
 * &gt;&gt; Product(k, {k, 3, n})
 * n! / 2
 * </pre>
 *
 * <p>Evaluate the <code>n</code>th primorial:
 *
 * <pre>
 * &gt;&gt; primorial(0) = 1;
 * &gt;&gt; primorial(n_Integer) := Product(Prime(k), {k, 1, n});
 * &gt;&gt; primorial(12)
 * 7420738134810
 * </pre>
 */
public class Product extends ListFunctions.Table implements ProductRules {
  private static final Logger LOGGER = LogManager.getLogger();

  public Product() {}

  @Override
  public IAST getRuleAST() {
    return RULES;
  }

  /**
   * Product of expressions.
   *
   * <p>See <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation">Wikipedia
   * Multiplication</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      arg1 = F.expand(arg1, false, false, false);
      if (!arg1.isPresent()) {
        arg1 = ast.arg1();
      }
    }
    if (arg1.isTimes()) {
      // IASTMutable prod = ast.setAtCopy(1, null);
      return ((IAST) arg1).mapThread(ast, 1);
    }
    if (ast.size() > 2) {
      IAST list;
      if (ast.last().isList()) {
        list = (IAST) ast.last();
      } else {
        list = F.List(ast.last());
      }
      if (list.isAST1()) {
        // indefinite product case
        IExpr variable = list.arg1();
        if (ast.arg1().isFree(variable) && variable.isVariable()) {
          return indefiniteProduct(ast, variable);
        }
      }
    }
    IExpr temp = F.NIL;
    try {
      temp = evaluateTableThrow(ast, Times(), Times(), engine);
      if (temp.isPresent()) {
        return temp;
      }
    } catch (final ValidateException ve) {
      LOGGER.log(engine.getLogLevel(), ve.getMessage(S.Product), ve);
      return F.NIL;
    }
    // arg1 = evalBlockExpandWithoutReap(arg1,
    // determineIteratorVariables(ast));

    if (arg1.isPower()) {
      IExpr exponent = arg1.exponent();
      boolean flag = true;
      // Prod( i^a, {i,from,to},... )
      for (int i = 2; i < ast.size(); i++) {
        IIterator<IExpr> iterator;
        if (ast.get(i).isList()) {
          iterator = Iterator.create((IAST) ast.get(i), i, engine);
        } else {
          iterator = Iterator.create(F.List(ast.get(i)), i, engine);
        }
        if (iterator.isValidVariable() && exponent.isFree(iterator.getVariable())) {
          continue;
        }
        flag = false;
        break;
      }
      if (flag) {
        IASTMutable prod = ast.copy();
        prod.set(1, arg1.base());
        return F.Power(prod, exponent);
      }
    }
    IExpr argN = ast.last();
    if (ast.size() >= 3 && argN.isList()) {
      try {
        if (arg1.isZero()) {
          // Product(0, {k, n, m})
          return F.C0;
        }
        IIterator<IExpr> iterator = Iterator.create((IAST) argN, ast.argSize(), engine);
        if (iterator.isValidVariable() && iterator.getUpperLimit().isInfinity()) {
          if (arg1.isOne()) {
            // Product(1, {k, a, Infinity})
            return F.C1;
          }
          if (arg1.isPositiveResult() && arg1.isIntegerResult()) {
            // Product(n, {k, a, Infinity}) ;n is positive integer
            return F.CInfinity;
          }
        }
        if (iterator.isValidVariable() && !iterator.isNumericFunction()) {
          // if (iterator.getLowerLimit().isInteger() && iterator.getUpperLimit().isSymbol()
          // && iterator.getStep().isOne()) {
          if (iterator.getUpperLimit().isSymbol() && iterator.getStep().isOne()) {
            final ISymbol var = iterator.getVariable();
            final IExpr from = iterator.getLowerLimit();
            final ISymbol to = (ISymbol) iterator.getUpperLimit();
            if (arg1.isPower()) {
              IExpr base = arg1.base();
              if (base.isFree(var)) {
                if (iterator.getLowerLimit().isOne()) {
                  IExpr exponent = arg1.exponent();
                  if (exponent.equals(var)) {
                    // Prod( a^i, ..., {i,from,to} )
                    if (ast.isAST2()) {
                      return F.Power(base, Times(C1D2, to, Plus(C1, to)));
                    }
                    IASTAppendable result = ast.removeAtClone(ast.argSize());
                    // result.remove(ast.argSize());
                    result.set(1, F.Power(base, Times(C1D2, to, Plus(C1, to))));
                    return result;
                  }
                }
              }
            }

            if (arg1.isFree(var)) {

              if (ast.isAST2()) {
                if (from.isOne()) {
                  return F.Power(ast.arg1(), to);
                }
                if (from.isZero()) {
                  return F.Power(ast.arg1(), Plus(to, C1));
                }
                if (from.isSymbol()) {
                  // 2^(1-from+to)
                  return F.Power(arg1, F.Plus(F.C1, from.negate(), to));
                }
              } else {
                IASTAppendable result = ast.removeAtClone(ast.argSize());
                // result.remove(ast.argSize());
                if (from.isOne()) {
                  result.set(1, F.Power(ast.arg1(), to));
                  return result;
                }
                if (from.isZero()) {
                  result.set(1, F.Power(ast.arg1(), Plus(to, C1)));
                  return result;
                }
                if (from.isSymbol()) {
                  // 2^(1-from+to)
                  result.set(1, F.Power(arg1, F.Plus(F.C1, from.negate(), to)));
                  return result;
                }
              }
            }
          }
        }
        temp = F.NIL;
        IAST resultList = Times();
        temp = evaluateLast(ast.arg1(), iterator, resultList, F.C1);
        if (!temp.isPresent() || temp.equals(resultList)) {
          return F.NIL;
        }
      } catch (final ValidateException ve) {
        LOGGER.log(engine.getLogLevel(), ve.getMessage(S.Product), ve);
        return F.NIL;
      } catch (RecursionLimitExceeded rle) {
        LOGGER.log(engine.getLogLevel(), "Product: Recursionlimit exceeded");
        return F.NIL;
      }
      if (ast.isAST2()) {
        return temp;
      } else {
        IASTAppendable result = ast.removeAtClone(ast.argSize());
        // result.remove(ast.argSize());
        result.set(1, temp);
        return result;
      }
    }
    return F.NIL;
  }

  /**
   * Create a new Product() by removing last iterator or return result of indefinite sum case for
   * Product(a, x)
   *
   * @param ast
   * @param variable the iterator variable
   * @return
   */
  private static IExpr indefiniteProduct(final IAST ast, IExpr variable) {
    IExpr result = F.Power(ast.arg1(), F.Plus(F.CN1, variable));
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

  @Override
  public IExpr numericEval(final IAST functionList, EvalEngine engine) {
    return evaluate(functionList, engine);
  }

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
