package org.matheclipse.image.builtin;

import java.util.ArrayDeque;
import java.util.Arrays;
import org.matheclipse.core.builtin.FilterFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;
import org.matheclipse.image.algo.Volumes;
import org.matheclipse.image.expression.data.Image3DExpr;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/**
 * The filters of an image:
 * <ul>
 * <li>linear: <code>GaussianFilter</code>, <code>ImageConvolve</code>, <code>ImageCorrelate</code>,
 * <code>DerivativeFilter</code>, <code>GradientFilter</code>,
 * <li>of the block around a pixel: <code>MeanFilter</code>, <code>MedianFilter</code>,
 * <code>MinFilter</code>, <code>MaxFilter</code>,
 * <li>morphological: <code>Dilation</code>, <code>Erosion</code>, <code>Opening</code>,
 * <code>Closing</code>, <code>MorphologicalComponents</code>, <code>DistanceTransform</code>.
 * </ul>
 *
 * <p>
 * Every filter works on the real samples of <code>ImageData(image)</code>, channel by channel, and
 * keeps the filtered samples exactly; nothing is rounded to the 8 bit bitmap an image is shown
 * with. The linear filters return a <code>"Real32"</code> image and continue an image beyond its
 * border with the pixels of the border (<code>"Fixed"</code> padding), while the block and the
 * morphological filters return an image of the type they were given and use the part of the block
 * which lies inside of the image.
 *
 * <p>
 * The filters which core defines for lists keep doing that: the evaluators installed here take an
 * image and hand everything else on to the evaluator of core.
 */
public class ImageFilterFunctions {

  private static class Initializer {

    private static void init() {
      install(S.GaussianFilter, new GaussianFilter());
      install(S.MeanFilter, new MeanFilter());
      install(S.MedianFilter, new RankFilter(RankFilter.MEDIAN));
      install(S.MinFilter, new RankFilter(RankFilter.MIN));
      install(S.MaxFilter, new RankFilter(RankFilter.MAX));
      S.ImageConvolve.setEvaluator(new ImageConvolve(true));
      S.ImageCorrelate.setEvaluator(new ImageConvolve(false));
      S.DerivativeFilter.setEvaluator(new DerivativeFilter());
      S.GradientFilter.setEvaluator(new GradientFilter());
      S.Dilation.setEvaluator(new Morphology(true, false));
      S.Erosion.setEvaluator(new Morphology(false, false));
      S.Opening.setEvaluator(new Morphology(false, true));
      S.Closing.setEvaluator(new Morphology(true, true));
      S.MorphologicalComponents.setEvaluator(new MorphologicalComponents());
      S.DistanceTransform.setEvaluator(new DistanceTransform());
    }

    private static void install(IBuiltInSymbol symbol, ImageFilter filter) {
      filter.listEvaluator = symbol.getEvaluator();
      symbol.setEvaluator(filter);
    }
  }

  /**
   * The samples of an image as <code>[channel][row][column]</code>.
   *
   * @return <code>null</code> if the image data is no array of real numbers
   */
  public static double[][][] channels(ImageExpr image, EvalEngine engine) {
    // one pixel next to the other, also for an image which stores its channels as planes
    return channels(
        engine.evaluate(F.binaryAST2(S.ImageData, image, F.Rule(S.Interleaving, S.True))));
  }

  /**
   * The samples of a matrix of numbers, or of pixels which are lists of numbers, as
   * <code>[channel][row][column]</code>.
   *
   * @return <code>null</code> if <code>data</code> is no array of real numbers
   */
  static double[][][] channels(IExpr data) {
    if (!data.isList() || data.argSize() == 0 || !data.first().isList()) {
      return null;
    }
    IAST rows = (IAST) data;
    final int height = rows.argSize();
    final int width = rows.arg1().argSize();
    if (width == 0) {
      return null;
    }
    final int channels = rows.arg1().first().isList() ? rows.arg1().first().argSize() : 1;
    double[][][] result = new double[channels][height][width];
    for (int y = 0; y < height; y++) {
      if (!rows.get(y + 1).isList() || rows.get(y + 1).argSize() != width) {
        return null;
      }
      IAST row = (IAST) rows.get(y + 1);
      for (int x = 0; x < width; x++) {
        IExpr pixel = row.get(x + 1);
        if (pixel.isList()) {
          if (pixel.argSize() != channels) {
            return null;
          }
          for (int c = 0; c < channels; c++) {
            result[c][y][x] = ((IAST) pixel).get(c + 1).evalfNaN();
          }
        } else {
          if (channels != 1) {
            return null;
          }
          result[0][y][x] = pixel.evalfNaN();
        }
      }
    }
    for (double[][] channel : result) {
      for (double[] row : channel) {
        for (double sample : row) {
          if (Double.isNaN(sample)) {
            return null;
          }
        }
      }
    }
    return result;
  }

