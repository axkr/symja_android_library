package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>Dt(f, x)</code> - the total derivative of <code>f</code> with respect to <code>x</code>.
 *
 * <p>
 * Every symbol in <code>f</code> which isn't a constant is assumed to depend on <code>x</code>. The
 * total derivative is assembled from the partial derivatives of {@link D}:
 *
 * <pre>
 * Dt(f, x) = D(f, x) + D(f, y1)*Dt(y1, x) + D(f, y2)*Dt(y2, x) + ...
 * Dt(f)    = D(f, y1)*Dt(y1) + D(f, y2)*Dt(y2) + ...
 * </pre>
 *
 * so all the differentiation rules of <code>D</code> (special functions, <code>Piecewise</code>,
 * <code>Integrate</code>, ...) are available for <code>Dt</code> too.
 */
public class Dt extends AbstractFunctionOptionEvaluator {

  public Dt() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine, IAST originalAST) {
    for (int i = 2; i <= argSize; i++) {
      if (ast.get(i).isRuleAST()) {
        // an unknown option was given - leave the expression unevaluated
        return F.NIL;
      }
    }
    IAST constantsList = option[0].makeList();
    IExpr f = ast.arg1();

    // Dt(f) -> total differential
    if (argSize == 1) {
      return totalDerivative(f, F.NIL, constantsList, engine);
    }

    // Dt(f, x1, x2, ...) -> successive derivatives
    if (argSize > 2) {
      IExpr temp = f;
      boolean evaluated = false;
      for (int i = 2; i <= argSize; i++) {
        IExpr xi = ast.get(i);
        if (temp.isAST(S.Dt) && i > 2) {
          // an unevaluated Dt(y, x1, ...) - fold the variable in without evaluating, otherwise the
          // folded Dt(y, x1, xi) would come back here
          IExpr folded = foldSpec((IAST) temp, xi);
          if (folded.isPresent()) {
            temp = folded;
            continue;
          }
        }
        IExpr nextDt = buildDt(temp, xi, constantsList);
        IExpr evalNext = engine.evaluateNIL(nextDt);
        if (evalNext.isPresent()) {
          temp = evalNext;
          evaluated = true;
        } else {
          temp = nextDt;
        }
      }
      if (temp.equals(ast) || temp.equals(originalAST)) {
        return F.NIL;
      }
      return evaluated || temp.isAST(S.Dt) ? temp : F.NIL;
    }

    IExpr x = ast.arg2();

    // Dt(f, {x, n})
    if (x.isList()) {
      IAST xList = (IAST) x;
      if (xList.isAST2() && xList.arg2().isInteger()) {
        IExpr variable = xList.arg1();
        int n = xList.arg2().toIntDefault();
        if (n >= 0) {
          IExpr temp = f;
          for (int i = 0; i < n; i++) {
            IExpr evalNext = engine.evaluateNIL(buildDt(temp, variable, constantsList));
            if (evalNext.isNIL()) {
              // an unevaluated step like Dt(y, x) would be folded back into Dt(y, {x, n}) forever
              return F.NIL;
            }
            temp = evalNext;
          }
          return temp;
        }
      }
      return F.NIL;
    }
    if (!isDifferentiationVariable(x)) {
      return F.NIL;
    }
    return totalDerivative(f, x, constantsList, engine);
  }

  /**
   * Evaluate the total derivative <code>Dt(f, x)</code>, or the total differential
   * <code>Dt(f)</code> if <code>x</code> is {@link F#NIL}.
   */
  private static IExpr totalDerivative(final IExpr f, final IExpr x, final IAST constants,
      EvalEngine engine) {
    if (f.isList() || f.isAST(S.Equal)) {
      // thread over lists and equations
      return ((IAST) f).mapThread(buildDt(F.Slot1, x, constants), 1);
    }
    if (isConstant(f, constants)) {
      return F.C0;
    }
    if (x.isPresent() && f.equals(x)) {
      return F.C1;
    }
    if (f.isAST(S.Dt)) {
      return x.isPresent() ? nestedDt((IAST) f, x) : F.NIL;
    }
    if (!f.isAST()) {
      // keeps Dt(y, x) and Dt(y) unevaluated
      return F.NIL;
    }
    return chainRule((IAST) f, x, constants, engine);
  }

  /**
   * Apply the chain rule <code>Dt(f, x) = D(f, x) + Sum(D(f, y)*Dt(y, x))</code> over all the
   * symbols <code>y</code> which <code>f</code> depends on.
   *
   * <p>
   * A nested <code>Dt(...)</code> expression is treated like an independent variable, and a
   * function with a constant head like an independent constant, while the partial derivatives are
   * computed; both are substituted back afterwards.
   *
   * @param f the function
   * @param x the differentiation variable, or {@link F#NIL} for the total differential
   * @param constants the value of the {@link S#Constants} option
   * @param engine
   * @return
   */
  private static IExpr chainRule(final IAST f, final IExpr x, final IAST constants,
      EvalEngine engine) {
    final Map<IExpr, IExpr> dummyToOriginal = new HashMap<IExpr, IExpr>();
    final Map<IExpr, IExpr> originalToDummy = new HashMap<IExpr, IExpr>();
    final Set<IExpr> variableDummies = new HashSet<IExpr>();
    IExpr g = F.subst(f, node -> {
      boolean isDt = node.isAST(S.Dt);
      if (isDt || (node.isAST() && isConstantHead(node.head(), constants))) {
        IExpr dummy = originalToDummy.get(node);
        if (dummy == null) {
          dummy = F.Dummy();
          originalToDummy.put(node, dummy);
          dummyToOriginal.put(dummy, node);
          if (isDt) {
            variableDummies.add(dummy);
          }
        }
        return dummy;
      }
      return F.NIL;
    });

    Set<IExpr> variables = new LinkedHashSet<IExpr>();
    collectVariables(g, variables, new HashSet<IExpr>(), constants);
    // a dummy for a function with a constant head is a constant
    variables.removeIf(v -> dummyToOriginal.containsKey(v) && !variableDummies.contains(v));
    if (x.isPresent()) {
      // the partial derivative D(g, x) already accounts for the symbols in x
      if (x.isSymbol()) {
        variables.remove(x);
      } else {
        variables.removeIf(v -> !x.isFree(v, true));
      }
    }

    IASTAppendable plus = F.PlusAlloc(variables.size() + 1);
    if (x.isPresent()) {
      plus.append(F.D(g, x));
    }
    for (IExpr y : variables) {
      IExpr original = dummyToOriginal.get(y);
      plus.append(F.Times(F.D(g, y), buildDt(original == null ? y : original, x, constants)));
    }
    IExpr result = engine.evaluate(plus.oneIdentity0());
    if (!dummyToOriginal.isEmpty()) {
      result = engine.evaluate(F.subst(result, dummyToOriginal));
    }
    return result;
  }

  /**
   * Fold a derivative of an unevaluated <code>Dt(...)</code> into a single <code>Dt</code> with
   * the variables in canonical order, like in Mathematica:
   *
   * <pre>
   * Dt(Dt(y, z), x)      -> Dt(y, x, z)
   * Dt(Dt(y, x), x)      -> Dt(y, {x, 2})
   * Dt(Dt(y, {x, n}), x) -> Dt(y, {x, n + 1})
   * </pre>
   *
   * @param dtAst the inner <code>Dt(y, spec1, spec2, ...)</code> expression
   * @param x the variable of the outer derivative
   * @return {@link F#NIL} for a total differential <code>Dt(y)</code> or a non-integer order
   */
  private static IExpr nestedDt(final IAST dtAst, final IExpr x) {
    Map<IExpr, Integer> orders = new TreeMap<IExpr, Integer>();
    orders.put(x, 1);
    IASTAppendable options = F.ListAlloc();
    for (int i = 2; i < dtAst.size(); i++) {
      IExpr spec = dtAst.get(i);
      if (spec.isRuleAST()) {
        options.append(spec);
        continue;
      }
      IExpr variable = spec;
      int n = 1;
      if (spec.isList()) {
        if (!spec.isAST2() || !spec.second().isInteger()) {
          return F.NIL;
        }
        variable = spec.first();
        n = spec.second().toIntDefault();
        if (n < 0) {
          return F.NIL;
        }
      }
      orders.merge(variable, n, Integer::sum);
    }
    if (orders.size() == 1 && orders.get(x) == 1) {
      // the total differential Dt(y) isn't folded
      return F.NIL;
    }
    IASTAppendable result = F.ast(S.Dt, orders.size() + options.size() + 1);
    result.append(dtAst.arg1());
    for (Map.Entry<IExpr, Integer> entry : orders.entrySet()) {
      int n = entry.getValue();
      if (n == 1) {
        result.append(entry.getKey());
      } else if (n > 1) {
        result.append(F.List(entry.getKey(), F.ZZ(n)));
      }
    }
    result.appendArgs(options);
    return result;
  }

  /**
   * Fold the variable specification <code>x</code> or <code>{x, n}</code> into the unevaluated
   * <code>dtAst</code>.
   */
  private static IExpr foldSpec(final IAST dtAst, final IExpr spec) {
    if (spec.isList()) {
      if (!spec.isAST2() || !spec.second().isInteger()) {
        return F.NIL;
      }
      int n = spec.second().toIntDefault();
      if (n < 0) {
        return F.NIL;
      }
      IExpr result = dtAst;
      for (int i = 0; i < n && result.isPresent(); i++) {
        result = nestedDt((IAST) result, spec.first());
      }
      return result;
    }
    return spec.isRuleAST() ? F.NIL : nestedDt(dtAst, spec);
  }

  /**
   * Collect the symbols in <code>expr</code> which <code>expr</code> can depend on. Function heads,
   * constants and variables which are bound by a scoping construct like <code>Sum</code>,
   * <code>Integrate</code>, <code>Function</code> or <code>Module</code> are skipped.
   */
  private static void collectVariables(IExpr expr, Set<IExpr> variables, Set<IExpr> bound,
      IAST constants) {
    if (expr.isSymbol()) {
      ISymbol symbol = (ISymbol) expr;
      if (symbol.isVariable() && !symbol.isBuiltInSymbolID() && !constants.contains(symbol)
          && !bound.contains(symbol)) {
        variables.add(symbol);
      }
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    switch (ast.headID()) {
      case ID.Sum:
      case ID.Product:
      case ID.NSum:
      case ID.NProduct:
      case ID.Table:
      case ID.Do:
      case ID.Integrate:
      case ID.NIntegrate:
        if (ast.argSize() >= 2) {
          Set<IExpr> innerBound = new HashSet<IExpr>(bound);
          for (int i = 2; i < ast.size(); i++) {
            IExpr iterator = ast.get(i);
            if (iterator.isList() && iterator.argSize() >= 1) {
              if (iterator.first().isSymbol()) {
                innerBound.add(iterator.first());
              }
              for (int j = 2; j < ((IAST) iterator).size(); j++) {
                collectVariables(((IAST) iterator).get(j), variables, bound, constants);
              }
            } else if (iterator.isSymbol()) {
              innerBound.add(iterator);
            } else {
              collectVariables(iterator, variables, bound, constants);
            }
          }
          collectVariables(ast.arg1(), variables, innerBound, constants);
          return;
        }
        break;
      case ID.Function:
        if (ast.argSize() >= 2) {
          Set<IExpr> innerBound = new HashSet<IExpr>(bound);
          if (ast.arg1().isList()) {
            ((IAST) ast.arg1()).forEach(innerBound::add);
          } else {
            innerBound.add(ast.arg1());
          }
          collectVariables(ast.arg2(), variables, innerBound, constants);
          return;
        }
        break;
      case ID.Module:
      case ID.Block:
      case ID.With:
        if (ast.isAST2() && ast.arg1().isList()) {
          Set<IExpr> innerBound = new HashSet<IExpr>(bound);
          for (IExpr local : (IAST) ast.arg1()) {
            if (local.isAST(S.Set, 3) || local.isAST(S.SetDelayed, 3)) {
              innerBound.add(local.first());
              collectVariables(local.second(), variables, bound, constants);
            } else {
              innerBound.add(local);
            }
          }
          collectVariables(ast.arg2(), variables, innerBound, constants);
          return;
        }
        break;
      default:
        break;
    }
    // the head is a function name or operator, only the arguments are variables
    ast.forEach(arg -> collectVariables(arg, variables, bound, constants));
  }

  /**
   * Test if <code>x</code> can be used as the differentiation variable of <code>Dt(f, x)</code>.
   */
  private static boolean isDifferentiationVariable(IExpr x) {
    if (x.isVariable()) {
      return true;
    }
    return x.isAST() && !x.isList() && !x.isPlusTimesPower() && !x.isRuleAST();
  }

  /**
   * Checks if an expression is considered constant per Dt definitions.
   */
  private static boolean isConstant(IExpr expr, IAST constants) {
    if (expr.isNumber() || expr.isString()) {
      return true;
    }
    if (expr.isSymbol()) {
      ISymbol sym = (ISymbol) expr;
      return sym.isConstantAttribute() || constants.contains(sym);
    }
    return expr.isAST() && isConstantHead(expr.head(), constants);
  }

  /**
   * A function whose head has the {@link ISymbol#CONSTANT} attribute or is listed in the
   * {@link S#Constants} option is a constant.
   */
  private static boolean isConstantHead(IExpr head, IAST constants) {
    if (!head.isSymbol() || head.isBuiltInSymbolID()) {
      return false;
    }
    return ((ISymbol) head).isConstantAttribute() || constants.contains(head);
  }

  /**
   * Helper utility for creating nested `Dt` nodes to feed back into the evaluation loop.
   */
  private static IAST buildDt(IExpr expr, IExpr x, IAST constants) {
    IASTAppendable dt = F.ast(S.Dt);
    dt.append(expr);
    if (x.isPresent()) {
      dt.append(x);
    }
    if (constants.isList() && !constants.isEmpty()) {
      dt.append(F.Rule(S.Constants, constants));
    }
    return dt;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, S.Constants, F.CEmptyList);
  }
}
