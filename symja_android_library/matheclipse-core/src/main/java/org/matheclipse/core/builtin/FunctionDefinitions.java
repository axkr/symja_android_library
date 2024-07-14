package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.reflection.system.steps.QuarticSolve;

public final class FunctionDefinitions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (ToggleFeature.SHOW_STEPS) {
        S.QuarticSolve.setEvaluator(new QuarticSolve());
      }
      S.Beep.setEvaluator(new org.matheclipse.core.reflection.system.Beep());
      S.BezierFunction.setEvaluator(new org.matheclipse.core.reflection.system.BezierFunction());
      S.ComplexExpand.setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
      S.D.setEvaluator(new org.matheclipse.core.reflection.system.D());
      S.Derivative.setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
      S.DifferenceDelta.setEvaluator(new org.matheclipse.core.reflection.system.DifferenceDelta());
      S.DiscretePlot.setEvaluator(new org.matheclipse.core.reflection.system.DiscretePlot());
      S.DSolve.setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
      S.EasterSunday.setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
      S.ElementData.setEvaluator(new org.matheclipse.core.data.ElementData());
      S.Eliminate.setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
      S.ExportString.setEvaluator(new org.matheclipse.core.reflection.system.ExportString());
      S.ExpToTrig.setEvaluator(new org.matheclipse.core.reflection.system.ExpToTrig());
      S.FindFormula.setEvaluator(new org.matheclipse.core.reflection.system.FindFormula());
      S.FindInstance.setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
      S.FindMaximum.setEvaluator(new org.matheclipse.core.reflection.system.FindMaximum());
      S.FindMinimum.setEvaluator(new org.matheclipse.core.reflection.system.FindMinimum());
      S.FindRoot.setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
      S.Fourier.setEvaluator(new org.matheclipse.core.reflection.system.Fourier());
      S.FourierCosTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.FourierCosTransform());
      S.FourierDCTMatrix
          .setEvaluator(new org.matheclipse.core.reflection.system.FourierDCTMatrix());
      S.FourierDSTMatrix
          .setEvaluator(new org.matheclipse.core.reflection.system.FourierDSTMatrix());
      S.FourierSinTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.FourierSinTransform());

      S.FrobeniusSolve.setEvaluator(new org.matheclipse.core.reflection.system.FrobeniusSolve());
      S.FunctionExpand.setEvaluator(new org.matheclipse.core.reflection.system.FunctionExpand());
      S.HeavisideTheta.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideTheta());
      S.Horner.setEvaluator(new org.matheclipse.core.reflection.system.Horner());
      S.ImportString.setEvaluator(new org.matheclipse.core.reflection.system.ImportString());
      S.In.setEvaluator(new org.matheclipse.core.reflection.system.In());
      S.InterpolatingFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingFunction());
      S.InterpolatingPolynomial
          .setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingPolynomial());
      S.Interpolation.setEvaluator(new org.matheclipse.core.reflection.system.Interpolation());
      S.InverseFourier.setEvaluator(new org.matheclipse.core.reflection.system.InverseFourier());
      S.InverseFunction.setEvaluator(new org.matheclipse.core.reflection.system.InverseFunction());
      S.InverseLaplaceTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.InverseLaplaceTransform());
      S.InverseZTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.InverseZTransform());
      S.LaplaceTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.LaplaceTransform());
      S.LinearOptimization
          .setEvaluator(new org.matheclipse.core.reflection.system.LinearOptimization());
      S.LinearProgramming
          .setEvaluator(new org.matheclipse.core.reflection.system.LinearProgramming());
      S.ListLinePlot.setEvaluator(new org.matheclipse.core.reflection.system.ListLinePlot());
      S.ListLinePlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListLinePlot3D());
      S.ListPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot());
      S.ListLogPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListLogPlot());
      S.ListLogLogPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListLogLogPlot());
      S.ListLogLinearPlot
          .setEvaluator(new org.matheclipse.core.reflection.system.ListLogLinearPlot());
      S.ListPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot3D());
      S.ListPointPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListPointPlot3D());
      S.ListPolarPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListPolarPlot());
      S.LogPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogPlot());
      S.LogLinearPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogLinearPlot());
      S.LogLogPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogLogPlot());

      S.MatrixD.setEvaluator(new org.matheclipse.core.reflection.system.MatrixD());
      S.MeijerG.setEvaluator(new org.matheclipse.core.reflection.system.MeijerG());

      S.ND.setEvaluator(new org.matheclipse.core.reflection.system.ND());
      S.NDSolve.setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
      S.NFourierTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.NFourierTransform());
      S.NIntegrate.setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
      S.NonCommutativeMultiply
          .setEvaluator(new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
      S.NSolve.setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
      S.NumberLinePlot.setEvaluator(new org.matheclipse.core.reflection.system.NumberLinePlot());

      S.Out.setEvaluator(new org.matheclipse.core.reflection.system.Out());
      S.Outer.setEvaluator(new org.matheclipse.core.reflection.system.Outer());
      S.ParametricPlot.setEvaluator(new org.matheclipse.core.reflection.system.ParametricPlot());
      S.Plot.setEvaluator(new org.matheclipse.core.reflection.system.Plot());
      S.Plot3D.setEvaluator(new org.matheclipse.core.reflection.system.Plot3D());
      S.PolarPlot.setEvaluator(new org.matheclipse.core.reflection.system.PolarPlot());
      S.Product.setEvaluator(new org.matheclipse.core.reflection.system.Product());
      S.Reduce.setEvaluator(new org.matheclipse.core.reflection.system.Reduce());
      S.Solve.setEvaluator(new org.matheclipse.core.reflection.system.Solve());
      S.SudokuSolve.setEvaluator(new org.matheclipse.core.reflection.system.SudokuSolve());
      S.Sum.setEvaluator(new org.matheclipse.core.reflection.system.Sum());
      S.Taylor.setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
      S.TrigExpand.setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
      S.TrigReduce.setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
      S.TrigSimplifyFu.setEvaluator(new org.matheclipse.core.reflection.system.TrigSimplifyFu());
      S.TrigToExp.setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());
      S.ZTransform.setEvaluator(new org.matheclipse.core.reflection.system.ZTransform());

      if (!Config.FUZZY_PARSER) {
        S.Export.setEvaluator(new org.matheclipse.core.reflection.system.Export());
        S.OptimizeExpression
            .setEvaluator(new org.matheclipse.core.reflection.system.OptimizeExpression());
        S.Share.setEvaluator(new org.matheclipse.core.reflection.system.Share());
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private FunctionDefinitions() {}
}
