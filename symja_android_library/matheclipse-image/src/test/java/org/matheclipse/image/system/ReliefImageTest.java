package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * <code>ReliefImage</code> - an array of heights lit as though it were a landscape seen from
 * directly overhead.
 *
 * <p>
 * Most of these switch the light off with <code>LightingAngle -&gt; None</code>, because colour and
 * shading are separate concerns and a test of one should not have to predict the other. The shading
 * is checked on its own further down, against surfaces whose slope is known.
 */
public class ReliefImageTest extends AbstractTestCase {

  /** One pixel per height, and always an 8 bit RGB image. */
  @Test
  public void theResultIsAnRgbImageWithOnePixelPerHeight() {
    check("Head(ReliefImage({{0,1},{1,0}}))", //
        "Image");
    // ImageDimensions is {width, height}, so a 2 row 3 column array is 3 by 2
    check("ImageDimensions(ReliefImage({{0,1,2},{3,4,5}}))", //
        "{3,2}");
    check("ImageChannels(ReliefImage({{0,1},{1,0}}))", //
        "3");
    check("ImageColorSpace(ReliefImage({{0,1},{1,0}}))", //
        "RGB");
    check("ImageType(ReliefImage({{0,1},{1,0}}))", //
        "Byte");
    check("ImageDimensions(ReliefImage({{1}}))", //
        "{1,1}");
  }

  @Test
  public void everyDocumentedOptionIsDeclared() {
    check("Options(ReliefImage)", //
        "{ClippingStyle->{RGBColor(0,0,0),RGBColor(1,1,1)},ColorFunction->Automatic,"
            + "ColorFunctionScaling->True,ImageSize->Automatic,LightingAngle->Automatic,"
            + "Method->Automatic,PlotRange->All}");
  }

  // ------------------------------------------------------------------- shading

  /**
   * A flat surface is lit by however high the light stands: the default is half way up the sky, so
   * <code>sin(Pi/4) = 0.7071</code> of the light reaches it and white comes out at 180.
   */
  @Test
  public void aFlatSurfaceIsLitByTheHeightOfTheLightAlone() {
    check("ImageData(ReliefImage({{1,1},{1,1}}),\"Byte\")"
        + "=={{{180,180,180},{180,180,180}},{{180,180,180},{180,180,180}}}", //
        "True");
    // straight overhead, and none of it is lost
    check("ImageData(ReliefImage({{1,1}},LightingAngle->{0,Pi/2}),\"Byte\")"
        + "=={{{255,255,255},{255,255,255}}}", //
        "True");
    // a constant array has no range to scale, and comes out flat rather than undefined
    check("ImageData(ReliefImage({{5,5},{5,5}},LightingAngle->None),\"Byte\")"
        + "=={{{255,255,255},{255,255,255}},{{255,255,255},{255,255,255}}}", //
        "True");
  }

  /**
   * A slope rising to the right presents its face to the left, so it is lit by a light on the left
   * - 180 degrees - and dark under one on the right.
   */
  @Test
  public void aSlopeIsLitFromTheSideItFaces() {
    check("ImageData(ReliefImage({{0,1,2,3}},LightingAngle->180 Degree),\"Byte\")"
        + "=={{{255,255,255},{255,255,255},{255,255,255},{255,255,255}}}", //
        "True");
    check("ImageData(ReliefImage({{0,1,2,3}},LightingAngle->0 Degree),\"Byte\")"
        + "=={{{0,0,0},{0,0,0},{0,0,0},{0,0,0}}}", //
        "True");
    // rows run down the image, so an array rising down the page faces up: lit from 90 degrees
    check("ImageData(ReliefImage({{0},{1},{2},{3}},LightingAngle->90 Degree),\"Byte\")"
        + "=={{{255,255,255}},{{255,255,255}},{{255,255,255}},{{255,255,255}}}", //
        "True");
    check("ImageData(ReliefImage({{0},{1},{2},{3}},LightingAngle->270 Degree),\"Byte\")"
        + "=={{{0,0,0}},{{0,0,0}},{{0,0,0}},{{0,0,0}}}", //
        "True");
  }

  @Test
  public void lightingAngleNoneLeavesTheColoursAlone() {
    check("ImageData(ReliefImage({{0,1},{1,0}},LightingAngle->None),\"Byte\")"
        + "=={{{255,255,255},{255,255,255}},{{255,255,255},{255,255,255}}}", //
        "True");
  }

  /**
   * The heights are read as a surface over the unit square, so the same surface sampled more finely
   * is the same picture rather than a flatter one.
   */
  @Test
  public void theShadingDoesNotDependOnHowFinelyTheSurfaceIsSampled() {
    check("ImageData(ReliefImage({{0,1,2}},LightingAngle->180 Degree),\"Byte\")"
        + "=={{{255,255,255},{255,255,255},{255,255,255}}}", //
        "True");
    check("ImageData(ReliefImage({{0,0.5,1,1.5,2}},LightingAngle->180 Degree),\"Byte\")"
        + "=={{{255,255,255},{255,255,255},{255,255,255},{255,255,255},{255,255,255}}}", //
        "True");
  }

