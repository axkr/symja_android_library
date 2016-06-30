package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.math4.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.function.PossibleZeroQ;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions).
 */
public class Solve extends AbstractFunctionEvaluator {

	/**
	 * Analyze an expression, if it has linear, polynomial or other form.
	 * 
	 */
	protected static class ExprAnalyzer implements Comparable<ExprAnalyzer> {

		public static final int LINEAR = 0;
		public static final int OTHERS = 2;
		public static final int POLYNOMIAL = 1;

		private int fEquationType;
		private IExpr fExpr;
		private IExpr fNumer;
		private IExpr fDenom;
		private long fLeafCount;

		private HashSet<ISymbol> fSymbolSet;
		private IAST fMatrixRow;
		private IAST fPlusAST;

		final IAST vars;
		final EvalEngine engine;

		public ExprAnalyzer(IExpr expr, IAST vars, EvalEngine engine) {
			super();
			this.engine = engine;
			this.fExpr = expr;
			this.fNumer = expr;
			this.fDenom = F.C1;
			if (this.fExpr.isAST()) {
				splitNumeratorDenominator((IAST) this.fExpr);
			}
			this.vars = vars;
			this.fSymbolSet = new HashSet<ISymbol>();
			this.fLeafCount = 0;
			reset();
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
				return (fSymbolSet.size() < o.fSymbolSet.size()) ? -1 : 1;
			}
			if (fEquationType != o.fEquationType) {
				return (fEquationType < o.fEquationType) ? -1 : 1;
			}
			if (fLeafCount != o.fLeafCount) {
				return (fLeafCount < o.fLeafCount) ? -1 : 1;
			}
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ExprAnalyzer other = (ExprAnalyzer) obj;
			if (fDenom == null) {
				if (other.fDenom != null)
					return false;
			} else if (!fDenom.equals(other.fDenom))
				return false;
			if (fEquationType != other.fEquationType)
				return false;
			if (fExpr == null) {
				if (other.fExpr != null)
					return false;
			} else if (!fExpr.equals(other.fExpr))
				return false;
			if (fLeafCount != other.fLeafCount)
				return false;
			if (fMatrixRow == null) {
				if (other.fMatrixRow != null)
					return false;
			} else if (!fMatrixRow.equals(other.fMatrixRow))
				return false;
			if (fNumer == null) {
				if (other.fNumer != null)
					return false;
			} else if (!fNumer.equals(other.fNumer))
				return false;
			if (fPlusAST == null) {
				if (other.fPlusAST != null)
					return false;
			} else if (!fPlusAST.equals(other.fPlusAST))
				return false;
			if (fSymbolSet == null) {
				if (other.fSymbolSet != null)
					return false;
			} else if (!fSymbolSet.equals(other.fSymbolSet))
				return false;
			if (vars == null) {
				if (other.vars != null)
					return false;
			} else if (!vars.equals(other.vars))
				return false;
			return true;
		}

		public IExpr getDenominator() {
			return fDenom;
		}

		/**
		 * @return the expr
		 */
		public IExpr getExpr() {
			return fExpr;
		}

		public int getNumberOfVars() {
			return fSymbolSet.size();
		}

