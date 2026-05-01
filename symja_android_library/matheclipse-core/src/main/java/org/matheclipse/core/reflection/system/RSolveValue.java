package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * RSolveValue(eqns, expr, n)
 * </pre>
 */
public class RSolveValue extends AbstractFunctionEvaluator {

  public RSolveValue() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 3) {
      return F.NIL;
    }

    IExpr eqns = ast.arg1();
    IExpr expr = ast.arg2();
    IExpr nVar = ast.arg3();

    // Intelligently extract dependent variables from the target expression
    IExpr dependentVars = expr;
    java.util.Set<IExpr> vars = new java.util.LinkedHashSet<>();

    expr.accept(new org.matheclipse.core.visit.VisitorExpr() {
      @Override
      public IExpr visitAST(IAST node) {
        if (node.argSize() == 1 && !node.isFree(nVar)) {
          IExpr head = node.head();
          ISymbol targetSymbol = null;

          IAST[] derivativeAST1 = node.isDerivativeAST1();
          // Case 1: Standard function, e.g., a(n)
          if (derivativeAST1 == null && head.isSymbol()) {
            targetSymbol = (ISymbol) head;
          } else if (derivativeAST1 != null && head.argSize() == 1 && head.first().isSymbol()) {
            // Case 2: Derivative form, should it ever arise in RSolve expressions
            targetSymbol = (ISymbol) head.first();
          }

          // Add the bare symbol (e.g., 'a') to force RSolve to output a Function rule
          if (targetSymbol != null && !targetSymbol.isBuiltInSymbol()) {
            vars.add(targetSymbol);
          }
        }
        return super.visitAST(node);
      }
    });

    if (!vars.isEmpty()) {
      if (vars.size() == 1) {
        dependentVars = vars.iterator().next();
      } else {
        IASTAppendable list = F.ListAlloc(vars.size());
        for (IExpr v : vars) {
          list.append(v);
        }
        dependentVars = list;
      }
    }

    // Delegate to the core RSolve function
    IAST rsolveAST = F.ternaryAST3(S.RSolve, eqns, dependentVars, nVar);
    IExpr rsolveResult = engine.evaluate(rsolveAST);

    // Map the generated Rules back onto the requested expression
    if (rsolveResult.isList()) {
      IAST resultList = (IAST) rsolveResult;

      if (resultList.isEmptyList()) {
        return resultList;
      }

      IASTAppendable values = F.ListAlloc(resultList.argSize());

      for (int i = 1; i <= resultList.argSize(); i++) {
        IExpr rules = resultList.get(i);
        if (rules.isList()) {
          // Ensure pure functions are fully evaluated into their expressions
          IExpr val = engine.evaluate(F.subst(expr, (IAST) rules));
          values.append(val);
        } else {
          return F.NIL;
        }
      }

      if (values.argSize() == 1) {
        return values.arg1();
      }
      return values;
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }
}