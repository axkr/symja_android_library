package org.matheclipse.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A part that is not there is the question <code>dataset[All, "c", 1]</code> asks, and the cell
 * records the answer as <code>Missing(PartAbsent, 1)</code>. Nothing should be reported about it
 * on top of that.
 *
 * <p>
 * This lives here rather than with the other dataset tests because it is about what the engine
 * <b>prints</b>, which the string comparison those use never sees. Nor would <code>Check</code>
 * serve: it reads the message shortcut, which is recorded whether or not anything was printed.
 */
public class MissingCellTest {

  @BeforeEach
  public void setUp() throws Exception {
    // the Dataset evaluators live in this module and nothing else here installs them - and they
    // are installed only with the file system enabled, so that has to be set first
    Config.FILESYSTEM_ENABLED = true;
    F.initSymja();
    DatasetInit.init();
    F.await();
  }

  @Test
  public void testAnAbsentPartIsNotReported() {
    EvalEngine engine = EvalEngine.get();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    PrintStream outStream = engine.getOutPrintStream();
    PrintStream errStream = engine.getErrorPrintStream();
    try {
      engine.setOutPrintStream(new PrintStream(out));
      engine.setErrorPrintStream(new PrintStream(err));
      engine.evaluate("dm = Dataset({<|\"a\" -> 1, \"c\" -> {1}|>, <|\"a\" -> 6, \"c\" -> {}|>})");
      IExpr result = engine.evaluate("dm[All, \"c\", 1]");

      assertTrue(result instanceof IASTDataset, "expected a Dataset but got " + result);
      // the answer is kept ...
      assertEquals("{1,Missing(\"PartAbsent\",1)}",
          engine.evaluate("Normal(dm[All, \"c\", 1]) // InputForm").toString());
    } finally {
      engine.setOutPrintStream(outStream);
      engine.setErrorPrintStream(errStream);
    }
    // ... and nothing was said about it. This used to reach the servlet as
    // "Error: Part: Part 1 of {} does not exist." beside a table that had already dealt with it
    assertEquals("", out.toString(), "nothing should be printed");
    assertFalse(err.toString().contains("Part"), "no message about Part: " + err);
    assertEquals("", err.toString(), "nothing should be reported");
  }

  /**
   * Two datasets holding the same data in the same shape are equal. This used to hand the question
   * to <code>Table</code>, which has no equality of its own and answered by identity - so no
   * dataset was equal to any other. The canonical order was never affected: two datasets already
   * compared by their rows, which is why <code>Union</code> and <code>DeleteDuplicates</code> were
   * right while <code>SameQ</code> was not.
   */
  @Test
  public void testDatasetsWithTheSameDataAreEqual() {
    EvalEngine engine = EvalEngine.get();
    engine.evaluate("rows = {<|\"a\" -> 1, \"b\" -> \"x\"|>, <|\"a\" -> 2, \"b\" -> \"y\"|>}");

    assertEquals("True", engine.evaluate("Dataset(rows) === Dataset(rows)").toString());
    assertEquals("False", engine.evaluate("Dataset(rows) === Dataset(Reverse(rows))").toString());
    assertEquals("True", engine.evaluate("Dataset(rows) == Dataset(rows)").toString());

    // equal objects hash alike, so a dataset can be a key
    IExpr one = engine.evaluate("Dataset(rows)");
    IExpr two = engine.evaluate("Dataset(rows)");
    assertEquals(one, two);
    assertEquals(one.hashCode(), two.hashCode());
    assertEquals("seen",
        engine.evaluate("Lookup(<|Dataset(rows) -> \"seen\"|>, Dataset(rows))").toString());

    // the shape is part of it: a vector is not the one column table holding the same values
    assertEquals("True", engine.evaluate("Dataset({1,2}) === Dataset({1,2})").toString());
    assertEquals("False", engine
        .evaluate("Dataset({1,2}) === Dataset({<|\"value\"->1|>,<|\"value\"->2|>})").toString());

    // ... but a vector's own column name is storage and not data, so a selected column equals a
    // vector built from the same values - and the two are stored in different column types, so
    // the cells have to be compared as expressions rather than as tablesaw holds them
    assertEquals("True",
        engine.evaluate("Dataset(rows)[All,\"a\"] === Dataset({1,2})").toString());
    assertEquals("True", engine
        .evaluate("Take(Dataset(rows), 1) === Dataset({<|\"a\"->1,\"b\"->\"x\"|>})").toString());

    // display options are part of what the object is
    assertEquals("False",
        engine.evaluate("Dataset(rows) === Dataset(rows, HeaderBackground->Red)").toString());
  }

  /** Every kind of missing cell is drawn as one, and none of them spells out why. */
  @Test
  public void testAMissingCellIsDrawnAsAHyphen() throws Exception {
    EvalEngine engine = EvalEngine.get();
    engine.evaluate("dm = Dataset({<|\"a\" -> 1, \"c\" -> {1}|>, <|\"a\" -> 6, \"c\" -> {}|>})");

    String absent = ((IASTDataset) engine.evaluate("dm[All, \"c\", 1]")).datasetToJSForm();
    assertTrue(absent.contains("<span style=\"color:darkgray\">-</span>"), absent);
    assertFalse(absent.contains("Missing"), absent);

    // a row of the source that simply lacked a key, in a column that is not one of expressions
    engine.evaluate("gappy = Dataset({<|\"a\" -> 1, \"b\" -> 2|>, <|\"a\" -> 3|>})");
    String gappy = ((IASTDataset) engine.evaluate("gappy")).datasetToJSForm();
    assertTrue(gappy.contains("<span style=\"color:darkgray\">-</span>"), gappy);
    // the cell reads as missing rather than as the sentinel an int column keeps for one
    assertEquals("{<|\"a\"->1,\"b\"->2|>,<|\"a\"->3,\"b\"->Missing(NotAvailable)|>}",
        engine.evaluate("Normal(gappy) // InputForm").toString());
  }
}
