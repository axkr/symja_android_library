package org.matheclipse.image.builtin;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Boof;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.expression.data.ImageExpr;

/**
 * Colour: <code>ColorConvert</code>, <code>ColorNegate</code>, <code>ColorSeparate</code>,
 * <code>ColorCombine</code>, <code>ColorQuantize</code>, <code>ColorReplace</code>,
 * <code>ColorDistance</code>, and the three alpha channel functions.
 *
 * <p>
 * <b>An image is always RGB or greyscale here.</b> An <code>ImageExpr</code> stores an sRGB or
 * greyscale bitmap and has nowhere to record a colour space, and <code>Image[data, ColorSpace
 * -&gt; "HSB"]</code> already <em>interprets</em> its data as HSB and stores the RGB result. So
 * <code>ColorConvert</code> of an image accepts only <code>"Grayscale"</code> and
 * <code>"RGB"</code>; handing back an image whose channels are secretly H, S and B would make
 * <code>ImageColorSpace</code> lie and would break the round trip through <code>ColorConvert</code>.
 *
 * <p>
 * The other colour spaces are reachable one channel at a time, where there is no ambiguity:
 * <code>ColorSeparate(image, "HSB")</code> gives three greyscale planes and
 * <code>ColorCombine({h, s, b}, "HSB")</code> puts them back together. A colour <em>directive</em>
 * has no such problem and converts to <code>"HSB"</code> and <code>"CMYK"</code> directly.
 */
public class ColorFunctions {

  private static class Initializer {

    private static void init() {
      S.AlphaChannel.setEvaluator(new AlphaChannel());
      S.ColorCombine.setEvaluator(new ColorCombine());
      S.ColorConvert.setEvaluator(new ColorConvert());
      S.ColorDistance.setEvaluator(new ColorDistance());
      S.ColorNegate.setEvaluator(new ColorNegate());
      S.ColorQuantize.setEvaluator(new ColorQuantize());
      S.ColorReplace.setEvaluator(new ColorReplace());
      S.ColorSeparate.setEvaluator(new ColorSeparate());
      S.RemoveAlphaChannel.setEvaluator(new RemoveAlphaChannel());
      S.SetAlphaChannel.setEvaluator(new SetAlphaChannel());
    }
  }

