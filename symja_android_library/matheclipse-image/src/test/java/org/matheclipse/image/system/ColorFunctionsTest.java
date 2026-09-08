package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/** Colour conversion, separation, quantization and the alpha channel. */
public class ColorFunctionsTest extends AbstractTestCase {

  @Test
  public void colorNegateOnADirective() {
    check("ColorNegate(RGBColor(1.0,0.0,0.25))", "RGBColor(0.0,1.0,0.75)");
  }

  @Test
  public void colorNegateOnAnImage() {
    check("ImageData(ColorNegate(Image({{0.0,1.0}})),\"Byte\")", "{{255,0}}");
  }

  /** Negating a colour leaves its transparency alone. */
  @Test
  public void colorNegateKeepsAlpha() {
    check("ImageData(ColorNegate(Image({{{1.0,0.0,0.0,0.5}}})),\"Byte\")", //
        "{{{0,255,255,128}}}");
  }

  @Test
  public void colorConvertBetweenDirectives() {
    check("ColorConvert(RGBColor(1.0,0.0,0.0),\"HSB\")", "Hue(0.0,1.0,1.0)");
    check("ColorConvert(RGBColor(1.0,0.0,0.0),\"Grayscale\")", "GrayLevel(0.299)");
    check("ColorConvert(GrayLevel(0.5),\"RGB\")", "RGBColor(0.5,0.5,0.5)");
    check("ColorConvert(CMYKColor(0.0,1.0,1.0,0.0),\"RGB\")", "RGBColor(1.0,0.0,0.0)");
  }

  @Test
  public void colorConvertOfAnImage() {
    check("ImageChannels(ColorConvert(Image({{{1.0,0.0,0.0}}}),\"Grayscale\"))", "1");
    check("ImageData(ColorConvert(Image({{0.5}}),\"RGB\"),\"Byte\")", "{{{128,128,128}}}");
  }

  /**
   * An image has nowhere to record a colour space, so only the two it can actually be in are
   * accepted. See the class comment of <code>ColorFunctions</code>.
   */
  @Test
  public void anImageCannotBeConvertedIntoASpaceItCannotStore() {
    check("ColorConvert(Image({{{1.0,0.5,0.25}}}),\"HSB\")", //
        "ColorConvert(Image(Dimensions: 1,1 Transparency: 1),HSB)");
    check("ColorConvert(Image({{{1.0,0.5,0.25}}}),\"LAB\")", //
        "ColorConvert(Image(Dimensions: 1,1 Transparency: 1),LAB)");
  }

  @Test
  public void colorSeparateGivesOnePlanePerChannel() {
    check("Length(ColorSeparate(Image({{{1.0,0.5,0.0}}})))", "3");
    check("ImageData(ColorSeparate(Image({{{1.0,0.5,0.0}}}),\"R\"),\"Byte\")", "{{255}}");
    check("ImageData(ColorSeparate(Image({{{1.0,0.5,0.0}}}),\"B\"),\"Byte\")", "{{0}}");
  }

  /** Planes have no colour space of their own, so these conversions are well defined. */
  @Test
  public void colorSeparateReachesTheOtherSpacesOnePlaneAtATime() {
    check("Length(ColorSeparate(Image({{{1.0,0.5,0.25}}}),\"HSB\"))", "3");
    check("Length(ColorSeparate(Image({{{1.0,0.5,0.25}}}),\"CMYK\"))", "4");
    check("ColorSeparate(Image({{{1.0,0.5,0.0}}}),\"LAB\")", //
        "ColorSeparate(Image(Dimensions: 1,1 Transparency: 1),LAB)");
  }

