package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.transform.DftNormalization;
import org.hipparchus.transform.FastFourierTransformer;
import org.hipparchus.transform.TransformType;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class NFourierTransform extends AbstractFunctionEvaluator {

	public NFourierTransform() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 4, 5);

		IExpr expr = ast.arg1();
		ISymbol t = Validate.checkSymbolType(ast, 2);
		// IExpr omega = ast.arg3();
		if (ast.size() > 4) {
			final Options options = new Options(ast.topHead(), ast, 4, engine);
			IExpr optionFourierParameters = options.getOption("FourierParameters");
			if (optionFourierParameters.isList()) {
				// analyze the parameters, if they are correct
			}
		}

		UnivariateFunction f = new UnaryNumerical(expr, t, engine);
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		org.hipparchus.complex.Complex[] result = fft.transform(f, -1.0, 1.0, 8, TransformType.FORWARD);
		return Object2Expr.convertComplex(true, result);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDFIRST);
	}
}