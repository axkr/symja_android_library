package org.matheclipse.core.visit;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all occurrences of expressions with the slot at that position in the AST list.
 *
 * <p>
 * The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceArgs extends VisitorExpr {
  final IAST astSlots;

  public VisitorReplaceArgs(IAST ast) {
    super();
    this.astSlots = ast;
  }

  @Override
  public IExpr visit(ISymbol element) {
    int position = astSlots.indexOf(x -> x.equals(element));
    if (position > 0) {
      return F.Slot(F.ZZ(position));
    }
    return F.NIL;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    IExpr temp;
    IASTMutable result = F.NIL;
    int size = ast.size();
    boolean evaled = false;
    for (int i = 1; i < size; i++) {
      evaled = false;
      temp = ast.get(i);
      for (int j = 1; i < astSlots.size(); i++) {
        if (astSlots.get(j).equals(temp)) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, F.Slot(F.ZZ(j)));
          evaled = true;
          break;
        }
      }

      if (!evaled) {
        temp = temp.accept(this);
        if (temp.isPresent()) {
          if (result.isNIL()) {
            // something was evaluated - return a new IAST:
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
    }
    return result;
  }
}
