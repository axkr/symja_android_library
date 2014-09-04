package org.matheclipse.core.polynomials;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Monomial implements Comparable<Monomial>, Cloneable {
	ExponentArray fExpArray;
	IExpr fCoefficient;

	public Monomial(IExpr coefficient, ExponentArray array) {
		fCoefficient = coefficient;
		fExpArray = array;
	}

	protected void appendToExpr(IAST list, IAST variables) {
		long[] arr = fExpArray.getExponents();
		list.add(fCoefficient);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != 0L) {
				if (arr[i] == 1L) {
					list.add(variables.get(i + 1));
				} else {
					list.add(F.Power(variables.get(i + 1), arr[i]));
				}
			}
		}
	}

	protected void appendToString(StringBuilder buf, IAST variables) {
		long[] arr = fExpArray.getExponents();
		buf.append(fCoefficient.toString());
		buf.append("*");
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != 0L) {
				buf.append(variables.get(i + 1));
				if (arr[i] != 1L) {
					buf.append("^");
					buf.append(arr[i]);
				}
			}
		}
	}

	@Override
	protected Monomial clone() {
		Monomial m;
		try {
			m = (Monomial) super.clone();
			m.fExpArray = fExpArray.clone();
			return m;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int compareTo(Monomial o) {
		int c = fExpArray.compareTo(o.fExpArray);
		if (c != 0) {
			return c;
		}
		return fCoefficient.compareTo(o.fCoefficient);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Monomial) {
			Monomial other = (Monomial) obj;
			return fCoefficient.equals(other.fCoefficient) && fExpArray.equals(other.fExpArray);
		}
		return false;
	}

	public IExpr getCoefficient() {
		return fCoefficient;
	}

	public ExponentArray getExponents() {
		return fExpArray;
	}

	@Override
	public int hashCode() {
		return fCoefficient.hashCode() * fExpArray.hashCode();
	}

	public void setCoefficient(IExpr fCoefficient) {
		this.fCoefficient = fCoefficient;
	}

	/**
	 * 
	 * @param lhs
	 * @param coefficient
	 */
	protected void timesByMonomial(IExpr coefficient) {
		setCoefficient(getCoefficient().times(coefficient));
	}
	

	protected void timesByMonomial(int position) {
		getExponents().timesBy(position);
	}

	protected void timesByMonomial(Monomial rhs) {
		setCoefficient(getCoefficient().times(rhs.getCoefficient()));
		getExponents().timesBy(rhs.getExponents());
	}
	
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		long[] arr = fExpArray.getExponents();
		buf.append(fCoefficient.toString());
		buf.append("*");
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != 0L) {
				buf.append("v[" + i + "]");
				if (arr[i] != 1L) {
					buf.append("^");
					buf.append(arr[i]);
				}
			}
		}
		return buf.toString();
	}
	
}
