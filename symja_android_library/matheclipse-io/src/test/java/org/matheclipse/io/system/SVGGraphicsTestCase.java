package org.matheclipse.io.system;

import org.junit.jupiter.api.Disabled;
import org.matheclipse.io.system.AbstractTestCase;

/** View with: http://www.rapidtables.com/web/tools/svg-viewer-editor.htm */
public class SVGGraphicsTestCase extends AbstractTestCase {
  public SVGGraphicsTestCase(String name) {
    super(name);
  }

  @Disabled
  public void testPoint001() {
//
//    checkSVGGraphics(
//        "Show(Graphics(Point({0, 0})))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<circle cx=\"175.0\" cy=\"175.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "\n"
//            + "</svg>");
  }

  @Disabled
  public void testPoint002() {

//    checkSVGGraphics(
//        "Show(Graphics(Point(Table({t, Sin(t)}, {t, 0, 2*Pi, 2*Pi/10}))))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<circle cx=\"0.0\" cy=\"175.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"35.0\" cy=\"66.84405\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"70.0\" cy=\"0.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"105.0\" cy=\"0.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"140.0\" cy=\"66.84405\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"175.0\" cy=\"175.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"210.0\" cy=\"283.15595\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"245.0\" cy=\"350.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"280.0\" cy=\"350.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"315.0\" cy=\"283.15595\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "<circle cx=\"350.0\" cy=\"175.0\" r=\"2.16667\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); fill-opacity: 1\" />\n"
//            + "\n"
//            + "</svg>");
  }

  @Disabled
  public void testLine001() {
//
//    checkSVGGraphics(
//        "Show(Graphics(Line({{0,1},{0,0},{1,0},{1,1}})))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<polyline points=\"0.0,0.0 0.0,350.0 350.0,350.0 350.0,0.0\" \n"
//            + "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />\n"
//            + "</svg>");
  }
  
  @Disabled
  public void testLine002() {

//    checkSVGGraphics(
//        "Show(Graphics(Line({{1, 0}, {2, 1}, {3, 0}, {4, 1}})))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<polyline points=\"0.0,350.0 116.66667,0.0 233.33333,350.0 350.0,0.0\" \n"
//            + "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />\n"
//            + "</svg>");
  }

 
  @Disabled
  public void testRectangle001() {

//    checkSVGGraphics(
//        "Show(Graphics({Rectangle({1, 1})}))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<rect x=\"0.0\" y=\"0.0\" width=\"350.0\" height=\"350.0\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + "\n"
//            + "</svg>");
  }

  @Disabled
  public void testRectangle002() {

//    checkSVGGraphics(
//        "Show(Graphics({Red, Rectangle({0, 0}), Blue, Rectangle({0.5, 0.5})}))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + //
//            "<rect x=\"0.0\" y=\"116.66667\" width=\"233.33333\" height=\"233.33333\" \n"
//            + //
//            "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(100.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + //
//            "<rect x=\"116.66667\" y=\"0.0\" width=\"233.33333\" height=\"233.33333\" \n"
//            + //
//            "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + //
//            "\n"
//            + //
//            "</svg>");
  }
  @Disabled
  public void testRectangle003() {

//    checkSVGGraphics(
//        "Show(Graphics({Red, Rectangle({0, 0},{1, 3}), Blue, Rectangle({2, 1},{4, 2})}))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + //
//            "<rect x=\"0.0\" y=\"0.0\" width=\"87.5\" height=\"350.0\" \n"
//            + //
//            "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(100.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + //
//            "<rect x=\"175.0\" y=\"116.66667\" width=\"175.0\" height=\"116.66667\" \n"
//            + //
//            "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + //
//            "\n"
//            + //
//            "</svg>");
  }
  @Disabled
  public void testRectangle004() {

//    checkSVGGraphics(
//        "Show(Graphics({Rectangle({1, 1})}, Axes -> True, PlotRange -> {{-2, 1.5}, {-1, 1.5}}))",
//        "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
//            + "<rect x=\"300.0\" y=\"-70.0\" width=\"100.0\" height=\"140.0\" \n"
//            + "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
//            + "<polyline points=\"200.0,0.0 200.0,350.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
//            + "<polyline points=\"0.0,140.0 350.0,140.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
//            + "\n"
//            + "</svg>");
  }
}
