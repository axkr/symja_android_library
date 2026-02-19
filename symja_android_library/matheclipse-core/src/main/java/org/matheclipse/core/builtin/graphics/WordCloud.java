package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;

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
    double fontSize;
    double finalX;
    double finalY;

    WordItem(IExpr word, double weight) {
      this.word = word;
      this.weight = weight;
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

      double minW = Double.MAX_VALUE;
      double maxW = -Double.MAX_VALUE;
      for (WordItem item : items) {
        if (item.weight < minW)
          minW = item.weight;
        if (item.weight > maxW)
          maxW = item.weight;
      }

      Collections.sort(items);

      // Distribute sizes in abstract layout space
      double minFontSize = 10.0;
      double maxFontSize = 60.0; // Increased for better visual contrast
      for (WordItem item : items) {
        if (maxW == minW) {
          item.fontSize = maxFontSize;
        } else {
          item.fontSize =
              minFontSize + ((item.weight - minW) / (maxW - minW)) * (maxFontSize - minFontSize);
        }
      }

      List<Rect> placedRects = new ArrayList<>();
      double overallMinX = Double.MAX_VALUE, overallMaxX = -Double.MAX_VALUE;
      double overallMinY = Double.MAX_VALUE, overallMaxY = -Double.MAX_VALUE;

      for (WordItem item : items) {
        String text =
            item.word.isString() ? item.word.toString().replace("\"", "") : item.word.toString();

        // Generous bounding box to prevent touching
        // Width: ~0.6 per char + 0.4 padding. Height: 1.2 to account for ascenders/descenders.
        double rectWidth = Math.max(text.length(), 1) * item.fontSize * 0.6 + item.fontSize * 0.4;
        double rectHeight = item.fontSize * 1.2;

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
          try {
            imgW = imageSizeOpt.first().evalf();
            imgH = imageSizeOpt.second().evalf();
          } catch (Exception e) {
          }
        } else if (imageSizeOpt.isNumber()) {
          try {
            imgW = imageSizeOpt.evalf();
            imgH = imgW;
          } catch (Exception e) {
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

      for (WordItem item : items) {
        RGBColor rgb = GraphicsOptions.PLOT_COLORS[colorIndex % GraphicsOptions.PLOT_COLORS.length];
        IAST colorAST = F.RGBColor(F.num(rgb.getRed() / 255.0), F.num(rgb.getGreen() / 255.0),
            F.num(rgb.getBlue() / 255.0));
        colorIndex++;

        // Translate proportional abstract font to Scaled representation required by SVG mapping
        double f = (item.fontSize * scaleY) / imgW;

        IAST style =
            F.Style(item.word, F.Rule(S.FontSize, F.Scaled(f)), F.Rule(S.FontColor, colorAST));

        IAST inset =
            F.Inset(style, F.List(F.num(item.finalX), F.num(item.finalY)), S.Center, S.Automatic);
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
   * Safely extracts a numeric weight from expressions, handling Number, Quantity, and Missing
   * cases.
   */
  private double getWeightValue(IExpr expr, EvalEngine engine) {
    if (expr.isNumber()) {
      return expr.evalf();
    }
    if (expr.isQuantity()) {
      IQuantity q = (IQuantity) expr;
      IExpr val = q.value();
      try {
        return val.evalf();
      } catch (ArgumentTypeException ate) {
      }
    }
    if (expr.isAST(S.Missing)) {
      return Double.NaN;
    }
    try {
      IExpr evaled = engine.evaluate(expr);
      if (evaled.isNumber()) {
        return evaled.evalf();
      }
      if (evaled.isQuantity()) {
        return ((IQuantity) evaled).value().evalf();
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
    setOptions(newSymbol, new IBuiltInSymbol[] {S.ImageSize, S.PlotRange},
        new IExpr[] {S.Automatic, S.Automatic});
  }
}
