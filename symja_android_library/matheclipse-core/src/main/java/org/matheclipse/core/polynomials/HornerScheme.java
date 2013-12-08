package org.matheclipse.core.polynomials;

import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Generate the horner scheme for univariate polynomials
 */
public class HornerScheme {

	private TreeMap<ISignedNumber, IAST> map;

	public HornerScheme() {
		Comparator<ISignedNumber> comp = new Comparator<ISignedNumber>() {

			public int compare(ISignedNumber arg0, ISignedNumber arg1) {
				if (arg0.isGreaterThan(arg1)) {
					return 1;
				}
				if (arg0.isLessThan(arg1)) {
					return -1;
				}
				return 0;
			}

		};
		map = new TreeMap<ISignedNumber, IAST>(comp);
	}

	public IAST generate(boolean numericMode, IAST poly, ISymbol sym) {
		if (numericMode) {
			for (int i = 1; i < poly.size(); i++) {
				collectTermN(sym, poly.get(i));
			}
			IAST result = F.Plus();
			IAST startResult = result;
			IAST temp;
			ISignedNumber start = F.CD0;
			for (Iterator<ISignedNumber> iter = map.keySet().iterator(); iter.hasNext();) {
				ISignedNumber exponent = iter.next();
				IExpr coefficient = getCoefficient(exponent);
				if (exponent.isLessThan(F.CD1)) {
					if (exponent.compareTo(F.CD0) == 0) {
						result.add(coefficient);
					} else {
						result.add(F.Times(coefficient, F.Power(sym, exponent)));
					}
				} else {
					temp = F.Times();
					ISignedNumber currentExponent = exponent.subtractFrom(start);
					if (currentExponent.equals(F.CD1)) {
						temp.add(sym);
					} else {
						temp.add(F.Power(sym, currentExponent));
					}
					result.add(temp);
					result = F.Plus();
					temp.add(result);
					result.add(coefficient);
					start = exponent;
				}
			}
			return startResult;
		} else {
			for (int i = 1; i < poly.size(); i++) {
				collectTerm(sym, poly.get(i));
			}
			IAST result = F.Plus();
			IAST startResult = result;
			IAST temp;
			ISignedNumber start = F.C0;
			for (Iterator<ISignedNumber> iter = map.keySet().iterator(); iter.hasNext();) {
				ISignedNumber exponent = iter.next();
				IExpr coefficient = getCoefficient(exponent);
				if (exponent.isLessThan(F.C1)) {
					if (exponent.compareTo(F.C0) == 0) {
						result.add(coefficient);
					} else {
						result.add(F.Times(coefficient, F.Power(sym, exponent)));
					}
				} else {
					temp = F.Times();
					ISignedNumber currentExponent = exponent.subtractFrom(start);
					if (currentExponent.equals(F.C1)) {
						temp.add(sym);
					} else {
						temp.add(F.Power(sym, currentExponent));
					}
					result.add(temp);
					result = F.Plus();
					temp.add(result);
					result.add(coefficient);
					start = exponent;
				}
			}
			return startResult;
		}
	}

	private IExpr getCoefficient(ISignedNumber key) {
		IAST value = map.get(key);
		IExpr coefficient;
		if (value.isAST(F.Plus, 2)) {
			coefficient = value.get(1);
			if (coefficient.isAST(F.Times, 2)) {
				coefficient = ((IAST) coefficient).get(1);
			}
		} else {
			coefficient = value;
		}
		return coefficient;
	}

	private void collectTerm(ISymbol sym, IExpr expr) {
		if (expr instanceof IAST) {
			IAST term = (IAST) expr;
			if (term.isASTSizeGE(F.Times, 2)) {
				for (int i = 1; i < term.size(); i++) {
					if (sym.equals(term.get(i))) {
						IAST temp = F.ast(term, F.Times, false, i, i + 1);
						addToMap(F.C1, temp);
						return;
					} else if (term.get(i).isAST(F.Power, 3)) {
						IAST pow = (IAST) term.get(i);
						if (pow.get(1).equals(sym) && pow.get(2) instanceof ISignedNumber) {
							IAST temp = F.ast(term, F.Times, false, i, i + 1);
							addToMap((ISignedNumber) pow.get(2), temp);
							return;
						}
					}
				}
			} else if (term.isAST(F.Power, 3)) {
				if (term.get(1).equals(sym) && term.get(2) instanceof ISignedNumber) {
					addToMap((ISignedNumber) term.get(2), F.C1);
					return;
				}
			}
		} else if (expr instanceof ISymbol && expr.equals(sym)) {
			addToMap(F.C1, F.C1);
			return;
		}
		addToMap(F.C0, expr);
	}

	private void collectTermN(ISymbol sym, IExpr expr) {
		if (expr instanceof IAST) {
			IAST term = (IAST) expr;
			if (term.isASTSizeGE(F.Times, 2)) {
				for (int i = 1; i < term.size(); i++) {
					if (sym.equals(term.get(i))) {
						IAST temp = F.ast(term, F.Times, false, i, i + 1);
						addToMap(F.CD1, temp);
						return;
					} else if (term.get(i).isAST(F.Power, 3)) {
						IAST pow = (IAST) term.get(i);
						if (pow.get(1).equals(sym) && pow.get(2).isSignedNumber()) {
							IAST temp = F.ast(term, F.Times, false, i, i + 1);
							addToMap((ISignedNumber) pow.get(2), temp);
							return;
						}
					}
				}
			} else if (term.isAST(F.Power, 3)) {
				if (term.get(1).equals(sym) && term.get(2).isSignedNumber()) {
					addToMap((ISignedNumber) term.get(2), F.CD1);
					return;
				}
			}
		} else if (expr instanceof ISymbol && expr.equals(sym)) {
			addToMap(F.CD1, F.CD1);
			return;
		}
		addToMap(F.CD0, expr);
	}

	public IAST addToMap(final ISignedNumber key, final IExpr value) {
		IAST temp = map.get(key);
		if (temp == null) {
			temp = F.Plus();
			temp.add(value);
			map.put(key, temp);
		} else {
			temp.add(value);
		}
		return temp;
	}

}
