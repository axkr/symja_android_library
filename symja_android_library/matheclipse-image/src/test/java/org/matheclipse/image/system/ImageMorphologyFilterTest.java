package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Image morphology, thresholds, periodograms, channels and filters. */
public class ImageMorphologyFilterTest extends AbstractTestCase {

  @Test
  public void testMorphologicalEulerNumber() {
    // two rings, each with one hole
    check("MorphologicalEulerNumber(Image({{1, 1, 1, 0, 1, 1, 1}, {1, 0, 1, 0, 1, 0, 1}, "
        + "{1, 1, 1, 0, 1, 1, 1}}))", //
        "0");
    // a diagonal pair is one component, or two without corner neighbours
    check("{MorphologicalEulerNumber(Image({{0, 1}, {1, 0}})), "
        + "MorphologicalEulerNumber(Image({{0, 1}, {1, 0}}), CornerNeighbors -> False)}", //
        "{1,2}");
    check("MorphologicalEulerNumber(Image({{0.1, 0.7}, {0.2, 0.0}}), 0.5)", //
        "1");
    check("MorphologicalEulerNumber(Image({{0, 0}, {0, 0}}))", //
        "0");
    // a foreground padding closes the background inside into a hole
    check("MorphologicalEulerNumber(Image({{0, 0, 0}, {0, 0, 0}}), Padding -> 1)", //
        "0");
  }

  @Test
  public void testThreshold() {
    check("ImageData(Threshold(Image({{0.3, 0.05}, {0.15, 0.6}}), 0.2))", //
        "{{0.3,0.0},\n" + " {0.0,0.6}}");
    check("Threshold({-0.3, 0.05, 2/3}, 0.1)", //
        "{-0.3,0.0,2/3}");
    check("Threshold({-0.3, 0.05, 0.5}, {\"Soft\", 0.1})", //
        "{-0.2,0.0,0.4}");
  }

  @Test
  public void testImagePeriodogram() {
    check("p = ImagePeriodogram(Image({{0.2, 0.9}, {0.4, 0.1}, {0.8, 0.3}})); "
        + "{ImageType(p), ImageDimensions(p)}", //
        "{Byte,{2,3}}");
    // the zero frequency is the largest and lies in the centre, or in the top left corner
    check("ImageData(ImagePeriodogram(Image({{0.2, 0.9, 0.5}, {0.4, 0.1, 0.6}, "
        + "{0.8, 0.3, 0.7}})), \"Byte\")[[2, 2]]", //
        "255");
    check("ImageData(ImagePeriodogram(Image({{0.2, 0.9}, {0.4, 0.1}}), Alignment -> Top), "
        + "\"Byte\")[[1, 1]]", //
        "255");
  }

  @Test
  public void testImageAdjustChannels() {
    // every channel is stretched on its own; a flat channel becomes 0
    check("ImageData(ImageAdjust(Image({{{0.2, 0.4, 0.5}, {0.6, 0.4, 0.7}}})))", //
        "{{{0.0,0.0,0.0},{1.0,0.0,1.0}}}");
  }

  @Test
  public void testConvolvePadding() {
    check("ImageData(ImageConvolve(Image({{0.1, 0.2, 0.4}}), {{1, 0, 0}}, "
        + "Padding -> \"Periodic\"))", //
        "{{0.2,0.4,0.1}}");
    check("ImageData(ImageCorrelate(Image({{0.1, 0.2, 0.4}}), {{1, 0, 0}}, Padding -> 0.5))", //
        "{{0.5,0.1,0.2}}");
    check("ImageData(ImageCorrelate(Image({{0.1, 0.2, 0.4}}), {{1, 0, 0}}, "
        + "Padding -> \"Reflected\"))", //
        "{{0.2,0.1,0.2}}");
    check("ImageData(ImagePad(Image({{0.1, 0.2, 0.4}}), {{1, 1}, {0, 0}}, \"Reflected\"))", //
        "{{0.2,0.1,0.2,0.4,0.2}}");
    check("ImageData(ImagePad(Image({{0.1, 0.2, 0.4}}), {{1, 1}, {0, 0}}, \"Reversed\"))", //
        "{{0.1,0.1,0.2,0.4,0.4}}");
  }

  @Test
  public void testTotalVariationFilterImage() {
    check("r = TotalVariationFilter(Image({{12, 240}, {30, 60}}, \"Byte\"), 0); "
        + "{ImageType(r), ImageData(r, \"Byte\")}", //
        "{Byte,{{12,240},{30,60}}}");
    check("r = TotalVariationFilter(Image({{0.1, 0.9}, {0.3, 0.2}}), 0.3); "
        + "{Head(r), ImageType(r)}", //
        "{Image,Real32}");
  }
}
