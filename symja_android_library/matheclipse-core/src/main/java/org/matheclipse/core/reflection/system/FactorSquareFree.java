package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
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
public class FactorSquareFree extends Factor {

	public FactorSquareFree() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		VariablesSet eVar = new VariablesSet(ast.arg1());
		if (!eVar.isSize(1)) {
			throw new WrongArgumentType(ast, ast.arg1(), 1, "Factorization only implemented for univariate polynomials");
		}
		try {
			IExpr expr = F.evalExpandAll(ast.arg1());
			ASTRange r = new ASTRange(eVar.getVarList(), 1);
			List<IExpr> varList = r.toList();

			if (ast.isAST2()) {
				return factorWithOption(ast, expr, varList, true, engine);
			}
			return factor(expr, varList, true);

		} catch (JASConversionException jce) {
			// toInt() conversion failed
			if (Config.DEBUG) {
				jce.printStackTrace();
			}
		}
		return F.NIL;
	}

}