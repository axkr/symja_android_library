package org.matheclipse.core.interfaces;

@FunctionalInterface
public interface IExprProcessor {

	boolean process(IExpr min, IExpr max, IASTAppendable result, int index);

}
