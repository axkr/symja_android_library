package org.matheclipse.core.reflection.system;


import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
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
        result = engine
            .evaluate(
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

      // 2. Fallback Method: Borel Transform (Inverse Laplace mapping)
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
        if (ilt.isPresent() && !ilt.isAST(S.InverseLaplaceTransform)) {
          return engine.evaluate(ilt);
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