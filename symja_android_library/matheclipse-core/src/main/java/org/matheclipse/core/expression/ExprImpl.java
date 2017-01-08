package org.matheclipse.core.expression;

import org.hipparchus.complex.Complex;
import java.io.Serializable;
import java.util.List;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import edu.jas.structure.ElemFactory;

/**
 * Abstract base class for atomic expression objects.
 */
public abstract class ExprImpl implements IExpr, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3346614106664542983L;

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}


}
