package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class List extends AbstractConverter {

	/** constructor will be called by reflection */
	public List() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST ast, final int precedence) {
		int[] dims = ast.isMatrix();
		if (dims != null) {
			if (Config.MATHJAX) {
				convertMatrixMathJAX(buf, ast, dims[1]);
			} else {
				convertMatrixTeX(buf, ast, dims[1]);
			}
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
			buf.append("\\{");
			if (ast.size() > 1) {
				fFactory.convert(buf, ast.arg1(), 0);
				for (int i = 2; i < ast.size(); i++) {
					buf.append(',');
					fFactory.convert(buf, ast.get(i), 0);
				}
			}
			buf.append("\\}");
		}
		return true;
	}

	private void convertMatrixTeX(final StringBuffer buf, final IAST ast, int colDim) {
		// create a LaTeX matrix
		buf.append("\\left(\n\\begin{array}{");
		for (int i = 0; i < colDim; i++) {
			buf.append("c");
		}
		buf.append("}\n");
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
					buf.append(" \\\\\n");
				} else {

					buf.append(" \n");
				}
			}
		}
		buf.append("\\end{array}\n\\right) ");
	}

	private void convertMatrixMathJAX(final StringBuffer buf, final IAST ast, int colDim) {
		// create a LaTeX matrix
		buf.append("\\begin{pmatrix} ");
		if (ast.size() > 1) {
			for (int i = 1; i < ast.size(); i++) {
				IAST row = ast.getAST(i);
				for (int j = 1; j < colDim; j++) {
					fFactory.convert(buf, row.get(j), 0);
					if (j < colDim - 1) {
						buf.append(" & ");
					}
				}
				if (i < ast.size() - 1) {
					buf.append(" \\\\ ");
				}
			}
		}
		buf.append(" \\end{pmatrix} ");
	}
}