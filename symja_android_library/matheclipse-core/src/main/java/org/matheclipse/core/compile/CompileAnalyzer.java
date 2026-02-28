package org.matheclipse.core.compile;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Phase 1 Compiler: Static Analysis and Type Inference. Traverses an expression tree to infer
 * variable types, validate control flow boundaries (Break, Continue, Return), and build a lexical
 * scope hierarchy.
 */
public class CompileAnalyzer {

  public enum VarType {
    UNKNOWN, BOOLEAN, INTEGER, REAL, COMPLEX, SYMBOLIC;

    /**
     * Finds the most general type when combining two branches or operations.
     */
    public static VarType widen(VarType a, VarType b) {
      if (a == SYMBOLIC || b == SYMBOLIC)
        return SYMBOLIC;
      if (a == COMPLEX || b == COMPLEX)
        return COMPLEX;
      if (a == REAL || b == REAL)
        return REAL;
      if (a == INTEGER || b == INTEGER)
        return INTEGER;
      if (a == BOOLEAN && b == BOOLEAN)
        return BOOLEAN;
      return UNKNOWN;
    }
  }

  /**
   * Represents a lexical environment mapping symbols to their inferred types.
   */
  public static class Scope {
    public final Scope parent;
    public final Map<ISymbol, VarType> variables = new HashMap<>();

    public Scope(Scope parent) {
      this.parent = parent;
    }

    public void put(ISymbol sym, VarType type) {
      Scope curr = this;
      while (curr != null) {
        if (curr.variables.containsKey(sym)) {
          VarType existing = curr.variables.get(sym);
          curr.variables.put(sym, VarType.widen(existing, type));
          return;
        }
        curr = curr.parent;
      }
      variables.put(sym, type);
    }

    public VarType get(ISymbol sym) {
      Scope curr = this;
      while (curr != null) {
        if (curr.variables.containsKey(sym)) {
          return curr.variables.get(sym);
        }
        curr = curr.parent;
      }
      return VarType.UNKNOWN;
    }
  }

  private Scope currentScope = new Scope(null);
  private int loopDepth = 0;

  /**
   * Map specific AST node instances in memory to their resolved types.
   */
  private final Map<IExpr, VarType> nodeTypes = new IdentityHashMap<>();

  public CompileAnalyzer() {}

  public Map<IExpr, VarType> getNodeTypes() {
    return nodeTypes;
  }

  /**
   * Entry point for analyzing an expression.
   * 
   * @param expr the expression to analyze
   * @return the inferred VarType of the expression
   */
  public VarType analyze(IExpr expr) {
    VarType type = VarType.UNKNOWN;

    if (expr.isInteger()) {
      type = VarType.INTEGER;
    } else if (expr.isReal()) {
      type = VarType.REAL;
    } else if (expr.isComplex() || expr.isComplexNumeric()) {
      type = VarType.COMPLEX;
    } else if (expr.isSymbol()) {
      if (expr == S.True || expr == S.False) {
        type = VarType.BOOLEAN;
      } else {
        type = currentScope.get((ISymbol) expr);
      }
    } else if (expr.isAST()) {
      type = analyzeAST((IAST) expr);
    }

    nodeTypes.put(expr, type);
    return type;
  }

  private VarType analyzeAST(IAST ast) {
    IExpr head = ast.head();
    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Set:
        case ID.SetDelayed:
          return analyzeSet(ast);
        case ID.Module:
        case ID.Block:
        case ID.With:
          return analyzeScope(ast);
        case ID.While:
        case ID.Do:
        case ID.For:
          return analyzeLoop(ast);
        case ID.If:
        case ID.Which:
        case ID.Switch:
          return analyzeConditional(ast);
        case ID.CompoundExpression:
          return analyzeCompoundExpression(ast);
        case ID.Plus:
        case ID.Times:
        case ID.Power:
        case ID.Subtract:
        case ID.Divide:
          return analyzeMath(ast);
        case ID.Less:
        case ID.LessEqual:
        case ID.Greater:
        case ID.GreaterEqual:
        case ID.Equal:
        case ID.Unequal:
          return analyzeComparison(ast);
        case ID.Break:
        case ID.Continue:
        case ID.Return:
          return analyzeControlFlow(ast);
        default:
          break;
      }
    }

    for (int i = 1; i <= ast.argSize(); i++) {
      analyze(ast.get(i));
    }
    return VarType.SYMBOLIC;
  }

  private VarType analyzeSet(IAST ast) {
    if (ast.argSize() != 2)
      return VarType.UNKNOWN;

    VarType rhsType = analyze(ast.arg2());
    if (ast.arg1().isSymbol()) {
      currentScope.put((ISymbol) ast.arg1(), rhsType);
    }
    return rhsType;
  }

  private VarType analyzeScope(IAST ast) {
    if (ast.argSize() < 2)
      return VarType.UNKNOWN;

    currentScope = new Scope(currentScope);
    try {
      IExpr vars = ast.arg1();
      if (vars.isList()) {
        IAST list = (IAST) vars;
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr v = list.get(i);
          if (v.isSymbol()) {
            currentScope.variables.put((ISymbol) v, VarType.UNKNOWN);
          } else if (v.isAST(S.Set, 3) && v.first().isSymbol()) {
            VarType initType = analyze(v.second());
            currentScope.variables.put((ISymbol) v.first(), initType);
          }
        }
      }
      return analyze(ast.arg2());
    } finally {
      currentScope = currentScope.parent;
    }
  }

  private VarType analyzeLoop(IAST ast) {
    loopDepth++;
    try {
      for (int i = 1; i <= ast.argSize(); i++) {
        analyze(ast.get(i));
      }
      return VarType.SYMBOLIC;
    } finally {
      loopDepth--;
    }
  }

  private VarType analyzeConditional(IAST ast) {
    VarType mergedType = VarType.UNKNOWN;
    for (int i = 1; i <= ast.argSize(); i++) {
      VarType branchType = analyze(ast.get(i));
      if (i > 1) {
        mergedType = VarType.widen(mergedType, branchType);
      }
    }
    return mergedType;
  }

  private VarType analyzeCompoundExpression(IAST ast) {
    if (ast.argSize() == 0)
      return VarType.UNKNOWN;

    for (int i = 1; i < ast.argSize(); i++) {
      analyze(ast.get(i));
    }
    return analyze(ast.last());
  }

  private VarType analyzeMath(IAST ast) {
    VarType mergedType = VarType.UNKNOWN;
    for (int i = 1; i <= ast.argSize(); i++) {
      mergedType = VarType.widen(mergedType, analyze(ast.get(i)));
    }
    return mergedType;
  }

  private VarType analyzeComparison(IAST ast) {
    for (int i = 1; i <= ast.argSize(); i++) {
      analyze(ast.get(i));
    }
    return VarType.BOOLEAN;
  }

  private VarType analyzeControlFlow(IAST ast) {
    IExpr head = ast.head();
    if ((head == S.Break || head == S.Continue) && loopDepth == 0) {
      nodeTypes.put(ast, VarType.UNKNOWN);
      return VarType.UNKNOWN;
    }

    if (ast.argSize() == 1) {
      analyze(ast.arg1());
    }
    return VarType.SYMBOLIC;
  }
}
