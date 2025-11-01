package org.matheclipse.core.compile;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A stack-based variable manager for mapping IExpr keys to String variable names.
 */
public class VariableManager implements Function<IExpr, String> {
  ArrayDeque<Map<IExpr, String>> varStack;

  public void put(IExpr key, String variable) {
    varStack.peek().put(key, variable);
  }

  public Map<IExpr, String> peek() {
    return varStack.peek();
  }

  public void push() {
    Map<IExpr, String> map = new HashMap<IExpr, String>();
    varStack.push(map);
  }

  public void push(Map<IExpr, String> map) {
    varStack.push(map);
  }

  public Map<IExpr, String> pop() {
    return varStack.pop();
  }

  public VariableManager(Map<IExpr, String> map) {
    varStack = new ArrayDeque<Map<IExpr, String>>();
    varStack.add(map);
  }

  @Override
  public String apply(IExpr expr) {
    for (Iterator<Map<IExpr, String>> iterator = varStack.descendingIterator(); iterator
        .hasNext();) {
      Map<IExpr, String> map = iterator.next();
      String temp = map.get(expr);
      if (temp != null) {
        return temp;
      }
    }
    return null;
  }
}
