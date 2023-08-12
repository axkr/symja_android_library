package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.eval.Errors;
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
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.reflection.system.rulesets.ProductRules;
import com.google.common.base.Suppliers;

/**
 *
 *
 * <pre>
 * Product(expr, {i, imin, imax})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * evaluates the discrete product of <code>expr</code> with <code>i</code> ranging from <code>
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
 * <p>
 * <code>i</code> ranges from <code>imin</code> to <code>imax</code> in steps of <code>di</code>.
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
 * <p>
 * evaluates <code>expr</code> as a multiple sum, with <code>{i, ...}, {j, ...}, ...</code> being in
 * outermost-to-innermost order.
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
 * <p>
 * Symbolic products involving the factorial are evaluated:
 *
 * <pre>
 * &gt;&gt; Product(k, {k, 3, n})
 * n! / 2
 * </pre>
 *
 * <p>
 * Evaluate the <code>n</code>th primorial:
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

  private static Supplier<Matcher> MATCHER1;

  private static Matcher matcher1() {
    return MATCHER1.get();
  }

  public Product() {}

  /**
   * Product of expressions.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation">Wikipedia
   * Multiplication</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      arg1 = F.expand(arg1, false, false, false);
      if (arg1.isNIL()) {
        arg1 = ast.arg1();
      }
    }
    if (arg1.isTimes()) {
      // IASTMutable prod = ast.setAtCopy(1, null);
      return arg1.mapThread(ast, 1);
    }
    IAST preevaledProduct = engine.preevalForwardBackwardAST(ast, 1);
    arg1 = preevaledProduct.arg1();
    return evaluateProduct(preevaledProduct, arg1, engine);
  }

  private IExpr evaluateProduct(final IAST preevaledProduct, IExpr arg1, EvalEngine engine) {
    if (preevaledProduct.size() > 2) {
      final IAST list = preevaledProduct.last().makeList();
      if (list.isAST1()) {
        // indefinite product case

        IExpr variable = list.arg1();
        if (variable.isVariable()) {
          IExpr arg = preevaledProduct.arg1();
          if (preevaledProduct.arg1().isFree(variable)) {
            return indefiniteProduct(preevaledProduct, variable);
          }
          if (arg.isPower() && arg.equalsAt(1, variable)) {
            return productPowerFormula(arg, variable, F.C1, variable.dec());
          }
        }
      }
    }
    IExpr temp = F.NIL;
    if (preevaledProduct.size() == 3) {
      IExpr result = matcher1().apply(preevaledProduct);
      if (result.isPresent()) {
        return result;
      }
    }
    try {
      temp = evaluateTableThrow(preevaledProduct, Times(), Times(), engine);
      if (temp.isPresent()) {
        return temp;
      }
    } catch (final ValidateException ve) {
      return Errors.printMessage(S.Product, ve, engine);
    }
    if (arg1.isPower()) {
      IExpr exponent = arg1.exponent();
      boolean flag = true;
      // Prod( i^a, {i,from,to},... )
      for (int i = 2; i < preevaledProduct.size(); i++) {
        IIterator<IExpr> iterator;
        if (preevaledProduct.get(i).isList()) {
          iterator = Iterator.create((IAST) preevaledProduct.get(i), i, engine);
        } else {
          iterator = Iterator.create(F.list(preevaledProduct.get(i)), i, engine);
        }
        if (iterator.isValidVariable() && exponent.isFree(iterator.getVariable())) {
          continue;
        }
        flag = false;
        break;
      }
      if (flag) {
        IASTMutable prod = preevaledProduct.copy();
        prod.set(1, arg1.base());
        return F.Power(prod, exponent);
      }
    }
    IExpr argN = preevaledProduct.last();
    if (preevaledProduct.size() >= 3 && argN.isList()) {
      try {
        if (arg1.isZero()) {
          // Product(0, {k, n, m})
          return F.C0;
        }
        IIterator<IExpr> iterator =
            Iterator.create((IAST) argN, preevaledProduct.argSize(), engine);
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
                    if (preevaledProduct.isAST2()) {
                      return F.Power(base, Times(C1D2, to, Plus(C1, to)));
                    }
                    IASTAppendable result =
                        preevaledProduct.removeAtClone(preevaledProduct.argSize());
                    // result.remove(ast.argSize());
                    result.set(1, F.Power(base, Times(C1D2, to, Plus(C1, to))));
                    return result;
                  }
                }
              }
            }

            if (arg1.isFree(var)) {

              if (preevaledProduct.isAST2()) {
                if (from.isOne()) {
                  return F.Power(preevaledProduct.arg1(), to);
                }
                if (from.isZero()) {
                  return F.Power(preevaledProduct.arg1(), Plus(to, C1));
                }
                if (from.isSymbol()) {
                  // 2^(1-from+to)
                  return F.Power(arg1, F.Plus(F.C1, from.negate(), to));
                }
              } else {
                IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
                // result.remove(ast.argSize());
                if (from.isOne()) {
                  result.set(1, F.Power(preevaledProduct.arg1(), to));
                  return result;
                }
                if (from.isZero()) {
                  result.set(1, F.Power(preevaledProduct.arg1(), Plus(to, C1)));
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
        temp = evaluateLast(preevaledProduct.arg1(), iterator, resultList, F.C1);
        if (temp.isNIL() || temp.equals(resultList)) {
          return F.NIL;
        }
      } catch (final ValidateException ve) {
        return Errors.printMessage(S.Product, ve, engine);
      } catch (RecursionLimitExceeded rle) {
        // Recursion depth of `1` exceeded during evaluation of `2`.
        int recursionLimit = engine.getRecursionLimit();
        Errors.printMessage(S.Product, "reclim2",
            F.list(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), preevaledProduct),
            engine);
        return F.NIL;
      }
      if (preevaledProduct.isAST2()) {
        return temp;
      } else {
        IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
        result.set(1, temp);
        return result;
      }
    }
    return F.NIL;
  }

  private IExpr productPowerFormula(IExpr powerAST, IExpr k, IExpr from, IExpr to) {
    if (from.isOne()) {
      // ((-1+variable)!)^exponent
      return F.Power(F.Factorial(to), powerAST.exponent());
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
    newSymbol.setAttributes(ISymbol.HOLDALL);
    MATCHER1 = Suppliers.memoize(ProductRules::init1);
  }
}
