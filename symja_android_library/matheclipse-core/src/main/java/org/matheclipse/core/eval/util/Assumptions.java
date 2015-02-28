package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Assumptions extends AbstractAssumptions {
	private Map<IExpr, ISymbol> elementsMap = new HashMap<IExpr, ISymbol>();

	private Assumptions() {

	}

	/**
	 * Create <code>Assumptions</code> from the given expression. If the creation is not possible return <code>null</code>
	 * 
	 * @param expr
	 * @return <code>null</code> if <code>Assumptions</code> could not be created from the given expression.
	 */
	public static IAssumptions getInstance(IExpr expr) {

		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			Assumptions assumptions = new Assumptions();
			if (ast.isAST(F.Element, 3)) {
				if (addElement(ast, assumptions)) {
					return assumptions;
				}
			} else if (ast.isASTSizeGE(F.And, 2) || ast.isASTSizeGE(F.List, 2)) {
				return addList(ast, assumptions);
			}
		}

		return null;
	}

	private static IAssumptions addList(IAST ast, Assumptions assumptions) {
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST(F.Element, 3)) {
				if (!addElement((IAST) ast.get(i), assumptions)) {
					return null;
				}
			}
		}
		return assumptions;
	}

	private static boolean addElement(IAST element, Assumptions assumptions) {
		if (element.arg2().isSymbol()) {
			ISymbol domain = (ISymbol) element.arg2();
			if (domain.equals(F.Algebraics) || domain.equals(F.Booleans) || domain.equals(F.Complexes) || domain.equals(F.Integers)
					|| domain.equals(F.Primes) || domain.equals(F.Rationals) || domain.equals(F.Reals)) {
				assumptions.elementsMap.put(element.arg1(), domain);
				return true;
			}
		}
		return false;
	}

	final private boolean isDomain(IExpr expr, ISymbol domain) {
		ISymbol mappedDomain = elementsMap.get(expr);
		return mappedDomain != null && mappedDomain.equals(domain);
	}

	@Override
	public boolean isAlgebraic(IExpr expr) {
		return isDomain(expr, F.Algebraics);
	}

	@Override
	public boolean isBoolean(IExpr expr) {
		return isDomain(expr, F.Booleans); 
	}

	@Override
	public boolean isComplex(IExpr expr) {
		return isDomain(expr, F.Complexes); 
	}

	@Override
	public boolean isInteger(IExpr expr) {
		return isDomain(expr, F.Integers); 
	}

	@Override
	public boolean isPrime(IExpr expr) {
		return isDomain(expr, F.Primes); 
	}

	@Override
	public boolean isRational(IExpr expr) {
		return isDomain(expr, F.Rationals); 
	}

	@Override
	public boolean isReal(IExpr expr) {
		return isDomain(expr, F.Reals); 
	}

}
