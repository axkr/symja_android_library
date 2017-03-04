package org.matheclipse.core.expression;

import java.io.IOException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.generic.interfaces.INumericFunction;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements Symbols for function, constant and variable names
 * 
 */
public class BuiltInSymbol extends Symbol implements IBuiltInSymbol {

	static class DummyEvaluator implements IEvaluator {
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
	 * <code>org.matheclipse.core.builtin.function</code> and
	 * <code>org.matheclipse.core.reflection.system</code>.
	 */
	private transient IEvaluator fEvaluator;

	public BuiltInSymbol(final String symbolName) {
		this(symbolName, null);
	}

	public BuiltInSymbol(final String symbolName, final IEvaluator evaluator) {
		super(symbolName);
		fEvaluator = evaluator;
	}

	/** {@inheritDoc} */
	@Override
	public String definitionToString() throws IOException {
		// dummy call to ensure, that the associated rules are loaded:
		getEvaluator();
		return super.definitionToString();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (hasLocalVariableStack()) {
			return IExpr.ofNullable(get());
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
	public IEvaluator getEvaluator() {
		// use "Double-Checked Locking" idiom
		// https://en.wikipedia.org/wiki/Double-checked_locking
		if (fEvaluator == null) {
			synchronized (this) {
				if (fEvaluator == null) {
					fEvaluator = DUMMY_EVALUATOR;
//					if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
//						SystemNamespace.DEFAULT.setEvaluator(this);
//					} else {
//						if (Character.isUpperCase(fSymbolName.charAt(0))) {
//							SystemNamespace.DEFAULT.setEvaluator(this);
//						}
//					}
				}
			}
		}
		return fEvaluator;
	}

	@Override
	public boolean isBuiltInSymbol() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCoreFunctionSymbol() {
		return fEvaluator instanceof ICoreFunctionEvaluator;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isE() {
		return this == F.E;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFalse() {
		return this == F.False;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isIndeterminate() {
		return this == F.Indeterminate;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPi() {
		return this == F.Pi;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isSignedNumberConstant() {
		return fEvaluator instanceof ISignedNumberConstant;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return this == F.True;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr mapConstantDouble(INumericFunction<IExpr> function) {
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
		fEvaluator = evaluator;
		evaluator.setUp(this);
	}
}