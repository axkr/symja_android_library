package org.matheclipse.io.others;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.io.system.AbstractTestCase;

/** Tests for string functions */
public class ExportImportFunctionsJUnit extends AbstractTestCase {

  public ExportImportFunctionsJUnit(String name) {
    super(name);
  }

  public void testImportExport() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check(
            "Export(\"c:\\\\temp\\\\testgraph.csv\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
            "c:\\temp\\testgraph.csv");
        System.out.println(".");
        check(
            "Export(\"c:\\\\temp\\\\dotgraph.dot\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
            "c:\\temp\\dotgraph.dot");
        System.out.println(".");
        check("Import(\"c:\\\\temp\\\\dotgraph.dot\")", //
            "Graph({1,2,3},{1->2,2->3,3->1})");
        System.out.println(".");
        check(
            "Export(\"c:\\\\temp\\\\dotgraph.graphml\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}),\"GraphML\")", //
            "c:\\temp\\dotgraph.graphml");
        System.out.println(".");
        check("gr=Import(\"c:\\\\temp\\\\dotgraph.graphml\", \"GraphML\")", //
            "Graph({1,2,3},{1->2,2->3,3->1})");
        System.out.println(".");
        // check("ExportString(gr, \"GraphML\")", //
        // "<?xml version=\"1.0\" encoding=\"UTF-8\"?><graphml
        // xmlns=\"http://graphml.graphdrawing.org/xmlns\"
        // xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns
        // http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\"
        // xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" //
        // + " <graph edgedefault=\"directed\">\n" //
        // + " <node id=\"1\"/>\n"
        // + " <node id=\"2\"/>\n" + " <node id=\"3\"/>\n"
        // + " <edge source=\"1\" target=\"2\"/>\n"
        // + " <edge source=\"2\" target=\"3\"/>\n"
        // + " <edge source=\"3\" target=\"1\"/>\n" + " </graph>\n" + "</graphml>\n"
        // + "");
        System.out.println(".");
        check("Export(\"c:\\\\temp\\\\out.wxf\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"WXF\")", //
            "c:\\temp\\out.wxf");
        System.out.println(".");
        check("Import(\"c:\\\\temp\\\\out.wxf\", \"WXF\")", //
            "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
        System.out.println(".");
        check("Export(\"c:\\\\temp\\\\data.txt\", Integrate(sin(x)^10,x), \"Data\")", //
            "c:\\temp\\data.txt");
        System.out.println(".");
        check("Import(\"c:\\\\temp\\\\data.txt\", \"String\") // InputForm", //
            "63/256*x - 63/256*Cos(x)*Sin(x) - 21/128*Cos(x)*Sin(x)^3 - 21/160*Cos(x)*Sin(x)^5 - 9/80*Cos(x)*Sin(x)^7 - 1/10*Cos(x)*Sin(x)^9");
        System.out.println(".");
        check("Import(\"c:\\\\temp\\\\data.txt\", \"Text\") // InputForm", //
            "\"63/256*x-63/256*Cos(x)*Sin(x)-21/128*Cos(x)*Sin(x)^3-21/160*Cos(x)*Sin(x)^5-9/80*Cos(x)*Sin(x)^\n" //
                + "7-1/10*Cos(x)*Sin(x)^9\"");
        System.out.println(".");
      }
    }
  }

  public void testImportExportTable() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check("Export(\"c:\\\\temp\\\\out.dat\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"Table\")", //
            "c:\\temp\\out.dat");
        System.out.println(".");
        check("Import(\"c:\\\\temp\\\\out.dat\", \"Table\")", //
            "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");

      }
    }
  }

  public void testExportStringList() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check("ExportString[{1, 2, 3}, \"ExpressionJSON\"]", //
            "[\"List\",\"1\",\"2\",\"3\"]");
      }
    }
  }

  public void testExportStringTrue() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check("ExportString[ True ,\"ExpressionJSON\"]", //
            "true");
      }
    }
  }

  public void testExportStringComplex() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check("ExportString[{2.1+I*3.4}, \"ExpressionJSON\"]", //
            "[\"List\",[\"Complex\",2.1,3.4]]");
      }
    }
  }

  public void testExportStringAssociation() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {
        check(
            "ExportString(<|\"PlanckConstant\" -> 6.626070040*10^-34, \"AvogadroConstant\" -> 6.02214*10^23, \"Pi\" -> N[Pi, 20]|>, \"ExpressionJSON\")", //
            "[\"Association\",[\"Rule\",\"'PlanckConstant'\",6.62607004E-34],[\"Rule\",\"'AvogadroConstant'\",6.022139999999999E23],[\"Rule\",\"'Pi'\",3.1415926535897927]]");
        check("ExportString(<|\"x\" -> 1, \"y\" -> 2, \"z\" -> 3|>, \"ExpressionJSON\")", //
            "[\"Association\",[\"Rule\",\"'x'\",\"1\"],[\"Rule\",\"'y'\",\"2\"],[\"Rule\",\"'z'\",\"3\"]]");
      }
    }
  }

  public void testExportStringLine() {
    if (Config.FILESYSTEM_ENABLED) {
      String s = System.getProperty("os.name");
      if (s.contains("Windows")) {

        check("l=Graphics3D(Line({{1, 1, -1}, {2, 2, 1}, {3, 3, -1}, {4, 4, 1}}))", //
            "Graphics3D(Line({{1,1,-1},{2,2,1},{3,3,-1},{4,4,1}}))");

        check("ExportString(l//N, \"ExpressionJSON\")", //
            "[\"Graphics3D\"," //
                + "[\"Line\",[\"List\"," + "[\"List\",1.0,1.0,-1.0]," + "[\"List\",2.0,2.0,1.0],"
                + "[\"List\",3.0,3.0,-1.0]," + "[\"List\",4.0,4.0,1.0]]" + "]" + "]");
      }
    }
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
