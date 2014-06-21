package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class List extends AbstractConverter {

	/** constructor will be called by reflection */
	public List() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST ast, final int precedence) {

		if ((ast.getEvalFlags() & IAST.IS_MATRIX) == IAST.IS_MATRIX) {
			// create a LaTeX matrix
			// \begin{pmatrix} x & y \\ u & v \end{pmatrix}
			buf.append("\\begin{pmatrix} ");
			if (ast.size() > 1) {
				for (int i = 1; i < ast.size(); i++) {
					IAST row = ast.getAST(i);
					for (int j = 1; j < row.size(); j++) {
						fFactory.convert(buf, row.get(j), 0);
						if (j < row.size() - 1) {
							buf.append(" & ");
						}
					}
					if (i < ast.size() - 1) {
						buf.append(" \\\\ ");
					}
				}
			}
			buf.append(" \\end{pmatrix} ");
		} else if ((ast.getEvalFlags() & IAST.IS_VECTOR) == IAST.IS_VECTOR) {
			// create a LaTeX row vector
			// \begin{pmatrix} x & y \end{pmatrix}
			buf.append("\\begin{pmatrix} ");
			if (ast.size() > 1) {
				for (int j = 1; j < ast.size(); j++) {
					fFactory.convert(buf, ast.get(j), 0);
					if (j < ast.size() - 1) {
						buf.append(" & ");
					}
				}
			}
			buf.append(" \\end{pmatrix} ");
		} else {
			buf.append("\\{ ");
			if (ast.size() > 1) {
				fFactory.convert(buf, ast.get(1), 0);
				for (int i = 2; i < ast.size(); i++) {
					buf.append(',');
					fFactory.convert(buf, ast.get(i), 0);
				}
			}
			buf.append("\\} ");
		}
		return true;
	}
}