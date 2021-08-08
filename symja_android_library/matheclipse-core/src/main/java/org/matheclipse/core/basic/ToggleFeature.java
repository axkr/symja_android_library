package org.matheclipse.core.basic;

/**
 * Toggle a new experimental Symja function <b>ON</b> or <b>OFF</b>, usually by changing the method
 * implementations of the <code>IFunctionEvaluator</code> interface according to the defined
 * &quot;toggle flag&quot;. If a function has a <code>ToggleFeature</code> flag it's considered
 * unstable or has a very incomplete implementation.
 */
public class ToggleFeature {

  /** If <code>true</code>, enable choco solver in <code>Solve()</code> function. */
  public static boolean CHOCO_SOLVER = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.builtin.function.Compile</code>
   * function.
   */
  public static boolean COMPILE = false;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.builtin.function.Defer</code> function.
   */
  //  public static boolean DEFER = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.ReplaceList</code>
   * function.
   */
  public static boolean REPLACE_LIST = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.DSolve</code>
   * function.
   */
  public static boolean DSOLVE = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.Series</code> and
   * <code>org.matheclipse.core.reflection.system.SeriesData</code> functions.
   */
  public static boolean SERIES = true;

  /**
   * If <code>true</code>, enable usage of <code>denominators != 1</code> in <code>
   * org.matheclipse.core.reflection.system.Series</code> and <code>
   * org.matheclipse.core.reflection.system.SeriesData</code> functions.
   */
  public static boolean SERIES_DENOMINATOR = false;

  /** If <code>true</code>, enable experimental Quantity functions */
  public static boolean QUANTITY = true;

  /** If <code>true</code>, enable experimental financial functions */
  public static boolean FINANCE = true;

  /** If <code>true</code>, enable experimental f<code>MANIPULATE()</code> function */
  public static boolean MANIPULATE = true;
}
