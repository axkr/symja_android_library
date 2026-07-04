package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class JacobiEpsilon extends AbstractFunctionEvaluator {

  public JacobiEpsilon() {
    super();
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr z = ast.arg1();
    IExpr m = ast.arg2();

    if (z.isZero()) {
      // JacobiEpsilon(0, m) = 0
      return F.C0;
    }

    if (m.isZero()) {
      // JacobiEpsilon(z, 0) = z
      return z;
    }

    if (m.isOne()) {
      // JacobiEpsilon(z, 1) = Tanh(z)
      // (evaluates to I*Tan(y) if z = I*y automatically through standard evaluation)
      return F.Tanh(z);
    }

    IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(z);
    if (negExpr.isPresent()) {
      // Parity transformation: JacobiEpsilon(-z, m) = -JacobiEpsilon(z, m)
      IASTAppendable result = F.ast(ast.head());
      result.append(negExpr);
      result.append(m);
      return F.Negate(result);
    }

    return F.NIL;
  }

  @Override
  public IExpr numericEval(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      IExpr z = ast.arg1();
      IExpr m = ast.arg2();

      // Numerically evaluate using the identity:
      // JacobiEpsilon(z, m) = EllipticE(JacobiAmplitude(z, m), m)
      return engine.evaluate(F.EllipticE(F.JacobiAmplitude(z, m), m));
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}