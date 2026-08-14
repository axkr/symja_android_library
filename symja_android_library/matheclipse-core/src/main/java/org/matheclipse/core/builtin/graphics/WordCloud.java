package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * WordCloud(data)
 * </pre>
 * 
 * * * Generates a word cloud graphic in which words are sized according to their multiplicity or
 * specified weights.
 */
public class WordCloud extends AbstractFunctionOptionEvaluator {

  private static class WordItem implements Comparable<WordItem> {
    IExpr word;
    double weight;
    /** The weight after {@code ScalingFunctions}, which is what the size is worked out from. */
    double scaledWeight;
    /** Where this word sits between the smallest and largest, in 0..1. */
    double fraction;
    double fontSize;
    double finalX;
    double finalY;
    /** Writing direction, {@code {1, 0}} for the usual left to right. */
    double dirX = 1;
    double dirY = 0;

    WordItem(IExpr word, double weight) {
      this.word = word;
      this.weight = weight;
      this.scaledWeight = weight;
    }

    /** True when the word reads bottom to top rather than left to right. */
    boolean isUpright() {
      return Math.abs(dirY) > Math.abs(dirX);
    }

    @Override
    public int compareTo(WordItem o) {
      return Double.compare(o.weight, this.weight); // Sort descending
    }
  }

  private static class Rect {
    double x, y, width, height;

    Rect(double x, double y, double width, double height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    boolean intersects(Rect other) {
      // Using >= and <= ensures that touching edges are treated safely
      return !(other.x >= x + width || other.x + other.width <= x || other.y >= y + height
          || other.y + other.height <= y);
    }
  }

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (argSize >= 1) {
      IExpr data = ast.arg1();
      List<WordItem> items = parseData(data, engine);

      if (items == null || items.isEmpty()) {
        return F.Graphics(F.CEmptyList);
      }

      IExpr wordOrientation =
          GraphicsOptions.optionValue(originalAST, S.WordOrientation, S.Automatic);
      IExpr wordSpacings = GraphicsOptions.optionValue(originalAST, S.WordSpacings, S.Automatic);
      IExpr colorFunctionSpec =
          GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic);
      IExpr scalingFunctions =
          GraphicsOptions.optionValue(originalAST, S.ScalingFunctions, S.Automatic);

      // ScalingFunctions decides how a weight turns into a size, so it is applied before the
      // weights are spread over the font range
      applyScaling(items, scalingFunctions);

      double minW = Double.MAX_VALUE;
      double maxW = -Double.MAX_VALUE;
      for (WordItem item : items) {
        if (item.scaledWeight < minW)
          minW = item.scaledWeight;
        if (item.scaledWeight > maxW)
          maxW = item.scaledWeight;
      }

      Collections.sort(items);

      // Distribute sizes in abstract layout space
      double minFontSize = 10.0;
      double maxFontSize = 60.0; // Increased for better visual contrast
      for (WordItem item : items) {
        item.fraction = maxW == minW ? 1.0 : (item.scaledWeight - minW) / (maxW - minW);
        item.fontSize =
            maxW == minW ? maxFontSize : minFontSize + item.fraction * (maxFontSize - minFontSize);
      }

      double spacing = spacingOf(wordSpacings);
      for (int i = 0; i < items.size(); i++) {
        double angle = orientationAngle(wordOrientation, i);
        items.get(i).dirX = Math.cos(angle);
        items.get(i).dirY = Math.sin(angle);
      }

      List<Rect> placedRects = new ArrayList<>();
      double overallMinX = Double.MAX_VALUE, overallMaxX = -Double.MAX_VALUE;
      double overallMinY = Double.MAX_VALUE, overallMaxY = -Double.MAX_VALUE;

