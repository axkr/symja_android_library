package org.matheclipse.image.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.algo.Colors;
import org.matheclipse.image.algo.Pixels;
import org.matheclipse.image.algo.Raster;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.image.expression.data.ImageOptions;

/**
 * Features found in an image and drawn onto it: <code>ImageLines</code> and
 * <code>HighlightImage</code>.
 *
 * <p>
 * Both work in the image coordinates of the graphics an image is shown as: the origin is the
 * bottom left corner, the image covers <code>[0, width] x [0, height]</code>, and the pixel in
 * column <code>i</code> and row <code>j</code> counted from the top has its centre at
 * <code>{i - 1/2, height - j + 1/2}</code>.
 */
public class ImageFeatureFunctions {

  private static class Initializer {

    private static void init() {
      S.HighlightImage.setEvaluator(new HighlightImage());
      S.ImageLines.setEvaluator(new ImageLines());
    }
  }

  /**
   * <code>ImageLines(image)</code> and <code>ImageLines(image, t)</code> - the straight lines in an
   * image, strongest first, each as a <code>Line</code> between the two points where it leaves the
   * image.
   *
   * <p>
   * A Hough transform: every pixel votes with its intensity for the lines through its centre. The
   * line with the most votes is fitted to the pixels close to it by total least squares; those
   * pixels are then taken out of the votes, so a pixel belongs to one line at most, and the next
   * line is looked for. A line needs pixels which add up to the fraction <code>t</code> of the
   * diagonal of the image, and at least two pixels. <code>MaxFeatures -&gt; n</code> stops after
   * <code>n</code> lines.
   */
  private static class ImageLines extends AbstractEvaluator {

    /** The default fraction of the diagonal a line has to cover. */
    private static final double DEFAULT_THRESHOLD = 0.25;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      int optionStart = 2;
      double threshold = DEFAULT_THRESHOLD;
      if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
        threshold = ast.arg2().evalfNaN();
        if (!(threshold >= 0.0)) {
          return F.NIL;
        }
        optionStart = 3;
      }
      int maxFeatures = Integer.MAX_VALUE;
      if (ast.argSize() >= optionStart) {
        OptionArgs options = new OptionArgs(ast.topHead(), ast, optionStart, engine);
        IExpr max = options.getOption(S.MaxFeatures);
        if (max.isPresent() && !max.isAutomatic() && !max.isInfinity() && max != S.All) {
          maxFeatures = max.toIntDefault();
          if (maxFeatures < 0) {
            return F.NIL;
          }
        }
      }
      Raster raster = ((ImageExpr) ast.arg1()).raster();
      if (raster == null) {
        return F.NIL;
      }
      return new HoughLines(raster, engine).lines(threshold, maxFeatures);
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /** The line search of {@link ImageLines} on one image. */
  private static final class HoughLines {
    /**
     * The largest distance of a pixel centre from the line it belongs to: the centres of the pixels
     * of a line drawn one pixel wide are at most half a pixel away from it.
     */
    private static final double BAND = 0.55;

    final int width;
    final int height;
    /** The intensity of the pixels, row by row from the top, clipped to <code>0 ... 1</code>. */
    final double[] weight;
    /** The pixel centres in image coordinates. */
    final double[] centreX;
    final double[] centreY;
    final int angles;
    final double[] cos;
    final double[] sin;
    /** The distance of the origin from the lines of the first bin. */
    final double rhoOffset;
    final int rhoBins;
    /** The votes, <code>[angle * rhoBins + rho]</code>. */
    final double[] votes;
    /** Checks the time limits in the loops over all pixels and bins. */
    final EvalEngine engine;

