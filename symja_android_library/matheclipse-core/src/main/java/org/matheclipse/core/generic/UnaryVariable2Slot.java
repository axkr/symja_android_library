package org.matheclipse.core.generic;

import static org.matheclipse.core.expression.F.Slot;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class UnaryVariable2Slot implements Function<IExpr, IExpr> {
  private final Map<IExpr, IExpr> fMap;

  private final Collection<IExpr> fVariableList;

  private int fSlotCounter;

  public UnaryVariable2Slot(final Map<IExpr, IExpr> map, final Collection<IExpr> variableList) {
    fMap = map;
    fVariableList = variableList;
    fSlotCounter = 0;
  }

  /**
   * For every given argument return the associated unique slot from the internal Map
   *
   * @return <code>F.NIL</code>
   */
  @Override
  public IExpr apply(final IExpr firstArg) {
    if (firstArg.isVariable()) {
      if ((firstArg.toString().charAt(0) >= 'A') && (firstArg.toString().charAt(0) <= 'Z')) {
        if (((ISymbol) firstArg).hasOrderlessAttribute()) {
          return F.NIL;
        }
        if (firstArg.equals(S.Print)) {
          // Print function has "side-effects"
          return F.NIL;
        }
        // probably a built-in function
        return firstArg;
      }
      if (Config.SERVER_MODE && (firstArg.toString().charAt(0) == '$')) {
        // a user-modifiable variable in server mode is not allowed
        return F.NIL;
      }
    } else {
      // not a symbol
      return F.NIL;
    }

    // a variable which could be replaced by a slot:
    IExpr result = fMap.get(firstArg);
    if (result == null) {
      result = Slot(F.ZZ(++fSlotCounter));
      fMap.put(firstArg, result);
      fVariableList.add(firstArg);
    }
    return result;
  }
}
