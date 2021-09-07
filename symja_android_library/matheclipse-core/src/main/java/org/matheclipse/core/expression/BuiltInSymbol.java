package org.matheclipse.core.expression;

import java.io.IOException;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractPredicateEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBooleanFormula;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

/** Implements Symbols for function, constant and variable names */
public class BuiltInSymbol extends Symbol implements IBuiltInSymbol {
  private static final class PredicateEvaluator extends AbstractPredicateEvaluator
      implements IPredicate {
    Predicate<IExpr> predicate;

    public PredicateEvaluator(Predicate<IExpr> predicate) {
      this.predicate = predicate;
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return predicate.test(ast.arg1()) ? S.True : S.False;
    }

    @Override
    public boolean evalArg1Boole(IExpr arg1, EvalEngine engine) {
      return predicate.test(arg1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }
  }

  private static class DummyEvaluator implements IEvaluator {

    /**
     * Causes the current thread to wait until the INIT_THREAD has initialized the Integrate()
     * rules.
     */
    @Override
    public void await() throws InterruptedException {
      F.await();
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      // do nothing because of dummy evaluator
    }
  }

  /** */
  private static final long serialVersionUID = -4991038487281911261L;

  public static final DummyEvaluator DUMMY_EVALUATOR = new DummyEvaluator();

  /**
   * The evaluation class of this built-in-function. See packages: package <code>
   * org.matheclipse.core.builtin.function</code> and <code>org.matheclipse.core.reflection.system
   * </code>.
   */
  private transient IEvaluator fEvaluator;

  private transient int fOrdinal;

  // private BuiltInSymbol(final String symbolName) {
  // this(symbolName, null);
  // }

  public BuiltInSymbol(final String symbolName, int ordinal) {
    super(symbolName, Context.SYSTEM);
    fEvaluator = DUMMY_EVALUATOR;
    fOrdinal = ordinal;
    if ((symbolName.charAt(0) != '$') || ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      fAttributes = Config.BUILTIN_PROTECTED;
    }
  }

  // private BuiltInSymbol(final String symbolName, final IEvaluator evaluator) {
  // this(symbolName, Context.SYSTEM, evaluator);
  // }

  // private BuiltInSymbol(final String symbolName, Context context, final IEvaluator evaluator) {
  // super(symbolName, context);
  // fEvaluator = evaluator;
  // }

  /** {@inheritDoc} */
  @Override
  public final void assignValue(final IExpr value, boolean setDelayed) {
    super.assignValue(value, setDelayed);
    //    if (Config.FUZZ_TESTING) {
    //      // Cannot assign to raw object `1`.
    //      throw new NullPointerException();
    //    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clearAttributes(final int attributes) {
    if (Config.FUZZ_TESTING) {
      // Cannot assign to raw object `1`.
      throw new NullPointerException();
    }
    super.clearAttributes(attributes);
  }

  /** {@inheritDoc} */
  @Override
  public void clearAll(EvalEngine engine) {
    if (Config.FUZZ_TESTING) {
      // Cannot assign to raw object `1`.
      throw new NullPointerException();
    }
    // clear(engine);
    // fAttributes = NOATTRIBUTE;
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof BuiltInSymbol) {
      final int ordinal = ((BuiltInSymbol) expr).fOrdinal;
      return fOrdinal < ordinal ? -1 : fOrdinal == ordinal ? 0 : 1;
    }
    return super.compareTo(expr);
  }

