package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * What the <code>Image</code> object already does, so that the image processing functions have a
 * baseline to be built on.
 */
public class ImageExprTest extends AbstractTestCase {

  @Test
  public void imageDataReturnsThePixelsItWasBuiltFrom() {
    // ImageExpr keeps the source matrix, so this round trip is lossless rather than going
    // through the PNG buffer
    check("ImageData(Image({{0.0,1.0},{1.0,0.0}}))=={{0.0,1.0},{1.0,0.0}}", //
        "True");
  }

  @Test
  public void imageDimensionsIsWidthByHeightUnlikeDimensions() {
    check("ImageDimensions(Image({{0.0,1.0,0.5}}))", //
        "{3,1}");
    check("Dimensions(ImageData(Image({{0.0,1.0,0.5}})))", //
        "{1,3}");
  }

  @Test
  public void greyscalePixelsSurviveThePngBuffer() {
    // "Byte" bypasses the stored matrix and reads the bitmap back, so this is the round trip
    // through the encoder; 0.5 lands on 128 because the sample is rounded rather than truncated
    check("ImageData(Image({{0.0,0.5},{1.0,0.25}},ColorSpace->\"Grayscale\"),\"Byte\")"
        + "=={{0,128},{255,64}}", //
        "True");
  }

  @Test
  public void anImageIsItsOwnHead() {
    check("Head(Image({{0.0,1.0}}))", //
        "Image");
    check("Image({{0.0,1.0}})===Image({{0.0,1.0}})", //
        "True");
  }

  /**
   * The symbols of the image processing guide are declared, but their evaluators are installed one
   * stage at a time. Until then they stay unevaluated rather than failing, which is what makes it
   * safe to declare them all at once.
   */
  @Test
  public void functionsWithoutAnEvaluatorStayUnevaluated() {
    check("EdgeDetect(Image({{0.0,1.0}}),1)", //
        "EdgeDetect(Image(Dimensions: 2,1 Transparency: 1),1)");
  }

  @Test
  public void testRasterizeAndGraphicsAsImages() {
    // any expression is drawn as a text; the image is RGB without transparency
    check("{Head(Rasterize(\"Symja\")), ImageChannels(Rasterize(Graphics(Circle())))}", //
        "{Image,3}");
    check("ImageDimensions(Rasterize(Graphics(Rectangle()), RasterSize -> 30))", //
        "{30,30}");
    check("ImageDimensions(Rasterize(Style(\"B\", 20), RasterSize -> {12, 18}))", //
        "{12,18}");
    // the image functions take a graphic for the image it is drawn to
    check("{Head(ColorNegate(Graphics(Disk()))), Head(ColorConvert(Graphics(Disk()), \"Grayscale\")), "
        + "ImageDimensions(ImageResize(Graphics(Disk()), 16))}", //
        "{Image,Image,{16,16}}");
  }

  /** Images are told apart by their samples, so that Union, Sort and Tally keep them apart. */
  @Test
  public void imagesAreOrderedByTheirSamples() {
    check("{Length(Union({Image({{0.3, 0.6}}), Image({{0.3, 0.7}})})), "
        + "Length(Union({Image({{0.3, 0.6}}), Image({{0.3, 0.6}})})), "
        + "Length(Tally({Image({{0.4}}), Image({{0.6}}), Image({{0.4}})})), "
        + "Length(DeleteDuplicates({Image({{0.4}}), Image({{0.6}})}))}", //
        "{2,1,2,2}");
    check("{Image({{0.4}}) === Image({{0.4}}), Image({{0.4}}) === Image({{0.40001}}), "
        + "Image({{0.4}}) === Image({{0.4}}, \"Real64\")}", //
        "{True,False,False}");
    check("Flatten(ImageData /@ Sort({Image({{0.7}}), Image({{0.1}}), Image({{0.4}})}))", //
        "{0.1,0.4,0.7}");
  }

