package org.matheclipse.core.patternmatching;

import java.util.ArrayList;
import java.util.HashMap;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The rules which are associated to exactly one symbol in an EvaluationEngine
 * 
 */
public class AssociatedRules {
	/**
	 * Stack list where the last element contains the current value of the
	 * associate symbol
	 */
	ArrayList<IExpr> fLocalVariablesStack;

	/**
	 * HashMap for associated pattern rules there the left-hand-side of the rule
	 * contains no pattern i.e.: Sin(0)=0 etc.
	 */
	private HashMap<IExpr, IExpr> fEqualDownRules;

	public AssociatedRules() {
		fLocalVariablesStack = new ArrayList<IExpr>();
		fEqualDownRules = new HashMap<IExpr, IExpr>();
	}

	/**
	 * Search a rule for the given symbol First look into the LocalVariableStack
	 * and then into the general HashMap
	 * 
	 * @param symbol
	 * @return
	 */
	public Object evalSymbol(final ISymbol symbol) {
		final int i = fLocalVariablesStack.size();
		if (i > 0) {
			return fLocalVariablesStack.get(i);
		}
		return fEqualDownRules.get(symbol);
	}

	/**
	 * Search a rule for the given list
	 * 
	 * @param list
	 * @return
	 */
	public Object evalHeadList(final IAST list) {
		return fEqualDownRules.get(list);
	}

	/**
	 * Push a value for the associated symbol on the top of the local variable
	 * stack
	 * 
	 * @param arg0
	 * @return
	 */
	public boolean pushLocalVariable(final IExpr arg0) {
		return fLocalVariablesStack.add(arg0);
	}

	/**
	 * Get the value for the associated symbol from the top of the local variable
	 * stack
	 * 
	 * @return
	 */
	public Object peekLocalVariable() {
		return fLocalVariablesStack.get(fLocalVariablesStack.size() - 1);
	}

	/**
	 * Pop a value for the associated symbol from the top of the local variable
	 * stack
	 * 
	 * @return
	 */
	public Object popLocalVariable() {
		return fLocalVariablesStack.remove(fLocalVariablesStack.size() - 1);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public IExpr get(final IExpr arg0) {
		return fEqualDownRules.get(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public IExpr put(final IExpr arg0, final IExpr arg1) {
		return fEqualDownRules.put(arg0, arg1);
	}

}
