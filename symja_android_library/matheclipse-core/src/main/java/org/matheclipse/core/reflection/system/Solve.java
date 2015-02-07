package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions).
 */
public class Solve extends AbstractFunctionEvaluator {
	/**
	 * Analyze an expression, if it has linear, polynomial or other form.
	 * 
	 */
	private static class ExprAnalyzer implements Comparable<ExprAnalyzer> {

		final static public int LINEAR = 0;
		final static public int OTHERS = 2;
		// final static public int INVERSE_FUNCTION = 2;
		final static public int POLYNOMIAL = 1;

		private int fEquationType;
		private IExpr fExpr;
		private IExpr fNumer;
		private IExpr fDenom;
		private long fLeafCount;

		private HashSet<ISymbol> fSymbolSet;
		private IAST fMatrixRow;
		private IAST fPlusAST;

		final IAST vars;

		public ExprAnalyzer(IExpr expr, IAST vars) {
			super();
			this.fExpr = expr;
			this.fNumer = expr;
			this.fDenom = F.C1;
			if (this.fExpr.isAST()) {
				this.fExpr = Together.together((IAST) this.fExpr);
				// split expr into numerator and denominator
				this.fDenom = F.eval(F.Denominator(this.fExpr));
				if (!this.fDenom.isOne()) {
					// search roots for the numerator expression
					this.fNumer = F.eval(F.Numerator(this.fExpr));
				} else {
					this.fNumer = this.fExpr;
				}
			}
			this.vars = vars;
			this.fSymbolSet = new HashSet<ISymbol>();
			this.fLeafCount = 0;
			reset();
		}

		/**
		 * If possible simplify the numerator expression. After that analyze the numerator expression, if it has linear, polynomial
		 * or other form.
		 */
		protected void simplifyAndAnalyze() {
			IExpr temp = null;
			if (fNumer.isPlus()) {
				temp = rewritePlusWithInverseFunctions((IAST) fNumer);
			} else if (fNumer.isTimes() && !fNumer.isFree(Predicates.in(vars), true)) {
				temp = rewriteTimesWithInverseFunctions((IAST) fNumer);
			} else if (fNumer.isAST() && !fNumer.isFree(Predicates.in(vars), true)) {
				temp = rewriteInverseFunction((IAST) fNumer, F.C0);
			}
			if (temp != null) {
				fNumer = temp;
			}

			analyze(fNumer);
		}

		/**
		 * Try to rewrite a <code>Times(...,f(x), ...)</code> expression which may contain an invertable function argument
		 * <code>f(x)</code> as subexpression.
		 */
		private IExpr rewriteTimesWithInverseFunctions(IAST times) {
			IAST result = null;
			int j = 1;
			// remove constant sub-expressions from Times() expression
			for (int i = 1; i < times.size(); i++) {
				if (times.get(i).isFree(Predicates.in(vars), true) && times.get(i).isNumericFunction()) {
					if (result == null) {
						result = times.clone();
					}
					result.remove(j);
					continue;
				}
				j++;
			}
			if (result == null) {
				return rewriteInverseFunction(times, F.C0);
			}
			IExpr temp0 = result.getOneIdentity(F.C1);
			if (temp0.isAST()) {
				IExpr temp = rewriteInverseFunction((IAST) temp0, F.C0);
				if (temp != null) {
					return temp;
				}
			}
			return temp0;
		}

		/**
		 * Try to rewrite a <code>Plus(...,f(x), ...)</code> function which contains an invertable function argument
		 * <code>f(x)</code>.
		 */
		private IExpr rewritePlusWithInverseFunctions(IAST plusAST) {
			IExpr expr;
			for (int i = 1; i < plusAST.size(); i++) {
				expr = plusAST.get(i);
				if (expr.isFree(Predicates.in(vars), true)) {
					continue;
				}

				if (expr.isAST()) {
					IAST inverseFunction = InverseFunction.getUnaryInverseFunction((IAST) expr);
					if (inverseFunction != null) {
						IExpr temp = rewriteInverseFunction(plusAST, i);
						if (temp != null) {
							return temp;
						}
					}
				}

			}
			return null;
		}

