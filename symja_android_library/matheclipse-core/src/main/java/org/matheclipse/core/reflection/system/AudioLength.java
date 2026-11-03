package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>AudioLength(Audio(data, ...))</code> - the number of samples of every channel of an audio
 * object whose data is a list of samples, or a list of channels which are lists of samples.
 */
public class AudioLength extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr audio = ast.arg1();
    if (audio.isAST(S.Audio) && audio.argSize() >= 1 && audio.first().isList()) {
      IAST data = (IAST) audio.first();
      if (data.argSize() > 0 && data.first().isList()) {
        // a list of channels of the same length
        int length = data.first().argSize();
        for (IExpr channel : data) {
          if (!channel.isList() || channel.argSize() != length) {
            return F.NIL;
          }
        }
        return F.ZZ(length);
      }
      return F.ZZ(data.argSize());
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
