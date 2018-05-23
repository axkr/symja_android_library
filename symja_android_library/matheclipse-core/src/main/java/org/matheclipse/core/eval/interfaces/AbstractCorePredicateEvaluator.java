package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates to the
 * <code>evaluate()</code>
 * 
 */
public abstract class AbstractCorePredicateEvaluator extends AbstractCoreFunctionEvaluator {

	private final ISymbol fSymbol;

	public AbstractCorePredicateEvaluator() {
		fSymbol = null;
	}

	public AbstractCorePredicateEvaluator(ISymbol symbol) {
		fSymbol = symbol;
	}

	/**
	 * Evaluate the function for 1 argument, with possible options.
	 * 
	 * @param arg1
	 * @param engine
	 * @return
	 */
	abstract public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine);

	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, Options options) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() >= 2) {
			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isList()) {
				// thread over list?
				if ((ast.topHead().getAttributes() & ISymbol.LISTABLE) == ISymbol.LISTABLE) {
					return ((IAST) arg1).mapThread(ast.copy(), 1);
				}
			}
			if (ast.size() == 3) {
				Options options = new Options(ast.topHead(), ast, 2, engine);
				return F.bool(evalArg1Boole(arg1, engine, options));
			}
			return F.bool(evalArg1Boole(arg1, engine));
		}
//		if (ast.isAST1()) {
//			IExpr arg1 = engine.evaluate(ast.arg1());
//			if (arg1.isList()) {
//				// thread over list?
//				if ((ast.topHead().getAttributes() & ISymbol.LISTABLE) == ISymbol.LISTABLE) {
//					return ((IAST) arg1).mapThread(F.unaryAST1(fSymbol, null), 1);
//				}
//			}
//			return F.bool(evalArg1Boole(arg1, engine));
//		}
		Validate.checkSize(ast, 2);
		return F.NIL;
	}

}