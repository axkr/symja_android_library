package org.matheclipse.core.expression.data;

import org.gavaghan.geodesy.GlobalPosition;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
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
	public IExpr copy() {
		return new ByteArrayExpr(fData);
	}

}
