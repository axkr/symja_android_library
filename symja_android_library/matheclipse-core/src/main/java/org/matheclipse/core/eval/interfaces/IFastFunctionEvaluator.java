package org.matheclipse.core.eval.interfaces;

/**
 * Interface for &quot;fast core built-in functions&quot; which don't have associated rules and are
 * only defined by a derived classes from this interface. This is a <i>marker interface</i> for
 * special built-in functions to avoid the engines evaluation loop but directly call the built-in
 * functions
 * {@link #evaluate(org.matheclipse.core.interfaces.IAST, org.matheclipse.core.eval.EvalEngine)}
 * method. The methods returned value won't be evaluated again in the engines evaluation loop, but
 * directly returned back.
 */
public interface IFastFunctionEvaluator extends ICoreFunctionEvaluator {

}
