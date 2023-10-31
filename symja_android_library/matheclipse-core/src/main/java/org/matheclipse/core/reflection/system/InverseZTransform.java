package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class InverseZTransform extends AbstractFunctionEvaluator {

  public InverseZTransform() {}


  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // see http://www.reduce-algebra.com/docs/ztrans.pdf
    try {
      final IExpr fx = ast.arg1();
      if (fx.isIndeterminate()) {
        return S.Indeterminate;
      }
      IExpr z = ast.arg2();
      if (!(z.isVariable() || z.isList())) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(z), engine);
      }
      IExpr n = ast.arg3();
      if (!(n.isVariable() || n.isList())) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(n), engine);
      }
      if (!z.isList() && !n.isList()) {
        if (fx.isNumber()) {
          return F.Times(fx, F.DiscreteDelta(z));
        }
        if (fx.isPlus()) {
          // InverseZTransform(a_+b_+c_,z_,n_) ->
          // InverseZTransform(a,z,n)+InverseZTransform(b,z,n)+InverseZTransform(c,z,n)
          return fx.mapThread(F.InverseZTransform(F.Slot1, n, z), 1) //
              .eval(engine);
        }
      }
    } catch (final ValidateException ve) {
      // int number validation
      return Errors.printMessage(S.InverseZTransform, ve, engine);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
