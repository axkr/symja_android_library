package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * DSolve(equation, f(var), var)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Attempts to solve a linear differential <code>equation</code> for the function
 * <code>f(var)</code> and variable <code>var</code>.
 * </p>
 * </blockquote>
 *
 * <p>
 * Solves ordinary differential equations, systems of them, differential algebraic equations, and
 * partial differential equations in two independent variables. The methods are shared out over
 * {@link DSolveODE}, {@link DSolveSystem} and {@link DSolvePDE}; this class reads the arguments,
 * separates the conditions from the equations, dispatches on the shape of the problem and puts the
 * answer together.
 * </p>
 *
 * <p>
 * Delay differential equations, integral and integro-differential equations, and hybrid systems
 * are not solved.
 * </p>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; DSolve({y'(x)==y(x)+2},y(x), x)
 * {{y(x)-&gt;-2+E^x*C(1)}}
 *
 * &gt;&gt; DSolve({y''(x) + y(x) == 0}, y(x), x)
 * {{y(x)-&gt;C(1)*Cos(x)+C(2)*Sin(x)}}
 * </pre>
 */
public class DSolve extends AbstractFunctionOptionEvaluator {

  /** The index of the GeneratedParameters option in the option array. */
  private static final int GENERATED_PARAMETERS = 0;

  private static boolean checkDSolveEquation(IAST equations, IExpr expr,
      IASTAppendable termsEqualNumberList) {
    if (expr.isASTSizeGE(S.Equal, 3)) {
      IAST equal = (IAST) expr;
      IExpr last = equal.last();
      for (int i = 1; i < equal.argSize(); i++) {
        IExpr temp = F.evalExpandAll(F.Subtract(equal.get(i), last));
        termsEqualNumberList.append(temp);
      }
      return true;
    }
    // Equation or list of equations expected instead of `1` in the first argument `2`.
    Errors.printMessage("deqn", F.List(expr, equations));
    return false;
  }

  /**
   * Rewrite a list of equations <code>a==b</code> into the list of expanded expressions
   * <code>a-b</code> which should be equal to <code>0</code>.
   *
   * <p>
   * Also used by {@link NDSolve}, which needs the same normalization: the expansion is what lets a
   * system such as <code>u''(t)+v''(t)==-(u(t)+v(t))</code> be separated into terms and solved for
   * its highest derivatives.
   *
   * @param listOrAndAST a list of equations
   * @return the expressions which should be zero, or {@link F#NIL} if an argument is not an
   *         equation
   */
  public static IASTAppendable checkDSolveEquations(final IAST listOrAndAST) {
    IASTAppendable termsEqualNumberList = F.ListAlloc(listOrAndAST.argSize());
    for (int i = 1; i < listOrAndAST.size(); i++) {
      if (!checkDSolveEquation(listOrAndAST, listOrAndAST.get(i), termsEqualNumberList)) {
        return F.NIL;
      }
    }
    return termsEqualNumberList;
  }

  public DSolve() {}

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.GeneratedParameters}, //
        new IExpr[] {S.C});
  }

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    // an `And(...)` of equations is equivalent to a `List(...)` of equations
    IAST arg1 = SolveUtils.toEquationList(ast.arg1()).makeList();

    // The unknown and the variable it depends on can be read off the equations when they are not
    // given: DSolve(y'(x) == y(x)) asks about y and x, and there is nothing else it could mean.
    IExpr arg2 = argSize >= 2 ? ast.arg2() : F.NIL;
    IExpr arg3 = argSize >= 3 ? ast.arg3() : F.NIL;
    if (arg2.isNIL() || arg3.isNIL()) {
      IExpr[] inferred = inferUnknowns(arg1, arg2, engine);
      if (inferred == null) {
        return F.NIL;
      }
      arg2 = inferred[0];
      arg3 = inferred[1];
      if (inferred[2].isTrue()) {
        // The equation was written without arguments, as in DSolve(y' == y), so it has to be
        // rewritten with them before it can be solved, and the answer is a pure function.
        arg1 = (IAST) applyArguments(arg1, arg2, arg3, engine);
      }
    }
    IExpr result = solve(ast, arg1, arg2, arg3, engine);
    if (result.isNIL()) {
      return F.NIL;
    }
    IExpr parameterHead = options[GENERATED_PARAMETERS];
    if (parameterHead.isPresent() && !parameterHead.equals(S.C)) {
      result = engine.evaluate(renameParameters(result, parameterHead));
    }
    return result;
  }

  private IExpr solve(final IAST ast, IAST arg1, IExpr arg2, IExpr arg3, EvalEngine engine) {

    IASTAppendable listOfEquations = checkDSolveEquations(arg1);
    if (listOfEquations.isNIL()) {
      return F.NIL;
    }
    // Trying a method the equation does not belong to is a step of the algorithm, not something to
    // report, and those attempts are where the InverseFunction and Solve warnings came from.
    // DSolve's own messages are collected in the context and shown once the cascade has finished,
    // because the quiet mode would otherwise swallow them too.
    DSolveContext context = new DSolveContext(engine, F.CEmptyList);
    final boolean quietMode = engine.isQuietMode();
    try {
      engine.setQuietMode(true);
      // Intercept Partial Differential Equations (PDEs)
      if (arg3.isList()) {
        int savedCounter = engine.getConstantCounter();
        try {
          IExpr equation = arg1;
          IAST pdeConditions = F.CEmptyList;
          if (arg1.isList()) {
            if (arg1.argSize() == 1) {
              equation = arg1.arg1();
            } else if (arg2.isList()) {
              // System of PDEs
              IExpr pdeResult = DSolvePDE.solveSystemPDE(arg1, (IAST) arg2, (IAST) arg3,
                  context);
              if (pdeResult.isPresent()) {
                return pdeResult;
              }
              return F.NIL;
            } else {
              // One equation together with the conditions it has to satisfy. The equation is the
              // one which differentiates the unknown; the others prescribe values.
              IExpr head = arg2.isAST() ? arg2.head() : arg2;
              IASTAppendable conditions = F.ListAlloc(arg1.argSize());
              IExpr found = F.NIL;
              for (int i = 1; i <= arg1.argSize(); i++) {
                IExpr candidate = ((IAST) arg1).get(i);
                if (DSolvePDE.differentiates(candidate, head)) {
                  if (found.isPresent()) {
                    return F.NIL;
                  }
                  found = candidate;
                } else {
                  conditions.append(candidate);
                }
              }
              if (found.isNIL()) {
                return F.NIL;
              }
              equation = found;
              pdeConditions = conditions;
            }
          }
          IExpr pdeResult = DSolvePDE.solvePDE(equation, arg2, (IAST) arg3,
              context.withConditions(pdeConditions));
          if (pdeResult.isPresent()) {
            return pdeResult;
          }
          return F.NIL;
        } finally {
          engine.setConstantCounter(savedCounter);
        }
      }


      IExpr xVar = arg3;

      IASTAppendable listOfVariables = F.ListAlloc();

      if (arg2.isList()) {
        listOfVariables = F.ListAlloc(((IAST) arg2).argSize());
        for (int i = 1; i <= ((IAST) arg2).argSize(); i++) {
          IExpr v = ((IAST) arg2).get(i);
          if (v.isAST1() && v.first().equals(xVar)) {
            listOfVariables.append(v);
          } else if (v.isSymbol() && xVar.isSymbol()) {
            listOfVariables.append(F.unaryAST1(v, xVar));
          } else {
            return F.NIL;
          }
        }
      } else if (arg2.isAST1() && arg2.first().equals(xVar)) {
        listOfVariables.append(arg2);
        if (arg1.isFree(arg2.head()) || arg1.isFree(xVar)) {
          return F.NIL;
        }
      } else if (arg2.isSymbol() && xVar.isSymbol()) {
        if (arg1.isFree(arg2) || arg1.isFree(xVar)) {
          return F.NIL;
        }
        listOfVariables.append(F.unaryAST1(arg2, xVar));
      }

      if (listOfVariables.isPresent()) {
        // Extract the bare symbols of the target functions (e.g., y(x) -> y)
        java.util.Set<IExpr> fHeads = new java.util.HashSet<>();
        for (int i = 1; i <= listOfVariables.argSize(); i++) {
          IExpr var = listOfVariables.get(i);
          fHeads.add(var.isAST() ? var.head() : var);
        }

        // Validate all equations for missing arguments on functions or derivatives
        for (int i = 1; i <= listOfEquations.argSize(); i++) {
          IExpr eq = listOfEquations.get(i);
          IExpr badVar = findMissingArgs(eq, fHeads);
          if (badVar.isPresent()) {
            // The function `1` appears with no arguments.
            return Errors.printMessage(S.DSolve, "dvnoarg", F.List(badVar));
          }
        }

        if (listOfVariables.argSize() == 1) {
          // Extract ALL boundary/initial conditions for unary ODEs
          // (equations free of the independent variable, e.g. y(0)==1, y'(0)==2)
          IASTAppendable boundaryConditions = F.ListAlloc();
          int i = 1;
          while (i <= listOfEquations.argSize()) {
            IExpr equation = listOfEquations.get(i);
            if (equation.isFree(xVar)) {
              boundaryConditions.append(equation);
              listOfEquations.remove(i);
            } else {
              i++;
            }
          }
          return DSolveODE.unaryODE((IAST) listOfVariables.arg1(),
              arg2.isList() ? ((IAST) arg2).arg1() : arg2, xVar, listOfEquations,
              boundaryConditions, context.withConditions(boundaryConditions));
        } else {
          // Extract boundary conditions for the system solver globally
          IASTAppendable bcs = F.ListAlloc();
          int i = 1;
          while (i <= listOfEquations.argSize()) {
            IExpr equation = listOfEquations.get(i);
            if (equation.isFree(xVar)) {
              bcs.append(equation);
              listOfEquations.remove(i);
            } else {
              i++;
            }
          }
          int nVars = listOfVariables.argSize();
          int nEqs = listOfEquations.argSize();

          if (nEqs < nVars) {
            // The system is underdetermined
            return Errors.printMessage(S.DSolve, "underdet", F.CEmptyList);
          } else if (nEqs > nVars) {
            // The system is overdetermined
            return Errors.printMessage(S.DSolve, "overdet", F.CEmptyList);
          }

          // Solve Linear System of ODEs / DAEs
          return DSolveSystem.solveSystemODE(listOfEquations, listOfVariables, xVar, bcs, arg2,
              context.withConditions(bcs));
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      engine.setQuietMode(quietMode);
      return Errors.printMessage(S.DSolve, rex);
    } finally {
      engine.setQuietMode(quietMode);
      context.flushMessages();
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_3;
  }

  /**
   * The unknown functions and the independent variable of an equation which does not name them.
   *
   * @param arg2 the unknown if it was given, {@link F#NIL} otherwise
   * @return <code>{unknown, variable, pureFunctionForm}</code>, or <code>null</code> if the
   *         equations do not determine them
   */
  private static IExpr[] inferUnknowns(IAST equations, IExpr arg2, EvalEngine engine) {
    IASTAppendable heads = F.ListAlloc();
    IASTAppendable variables = F.ListAlloc();
    collectUnknowns(equations, heads, variables);
    if (heads.argSize() == 0) {
      return null;
    }
    // Without arguments anywhere the equation cannot say what the variable is, and Wolfram uses x.
    boolean withoutArguments = variables.argSize() == 0;
    if (!withoutArguments && variables.argSize() != 1) {
      return null;
    }
    IExpr variable = withoutArguments ? F.x : variables.arg1();
    if (!variable.isSymbol()) {
      return null;
    }
    IExpr unknown;
    if (arg2.isPresent()) {
      unknown = arg2;
    } else if (heads.argSize() == 1) {
      // An equation which named its arguments is answered for y(x), one which did not for y.
      unknown = withoutArguments ? heads.arg1() : F.unaryAST1(heads.arg1(), variable);
    } else {
      IASTAppendable list = F.ListAlloc(heads.argSize());
      for (int i = 1; i <= heads.argSize(); i++) {
        list.append(withoutArguments ? heads.get(i) : F.unaryAST1(heads.get(i), variable));
      }
      unknown = list;
    }
    return new IExpr[] {unknown, variable, F.bool(withoutArguments)};
  }

  /** Collects the symbols which are differentiated, and the arguments they are applied to. */
  private static void collectUnknowns(IExpr expr, IASTAppendable heads,
      IASTAppendable variables) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    IAST[] derivative = ast.isDerivativeAST1();
    if (derivative != null && derivative[1].isAST1() && derivative[1].arg1().isSymbol()
        && !derivative[1].arg1().isBuiltInSymbol()) {
      IExpr head = derivative[1].arg1();
      if (!heads.contains(head)) {
        heads.append(head);
      }
      if (derivative[2] != null && derivative[2].isAST1() && derivative[2].first().isSymbol()
          && !variables.contains(derivative[2].first())) {
        variables.append(derivative[2].first());
      }
      return;
    }
    for (int i = 0; i < ast.size(); i++) {
      collectUnknowns(ast.get(i), heads, variables);
    }
  }

  /** Rewrites an equation written without arguments, as <code>y' == y</code>, to use them. */
  private static IExpr applyArguments(IExpr expr, IExpr unknown, IExpr variable,
      EvalEngine engine) {
    IAST unknowns = unknown.makeList();
    IExpr result = expr;
    for (int i = 1; i <= unknowns.argSize(); i++) {
      IExpr head = unknowns.get(i);
      if (!head.isSymbol()) {
        continue;
      }
      // The derivatives are parked under a name of their own first. Applying the variable to them
      // directly and only then to the function itself would reach the function inside the
      // derivative as well, turning Derivative(1)(y)(x) into Derivative(1)(y(x))(x).
      IExpr placeholder = F.Dummy("applied");
      for (int order = DSolveODE.MAX_DERIVATIVE_ORDER; order >= 1; order--) {
        IExpr bare = F.unaryAST1(F.Derivative(F.ZZ(order)), head);
        result = F.subst(result, bare, F.unaryAST1(F.Derivative(F.ZZ(order)), placeholder));
      }
      result = F.subst(result, head, F.unaryAST1(head, variable));
      for (int order = DSolveODE.MAX_DERIVATIVE_ORDER; order >= 1; order--) {
        IExpr parked = F.unaryAST1(F.Derivative(F.ZZ(order)), placeholder);
        result = F.subst(result, parked,
            F.unaryAST1(F.unaryAST1(F.Derivative(F.ZZ(order)), head), variable));
      }
    }
    return engine.evaluate(result);
  }

  /**
   * Renames the arbitrary constants and functions of a solution, for
   * <code>GeneratedParameters -&gt; f</code>.
   */
  private static IExpr renameParameters(IExpr expr, IExpr head) {
    if (expr.isAST(S.C, 2)) {
      return F.unaryAST1(head, expr.first());
    }
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    IASTAppendable result = F.ast(renameParameters(ast.head(), head), ast.argSize());
    for (int i = 1; i <= ast.argSize(); i++) {
      result.append(renameParameters(ast.get(i), head));
    }
    return result;
  }

  /**
   * Recursively scans an expression to find any target function or derivative that is missing its
   * independent variable arguments (e.g., 'y' or 'y^P' instead of 'y(x)').
   */
  private IExpr findMissingArgs(IExpr expr, java.util.Set<IExpr> fHeads) {
    // 1. Bare symbol check: e.g., 'y'
    if (expr.isSymbol() && fHeads.contains(expr)) {
      return expr;
    }

    // 2. Bare derivative check: e.g., 'Derivative(1)[y]'
    if (isDerivativeOf(expr, fHeads)) {
      return expr;
    }

    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      if (ast.isAST() && head.head().isAST(S.Derivative)) {
        if (!ast.isAST1()) {
          return ast.head();
        }
      }

      // Check all arguments
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr bad = findMissingArgs(ast.get(i), fHeads);
        if (bad.isPresent()) {
          return bad;
        }
      }

      // Check the head, BUT skip if this AST is a valid application:
      // a) y(x) -> head is y
      // b) Derivative(n)[y][x] -> head is Derivative(n)[y]
      boolean isValidApplication = false;
      if (fHeads.contains(head) || isDerivativeOf(head, fHeads)) {
        isValidApplication = true;
      }

      if (!isValidApplication) {
        IExpr bad = findMissingArgs(head, fHeads);
        if (bad.isPresent()) {
          return bad;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Checks if the given expression is structurally Derivative(...)[f] where f is one of the target
   * function symbols.
   */
  private boolean isDerivativeOf(IExpr expr, java.util.Set<IExpr> fHeads) {
    if (expr.isAST() && expr.head().isAST(S.Derivative)) {
      if (expr.isAST1() && fHeads.contains(((IAST) expr).arg1())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
