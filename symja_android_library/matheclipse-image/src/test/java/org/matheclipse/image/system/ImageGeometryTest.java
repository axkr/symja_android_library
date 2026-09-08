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
}
