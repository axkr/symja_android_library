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
    //    if (Config.FILESYSTEM_ENABLED) {
    // check("Export(\"c:\\\\temp\\\\testgraph.csv\",Graph({1 \\[DirectedEdge] 2, 2
    // \\[DirectedEdge] 3, 3
    // \\[DirectedEdge] 1}))", //
    // "c:\\temp\\testgraph.csv");

//    check(
//        "Export(\"c:\\\\temp\\\\dotgraph.dot\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
//        "c:\\temp\\dotgraph.dot");
//    check(
//        "Import(\"c:\\\\temp\\\\dotgraph.dot\")", //
//        "Graph({1,2,3},{1->2,2->3,3->1})");
//
//    check(
//        "Export(\"c:\\\\temp\\\\dotgraph.graphml\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}),\"GraphML\")", //
//        "c:\\temp\\dotgraph.graphml");
//    check(
//        "gr=Import(\"c:\\\\temp\\\\dotgraph.graphml\", \"GraphML\")", //
//        "Graph({1,2,3},{1->2,2->3,3->1})");
//    check(
//        "ExportString(gr, \"GraphML\")", //
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
//            + //
//            "<graph edgedefault=\"directed\">\r\n"
//            + //
//            "<node id=\"1\"/>\r\n"
//            + //
//            "<node id=\"2\"/>\r\n"
//            + //
//            "<node id=\"3\"/>\r\n"
//            + //
//            "<edge id=\"1\" source=\"1\" target=\"2\"/>\r\n"
//            + //
//            "<edge id=\"2\" source=\"2\" target=\"3\"/>\r\n"
//            + //
//            "<edge id=\"3\" source=\"3\" target=\"1\"/>\r\n"
//            + //
//            "</graph>\r\n"
//            + //
//            "</graphml>\r\n"
//            + //
//            "");
//    check(
//        "Export(\"c:\\\\temp\\\\out.wxf\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"WXF\")", //
//        "c:\\temp\\out.wxf");
//    check(
//        "Import(\"c:\\\\temp\\\\out.wxf\", \"WXF\")", //
//        "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
//
//    check(
//        "Export(\"c:\\\\temp\\\\out.dat\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"Table\")", //
//        "c:\\temp\\out.dat");
//    check(
//        "Import(\"c:\\\\temp\\\\out.dat\", \"Table\")", //
//        "{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
//    check(
//        "Export(\"c:\\\\temp\\\\data.txt\", Integrate(sin(x)^10,x), \"Data\")", //
//        "c:\\temp\\data.txt");
//    check(
//        "Import(\"c:\\\\temp\\\\data.txt\", \"String\")", //
//        "63/256*x-63/256*Cos(x)*Sin(x)-21/128*Cos(x)*Sin(x)^3-21/160*Cos(x)*Sin(x)^5-9/80*Cos(x)*Sin(x)^\n" //
//            + "7-1/10*Cos(x)*Sin(x)^9");
    //    }
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    Config.FILESYSTEM_ENABLED=true;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
