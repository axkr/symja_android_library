package org.matheclipse.core.expression.data;

import java.util.Arrays;

import org.gavaghan.geodesy.GlobalPosition;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

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
			return Arrays.equals(fData, ((ByteArrayExpr) obj).fData);
		}
		return false;
	}

	@Override
	public int hierarchy() {
		return BYTEARRAYID;
	}

	@Override
	public int hashCode() {
		return (fData == null) ? 541 : 541 + Arrays.hashCode(fData);
	}

	@Override
	public IExpr copy() {
		return new ByteArrayExpr(fData);
	}

	public IASTMutable normal(boolean nilIfUnevaluated) {
		byte[] bArray = toData();
		return WL.toList(bArray);
	}
}
