package org.matheclipse.compile;

import org.matheclipse.compile.builtin.CompilerFunctions;
import org.matheclipse.core.compile.IExprCompiler;

/**
 * Registers the <code>Compile</code>, <code>CompiledFunction</code> and <code>CompilePrint</code>
 * functions of the <code>matheclipse-compile</code> module with the evaluation engine, and installs
 * the {@link IExprCompiler} which core's numerical functions use. Call this after
 * <code>F.initSymja()</code>; <code>org.matheclipse.io.IOInit</code> already does so for the
 * servlets and the consoles.
 *
 * <p>
 * <code>matheclipse-core</code> owns the <code>Compile</code>, <code>CompiledFunction</code> and
 * <code>CompilePrint</code> symbols but no longer implements them: without this module they stay
 * unevaluated, exactly as <code>Molecule</code> does without <code>matheclipse-chem</code>. That is
 * deliberate for <code>matheclipse-api</code> and <code>matheclipse-discord</code>, which take
 * untrusted input and do not depend on this module.
 *
 * <p>
 * Whether <code>Compile</code> actually does anything once registered is still governed by
 * {@link org.matheclipse.core.basic.ToggleFeature#COMPILE} and
 * {@link org.matheclipse.core.basic.ToggleFeature#COMPILE_PRINT}, which are checked per evaluation.
 */
public class CompileInit {

  public static void init() {
    CompilerFunctions.initialize();
    IExprCompiler.install(new JaninoExprCompiler());
  }

  private CompileInit() {}
}
