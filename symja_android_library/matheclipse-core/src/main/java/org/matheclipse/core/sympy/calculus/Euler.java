package org.matheclipse.core.sympy.calculus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Euler-Lagrange equations for a given Lagrangian. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/calculus/euler.py">sympy/calculus/euler.py</a>
 */
public class Euler {

  private Euler() {}

  /**
   * Find the Euler-Lagrange equations for a given Lagrangian.
   *
   * @param L the Lagrangian that should be a function of the functions listed in
   *        <code>funcs</code> and their derivatives
   * @param funcs the list of functions <code>f(x,y,...)</code> that the Lagrangian depends on
   * @param vars the list of symbols that are the independent variables of the functions
   * @return the list of differential equations <code>expr==0</code>, one for each function
   * @throws ValueError if the variables don't match the arguments of the functions
   */
  public static IAST eulerEquations(IExpr L, IAST funcs, IAST vars) {
    // >>> x = Function('x')
    // >>> t = Symbol('t')
    // >>> L = (x(t).diff(t))**2/2 - x(t)**2/2
    // >>> euler_equations(L, x(t), t)
    // [Eq(-x(t) - Derivative(x(t), (t, 2)), 0)]
    // >>> u = Function('u')
    // >>> x = Symbol('x')
    // >>> L = (u(t, x).diff(t))**2/2 - (u(t, x).diff(x))**2/2
    // >>> euler_equations(L, u(t, x), [t, x])
    // [Eq(-Derivative(u(t, x), (t, 2)) + Derivative(u(t, x), (x, 2)), 0)]
    IASTAppendable eqns = F.ListAlloc(funcs.argSize());
    for (int i = 1; i < funcs.size(); i++) {
      IExpr eq = variationalD(L, funcs.get(i), vars);
      // new_eq = Eq(eq, 0)
      // if isinstance(new_eq, Eq):
      IExpr newEq = EvalEngine.get().evaluate(F.Equal(eq, F.C0));
      if (newEq.isEqual()) {
        eqns.append(newEq);
      }
    }
    return eqns;
  }

  /**
   * The variational derivative of the Lagrangian <code>L</code> with respect to the function
   * <code>func</code>; that's the left-hand-side of the Euler-Lagrange equation.
   *
   * @param L the Lagrangian
   * @param func the function <code>f(x,y,...)</code>
   * @param vars the list of symbols that are the independent variables of the function
   * @return
   * @throws ValueError if the variables don't match the arguments of the function
   */
  public static IExpr variationalD(IExpr L, IExpr func, IAST vars) {
    if (!func.isAST() || func.head().isBuiltInSymbol() || !func.head().isSymbol()) {
      throw new ValueError("Function expected, got: " + func);
    }
    final IAST f = (IAST) func;
    for (int i = 1; i < vars.size(); i++) {
      if (!vars.get(i).isSymbol()) {
        throw new ValueError("Variables are not symbols, got " + vars);
      }
    }
    // if not vars == f.args:
    if (f.argSize() != vars.argSize() || !f.apply(F.List).equals(vars)) {
      throw new ValueError("Variables " + vars + " do not match args: " + f);
    }

    // replace the function and its derivatives with dummy variables
    Map<IExpr, ISymbol> dummies = new LinkedHashMap<IExpr, ISymbol>();
    Map<ISymbol, int[]> orders = new HashMap<ISymbol, int[]>();
    ISymbol fDummy = F.Dummy();
    dummies.put(f, fDummy);
    orders.put(fDummy, new int[vars.argSize()]);
    collectDerivatives(L, f, dummies, orders);

    EvalEngine engine = EvalEngine.get();
    IExpr lagrangian = F.subst(L, dummies);
    Map<IExpr, IExpr> backSubstitution = new HashMap<IExpr, IExpr>();
    for (Map.Entry<IExpr, ISymbol> entry : dummies.entrySet()) {
      backSubstitution.put(entry.getValue(), entry.getKey());
    }

    // eq = diff(L, f)
    // for i in range(1, order + 1):
    // for p in combinations_with_replacement(vars, i):
    // eq = eq + S.NegativeOne**i*diff(L, diff(f, *p), *p)
    IASTAppendable eq = F.PlusAlloc(dummies.size());
    for (ISymbol dummy : dummies.values()) {
      IExpr term = engine.evaluate(F.D(lagrangian, dummy));
      if (term.isZero()) {
        continue;
      }
      term = F.subst(term, backSubstitution);
      int[] p = orders.get(dummy);
      int total = 0;
      IASTAppendable derivative = F.ast(S.D, p.length + 1);
      derivative.append(term);
      for (int i = 0; i < p.length; i++) {
        if (p[i] > 0) {
          total += p[i];
          derivative.append(F.List(vars.get(i + 1), F.ZZ(p[i])));
        }
      }
      if (total > 0) {
        term = engine.evaluate(derivative);
      }
      eq.append((total & 1) == 1 ? F.Negate(term) : term);
    }
    return engine.evaluate(eq);
  }

  /**
   * Collect all derivatives <code>Derivative(n1,n2,...)[f][x1,x2,...]</code> of the function
   * <code>f(x1,x2,...)</code> which occur in <code>expr</code>.
   */
  private static void collectDerivatives(IExpr expr, IAST f, Map<IExpr, ISymbol> dummies,
      Map<ISymbol, int[]> orders) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    IAST[] derivative = ast.isDerivative();
    if (derivative != null && derivative[2] != null) {
      if (derivative[1].arg1().equals(f.head()) && ast.argSize() == f.argSize()
          && ast.apply(F.List).equals(f.apply(F.List))) {
        if (!dummies.containsKey(ast)) {
          int[] p = new int[f.argSize()];
          for (int i = 0; i < p.length; i++) {
            p[i] = derivative[0].get(i + 1).toIntDefault();
            if (p[i] < 0) {
              throw new ValueError("Derivative order expected, got: " + derivative[0]);
            }
          }
          ISymbol dummy = F.Dummy();
          dummies.put(ast, dummy);
          orders.put(dummy, p);
        }
        return;
      }
    }
    for (int i = 0; i < ast.size(); i++) {
      collectDerivatives(ast.get(i), f, dummies, orders);
    }
  }
}
