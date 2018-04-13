package org.matheclipse.core.interfaces;

/**
 * Any distribution for which an analytic expression of the mean exists should implement {@link IMean}.
 * 
 * <p>
 * The function is used in {@link Expectation} to provide the mean of a given {@link IDistribution}.
 */
public interface IMean {
	IExpr mean(IAST distribution);
}