package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.combinatoric.CartesianProductList;

/**
 * Cartesian product for multiple lists.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Cartesian_product">Wikipedia -
 * Cartesian product</a>
 */
public class CartesianProduct extends AbstractFunctionEvaluator {

	public CartesianProduct() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);
		List<IAST> la = new ArrayList<IAST>(ast.size() - 1);
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isList()) {
				la.add((IAST) ast.get(i));
			} else {
				return null;
			}
		}
		CartesianProductList<IExpr, IAST> cpi = new CartesianProductList<IExpr, IAST>(la, F.List(), AST.COPY);
		IAST result = F.List();
		for (IAST iast : cpi) {
			result.add(iast);
		}
		return result;
	}

}
