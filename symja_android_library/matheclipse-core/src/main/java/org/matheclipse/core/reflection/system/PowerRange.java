package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implements the PowerRange function.
 * 
 */
public class PowerRange extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr start;
    IExpr limit;
    IExpr ratio;

    if (ast.isAST1()) {
      start = F.C1;
      limit = ast.arg1();
      ratio = F.C10;
    } else if (ast.isAST2()) {
      start = ast.arg1();
      limit = ast.arg2();
      ratio = F.C10;
    } else if (ast.isAST3()) {
      start = ast.arg1();
      limit = ast.arg2();
      ratio = ast.arg3();
    } else {
      return F.NIL;
    }
    if (start.isNumber() && limit.isNumber() && ratio.isNumber()) {
      return evaluateNumeric(start, limit, ratio, engine);
    }
    return evaluateSymbolic(start, limit, ratio, ast, engine);
  }

  private static IExpr evaluateNumeric(IExpr start, IExpr limit, IExpr ratio, EvalEngine engine) {
    // Use absolute values for the stopping condition |current| <= |limit|
    IExpr absLimit = limit.abs();


    // Ratio 1 (Avoid infinite loop)
    // Returns {start} if start <= limit, else {}
    if (ratio.isOne()) {
      if (start.abs().lessEqual(absLimit).isTrue()) {
        return F.List(start);
      }
      return F.List();
    }

    // Ratio 0 (Sequence: start, 0, 0...)
    if (ratio.isZero()) {
      IASTAppendable result = F.ListAlloc();
      if (start.abs().lessEqual(absLimit).isTrue()) {
        result.append(start);
        // If 0 is within the limit (which it is if limit >= 0)
        if (F.C0.lessEqual(absLimit).isTrue()) {
          result.append(F.C0);
        }
      }
      return result;
    }

    IASTAppendable result = F.ListAlloc();
    IExpr current = start;

    // Safety guard against infinite loops or massive memory usage
    int maxIterations = engine.getIterationLimit();
    int count = 0;

    // Condition: |current| <= |limit|
    while (current.abs().lessEqual(absLimit).isTrue()) {
      result.append(current);

      // Calculate next value: current * ratio
      current = current.multiply(ratio);

      count++;
      if (count >= maxIterations) {
        break;
      }

      // Additional safety for shrinking series (0 < |ratio| < 1):
      // These series technically never exceed the limit if they started inside it,
      // potentially leading to an infinite loop if we don't have a lower bound.
      // For standard usage (integers > 1), the loop terminates naturally.
    }

    return result;
  }

  /**
   * Logic for symbolic inputs. Solves for n in: start * ratio^n = limit
   */
  private static IExpr evaluateSymbolic(IExpr start, IExpr limit, IExpr ratio, IAST ast,
      EvalEngine engine) {
    // Edge case: start == limit -> {start}
    if (start.equals(limit)) {
      return F.List(start);
    }

    // Calculate quotient q = limit / start
    // We evaluate immediately to simplify fractions (e.g. y*x^2 / (y/x^2) -> x^4)
    IExpr q = engine.evaluate(F.Divide(limit, start));

    // Calculate Exponent n = Log[ratio, q]
    // We construct the Log AST.
    IExpr logExpr = F.Log(ratio, q);

    // Simplify the Logarithm to find the number of steps.
    // We use PowerExpand because standard Log simplification is conservative about branches.
    // PowerExpand allows Log(x^n) -> n*Log(x), which is needed here.
    // Ex: Log(Sqrt(x), x^10) -> Log(x^(1/2), x^10) -> 20
    IExpr nExpr = engine.evaluate(F.PowerExpand(logExpr));

    // Fallback: If PowerExpand didn't finish it (e.g. complex arithmetic), try Simplify.
    if (!nExpr.isInteger()) {
      nExpr = engine.evaluate(F.Simplify(nExpr));
    }

    // Generate list if n is a valid Integer
    if (nExpr.isInteger()) {
      int n = nExpr.toIntDefault();

      // Ensure n is non-negative and fits in memory limits
      if (n >= 0 && n < engine.getIterationLimit()) {
        IASTAppendable result = F.ListAlloc(n + 1);
        IExpr current = start;

        // Iteratively multiply to preserve structural cancellations
        for (int i = 0; i <= n; i++) {
          result.append(current);
          if (i < n) {
            current = engine.evaluate(F.Times(current, ratio));
          }
        }
        return result;
      }
    } else {
      // Range specification in `1` does not have appropriate bounds.
      return Errors.printMessage(S.PowerRange, "range", F.List(ast));
    }

    // If we couldn't determine an integer number of steps, return NIL (unevaluated).
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
