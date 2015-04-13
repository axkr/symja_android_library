package org.matheclipse.core.expression;

import java.io.ObjectStreamException;

import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * The ExprID class holds an index wrapper for the <code>F.GLOBAL_IDS[]</code> array of predefined constant expressions. The ExprID
 * is especially used in the serialization and deserialization of an <code>IExpr</code> object, by representing an index entry in the
 * <code>F.GLOBAL_IDS[]</code> array.
 *
 * @see F#GLOBAL_IDS
 */
public class ExprID extends ExprImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2183178872222820512L;

	private short fExprID;

	/**
	 * The ExprID class holds an index wrapper for the <code>F.GLOBAL_IDS[]</code> array of predefined constant expressions. The ExprID
	 * is especially used in the serialization and deserialization of an <code>IExpr</code> object, by representing an index entry in the
	 * <code>F.GLOBAL_IDS[]</code> array.
	 *
	 * @param the index in array <code>F.GLOBAL_IDS</code>
	 * @see F#GLOBAL_IDS
	 */
	public ExprID(short exprID) {
		fExprID = exprID;
	}

	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return null;
	}

	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return false;
	}

	@Override
	public int accept(IVisitorInt visitor) {
		return 0;
	}

	@Override
	public long accept(IVisitorLong visitor) {
		return 0;
	}

	@Override
	public int hierarchy() {
		return 0;
	}

	@Override
	public ISymbol head() {
		return null;
	}

	/**
	 * 
	 * @return the <code>F.GLOBAL_IDS[ExprID]</code> expression
	 * @throws ObjectStreamException
	 */
	public Object readResolve() throws ObjectStreamException {
		return F.GLOBAL_IDS[fExprID];
	}

}
