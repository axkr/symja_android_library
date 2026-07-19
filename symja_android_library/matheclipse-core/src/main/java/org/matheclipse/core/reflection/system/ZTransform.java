package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
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
    if (expr.equals(n)) {
      return F.Times(F.Power(F.Plus(F.CN1, z), F.CN2), z);
    }
    if (expr.isAST(S.UnitStep, 2) && expr.first().equals(n)) {
      // UnitStep(n) == 1 for n >= 0, so ZTransform(UnitStep(n), n, z) == ZTransform(1, n, z)
      return engine.evaluate(F.Divide(z, F.Subtract(z, F.C1)));
    }
    if (expr.isPlus()) {
      IExpr mapped = ((IAST) expr).mapThread(F.ZTransform(F.Slot1, n, z), 1);
      // For polynomial inputs in n, combine the partial results into a single
      // rational function (matches Mathematica's ZTransform output).
      if (engine.evaluate(F.PolynomialQ(expr, n)).isTrue()) {
        return engine.evaluate(F.Together(mapped));
      }
      return mapped;
    }

    if (expr.isTimes()) {
      IExpr temp = times(engine, (IAST) expr, n, z);
      if (temp.isPresent()) {
        return temp;
      }
    } else if (expr.isPower()) {
      IExpr base = expr.base();
      IExpr exponent = expr.exponent();
      if (base.equals(n) && exponent.isInteger() && exponent.isPositive()) {
        // ZTransform(n^k, n, z) = (-z * d/dz)^k ZTransform(1, n, z) for integer k > 0
        int k = exponent.toIntDefault();
        if (k > 0) {
          return polynomialTransform(engine, k, F.C1, n, z);
        }
      }
      // Expand polynomial powers with a compound base, e.g. (1 + n)^2
      if (!expr.isExpanded()) {
        IExpr expanded = engine.evaluate(F.ExpandAll(expr));
        if (!expanded.equals(expr)) {
          return engine.evaluate(F.ZTransform(expanded, n, z));
        }
      }
    }

    // ==============================================================================
    // Time Shift Property - ZTransform(f(n + k)) and ZTransform(f(n - k))
    // Evaluates to: z^k * ZTransform(f(n)) - sum_{i=0}^{k-1} z^{k-i} * f(i) (for k > 0)
    // Evaluates to: z^k * ZTransform(f(n)) (for k < 0 causal)
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

          if (nPart.equals(n) && kPart.isInteger()) {
            int k = kPart.toIntDefault();
            if (k > 0) {
              IASTAppendable sum = F.PlusAlloc(k + 1);
              for (int i = 0; i < k; i++) {
                IExpr fi = function.setAtCopy(1, F.ZZ(i));
                sum.append(F.Times(F.CN1, F.Power(z, F.ZZ(k - i)), fi));
              }
              IExpr fn = function.setAtCopy(1, n);
              sum.append(F.Times(F.Power(z, F.ZZ(k)), F.ZTransform(fn, n, z)));
              return engine.evaluate(sum);
            } else if (k < 0) {
              // Causal negative shift: ZTransform(f(n - m)) = z^(-m) ZTransform(f(n))
              IExpr fn = function.setAtCopy(1, n);
              return engine.evaluate(F.Times(F.Power(z, F.ZZ(k)), F.ZTransform(fn, n, z)));
            }
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

  private IExpr times(EvalEngine engine, IAST timesAST, IExpr n, IExpr z) {
    IASTAppendable constantArgs = F.TimesAlloc();
    IASTAppendable nArgs = F.TimesAlloc();
    for (int indx = 1; indx < timesAST.size(); indx++) {
      IExpr arg = timesAST.get(indx);
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

    // ==============================================================================
    // Higher-Order Polynomial Transform
    // ZTransform(n^k * f(n), n, z) = (-z * d/dz)^k ZTransform(f(n), n, z)
    // ==============================================================================
    int kExponent = 0;
    IASTAppendable restNArgs = F.TimesAlloc();
    for (int i = 1; i <= timesAST.argSize(); i++) {
      IExpr arg = timesAST.get(i);
      if (kExponent == 0 && arg.equals(n)) {
        kExponent = 1;
      } else if (kExponent == 0 && arg.isPower() && arg.first().equals(n)
          && arg.second().isInteger()) {
        int k = arg.second().toIntDefault();
        // Only trigger derivative expansion for positive integer powers (n^1, n^2, n^3...)
        if (k > 0) {
          kExponent = k;
        } else {
          restNArgs.append(arg);
        }
      } else {
        restNArgs.append(arg);
      }
    }

    if (kExponent > 0) {
      IExpr f_n;
      if (restNArgs.argSize() == 0) {
        f_n = F.C1;
      } else if (restNArgs.argSize() == 1) {
        f_n = restNArgs.arg1();
      } else {
        f_n = restNArgs;
      }
      return polynomialTransform(engine, kExponent, f_n, n, z);
    }

    // Expand polynomials multiplied by functions (e.g., (1+n)^2 * f(n))
    // So ZTransformRules.m can properly match n^2 * f(n) and calculate the derivatives
    if (!timesAST.isExpanded()) {
      IExpr expanded = engine.evaluate(F.ExpandAll(timesAST));
      if (expanded.equals(timesAST)) {
        // Expansion did not change the expression, avoid infinite recursion
        return F.NIL;
      }
      return engine.evaluate(F.ZTransform(expanded, n, z));
    }
    return F.NIL;
  }

  /**
   * Apply the polynomial-multiplication property
   * <code>ZTransform(n^k * f(n), n, z) == (-z * d/dz)^k ZTransform(f(n), n, z)</code>.
   *
   * @param k a positive integer exponent
   * @param f_n the remaining factor <code>f(n)</code> (or {@link F#C1})
   */
  private IExpr polynomialTransform(EvalEngine engine, int k, IExpr f_n, IExpr n, IExpr z) {
    // Use a secure dummy variable for differentiation.
    // If 'z' is a compound fractional expression like (1/s), D(..., 1/s) is invalid!
    IExpr zSym = z.isSymbol() ? z : F.Dummy("zDummy");
    IExpr result = engine.evaluate(F.ZTransform(f_n, n, zSym));

    // Iteratively apply the (-z * D(..., z)) operator k times
    for (int i = 0; i < k; i++) {
      result = engine.evaluate(F.Times(F.CN1, zSym, F.D(result, zSym)));
    }

    // Safely project the solved derivative result back onto the original compound variable
    if (!zSym.equals(z)) {
      result = engine.evaluate(F.subst(result, zSym, z));
    }
    if (f_n.isOne()) {
      // Pure power ZTransform(n^k, n, z) is a rational function of z. Together first so that
      // higher-order derivatives (n^4, ...) collapse into a single fraction that Simplify alone
      // would otherwise leave expanded. (Only for the pure-power case, to avoid reshuffling the
      // symbolic D(ZTransform(f(n),...)) forms produced when f(n) is an unknown sequence.)
      result = engine.evaluate(F.Together(result));
    }
    return engine.evaluate(F.Simplify(result));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }
}
