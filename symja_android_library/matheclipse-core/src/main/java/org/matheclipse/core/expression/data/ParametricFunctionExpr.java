package org.matheclipse.core.expression.data;

import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The function of parameters which <code>ParametricNDSolveValue(eqns, expr, {t, t0, t1}, pars)
 * </code> returns. Applied to numeric values of the parameters it solves the differential equations
 * with these values and gives what <code>NDSolveValue(eqns, expr, {t, t0, t1})</code> gives for
 * them: an interpolating function, a number or a list of them.
 *
 * <p>
 * The stored data is the defining <code>ParametricNDSolveValue(...)</code> call. The solutions are
 * kept per parameter vector, so applying the function again to the same values doesn't solve the
 * equations again.
 *
 * <p>
 * The head of the object is <code>ParametricFunction</code>; calls with the object as head are
 * dispatched to the evaluator of <code>ParametricNDSolveValue</code>.
 */
public class ParametricFunctionExpr extends DataExpr<IAST> {

  private static final long serialVersionUID = -3415872310574112087L;

  /**
   * The most solutions kept per parametric function. A solution holds the whole dense output of
   * the integrator, so only a few recent parameter vectors are kept: a sweep over many values
   * doesn't pile up memory, a value used again shortly after isn't solved again.
   */
  private static final int CACHE_SIZE = 8;

  /** The head <code>ParametricFunction</code> of the objects. */
  public static final IBuiltInSymbol PARAMETRIC_FUNCTION = F.contextFunction(Context.SYSTEM,
      "ParametricFunction", new AbstractCoreFunctionEvaluator() {
        @Override
        public IExpr evaluate(IAST ast, EvalEngine engine) {
          return F.NIL;
        }
      });

  /** Let the parser answer the name <code>ParametricFunction</code> with {@link #PARAMETRIC_FUNCTION}. */
  public static void registerHead() {
    ContextPath.registerSystemAlias(Context.SYSTEM_CONTEXT_NAME + "ParametricFunction",
        PARAMETRIC_FUNCTION);
  }

  /**
   * Create the parametric function of a <code>ParametricNDSolveValue(...)</code> call.
   *
   * @param definition the call <code>ParametricNDSolveValue(eqns, expr, {t, t0, t1}, pars,
   *        options...)</code>
   * @param parameters the parameter symbols in the order of <code>pars</code>
   * @return the parametric function
   */
  public static ParametricFunctionExpr newInstance(IAST definition, IAST parameters) {
    return new ParametricFunctionExpr(definition, parameters);
  }

  /** The parameter symbols in the order the values are given. */
  private final IAST parameters;

  /** The solutions per parameter vector, the least recently used dropped first. */
  private transient Map<IAST, IExpr> solutions;

  private ParametricFunctionExpr(IAST definition, IAST parameters) {
    super(S.ParametricNDSolveValue, definition);
    this.parameters = parameters;
  }

  private synchronized Map<IAST, IExpr> solutions() {
    if (solutions == null) {
      solutions = new LinkedHashMap<IAST, IExpr>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<IAST, IExpr> eldest) {
          return size() > CACHE_SIZE;
        }
      };
    }
    return solutions;
  }

  /**
   * Apply the function to the parameter values in <code>ast</code>, which has this object as head.
   *
   * @param ast the call <code>pf(v1, v2, ...)</code>
   * @param engine the evaluation engine
   * @return the solution for these parameter values, or {@link F#NIL} if the values aren't
   *         numeric, their number doesn't match the number of parameters or the equations can't be
   *         solved
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.argSize() != parameters.argSize()) {
      // Too many parameters in `1` to be filled from `2`. - also given for too few values
      return Errors.printMessage(S.ParametricNDSolveValue, "fpct",
          F.list(parameters, ast.apply(S.List)), engine);
    }
    for (int i = 1; i < ast.size(); i++) {
      if (!ast.get(i).isNumericFunction(true)) {
        return F.NIL;
      }
    }
    IAST values = ast.apply(S.List);
    Map<IAST, IExpr> cache = solutions();
    IExpr cached;
    synchronized (this) {
      cached = cache.get(values);
    }
    if (cached != null) {
      return cached;
    }
    IAST definition = toData();
    IASTAppendable rules = F.ListAlloc(parameters.argSize());
    for (int i = 1; i < parameters.size(); i++) {
      // a parameter given more than once takes the value of its first position
      IExpr parameter = parameters.get(i);
      if (!rules.exists(rule -> rule.first().equals(parameter))) {
        rules.append(F.Rule(parameter, values.get(i)));
      }
    }
    // NDSolveValue(eqns, expr, {t, t0, t1}, options...) with the values in place of the parameters
    IASTAppendable call = F.ast(S.NDSolveValue, definition.argSize() - 1);
    for (int i = 1; i < definition.size(); i++) {
      if (i != 4) {
        call.append(definition.get(i));
      }
    }
    IExpr solution = engine.evaluate(F.subst(call, rules));
    if (solution.isAST(S.NDSolveValue) || solution.has(S.NDSolveValue, true)) {
      return F.NIL;
    }
    synchronized (this) {
      cache.put(values, solution);
    }
    return solution;
  }

  /** The parameter symbols in the order the values are given. */
  public IAST parameters() {
    return parameters;
  }

  @Override
  public IExpr copy() {
    return new ParametricFunctionExpr(toData(), parameters);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ParametricFunctionExpr) {
      return fData.equals(((ParametricFunctionExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + fData.hashCode();
  }

  @Override
  public ISymbol head() {
    return PARAMETRIC_FUNCTION;
  }

  @Override
  public ISymbol topHead() {
    // calls pf(v1, v2, ...) are evaluated by ParametricNDSolveValue
    return S.ParametricNDSolveValue;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return toData();
  }

  /** The solutions are opaque: shown as a placeholder, with the brackets of the input syntax. */
  @Override
  public String toString() {
    return EvalEngine.get().isRelaxedSyntax() ? "ParametricFunction(<>)"
        : "ParametricFunction[<>]";
  }

  @Override
  public String fullFormString() {
    return toString();
  }

  @Override
  public String toHTML() {
    return toString();
  }
}
