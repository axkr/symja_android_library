package org.matheclipse.core.compile;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Compiles a Symja expression into JVM bytecode.
 *
 * <p>
 * The implementation lives in the <code>matheclipse-compile</code> module, which
 * <code>matheclipse-core</code> must not depend on, so the dependency is inverted:
 * <code>org.matheclipse.compile.CompileInit#init()</code> installs an implementation here and core
 * calls it through this interface. The same pattern
 * {@link org.matheclipse.core.io.BioSequenceFormat} uses for <code>matheclipse-bio</code>.
 *
 * <p>
 * While no implementation is installed - on Android, or on any classpath without
 * <code>matheclipse-compile</code> - {@link #get()} returns <code>null</code> and every caller in
 * core has to evaluate the expression the interpreted way instead.
 */
public interface IExprCompiler {

  /**
   * The installed compiler, or <code>null</code> when <code>matheclipse-compile</code> is not
   * present.
   */
  IExprCompiler[] INSTANCE = new IExprCompiler[1];

  /** Install the compiler. Called from <code>org.matheclipse.compile.CompileInit</code>. */
  static void install(IExprCompiler compiler) {
    INSTANCE[0] = compiler;
  }

  /** @return the installed compiler, or <code>null</code> */
  static IExprCompiler get() {
    return INSTANCE[0];
  }

  /** @return <code>true</code> if a compiler is installed */
  static boolean isAvailable() {
    return INSTANCE[0] != null;
  }

  /**
   * Compile <code>function</code> of the single real <code>variable</code>.
   *
   * <p>
   * Compilation fails routinely - for an unsupported head, a symbolic subexpression, or a Java
   * compiler error - so callers must always keep a non-compiled path. Failures are reported through
   * {@link org.matheclipse.core.eval.Errors} and are not thrown.
   *
   * @param function the expression to compile
   * @param variable the single argument of the compiled function
   * @param engine the evaluation engine
   * @return the compiled function, or <code>null</code> if the expression cannot be compiled
   */
  ICompiledFunction compileReal(IExpr function, ISymbol variable, EvalEngine engine);
}
