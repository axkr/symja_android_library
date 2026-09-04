package org.matheclipse.core.expression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.reflection.system.Integrate;

/**
 * Diagnostic: how often do the special pattern matching constructs actually occur in the
 * left-hand-sides of the rule sets which get a
 * {@link org.matheclipse.core.patternmatching.ruleindex.RuleFeatureIndex}?
 *
 * <pre>
 * mvn -pl matheclipse-core test -Dtest=PatternConstructCensus
 * </pre>
 */
public class PatternConstructCensus {

  private static final ISymbol[] SPECIAL = {S.Alternatives, S.Except, S.Repeated, S.RepeatedNull,
      S.OptionsPattern, S.Longest, S.Shortest, S.Verbatim, S.HoldPattern, S.PatternTest,
      S.Condition, S.Optional};

  private final Map<String, int[]> counts = new TreeMap<String, int[]>();

  private void bump(String key) {
    int[] c = counts.get(key);
    if (c == null) {
      counts.put(key, new int[] {1});
    } else {
      c[0]++;
    }
  }

  private void scan(IExpr expr, int depth) {
    if (depth > 40) {
      return;
    }
    if (!expr.isAST()) {
      if (expr instanceof PatternNested) {
        bump("PatternNested (x:pattern)");
      } else if (expr instanceof IPatternSequence) {
        bump("PatternSequence (__ / ___)");
      } else if (expr instanceof IPatternObject) {
        IPatternObject pattern = (IPatternObject) expr;
        if (pattern.getHeadTest() != null) {
          bump("Blank with head test (x_Symbol)");
        }
      }
      return;
    }
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head.isSymbol()) {
      for (ISymbol special : SPECIAL) {
        if (head == special) {
          bump(special.toString());
        }
      }
    } else if (head.isAST()) {
      bump("curried head (f(a)(b))");
      scan(head, depth + 1);
    }
    for (int i = 1; i < ast.size(); i++) {
      scan(ast.get(i), depth + 1);
    }
  }

  private void scanRules(String label, RulesData rulesData) {
    if (rulesData == null) {
      return;
    }
    counts.clear();
    int rules = 0;
    for (IPatternMatcher matcher : rulesData.patternDownRules()) {
      IExpr lhs = matcher == null ? null : matcher.getLHS();
      if (lhs != null) {
        rules++;
        scan(lhs, 0);
      }
    }
    if (counts.isEmpty()) {
      System.out.println(String.format("%-22s %5d rules   (no special constructs)", label, rules));
      return;
    }
    StringBuilder buf = new StringBuilder();
    for (Map.Entry<String, int[]> entry : counts.entrySet()) {
      if (buf.length() > 0) {
        buf.append(", ");
      }
      buf.append(entry.getKey()).append('=').append(entry.getValue()[0]);
    }
    System.out.println(String.format("%-22s %5d rules   %s", label, rules, buf));
  }

  @Test
  public void census() throws Exception {
    F.initSymja();
    Integrate.CONST.await();
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    for (String trigger : new String[] {"Integrate(x^2*Sin(3*x), x)", "D(Sin(x)*Log(x), x)",
        "Limit(Sin(x)/x, x->0)", "LaplaceTransform(t^2, t, s)",
        "Hypergeometric2F1(1, 2, 3, x)"}) {
      try {
        engine.evaluate(engine.parse(trigger));
      } catch (RuntimeException rex) {
        // ignore
      }
    }

    System.out.println("=== special pattern constructs in indexed rule sets ===");
    Map<String, RulesData> targets = new LinkedHashMap<String, RulesData>();
    for (IBuiltInSymbol symbol : S.BUILT_IN_SYMBOLS) {
      if (symbol == null) {
        continue;
      }
      RulesData rulesData = symbol.getRulesData();
      if (rulesData != null && rulesData.patternDownRulesSize() >= 16) {
        targets.put(symbol.toString(), rulesData);
      }
    }
    for (Map.Entry<String, RulesData> entry : targets.entrySet()) {
      scanRules(entry.getKey(), entry.getValue());
    }
  }
}
