package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Modular_arithmetic">Wikipedia -
 * Modular arithmetic</a>
 */
public class Mod extends AbstractArg2 {
	public Mod() {
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		IInteger temp = i1;
		if (temp.compareTo(F.C0) < 0) {
			temp = temp.multiply(F.CN1);
		}
		return ((IntegerSym) i0).mod((IntegerSym) temp);
	}

}