  /** {@inheritDoc} */
  @Override
  public String definitionToString() {
    // dummy call to ensure, that the associated rules are loaded:
    getEvaluator();
    return super.definitionToString();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr.COMPARE_TERNARY equalTernary(IExpr arg2, EvalEngine engine) {
    if (isIndeterminate() || arg2.isIndeterminate()) {
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    if (this == arg2) {
      return IExpr.COMPARE_TERNARY.TRUE;
    }

    if (isTrue()) {
      if (arg2.isFalse()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
    } else if (isFalse()) {
      if (arg2.isTrue()) {
        return IExpr.COMPARE_TERNARY.FALSE;
      }
    }
    if (isConstantAttribute() && arg2.isConstantAttribute()) {
      return IExpr.COMPARE_TERNARY.FALSE;
    }
    return super.equalTernary(arg2, engine);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    // final IEvaluator module = getEvaluator();
    if (fEvaluator instanceof ISymbolEvaluator) {
      if (engine.isNumericMode()) {
        if (engine.isArbitraryMode()) {
          return ((ISymbolEvaluator) fEvaluator).apfloatEval(this, engine);
        } else {
          return ((ISymbolEvaluator) fEvaluator).numericEval(this, engine);
        }
      }
      return ((ISymbolEvaluator) fEvaluator).evaluate(this, engine);
    }
    if (hasAssignedSymbolValue()) {
      return assignedValue();
    }
    // if (hasLocalVariableStack()) {
    // return ExprUtil.ofNullable(get());
    // }
    // IExpr result;
    // if ((result = evalDownRule(engine, this)).isPresent()) {
    // return result;
    // }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    return isConstantAttribute() ? F.NIL : super.evaluateHead(ast, engine);
  }

  /** {@inheritDoc} */
  @Override
  public IEvaluator getEvaluator() {
    return fEvaluator;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return fOrdinal;
  }

  /** {@inheritDoc} */
  @Override
  public int ordinal() {
    return fOrdinal;
  }

  @Override
  protected CharSequence internalJavaStringAsFactoryMethod() {
    if (Config.RUBI_CONVERT_SYMBOLS && fOrdinal >= 1) {
      if (Config.RUBI_CONVERT_SYMBOLS && "C".equals(fSymbolName)) {
        return new StringBuilder(fSymbolName).append("Symbol");
      }
      return fSymbolName;
    }
    return super.internalJavaStringAsFactoryMethod();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCoreFunctionSymbol() {
    return fEvaluator instanceof ICoreFunctionEvaluator;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBooleanFormulaSymbol() {
    return fEvaluator instanceof IBooleanFormula;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isComparatorFunctionSymbol() {
    return fEvaluator instanceof IComparatorFunction;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPredicateFunctionSymbol() {
    return fEvaluator instanceof IPredicate;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isHoldOrHoldFormOrDefer() {
    return this.equals(S.Defer) || this.equals(S.Hold) || this.equals(S.HoldForm);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isE() {
    return this == S.E;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isFalse() {
    return this == S.False;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isIndeterminate() {
    return this == S.Indeterminate;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPi() {
    return this == S.Pi;
  }

  @Override
  public final boolean isNegative() {
    if (isRealConstant()) {
      return ((ISignedNumberConstant) fEvaluator).isNegative();
    }
    return false;
  }

  @Override
  public final boolean isPositive() {
    if (isRealConstant()) {
      return ((ISignedNumberConstant) fEvaluator).isPositive();
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isRealConstant() {
    return fEvaluator instanceof ISignedNumberConstant;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSymbolID(int... ids) {
    for (int i = 0; i < ids.length; i++) {
      if (fOrdinal == ids[i]) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTrue() {
    return this == S.True;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr of(EvalEngine engine, IExpr... args) {
    if (fEvaluator instanceof ICoreFunctionEvaluator) {
      // evaluate a core function (without no rule definitions)
      final ICoreFunctionEvaluator coreFunction = (ICoreFunctionEvaluator) getEvaluator();
      IAST ast = F.ast(args, this);
      return coreFunction.evaluate(ast, engine);
    }

    return super.of(engine, args);
  }

  /** {@inheritDoc} */
  @Override
  public boolean ofQ(EvalEngine engine, IExpr... args) {
    if (args.length == 1) {
      if (fEvaluator instanceof AbstractCorePredicateEvaluator) {
        // evaluate a core function (without no rule definitions)
        final AbstractCorePredicateEvaluator coreFunction =
            (AbstractCorePredicateEvaluator) getEvaluator();
        return coreFunction.evalArg1Boole(args[0], engine);
      }
    }
    return super.ofQ(engine, args);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr mapConstantDouble(DoubleFunction<IExpr> function) {
    if (fEvaluator instanceof ISignedNumberConstant) {
      double value = ((ISignedNumberConstant) fEvaluator).evalReal();
      if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
        return function.apply(value);
      }
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public final void setAttributes(final int attributes) {
    super.setAttributes(attributes | Config.BUILTIN_PROTECTED);
  }

  /** {@inheritDoc} */
  @Override
  public final void setEvaluator(final IEvaluator evaluator) {
    evaluator.setUp(this);
    fEvaluator = evaluator;
  }

  /** {@inheritDoc} */
  @Override
  public final void setPredicateQ(final Predicate<IExpr> predicate) {
    fEvaluator = new PredicateEvaluator(predicate);
  }

  private void readObject(java.io.ObjectInputStream stream) throws IOException {
    fOrdinal = stream.readInt();
  }

  @Override
  public Object readResolve() {
    return S.symbol(fOrdinal);
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.writeInt(fOrdinal);
  }
}
