package org.matheclipse.core.expression;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISignedNumber;

public class ExprFieldElement implements FieldElement<ExprFieldElement>, Comparable<ExprFieldElement> {
	private final IExpr val;

	public ExprFieldElement(IExpr v) {
		if (v.isFraction()) {
			val = ((IFraction) v).normalize();
			return;
		} else if (v.isComplex()) {
			val = ((IComplex) v).normalize();
			return;
		}
		val = v;
	}

	@Override
	public ExprFieldElement add(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.plus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(F.Plus(val, a.val)));
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
		return new ExprFieldElement(F.evalExpandAll(F.Divide(val, a.val)));
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
		return new ExprFieldElement(F.evalExpandAll(F.Times(val, a.val)));
	}

	@Override
	public ExprFieldElement multiply(int a) {
		if (val.isAtom()) {
			return new ExprFieldElement(val.times(F.integer(a)));
		}
		return new ExprFieldElement(F.evalExpandAll(F.Times(val, F.integer(a))));
	}

	@Override
	public ExprFieldElement negate() {
		if (val.isAtom()) {
			return new ExprFieldElement(val.times(F.CN1));
		}
		return new ExprFieldElement(F.evalExpandAll(F.Times(val, F.CN1)));
	}

	@Override
	public ExprFieldElement reciprocal() {
		if (val.isSignedNumber()) {
			return new ExprFieldElement(((ISignedNumber) val).inverse());
		}
		return new ExprFieldElement(F.evalExpandAll(val.power(-1)));
	}

	@Override
	public ExprFieldElement subtract(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.minus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(F.Subtract(val, a.val)));
	}

	@Override
	public String toString() {
		return val.toString();
	}

}
