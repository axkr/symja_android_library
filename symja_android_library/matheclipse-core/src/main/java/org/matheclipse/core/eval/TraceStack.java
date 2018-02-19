package org.matheclipse.core.eval;

import java.util.Stack;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.AbstractEvalStepListener;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

import com.duy.lambda.Predicate;

final public class TraceStack extends AbstractEvalStepListener {

	final Stack<IASTAppendable> fStack = new Stack<IASTAppendable>();
	final Predicate<IExpr> fMatcher;
	final IAST fList;
	IASTAppendable fTraceList;

	public TraceStack(Predicate<IExpr> matcher, IAST list) {
		super();
		fMatcher = matcher;
		fList = list;
		pushList();
	}

	public void pushList() {
		fTraceList = fList.copyAppendable();
		fStack.push(fTraceList);
	}

	public void popList() {
		IAST traceList = fTraceList;
		fStack.pop();
		fTraceList = fStack.peek();
		if (traceList.size() > 1) {
			fTraceList.append(traceList);
		}
	}

	public IAST getList() {
		return fTraceList;
	}

	/**
	 * Add the expression to the internal trace list, if the trace matcher
	 * returns <code>true</code>.
	 * 
	 * @param expr
	 *            an expression
	 */
	public void add(IExpr expr) {
		if (fMatcher != null) {
			if (fMatcher.test(expr.head())) {
				fTraceList.append(F.HoldForm(expr));
			}
		} else {
			fTraceList.append(F.HoldForm(expr));
		}
	}

	/**
	 * Add the expression to the internal trace list, if the trace matcher
	 * returns <code>true</code> and the trace lit is empty.
	 * 
	 * @param expr
	 *            an expression
	 */
	public void addIfEmpty(IExpr expr) {
		if (fTraceList.isAST0()) {
			add(expr);
		}
	}

	@Override
	public void setUp(IExpr inputExpr, int recursionDepth) {
		pushList();
		// addIfEmpty(inputExpr);
		// add(resultExpr);
	}

	@Override
	public void tearDown(int recursionDepth) {
		popList();
	}

	@Override
	public void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter, String hint) {
		if (iterationCounter == 0L) {
			addIfEmpty(inputExpr);
			add(resultExpr);
		} else {
			add(resultExpr);
		}
	}
}
