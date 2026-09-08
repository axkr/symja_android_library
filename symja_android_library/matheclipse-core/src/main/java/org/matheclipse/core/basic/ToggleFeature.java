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
   * If <code>true</code>, enable {@link S#Compile} function. function.
   */
  public static boolean COMPILE = false;

  /**
   * If <code>true</code>, enable {@link S#CompilePrint} function. function.
   */
  public static boolean COMPILE_PRINT = false;

  public static boolean COMPILE_WITH_JAVAPARSER = false;

  /**
   * If <code>true</code>, enable {@link S#Defer} function.
   */
  // public static boolean DEFER = true;

  /**
   * If <code>true</code> calculate &quot;eigen&quot; functions {@link S#Eigensystem},
   * {@link S#Eigenvalues}, {@link S#Eigenvectors} symbolically. If <code>false</code> always
   * calculate numerically.
   */
  public static boolean EIGENSYSTEM_SYMBOLIC = true;

  /** If <code>true</code>, enable experimental f<code>Manipulate()</code> function */
  public static boolean MANIPULATE = true;

  /**
   * If <code>true</code>, enable the space dynamics and astronomy functions implemented in the
   * <code>matheclipse-astro</code> module. Most of them additionally need the external
   * <code>orekit-data</code> files; see <code>org.matheclipse.astro.data.AstroDataContext</code>.
   */
  public static boolean ASTRO = true;

  /**
   * Enable the chemistry functions (<code>Molecule</code>, <code>MoleculeValue</code>,
   * <code>ChemicalFormula</code>, ...). Their evaluators live in the
   * <code>matheclipse-chem</code> module, which is backed by the Chemistry Development Kit.
   */
  public static boolean CHEM = true;

  /**
   * If <code>true</code>, enable <code>org.matheclipse.core.reflection.system.Series</code> and
   * <code>org.matheclipse.core.reflection.system.SeriesData</code> functions.
   */
  public static boolean SERIES = true;

  /**
   * If <code>true</code>, enable solvers from package <code>io.github.mangara.diophantine</code> to
   * find some solutions in {@link S#FindInstance}
   */
  public static boolean SOLVE_DIOPHANTINE = true;

  /**
   * Use Apache ECharts for rendering some 2D plot functions.
   */
  public static boolean JS_ECHARTS = false;

  /**
   * Master switch for the step-by-step evaluation of {@link S#TraceForm}: the listener in
   * <code>org.matheclipse.core.eval.steps</code>, the hints the built-in functions and the pattern
   * matcher announce, and the levels in
   * {@link org.matheclipse.core.eval.steps.StepLevel} which select how fine grained they are.
   *
   * <p>
   * <code>final</code> on purpose: with <code>false</code> every
   * <code>EvalEngine#addTraceStep...</code> body and every site which builds a hint expression is
   * a dead branch which the compiler removes, so nothing of this costs anything at run time - not
   * even in the arithmetic paths, where a step is announced for cancelling the gcd of a fraction.
   * <code>TraceForm(expr)</code> then evaluates its argument and reports that steps are switched
   * off in this build.
   */
  public static final boolean SHOW_STEPS = true;
}
