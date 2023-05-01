package org.matheclipse.core.visit;

import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
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
  final String moduleCounter;

  public ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter) {
    this(moduleVariables, engine, moduleCounter, 0);
  }

  public ModuleReplaceAll(Map<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
      String moduleCounter, int offset) {
    this.fModuleVariables = moduleVariables;
    this.fOffset = offset;
    this.fEngine = engine;
    this.moduleCounter = moduleCounter;
  }

  private IExpr apply(final ISymbol arg) {
    IExpr temp = fModuleVariables.get(arg);
    return temp != null ? temp : F.NIL;
  }

  @Override
  public IExpr visit(ISymbol element) {
    return apply(element);
  }

  @Override
  public IExpr visit(IPattern element) {
    ISymbol symbol = element.getSymbol();
    if (symbol != null) {
      IExpr expr = apply(symbol);
      if (expr.isSymbol()) {
        return F.$p((ISymbol) expr, element.getHeadTest(), element.isPatternDefault());
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr visit(IPatternSequence element) {
    ISymbol symbol = element.getSymbol();
    if (symbol != null) {
      IExpr expr = apply(symbol);
      if (expr.isPresent() && expr.isSymbol()) {
        return F.$ps((ISymbol) expr, element.getHeadTest(), element.isDefault(),
            element.isNullSequence());
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    if (ast.isSameHeadSizeGE(S.Function, 2)) {
      return visitNestedScope(ast, true);
    } else if (ast.isWith()) {
      return visitNestedScope(ast, false).orElse(ast);
    } else if (ast.isModule()) {
      return visitNestedScope(ast, false).orElse(ast);
    }
    return visitASTModule(ast);
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
        // extract formal parameters of Function(x,body)
        if (ast.arg1().isSymbol()) {
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
      IdentityHashMap<ISymbol, IExpr> variables = renamedVariables(localVariablesList, isFunction);
      if (variables != null) {
        visitor = new ModuleReplaceAll(variables, fEngine, moduleCounter);
      }
    }

    IExpr temp;
    int i = fOffset;
    while (i < ast.size()) {
      temp = ast.get(i).accept(visitor);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IASTMutable result = ast.setAtCopy(i++, temp);
        while (i < ast.size()) {
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
    if (isFunction) {
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

  private IExpr visitASTModule(IAST ast) {
    IExpr temp;
    int i = fOffset;
    final int size = ast.size();
    while (i < size) {
      temp = ast.get(i).accept(this);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IASTMutable result = ast.setAtCopy(i++, temp);
        ast.forEach(i, size, (x, j) -> {
          IExpr t = x.accept(this);
          if (t.isPresent()) {
            result.set(j, t);
          }
        });
        return result;
      }
      i++;
    }
    return F.NIL;
  }
}
