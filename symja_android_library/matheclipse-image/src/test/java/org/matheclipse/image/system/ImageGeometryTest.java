package org.matheclipse.image.system;

import org.junit.jupiter.api.Test;

/**
 * Where the pixels are.
 *
 * <p>
 * The image these tests use is <code>Image({{0.0,1.0},{0.25,0.5}})</code>, whose samples as bytes
 * are <code>{{0,255},{64,128}}</code>; every expectation below is that matrix moved around.
 */
public class ImageGeometryTest extends AbstractTestCase {

  private static final String IMAGE = "Image({{0.0,1.0},{0.25,0.5}})";

  @Test
  public void resizeTakesAWidthAPairOrAFactor() {
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{4,8})),4))", "{4,2}");
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{4,8})),{4,2}))", "{4,2}");
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{4,8})),{4,Automatic}))", "{4,2}");
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{4,8})),Scaled(0.5)))", "{4,2}");
  }

  /** Shrinking averages the pixels that fall together rather than dropping all but one. */
  @Test
  public void shrinkingAveragesTheSamples() {
    check("ImageData(ImageResize(Image({{0.0,1.0}}),{1,1}),\"Byte\")", "{{128}}");
  }

  /** Without an angle the image turns a quarter turn counterclockwise. */
  @Test
  public void rotateDefaultsToAQuarterTurn() {
    check("ImageData(ImageRotate(" + IMAGE + "),\"Byte\")", "{{255,128},{0,64}}");
  }

  /** A whole number of quarter turns is a transposition, so no sample is interpolated. */
  @Test
  public void aHalfTurnIsExact() {
    check("ImageData(ImageRotate(" + IMAGE + ",Pi),\"Byte\")", "{{128,64},{255,0}}");
  }

  @Test
  public void rotatingGrowsTheCanvasToFit() {
    check("ImageDimensions(ImageRotate(Image(ConstantArray(0.5,{4,8})),Pi/4))", "{9,9}");
    check("ImageDimensions(ImageRotate(Image(ConstantArray(0.5,{4,8})),Pi/4,{8,4}))", "{8,4}");
  }

  @Test
  public void reflectDefaultsToTopToBottom() {
    check("ImageData(ImageReflect(" + IMAGE + "),\"Byte\")", "{{64,128},{0,255}}");
    check("ImageData(ImageReflect(" + IMAGE + ",Left),\"Byte\")", "{{255,0},{128,64}}");
  }

  /** A rule between two sides that are not opposite reflects about the diagonal between them. */
  @Test
  public void reflectAboutADiagonalTransposes() {
    check("ImageData(ImageReflect(" + IMAGE + ",Top->Left),\"Byte\")", "{{0,64},{255,128}}");
    check("ImageData(ImageReflect(" + IMAGE + ",Left->Right),\"Byte\")", "{{255,0},{128,64}}");
  }

  @Test
  public void padGrowsTheImage() {
    check("ImageDimensions(ImagePad(Image({{0.5}}),2))", "{5,5}");
    check("ImageDimensions(ImagePad(Image(ConstantArray(0.5,{4,4})),{{1,2},{3,4}}))", "{7,11}");
  }

  @Test
  public void padTakesAColor() {
    check("ImageData(ImagePad(Image({{0.5}}),1,Black),\"Byte\")", //
        "{{0,0,0},{0,128,0},{0,0,0}}");
    check("ImageData(ImagePad(Image({{0.5}}),1,White),\"Byte\")", //
        "{{255,255,255},{255,128,255},{255,255,255}}");
  }

  /** ImageTake counts rows and columns of the matrix, from the top, the way Take does. */
  @Test
  public void takeCountsRowsFromTheTop() {
    check("ImageData(ImageTake(" + IMAGE + ",1),\"Byte\")", "{{0,255}}");
    check("ImageData(ImageTake(" + IMAGE + ",-1),\"Byte\")", "{{64,128}}");
    check("ImageData(ImageTake(" + IMAGE + ",{1,2},{2,2}),\"Byte\")", "{{255},{128}}");
  }

  /** ImageTrim uses image coordinates, so its y counts from the bottom. */
  @Test
  public void trimCutsOutARegionInImageCoordinates() {
    check("ImageData(ImageTrim(" + IMAGE + ",{{0,0},{1,1}}),\"Byte\")", "{{64}}");
    check("ImageData(ImageTrim(" + IMAGE + ",{{0,1},{1,2}}),\"Byte\")", "{{0}}");
  }

  @Test
  public void composePutsOneImageOnAnother() {
    check("ImageDimensions(ImageCompose(Image(ConstantArray(0.0,{4,4})),"
        + "Image(ConstantArray(1.0,{2,2}))))", //
        "{4,4}");
    check("ImageData(ImageCompose(Image(ConstantArray(0.0,{2,2})),"
        + "Image(ConstantArray(1.0,{2,2}))),\"Byte\")", //
        "{{255,255},{255,255}}");
  }

  /** The position in the background, and the point of the overlay which is put there. */
  @Test
  public void composePutsAPointOfTheOverlayOnAPosition() {
    check("ImageData(ImageCompose(Image(ConstantArray(0,{3,3})),Image({{1}}),{0.5,2.5}))", //
        "{{1.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}}");
    check("ImageData(ImageCompose(Image(ConstantArray(0,{3,3})),Image({{1}}),{Right,Top},{Right,Top}))", //
        "{{0.0,0.0,1.0},{0.0,0.0,0.0},{0.0,0.0,0.0}}");
    check("ImageData(ImageCompose(Image(ConstantArray(0,{3,3})),Image({{1}}),{Left,Bottom},{Left,Bottom}))", //
        "{{0.0,0.0,0.0},{0.0,0.0,0.0},{1.0,0.0,0.0}}");
  }

  /** Scaled with a pair scales the two directions on their own. */
  @Test
  public void resizeScalesTheTwoDirections() {
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{60,80})),Scaled({0.5,0.25})))", //
        "{40,15}");
    check("ImageDimensions(ImageResize(Image(ConstantArray(0.5,{60,80})),Scaled(0.5)))", //
        "{40,30}");
  }

  @Test
  public void composeBlendsWithAnOpacity() {
    check("ImageData(ImageCompose(Image(ConstantArray(0.0,{2,2})),"
        + "{Image(ConstantArray(1.0,{2,2})),0.5}),\"Byte\")", //
        "{{128,128},{128,128}}");
  }

  @Test
  public void thumbnailScalesTheLargestDimension() {
    check("ImageDimensions(Thumbnail(Image(ConstantArray(0.5,{100,200}))))", "{48,24}");
    check("ImageDimensions(Thumbnail(Image(ConstantArray(0.5,{100,200})),10))", "{10,5}");
  }

  @Test
  public void rasterizeDrawsAGraphicsObject() {
    check("Head(Rasterize(Graphics(Disk())))", "Image");
    check("Rasterize(Image({{0.5}}))", "Image(Dimensions: 1,1 Transparency: 1)");
  }

  /**
   * The function of <code>ImageTransformation</code> runs backwards - it says where each output
   * pixel reads from - so shifting the read position right moves the picture left.
   */
  @Test
  public void transformationReadsBackwards() {
    check("ImageData(ImageTransformation(" + IMAGE + ",#+{1,0}&),\"Byte\")", //
        "{{255,255},{128,255}}");
  }

  /** The forward version of the same translation moves the picture the other way. */
  @Test
  public void forwardTransformationMovesThePicture() {
    check("ImageData(ImageForwardTransformation(" + IMAGE + ",{{1,0,1},{0,1,0},{0,0,1}}),\"Byte\")",
        "{{255,0},{255,64}}");
  }

  @Test
  public void aTransformationFunctionIsAccepted() {
    check("ImageData(ImageTransformation(" + IMAGE
        + ",TransformationFunction({{1,0,1},{0,1,0},{0,0,1}})),\"Byte\")", //
        "{{255,255},{128,255}}");
  }

  /**
   * With a <code>DataRange</code> the image rectangle gets coordinates with y growing upwards, so
   * mirroring y turns the rows upside down and mirroring x the columns.
   */
  @Test
  public void transformationDataRangeMirrors() {
    check("ImageData(ImageTransformation(Image({{0.25,0.75},{0.5,1.0}}),{#[[1]],4-#[[2]]}&,"
        + "DataRange->{{-1,1},{0,4}}))", //
        "{{0.5,1.0},\n {0.25,0.75}}");
    check("ImageData(ImageTransformation(Image({{0.25,0.75,0.0},{0.5,1.0,0.125}}),"
        + "{1-#[[1]],#[[2]]}&,DataRange->{{0,1},{0,1}}))", //
        "{{0.0,0.75,0.25},\n {0.125,1.0,0.5}}");
  }

  /**
   * <code>DataRange -&gt; Full</code> measures in pixels. Between pixel centres the samples are
   * interpolated, and beyond the outermost centres the padding, 0 by default, is a ring of pixels.
   */
  @Test
  public void transformationDataRangeFullInterpolates() {
    check("ImageData(ImageTransformation(Image({{0.25,0.75,0.5}}),{#[[1]]-0.5,#[[2]]}&,"
        + "DataRange->Full))", //
        "{{0.125,0.5,0.625}}");
    check("ImageData(ImageTransformation(Image({{0.25,0.75,0.5}}),{#[[1]]+0.5,#[[2]]}&,"
        + "DataRange->Full))", //
        "{{0.5,0.625,0.25}}");
    // two pixels further out only the padding is left
    check("ImageData(ImageTransformation(Image({{0.25,0.75,0.5}}),{#[[1]]+5,#[[2]]}&,"
        + "DataRange->Full))", //
        "{{0.0,0.0,0.0}}");
  }

  /** A padding colour is converted to the colour space the samples are stored in. */
  @Test
  public void transformationPaddingInAStoredColourSpace() {
    check("ImageData(ImageTransformation(Image({{{0.,0.,0.,0.5}}},ColorSpace->\"CMYK\"),"
        + "{#[[1]]+1,#[[2]]}&,DataRange->Full,Padding->Blue))", //
        "{{{1.0,1.0,0.0,0.0}}}");
    check("ImageData(ImageTransformation(Image({{{0.5,0.5,0.5}}},ColorSpace->\"HSB\"),"
        + "{#[[1]]+1,#[[2]]}&,DataRange->Full,Padding->Green))", //
        "{{{0.333333,1.0,1.0}}}");
    // no colour for five channels
    check("Head(ImageTransformation(Image(ConstantArray(0.5,{1,1,5})),#&,Padding->Red))", //
        "ImageTransformation");
  }

  @Test
  public void transformationPadding() {
    check("ImageData(ImageTransformation(Image({{0.25,0.75},{0.5,1.0}}),{#[[1]]-1,#[[2]]}&,"
        + "DataRange->Full,Padding->0.125))", //
        "{{0.125,0.25},\n {0.125,0.5}}");
    // the border repeated
    check("ImageData(ImageTransformation(Image({{0.25,0.75},{0.5,1.0}}),{#[[1]]+0.5,#[[2]]-2}&,"
        + "DataRange->Full,Padding->\"Fixed\"))", //
        "{{0.75,1.0},\n {0.75,1.0}}");
    // the image repeated
    check("ImageData(ImageTransformation(Image({{0.25,0.75},{0.5,1.0}}),{#[[1]]+3,#[[2]]}&,"
        + "DataRange->Full,Padding->\"Periodic\"))", //
        "{{0.75,0.25},\n {1.0,0.5}}");
    check("ImageTransformation(Image({{0.5}}),#&,Padding->\"Unknown\")", //
        "ImageTransformation(Image(Dimensions: 1,1 Transparency: 1),#1&,Padding->Unknown)");
    // an empty rectangle
    check("Head(ImageTransformation(Image({{0.5}}),#&,DataRange->{{1,1},{0,1}}))", //
        "ImageTransformation");
  }

  /** A colour image is padded with a colour, or with one value per channel. */
  @Test
  public void transformationPaddingOfAColourImage() {
    check("ImageData(ImageTransformation(Image({{{0.25,0.5,0.75},{1.0,0.0,0.5}}}),"
        + "{#[[1]]-1,#[[2]]}&,DataRange->Full,Padding->Blue))", //
        "{{{0.0,0.0,1.0},{0.25,0.5,0.75}}}");
    check("ImageData(ImageTransformation(Image({{{0.25,0.5,0.75},{1.0,0.0,0.5}}}),"
        + "{#[[1]]+1,#[[2]]}&,DataRange->Full,Padding->{0.125,0.375,0.625}))", //
        "{{{1.0,0.0,0.5},{0.125,0.375,0.625}}}");
  }

  /** The size of the result is a third argument also when options follow. */
  @Test
  public void transformationSizeAndDataRange() {
    check("ImageData(ImageTransformation(Image({{0.25,0.75}}),#&,{4,1},DataRange->{{0,1},{0,1}},"
        + "Padding->\"Fixed\"))", //
        "{{0.25,0.375,0.625,0.75}}");
    check("ImageData(ImageTransformation(Image({{0.25,0.75},{0.5,1.0}}),"
        + "TransformationFunction({{1,0,0},{0,1,1},{0,0,1}}),DataRange->{{0,2},{0,2}}))", //
        "{{0.0,0.0},\n {0.25,0.75}}");
  }

  /** Running a general function forwards would leave holes, so only a matrix is accepted there. */
  @Test
  public void forwardTransformationRejectsAPlainFunction() {
    check("ImageForwardTransformation(Image({{0.5}}),#&)", //
        "ImageForwardTransformation(Image(Dimensions: 1,1 Transparency: 1),#1&)");
  }

  @Test
  public void perspectiveTransformationKeepsTheSize() {
    check("ImageDimensions(ImagePerspectiveTransformation(Image(ConstantArray(0.5,{4,4})),"
        + "{{1,0,0},{0,1,0},{0,0,1}}))", //
        "{4,4}");
  }

  @Test
  public void imageComposeRasterizesGraphics() {
    check("Head(ImageCompose(Graphics(Rectangle()), {Graphics(Disk()), 0.25}))", //
        "Image");
    check("Head(ImageCompose(Image(ConstantArray(0.5, {20, 20})), Graphics(Circle())))", //
        "Image");
  }
}
