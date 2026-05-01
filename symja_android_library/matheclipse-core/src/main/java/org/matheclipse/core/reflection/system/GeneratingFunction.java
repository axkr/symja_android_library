package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * GeneratingFunction(expr, n, x)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Gives the ordinary generating function in <code>x</code> for the sequence whose <code>n</code>-th
 * series coefficient is given by the expression <code>expr</code>.
 * </p>
 * </blockquote>
 */
public class GeneratingFunction extends AbstractFunctionOptionEvaluator {

  public GeneratingFunction() {}

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

    // Handle multidimensional generating functions
    if (n.isList() && x.isList()) {
      IAST nList = (IAST) n;
      IAST xList = (IAST) x;
      if (nList.argSize() != xList.argSize()) {
        return F.NIL;
      }
      IExpr result = expr;
      for (int i = 1; i <= nList.argSize(); i++) {
        result = engine
            .evaluate(F.ternaryAST3(S.GeneratingFunction, result, nList.get(i), xList.get(i)));
      }
      return result;
    }

    if (n.isVariable() && x.isVariable()) {
      // 1. Primary Method: Map to ZTransform
      // mathematically: GF(expr, n, x) = ZTransform(expr, n, z) substituted with z -> 1/x
      IExpr zDummy = F.Dummy("zGF");
      IExpr zTrans = engine.evaluate(F.ZTransform(expr, n, zDummy));

      if (zTrans.isPresent() && !zTrans.isAST(S.ZTransform)) {
        IExpr sub = engine.evaluate(F.subst(zTrans, zDummy, F.Power(x, F.CN1)));

        // Use Factor to clear nested fractions like 1/(1/x) and group polynomials
        return engine.evaluate(F.Factor(F.Together(sub)));
      }

      // 2. Fallback Method: Direct Summation
      // Sum[expr * x^n, {n, 0, Infinity}]
      IExpr term = F.Times(expr, F.Power(x, n));
      IExpr sum = F.Sum(term, F.List(n, F.C0, S.Infinity));
      IExpr evSum = engine.evaluate(sum);

      if (evSum.isPresent() && !evSum.isAST(S.Sum)) {
        return engine.evaluate(evSum);
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
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions}, //
        new IExpr[] {S.$Assumptions, S.False});
    super.setUp(newSymbol);
  }


}