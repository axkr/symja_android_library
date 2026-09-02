package org.matheclipse.compile;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Rewrites the compound assignments - <code>x += y</code> and its relatives - of an expression
 * which is about to be compiled into the <code>Set</code> they stand for.
 *
 * <p>
 * Without this the code generator has no case for them, so they reach its symbolic fallback, which
 * writes <code>F.AddTo(...)</code> into the generated source. There is no such factory method on
 * {@link F}, and the generated class then fails to compile with <i>a method named "AddTo" is not
 * declared</i>. Adding the missing factory methods would not be enough: the symbolic form assigns
 * to the <code>ExprTrie</code> entry of the variable while the generated code reads the numeric
 * field, so the two would disagree and the compiled function would quietly return the wrong value -
 * which is exactly what <code>Increment</code>, whose factory method does exist, does today.
 * Rewriting to <code>Set</code> instead routes the assignment through the code generator's
 * <code>Set</code> case, which writes the field.
 *
 * <p>
 * The rewrite runs once over the whole expression before the analyzer sees it, so that the analyzer
 * and the code generator work on the same nodes: the analyzer records inferred types in an
 * {@link java.util.IdentityHashMap}, so a second, separately built copy of the same rewrite would
 * not be found in it.
 *
 * <p>
 * <code>Increment</code> and <code>Decrement</code> are deliberately not rewritten here. They
 * return the value the variable had <i>before</i> the assignment, which no arithmetic around a
 * <code>Set</code> can express in a form the code generator keeps numeric, so
 * {@link CompileFactory} generates them directly.
 */
public final class CompoundAssignment {

  private CompoundAssignment() {}

  /**
   * Rewrite every compound assignment in <code>expression</code>, innermost first.
   *
   * @param expression the expression to rewrite
   * @return an expression with the compound assignments replaced, or <code>expression</code> itself
   *         if it contains none
   */
  public static IExpr normalize(IExpr expression) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST ast = (IAST) expression;

    // rewrite the arguments first, so that a compound assignment nested in another one is dealt
    // with before the enclosing rewrite copies it
    IASTMutable rewrittenArgs = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      IExpr rewritten = normalize(argument);
      if (rewritten != argument) {
        if (rewrittenArgs.isNIL()) {
          rewrittenArgs = ast.copy();
        }
        rewrittenArgs.set(i, rewritten);
      }
    }

    IAST result = rewrittenArgs.isPresent() ? rewrittenArgs : ast;
    IAST rewritten = rewrite(result);
    return rewritten.isPresent() ? rewritten : result;
  }

  /**
   * The <code>Set</code> a single compound assignment stands for.
   *
   * <p>
   * Only an assignment to a variable is rewritten. <code>x += y</code> becomes
   * <code>x = x + y</code>, which mentions the left hand side twice - harmless for a variable, but
   * a second evaluation of anything else.
   *
   * @param ast the expression to rewrite
   * @return the equivalent <code>Set</code>, or {@link F#NIL} if <code>ast</code> is not a compound
   *         assignment this rewrites
   */
  public static IAST rewrite(IAST ast) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.AddTo:
        return binary(ast) ? F.Set(ast.arg1(), F.Plus(ast.arg1(), ast.arg2())) : F.NIL;
      case ID.SubtractFrom:
        return binary(ast) ? F.Set(ast.arg1(), F.Subtract(ast.arg1(), ast.arg2())) : F.NIL;
      case ID.TimesBy:
        return binary(ast) ? F.Set(ast.arg1(), F.Times(ast.arg1(), ast.arg2())) : F.NIL;
      case ID.DivideBy:
        return binary(ast) ? F.Set(ast.arg1(), F.Divide(ast.arg1(), ast.arg2())) : F.NIL;
      case ID.PreIncrement:
        return unary(ast) ? F.Set(ast.arg1(), F.Plus(ast.arg1(), F.C1)) : F.NIL;
      case ID.PreDecrement:
        return unary(ast) ? F.Set(ast.arg1(), F.Subtract(ast.arg1(), F.C1)) : F.NIL;
      default:
        return F.NIL;
    }
  }

  private static boolean binary(IAST ast) {
    return ast.isAST2() && ast.arg1().isVariable();
  }

  private static boolean unary(IAST ast) {
    return ast.isAST1() && ast.arg1().isVariable();
  }
}
