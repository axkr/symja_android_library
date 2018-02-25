package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.function.DoubleFunction;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.interfaces.ExprUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements Symbols for function, constant and variable names
 * 
 */
public class BuiltInSymbol extends Symbol implements IBuiltInSymbol {

	private static class DummyEvaluator implements IEvaluator {

		@Override
		public void join() {
			F.join();
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			// do nothing because of dummy evaluator
		}
	}

	/** 
	 * 
	 */
	private static final long serialVersionUID = -4991038487281911261L;

	protected static final DummyEvaluator DUMMY_EVALUATOR = new DummyEvaluator();

	/**
	 * The evaluation class of this built-in-function. See packages: package
	 * <code>org.matheclipse.core.builtin.function</code> and <code>org.matheclipse.core.reflection.system</code>.
	 */
	private transient IEvaluator fEvaluator;

	private transient int fOrdinal;

	// private BuiltInSymbol(final String symbolName) {
	// this(symbolName, null);
	// }

	public BuiltInSymbol(final String symbolName, int ordinal) {
		super(symbolName, Context.SYSTEM);  
		// this(symbolName, null);
		fOrdinal = ordinal;
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
	public String definitionToString() throws IOException {
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
	public IExpr evaluate(EvalEngine engine) {
		if (hasLocalVariableStack()) {
			return ExprUtil.ofNullable(get());
		}
		IExpr result;
		if ((result = evalDownRule(engine, this)).isPresent()) {
			return result;
		}
		final IEvaluator module = getEvaluator();
		if (module instanceof ISymbolEvaluator) {
			IExpr temp;
			if (engine.isNumericMode()) {
				if (engine.isApfloat()) {
					temp = ((ISymbolEvaluator) module).apfloatEval(this, engine);
				} else {
					temp = ((ISymbolEvaluator) module).numericEval(this);
				}
			} else {
				temp = ((ISymbolEvaluator) module).evaluate(this);
			}
			return temp;
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluateHead(IAST ast, EvalEngine engine) {
		return isConstant() ? F.NIL : super.evaluateHead(ast, engine);
	}

	/** {@inheritDoc} */
	@Override
	public IEvaluator getEvaluator() {
		// use "Double-Checked Locking" idiom
		// https://en.wikipedia.org/wiki/Double-checked_locking
		if (fEvaluator == null) {
			synchronized (this) {
				if (fEvaluator == null) {
					fEvaluator = DUMMY_EVALUATOR;
					// if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
					// SystemNamespace.DEFAULT.setEvaluator(this);
					// } else {
					// if (Character.isUpperCase(fSymbolName.charAt(0))) {
					// SystemNamespace.DEFAULT.setEvaluator(this);
					// }
					// }
				}
			}
		}
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
	final public boolean isBuiltInSymbol() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCoreFunctionSymbol() {
		return fEvaluator instanceof ICoreFunctionEvaluator;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isHoldOrHoldFormOrDefer() {
		return this.equals(F.Defer) || this.equals(F.Hold) || this.equals(F.HoldForm);
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isE() {
		return this == F.E;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isFalse() {
		return this == F.False;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isIndeterminate() {
		return this == F.Indeterminate;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isPi() {
		return this == F.Pi;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isSignedNumberConstant() {
		return fEvaluator instanceof ISignedNumberConstant;
	}

	/** {@inheritDoc} */
	@Override
	final public boolean isTrue() {
		return this == F.True;
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

		return engine.evaluate(F.ast(args, this));
	}

	/** {@inheritDoc} */
	@Override
	public boolean ofQ(EvalEngine engine, IExpr... args) {
		if (fEvaluator instanceof AbstractCorePredicateEvaluator&&args.length==1) {
			// evaluate a core function (without no rule definitions)
			final AbstractCorePredicateEvaluator coreFunction = (AbstractCorePredicateEvaluator) getEvaluator();
			return coreFunction.evalArg1Boole(args[0], engine);
		}
		IAST ast = F.ast(args, this);
		return engine.evalTrue(ast);
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
	public final void setEvaluator(final IEvaluator evaluator) {
		evaluator.setUp(this);
		fEvaluator = evaluator;
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		fOrdinal = stream.readInt();
	}

	public Object readResolve() throws ObjectStreamException {
		return BuiltIns.symbol(fOrdinal);
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.writeInt(fOrdinal);
	}

}