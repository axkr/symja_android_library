package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class Assumptions extends AbstractAssumptions {

	private static class SignedNumberRelations {

		final static int GREATER_ID = 0;
		final static int GREATEREQUAL_ID = 1;
		final static int LESS_ID = 2;
		final static int LESSEQUAL_ID = 3;
		final static int EQUALS_ID = 4;

		final private ISignedNumber[] values;

		public SignedNumberRelations() {
			this.values = new ISignedNumber[5];
		}

		final public void addGreater(ISignedNumber expr) {
			values[GREATER_ID] = expr;
		}

		final public void addGreaterEqual(ISignedNumber expr) {
			values[GREATEREQUAL_ID] = expr;
		}

		final public void addLess(ISignedNumber expr) {
			values[LESS_ID] = expr;
		}

		final public void addLessEqual(ISignedNumber expr) {
			values[LESSEQUAL_ID] = expr;
		}

		final public void addEquals(ISignedNumber expr) {
			values[EQUALS_ID] = expr;
		}

		final public ISignedNumber getGreater() {
			return values[GREATER_ID];
		}

		final public ISignedNumber getGreaterEqual() {
			return values[GREATEREQUAL_ID];
		}

		final public ISignedNumber getLess() {
			return values[LESS_ID];
		}

		final public ISignedNumber getLessEqual() {
			return values[LESSEQUAL_ID];
		}

		final public ISignedNumber getEquals() {
			return values[EQUALS_ID];
		}
	}

	/**
	 * Map for storing the domain of an expression
	 */
	private Map<IExpr, ISymbol> elementsMap = new HashMap<IExpr, ISymbol>();

	private Map<IExpr, SignedNumberRelations> valueMap = new HashMap<IExpr, SignedNumberRelations>();

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
			} else if (ast.isAST(F.Greater, 3)) {
				if (addGreater(ast, assumptions)) {
					return assumptions;
				}
			} else if (ast.isAST(F.GreaterEqual, 3)) {
				if (addGreaterEqual(ast, assumptions)) {
					return assumptions;
				}
			} else if (ast.isAST(F.Less, 3)) {
				if (addLess(ast, assumptions)) {
					return assumptions;
				}
			} else if (ast.isAST(F.LessEqual, 3)) {
				if (addLessEqual(ast, assumptions)) {
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
			} else if (ast.isAST(F.Greater, 3)) {
				if (!addGreater((IAST) ast.get(i), assumptions)) {
					return null;
				}
			} else if (ast.isAST(F.GreaterEqual, 3)) {
				if (!addGreaterEqual((IAST) ast.get(i), assumptions)) {
					return null;
				}
			} else if (ast.isAST(F.Less, 3)) {
				if (!addLess((IAST) ast.get(i), assumptions)) {
					return null;
				}
			} else if (ast.isAST(F.LessEqual, 3)) {
				if (!addLessEqual((IAST) ast.get(i), assumptions)) {
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
				IExpr arg1 = element.arg1();
				if (arg1.isAST(F.Alternatives)) {
					IAST alternatives = (IAST) arg1;
					for (int i = 1; i < alternatives.size(); i++) {
						assumptions.elementsMap.put(alternatives.get(i), domain);
					}
				} else {
					assumptions.elementsMap.put(arg1, domain);
				}
				return true;
			}
		}
		return false;
	}

	private static boolean addGreater(IAST element, Assumptions assumptions) {
		if (element.arg2().isSignedNumber()) {
			SignedNumberRelations gla = assumptions.valueMap.get(element.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
				gla.addGreater((ISignedNumber) element.arg2());
			} else {
				gla.addGreater((ISignedNumber) element.arg2());
			}
			assumptions.valueMap.put(element.arg1(), gla);
			return true;
		}
		if (element.arg1().isSignedNumber()) {
			ISignedNumber num = (ISignedNumber) element.arg1();
			IExpr key = element.arg2();
			SignedNumberRelations gla = assumptions.valueMap.get(key);
			if (gla == null) {
				gla = new SignedNumberRelations();
				gla.addLess(num);
			} else {
				gla.addLess(num);
			}
			assumptions.valueMap.put(key, gla);
			return true;
		}
		return false;
	}

	private static boolean addGreaterEqual(IAST element, Assumptions assumptions) {
		// arg1 >= arg2
		if (element.arg2().isSignedNumber()) {
			SignedNumberRelations gla = assumptions.valueMap.get(element.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addGreaterEqual((ISignedNumber) element.arg2());
			assumptions.valueMap.put(element.arg1(), gla);
			return true;
		}

		if (element.arg1().isSignedNumber()) {
			ISignedNumber num = (ISignedNumber) element.arg1();
			IExpr key = element.arg2();
			SignedNumberRelations gla = assumptions.valueMap.get(key);
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addLessEqual(num);
			assumptions.valueMap.put(key, gla);
			return true;
		}
		return false;
	}

	private static boolean addLess(IAST element, Assumptions assumptions) {
		if (element.arg2().isSignedNumber()) {
			SignedNumberRelations gla = assumptions.valueMap.get(element.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
				gla.addLess((ISignedNumber) element.arg2());
			} else {
				gla.addLess((ISignedNumber) element.arg2());
			}
			assumptions.valueMap.put(element.arg1(), gla);
			return true;
		}
		if (element.arg1().isSignedNumber()) {
			ISignedNumber num = (ISignedNumber) element.arg1();
			IExpr key = element.arg2();
			SignedNumberRelations gla = assumptions.valueMap.get(key);
			if (gla == null) {
				gla = new SignedNumberRelations();
				gla.addGreater(num);
			} else {
				gla.addGreater(num);
			}
			assumptions.valueMap.put(key, gla);
			return true;
		}
		return false;
	}

	private static boolean addLessEqual(IAST element, Assumptions assumptions) {
		// arg1 <= arg2;
		if (element.arg2().isSignedNumber()) {
			SignedNumberRelations gla = assumptions.valueMap.get(element.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addLessEqual((ISignedNumber) element.arg2());
			assumptions.valueMap.put(element.arg1(), gla);
			return true;
		}
		if (element.arg1().isSignedNumber()) {
			ISignedNumber num = (ISignedNumber) element.arg1();
			IExpr key = element.arg2();
			SignedNumberRelations gla = assumptions.valueMap.get(key);
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addGreaterEqual(num);
			assumptions.valueMap.put(key, gla);
			return true;
		}
		return false;
	}

	@Override
	public boolean isNegative(IExpr expr) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getLess();
			if (num != null) {
				if (!num.isZero()) {
					if (!num.isLessThan(F.C0)) {
						return false;
					}
				}
				result = true;
			}
			if (!result) {
				num = gla.getLessEqual();
				if (num != null) {
					if (!num.isLessThan(F.C0)) {
						return false;
					}
					result = true;
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean isPositive(IExpr expr) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getGreater();
			if (num != null) {
				if (!num.isZero()) {
					if (!num.isGreaterThan(F.C0)) {
						return false;
					}
				}
				result = true;
			}
			if (!result) {
				num = gla.getGreaterEqual();
				if (num != null) {
					if (!num.isGreaterThan(F.C0)) {
						return false;
					}
					result = true;
				}
			}
			return result;
		}
		return false;
	}

	@Override
	public boolean isNonNegative(IExpr expr) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getGreater();
			if (num != null) {
				if (num.isZero()) {
					result = true;
				}
			}
			if (!result) {
				num = gla.getGreaterEqual();
				if (num != null) {
					if (num.isZero()) {
						result = true;
					}
				}
			}
			if (result) {
				return true;
			}
			return isPositive(expr);
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
