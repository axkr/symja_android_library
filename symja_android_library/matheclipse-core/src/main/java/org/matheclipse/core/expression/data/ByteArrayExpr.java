package org.matheclipse.core.expression.data;

import org.gavaghan.geodesy.GlobalPosition;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class ByteArrayExpr extends DataExpr<byte[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5799157739970931450L;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static ByteArrayExpr newInstance(final byte[] value) {
		return new ByteArrayExpr(value);
	}

	protected ByteArrayExpr(final byte[] array) {
		super(F.ByteArray, array);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ByteArrayExpr) {
			return fData.equals(((ByteArrayExpr) obj).fData);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (fData == null) ? 541 : 541 + fData.hashCode();
	}

	@Override
	public IExpr copy() {
		return new ByteArrayExpr(fData);
	}

}