  /**
   * Diffuse reflection cares how steep a slope is; aspect based shading asks only which way it
   * faces. The same ramp over a wider plot range is a gentler hill, and only the first method
   * notices.
   */
  @Test
  public void aspectBasedShadingIgnoresHowSteepTheSlopeIs() {
    check("ImageData(ReliefImage({{0,1,2,3}},PlotRange->{0,20},LightingAngle->180 Degree),\"Byte\")"
        + "=={{{205,205,205},{205,205,205},{205,205,205},{205,205,205}}}", //
        "True");
    check("ImageData(ReliefImage({{0,1,2,3}},PlotRange->{0,20},LightingAngle->180 Degree,"
        + "Method->\"AspectBasedShading\"),\"Byte\")"
        + "=={{{255,255,255},{255,255,255},{255,255,255},{255,255,255}}}", //
        "True");
    // and a surface with no slope at all faces nowhere, so it lands in the middle
    check("ImageData(ReliefImage({{1,1}},Method->\"AspectBasedShading\"),\"Byte\")"
        + "=={{{128,128,128},{128,128,128}}}", //
        "True");
  }

  // ------------------------------------------------------------------- colour

  @Test
  public void colorFunctionGivesEachHeightItsColour() {
    check("ImageData(ReliefImage({{0,1},{1,0}},ColorFunction->Function(RGBColor(1,0,0)),"
        + "LightingAngle->None),\"Byte\")"
        + "=={{{255,0,0},{255,0,0}},{{255,0,0},{255,0,0}}}", //
        "True");
    check("ImageData(ReliefImage({{0,1},{1,0}},ColorFunction->(GrayLevel(#)&),"
        + "LightingAngle->None),\"Byte\")"
        + "=={{{0,0,0},{255,255,255}},{{255,255,255},{0,0,0}}}", //
        "True");
  }

  /**
   * <code>ColorFunctionScaling -&gt; True</code>, the default, hands the colour function the height
   * scaled onto 0 ... 1; <code>False</code> hands it the height itself.
   */
  @Test
  public void colorFunctionScalingDecidesWhatTheFunctionIsAsked() {
    check("ImageData(ReliefImage({{0,0.5}},ColorFunction->(GrayLevel(#)&),"
        + "LightingAngle->None),\"Byte\")=={{{0,0,0},{255,255,255}}}", //
        "True");
    check("ImageData(ReliefImage({{0,0.5}},ColorFunction->(GrayLevel(#)&),"
        + "ColorFunctionScaling->False,LightingAngle->None),\"Byte\")"
        + "=={{{0,0,0},{128,128,128}}}", //
        "True");
  }

  /** The lighting darkens whatever colour came out, so the two are independent. */
  @Test
  public void theLightingDarkensTheColour() {
    check("ImageData(ReliefImage({{1,1}},ColorFunction->Function(RGBColor(1,0,0))),\"Byte\")"
        + "=={{{180,0,0},{180,0,0}}}", //
        "True");
  }

  // ------------------------------------------------------------------- range

  /**
   * Heights outside the plot range are marked rather than merely clamped, black below and white
   * above unless another style is asked for.
   */
  @Test
  public void clippingStyleMarksHeightsOutsideThePlotRange() {
    check("ImageData(ReliefImage({{-1,0,1,2}},PlotRange->{0,1},"
        + "ColorFunction->Function(RGBColor(1,0,0)),LightingAngle->None),\"Byte\")"
        + "=={{{0,0,0},{255,0,0},{255,0,0},{255,255,255}}}", //
        "True");
    check("ImageData(ReliefImage({{-1,0,1,2}},PlotRange->{0,1},"
        + "ColorFunction->Function(RGBColor(1,0,0)),ClippingStyle->{Green,Blue},"
        + "LightingAngle->None),\"Byte\")"
        + "=={{{0,255,0},{255,0,0},{255,0,0},{0,0,255}}}", //
        "True");
    // ClippingStyle -> None leaves the colour function to speak for the clipped heights too
    check("ImageData(ReliefImage({{-1,0,1,2}},PlotRange->{0,1},"
        + "ColorFunction->Function(RGBColor(1,0,0)),ClippingStyle->None,"
        + "LightingAngle->None),\"Byte\")"
        + "=={{{255,0,0},{255,0,0},{255,0,0},{255,0,0}}}", //
        "True");
  }

  /** A clipped height is still part of the landscape, so the light falls on it as well. */
  @Test
  public void clippedHeightsAreShadedToo() {
    check("ImageData(ReliefImage({{2,2},{2,2}},PlotRange->{0,1}),\"Byte\")"
        + "=={{{180,180,180},{180,180,180}},{{180,180,180},{180,180,180}}}", //
        "True");
  }

  // ------------------------------------------------------------------- refusals

  @Test
  public void whatIsNotAnArrayOfHeightsIsLeftAlone() {
    check("ReliefImage(7)", //
        "ReliefImage(7)");
    check("ReliefImage({1,2,3})", //
        "ReliefImage({1,2,3})");
    check("ReliefImage({{0,1},{1}})", //
        "ReliefImage({{0,1},{1}})");
    // a height that is not a number has no place on a surface
    check("ReliefImage({{0,x},{1,0}})", //
        "ReliefImage({{0,x},{1,0}})");
    // a plot range that runs backwards says nothing
    check("ReliefImage({{0,1}},PlotRange->{2,1})", //
        "ReliefImage({{0,1}},PlotRange->{2,1})");
  }

  /** <code>ImageSize</code> is a display option and does not change a single pixel. */
  @Test
  public void imageSizeDoesNotChangeThePixels() {
    check("ImageDimensions(ReliefImage({{0,1},{1,0}},ImageSize->200))", //
        "{2,2}");
    check("ImageData(ReliefImage({{0,1},{1,0}},ImageSize->200),\"Byte\")"
        + "==ImageData(ReliefImage({{0,1},{1,0}}),\"Byte\")", //
        "True");
  }
}