    HoughLines(Raster raster, EvalEngine engine) {
      this.engine = engine;
      width = raster.width;
      height = raster.height;
      int size = width * height;
      weight = new double[size];
      centreX = new double[size];
      centreY = new double[size];
      int colorChannels = raster.colorChannels();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int i = y * width + x;
          double value = colorChannels >= 3
              ? Colors.grayLevel(raster.get(x, y, 0), raster.get(x, y, 1), raster.get(x, y, 2))
              : raster.get(x, y, 0);
          weight[i] = Math.max(0.0, Math.min(1.0, value));
          centreX[i] = x + 0.5;
          centreY[i] = height - y - 0.5;
        }
      }
      double diagonal = Math.hypot(width, height);
      // about two bins per pixel along the circumference of the image, which places a line to a
      // fraction of a pixel inside the image
      angles = (int) Math.max(180, Math.min(1440, Math.ceil(Math.PI * diagonal)));
      cos = new double[angles];
      sin = new double[angles];
      for (int a = 0; a < angles; a++) {
        double theta = Math.PI * a / angles;
        cos[a] = Math.cos(theta);
        sin[a] = Math.sin(theta);
      }
      rhoOffset = Math.ceil(diagonal) + 1;
      rhoBins = 2 * (int) rhoOffset + 2;
      votes = new double[angles * rhoBins];
      for (int i = 0; i < size; i++) {
        if (weight[i] > 0.0) {
          vote(i, weight[i]);
        }
      }
    }

    /** Add the votes of pixel <code>i</code>, split linearly between the two nearest bins. */
    private void vote(int i, double w) {
      engine.checkTimeLimit();
      for (int a = 0; a < angles; a++) {
        double rho = centreX[i] * cos[a] + centreY[i] * sin[a] + rhoOffset;
        int bin = (int) Math.floor(rho);
        double fraction = rho - bin;
        votes[a * rhoBins + bin] += w * (1.0 - fraction);
        votes[a * rhoBins + bin + 1] += w * fraction;
      }
    }

    IAST lines(double threshold, int maxFeatures) {
      double minimum = Math.max(2.0, threshold * Math.hypot(width, height));
      IASTAppendable result = F.ListAlloc();
      // every round takes out at least one pixel or one pair of bins
      int rounds = weight.length + votes.length;
      while (result.argSize() < maxFeatures && rounds-- > 0) {
        // two neighbouring bins together hold all votes of a line wherever it lies between them
        int best = -1;
        double bestVotes = 0.0;
        for (int a = 0; a < angles; a++) {
          engine.checkTimeLimit();
          int row = a * rhoBins;
          for (int r = 0; r + 1 < rhoBins; r++) {
            double v = votes[row + r] + votes[row + r + 1];
            if (v > bestVotes) {
              bestVotes = v;
              best = row + r;
            }
          }
        }
        if (best < 0 || bestVotes < minimum - 1e-9) {
          break;
        }
        int a = best / rhoBins;
        double rho = best % rhoBins + votes[best + 1] / bestVotes - rhoOffset;
        // the pixels close to the line, fitted again so the band follows the fitted line
        double[] line = {rho * cos[a], rho * sin[a], -sin[a], cos[a]};
        for (int k = 0; k < 3; k++) {
          double[] fitted = fit(line, BAND);
          if (fitted == null) {
            break;
          }
          line = fitted;
        }
        double total = 0.0;
        int pixels = 0;
        for (int i = 0; i < weight.length; i++) {
          if (weight[i] > 0.0 && distance(line, i) <= BAND) {
            total += weight[i];
            pixels++;
          }
        }
        // a line drawn more than a pixel wide goes as a whole: the parallel strips next to it go
        // with it as long as they are at least half as full as the line itself
        double lower = -BAND;
        double upper = BAND;
        while (pixels > 0 && strip(line, upper, upper + 1.0) * 2 >= pixels) {
          upper += 1.0;
        }
        while (pixels > 0 && strip(line, lower - 1.0, lower) * 2 >= pixels) {
          lower -= 1.0;
        }
        for (int i = 0; i < weight.length; i++) {
          if (weight[i] > 0.0) {
            double d = signedDistance(line, i);
            if (lower <= d && d <= upper) {
              vote(i, -weight[i]);
              weight[i] = 0.0;
            }
          }
        }
        if (pixels == 0) {
          votes[best] = 0.0;
          votes[best + 1] = 0.0;
          continue;
        }
        boolean isLine = pixels >= 2 && total >= minimum - 1e-9;
        if (isLine) {
          IExpr segment = clip(line);
          if (segment.isPresent()) {
            result.append(segment);
          }
        }
      }
      return result;
    }

    /** The distance of the centre of pixel <code>i</code> from <code>{x0, y0, dx, dy}</code>. */
    private double distance(double[] line, int i) {
      return Math.abs(signedDistance(line, i));
    }

    /** The distance of the centre of pixel <code>i</code>, positive on the one side of the line. */
    private double signedDistance(double[] line, int i) {
      return (centreX[i] - line[0]) * line[3] - (centreY[i] - line[1]) * line[2];
    }

    /** The number of pixels whose signed distance from the line is in <code>(from, to]</code>. */
    private int strip(double[] line, double from, double to) {
      int count = 0;
      for (int i = 0; i < weight.length; i++) {
        if (weight[i] > 0.0) {
          double d = signedDistance(line, i);
          if (from < d && d <= to) {
            count++;
          }
        }
      }
      return count;
    }

    /**
     * The line through the weighted centre of the pixels within <code>band</code> of
     * <code>line</code>, along the principal axis of their spread, as
     * <code>{x0, y0, dx, dy}</code> with a unit direction: the total least squares fit.
     *
     * @return <code>null</code> if no pixel is close to the line
     */
    private double[] fit(double[] line, double band) {
      double sum = 0.0;
      double sx = 0.0;
      double sy = 0.0;
      for (int i = 0; i < weight.length; i++) {
        if (weight[i] > 0.0 && distance(line, i) <= band) {
          sum += weight[i];
          sx += weight[i] * centreX[i];
          sy += weight[i] * centreY[i];
        }
      }
      if (sum <= 0.0) {
        return null;
      }
      double mx = sx / sum;
      double my = sy / sum;
      double xx = 0.0;
      double xy = 0.0;
      double yy = 0.0;
      for (int i = 0; i < weight.length; i++) {
        if (weight[i] > 0.0 && distance(line, i) <= band) {
          double dx = centreX[i] - mx;
          double dy = centreY[i] - my;
          xx += weight[i] * dx * dx;
          xy += weight[i] * dx * dy;
          yy += weight[i] * dy * dy;
        }
      }
      if (xx + yy == 0.0) {
        // a single pixel keeps the direction of the votes
        return new double[] {mx, my, line[2], line[3]};
      }
      double angle = 0.5 * Math.atan2(2.0 * xy, xx - yy);
      return new double[] {mx, my, Math.cos(angle), Math.sin(angle)};
    }

    /**
     * The part of <code>line</code> inside the image, from left to right, or from top to bottom for
     * a vertical line.
     */
    private IExpr clip(double[] line) {
      double x0 = line[0];
      double y0 = line[1];
      double dx = line[2];
      double dy = line[3];
      if (Math.abs(dx) < 1e-12) {
        dx = 0.0;
      }
      if (Math.abs(dy) < 1e-12) {
        dy = 0.0;
      }
      if (dx < 0.0 || (dx == 0.0 && dy > 0.0)) {
        dx = -dx;
        dy = -dy;
      }
      double[] range = clipRange(x0, y0, dx, dy, width, height);
      if (range == null || !(range[0] < range[1])) {
        return F.NIL;
      }
      return F.Line(F.List(point(x0 + range[0] * dx, y0 + range[0] * dy),
          point(x0 + range[1] * dx, y0 + range[1] * dy)));
    }

    /** A point on the border of the image, its coordinate on the border exactly. */
    private IAST point(double x, double y) {
      return F.List(F.num(snap(x, width)), F.num(snap(y, height)));
    }

    private static double snap(double value, int size) {
      if (Math.abs(value) < 1e-9) {
        return 0.0;
      }
      if (Math.abs(value - size) < 1e-9) {
        return size;
      }
      return value;
    }
  }

  /**
   * <code>HighlightImage(image, primitives)</code> - the image with the graphics primitives
   * <code>Line</code>, <code>Point</code>, <code>Rectangle</code> and <code>Circle</code>, or lists
   * of them, drawn on it in red, in the coordinates of the image. A colour in a list draws the
   * primitives which follow it in that list in that colour. The result is an RGB image with an
   * opacity channel, of the type <code>"Byte"</code> and the size of the image.
   */
  private static class HighlightImage extends AbstractEvaluator {

    private static final double[] RED = {1.0, 0.0, 0.0, 1.0};

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ImageExpr)) {
        return F.NIL;
      }
      Raster source = ((ImageExpr) ast.arg1()).raster();
      if (source == null) {
        return F.NIL;
      }
      Raster canvas = new Raster(source.width, source.height, 4);
      int colorChannels = source.colorChannels();
      boolean hasAlpha = source.channels != colorChannels;
      for (int y = 0; y < source.height; y++) {
        for (int x = 0; x < source.width; x++) {
          for (int c = 0; c < 3; c++) {
            canvas.set(x, y, c, source.get(x, y, colorChannels >= 3 ? c : 0));
          }
          canvas.set(x, y, 3, hasAlpha ? source.get(x, y, colorChannels) : 1.0);
        }
      }
      draw(ast.arg2(), canvas, RED);
      ImageExpr image = ImageExpr.fromRaster(canvas, ImageOptions.DEFAULT, Pixels.BYTE);
      return image == null ? F.NIL : image;
    }

    /**
     * Draw the primitives onto the canvas. What isn't a primitive, a colour or a list of them is
     * passed over; a primitive wrapped in another, as <code>Line(Line(...))</code>, is drawn as the
     * inner one.
     */
    private static void draw(IExpr primitives, Raster canvas, double[] color) {
      if (primitives.isList()) {
        double[] listColor = color;
        for (IExpr primitive : (IAST) primitives) {
          double[] newColor =
              primitive.isList() || isPrimitive(primitive) ? null : color(primitive);
          if (newColor != null) {
            listColor = newColor;
          } else {
            draw(primitive, canvas, listColor);
          }
        }
        return;
      }
      if (!isPrimitive(primitives) || primitives.argSize() == 0) {
        return;
      }
      if (isPrimitive(primitives.first())) {
        draw(primitives.first(), canvas, color);
        return;
      }
      if (primitives.isAST(S.Line, 2)) {
        IExpr points = primitives.first();
        if (points.isListOfLists() && points.first().isListOfLists()) {
          // several lines
          for (IExpr part : (IAST) points) {
            polyline((IAST) part, canvas, color);
          }
        } else if (points.isListOfLists()) {
          polyline((IAST) points, canvas, color);
        }
      } else if (primitives.isAST(S.Point, 2)) {
        IExpr points = primitives.first();
        if (points.isListOfLists()) {
          for (IExpr p : (IAST) points) {
            point(p, canvas, color);
          }
        } else {
          point(points, canvas, color);
        }
      } else if (primitives.isAST(S.Rectangle, 2, 3)) {
        double[] lower = coordinates(primitives.first());
        double[] upper = primitives.argSize() == 2 ? coordinates(primitives.second())
            : lower == null ? null : new double[] {lower[0] + 1.0, lower[1] + 1.0};
        if (lower != null && upper != null) {
          double[][] corners = {lower, {upper[0], lower[1]}, upper, {lower[0], upper[1]}, lower};
          for (int k = 0; k < 4; k++) {
            segment(corners[k], corners[k + 1], canvas, color);
          }
        }
      } else if (primitives.isAST(S.Circle, 2, 3)) {
        double[] centre = coordinates(primitives.first());
        double radius = primitives.argSize() == 2 ? primitives.second().evalfNaN() : 1.0;
        if (centre != null && radius >= 0.0 && radius < 1e6) {
          int steps = (int) Math.max(16, Math.ceil(8.0 * Math.PI * radius));
          double[] last = {centre[0] + radius, centre[1]};
          for (int k = 1; k <= steps; k++) {
            double phi = 2.0 * Math.PI * k / steps;
            double[] next =
                {centre[0] + radius * Math.cos(phi), centre[1] + radius * Math.sin(phi)};
            segment(last, next, canvas, color);
            last = next;
          }
        }
      }
    }

    /**
     * The colour of a colour directive, or the last colour in a <code>Directive(...)</code>.
     *
     * @return <code>null</code> if <code>expr</code> sets no colour
     */
    private static double[] color(IExpr expr) {
      if (expr.isAST(S.Directive)) {
        double[] result = null;
        for (IExpr directive : (IAST) expr) {
          double[] c = color(directive);
          if (c != null) {
            result = c;
          }
        }
        return result;
      }
      return expr.isAST() ? Colors.toRgbaDouble(expr) : null;
    }

    private static boolean isPrimitive(IExpr expr) {
      return expr.isAST(S.Line) || expr.isAST(S.Point) || expr.isAST(S.Rectangle)
          || expr.isAST(S.Circle);
    }

    /** The coordinates of a point <code>{x, y}</code>, or <code>null</code>. */
    private static double[] coordinates(IExpr point) {
      if (!point.isList2()) {
        return null;
      }
      double x = point.first().evalfNaN();
      double y = point.second().evalfNaN();
      return Double.isFinite(x) && Double.isFinite(y) ? new double[] {x, y} : null;
    }

    /** The segments between consecutive points; a point which isn't one breaks the line. */
    private static void polyline(IAST points, Raster canvas, double[] color) {
      double[] last = null;
      for (IExpr p : points) {
        double[] next = coordinates(p);
        if (next != null) {
          if (last == null) {
            plot(next[0], next[1], canvas, color);
          } else {
            segment(last, next, canvas, color);
          }
        }
        last = next;
      }
    }

    /** A point is the pixel it lies in and its four neighbours. */
    private static void point(IExpr p, Raster canvas, double[] color) {
      double[] xy = coordinates(p);
      if (xy != null) {
        plot(xy[0], xy[1], canvas, color);
        plot(xy[0] - 1.0, xy[1], canvas, color);
        plot(xy[0] + 1.0, xy[1], canvas, color);
        plot(xy[0], xy[1] - 1.0, canvas, color);
        plot(xy[0], xy[1] + 1.0, canvas, color);
      }
    }

    /**
     * Colour the pixels the part of the segment inside the image passes through, sampled every
     * quarter of a pixel.
     */
    private static void segment(double[] from, double[] to, Raster canvas, double[] color) {
      double dx = to[0] - from[0];
      double dy = to[1] - from[1];
      double[] range = clipRange(from[0], from[1], dx, dy, canvas.width, canvas.height);
      if (range == null) {
        if (dx == 0.0 && dy == 0.0) {
          plot(from[0], from[1], canvas, color);
        }
        return;
      }
      double tMin = Math.max(0.0, range[0]);
      double tMax = Math.min(1.0, range[1]);
      if (tMin > tMax) {
        return;
      }
      int steps = (int) Math.ceil(4.0 * (tMax - tMin) * Math.hypot(dx, dy));
      for (int k = 0; k <= steps; k++) {
        double t = steps == 0 ? tMin : tMin + (tMax - tMin) * k / steps;
        plot(from[0] + t * dx, from[1] + t * dy, canvas, color);
      }
    }

    /** Colour the pixel the point <code>{x, y}</code> lies in, if it is inside the image. */
    private static void plot(double x, double y, Raster canvas, double[] color) {
      // a point on the right or top border belongs to the last pixel
      int column = (int) Math.floor(Math.min(x, canvas.width - 0.5));
      int row = canvas.height - 1 - (int) Math.floor(Math.min(y, canvas.height - 0.5));
      if (x < 0.0 || y < 0.0 || x > canvas.width || y > canvas.height) {
        return;
      }
      for (int c = 0; c < 4; c++) {
        canvas.set(column, row, c, color[c]);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * The parameters <code>{tMin, tMax}</code> of the part of the line
   * <code>{x0, y0} + t {dx, dy}</code> inside <code>[0, width] x [0, height]</code>.
   *
   * @return <code>null</code> if the line misses the box or doesn't move
   */
  private static double[] clipRange(double x0, double y0, double dx, double dy, int width,
      int height) {
    if (dx == 0.0 && dy == 0.0) {
      return null;
    }
    double tMin = Double.NEGATIVE_INFINITY;
    double tMax = Double.POSITIVE_INFINITY;
    double[] origin = {x0, y0};
    double[] direction = {dx, dy};
    int[] size = {width, height};
    for (int k = 0; k < 2; k++) {
      if (direction[k] != 0.0) {
        double t1 = -origin[k] / direction[k];
        double t2 = (size[k] - origin[k]) / direction[k];
        tMin = Math.max(tMin, Math.min(t1, t2));
        tMax = Math.min(tMax, Math.max(t1, t2));
      } else if (origin[k] < 0.0 || origin[k] > size[k]) {
        return null;
      }
    }
    return tMin <= tMax ? new double[] {tMin, tMax} : null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ImageFeatureFunctions() {}
}
