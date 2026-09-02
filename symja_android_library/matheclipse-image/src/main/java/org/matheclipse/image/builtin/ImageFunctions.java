package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/** <code>Image</code>, <code>ImageData</code> and <code>ImageDimensions</code>. */
public class ImageFunctions {

  private static class Initializer {

    private static void init() {
      S.Image.setEvaluator(new Image());
      S.ImageData.setEvaluator(new ImageData());
      S.ImageDimensions.setEvaluator(new ImageDimensions());
    }
  }

  /**
   * <pre>
   * Image(data)
   * </pre>
   *
   * <blockquote>
   * <p>
   * an image with the pixel values <code>data</code>, a matrix of intensities for a greyscale image
   * or a matrix of channel triples or quadruples for a colour image.
   * </p>
   * </blockquote>
   *
   * <p>
   * Real samples run from <code>0.0</code> to <code>1.0</code> and integer samples from
   * <code>0</code> to <code>255</code>, unless every integer sample is 0 or 1, in which case the
   * image is bilevel. Also accepts a <code>Graphics</code> or <code>Graphics3D</code> object, which
   * is rasterized.
   *
   * <p>
   * <code>Image(data, type)</code> reads the samples on the scale <code>type</code> names, one of
   * <code>"Bit"</code>, <code>"Byte"</code>, <code>"Bit16"</code>, <code>"Real32"</code> or
   * <code>"Real64"</code>, instead of taking the scale from the data.
   *
   * <p>
   * <b>Two of the options describe the pixels</b> and the rest only describe how the image is
   * displayed. <code>Interleaving</code> says which way round the data is written and
   * <code>ColorSpace</code> says what the samples mean; <code>ImageSize</code>,
   * <code>ImageResolution</code>, <code>Magnification</code>, <code>MetaInformation</code>,
   * <code>AlignmentPoint</code> and <code>BaselinePosition</code> are carried along and change
   * nothing about the picture.
   */
  private static class Image extends AbstractFunctionOptionEvaluator {

    private static final int OPTION_ALIGNMENT_POINT = 0;
    private static final int OPTION_BASELINE_POSITION = 1;
    private static final int OPTION_COLOR_SPACE = 2;
    private static final int OPTION_IMAGE_RESOLUTION = 3;
    private static final int OPTION_IMAGE_SIZE = 4;
    private static final int OPTION_INTERLEAVING = 5;
    private static final int OPTION_MAGNIFICATION = 6;
    private static final int OPTION_META_INFORMATION = 7;

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!ast.arg1().isAST()) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();

      // an option name Image does not know sits where the sample type goes, because the scan for
      // options stops at the first argument that cannot be one. Say so and carry on, which is what
      // a misspelled option does everywhere else.
      if (argSize >= 2 && isOptionShaped(ast.arg2())) {
        Errors.printMessage(S.Image, "optx", F.list(ast.arg2(), S.Image), engine);
        argSize = 1;
      }

      String type = null;
      if (argSize >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        type = ast.arg2().toString();
        if (!Pixels.isImageType(type)) {
          return Errors.printMessage(S.Image, "imgtype", F.list(ast.arg2()), engine);
        }
      }

      IExpr interleavingValue = options[OPTION_INTERLEAVING];
      if (!interleavingValue.isTrue() && !interleavingValue.isFalse()
          && interleavingValue != S.Automatic) {
        return Errors.printMessage(S.Image, "opttfa",
            F.list(S.Interleaving, interleavingValue), engine);
      }
      // Automatic reads the data the way it is usually written, which is interleaved
      boolean interleaved = !interleavingValue.isFalse();

      ImageOptions imageOptions = new ImageOptions(options[OPTION_COLOR_SPACE], interleaved,
          options[OPTION_IMAGE_SIZE], options[OPTION_IMAGE_RESOLUTION],
          options[OPTION_MAGNIFICATION], metaInformation(options[OPTION_META_INFORMATION]),
          options[OPTION_ALIGNMENT_POINT], options[OPTION_BASELINE_POSITION]);

