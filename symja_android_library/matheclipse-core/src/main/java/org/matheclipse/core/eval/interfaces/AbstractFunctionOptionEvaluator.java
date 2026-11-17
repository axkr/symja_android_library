package org.matheclipse.core.eval.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotShapeProbe;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractFunctionOptionEvaluator extends AbstractFunctionEvaluator {
  protected IBuiltInSymbol[] optionSymbols = null;

  /**
   * Whether the variables of the iterators <code>{x, xmin, xmax}</code> in a call are local to it,
   * as the variable of a plot is: the call is held, and its arguments are evaluated - and the
   * function is run - with these variables cleared, so that a value which <code>x</code> has
   * outside does not reach the call.
   */
  public boolean localIterators() {
    return false;
  }

  /**
   * Whether the first argument of a call with {@link #localIterators()} is evaluated at every
   * sample only, as the body of <code>Table</code> is, so that its side effects happen once per
   * sample. A symbol or <code>Evaluate(...)</code> there is still evaluated first.
   */
  protected boolean samplesFirstArgument() {
    return false;
  }

  /**
   * The milliseconds which the first argument of a call with {@link #samplesFirstArgument()} may
   * take to evaluate once with symbolic variables, quietly, before it is sampled: if it finishes,
   * its value is sampled, so that <code>D(f, x)</code> is differentiated and an expansion is done
   * once; if it doesn't, as for an iteration which never settles on symbols, the argument as it is
   * written is sampled. <code>0</code> samples the argument as it is written.
   */
  protected long symbolicFirstArgumentMillis() {
    return 0L;
  }

  /** The time a plot gives its function to evaluate with symbolic variables before sampling it. */
  protected static final long PLOT_SYMBOLIC_MILLIS = 400L;

  /**
   * Whether a range of the call may have bounds which depend on the variable of an earlier range,
   * and how the call is told the region between them: the number of arguments which its
   * <code>RegionFunction</code> gets before the variables of the ranges, or
   * <code>DependentRange.PREDICATE</code> for a plot of a predicate.
   *
   * @return <code>DependentRange.UNSUPPORTED</code> if the call takes no such range
   */
  protected int dependentRangeOffset() {
    return org.matheclipse.core.builtin.graphics.DependentRange.UNSUPPORTED;
  }

  /**
   * The variables of a held call with {@link #localIterators()} which are cleared while its
   * arguments are evaluated and the call is run: by default the first elements of the iterators
   * <code>{x, xmin, xmax}</code> behind the first argument.
   *
   * @param held the call as it was written
   * @return the list of the local variables
   */
  protected IAST localVariables(IAST held) {
    IASTAppendable variables = F.ListAlloc();
    for (int i = 2; i < held.size(); i++) {
      IExpr arg = held.get(i);
      if (arg.isList() && arg.argSize() >= 2 && arg.argSize() <= 4) {
        appendLocalVariable(arg.first(), variables);
      }
    }
    return variables;
  }

  /**
   * Append <code>variable</code> to the local <code>variables</code>, if it is a symbol which is
   * not protected and not already in the list.
   */
  protected static void appendLocalVariable(IExpr variable, IASTAppendable variables) {
    if (variable.isSymbol() && !((ISymbol) variable).hasProtectedAttribute()
        && !variables.contains(variable)) {
      variables.append(variable);
    }
  }

  /**
   * Evaluates the arguments of a held call with the variables of its iterators cleared, and runs
   * <code>body</code> on the evaluated call while they still are.
   *
   * @param held the call as it was written
   * @param body what is done with the evaluated call
   * @see #localIterators()
   */
  public final IExpr evaluateLocal(IAST held, EvalEngine engine, Function<IAST, IExpr> body) {
    if (compoundIteratorVariables()) {
      IASTAppendable renamed = F.ListAlloc();
      IAST call = renameCompoundIterators(held, renamed);
      if (renamed.argSize() > 0) {
        IExpr result = evaluateLocalVariables(call, engine, body);
        if (result.isNIL()) {
          return F.NIL;
        }
        // the unique symbols back to the variables they replaced
        return F.subst(result,
            F.mapList(renamed, (IExpr rule) -> F.Rule(rule.second(), rule.first())));
      }
    }
    return evaluateLocalVariables(held, engine, body);
  }

  /**
   * Whether an iterator <code>{x(1), xmin, xmax}</code> or <code>{Subscript(w, f), xmin,
   * xmax}</code> of a call with {@link #localIterators()} is allowed: its variable is replaced by a
   * unique symbol in the whole call.
   */
  protected boolean compoundIteratorVariables() {
    return false;
  }

  /**
   * Replace the variables of the iterators <code>{v, ...}</code> behind the first argument which
   * are no symbols, like <code>x(1)</code>, by unique symbols in the whole call.
   *
   * @param held the call as it was written
   * @param renamed the rules <code>x(1) -> $1</code> are appended to this list
   * @return the call with the replaced variables
   */
  private static IAST renameCompoundIterators(IAST held, IASTAppendable renamed) {
    IAST call = held;
    for (int i = 2; i < held.size(); i++) {
      IExpr arg = held.get(i);
      if (arg.isList() && arg.argSize() >= 2 && arg.argSize() <= 4) {
        IExpr variable = arg.first();
        // {opt1 -> value1, opt2 -> value2} is a list of options, not an iterator
        if (variable.isAST() && !variable.isList() && !variable.isAST(S.Evaluate)
            && !variable.isRuleAST()
            && variable.head().isSymbol() && !variable.isNumericFunction(true)
            && !renamed.exists(rule -> rule.first().equals(variable))) {
          ISymbol dummy = F.Dummy();
          renamed.append(F.Rule(variable, dummy));
          call = (IAST) F.subst(call, F.Rule(variable, dummy));
        }
      }
    }
    return call;
  }

  private IExpr evaluateLocalVariables(IAST held, EvalEngine engine,
      Function<IAST, IExpr> body) {
    IAST variables = localVariables(held);
    return engine.evalBlock(() -> {
      // as one list, so that a Sequence of options is spliced in and Evaluate is released; a new
      // list, because a copy of the held call would also copy the mark that it is evaluated
      IExpr first = held.argSize() > 0 ? held.arg1() : F.NIL;
      boolean holdFirst = samplesFirstArgument() && first.isPresent() && !first.isSymbol()
          && !first.isAST(S.Evaluate);
      if (holdFirst && symbolicFirstArgumentMillis() > 0L) {
        first = symbolicValue(first, held, variables, symbolicFirstArgumentMillis(), engine);
      }
      IASTAppendable list = F.ListAlloc(held.argSize());
      list.appendArgs(held);
      if (holdFirst) {
        list.remove(1);
      }
      IExpr arguments = engine.evaluate(list);
      if (!arguments.isList()) {
        return F.NIL;
      }
      IASTAppendable call = F.ast(held.head(), arguments.argSize() + 1);
      if (holdFirst) {
        call.append(first);
      }
      call.appendArgs((IAST) arguments);
      int regionOffset = dependentRangeOffset();
      if (regionOffset != org.matheclipse.core.builtin.graphics.DependentRange.UNSUPPORTED) {
        // {y, ymin(x), ymax(x)}: plotted over the numbers the bounds reach, as a region
        return body.apply(org.matheclipse.core.builtin.graphics.DependentRange.rewrite(call,
            regionOffset, engine));
      }
      return body.apply(call);
    }, variables);
  }

  /**
   * The value of <code>expr</code> evaluated quietly within <code>millis</code> milliseconds, or
   * <code>expr</code> itself if the evaluation doesn't finish in time or runs into a limit, or if
   * the value is not what <code>expr</code> gives at numeric points of the ranges of the call.
   *
   * <p>
   * With the variables symbolic, a function may take the variable of the call for one of its own:
   * <code>h(y_) := FindRoot(Cos(x) == y*x, {x, 1.0})</code> solves for the plot variable in
   * <code>h(x)</code> and gives a constant, and a function which only has a value for numbers gives
   * an expression of other symbols. Where the value has lost the dependence on the variables or is
   * no number at a point of the ranges, <code>expr</code> is evaluated with the variables set to the
   * numbers of a few such points, as a sample would be; a number there which the value doesn't give
   * means that <code>expr</code> is sampled as it is written.
   *
   * @param expr the first argument as it is written
   * @param held the call as it is written
   * @param variables the variables of the ranges of the call
   */
  private static IExpr symbolicValue(IExpr expr, IAST held, IAST variables, long millis,
      EvalEngine engine) {
    boolean quiet = engine.isQuietMode();
    try {
      engine.setQuietMode(true);
      IExpr value = TimeLimits.evaluateOrNil(expr, millis);
      if (value.isNIL()) {
        return expr;
      }
      if (value.equals(expr) || variables.argSize() == 0) {
        return value;
      }
      IExpr decided = TimeLimits.callOrNil(engine, () -> {
        List<IAST> probes = rangeProbes(held, variables, engine);
        if (probes.isEmpty()) {
          return value;
        }
        // a value of other symbols than the variables gives no number at a point
        boolean suspect = lostDependence(expr, value, variables)
            || numericLeaves(engine.evaluate(F.subst(value, probes.get(0)))) == null;
        return !suspect || agreesAtProbes(expr, value, probes, engine) ? value : expr;
      }, millis);
      return decided.isNIL() ? value : decided;
    } finally {
      engine.setQuietMode(quiet);
    }
  }

  /** The most points of the ranges at which a symbolic value is compared with its definition. */
  private static final int MAX_SYMBOLIC_PROBES = 3;

  /**
   * Points of the ranges <code>{x, xmin, xmax}</code> of the call with numeric bounds, as lists of
   * rules <code>{x -> value, ...}</code>; empty if a variable has no such range.
   */
  private static List<IAST> rangeProbes(IAST held, IAST variables, EvalEngine engine) {
    int n = variables.argSize();
    IExpr[] vars = new IExpr[n];
    double[] mins = new double[n];
    double[] maxs = new double[n];
    for (int k = 0; k < n; k++) {
      mins[k] = Double.NaN;
      maxs[k] = Double.NaN;
    }
    for (int i = 2; i < held.size(); i++) {
      IExpr arg = held.get(i);
      if (arg.isList() && arg.argSize() >= 3) {
        int k = variables.indexOf(arg.first()) - 1;
        if (k >= 0 && Double.isNaN(mins[k])) {
          vars[k] = arg.first();
          mins[k] = engine.evalN(arg.second()).evalfNaN();
          maxs[k] = engine.evalN(arg.get(3)).evalfNaN();
        }
      }
    }
    List<IAST> probes = PlotShapeProbe.rangeProbes(vars, mins, maxs);
    return probes.size() > MAX_SYMBOLIC_PROBES ? probes.subList(0, MAX_SYMBOLIC_PROBES) : probes;
  }

  /**
   * Whether <code>value</code>, or one of the items of a list it is, is free of the
   * <code>variables</code> while the matching part of <code>expr</code> is not.
   */
  private static boolean lostDependence(IExpr expr, IExpr value, IAST variables) {
    if (expr.isList() && value.isList() && expr.size() == value.size()) {
      for (int i = 1; i < value.size(); i++) {
        if (lostDependence(((IAST) expr).get(i), ((IAST) value).get(i), variables)) {
          return true;
        }
      }
      return false;
    }
    if (isFreeOf(expr, variables)) {
      return false;
    }
    if (value.isList()) {
      for (IExpr item : (IAST) value) {
        if (lostDependence(expr, item, variables)) {
          return true;
        }
      }
      return false;
    }
    return isFreeOf(value, variables);
  }

  private static boolean isFreeOf(IExpr expr, IAST variables) {
    return expr.isFree(x -> variables.contains(x), true);
  }

  /**
   * Whether <code>expr</code>, evaluated with the variables set to the numbers of a probe, gives
   * the numbers <code>value</code> gives there, at every probe where it gives numbers.
   */
  private static boolean agreesAtProbes(IExpr expr, IExpr value, List<IAST> probes,
      EvalEngine engine) {
    for (IAST probe : probes) {
      engine.checkTimeLimit();
      IASTAppendable assignments = F.ListAlloc(probe.argSize());
      for (IExpr rule : probe) {
        assignments.append(F.Set(rule.first(), rule.second()));
      }
      double[] written = numericLeaves(engine.evalBlock(expr, assignments));
      if (written == null) {
        // not a sample which the plot could draw either
        continue;
      }
      double[] symbolic = numericLeaves(engine.evaluate(F.subst(value, probe)));
      if (symbolic == null || symbolic.length != written.length) {
        return false;
      }
      for (int k = 0; k < written.length; k++) {
        double scale = Math.max(1.0, Math.max(Math.abs(written[k]), Math.abs(symbolic[k])));
        if (!(Math.abs(written[k] - symbolic[k]) <= 1.0e-6 * scale)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * The real numbers of <code>expr</code>, a real number or nested lists of them, in order;
   * <code>null</code> if it is something else.
   */
  private static double[] numericLeaves(IExpr expr) {
    List<Double> leaves = new ArrayList<>();
    return collectNumericLeaves(expr, leaves)
        ? leaves.stream().mapToDouble(Double::doubleValue).toArray()
        : null;
  }

  private static boolean collectNumericLeaves(IExpr expr, List<Double> leaves) {
    if (expr.isList()) {
      for (IExpr item : (IAST) expr) {
        if (!collectNumericLeaves(item, leaves)) {
          return false;
        }
      }
      return true;
    }
    if (!expr.isReal()) {
      return false;
    }
    double d = expr.evalfNaN();
    if (!Double.isFinite(d)) {
      return false;
    }
    leaves.add(d);
    return true;
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (localIterators()) {
      return evaluateLocal(ast, engine, call -> evaluateWithOptions(call, engine));
    }
    return evaluateWithOptions(ast, engine);
  }

  // CPD-OFF option handling of the core and the normal evaluator hierarchies
  private IExpr evaluateWithOptions(IAST ast, EvalEngine engine) {
    IExpr[] option;
    int argSize = ast.argSize();
    if (optionSymbols == null) {
      option = new IExpr[0];
    } else {
      option = new IExpr[optionSymbols.length];
      argSize = AbstractFunctionEvaluator.determineOptions(option, ast, ast.argSize(),
          expectedArgSize(ast), optionSymbols, engine);
    }
    return evaluate(ast, argSize, option, engine, ast);
    // CPD-ON
  }

  protected void setOptions(final ISymbol symbol, IBuiltInSymbol lhsOptionSymbol, IExpr rhsValue) {
    optionSymbols = new IBuiltInSymbol[] {lhsOptionSymbol};
    super.setOptions(symbol, F.list(F.Rule(lhsOptionSymbol, rhsValue)));
  }

  protected void setOptions(final ISymbol symbol, IBuiltInSymbol[] lhsOptionSymbols,
      IExpr[] rhsValues) {
    optionSymbols = lhsOptionSymbols;
    IASTAppendable list =
        F.mapRange(0, rhsValues.length, i -> F.Rule(lhsOptionSymbols[i], rhsValues[i]));
    super.setOptions(symbol, list);
    if (localIterators()) {
      symbol.addAttributes(ISymbol.HOLDALL);
    }
  }

  /**
   * Convenience method for matrix functions that declare {@code ZeroTest} as {@code options[0]} and
   * {@code Tolerance} as {@code options[1]}.
   *
   * @param ast the top-level call AST (arg1 must be the matrix)
   * @param options the option array populated by {@link #evaluate(IAST, EvalEngine)}
   * @param engine the evaluation engine
   * @return a zero predicate that respects both options
   */
  public static Predicate<IExpr> buildZeroChecker(final IAST ast, final IExpr[] options,
      EvalEngine engine) {
    IExpr zeroTest = options.length > 0 ? options[0] : S.Automatic;
    IExpr tolerance = options.length > 1 ? options[1] : S.Automatic;
    return AbstractMatrix1Expr.optionZeroTest(ast, engine, zeroTest, tolerance);
  }

  @Override
  public IBuiltInSymbol[] getOptionSymbols() {
    return optionSymbols;
  }

  public abstract IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST);
}