  /**
   * <code>ColorConvert(image, space)</code> or <code>ColorConvert(color, space)</code> - the same
   * colour in another colour space.
   */
  private static class ColorConvert extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      String space = ast.arg2().toString();
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image != null) {
        return convertImage(image, space);
      }
      float[] rgba = Colors.toRgba(ast.arg1());
      return rgba == null ? F.NIL : convertColor(rgba, space);
    }

    private static IExpr convertImage(BufferedImage image, String space) {
      int channels = Boof.channels(image);
      int width = image.getWidth();
      int height = image.getHeight();
      if ("Grayscale".equalsIgnoreCase(space) || "Gray".equalsIgnoreCase(space)) {
        return new ImageExpr(Pixels.fromPixels(width, height, 1,
            (x, y) -> new float[] {Boof.intensity(image, x, y)}), null);
      }
      if ("RGB".equalsIgnoreCase(space)) {
        return new ImageExpr(Pixels.fromPixels(width, height, 3, (x, y) -> {
          float[] values = Pixels.pixel(image, x, y, channels);
          return channels == 1 //
              ? new float[] {values[0], values[0], values[0]}
              : new float[] {values[0], values[1], values[2]};
        }), null);
      }
      // HSB, CMYK, LAB and the rest are not colour spaces an ImageExpr can be in - see the class
      // comment. ColorSeparate(image, space) is the way to reach their channels.
      return F.NIL;
    }

    private static IExpr convertColor(float[] rgba, String space) {
      if ("Grayscale".equalsIgnoreCase(space) || "Gray".equalsIgnoreCase(space)) {
        float gray = 0.299f * rgba[0] + 0.587f * rgba[1] + 0.114f * rgba[2];
        return F.GrayLevel(F.num(gray));
      }
      if ("RGB".equalsIgnoreCase(space)) {
        return Colors.toRGBColor(rgba);
      }
      if ("HSB".equalsIgnoreCase(space) || "HSV".equalsIgnoreCase(space)) {
        float[] hsb = Colors.rgbToHsb(rgba);
        return F.Hue(F.num(hsb[0]), F.num(hsb[1]), F.num(hsb[2]));
      }
      if ("CMYK".equalsIgnoreCase(space)) {
        float[] cmyk = Colors.rgbToCmyk(rgba);
        return F.quaternary(S.CMYKColor, F.num(cmyk[0]), F.num(cmyk[1]), F.num(cmyk[2]),
            F.num(cmyk[3]));
      }
      return F.NIL;
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

  /** <code>ColorNegate(image)</code> or <code>ColorNegate(color)</code> - the complementary colour. */
  private static class ColorNegate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image != null) {
        int channels = Boof.channels(image);
        int colorChannels = channels == 4 ? 3 : channels;
        return new ImageExpr(
            Pixels.fromPixels(image.getWidth(), image.getHeight(), channels, (x, y) -> {
              float[] values = Pixels.pixel(image, x, y, channels);
              float[] result = values.clone();
              for (int c = 0; c < colorChannels; c++) {
                result[c] = 255.0f - values[c];
              }
              return result;
            }), null);
      }
      float[] rgba = Colors.toRgba(ast.arg1());
      if (rgba == null) {
        return F.NIL;
      }
      return Colors.toRGBColor(
          new float[] {1.0f - rgba[0], 1.0f - rgba[1], 1.0f - rgba[2], rgba[3]});
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
   * <code>ColorSeparate(image)</code> - the channels as separate greyscale images.
   * <code>ColorSeparate(image, "R")</code> gives one of them; a colour space name converts first.
   */
  private static class ColorSeparate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr source = ast.arg1();
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        String name = ast.arg2().toString();
        int channel = channelIndex(name);
        if (channel < 0) {
          // a colour space name: one greyscale plane per channel of that space
          return separateSpace(ImagePropertyFunctions.bufferedImage(source), name);
        }
        BufferedImage image = ImagePropertyFunctions.bufferedImage(source);
        if (image == null) {
          return F.NIL;
        }
        IExpr channels = separate(source);
        if (!channels.isList() || ((IAST) channels).argSize() <= channel) {
          return F.NIL;
        }
        return ((IAST) channels).get(channel + 1);
      }
      return separate(source);
    }

    /** R, G, B and A as channel positions; anything else is a colour space name. */
    private static int channelIndex(String name) {
      switch (name) {
        case "R":
        case "Red":
          return 0;
        case "G":
        case "Green":
          return 1;
        case "B":
        case "Blue":
          return 2;
        case "A":
        case "Alpha":
          return 3;
        default:
          return -1;
      }
    }

    /**
     * One greyscale plane per channel of the named colour space. A plane carries no colour space of
     * its own, so this is well defined where converting the whole image would not be.
     */
    private static IExpr separateSpace(BufferedImage image, String space) {
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      int planes;
      if ("HSB".equalsIgnoreCase(space) || "HSV".equalsIgnoreCase(space)) {
        planes = 3;
      } else if ("CMYK".equalsIgnoreCase(space)) {
        planes = 4;
      } else if ("RGB".equalsIgnoreCase(space)) {
        planes = 3;
      } else if ("Grayscale".equalsIgnoreCase(space) || "Gray".equalsIgnoreCase(space)) {
        planes = 1;
      } else {
        return F.NIL;
      }
      final boolean hsb = planes == 3 && !"RGB".equalsIgnoreCase(space);
      final boolean cmyk = planes == 4;
      final boolean gray = planes == 1;
      IASTAppendable result = F.ListAlloc(planes);
      for (int p = 0; p < planes; p++) {
        final int plane = p;
        result.append(new ImageExpr(
            Pixels.fromPixels(image.getWidth(), image.getHeight(), 1, (x, y) -> {
              float[] rgb = unit(Pixels.pixel(image, x, y, channels), channels);
              if (gray) {
                return new float[] {(0.299f * rgb[0] + 0.587f * rgb[1] + 0.114f * rgb[2]) * 255.0f};
              }
              float[] converted = hsb ? Colors.rgbToHsb(rgb) : (cmyk ? Colors.rgbToCmyk(rgb) : rgb);
              return new float[] {converted[plane] * 255.0f};
            }), null));
      }
      return result;
    }

    private static IExpr separate(IExpr source) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(source);
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      IASTAppendable result = F.ListAlloc(channels);
      for (int c = 0; c < channels; c++) {
        final int channel = c;
        result.append(new ImageExpr(
            Pixels.fromPixels(image.getWidth(), image.getHeight(), 1,
                (x, y) -> new float[] {Pixels.pixel(image, x, y, channels)[channel]}),
            null));
      }
      return result;
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
   * <code>ColorCombine({image1, image2, image3})</code> - one colour image out of the channel
   * images. A fourth image becomes the alpha channel, and a colour space name says how to read the
   * channels.
   */
  private static class ColorCombine extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      int count = list.argSize();
      if (count < 1 || count > 4) {
        return F.NIL;
      }
      String space = "RGB";
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        space = ast.arg2().toString();
      }
      boolean hsb = "HSB".equalsIgnoreCase(space) || "HSV".equalsIgnoreCase(space);
      boolean cmyk = "CMYK".equalsIgnoreCase(space);
      if (!hsb && !cmyk && !"RGB".equalsIgnoreCase(space)
          && !"Grayscale".equalsIgnoreCase(space)) {
        return F.NIL;
      }
      if (cmyk && count != 4) {
        return F.NIL;
      }

      BufferedImage[] planes = new BufferedImage[count];
      for (int i = 0; i < count; i++) {
        planes[i] = ImagePropertyFunctions.bufferedImage(list.get(i + 1));
        if (planes[i] == null) {
          return F.NIL;
        }
        if (planes[i].getWidth() != planes[0].getWidth()
            || planes[i].getHeight() != planes[0].getHeight()) {
          return F.NIL;
        }
      }

      int outputChannels = count == 1 ? 1 : (cmyk ? 3 : Math.min(count, 4));
      final boolean toHsb = hsb;
      final boolean toCmyk = cmyk;
      return new ImageExpr(Pixels.fromPixels(planes[0].getWidth(), planes[0].getHeight(),
          outputChannels, (x, y) -> {
            float[] samples = new float[count];
            for (int i = 0; i < count; i++) {
              samples[i] = Pixels.pixel(planes[i], x, y, Boof.channels(planes[i]))[0] / 255.0f;
            }
            if (count == 1) {
              return new float[] {samples[0] * 255.0f};
            }
            float[] rgb;
            if (toHsb) {
              rgb = Colors.hsbToRgb(samples);
            } else if (toCmyk) {
              rgb = Colors.cmykToRgb(samples);
            } else {
              rgb = new float[] {samples[0], samples[1], samples[2]};
            }
            if (outputChannels == 4) {
              return scale(new float[] {rgb[0], rgb[1], rgb[2], samples[3]});
            }
            return scale(rgb);
          }), null);
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
   * <code>ColorQuantize(image, n)</code> - the image with at most <code>n</code> distinct colours,
   * chosen by median cut.
   *
   * <p>
   * Median cut splits the box of colours present in the image along its longest channel, at the
   * median, until there are <code>n</code> boxes; each box then contributes the mean of its colours.
   * It is deterministic, which a k-means palette would not be.
   */
  private static class ColorQuantize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int colorCount = ast.arg2().toIntDefault();
      if (colorCount < 1) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      int width = image.getWidth();
      int height = image.getHeight();

      List<float[]> colors = new ArrayList<>(width * height);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          colors.add(Pixels.pixel(image, x, y, channels));
        }
      }
      List<float[]> palette = medianCut(colors, colorCount, Math.min(channels, 3));
      if (palette.isEmpty()) {
        return F.NIL;
      }
      final int colorChannels = Math.min(channels, 3);
      return new ImageExpr(Pixels.fromPixels(width, height, channels, (x, y) -> {
        float[] values = Pixels.pixel(image, x, y, channels);
        float[] nearest = nearest(palette, values, colorChannels);
        float[] result = values.clone();
        System.arraycopy(nearest, 0, result, 0, colorChannels);
        return result;
      }), null);
    }

    private static List<float[]> medianCut(List<float[]> colors, int colorCount, int channels) {
      List<List<float[]>> boxes = new ArrayList<>();
      boxes.add(colors);
      while (boxes.size() < colorCount) {
        int widest = -1;
        int widestChannel = 0;
        float widestRange = 0.0f;
        for (int i = 0; i < boxes.size(); i++) {
          List<float[]> box = boxes.get(i);
          if (box.size() < 2) {
            continue;
          }
          for (int c = 0; c < channels; c++) {
            float low = Float.MAX_VALUE;
            float high = -Float.MAX_VALUE;
            for (float[] color : box) {
              low = Math.min(low, color[c]);
              high = Math.max(high, color[c]);
            }
            if (high - low > widestRange) {
              widestRange = high - low;
              widest = i;
              widestChannel = c;
            }
          }
        }
        if (widest < 0 || widestRange == 0.0f) {
          break;
        }
        List<float[]> box = boxes.remove(widest);
        final int channel = widestChannel;
        box.sort((left, right) -> Float.compare(left[channel], right[channel]));
        int middle = box.size() / 2;
        boxes.add(new ArrayList<>(box.subList(0, middle)));
        boxes.add(new ArrayList<>(box.subList(middle, box.size())));
      }

      List<float[]> palette = new ArrayList<>(boxes.size());
      for (List<float[]> box : boxes) {
        if (box.isEmpty()) {
          continue;
        }
        float[] mean = new float[channels];
        for (float[] color : box) {
          for (int c = 0; c < channels; c++) {
            mean[c] += color[c];
          }
        }
        for (int c = 0; c < channels; c++) {
          mean[c] /= box.size();
        }
        palette.add(mean);
      }
      return palette;
    }

    private static float[] nearest(List<float[]> palette, float[] color, int channels) {
      float[] best = palette.get(0);
      float bestDistance = Float.MAX_VALUE;
      for (float[] candidate : palette) {
        float distance = 0.0f;
        for (int c = 0; c < channels; c++) {
          float difference = candidate[c] - color[c];
          distance += difference * difference;
        }
        if (distance < bestDistance) {
          bestDistance = distance;
          best = candidate;
        }
      }
      return best;
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
   * <code>ColorReplace(image, old -&gt; new)</code> - replace every pixel close to <code>old</code>.
   * A list of rules replaces several colours at once, and a third argument widens the tolerance,
   * measured the way {@link ColorDistance} measures it.
   */
  private static class ColorReplace extends AbstractEvaluator {

    /** Half of one step of an 8 bit channel, in CIE76 units - close enough to "the same colour". */
    private static final double DEFAULT_TOLERANCE = 0.1;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      IAST rules = ast.arg2().isListOfRules(false) //
          ? (IAST) ast.arg2()
          : (ast.arg2().isRuleAST() ? F.List(ast.arg2()) : null);
      if (rules == null) {
        return F.NIL;
      }
      float[][] from = new float[rules.argSize()][];
      float[][] to = new float[rules.argSize()][];
      for (int i = 0; i < rules.argSize(); i++) {
        IAST rule = (IAST) rules.get(i + 1);
        from[i] = Colors.toRgba(rule.arg1());
        to[i] = Colors.toRgba(rule.arg2());
        if (from[i] == null || to[i] == null) {
          return F.NIL;
        }
      }
      double tolerance = DEFAULT_TOLERANCE;
      if (ast.argSize() >= 3) {
        tolerance = ast.arg3().evalfNaN();
        if (Double.isNaN(tolerance)) {
          return F.NIL;
        }
        // the third argument is a fraction of the colour space, CIE76 counts in about 100 units
        tolerance *= 100.0;
      }
      final double limit = tolerance;

      int channels = Boof.channels(image);
      int colorChannels = channels == 4 ? 3 : channels;
      return new ImageExpr(
          Pixels.fromPixels(image.getWidth(), image.getHeight(), channels, (x, y) -> {
            float[] values = Pixels.pixel(image, x, y, channels);
            float[] rgb = unit(values, channels);
            for (int i = 0; i < from.length; i++) {
              if (Colors.distance(rgb, from[i]) <= limit) {
                float[] result = values.clone();
                for (int c = 0; c < colorChannels; c++) {
                  result[c] = to[i][Math.min(c, 2)] * 255.0f;
                }
                return result;
              }
            }
            return values;
          }), null);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ColorDistance(color1, color2)</code> - the CIE 1976 difference between two colours.
   * <code>ColorDistance(image, color)</code> gives a greyscale image of the distance of every pixel.
   */
  private static class ColorDistance extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      float[] second = Colors.toRgba(ast.arg2());
      if (second == null) {
        return F.NIL;
      }
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image != null) {
        int channels = Boof.channels(image);
        return new ImageExpr(
            Pixels.fromPixels(image.getWidth(), image.getHeight(), 1, (x, y) -> {
              float[] rgb = unit(Pixels.pixel(image, x, y, channels), channels);
              // CIE76 runs to about 100, and the result has to fit in a greyscale image
              return new float[] {(float) (255.0 * Colors.distance(rgb, second) / 100.0)};
            }), null);
      }
      float[] first = Colors.toRgba(ast.arg1());
      return first == null ? F.NIL : F.num(Colors.distance(first, second));
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
   * <code>AlphaChannel(image)</code> - the transparency as a greyscale image. An image without an
   * alpha channel is fully opaque, so the result is all white.
   */
  private static class AlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      return new ImageExpr(
          Pixels.fromPixels(image.getWidth(), image.getHeight(), 1, (x, y) -> new float[] {
              channels == 4 ? Pixels.pixel(image, x, y, channels)[3] : 255.0f}),
          null);
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
   * <code>SetAlphaChannel(image, alpha)</code> - the image with the given transparency, taken from a
   * number or from a greyscale image. Without a second argument the image becomes fully opaque.
   */
  private static class SetAlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      int channels = Boof.channels(image);
      BufferedImage alphaImage = null;
      float alpha = 255.0f;
      if (ast.argSize() >= 2) {
        alphaImage = ImagePropertyFunctions.bufferedImage(ast.arg2());
        if (alphaImage == null) {
          double value = ast.arg2().evalfNaN();
          if (Double.isNaN(value)) {
            return F.NIL;
          }
          alpha = (float) (255.0 * value);
        } else if (alphaImage.getWidth() != image.getWidth()
            || alphaImage.getHeight() != image.getHeight()) {
          return F.NIL;
        }
      }
      final BufferedImage alphaPlane = alphaImage;
      final float constantAlpha = alpha;
      return new ImageExpr(
          Pixels.fromPixels(image.getWidth(), image.getHeight(), 4, (x, y) -> {
            float[] values = Pixels.pixel(image, x, y, channels);
            float[] result = new float[4];
            for (int c = 0; c < 3; c++) {
              result[c] = channels == 1 ? values[0] : values[c];
            }
            result[3] = alphaPlane == null //
                ? constantAlpha
                : Pixels.pixel(alphaPlane, x, y, Boof.channels(alphaPlane))[0];
            return result;
          }), null);
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
   * <code>RemoveAlphaChannel(image)</code> - the image composed onto white, or onto the colour given
   * as a second argument.
   */
  private static class RemoveAlphaChannel extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BufferedImage image = ImagePropertyFunctions.bufferedImage(ast.arg1());
      if (image == null) {
        return F.NIL;
      }
      float[] background = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
      if (ast.argSize() >= 2) {
        background = Colors.toRgba(ast.arg2());
        if (background == null) {
          return F.NIL;
        }
      }
      int channels = Boof.channels(image);
      if (channels != 4) {
        return ast.arg1();
      }
      final float[] onto = background;
      return new ImageExpr(
          Pixels.fromPixels(image.getWidth(), image.getHeight(), 3, (x, y) -> {
            float[] values = Pixels.pixel(image, x, y, 4);
            float alpha = values[3] / 255.0f;
            float[] result = new float[3];
            for (int c = 0; c < 3; c++) {
              result[c] = values[c] * alpha + onto[c] * 255.0f * (1.0f - alpha);
            }
            return result;
          }), null);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  // -------------------------------------------------------------------- internals

  /** Channel values on the 0 ... 255 scale as an RGB triple on the 0.0 ... 1.0 scale. */
  static float[] unit(float[] values, int channels) {
    if (channels == 1) {
      float gray = values[0] / 255.0f;
      return new float[] {gray, gray, gray};
    }
    return new float[] {values[0] / 255.0f, values[1] / 255.0f, values[2] / 255.0f};
  }

  /** Channel values on the 0.0 ... 1.0 scale back on the 0 ... 255 scale. */
  static float[] scale(float[] values) {
    float[] result = new float[values.length];
    for (int i = 0; i < values.length; i++) {
      result[i] = values[i] * 255.0f;
    }
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ColorFunctions() {}
}
