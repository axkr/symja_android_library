package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Operator function conversions
 */
public class MatrixForm extends AbstractConverter {
	public MatrixForm() {
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *            StringBuilder for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		int[] dims = f.arg1().isMatrix();
		if (dims == null) {
			int dim = f.arg1().isVector();
			if (dim < 0) {
				return false;
			} else {
				final IAST vector = (IAST) f.arg1();
				fFactory.tagStart(buf, "mrow");
				fFactory.tag(buf, "mo", "(");
				fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

				IExpr temp;
				for (int i = 1; i < vector.size(); i++) {

					temp = vector.get(i);
					fFactory.tagStart(buf, "mtr");
					fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
					fFactory.convert(buf, temp, Integer.MIN_VALUE, false);
					fFactory.tagEnd(buf, "mtd");
					fFactory.tagEnd(buf, "mtr");
				}

				fFactory.tagEnd(buf, "mtable");
				fFactory.tag(buf, "mo", ")");
				fFactory.tagEnd(buf, "mrow");
			}
		} else {
			final IAST matrix = (IAST) f.arg1();
			fFactory.tagStart(buf, "mrow");
			fFactory.tag(buf, "mo", "(");
			fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

			IAST temp;
			for (int i = 1; i < matrix.size(); i++) {

				temp = (IAST) matrix.get(i);
				fFactory.tagStart(buf, "mtr");
				for (int j = 1; j < temp.size(); j++) {

					fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
					fFactory.convert(buf, temp.get(j), Integer.MIN_VALUE, false);
					fFactory.tagEnd(buf, "mtd");
				}
				fFactory.tagEnd(buf, "mtr");
			}

			fFactory.tagEnd(buf, "mtable");
			fFactory.tag(buf, "mo", ")");
			fFactory.tagEnd(buf, "mrow");
		}
		return true;
	}

}
