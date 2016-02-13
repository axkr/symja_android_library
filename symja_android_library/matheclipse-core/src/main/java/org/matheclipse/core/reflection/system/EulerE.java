package org.matheclipse.core.reflection.system;

import java.util.ArrayList;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.math.BigIntegerMath;

/**
 * Euler number
 * 
 * @see <a href="http://oeis.org/A000364">A000364</a> in the OEIS.
 */
public class EulerE extends AbstractTrigArg1 {

	public EulerE() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			try {
				int n = ((IInteger) arg1).toInt();
				if ((n & 0x00000001) == 0x00000001) {
					return F.C0;
				}
				n /= 2;

				// The list of all Euler numbers as a vector, n=0,2,4,....
				ArrayList<IInteger> a = new ArrayList<IInteger>();
				if (a.size() == 0) {
					a.add(F.C1);
					a.add(F.C1);
					a.add(F.C5);
					a.add(AbstractIntegerSym.valueOf(61));
				}

				IInteger eulerE = eulerE(a, n);
				if (n > 0) {
					n -= 1;
					n %= 2;
					if (n == 0) {
						eulerE = eulerE.negate();
					}
				}
				return eulerE;
			} catch (ArithmeticException e) {
				// integer to large?
			}
		}
		return F.NIL;
	}

	/**
	 * Compute a coefficient in the internal table.
	 * 
	 * @param n
	 *            the zero-based index of the coefficient. n=0 for the E_0 term.
	 */
	protected void set(ArrayList<IInteger> a, final int n) {
		while (n >= a.size()) {
			IInteger val = F.C0;
			boolean sigPos = true;
			int thisn = a.size();
			for (int i = thisn - 1; i > 0; i--) {
				IInteger f = a.get(i);
				f = f.multiply(AbstractIntegerSym.valueOf(BigIntegerMath.binomial(2 * thisn, 2 * i)));
				if (sigPos)
					val = val.add(f);
				else
					val = val.subtract(f);
				sigPos = !sigPos;
			}
			if (thisn % 2 == 0)
				val = val.subtract(F.C1);
			else
				val = val.add(F.C1);
			a.add(val);
		}
	}

	/**
	 * The Euler number at the index provided.
	 * 
	 * @param n
	 *            the index, non-negative.
	 * @return the E_0=E_1=1 , E_2=5, E_3=61 etc
	 */
	public IInteger eulerE(ArrayList<IInteger> a, int n) {
		set(a, n);
		return (a.get(n));
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}

}
