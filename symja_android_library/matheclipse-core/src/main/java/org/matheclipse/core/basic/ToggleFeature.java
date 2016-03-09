package org.matheclipse.core.basic;

/**
 * Toggle a new experimental Symja function <b>ON</b> or <b>OFF</b>, usually by
 * changing the method implementations of the <code>IFunctionEvaluator</code>
 * interface according to the defined &quot;toggle flag&quot;. If a function has
 * a <code>ToggleFeature</code> flag it's considered unstable or has a very
 * incomplete implementation.
 *
 */
public class ToggleFeature {
	/**
	 * If <code>true</code>, enable
	 * <code>org.matheclipse.core.builtin.function.Compile</code> function.
	 */
	public static boolean COMPILE = true;

	/**
	 * If <code>true</code>, enable
	 * <code>org.matheclipse.core.builtin.function.Defer</code> function.
	 */
	public static boolean DEFER = true;

	/**
	 * If <code>true</code>, enable
	 * <code>org.matheclipse.core.reflection.system.ReplaceList</code> function.
	 */
	public static boolean REPLACE_LIST = true;

	/**
	 * If <code>true</code>, enable
	 * <code>org.matheclipse.core.builtin.function.Unevaluated</code> function.
	 */
	public static boolean UNEVALUATED = true;

	/**
	 * If <code>true</code>, enable
	 * <code>org.matheclipse.core.reflection.system.DSolve</code> function.
	 */
	public static boolean DSOLVE = true;

}
