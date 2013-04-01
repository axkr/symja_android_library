package org.matheclipse.core.expression;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.matheclipse.core.interfaces.IExpr;

public class ExprFieldElement implements FieldElement<ExprFieldElement>, Comparable<ExprFieldElement> {
	private final IExpr val;

	public ExprFieldElement(IExpr v) {
		val = v;
	}

	final public IExpr getExpr() {
		return val;
	}

	@Override
	public ExprFieldElement divide(ExprFieldElement a) throws ArithmeticException {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.div(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.div(a.val)));
	}

	@Override
	public Field<ExprFieldElement> getField() {
		return ExprField.CONST;
	}

	@Override
	public ExprFieldElement subtract(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.minus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.minus(a.val)));
	}

	@Override
	public ExprFieldElement add(ExprFieldElement a) {
		if (val.isAtom() && a.val.isAtom()) {
			return new ExprFieldElement(val.plus(a.val));
		}
		return new ExprFieldElement(F.evalExpandAll(val.plus(a.val)));
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
//		if (val.isAtom()) {
//			return new ExprFieldElement(val.times(a.val));
//		}
		return new ExprFieldElement(F.evalExpandAll(val.times(F.integer(a))));
	}
	
	@Override
	public int compareTo(ExprFieldElement o) {
		return val.compareTo(o.val);
	}

	@Override
	public ExprFieldElement negate() {
		return new ExprFieldElement(F.evalExpandAll(val.times(F.CN1)));
	}

	@Override 
	public ExprFieldElement reciprocal() {
		return new ExprFieldElement(F.evalExpandAll(val.power(-1)));
	}

}
