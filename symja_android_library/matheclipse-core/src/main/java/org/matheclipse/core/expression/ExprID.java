package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;

import org.matheclipse.core.builtin.constant.E;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

public class ExprID extends ExprImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2183178872222820512L;

	private int fExprID;

	public ExprID(int exprID) {
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

	public Object readResolve() throws ObjectStreamException {
		return F.GLOBAL_IDS[fExprID];
	}

}
