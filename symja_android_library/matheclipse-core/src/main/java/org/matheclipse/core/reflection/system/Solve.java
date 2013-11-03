package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.matheclipse.core.builtin.function.LeafCount;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
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
		final static public int POLYNOMIAL = 1;

		private int fEquationType;
		private IExpr fExpr;
		private IExpr fNumer;
		private IExpr fDenom;
		private int fLeafCount;

		IAST row;
		HashSet<ISymbol> symbolSet;
		IAST value;

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
			this.symbolSet = new HashSet<ISymbol>();
			this.fLeafCount = 0;
			reset();
		}

		public void analyze() {
			analyze(getNumerator());
		}

		/**
		 * Analyze an expression, if it has linear, polynomial or other form.
		 * 
		 */
		private void analyze(IExpr eqExpr) {
			if (eqExpr.isFree(Predicates.in(vars), true)) {
				fLeafCount++;
				value.add(eqExpr);
			} else if (eqExpr.isPlus()) {
				fLeafCount++;
				IAST arg = (IAST) eqExpr;
				IExpr expr;
				for (int i = 1; i < arg.size(); i++) {
					expr = arg.get(i);
					if (expr.isFree(Predicates.in(vars), true)) {
						fLeafCount++;
						value.add(expr);
					} else {
						getPlusEquationType(expr);
					}
				}
			} else {
				getPlusEquationType(eqExpr);
			}
		}

		@Override
		public int compareTo(ExprAnalyzer o) {
			if (symbolSet.size() != o.symbolSet.size()) {
				if (symbolSet.size() < o.symbolSet.size()) {
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
			return symbolSet.size();
		}

		private void getPlusEquationType(IExpr eqExpr) {
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
								symbolSet.add((ISymbol) expr);
								if (sym != null) {
									if (fEquationType == LINEAR) {
										fEquationType = POLYNOMIAL;
									}
								} else {
									sym = (ISymbol) expr;
									if (fEquationType == LINEAR) {
										IAST cloned = arg.clone();
										cloned.remove(i);
										row.set(j, F.Plus(row.get(j), cloned));
									}
								}
							}
						}
					} else if (expr.isPower() && (expr.getAt(2).isInteger() || expr.getAt(2).isNumIntValue())) {
						// (JASConvert.getExponent((IAST) expr) > 0)) {
						if (fEquationType == LINEAR) {
							fEquationType = POLYNOMIAL;
						}
						getTimesEquationType(((IAST) expr).arg1());
					} else {
						fLeafCount += LeafCount.leafCount(eqExpr);
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
				getTimesEquationType(eqExpr);
			}
		}

		/**
		 * @return the row
		 */
		public IAST getRow() {
			return row;
		}

		/**
		 * @return the symbolSet
		 */
		public HashSet<ISymbol> getSymbolSet() {
			return symbolSet;
		}

		private void getTimesEquationType(IExpr expr) {
			if (expr.isSymbol()) {
				fLeafCount++;
				for (int i = 1; i < vars.size(); i++) {
					if (vars.get(i).equals(expr)) {
						symbolSet.add((ISymbol) expr);
						if (fEquationType == LINEAR) {
							row.set(i, F.Plus(row.get(i), F.C1));
						}
					}
				}
				return;
			}
			if (expr.isFree(Predicates.in(vars), true)) {
				fLeafCount++;
				value.add(expr);
				return;
			}
			if (expr.isPower()) {
				if (((IAST) expr).arg2().isInteger()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesEquationType(((IAST) expr).arg1());
					return;
				}
				if (((IAST) expr).arg2().isNumIntValue()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesEquationType(((IAST) expr).arg1());
					return;
				}
			}
			fLeafCount += LeafCount.leafCount(expr);
			if (fEquationType <= POLYNOMIAL) {
				fEquationType = OTHERS;
			}

		}

		/**
		 * @return the value
		 */
		public IAST getValue() {
			return value;
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
			this.row = F.List();
			for (int i = 1; i < vars.size(); i++) {
				row.add(F.C0);
			}
			this.value = F.Plus();
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
									exprAnalyzer.analyze();
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
		// F.C1;
		// if (expr.isAST()) {
		// expr = Together.together((IAST) expr);
		//
		// // split expr into numerator and denominator
		// denom = F.eval(F.Denominator(expr));
		// if (!denom.isOne()) {
		// // search roots for the numerator expression
		// expr = F.eval(F.Numerator(expr));
		// }
		// }

		// try to solve the expr for a symbol in the symbol set
		for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
			IExpr temp = Roots.rootsOfVariable(expr, denom, F.List(sym), false);
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

	/**
	 * Check if the argument at the given position is an equation (i.e. Equal[a,b]) or a list of equations and return a list of
	 * expressions, which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	private IAST checkEquations(final IAST ast, int position) {
		IAST termsEqualZeroList = F.List();
		IAST eqns = null;
		IAST eq;
		if (ast.get(position).isList()) {
			eqns = (IAST) ast.get(position);
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST(F.Equal, 3)) {
					eq = (IAST) eqns.get(i);
					termsEqualZeroList.add(F.evalExpandAll(F.Subtract(eq.arg1(), eq.arg2())));
				} else {
					// not an equation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equal[] expression (a==b) expected");
				}
			}
		} else {
			if (ast.get(position).isAST(F.Equal, 3)) {
				eq = (IAST) ast.get(position);
				termsEqualZeroList.add(F.evalExpandAll(F.Subtract(eq.arg1(), eq.arg2())));
			} else {
				// not an equation
				throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal[] expression (a==b) expected");
			}
		}
		return termsEqualZeroList;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);
		IAST termsEqualZeroList = checkEquations(ast, 1);

		ExprAnalyzer exprAnalyzer;
		ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();
		// collect linear and univariate polynomial equations:
		for (IExpr expr : termsEqualZeroList) {
			exprAnalyzer = new ExprAnalyzer(expr, vars);
			exprAnalyzer.analyze();
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
			if (e.getType() == NoSolution.WRONG_SOLUTION) {
				return F.List();
			}
			return null;
		}
	}
}
