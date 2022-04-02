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
import org.matheclipse.core.reflection.system.rules.ZTransformRules;

public class ZTransform extends AbstractFunctionEvaluator implements ZTransformRules {
  private static final Logger LOGGER = LogManager.getLogger();

  public ZTransform() {}

  @Override
  public IAST getRuleAST() {
    return RULES;
  }


  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      final IExpr fx = ast.arg1();
      if (fx.isIndeterminate()) {
        return S.Indeterminate;
      }
      IExpr n = ast.arg2();
      if (!(n.isVariable() || n.isList())) {
        // `1` is not a valid variable.
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(n), engine);
      }
      IExpr z = ast.arg3();
      if (!(z.isVariable() || z.isList())) {
        // `1` is not a valid variable.
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(z), engine);
      }
      if (!n.isList() && !z.isList()) {
        if (fx.isFree(n, true) && fx.isFree(z, true)) {
          // (fx*z)/(-1 + z)
          return F.Times(fx, z, F.Power(F.Plus(F.CN1, z), F.CN1));
        }
        if (fx.equals(n)) {
          // z/(-1 + z)^2
          return F.Times(z, F.Power(F.Plus(F.CN1, z), F.CN2));
        }
        if (fx.isAST()) {
          final IAST function = (IAST) fx;
          final IExpr header = function.head();
          if (function.isPlus()) {
            // ZTransform(a_+b_+c_,n_,z_) -> ZTransform(a,n,z)+ZTransform(b,n,z)+ZTransform(c,n,z)
            IExpr result = function.mapThread(F.ZTransform(F.Slot1, n, z), 1);
            return engine.evaluate(result);
          } else if (function.isTimes()) {
            int indx = function.indexOf(x -> x.isFree(n, true));
            if (indx > 0) {
              IExpr arg = function.get(indx);
              IExpr rest = function.removeAtCopy(indx);
              return F.Times(arg, F.ZTransform(rest, n, z));
            }
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
    return ARGS_1_3;
  }
}
