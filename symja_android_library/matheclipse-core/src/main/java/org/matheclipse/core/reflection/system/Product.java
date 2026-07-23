package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Times;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.reflection.system.rulesets.ProductRules;
import com.google.common.base.Suppliers;

public class Product extends ListFunctions.Table implements ProductRules {

  private static com.google.common.base.Supplier<Matcher> MATCHER1;

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
      IExpr resultTimes = engine.evaluate(arg1.mapThread(ast, 1));
      if (resultTimes.isTimes()) {
        return engine.evaluate(F.FullSimplify(resultTimes));
      }
      return resultTimes;
    }
    IAST preevaledProduct = engine.preevalForwardBackwardAST(ast, 1);
    arg1 = preevaledProduct.arg1();
    return evaluateProduct(preevaledProduct, arg1, false, engine);
  }

  protected static IExpr evaluateProduct(final IAST preevaledProduct, IExpr arg1,
      boolean approximationMode, EvalEngine engine) {
    if (preevaledProduct.argSize() >= 2) {
      final IExpr lastArg = preevaledProduct.last();
      final IAST list = lastArg.makeList();

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

      if (preevaledProduct.argSize() >= 2) {
        IAST productForm = preevaledProduct;
        IAST lastList = list;
        if (list.isAST2()) {
          // Product(f(x),..., {x, a}) ==> Product(f(x),..., {x, 1, a})
          lastList = F.List(list.arg1(), F.C1, list.arg2());
          productForm = productForm.setAtCopy(productForm.argSize(), lastList);
        }

        if (productForm.argSize() > 2) {
          // Multiple iterators: Evaluate the innermost product recursively.
          IAST reducedProductForm = F.Product(productForm.arg1(), lastList);
          IExpr reducedResult = engine.evaluate(reducedProductForm);
          if (reducedResult.isPresent() && !reducedResult.equals(reducedProductForm)) {
            IASTAppendable result = productForm.removeAtClone(productForm.argSize());
            result.set(1, reducedResult);
            return result;
          }
        } else {
          // Single iterator: apply pattern matcher rules if any
          IExpr result = matcher1().apply(productForm);
          if (result.isPresent()) {
            return result;
          }
        }
      }

      IExpr argN = lastArg;
      IIterator<IExpr> iterator = null;

      // === 1. SYMBOLIC REDUCTION INTERCEPT ===
      // Executed before evaluateTableThrow to prevent dummy variable shadowing
      // when limits contain the iterator variable symbolically.
      if (preevaledProduct.argSize() >= 2 && argN.isList()) {
        if (arg1.isZero()) {
          return F.C0;
        }

        try {
          iterator = Iterator.create((IAST) argN, preevaledProduct.argSize(), engine);
        } catch (final ValidateException ve) {
          return Errors.printMessage(S.Product, ve, engine);
        }

        // A list style iterator like {e, {2,1,1,1}} has no lower/upper limit and no step
        // ({@link IIterator} returns null for those). The symbolic reduction below only applies to
        // iterators with real limits; leave the list case to the numerical unrolling fallback.
        if (iterator != null && iterator.isValidVariable() && iterator.getLowerLimit() != null
            && iterator.getUpperLimit() != null && iterator.getStep() != null) {
          if (iterator.getUpperLimit().isInfinity()) {
            if (arg1.isOne()) {
              return F.C1;
            }
            if (arg1.isPositiveResult() && arg1.isIntegerResult()) {
              return F.CInfinity;
            }
          }

          if (!iterator.isNumericFunction() && iterator.getStep().isOne()) {
            final ISymbol var = iterator.getVariable();
            final IExpr from = iterator.getLowerLimit();
            final IExpr to = iterator.getUpperLimit();

            // Divert to the Hypergeometric Symbolic Product Engine
            IExpr symProd = tryClosedFormReduction(arg1, var, from, to, engine);
            if (symProd.isPresent()) {

              // Simplify the result to ensure factorial ratios are reduced
              symProd = engine.evaluate(F.Simplify(symProd));

              if (preevaledProduct.isAST2()) {
                return symProd;
              }
              IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
              result.set(1, symProd);
              return result;
            }

            // Universal evaluation for terms free of the iterator
            if (arg1.isFree(var)) {
              IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(to, from), F.C1)));
              IExpr evalPower = F.Power(arg1, count);
              if (preevaledProduct.isAST2()) {
                return engine.evaluate(evalPower);
              }
              IASTAppendable result = preevaledProduct.removeAtClone(preevaledProduct.argSize());
              result.set(1, engine.evaluate(evalPower));
              return result;
            }
          }
        }
      }

      // === 2. NUMERICAL UNROLLING FALLBACK ===
      try {
        IExpr temp = evaluateTableThrow(preevaledProduct, Times(), Times(), engine);
        if (temp.isPresent()) {
          return temp;
        }
      } catch (final ValidateException ve) {
        return Errors.printMessage(S.Product, ve, engine);
      } catch (RecursionLimitExceeded rle) {
        int recursionLimit = engine.getRecursionLimit();
        Errors.printMessage(S.Product, "reclim2",
            F.list(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), preevaledProduct),
            engine);
        return F.NIL;
      }

      // === 3. FINAL MANUAL LOOP EVALUATION ===
      if (iterator != null) {
        try {
          if (preevaledProduct.argSize() > 2) {
            return F.NIL;
          }
          IAST resultList = Times();
          IExpr temp = evaluateLast(preevaledProduct.arg1(), iterator, resultList, F.C1);
          if (temp.isNIL() || temp.equals(resultList)) {
            return F.NIL;
          }
          return temp;
        } catch (final ValidateException ve) {
          return Errors.printMessage(S.Product, ve, engine);
        } catch (RecursionLimitExceeded rle) {
          int recursionLimit = engine.getRecursionLimit();
          Errors.printMessage(S.Product, "reclim2",
              F.list(recursionLimit < 0 ? F.CInfinity : F.ZZ(recursionLimit), preevaledProduct),
              engine);
          return F.NIL;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Hypergeometric Term Recognition (Pochhammer Mapping & Exponential Products) * @param pK the
   * product term with iterator k (e.g. k^2 + 3k + 2 or 2^k)
   * 
   * @param k the iterator variable
   * @param lower the lower bound of the product
   * @param upper the upper bound of the product
   * @param engine the evaluation engine
   * @return the reduced form of the product or F.NIL if not reducible
   */
  public static IExpr tryClosedFormReduction(IExpr pK, IExpr k, IExpr lower, IExpr upper,
      EvalEngine engine) {
    if (pK.isFree(k)) {
      IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(upper, lower), F.C1)));
      return engine.evaluate(F.Power(pK, count));
    }

    // 1. Intercept Exponential Products & Constant Powers
    if (pK.isPower()) {
      IExpr base = pK.base();
      IExpr exponent = pK.exponent();
      if (!exponent.isFree(k)) {
        // Exponential product: base^f(k) -> base^Sum(f(k))
        IExpr sum = engine.evaluate(F.Sum(exponent, F.List(k, lower, upper)));
        if (sum.isPresent() && !sum.isAST(S.Sum)) {
          return engine.evaluate(F.Power(base, sum));
        }
      } else {
        // Constant power: f(k)^p -> Product(f(k))^p
        IExpr baseProd = tryClosedFormReduction(base, k, lower, upper, engine);
        if (baseProd.isPresent()) {
          return engine.evaluate(F.Power(baseProd, exponent));
        }
      }
    }

    // 2. Intercept already-factored terms (e.g. (k + 1/2) * (k + 3/2))
    if (pK.isTimes()) {
      IASTAppendable res = F.TimesAlloc(pK.argSize());
      for (IExpr arg : (IAST) pK) {
        IExpr termProd = tryClosedFormReduction(arg, k, lower, upper, engine);
        if (!termProd.isPresent()) {
          return F.NIL;
        }
        res.append(termProd);
      }
      return engine.evaluate(res);
    }

    // 3. Strict Linear Coefficient Extraction
    IExpr A = engine.evaluate(F.Coefficient(pK, k));
    IExpr B = engine.evaluate(F.Expand(F.Subtract(pK, F.Times(A, k))));
    IExpr check = engine.evaluate(F.ExpandAll(F.Subtract(pK, F.Plus(F.Times(A, k), B))));

    // A and B must strictly be free of the iterator K!
    if (check.isZero() && !A.isZero() && A.isFree(k) && B.isFree(k)) {
      IExpr root = engine.evaluate(F.Divide(B, A));
      IExpr count = engine.evaluate(F.Simplify(F.Plus(F.Subtract(upper, lower), F.C1)));
      IExpr startVal = engine.evaluate(F.Simplify(F.Plus(lower, root)));

      // If the very first term in the sequence evaluates to 0, the entire product is trivially 0.
      if (startVal.isZero()) {
        return F.C0;
      }

      IExpr poch;
      if (startVal.isOne()) {
        // Pochhammer(1, count) is strictly Factorial(count)
        poch = F.Factorial(count);
      } else if (startVal.isInteger() && startVal.greaterThan(F.C0).isTrue()) {
        IExpr m = startVal;
        // Pochhammer(m, count) mapped to (count + m - 1)! / (m - 1)!
        IExpr num = F.Factorial(F.Subtract(F.Plus(count, m), F.C1));
        IExpr den = engine.evaluate(F.Factorial(F.Subtract(m, F.C1)));
        poch = engine.evaluate(F.Divide(num, den));
      } else if (startVal.isNumber() && !startVal.isInteger()) {
        // Return Gamma ratio for numeric fractional or complex shifts
        IExpr num = F.Gamma(F.Plus(startVal, count));
        IExpr den = F.Gamma(startVal);
        poch = engine.evaluate(F.Divide(num, den));
      } else {
        // Keep Pochhammer for symbolic shift or non-positive integers
        poch = F.Pochhammer(startVal, count);
      }

      if (A.isOne()) {
        return poch;
      }
      return engine.evaluate(F.Times(F.Power(A, count), poch));
    }

    // 4. Try factoring generic polynomials (e.g. k^2 + 3k + 2 -> (k+1)*(k+2))
    IExpr factored = engine.evaluate(F.Factor(pK));
    if (!factored.equals(pK) && factored.isTimes()) {
      IASTAppendable res = F.TimesAlloc(factored.argSize());
      for (IExpr arg : (IAST) factored) {
        IExpr termProd = tryClosedFormReduction(arg, k, lower, upper, engine);
        if (!termProd.isPresent()) {
          return F.NIL;
        }
        res.append(termProd);
      }
      return engine.evaluate(res);
    }

    return F.NIL;
  }

  private static IExpr productPowerFormula(IExpr powerAST, IExpr k, IExpr from, IExpr to) {
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
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
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
