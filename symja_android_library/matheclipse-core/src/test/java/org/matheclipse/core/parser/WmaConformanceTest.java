package org.matheclipse.core.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;

/**
 * This is a different kind of test from the golden corpus. The corpus records what Symja
 * <em>does</em>, so it detects change; these cases record what Symja <em>should</em> do, so they
 * detect wrongness. A golden file happily preserves a bug forever - the two RightComposition cases
 * below were wrong in every golden recording.
 *
 * <p>
 * Expected values are the {@code FullForm} of {@code HoldForm[...]}, with the {@code HoldForm}
 * wrapper dropped and rewritten in Symja's function-call spelling. Add a case here whenever a
 * parser question gets settled against WMA; see
 * {@code matheclipse-parser/src/test/resources/data/wma-reference-inputs.md} for the cell that
 * produces the reference output.
 */
public class WmaConformanceTest {

  static {
    Config.FILESYSTEM_ENABLED = false;
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  /**
   * {@code f @ x} is {@code f[x]} and not {@code Apply[f, x]}, which is worth stating explicitly:
   * Symja registers that operator under the function name "Apply" even though it builds a plain
   * function call, and that misnomer is why the operator-table report pairs it with Mathics'
   * {@code Apply} (620) when its real counterpart is {@code Prefix} (640).
   */
  private static final String[][] APPLY_AND_MAP_CASES = { //
      {"f @ g @@ h", "Apply(f(g), h)"}, //
      {"f @@ g @@ h", "Apply(f, Apply(g, h))"}, //
      {"f @@ g @ h", "Apply(f, g(h))"}, //
      {"f @x", "f(x)"}, //
      {"a @@@ b /@ c", "MapApply(a, Map(b, c))"}, //
      {"a /@ b @@@ c", "Map(a, MapApply(b, c))"}, //
      {"a @@@ b @* c", "MapApply(a, Composition(b, c))"}, //
  };

  /**
   * Composition binds tighter than RightComposition - {@code @*} groups first in both orders. Symja
   * had this backwards until the precedences were corrected, and so does Mathics3's table.
   */
  private static final String[][] COMPOSITION_CASES = { //
      {"f @* g /* h", "RightComposition(Composition(f, g), h)"}, //
      {"f /* g @* h", "RightComposition(f, Composition(g, h))"}, //
  };

  /**
   * PatternTest is left-associative: <code>a?b?c</code> is <code>(a?b)?c</code>.
   * 
   */
  private static final String[][] PATTERN_TEST_CASES = { //
      {"a ? b ? c", "PatternTest(PatternTest(a, b), c)"}, //
      {"x_?NumberQ?Positive", "PatternTest(PatternTest(Pattern(x, Blank()), NumberQ), Positive)"}, //
  };

  /**
   * A flat operator flattens the same way however its token is spelled.
   *
   * <p>
   * A unicode operator is registered as a second token for the same operator instance, and the
   * chaining loops used to compare the scanned token against the operator's own token text - so
   * only the ASCII spelling ever matched itself. <code>a &lt;= b &lt;= c</code> flattened and
   * <code>a \u2264 b \u2264 c</code> nested, and a mixed chain such as <code>a == b \u2264 c</code>
   * produced <code>LessEqual(Equal(a,b),c)</code> instead of an Inequality.
   *
   * <p>
   * <code>LessEqual(a,b,c)</code> is FullForm for the ASCII spelling; the rest follow from the two
   * spellings of an operator as the same operator.
   */
  private static final String[][] FLAT_OPERATOR_SPELLING_CASES = { //
      {"a <= b <= c", "LessEqual(a, b, c)"}, //
      {"a \u2264 b \u2264 c", "LessEqual(a, b, c)"}, //
      {"a == b \u2264 c", "Inequality(a, Equal, b, LessEqual, c)"}, //
      {"a && b && c", "And(a, b, c)"}, //
      {"a \u2227 b \u2227 c", "And(a, b, c)"}, //
      {"a || b || c", "Or(a, b, c)"}, //
      {"a \u2228 b \u2228 c", "Or(a, b, c)"}, //
      {"a * b \u00d7 c", "Times(a, b, c)"}, //
  };

  /**
   * RightComposition binds tighter than <code>@@</code> and <code>/@</code>.
   */
  private static final String[][] RIGHT_COMPOSITION_BINDING_CASES = { //
      {"f /* g @@ h", "Apply(RightComposition(f, g), h)"}, //
      {"f /* g /@ h", "Map(RightComposition(f, g), h)"}, //
  };

  /**
   * The edge arrows are right-associative:
   * <code>HoldForm[a \[DirectedEdge] b \[DirectedEdge] c] // FullForm</code> gives
   * <code>DirectedEdge[a, DirectedEdge[b, c]]</code>. Mathics3 records both arrows as
   * non-associative, which is the fourth row where its table disagrees with WMA itself.
   */
  private static final String[][] EDGE_ARROW_CASES = { //
      {"a \uf3d5 b \uf3d5 c", "DirectedEdge(a, DirectedEdge(b, c))"}, //
      {"a \uf3d4 b \uf3d4 c", "UndirectedEdge(a, UndirectedEdge(b, c))"}, //
  };

  @Test
  public void edgeArrowsAreRightAssociative() {
    assertParsesAs(EDGE_ARROW_CASES);
  }

  @Test
  public void flatOperatorsFlattenWhicheverSpellingIsUsed() {
    assertParsesAs(FLAT_OPERATOR_SPELLING_CASES);
  }

  @Test
  public void rightCompositionBindsTighterThanApplyAndMap() {
    assertParsesAs(RIGHT_COMPOSITION_BINDING_CASES);
  }

  @Test
  public void patternTestIsLeftAssociative() {
    assertParsesAs(PATTERN_TEST_CASES);
  }

  @Test
  public void applyAndMapOperatorsGroupAsInWMA() {
    assertParsesAs(APPLY_AND_MAP_CASES);
  }

  @Test
  public void compositionBindsTighterThanRightComposition() {
    assertParsesAs(COMPOSITION_CASES);
  }

  /**
   * Parse every case and report all mismatches at once - fixing a precedence usually moves several
   * of these together, and seeing one failure at a time hides whether the change helped or just
   * moved the problem.
   */
  private static void assertParsesAs(String[][] cases) {
    EvalEngine engine = new EvalEngine("wma-conformance", 256, 256, System.out, System.err, false);
    engine.init();
    EvalEngine.set(engine);

    List<String> failures = new ArrayList<>();
    for (String[] testCase : cases) {
      String input = testCase[0];
      String expected = testCase[1];
      String actual;
      try {
        actual = engine.parse(input).fullFormString();
      } catch (RuntimeException e) {
        actual = e.getClass().getSimpleName() + ": " + e.getMessage();
      }
      if (!normalize(expected).equals(normalize(actual))) {
        failures.add("  " + input + "\n    WMA " + expected + "\n    Symja:   " + actual);
      }
    }
    assertEquals("", String.join("\n", failures),
        failures.size() + " of " + cases.length + " inputs parse differently from WMA");
  }

  /** Whitespace inside a full form is not part of the tree. */
  private static String normalize(String fullForm) {
    return fullForm.replace(" ", "");
  }
}
