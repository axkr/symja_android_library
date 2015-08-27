package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * MessageName[{&lt;file name&gt;}}
 * 
 */
public class MessageName extends AbstractFunctionEvaluator {

	public MessageName() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (!ast.arg1().isSymbol()) {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "");
		}
		IExpr arg2 = F.eval(ast.arg2());
		if (arg2 instanceof IStringX || arg2.isSymbol()) {
			return null;
		}
		if (!ast.arg2().isAST(F.Set, 3)) { // instanceof IStringX) && !ast.arg2().isSymbol()) {
			throw new WrongArgumentType(ast, ast.arg2(), 2, "");
		}
		// Assignemnt of the message is handled in Set() function
		// ISymbol symbol = (ISymbol) ast.arg1();
		// symbol.putDownRule(RuleType.SET, true, symbol, ast.arg2(), true);
		return F.Null;
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
