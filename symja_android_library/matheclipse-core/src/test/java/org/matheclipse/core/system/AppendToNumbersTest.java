package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>AppendTo</code>, <code>PrependTo</code>, <code>Append</code> and <code>Prepend</code> on a
 * list of numbers and strings: the result is known to be one too, so a loop of such calls doesn't
 * evaluate the whole list again at every step.
 */
public class AppendToNumbersTest extends ExprEvaluatorTestCase {

  @Test
  public void testLoopIsNotQuadratic() {
    // 19000 appends to a list of reals: about 3 s when every step evaluated the whole list
    check("TimeConstrained(Module({l={}}, Do(AppendTo(l, 0.5*i), {i, 19000}); {Length(l), l[[-1]]}), 2, slow)", //
        "{19000,9500.0}");
    check("TimeConstrained(Module({l={}}, Do(PrependTo(l, \"s\"), {i, 19000}); Length(l)), 2, slow)", //
        "19000");
  }

  @Test
  public void testMixedElements() {
    check("Module({l={1,2}}, AppendTo(l, 3); AppendTo(l, x); AppendTo(l, Nothing); {l, N(l)})", //
        "{{1,2,3,x},{1.0,2.0,3.0,x}}");
    check("Module({l={1,2}}, AppendTo(l, 3); y=5; AppendTo(l, y); PrependTo(l, \"a\"); l)", //
        "{a,1,2,3,5}");
    check("l2={2,3}; l2=Append(l2, 7/2); l2=Prepend(l2, 1); {l2, N(l2)}", //
        "{{1,2,3,7/2},{1.0,2.0,3.0,3.5}}");
  }
}
