package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Sequence extends ListSizeSequence {

	public Sequence(final IInteger i) {
		super(i.toInt(), Integer.MIN_VALUE, 1, 1);
	}

	public Sequence(final IAST lst) {
		super(getASTFrom(lst), getASTTo(lst), getASTStep(lst), 1);
	}

	/**
	 * Factory method for creating a Sequence from a given expression
	 *
	 * @param expr
	 *          an expression, which should represent a sequence specification
	 * @return returns <code>null</code> if the given expression is no sequence
	 *         specification
	 */
	public static Sequence createSequence(final IExpr expr) {
		Sequence sequ = null;
		if (expr.isList()) {
			sequ = new Sequence((IAST) expr);
		} else if (expr instanceof IInteger) {
			sequ = new Sequence((IInteger) expr);
		}
		return sequ;
	}

	public static Sequence[] createSequences(final IAST ast, final int offset) {
		final Sequence[] sequArray = new Sequence[ast.size() - offset];
		Sequence sequ = null;
		int j = 0;
		for (int i = offset; i < ast.size(); i++) {
			if (ast.get(i).isList()) {
				sequ = new Sequence((IAST) ast.get(i));
			} else if (ast.get(i) instanceof IInteger) {
				sequ = new Sequence((IInteger) ast.get(i));
			}
			sequArray[j++] = sequ;
		}
		return sequArray;
	}

	private static int getASTFrom(final IAST lst) {
		if ((lst.size() > 1) && !(lst.arg1() instanceof IInteger)) {
			throw new WrongArgumentType(lst, lst.arg1(), 1);
		}
		if (lst.size() > 1) {
			// TODO check correct ISignedNumber#toInt() usage
			return ((IInteger) lst.arg1()).toInt();
		}
		return 0;
	}

	private static int getASTTo(final IAST lst) {
		if ((lst.size() == 2) && (lst.arg1() instanceof IInteger)) {
			// TODO check correct ISignedNumber#toInt() usage
			return ((IInteger) lst.arg1()).toInt();
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
		if ((lst.size() > 3) && !(lst.arg3().isInteger())) {
			throw new WrongArgumentType(lst, lst.arg3(), 3);
		}
		if (lst.size() > 3) {
			return ((IInteger) lst.arg3()).toInt();
		}
		return 1;
	}
}
