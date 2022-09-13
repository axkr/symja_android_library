// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import org.hipparchus.stat.StatUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.lang.Unicode;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.expression.data.ImageExpr;

public class Histogram extends AbstractEvaluator {

  public Histogram() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    VisualSet visualSet = null;
    IExpr arg1 = ast.arg1();
    if (arg1.isListOfLists()) {
      IAST list = (IAST) arg1;
      visualSet = new VisualSet();
      for (int i = 1; i < list.size(); i++) {
        histogram(visualSet, (IAST) list.get(i));
      }
    } else if (arg1.isList()) {
      IAST list = (IAST) arg1;
      visualSet = new VisualSet();
      histogram(visualSet, list);
    }
    if (visualSet != null) {
      JFreeChart jFreeChart = histogram(visualSet, false);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      BufferedImage buffer = jFreeChart.createBufferedImage(600, 480);
      return new ImageExpr(buffer, null);
    }
    return F.NIL;
  }

  private static void histogram(VisualSet visualSet, IAST pointList) {
    double[] dData = pointList.toDoubleVectorIgnore();
    if (dData == null) {
      return;
    }
    double min = StatUtils.min(dData);
    double max = StatUtils.max(dData);
    double defaultRange = (max - min) / (0.5);
    int nRanges = (int) Math.ceil(defaultRange);
    // if (nRanges < 10) {
    // nRanges = 10;
    // }
    if (nRanges > 100) {
      nRanges = 100;
    }
    defaultRange = (max - min) / (nRanges);
    int[] buckets = calcHistogram(dData, min, max, nRanges);

    IASTAppendable points = F.ListAlloc(buckets.length);
    for (int i = 0; i < buckets.length; i++) {
      points.append(F.List(F.ZZ(i), F.ZZ(buckets[i])));
    }
    visualSet.add(points);
  }
  // private static void histogram(VisualSet visualSet, IAST arg1) {
  // IASTAppendable points = F.ListAlloc(arg1.argSize());
  // for (int i = 1; i < arg1.size(); i++) {
  // points.append(F.List(F.ZZ(i), arg1.get(i)));
  // }
  // visualSet.add(points);
  // }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  /**
   * @param visualSet
   * @return
   */
  public static JFreeChart histogram(VisualSet visualSet) {
    return histogram(visualSet, false);
  }

  /**
   * @param visualSet
   * @param stacked
   * @return
   * @see StackedHistogram
   */
  /* package */ static JFreeChart histogram(VisualSet visualSet, boolean stacked) {
    return JFreeChartFactory.barChart(visualSet, stacked, Unicode::valueOf);
  }

  /**
   * @param visualSet
   * @param stacked
   * @param naming for coordinates on x-axis
   * @return
   */
  public static JFreeChart histogram(VisualSet visualSet, boolean stacked,
      Function<IExpr, String> naming) {
    return JFreeChartFactory.barChart(visualSet, stacked, naming);
  }



  private static int[] calcHistogram(double[] data, double min, double max, int numBins) {
    final int[] result = new int[numBins];
    final double binSize = (max - min) / numBins;

    for (double d : data) {
      int bin = (int) ((d - min) / binSize);
      if ((bin < 0) || (bin >= numBins)) {
        /* this data is smaller than min */
      } else {
        result[bin] += 1;
      }
    }
    return result;
  }
}
