package org.matheclipse.core.builtin.function;

import java.io.StringWriter;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Return the TeX form of this expression.
 */
public class TeXForm extends AbstractCoreFunctionEvaluator {

	public TeXForm() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
		IExpr arg1 = engine.evaluate(ast.arg1());
		StringWriter stw = new StringWriter();
		texUtil.toTeX(arg1, stw);
		return F.$str(stw.toString());
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
