package org.matheclipse.core.tensor.qty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** associates strings with instances of unit */
/* package */ enum UnitHelper {
  MEMO;
  // ---
  private static final int SIZE = 500;
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]+");
  protected static EvalEngine ENGINE = null;
  // ---
  private final Map<String, IUnit> map =
      new LinkedHashMap<String, IUnit>(SIZE * 4 / 3, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, IUnit> eldest) {
          return size() > SIZE;
        }
      };

  /**
   * @param str for instance "A*kg^-1*s^2"
   * @return unit <code>null</code> if unit couldn't be found
   */
  IUnit lookup(String str) {
    IUnit unit = map.get(str);
    if (Objects.isNull(unit)) {
      unit = create(str);
      // map.put(str, unit);
    }
    return unit;
  }

  /**
   * @param str for instance "A*kg^-1*s^2"
   * @return unit <code>null</code> if unit couldn't be found
   */
  IUnit lookupAndPutIfAbsent(String str) {
    IUnit unit = map.get(str);
    if (Objects.isNull(unit)) {
      unit = create(str);
      if (unit != null) {
        map.put(str, unit);
      }
    }
    return unit;
  }

  /* package */ static String requireValid(String key) {
    if (!PATTERN.matcher(key).matches()) throw new IllegalArgumentException(key);
    return key;
  }

  // helper function
  private static IUnit create(String string) {
    if (ENGINE == null) {
      // lazy initialization
      ENGINE = new EvalEngine(false);
    }
    IExpr value = F.NIL;
    String key = string.trim();
    NavigableMap<String, IExpr> map = new TreeMap<>();
    if (key.length() != 0) {
      // value = F.C1;
      // map.put(key, value);
      // } else {
      // key = requireValid(key);
      value = ENGINE.parse(key);
      if (value.isTimes()) {
        IAST times = (IAST) value;
        for (int i = 1; i < times.size(); i++) {
          IExpr temp = times.get(i);
          IExpr base = temp;
          IExpr exponent = F.C1;
          if (temp.isPower()) {
            base = temp.base();
            key = base.toString();
            exponent = temp.exponent();
            if (exponent.isOne()) {
              exponent = F.C1;
            }
          } else {
            key = temp.toString();
          }
          putKeyExponent(map, key, exponent);
        }
      } else if (value.isPower()) {
        IExpr base = value.base();
        key = base.toString();
        IExpr exponent = value.exponent();
        if (exponent.isOne()) {
          exponent = F.C1;
        }
        putKeyExponent(map, key, exponent);
      } else {
        map.put(key, F.C1);
      }
    }
    if (map.size() == 0) {
      return null;
    }
    return new UnitImpl(map);
  }

  private static void putKeyExponent(NavigableMap<String, IExpr> map, String key, IExpr exponent) {
    if (map.containsKey(key)) {
      IExpr sum = map.get(key).add(exponent);
      if (sum.isZero()) {
        map.remove(key); // exponents cancel
      } else {
        map.put(key, sum); // update total exponent
      }
    } else if (!exponent.isZero()) {
      map.put(key, exponent);
    }
  }
}
