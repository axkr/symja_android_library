package org.matheclipse.core.patternmatching;

import org.matheclipse.core.interfaces.IExpr;

@FunctionalInterface
public interface IPatternMethod {
	public abstract IExpr eval(PatternMap pm);
}
