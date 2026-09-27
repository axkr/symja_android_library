package org.matheclipse.core.patternmatching;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.ModuleReplaceAll;

/**
 * Capture-avoiding substitution into the scoping constructs <code>Module</code>,
 * <code>With</code> and <code>Function</code>.
 *
 * <p>
 * When the values of a rule's patterns, or the arguments of a pure function, are substituted into
 * such a construct, a local variable of the construct must not capture a symbol of the same name
 * occurring free in a value. WMA renames such a local first:
 * <code>g[v_] := Module[{e = 1}, v + e]; g[e + 1]</code> is <code>2 + e</code>, not
 * <code>3</code>. Only colliding locals are renamed. A local named like a pattern variable is not
 * shielded: <code>sh[e_] := Module[{e = 1}, e + 1]; sh[99]</code> substitutes 99 into the local
 * list, as WMA does. <code>Block</code>, <code>Table</code> and the other iterators scope
 * dynamically and aren't renamed (WMA).
 */
public final class CaptureAvoidance {

  private CaptureAvoidance() {}

  /**
   * The local variables of the <code>Module</code>, <code>With</code> and named
   * <code>Function</code> constructs in <code>expr</code>.
   *
   * @return an empty set if <code>expr</code> has no such construct
   */
  public static Set<ISymbol> scopeLocals(IExpr expr) {
    Set<ISymbol> locals = new HashSet<ISymbol>();
    collectLocals(expr, locals);
    return locals;
  }

  private static void collectLocals(IExpr expr, Set<ISymbol> locals) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if ((ast.isModule() || ast.isWith() || ast.isAST(S.Function, 3) || ast.isAST(S.Function, 4))
        && ast.arg1() != S.Null) {
      IAST list = ast.arg1().isList() ? (IAST) ast.arg1() : F.list(ast.arg1());
      for (IExpr local : list) {
        if (local.isSymbol()) {
          locals.add((ISymbol) local);
        } else if (local.isAST(S.Set, 3) && local.first().isSymbol()) {
          locals.add((ISymbol) local.first());
        }
      }
    }
    for (int i = 0; i < ast.size(); i++) {
      collectLocals(ast.get(i), locals);
    }
  }

  /** Whether one of the <code>values</code> contains one of the <code>locals</code>. */
  public static boolean collides(Iterable<? extends IExpr> values, Set<ISymbol> locals) {
    for (IExpr value : values) {
      if (value != null && value.isPresent()
          && !value.isFree(x -> x.isSymbol() && locals.contains(x), true)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Rename the locals of the <code>Module</code>, <code>With</code> and <code>Function</code>
   * constructs in <code>body</code> which occur free in one of the <code>values</code>.
   *
   * @param body the expression the values will be substituted into
   * @param values the values to be substituted; <code>null</code> or {@link F#NIL} entries are
   *        ignored
   * @param substituted the symbols the values replace, which are substituted as they are
   * @return the body with the colliding locals renamed, or {@link F#NIL} if nothing collides
   */
  public static IExpr renameColliding(IExpr body, Iterable<? extends IExpr> values,
      Iterable<ISymbol> substituted, EvalEngine engine) {
    Map<ISymbol, IExpr> freeSymbols = new IdentityHashMap<ISymbol, IExpr>();
    for (IExpr value : values) {
      if (value != null && value.isPresent() && !value.isNumber() && !value.isString()) {
        new VariablesSet(value).initSymbols(freeSymbols);
      }
    }
    for (ISymbol symbol : substituted) {
      freeSymbols.remove(symbol);
    }
    if (freeSymbols.isEmpty()) {
      return F.NIL;
    }
    return body.accept(
        new ModuleReplaceAll(freeSymbols, engine, EvalEngine.uniqueName("$"), 0, false));
  }
}
