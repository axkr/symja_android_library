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
  public static Sequence[] createSequences(final IAST ast, final int startPosition, int endPosition,
      String messageShortcut, IBuiltInSymbol head, EvalEngine engine) {
    final Sequence[] sequArray = new Sequence[endPosition - startPosition];
    Sequence sequ = null;
    int j = 0;
    for (int i = startPosition; i < endPosition; i++) {
      IExpr element = integerSpecification(ast.get(i));
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
          if (F.isNotPresent(num)) {
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
      } else if (element.isAST(S.Span, 3) || element.isAST(S.Span, 4)) {
        // `Span(m, n)` and `Span(m, n, s)` denote the same range as the list specifications
        // `{m, n}` and `{m, n, s}`, so `Take(list, 2;;4)` is `Take(list, {2, 4})`.
        sequ = new Sequence(spanToListSpec((IAST) element));
        sequArray[j++] = sequ;
        continue;
      } else if (element == S.All) {
        sequ = new Sequence(1, Integer.MAX_VALUE);
        sequArray[j++] = sequ;
        continue;
      } else if (element == S.None) {
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

  /**
   * A real number with an integer value in a sequence specification stands for that integer:
   * <code>Take(list, 3.0)</code> is <code>Take(list, 3)</code> and <code>Take(list, {1.0, 5.0,
   * 2.0})</code> is <code>Take(list, {1, 5, 2})</code>. A real number which is not an integer, like
   * the <code>2.5</code> in <code>Take(list, 2.5)</code>, and any other expression are returned
   * unchanged.
   *
   * @param spec a sequence specification or one of the numbers of a list specification
   */
  public static IExpr integerSpecification(final IExpr spec) {
    if (spec.isList()) {
      // the list is copied only if one of its numbers changes
      return ((IAST) spec).map(x -> {
        IExpr integer = integerValuedReal(x);
        return integer == x ? F.NIL : integer;
      }, 1);
    }
    return integerValuedReal(spec);
  }

  /**
   * The integer value of a real number like <code>3.0</code>; any other expression is returned
   * unchanged.
   */
  public static IExpr integerValuedReal(final IExpr expr) {
    if (expr.isReal() && !expr.isRational() && expr.isNumIntValue()) {
      int value = expr.toIntDefault();
      if (!F.isNotPresent(value)) {
        return F.ZZ(value);
      }
    }
    return expr;
  }

  /**
   * The <code>{m, n}</code> or <code>{m, n, s}</code> list specification a <code>Span</code>
   * denotes.
   *
   * <p>
   * An open bound is written <code>All</code>: <code>2;;</code> parses as
   * <code>Span(2, All)</code>, which runs to the last element and so is <code>-1</code> here.
   */
  public static IAST spanToListSpec(final IAST span) {
    IExpr from = span.arg1() == S.All ? F.C1 : span.arg1();
    IExpr to = span.arg2() == S.All ? F.CN1 : span.arg2();
    return span.isAST3() ? F.List(from, to, span.arg3()) : F.List(from, to);
  }

  private static int getASTFrom(final IAST lst) {
    if (lst.size() > 1 && (lst.arg1().isReal())) {
      int sequ1 = lst.arg1().toIntDefault();
      if (F.isNotPresent(sequ1)) {
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
      if (F.isNotPresent(sequ1)) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C1));
      }
      return sequ1;
    }
    if ((lst.size() > 2) && lst.arg2().isReal()) {
      int sequ2 = lst.arg2().toIntDefault();
      if (F.isNotPresent(sequ2)) {
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
      if (F.isNotPresent(sequ3)) {
        // Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s} expected at position `2` in
        // `1`.
        throw new ArgumentTypeException("seqs", F.list(lst, F.C3));
      }
      return sequ3;
    }
    return 1;
  }
}
