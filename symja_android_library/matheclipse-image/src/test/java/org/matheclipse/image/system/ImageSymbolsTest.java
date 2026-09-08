package org.matheclipse.image.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Every image processing symbol has to reach the parser.
 *
 * <p>
 * A built-in symbol lives in three hand maintained tables: the constant and the two parallel arrays
 * in <code>ID</code>, the field in <code>S</code>, and the name in one of the string arrays of
 * <code>AST2Expr</code>. <code>SymbolTableTest</code> in <code>matheclipse-core</code> guards the
 * first two against each other, but nothing guards the third: a name that is missing from
 * <code>AST2Expr</code> still compiles, and <code>Dilation</code> then silently parses as
 * <code>Global`Dilation</code> instead of the built-in. That is what the context assertion below
 * catches.
 *
 * <p>
 * These symbols are declared in <code>matheclipse-core</code> - core owns the symbol table - while
 * their evaluators are installed from this module by <code>ImageInit</code>, the same arrangement
 * <code>matheclipse-chem</code> uses for CDK and <code>matheclipse-astro</code> for Orekit. Until
 * an evaluator exists a symbol simply stays unevaluated, which is why this test says nothing about
 * behaviour.
 */
public class ImageSymbolsTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  /** The functions of the image processing guide that this module implements. */
  private static final String[] FUNCTIONS = { //
      "AlphaChannel", "BilateralFilter", "Binarize", "Blur", "BottomHatTransform", "Closing", //
      "ColorCombine", "ColorConvert", "ColorDistance", "ColorNegate", "ColorQuantize", //
      "ColorReplace", "ColorSeparate", "CommonestFilter", "ComponentMeasurements", "CornerFilter", //
      "CrossingDetect", "DeleteBorderComponents", "DeleteSmallComponents", "DerivativeFilter", //
      "Dilation", "DistanceTransform", "EdgeDetect", "EntropyFilter", "Erosion", //
      "FillingTransform", "FindThreshold", "GaussianFilter", "GradientFilter", //
      "GradientOrientationFilter", "HistogramTransform", "ImageAdd", "ImageAdjust", //
      "ImageApply", "ImageAspectRatio", "ImageAssemble", "ImageClip", "ImageCompose", //
      "ImageConvolve", "ImageCorners", "ImageCorrelate", "ImageDeconvolve", "ImageDifference", //
      "ImageDivide", "ImageEffect", "ImageFilter", "ImageForwardTransformation", "ImageKeypoints", //
      "ImageLines", "ImageMeasurements", "ImageMultiply", "ImagePad", "ImagePartition", //
      "ImagePerspectiveTransformation", "ImageReflect", "ImageScan", "ImageSegmentationComponents", //
      "ImageSubtract", "ImageTake", "ImageTransformation", "ImageTrim", "ImageValue", //
      "ImageValuePositions", "Inpaint", "LaplacianFilter", "LaplacianGaussianFilter", //
      "LocalAdaptiveBinarize", "MeanShiftFilter", "MorphologicalBinarize", //
      "MorphologicalComponents", "MorphologicalPerimeter", "MorphologicalTransform", "Opening", //
      "Pruning", "RangeFilter", "Rasterize", "RemoveAlphaChannel", "RemoveBackground", //
      "RidgeFilter", "SelectComponents", "SetAlphaChannel", "Sharpen", "SkeletonTransform", //
      "StandardDeviationFilter", "Thinning", "Thumbnail", "TopHatTransform", //
      "TotalVariationFilter", "WatershedComponents", "WienerFilter"};

  /** The option symbols those functions need that did not exist before. */
  private static final String[] OPTIONS = { //
      "Alpha", "CornerNeighbors", "Dithering", "ImageResolution", "Masking", "MaxFeatures", //
      "Padding", "RasterSize", "Resampling", "Segmented", "Sharpening", "Standardized", //
      "TransformationClass"};

  /** The image symbols that already existed and keep their identity. */
  private static final String[] ESTABLISHED = { //
      "Image", "ImageChannels", "ImageColorSpace", "ImageCrop", "ImageData", "ImageDimensions", //
      "ImageHistogram", "ImageQ", "ImageResize", "ImageRotate", "ImageType", "MaxFilter", //
      "MeanFilter", "MedianFilter", "MinFilter"};

  @Test
  public void functionsAreBuiltInSymbols() {
    for (String name : FUNCTIONS) {
      assertIsBuiltIn(name);
    }
  }

  @Test
  public void optionsAreBuiltInSymbols() {
    for (String name : OPTIONS) {
      assertIsBuiltIn(name);
    }
  }

  @Test
  public void theSymbolsThatAlreadyExistedStillResolve() {
    for (String name : ESTABLISHED) {
      assertIsBuiltIn(name);
    }
  }

  @Test
  public void theListsHaveTheSizeTheGuideCallsFor() {
    assertEquals(90, FUNCTIONS.length);
    assertEquals(13, OPTIONS.length);
  }

  private static void assertIsBuiltIn(String name) {
    ExprParser parser = new ExprParser(EvalEngine.get());
    IExpr symbol = parser.parse(name);
    assertInstanceOf(IBuiltInSymbol.class, symbol,
        name + " parses as " + symbol + ", so it is missing from AST2Expr");
    assertEquals(name, symbol.toString(), "the parsed symbol is not spelled the same");
  }
}
