package org.matheclipse.core.reflection.system;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.solvers.BaseAbstractUnivariateSolver;
import org.apache.commons.math4.analysis.solvers.BisectionSolver;
import org.apache.commons.math4.analysis.solvers.BrentSolver;
import org.apache.commons.math4.analysis.solvers.IllinoisSolver;
import org.apache.commons.math4.analysis.solvers.MullerSolver;
import org.apache.commons.math4.analysis.solvers.PegasusSolver;
import org.apache.commons.math4.analysis.solvers.RegulaFalsiSolver;
import org.apache.commons.math4.analysis.solvers.RiddersSolver;
import org.apache.commons.math4.analysis.solvers.SecantSolver;
import org.matheclipse.commons.math.analysis.solvers.DifferentiableUnivariateFunction;
import org.matheclipse.commons.math.analysis.solvers.NewtonSolver;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Function for <a href="http://en.wikipedia.org/wiki/Root-finding_algorithm">numerically finding roots</a> of a univariate real
 * function.
 * 
 * Uses the <a href= "http://commons.apache.org/math/apidocs/org/apache/commons/math/analysis/solvers/UnivariateRealSolver.html"
 * >Commons math BisectionSolver, BrentSolver, MullerSolver, NewtonSolver, RiddersSolver, SecantSolver</a> implementations.
 */
public class FindRoot extends AbstractFunctionEvaluator {

	public final static ISymbol Newton = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "newton" : "Newton");

	public FindRoot() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		ISymbol method = Newton;
		int maxIterations = 100;
		if (ast.size() >= 4) {
			final Options options = new Options(ast.topHead(), ast, 3);
			IExpr optionMaxIterations = options.getOption("MaxIterations");
			if (optionMaxIterations != null && optionMaxIterations.isSignedNumber()) {
				maxIterations = ((ISignedNumber) optionMaxIterations).toInt();
			}
			IExpr optionMethod = options.getOption("Method");
			if (optionMethod != null && optionMethod.isSymbol()) {
				method = ((ISymbol) optionMethod);
			} else {
				if (ast.arg3().isSymbol()) {
					method = (ISymbol) ast.arg3();
				}
			}
		}
		if ((ast.arg2().isList())) {
			IAST list = (IAST) ast.arg2();
			IExpr function = ast.arg1();
			if (list.size() >= 3 && list.arg1().isSymbol()) {
				if (function.isAST(F.Equal, 3)) {
					function = F.Plus(((IAST) function).arg1(), F.Negate(((IAST) function).arg2()));
				}
				ISignedNumber min = list.arg2().evalSignedNumber();
				ISignedNumber max = null;
				if (list.size() > 3) {
					max = list.arg3().evalSignedNumber();
				}
				return F.List(F.Rule(list.arg1(), Num.valueOf(findRoot(method, maxIterations, list, min, max, function))));
			}
		}
		return null;
	}

	private double findRoot(ISymbol method, int maxIterations, IAST list, ISignedNumber min, ISignedNumber max, IExpr function) {
		ISymbol xVar = (ISymbol) list.arg1();
		final EvalEngine engine = EvalEngine.get();
		function = F.eval(function);
		UnivariateFunction f = new UnaryNumerical(function, xVar, engine);
		BaseAbstractUnivariateSolver<UnivariateFunction> solver = null;
		if (method.isSymbolName("Bisection")) {
			solver = new BisectionSolver();
		} else if (method.isSymbolName("Brent")) {
			solver = new BrentSolver();
			// } else if (method.isSymbolName("Laguerre")) {
			// solver = new LaguerreSolver();
		} else if (method.isSymbolName("Muller")) {
			solver = new MullerSolver();
		} else if (method.isSymbolName("Ridders")) {
			solver = new RiddersSolver();
		} else if (method.isSymbolName("Secant")) {
			solver = new SecantSolver();
		} else if (method.isSymbolName("RegulaFalsi")) {
			solver = new RegulaFalsiSolver();
		} else if (method.isSymbolName("Illinois")) {
			solver = new IllinoisSolver();
		} else if (method.isSymbolName("Pegasus")) {
			solver = new PegasusSolver();
		} else {
			// default: NewtonSolver
			DifferentiableUnivariateFunction fNewton = new UnaryNumerical(function, xVar, engine);
			BaseAbstractUnivariateSolver<DifferentiableUnivariateFunction> solver2 = new NewtonSolver();
			if (max == null) {
				return solver2.solve(maxIterations, fNewton, min.doubleValue());
			}
			return solver2.solve(maxIterations, fNewton, min.doubleValue(), max.doubleValue());
		}
		if (max == null) {
			return solver.solve(maxIterations, f, min.doubleValue());
		}
		return solver.solve(maxIterations, f, min.doubleValue(), max.doubleValue());

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}