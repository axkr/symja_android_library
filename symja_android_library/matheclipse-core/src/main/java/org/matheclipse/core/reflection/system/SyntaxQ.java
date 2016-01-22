package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Returns <code>True</code>, if the given expression is a string which has the
 * correct syntax
 * 
 */
public class SyntaxQ extends AbstractFunctionEvaluator implements Predicate<String> {

	public SyntaxQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (!(ast.arg1() instanceof IStringX)) {
			return F.False;
		}
		return F.bool(test(ast.arg1().toString()));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final String str) {
		try {
			ExprParser fParser = new ExprParser(EvalEngine.get());
			final IExpr parsedExpression = fParser.parse(str);
			// final Parser fParser = new Parser();
			// final ASTNode parsedAST = fParser.parse(str);
			// final IExpr parsedExpression = AST2Expr.CONST.convert(parsedAST);
			if (parsedExpression != null) {
				return true;
			}
		} catch (final SyntaxError e) {

		}
		return false;
	}

}