      for (WordItem item : items) {
        String text =
            item.word.isString() ? item.word.toString().replace("\"", "") : item.word.toString();

        // Generous bounding box to prevent touching
        // Width: ~0.6 per char + 0.4 padding. Height: 1.2 to account for ascenders/descenders.
        // WordSpacings widens that padding, in multiples of the word's own size.
        double rectWidth = Math.max(text.length(), 1) * item.fontSize * 0.6
            + item.fontSize * (0.4 + 2.0 * spacing);
        double rectHeight = item.fontSize * (1.2 + 2.0 * spacing);
        if (item.isUpright()) {
          // a word written upwards takes up the room the other way round
          double swap = rectWidth;
          rectWidth = rectHeight;
          rectHeight = swap;
        }

        double theta = 0.0;
        boolean placed = false;

        while (!placed) {
          // Radius expands gradually
          double r = 3.0 * theta;

          double x = r * Math.cos(theta);
          // Multiply y by 0.6 to make the spiral elliptical (matches 600x400 landscape aspect
          // ratio)
          double y = r * Math.sin(theta) * 0.6;

          Rect cand = new Rect(x - rectWidth / 2.0, y - rectHeight / 2.0, rectWidth, rectHeight);

          boolean overlap = false;
          for (Rect pr : placedRects) {
            if (cand.intersects(pr)) {
              overlap = true;
              break;
            }
          }

          if (!overlap) {
            placedRects.add(cand);
            item.finalX = x;
            item.finalY = y;
            placed = true;

            if (cand.x < overallMinX)
              overallMinX = cand.x;
            if (cand.x + cand.width > overallMaxX)
              overallMaxX = cand.x + cand.width;
            if (cand.y < overallMinY)
              overallMinY = cand.y;
            if (cand.y + cand.height > overallMaxY)
              overallMaxY = cand.y + cand.height;
          } else {
            // Adaptive theta step: move by ~2.0 units along the arc to find tight fits safely
            double dTheta = 2.0 / Math.max(r, 1.0);
            theta += Math.min(dTheta, 0.5);
          }
        }
      }

      double W = overallMaxX - overallMinX;
      double H = overallMaxY - overallMinY;
      if (W == 0)
        W = 1;
      if (H == 0)
        H = 1;

      // Add proportional 5% padding to the final box
      overallMinX -= W * 0.05;
      overallMaxX += W * 0.05;
      overallMinY -= H * 0.05;
      overallMaxY += H * 0.05;
      W = overallMaxX - overallMinX;
      H = overallMaxY - overallMinY;

      // Identify bounding boxes against expected SVG output
      double imgW = 600.0;
      double imgH = 400.0;
      IExpr imageSizeOpt = options[0];
      if (imageSizeOpt != null) {
        if (imageSizeOpt.isList2()) {
          double width = imageSizeOpt.first().evalfNaN();
          double height = imageSizeOpt.second().evalfNaN();
          if (!Double.isNaN(width) && !Double.isNaN(height)) {
            imgW = width;
            imgH = height;
          }
        } else if (imageSizeOpt.isNumber()) {
          double width = imageSizeOpt.evalfNaN();
          if (!Double.isNaN(width)) {
            imgW = width;
            imgH = width;
          }
        }
      }

      double drawW = imgW - 10.0;
      double drawH = imgH - 10.0;
      double targetRatio = H / W;
      double screenRatio = drawH / drawW;
      double scaleY;

      if (targetRatio > screenRatio) {
        scaleY = drawH / H;
      } else {
        double effW = drawW;
        double effH = effW * targetRatio;
        scaleY = effH / H;
      }

      IASTAppendable insets = F.ListAlloc(items.size());
      int colorIndex = 0;
      // a ColorFunction paints by weight; without one the words cycle through the palette
      java.util.function.DoubleFunction<IExpr> colorFn =
          GraphicsOptions.colorFunction(colorFunctionSpec, engine, t -> F.NIL);

      for (WordItem item : items) {
        IExpr colorAST = colorFn.apply(item.fraction);
        if (!colorAST.isPresent()) {
          RGBColor rgb =
              GraphicsOptions.PLOT_COLORS[colorIndex % GraphicsOptions.PLOT_COLORS.length];
          colorAST = F.RGBColor(F.num(rgb.getRed() / 255.0), F.num(rgb.getGreen() / 255.0),
              F.num(rgb.getBlue() / 255.0));
        }
        colorIndex++;

        // Translate proportional abstract font to Scaled representation required by SVG mapping
        double f = (item.fontSize * scaleY) / imgW;

        IAST style =
            F.Style(item.word, F.Rule(S.FontSize, F.Scaled(f)), F.Rule(S.FontColor, colorAST));

        // the fourth argument is the writing direction; left as Automatic the word reads the
        // usual way, and the emitted expression stays what it always was
        IExpr direction = item.dirY == 0 && item.dirX > 0 ? S.Automatic
            : F.List(F.num(item.dirX), F.num(item.dirY));
        IAST inset =
            F.Inset(style, F.List(F.num(item.finalX), F.num(item.finalY)), S.Center, direction);
        insets.append(inset);
      }

