package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.CreamConvert;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * Solve(equations, vars)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Solve({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;2,y-&gt;2},{x-&gt;2,y-&gt;-2},{x-&gt;-2,y-&gt;2*2^(1/2)},{x-&gt;-2,y-&gt;(-2)*2^(1/2)}}
 * </pre>
 * 
 * <h3>Related terms</h3>
 * <p>
 * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>, <a href="GroebnerBasis.md">GroebnerBasis</a>,
 * <a href="FindRoot.md">FindRoot</a>, <a href="NRoots.md">NRoots</a>
 * </p>
 */
public class Solve extends AbstractFunctionEvaluator {

	/**
	 * Analyze an expression, if it has linear, polynomial or other form.
	 * 
	 */
	protected static class ExprAnalyzer implements Comparable<ExprAnalyzer> {

		/**
		 * A linear expression for the given variables.
		 */
		public static final int LINEAR = 0;
		/**
		 * A polynomial expression for the given variables.
		 */
		public static final int POLYNOMIAL = 1;
		/**
		 * Others type of expression for the given variables.
		 */
		public static final int OTHERS = 2;

		/**
		 * LINEAR, POLYNOMIAL or OTHERS
		 */
		private int fEquationType;

		/**
		 * The expression which should be <code>0</code>.
		 */
		private IExpr fExpr;

		/**
		 * The numerator of the expression
		 */
		private IExpr fNumerator;

		/**
		 * The denominator of the expression
		 */
		private IExpr fDenominator;

		/**
		 * The number of leaves in an expression, used as an indicator for the complexity of the expression.
		 */
		private long fLeafCount;

		private HashSet<ISymbol> fSymbolSet;
		private IASTAppendable fMatrixRow;
		private IASTAppendable fPlusAST;

		final IAST fListOfVariables;
		final EvalEngine fEngine;

		public ExprAnalyzer(IExpr expr, IAST listOfVariables, EvalEngine engine) {
			super();
			this.fEngine = engine;
			this.fExpr = expr;
			this.fNumerator = expr;
			this.fDenominator = F.C1;
			if (this.fExpr.isAST()) {
				splitNumeratorDenominator((IAST) this.fExpr);
			}
			this.fListOfVariables = listOfVariables;
			this.fSymbolSet = new HashSet<ISymbol>();
			this.fLeafCount = 0;
			reset();
		}

