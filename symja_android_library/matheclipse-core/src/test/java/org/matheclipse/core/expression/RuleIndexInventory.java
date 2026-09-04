package org.matheclipse.core.expression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.patternmatching.ruleindex.RuleFeatureIndex;
import org.matheclipse.core.reflection.system.Integrate;

/**
 * Diagnostic inventory: which symbols and which {@link Matcher} based rule sets are large enough to
 * get a {@link RuleFeatureIndex}, and how many symbols the index can discriminate on.
 *
 * <p>
 * The class name deliberately does not match the surefire includes, so this runs only on demand:
 *
 * <pre>
 * mvn -pl matheclipse-core test -Dtest=RuleIndexInventory
 * </pre>
 *
 * <p>
 * Re-run it after a Rubi upgrade or after changing {@link Config#RULE_INDEX_MIN_RULES}.
 */
public class RuleIndexInventory {

  /** Expressions which force the lazily built Matcher based rule sets to be created. */
  private static final String[] TRIGGERS = {"Integrate(x^2*Sin(3*x), x)",
      "FunctionExpand(Binomial(n, 3))", "Sum(i^2, {i, 1, n})", "Product(i, {i, 1, n})",
      "SeriesCoefficient(Sin(x), {x, 0, 3})", "Eliminate({x==2+y, y==z}, y)",
      "D(Sin(x)*Log(x), x)", "Limit(Sin(x)/x, x->0)", "Simplify(Sin(x)^2+Cos(x)^2)",
      "Refine(Abs(x), x>0)", "PowerExpand(Log(a*b))", "TrigToExp(Tan(x))",
      "Derivative(1)[Sin][x]", "Together(1/a+1/b)", "InverseLaplaceTransform(1/s, s, t)",
      "LaplaceTransform(t^2, t, s)", "N(Zeta(3))", "Solve(x^2==4, x)"};

  @Test
  public void inventory() throws Exception {
    F.initSymja();
    Integrate.CONST.await();
    Config.RULE_INDEX_MIN_RULES = 16;
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    for (String trigger : TRIGGERS) {
      try {
        engine.evaluate(engine.parse(trigger));
      } catch (RuntimeException rex) {
        // a failing trigger still initialized what it touched
      }
    }

    System.out.println("=== symbols with pattern down-rules (threshold "
        + Config.RULE_INDEX_MIN_RULES + ") ===");
    System.out.println(String.format("%-28s %8s %9s %9s", "symbol", "rules", "indexed", "features"));
    List<String> below = new ArrayList<String>();
    int indexedCount = 0;
    // fall through to the assertions at the end of the method
    for (IBuiltInSymbol symbol : S.BUILT_IN_SYMBOLS) {
      if (symbol == null) {
        continue;
      }
      RulesData rulesData = symbol.getRulesData();
      if (rulesData == null) {
        continue;
      }
      int size = rulesData.patternDownRulesSize();
      if (size == 0) {
        continue;
      }
      if (size >= Config.RULE_INDEX_MIN_RULES) {
        RuleFeatureIndex index = rulesData.diagnosticRuleIndex();
        indexedCount++;
        System.out.println(String.format("%-28s %8d %9s %9s", symbol.toString(), size,
            index != null ? "yes" : "NO",
            index != null ? Integer.toString(index.featureCount()) : "-"));
      } else if (size >= 4) {
        below.add(symbol.toString() + "(" + size + ")");
      }
    }
    System.out.println("indexed symbols: " + indexedCount);
    System.out.println();
    System.out.println("=== below threshold, 4..15 rules (linear scan) ===");
    System.out.println(String.join(" ", below));

    System.out.println();
    System.out.println("=== Matcher based rule sets ===");
    System.out.println(String.format("%-46s %8s %9s %9s", "holder", "rules", "indexed", "features"));
    reportMatcher("Integrate.POWER_TIMES_FUNCTION_MATCHER",
        "org.matheclipse.core.reflection.system.Integrate", "POWER_TIMES_FUNCTION_MATCHER");
    reportSupplier("FunctionExpand.LAZY_MATCHER",
        "org.matheclipse.core.reflection.system.FunctionExpand", "LAZY_MATCHER");
    reportSupplier("Sum.MATCHER1", "org.matheclipse.core.reflection.system.Sum", "MATCHER1");
    reportSupplier("Product.MATCHER1", "org.matheclipse.core.reflection.system.Product",
        "MATCHER1");
    reportSupplier("SeriesFunctions.SeriesCoefficient.MATCHER1",
        "org.matheclipse.core.builtin.SeriesFunctions$SeriesCoefficient", "MATCHER1");

    RulesData integrateRules = S.Integrate.getRulesData();
    assertNotNull(integrateRules, "Integrate must have rules data");
    assertNotNull(integrateRules.diagnosticRuleIndex(),
        "the Rubi rule set must get a RuleFeatureIndex");
    assertTrue(indexedCount >= 4,
        "expected at least 4 indexed symbols, found " + indexedCount);
  }

  private static void reportMatcher(String label, String className, String fieldName) {
    try {
      Object matcher = readField(className, fieldName);
      describe(label, matcher);
    } catch (Exception e) {
      System.out.println(String.format("%-46s %8s", label, "n/a (" + e.getClass().getSimpleName() + ")"));
    }
  }

  private static void reportSupplier(String label, String className, String fieldName) {
    try {
      Object supplier = readField(className, fieldName);
      Object matcher = supplier;
      if (supplier instanceof com.google.common.base.Supplier) {
        matcher = ((com.google.common.base.Supplier<?>) supplier).get();
      } else if (supplier instanceof java.util.function.Supplier) {
        matcher = ((java.util.function.Supplier<?>) supplier).get();
      }
      describe(label, matcher);
    } catch (Exception e) {
      System.out.println(String.format("%-46s %8s", label, "n/a (" + e.getClass().getSimpleName() + ")"));
    }
  }

  private static Object readField(String className, String fieldName) throws Exception {
    Class<?> clazz = Class.forName(className);
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(null);
  }

  private static void describe(String label, Object matcher) throws Exception {
    if (!(matcher instanceof Matcher)) {
      System.out.println(String.format("%-46s %8s", label, matcher == null ? "null" : "?"));
      return;
    }
    Field rulesField = Matcher.class.getDeclaredField("rules");
    rulesField.setAccessible(true);
    RulesData rulesData = (RulesData) rulesField.get(matcher);
    if (rulesData == null) {
      System.out.println(String.format("%-46s %8s", label, "no rules"));
      return;
    }
    int size = rulesData.patternDownRulesSize();
    RuleFeatureIndex index = rulesData.diagnosticRuleIndex();
    System.out.println(String.format("%-46s %8d %9s %9s", label, size,
        size >= Config.RULE_INDEX_MIN_RULES ? (index != null ? "yes" : "NO") : "no (small)",
        index != null ? Integer.toString(index.featureCount()) : "-"));
  }
}
