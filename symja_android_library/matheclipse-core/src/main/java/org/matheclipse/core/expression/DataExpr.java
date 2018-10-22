package org.matheclipse.core.expression;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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
public class DataExpr implements IDataExpr {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3866374701012688L;

	public static DataExpr newInstance(final Symbol head, final byte[] bytes) {
		DataExpr d = new DataExpr(head, WL.deserialize(bytes));
		return d;
	}

	/**
	 * Be cautious with this method, no new internal String object is created
	 * 
	 * @param value
	 * @return
	 */
	public static DataExpr newInstance(final Symbol head, final Object value) {
		return new DataExpr(head, value);
	}

	private Symbol fHead;
	private Object fData;

	private DataExpr(final Symbol head, final Object data) {
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
	public String toString() {
		return fHead + "(" + fData.toString() + ")";
	}

}