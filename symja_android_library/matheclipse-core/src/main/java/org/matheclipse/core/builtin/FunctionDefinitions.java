package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.S.Beep;
import static org.matheclipse.core.expression.S.ComplexExpand;
import static org.matheclipse.core.expression.S.D;
import static org.matheclipse.core.expression.S.DSolve;
import static org.matheclipse.core.expression.S.Derivative;
import static org.matheclipse.core.expression.S.DifferenceDelta;
import static org.matheclipse.core.expression.S.EasterSunday;
import static org.matheclipse.core.expression.S.ElementData;
import static org.matheclipse.core.expression.S.Eliminate;
import static org.matheclipse.core.expression.S.ExpToTrig;
import static org.matheclipse.core.expression.S.Export;
import static org.matheclipse.core.expression.S.ExportString;
import static org.matheclipse.core.expression.S.FindInstance;
import static org.matheclipse.core.expression.S.FindRoot;
import static org.matheclipse.core.expression.S.Fourier;
import static org.matheclipse.core.expression.S.FrobeniusSolve;
import static org.matheclipse.core.expression.S.FunctionExpand;
import static org.matheclipse.core.expression.S.HeavisideTheta;
import static org.matheclipse.core.expression.S.Horner;
import static org.matheclipse.core.expression.S.ImportString;
import static org.matheclipse.core.expression.S.In;
import static org.matheclipse.core.expression.S.InterpolatingFunction;
import static org.matheclipse.core.expression.S.InterpolatingPolynomial;
import static org.matheclipse.core.expression.S.Interpolation;
import static org.matheclipse.core.expression.S.InverseFourier;
import static org.matheclipse.core.expression.S.InverseFunction;
import static org.matheclipse.core.expression.S.InverseLaplaceTransform;
import static org.matheclipse.core.expression.S.LaplaceTransform;
import static org.matheclipse.core.expression.S.LinearProgramming;
import static org.matheclipse.core.expression.S.ListLinePlot;
import static org.matheclipse.core.expression.S.ListPlot;
import static org.matheclipse.core.expression.S.ListPlot3D;
import static org.matheclipse.core.expression.S.ListPointPlot3D;
import static org.matheclipse.core.expression.S.MatrixD;
import static org.matheclipse.core.expression.S.ND;
import static org.matheclipse.core.expression.S.NDSolve;
import static org.matheclipse.core.expression.S.NFourierTransform;
import static org.matheclipse.core.expression.S.NIntegrate;
import static org.matheclipse.core.expression.S.NSolve;
import static org.matheclipse.core.expression.S.NonCommutativeMultiply;
import static org.matheclipse.core.expression.S.OptimizeExpression;
import static org.matheclipse.core.expression.S.Out;
import static org.matheclipse.core.expression.S.Outer;
import static org.matheclipse.core.expression.S.ParametricPlot;
import static org.matheclipse.core.expression.S.Plot;
import static org.matheclipse.core.expression.S.Plot3D;
import static org.matheclipse.core.expression.S.PolarPlot;
import static org.matheclipse.core.expression.S.Product;
import static org.matheclipse.core.expression.S.Ramp;
import static org.matheclipse.core.expression.S.Reduce;
import static org.matheclipse.core.expression.S.Share;
import static org.matheclipse.core.expression.S.Solve;
import static org.matheclipse.core.expression.S.Sum;
import static org.matheclipse.core.expression.S.Taylor;
import static org.matheclipse.core.expression.S.TrigExpand;
import static org.matheclipse.core.expression.S.TrigReduce;
import static org.matheclipse.core.expression.S.TrigToExp;
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
      ListPointPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListPointPlot3D());
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
      Reduce.setEvaluator(new org.matheclipse.core.reflection.system.Reduce());
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
