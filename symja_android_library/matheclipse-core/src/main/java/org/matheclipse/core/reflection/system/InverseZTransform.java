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

      // ========================================================================
      // Single linear-pole base case (geometric and polynomial-geometric):
      // any F(z) whose F(z)/z has the shape numerator(z) / (b*z + a)^p is inverted
      // in closed form. Handles every sign/scaling variant of the pole (including a
      // symbolic pole such as (a - z)), which neither the pattern rules in
      // InverseZTransformRules.m nor Apart (for symbolic poles) can resolve.
      // ========================================================================
      IExpr geometric = inverseLinearPole(fx, z, n, engine);
      if (geometric.isPresent()) {
        return geometric;
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

  /**
   * Inverse Z transform of any <code>F(z)</code> whose <code>F(z)/z</code> is a proper rational
   * function with a single linear pole, i.e. <code>numerator(z) / (b*z + a)^p</code> where
   * <code>b</code>, <code>a</code> are free of <code>z</code> and <code>p</code> is a positive
   * integer.
   *
   * <p>
   * The numerator is re-expanded in powers of the pole <code>L = b*z + a</code> via the
   * substitution <code>z -> (w - a)/b</code>, splitting <code>F(z)/z</code> into simple partial
   * fractions <code>d_j * L^(j-p)</code>. Multiplying back by <code>z</code> maps each term through
   * the elementary pair
   *
   * <pre>
   * Z^-1{ z / (b*z + a)^k } = b^(-k) * Binomial(n, k-1) * r^(n-k+1),   r = -a/b
   * </pre>
   *
   * This covers every sign and scaling variant of the pole (e.g. <code>z/(z-1)^2</code>,
   * <code>z/(1-z)^2</code>, <code>(3*z)/(-1+3*z)</code>, <code>-(z/(a-z))</code>) and non-trivial
   * numerators over a symbolic pole (e.g. <code>-(a*z*(a+z))/(a-z)^3</code>), which neither the
   * fixed pattern rules nor {@link org.matheclipse.core.expression.S#Apart} (for symbolic poles)
   * resolve.
   *
   * @return the inverse transform, or {@link F#NIL} if <code>fx</code> is not of this shape
   */
  private static IExpr inverseLinearPole(IExpr fx, IExpr z, IExpr n, EvalEngine engine) {
    if (fx.isFree(z)) {
      return F.NIL;
    }
    // Read numerator/denominator from the raw input so a factored pole such as (z - 1)^3
    // stays factored (Together would expand it into -1 + 3*z - 3*z^2 + z^3).
    IExpr num = engine.evaluate(F.Numerator(fx));
    IExpr den = engine.evaluate(F.Denominator(fx));
    if (den.isOne()) {
      return F.NIL;
    }
    // numG = numerator(F(z)/z): F(z)/z is the standard quotient used in the inverse transform,
    // and a genuine Z-transform carries a factor z in the numerator that must cancel cleanly.
    IExpr numG = engine.evaluate(F.Cancel(F.Divide(num, z)));
    if (!engine.evaluate(F.PolynomialQ(numG, z)).isTrue()) {
      return F.NIL;
    }
    // Recover a factored form if the denominator arrived expanded.
    if (den.isPlus()) {
      den = engine.evaluate(F.Factor(den));
    }
    // Peel off any factor free of z (e.g. Factor may yield 9*(z - 1/3)^2), folding it into numG.
    if (den.isTimes()) {
      IASTAppendable constPart = F.TimesAlloc();
      IASTAppendable rest = F.TimesAlloc();
      for (IExpr factor : (IAST) den) {
        (factor.isFree(z) ? constPart : rest).append(factor);
      }
      if (constPart.argSize() > 0 && rest.argSize() > 0) {
        numG = engine.evaluate(F.Divide(numG, constPart.oneIdentity1()));
        den = engine.evaluate(rest.oneIdentity1());
      }
    }
    // denominator must be linear(z)^p with p a positive integer
    IExpr base;
    int p;
    if (den.isPower() && den.exponent().isInteger() && den.exponent().isPositive()) {
      base = den.base();
      p = den.exponent().toIntDefault();
    } else {
      base = den;
      p = 1;
    }
    if (p < 1) {
      return F.NIL;
    }
    // base must be linear in z: b*z + a (b, a free of z, a != 0 so the pole r = -a/b != 0)
    IExpr b = engine.evaluate(F.Coefficient(base, z, F.C1));
    IExpr a = engine.evaluate(F.Coefficient(base, z, F.C0));
    if (b.isZero() || a.isZero() || !b.isFree(z) || !a.isFree(z)) {
      return F.NIL;
    }
    if (!engine.evaluate(F.Subtract(base, F.Plus(F.Times(b, z), a))).isZero()) {
      return F.NIL;
    }
    IExpr r = engine.evaluate(F.Divide(F.Negate(a), b));

    // Re-expand numG as a polynomial in w = base, via z -> (w - a)/b:
    // numG(z) = Sum_j d_j * base^j, so F(z)/z = Sum_j d_j * base^(j - p).
    IExpr w = F.Dummy("w");
    IExpr q = engine.evaluate(F.ExpandAll(F.subst(numG, z, F.Divide(F.Subtract(w, a), b))));
    int degQ = engine.evaluate(F.Exponent(q, w)).toIntDefault();
    if (degQ < 0 || degQ >= p) {
      // improper: a polynomial part would contribute DiscreteDelta terms - not handled here
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(degQ + 1);
    for (int j = 0; j <= degQ; j++) {
      IExpr dj = engine.evaluate(F.Coefficient(q, w, F.ZZ(j)));
      if (dj.isZero()) {
        continue;
      }
      long k = p - j; // k >= 1 because j <= degQ < p
      // Z^-1{ z / base^k } = b^(-k) * Binomial(n, k-1) * r^(n-k+1)
      sum.append(F.Times(dj, //
          F.Power(b, F.ZZ(-k)), //
          F.Binomial(n, F.ZZ(k - 1L)), //
          F.Power(r, F.Plus(n, F.ZZ(1L - k)))));
    }
    return engine.evaluate(F.Simplify(F.FunctionExpand(sum)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

}
