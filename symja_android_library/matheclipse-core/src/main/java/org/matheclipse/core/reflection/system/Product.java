package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.GreaterEqual;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Quotient;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.SetDelayed;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Slot;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;
import static org.matheclipse.core.expression.F.x;

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

	final static IAST RULES = List(
			SetDelayed(Product($p(x), List($p(x), C0, $p(n))), C0),
			SetDelayed(Product($p(x), List($p(x), C0, $p(n), $p(s))), C0),
			SetDelayed(Product($p(x), List($p(x), C1, $p(n))), Factorial(n))
			);

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
				// if (from.isPositive()) {
				// return F.Divide(F.Product(ast.get(1), F.List(var, C0, to)),
				// F.Product(ast.get(1), F.List(var, C0, from.minus(F.C1))));
				// }
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
