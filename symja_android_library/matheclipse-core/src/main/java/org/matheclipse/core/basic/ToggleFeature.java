package org.matheclipse.core.basic;

import org.matheclipse.core.expression.S;

/**
 * Toggle a new experimental Symja function <b>ON</b> or <b>OFF</b>, usually by changing the method
 * implementations of the <code>IFunctionEvaluator</code> interface according to the defined
 * &quot;toggle flag&quot;. If a function has a <code>ToggleFeature</code> flag it's considered
 * unstable or has a very incomplete implementation.
 */
public class ToggleFeature {

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.builtin.function.Compile</code>
   * function.
   */
  public static boolean COMPILE = false;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.builtin.function.CompilePrint</code>
   * function.
   */
  public static boolean COMPILE_PRINT = false;

  public static boolean COMPILE_WITH_JAVAPARSER = false;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.builtin.function.Defer</code> function.
   */
  // public static boolean DEFER = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.DSolve</code>
   * function.
   */
  public static boolean DSOLVE = true;

  /**
   * If <code>true</code> calculate &quot;eigen&quot; functions {@link S#Eigensystem},
   * {@link S#Eigenvalues}, {@link S#Eigenvectors} symbolically. If <code>false</code> always
   * calculate numerically.
   */
  public static boolean EIGENSYSTEM_SYMBOLIC = true;

  /** If <code>true</code>, enable experimental f<code>Manipulate()</code> function */
  public static boolean MANIPULATE = true;

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

  /**
   * If <code>true</code>, enable solvers from package <code>io.github.mangara.diophantine</code> to
   * find some solutions in {@link S#FindInstance}
   */
  public static boolean SOLVE_DIOPHANTINE = true;

  /**
   * Use Apache ECharts for rendering some 2D plot functions.
   */
  public static boolean JS_ECHARTS = true;

  public static boolean SHOW_STEPS = true;
}
