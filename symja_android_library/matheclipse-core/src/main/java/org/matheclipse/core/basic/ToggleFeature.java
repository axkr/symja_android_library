package org.matheclipse.core.basic;

/**
 * Toggle a new experimental Symja function <b>ON</b> or <b>OFF</b>, usually by changing the method implementations of the
 * <code>IFunctionEvaluator</code> interface according to the defined &quot;toggle flag&quot;. If a function has a
 * <code>ToggleFeature</code> flag it's considered unstable.
 *
 */
public class ToggleFeature {
	/**
	 * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.ReplaceList</code> function.
	 */
	public static boolean REPLACE_LIST_FUNCTION = true;

}
