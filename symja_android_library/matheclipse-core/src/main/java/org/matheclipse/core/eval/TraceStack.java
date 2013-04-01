package org.matheclipse.core.eval;

import java.util.Stack;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;

import com.google.common.base.Predicate;

final public class TraceStack {

	private static final long serialVersionUID = 3939797221814484213L;

	final Stack<IAST> fStack = new Stack<IAST>();
	final Predicate<IExpr> fMatcher;
	final IAST fList;
	IAST fTraceList;

	public TraceStack(Predicate<IExpr>  matcher, IAST list) {
		super();
		fMatcher = matcher;
		fList = list;
		pushList();
	}

	public void pushList() {
		fTraceList = fList.clone();
		fStack.push(fTraceList);
	}

	public void popList() {
		IAST traceList = fTraceList;
		fStack.pop();
		fTraceList = fStack.peek(); 
		if (traceList.size() > 1) {
			fTraceList.add(traceList);
		}
	}

	public IAST getList() {
		return fTraceList;
	}

	/**
	 * Add the expression to the internal trace list, if the trace matcher returns
	 * <code>true</code>.
	 * 
	 * @param expr
	 *          an expression
	 */
	public void add(IExpr expr) {
		if (fMatcher != null) {
			if (fMatcher.apply(expr)) {
				fTraceList.add(expr);
			}
		} else {
			fTraceList.add(expr);
		}
	}

	/**
	 * Add the expression to the internal trace list, if the trace matcher returns
	 * <code>true</code> and the trace lit is empty.
	 * 
	 * @param expr
	 *          an expression
	 */
	public void addIfEmpty(IExpr expr) {
		if (fTraceList.size() == 1) {
			if (fMatcher != null) {
				if (fMatcher.apply(expr)) {
					fTraceList.add(expr);
				}
			} else {
				fTraceList.add(expr);
			}
		}
	}
}