  /** A <code>"Real32"</code> image of the samples <code>[channel][row][column]</code>. */
  public static IExpr toImage(double[][][] channels, ImageOptions options) {
    return toImage(channels, options, "Real32");
  }

  /**
   * An image of the samples <code>[channel][row][column]</code>, which are on the scale
   * <code>0...1</code> of <code>ImageData</code>. A <code>"Byte"</code>, <code>"Bit16"</code> or
   * <code>"Bit"</code> image stores them as the integers of its type, every other one is a
   * <code>"Real32"</code> image unless <code>"Real64"</code> is asked for.
   */
  static IExpr toImage(double[][][] channels, ImageOptions options, String type) {
    final double scale = "Byte".equals(type) ? 255.0
        : "Bit16".equals(type) ? 65535.0 : "Bit".equals(type) ? 1.0 : 0.0;
    final int count = channels.length;
    final int height = channels[0].length;
    final int width = channels[0][0].length;
    IASTAppendable rows = F.ListAlloc(height);
    for (int y = 0; y < height; y++) {
      IASTAppendable row = F.ListAlloc(width);
      for (int x = 0; x < width; x++) {
        if (count == 1) {
          row.append(sample(channels[0][y][x], scale));
        } else {
          IASTAppendable pixel = F.ListAlloc(count);
          for (int c = 0; c < count; c++) {
            pixel.append(sample(channels[c][y][x], scale));
          }
          row.append(pixel);
        }
      }
      rows.append(row);
    }
    ImageExpr image = ImageExpr.toImageExpr(rows, options.withInterleaved(true),
        scale != 0.0 || "Real64".equals(type) ? type : "Real32");
    return image == null ? F.NIL : image;
  }

  private static IExpr sample(double value, double scale) {
    return scale == 0.0 ? F.num(value) : F.ZZ(Math.round(value * scale));
  }

