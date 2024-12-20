package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/** Represent Sequences in Take[] or Drop[] functions. */
public final class Sequence extends ListSizeSequence {

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
   * @param startPosition the position in <code>ast</code> inclusive, where the first ISequence
   *        specification starts.
   * @param endPosition the position in <code>ast</code> exclusive.
   * @param messageShortcut
   * @param head the header symbol we create th sequence for
   * @param engine
   * @return <code>null</code> if no <code>Sequence[]</code> can be created
   */
  public static Sequence[] createSequences(final IAST ast, final int startPosition,
      int endPosition,
      String messageShortcut, IBuiltInSymbol head, EvalEngine engine) {
    final Sequence[] sequArray = new Sequence[endPosition - startPosition];
    Sequence sequ = null;
    int j = 0;
    for (int i = startPosition; i < endPosition; i++) {
      IExpr element = ast.get(i);
      if (element.isList()) {
        if (element.argSize() < 1 || element.argSize() > 3) {
          // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
          // `1`.
          Errors.printMessage(head, "seqs", F.list(ast.arg2(), F.ZZ(i)), engine);
          return null;
        }
        sequ = new Sequence((IAST) element);
        sequArray[j++] = sequ;
        continue;
      } else if (element instanceof IInteger) {
        IInteger integerValue = (IInteger) element;
        int num = integerValue.toIntDefault();
        if (num < 0) {
          if (num == Integer.MIN_VALUE) {
            // default value for overflow from toIntDefault()
            // Cannot <messageShortcut> positions `1` through `2` in `3`.
            Errors.printMessage(ast.topHead(), messageShortcut, F.list(F.C1, ast.arg2(), ast),
                engine);
            return null;
          }
          sequ = new Sequence(num, Integer.MAX_VALUE);
          sequArray[j++] = sequ;
          continue;
        } else {
          sequ = new Sequence(1, num);
          sequArray[j++] = sequ;
          continue;
        }
      } else if (element.equals(S.All)) {
        sequ = new Sequence(1, Integer.MAX_VALUE);
        sequArray[j++] = sequ;
        continue;
      } else if (element.equals(S.None)) {
        sequ = new Sequence(1, 0);
        sequArray[j++] = sequ;
        continue;
      }
      // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
      // `1`.
      Errors.printMessage(head, "seqs", F.list(element, F.ZZ(i)), engine);
      return null;
    }
    return sequArray;
  }

  private static int getASTFrom(final IAST lst) {
    if (lst.size() > 1 && (lst.arg1().isReal())) {
      int sequ1 = lst.arg1().toIntDefault();
      if (sequ1 == Integer.MIN_VALUE) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C1));
      }
      return sequ1;
    }
    return 0;
  }

  private static int getASTTo(final IAST lst) {
    if ((lst.isAST1()) && (lst.arg1().isReal())) {
      int sequ1 = lst.arg1().toIntDefault();
      if (sequ1 == Integer.MIN_VALUE) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C1));
      }
      return sequ1;
    }
    if ((lst.size() > 2) && lst.arg2().isReal()) {
      int sequ2 = lst.arg2().toIntDefault();
      if (sequ2 == Integer.MIN_VALUE) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C2));
      }
      return sequ2;
    }
    // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
    // `1`.
    throw new ArgumentTypeException("seqs", F.list(lst, F.C2));
  }

  private static int getASTStep(final IAST lst) {
    if ((lst.size() > 3) && lst.arg3().isReal()) {
      int sequ3 = lst.arg3().toIntDefault();
      if (sequ3 == Integer.MIN_VALUE) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C3));
      }
      return sequ3;
    }
    return 1;
  }
}
