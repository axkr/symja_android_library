package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExprVariables;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;

public class FactorTerms extends AbstractFunctionEvaluator {

	public FactorTerms() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);

		IAST variableList = F.List();
		if (ast.size() == 3) {
			if (ast.get(2).isSymbol()) {
				ISymbol variable = (ISymbol) ast.get(2);
				variableList = F.List(variable);
			} else if (ast.get(2).isList()) {
				variableList = (IAST) ast.get(2);
			} else {
				return null;
			}
		} else {
			if (ast.size() == 2) {
				ExprVariables eVar;
				eVar = new ExprVariables(ast.get(1));
				if (!eVar.isSize(1)) {
					// only possible for univariate polynomials
					return null;
				}
				variableList = eVar.getVarList();
			}
		}
		if (variableList.size() != 2) {
			// FactorTerms only possible for univariate polynomials
			return null;
		}
		ASTRange r = new ASTRange(variableList, 1);
		IExpr expr = F.evalExpandAll(ast.get(1));
		// IExpr variable = variableList.get(1);
		try {

			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas.expr2JAS(expr);
			Object[] objects = jas.factorTerms(poly);
			java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
			java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
			if (lcm.equals(java.math.BigInteger.ZERO)) {
				// no changes
				return expr;
			}
			GenPolynomial<edu.jas.arith.BigInteger> iPoly = (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
			IAST result = F.Times();
			result.add(F.fraction(gcd, lcm));
			result.add(jas.integerPoly2Expr(iPoly));
			return result;
		} catch (JASConversionException e) {
			// if (Config.DEBUG) {
			e.printStackTrace();
			// }
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}