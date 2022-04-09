package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.InverseZTransformRules;

public class InverseZTransform extends AbstractFunctionEvaluator implements InverseZTransformRules {
  private static final Logger LOGGER = LogManager.getLogger();

  public InverseZTransform() {}

  @Override
  public IAST getRuleAST() {
    return RULES;
  }


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
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(z), engine);
      }
      IExpr n = ast.arg3();
      if (!(n.isVariable() || n.isList())) {
        // `1` is not a valid variable.
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(n), engine);
      }
      if (!z.isList() && !n.isList()) {
        if (fx.isNumber()) {
          return F.Times(fx, F.DiscreteDelta(z));
        }
        if (fx.isAST()) {
          final IAST function = (IAST) fx;
          final IExpr header = function.head();
          if (function.isPlus()) {
            // InverseZTransform(a_+b_+c_,z_,n_) ->
            // InverseZTransform(a,z,n)+InverseZTransform(b,z,n)+InverseZTransform(c,z,n)
            IExpr result = function.mapThread(F.InverseZTransform(F.Slot1, n, z), 1);
            return engine.evaluate(result);
          }
        }
      }
    } catch (final ValidateException ve) {
      // int number validation
      LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
      return F.NIL;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }
}
