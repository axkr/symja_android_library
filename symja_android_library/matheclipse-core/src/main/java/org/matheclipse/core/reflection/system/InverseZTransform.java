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

public class InverseZTransform extends AbstractFunctionEvaluator {

  public InverseZTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // see http://www.reduce-algebra.com/docs/ztrans.pdf
    final IExpr fx = ast.arg1();
    if (fx.isIndeterminate()) {
      return S.Indeterminate;
    }
    IExpr z = ast.arg2();
    if (!(z.isVariable() || z.isList())) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(z), engine);
    }
    IExpr n = ast.arg3();
    if (!(n.isVariable() || n.isList())) {
      return Errors.printMessage(ast.topHead(), "ivar", F.list(n), engine);
    }
    if (!z.isList() && !n.isList()) {

      if (fx.isNumber()) {
        // Inverse Z transform of a constant C is C * DiscreteDelta(n)
        return F.Times(fx, F.DiscreteDelta(n));
      }

      if (fx.isPlus()) {
        // InverseZTransform(a_+b_+c_,z_,n_) ->
        // InverseZTransform(a,z,n)+InverseZTransform(b,z,n)+InverseZTransform(c,z,n)
        return fx.mapThread(F.InverseZTransform(F.Slot1, z, n), 1);
      }

      // ========================================================================
      // Table Rules: Exponential Fractions E^(a / z^p)
      // Z^-1{ z^m * E^(a/z) } = a^(n+m) / Gamma(n+m+1)
      // Z^-1{ z^m * E^(a/z^2) } = a^((n+m)/2) * (1 + (-1)^(n+m)) / (2 * Gamma((n+m)/2 + 1))
      // ========================================================================
      IExpr mPow = F.C0;
      IExpr expTerm = fx;
      IExpr coeffTerm = F.C1;

      if (fx.isTimes()) {
        IASTAppendable rest = F.TimesAlloc();
        for (IExpr arg : (IAST) fx) {
          if (arg.isPower() && arg.first().equals(z) && arg.second().isInteger()) {
            mPow = arg.second();
          } else if (arg.isExp()) {
            expTerm = arg;
          } else {
            rest.append(arg);
          }
        }
        if (rest.argSize() > 0) {
          coeffTerm = rest.argSize() == 1 ? rest.arg1() : rest;
        }
      }

      if (expTerm.isPower() && expTerm.first().equals(S.E)) {
        IExpr exponent = expTerm.second();
        IExpr aParam = F.C1;
        IExpr pPow = F.C0;

        if (exponent.isTimes()) {
          IASTAppendable aRest = F.TimesAlloc();
          for (IExpr arg : (IAST) exponent) {
            if (arg.isPower() && arg.first().equals(z) && arg.second().isInteger()) {
              pPow = arg.second();
            } else if (arg.equals(z)) {
              pPow = F.C1;
            } else {
              aRest.append(arg);
            }
          }
          aParam = aRest.argSize() == 1 ? aRest.arg1() : aRest;
        } else if (exponent.isPower() && exponent.first().equals(z)
            && exponent.second().isInteger()) {
          pPow = exponent.second();
        } else if (exponent.equals(z)) {
          pPow = F.C1;
        }

        if (pPow.isInteger() && pPow.isNegative()) {
          int p = -pPow.toIntDefault(); // p is positive denominator power
          IExpr nPlusM = engine.evaluate(F.Plus(n, mPow));

          if (p == 1) {
            IExpr num = engine.evaluate(F.Power(aParam, nPlusM));
            IExpr den = F.Gamma(F.Plus(nPlusM, F.C1));
            return engine.evaluate(F.Times(coeffTerm, F.Divide(num, den)));
          } else if (p == 2) {
            IExpr halfNPlusM = engine.evaluate(F.Divide(nPlusM, F.C2));
            IExpr num1 = engine.evaluate(F.Power(aParam, halfNPlusM));
            IExpr num2 = engine.evaluate(F.Plus(F.C1, F.Power(F.CN1, nPlusM)));
            IExpr den = F.Times(F.C2, F.Gamma(F.Plus(halfNPlusM, F.C1)));
            return engine.evaluate(F.Times(coeffTerm, F.Divide(F.Times(num1, num2), den)));
          }
        }
      }

      if (fx.isTimes()) {
        IAST function = (IAST) fx;
        IASTAppendable constantArgs = F.TimesAlloc();
        IASTAppendable zArgs = F.TimesAlloc();

        // Safely separate variables without mutating the source AST
        for (int indx = 1; indx < function.size(); indx++) {
          IExpr arg = function.get(indx);
          if (arg.isFree(z)) {
            constantArgs.append(arg);
          } else {
            zArgs.append(arg);
          }
        }

        if (constantArgs.argSize() > 0) {
          IExpr constantPart = constantArgs.argSize() == 1 ? constantArgs.arg1() : constantArgs;
          if (zArgs.argSize() == 0) {
            return F.Times(constantPart, F.DiscreteDelta(n));
          }
          IExpr zPart = zArgs.argSize() == 1 ? zArgs.arg1() : zArgs;
          return F.Times(constantPart, F.InverseZTransform(zPart, z, n));
        }
      }

      // ========================================================================
      // Partial Fraction Expansion: F(z) = z * Apart(F(z)/z, z)
      // Splits complex rational polynomials into simple transformable table pairs
      // ========================================================================
      if (!fx.isFree(z)) {
        IAST apart = F.Apart(F.Divide(fx, z), z);
        IExpr fxOverZ = engine.evaluate(apart);

        // If Apart successfully broke the expression into a Sum
        if (fxOverZ.isPlus()) {
          IASTAppendable sum = F.PlusAlloc(fxOverZ.argSize());
          for (IExpr arg : (IAST) fxOverZ) {
            // Re-multiply by z
            IExpr zTerm = engine.evaluate(F.Times(z, arg));
            sum.append(F.InverseZTransform(zTerm, z, n));
          }
          return sum;
        } else {
          // Apart may have simplified it to a single factored term (e.g. extracting a constant
          // denominator)
          IExpr zTerm = engine.evaluate(F.Times(z, fxOverZ));

          // Prevent infinite recursion if Apart did not change the structure
          if (!zTerm.equals(fx)) {
            return F.InverseZTransform(zTerm, z, n);
          }
        }
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

}
