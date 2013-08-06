package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.*;

import java.util.HashMap;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Summation of expressions.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Summation">Wikipedia Summation</a>
 */
public class Sum extends Table {
	// private static HashMap<IExpr, IExpr> MAP_1_N = new HashMap<IExpr,
	// IExpr>();
	private static HashMap<IExpr, IExpr> MAP_0_N = new HashMap<IExpr, IExpr>();
	static {
		// #^2 -> 1/6*(#+(#+1)*(2*#+1))
		MAP_0_N.put(Power(Slot(C1), C2),
				Times(fraction(1L, 6L), Times(Times(Slot(C1), Plus(Slot(C1), C1)), Plus(Times(C2, Slot(C1)), C1))));
		// #^3 -> 1/4*(#*(#+1))^2
		MAP_0_N.put(Power(Slot(C1), C3), Times(C1D4, Power(Times(Slot(C1), Plus(Slot(C1), C1)), C2)));
		// Binomial[#2,#] -> 2^#
		MAP_0_N.put(Binomial(Slot(C2), Slot(C1)), Power(C2, Slot(C1)));
		// #*Binomial[#2,#] -> #*2^(#-1)
		MAP_0_N.put(Times(Slot(C1), Binomial(Slot(C2), Slot(C1))), Times(Slot(C1), Power(C2, Plus(Slot(C1), Times(CN1, C1)))));
	}

	public Sum() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3);

		if (ast.size() == 3 && ast.get(2).isList() && ((IAST) ast.get(2)).size() == 4) {
			IAST list = (IAST) ast.get(2);
			if (ast.get(1).isPlus()) {
				return ((IAST) ast.get(1)).map(Functors.replace1st(F.Sum(F.Null, ast.get(2))));
			}
			if (list.get(1).isSymbol() && list.get(2).isInteger() && list.get(3).isSymbol()) {
				final ISymbol var = (ISymbol) list.get(1);
				final IInteger from = (IInteger) list.get(2);
				final ISymbol to = (ISymbol) list.get(3);
				if (ast.get(1).isFree(var, true) && ast.get(1).isFree(to, true)) {
					if (from.equals(F.C1)) {
						return F.Times(to, ast.get(1));
					}
					if (from.equals(F.C0)) {
						return F.Times(Plus(to, C1), ast.get(1));
					}
				} else {
					if (ast.get(1).isTimes()) {
						// Sum[ Times[a,b,c,...], {var, from, to} ]
						IAST filterCollector = F.Times();
						IAST restCollector = F.Times();
						((IAST) ast.get(1)).filter(filterCollector, restCollector, new Predicate<IExpr>() {
							@Override
							public boolean apply(IExpr input) {
								return input.isFree(var, true) && input.isFree(to, true);
							}
						});
						if (filterCollector.size() > 1) {
							if (restCollector.size() == 2) {
								filterCollector.add(F.Sum(restCollector.get(1), ast.get(2)));
							} else {
								filterCollector.add(F.Sum(restCollector, ast.get(2)));
							}
							return filterCollector;
						}
					}

					if (from.equals(F.C0)) {
						IExpr repl = ast.get(1).replaceAll(F.List(F.Rule(var, F.Slot(F.C1)), F.Rule(to, F.Slot(F.C2))));
						if (repl != null) {
							IExpr temp = MAP_0_N.get(repl);
							if (temp != null) {
								return temp.replaceAll(F.Rule(F.Slot(F.C1), to));
							}
						}
					}
				}
				if (from.isPositive()) {
					return F.Subtract(F.Sum(ast.get(1), F.List(var, C0, to)), F.Sum(ast.get(1), F.List(var, C0, from.minus(F.C1))));
				}
			}
		}
		IAST resultList = Plus();
		IExpr temp = evaluateTable(ast, resultList, C0);
		if (temp == null || temp.equals(resultList)) {
			return null;
		}
		return temp;
	}
}
