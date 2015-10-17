package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
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
import org.matheclipse.core.visit.AbstractVisitorBoolean;

import com.google.common.base.Predicate;

/**
 * Try to eliminate a variable in a set of equations (i.e.
 * <code>Equal[...]</code> expressions).
 * 
 * See <a href=
 * "http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables">
 * Wikipedia - System of linear equations - Elimination of variables</a>.
 */
public class Eliminate extends AbstractFunctionEvaluator {

	private static class VariableCounterVisitor extends AbstractVisitorBoolean
			implements Comparable<VariableCounterVisitor> {

		/**
		 * Count the number of nodes in <code>fExpr</code>, which equals
		 * <code>fVariable</code>.
		 */
		int fVariableCounter;

		/**
		 * Count the total number of nodes in <code>fExpr</code>..
		 */
		int fNodeCounter;

		/**
		 * The maximum number of recursion levels for visiting nodes, which
		 * equals <code>fVariable</code>.
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
				for (int i = 1; i < ast.size(); i++) {
					ast.get(i).accept(this);
				}
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
	 * Check if the argument at the given position is an equation (i.e.
	 * Equal[a,b]) or a list of equations and return a list of expressions,
	 * which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	private static IAST checkEquations(final IAST ast, int position) {
		IAST equalList = F.List();
		IAST eqns = null;
		IAST eq;
		if (ast.get(position).isList()) {
			eqns = (IAST) ast.get(position);
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST(F.Equal, 3)) {
					eq = (IAST) eqns.get(i);
					// equalList.add(F.Equal(F.evalExpandAll(eq.arg1()),
					// F.evalExpandAll(eq.arg2())));
					equalList.add(Equal.equal(eq));
				} else {
					// not an equation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equal[] expression (a==b) expected");
				}
			}
		} else {
			if (ast.get(position).isAST(F.Equal, 3)) {
				eq = (IAST) ast.get(position);
				equalList.add(F.Equal(F.evalExpandAll(eq.arg1()), F.evalExpandAll(eq.arg2())));
			} else {
				// not an equation
				throw new WrongArgumentType(ast, ast.arg1(), 1, "Equal[] expression (a==b) expected");
			}
		}
		return equalList;
	}

	/**
	 * Analyze an <code>Equal()</code> expression.
	 * 
	 * @param equalAST
	 *            an <code>Equal()</code> expression.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't find an equation for the given
	 *         <code>variable</code>.
	 */
	private static IExpr eliminateAnalyze(IAST equalAST, ISymbol variable) {
		IExpr arg1 = equalAST.arg1();
		IExpr arg2 = equalAST.arg2();
		Predicate<IExpr> predicate = Predicates.in(variable);
		boolean boolArg1 = arg1.isFree(predicate, true);
		boolean boolArg2 = arg2.isFree(predicate, true);
		IExpr result = null;
		if (!boolArg1 && boolArg2) {
			result = extractVariable(arg1, arg2, predicate, variable);
		} else if (boolArg1 && !boolArg2) {
			result = extractVariable(arg2, arg1, predicate, variable);
		}
		return result;
	}

	/**
	 * Extract a value for the given <code>variabe</code>.
	 * 
	 * @param exprWithVariable
	 *            expression which contains the given <code>variabe</code>.
	 * @param exprWithoutVariable
	 *            expression which doesn't contain the given
	 *            <code>variabe</code>.
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't find an equation for the given
	 *         <code>variable</code>.
	 */
	public static IExpr extractVariable(IExpr exprWithVariable, IExpr exprWithoutVariable, Predicate<IExpr> predicate,
			ISymbol variable) {
		if (exprWithVariable.isAST()) {
			IAST ast = (IAST) exprWithVariable;
			if (ast.size() == 2) {
				IAST inverseFunction = InverseFunction.getUnaryInverseFunction(ast);
				if (inverseFunction != null) {
					// example: Sin(f(x)) == y -> f(x) == ArcSin(y)
					inverseFunction.add(exprWithoutVariable);
					return extractVariable(ast.arg1(), inverseFunction, predicate, variable);
				}
			} else {
				if (ast.isPlus()) {
					// a + b + c....
					IAST rest = F.Plus();
					IAST plusClone = ast.clone();
					int j = 1;
					for (int i = 1; i < ast.size(); i++) {
						if (ast.get(i).isFree(predicate, true)) {
							j++;
						} else {
							rest.add(ast.get(i));
							plusClone.remove(j);
						}
					}
					if (plusClone.size() == 1) {
						// no change for given expression
						return null;
					}
					IExpr value = F.Subtract(exprWithoutVariable, plusClone);
					return extractVariable(rest.getOneIdentity(F.C0), value, predicate, variable);
				} else if (ast.isTimes()) {
					// a * b * c....
					IAST rest = F.Times();
					IAST timesClone = ast.clone();
					int j = 1;
					for (int i = 1; i < ast.size(); i++) {
						if (ast.get(i).isFree(predicate, true)) {
							j++;
						} else {
							rest.add(ast.get(i));
							timesClone.remove(j);
						}
					}
					if (timesClone.size() == 1) {
						// no change for given expression
						return null;
					}
					IExpr value = F.Divide(exprWithoutVariable, timesClone);
					return extractVariable(rest.getOneIdentity(F.C1), value, predicate, variable);
				} else if (ast.isPower()) {
					if (ast.arg2().isFree(predicate, true)) {
						// f(x) ^ a
						IExpr value = F.Power(exprWithoutVariable, F.Divide(F.C1, ast.arg2()));
						return extractVariable(ast.arg1(), value, predicate, variable);
					} else if (ast.arg1().isFree(predicate, true)) {
						// a ^ f(x)
						IExpr value = F.Divide(F.Log(exprWithoutVariable), F.Log(ast.arg1()));
						return extractVariable(ast.arg2(), value, predicate, variable);
					}
				}
			}
		} else if (exprWithVariable.equals(variable)) {
			return exprWithoutVariable;
		}
		return null;
	}

	public Eliminate() {
	}

	/**
	 * Analyze the <code>Equal()</code> terms, if we find an expression which
	 * equals the given <code>variabe</code>
	 * 
	 * @param termsEqualZeroList
	 *            list of <code>Equal()</code> terms
	 * @param variable
	 *            the variable which should be eliminated.
	 * @return <code>null</code> if we can't eliminate an equation from the list
	 *         for the given <code>variabe</code>
	 */
	public IAST eliminateOneVariable(ArrayList<VariableCounterVisitor> analyzerList, ISymbol variable) {
		IAST equalAST;
		IAST termsEqualZeroList = F.List();
		VariableCounterVisitor exprAnalyzer;
		for (int i = 0; i < analyzerList.size(); i++) {
			exprAnalyzer = analyzerList.get(i);
			equalAST = exprAnalyzer.getExpr();
			IExpr variableExpr = eliminateAnalyze(equalAST, variable);
			if (variableExpr != null) {
				variableExpr = F.eval(variableExpr);
				IExpr temp;
				IExpr expr;
				IAST rule = F.Rule(variable, variableExpr);
				analyzerList.remove(i);
				for (int j = 0; j < analyzerList.size(); j++) {
					expr = analyzerList.get(j).getExpr();
					temp = expr.replaceAll(rule);
					if (temp != null) {
						temp = F.expandAll(temp, true, true);
						termsEqualZeroList.add(temp);
					} else {
						termsEqualZeroList.add(expr);
					}
				}
				return termsEqualZeroList;

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

			IAST termsEqualZeroList = checkEquations(ast, 1);

			IAST result = termsEqualZeroList;
			IAST temp;
			IAST equalAST;
			ISymbol variable;
			VariableCounterVisitor exprAnalyzer;
			for (int i = 1; i < vars.size(); i++) {
				variable = (ISymbol) vars.get(i);

				ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
				for (int j = 1; j < result.size(); j++) {
					equalAST = result.getAST(j);
					exprAnalyzer = new VariableCounterVisitor(equalAST, variable);
					equalAST.accept(exprAnalyzer);
					analyzerList.add(exprAnalyzer);
				}
				Collections.sort(analyzerList);

				temp = eliminateOneVariable(analyzerList, variable);
				if (temp != null) {
					result = temp;
				} else {
					return result;
				}
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
