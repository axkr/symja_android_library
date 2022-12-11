package org.matheclipse.image;

import org.matheclipse.core.expression.S;
import org.matheclipse.image.builtin.ImageFunctions;

public class ImageInit {
  public static void init() {
    ImageFunctions.initialize();

    S.ArrayPlot.setEvaluator(new org.matheclipse.image.builtin.ArrayPlot());
    S.ImageCrop.setEvaluator(new org.matheclipse.image.builtin.ImageCrop());
    S.ListDensityPlot.setEvaluator(new org.matheclipse.image.builtin.ListDensityPlot());
    // S.ListLogPlot.setEvaluator(new org.matheclipse.image.builtin.ListLogPlot());
    // S.ListLogLogPlot.setEvaluator(new org.matheclipse.image.builtin.ListLogLogPlot());

  }
}
