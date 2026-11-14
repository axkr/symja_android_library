package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * An image inside a <code>Pane</code> or a <code>Grid</code> exported as SVG: the picture itself,
 * embedded as a PNG at its size in pixels, placed in a fixed box by the alignment.
 */
public class ImageInLayoutTest extends AbstractTestCase {

  /** The viewports the layout places its cells in. */
  private static String cells(String layout) {
    return "StringCases(ExportString(" + layout
        + ", \"SVG\"), RegularExpression(\"<svg x=[^>]*>\"))";
  }

  @Test
  public void paneDrawsTheImageNotItsDescription() {
    check("svg=ExportString(Pane(Image(ConstantArray(0.3, {12, 16})), {50, 40}), \"SVG\");"
        + "{StringContainsQ(svg, \"<image width=\\\"16\\\" height=\\\"12\\\"\"),"
        + "StringContainsQ(svg, \"data:image/png;base64,\"),"
        + "StringContainsQ(svg, \"Dimensions\")}", //
        "{True,True,False}");
  }

  @Test
  public void paneAlignsTheImageInItsBox() {
    // 16 pixels wide and 12 high in a box of 50 by 40
    check(cells("Pane(Image(ConstantArray(0.3, {12, 16})), {50, 40})"), //
        "{<svg x=\"0\" y=\"0\" width=\"16\" height=\"12\" viewBox=\"0 0 16 12\">}");
    check(cells("Pane(Image(ConstantArray(0.3, {12, 16})), {50, 40}, Alignment -> Center)"), //
        "{<svg x=\"17\" y=\"14\" width=\"16\" height=\"12\" viewBox=\"0 0 16 12\">}");
    check(cells("Pane(Image(ConstantArray(0.3, {12, 16})), ImageSize -> {50, 40}, "
        + "Alignment -> {Right, Bottom})"), //
        "{<svg x=\"34\" y=\"28\" width=\"16\" height=\"12\" viewBox=\"0 0 16 12\">}");
    check(cells("Pane(Image(ConstantArray(0.3, {12, 16})), {50, 40}, Alignment -> {Left, Bottom})"), //
        "{<svg x=\"0\" y=\"28\" width=\"16\" height=\"12\" viewBox=\"0 0 16 12\">}");
  }

  @Test
  public void displayWrappedImageIsStillTheImage() {
    check(cells("Pane(Style(Image(ConstantArray(0.3, {12, 16})), Red), {50, 40}, "
        + "Alignment -> Center)"), //
        "{<svg x=\"17\" y=\"14\" width=\"16\" height=\"12\" viewBox=\"0 0 16 12\">}");
    check("svg=ExportString(Grid({{Style(Image(ConstantArray(0.7, {6, 9})), Bold)}}), \"SVG\");"
        + "{StringContainsQ(svg, \"<image width=\\\"9\\\" height=\\\"6\\\"\"),"
        + "StringContainsQ(svg, \"Dimensions\")}", //
        "{True,False}");
  }

  @Test
  public void gridCellDrawsTheImage() {
    check("svg=ExportString(Grid({{Image(ConstantArray(0.7, {6, 9})), \"label\"}}), \"SVG\");"
        + "{StringContainsQ(svg, \"<image width=\\\"9\\\" height=\\\"6\\\"\"),"
        + "StringContainsQ(svg, \"Dimensions\")}", //
        "{True,False}");
  }
}
