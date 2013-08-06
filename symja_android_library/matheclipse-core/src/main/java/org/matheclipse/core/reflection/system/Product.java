package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Slot;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;

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
 * See <a
 * href="http://en.wikipedia.org/wiki/Multiplication#Capital_Pi_notation">
 * Wikipedia Multiplication</a>
 */
public class Product extends Table {
	// private static HashMap<IExpr, IExpr> MAP_0_N = new HashMap<IExpr,
	// IExpr>();
	// static {
	// }

	public Product() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		if (ast.size() == 3 && ast.get(2).isList() && ((IAST) ast.get(2)).size() == 4) {
			IAST list = (IAST) ast.get(2);
			if (ast.get(1).isTimes()) {
				return ((IAST) ast.get(1)).map(Functors.replace1st(F.Product(F.Null, ast.get(2))));
			}
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
				} else {
					// if (from.equals(F.C0)) {
					// IExpr repl = ast.get(1).replaceAll(F.List(F.Rule(var,
					// F.Slot(F.C1)), F.Rule(to, F.Slot(F.C2))));
					// if (repl != null) {
					// IExpr temp = MAP_0_N.get(repl);
					// if (temp != null) {
					// return temp.replaceAll(F.Rule(F.Slot(F.C1), to));
					// }
					// }
					// }
				}
				if (from.isPositive()) {
					return F.Divide(F.Product(ast.get(1), F.List(var, C0, to)),
							F.Product(ast.get(1), F.List(var, C0, from.minus(F.C1))));
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
