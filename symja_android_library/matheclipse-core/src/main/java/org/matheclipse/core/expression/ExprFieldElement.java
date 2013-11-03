package org.matheclipse.core.expression;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.matheclipse.core.interfaces.IExpr;

public class ExprFieldElement implements FieldElement<ExprFieldElement>, Comparable<ExprFieldElement> {
	private final IExpr val;

	public ExprFieldElement(IExpr v) {
		val = v;
	}

	@Override
	public ExprFieldElement add(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.plus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.plus(a.val)));
	}

	@Override
	public int compareTo(ExprFieldElement o) {
		return val.compareTo(o.val);
	}

	@Override
	public ExprFieldElement divide(ExprFieldElement a) throws ArithmeticException {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.divide(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.divide(a.val)));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ExprFieldElement) {
			return val.equals(((ExprFieldElement) obj).val);
		}
		return false;
	}

	final public IExpr getExpr() {
		return val;
	}

	@Override
	public Field<ExprFieldElement> getField() {
		return ExprField.CONST;
	}

	@Override
	public int hashCode() {
		return val.hashCode();
	}

	@Override
	public ExprFieldElement multiply(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.times(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.times(a.val)));
	}

	@Override
	public ExprFieldElement multiply(int a) {
		// if (val.isAtom()) {
		// return new ExprFieldElement(val.times(a.val));
		// }
		return new ExprFieldElement(F.evalExpandAll(val.times(F.integer(a))));
	}

	@Override
	public ExprFieldElement negate() {
		return new ExprFieldElement(F.evalExpandAll(val.times(F.CN1)));
	}

	@Override
	public ExprFieldElement reciprocal() {
		return new ExprFieldElement(F.evalExpandAll(val.power(-1)));
	}

	@Override
	public ExprFieldElement subtract(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.minus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.minus(a.val)));
	}

}
