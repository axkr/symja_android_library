package org.matheclipse.core.eval.util;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorReplaceArgs;
import org.matheclipse.core.visit.VisitorReplaceSlots;

public class Lambda {
  private Lambda() {}

  /**
   * Replace all occurrences of Slot[&lt;index&gt;] expressions with the expression at the
   * appropriate <code>index</code> in the given <code>slotsList</code>.
   *
   * <p>
   * <b>Note:</b> If a slot value is <code>null</code> the Slot will not be substituted.
   *
   * @param expr
   * @param slotsList the values for the slots.
   * @return <code>F.NIL</code> if no substitution occurred.
   */
  public static IExpr replaceSlots(IExpr expr, final IAST slotsList) {
    return expr.accept(new VisitorReplaceSlots(slotsList));
  }

  public static IExpr replaceSlotsOrElse(IExpr expr, final IAST slotsList, IExpr elseExpr) {
    IExpr temp = expr.accept(new VisitorReplaceSlots(slotsList));
    return temp.orElse(elseExpr);
  }

  /**
   * Replace all occurrences of the expressions in the given list with the appropriate <code>
   * Slot(index)</code>.
   *
   * @param expr
   * @param exprsList the values for the slots.
   * @return <code>F.NIL</code> if no substitution occurred.
   */
  public static IExpr replaceArgs(IExpr expr, final IAST exprsList) {
    return expr.accept(new VisitorReplaceArgs(exprsList));
  }

  /**
   * Append each argument of <code>ast</code> to <code>result</code> by applying the given <code>
   * function</code> to each argument.
   *
   * @param ast
   * @param result
   * @param function
   * @return
   */
  public static IAST forEachAppend(IAST ast, IASTAppendable result,
      Function<IExpr, IExpr> function) {
    int size = ast.size();
    for (int i = 1; i < size; i++) {
      result.append(function.apply(ast.get(i)));
    }
    return result;
  }

  /**
   * Consume each argument of <code>ast</code> which fulfills the <code>predicate</code>.
   *
   * @param ast
   * @param predicate
   * @param consumer
   * @return
   */
  public static void forEach(IAST ast, Predicate<IExpr> predicate, Consumer<IExpr> consumer) {
    int size = ast.size();
    for (int i = 1; i < size; i++) {
      IExpr t = ast.get(i);
      if (predicate.test(t)) {
        consumer.accept(t);
      }
    }
  }

  /**
   * Compare the arguments pairwise with the <code>stopPredicate</code>. If the predicate gives
   * <code>true</code> return the <code>stopExpr</code>. If the <code>stopPredicate</code> gives
   * false for each pairwise comparison return the <code>resultExpr</code>
   *
   * @param ast
   * @param stopPredicate
   * @param stopExpr
   * @param resultExpr
   * @return
   * @deprecated use IAST#existsLeft()
   */
  @Deprecated
  public static IExpr existsLeft(IAST ast, BiPredicate<IExpr, IExpr> stopPredicate, IExpr stopExpr,
      IExpr resultExpr) {
    int size = ast.size();
    for (int i = 2; i < size; i++) {
      if (stopPredicate.test(ast.get(i - 1), ast.get(i))) {
        return stopExpr;
      }
    }
    return resultExpr;
  }

  /**
   * Tests each argument with the <code>stopPredicate</code>. If the predicate gives <code>true
   * </code> return <code>true</code>. If the <code>stopPredicate</code> gives <code>false</code>
   * for each test return <code>false</code>
   *
   * @param ast
   * @param stopPredicate
   * @param offset the offset where to start testing the predicate
   * @return
   * @deprecated use IAST#exists()
   */
  @Deprecated
  public static boolean exists(IAST ast, Predicate<IExpr> stopPredicate, int offset) {
    int size = ast.size();
    for (int i = offset; i < size; i++) {
      if (stopPredicate.test(ast.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests each argument with the <code>stopPredicate</code>. If the <code>stopPredicate</code>
   * gives <code>false</code> return <code>false</code>. If the <code>stopPredicate</code> gives
   * <code>true</code> for each test, return <code>true</code>
   *
   * @param ast
   * @param stopPredicate
   * @param offset the offset where to start testing the predicate
   * @return
   * @deprecated use IAST#forAll()
   */
  @Deprecated
  public static boolean forAll(IAST ast, Predicate<IExpr> stopPredicate, int offset) {
    int size = ast.size();
    for (int i = offset; i < size; i++) {
      if (!stopPredicate.test(ast.get(i))) {
        return false;
      }
    }
    return true;
  }

  private static IExpr testMap(IAST ast, Predicate<IExpr> predicate,
      Function<IExpr, IExpr> function) {
    IASTAppendable result = F.NIL;
    int size = ast.size();
    for (int i = 1; i < size; i++) {
      IExpr temp = ast.get(i);
      if (predicate.test(temp)) {
        if (!result.isPresent()) {
          result = ast.copyAppendable();
        }
        temp = function.apply(temp);
        if (temp != null) {
          result.set(i, function.apply(temp));
        }
        continue;
      }
    }

    return result;
  }

  private static IExpr testMap2(IAST list, Predicate<IExpr> predicate,
      Function<IExpr, IExpr> function1, Function<IExpr, IExpr> function2) {
    IASTAppendable result = F.NIL;
    int size = list.size();
    for (int i = 1; i < size; i++) {
      IExpr temp = list.get(i);
      if (predicate.test(temp)) {
        if (!result.isPresent()) {
          result = list.copyAppendable();
          for (int j = 0; j < i; j++) {
            result.set(j, function2.apply(temp));
          }
        }
        result.set(i, function1.apply(temp));
        continue;
      }
      if (result != null) {
        result.set(i, function2.apply(temp));
      }
    }

    return result;
  }
}
