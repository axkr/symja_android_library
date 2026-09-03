package org.matheclipse.compile;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Rewrites that normalize an expression for the compiler, run once before the analyzer and the
 * code generator see it.
 *
 * <p>
 * <code>Do(body, it1, it2, ..., itN)</code> with two or more iterators becomes
 * <code>Do(Do(...Do(body, itN)..., it2), it1)</code>. {@link F#Do} only has a two-argument
 * factory method (body plus one iterator), so a multi-iterator <code>Do</code> written out by the
 * symbolic fallback is source Janino cannot compile; nesting single-iterator <code>Do</code>s is
 * what a <code>Do</code> with several iterators means anyway, and the native loop generator
 * already knows how to compile one of those into a plain nested <code>for</code>.
 *
 * <p>
 * The rewrite is recursive, bottom-up, and returns the input unchanged when nothing applies, so
 * identity checks (<code>result == input</code>) are safe.
 */
public final class CompileRewrites {

  private CompileRewrites() {}

  /**
   * Rewrite <code>expression</code> for the compiler.
   *
   * @param expression the expression to rewrite
   * @return the rewritten expression, or <code>expression</code> itself if nothing changed
   */
  public static IExpr rewrite(IExpr expression) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST ast = (IAST) expression;

    // rewrite arguments first (bottom-up)
    IASTMutable rewrittenArgs = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      IExpr rewritten = rewrite(argument);
      if (rewritten != argument) {
        if (rewrittenArgs.isNIL()) {
          rewrittenArgs = ast.copy();
        }
        rewrittenArgs.set(i, rewritten);
      }
    }
    IAST result = rewrittenArgs.isPresent() ? rewrittenArgs : ast;

    IExpr local = applyLocal(result);
    return local != null ? local : result;
  }

  /** A single-step rewrite of <code>ast</code>, or <code>null</code> if none applies. */
  private static IExpr applyLocal(IAST ast) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return null;
    }
    if (((IBuiltInSymbol) head).ordinal() == ID.Do) {
      return rewriteDo(ast);
    }
    return null;
  }

  /**
   * <code>Do(body, it1, it2, ..., itN)</code> with two or more iterators &rarr;
   * <code>Do(Do(body, it2, ..., itN), it1)</code>.
   */
  private static IExpr rewriteDo(IAST ast) {
    if (ast.argSize() < 3) {
      return null;
    }
    // build from the innermost (last) iterator outward, so the nesting order matches what
    // Do(body, it1, it2, ..., itN) means: it1 is the outermost loop, itN the innermost
    IExpr inner = ast.arg1();
    for (int i = ast.argSize(); i >= 3; i--) {
      inner = F.Do(inner, ast.get(i));
    }
    return F.Do(inner, ast.arg2());
  }
}
