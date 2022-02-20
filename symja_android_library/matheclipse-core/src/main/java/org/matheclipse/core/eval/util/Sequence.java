package org.matheclipse.core.eval.util;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IInteger;

/** Represent Sequences in Take[] or Drop[] functions. */
public class Sequence extends ListSizeSequence {

  public Sequence(int start, int end) {
    super(start, end, 1, 1);
  }

  public Sequence(final IAST lst) {
    super(getASTFrom(lst), getASTTo(lst), getASTStep(lst), 1);
  }

  /**
   * Factory method for creating a sequences from the arguments of an <code>IAST</code> starting
   * with the argument at position <code>offset</code>.
   *
   * @param ast
   * @param offset the position in <code>ast</code>, where the first ISequence specification starts.
   * @param messageShortcut
   * @param engine
   * @return <code>null</code> if no <code>Sequence[]</code> can be created
   */
  public static Sequence[] createSequences(final IAST ast, final int offset, String messageShortcut,
      EvalEngine engine) {
    final Sequence[] sequArray = new Sequence[ast.size() - offset];
    Sequence sequ = null;
    int j = 0;
    for (int i = offset; i < ast.size(); i++) {
      if (ast.get(i).isList()) {
        sequ = new Sequence((IAST) ast.get(i));
      } else if (ast.get(i) instanceof IInteger) {
        IInteger integerValue = (IInteger) ast.get(i);
        int num = integerValue.toIntDefault();
        if (num < 0) {
          if (num == Integer.MIN_VALUE) {
            // default value for overflow from toIntDefault()
            // Cannot <messageShortcut> positions `1` through `2` in `3`.
            IOFunctions.printMessage(ast.topHead(), messageShortcut, F.list(F.C1, ast.arg2(), ast),
                engine);
            return null;
          }
          sequ = new Sequence(num, Integer.MAX_VALUE);
        } else {
          sequ = new Sequence(1, num);
        }
      } else if (ast.get(i).equals(S.All)) {
        sequ = new Sequence(1, Integer.MAX_VALUE);
      } else if (ast.get(i).equals(S.None)) {
        sequ = new Sequence(1, 0);
      }
      sequArray[j++] = sequ;
    }
    return sequArray;
  }

  private static int getASTFrom(final IAST lst) {
    if ((lst.size() > 1) && !(lst.arg1().isReal())) {
      throw new ArgumentTypeException(
          "real number expected at position 1 instead of " + lst.arg1().toString());
    }
    if (lst.size() > 1) {
      return lst.arg1().toIntDefault();
    }
    return 0;
  }

  private static int getASTTo(final IAST lst) {
    if ((lst.isAST1()) && (lst.arg1().isReal())) {
      return lst.arg1().toIntDefault();
    }
    if ((lst.size() > 2) && !(lst.arg2() instanceof IInteger)) {
      throw new ArgumentTypeException(
          "integer number expected at position 2 instead of " + lst.arg2().toString());
    }
    if (lst.size() > 2) {
      return lst.arg2().toIntDefault();
    }
    return Integer.MIN_VALUE;
  }

  private static int getASTStep(final IAST lst) {
    if ((lst.size() > 3) && !(lst.arg1().isReal())) {
      throw new ArgumentTypeException(
          "real number expected at position 1 instead of " + lst.arg1().toString());
    }
    if (lst.size() > 3) {
      return lst.arg3().toIntDefault();
    }
    return 1;
  }
}
