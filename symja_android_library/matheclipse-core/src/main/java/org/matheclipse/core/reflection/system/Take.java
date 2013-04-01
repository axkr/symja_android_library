package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Sequence;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.interfaces.ISequence;

public class Take extends AbstractFunctionEvaluator {

	public Take() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);
		
		try {
			if (ast.get(1).isAST()) {
				final ISequence[] sequ = Sequence.createSequences(ast, 2);
				final IAST arg1 = (IAST) ast.get(1);
				if (sequ != null) {
					return AST.COPY.take(arg1, 0, sequ);
				}
			}
		} catch (final Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDREST);
	}
}