		/**
		 * Check for an applicable inverse function at the given <code>position</code> in the <code>Plus(..., ,...)</code>
		 * expression.
		 * 
		 * @param plusAST
		 *            the <code>Plus(..., ,...)</code> expression
		 * @param position
		 * @return <code>null</code> if no inverse function was found, otherwise return the rewritten expression
		 */
		private IExpr rewriteInverseFunction(IAST plusAST, int position) {
			IAST ast = (IAST) plusAST.get(position);
			IExpr plus = plusAST.removeAtClone(position).getOneIdentity(F.C0);
			if (plus.isFree(Predicates.in(vars), true)) {
				return rewriteInverseFunction(ast, F.Negate(plus));
			}
			return null;
		}

		/**
		 * Check for an applicable inverse function at the given <code>position</code> in the <code>Plus(..., ,...)</code>
		 * expression.
		 * 
		 * @param ast
		 * @param arg1
		 * @return
		 */
		private IExpr rewriteInverseFunction(IAST ast, IExpr arg1) {
			if (ast.size() == 2 && ast.arg1().isSymbol()) {
				int position = vars.findFirstEquals(ast.arg1());
				if (position > 0) {
					IAST inverseFunction = InverseFunction.getUnaryInverseFunction(ast);
					if (inverseFunction != null) {
						// rewrite fNumer
						inverseFunction.add(arg1);
						return F.eval(F.Subtract(ast.arg1(), inverseFunction));
					}
				}

			} else if (ast.isPower() && ast.arg1().isSymbol() && ast.arg2().isNumber()) {
				int position = vars.findFirstEquals(ast.arg1());
				if (position > 0) {
					IAST inverseFunction = F.Power(arg1, ast.arg2().inverse());
					return F.eval(F.Subtract(ast.arg1(), inverseFunction));
				}

			}
			return null;
		}

		/**
		 * Analyze an expression, if it has linear, polynomial or other form.
		 * 
		 */
		private void analyze(IExpr eqExpr) {
			if (eqExpr.isFree(Predicates.in(vars), true)) {
				fLeafCount++;
				fPlusAST.add(eqExpr);
			} else if (eqExpr.isPlus()) {
				fLeafCount++;
				IAST plusAST = (IAST) eqExpr;
				IExpr expr;
				for (int i = 1; i < plusAST.size(); i++) {
					expr = plusAST.get(i);
					if (expr.isFree(Predicates.in(vars), true)) {
						fLeafCount++;
						fPlusAST.add(expr);
					} else {
						getPlusArgumentEquationType(expr);
					}
				}
			} else {
				getPlusArgumentEquationType(eqExpr);
			}
		}

		@Override
		public int compareTo(ExprAnalyzer o) {
			if (fSymbolSet.size() != o.fSymbolSet.size()) {
				if (fSymbolSet.size() < o.fSymbolSet.size()) {
					return -1;
				}
				return 1;
			}
			if (fEquationType != o.fEquationType) {
				if (fEquationType < o.fEquationType) {
					return -1;
				}
				return 1;
			}
			if (fLeafCount != o.fLeafCount) {
				if (fLeafCount < o.fLeafCount) {
					return -1;
				}
				return 1;
			}

			return 0;
		}

		/**
		 * @return the expr
		 */
		public IExpr getExpr() {
			return fExpr;
		}

		public IExpr getNumerator() {
			return fNumer;
		}

		public IExpr getDenominator() {
			return fDenom;
		}

		public int getNumberOfVars() {
			return fSymbolSet.size();
		}

		/**
		 * Get the argument type.
		 * 
		 * @param eqExpr
		 */
		private void getPlusArgumentEquationType(IExpr eqExpr) {
			if (eqExpr.isTimes()) {
				ISymbol sym = null;
				fLeafCount++;
				IAST arg = (IAST) eqExpr;
				IExpr expr;
				for (int i = 1; i < arg.size(); i++) {
					expr = arg.get(i);
					if (expr.isFree(Predicates.in(vars), true)) {
						fLeafCount++;
					} else if (expr.isSymbol()) {
						fLeafCount++;
						for (int j = 1; j < vars.size(); j++) {
							if (vars.get(j).equals(expr)) {
								fSymbolSet.add((ISymbol) expr);
								if (sym != null) {
									if (fEquationType == LINEAR) {
										fEquationType = POLYNOMIAL;
									}
								} else {
									sym = (ISymbol) expr;
									if (fEquationType == LINEAR) {
										IAST cloned = arg.removeAtClone(i);
										fMatrixRow.set(j, F.Plus(fMatrixRow.get(j), cloned));
									}
								}
							}
						}
					} else if (expr.isPower() && (expr.getAt(2).isInteger() || expr.getAt(2).isNumIntValue())) {
						// (JASConvert.getExponent((IAST) expr) > 0)) {
						if (fEquationType == LINEAR) {
							fEquationType = POLYNOMIAL;
						}
						getTimesArgumentEquationType(((IAST) expr).arg1());
					} else {
						fLeafCount += eqExpr.leafCount();
						if (fEquationType <= POLYNOMIAL) {
							fEquationType = OTHERS;
						}
					}
				}
				if (fEquationType == LINEAR) {
					if (sym == null) {
						// should never happen??
						System.out.println("sym == null???");
					}
				}
			} else {
				getTimesArgumentEquationType(eqExpr);
			}
		}

