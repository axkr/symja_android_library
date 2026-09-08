package org.matheclipse.core.eval.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * What each rule of the Rubi integration rule set does, in words.
 *
 * <p>
 * Rubi ships a second spelling of its rules in which every rewrite is wrapped in a
 * <code>ShowStep</code> saying under which condition it applies and what it turns the integral
 * into. <code>ConvertRubiShowSteps</code> in the <code>tools</code> module reads those out and
 * writes them as <code>rubi/rubi_steps.tsv.gz</code>, keyed by the rule number Symja knows a rule
 * by, so a step of an integration can say more than "rule 3125 was applied":
 *
 * <pre>
 * Rubi integration rule 3125. If IGtQ[(n-1)/(2),0], rewrite the integral of sin(c+d x)^n
 * as -1/d times the integral of Expand[(1-x^2)^((n-1)/2)] substituted at x = cos(c+d x).
 * </pre>
 *
 * <p>
 * The before and after are the rule's own general shape, written with its own pattern names, not
 * the expression at hand - the step carries that itself. They are parsed on first use and kept, so
 * a rule which appears twice in one derivation is only read once.
 *
 * <p>
 * The table is about 230 KB packed and is read the first time a rule is asked for, so an evaluation
 * which shows no integration steps never touches it.
 */
public final class RubiStepDescriptions {

  /** Where the table is packed in the jar. */
  private static final String RESOURCE = "rubi/rubi_steps.tsv.gz";

  /**
   * Rubi's display operator for "this factor stands in front of that integral". It carries no
   * meaning of its own, so it is read as an ordinary product.
   */
  private static final char STAR = '⋆';

  /** What one rule of the rule set does. */
  public static final class Description {

    private final String condition;
    private final String before;
    private final String after;

    private IExpr beforeExpr;
    private IExpr afterExpr;

    private Description(String condition, String before, String after) {
      this.condition = condition;
      this.before = before;
      this.after = after;
    }

    /**
     * Under which condition the rule applies, as a sentence: <code>If IGtQ[(n-1)/(2),0]</code>.
     *
     * @return <code>""</code> for the few steps Rubi shows without naming a condition
     */
    public String condition() {
      return condition;
    }

    /** The general shape of the integral the rule matches, or {@link F#NIL} if it cannot be read. */
    public IExpr before() {
      if (beforeExpr == null) {
        beforeExpr = parse(before);
      }
      return beforeExpr;
    }

    /** What the rule rewrites it to, or {@link F#NIL} if it cannot be read. */
    public IExpr after() {
      if (afterExpr == null) {
        afterExpr = parse(after);
      }
      return afterExpr;
    }

    /** The general shape of the integral as it stands in the rule set. */
    public String beforeText() {
      return before;
    }

    /** What the rule rewrites it to, as it stands in the rule set. */
    public String afterText() {
      return after;
    }
  }

  private RubiStepDescriptions() {}

  /** Read the table the first time a description is asked for, and not before. */
  private static final class Table {
    static final Map<Integer, Description> MAP = read();

    private static Map<Integer, Description> read() {
      Map<Integer, Description> map = new HashMap<Integer, Description>(8192);
      ClassLoader classLoader = RubiStepDescriptions.class.getClassLoader();
      try (InputStream stream = classLoader.getResourceAsStream(RESOURCE)) {
        if (stream == null) {
          // the table is optional: without it a step still names the rule it applied
          return map;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            new GZIPInputStream(stream), StandardCharsets.UTF_8), 1 << 16)) {
          String line;
          while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\t", -1);
            if (fields.length < 4) {
              continue;
            }
            try {
              map.put(Integer.valueOf(fields[0]),
                  new Description(fields[1], fields[2], fields[3]));
            } catch (NumberFormatException nfe) {
              // a line which is not a rule
            }
          }
        }
      } catch (IOException ioe) {
        if (Config.SHOW_STACKTRACE) {
          ioe.printStackTrace();
        }
      }
      return map;
    }
  }

  /**
   * What the rule with this number does.
   *
   * @param ruleNumber the number Symja knows the rule by, which is its left-hand-side priority in
   *        the pattern matcher
   * @return <code>null</code> if the rule set carries no description for it - about 250 of the
   *         7300 rules are plumbing which Rubi itself does not show as a step
   */
  public static Description get(int ruleNumber) {
    if (!ToggleFeature.SHOW_STEPS) {
      return null;
    }
    return Table.MAP.get(Integer.valueOf(ruleNumber));
  }

  /**
   * Read one of the rule set's templates.
   *
   * <p>
   * They are written in Mathematica syntax whatever syntax the session uses, and they are wrapped
   * in {@link org.matheclipse.core.expression.S#HoldForm}: a template mentions
   * <code>Integrate[...]</code>, and evaluating that would set the integrator going on the rule's
   * own pattern variables.
   *
   * @return {@link F#NIL} if the template cannot be read
   */
  private static IExpr parse(String template) {
    try {
      ExprParser parser = new ExprParser(EvalEngine.get(), false);
      IExpr expr = parser.parse(template.replace(STAR, '*'));
      return expr.isPresent() ? F.HoldForm(expr) : F.NIL;
    } catch (RuntimeException rex) {
      // a handful of templates are prose rather than an expression, for example the one which
      // writes a sum of integrals with an ellipsis
      return F.NIL;
    }
  }
}
