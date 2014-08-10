package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
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
	 *            StringBuffer for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
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
				fFactory.tagStart(buf, "mrow");
				fFactory.tag(buf, "mo", "(");
				fFactory.tagStart(buf, "mtable");

				IExpr temp;
				for (int i = 1; i < vector.size(); i++) {

					temp = vector.get(i);
					fFactory.tagStart(buf, "mtr");
					fFactory.tagStart(buf, "mtd");
					fFactory.convert(buf, temp, 0);
					fFactory.tagEnd(buf, "mtd");
					fFactory.tagEnd(buf, "mtr");
				}

				fFactory.tagEnd(buf, "mtable");
				fFactory.tag(buf, "mo", ")");
				fFactory.tagEnd(buf, "mrow");
			}
		} else {
			fFactory.tagStart(buf, "mrow");
			fFactory.tag(buf, "mo", "(");
			fFactory.tagStart(buf, "mtable");

			IAST temp;
			for (int i = 1; i < matrix.size(); i++) {

				temp = (IAST) matrix.get(i);
				fFactory.tagStart(buf, "mtr");
				for (int j = 1; j < temp.size(); j++) {

					fFactory.tagStart(buf, "mtd");
					fFactory.convert(buf, temp.get(j), 0);
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

	public IAST matrixQ(final IExpr expr) {
		if (!(expr instanceof IAST)) {
			return null;
		}
		final IAST list = (IAST) expr;
		if (!list.isList()) {  
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
