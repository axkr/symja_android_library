package org.matheclipse.io.others;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.io.system.AbstractTestCase;

/** Tests for string functions */
public class ExportImportFunctionsJUnit extends AbstractTestCase {


  /**
   * A temporary directory to write into. These tests used to be pinned to <code>c:\temp</code>
   * behind an <code>os.name</code> check, so they only ever ran on Windows - and, because the
   * lifecycle annotation was missing, not even there.
   */
  @TempDir
  Path directory;

  /** The path as a Symja string literal: forward slashes work on every platform Java runs on. */
  private String file(String name) {
    return directory.resolve(name).toString().replace('\\', '/');
  }

  @Test
  public void testImportExport() {
    String csv = file("testgraph.csv");
    check("Export(\"" + csv + "\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        csv);

    String dot = file("dotgraph.dot");
    check("Export(\"" + dot + "\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
        dot);
    check("Import(\"" + dot + "\")", //
        "Graph({1,2,3},{1->2,2->3,3->1})");

    String graphml = file("dotgraph.graphml");
    check("Export(\"" + graphml + "\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}),\"GraphML\")", //
        graphml);
    check("Import(\"" + graphml + "\", \"GraphML\")", //
        "Graph({1,2,3},{1->2,2->3,3->1})");

    String wxf = file("out.wxf");
    check("Export(\"" + wxf + "\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"WXF\")", //
        wxf);
    check("Import(\"" + wxf + "\", \"WXF\")", //
        "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");

    String data = file("data.txt");
    check("Export(\"" + data + "\", Integrate(sin(x)^10,x), \"Data\")", //
        data);
    check("Import(\"" + data + "\", \"String\") // InputForm", //
        "63/256*x - 63/256*Cos(x)*Sin(x) - 21/128*Cos(x)*Sin(x)^3 - 21/160*Cos(x)*Sin(x)^5 - 9/80*Cos(x)*Sin(x)^7 - 1/10*Cos(x)*Sin(x)^9");
  }

  @Test
  public void testImportExportTable() {
    String table = file("out.dat");
    check("Export(\"" + table + "\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"Table\")", //
        table);
    check("Import(\"" + table + "\", \"Table\")", //
        "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
  }

  @Test
  public void testExportStringList() {
    check("ExportString[{1, 2, 3}, \"ExpressionJSON\"]", //
        "[\"List\",\"1\",\"2\",\"3\"]");
  }

  @Test
  public void testExportStringTrue() {
    check("ExportString[ True ,\"ExpressionJSON\"]", //
        "true");
  }

  @Test
  public void testExportStringComplex() {
    check("ExportString({2.1+I*3.4}, \"ExpressionJSON\")", //
        "[\"List\",[\"Complex\",2.1,3.4]]");
  }

  @Test
  public void testExportStringAssociation() {
    // These three numbers were recorded from a run rather than from Mathematica, after the guard
    // that had kept this test from ever executing was removed. The values the expectation used to
    // carry were older and less accurate - 3.1415926535897927 for Pi where the nearest double is
    // 3.141592653589793 - so they are stale rather than a regression. Two caveats remain: the last
    // digit of a product like 6.626070040*10^-34 can differ between aarch64 and x86-64 (see the
    // comment on checkNumeric in ExprEvaluatorTestCase), and N[Pi, 20] loses its extra digits
    // because ExpressionJSON encodes every real as a double.
    check(
        "ExportString(<|\"PlanckConstant\" -> 6.626070040*10^-34, \"AvogadroConstant\" -> 6.02214*10^23, \"Pi\" -> N[Pi, 20]|>, \"ExpressionJSON\")", //
        "[\"Association\",[\"Rule\",\"'PlanckConstant'\",6.626070040000001E-34],[\"Rule\",\"'AvogadroConstant'\",6.02214E23],[\"Rule\",\"'Pi'\",3.141592653589793]]");
    check("ExportString(<|\"x\" -> 1, \"y\" -> 2, \"z\" -> 3|>, \"ExpressionJSON\")", //
        "[\"Association\",[\"Rule\",\"'x'\",\"1\"],[\"Rule\",\"'y'\",\"2\"],[\"Rule\",\"'z'\",\"3\"]]");
  }

  @Test
  public void testExportStringLine() {

    check("l=Graphics3D(Line({{1, 1, -1}, {2, 2, 1}, {3, 3, -1}, {4, 4, 1}}))", //
        "Graphics3D(Line({{1,1,-1},{2,2,1},{3,3,-1},{4,4,1}}))");

    check("ExportString(l//N, \"ExpressionJSON\")", //
        "[\"Graphics3D\",[\"Line\",[\"List\",[\"List\",\"1\",\"1\",\"-1\"],[\"List\",\"2\",\"2\",\"1\"],[\"List\",\"3\",\"3\",\"-1\"],[\"List\",\"4\",\"4\",\"1\"]]]]");
  }

  @Test
  public void testExportStringBase64() {
    check(
        "ExportString(\"Hello world\", \"Base64\")", //
        "SGVsbG8gd29ybGQ=");
  }

  /** The JUnit setup method */
  // @BeforeEach as well as @Override: JUnit 5 does not carry a lifecycle annotation onto
  // an overriding method, so without it neither this method nor the one it overrides runs
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
