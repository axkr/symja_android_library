package org.matheclipse.core.tensor.qty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** associates strings with instances of unit */
/* package */ enum UnitHelper {
  MEMO;

  // ---
  private static final int SIZE = 500;
  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]+");
  static EvalEngine ENGINE = null;
  // ---
  private final Map<String, IUnit> map =
      new LinkedHashMap<String, IUnit>(SIZE * 4 / 3, 0.75f, true) {
        private static final long serialVersionUID = -5110699298658386612L;

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
    if (unit == null) {
      unit = create(str);
    }
    return unit;
  }

  /**
   * @param str for instance "A*kg^-1*s^2"
   * @return unit <code>null</code> if unit couldn't be found
   */
  IUnit lookupAndPutIfAbsent(String str) {
    return map.computeIfAbsent(str, UnitHelper::create);
  }

  /* package */ static String requireValid(String key) {
    if (!PATTERN.matcher(key).matches()) {
      throw new IllegalArgumentException(key);
    }
    return key;
  }

  // helper function
  private static IUnit create(String string) {
    if (ENGINE == null) {
      // lazy initialization
      ENGINE = new EvalEngine(false);
    }
    String key = string.strip();
    NavigableMap<String, IExpr> map = new TreeMap<>();
    if (!key.isEmpty()) {
      IExpr value = ENGINE.parse(key);
      if (value.isTimes()) {
        for (IExpr factor : (IAST) value) {
          if (factor.isPower()) {
            putPowerExponent(map, factor);
          } else {
            putKeyExponent(map, factor, F.C1);
          }
        }
      } else if (value.isPower()) {
        putPowerExponent(map, value);
      } else {
        map.put(key, F.C1);
      }
    }
    return map.isEmpty() ? null : new UnitImpl(map);
  }

  private static void putPowerExponent(NavigableMap<String, IExpr> map, IExpr power) {
    IExpr exponent = power.exponent();
    if (exponent.isOne()) {
      exponent = F.C1;
    }
    putKeyExponent(map, power.base(), exponent);
  }

  private static void putKeyExponent(NavigableMap<String, IExpr> map, IExpr key, IExpr exponent) {
    if (!exponent.isZero()) {
      addValue(map, key.toString(), exponent);
    }
  }

  static void addValue(Map<String, IExpr> map, String key, IExpr value) {
    map.merge(key, value, (oldValue, newValue) -> {
      // TODO this may not always use the defined UnitHelper.EvalEngine
      IExpr sum = S.Plus.of(ENGINE, oldValue, newValue);
      return sum.isZero() ? null : sum; // update exponent or remove if exponents cancel out
    });
  }
}
