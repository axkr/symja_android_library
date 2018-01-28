package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Operator function conversions
 */
public class MatrixForm extends AbstractConverter {
	public MatrixForm() {
	}

	/** {@inheritDoc} */
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
				buf.append("\\begin{pmatrix}");
				IExpr element;
				for (int i = 1; i < vector.size(); i++) {
					element = vector.get(i);
					buf.append(' ');
					fFactory.convert(buf, element, 0);
					buf.append(' ');
					if (i < vector.argSize()) {
						buf.append('&');
					}
				}
				buf.append("\\end{pmatrix}");
			}
		} else {
			final IAST matrix = (IAST) f.arg1();
			buf.append("\\begin{pmatrix}");
			IAST row;
			for (int i = 1; i < matrix.size(); i++) {
				row = (IAST) matrix.get(i);
				for (int j = 1; j < row.size(); j++) {
					buf.append(' ');
					fFactory.convert(buf, row.get(j), 0);
					buf.append(' ');
					if (j < row.argSize()) {
						buf.append('&');
					}
				}
				buf.append("\\\\\n");
			}

			buf.append("\\end{pmatrix}");
		}
		return true;
	}

}
