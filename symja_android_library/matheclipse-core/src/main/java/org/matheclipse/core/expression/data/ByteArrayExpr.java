package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Tries;

public class ByteArrayExpr extends DataExpr<byte[]> implements Externalizable {

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

	public ByteArrayExpr() {
		super(S.ByteArray, null);
	}

	protected ByteArrayExpr(final byte[] array) {
		super(S.ByteArray, array);
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

	public IAST normal(boolean nilIfUnevaluated) {
		byte[] bArray = toData();
		return WL.toList(bArray);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		final int len = in.readInt();
		fData = new byte[len];
		in.read(fData);
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeInt(fData.length);
		output.write(fData);
	}
}
