package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
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
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 2) {
			return false;
		}
		final IAST matrix = matrixQ(f.get(1));
		if (matrix == null) {
			final IAST vector = vectorQ(f.get(1));
			if (vector == null) {
				return false;
			} else {
				buf.append("\\begin{pmatrix}");
				IExpr element;
				for (int i = 1; i < vector.size(); i++) {
					element = vector.get(i);
					buf.append(' ');
					fFactory.convert(buf, element, 0);
					buf.append(' ');
					if (i < vector.size() - 1) {
						buf.append('&');
					}
				}
				buf.append("\\end{pmatrix}");
			}
		} else {
			buf.append("\\begin{pmatrix}");
			IAST row;
			for (int i = 1; i < matrix.size(); i++) {
				row = (IAST) matrix.get(i);
				for (int j = 1; j < row.size(); j++) {
					buf.append(' ');
					fFactory.convert(buf, row.get(j), 0);
					buf.append(' ');
					if (j < row.size() - 1) {
						buf.append('&');
					}
				}
				buf.append("\\\\\n");
			}

			buf.append("\\end{pmatrix}");
		}
		return true;
	}

	public IAST matrixQ(final IExpr expr) {
		if (!(expr instanceof IAST)) {
			return null;
		}
		final IAST list = (IAST) expr;
		if (!expr.isList()) {
			return null;
		}
		final int size = list.size();
		int subSize = -1;
		IExpr temp;
		for (int i = 1; i < size; i++) {
			temp = list.get(i);
			if (!(temp instanceof IAST)) {
				return null;
			}
			final IAST subList = (IAST) temp;
			if (!subList.isList()) {
				return null;
			}
			if (subSize < 0) {
				subSize = subList.size();
			} else if (subSize != subList.size()) {
				return null;
			}
		}
		return list;
	}

	public IAST vectorQ(final IExpr expr) {
		if (!(expr instanceof IAST)) {
			return null;
		}
		final IAST list = (IAST) expr;
		if (!list.isList()) {
			return null;
		}
		final int size = list.size();
		IExpr temp;
		for (int i = 1; i < size; i++) {
			temp = list.get(i);
			if ((temp instanceof IAST) && (((IAST) temp).isList())) {
				return null;
			}
		}
		return list;
	}
}
