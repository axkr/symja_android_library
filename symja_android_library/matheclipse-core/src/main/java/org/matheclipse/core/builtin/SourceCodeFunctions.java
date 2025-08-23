package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.BuiltInSymbol;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class SourceCodeFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FunctionURL.setEvaluator(new FunctionURL());
    }
  }

  /**
   *
   *
   * <pre>
   * <code>FunctionURL(built-in-symbol)</code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the GitHub URL of the <code>built-in-symbol</code> implementation in the
   * <a href="https://github.com/axkr/symja_android_library">Symja GitHub repository</a>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Get the GitHub URL of the <code>NIntegrate</code> function implementation:
   *
   * <pre>
   * <code>&gt;&gt; FunctionURL(NIntegrate)
   * https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NIntegrate.java#L71
   * </code>
   * </pre>
   */
  private static class FunctionURL extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isBuiltInSymbol()) {
        IBuiltInSymbol builtin = (IBuiltInSymbol) ast.arg1();
        String url = Documentation.functionURL(builtin);
        if (url != null) {
          return F.stringx(url);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * Return the implementation status of this symbol or function as a string.
   * <p>
   * <ul>
   * <li>&#x2705; - {@link ImplementationStatus#FULL_SUPPORT} the symbol / function is supported.
   * Note that this doesn't mean that every symbolic evaluation is supported.
   * <li>&#x2611; - {@link ImplementationStatus#PARTIAL_SUPPORT} the symbol / function is partially
   * implemented and might not support most basic features of the element
   * <li>&#x274C; - {@link ImplementationStatus#NO_SUPPORT} the symbol / function is currently not
   * supported
   * <li>&#x26A0; - {@link ImplementationStatus#DEPRECATED} the symbol / function is deprecated and
   * will not be further improved
   * <li>&#x1F9EA; - {@link ImplementationStatus#EXPERIMENTAL} the symbol / function is an
   * experimental implementation. It may not fully behave as expected.
   * </ul>
   * 
   */
  public static String statusAsString(ISymbol builtin) {
    int ordinal = builtin.ordinal();
    if (ordinal > 0 && ordinal < ID.LINE_NUMBER_OF_JAVA_CLASS.length) {
      int line = ID.LINE_NUMBER_OF_JAVA_CLASS[ordinal];
      if (line > 0) {
        IEvaluator evaluator = ((IBuiltInSymbol) builtin).getEvaluator();
        if (evaluator != null //
            && evaluator != BuiltInSymbol.DUMMY_EVALUATOR //
            && evaluator != ICoreFunctionEvaluator.ARGS_EVALUATOR) {
          int implementationStatus = evaluator.status();
          return ImplementationStatus.STATUS_STRINGS[implementationStatus];
        }
      }
    }
    return null;
  }

  public static String statusAsEmoji(ISymbol builtin) {
    int ordinal = builtin.ordinal();
    if (ordinal > 0 && ordinal < ID.LINE_NUMBER_OF_JAVA_CLASS.length) {
      int line = ID.LINE_NUMBER_OF_JAVA_CLASS[ordinal];
      if (line > 0) {
        IEvaluator evaluator = ((IBuiltInSymbol) builtin).getEvaluator();
        if (evaluator != null //
            && evaluator != BuiltInSymbol.DUMMY_EVALUATOR //
            && evaluator != ICoreFunctionEvaluator.ARGS_EVALUATOR) {
          int implementationStatus = evaluator.status();
          return ImplementationStatus.STATUS_EMOJIS[implementationStatus];
        }
      }
    }
    return null;
  }
  public static void initialize() {
    Initializer.init();
  }

  private SourceCodeFunctions() {}
}
