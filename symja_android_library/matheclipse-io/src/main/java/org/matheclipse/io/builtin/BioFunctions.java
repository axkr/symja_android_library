package org.matheclipse.io.builtin;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.io.expression.data.BioSequenceExpr;

public class BioFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BioSequence.setEvaluator(new BioSequence());
      S.BioSequenceQ.setEvaluator(new BioSequenceQ());
    }
  }

  private static class BioSequence extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString() && ast.arg2().isString()) {
        String typeStr = ((IStringX) ast.arg1()).toString();
        String sequenceStr = ((IStringX) ast.arg2()).toString();
        try {
          if (typeStr.equals("DNA")) {
            return BioSequenceExpr.newDNASequence(sequenceStr);

          } else if (typeStr.equals("RNA")) {
            return BioSequenceExpr.newRNASequence(sequenceStr);
          }
        } catch (CompoundNotFoundException e) {
          // IOFunctions.printMessage(ast.topHead(), "", ast, engine);
          return F.NIL;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class BioSequenceQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof BioSequenceExpr) {
        return S.True;
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private BioFunctions() {}
}
