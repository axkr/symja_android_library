package org.matheclipse.core.eval.interfaces;

import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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
   * Evaluates the arguments of a held call with the variables of its iterators cleared, and runs
   * <code>body</code> on the evaluated call while they still are.
   *
   * @param held the call as it was written
   * @param body what is done with the evaluated call
   * @see #localIterators()
   */
  public final IExpr evaluateLocal(IAST held, EvalEngine engine, Function<IAST, IExpr> body) {
    IASTAppendable variables = F.ListAlloc();
    for (int i = 2; i < held.size(); i++) {
      IExpr arg = held.get(i);
      if (arg.isList() && arg.argSize() >= 2 && arg.argSize() <= 4 && arg.first().isSymbol()
          && !((ISymbol) arg.first()).hasProtectedAttribute()) {
        variables.append(arg.first());
      }
    }
    return engine.evalBlock(() -> {
      // as one list, so that a Sequence of options is spliced in and Evaluate is released; a new
      // list, because a copy of the held call would also copy the mark that it is evaluated
      IExpr first = held.argSize() > 0 ? held.arg1() : F.NIL;
      boolean holdFirst = samplesFirstArgument() && first.isPresent() && !first.isSymbol()
          && !first.isAST(S.Evaluate);
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

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (localIterators()) {
      return evaluateLocal(ast, engine, call -> evaluateWithOptions(call, engine));
    }
    return evaluateWithOptions(ast, engine);
  }

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