		public IExpr getNumerator() {
			return fNumer;
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
				if (fEquationType == LINEAR && sym == null) {
					// should never happen??
					System.err.println("sym == null???");
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
		public Set<ISymbol> getSymbolSet() {
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fDenom == null) ? 0 : fDenom.hashCode());
			result = prime * result + fEquationType;
			result = prime * result + ((fExpr == null) ? 0 : fExpr.hashCode());
			result = prime * result + (int) (fLeafCount ^ (fLeafCount >>> 32));
			result = prime * result + ((fMatrixRow == null) ? 0 : fMatrixRow.hashCode());
			result = prime * result + ((fNumer == null) ? 0 : fNumer.hashCode());
			result = prime * result + ((fPlusAST == null) ? 0 : fPlusAST.hashCode());
			result = prime * result + ((fSymbolSet == null) ? 0 : fSymbolSet.hashCode());
			result = prime * result + ((vars == null) ? 0 : vars.hashCode());
			return result;
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

		/**
		 * Check for an applicable inverse function at the given
		 * <code>position</code> in the <code>Plus(..., ,...)</code> expression.
		 * 
		 * @param ast
		 * @param arg1
		 * @return
		 */
		private IExpr rewriteInverseFunction(IAST ast, IExpr arg1) {
			if (ast.isAbs()) {
				return engine.evaluate(
						F.Expand(F.Times(F.Subtract(ast.arg1(), F.Times(F.CN1, arg1)), F.Subtract(ast.arg1(), arg1))));
			} else if (ast.isAST1()) {
				IAST inverseFunction = InverseFunction.getUnaryInverseFunction(ast);
				if (inverseFunction.isPresent()) {
					engine.printMessage("Solve: using of inverse functions may omit some solutions.");
					// rewrite fNumer
					inverseFunction.add(arg1);
					return engine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
				}

			} else if (ast.isPower() && ast.arg1().isSymbol() && ast.arg2().isNumber()) {
				int position = vars.findFirstEquals(ast.arg1());
				if (position > 0) {
					engine.printMessage("Solve: using of inverse functions may omit some solutions.");
					IAST inverseFunction = F.Power(arg1, ast.arg2().inverse());
					return engine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
				}

			}
			return F.NIL;
		}

		/**
		 * Check for an applicable inverse function at the given
		 * <code>position</code> in the <code>Plus(..., ,...)</code> expression.
		 * 
		 * @param plusAST
		 *            the <code>Plus(..., ,...)</code> expression
		 * @param position
		 * @return <code>F.NIL</code> if no inverse function was found,
		 *         otherwise return the rewritten expression
		 */
		private IExpr rewriteInverseFunction(IAST plusAST, int position) {
			IAST ast = (IAST) plusAST.get(position);
			IExpr plus = plusAST.removeAtClone(position).getOneIdentity(F.C0);
			if (plus.isFree(Predicates.in(vars), true)) {
				return rewriteInverseFunction(ast, F.Negate(plus));
			}
			return F.NIL;
		}

		/**
		 * Try to rewrite a <code>Plus(...,f(x), ...)</code> function which
		 * contains an invertable function argument <code>f(x)</code>.
		 */
		private IExpr rewritePlusWithInverseFunctions(IAST plusAST) {
			IExpr expr;
			for (int i = 1; i < plusAST.size(); i++) {
				expr = plusAST.get(i);
				if (expr.isFree(Predicates.in(vars), true)) {
					continue;
				}

				if (expr.isAST()) {
					IAST function = (IAST) expr;
					IAST inverseFunction = InverseFunction.getUnaryInverseFunction(function);
					if (inverseFunction.isPresent()) {
						IExpr temp = rewriteInverseFunction(plusAST, i);
						if (temp.isPresent()) {
							return temp;
						}
					} else if (function.isPower() && function.arg2().isFraction()) {
						// issue #95
						IFraction arg2 = (IFraction) function.arg2();
						IExpr plus = plusAST.removeAtClone(i).getOneIdentity(F.C0);
						if (plus.isPositiveResult()) {
							// no solution possible
							return NO_EQUATION_SOLUTION;
						}
						return engine.evaluate(
								F.Subtract(F.Expand(F.Power(F.Negate(plus), arg2.inverse())), function.arg1()));

					}
				}

			}
			return F.NIL;
		}

		/**
		 * Try to rewrite a <code>Times(...,f(x), ...)</code> expression which
		 * may contain an invertable function argument <code>f(x)</code> as
		 * subexpression.
		 */
		private IExpr rewriteTimesWithInverseFunctions(IAST times) {
			IAST result = F.NIL;
			int j = 1;
			// remove constant sub-expressions from Times() expression
			for (int i = 1; i < times.size(); i++) {
				if (times.get(i).isFree(Predicates.in(vars), true) && times.get(i).isNumericFunction()) {
					if (!result.isPresent()) {
						result = times.clone();
					}
					result.remove(j);
					continue;
				}
				j++;
			}
			if (!result.isPresent()) {
				return rewriteInverseFunction(times, F.C0);
			}
			IExpr temp0 = result.getOneIdentity(F.C1);
			if (temp0.isAST()) {
				return rewriteInverseFunction((IAST) temp0, F.C0).orElse(temp0);
			}
			return temp0;
		}

		/**
		 * If possible simplify the numerator expression. After that analyze the
		 * numerator expression, if it has linear, polynomial or other form.
		 */
		protected void simplifyAndAnalyze() {
			IExpr temp = F.NIL;
			if (fNumer.isPlus()) {
				temp = rewritePlusWithInverseFunctions((IAST) fNumer);
			} else if (fNumer.isTimes() && !fNumer.isFree(Predicates.in(vars), true)) {
				temp = rewriteTimesWithInverseFunctions((IAST) fNumer);
			} else if (fNumer.isAST() && !fNumer.isFree(Predicates.in(vars), true)) {
				temp = rewriteInverseFunction((IAST) fNumer, F.C0);
			}
			if (temp.isPresent()) {
				if (temp.isAST() && fDenom.isOne()) {
					splitNumeratorDenominator((IAST) temp);
				} else {
					fNumer = temp;
				}
			}

			analyze(fNumer);
		}

		private void splitNumeratorDenominator(IAST expr) {
			this.fExpr = Together.together(expr);
			// split expr into numerator and denominator
			this.fDenom = engine.evaluate(F.Denominator(this.fExpr));
			if (!this.fDenom.isOne()) {
				// search roots for the numerator expression
				this.fNumer = engine.evaluate(F.Numerator(this.fExpr));
			} else {
				this.fNumer = this.fExpr;
			}
		}

	}

