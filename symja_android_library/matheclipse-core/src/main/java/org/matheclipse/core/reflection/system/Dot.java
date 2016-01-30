package org.matheclipse.core.reflection.system;
 
import org.apache.commons.math4.linear.BlockFieldMatrix;
import org.apache.commons.math4.linear.FieldMatrix;
import org.apache.commons.math4.linear.FieldVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.expression.ExprField;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Dot extends AbstractNonOrderlessArgMultiple {

	public Dot() {
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		FieldMatrix<IExpr> matrix0;
		FieldMatrix<IExpr> matrix1;
		FieldVector<IExpr> vector0;
		FieldVector<IExpr> vector1;
		try {
			IAST list;

			if (o0.isMatrix() != null) {
				list = (IAST) o0;
				matrix0 = Convert.list2Matrix(list);
				if (o1.isMatrix() != null) {
					list = (IAST) o1;
					matrix1 = Convert.list2Matrix(list);
					return Convert.matrix2List(matrix0.multiply(matrix1));
				} else if (o1.isVector() != (-1)) {
					list = (IAST) o1;
					vector1 = Convert.list2Vector(list);
					IAST res = Convert.vector2List(matrix0.operate(vector1));
					if (res==null){
						return F.NIL;
					}
					return res;
				}
			} else if (o0.isVector() != (-1)) {
				list = (IAST) o0;
				vector0 = Convert.list2Vector(list);
				if (o1.isMatrix() != null) {
					list = (IAST) o1;
					matrix1 = Convert.list2Matrix(list);
					IExpr[] av = vector0.toArray();
					BlockFieldMatrix<IExpr> m = new BlockFieldMatrix<IExpr>(ExprField.CONST, 1, av.length);
					m.setRow(0, vector0.toArray());
					return Convert.matrix2List(m.multiply(matrix1));
				} else if (o1.isVector() != (-1)) {
					list = (IAST) o1;
					vector1 = Convert.list2Vector(list);
					return vector0.dotProduct(vector1);
				}
			}

		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		return evaluate(ast, engine);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
	}

}