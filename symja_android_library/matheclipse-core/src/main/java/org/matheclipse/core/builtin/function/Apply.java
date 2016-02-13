package org.matheclipse.core.builtin.function;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 * <p>
 * See the online Symja function reference: <a href=
 * "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Apply">
 * Apply</a>
 * </p>
 *
 */
public class Apply extends AbstractCoreFunctionEvaluator {

	public Apply() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 5);

		IAST evaledAST = ast.clone();
		for (int i = 1; i < evaledAST.size(); i++) {
			evaledAST.set(i, engine.evaluate(evaledAST.get(i)));
		}
		int lastIndex = evaledAST.size() - 1;
		boolean heads = false;
		final Options options = new Options(evaledAST.topHead(), evaledAST, lastIndex, engine);
		IExpr option = options.getOption("Heads");
		if (option != null) {
			lastIndex--;
			if (option.isTrue()) {
				heads = true;
			}
		} else {
			Validate.checkRange(evaledAST, 3, 4);
		}

		VisitorLevelSpecification level = null;
		Function<IExpr, IExpr> af = Functors.apply(evaledAST.arg1());
		if (lastIndex == 3) {
			level = new VisitorLevelSpecification(af, evaledAST.get(lastIndex), heads);
		} else {
			level = new VisitorLevelSpecification(af, 0);
		}

		try {

			if (!evaledAST.arg2().isAtom()) {
				final IExpr result = evaledAST.arg2().accept(level);

				return result.isPresent() ? result : evaledAST.arg2();
			} else if (evaledAST.size() == 3) {
				if (evaledAST.arg1().isFunction()) {
					return F.unaryAST1(evaledAST.arg1(), evaledAST.arg2());
				}
				return evaledAST.arg2();
			}
		} catch (final ArithmeticException e) {

		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
