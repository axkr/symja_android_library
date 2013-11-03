package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Factors out only multiple factors of a univariate polynomial
 * 
 */
public class FactorSquareFreeList extends Factor {

	public FactorSquareFreeList() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		ExprVariables eVar = new ExprVariables(ast.arg1());
		if (!eVar.isSize(1)) {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "Factorization only implemented for univariate polynomials");
		}
		try {
			IExpr expr = F.evalExpandAll(ast.arg1());
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			List<IExpr> varList = r.toList();

//			if (ast.size() == 3) {
//				return factorWithOption(ast, expr, varList, true);
//			}
			return factorList(expr, varList, true);

		} catch (JASConversionException jce) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return null;
	}

}