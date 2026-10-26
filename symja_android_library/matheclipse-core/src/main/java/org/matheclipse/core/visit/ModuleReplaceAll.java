package org.matheclipse.core.visit;

import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method
 * returns a non <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>
 * F.NIL</code> if no substitution occurred.
 */
public class ModuleReplaceAll extends VisitorExpr {
  final Map<ISymbol, ? extends IExpr> fModuleVariables;
  final int fOffset;
  final EvalEngine fEngine;
  private final String moduleCounter;
  /**
   * Whether the parameters of a nested <code>Function</code> are always renamed, or, like the
   * locals of <code>Module</code> and <code>With</code>, only when they collide with a symbol mapped
   * to {@link F#NIL}.
   */
  private final boolean renameFunctionParameters;

  /**
   * Whether the variables of a nested scoping construct are renamed by appending <code>$</code>
   * when a substituted variable reaches inside it, as <code>Module</code> and <code>With</code> do:
   * the parameters of a <code>Function</code> and the pattern variables of a rule or an assignment
   * <code>lhs = rhs</code>, <code>lhs := rhs</code>, <code>lhs -&gt; rhs</code>,
   * <code>lhs :&gt; rhs</code>. <code>With({a = 1}, Hold(f(t_) := t + a))</code> is
   * <code>Hold(f(t$_) := t$ + 1)</code>.
   */
  private final boolean renameReachedScopes;

  /**
   * The renamed variables <code>name$</code> of the nested scopes, shared by all visitors of one
   * <code>Module</code> or <code>With</code> evaluation: two rules in its body which bind the same
   * name get the same <code>name$</code>.
   */
  private final Map<String, IExpr> reachedScopeNames;