      IASTAppendable graphicsExpr = F.Graphics();
      graphicsExpr.append(F.List(F.Style(insets, F.CEmptyList)));

      IAST plotRange = F.Rule(S.PlotRange, F.List(F.List(F.num(overallMinX), F.num(overallMaxX)),
          F.List(F.num(overallMinY), F.num(overallMaxY))));
      graphicsExpr.append(plotRange);
      // System.out.println(graphicsExpr);
      return graphicsExpr;
    }
    return F.NIL;
  }

  /**
   * Re-weight the words for {@code ScalingFunctions}, which decides how a count turns into a size.
   *
   * <p>
   * A scale that cannot take one of the weights -- a log of a count of zero, say -- leaves the
   * weights alone rather than dropping the word or sizing it off the scale.
   */
  private static void applyScaling(List<WordItem> items, IExpr scalingFunctions) {
    if (scalingFunctions == null || scalingFunctions == S.Automatic || scalingFunctions.isNone()) {
      return;
    }
    IExpr spec =
        scalingFunctions.isList() && scalingFunctions.size() > 1 ? ((IAST) scalingFunctions).arg1()
            : scalingFunctions;
    if (!spec.isString()) {
      return;
    }
    DoubleUnaryOperator scale = GraphicsOptions.getScalingFunction(spec.toString());
    double[] scaled = new double[items.size()];
    for (int i = 0; i < items.size(); i++) {
      scaled[i] = scale.applyAsDouble(items.get(i).weight);
      if (!Double.isFinite(scaled[i])) {
        return;
      }
    }
    for (int i = 0; i < items.size(); i++) {
      items.get(i).scaledWeight = scaled[i];
    }
  }

  /**
   * Extra room to leave around each word, as a multiple of its own size.
   *
   * @param wordSpacings the option value; a number, or a {@code {x, y}} pair of which the larger is
   *        taken
   */
  private static double spacingOf(IExpr wordSpacings) {
    if (wordSpacings == null || wordSpacings == S.Automatic || wordSpacings.isNone()) {
      return 0.0;
    }
    IExpr value = wordSpacings;
    if (wordSpacings.isList() && wordSpacings.size() > 1) {
      IAST pair = (IAST) wordSpacings;
      double widest = 0;
      for (int i = 1; i < pair.size(); i++) {
        double v = pair.get(i).evalfNaN();
        if (Double.isFinite(v)) {
          widest = Math.max(widest, v);
        }
      }
      return Math.max(0, widest);
    }
    double spacing = value.evalfNaN();
    return Double.isFinite(spacing) && spacing > 0 ? spacing : 0.0;
  }

  /**
   * Which way the word at this position is written, in radians.
   *
   * <p>
   * {@code "Random"} is spread over the words by position rather than drawn at random, because two
   * renderings of the same cloud have to come out the same.
   *
   * @param wordOrientation the option value: a direction name, an angle, or a list to cycle
   * @param index position of the word, largest first
   */
  private static double orientationAngle(IExpr wordOrientation, int index) {
    if (wordOrientation == null || wordOrientation == S.Automatic || wordOrientation.isNone()) {
      return 0.0;
    }
    IExpr spec = wordOrientation;
    if (wordOrientation.isList() && wordOrientation.size() > 1) {
      IAST list = (IAST) wordOrientation;
      spec = list.get(1 + Math.floorMod(index, list.argSize()));
    }
    if (spec.isString()) {
      String name = spec.toString();
      if ("Vertical".equalsIgnoreCase(name)) {
        return Math.PI / 2.0;
      }
      if ("Random".equalsIgnoreCase(name)) {
        // every third word stands up, which mixes the two without needing a random source
        return Math.floorMod(index, 3) == 1 ? Math.PI / 2.0 : 0.0;
      }
      return 0.0;
    }
    double angle = spec.evalfNaN();
    return Double.isFinite(angle) ? angle : 0.0;
  }

  /**
   * Safely extracts a numeric weight from expressions, handling Number, Quantity, and Missing
   * cases.
   */
  private double getWeightValue(IExpr expr, EvalEngine engine) {
    if (expr.isNumber()) {
      return expr.evalfNaN();
    }
    if (expr.isQuantity()) {
      double value = expr.first().evalfNaN();
      if (!Double.isNaN(value)) {
        return value;
      }
    }
    if (expr.isAST(S.Missing)) {
      return Double.NaN;
    }
    try {
      IExpr evaled = engine.evaluate(expr);
      if (evaled.isNumber()) {
        return evaled.evalfNaN();
      }
      if (evaled.isQuantity()) {
        return evaled.first().evalfNaN();
      }
    } catch (Exception e) {
    }
    return Double.NaN;
  }

  private List<WordItem> parseData(IExpr data, EvalEngine engine) {
    List<WordItem> items = new ArrayList<>();

    if (data.isString()) {
      // Parse sentence into words
      String text = data.toString().replace("\"", "");
      String[] words = text.split("\\s+");
      Map<String, Integer> counts = new HashMap<>();
      for (String w : words) {
        String clean = w.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (!clean.isEmpty()) {
          counts.put(clean, counts.getOrDefault(clean, 0) + 1);
        }
      }
      for (Map.Entry<String, Integer> entry : counts.entrySet()) {
        items.add(new WordItem(F.stringx(entry.getKey()), entry.getValue()));
      }
      return items;
    }

    if (data.isList()) {
      IAST list = (IAST) data;
      if (list.argSize() == 0)
        return items;

      IExpr first = list.arg1();

      if (first.isRuleAST()) {
        // Form: {w1 -> s1, ...} or {s1 -> w1, ...}
        for (int i = 1; i < list.size(); i++) {
          IExpr element = list.get(i);
          if (element.isRuleAST()) {
            IAST rule = (IAST) element;
            double wLHS = getWeightValue(rule.arg1(), engine);
            double wRHS = getWeightValue(rule.arg2(), engine);

            // Best effort guess if weight is LHS or RHS
            if (!Double.isNaN(wLHS)) {
              items.add(new WordItem(rule.arg2(), wLHS));
            } else if (!Double.isNaN(wRHS)) {
              items.add(new WordItem(rule.arg1(), wRHS));
            } else {
              items.add(new WordItem(rule.arg1(), 1.0)); // fallback
            }
          }
        }
      } else if (first.isList() && ((IAST) first).argSize() >= 2) {
        // Form: {{s1, w1}, {s2, w2}, ...}
        for (int i = 1; i < list.size(); i++) {
          IExpr element = list.get(i);
          if (element.isList() && ((IAST) element).argSize() >= 2) {
            IAST pair = (IAST) element;
            double weight = getWeightValue(pair.arg2(), engine);
            if (!Double.isNaN(weight)) {
              items.add(new WordItem(pair.arg1(), weight));
            }
          }
        }
      } else {
        // Form: {s1, s2, s3, s1, ...} -> Count frequencies
        Map<IExpr, Integer> counts = new HashMap<>();
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr word = list.get(i);
          counts.put(word, counts.getOrDefault(word, 0) + 1);
        }
        for (Map.Entry<IExpr, Integer> entry : counts.entrySet()) {
          items.add(new WordItem(entry.getKey(), entry.getValue()));
        }
      }
    } else if (data.isRuleAST()) {
      // Form: {w1, w2, ...} -> {s1, s2, ...}
      IAST rule = (IAST) data;
      if (rule.arg1().isList() && rule.arg2().isList()) {
        IAST weights = (IAST) rule.arg1();
        IAST words = (IAST) rule.arg2();
        int minLen = Math.min(weights.argSize(), words.argSize());
        for (int i = 1; i <= minLen; i++) {
          double w = getWeightValue(weights.get(i), engine);
          if (Double.isNaN(w))
            w = 1.0;
          items.add(new WordItem(words.get(i), w));
        }
      }
    } else if (data.isAST(S.Association)) {
      // Form: <| s1 -> w1, ... |>
      IAST assoc = (IAST) data;
      for (int i = 1; i <= assoc.argSize(); i++) {
        IExpr element = assoc.get(i);
        if (element.isRuleAST()) {
          IAST rule = (IAST) element;
          double w = getWeightValue(rule.arg2(), engine);
          if (Double.isNaN(w))
            w = 1.0;
          items.add(new WordItem(rule.arg1(), w));
        }
      }
    }

    return items;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.wordCloudExtras(
        new GraphicsOptions.OptionSet().add(new IBuiltInSymbol[] {S.ImageSize, S.PlotRange},
            new IExpr[] {S.Automatic, S.Automatic}));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
