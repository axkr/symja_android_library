package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.*; 

import org.matheclipse.core.expression.F;

public final class FunctionDefinitions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			ComplexExpand.setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
			Compile.setEvaluator(new org.matheclipse.core.reflection.system.Compile());
			CreateDirectory.setEvaluator(new org.matheclipse.core.reflection.system.CreateDirectory());
			D.setEvaluator(new org.matheclipse.core.reflection.system.D());
			Derivative.setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
			DSolve.setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
			EasterSunday.setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
			ElementData.setEvaluator(new org.matheclipse.core.data.ElementData());
			Eliminate.setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
			Export.setEvaluator(new org.matheclipse.core.reflection.system.Export());
			ExpToTrig.setEvaluator(new org.matheclipse.core.reflection.system.ExpToTrig());
			FindInstance.setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
			FindRoot.setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
			F.Fourier.setEvaluator(new org.matheclipse.core.reflection.system.Fourier());
			FrobeniusSolve.setEvaluator(new org.matheclipse.core.reflection.system.FrobeniusSolve());
			FunctionExpand.setEvaluator(new org.matheclipse.core.reflection.system.FunctionExpand());
			GroebnerBasis.setEvaluator(new org.matheclipse.core.reflection.system.GroebnerBasis());
			HeavisideTheta.setEvaluator(new org.matheclipse.core.reflection.system.HeavisideTheta());
			Horner.setEvaluator(new org.matheclipse.core.reflection.system.Horner());
			Import.setEvaluator(new org.matheclipse.core.reflection.system.Import());
			InterpolatingFunction.setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingFunction());
			InterpolatingPolynomial.setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingPolynomial());
			Interpolation.setEvaluator(new org.matheclipse.core.reflection.system.Interpolation());
			Interval.setEvaluator(new org.matheclipse.core.reflection.system.Interval());
			F.InverseFourier.setEvaluator(new org.matheclipse.core.reflection.system.InverseFourier());
			InverseFunction.setEvaluator(new org.matheclipse.core.reflection.system.InverseFunction());
			InverseLaplaceTransform.setEvaluator(new org.matheclipse.core.reflection.system.InverseLaplaceTransform());
			LaplaceTransform.setEvaluator(new org.matheclipse.core.reflection.system.LaplaceTransform());
			LinearProgramming.setEvaluator(new org.matheclipse.core.reflection.system.LinearProgramming());
			F.ListLinePlot.setEvaluator(new org.matheclipse.core.reflection.system.ListLinePlot());
			F.ListPlot.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot());
			F.ListPlot3D.setEvaluator(new org.matheclipse.core.reflection.system.ListPlot3D());
			MonomialList.setEvaluator(new org.matheclipse.core.reflection.system.MonomialList());
			NDSolve.setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
			NFourierTransform.setEvaluator(new org.matheclipse.core.reflection.system.NFourierTransform());
			NIntegrate.setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
			NonCommutativeMultiply.setEvaluator(new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
			NSolve.setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
			OptimizeExpression.setEvaluator(new org.matheclipse.core.reflection.system.OptimizeExpression());
			Out.setEvaluator(new org.matheclipse.core.reflection.system.Out());
			Outer.setEvaluator(new org.matheclipse.core.reflection.system.Outer());
			ParametricPlot.setEvaluator(new org.matheclipse.core.reflection.system.ParametricPlot());
			Plot.setEvaluator(new org.matheclipse.core.reflection.system.Plot());
			Plot3D.setEvaluator(new org.matheclipse.core.reflection.system.Plot3D());
			Product.setEvaluator(new org.matheclipse.core.reflection.system.Product());

			Share.setEvaluator(new org.matheclipse.core.reflection.system.Share());
			Solve.setEvaluator(new org.matheclipse.core.reflection.system.Solve());
			Sum.setEvaluator(new org.matheclipse.core.reflection.system.Sum());
			Taylor.setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
			TrigExpand.setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
			TrigReduce.setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
			TrigToExp.setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private FunctionDefinitions() {

	}
}
