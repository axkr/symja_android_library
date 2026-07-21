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

      S.Activate.setEvaluator(new org.matheclipse.core.reflection.system.Activate());
      S.AcyclicGraphQ.setEvaluator(new org.matheclipse.core.reflection.system.AcyclicGraphQ());
      S.AdjacencyGraph.setEvaluator(new org.matheclipse.core.reflection.system.AdjacencyGraph());
      S.AdjacencyList.setEvaluator(new org.matheclipse.core.reflection.system.AdjacencyList());
      S.ArrayPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ArrayPlot());
      S.ArcLength.setEvaluator(new org.matheclipse.core.reflection.system.ArcLength());
      S.Area.setEvaluator(new org.matheclipse.core.reflection.system.Area());
      S.ArrayDot.setEvaluator(new org.matheclipse.core.reflection.system.ArrayDot());
      S.AlgebraicIntegerQ
          .setEvaluator(new org.matheclipse.core.reflection.system.AlgebraicIntegerQ());
      S.ArraySymbol.setEvaluator(new org.matheclipse.core.reflection.system.ArraySymbol());
      S.Asymptotic.setEvaluator(new org.matheclipse.core.reflection.system.Asymptotic());
      S.AsymptoticDSolveValue
          .setEvaluator(new org.matheclipse.core.reflection.system.AsymptoticDSolveValue());
      S.AsymptoticIntegrate
          .setEvaluator(new org.matheclipse.core.reflection.system.AsymptoticIntegrate());
      S.AsymptoticRSolveValue
          .setEvaluator(new org.matheclipse.core.reflection.system.AsymptoticRSolveValue());
      S.AsymptoticSolve.setEvaluator(new org.matheclipse.core.reflection.system.AsymptoticSolve());
      S.BarnesG.setEvaluator(new org.matheclipse.core.reflection.system.BarnesG());
      S.DawsonF.setEvaluator(new org.matheclipse.core.reflection.system.DawsonF());
      S.MatrixSymbol.setEvaluator(new org.matheclipse.core.reflection.system.MatrixSymbol());
      S.VectorSymbol.setEvaluator(new org.matheclipse.core.reflection.system.VectorSymbol());
      S.VertexAdd.setEvaluator(new org.matheclipse.core.reflection.system.VertexAdd());
      S.VertexContract.setEvaluator(new org.matheclipse.core.reflection.system.VertexContract());
      S.VertexDegree.setEvaluator(new org.matheclipse.core.reflection.system.VertexDegree());
      S.VertexDelete.setEvaluator(new org.matheclipse.core.reflection.system.VertexDelete());
      S.VertexInDegree.setEvaluator(new org.matheclipse.core.reflection.system.VertexInDegree());
      S.VertexOutDegree.setEvaluator(new org.matheclipse.core.reflection.system.VertexOutDegree());
      S.Volume.setEvaluator(new org.matheclipse.core.reflection.system.Volume());
      S.BarChart.setEvaluator(new org.matheclipse.core.builtin.graphics.BarChart());
      S.BoxWhiskerChart.setEvaluator(new org.matheclipse.core.builtin.graphics.BoxWhiskerChart());
      S.Beep.setEvaluator(new org.matheclipse.core.reflection.system.Beep());
      S.BellY.setEvaluator(new org.matheclipse.core.reflection.system.BellY());
      S.BezierFunction.setEvaluator(new org.matheclipse.core.reflection.system.BezierFunction());
      S.BinLists.setEvaluator(new org.matheclipse.core.reflection.system.BinLists());
      S.BoundingRegion.setEvaluator(new org.matheclipse.core.reflection.system.BoundingRegion());
      S.BoxMatrix.setEvaluator(new org.matheclipse.core.reflection.system.BoxMatrix());
      S.CellularAutomaton
          .setEvaluator(new org.matheclipse.core.reflection.system.CellularAutomaton());
      S.CirclePoints.setEvaluator(new org.matheclipse.core.reflection.system.CirclePoints());
      S.CentralFeature.setEvaluator(new org.matheclipse.core.reflection.system.CentralFeature());
      S.ChromaticNumber.setEvaluator(new org.matheclipse.core.reflection.system.ChromaticNumber());
      S.ChromaticPolynomial
          .setEvaluator(new org.matheclipse.core.reflection.system.ChromaticPolynomial());
      S.ColorData.setEvaluator(new org.matheclipse.core.reflection.system.ColorData());
      S.ColorDataFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.ColorDataFunction());
      S.CompleteGraphQ.setEvaluator(new org.matheclipse.core.reflection.system.CompleteGraphQ());
      S.CompleteKaryTree
          .setEvaluator(new org.matheclipse.core.reflection.system.CompleteKaryTree());
      S.ComplexExpand.setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
      S.ConnectedGraphComponents
          .setEvaluator(new org.matheclipse.core.reflection.system.ConnectedGraphComponents());
      S.CountsBy.setEvaluator(new org.matheclipse.core.reflection.system.CountsBy());
      S.CrossMatrix.setEvaluator(new org.matheclipse.core.reflection.system.CrossMatrix());

      S.D.setEvaluator(new org.matheclipse.core.reflection.system.D());
      S.Dt.setEvaluator(new org.matheclipse.core.reflection.system.Dt());
      S.DeBruijnSequence
          .setEvaluator(new org.matheclipse.core.reflection.system.DeBruijnSequence());
      S.Decompose.setEvaluator(new org.matheclipse.core.reflection.system.Decompose());
      S.Derivative.setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
      S.DiamondMatrix.setEvaluator(new org.matheclipse.core.reflection.system.DiamondMatrix());
      S.DifferenceDelta.setEvaluator(new org.matheclipse.core.reflection.system.DifferenceDelta());
      S.DifferenceQuotient
          .setEvaluator(new org.matheclipse.core.reflection.system.DifferenceQuotient());
      S.DifferenceRoot.setEvaluator(new org.matheclipse.core.reflection.system.DifferenceRoot());
      S.DigitSum.setEvaluator(new org.matheclipse.core.reflection.system.DigitSum());
      S.DirectedGraphQ.setEvaluator(new org.matheclipse.core.reflection.system.DirectedGraphQ());
      S.DiscretePlot.setEvaluator(new org.matheclipse.core.builtin.graphics.DiscretePlot());
      S.DiscreteRatio.setEvaluator(new org.matheclipse.core.reflection.system.DiscreteRatio());
      S.DiscreteShift.setEvaluator(new org.matheclipse.core.reflection.system.DiscreteShift());
      S.DiskMatrix.setEvaluator(new org.matheclipse.core.reflection.system.DiskMatrix());
      S.Divisors.setEvaluator(new org.matheclipse.core.reflection.system.Divisors());
      S.DSolve.setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
      S.DSolveValue.setEvaluator(new org.matheclipse.core.reflection.system.DSolveValue());
      S.EasterSunday.setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
      S.EdgeAdd.setEvaluator(new org.matheclipse.core.reflection.system.EdgeAdd());
      S.EdgeContract.setEvaluator(new org.matheclipse.core.reflection.system.EdgeContract());
      S.EdgeDelete.setEvaluator(new org.matheclipse.core.reflection.system.EdgeDelete());
      S.ElementData.setEvaluator(new org.matheclipse.core.data.ElementData());
      S.Eliminate.setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
      S.EntityList.setEvaluator(new org.matheclipse.core.reflection.system.EntityList());
      S.ExponentialGeneratingFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.ExponentialGeneratingFunction());
      S.ExportString.setEvaluator(new org.matheclipse.core.reflection.system.ExportString());
      S.ExpToTrig.setEvaluator(new org.matheclipse.core.reflection.system.ExpToTrig());
      S.Extract.setEvaluator(new org.matheclipse.core.reflection.system.Extract());
      S.FactorList.setEvaluator(new org.matheclipse.core.reflection.system.FactorList());
      S.FindFormula.setEvaluator(new org.matheclipse.core.reflection.system.FindFormula());
      S.FindGeneratingFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.FindGeneratingFunction());
      S.FindInstance.setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
      S.FindMaximum.setEvaluator(new org.matheclipse.core.reflection.system.FindMaximum());
      S.FindMinimum.setEvaluator(new org.matheclipse.core.reflection.system.FindMinimum());
      S.FindRoot.setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
      S.FindSequenceFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.FindSequenceFunction());
      S.FindShortestCurve
          .setEvaluator(new org.matheclipse.core.reflection.system.FindShortestCurve());
      S.FindVertexColoring
          .setEvaluator(new org.matheclipse.core.reflection.system.FindVertexColoring());
      S.FiniteGroupCount
          .setEvaluator(new org.matheclipse.core.reflection.system.FiniteGroupCount());
      S.FiniteAbelianGroupCount
          .setEvaluator(new org.matheclipse.core.reflection.system.FiniteAbelianGroupCount());
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
      S.FunctionRange.setEvaluator(new org.matheclipse.core.reflection.system.FunctionRange());
      S.GeneratingFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.GeneratingFunction());
      S.GlobalClusteringCoefficient
          .setEvaluator(new org.matheclipse.core.reflection.system.GlobalClusteringCoefficient());
      S.GraphDistance.setEvaluator(new org.matheclipse.core.reflection.system.GraphDistance());
      S.Groupings.setEvaluator(new org.matheclipse.core.reflection.system.Groupings());
      S.HeavisideLambda.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideLambda());
      S.HeavisidePi.setEvaluator(new org.matheclipse.core.reflection.system.HeavisidePi());
      S.HeavisideTheta.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideTheta());
      S.HermiteDecomposition
          .setEvaluator(new org.matheclipse.core.reflection.system.HermiteDecomposition());
      S.Horner.setEvaluator(new org.matheclipse.core.reflection.system.Horner());
      S.HurwitzZeta.setEvaluator(new org.matheclipse.core.reflection.system.HurwitzZeta());

      S.ImportString.setEvaluator(new org.matheclipse.core.reflection.system.ImportString());
      S.In.setEvaluator(new org.matheclipse.core.reflection.system.In());
      S.Inactivate.setEvaluator(new org.matheclipse.core.reflection.system.Inactivate());
      S.IncidenceMatrix.setEvaluator(new org.matheclipse.core.reflection.system.IncidenceMatrix());
      S.IntegerPartitions
          .setEvaluator(new org.matheclipse.core.reflection.system.IntegerPartitions());
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
      S.IrreduciblePolynomialQ
          .setEvaluator(new org.matheclipse.core.reflection.system.IrreduciblePolynomialQ());

      S.JacobiEpsilon.setEvaluator(new org.matheclipse.core.reflection.system.JacobiEpsilon());
      S.JordanDecomposition
          .setEvaluator(new org.matheclipse.core.reflection.system.JordanDecomposition());

      S.KaryTree.setEvaluator(new org.matheclipse.core.reflection.system.KaryTree());
      S.KeyFreeQ.setEvaluator(new org.matheclipse.core.reflection.system.KeyFreeQ());
      S.KeyMap.setEvaluator(new org.matheclipse.core.reflection.system.KeyMap());
      S.KeyMemberQ.setEvaluator(new org.matheclipse.core.reflection.system.KeyMemberQ());
      S.KeySortBy.setEvaluator(new org.matheclipse.core.reflection.system.KeySortBy());
      S.KeyUnion.setEvaluator(new org.matheclipse.core.reflection.system.KeyUnion());
      S.KirchhoffMatrix.setEvaluator(new org.matheclipse.core.reflection.system.KirchhoffMatrix());

      S.LaplaceTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.LaplaceTransform());
      S.Limit.setEvaluator(new org.matheclipse.core.reflection.system.Limit());
      S.LinearOptimization
          .setEvaluator(new org.matheclipse.core.reflection.system.LinearOptimization());
      S.LinearProgramming
          .setEvaluator(new org.matheclipse.core.reflection.system.LinearProgramming());
      S.LocalClusteringCoefficient
          .setEvaluator(new org.matheclipse.core.reflection.system.LocalClusteringCoefficient());
      S.LogBarnesG.setEvaluator(new org.matheclipse.core.reflection.system.LogBarnesG());
      S.LogicalExpand.setEvaluator(new org.matheclipse.core.reflection.system.LogicalExpand());

      S.MarginalDistribution
          .setEvaluator(new org.matheclipse.core.reflection.system.MarginalDistribution());
      S.MatrixExp.setEvaluator(new org.matheclipse.core.reflection.system.MatrixExp());
      S.MatrixFunction.setEvaluator(new org.matheclipse.core.reflection.system.MatrixFunction());
      S.MatrixLog.setEvaluator(new org.matheclipse.core.reflection.system.MatrixLog());
      S.MaximalBy.setEvaluator(new org.matheclipse.core.reflection.system.MaximalBy());
      S.Maximize.setEvaluator(new org.matheclipse.core.reflection.system.Maximize());
      S.MeanClusteringCoefficient
          .setEvaluator(new org.matheclipse.core.reflection.system.MeanClusteringCoefficient());
      S.Minimize.setEvaluator(new org.matheclipse.core.reflection.system.Minimize());
      S.Molecule.setEvaluator(new org.matheclipse.core.reflection.system.Molecule());
      S.MoleculeValue.setEvaluator(new org.matheclipse.core.reflection.system.MoleculeValue());
      S.MovingAverage.setEvaluator(new org.matheclipse.core.reflection.system.MovingAverage());
      S.MovingMedian.setEvaluator(new org.matheclipse.core.reflection.system.MovingMedian());
      S.MultivariateTDistribution
          .setEvaluator(new org.matheclipse.core.reflection.system.MultivariateTDistribution());

      S.MeijerG.setEvaluator(new org.matheclipse.core.reflection.system.MeijerG());
      S.MeijerGReduce.setEvaluator(new org.matheclipse.core.reflection.system.MeijerGReduce());
      S.MinimalBy.setEvaluator(new org.matheclipse.core.reflection.system.MinimalBy());
      S.MinimalPolynomial
          .setEvaluator(new org.matheclipse.core.reflection.system.MinimalPolynomial());
      S.N.setEvaluator(new org.matheclipse.core.reflection.system.N());
      S.ND.setEvaluator(new org.matheclipse.core.reflection.system.ND());
      S.NeighborhoodGraph
          .setEvaluator(new org.matheclipse.core.reflection.system.NeighborhoodGraph());
      S.NDSolve.setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
      S.NFourierTransform
          .setEvaluator(new org.matheclipse.core.reflection.system.NFourierTransform());
      S.NIntegrate.setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
      S.NonCommutativeMultiply
          .setEvaluator(new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
      S.Normal.setEvaluator(new org.matheclipse.core.reflection.system.Normal());
      S.NSolve.setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
      S.NSum.setEvaluator(new org.matheclipse.core.reflection.system.NSum());
      S.NumberLinePlot.setEvaluator(new org.matheclipse.core.builtin.graphics.NumberLinePlot());

      S.OptimizeExpression
          .setEvaluator(new org.matheclipse.core.reflection.system.OptimizeExpression());
      S.Out.setEvaluator(new org.matheclipse.core.reflection.system.Out());
      S.Outer.setEvaluator(new org.matheclipse.core.reflection.system.Outer());

      S.Part.setEvaluator(new org.matheclipse.core.reflection.system.Part());
      S.Partition.setEvaluator(new org.matheclipse.core.reflection.system.Partition());
      S.Perimeter.setEvaluator(new org.matheclipse.core.reflection.system.Perimeter());
      S.PeriodicTablePlot
          .setEvaluator(new org.matheclipse.core.reflection.system.PeriodicTablePlot());
      S.PeriodogramArray
          .setEvaluator(new org.matheclipse.core.reflection.system.PeriodogramArray());
      S.Permanent.setEvaluator(new org.matheclipse.core.reflection.system.Permanent());
      S.PermutationProduct
          .setEvaluator(new org.matheclipse.core.reflection.system.PermutationProduct());
      S.Permutations.setEvaluator(new org.matheclipse.core.reflection.system.Permutations());
      S.PiecewiseExpand.setEvaluator(new org.matheclipse.core.reflection.system.PiecewiseExpand());
      S.PolynomialReduce
          .setEvaluator(new org.matheclipse.core.reflection.system.PolynomialReduce());
      S.PositionIndex.setEvaluator(new org.matheclipse.core.reflection.system.PositionIndex());
      S.PowerRange.setEvaluator(new org.matheclipse.core.reflection.system.PowerRange());
      S.PrimeNu.setEvaluator(new org.matheclipse.core.reflection.system.PrimeNu());
      S.PrimePi.setEvaluator(new org.matheclipse.core.reflection.system.PrimePi());
      S.PrimeZetaP.setEvaluator(new org.matheclipse.core.reflection.system.PrimeZetaP());
      S.PrimitivePolynomialQ
          .setEvaluator(new org.matheclipse.core.reflection.system.PrimitivePolynomialQ());
      S.Product.setEvaluator(new org.matheclipse.core.reflection.system.Product());

      S.QPochhammer.setEvaluator(new org.matheclipse.core.reflection.system.QPochhammer());

      S.RankDecomposition
          .setEvaluator(new org.matheclipse.core.reflection.system.RankDecomposition());
      S.RangeSpace.setEvaluator(new org.matheclipse.core.reflection.system.RangeSpace());
      S.Ratios.setEvaluator(new org.matheclipse.core.reflection.system.Ratios());
      S.Reduce.setEvaluator(new org.matheclipse.core.reflection.system.Reduce());
      S.RegionBounds.setEvaluator(new org.matheclipse.core.reflection.system.RegionBounds());
      S.RegionCentroid.setEvaluator(new org.matheclipse.core.reflection.system.RegionCentroid());
      S.RegionDimension.setEvaluator(new org.matheclipse.core.reflection.system.RegionDimension());
      S.RegionEmbeddingDimension
          .setEvaluator(new org.matheclipse.core.reflection.system.RegionEmbeddingDimension());
      S.RegionDistance.setEvaluator(new org.matheclipse.core.reflection.system.RegionDistance());
      S.RegionMeasure.setEvaluator(new org.matheclipse.core.reflection.system.RegionMeasure());
      S.RegionMember.setEvaluator(new org.matheclipse.core.reflection.system.RegionMember());
      S.RegionNearest.setEvaluator(new org.matheclipse.core.reflection.system.RegionNearest());
      S.RegionNearestFunction
          .setEvaluator(new org.matheclipse.core.reflection.system.RegionNearestFunction());
      S.RegionWithin.setEvaluator(new org.matheclipse.core.reflection.system.RegionWithin());
      S.Root.setEvaluator(new org.matheclipse.core.reflection.system.Root());
      S.RootReduce.setEvaluator(new org.matheclipse.core.reflection.system.RootReduce());
      S.RootSum.setEvaluator(new org.matheclipse.core.reflection.system.RootSum());
      S.RSolve.setEvaluator(new org.matheclipse.core.reflection.system.RSolve());
      S.RSolveValue.setEvaluator(new org.matheclipse.core.reflection.system.RSolveValue());

      S.Solve.setEvaluator(new org.matheclipse.core.reflection.system.Solve());
      S.SolveAlways.setEvaluator(new org.matheclipse.core.reflection.system.SolveAlways());
      S.SpearmanRho.setEvaluator(new org.matheclipse.core.reflection.system.SpearmanRho());
      S.SpectrogramArray
          .setEvaluator(new org.matheclipse.core.reflection.system.SpectrogramArray());
      S.Subgraph.setEvaluator(new org.matheclipse.core.reflection.system.Subgraph());
      S.Subsets.setEvaluator(new org.matheclipse.core.reflection.system.Subsets());
      S.SudokuSolve.setEvaluator(new org.matheclipse.core.reflection.system.SudokuSolve());
      S.Sum.setEvaluator(new org.matheclipse.core.reflection.system.Sum());
      S.SymbolicIdentityArray
          .setEvaluator(new org.matheclipse.core.reflection.system.SymbolicIdentityArray());
      S.SymmetricPolynomial
          .setEvaluator(new org.matheclipse.core.reflection.system.SymmetricPolynomial());
      S.SymmetricReduction
          .setEvaluator(new org.matheclipse.core.reflection.system.SymmetricReduction());
      S.Symmetrize.setEvaluator(new org.matheclipse.core.reflection.system.Symmetrize());

      S.Taylor.setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
      S.TreeForm.setEvaluator(new org.matheclipse.core.reflection.system.TreeForm());
      S.TreeGraph.setEvaluator(new org.matheclipse.core.reflection.system.TreeGraph());
      S.TreeGraphQ.setEvaluator(new org.matheclipse.core.reflection.system.TreeGraphQ());
      S.TreePlot.setEvaluator(new org.matheclipse.core.reflection.system.TreePlot());
      S.TriangleCenter.setEvaluator(new org.matheclipse.core.reflection.system.TriangleCenter());
      S.TrigExpand.setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
      S.TrigFactor.setEvaluator(new org.matheclipse.core.reflection.system.TrigFactor());
      S.TrigReduce.setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
      S.TrigSimplifyFu.setEvaluator(new org.matheclipse.core.reflection.system.TrigSimplifyFu());
      S.TrigToExp.setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());
      S.ZTransform.setEvaluator(new org.matheclipse.core.reflection.system.ZTransform());

      S.Hash.setEvaluator(new org.matheclipse.core.reflection.system.Hash());
      S.FileHash.setEvaluator(new org.matheclipse.core.reflection.system.FileHash());
      S.RiemannSiegelTheta
          .setEvaluator(new org.matheclipse.core.reflection.system.RiemannSiegelTheta());

      S.ShortestCurveDistance
          .setEvaluator(new org.matheclipse.core.reflection.system.ShortestCurveDistance());
      S.SignedRegionDistance
          .setEvaluator(new org.matheclipse.core.reflection.system.SignedRegionDistance());
      S.ShiftRegisterSequence
          .setEvaluator(new org.matheclipse.core.reflection.system.ShiftRegisterSequence());
      S.TensorContract.setEvaluator(new org.matheclipse.core.reflection.system.TensorContract());
      S.TensorProduct.setEvaluator(new org.matheclipse.core.reflection.system.TensorProduct());
      S.TensorTranspose.setEvaluator(new org.matheclipse.core.reflection.system.TensorTranspose());
      S.TopologicalSort.setEvaluator(new org.matheclipse.core.reflection.system.TopologicalSort());
      S.ToRadicals.setEvaluator(new org.matheclipse.core.reflection.system.ToRadicals());
      S.Zeta.setEvaluator(new org.matheclipse.core.reflection.system.Zeta());
      S.ZetaZero.setEvaluator(new org.matheclipse.core.reflection.system.ZetaZero());

      // Graphics
      S.ComplexPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ComplexPlot());
      S.ContourPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ContourPlot());
      S.DensityHistogram.setEvaluator(new org.matheclipse.core.builtin.graphics.DensityHistogram());
      S.DensityPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.DensityPlot());
      S.Graph3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.Graph3D());
      S.Histogram.setEvaluator(new org.matheclipse.core.builtin.graphics.Histogram());
      S.ListContourPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListContourPlot());
      S.ListLinePlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListLinePlot());
      S.ListPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListPlot());
      S.ListLogPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListLogPlot());
      S.ListLogLogPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListLogLogPlot());
      S.ListLogLinearPlot
          .setEvaluator(new org.matheclipse.core.builtin.graphics.ListLogLinearPlot());
      S.ListPolarPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListPolarPlot());
      S.ListStepPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ListStepPlot());
      S.LogPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.LogPlot());
      S.LogLinearPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.LogLinearPlot());
      S.LogLogPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.LogLogPlot());
      S.MatrixPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.MatrixPlot());
      S.ParametricPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.ParametricPlot());
      S.PieChart.setEvaluator(new org.matheclipse.core.builtin.graphics.PieChart());
      S.Plot.setEvaluator(new org.matheclipse.core.builtin.graphics.Plot());
      S.WordCloud.setEvaluator(new org.matheclipse.core.builtin.graphics.WordCloud());

      // Graphics3D
      S.ContourPlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.ContourPlot3D());
      S.ComplexPlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.ComplexPlot3D());
      S.DiscretePlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.DiscretePlot3D());
      S.ListLinePlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.ListLinePlot3D());
      S.ListPlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.ListPlot3D());
      S.ListPointPlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.ListPointPlot3D());
      S.Plot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.Plot3D());
      S.ParametricPlot3D
          .setEvaluator(new org.matheclipse.core.builtin.graphics3d.ParametricPlot3D());
      S.PolarPlot.setEvaluator(new org.matheclipse.core.builtin.graphics.PolarPlot());
      S.RevolutionPlot3D
          .setEvaluator(new org.matheclipse.core.builtin.graphics3d.RevolutionPlot3D());
      S.SphericalPlot3D.setEvaluator(new org.matheclipse.core.builtin.graphics3d.SphericalPlot3D());

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
