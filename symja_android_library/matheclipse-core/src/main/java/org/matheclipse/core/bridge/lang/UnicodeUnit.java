// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.io.ResourceData;
import org.matheclipse.core.tensor.qty.IUnit;
import org.matheclipse.core.tensor.qty.UnitSystem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* package */ enum UnicodeUnit {
  INSTANCE;

  /**
   * @param unit
   * @return string expression of unit using unicode characters
   */
  public static String toString(IUnit unit) {
    return UnicodeUnit.cache.getIfPresent(unit);
  }

  public static Cache<IUnit, String> cache = CacheBuilder.newBuilder().maximumSize(512).build();
  // private final Cache<IUnit, String> cache = Caffeine.newBuilder()
  // .expireAfterWrite(1, TimeUnit.MINUTES)
  // .maximumSize(512)
  // .build();

  private final Map<String, String> terminators = new HashMap<>();
  private final Map<String, String> exponents = new HashMap<>();
  private final Map<String, String> micros = new HashMap<>();

  private UnicodeUnit() {
    terminators.put("degC", "\u2103"); // unicode oC
    terminators.put("K", "\u212a"); // unicode K
    for (String unit : UnitSystem.SI().map().keySet())
      if (unit.endsWith("Ohm")) {
        String prefix = unit.substring(0, unit.length() - 3);
        terminators.put(unit, prefix + '\u2126'); // unicode Omega
      }
    // ---
    terminators.put("EUR", "\u20ac"); // unicode EUR
    terminators.put("GBP", "\u00a3"); // unicode GBP
    terminators.put("USD", "$"); // unicode USD
    terminators.put("JPY", "\u00a5"); // unicode JPY
    // ---
    exponents.put("1", "");
    exponents.put("-1", "\u207b\u00b9");
    exponents.put("2", "\u00b2");
    exponents.put("-2", "\u207b\u00b2");
    exponents.put("3", "\u00b3");
    exponents.put("-3", "\u207b\u00b3");
    // ---
    Properties properties = ResourceData.properties("/unit/si.properties");
    Set<String> set = properties.stringPropertyNames();
    for (String key : set)
      if (key.startsWith("_")) { // inflator
        String atom = key.substring(1);
        String uKey = "u" + atom;
        if (!properties.containsValue(uKey)) {
          micros.put(uKey, '\u03BC' + terminate(atom));
        }
      }
  }

  private String build(IUnit unit) {
    Map<String, IExpr> map = unit.map();
    List<Entry<String, IExpr>> list = map.entrySet().stream() //
        .filter(entry -> entry.getValue().isNegative()) //
        .collect(Collectors.toList());
    if (list.size() == 1 && 1 < map.size()) {
      Entry<String, IExpr> entry = list.iterator().next();
      IUnit den = IUnit.of(entry.getKey() + IUnit.POWER_DELIMITER + entry.getValue().negate());
      return toString(unit.add(den).map()) + "/" + toString(den.map());
    }
    return toString(map);
  }

  /**
   * @param map
   * @return for instance "m*s^-2"
   */
  private String toString(Map<String, IExpr> map) {
    return map.entrySet().stream() //
        .map(entry -> atomString(entry.getKey()) + exponentString(entry.getValue().toString())) //
        .collect(Collectors.joining(IUnit.JOIN_DELIMITER)); // delimited by '*'
  }

  private String atomString(String atom) {
    return micros.getOrDefault(atom, terminate(atom));
  }

  private String terminate(String atom) {
    return terminators.getOrDefault(atom, atom);
  }

  private String exponentString(String string) {
    return exponents.getOrDefault(string, IUnit.POWER_DELIMITER + string);
  }
}
