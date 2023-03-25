// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.qty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;

/* package */ class UnitImpl implements IUnit, Serializable {

  private static final long serialVersionUID = 2551378967100742904L;

  private final NavigableMap<String, IExpr> navigableMap;

  UnitImpl(NavigableMap<String, IExpr> navigableMap) {
    this.navigableMap = Collections.unmodifiableNavigableMap(navigableMap);
  }

  @Override // from Unit
  public IUnit negate() {
    return new UnitImpl(navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().negate(), (e1, e2) -> null, TreeMap::new)));
  }

  @Override // from Unit
  public IUnit add(IUnit unit) {
    NavigableMap<String, IExpr> map = new TreeMap<>(navigableMap);
    unit.map().forEach((k, v) -> UnitHelper.addValue(map, k, v));
    return new UnitImpl(map);
  }

  @Override // from Unit
  public IUnit multiply(IExpr factor) {
    if (factor instanceof IReal) {
      NavigableMap<String, IExpr> map = new TreeMap<>();
      navigableMap.forEach((key, value) -> {
        // TODO this may not always use the defined UnitHelper.EvalEngine
        IExpr product = S.Times.of(UnitHelper.ENGINE, value, factor);
        if (!product.isZero()) {
          map.put(key, product);
        }
      });
      return new UnitImpl(map);
    }
    return null;
  }

  @Override // from Unit
  public NavigableMap<String, IExpr> map() {
    return navigableMap;
  }

  /** ************************************************ */
  @Override // from Object
  public int hashCode() {
    return navigableMap.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof IUnit && navigableMap.equals(((IUnit) object).map());
  }

  private static String exponentString(IExpr exponent) {
    String string = exponent.toString();
    return string.equals("1") ? "" : IUnit.POWER_DELIMITER + string;
  }

  @Override // from Object
  public String toString() {
    return navigableMap.entrySet().stream() //
        .map(entry -> entry.getKey() + exponentString(entry.getValue())) //
        .collect(Collectors.joining(IUnit.JOIN_DELIMITER));
  }

  @Override
  public int compareTo(IUnit o) {
    Map<String, IExpr> map = o.map();
    if (navigableMap.size() == map.size()) {
      return toString().compareTo(o.toString());
    }
    return navigableMap.size() > map.size() ? 1 : -1;
  }
}
