package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
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
			if (ast.arg2().isSymbol()) {
				ISymbol variable = (ISymbol) ast.arg2();
				variableList = F.List(variable);
			} else if (ast.arg2().isList()) {
				variableList = (IAST) ast.arg2();
			} else {
				return null;
			}
		} else {
			if (ast.size() == 2) {
				VariablesSet eVar;
				eVar = new VariablesSet(ast.arg1());
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
		IExpr expr = F.evalExpandAll(ast.arg1());
		// IExpr variable = variableList.arg1();
		try {

			JASConvert<BigRational> jas = new JASConvert<BigRational>(r.toList(), BigRational.ZERO);
			GenPolynomial<BigRational> poly = jas.expr2JAS(expr, false);
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
			// e.printStackTrace();
			// }
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT);
	}
}