package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
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

		final public void addEquals(ISignedNumber expr) {
			values[EQUALS_ID] = expr;
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

		final public ISignedNumber getEquals() {
			return values[EQUALS_ID];
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

		final public boolean isLessOrGreaterRelation() {
			for (int i = 0; i <= LESSEQUAL_ID; i++) {
				if (values != null) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Add a domain. Domain can be <code>Algebraics, Booleans, Complexes, Integers, Primes, Rationals, Reals</code>
	 * 
	 * @param element
	 *            a <code>Element(x, &lt;domain&gt;)</code> expression
	 * @param assumptions
	 * @return
	 */
	private static boolean addElement(IAST element, Assumptions assumptions) {
		if (element.arg2().isSymbol()) {
			ISymbol domain = (ISymbol) element.arg2();
			if (domain.equals(F.Algebraics) || domain.equals(F.Booleans) || domain.equals(F.Complexes)
					|| domain.equals(F.Integers) || domain.equals(F.Primes) || domain.equals(F.Rationals)
					|| domain.equals(F.Reals)) {
				IExpr arg1 = element.arg1();
				if (arg1.isAST(F.Alternatives)) {
					((IAST) arg1).forEach(x -> assumptions.elementsMap.put(x, domain));
				} else {
					assumptions.elementsMap.put(arg1, domain);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a distribution.
	 * 
	 * @param element
	 *            a <code>Distributed(x, &lt;distribution&gt;)</code> expression
	 * @param assumptions
	 * @return
	 */
	private static boolean addDistribution(IAST element, Assumptions assumptions) {
		if (element.arg2().isAST()) {
			IAST dist = (IAST) element.arg2();

			ISymbol head = (ISymbol) dist.head();
			if (head instanceof IBuiltInSymbol) {
				IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
				if (evaluator instanceof IDistribution) {
					IExpr arg1 = element.arg1();
					if (arg1.isAST(F.Alternatives)) {
						((IAST) arg1).forEach(x -> assumptions.distributionsMap.put(x, dist));
					} else {
						assumptions.distributionsMap.put(arg1, dist);
					}
					return true;
				}
			}
		}
		return false;
	}

	private static boolean addGreater(IAST greaterAST, Assumptions assumptions) {
		if (greaterAST.isAST3()) {
			// arg1 > arg2 > arg3
			IExpr arg1 = greaterAST.arg1();
			IExpr arg2 = greaterAST.arg2();
			IExpr arg3 = greaterAST.arg3();
			if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
				if (((ISignedNumber) arg1).isGreaterThan(((ISignedNumber) arg3))) {
					ISignedNumber num1 = (ISignedNumber) arg1;
					ISignedNumber num3 = (ISignedNumber) arg3;
					IExpr key = arg2;
					SignedNumberRelations gla = assumptions.valueMap.get(key);
					if (gla == null) {
						gla = new SignedNumberRelations();
					}
					gla.addLess(num1);
					gla.addGreater(num3);
					assumptions.valueMap.put(key, gla);
					return true;
				}
			}
			return false;
		}

		// arg1 > arg2
		if (greaterAST.arg2().isReal()) {
			SignedNumberRelations gla = assumptions.valueMap.get(greaterAST.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
				gla.addGreater((ISignedNumber) greaterAST.arg2());
			} else {
				gla.addGreater((ISignedNumber) greaterAST.arg2());
			}
			assumptions.valueMap.put(greaterAST.arg1(), gla);
			return true;
		}
		if (greaterAST.arg1().isReal()) {
			ISignedNumber num = (ISignedNumber) greaterAST.arg1();
			IExpr key = greaterAST.arg2();
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

	private static boolean addGreaterEqual(IAST greaterEqualAST, Assumptions assumptions) {
		if (greaterEqualAST.isAST3()) {
			// arg1 >= arg2 >= arg3
			IExpr arg1 = greaterEqualAST.arg1();
			IExpr arg2 = greaterEqualAST.arg2();
			IExpr arg3 = greaterEqualAST.arg3();
			if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
				if (!((ISignedNumber) arg1).isLessThan(((ISignedNumber) arg3))) {
					ISignedNumber num1 = (ISignedNumber) arg1;
					ISignedNumber num3 = (ISignedNumber) arg3;
					IExpr key = arg2;
					SignedNumberRelations gla = assumptions.valueMap.get(key);
					if (gla == null) {
						gla = new SignedNumberRelations();
					}
					gla.addLessEqual(num1);
					gla.addGreaterEqual(num3);
					assumptions.valueMap.put(key, gla);
					return true;
				}
			}
			return false;
		}

		// arg1 >= arg2
		if (greaterEqualAST.arg2().isReal()) {
			SignedNumberRelations gla = assumptions.valueMap.get(greaterEqualAST.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addGreaterEqual((ISignedNumber) greaterEqualAST.arg2());
			assumptions.valueMap.put(greaterEqualAST.arg1(), gla);
			return true;
		}

		if (greaterEqualAST.arg1().isReal()) {
			ISignedNumber num = (ISignedNumber) greaterEqualAST.arg1();
			IExpr key = greaterEqualAST.arg2();
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

	private static boolean addLess(IAST lessAST, Assumptions assumptions) {
		if (lessAST.isAST3()) {
			// arg1 < arg2 < arg3;
			IExpr arg1 = lessAST.arg1();
			IExpr arg2 = lessAST.arg2();
			IExpr arg3 = lessAST.arg3();
			if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
				if (((ISignedNumber) arg1).isLessThan(((ISignedNumber) arg3))) {
					ISignedNumber num1 = (ISignedNumber) arg1;
					ISignedNumber num3 = (ISignedNumber) arg3;
					IExpr key = arg2;
					SignedNumberRelations gla = assumptions.valueMap.get(key);
					if (gla == null) {
						gla = new SignedNumberRelations();
					}
					gla.addGreater(num1);
					gla.addLess(num3);
					assumptions.valueMap.put(key, gla);
					return true;
				}
			}
			return false;
		}

		// arg1 < arg2
		if (lessAST.arg2().isReal()) {
			SignedNumberRelations gla = assumptions.valueMap.get(lessAST.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addLess((ISignedNumber) lessAST.arg2());
			assumptions.valueMap.put(lessAST.arg1(), gla);
			return true;
		}
		if (lessAST.arg1().isReal()) {
			ISignedNumber num = (ISignedNumber) lessAST.arg1();
			IExpr key = lessAST.arg2();
			SignedNumberRelations gla = assumptions.valueMap.get(key);
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addGreater(num);
			assumptions.valueMap.put(key, gla);
			return true;
		}
		return false;
	}

	private static boolean addLessEqual(IAST lessEqualAST, Assumptions assumptions) {

		if (lessEqualAST.isAST3()) {
			// arg1 <= arg2 <= arg3
			IExpr arg1 = lessEqualAST.arg1();
			IExpr arg2 = lessEqualAST.arg2();
			IExpr arg3 = lessEqualAST.arg3();
			if (arg1.isReal() && arg3.isReal() && !arg2.isNumber()) {
				if (!((ISignedNumber) arg1).isGreaterThan(((ISignedNumber) arg3))) {
					ISignedNumber num1 = (ISignedNumber) arg1;
					ISignedNumber num3 = (ISignedNumber) arg3;
					IExpr key = arg2;
					SignedNumberRelations gla = assumptions.valueMap.get(key);
					if (gla == null) {
						gla = new SignedNumberRelations();
					}
					gla.addGreaterEqual(num1);
					gla.addLessEqual(num3);
					assumptions.valueMap.put(key, gla);
					return true;
				}
			}
			return false;
		}

		// arg1 <= arg2;
		if (lessEqualAST.arg2().isReal()) {
			SignedNumberRelations gla = assumptions.valueMap.get(lessEqualAST.arg1());
			if (gla == null) {
				gla = new SignedNumberRelations();
			}
			gla.addLessEqual((ISignedNumber) lessEqualAST.arg2());
			assumptions.valueMap.put(lessEqualAST.arg1(), gla);
			return true;
		}
		if (lessEqualAST.arg1().isReal()) {
			ISignedNumber num = (ISignedNumber) lessEqualAST.arg1();
			IExpr key = lessEqualAST.arg2();
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

	private static IAssumptions addList(IAST ast, Assumptions assumptions) {
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isAST()) {
				IAST temp = (IAST) ast.get(i);
				if (temp.isAST(F.Element, 3)) {
					if (!addElement(temp, assumptions)) {
						return null;
					}
				} else if (temp.isAST(F.Greater, 3)) {
					if (!addGreater(temp, assumptions)) {
						return null;
					}
				} else if (temp.isAST(F.GreaterEqual, 3)) {
					if (!addGreaterEqual(temp, assumptions)) {
						return null;
					}
				} else if (temp.isAST(F.Less, 3)) {
					if (!addLess(temp, assumptions)) {
						return null;
					}
				} else if (temp.isAST(F.LessEqual, 3)) {
					if (!addLessEqual(temp, assumptions)) {
						return null;
					}
				}
			}
		}
		return assumptions;
	}

	/**
	 * Create a new <code>IAssumptions</code> from the given expression. If the creation is not possible return
	 * <code>null</code>
	 * 
	 * @param expr
	 * @return <code>null</code> if <code>Assumptions</code> could not be created from the given expression.
	 */
	public static IAssumptions getInstance(IExpr expr) {
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			Assumptions assumptions = new Assumptions();
			return assumptions.addAssumption(ast);
		}

		return null;
	}

	/**
	 * Add more assumptions from the given ast. If the creation is not possible return <code>null</code>
	 * 
	 * @param ast
	 *            the assumptions which should be added to the <code>assumptions</code> instance.
	 * @param assumptions
	 * @return <code>null</code> if <code>assumptions</code> could not be added from the given expression.
	 */
	public IAssumptions addAssumption(IAST ast) {
		if (ast.isAST(F.Element, 3)) {
			if (addElement(ast, this)) {
				return this;
			}
		} else if (ast.isAST(F.Greater, 3, 4)) {
			if (addGreater(ast, this)) {
				return this;
			}
		} else if (ast.isAST(F.GreaterEqual, 3, 4)) {
			if (addGreaterEqual(ast, this)) {
				return this;
			}
		} else if (ast.isAST(F.Less, 3, 4)) {
			if (addLess(ast, this)) {
				return this;
			}
		} else if (ast.isAST(F.LessEqual, 3, 4)) {
			if (addLessEqual(ast, this)) {
				return this;
			}
		} else if (ast.isASTSizeGE(F.And, 2) || ast.isASTSizeGE(F.List, 2)) {
			return addList(ast, this);
		} else if (ast.isAST(F.Distributed, 3)) {
			if (addDistribution(ast, this)) {
				return this;
			}
		}
		return null;
	}

	final public IAST distribution(IExpr expr) {
		IAST dist = distributionsMap.get(expr);
		return (dist == null) ? F.NIL : dist;
	}

	/**
	 * Map for storing the domain of an expression
	 */
	private Map<IExpr, ISymbol> elementsMap = new HashMap<IExpr, ISymbol>();

	private Map<IExpr, IAST> distributionsMap = new HashMap<IExpr, IAST>();

	private Map<IExpr, SignedNumberRelations> valueMap = new HashMap<IExpr, SignedNumberRelations>();

	private Assumptions() {

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

	final private boolean isDomain(IExpr expr, ISymbol domain) {
		ISymbol mappedDomain = elementsMap.get(expr);
		return mappedDomain != null && mappedDomain.equals(domain);
	}

	@Override
	public boolean isGreaterEqual(IExpr expr, ISignedNumber number) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getGreater();
			if (num != null) {
				if (num.equals(number)) {
					result = true;
				}
			}
			if (!result) {
				num = gla.getGreaterEqual();
				if (num != null) {
					if (num.equals(number)) {
						result = true;
					}
				}
			}
			if (result) {
				return true;
			}
			return isGreaterThan(expr, number);
		}
		return false;
	}

	@Override
	public boolean isGreaterThan(IExpr expr, ISignedNumber number) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getGreater();
			if (num != null) {
				if (!num.equals(number)) {
					if (!num.isGreaterThan(number)) {
						return false;
					}
				}
				result = true;
			}
			if (!result) {
				num = gla.getGreaterEqual();
				if (num != null) {
					if (!num.isGreaterThan(number)) {
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
	public boolean isInteger(IExpr expr) {
		return isDomain(expr, F.Integers);
	}

	@Override
	public boolean isLessThan(IExpr expr, ISignedNumber number) {
		ISignedNumber num;
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null) {
			boolean result = false;
			num = gla.getLess();
			if (num != null) {
				if (!num.equals(number)) {
					if (!num.isLessThan(number)) {
						return false;
					}
				}
				result = true;
			}
			if (!result) {
				num = gla.getLessEqual();
				if (num != null) {
					if (!num.isLessThan(number)) {
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
	public boolean isNegative(IExpr expr) {
		return isLessThan(expr, F.C0);
	}

	@Override
	public boolean isNonNegative(IExpr expr) {
		return isGreaterEqual(expr, F.C0);
	}

	@Override
	public boolean isPositive(IExpr expr) {
		return isGreaterThan(expr, F.C0);
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
		SignedNumberRelations gla = valueMap.get(expr);
		if (gla != null && gla.isLessOrGreaterRelation()) {
			return true;
		}
		return isDomain(expr, F.Reals);
	}

}