  /** The options of an image, and a single one of them at its default. */
  @Test
  public void optionsOfAnImage() {
    check("Options(Image({{0.3, 0.6}}))", //
        "{ColorSpace->Automatic,Interleaving->None}");
    check("Options(Image({{{0.1, 0.2, 0.3}}}, ColorSpace -> \"RGB\"))", //
        "{ColorSpace->RGB,Interleaving->True}");
    check("Options(Image({{0.3, 0.6}}), {ImageSize, ColorSpace})", //
        "{ColorSpace->Automatic,ImageSize->Automatic}");
    check("Options(Image({{0.3, 0.6}}, ImageSize -> 40), ImageSize)", //
        "{ImageSize->40}");
  }
  @Test
  public void inputFormWritesTheSamplesAsANumericArray() {
    // the samples of a Real32 image in single precision
    check("ToString(InputForm(Image({{0.1, 0.75}}, \"Real32\")))", //
        "Image(NumericArray({{0.10000000149011612`,0.75`}},\"Real32\"),\"Real32\","
            + "ColorSpace->Automatic,Interleaving->None)");
    check("InputForm(Image({{{7, 8, 9}, {250, 251, 252}}}, \"Byte\"))", //
        "Image(NumericArray({{{7,8,9},{250,251,252}}},\"UnsignedInteger8\"),\"Byte\","
            + "ColorSpace->Automatic,Interleaving->True)");
    check("InputForm({Image({{0, 1, 1}}, \"Bit\"), Image({{3, 60000}}, \"Bit16\"), "
        + "Image({{0.3, 0.6}}, \"Real64\", ColorSpace -> \"Grayscale\")})", //
        "{Image(NumericArray({{0,1,1}},\"Integer8\"),\"Bit\","
            + "ColorSpace->Automatic,Interleaving->None),"
            + "Image(NumericArray({{3,60000}},\"UnsignedInteger16\"),\"Bit16\","
            + "ColorSpace->Automatic,Interleaving->None),"
            + "Image(NumericArray({{0.3`,0.6`}},\"Real64\"),\"Real64\","
            + "ColorSpace->\"Grayscale\",Interleaving->None)}");
    // planar data stays planar
    check("InputForm(Image({{{1, 0}}, {{0, 1}}, {{0, 0}}}, \"Byte\", Interleaving -> False))", //
        "Image(NumericArray({{{1,0}},{{0,1}},{{0,0}}},\"UnsignedInteger8\"),\"Byte\","
            + "ColorSpace->Automatic,Interleaving->False)");
    // and every one of them reads back as the same image
    check("imgs = {Image({{0.1, 0.35}, {0.55, 0.9}}, \"Real32\"), "
        + "Image({{0.1, 0.35}}, \"Real64\"), Image({{{7, 8, 9}}}, \"Byte\"), "
        + "Image({{1, 0}}, \"Bit\"), Image({{3, 60000}}, \"Bit16\"), "
        + "Image({{{0.1, 0.2}}, {{0.3, 0.4}}, {{0.5, 0.6}}}, Interleaving -> False)}; "
        + "Map(ToExpression(ToString(InputForm(#))) == # &, imgs)", //
        "{True,True,True,True,True,True}");
    check("ImageType /@ Map(ToExpression(ToString(InputForm(#))) &, imgs)", //
        "{Real32,Real64,Byte,Bit,Bit16,Real32}");
    // the other forms stay the summary
    check("ToString(Image({{0.1, 0.75}}))", //
        "Image(Dimensions: 2,1 Transparency: 1)");
  }

  @Test
  public void real32ImagesAreComparedInSinglePrecision() {
    check("{Image({{0.3, 0.7}}) === Image({{0.30000001, 0.7}}), "
        + "Image({{0.3, 0.7}}) === Image({{0.3001, 0.7}}), "
        + "Image({{0.3, 0.7}}, \"Real64\") === Image({{0.30000001, 0.7}}, \"Real64\")}", //
        "{True,False,False}");
  }
}
