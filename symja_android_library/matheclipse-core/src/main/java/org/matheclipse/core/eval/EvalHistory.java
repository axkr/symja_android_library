package org.matheclipse.core.eval;

import java.io.Serializable;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

/** Contains the last <code>n</code> entries of the calculation history. */
public class EvalHistory implements Serializable {

  private static final long serialVersionUID = -5542189869048554333L;

  Int2ObjectAVLTreeMap<IExpr> fInHistory;
  Int2ObjectAVLTreeMap<IExpr> fOutHistory;

  private short fHistoryLength;
  private int fIndex;

  int[] historyIndices;
  /** The overall line counter for all calculations. */
  private int fLine;

  /**
   * Contains the last <code>n</code> output entries of the calculation history.
   *
   * @param historyLength maximum capacity of the last calculated entries which the history could
   *     contain.
   */
  public EvalHistory(short historyLength) {
    fHistoryLength = historyLength;
    historyIndices = new int[fHistoryLength + 1];
    fInHistory = new Int2ObjectAVLTreeMap<IExpr>();
    fOutHistory = new Int2ObjectAVLTreeMap<IExpr>();
    fIndex = 0;
    fLine = 0;
  }

  private void reset(short historyLength) {
    fHistoryLength = historyLength;
    historyIndices = new int[fHistoryLength + 1];
    fInHistory = new Int2ObjectAVLTreeMap<IExpr>();
    fOutHistory = new Int2ObjectAVLTreeMap<IExpr>();
    fIndex = 0;
    fLine = 0;
  }

  /**
   * Add a new calculation to the history. Only the last <code>N</code> calculations are stored in
   * the history. It may be that the last <code>N+1</code> entry will be overriden by the new entry.
   *
   * @param inExpr the input expression
   * @param outExpr the output expression, which is the result of the <code>inExpr</code>
   */
  public void addInOut(IExpr inExpr, IExpr outExpr) {
    int deletePosition = fIndex - fHistoryLength + 1;
    if (deletePosition < 0) {
      deletePosition = fHistoryLength + 1 + deletePosition;
    }
    int deleteLine = historyIndices[deletePosition];
    if (deleteLine != Integer.MIN_VALUE) {
      fInHistory.remove(deleteLine);
      fOutHistory.remove(deleteLine);
      historyIndices[deletePosition] = Integer.MIN_VALUE;
    }

    fLine++;
    fInHistory.put(fLine, inExpr);
    fOutHistory.put(fLine, outExpr);
    ++fIndex;
    if (fIndex >= historyIndices.length) {
      fIndex = 1;
    }
    historyIndices[fIndex] = fLine;
  }

  public IAST definitionIn() {
    ObjectSortedSet<Int2ObjectMap.Entry<IExpr>> set = fInHistory.int2ObjectEntrySet();
    return defintion(set, S.In, true);
  }

  public IAST definitionOut() {
    ObjectSortedSet<Int2ObjectMap.Entry<IExpr>> set = fOutHistory.int2ObjectEntrySet();
    return defintion(set, S.Out, false);
  }

  private IAST defintion(
      ObjectSortedSet<Int2ObjectMap.Entry<IExpr>> set, IBuiltInSymbol symbol, boolean setDelayed) {
    IASTAppendable result = F.ListAlloc(set.size());

    for (Int2ObjectMap.Entry<IExpr> entry : set) {
      final IAST line = F.unaryAST1(symbol, F.ZZ(entry.getIntKey()));
      if (setDelayed) {
        result.append(F.SetDelayed(line, entry.getValue()));
      } else {
        result.append(F.Set(line, entry.getValue()));
      }
    }
    return result;
  }

  /**
   * Get the history length. -1 means Infinity.
   *
   * @return
   */
  public short getHistoryLength() {
    return fHistoryLength;
  }

  public IExpr getIn(int index) {
    int line = historyIndices[fIndex];
    if (index < 0) {
      line = line + index + 1;
    } else {
      line = index;
    }
    IExpr value = fInHistory.get(line);
    return value == null ? F.NIL : value;
  }

  /**
   * Get the last <code>In[], Out[]</code> evaluation counter (i.e. <code>$Line</code>).
   *
   * @return
   */
  public int getLineCounter() {
    return fLine + 1;
  }

  /**
   * Get the history entry at the given <code>index</code>. If the <code>index</code> is positive it
   * ranges from <code>1</code> to <code>fAllEntriesCounter</code>. If the <code>index</code> is
   * negative it ranges from <code>-1</code> to <code>-fAllEntriesCounter</code>.
   *
   * @param index
   * @return <code>F.NIL</code> if the output history isn't available for the given <code>index
   *     </code>.
   */
  public IExpr getOut(int index) {
    if (index > fLine || index == 0) {
      return F.NIL;
    }
    IExpr value;
    if (index < 0) {
      value = fOutHistory.get(fLine + index + 1);
    } else {
      value = fOutHistory.get(index);
    }
    return value == null ? F.NIL : value;
  }

  public void resetLineCounter(int line) {
    fLine = line - 1;
  }

  public void setHistoryLength(short historyLength) {
    reset(historyLength);
  }

  /**
   * Get the size of the last history entry array.
   *
   * @return
   */
  public int size() {
    return fOutHistory.size();
  }
}
