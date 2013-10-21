package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Product of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation"> Wikipedia Multiplication</a>
 */
public class Product extends Table {

	// {
	// Product[x_,{x_,0,n_]:=0,
	// Product[x_,{x_,0,n_, s_]:=0,
	// Product[x_,{x_,1,n_]:=n!
	// }

	final static IAST RULES = List(SetDelayed(Product($p(x), List($p(x), C0, $p(n))), C0),
			SetDelayed(Product($p(x), List($p(x), C0, $p(n), $p(s))), C0),
			SetDelayed(Product($p(x), List($p(x), C1, $p(n))), Factorial(n)));

	public Product() {
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		if (ast.get(1).isTimes()) {
			IAST prod = ast.clone();
			prod.set(1, F.Null);
			return ((IAST) ast.get(1)).map(Functors.replace1st(prod));
		}
		if (ast.get(1).isPower()) {
			IExpr powArg1 = ast.get(1).getAt(1);
			IExpr powArg2 = ast.get(1).getAt(2);
			boolean flag = true;
			// Prod( i^a, {i,from,to},... )
			for (int i = 2; i < ast.size(); i++) {
				if (ast.get(i).isList() && (((IAST) ast.get(i)).size() == 4 || ((IAST) ast.get(i)).size() == 5)) {
					IAST list = (IAST) ast.get(i);
					if (powArg2.isFree(list.get(1), true)) {
						continue;
					}
				}
				flag = false;
				break;
			}
			if (flag) {
				IAST prod = ast.clone();
				prod.set(1, powArg1);
				return F.Power(prod, powArg2);
			}
		}
		if (ast.size() == 3 && ast.get(2).isList() && ((IAST) ast.get(2)).size() == 4) {
			IAST list = (IAST) ast.get(2);
			if (list.get(1).isSymbol() && list.get(2).isInteger() && list.get(3).isSymbol()) {
				final ISymbol var = (ISymbol) list.get(1);
				final IInteger from = (IInteger) list.get(2);
				final ISymbol to = (ISymbol) list.get(3);
				if (ast.get(1).isFree(var, true) && ast.get(1).isFree(to, true)) {
					if (from.equals(F.C1)) {
						return F.Power(ast.get(1), to);
					}
					if (from.equals(F.C0)) {
						return F.Power(ast.get(1), Plus(to, C1));
					}
				}
			}
		}
		IAST resultList = Times();
		IExpr temp = evaluateTable(ast, resultList, C0);
		if (temp == null || temp.equals(resultList)) {
			return null;
		}
		return temp;
	}

}
