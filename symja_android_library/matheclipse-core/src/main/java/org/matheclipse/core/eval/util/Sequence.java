package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * Represent Sequences in Take[] or Drop[] functions.
 * 
 *
 */
public class Sequence extends ListSizeSequence {

	
	public Sequence(int start, int end) {
		super(start, end, 1, 1);
	}

	public Sequence(final IAST lst) {
		super(getASTFrom(lst), getASTTo(lst), getASTStep(lst), 1);
	}

	/**
	 * Factory method for creating a sequences from the arguments of an
	 * <code>IAST</code> starting with the argument at position
	 * <code>offset</code>.
	 * 
	 * @param ast
	 * @param offset
	 *            the position in <code>ast</code>, where the first ISequence
	 *            specification starts.
	 * @return
	 */
	public static Sequence[] createSequences(final IAST ast, final int offset) {
		final Sequence[] sequArray = new Sequence[ast.size() - offset];
		Sequence sequ = null;
		int j = 0;
		for (int i = offset; i < ast.size(); i++) {
			if (ast.get(i).isList()) {
				sequ = new Sequence((IAST) ast.get(i));
			} else if (ast.get(i) instanceof IInteger) {
				int num = ((IInteger) ast.get(i)).toInt();
				if (num < 0) {
					sequ = new Sequence(num, Integer.MAX_VALUE);
				} else {
					sequ = new Sequence(1, num);
				}
			} else if (ast.get(i).equals(F.All)) {
				sequ = new Sequence(1, Integer.MAX_VALUE);
			} else if (ast.get(i).equals(F.None)) {
				sequ = new Sequence(1, 0);
			}
			sequArray[j++] = sequ;
		}
		return sequArray;
	}

	private static int getASTFrom(final IAST lst) {
		if ((lst.size() > 1) && !(lst.arg1().isReal())) {
			throw new WrongArgumentType(lst, lst.arg1(), 1);
		}
		if (lst.size() > 1) {
			return ((ISignedNumber) lst.arg1()).toInt();
		}
		return 0;
	}

	private static int getASTTo(final IAST lst) {
		if ((lst.isAST1()) && (lst.arg1().isReal())) {
			return ((ISignedNumber) lst.arg1()).toInt();
		}
		if ((lst.size() > 2) && !(lst.arg2() instanceof IInteger)) {
			throw new WrongArgumentType(lst, lst.arg2(), 2);
		}
		if (lst.size() > 2) {
			return ((IInteger) lst.arg2()).toInt();
		}
		return Integer.MIN_VALUE;
	}

	private static int getASTStep(final IAST lst) {
		if ((lst.size() > 3) && !(lst.arg1().isReal())) {
			throw new WrongArgumentType(lst, lst.arg3(), 3);
		}
		if (lst.size() > 3) {
			return ((ISignedNumber) lst.arg3()).toInt();
		}
		return 1;
	}
}
