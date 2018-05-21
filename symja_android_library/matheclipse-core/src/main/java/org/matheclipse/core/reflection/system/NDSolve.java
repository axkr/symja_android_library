package org.matheclipse.core.reflection.system;

import org.hipparchus.ode.AbstractIntegrator;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation"> Wikipedia:Ordinary differential
 * equation</a>
 * 
 */
public class NDSolve extends AbstractFunctionEvaluator {

	private static class FirstODE implements OrdinaryDifferentialEquation {
		private final EvalEngine fEngine;
		private final IExpr[] fDotEquations;
		private final int fDimension;
		private final IAST fVariables;
		private final ISymbol fT;

		public FirstODE(EvalEngine engine, IExpr[] dotEquations, IAST variables, ISymbol t) {
			this.fEngine = engine;
			this.fDotEquations = dotEquations;
			this.fDimension = fDotEquations.length;
			this.fVariables = variables;
			this.fT = t;
		}

		@Override
		public int getDimension() {
			return fDimension;
		}

		@Override
		public double[] computeDerivatives(double t, double[] xyz) {
			double[] xyzDot = new double[fDimension];
			IExpr[] replacements = new IExpr[fDimension];
			IASTAppendable rules = F.ListAlloc(fDimension + 1);
			for (int i = 0; i < fDimension; i++) {
				replacements[i] = F.$(fVariables.get(i + 1), fT);
				rules.append(F.Rule(replacements[i], F.num(xyz[i])));
			}
			rules.append(F.Rule(fT, F.num(t)));
			IExpr[] dotEquations = new IExpr[fDimension];
			for (int i = 0; i < fDimension; i++) {
				dotEquations[i] = fDotEquations[i].replaceAll(rules);
			}
			for (int i = 0; i < fDimension; i++) {
				xyzDot[i] = ((INum) fEngine.evalN(dotEquations[i])).doubleValue();
			}
			return xyzDot;
		}
	}

