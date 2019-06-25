package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A concrete IDataExpr implementation. Container for a header and data object.
 * 
 * @see org.matheclipse.core.interfaces.IDataExpr
 */
public class DataExpr<T> implements IDataExpr<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4987827851920443376L;

	/**
	 * Be cautious with this method, no new internal IExpr object is created
	 * 
	 * @param value
	 * @return
	 */
	public static <T> DataExpr<T> newInstance(final IBuiltInSymbol head, final T value) {
		return new DataExpr(head, value);
	}

	private IBuiltInSymbol fHead;
	private T fData;

	private DataExpr(final IBuiltInSymbol head, final T data) {
		fHead = head;
		fData = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return 0L;
	}

	@Override
	public IExpr copy() {
		return new DataExpr(fHead, fData);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DataExpr) {
			return fData.equals(((DataExpr) obj).fData);
		}
		return false;
	}

	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public String fullFormString() {
		return fHead + "(" + fData.toString() + ")";
	}

	@Override
	public int hashCode() {
		return (fData == null) ? 59 : 59 + fData.hashCode();
	}

	@Override
	public ISymbol head() {
		return fHead;
	}

	@Override
	public int hierarchy() {
		return DATAID;
	}

	@Override
	public T toData() {
		return fData;
	}

	@Override
	public String toString() {
		if (fHead.equals(F.ByteArray)) {
			return fHead.toString() + "[" + ((byte[]) fData).length + " Bytes]";
		}
		return fHead + "[" + fData.toString() + "]";
	}

}