  /** A filter which takes an image, and leaves a list to the evaluator of core. */
  private abstract static class ImageFilter extends AbstractEvaluator {
    IFunctionEvaluator listEvaluator;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 1 && ast.arg1() instanceof ImageExpr) {
        ImageExpr image = (ImageExpr) ast.arg1();
        double[][][] channels = channels(image, engine);
        if (channels == null) {
          return F.NIL;
        }
        for (int c = 0; c < channels.length; c++) {
          channels[c] = filter(channels[c], ast, engine);
          if (channels[c] == null) {
            return F.NIL;
          }
        }
        return toImage(channels, image.getOptions(), keepsType() ? image.sampleType() : "Real32");
      }
      if (ast.argSize() >= 1 && ast.arg1() instanceof Image3DExpr) {
        // a 3D image: the same filter with one more direction
        Image3DExpr volume = (Image3DExpr) ast.arg1();
        double[][][][] samples = volume.samples();
        for (int c = 0; c < samples.length; c++) {
          samples[c] = filter3D(samples[c], ast, engine);
          if (samples[c] == null) {
            return F.NIL;
          }
        }
        return Image3DExpr.of(samples, keepsType() ? volume.sampleType() : "Real32",
            volume.getOptions());
      }
      return listEvaluator == null ? F.NIL : listEvaluator.evaluate(ast, engine);
    }

    /**
     * Filter one channel <code>[slice][row][column]</code> of a 3D image.
     *
     * @return <code>null</code> if the filter has no 3D form, or its arguments aren't valid
     */
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      return null;
    }

    /**
     * Filter one channel.
     *
     * @return <code>null</code> if the arguments behind the image aren't valid
     */
    abstract double[][] filter(double[][] channel, IAST ast, EvalEngine engine);

    /** Whether the result has the type of the image; a linear filter returns "Real32". */
    boolean keepsType() {
      return false;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>GaussianFilter(image, r)</code> and <code>GaussianFilter(image, {r, sigma})</code>: the
   * discrete Gaussian kernel of {@link FilterFunctions#gaussianKernel}, with the standard deviation
   * <code>r/2</code> if none is given.
   */
  private static class GaussianFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      int radius = spec.isList2() ? spec.first().toIntDefault() : spec.toIntDefault();
      double sigma = spec.isList2() ? spec.second().evalfNaN() : radius / 2.0;
      if (radius < 0 || Double.isNaN(sigma) || sigma < 0.0) {
        return null;
      }
      return FilterFunctions.separableFilter(channel,
          FilterFunctions.gaussianKernel(radius, sigma, engine));
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      int radius = spec.isList2() ? spec.first().toIntDefault() : spec.toIntDefault();
      double sigma = spec.isList2() ? spec.second().evalfNaN() : radius / 2.0;
      if (radius < 0 || Double.isNaN(sigma) || sigma < 0.0) {
        return null;
      }
      return Volumes.separable(volume, FilterFunctions.gaussianKernel(radius, sigma, engine));
    }
  }

  /**
   * <code>MeanFilter(image, r)</code>: the mean of the <code>(2r+1) x (2r+1)</code> block, of the
   * part of it which lies inside of the image.
   */
  private static class MeanFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      if (radius < 0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          int count = 0;
          for (int row = Math.max(0, y - radius); row <= Math.min(height - 1, y + radius); row++) {
            for (int column = Math.max(0, x - radius); column <= Math.min(width - 1,
                x + radius); column++) {
              sum += channel[row][column];
              count++;
            }
          }
          result[y][x] = sum / count;
        }
      }
      return result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      return radius < 0 ? null : Volumes.block(volume, radius, Volumes.MEAN);
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>MedianFilter(image, r)</code>, <code>MinFilter(image, r)</code> and
   * <code>MaxFilter(image, r)</code>: the median, the smallest or the largest sample of the part of
   * the <code>(2r+1) x (2r+1)</code> block which lies inside of the image. The median of an even
   * number of samples is the upper one of the two in the middle - a sample of the image, not a mean
   * of two.
   */
  private static class RankFilter extends ImageFilter {
    static final int MIN = 0;
    static final int MEDIAN = 1;
    static final int MAX = 2;

    private final int rank;

    RankFilter(int rank) {
      this.rank = rank;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      if (radius < 0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      final int side = 2 * radius + 1;
      double[] block = new double[side * side];
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int count = 0;
          for (int row = Math.max(0, y - radius); row <= Math.min(height - 1, y + radius); row++) {
            for (int column = Math.max(0, x - radius); column <= Math.min(width - 1,
                x + radius); column++) {
              block[count++] = channel[row][column];
            }
          }
          Arrays.sort(block, 0, count);
          result[y][x] = rank == MIN ? block[0] : rank == MAX ? block[count - 1] : block[count / 2];
        }
      }
      return result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      int radius = ast.arg2().toIntDefault();
      return radius < 0 ? null
          : Volumes.block(volume, radius,
              rank == MIN ? Volumes.MIN : rank == MAX ? Volumes.MAX : Volumes.MEDIAN);
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>ImageConvolve(image, kernel)</code> and <code>ImageCorrelate(image, kernel)</code>. The
   * convolution reflects the kernel, the correlation doesn't. The correlation centres a kernel of
   * <code>k</code> elements on its element <code>Floor(k/2)</code> (counted from 0), and the
   * convolution, being the correlation with the reflected kernel, on the element
   * <code>Floor((k-1)/2)</code>.
   */
  private static class ImageConvolve extends ImageFilter {
    private final boolean convolve;

    ImageConvolve(boolean convolve) {
      this.convolve = convolve;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      if (!arg2.isList() || arg2.argSize() == 0) {
        return null;
      }
      // a vector is a kernel of one row
      double[][] kernel =
          arg2.first().isList() ? arg2.toDoubleMatrix() : new double[][] {arg2.toDoubleVector()};
      if (kernel == null || kernel.length == 0 || kernel[0] == null || kernel[0].length == 0) {
        return null;
      }
      final int kernelRows = kernel.length;
      final int kernelColumns = kernel[0].length;
      final int centerRow = convolve ? (kernelRows - 1) / 2 : kernelRows / 2;
      final int centerColumn = convolve ? (kernelColumns - 1) / 2 : kernelColumns / 2;
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          for (int a = 0; a < kernelRows; a++) {
            int row = convolve ? y + centerRow - a : y + a - centerRow;
            double[] samples = channel[Math.min(height - 1, Math.max(0, row))];
            for (int b = 0; b < kernelColumns; b++) {
              int column = convolve ? x + centerColumn - b : x + b - centerColumn;
              sum += kernel[a][b] * samples[Math.min(width - 1, Math.max(0, column))];
            }
          }
          result[y][x] = sum;
        }
      }
      return result;
    }
  }

  /**
   * <code>Dilation(image, r)</code>, <code>Erosion(image, r)</code>, <code>Opening(image, r)</code>
   * and <code>Closing(image, r)</code> with the <code>(2r+1) x (2r+1)</code> box, or with a matrix
   * whose non-zero elements are the structuring element.
   *
   * <p>
   * The dilation is the largest and the erosion the smallest sample under the element, where the
   * element <code>ker[a][b]</code> lies on the pixel <code>(y+a-cy, x+b-cx)</code>:
   * <code>Dilation({{0,0,1,0,0}}, {{1,1,0}})</code> is <code>{{0,0,1,1,0}}</code>. The opening is
   * the dilation of the erosion and the closing the erosion of the dilation.
   */
  private static class Morphology extends ImageFilter {
    private final boolean dilateFirst;
    private final boolean both;

    /**
     * @param dilateFirst the first (or only) operation is a dilation
     * @param both the other operation follows
     */
    Morphology(boolean dilateFirst, boolean both) {
      this.dilateFirst = dilateFirst;
      this.both = both;
    }

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      boolean[][] element = element(ast.arg2());
      if (element == null) {
        return null;
      }
      double[][] result = apply(channel, element, dilateFirst);
      return both ? apply(result, element, !dilateFirst) : result;
    }

    @Override
    double[][][] filter3D(double[][][] volume, IAST ast, EvalEngine engine) {
      IExpr spec = ast.arg2();
      if (!spec.isList()) {
        // the cube of 2r+1 voxels
        int radius = spec.toIntDefault();
        if (radius < 0) {
          return null;
        }
        double[][][] result =
            Volumes.block(volume, radius, dilateFirst ? Volumes.MAX : Volumes.MIN);
        return both ? Volumes.block(result, radius, dilateFirst ? Volumes.MIN : Volumes.MAX)
            : result;
      }
      boolean[][][] element = element3D((IAST) spec);
      if (element == null) {
        return null;
      }
      double[][][] result = Volumes.extreme(volume, element, dilateFirst);
      return both ? Volumes.extreme(result, element, !dilateFirst) : result;
    }

    /**
     * The structuring element of an array of rank 3; a matrix is an element of one slice and a
     * vector one of one row.
     */
    private static boolean[][][] element3D(IAST spec) {
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(spec);
      if (dimensions == null || dimensions.size() < 1 || dimensions.size() > 3) {
        return null;
      }
      IExpr array = spec;
      for (int rank = dimensions.size(); rank < 3; rank++) {
        array = F.List(array);
      }
      IAST slices = (IAST) array;
      final int depth = slices.argSize();
      final int height = slices.arg1().argSize();
      final int width = slices.arg1().first().argSize();
      if (depth == 0 || height == 0 || width == 0) {
        return null;
      }
      boolean[][][] element = new boolean[depth][height][width];
      for (int a = 0; a < depth; a++) {
        for (int b = 0; b < height; b++) {
          for (int c = 0; c < width; c++) {
            double value = ((IAST) ((IAST) slices.get(a + 1)).get(b + 1)).get(c + 1).evalfNaN();
            if (Double.isNaN(value)) {
              return null;
            }
            element[a][b][c] = value != 0.0;
          }
        }
      }
      return element;
    }

    private static boolean[][] element(IExpr spec) {
      if (spec.isList()) {
        double[][] matrix = spec.argSize() > 0 && spec.first().isList() ? spec.toDoubleMatrix()
            : new double[][] {spec.toDoubleVector()};
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
          return null;
        }
        boolean[][] element = new boolean[matrix.length][matrix[0].length];
        for (int a = 0; a < matrix.length; a++) {
          for (int b = 0; b < matrix[0].length; b++) {
            element[a][b] = matrix[a][b] != 0.0;
          }
        }
        return element;
      }
      int radius = spec.toIntDefault();
      if (radius < 0) {
        return null;
      }
      boolean[][] element = new boolean[2 * radius + 1][2 * radius + 1];
      for (boolean[] row : element) {
        Arrays.fill(row, true);
      }
      return element;
    }

    private static double[][] apply(double[][] channel, boolean[][] element, boolean dilate) {
      final int height = channel.length;
      final int width = channel[0].length;
      final int centerRow = element.length / 2;
      final int centerColumn = element[0].length / 2;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double value = dilate ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
          for (int a = 0; a < element.length; a++) {
            int row = y + a - centerRow;
            if (row < 0 || row >= height) {
              continue;
            }
            for (int b = 0; b < element[0].length; b++) {
              int column = x + b - centerColumn;
              if (element[a][b] && column >= 0 && column < width) {
                value = dilate ? Math.max(value, channel[row][column])
                    : Math.min(value, channel[row][column]);
              }
            }
          }
          // no element of the structuring element lies on the image
          result[y][x] = Double.isInfinite(value) ? channel[y][x] : value;
        }
      }
      return result;
    }

    @Override
    boolean keepsType() {
      return true;
    }
  }

  /**
   * <code>DerivativeFilter(image, {n1, n2})</code> and
   * <code>DerivativeFilter(image, {n1, n2}, sigma)</code>: the derivative of order <code>n1</code>
   * along the rows' direction (down) and <code>n2</code> along the columns' direction (right).
   *
   * <p>
   * The derivative is the one of the cubic spline which interpolates the samples - of the image
   * continued with the pixels of its border - and <code>sigma</code> convolves that spline with a
   * Gaussian of the standard deviation <code>sigma</code>. With the B-spline coefficients
   * <code>c(j)</code> of the samples the value at the pixel <code>x</code> is
   * <code>Sum(c(j) * h(x-j))</code>, where <code>h</code> is the <code>n</code>-th derivative of
   * the cubic B-spline convolved with the Gaussian. Without <code>sigma</code> the first derivative
   * is <code>(c(x+1)-c(x-1))/2</code>.
   */
  private static class DerivativeFilter extends ImageFilter {

    @Override
    double[][] filter(double[][] channel, IAST ast, EvalEngine engine) {
      if (!ast.arg2().isList2()) {
        return null;
      }
      int rowOrder = ast.arg2().first().toIntDefault();
      int columnOrder = ast.arg2().second().toIntDefault();
      double sigma = ast.isAST3() ? ast.arg3().evalfNaN() : 0.0;
      if (rowOrder < 0 || rowOrder > 2 || columnOrder < 0 || columnOrder > 2 || Double.isNaN(sigma)
          || sigma < 0.0) {
        return null;
      }
      final int height = channel.length;
      final int width = channel[0].length;
      double[][] result = new double[height][width];
      // along the columns' direction: every row is a line
      double[] rowKernel = splineKernel(columnOrder, sigma);
      for (int y = 0; y < height; y++) {
        result[y] = rowKernel == null ? channel[y].clone() : splineFilter(channel[y], rowKernel);
      }
      // along the rows' direction: every column is a line
      double[] columnKernel = splineKernel(rowOrder, sigma);
      if (columnKernel != null) {
        double[] line = new double[height];
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            line[y] = result[y][x];
          }
          double[] filtered = splineFilter(line, columnKernel);
          for (int y = 0; y < height; y++) {
            result[y][x] = filtered[y];
          }
        }
      }
      return result;
    }

    /** The <code>order</code>-th derivative of the cubic B-spline. */
    private static double bSpline(int order, double u) {
      double a = Math.abs(u);
      if (a >= 2.0) {
        return 0.0;
      }
      double sign = u < 0 ? -1.0 : 1.0;
      switch (order) {
        case 0:
          return a < 1.0 ? 2.0 / 3.0 - a * a + a * a * a / 2.0
              : (2.0 - a) * (2.0 - a) * (2.0 - a) / 6.0;
        case 1:
          return a < 1.0 ? sign * (1.5 * a * a - 2.0 * a) : -sign * 0.5 * (2.0 - a) * (2.0 - a);
        default:
          return a < 1.0 ? 3.0 * a - 2.0 : 2.0 - a;
      }
    }

    /**
     * The values <code>h(-K), ..., h(K)</code> of the <code>order</code>-th derivative of the cubic
     * B-spline convolved with the Gaussian.
     *
     * @return <code>null</code> for the interpolation itself (order 0 without a Gaussian), which
     *         gives the samples back
     */
    private static double[] splineKernel(int order, double sigma) {
      // a Gaussian this narrow changes the kernel by less than the precision of an image
      if (sigma < 1.0e-3) {
        if (order == 0) {
          return null;
        }
        return new double[] {bSpline(order, -1), bSpline(order, 0), bSpline(order, 1)};
      }
      final int radius = (int) Math.ceil(6.0 * sigma) + 2;
      double[] kernel = new double[2 * radius + 1];
      // Simpson's rule on each of the four pieces of the spline
      final int steps = 2 * Math.max(100, (int) Math.ceil(40.0 / sigma));
      final double norm = 1.0 / (sigma * Math.sqrt(2.0 * Math.PI));
      for (int k = -radius; k <= radius; k++) {
        double total = 0.0;
        for (int piece = -2; piece < 2; piece++) {
          final double h = 1.0 / steps;
          double sum = 0.0;
          for (int i = 0; i <= steps; i++) {
            // inside of the piece, so that the one sided values at its ends are used
            double v = piece + i * h;
            double inside = piece + Math.min(Math.max(i * h, 1e-12), 1.0 - 1e-12);
            double gauss = Math.exp(-(k - v) * (k - v) / (2.0 * sigma * sigma)) * norm;
            double weight = i == 0 || i == steps ? 1.0 : i % 2 == 1 ? 4.0 : 2.0;
            sum += weight * bSpline(order, inside) * gauss;
          }
          total += sum * h / 3.0;
        }
        kernel[k + radius] = total;
      }
      return kernel;
    }

    /**
     * The B-spline coefficients of the line, continued with its end values, summed with the
     * <code>kernel</code>.
     */
    private static double[] splineFilter(double[] line, double[] kernel) {
      final int radius = kernel.length / 2;
      // the coefficients decay like (Sqrt(3)-2)^n from an end, 40 pixels are below the precision
      final int pad = radius + 40;
      final int n = line.length + 2 * pad;
      double[] samples = new double[n];
      for (int i = 0; i < n; i++) {
        samples[i] = line[Math.min(line.length - 1, Math.max(0, i - pad))];
      }
      // (c(i-1) + 4*c(i) + c(i+1))/6 == samples(i), the ends continued with their own value
      double[] diagonal = new double[n];
      double[] coefficients = new double[n];
      diagonal[0] = 5.0 / 6.0;
      coefficients[0] = samples[0];
      for (int i = 1; i < n; i++) {
        double factor = (1.0 / 6.0) / diagonal[i - 1];
        diagonal[i] = (i == n - 1 ? 5.0 / 6.0 : 4.0 / 6.0) - factor / 6.0;
        coefficients[i] = samples[i] - factor * coefficients[i - 1];
      }
      coefficients[n - 1] /= diagonal[n - 1];
      for (int i = n - 2; i >= 0; i--) {
        coefficients[i] = (coefficients[i] - coefficients[i + 1] / 6.0) / diagonal[i];
      }
      double[] result = new double[line.length];
      for (int x = 0; x < line.length; x++) {
        double sum = 0.0;
        for (int k = -radius; k <= radius; k++) {
          sum += coefficients[x + pad - k] * kernel[k + radius];
        }
        result[x] = sum;
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  /**
   * <code>GradientFilter(image, r)</code>: the magnitude of the gradient, from the discrete
   * derivative of the Gaussian kernel of radius <code>r</code> along one direction and that kernel
   * along the other. For <code>r == 1</code> the derivative is the central difference
   * <code>(f(x+1)-f(x-1))/2</code>.
   */
  private static class GradientFilter extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      int radius = ast.arg2().toIntDefault();
      if (radius < 1) {
        return F.NIL;
      }
      ImageExpr image = (ImageExpr) ast.arg1();
      double[][][] channels = channels(image, engine);
      if (channels == null) {
        return F.NIL;
      }
      // the discrete Gaussian of radius r+1, whose central difference is the derivative kernel
      double[] wide = FilterFunctions.gaussianKernel(radius + 1, radius / 2.0, engine);
      double[] derivative = new double[2 * radius + 1];
      double scale = 0.0;
      for (int k = 1; k <= radius; k++) {
        double weight = (wide[radius + 1 + k - 1] - wide[radius + 1 + k + 1]) / 2.0;
        derivative[radius + k] = weight;
        derivative[radius - k] = -weight;
        scale += 2.0 * k * weight;
      }
      for (int i = 0; i < derivative.length; i++) {
        // a ramp of slope 1 has the derivative 1
        derivative[i] /= scale;
      }
      double[] smooth = FilterFunctions.gaussianKernel(radius, radius / 2.0, engine);
      final int height = channels[0].length;
      final int width = channels[0][0].length;
      double[][] magnitude = new double[height][width];
      for (double[][] channel : channels) {
        double[][] dx = correlate(correlate(channel, derivative, false), smooth, true);
        double[][] dy = correlate(correlate(channel, derivative, true), smooth, false);
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            magnitude[y][x] += dx[y][x] * dx[y][x] + dy[y][x] * dy[y][x];
          }
        }
      }
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          magnitude[y][x] = Math.sqrt(magnitude[y][x]);
        }
      }
      return toImage(new double[][][] {magnitude}, ImageOptions.DEFAULT);
    }

    /** Correlate the rows, or the columns, with the kernel; "Fixed" padding. */
    private static double[][] correlate(double[][] data, double[] kernel, boolean columns) {
      final int height = data.length;
      final int width = data[0].length;
      final int radius = kernel.length / 2;
      double[][] result = new double[height][width];
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          double sum = 0.0;
          for (int k = -radius; k <= radius; k++) {
            sum += kernel[k + radius] * (columns //
                ? data[Math.min(height - 1, Math.max(0, y + k))][x]
                : data[y][Math.min(width - 1, Math.max(0, x + k))]);
          }
          result[y][x] = sum;
        }
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * The foreground of an image or a matrix as <code>[row][column]</code>: the pixels with a sample
   * greater than the threshold. A pixel of several channels counts with its largest sample.
   *
   * @return <code>null</code> if <code>arg</code> is neither an image nor a matrix of numbers
   */
  private static boolean[][] foreground(IExpr arg, double threshold, EvalEngine engine) {
    double[][][] channels =
        arg instanceof ImageExpr ? channels((ImageExpr) arg, engine) : channels(arg);
    if (channels == null) {
      return null;
    }
    final int height = channels[0].length;
    final int width = channels[0][0].length;
    boolean[][] result = new boolean[height][width];
    for (double[][] channel : channels) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          result[y][x] |= channel[y][x] > threshold;
        }
      }
    }
    return result;
  }

  /**
   * <code>MorphologicalComponents(image)</code>, <code>MorphologicalComponents(image, t)</code>:
   * the matrix in which every pixel greater than <code>t</code> (<code>0</code> if not given) has
   * the number of its connected component and every other pixel has <code>0</code>. The components
   * are numbered in the order their first pixel is met row by row, and pixels which only touch at a
   * corner are connected unless <code>CornerNeighbors -> False</code> is given.
   */
  private static class MorphologicalComponents extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double threshold = 0.0;
      boolean cornerNeighbors = true;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          if (arg.first() != S.CornerNeighbors) {
            return F.NIL;
          }
          cornerNeighbors = !arg.second().isFalse();
        } else if (i == 2) {
          threshold = arg.evalfNaN();
          if (Double.isNaN(threshold)) {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      }
      boolean[][] foreground = foreground(ast.arg1(), threshold, engine);
      if (foreground == null) {
        return F.NIL;
      }
      final int height = foreground.length;
      final int width = foreground[0].length;
      int[][] label = new int[height][width];
      int components = 0;
      ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (!foreground[y][x] || label[y][x] != 0) {
            continue;
          }
          label[y][x] = ++components;
          queue.add(new int[] {y, x});
          while (!queue.isEmpty()) {
            int[] pixel = queue.poll();
            for (int dy = -1; dy <= 1; dy++) {
              for (int dx = -1; dx <= 1; dx++) {
                if ((dy == 0 && dx == 0) || (!cornerNeighbors && dy != 0 && dx != 0)) {
                  continue;
                }
                int row = pixel[0] + dy;
                int column = pixel[1] + dx;
                if (row >= 0 && row < height && column >= 0 && column < width
                    && foreground[row][column] && label[row][column] == 0) {
                  label[row][column] = components;
                  queue.add(new int[] {row, column});
                }
              }
            }
          }
        }
      }
      final int[][] labels = label;
      return F.matrix((i, j) -> F.ZZ(labels[i][j]), height, width);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>DistanceTransform(image)</code>, <code>DistanceTransform(image, t)</code>: a
   * <code>"Real32"</code> image in which every pixel greater than <code>t</code> (<code>0</code> if
   * not given) has its Euclidean distance to the nearest pixel which isn't, and every other pixel
   * has <code>0</code>. The distances are in pixels and not scaled to <code>0...1</code>. An image
   * without such a pixel gets the distance to the outside of the image.
   */
  private static class DistanceTransform extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      double threshold = 0.0;
      if (ast.isAST2()) {
        threshold = ast.arg2().evalfNaN();
        if (Double.isNaN(threshold)) {
          return F.NIL;
        }
      }
      boolean[][] foreground = foreground(ast.arg1(), threshold, engine);
      if (foreground == null) {
        return F.NIL;
      }
      final int height = foreground.length;
      final int width = foreground[0].length;
      boolean background = false;
      for (int y = 0; y < height && !background; y++) {
        for (int x = 0; x < width && !background; x++) {
          background = !foreground[y][x];
        }
      }
      double[][] distance = new double[height][width];
      if (!background) {
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            distance[y][x] = Math.min(Math.min(y + 1, height - y), Math.min(x + 1, width - x));
          }
        }
        return toImage(new double[][][] {distance}, ImageOptions.DEFAULT);
      }
      // the exact squared distances in two passes: along the columns to the nearest background
      // pixel of the column, then along every row the lower envelope of the parabolas
      final double far = (double) (height + width) * (height + width);
      double[][] squared = new double[height][width];
      for (int x = 0; x < width; x++) {
        double[] column = new double[height];
        for (int y = 0; y < height; y++) {
          column[y] = foreground[y][x] ? far : 0.0;
        }
        double[] d = lowerEnvelope(column);
        for (int y = 0; y < height; y++) {
          squared[y][x] = d[y];
        }
      }
      for (int y = 0; y < height; y++) {
        double[] d = lowerEnvelope(squared[y]);
        for (int x = 0; x < width; x++) {
          distance[y][x] = Math.sqrt(d[x]);
        }
      }
      return toImage(new double[][][] {distance}, ImageOptions.DEFAULT);
    }

    /**
     * <code>d(p) == Min((p-q)^2 + f(q))</code> over <code>q</code>, the lower envelope of the
     * parabolas rooted at the samples (P. Felzenszwalb, D. Huttenlocher: Distance transforms of
     * sampled functions).
     */
    private static double[] lowerEnvelope(double[] f) {
      final int n = f.length;
      double[] d = new double[n];
      int[] vertex = new int[n];
      double[] boundary = new double[n + 1];
      int k = 0;
      vertex[0] = 0;
      boundary[0] = Double.NEGATIVE_INFINITY;
      boundary[1] = Double.POSITIVE_INFINITY;
      for (int q = 1; q < n; q++) {
        double s;
        while (true) {
          int v = vertex[k];
          s = ((f[q] + (double) q * q) - (f[v] + (double) v * v)) / (2.0 * q - 2.0 * v);
          if (s <= boundary[k] && k > 0) {
            k--;
          } else {
            break;
          }
        }
        k++;
        vertex[k] = q;
        boundary[k] = s;
        boundary[k + 1] = Double.POSITIVE_INFINITY;
      }
      k = 0;
      for (int q = 0; q < n; q++) {
        while (boundary[k + 1] < q) {
          k++;
        }
        int v = vertex[k];
        d[q] = (double) (q - v) * (q - v) + f[v];
      }
      return d;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFilterFunctions() {}
}
