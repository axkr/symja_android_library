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
 * DSolveValue(eqns, expr, x)
 * </pre>
 * 
 */
public class DSolveValue extends AbstractFunctionEvaluator {

  public DSolveValue() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 3) {
      return F.NIL;
    }

    IExpr eqns = ast.arg1();
    IExpr expr = ast.arg2();
    IExpr xVar = ast.arg3();

    // Intelligently extract dependent variables from the target expression
    IExpr dependentVars = expr;
    java.util.Set<IExpr> vars = new java.util.LinkedHashSet<>();

    expr.accept(new org.matheclipse.core.visit.VisitorExpr() {
      @Override
      public IExpr visitAST(IAST node) {
        // Find any 1-argument function that depends on xVar
        if (node.argSize() == 1 && !node.isFree(xVar)) {
          IExpr head = node.head();
          ISymbol targetSymbol = null;

          IAST[] derivativeAST1 = node.isDerivativeAST1();
          // Case 1: Standard function, e.g., y(x)
          if (derivativeAST1 == null && head.isSymbol()) {
            targetSymbol = (ISymbol) head;
          } else if (derivativeAST1 != null && head.argSize() == 1 && head.first().isSymbol()) {
            // Case 2: Derivative form, e.g., Derivative(1)[y][x]
            targetSymbol = (ISymbol) head.first();
          }

          // If a valid non-builtin symbol was found, add it to our variables list
          if (targetSymbol != null && !targetSymbol.isBuiltInSymbol()) {
            // Add the bare symbol (e.g., 'y') to force DSolve to output a Function rule
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

    // Delegate to the core DSolve function
    IAST dsolveAST = F.ternaryAST3(S.DSolve, eqns, dependentVars, xVar);
    IExpr dsolveResult = engine.evaluate(dsolveAST);

    // Map the generated Rules back onto the requested expression
    if (dsolveResult.isList()) {
      IAST resultList = (IAST) dsolveResult;

      // If no solution found, return the empty list {}
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

      // If there is only one unique solution branch, strip the outer list
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
