package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ZTransform(expr, n, z)
 * </pre>
 */
public class ZTransform extends AbstractFunctionEvaluator {

  public ZTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 3) {
      return F.NIL;
    }

    IExpr expr = ast.arg1();
    IExpr n = ast.arg2();
    IExpr z = ast.arg3();

    if (!n.isVariable()) {
      return Errors.printMessage(ast.topHead(), "ivar", F.List(n), engine);
    }

      if (expr.isFree(n)) {
        // ZTransform(c, n, z) = c * z / (z - 1)
        return engine.evaluate(F.Times(expr, F.Divide(z, F.Subtract(z, F.C1))));
      }

      if (expr.isPlus()) {
        return ((IAST) expr).mapThread(F.ZTransform(F.Slot1, n, z), 1);
      }

      if (expr.isTimes()) {
        IAST function = (IAST) expr;
        IASTAppendable constantArgs = F.TimesAlloc();
        IASTAppendable nArgs = F.TimesAlloc();
        for (int indx = 1; indx < function.size(); indx++) {
          IExpr arg = function.get(indx);
          if (arg.isFree(n)) {
            constantArgs.append(arg);
          } else {
            nArgs.append(arg);
          }
        }

        if (constantArgs.argSize() > 0) {
          IExpr constantPart = constantArgs.argSize() == 1 ? constantArgs.arg1() : constantArgs;
          IExpr nPart = nArgs.argSize() == 1 ? nArgs.arg1() : nArgs;
          return engine.evaluate(F.Times(constantPart, F.ZTransform(nPart, n, z)));
        }

        if (function.argSize() == 2) {
          IExpr arg1 = function.arg1();
          IExpr arg2 = function.arg2();

          // ZTransform(n * f, n, z) = -z * D(ZTransform(f, n, z), z)
          if (arg1.equals(n)) {
            return engine.evaluate(F.Times(F.CN1, z, F.D(F.ZTransform(arg2, n, z), z)));
          } else if (arg2.equals(n)) {
            return engine.evaluate(F.Times(F.CN1, z, F.D(F.ZTransform(arg1, n, z), z)));
          }
        }

        // RESTORED: Expand polynomials multiplied by functions (e.g., (1+n)^2 * f(n))
        // So ZTransformRules.m can properly match n^2 * f(n) and calculate the derivatives
        if (!function.isExpanded()) {
          IExpr expanded = engine.evaluate(F.ExpandAll(function));
          if (expanded.equals(function)) {
            // Expansion did not change the expression, avoid infinite recursion
            return F.NIL;
          }
          return engine.evaluate(F.ZTransform(expanded, n, z));
        }

      } else if (expr.isPower()) {
        // RESTORED: Expand polynomial powers
        if (!expr.isExpanded()) {
          return engine.evaluate(F.ZTransform(engine.evaluate(F.ExpandAll(expr)), n, z));
        }
      }

      // ==============================================================================
      // RESTORED: Time Shift Property - ZTransform(f(n + k))
      // Evaluates to: z^k * ZTransform(f(n)) - sum_{i=0}^{k-1} z^{k-i} * f(i)
      // ==============================================================================
      if (expr.isAST1()) {
        IAST function = (IAST) expr;
        if (function.arg1().isPlus()) {
          IAST plus = (IAST) function.arg1();
          IExpr nPart = F.C0;
          IExpr kPart = F.C0;
          boolean isValidShift = true;

          for (int i = 1; i < plus.size(); i++) {
            if (plus.get(i).equals(n)) {
              nPart = F.Plus(nPart, plus.get(i));
            } else if (plus.get(i).isInteger()) {
              kPart = F.Plus(kPart, plus.get(i));
            } else {
              isValidShift = false;
              break;
            }
          }

          if (isValidShift) {
            nPart = engine.evaluate(nPart);
            kPart = engine.evaluate(kPart);

            if (nPart.equals(n) && kPart.isInteger() && kPart.isPositive()) {
              int k = kPart.toIntDefault();
              IASTAppendable sum = F.PlusAlloc(k + 1);
              for (int i = 0; i < k; i++) {
                IExpr fi = function.setAtCopy(1, F.ZZ(i));
                sum.append(F.Times(F.CN1, F.Power(z, F.ZZ(k - i)), fi));
              }
              IExpr fn = function.setAtCopy(1, n);
              sum.append(F.Times(F.Power(z, F.ZZ(k)), F.ZTransform(fn, n, z)));
              return engine.evaluate(sum);
            }
          }
        }
      }

      // ==============================================================================
      // STRICT MUTUAL RECURSION VALIDATION
      // Check if the expression contains an unknown/user-defined function of n
      // (e.g., a(Sqrt(n)) or Derivative(1)(a)(n)). If so, block the Sum fallback.
      // ==============================================================================
      boolean containsUnknownSequence = !expr.isFree(x -> {
        if (x.isAST() && !x.isFree(n)) {
          IExpr head = x.head();
          // Check for simple non-builtin symbol (e.g., a(n))
          if (head.isSymbol() && !head.isBuiltInSymbol()) {
            return true;
          }
          // Check for compound non-builtin heads (e.g., Derivative(1)(a)(n))
          if (head.isAST() && !head.isFree(y -> y.isSymbol() && !y.isBuiltInSymbol(), false)) {
            return true;
          }
        }
        return false;
      }, false);

      if (!containsUnknownSequence) {
        // Fallback to definition: ZTransform(expr, n, z) = Sum(expr * z^(-n), {n, 0, Infinity})
        IExpr sumTerm = F.Times(expr, F.Power(z, F.Negate(n)));
        IExpr sum = F.Sum(sumTerm, F.List(n, F.C0, S.Infinity));
        IExpr evSum = engine.evaluate(sum);

        if (evSum.isPresent() && !evSum.isAST(S.Sum)) {
          return evSum;
        }
      }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }
}
