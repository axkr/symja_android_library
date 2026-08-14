package org.matheclipse.core.reflection.system;


import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ExponentialGeneratingFunction(expr, n, x)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Gives the exponential generating function in <code>x</code> for the sequence whose
 * <code>n</code>-th term is given by the expression <code>expr</code>.
 * </p>
 * </blockquote>
 */
public class ExponentialGeneratingFunction extends AbstractFunctionOptionEvaluator {

  public ExponentialGeneratingFunction() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (ast.argSize() < 3) {
      return F.NIL;
    }

    IExpr expr = ast.arg1();
    IExpr n = ast.arg2();
    IExpr x = ast.arg3();
    IExpr assumptions = options[0];
    IExpr generateConditions = options[1];

    // Handle multidimensional exponential generating functions
    if (n.isList() && x.isList()) {
      IAST nList = (IAST) n;
      IAST xList = (IAST) x;
      if (nList.argSize() != xList.argSize()) {
        return F.NIL;
      }
      IExpr result = expr;
      for (int i = 1; i <= nList.argSize(); i++) {
        result = engine.evaluate(
            F.ternaryAST3(S.ExponentialGeneratingFunction, result, nList.get(i), xList.get(i)));
      }
      return result;
    }

    if (n.isVariable() && x.isVariable()) {
      // 1. Primary Method: Direct Summation
      // Sum[expr * x^n / n!, {n, 0, Infinity}]
      IExpr term = F.Divide(F.Times(expr, F.Power(x, n)), F.Factorial(n));
      IExpr sum = F.Sum(term, F.List(n, F.C0, S.Infinity));
      IExpr evSum = engine.evaluate(sum);

      if (evSum.isPresent() && !evSum.isAST(S.Sum)) {
        return engine.evaluate(evSum);
      }

      // 2. Polynomial Method: Touchard/Bell expansion.
      // For a polynomial a_n in n: Sum[a_n x^n/n!] = E^x * Sum_k ([n^k] a_n) * BellB(k, x),
      // using the identity Sum[n^k x^n/n!] = E^x * BellB(k, x). Direct summation above cannot
      // close this for degree >= 3 (e.g. n^3), and the Borel fallback below fails on the
      // resulting high-order repeated pole, so handle polynomials explicitly here.
      if (engine.evalTrue(F.PolynomialQ(expr, n))) {
        int degree = engine.evaluate(F.Exponent(expr, n)).toIntDefault();
        if (degree >= 0 && degree <= Config.MAX_POLYNOMIAL_DEGREE) {
          IASTAppendable bellSum = F.PlusAlloc(degree + 1);
          for (int k = 0; k <= degree; k++) {
            IExpr coeff = engine.evaluate(F.Coefficient(expr, n, F.ZZ(k)));
            bellSum.append(F.Times(coeff, F.BellB(F.ZZ(k), x)));
          }
          IExpr result = engine.evaluate(F.Factor(F.Times(F.Exp(x), bellSum)));
          if (result.isPresent() && result.isFree(S.BellB)) {
            return result;
          }
        }
      }

      // 3. Fallback Method: Borel Transform (Inverse Laplace mapping)
      // EGF(expr, n, x) = InverseLaplaceTransform[ (1/s) * GF(expr, n, 1/s), s, x ]
      IExpr sDummy = F.Dummy("sEGF");
      IExpr gf = engine.evaluate(F.ternaryAST3(S.GeneratingFunction, expr, n, sDummy));

      if (gf.isPresent() && !gf.isAST(S.GeneratingFunction)) {
        IExpr sInverse = F.Power(sDummy, F.CN1);
        // Map s -> 1/s in the generating function result
        IExpr gfSubbed = engine.evaluate(F.subst(gf, sDummy, sInverse));
        IExpr laplaceExpr = engine.evaluate(F.ExpandAll(F.Divide(gfSubbed, sDummy)));
        // System.out.println("Laplace expression for EGF: " + laplaceExpr);
        IExpr ilt = engine.evaluate(F.InverseLaplaceTransform(laplaceExpr, sDummy, x));
        if (ilt.isPresent() && ilt.isFree(S.InverseLaplaceTransform)) {
          // Canonicalize: Simplify collapses e.g. Sqrt(1-Cos[a]^2) -> Sin[a]; ExpToTrig then
          // renders the exponential factor in Cosh/Sinh form to match the standard EGF output.
          return engine.evaluate(F.ExpToTrig(F.Simplify(ilt)));
        }
      }

      return F.NIL;
    }

    return Errors.printMessage(ast.topHead(), "ivar", F.List(n), engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions}, //
        new IExpr[] {S.$Assumptions, S.False});
    super.setUp(newSymbol);
  }
}
