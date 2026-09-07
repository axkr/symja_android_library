package org.matheclipse.core.visit;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;

/**
 * Replace all occurrences of Slot[] expressions.
 *
 * <p>
 * The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceSlots extends VisitorExpr {
  final IAST astSlots;

  /**
   * The number of the first integer <code>Slot</code> which could not be filled, or
   * {@link F#NIL} if every slot was fillable. Recorded so that the caller can report it; the
   * visitor itself never emits a message, because it is also used to fill templates where an
   * unfilled slot is not an error.
   *
   * <p>
   * This makes the visitor stateful, so a fresh one is needed per substitution. All entry points in
   * {@link org.matheclipse.core.eval.util.PureFunctions} construct one.
   */
  private IExpr unfillableSlot = F.NIL;

  public VisitorReplaceSlots(IAST ast) {
    super();
    this.astSlots = ast;
  }

  /**
   * The number of the first integer <code>Slot</code> which could not be filled from the arguments,
   * or {@link F#NIL} if there was none. Named and string slots are not reported: an absent key is a
   * normal outcome for the <code>Dataset</code> and <code>Association</code> forms.
   */
  public IExpr getUnfillableSlot() {
    return unfillableSlot;
  }

  private IExpr getSlot(IInteger ii) {
    int i = ii.toIntDefault();
    if (i >= 0 && i < astSlots.size()) {
      return astSlots.get(i);
    }
    if (unfillableSlot.isNIL()) {
      unfillableSlot = ii;
    }
    return F.NIL;
  }

  private IExpr getSlot(IStringX str) {
    IExpr arg1 = astSlots.arg1();
    if (arg1.isDataset()) {
      return ((IASTDataset) arg1).getValue(str);
    }
    if (arg1.isAST(S.Association)) {
      arg1 = EvalEngine.get().evaluate(arg1);
    }
    if (arg1.isAssociation()) {
      return ((IAssociation) arg1).getValue(str);
    }
    return F.NIL;
  }

  /**
   * A <code>SlotSequence(n)</code> can be filled when <code>1 &lt;= n &lt;= astSlots.size()</code>.
   * The upper end is inclusive: <code>n == astSlots.size()</code> names the arguments after the
   * last one, which is the empty sequence. This is the single in-range test; both the standalone
   * and the spliced form use it, so they cannot disagree.
   */
  private boolean isFillableSlotSequence(int n) {
    return n >= 1 && n <= astSlots.size();
  }

  private IExpr getSlotSequence(IInteger ii) {
    int i = ii.toIntDefault();
    if (isFillableSlotSequence(i)) {
      return F.mapRange(S.Sequence, i, astSlots.size(), j -> astSlots.get(j));
    }
    return F.NIL;
  }

  /**
   * Splice the arguments from <code>startSlot</code> onwards into <code>ast</code> at
   * <code>pos</code>.
   *
   * @param startSlot must satisfy {@link #isFillableSlotSequence(int)}
   * @return the position just after the spliced arguments
   */
  private int getSlotSequence(IASTAppendable ast, int pos, int startSlot) {
    for (int j = startSlot; j < astSlots.size(); j++) {
      ast.append(pos++, astSlots.get(j));
    }
    return pos;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (ast.size() == 2) {
      final IExpr arg1 = ast.arg1();
      if (ast.isSlot()) {
        if (arg1.isInteger()) {
          return getSlot((IInteger) arg1);
        } else if (arg1.isString()) {
          return getSlot((IStringX) arg1);
        }
      } else if (ast.isSlotSequence() && arg1.isInteger()) {
        return getSlotSequence((IInteger) arg1);
      }
    }
    return visitAST(ast);
  }

  @Override
  protected IExpr visitAST(IAST ast) {
    // One pass, one source index and one destination index. The previous two-loop form advanced the
    // source index only when a SlotSequence was in range, so an out-of-range one was read twice and
    // removed twice, which threw IndexOutOfBoundsException.
    IASTAppendable result = F.NIL;
    int destination = 0;
    final int size = ast.size();
    for (int source = 0; source < size; source++) {
      IExpr arg = ast.get(source);
      if (arg.isPureFunction()) {
        // a nested pure function binds its own slots
        destination++;
        continue;
      }
      if (arg.isSlotSequence()) {
        int sequ = ((IAST) arg).arg1().toIntDefault();
        if (!isFillableSlotSequence(sequ)) {
          // leave an unfillable ##n in place, as the standalone form does
          destination++;
          continue;
        }
        if (result.isNIL()) {
          result = ast.copyAppendable(astSlots.argSize());
        }
        result.remove(destination);
        destination = getSlotSequence(result, destination, sequ);
        continue;
      }
      IExpr temp = arg.accept(this);
      if (temp.isPresent()) {
        if (result.isNIL()) {
          result = ast.copyAppendable(astSlots.argSize());
        }
        result.set(destination, temp);
      }
      destination++;
    }
    return result;
  }
}
