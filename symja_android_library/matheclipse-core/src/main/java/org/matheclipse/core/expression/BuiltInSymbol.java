package org.matheclipse.core.expression;

import java.io.IOException;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractPredicateEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IRealConstant;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBooleanFormula;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComparatorFunction;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPredicate;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.ParserConfig;

/** Implements Symbols for function, constant and variable names */
public class BuiltInSymbol extends Symbol implements IBuiltInSymbol {
  private static class DummyEvaluator extends AbstractSymbolEvaluator implements ISymbolEvaluator {

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

  private static final class PredicateEvaluator extends AbstractPredicateEvaluator
      implements IPredicate {
    Predicate<IExpr> predicate;

    public PredicateEvaluator(Predicate<IExpr> predicate) {
      this.predicate = predicate;
    }

    @Override
    public boolean evalArg1Boole(IExpr arg1, EvalEngine engine) {
      return predicate.test(arg1);
    }

    /** {@inheritDoc} */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return predicate.test(ast.arg1()) ? S.True : S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }
  }

  /** */
  private static final long serialVersionUID = -4991038487281911261L;

  public static final DummyEvaluator DUMMY_EVALUATOR = new DummyEvaluator();

  /**
   * The evaluation class of this built-in-function.
   */
  private transient IFunctionEvaluator fEvaluator;

  private transient int fOrdinal;

  public BuiltInSymbol(final String symbolName, int ordinal) {
    super(symbolName, Context.SYSTEM);
    fEvaluator = DUMMY_EVALUATOR;
    fOrdinal = ordinal;
    if ((symbolName.charAt(0) != '$') || ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      fAttributes = Config.BUILTIN_PROTECTED;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr assignedValue() {
    if (isDollarSymbol()) {
      IExpr dollarValue = EvalEngine.get().getDollarValue(this);
      if (dollarValue.isPresent()) {
        return dollarValue;
      }
      return null;
    }
    return super.assignedValue();
  }

  /** {@inheritDoc} */
  @Override
  public final void assignValue(final IExpr value, boolean setDelayed) {
    if (isDollarSymbol()) {
      EvalEngine.get().setDollarValue(this, value);
    } else {
      super.assignValue(value, setDelayed);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void clearAll(EvalEngine engine) {
    if (Config.FUZZ_TESTING) {
      // Cannot assign to raw object `1`.
      throw new NullPointerException();
    }
    // clear(engine);
    // fAttributes = NOATTRIBUTE;
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
  public final RulesData createRulesData(int[] sizes) {
    return (fRulesData == null) ? (fRulesData = new RulesData(sizes, this)) : fRulesData;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object obj) {
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
    if (fEvaluator instanceof ISymbolEvaluator) {
      IExpr assignedValue = F.NIL;
      if (engine.isNumericMode()) {
        if (engine.isArbitraryMode()) {
          assignedValue = ((ISymbolEvaluator) fEvaluator).apfloatEval(this, engine);
        } else {
          assignedValue = ((ISymbolEvaluator) fEvaluator).numericEval(this, engine);
        }
      }
      if (assignedValue.isPresent()) {
        return assignedValue;
      }
      assignedValue = ((ISymbolEvaluator) fEvaluator).evaluate(this, engine);
      if (assignedValue.isPresent()) {
        return assignedValue;
      }
    }
    if (hasAssignedSymbolValue()) {
      return ISymbol.evalAssignedValue(assignedValue(), engine);
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public final IExpr evaluateHead(IAST ast, EvalEngine engine) {
    return isConstantAttribute() ? F.NIL : super.evaluateHead(ast, engine);
  }

  /** {@inheritDoc} */
  @Override
  public final IFunctionEvaluator getEvaluator() {
    return fEvaluator;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
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

  @Override
  public final COMPARE_TERNARY isAlgebraic() {
    if (isRealConstant()) {
      return ((IRealConstant) fEvaluator).isAlgebraic();
    }
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isBooleanFormulaSymbol() {
    return fEvaluator instanceof IBooleanFormula;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isComparatorFunctionSymbol() {
    return fEvaluator instanceof IComparatorFunction;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isCoreFunctionSymbol() {
    return fEvaluator instanceof ICoreFunctionEvaluator;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isDollarSymbol() {
    return fSymbolName.startsWith("$");
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
  public final boolean isHoldOrHoldFormOrDefer() {
    return this.equals(S.Defer) || this.equals(S.Hold) || this.equals(S.HoldForm);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isIndeterminate() {
    return this == S.Indeterminate;
  }

  @Override
  public final COMPARE_TERNARY isIrrational() {
    if (isRealConstant()) {
      return ((IRealConstant) fEvaluator).isIrrational();
    }
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  @Override
  public final boolean isNegative() {
    if (isRealConstant()) {
      return ((IRealConstant) fEvaluator).isNegative();
    }
    return false;
  }

  @Override
  public final boolean isNonNegative() {
    return isRealConstant() ? ((IRealConstant) fEvaluator).isNonNegative() : false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPi() {
    return this == S.Pi;
  }

  @Override
  public final boolean isPositive() {
    return isRealConstant() ? ((IRealConstant) fEvaluator).isPositive() : false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isPredicateFunctionSymbol() {
    return fEvaluator instanceof IPredicate;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isRealConstant() {
    return fEvaluator instanceof IRealConstant;
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

  @Override
  public final COMPARE_TERNARY isTranscendental() {
    if (isRealConstant()) {
      return ((IRealConstant) fEvaluator).isTranscendental();
    }
    return COMPARE_TERNARY.UNDECIDABLE;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isTrue() {
    return this == S.True;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isUndefined() {
    return this == S.Undefined;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr mapConstantDouble(DoubleFunction<IExpr> function) {
    if (fEvaluator instanceof IRealConstant) {
      double value = ((IRealConstant) fEvaluator).evalReal();
      if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
        return function.apply(value);
      }
    }
    return F.NIL;
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
  public IExpr of(EvalEngine engine, Object... args) {
    IExpr[] convertedArgs = Object2Expr.convertArray(args, false, false);
    if (fEvaluator instanceof ICoreFunctionEvaluator) {
      // evaluate a core function (without no rule definitions)
      final ICoreFunctionEvaluator coreFunction = (ICoreFunctionEvaluator) getEvaluator();
      IAST ast = F.ast(convertedArgs, this);
      return coreFunction.evaluate(ast, engine);
    }

    return super.of(engine, convertedArgs);
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
  public final int ordinal() {
    return fOrdinal;
  }

  private void readObject(java.io.ObjectInputStream stream) throws IOException {
    fOrdinal = stream.readInt();
  }

  @Override
  public Object readResolve() {
    return S.symbol(fOrdinal);
  }

  /** {@inheritDoc} */
  @Override
  public final void setAttributes(final int attributes) {
    super.setAttributes(attributes | Config.BUILTIN_PROTECTED);
  }

  /** {@inheritDoc} */
  @Override
  public final void setEvaluator(final IFunctionEvaluator evaluator) {
    evaluator.setUp(this);
    fEvaluator = evaluator;
  }

  /** {@inheritDoc} */
  @Override
  public final void setPredicateQ(final Predicate<IExpr> predicate) {
    fEvaluator = new PredicateEvaluator(predicate);
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.writeInt(fOrdinal);
  }
}