		/**
		 * @return the row
		 */
		public IAST getRow() {
			return fMatrixRow;
		}

		/**
		 * @return the symbolSet
		 */
		public HashSet<ISymbol> getSymbolSet() {
			return fSymbolSet;
		}

		private void getTimesArgumentEquationType(IExpr expr) {
			if (expr.isSymbol()) {
				fLeafCount++;
				int position = vars.findFirstEquals(expr);
				if (position > 0) {
					fSymbolSet.add((ISymbol) expr);
					if (fEquationType == LINEAR) {
						fMatrixRow.set(position, F.Plus(fMatrixRow.get(position), F.C1));
					}
				}
				return;
			}
			if (expr.isFree(Predicates.in(vars), true)) {
				fLeafCount++;
				fPlusAST.add(expr);
				return;
			}
			if (expr.isPower()) {
				if (((IAST) expr).arg2().isInteger()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesArgumentEquationType(((IAST) expr).arg1());
					return;
				}
				if (((IAST) expr).arg2().isNumIntValue()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesArgumentEquationType(((IAST) expr).arg1());
					return;
				}
			}
			fLeafCount += expr.leafCount();
			if (fEquationType <= POLYNOMIAL) {
				fEquationType = OTHERS;
			}

		}

		/**
		 * @return the value
		 */
		public IExpr getValue() {
			return fPlusAST.getOneIdentity(F.C0);
		}

		/**
		 * Return <code>true</code> if the expression is linear.
		 * 
		 * @return <code>true</code> if the expression is linear
		 */
		public boolean isLinear() {
			return fEquationType == LINEAR;
		}

		public boolean isLinearOrPolynomial() {
			return fEquationType == LINEAR || fEquationType == POLYNOMIAL;
		}

		public void reset() {
			this.fMatrixRow = F.List();
			for (int i = 1; i < vars.size(); i++) {
				fMatrixRow.add(F.C0);
			}
			this.fPlusAST = F.Plus();
			this.fEquationType = LINEAR;
		}

	}

	@SuppressWarnings("serial")
	private static class NoSolution extends Exception {
		/**
		 * Solution couldn't be found.
		 */
		final static public int NO_SOLUTION_FOUND = 1;

		/**
		 * Definitely wrong solution.
		 */
		final static public int WRONG_SOLUTION = 0;

		final int solType;

		public NoSolution(int solType) {
			super();
			this.solType = solType;
		}

		public int getType() {
			return solType;
		}
	}

