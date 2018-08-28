package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.visit.AbstractVisitorBoolean;

/**
 * <pre>
 * Eliminate(list - of - equations, list - of - variables)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * attempts to eliminate the variables from the <code>list-of-variables</code> in the <code>list-of-equations</code>.
 * </p>
 * </blockquote>
 * <p>
 * See:
 * </p>
 * <ul>
 * <li><a href= "http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables">Wikipedia - System of
 * linear equations - Elimination of variables</a></li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt;&gt; Eliminate({x==2+y, y==z}, y)
 * x==2+z
 * </pre>
 * 
 */
public class Eliminate extends AbstractFunctionEvaluator {

	private static class VariableCounterVisitor extends AbstractVisitorBoolean
			implements Comparable<VariableCounterVisitor> {

		/**
		 * Count the number of nodes in <code>fExpr</code>, which equals <code>fVariable</code>.
		 */
		int fVariableCounter;

		/**
		 * Count the total number of nodes in <code>fExpr</code>..
		 */
		int fNodeCounter;

		/**
		 * The maximum number of recursion levels for visiting nodes, which equals <code>fVariable</code>.
		 */
		int fMaxVariableDepth;

		/**
		 * Holds the current recursion level for visiting nodes.
		 */
		int fCurrentDepth;

		final IExpr fVariable;
		final IAST fExpr;

		public VariableCounterVisitor(final IAST expr, final IExpr variable) {
			super();
			fVariable = variable;
			fExpr = expr;
			fVariableCounter = 0;
			fNodeCounter = 0;
			fMaxVariableDepth = 0;
			fCurrentDepth = 0;
		}

		@Override
		public int compareTo(VariableCounterVisitor other) {
			if (fVariableCounter < other.fVariableCounter) {
				return -1;
			}
			if (fVariableCounter > other.fVariableCounter) {
				return 1;
			}
			if (fMaxVariableDepth < other.fMaxVariableDepth) {
				return -1;
			}
			if (fMaxVariableDepth > other.fMaxVariableDepth) {
				return 1;
			}
			if (fNodeCounter < other.fNodeCounter) {
				return -1;
			}
			if (fNodeCounter > other.fNodeCounter) {
				return 1;
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
			VariableCounterVisitor other = (VariableCounterVisitor) obj;
			if (fCurrentDepth != other.fCurrentDepth)
				return false;
			if (fExpr == null) {
				if (other.fExpr != null)
					return false;
			} else if (!fExpr.equals(other.fExpr))
				return false;
			if (fMaxVariableDepth != other.fMaxVariableDepth)
				return false;
			if (fNodeCounter != other.fNodeCounter)
				return false;
			if (fVariable == null) {
				if (other.fVariable != null)
					return false;
			} else if (!fVariable.equals(other.fVariable))
				return false;
			if (fVariableCounter != other.fVariableCounter)
				return false;
			return true;
		}

		public IAST getExpr() {
			return fExpr;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + fCurrentDepth;
			result = prime * result + ((fExpr == null) ? 0 : fExpr.hashCode());
			result = prime * result + fMaxVariableDepth;
			result = prime * result + fNodeCounter;
			result = prime * result + ((fVariable == null) ? 0 : fVariable.hashCode());
			result = prime * result + fVariableCounter;
			return result;
		}

		@Override
		public boolean visit(IAST ast) {
			fNodeCounter++;
			if (ast.equals(fVariable)) {
				fVariableCounter++;
				if (fMaxVariableDepth < fCurrentDepth) {
					fMaxVariableDepth = fCurrentDepth;
				}
				return true;
			}
			try {
				fCurrentDepth++;
				ast.forEach(x -> x.accept(this));
			} finally {
				fCurrentDepth--;
			}

			return false;
		}

		@Override
		public boolean visit(ISymbol symbol) {
			fNodeCounter++;
			if (symbol.equals(fVariable)) {
				fVariableCounter++;
				if (fMaxVariableDepth < fCurrentDepth) {
					fMaxVariableDepth = fCurrentDepth;
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean visit(IInteger element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IFraction element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IComplex element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(INum element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IComplexNum element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IPattern element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IPatternSequence element) {
			fNodeCounter++;
			return false;
		}

		@Override
		public boolean visit(IStringX element) {
			fNodeCounter++;
			return false;
		}

	}

	/**
	 * Check if the argument at the given position is an equation (i.e. Equal[a,b]) or a list of equations and return a
	 * list of expressions, which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	private static IAST checkEquations(final IAST ast, int position, EvalEngine engine) {
		IExpr arg = ast.get(position);
		if (arg.isList()) {
			IAST list = (IAST) arg;
			IASTAppendable equalList = F.ListAlloc(list.size());
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).isEqual()) {
					IAST equalAST = (IAST) list.get(i);
					// equalList.add(F.Equal(F.evalExpandAll(eq.arg1()),
					// F.evalExpandAll(eq.arg2())));
					equalList.append(BooleanFunctions.equals(equalAST));
				} else {
					// not an equation
					throw new WrongArgumentType(list, list.get(i), i, "Equal[] expression (a==b) expected");
				}
			}
			return equalList;
		}
		if (arg.isEqual()) {
			IAST equalAST = (IAST) arg;
			return F.List(F.Equal(F.evalExpandAll(equalAST.arg1(), engine), F.evalExpandAll(equalAST.arg2(), engine)));
			// return equalList;
		}
		// not an equation
		throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal[] expression (a==b) expected");
	}

	/**
	 * Analyze an <code>Equal()</code> expression.
	 * 
	 * @param equalAST
	 *            an <code>Equal()</code> expression.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
	 */
	private static IExpr eliminateAnalyze(IAST equalAST, IExpr variable) {
		if (equalAST.isEqual()) {
			IExpr arg1 = equalAST.arg1();
			IExpr arg2 = equalAST.arg2();
			Predicate<IExpr> predicate = Predicates.in(variable);
			boolean boolArg1 = arg1.isFree(predicate, true);
			boolean boolArg2 = arg2.isFree(predicate, true);
			IExpr result = F.NIL;
			if (!boolArg1 && boolArg2) {
				result = extractVariable(arg1, arg2, predicate, variable);
			} else if (boolArg1 && !boolArg2) {
				result = extractVariable(arg2, arg1, predicate, variable);
			}
			return result;
		}
		return F.NIL;
	}

	/**
	 * Extract the variable from the given <code>expr</code> assuming <code>expr == 0</code>.
	 * 
	 * @param expr
	 *            an expression.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
	 */
	public static IExpr extractVariable(IExpr expr, IExpr variable) {
		Predicate<IExpr> predicate = Predicates.in(variable);
		IExpr result = F.NIL;
		if (!expr.isFree(predicate, true)) {
			result = extractVariable(expr, F.C0, predicate, variable);
		}
		return result;
	}

	/**
	 * Extract a value for the given <code>variabe</code>.
	 * 
	 * @param exprWithVariable
	 *            expression which contains the given <code>variabe</code>.
	 * @param exprWithoutVariable
	 *            expression which doesn't contain the given <code>variabe</code>.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
	 */
	private static IExpr extractVariable(IExpr exprWithVariable, IExpr exprWithoutVariable, Predicate<IExpr> predicate,
			IExpr variable) {
		if (exprWithVariable.equals(variable)) {
			return exprWithoutVariable;
		}
		if (exprWithVariable.isAST()) {
			IAST ast = (IAST) exprWithVariable;
			if (ast.isAST1()) {
				IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast);
				if (inverseFunction.isPresent()) {
					// example: Sin(f(x)) == y -> f(x) == ArcSin(y)
					inverseFunction.append(exprWithoutVariable);
					return extractVariable(ast.arg1(), inverseFunction, predicate, variable);
				}
			} else {
				int size = ast.size();
				if (ast.isPlus()) {
					// a + b + c....
					IASTAppendable rest = F.PlusAlloc(size);
					IASTAppendable plusClone = ast.copyAppendable();
					int j = 1;
					for (int i = 1; i < size; i++) {
						if (ast.get(i).isFree(predicate, true)) {
							j++;
						} else {
							rest.append(ast.get(i));
							plusClone.remove(j);
						}
					}
					if (plusClone.isAST0()) {
						// no change for given expression
						if (ast.size() == 3) {
							IExpr temp = matchSpecialExpressions(ast, exprWithoutVariable, variable);
							if (temp.isPresent()) {
								return temp;
							}
						}
						return F.NIL;
					}
					IExpr value = F.Subtract(exprWithoutVariable, plusClone);
					return extractVariable(rest.getOneIdentity(F.C0), value, predicate, variable);
				} else if (ast.isTimes()) {
					// a * b * c....
					IASTAppendable rest = F.TimesAlloc(size);
					IASTAppendable timesClone = ast.copyAppendable();
					int j = 1;
					for (int i = 1; i < size; i++) {
						if (ast.get(i).isFree(predicate, true)) {
							j++;
						} else {
							rest.append(ast.get(i));
							timesClone.remove(j);
						}
					}
					if (timesClone.isAST0()) {
						IExpr[] numerDenom = Algebra.getNumeratorDenominator(ast, EvalEngine.get());
						if (!numerDenom[1].isOne()) {
							IExpr[] numerLinear = numerDenom[0].linear(variable);
							if (numerLinear != null) {
								IExpr[] denomLinear = numerDenom[1].linear(variable);
								if (denomLinear != null) {
									IExpr temp = EvalEngine.get().evaluate(
											numerLinear[1].subtract(denomLinear[1].times(exprWithoutVariable)));
									if (!temp.isZero()) {
										return numerLinear[0].negate().plus(denomLinear[0].times(exprWithoutVariable))
												.times(temp.power(-1L));
									}
								}
							}
						}
						// no change for given expression
						return F.NIL;
					}
					IExpr value = F.Divide(exprWithoutVariable, timesClone);
					return extractVariable(rest.getOneIdentity(F.C1), value, predicate, variable);
				} else if (ast.isPower()) {
					IExpr base = ast.base();
					IExpr exponent = ast.exponent();
					if (exponent.isFree(predicate, true)) {
						// f(x) ^ a
						IExpr value = F.Power(exprWithoutVariable, F.Divide(F.C1, exponent));
						return extractVariable(base, value, predicate, variable);
					} else if (base.isFree(predicate, true)) {
						// a ^ f(x)
						IExpr value = F.Divide(F.Log(exprWithoutVariable), F.Log(base));
						return extractVariable(exponent, value, predicate, variable);
					}
				}
			}
		}
		return F.NIL;

	}

	/**
	 * <p>
	 * Match <code>a_.*variable^n_+b_.*variable^m_</code> to
	 * <code>E^(((-I)*Pi + Log(a) - Log(b))/(m - n)) /; FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x)</code>
	 * </p>
	 * 
	 * @param ast
	 * @param x
	 * @return
	 */
	private static IExpr matchSpecialExpressions(IAST ast, IExpr exprWithoutVariable, IExpr x) {
		if (exprWithoutVariable.isZero()) {
			final Matcher matcher = new Matcher();
			// match a_.*variable^n_.+b_.*variable^m_ to E^(((-I)*Pi + Log(a) - Log(b))/(m - n))
			matcher.caseOf(
					F.Plus(F.Times(F.b_DEFAULT, F.Power(x, F.m_)), F.Times(F.a_DEFAULT, F.Power(x, F.n_DEFAULT))), //
					F.Condition(
							F.Exp(F.Times(F.Power(F.Plus(F.m, F.Negate(F.n)), -1),
									F.Plus(F.Times(F.CNI, F.Pi), F.Log(F.a), F.Negate(F.Log(F.b))))),
							F.And(F.FreeQ(F.a, x), F.FreeQ(F.b, x), F.FreeQ(F.n, x), F.FreeQ(F.m, x))));
			return matcher.replaceAll(ast);
		}
		return F.NIL;
	}

	public Eliminate() {
	}

	/**
	 * Analyze the <code>Equal()</code> terms, if we find an expression which equals the given <code>variabe</code>
	 * 
	 * @param analyzerList
	 *            the list of <code>Equal()</code> terms with statistics of it's equations.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>variable</code> or
	 *         the eliminated list of equations in index <code>[0]</code> and the last rule which is used for variable
	 *         elimination in index <code>[1]</code>.
	 */
	private static IAST[] eliminateOneVariable(ArrayList<VariableCounterVisitor> analyzerList, IExpr variable) {
		IASTAppendable eliminatedResultEquations = F.ListAlloc(analyzerList.size());
		for (int i = 0; i < analyzerList.size(); i++) {
			IExpr variableExpr = eliminateAnalyze(analyzerList.get(i).getExpr(), variable);
			if (variableExpr.isPresent()) {
				variableExpr = F.eval(variableExpr);
				IExpr expr;
				IAST rule = F.Rule(variable, variableExpr);
				analyzerList.remove(i);
				for (int j = 0; j < analyzerList.size(); j++) {
					expr = analyzerList.get(j).getExpr();
					IExpr temp = expr.replaceAll(rule);
					if (temp.isPresent()) {
						temp = F.expandAll(temp, true, true);
						eliminatedResultEquations.append(temp);
					} else {
						eliminatedResultEquations.append(expr);
					}
				}
				IAST[] result = new IAST[2];
				result[0] = eliminatedResultEquations;
				result[1] = rule;
				return result;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		try {
			IAST vars = Validate.checkSymbolOrSymbolList(ast, 2);

			IAST termsEqualZeroList = checkEquations(ast, 1, engine);

			IAST result = termsEqualZeroList;
			IAST[] temp;
			// IAST equalAST;
			ISymbol variable;
			// VariableCounterVisitor exprAnalyzer;
			for (int i = 1; i < vars.size(); i++) {
				variable = (ISymbol) vars.get(i);

				temp = eliminateOneVariable(result, variable);
				if (temp != null) {
					result = temp[0];
				} else {
					return resultAsAndEquations(result);
				}
			}
			return resultAsAndEquations(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return F.NIL;
	}

	private static IExpr resultAsAndEquations(IAST result) {
		if (result.isList()) {
			if (result.equals(F.CEmptyList)) {
				return F.True;
			}
			return result.apply(F.And);
		}
		return result;
	}

	/**
	 * 
	 * @param result
	 * @param variable
	 * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>variable</code> or
	 *         the eliminated list of equations in index <code>[0]</code> and the last rule which is used for variable
	 *         elimination in index <code>[1]</code>.
	 */
	public static IAST[] eliminateOneVariable(IAST result, IExpr variable) {
		IAST equalAST;
		VariableCounterVisitor exprAnalyzer;
		ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
		for (int j = 1; j < result.size(); j++) {
			equalAST = result.getAST(j);
			exprAnalyzer = new VariableCounterVisitor(equalAST, variable);
			equalAST.accept(exprAnalyzer);
			analyzerList.add(exprAnalyzer);
		}
		Collections.sort(analyzerList);

		return eliminateOneVariable(analyzerList, variable);
	}

	/**
	 * 
	 * @param result
	 * @param slot
	 * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>variable</code> or
	 *         the eliminated list of equations in index <code>[0]</code> and the last rule which is used for variable
	 *         elimination in index <code>[1]</code>.
	 */
	public static IAST[] eliminateSlot(IAST result, IExpr slot) {

		VariableCounterVisitor exprAnalyzer;
		ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
		IAST equalAST = result;
		exprAnalyzer = new VariableCounterVisitor(equalAST, slot);
		equalAST.accept(exprAnalyzer);
		analyzerList.add(exprAnalyzer);
		Collections.sort(analyzerList);

		return eliminateOneVariable(analyzerList, slot);
	}
}
