package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * AlgebraicIntegerQ(x)
 *
 * <p>
 * Gives True if x is an algebraic integer, and False otherwise.
 */
public class AlgebraicIntegerQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Fast paths for basic and primitive types
    if (arg1.isInteger()) {
      return S.True;
    }
    if (arg1.isRational()) {
      // A rational number that is not a pure integer cannot be an algebraic integer.
      return S.False;
    }
    if (arg1.isComplex()) {
      IComplex c = (IComplex) arg1;
      // For complex numbers with rational parts (a + bi), it is an algebraic integer
      // if and only if both real and imaginary parts are integers (Gaussian integers).
      return F.booleSymbol(c.re().isInteger() && c.im().isInteger());
    }

    // Algebraic integers are exact. If the expression contains any inexact numbers,
    // it cannot be considered an exact algebraic integer.
    if (!arg1.isFree(IExpr::isInexactNumber, true)) {
      return S.False;
    }

    // If it's not a numeric function (e.g., a standalone variable or symbol),
    // we cannot determine if it's an algebraic integer, so return unevaluated.
    if (!arg1.isNumericFunction()) {
      return F.NIL;
    }

    // Compute the minimal polynomial of the expression
    ISymbol x = F.$s("$mpVar"); // internal dummy variable
    IExpr poly = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, arg1, x));

    // If the MinimalPolynomial cannot be determined
    if (poly.isAST(S.MinimalPolynomial) || poly.isNIL()) {
      return F.NIL;
    }

    // Extract the coefficients of the resulting minimal polynomial
    IExpr coeffs = engine.evaluate(F.CoefficientList(poly, x));

    if (coeffs.isList()) {
      IAST list = (IAST) coeffs;

      // An algebraic integer's minimal polynomial must only have rational coefficients.
      // If the minimal polynomial returned has non-rational coefficients, it means
      // MinimalPolynomial couldn't find a polynomial over Q, so we return NIL.
      for (int i = 1; i <= list.argSize(); i++) {
        if (!list.get(i).isRational()) {
          // If we know the original expression is a strict transcendental, we confidently return
          // False.
          if (isKnownTranscendental(arg1)) {
            return S.False;
          }
          return F.NIL;
        }
      }

      // If all coefficients are rational, they MUST be integers for it to be an algebraic integer.
      for (int i = 1; i <= list.argSize(); i++) {
        if (!list.get(i).isInteger()) {
          return S.False;
        }
      }

      // The leading coefficient (the highest degree term's coefficient) must be 1 or -1
      // to make it a strictly monic polynomial natively.
      IExpr lc = list.get(list.argSize());
      if (lc.isOne() || lc.isMinusOne()) {
        return S.True;
      } else {
        return S.False;
      }
    }

    return F.NIL;
  }

  /**
   * Recursively checks if an expression is explicitly transcendental (e.g., Pi, E, EulerGamma) or a
   * simple exact arithmetic combination of exactly one such constant. This prevents false positives
   * where sums like E + Pi fail to find a minimal polynomial but we cannot strictly prove they are
   * transcendental.
   */
  private boolean isKnownTranscendental(IExpr expr) {
    if (expr.equals(S.Pi) || expr.equals(S.E) || expr.equals(S.EulerGamma)
        || expr.equals(S.Catalan)) {
      return true;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isPlus() || ast.isTimes()) {
        int transCount = 0;
        for (int i = 1; i <= ast.argSize(); i++) {
          IExpr arg = ast.get(i);
          if (isKnownTranscendental(arg)) {
            transCount++;
          } else if (!arg.isExactNumber()) {
            // If it's not an exact number (e.g. Integer, Fraction, Complex), we fallback to unknown
            return false;
          }
        }
        return transCount == 1; // exactly one transcendental term, rest are exact numbers
      }
      if (ast.isPower()) {
        if (ast.arg2().isRational() && !ast.arg2().isZero()) {
          return isKnownTranscendental(ast.arg1());
        }
      }
    }
    return false;
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