      try {
        String colorSpace = imageOptions.colorSpaceName();
        if (colorSpace != null && !data.isGraphicsObject() && !data.isAST(S.Graphics3D)) {
          IExpr message = checkColorSpace(colorSpace, data, interleaved, engine);
          if (message != null) {
            return message;
          }
        }
        ImageExpr imageExpr = ImageExpr.toImageExpr(data, imageOptions);
        if (imageExpr == null) {
          return F.NIL;
        }
        return type == null ? imageExpr : onScale(imageExpr, data, type, interleaved);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Image, rex, engine);
      }
    }

    /**
     * The image with its data written on the scale <code>type</code> names.
     *
     * <p>
     * Data already on that scale is left exactly as it was written, so that
     * <code>Image[data, "Real32"]</code> of real data is the same lossless thing as
     * <code>Image[data]</code>. Anything else is read back off the bitmap, which is where the
     * conversion has already happened.
     */
    private static ImageExpr onScale(ImageExpr imageExpr, IAST data, String type,
        boolean interleaved) {
      if (Pixels.sameScale(Pixels.imageTypeOf(data), type)) {
        return imageExpr;
      }
      IAST converted = Pixels.toData(imageExpr.getBufferedImage(), type, interleaved, false);
      // mark it for the matrix layout of OutputForm, as the data written by hand would be
      converted.isMatrix(true);
      return imageExpr.withMatrix(converted);
    }

    /**
     * @return the message to answer with, or <code>null</code> where the colour space can read the
     *         data
     */
    private static IExpr checkColorSpace(String colorSpace, IAST data, boolean interleaved,
        EvalEngine engine) {
      if (!Pixels.isColorSpace(colorSpace)) {
        return Errors.printMessage(S.Image, "imgcs", F.list(F.stringx(colorSpace)), engine);
      }
      Pixels.Samples samples = Pixels.samplesOf(data, interleaved);
      if (samples == null) {
        // not a picture at all; the shape is what the caller hears about, not the colour space
        return null;
      }
      if (!Pixels.colorSpaceFits(colorSpace, samples.channels())) {
        return Errors.printMessage(S.Image, "imgcsc",
            F.list(F.stringx(colorSpace), F.ZZ(samples.channels())), engine);
      }
      return null;
    }

    /** <code>MetaInformation</code> is an association, and a list of rules is read as one. */
    private static IExpr metaInformation(IExpr value) {
      return value.isListOfRules(false) ? F.assoc((IAST) value) : value;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.AlignmentPoint, S.BaselinePosition, S.ColorSpace,
              S.ImageResolution, S.ImageSize, S.Interleaving, S.Magnification,
              S.MetaInformation}, //
          new IExpr[] {S.Center, S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic,
              S.Automatic, F.assoc()});
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <pre>
   * ImageData(image)
   * </pre>
   *
   * <blockquote>
   * <p>
   * the pixel values of <code>image</code> as reals from <code>0.0</code> to <code>1.0</code>.
   * <code>ImageData(image, type)</code> reports them as <code>"Byte"</code>, <code>"Bit"</code>,
   * <code>"Bit16"</code>, <code>"Real32"</code> or <code>"Real64"</code> instead.
   * </p>
   * </blockquote>
   *
   * <p>
   * <b>The result is interleaved by default whatever the image stores.</b>
   * <code>Interleaving -&gt; True</code>, which is the default, gives a matrix of scalars for a
   * greyscale image and a matrix of channel lists otherwise, even for an image whose own data was
   * written the other way round. <code>Interleaving -&gt; False</code> gives one matrix per
   * channel, and <code>Interleaving -&gt; Automatic</code> is the only setting that hands back the
   * form the image actually stores.
   *
   * <p>
   * <code>DataReversed -&gt; True</code> gives the rows bottom to top.
   */
  private static class ImageData extends AbstractFunctionOptionEvaluator {

    private static final int OPTION_DATA_REVERSED = 0;
    private static final int OPTION_INTERLEAVING = 1;

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr arg1 = ast.arg1();
      if (!(arg1 instanceof ImageExpr)) {
        return F.NIL;
      }
      ImageExpr imageExpr = (ImageExpr) arg1;

      if (argSize >= 2 && isOptionShaped(ast.arg2())) {
        Errors.printMessage(S.ImageData, "optx", F.list(ast.arg2(), S.ImageData), engine);
        argSize = 1;
      }

      String type = Pixels.REAL32;
      if (argSize >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        type = ast.arg2().toString();
      }

      IExpr dataReversedValue = options[OPTION_DATA_REVERSED];
      if (!dataReversedValue.isTrue() && !dataReversedValue.isFalse()) {
        return Errors.printMessage(S.ImageData, "opttf",
            F.list(S.DataReversed, dataReversedValue), engine);
      }
      boolean dataReversed = dataReversedValue.isTrue();

      IExpr interleavingValue = options[OPTION_INTERLEAVING];
      boolean interleaved;
      if (interleavingValue == S.Automatic) {
        // Automatic is the only setting that asks the image what it stores; True and False say
        // what the answer is to look like whatever that is
        interleaved = imageExpr.getOptions().interleaved();
      } else if (interleavingValue.isTrue() || interleavingValue.isFalse()) {
        interleaved = interleavingValue.isTrue();
      } else {
        return Errors.printMessage(S.ImageData, "opttfa",
            F.list(S.Interleaving, interleavingValue), engine);
      }

      // an image built from a matrix hands that matrix straight back, but only when it is the
      // answer to what was asked: on the requested scale - the Byte matrix of a colour gradient is
      // not the answer to a Real32 request - written the way round that was asked for, and the
      // right way up
      IAST matrix = imageExpr.getMatrix();
      if (matrix != null && !dataReversed && interleaved == imageExpr.getOptions().interleaved()
          && Pixels.sameScale(Pixels.imageTypeOf(matrix), type)) {
        return matrix;
      }

      BufferedImage bufferedImage = imageExpr.getBufferedImage();
      if (bufferedImage == null) {
        return F.NIL;
      }
      return Pixels.toData(bufferedImage, type, interleaved, dataReversed);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.DataReversed, S.Interleaving}, //
          new IExpr[] {S.False, S.True});
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <pre>
   * ImageDimensions(image)
   * </pre>
   *
   * <blockquote>
   * <p>
   * the <code>{width, height}</code> of <code>image</code> in pixels. Note the order:
   * <code>Dimensions(ImageData(image))</code> reports <code>{height, width}</code>, because that is
   * the shape of the matrix.
   * </p>
   * </blockquote>
   */
  private static class ImageDimensions extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof ImageExpr) {
        BufferedImage bufferedImage = ((ImageExpr) arg1).getBufferedImage();
        if (bufferedImage != null) {
          return F.List(F.ZZ(bufferedImage.getWidth()), F.ZZ(bufferedImage.getHeight()));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  /**
   * Whether an argument is a rule or a list of rules, and so was meant as an option.
   *
   * <p>
   * Both functions here take a positional argument as well as options, and the scan that pulls the
   * options off the end stops at the first argument that cannot be one. An option name that is not
   * recognized therefore lands in the positional slot and is never reported, which is what this is
   * for: the slot is checked, and a rule found there is an option nobody knows.
   */
  private static boolean isOptionShaped(IExpr arg) {
    return arg.isRule() || arg.isAST(S.RuleDelayed, 3)
        || (arg.isListOfRules(true) && !arg.isEmptyList());
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFunctions() {}
}
