package org.matheclipse.compile;

import java.util.ArrayDeque;
import java.util.Deque;
import org.matheclipse.compile.expression.CompiledFunctionExpr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Expands a call to another compiled function into the body of the function being compiled.
 *
 * <p>
 * Generated code has no way to reach a {@link CompiledFunctionExpr}: the symbol it is assigned to
 * is written into the source as a bare Java identifier which nothing declares, and the generated
 * class fails to compile with <i>Unknown variable or type</i>. Inlining is therefore the only way a
 * compiled function can call another one, which is why <code>Automatic</code> inlines every such
 * call. Expansion is recursive, so a function which calls a function which calls a third one comes
 * out flat.
 *
 * <p>
 * The body of the called function is alpha-renamed before its arguments are substituted in. Without
 * that, a <code>Module</code> in the called function captures any argument which mentions a
 * variable of the same name - and a noise function whose parameters are <code>x, y, z</code> called
 * with the caller's <code>x, y, z</code> is exactly the shape which hits it.
 */
public final class InlineDefinitions {

  private InlineDefinitions() {}

  /**
   * Expand the calls to compiled functions in <code>expression</code>.
   *
   * @param expression the body which is about to be compiled
   * @param options the <code>CompilationOptions</code> of the enclosing <code>Compile</code>
   * @param ast the <code>Compile</code> or <code>CompilePrint</code> expression, whose head names
   *        the symbol any message is reported for
   * @param engine the evaluation engine
   * @return the body with the calls expanded, or <code>expression</code> itself if it has none
   */
  public static IExpr inline(IExpr expression, CompilationOptions options, IAST ast,
      EvalEngine engine) {
    if (!options.isInlineCompiledFunctions()) {
      return expression;
    }
    return new InlineDefinitions().expand(expression, ast, engine);
  }

  /** The symbols currently being expanded, innermost last, to recognize a recursive call. */
  private final Deque<ISymbol> expanding = new ArrayDeque<>();

  /** Counts the names handed out by {@link #renameScopeVariables(IExpr)}. */
  private int renamed = 1;

  private IExpr expand(IExpr expression, IAST ast, EvalEngine engine) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST current = expandArguments((IAST) expression, ast, engine);

    IExpr head = current.head();
    if (!head.isSymbol() || head.isBuiltInSymbol()) {
      return current;
    }
    ISymbol symbol = (ISymbol) head;
    CompiledFunctionExpr called = compiledFunctionOf(symbol, engine);
    if (called == null) {
      return current;
    }
    if (expanding.contains(symbol)) {
      // The compiled function `1` calls itself; the call is not expanded.
      Errors.printMessage(ast.topHead(), "cfrec", F.list(symbol), engine);
      return current;
    }
    IAST variables = called.getVariables();
    if (current.argSize() != variables.argSize()) {
      // The number of arguments `1` does not match the length `2` of the argument template.
      Errors.printMessage(ast.topHead(), "cfct",
          F.List(F.ZZ(current.argSize()), F.ZZ(variables.argSize())), engine);
      return current;
    }

    IExpr body = renameScopeVariables(called.getExpr());
    IExpr inlined = F.subst(body, Functors.equalRules(variables, current));

    expanding.addLast(symbol);
    try {
      // the body which was just pasted in may call further compiled functions
      return expand(inlined, ast, engine);
    } finally {
      expanding.removeLast();
    }
  }

  /** Expand the arguments of <code>ast</code>, leaving its head alone. */
  private IAST expandArguments(IAST ast, IAST compileAST, EvalEngine engine) {
    IASTMutable result = F.NIL;
    for (int i = 1; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      IExpr expanded = expand(argument, compileAST, engine);
      if (expanded != argument) {
        if (result.isNIL()) {
          result = ast.copy();
        }
        result.set(i, expanded);
      }
    }
    return result.isPresent() ? result : ast;
  }

  /**
   * The compiled function assigned to <code>symbol</code>, or <code>null</code> if it has no value
   * or its value is something else.
   */
  private static CompiledFunctionExpr compiledFunctionOf(ISymbol symbol, EvalEngine engine) {
    IExpr value = symbol.assignedValue();
    if (value == null || !value.isPresent()) {
      return null;
    }
    return value instanceof CompiledFunctionExpr ? (CompiledFunctionExpr) value : null;
  }

  /**
   * Give every <code>Module</code>, <code>Block</code> and <code>With</code> in
   * <code>expression</code> fresh local names.
   *
   * <p>
   * Renaming is unconditional rather than only on a collision: it costs nothing at run time - the
   * names end up as local variables of the generated method - and a rename which is always applied
   * cannot be wrong about which names would have collided.
   */
  private IExpr renameScopeVariables(IExpr expression) {
    if (!expression.isAST()) {
      return expression;
    }
    IAST ast = (IAST) expression;
    IAST scope = ast;
    if (isScope(ast)) {
      IAST rules = freshNames((IAST) ast.arg1());
      if (rules.argSize() > 0) {
        IExpr substituted = F.subst(ast, rules);
        if (substituted.isAST()) {
          scope = (IAST) substituted;
        }
      }
    }

    // recurse into the arguments, so that a scope nested in this one gets its own fresh names
    IASTMutable result = F.NIL;
    for (int i = 1; i < scope.size(); i++) {
      IExpr argument = scope.get(i);
      IExpr rewritten = renameScopeVariables(argument);
      if (rewritten != argument) {
        if (result.isNIL()) {
          result = scope.copy();
        }
        result.set(i, rewritten);
      }
    }
    return result.isPresent() ? result : scope;
  }

  private static boolean isScope(IAST ast) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol() || ast.argSize() != 2 || !ast.arg1().isList()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Module:
      case ID.Block:
      case ID.With:
        return true;
      default:
        return false;
    }
  }

  /**
   * A rule for each variable declared in <code>localList</code>, renaming it to a name which has
   * not been handed out before.
   */
  private IAST freshNames(IAST localList) {
    IASTAppendable rules = F.ListAlloc(localList.argSize());
    for (int i = 1; i < localList.size(); i++) {
      IExpr local = localList.get(i);
      if (local.isAST(org.matheclipse.core.expression.S.Set, 3)) {
        local = local.first();
      }
      if (local.isSymbol()) {
        // `$` keeps the name out of the way of anything written by hand, and is a legal character
        // in the Java identifier the code generator makes of it
        rules.append(F.Rule(local, F.symbol(local.toString() + "$" + renamed++)));
      }
    }
    return rules;
  }
}
