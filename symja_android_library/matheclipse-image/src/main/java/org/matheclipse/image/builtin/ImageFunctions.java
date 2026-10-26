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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.Image3DExpr;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/** <code>Image</code>, <code>ImageData</code> and <code>ImageDimensions</code>. */
public class ImageFunctions {

  private static class Initializer {

    private static void init() {
      S.ConstantImage.setEvaluator(new ConstantImage());
      S.Image.setEvaluator(new Image());
      S.Image3D.setEvaluator(new Image3D());
      S.Image3DSlices.setEvaluator(new Image3DSlices());
      S.ImageData.setEvaluator(new ImageData());
      S.ImageDimensions.setEvaluator(new ImageDimensions());
    }
  }

  /**
   * <code>Image3D(data)</code> - the 3D image of the array <code>{slice, row, column}</code> of the
   * samples, or <code>{slice, row, column, channel}</code>. <code>Image3D(data, type)</code> takes
   * the samples as the ones of the type <code>"Bit"</code>, <code>"Byte"</code>,
   * <code>"Bit16"</code>, <code>"Real32"</code> or <code>"Real64"</code>. The data may be a
   * <code>NumericArray</code>, whose type then is the type of the image unless one is given.
   */
  private static class Image3D extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof Image3DExpr && ast.argSize() == 1) {
        return arg1;
      }
      String type = null;
      if (arg1.isList() && arg1.argSize() > 0
          && ((IAST) arg1).forAll(slice -> slice instanceof ImageExpr)) {
        return stack((IAST) arg1, ast, engine);
      }
      if (arg1.isNumericArray()) {
        INumericArray array = (INumericArray) arg1;
        type = Image.imageTypeOfArray(array.getStringType());
        arg1 = array.normal(false);
      }
      if (!arg1.isList()) {
        return F.NIL;
      }
      IExpr colorSpace = S.Automatic;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (i == 2 && arg.isString()) {
          type = Pixels.imageType(arg.toString());
          if (!Pixels.isImageType(type)) {
            return Errors.printMessage(S.Image3D, "imgtype", F.list(arg), engine);
          }
        } else if (arg.isRuleAST()) {
          if (arg.first() == S.ColorSpace) {
            colorSpace = arg.second();
          } else if (arg.first() == S.Interleaving && arg.second().isFalse()) {
            // one array per channel isn't read
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      }
      try {
        Image3DExpr image =
            Image3DExpr.of((IAST) arg1, type, ImageOptions.DEFAULT.withColorSpace(colorSpace));
        return image == null ? F.NIL : image;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Image3D, rex, engine);
      }
    }

    /**
     * <code>Image3D({image1, image2, ...})</code> - the 2D images of one size, number of channels and
     * color space as the slices of a volume, the first one on top; the type is the one given, or
     * theirs, or <code>"Real32"</code> where they differ. Other images stay unevaluated with a
     * message.
     */
    private static IExpr stack(IAST slices, IAST ast, EvalEngine engine) {
      ImageExpr first = (ImageExpr) slices.arg1();
      String type = first.sampleType();
      IExpr dimensions = engine.evaluate(F.unaryAST1(S.ImageDimensions, first));
      IExpr channels = engine.evaluate(F.unaryAST1(S.ImageChannels, first));
      IExpr space = engine.evaluate(F.unaryAST1(S.ImageColorSpace, first));
      for (IExpr slice : slices) {
        if (!dimensions.equals(engine.evaluate(F.unaryAST1(S.ImageDimensions, slice)))
            || !channels.equals(engine.evaluate(F.unaryAST1(S.ImageChannels, slice)))
            || !space.equals(engine.evaluate(F.unaryAST1(S.ImageColorSpace, slice)))) {
          // The specified argument `1` should be an array of rank 3 or 4 with machine-sized
          // numbers or a list of images of consistent dimension and color space.
          return Errors.printMessage(S.Image3D, "imgarray", F.list(slices), engine);
        }
        if (!type.equals(((ImageExpr) slice).sampleType())) {
          type = Pixels.REAL32;
        }
      }
      IASTAppendable options = F.ListAlloc(ast.size());
      boolean colorSpace = false;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (i == 2 && arg.isString()) {
          // the type asked for instead of the one of the slices
          type = Pixels.imageType(arg.toString());
          if (!Pixels.isImageType(type)) {
            return Errors.printMessage(S.Image3D, "imgtype", F.list(arg), engine);
          }
        } else if (arg.isRuleAST()) {
          colorSpace |= arg.first() == S.ColorSpace;
          options.append(arg);
        } else {
          return F.NIL;
        }
      }
      final String sampleType = type;
      IAST data = slices.map(
          slice -> engine.evaluate(F.binaryAST2(S.ImageData, slice, F.stringx(sampleType))));
      IASTAppendable volume = F.ast(S.Image3D, ast.size() + 2);
      volume.append(data);
      volume.append(F.stringx(type));
      volume.appendArgs(options);
      if (!colorSpace) {
        volume.append(F.Rule(S.ColorSpace, space));
      }
      return engine.evaluate(volume);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>Image3DSlices(image3d)</code> - the 2D images of the slices of a volume, the top one
   * first. <code>Image3DSlices(image3d, k)</code> is the <code>k</code>-th slice, counted from the
   * bottom where <code>k</code> is negative, and <code>Image3DSlices(image3d, spec, axis)</code>
   * cuts the volume across the axis 1, 2 or 3 of its data <code>{slice, row, column}</code>:
   * <code>spec</code> is a position, <code>All</code>, a list of positions or a <code>Span</code>.
   * A slice keeps the type and the colour space of the volume.
   */
  private static class Image3DSlices extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof Image3DExpr)) {
        return F.NIL;
      }
      Image3DExpr volume = (Image3DExpr) ast.arg1();
      int axis = 1;
      if (ast.argSize() == 3) {
        axis = ast.arg3().toIntDefault();
        if (axis < 1 || axis > 3) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.Image3DSlices, "intpm", F.list(ast, F.C3), engine);
        }
      }
      IAST data = volume.toData();
      int[] dimensions = volume.dimensions();
      // the number of slices along the axis: the data is {slice, row, column}
      final int count = dimensions[3 - axis];
      IExpr spec = ast.argSize() >= 2 ? ast.arg2() : S.All;
      if (spec.isInteger()) {
        int k = index(spec, count);
        if (k == 0) {
          // Part `1` of `2` does not exist.
          return Errors.printMessage(S.Image3DSlices, "partw", F.list(spec, volume), engine);
        }
        return slice(volume, data, axis, k);
      }
      int[] positions = positions(spec, count);
      if (positions == null) {
        // Part specification `1` is neither an integer nor a list of integer.
        return Errors.printMessage(S.Image3DSlices, "pspec", F.list(spec), engine);
      }
      IASTAppendable result = F.ListAlloc(positions.length);
      for (int k : positions) {
        if (k == 0) {
          return Errors.printMessage(S.Image3DSlices, "partw", F.list(spec, volume), engine);
        }
        IExpr image = slice(volume, data, axis, k);
        if (image.isNIL()) {
          return F.NIL;
        }
        result.append(image);
      }
      return result;
    }

    /**
     * The position <code>1 ... count</code> an integer names, a negative one counted from the end,
     * or <code>0</code> where there is no such slice.
     */
    private static int index(IExpr position, int count) {
      int k = position.toIntDefault(0);
      if (k < 0) {
        k += count + 1;
      }
      return k >= 1 && k <= count ? k : 0;
    }

    /**
     * The positions <code>All</code>, a list of integers or a <code>Span</code> names, with
     * <code>0</code> for one out of range.
     *
     * @return <code>null</code> if <code>spec</code> is none of them
     */
    private static int[] positions(IExpr spec, int count) {
      if (spec == S.All) {
        return span(1, count, 1, count);
      }
      if (spec.isList()) {
        IAST list = (IAST) spec;
        int[] result = new int[list.argSize()];
        for (int i = 1; i < list.size(); i++) {
          if (!list.get(i).isInteger()) {
            return null;
          }
          result[i - 1] = index(list.get(i), count);
        }
        return result;
      }
      if (spec.isAST(S.Span, 3, 4)) {
        IAST span = (IAST) spec;
        int step = span.argSize() == 3 ? span.arg3().toIntDefault(0) : 1;
        int start = spanEnd(span.arg1(), 1, count);
        int end = spanEnd(span.arg2(), count, count);
        if (step < 0 && span.arg2() == S.All) {
          // ;; ;; -1 is Span(1, All, -1), which runs from the last slice back to the first, as
          // it does in Part
          start = start == 1 ? count : start;
          end = 1;
        }
        if (start == 0 || end == 0 || step == 0) {
          return null;
        }
        return span(start, end, step, count);
      }
      return null;
    }

    /** One end of a span: <code>All</code> is <code>whole</code>, a negative one counts back. */
    private static int spanEnd(IExpr end, int whole, int count) {
      if (end == S.All) {
        return whole;
      }
      if (!end.isInteger()) {
        return 0;
      }
      int k = end.toIntDefault(0);
      return k < 0 ? k + count + 1 : k;
    }

    private static int[] span(int start, int end, int step, int count) {
      int length = step > 0 ? (end - start) / step + 1 : (start - end) / -step + 1;
      if (length <= 0) {
        return new int[0];
      }
      int[] result = new int[length];
      for (int i = 0; i < length; i++) {
        int k = start + i * step;
        result[i] = k >= 1 && k <= count ? k : 0;
      }
      return result;
    }

    /**
     * The 2D image of the <code>k</code>-th slice across <code>axis</code>: its rows run along the
     * next axis of the data that is not cut, its columns along the last one.
     */
    private static IExpr slice(Image3DExpr volume, IAST data, int axis, int k) {
      IAST matrix;
      if (axis == 1) {
        matrix = (IAST) data.get(k);
      } else {
        IASTAppendable rows = F.ListAlloc(data.argSize());
        for (int z = 1; z < data.size(); z++) {
          IAST slice = (IAST) data.get(z);
          if (axis == 2) {
            rows.append(slice.get(k));
          } else {
            IASTAppendable row = F.ListAlloc(slice.argSize());
            for (int y = 1; y < slice.size(); y++) {
              row.append(((IAST) slice.get(y)).get(k));
            }
            rows.append(row);
          }
        }
        matrix = rows;
      }
      // a copy, because the image marks its matrix for the layout ImageData prints
      ImageExpr image = ImageExpr.toImageExpr(matrix.copy(),
          volume.getOptions().withInterleaved(true), volume.sampleType());
      return image == null ? F.NIL : image;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ConstantImage(v, size)</code> - an image whose every pixel is <code>v</code>: a number
   * for a greyscale image, a list of channel values or a colour for a colour one. The size is a
   * width and a height, or one number for a square.
   */
  private static class ConstantImage extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int width;
      int height;
      IExpr size = ast.arg2();
      if (size.isList2()) {
        width = size.first().toIntDefault();
        height = size.second().toIntDefault();
      } else {
        width = size.toIntDefault();
        height = width;
      }
      if (width < 1 || height < 1) {
        return F.NIL;
      }
      float[] pixel = pixelOf(ast.arg1());
      if (pixel == null) {
        return F.NIL;
      }
      return new ImageExpr(Pixels.fromPixels(width, height, pixel.length, (x, y) -> pixel), null);
    }

    /** The channel values of the constant, on <code>0 ... 255</code>, or <code>null</code>. */
    private static float[] pixelOf(IExpr value) {
      if (value.isReal()) {
        return new float[] {(float) (255.0 * clamp(value.evalf()))};
      }
      if (value.isList() && (value.argSize() == 3 || value.argSize() == 4)) {
        float[] channels = new float[value.argSize()];
        for (int i = 0; i < channels.length; i++) {
          double c = value.getAt(i + 1).evalfNaN();
          if (Double.isNaN(c)) {
            return null;
          }
          channels[i] = (float) (255.0 * clamp(c));
        }
        return channels;
      }
      java.awt.Color color = org.matheclipse.core.graphics.svg.ColorUtil.parse(value);
      if (color == null) {
        return null;
      }
      return color.getAlpha() < 255
          ? new float[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()}
          : new float[] {color.getRed(), color.getGreen(), color.getBlue()};
    }

    private static double clamp(double v) {
      return Math.max(0.0, Math.min(1.0, v));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
      if (ast.arg1() instanceof ImageExpr) {
        // Image[image, type, options]: the same picture, its pixels read on the scale type names
        // and built again with the options given - which is how the WLJS notebook asks for the
        // Byte image it shows
        ImageExpr image = (ImageExpr) ast.arg1();
        if (argSize == 1 && ast.argSize() == 1) {
          return image;
        }
        java.awt.image.BufferedImage picture = image.getBufferedImage();
        if (picture == null) {
          return F.NIL;
        }
        IAST matrix = image.getMatrix();
        String currentType = image.sampleType();
        String newType =
            argSize >= 2 && ast.arg2().isString() ? Pixels.imageType(ast.arg2().toString())
                : currentType;
        if (!Pixels.isImageType(newType)) {
          return Errors.printMessage(S.Image, "imgtype", F.list(ast.arg2()), engine);
        }
        IExpr pixels = Pixels.toData(picture, newType, true, false);
        if (!pixels.isAST()) {
          return F.NIL;
        }
        ast = ast.setAtCopy(1, pixels);
      }
      // Image[NumericArray[data, "UnsignedInteger8"], ...]: writes an image in InputForm,
      // and what the pixels of an image pasted out of a notebook are. The type of the array is
      // the type of the image unless one is given.
      String arrayType = null;
      if (ast.arg1().isNumericArray()) {
        INumericArray array = (INumericArray) ast.arg1();
        arrayType = imageTypeOfArray(array.getStringType());
        IExpr list = array.normal(false);
        if (!list.isList()) {
          return F.NIL;
        }
        ast = ast.setAtCopy(1, list);
      }
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
        type = Pixels.imageType(ast.arg2().toString());
        if (!Pixels.isImageType(type)) {
          return Errors.printMessage(S.Image, "imgtype", F.list(ast.arg2()), engine);
        }
      }
      if (type == null) {
        type = arrayType;
      }

      IExpr interleavingValue = options[OPTION_INTERLEAVING];
      // Writes the image of one channel with Interleaving -> None
      if (!interleavingValue.isTrue() && !interleavingValue.isFalse()
          && interleavingValue != S.Automatic && interleavingValue != S.None) {
        return Errors.printMessage(S.Image, "opttfa", F.list(S.Interleaving, interleavingValue),
            engine);
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
        if (type != null && !data.isGraphicsObject() && !data.isAST(S.Graphics3D)) {
          // Takes the data as raw samples of the stated type, rounded and clipped to it
          IAST coerced = Pixels.coerceToType(data, type);
          if (coerced == null) {
            return F.NIL;
          }
          // null for data of no image shape - a vector, a ragged matrix, 2 channels - which
          // leaves Image unevaluated, as the untyped path does
          ImageExpr typedImage = ImageExpr.toImageExpr(coerced, imageOptions, type);
          return typedImage == null ? F.NIL : typedImage;
        }
        ImageExpr imageExpr = ImageExpr.toImageExpr(data, imageOptions);
        if (imageExpr == null) {
          return F.NIL;
        }
        return imageExpr;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Image, rex, engine);
      }
    }

    /** The image type of the elements of a <code>NumericArray</code>, or <code>null</code>. */
    static String imageTypeOfArray(String arrayType) {
      switch (arrayType) {
        case "UnsignedInteger8":
          return Pixels.BYTE;
        case "UnsignedInteger16":
          return Pixels.BIT16;
        case "Real32":
          return Pixels.REAL32;
        case "Real64":
          return Pixels.REAL64;
        default:
          return null;
      }
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
              S.ImageResolution, S.ImageSize, S.Interleaving, S.Magnification, S.MetaInformation}, //
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
      if (arg1 instanceof Image3DExpr) {
        // the array {slice, row, column} of the voxels, on the scale of the type asked for
        String type = Pixels.REAL32;
        if (argSize >= 2) {
          if (!ast.arg2().isString()
              || !Pixels.isImageType(Pixels.imageType(ast.arg2().toString()))) {
            return F.NIL;
          }
          type = Pixels.imageType(ast.arg2().toString());
        }
        return ((Image3DExpr) arg1).data(type);
      }
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
        type = Pixels.imageType(ast.arg2().toString());
      }

      IExpr dataReversedValue = options[OPTION_DATA_REVERSED];
      if (!dataReversedValue.isTrue() && !dataReversedValue.isFalse()) {
        return Errors.printMessage(S.ImageData, "opttf", F.list(S.DataReversed, dataReversedValue),
            engine);
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
        return Errors.printMessage(S.ImageData, "opttfa", F.list(S.Interleaving, interleavingValue),
            engine);
      }

      // an image built from a matrix hands that matrix straight back, but only when it is the
      // answer to what was asked: on the requested scale - the Byte matrix of a colour gradient is
      // not the answer to a Real32 request - written the way round that was asked for, and the
      // right way up
      IAST matrix = imageExpr.getMatrix();
      if (matrix != null && !dataReversed && Pixels.sameScale(imageExpr.sampleType(), type)) {
        if (interleaved == imageExpr.getOptions().interleaved()) {
          return matrix;
        }
        // the other layout has the same samples in another order
        IAST relayout = Pixels.relayout(matrix, interleaved);
        if (relayout != null) {
          return relayout;
        }
      }

      if (matrix != null) {
        // another scale, or upside down: from the exact samples, which a real type keeps outside
        // of 0 ... 1 and an integer type clips
        org.matheclipse.image.algo.Raster raster = imageExpr.raster();
        if (raster != null && raster.channels != 2) {
          IAST data = raster.toMatrix(type);
          if (dataReversed) {
            // the rows bottom to top
            final IAST rows = data;
            data = F.mapRange(1, rows.size(), i -> rows.get(rows.size() - i));
          }
          if (!interleaved) {
            data = Pixels.relayout(data, false);
          }
          if (data != null) {
            return data;
          }
        }
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
      if (arg1 instanceof Image3DExpr) {
        // {width, depth, height}
        int[] dimensions = ((Image3DExpr) arg1).dimensions();
        return F.List(F.ZZ(dimensions[0]), F.ZZ(dimensions[1]), F.ZZ(dimensions[2]));
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