		/**
		 * Analyze an expression, if it has linear, polynomial or other form.
		 * 
		 */
		private void analyze(IExpr eqExpr) {
			if (eqExpr.isFree(Predicates.in(fListOfVariables), true)) {
				fLeafCount++;
				fPlusAST.append(eqExpr);
			} else if (eqExpr.isPlus()) {
				fLeafCount++;
				IAST plusAST = (IAST) eqExpr;
				IExpr expr;
				for (int i = 1; i < plusAST.size(); i++) {
					expr = plusAST.get(i);
					if (expr.isFree(Predicates.in(fListOfVariables), true)) {
						fLeafCount++;
						fPlusAST.append(expr);
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
			if (fDenominator == null) {
				if (other.fDenominator != null)
					return false;
			} else if (!fDenominator.equals(other.fDenominator))
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
			if (fNumerator == null) {
				if (other.fNumerator != null)
					return false;
			} else if (!fNumerator.equals(other.fNumerator))
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
			if (fListOfVariables == null) {
				if (other.fListOfVariables != null)
					return false;
			} else if (!fListOfVariables.equals(other.fListOfVariables))
				return false;
			return true;
		}

		public IExpr getDenominator() {
			return fDenominator;
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
			return fNumerator;
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
					if (expr.isFree(Predicates.in(fListOfVariables), true)) {
						fLeafCount++;
					} else if (expr.isSymbol()) {
						fLeafCount++;
						for (int j = 1; j < fListOfVariables.size(); j++) {
							if (fListOfVariables.get(j).equals(expr)) {
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
				int position = fListOfVariables.findFirstEquals(expr);
				if (position > 0) {
					fSymbolSet.add((ISymbol) expr);
					if (fEquationType == LINEAR) {
						fMatrixRow.set(position, F.Plus(fMatrixRow.get(position), F.C1));
					}
				}
				return;
			}
			if (expr.isFree(Predicates.in(fListOfVariables), true)) {
				fLeafCount++;
				fPlusAST.append(expr);
				return;
			}
			if (expr.isPower()) {
				IExpr arg1 = ((IAST) expr).arg1();
				IExpr exponent = ((IAST) expr).arg2();
				if (exponent.isInteger()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesArgumentEquationType(arg1);
					return;
				}
				if (exponent.isNumIntValue()) {
					if (fEquationType == LINEAR) {
						fEquationType = POLYNOMIAL;
					}
					getTimesArgumentEquationType(arg1);
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
			result = prime * result + ((fDenominator == null) ? 0 : fDenominator.hashCode());
			result = prime * result + fEquationType;
			result = prime * result + ((fExpr == null) ? 0 : fExpr.hashCode());
			result = prime * result + (int) (fLeafCount ^ (fLeafCount >>> 32));
			result = prime * result + ((fMatrixRow == null) ? 0 : fMatrixRow.hashCode());
			result = prime * result + ((fNumerator == null) ? 0 : fNumerator.hashCode());
			result = prime * result + ((fPlusAST == null) ? 0 : fPlusAST.hashCode());
			result = prime * result + ((fSymbolSet == null) ? 0 : fSymbolSet.hashCode());
			result = prime * result + ((fListOfVariables == null) ? 0 : fListOfVariables.hashCode());
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
			int size = fListOfVariables.size();
			this.fMatrixRow = F.ListAlloc(size);
			for (int i = 1; i < size; i++) {
				fMatrixRow.append(F.C0);
			}
			this.fPlusAST = F.PlusAlloc(8);
			this.fEquationType = LINEAR;
		}

		/**
		 * Check for an applicable inverse function at the given <code>position</code> in the
		 * <code>Plus(..., ,...)</code> expression.
		 * 
		 * @param ast
		 * @param arg1
		 * @return
		 */
		private IExpr rewriteInverseFunction(IAST ast, IExpr arg1) {
			if (ast.isAbs()) {
				return fEngine.evaluate(
						F.Expand(F.Times(F.Subtract(ast.arg1(), F.Times(F.CN1, arg1)), F.Subtract(ast.arg1(), arg1))));
			} else if (ast.isAST1()) {
				IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast);
				if (inverseFunction.isPresent()) {
					fEngine.printMessage("Solve: using of inverse functions may omit some solutions.");
					// rewrite fNumer
					inverseFunction.append(arg1);
					return fEngine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
				}

			} else if (ast.isPower() && ast.arg1().isSymbol() && ast.arg2().isNumber()) {
				int position = fListOfVariables.findFirstEquals(ast.arg1());
				if (position > 0) {
					fEngine.printMessage("Solve: using of inverse functions may omit some solutions.");
					IAST inverseFunction = F.Power(arg1, ast.arg2().inverse());
					return fEngine.evaluate(F.Subtract(ast.arg1(), inverseFunction));
				}

			}
			return F.NIL;
		}

		/**
		 * Check for an applicable inverse function at the given <code>position</code> in the
		 * <code>Plus(..., ,...)</code> expression.
		 * 
		 * @param plusAST
		 *            the <code>Plus(..., ,...)</code> expression
		 * @param position
		 * @return <code>F.NIL</code> if no inverse function was found, otherwise return the rewritten expression
		 */
		private IExpr rewriteInverseFunction(IAST plusAST, int position) {
			IAST ast = (IAST) plusAST.get(position);
			IExpr plus = plusAST.removeAtClone(position).getOneIdentity(F.C0);
			if (plus.isFree(Predicates.in(fListOfVariables), true)) {
				return rewriteInverseFunction(ast, F.Negate(plus));
			}
			return F.NIL;
		}

		/**
		 * Try to rewrite a <code>Plus(...,f(x), ...)</code> function which contains an invertable function argument
		 * <code>f(x)</code>.
		 */
		private IExpr rewritePlusWithInverseFunctions(IAST plusAST) {
			IExpr expr;
			for (int i = 1; i < plusAST.size(); i++) {
				expr = plusAST.get(i);
				if (expr.isFree(Predicates.in(fListOfVariables), true)) {
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
					} else if (function.isPower()) {
						if (function.arg2().isFraction()
								|| (function.arg2().isRealNumber() && !function.arg2().isNumIntValue())) {
							ISignedNumber arg2 = (ISignedNumber) function.arg2();
							IExpr plus = plusAST.removeAtClone(i).getOneIdentity(F.C0);
							if (plus.isPositiveResult()) {
								// no solution possible
								return NO_EQUATION_SOLUTION;
							}
							return fEngine.evaluate(
									F.Subtract(F.Expand(F.Power(F.Negate(plus), arg2.inverse())), function.arg1()));
						}

					}
				}

			}
			return F.NIL;
		}

		/**
		 * Try to rewrite a <code>Times(...,f(x), ...)</code> expression which may contain an invertable function
		 * argument <code>f(x)</code> as subexpression.
		 */
		private IExpr rewriteTimesWithInverseFunctions(IAST times) {
			IASTAppendable result = F.NIL;
			int j = 1;
			// remove constant sub-expressions from Times() expression
			for (int i = 1; i < times.size(); i++) {
				if (times.get(i).isFree(Predicates.in(fListOfVariables), true) && times.get(i).isNumericFunction()) {
					if (!result.isPresent()) {
						result = times.copyAppendable();
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
		 * If possible simplify the numerator expression. After that analyze the numerator expression, if it has linear,
		 * polynomial or other form.
		 */
		protected void simplifyAndAnalyze() {
			IExpr temp = F.NIL;
			if (fNumerator.isPlus()) {
				temp = rewritePlusWithInverseFunctions((IAST) fNumerator);
			} else if (fNumerator.isTimes() && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
				temp = rewriteTimesWithInverseFunctions((IAST) fNumerator);
			} else if (fNumerator.isAST() && !fNumerator.isFree(Predicates.in(fListOfVariables), true)) {
				temp = rewriteInverseFunction((IAST) fNumerator, F.C0);
			}
			if (temp.isPresent()) {
				if (temp.isAST() && fDenominator.isOne()) {
					splitNumeratorDenominator((IAST) temp);
				} else {
					fNumerator = temp;
				}
			}

			analyze(fNumerator);
		}

		public void splitNumeratorDenominator(IAST ast) {
			IExpr[] result = Algebra.getNumeratorDenominator(ast, fEngine);
			this.fNumerator = result[0];
			this.fDenominator = result[1];
			this.fExpr = result[2];
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
	 * Use <code>-1</code> as an equation expression for which we get no solution (i.e. <code>(-1)==0  =>  False</code>)
	 */
	private static IExpr NO_EQUATION_SOLUTION = F.CN1;

	/**
	 * Recursively solve the list of analyzers.
	 * 
	 * @param analyzerList
	 *            list of analyzers, which determine, if an expression has linear, polynomial or other form
	 * @param variables
	 *            the list of variables
	 * @param resultList
	 *            the list of result values as rules assigned to each variable
	 * @param maximumNumberOfResults
	 *            the maximum number of results in <code>resultList</code>: <code>0</code> gives all results.
	 * @param matrix
	 * @param vector
	 * @param engine
	 * @return throws NoSolution
	 */
	protected static IASTAppendable analyzeSublist(ArrayList<ExprAnalyzer> analyzerList, IAST variables,
			IASTAppendable resultList, int maximumNumberOfResults, IASTAppendable matrix, IASTAppendable vector,
			EvalEngine engine) throws NoSolution {
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
					if (!F.PossibleZeroQ.ofQ(engine, expr)) {
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
							resultList.append(F.List(listOfRules.getAST(k)));
							if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
								return resultList;
							}
							evaled = true;
						} else {
							// collect linear and univariate polynomial
							// equations:
							IAST kListOfSolveRules = listOfRules.getAST(k);
							ArrayList<ExprAnalyzer> subAnalyzerList = substituteRulesInSubAnalyzerList(
									kListOfSolveRules, analyzerList, currEquation, variables, engine);
							try {
								IAST subResultList = analyzeSublist(subAnalyzerList, variables, F.List(),
										maximumNumberOfResults, matrix, vector, engine);
								if (subResultList.isPresent()) {
									evaled = true;
									IASTAppendable tempResult = addSubResultsToResultsList(resultList, subResultList,
											kListOfSolveRules, maximumNumberOfResults);
									if (tempResult.isPresent()) {
										return tempResult;
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
				matrix.append(engine.evaluate(exprAnalyzer.getRow()));
				vector.append(engine.evaluate(F.Negate(exprAnalyzer.getValue())));
			} else {
				throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
			}
			currEquation++;
		}
		return resultList;
	}

	/**
	 * Add the sub-results to the results list. If <code>maximumNumberOfResults</code> is reached return the resultList,
	 * otherwise return <code>F#NIL</code>.
	 * 
	 * @param resultList
	 * @param subResultList
	 * @param kListOfSolveRules
	 * @param maximumNumberOfResults
	 * @return if <code>maximumNumberOfResults</code> is reached return the resultList, otherwiaw return
	 *         <code>F#NIL</code>.
	 */
	private static IASTAppendable addSubResultsToResultsList(IASTAppendable resultList, IAST subResultList,
			IAST kListOfSolveRules, int maximumNumberOfResults) {
		for (IExpr expr : subResultList) {
			if (expr.isList()) {
				IASTAppendable list = (IASTAppendable) expr;
				list.append(1, kListOfSolveRules);
				resultList.append(list);
				if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
					return resultList;
				}
			} else {
				resultList.append(expr);
				if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
					return resultList;
				}
			}
		}
		return F.NIL;
	}

	/**
	 * For all analyzers in <code>analyzerList</code> from position to the last element substitute the variables by the
	 * rules in <code>kListOfSolveRules</code> and create a new (sub-)analyzer list.
	 * 
	 * @param kListOfSolveRules
	 * @param analyzerList
	 * @param position
	 * @param variables
	 * @param engine
	 * @return
	 */
	private static ArrayList<ExprAnalyzer> substituteRulesInSubAnalyzerList(IAST kListOfSolveRules,
			ArrayList<ExprAnalyzer> analyzerList, int position, IAST variables, EvalEngine engine) {
		ExprAnalyzer exprAnalyzer;
		ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
		for (int i = position; i < analyzerList.size(); i++) {
			IExpr expr = analyzerList.get(i).getExpr();
			IExpr temp = expr.replaceAll(kListOfSolveRules);
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
		return subAnalyzerList;
	}

	/**
	 * Solve boolean expressions recursivel.y
	 * 
	 * @param expr
	 * @param variables
	 * @param maximumNumberOfResults
	 * @param position
	 * @param resultList
	 */
	protected static void booleansSolve(IExpr expr, IAST variables, int maximumNumberOfResults, int position,
			IASTAppendable resultList) {
		if (maximumNumberOfResults > 0 && maximumNumberOfResults < resultList.size()) {
			return;
		}
		int size = variables.size();
		if (size <= position) {
			if (EvalEngine.get().evalTrue(expr)) {
				IASTAppendable list = F.ListAlloc(size);
				for (int i = 1; i < size; i++) {
					ISymbol sym = (ISymbol) variables.get(i);
					list.append(F.Rule(sym, sym.get()));
				}
				resultList.append(list);
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
	 * @param fListOfVariables
	 * @return
	 */
	private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
		IExpr numerator = exprAnalyzer.getNumerator();
		IExpr denominator = exprAnalyzer.getDenominator();
		// try to solve the expr for a symbol in the symbol set
		for (ISymbol sym : exprAnalyzer.getSymbolSet()) {
			IExpr temp = F.NIL;
			if (numerator.isNumericMode() && denominator.isOne()) {
				temp = PolynomialFunctions.roots(numerator, F.List(sym), engine);
			}
			if (!temp.isPresent()) {
				temp = PolynomialFunctions.rootsOfVariable(numerator, denominator, F.List(sym),
						numerator.isNumericMode(), engine);
			}
			if (temp.isPresent()) {
				if (temp.isASTSizeGE(F.List, 2)) {
					IAST rootsList = (IAST) temp;
					IASTAppendable resultList = F.ListAlloc(rootsList.size());
					for (IExpr root : rootsList) {
						resultList.append(F.Rule(sym, root));
					}
					return resultList;
				}
				return F.NIL;
			}
		}
		return F.NIL;

	}

	/**
	 * Sort the arguments, which are assumed to be of type <code>List()</code>
	 * 
	 * @param resultList
	 * @return
	 */
	private static IAST sortASTArguments(IAST resultList) {
		for (int i = 1; i < resultList.size(); i++) {
			if (resultList.get(i).isList()) {
				EvalAttributes.sort((IASTMutable) resultList.get(i));
			}
		}
		return resultList;
	}

	/**
	 * Return an immutable <code>List[numerator, denominator]</code> of the given expression. Uses
	 * <code>Numerator[] and Denominator[]</code> functions.
	 * 
	 * @param expr
	 * @param engine
	 * @param evalTogether
	 *            evaluate <code>Together[expr]</code> before determining numerator and denominator of the expression.
	 * @return <code>List[numerator, denominator]</code>
	 */
	private static IAST splitNumeratorDenominator(IAST expr, EvalEngine engine, boolean evalTogether) {
		IExpr numerator, denominator;
		if (evalTogether) {
			numerator = Algebra.together(expr, engine);
		} else {
			numerator = expr;
		}
		// split expr into numerator and denominator
		denominator = engine.evaluate(F.Denominator(numerator));
		if (!denominator.isOne()) {
			// search roots for the numerator expression
			numerator = engine.evaluate(F.Numerator(numerator));
		} else {
			numerator = expr;
		}
		return F.binaryAST2(F.List, numerator, denominator);
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		try {
			IAST variables = Validate.checkIsVariableOrVariableList(ast, 2, engine);
			if (variables == null) {
				return F.NIL;
			}
			IExpr domain = F.Complexes;
			if (ast.isAST3()) {
				domain = ast.arg3();
				if (domain.equals(F.Booleans)) {
					IASTAppendable resultList = F.List();
					booleansSolve(ast.arg1(), variables, 0, 1, resultList);
					return resultList;
				}
				if (domain.equals(F.Integers)) {
					IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast, 1);
					try {
						// call cream solver
						CreamConvert converter = new CreamConvert();
						IAST resultList = converter.integerSolve(equationsAndInequations, variables);
						EvalAttributes.sort((IASTMutable) resultList);
						return resultList;
					} catch (RuntimeException rex) {
						if (Config.SHOW_STACKTRACE) {
							rex.printStackTrace();
						}
						engine.printMessage("Integer solution not found: " + rex.getMessage());
					}
					return F.NIL;
				}
				if (!domain.equals(F.Reals) && !domain.equals(F.Complexes)) {
					throw new WrongArgumentType(ast, ast.arg3(), 3, "Booleans or Integers expected!");
				}
			}
			// IAST termsEqualZeroList = Validate.checkEquationsAndInequations(ast, 1);
			IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
			IAST[] lists = SolveUtils.filterSolveLists(termsList);
			if (lists[2].isPresent()) {
				return lists[2];
			}

			IAST termsEqualZeroList = lists[0];
			IAST temp = solveTimesEquationsRecursively(termsEqualZeroList, lists[1], variables, engine);
			if (temp.isPresent()) {
				if (lists[1].isEmpty()) {
					return temp;
				}
			}

			if (lists[1].isEmpty() && termsEqualZeroList.size() == 2 && variables.size() == 2) {
				return eliminateOneVariable(termsEqualZeroList, variables.arg1());
			}
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		}

		return F.NIL;

	}

	/**
	 * Use the <code>Eliminate()</code> function to extract one variable.
	 * 
	 * @param termsEqualZeroList
	 *            a list of expressions which equals zero.
	 * @param variable
	 *            the variable which should be eliminated in the term
	 * @return
	 */
	private static IExpr eliminateOneVariable(IAST termsEqualZeroList, IExpr variable) {
		if (!termsEqualZeroList.arg1().isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
			return F.NIL;
		}
		// copy the termsEqualZeroList back to a list of F.Equal(...) expressions
		// because Eliminate() operates on equations.
		IAST equalsASTList = termsEqualZeroList.mapThread(F.Equal(F.Null, F.C0), 1);
		IAST[] tempAST = Eliminate.eliminateOneVariable(equalsASTList, variable);
		if (tempAST != null && tempAST[1] != null) {
			return F.List(F.List(tempAST[1]));
		}
		return F.NIL;
	}

	/**
	 * Expand rational terms in Plus() expressions of the equations list.
	 * 
	 * @param termsEqualZeroList
	 * @param variables
	 * @param engine
	 * @return
	 */
	// private IExpr solvePlusEquationsSimplified(IAST termsEqualZeroList, IAST
	// variables, EvalEngine engine) {
	// IExpr temp;
	// boolean forEvaled = false;
	// for (int i = 1; i < termsEqualZeroList.size(); i++) {
	// temp = termsEqualZeroList.get(i);
	// if (temp.isPlus()) {
	// IAST plus = (IAST) temp;
	// IExpr[] fractionalParts;
	// boolean evaled = false;
	// IAST[] factors = new IAST[plus.argSize()];
	// for (int j = 0; j < factors.length; j++) {
	// factors[j] = F.Times();
	// }
	// IAST newPlus = F.Plus();
	// for (int j = 1; j < plus.size(); j++) {
	// fractionalParts = Algebra.fractionalPartsRational(plus.get(j));
	// if (fractionalParts != null && !fractionalParts[1].isOne()) {
	// for (int k = 1; k < plus.size(); k++) {
	// if (k != j) {
	// // append the denominator
	// factors[k - 1].append(fractionalParts[1]);
	// }
	// }
	// newPlus.append(fractionalParts[0]);
	// evaled = true;
	// continue;
	// }
	// newPlus.append(plus.get(j));
	// }
	// if (evaled) {
	// forEvaled = true;
	// for (int j = 1; j < plus.size(); j++) {
	// IExpr factor = factors[j - 1].getOneIdentity(F.C1);
	// if (!factor.isOne()) {
	// newPlus.set(j, F.Times(factor, newPlus.get(j)));
	// } else {
	// newPlus.set(j, newPlus.get(j));
	// }
	// }
	// termsEqualZeroList.set(i, F.expandAll(newPlus, true, true));
	// }
	// }
	// }
	// if (forEvaled) {
	// engine.printMessage(
	// "Solve: using of simplified rational expressions may not give solutions for
	// all variables.");
	//
	// temp = solveTimesEquationsRecursively(termsEqualZeroList, variables, engine);
	// if (temp.isPresent()) {
	// return temp;
	// }
	// return solveEquations(termsEqualZeroList, variables, 0, engine);
	// }
	// return F.NIL;
	// }

	/**
	 * 
	 * @param termsEqualZeroList
	 *            the list of expressions extracted form the given equations, which should equal <code>0</code>
	 * @param variables
	 *            the variables for which the equations should be solved
	 * @param maximumNumberOfResults
	 *            the maximum number of results which should be returned
	 * @param engine
	 *            the evaluation engine
	 * @return a &quot;list of rules list&quot; which solves the equations, or an empty list if no solution exists, or
	 *         <code>F.NIL</code> if the equations are not solvable by this algorithm.
	 */
	protected IAST solveEquations(IAST termsEqualZeroList, IAST inequationsList, IAST variables,
			int maximumNumberOfResults, EvalEngine engine) {
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
		IASTAppendable matrix = F.List();
		IASTAppendable vector = F.List();
		try {
			IASTAppendable resultList = F.List();
			resultList = analyzeSublist(analyzerList, variables, resultList, maximumNumberOfResults, matrix, vector,
					engine);
			if (vector.size() > 1) {
				// solve a linear equation <code>matrix.x == vector</code>
				FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix(matrix, vector);
				if (augmentedMatrix != null) {
					IAST subSolutionList = LinearAlgebra.rowReduced2RulesList(augmentedMatrix, variables, resultList,
							engine);
					return solveInequations(subSolutionList, inequationsList, variables, maximumNumberOfResults,
							engine);
				}
				return F.NIL;
			}
			return solveInequations(resultList, inequationsList, variables, maximumNumberOfResults, engine);
			// return sortASTArguments(resultList);
		} catch (NoSolution e) {
			if (e.getType() == NoSolution.WRONG_SOLUTION) {
				return F.List();
			}
			return F.NIL;
		}
	}

	protected IAST solveInequations(IAST subSolutionList, IAST inequationsList, IAST variables,
			int maximumNumberOfResults, EvalEngine engine) {
		if (inequationsList.isEmpty()) {
			return sortASTArguments(subSolutionList);
		}
		try {
			// TODO solve the inequations here

			// } catch (NoSolution e) {
			// if (e.getType() == NoSolution.WRONG_SOLUTION) {
			// return F.List();
			// }
			// return F.NIL;
		} finally {

		}
		return F.NIL;
	}

	/**
	 * Analyze the <code>termsEqualZeroList</code> if it contains a <code>Times[..., ,...]</code> expression. If true,
	 * set the factors equal to <code>0</code> and solve the equations recursively.
	 * 
	 * @param termsEqualZeroList
	 *            the list of expressions extracted form the given equations, which should equal <code>0</code>
	 * @param variables
	 *            the variables for which the equations should be solved
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	private IAST solveTimesEquationsRecursively(IAST termsEqualZeroList, IAST inequationsList, IAST variables,
			EvalEngine engine) {
		Set<IExpr> subSolutionSet = new TreeSet<IExpr>();
		for (int i = 1; i < termsEqualZeroList.size(); i++) {
			if (termsEqualZeroList.get(i).isTimes()) {
				IAST times = (IAST) termsEqualZeroList.get(i);
				IAST splittedList = splitNumeratorDenominator(times, engine, false);
				if (splittedList.arg2().isFree(Predicates.in(variables), true)) {

					for (int j = 1; j < times.size(); j++) {
						if (!times.get(j).isFree(Predicates.in(variables), true)) {
							// try to get a solution from this Times() factor
							IASTAppendable clonedEqualZeroList = termsEqualZeroList.setAtClone(i, times.get(j));
							IAST temp = solveEquations(clonedEqualZeroList, inequationsList, variables, 0, engine);
							if (temp.size() > 1) {
								// subSolutionSet.addAll(temp.args());
								temp.copyTo(subSolutionSet);
							}
						}
					}

					if (subSolutionSet.size() > 0) {
						IASTAppendable list = F.ListAlloc(subSolutionSet.size());
						list.appendAll(subSolutionSet);
						return list;
					}
				}
			}
		}
		return solveEquations(termsEqualZeroList, inequationsList, variables, 0, engine);
	}

}
