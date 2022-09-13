package org.matheclipse.core.bridge.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageFormat;
import org.matheclipse.image.bridge.fig.VisualImage;
import org.matheclipse.image.builtin.ArrayPlot;

class ArrayPlotTest {
  public static void main(String[] args) throws IOException {
    // data < 0.0 or > 1.0 should be set transparent
    // IAST raw = F.List(//
    // F.List(1, 0, 0, 0.3), //
    // F.List(1, 1, 3.3, 0.3), //
    // F.List(1, -2.1, 1, 0.7));

    // IAST raw = F.List(//
    // F.List(-1.0));
    IAST raw = F.List(//
        F.List(1, 0, 0, 0.3), //
        F.List(1, 1, 0, 0.3), //
        F.List(1, 0, 1, 0.7));
    // IAST raw = (IAST) S.RandomReal.of(EvalEngine.get(), F.C1, F.List(10, 20));
    // IAST raw = (IAST) S.RandomVariate.of(EvalEngine.get(),
    // F.UniformDistribution(F.List(F.C0, F.C1)), F.List(2, 70000));

    // testing scripting call:
    // ArrayPlot.arrayPlot(raw);

    BufferedImage bufferedImage =
        ImageFormat.toIntARGB(raw.mapLeaf(S.List, ColorDataGradients.GRAYSCALE));
    VisualImage visualImage = new VisualImage(bufferedImage);
    JFreeChart jFreeChart = ArrayPlot.arrayPlot(visualImage);
    ChartUtils.saveChartAsPNG(new File(HomeDirectory.Pictures(), "arrayplot001.png"), jFreeChart,
        1000, 300);
  }
}