  @Test
  public void colorCombineIsTheInverseOfColorSeparate() {
    check("ImageData(ColorCombine(ColorSeparate(Image({{{1.0,0.5,0.25}}})),\"RGB\"),\"Byte\")", //
        "{{{255,128,64}}}");
    check("ImageData(ColorCombine(ColorSeparate(Image({{{1.0,0.5,0.25}}}),\"CMYK\"),\"CMYK\"),"
        + "\"Byte\")", //
        "{{{255,128,64}}}");
    // the hue plane loses a little to the 8 bit storage on the way round
    check("ImageData(ColorCombine(ColorSeparate(Image({{{1.0,0.5,0.25}}}),\"HSB\"),\"HSB\"),"
        + "\"Byte\")", //
        "{{{255,127,64}}}");
  }

  @Test
  public void colorCombineBuildsAnImageFromPlanes() {
    check("ImageData(ColorCombine({Image({{1.0}}),Image({{0.5}}),Image({{0.0}})}),\"Byte\")", //
        "{{{255,128,0}}}");
  }

  /** Median cut collapses each cluster of colours onto its mean. */
  @Test
  public void colorQuantizeReducesThePalette() {
    check("ImageData(ColorQuantize(Image({{{1.0,0.0,0.0},{0.9,0.0,0.0},{0.0,0.0,1.0},"
        + "{0.0,0.0,0.9}}}),2),\"Byte\")", //
        "{{{243,0,0},{243,0,0},{0,0,243},{0,0,243}}}");
  }

  @Test
  public void colorReplaceSwapsOneColorForAnother() {
    check("ImageData(ColorReplace(Image({{{1.0,0.0,0.0},{0.0,0.0,1.0}}}),"
        + "RGBColor(1.0,0.0,0.0)->RGBColor(0.0,1.0,0.0)),\"Byte\")", //
        "{{{0,255,0},{0,0,255}}}");
  }

  /** CIE76 measures in a space whose lightness axis runs from 0 to 100. */
  @Test
  public void colorDistanceIsTheCie76Difference() {
    check("ColorDistance(RGBColor(0.0,0.0,0.0),RGBColor(1.0,1.0,1.0))", "100.0");
    check("ColorDistance(RGBColor(1.0,0.0,0.0),RGBColor(1.0,0.0,0.0))", "0.0");
  }

  /** Against an image it gives a greyscale picture of the distance, scaled into 0...1. */
  @Test
  public void colorDistanceAgainstAnImage() {
    check("ImageData(ColorDistance(Image({{{1.0,1.0,1.0}}}),RGBColor(0.0,0.0,0.0)))", "{{1.0}}");
  }

  @Test
  public void alphaChannelIsAGreyscalePlane() {
    check("ImageData(AlphaChannel(Image({{{1.0,0.0,0.0,0.5}}})),\"Byte\")", "{{128}}");
  }

  /** An image without transparency is fully opaque, not an error. */
  @Test
  public void alphaChannelOfAnOpaqueImageIsWhite() {
    check("ImageData(AlphaChannel(Image({{0.5}})),\"Byte\")", "{{255}}");
  }

  @Test
  public void setAlphaChannelTakesANumberOrAPlane() {
    check("ImageChannels(SetAlphaChannel(Image({{0.5}}),0.25))", "4");
    check("ImageData(SetAlphaChannel(Image({{0.5}}),0.25),\"Byte\")", "{{{128,128,128,64}}}");
    check("ImageData(SetAlphaChannel(Image({{0.0,1.0}}),Image({{1.0,0.0}})),\"Byte\")", //
        "{{{0,0,0,255},{255,255,255,0}}}");
  }

  /** Removing transparency composes onto white unless another background is given. */
  @Test
  public void removeAlphaChannelComposesOntoABackground() {
    check("ImageData(RemoveAlphaChannel(Image({{{0.0,0.0,0.0,0.5}}})),\"Byte\")", //
        "{{{127,127,127}}}");
    check("ImageData(RemoveAlphaChannel(Image({{{0.0,0.0,0.0,0.5}}}),Black),\"Byte\")", //
        "{{{0,0,0}}}");
  }

  @Test
  public void removingAlphaFromAnOpaqueImageChangesNothing() {
    check("ImageChannels(RemoveAlphaChannel(Image({{{1.0,0.0,0.0}}})))", "3");
  }
}