	/**
	 * Check an expression, if it's an allowed object.
	 *
	 */
	protected static final class IsWrongSolveExpression implements Predicate<IExpr> {
		IExpr wrongExpr;

		public IsWrongSolveExpression() {
			wrongExpr = null;
		}

		public IExpr getWrongExpr() {
			return wrongExpr;
		}

		@Override
		public boolean test(IExpr input) {
			if (input.isDirectedInfinity() || input.isIndeterminate()) {
				// input is representing a DirectedInfinity() or Indeterminate
				// object
				wrongExpr = input;
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("serial")
	protected static class NoSolution extends Exception {
		/**
		 * Solution couldn't be found.
		 */
		public static final int NO_SOLUTION_FOUND = 1;

		/**
		 * Definitely wrong solution.
		 */
		public static final int WRONG_SOLUTION = 0;

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
	 * Use <code>-1</code> as an equation expression for which we get no
	 * solution.
	 */
	private static IExpr NO_EQUATION_SOLUTION = F.CN1;

	/**
	 * 
	 * @param analyzerList
	 * @param variables
	 *            the list of variables
	 * @param resultList
	 *            the list of result values as rules assigned to each variable
	 * @param maximumNumberOfResults
	 *            the maximum number of results in <code>resultList</code>:
	 *            <code>0</code> gives all results.
	 * @param matrix
	 * @param vector
	 * @return
	 */
	protected static IAST analyzeSublist(ArrayList<ExprAnalyzer> analyzerList, IAST variables, IAST resultList,
			int maximumNumberOfResults, IAST matrix, IAST vector, EvalEngine engine) throws NoSolution {
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
					if (!PossibleZeroQ.possibleZeroQ(expr, engine)) {
						throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
					}
				}
			} else if (exprAnalyzer.getNumberOfVars() == 1 && exprAnalyzer.isLinearOrPolynomial()) {
				IAST listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer, engine);
				if (listOfRules.isPresent()) {
					boolean evaled = false;
					++currEquation;
					for (int k = 1; k < listOfRules.size(); k++) {
						if (currEquation >= analyzerList.size()) {
							resultList.add(F.List(listOfRules.getAST(k)));
							if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
								return resultList;
							}
							evaled = true;
						} else {

							ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
							// collect linear and univariate polynomial
							// equations:
							for (int i = currEquation; i < analyzerList.size(); i++) {
								IExpr expr = analyzerList.get(i).getExpr();
								IExpr temp = expr.replaceAll(listOfRules.getAST(k));
								if (temp.isPresent()) {
									expr = engine.evaluate(temp);
									exprAnalyzer = new ExprAnalyzer(expr, variables, engine);
									exprAnalyzer.simplifyAndAnalyze();
								} else {
									// reuse old analyzer; expression hasn't
									// changed
									exprAnalyzer = analyzerList.get(i);
								}
								subAnalyzerList.add(exprAnalyzer);
							}
							try {
								IAST subResultList = analyzeSublist(subAnalyzerList, variables, F.List(),
										maximumNumberOfResults, matrix, vector, engine);
								if (subResultList.isPresent()) {
									evaled = true;
									for (IExpr expr : subResultList) {
										if (expr.isList()) {
											IAST list = (IAST) expr;
											list.add(1, listOfRules.getAST(k));
											resultList.add(list);
											if (maximumNumberOfResults > 0
													&& maximumNumberOfResults <= resultList.size()) {
												return resultList;
											}
										} else {
											resultList.add(expr);
											if (maximumNumberOfResults > 0
													&& maximumNumberOfResults <= resultList.size()) {
												return resultList;
											}
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
				matrix.add(engine.evaluate(exprAnalyzer.getRow()));
				vector.add(engine.evaluate(F.Negate(exprAnalyzer.getValue())));
			} else {
				throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
			}
			currEquation++;
		}
		return resultList;
	}

	/**
	 * Solve boolean expressions.
	 * 
	 * @param expr
	 * @param variables
	 * @param maximumNumberOfResults
	 * @param position
	 * @param resultList
	 */
	protected static void booleansSolve(IExpr expr, IAST variables, int maximumNumberOfResults, int position,
			IAST resultList) {
		if (maximumNumberOfResults > 0 && maximumNumberOfResults < resultList.size()) {
			return;
		}
		if (variables.size() <= position) {
			if (EvalEngine.get().evalTrue(expr)) {
				IAST list = F.List();
				for (int i = 1; i < variables.size(); i++) {
					ISymbol sym = (ISymbol) variables.get(i);
					list.add(F.Rule(sym, sym.get()));
				}
				resultList.add(list);

			}
			return;
		}
		IExpr sym = variables.get(position);
		if (sym.isSymbol()) {
			try {
				((ISymbol) sym).pushLocalVariable(F.False);
				booleansSolve(expr, variables, maximumNumberOfResults, position + 1, resultList);
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
			try {
				((ISymbol) sym).pushLocalVariable(F.True);
				booleansSolve(expr, variables, maximumNumberOfResults, position + 1, resultList);
			} finally {
				((ISymbol) sym).popLocalVariable();
			}
		}
	}

	/**
	 * Evaluate the roots of a univariate polynomial with the Roots[] function.
	 * 
	 * @param exprAnalyzer
	 * @param vars
	 * @return
	 */
	private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
		IExpr expr = exprAnalyzer.getNumerator();
		IExpr denom = exprAnalyzer.getDenominator();
		// try to solve the expr for a symbol in the symbol set
		for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
			IExpr temp = Roots.rootsOfVariable(expr, denom, F.List(sym), expr.isNumericMode(), engine);
			if (temp.isPresent()) {
				IAST resultList = F.List();
				if (temp.isASTSizeGE(F.List, 2)) {
					IAST rootsList = (IAST) temp;
					for (IExpr root : rootsList) {
						IAST rule = F.Rule(sym, root);
						resultList.add(rule);
					}
					return resultList;
				}
				return F.NIL;
			}
		}
		return F.NIL;
	}

	protected static IAST sortResults(IAST resultList) {
		for (int i = 1; i < resultList.size(); i++) {
			if (resultList.get(i).isList()) {
				EvalAttributes.sort((IAST) resultList.get(i));
			}
		}
		return resultList;
	}

	/**
	 * Return an immutable <code>List[numerator, denominator]</code> of the
	 * given expression.
	 * 
	 * @param expr
	 * @param engine
	 * @param evalTogether
	 *            evaluate <code>Together[expr]</code> before determining
	 *            numerator and denominator of the expression.
	 * @return <code>List[numerator, denominator]</code>
	 */
	private static IAST splitNumeratorDenominator(IAST expr, EvalEngine engine, boolean evalTogether) {
		IExpr a0, a1;
		if (evalTogether) {
			a0 = Together.together(expr);
		} else {
			a0 = expr;
		}
		// split expr into numerator and denominator
		a1 = engine.evaluate(F.Denominator(a0));
		if (!a1.isOne()) {
			// search roots for the numerator expression
			a0 = engine.evaluate(F.Numerator(a0));
		} else {
			a0 = expr;
		}
		return F.binaryAST2(F.List, a0, a1);
	}

	public Solve() {
		// empty constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);
		IAST variables = Validate.checkSymbolOrSymbolList(ast, 2);
		if (ast.isAST3()) {
			if (ast.arg3().equals(F.Booleans)) {
				IAST resultList = F.List();
				booleansSolve(ast.arg1(), variables, 0, 1, resultList);
				return resultList;
			}
			throw new WrongArgumentType(ast, ast.arg3(), 3, "Booleans expected!");
		}
		IAST termsEqualZeroList = Validate.checkEquations(ast, 1);

		IAST temp = solveTimesEquationsRecursively(termsEqualZeroList, variables, engine);
		if (temp.isPresent()) {
			return temp;
		}
		return solveEquations(termsEqualZeroList, variables, 0, engine);
	}

	protected IAST solveEquations(IAST termsEqualZeroList, IAST variables, int maximumNumberOfResults,
			EvalEngine engine) {
		try {
			IAST list = GroebnerBasis.solveGroebnerBasis(termsEqualZeroList, variables);
			if (list.isPresent()) {
				termsEqualZeroList = list;
			}
		} catch (JASConversionException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		ExprAnalyzer exprAnalyzer;
		ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();
		IsWrongSolveExpression predicate = new IsWrongSolveExpression();
		// collect linear and univariate polynomial equations:
		for (IExpr expr : termsEqualZeroList) {
			if (expr.isMember(predicate, true)) {
				engine.printMessage(
						"Solve: the system contains the wrong object: " + predicate.getWrongExpr().toString());
				return F.NIL;
			}
			exprAnalyzer = new ExprAnalyzer(expr, variables, engine);
			exprAnalyzer.simplifyAndAnalyze();
			analyzerList.add(exprAnalyzer);
		}
		IAST matrix = F.List();
		IAST vector = F.List();
		try {
			IAST resultList = F.List();
			resultList = analyzeSublist(analyzerList, variables, resultList, maximumNumberOfResults, matrix, vector,
					engine);
			if (vector.size() > 1) {
				// solve a linear equation <code>matrix.x == vector</code>
				FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix(matrix, vector);
				return RowReduce.rowReduced2RulesList(augmentedMatrix, variables, resultList, engine);
			}

			return sortResults(resultList);
		} catch (NoSolution e) {
			if (e.getType() == NoSolution.WRONG_SOLUTION) {
				return F.List();
			}
			return F.NIL;
		}
	}

	/**
	 * Analyze the <code>termsEqualZeroList</code> if it contains a
	 * <code>Times[..., ,...]</code> expression. If true, set the factors equal
	 * to <code>0</code> and solve the equations recursively.
	 * 
	 * @param termsEqualZeroList
	 *            the list of equations which should be <code>0</code>.
	 * @param variables
	 *            the variables for which the equations should be solved
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	private IAST solveTimesEquationsRecursively(IAST termsEqualZeroList, IAST variables, EvalEngine engine) {
		for (int i = 1; i < termsEqualZeroList.size(); i++) {
			if (termsEqualZeroList.get(i).isTimes()) {
				IAST times = (IAST) termsEqualZeroList.get(i);
				IAST splittedList = splitNumeratorDenominator(times, engine, false);
				if (splittedList.arg2().isFree(Predicates.in(variables), true)) {
					Set<IExpr> subSolutionSet = new HashSet<IExpr>();
					for (int j = 1; j < times.size(); j++) {
						if (!times.get(j).isFree(Predicates.in(variables), true)) {
							IAST clonedEqualZeroList = termsEqualZeroList.clone();
							clonedEqualZeroList.set(i, times.get(j));
							IAST temp = solveEquations(clonedEqualZeroList, variables, 0, engine);
							if (temp.size() > 1) {
								subSolutionSet.addAll(temp); 
							}
						}
					}

					if (subSolutionSet.size() > 0) {
						IAST list = F.List();
						list.addAll(subSolutionSet);
						return list;
					}
				}
			}
		}
		return F.NIL;
	}
}