	public NDSolve() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.DSOLVE) {
			return F.NIL;
		}
		Validate.checkSize(ast, 4);

		if (ast.arg3().isAST(F.List, 4)) {
			try {
				IAST uFunctionSymbols = Validate.checkSymbolOrSymbolList(ast, 2);
				int dimension = uFunctionSymbols.argSize();
				IAST xVarList = (IAST) ast.arg3();
				ISymbol xVar = (ISymbol) xVarList.arg1();
				IExpr xMinExpr = xVarList.arg2();
				IExpr xMaxExpr = xVarList.arg3();
				if (xMinExpr.isReal() && xMaxExpr.isReal()) {
					double xMin = ((ISignedNumber) xMinExpr).doubleValue();
					double xMax = ((ISignedNumber) xMaxExpr).doubleValue();
					double xStep = 0.1;
					IASTAppendable listOfEquations = Validate.checkEquations(ast, 1).copyAppendable();
					IExpr[][] boundaryCondition = new IExpr[2][dimension];
					int i = 1;
					while (i < listOfEquations.size()) {
						IExpr equation = listOfEquations.get(i);
						if (equation.isFree(xVar)) {
							if (determineSingleBoundary(equation, uFunctionSymbols, xVar, boundaryCondition, engine)) {
								listOfEquations.remove(i);
								continue;
							}
						}
						i++;
					}

					IExpr[] dotEquations = new IExpr[dimension];
					i = 1;
					while (i < listOfEquations.size()) {
						IExpr equation = listOfEquations.get(i);
						if (!equation.isFree(xVar)) {
							if (determineSingleDotEquation(equation, uFunctionSymbols, xVar, dotEquations, engine)) {
								listOfEquations.remove(i);
								continue;
							}
						}
						i++;
					}

					if (uFunctionSymbols.isList()) {
						AbstractIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
						double[] xyz = new double[dimension];
						for (int j = 0; j < dimension; j++) {
							xyz[j] = ((INum) engine.evalN(boundaryCondition[1][j])).doubleValue();
						}

						OrdinaryDifferentialEquation ode = new FirstODE(engine, dotEquations, uFunctionSymbols, xVar);

						IASTAppendable resultList = F.ListAlloc(16);

						IAST result = F.Interpolation(resultList);

						IASTAppendable list;
						for (double tDouble = xMin; tDouble < xMax; tDouble += xStep) {
							list = F.List(F.num(tDouble));
							dp853.integrate(ode, tDouble, xyz, tDouble + xStep, xyz);
							for (int j = 0; j < xyz.length; j++) {
								list.append(F.num(xyz[j]));
							}
							resultList.append(list);
						}
						resultList.setEvalFlags(IAST.IS_MATRIX);
						return result;

					}
				}
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}

			}
		}
		return F.NIL;
	}

	/**
	 * Equation <code>-1+y(0)</code> gives <code>[0, 1]</code> (representing the boundary equation y(0)==1)
	 * 
	 * @param equation
	 *            the equation
	 * @param uFunctionSymbols
	 * @param xVar
	 * @param engine
	 * @return
	 */
	private boolean determineSingleBoundary(IExpr equation, IAST uFunctionSymbols, IExpr xVar,
			IExpr boundaryCondition[][], EvalEngine engine) {
		if (equation.isAST()) {
			IASTAppendable eq = ((IAST) equation).copyAppendable();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			int j = 1;
			IExpr uArg1 = null;
			IASTAppendable rest = F.PlusAlloc(16);
			boolean negate;
			while (j < eq.size()) {
				IExpr temp = eq.get(j);
				for (int i = 1; i < uFunctionSymbols.size(); i++) {
					negate = true;
					if (temp.isAST2() && temp.first().isMinusOne()) {
						temp = temp.second();
						negate = false;
					}
					if (temp.isAST(uFunctionSymbols.get(i))) {
						uArg1 = temp.first();
						if (boundaryCondition[0][i - 1] != null) {
							return false;
						}
						boundaryCondition[0][i - 1] = uArg1;
						if (negate) {
							temp = engine.evaluate(rest.getOneIdentity(F.C0).negate());
						} else {
							temp = engine.evaluate(rest.getOneIdentity(F.C0));
						}
						boundaryCondition[1][i - 1] = temp;
						eq.remove(j);
						return true;
					}
				}
				rest.append(eq.get(j));
				j++;
			}

		}
		return false;
	}

	private boolean determineSingleDotEquation(IExpr equation, IAST uFunctionSymbols, IExpr xVar, IExpr dotEquations[],
			EvalEngine engine) {
		if (equation.isAST()) {
			IASTAppendable eq = ((IAST) equation).copyAppendable();
			if (!eq.isPlus()) {
				// create artificial Plus(...) expression
				eq = F.Plus(eq);
			}

			int j = 1;
			IASTAppendable rest = F.PlusAlloc(16);
			boolean negate;
			while (j < eq.size()) {
				negate = true;
				IExpr temp = eq.get(j);
				if (temp.isAST2() && temp.first().isMinusOne()) {
					temp = temp.second();
					negate = false;
				}
				IAST[] deriveExpr = temp.isDerivativeAST1();
				if (deriveExpr != null) {
					for (int i = 1; i < uFunctionSymbols.size(); i++) {
						if (deriveExpr[1].arg1().equals(uFunctionSymbols.get(i))) {
							if (DSolve.derivativeOrder(deriveExpr) != 1) {
								return false;
							}
							if (dotEquations[i - 1] != null) {
								return false;
							}
							if (negate) {
								temp = engine.evaluate(rest.getOneIdentity(F.C0).negate());
							} else {
								temp = engine.evaluate(rest.getOneIdentity(F.C0));
							}
							dotEquations[i - 1] = temp;
							// eliminate deriveExpr from Plus(...) expression
							eq.remove(j);
							return true;
						}
					}
				} else {
					rest.append(eq.get(j));
				}
				j++;

			}

		}
		return false;
	}

}
