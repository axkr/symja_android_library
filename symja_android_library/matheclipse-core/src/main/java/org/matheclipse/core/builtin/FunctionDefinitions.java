package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.reflection.system.rules.AutomaticRules;
import org.matheclipse.core.reflection.system.steps.QuarticSolve;

public final class FunctionDefinitions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.QuarticSolve.setEvaluator(new QuarticSolve());

      // S.ArrayPlot.setEvaluator(new org.matheclipse.core.reflection.system.ArrayPlot());
      S.ArraySymbol.setEvaluator(new org.matheclipse.core.reflection.system.ArraySymbol());
      S.MatrixSymbol.setEvaluator(new org.matheclipse.core.reflection.system.MatrixSymbol());
      S.VectorSymbol.setEvaluator(new org.matheclipse.core.reflection.system.VectorSymbol());
      // S.BarChart.setEvaluator(new org.matheclipse.core.reflection.system.BarChart());
      S.BarnesG.setEvaluator(new org.matheclipse.core.reflection.system.BarnesG());
      // S.BoxWhiskerChart.setEvaluator(new
      // org.matheclipse.core.reflection.system.BoxWhiskerChart());
      S.LogBarnesG.setEvaluator(new org.matheclipse.core.reflection.system.LogBarnesG());
      S.Beep.setEvaluator(new org.matheclipse.core.reflection.system.Beep());
      S.BezierFunction.setEvaluator(new org.matheclipse.core.reflection.system.BezierFunction());
      S.ComplexExpand.setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
      // S.ComplexPlot.setEvaluator(new org.matheclipse.core.reflection.system.ComplexPlot());
      // S.ComplexPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ComplexPlot3D());
      // S.ContourPlot.setEvaluator(new org.matheclipse.core.reflection.system.ContourPlot());
      // S.ContourPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ContourPlot3D());
      S.D.setEvaluator(new org.matheclipse.core.reflection.system.D());
      // S.DensityHistogram
      // .setEvaluator(new org.matheclipse.core.reflection.system.DensityHistogram());
      // S.DensityPlot.setEvaluator(new org.matheclipse.core.reflection.system.DensityPlot());
      S.Derivative.setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
      S.DifferenceDelta.setEvaluator(new org.matheclipse.core.reflection.system.DifferenceDelta());
      S.DifferenceQuotient
          .setEvaluator(new org.matheclipse.core.reflection.system.DifferenceQuotient());
      S.DiscretePlot.setEvaluator(new org.matheclipse.core.reflection.system.DiscretePlot());
      S.DiscreteRatio.setEvaluator(new org.matheclipse.core.reflection.system.DiscreteRatio());
      S.DiscreteShift.setEvaluator(new org.matheclipse.core.reflection.system.DiscreteShift());
      S.DSolve.setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
      S.EasterSunday.setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
      S.ElementData.setEvaluator(new org.matheclipse.core.data.ElementData());
      S.Eliminate.setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
      S.ExportString.setEvaluator(new org.matheclipse.core.reflection.system.ExportString());
      S.ExpToTrig.setEvaluator(new org.matheclipse.core.reflection.system.ExpToTrig());
      S.FindFormula.setEvaluator(new org.matheclipse.core.reflection.system.FindFormula());
      S.FindGeneratingFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.FindGeneratingFunction());
      S.FindInstance.setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
      S.FindMaximum.setEvaluator(new org.matheclipse.core.reflection.system.FindMaximum());
      S.FindMinimum.setEvaluator(new org.matheclipse.core.reflection.system.FindMinimum());
      S.FindRoot.setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
      S.FindSequenceFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.FindSequenceFunction());
      S.FittedModel.setEvaluator(new org.matheclipse.core.reflection.system.FittedModel());
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
      S.Groupings.setEvaluator(new org.matheclipse.core.reflection.system.Groupings());
      S.HeavisideLambda.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideLambda());
      S.HeavisidePi.setEvaluator(new org.matheclipse.core.reflection.system.HeavisidePi());
      S.HeavisideTheta.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideTheta());
      S.HermiteDecomposition
          .setEvaluator(new org.matheclipse.core.reflection.system.HermiteDecomposition());
      // S.Histogram.setEvaluator(new org.matheclipse.core.reflection.system.Histogram());
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
      // S.ListContourPlot.setEvaluator(new
      // org.matheclipse.core.reflection.system.ListContourPlot());
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
      S.ListStepPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListStepPlot());
      S.LogPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogPlot());
      S.LogLinearPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogLinearPlot());
      S.LogLogPlot.setEvaluator(new org.matheclipse.core.reflection.system.LogLogPlot());

      // S.MatrixPlot.setEvaluator(new org.matheclipse.core.reflection.system.MatrixPlot());

      S.MeijerG.setEvaluator(new org.matheclipse.core.reflection.system.MeijerG());

      S.N.setEvaluator(new org.matheclipse.core.reflection.system.N());
      S.ND.setEvaluator(new org.matheclipse.core.reflection.system.ND());
      S.NDSolve.setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
      S.NFourierTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.NFourierTransform());
      S.NIntegrate.setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
      S.NonCommutativeMultiply
          .setEvaluator(new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
      S.Normal.setEvaluator(new org.matheclipse.core.reflection.system.Normal());
      S.NSolve.setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
      S.NSum.setEvaluator(new org.matheclipse.core.reflection.system.NSum());
      S.NumberLinePlot.setEvaluator(new org.matheclipse.core.reflection.system.NumberLinePlot());

      S.OptimizeExpression
          .setEvaluator(new org.matheclipse.core.reflection.system.OptimizeExpression());
      S.Out.setEvaluator(new org.matheclipse.core.reflection.system.Out());
      S.Outer.setEvaluator(new org.matheclipse.core.reflection.system.Outer());
      S.Partition.setEvaluator(new org.matheclipse.core.reflection.system.Partition());
      S.ParametricPlot.setEvaluator(new org.matheclipse.core.reflection.system.ParametricPlot());
      // S.ParametricPlot3D
      // .setEvaluator(new org.matheclipse.core.reflection.system.ParametricPlot3D());
      S.PeriodogramArray
          .setEvaluator(new org.matheclipse.core.reflection.system.PeriodogramArray());
      // S.PieChart.setEvaluator(new org.matheclipse.core.reflection.system.PieChart());
      S.Plot.setEvaluator(new org.matheclipse.core.reflection.system.Plot());
      S.Plot3D.setEvaluator(new org.matheclipse.core.reflection.system.Plot3D());
      S.PolarPlot.setEvaluator(new org.matheclipse.core.reflection.system.PolarPlot());
      S.PolynomialReduce
          .setEvaluator(new org.matheclipse.core.reflection.system.PolynomialReduce());
      S.PowerRange.setEvaluator(new org.matheclipse.core.reflection.system.PowerRange());
      S.Product.setEvaluator(new org.matheclipse.core.reflection.system.Product());
      S.Ratios.setEvaluator(new org.matheclipse.core.reflection.system.Ratios());
      S.Reduce.setEvaluator(new org.matheclipse.core.reflection.system.Reduce());
      // S.RevolutionPlot3D
      // .setEvaluator(new org.matheclipse.core.reflection.system.RevolutionPlot3D());
      S.Solve.setEvaluator(new org.matheclipse.core.reflection.system.Solve());
      S.SpectrogramArray
          .setEvaluator(new org.matheclipse.core.reflection.system.SpectrogramArray());
      // S.SphericalPlot3D.setEvaluator(new
      // org.matheclipse.core.reflection.system.SphericalPlot3D());
      S.SudokuSolve.setEvaluator(new org.matheclipse.core.reflection.system.SudokuSolve());
      S.Sum.setEvaluator(new org.matheclipse.core.reflection.system.Sum());
      S.SymbolicIdentityArray
          .setEvaluator(new org.matheclipse.core.reflection.system.SymbolicIdentityArray());
      S.SymmetricPolynomial
          .setEvaluator(new org.matheclipse.core.reflection.system.SymmetricPolynomial());
      S.SymmetricReduction
          .setEvaluator(new org.matheclipse.core.reflection.system.SymmetricReduction());
      S.Taylor.setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
      S.TrigExpand.setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
      S.TrigFactor.setEvaluator(new org.matheclipse.core.reflection.system.TrigFactor());
      S.TrigReduce.setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
      S.TrigSimplifyFu.setEvaluator(new org.matheclipse.core.reflection.system.TrigSimplifyFu());
      S.TrigToExp.setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());
      S.ZTransform.setEvaluator(new org.matheclipse.core.reflection.system.ZTransform());

      S.Hash.setEvaluator(new org.matheclipse.core.reflection.system.Hash());
      S.FileHash.setEvaluator(new org.matheclipse.core.reflection.system.FileHash());
      // S.MrvLimit.setEvaluator(new org.matheclipse.core.reflection.system.MrvLimit());
      S.TensorContract.setEvaluator(new org.matheclipse.core.reflection.system.TensorContract());
      S.TensorProduct.setEvaluator(new org.matheclipse.core.reflection.system.TensorProduct());
      S.TensorTranspose.setEvaluator(new org.matheclipse.core.reflection.system.TensorTranspose());

      if (!Config.FUZZY_PARSER) {
        S.Export.setEvaluator(new org.matheclipse.core.reflection.system.Export());
        S.Share.setEvaluator(new org.matheclipse.core.reflection.system.Share());
      }
    }
  }

  private FunctionDefinitions() {}

  public static boolean builtinFunctionInitializer() {

    Arithmetic.initialize();
    PredicateQ.initialize();
    AttributeFunctions.initialize();

    ConstantDefinitions.initialize();
    Initializer.init();
    S.Integrate.setEvaluator(org.matheclipse.core.reflection.system.Integrate.CONST);
    IOFunctions.initialize();
    Programming.initialize();
    PatternMatching.initialize();
    FileFunctions.initialize();
    Algebra.initialize();
    SimplifyFunctions.initialize();
    StructureFunctions.initialize();
    ExpTrigsFunctions.initialize();
    NumberTheory.initialize();
    BooleanFunctions.initialize();
    LinearAlgebra.initialize();
    TensorFunctions.initialize();
    ListFunctions.initialize();
    SubsetFunctions.initialize();
    SequenceFunctions.initialize();
    Combinatoric.initialize();
    IntegerFunctions.initialize();
    BesselFunctions.initialize();
    SpecialFunctions.initialize();
    StringFunctions.initialize();
    OutputFunctions.initialize();
    RandomFunctions.initialize();
    StatisticsFunctions.initialize();
    StatisticsContinousDistribution.initialize();
    StatisticsDiscreteDistributions.initialize();
    StatisticalMomentFunctions.initialize();
    HypergeometricFunctions.initialize();
    EllipticIntegrals.initialize();
    PolynomialFunctions.initialize();
    RootsFunctions.initialize();
    SeriesFunctions.initialize();
    AssumptionFunctions.initialize();
    ContainsFunctions.initialize();
    CurveFitterFunctions.initialize();
    VectorAnalysisFunctions.initialize();
    QuantityFunctions.initialize();
    IntervalFunctions.initialize();
    FinancialFunctions.initialize();
    WXFFunctions.initialize();
    WindowFunctions.initialize();
    MinMaxFunctions.initialize();
    GraphFunctions.initialize();
    GraphDataFunctions.initialize();
    AssociationFunctions.initialize();
    GeodesyFunctions.initialize();
    ManipulateFunction.initialize();
    FilterFunctions.initialize();
    EntityFunctions.initialize();
    ClusteringFunctions.initialize();
    SourceCodeFunctions.initialize();
    SparseArrayFunctions.initialize();
    UnitTestingFunctions.initialize();
    BoxesFunctions.initialize();
    NumericArrayFunctions.initialize();
    GraphicsFunctions.initialize();
    CompilerFunctions.initialize();
    JavaFunctions.initialize();
    SidesFunctions.initialize();
    ComputationalGeometryFunctions.initialize();
    PiecewiseFunctions.initialize();
    QuantumPhysicsFunctions.initialize();
    ConstantPhysicsDefinitions.initialize();

    AutomaticRules.initialize();
    return true;
  }
}
