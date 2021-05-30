package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.basic.Config;

public final class FunctionDefinitions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      Beep.setEvaluator(new org.matheclipse.core.reflection.system.Beep());
      ComplexExpand.setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
      D.setEvaluator(new org.matheclipse.core.reflection.system.D());
      Derivative.setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
      DifferenceDelta.setEvaluator(new org.matheclipse.core.reflection.system.DifferenceDelta());
      DSolve.setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
      EasterSunday.setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
      ElementData.setEvaluator(new org.matheclipse.core.data.ElementData());
      Eliminate.setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
      ExportString.setEvaluator(new org.matheclipse.core.reflection.system.ExportString());
      ExpToTrig.setEvaluator(new org.matheclipse.core.reflection.system.ExpToTrig());
      FindInstance.setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
      FindRoot.setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
      Fourier.setEvaluator(new org.matheclipse.core.reflection.system.Fourier());
      FrobeniusSolve.setEvaluator(new org.matheclipse.core.reflection.system.FrobeniusSolve());
      FunctionExpand.setEvaluator(new org.matheclipse.core.reflection.system.FunctionExpand());
      HeavisideTheta.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideTheta());
      Horner.setEvaluator(new org.matheclipse.core.reflection.system.Horner());
      ImportString.setEvaluator(new org.matheclipse.core.reflection.system.ImportString());
      In.setEvaluator(new org.matheclipse.core.reflection.system.In());
      InterpolatingFunction.setEvaluator(
          new org.matheclipse.core.reflection.system.InterpolatingFunction());
      InterpolatingPolynomial.setEvaluator(
          new org.matheclipse.core.reflection.system.InterpolatingPolynomial());
      Interpolation.setEvaluator(new org.matheclipse.core.reflection.system.Interpolation());
      InverseFourier.setEvaluator(new org.matheclipse.core.reflection.system.InverseFourier());
      InverseFunction.setEvaluator(new org.matheclipse.core.reflection.system.InverseFunction());
      InverseLaplaceTransform.setEvaluator(
          new org.matheclipse.core.reflection.system.InverseLaplaceTransform());
      LaplaceTransform.setEvaluator(new org.matheclipse.core.reflection.system.LaplaceTransform());
      LinearProgramming.setEvaluator(
          new org.matheclipse.core.reflection.system.LinearProgramming());
      ListLinePlot.setEvaluator(new org.matheclipse.core.reflection.system.ListLinePlot());
      ListPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot());
      ListPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot3D());
      MatrixD.setEvaluator(new org.matheclipse.core.reflection.system.MatrixD());
      ND.setEvaluator(new org.matheclipse.core.reflection.system.ND());
      NDSolve.setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
      NFourierTransform.setEvaluator(
          new org.matheclipse.core.reflection.system.NFourierTransform());
      NIntegrate.setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
      NonCommutativeMultiply.setEvaluator(
          new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
      NSolve.setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
      Out.setEvaluator(new org.matheclipse.core.reflection.system.Out());
      Outer.setEvaluator(new org.matheclipse.core.reflection.system.Outer());
      ParametricPlot.setEvaluator(new org.matheclipse.core.reflection.system.ParametricPlot());
      Plot.setEvaluator(new org.matheclipse.core.reflection.system.Plot());
      Plot3D.setEvaluator(new org.matheclipse.core.reflection.system.Plot3D());
      PolarPlot.setEvaluator(new org.matheclipse.core.reflection.system.PolarPlot());
      Product.setEvaluator(new org.matheclipse.core.reflection.system.Product());
      Ramp.setEvaluator(new org.matheclipse.core.reflection.system.Ramp());
      Solve.setEvaluator(new org.matheclipse.core.reflection.system.Solve());
      Sum.setEvaluator(new org.matheclipse.core.reflection.system.Sum());
      Taylor.setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
      TrigExpand.setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
      TrigReduce.setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
      TrigToExp.setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());

      if (!Config.FUZZY_PARSER) {
        Export.setEvaluator(new org.matheclipse.core.reflection.system.Export());
        OptimizeExpression.setEvaluator(
            new org.matheclipse.core.reflection.system.OptimizeExpression());
        Share.setEvaluator(new org.matheclipse.core.reflection.system.Share());
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FunctionDefinitions() {}
}
