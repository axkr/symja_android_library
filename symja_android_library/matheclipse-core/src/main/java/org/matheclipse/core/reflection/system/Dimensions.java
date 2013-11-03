package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the dimensions of an expression
 */
public class Dimensions extends AbstractFunctionEvaluator {

	public Dimensions() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			int m = list.size();
			ArrayList<Integer> dims = new ArrayList<Integer>();
			IAST res = F.List();
			ISymbol header = list.topHead();
			dims.add(m - 1);
			getLevel1(list, header, dims);
			checkLevel(list, header, dims, 1);
			for (int i = 0; i < dims.size(); i++) {
				res.add(F.integer(dims.get(i).intValue()));
			}
			return res;
		}

		return F.List();

	}

	private void getLevel1(IAST ast, ISymbol header, ArrayList<Integer> dims) {
		if (ast.size() > 1 && ast.arg1().isAST()) {
			IAST list = (IAST) ast.arg1();
			if (!header.equals(list.topHead())) {
				return;
			}
			dims.add(list.size() - 1);
			getLevel1(list, header, dims);
		}
	}

	private void checkLevel(final IAST ast, ISymbol header, ArrayList<Integer> dims, int index) {
		if (ast.size() > 1) {
			if (index < dims.size()) {
				int dim = dims.get(index);
				for (int i = 1; i < ast.size(); i++) {
					if (ast.get(i) instanceof IAST) {
						IAST list = (IAST) ast.get(i);
						if (!header.equals(list.topHead()) || dim != list.size() - 1) {
							while (index < dims.size()) {
								dims.remove(index);
							}
							return;
						}
						checkLevel(list, header, dims, index + 1);
					}
				}
			}
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