  public ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter) {
    this(moduleVariables, engine, moduleCounter, 0);
  }

  /**
   * The visitor which substitutes the variables of a <code>Module</code> or <code>With</code> in
   * its body, renaming the variables of the nested scoping constructs which the substitution
   * reaches into.
   *
   * @param moduleVariables the local variables and their substitutes
   * @param engine the evaluation engine
   * @param moduleCounter the suffix for renamed locals of a nested <code>Module</code> or
   *        <code>With</code> which collide with a substituted variable
   */
  public static ModuleReplaceAll scopingConstruct(Map<ISymbol, ? extends IExpr> moduleVariables,
      EvalEngine engine, String moduleCounter) {
    return new ModuleReplaceAll(moduleVariables, engine, moduleCounter, 0, true, true,
        new java.util.HashMap<String, IExpr>());
  }

  public ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter, int offset) {
    this(moduleVariables, engine, moduleCounter, offset, true);
  }

  /**
   * @param renameFunctionParameters <code>false</code> to rename the parameters of a nested
   *        <code>Function</code> only when they collide with a symbol mapped to {@link F#NIL}
   */
  public ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter, int offset, boolean renameFunctionParameters) {
    this(moduleVariables, engine, moduleCounter, offset, renameFunctionParameters, false, null);
  }

  private ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter, int offset, boolean renameFunctionParameters,
      boolean renameReachedScopes, Map<String, IExpr> reachedScopeNames) {
    this.fModuleVariables = moduleVariables;
    this.fOffset = offset;
    this.fEngine = engine;
    this.moduleCounter = moduleCounter;
    this.renameFunctionParameters = renameFunctionParameters;
    this.renameReachedScopes = renameReachedScopes;
    this.reachedScopeNames = reachedScopeNames;
  }

  private IExpr apply(final ISymbol arg) {
    IExpr temp = fModuleVariables.get(arg);
    return temp != null ? temp : F.NIL;
  }

  @Override
  public IExpr visit(ISymbol element) {
    return apply(element);
  }

  /**
   * A local variable written as the head a pattern must have - <code>a_s</code>, <code>_s</code>,
   * <code>b__s</code> - is as much an occurrence of the variable as a bare <code>s</code>.
   * <code>With[{s = f}, g[a_s] := ...]</code> is how a definition is made for each of several heads
   * at once (WLJS's <code>BoxesWorkarounds.wl</code> does it for the legend heads), and leaving the
   * head test alone defined a rule for the head <code>s</code>, which no expression has.
   *
   * @return the substituted head test, or <code>null</code> when it does not change
   */
  private IExpr substitutedHeadTest(IExpr headTest) {
    if (headTest != null && headTest.isSymbol()) {
      IExpr value = apply((ISymbol) headTest);
      if (value.isPresent()) {
        return value;
      }
    }
    return null;
  }

  /** The pattern name after substitution: the same symbol when it is not a local variable. */
  private ISymbol substitutedName(ISymbol symbol) {
    if (symbol != null) {
      IExpr expr = apply(symbol);
      if (expr.isPresent() && expr.isSymbol()) {
        return (ISymbol) expr;
      }
    }
    return symbol;
  }

  @Override
  public IExpr visit(IPattern element) {
    ISymbol symbol = element.getSymbol();
    ISymbol newSymbol = substitutedName(symbol);
    IExpr newHeadTest = substitutedHeadTest(element.getHeadTest());
    if (newSymbol == symbol && newHeadTest == null) {
      return F.NIL;
    }
    IExpr headTest = newHeadTest != null ? newHeadTest : element.getHeadTest();
    if (symbol == null) {
      return F.$b(headTest, element.isPatternDefault());
    }
    return F.$p(newSymbol, headTest, element.isPatternDefault());
  }

  @Override
  public IExpr visit(IPatternSequence element) {
    ISymbol symbol = element.getSymbol();
    ISymbol newSymbol = substitutedName(symbol);
    IExpr newHeadTest = substitutedHeadTest(element.getHeadTest());
    if (newSymbol == symbol && newHeadTest == null) {
      return F.NIL;
    }
    IExpr headTest = newHeadTest != null ? newHeadTest : element.getHeadTest();
    IPatternSequence result =
        F.$ps(newSymbol, headTest, element.isDefault(), element.isNullSequence());
    return element.isLongest() ? result.withLongest(true) : result;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (ast.isSameHeadSizeGE(S.Function, 2)) {
      return visitNestedScope(ast, true);
    } else if (ast.isWith()) {
      return visitNestedScope(ast, false).orElse(ast);
    } else if (ast.isModule()) {
      return visitNestedScope(ast, false).orElse(ast);
    } else if (renameReachedScopes && ast.size() == 3 && (ast.isAST(S.Set)
        || ast.isAST(S.SetDelayed) || ast.isAST(S.Rule) || ast.isAST(S.RuleDelayed))) {
      return visitRuleScope(ast);
    }
    return visitAST(ast, this, fOffset);
  }

  /**
   * A rule or an assignment binds the pattern variables of its left-hand side. They are renamed to
   * <code>name$</code> if a substituted variable occurs in the rule; a substitution of the head of
   * the left-hand side only - <code>Module({h}, h(y_) = z)</code> - does not rename them. A pattern
   * variable which is a substituted variable itself, or whose name ends with <code>$</code>
   * already, is left as it is.
   */
  private IAST visitRuleScope(IAST ast) {
    java.util.Set<ISymbol> patternSymbols =
        java.util.Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>());
    collectPatternSymbols(ast.arg1(), patternSymbols);
    // a free symbol of a substituted value - With({a = x}, f(x_) := x + a) - is renamed, the
    // value would be captured by the pattern otherwise
    patternSymbols.removeIf(symbol -> {
      IExpr value = fModuleVariables.get(symbol);
      return (value != null && value.isPresent()) || isRenamed(symbol);
    });
    if (patternSymbols.isEmpty() || !(isReachedBySubstitution(ast.arg2(), patternSymbols)
        || isLHSReachedBySubstitution(ast.arg1(), patternSymbols))) {
      return visitAST(ast, this, fOffset);
    }
    IdentityHashMap<ISymbol, IExpr> variables = new IdentityHashMap<>(fModuleVariables);
    for (ISymbol symbol : patternSymbols) {
      variables.put(symbol, reachedScopeName(symbol));
    }
    return visitAST(ast, new ModuleReplaceAll(variables, fEngine, moduleCounter, 0,
        renameFunctionParameters, true, reachedScopeNames), fOffset);
  }

  /**
   * Whether a substituted variable occurs in the left-hand side <code>lhs</code> of a rule apart
   * from its head: in the arguments, as <code>f(x_, a)</code>, or in a condition, as
   * <code>f(x_) /; x &gt; a</code>.
   */
  private boolean isLHSReachedBySubstitution(IExpr lhs,
      java.util.Set<? extends IExpr> locals) {
    if (lhs.isCondition()) {
      return isLHSReachedBySubstitution(lhs.first(), locals)
          || isReachedBySubstitution(lhs.second(), locals);
    }
    if (lhs.isAST()) {
      return ((IAST) lhs).exists(arg -> isReachedBySubstitution(arg, locals));
    }
    return false;
  }

  /** Whether the name of <code>symbol</code> ends with <code>$</code>, as a renamed variable's. */
  private static boolean isRenamed(ISymbol symbol) {
    return symbol.getSymbolName().endsWith("$");
  }

  /**
   * The renamed variable <code>name$</code> of a nested scope, the same one for the same name. It
   * is the symbol <code>name$</code> of the current context, the one an input <code>name$</code>
   * reads as; a variable of the dummy context is renamed to a dummy symbol.
   */
  private IExpr reachedScopeName(ISymbol symbol) {
    final String name = symbol.getSymbolName() + "$";
    final boolean dummy = symbol.getContext() == org.matheclipse.core.expression.Context.DUMMY;
    java.util.function.Function<String, IExpr> create =
        dummy ? F::Dummy : n -> F.symbol(n, fEngine);
    if (reachedScopeNames == null) {
      return create.apply(name);
    }
    return reachedScopeNames.computeIfAbsent(name, create);
  }

  /** Collect the names of the patterns <code>x_, x__, x : p, ...</code> in <code>expr</code>. */
  private static void collectPatternSymbols(IExpr expr, java.util.Set<ISymbol> symbols) {
    if (expr instanceof IPattern) {
      ISymbol symbol = ((IPattern) expr).getSymbol();
      if (symbol != null) {
        symbols.add(symbol);
      }
      if (expr instanceof PatternNested) {
        collectPatternSymbols(((PatternNested) expr).getPatternExpr(), symbols);
      }
    } else if (expr instanceof IPatternSequence) {
      ISymbol symbol = ((IPatternSequence) expr).getSymbol();
      if (symbol != null) {
        symbols.add(symbol);
      }
    } else if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        collectPatternSymbols(ast.get(i), symbols);
      }
    }
  }

  /**
   * Test if a variable which is substituted by this visitor - other than the bound
   * <code>locals</code> - occurs in <code>expr</code>.
   */
  private boolean isReachedBySubstitution(IExpr expr, java.util.Set<? extends IExpr> locals) {
    return !expr.isFree(x -> {
      if (x.isSymbol() && !locals.contains(x)) {
        IExpr value = fModuleVariables.get(x);
        return value != null && value.isPresent();
      }
      return false;
    }, true);
  }

  /**
   * Handle nested Module(), With() or Function()
   *
   * @param ast
   * @param isFunction <code>ast</code> has the form <code>Function(a1, a2)</code>
   * @return
   */
  private IAST visitNestedScope(IAST ast, boolean isFunction) {
    IAST localVariablesList = F.NIL;
    if (isFunction) {
      if (ast.isAST2()) {
        // extract formal parameters of Function(x,body). Null is not one of them: it says the
        // function binds nothing at all
        if (ast.arg1() == S.Null) {
          localVariablesList = F.NIL;
        } else if (ast.arg1().isSymbol()) {
          localVariablesList = F.list(ast.arg1());
        } else if (ast.arg1().isList()) {
          localVariablesList = (IAST) ast.arg1();
        }
      }
    } else {
      if (ast.arg1().isSymbol()) {
        localVariablesList = F.list(ast.arg1());
      } else if (ast.arg1().isList()) {
        localVariablesList = (IAST) ast.arg1();
      }
    }
    ModuleReplaceAll visitor = this;
    if (localVariablesList.isPresent()) {
      IdentityHashMap<ISymbol, IExpr> variables;
      if (isFunction && renameReachedScopes) {
        variables = renamedFunctionParameters(ast, localVariablesList);
      } else {
        variables = renamedVariables(localVariablesList, isFunction);
      }
      if (variables != null) {
        visitor = new ModuleReplaceAll(variables, fEngine, moduleCounter, 0,
            renameFunctionParameters, renameReachedScopes, reachedScopeNames);
      }
    }
    return visitAST(ast, visitor, fOffset);
  }

  /**
   * The parameters of <code>Function(params, body)</code> are renamed to <code>name$</code> if a
   * substituted variable occurs in the body - <code>With({a = 1}, Hold(Function(x, a*x)))</code> is
   * <code>Hold(Function(x$, 1*x$))</code> - or if they are substituted variables themselves.
   *
   * @return <code>null</code> if no parameter is renamed
   */
  private IdentityHashMap<ISymbol, IExpr> renamedFunctionParameters(IAST function,
      IAST parameters) {
    java.util.Set<ISymbol> parameterSymbols =
        java.util.Collections.newSetFromMap(new IdentityHashMap<ISymbol, Boolean>());
    for (int i = 1; i < parameters.size(); i++) {
      if (parameters.get(i).isSymbol() && !isRenamed((ISymbol) parameters.get(i))) {
        parameterSymbols.add((ISymbol) parameters.get(i));
      }
    }
    boolean reached = isReachedBySubstitution(function.arg2(), parameterSymbols);
    IdentityHashMap<ISymbol, IExpr> variables = null;
    for (ISymbol symbol : parameterSymbols) {
      if (reached || fModuleVariables.containsKey(symbol)) {
        if (variables == null) {
          variables = new IdentityHashMap<>(fModuleVariables);
        }
        variables.put(symbol, reachedScopeName(symbol));
      }
    }
    return variables;
  }

  /**
   * Traverse the children of <code>ast</code> starting at <code>startIndex</code>, applying
   * <code>visitor</code> to each child. Returns a modified copy of <code>ast</code> if any child
   * was substituted, or {@link F#NIL} if no substitution occurred.
   *
   * @param ast the AST whose children are visited
   * @param visitor the visitor to apply to each child
   * @param startIndex the index to start traversal from (typically {@link #fOffset})
   * @return a modified copy of <code>ast</code>, or <code>F.NIL</code> if unchanged
   */
  private static IAST visitAST(IAST ast, ModuleReplaceAll visitor, int startIndex) {
    final int size = ast.size();
    int i = startIndex;
    while (i < size) {
      IExpr temp = ast.get(i).accept(visitor);
      if (temp.isPresent()) {
        // something was substituted — copy the AST and continue
        IASTMutable result = ast.setAtCopy(i++, temp);
        while (i < size) {
          temp = ast.get(i).accept(visitor);
          if (temp.isPresent()) {
            result.set(i, temp);
          }
          i++;
        }
        return result;
      }
      i++;
    }
    return F.NIL;
  }

  private IdentityHashMap<ISymbol, IExpr> renamedVariables(IAST localVariablesList,
      boolean isFunction) {
    IdentityHashMap<ISymbol, IExpr> variables = null;
    final String varAppend = moduleCounter;
    int size = localVariablesList.size();
    for (int i = 1; i < size; i++) {
      IExpr temp = localVariablesList.get(i);
      if (temp.isSymbol()) {
        ISymbol symbol = (ISymbol) temp;
        variables = putSingleVariable(symbol, variables, varAppend, isFunction);
      } else {
        if (temp.isAST(S.Set, 3)) {
          // lhs = rhs
          final IAST setFun = (IAST) temp;
          if (setFun.arg1().isSymbol()) {
            ISymbol symbol = (ISymbol) setFun.arg1();
            variables = putSingleVariable(symbol, variables, varAppend, isFunction);
          }
        }
      }
    }
    return variables;
  }

  private IdentityHashMap<ISymbol, IExpr> putSingleVariable(ISymbol symbol,
      IdentityHashMap<ISymbol, IExpr> variables, final String varAppend, boolean isFunction) {
    IExpr temp = fModuleVariables.get(symbol);
    if (isFunction && renameFunctionParameters) {
      if (variables == null) {
        variables = new IdentityHashMap<>(fModuleVariables);
      }
      variables.put(symbol, F.Dummy(symbol.toString() + varAppend));
    } else if (temp != null) {
      if (variables == null) {
        variables = new IdentityHashMap<>(fModuleVariables);
      }
      variables.remove(symbol);
      if (temp.isNIL()) {
        variables.put(symbol, F.Dummy(symbol.toString() + varAppend));
      }
    }
    return variables;
  }
}
