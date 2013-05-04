package org.matheclipse.core.reflection.system;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.IllinoisSolver;
import org.apache.commons.math3.analysis.solvers.MullerSolver;
import org.apache.commons.math3.analysis.solvers.NewtonSolver;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.RegulaFalsiSolver;
import org.apache.commons.math3.analysis.solvers.RiddersSolver;
import org.apache.commons.math3.analysis.solvers.SecantSolver;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Function for <a
 * href="http://en.wikipedia.org/wiki/Root-finding_algorithm">numerically
 * finding roots</a> of a univariate real function.
 * 
 * Uses the <a href="http://commons.apache.org/math/apidocs/org/apache/commons/math/analysis/solvers/UnivariateRealSolver.html"
 * >Commons math BisectionSolver, BrentSolver, MullerSolver, NewtonSolver,
 * RiddersSolver, SecantSolver</a> implementations.
 */
public class FindRoot extends AbstractFunctionEvaluator implements IConstantHeaders {

	public FindRoot() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		String method = "Newton";
		int maxIterations = 100;
		if (ast.size() >= 4) {
			final Options options = new Options(ast.topHead(), ast, 3);
			IExpr optionMaxIterations = options.getOption("MaxIterations");
			if (optionMaxIterations != null && optionMaxIterations.isSignedNumber()) {
				maxIterations = ((ISignedNumber) optionMaxIterations).toInt();
			}
			IExpr optionMethod = options.getOption("Method");
			if (optionMethod != null && optionMethod.isSymbol()) {
				method = ((ISymbol) optionMethod).toString();
			} else {
				if (ast.get(3).isSymbol()) {
					method = ast.get(3).toString();
				}
			}
		}
		if ((ast.get(2).isList())) {
			IAST list = (IAST) ast.get(2);
			IExpr function = ast.get(1);
			if (list.size() == 4 && list.get(1).isSymbol() && list.get(2).isSignedNumber() && list.get(3) instanceof ISignedNumber) {
				if (function.isAST(F.Equal, 3)) {
					function = F.Plus(((IAST) function).get(1), F.Times(F.CN1, ((IAST) function).get(2)));
				}
				return F.List(F.Rule(list.get(1), Num.valueOf(findRoot(method, maxIterations, list, function))));
			}
		}
		return null;
	}

	private double findRoot(String method, int maxIterations, IAST list, IExpr function) {
		ISymbol xVar = (ISymbol) list.get(1);
		ISignedNumber min = (ISignedNumber) list.get(2);
		ISignedNumber max = (ISignedNumber) list.get(3);
		final EvalEngine engine = EvalEngine.get();
		function = F.eval(function);
		DifferentiableUnivariateFunction f = new UnaryNumerical(function, xVar, engine);
		BaseAbstractUnivariateSolver<UnivariateFunction> solver = null;
		if (method.equalsIgnoreCase("Bisection")) {
			solver = new BisectionSolver();
		} else if (method.equalsIgnoreCase("Brent")) {
			solver = new BrentSolver();
			// } else if (method.equalsIgnoreCase("Laguerre")) {
			// solver = new LaguerreSolver();
		} else if (method.equalsIgnoreCase("Muller")) {
			solver = new MullerSolver();
		} else if (method.equalsIgnoreCase("Ridders")) {
			solver = new RiddersSolver();
		} else if (method.equalsIgnoreCase("Secant")) {
			solver = new SecantSolver();
		} else if (method.equalsIgnoreCase("RegulaFalsi")) {
			solver = new RegulaFalsiSolver();
		} else if (method.equalsIgnoreCase("Illinois")) {
			solver = new IllinoisSolver();
		} else if (method.equalsIgnoreCase("Pegasus")) {
			solver = new PegasusSolver();
		} else {
			// default: NewtonSolver
			BaseAbstractUnivariateSolver<DifferentiableUnivariateFunction> solver2 = new NewtonSolver();
			return solver2.solve(maxIterations, f, min.doubleValue(), max.doubleValue());
		}
		return solver.solve(maxIterations, f, min.doubleValue(), max.doubleValue());

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}