package org.matheclipse.core.eval.steps;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * The LocaleMap class provides a mapping between locale keys and RuleDescription objects. It allows
 * storing and retrieving RuleDescription objects based on locale keys.
 */
public class LocaleMap {

  private final static Map<String, RuleDescription> LOCALE_MAP =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  static {
    String en = Locale.ENGLISH.getLanguage();
    // System.out.println(en);
    put(en, RuleDescription.EN_RULES);
  }

  public LocaleMap() {}

  public static boolean containsKey(String key) {
    return LOCALE_MAP.containsKey(key);
  }

  public static RuleDescription get(String key) {
    return LOCALE_MAP.get(key);
  }

  public static RuleDescription put(String key, RuleDescription value) {
    return LOCALE_MAP.put(key, value);
  }

}

