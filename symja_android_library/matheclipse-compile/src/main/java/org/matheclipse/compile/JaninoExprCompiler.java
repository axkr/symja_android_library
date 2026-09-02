package org.matheclipse.compile;

import org.matheclipse.compile.builtin.CompilerFunctions;
import org.matheclipse.compile.expression.CompiledFunctionExpr;
import org.matheclipse.core.compile.ICompiledFunction;
import org.matheclipse.core.compile.IExprCompiler;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The {@link IExprCompiler} implementation of this module: generates Java source with JavaPoet and
 * compiles it with Janino.
 *
 * <p>
 * Installed by {@link CompileInit#init()}. See {@link IExprCompiler} for why
 * <code>matheclipse-core</code> reaches this class through an interface instead of naming it.
 */
public class JaninoExprCompiler implements IExprCompiler {

  @Override
  public ICompiledFunction compileReal(IExpr function, ISymbol variable, EvalEngine engine) {
    CompiledFunctionArg[] args =
        new CompiledFunctionArg[] {new CompiledFunctionArg(variable, S.Real)};
    CompiledFunctionExpr compiled =
        CompilerFunctions.compile(F.Compile(F.NIL, function), args, F.CEmptyList,
            RuntimeOptions.DEFAULT, CompilationOptions.DEFAULT, engine);
    // compile(...) returns null for an expression it cannot handle; the contract of
    // IExprCompiler#compileReal passes that through unchanged.
    return compiled;
  }
}