	/**
	 * 
	 * @param analyzerList
	 * @param vars
	 * @param resultList
	 * @param matrix
	 * @param vector
	 * @return <code>null</code> if the solution couldn't be found
	 */
	private static IAST analyzeSublist(ArrayList<ExprAnalyzer> analyzerList, IAST vars, IAST resultList, IAST matrix, IAST vector)
			throws NoSolution {
		ExprAnalyzer exprAnalyzer;
		Collections.sort(analyzerList);
		int currEquation = 0;
		while (currEquation < analyzerList.size()) {
			exprAnalyzer = analyzerList.get(currEquation);
			if (exprAnalyzer.getNumberOfVars() == 0) {
				// check if the equation equals zero.
				IExpr expr = exprAnalyzer.getNumerator();
				if (!expr.isZero()) {
					if (expr.isNumber()) {
						throw new NoSolution(NoSolution.WRONG_SOLUTION);
					}
					if (!PossibleZeroQ.possibleZeroQ(expr)) {
						throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
					}
				}
			} else if (exprAnalyzer.getNumberOfVars() == 1 && exprAnalyzer.isLinearOrPolynomial()) {
				IAST listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer);
				if (listOfRules != null) {
					boolean evaled = false;
					++currEquation;
					for (int k = 1; k < listOfRules.size(); k++) {
						if (currEquation >= analyzerList.size()) {
							resultList.add(F.List(listOfRules.getAST(k)));
							evaled = true;
						} else {

							ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
							// collect linear and univariate polynomial
							// equations:
							for (int i = currEquation; i < analyzerList.size(); i++) {
								IExpr expr = analyzerList.get(i).getExpr();
								IExpr temp = expr.replaceAll(listOfRules.getAST(k));
								if (temp != null) {
									expr = F.eval(temp);
									exprAnalyzer = new ExprAnalyzer(expr, vars);
									exprAnalyzer.simplifyAndAnalyze();
								} else {
									// reuse old analyzer; expression hasn't
									// changed
									exprAnalyzer = analyzerList.get(i);
								}
								subAnalyzerList.add(exprAnalyzer);
							}
							try {
								IAST subResultList = analyzeSublist(subAnalyzerList, vars, F.List(), matrix, vector);
								if (subResultList != null) {
									evaled = true;
									for (IExpr expr : subResultList) {
										if (expr.isList()) {
											IAST list = (IAST) expr;
											list.add(1, listOfRules.getAST(k));
											resultList.add(list);
										} else {
											resultList.add(expr);
										}
									}
								}
							} catch (NoSolution e) {
								if (e.getType() == NoSolution.WRONG_SOLUTION) {
									evaled = true;
								}
							}
						}
					}
					if (evaled) {
						return resultList;
					}
				}
				throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
			} else if (exprAnalyzer.isLinear()) {
				matrix.add(F.eval(exprAnalyzer.getRow()));
				vector.add(F.eval(F.Negate(exprAnalyzer.getValue())));
			} else {
				throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
			}
			currEquation++;
		}
		return resultList;
	}

	/**
	 * Evaluate the roots of a univariate polynomial with the Roots[] function.
	 * 
	 * @param exprAnalyzer
	 * @param vars
	 * @return
	 */
	private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer) {
		IExpr expr = exprAnalyzer.getNumerator();
		IExpr denom = exprAnalyzer.getDenominator();
		// try to solve the expr for a symbol in the symbol set
		for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
			IExpr temp = Roots.rootsOfVariable(expr, denom, F.List(sym), expr.isNumericMode());
			if (temp != null) {
				IAST resultList = F.List();
				if (temp.isASTSizeGE(F.List, 2)) {
					IAST rootsList = (IAST) temp;
					for (IExpr root : rootsList) {
						IAST rule = F.Rule(sym, root);
						resultList.add(rule);
					}
					return resultList;
				}
				return null;
			}
		}
		return null;
	}

	public Solve() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);

		ExprAnalyzer exprAnalyzer;
		ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();
		// collect linear and univariate polynomial equations:
		for (IExpr expr : termsEqualZeroList) {
			exprAnalyzer = new ExprAnalyzer(expr, vars);
			exprAnalyzer.simplifyAndAnalyze();
			analyzerList.add(exprAnalyzer);
		}
		IAST matrix = F.List();
		IAST vector = F.List();
		try {
			IAST resultList = F.List();
			resultList = analyzeSublist(analyzerList, vars, resultList, matrix, vector);

			if (vector.size() > 1) {
				// solve a linear equation <code>matrix.x == vector</code>
				IExpr temp = F.eval(F.LinearSolve(matrix, vector));
				if (temp.isASTSizeGE(F.List, 2)) {
					IAST rootsList = (IAST) temp;
					IAST list = F.List();
					for (int j = 1; j < vars.size(); j++) {
						IAST rule = F.Rule(vars.get(j), rootsList.get(j));
						list.add(rule);
					}
					resultList.add(list);
				} else {
					return null;
				}
			}

			return resultList;
		} catch (NoSolution e) {
			// e.printStackTrace();
			if (e.getType() == NoSolution.WRONG_SOLUTION) {
				return F.List();
			}
			return null;
		}
	}
}
