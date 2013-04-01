package org.matheclipse.core.convert;

import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISignedNumber;

public class Expr2Object {
	public static double[] toDoubleVector(IAST ast) throws WrongArgumentType {
		double[] result = new double[ast.size() - 1];
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isSignedNumber()) {
				result[i - 1] = ((ISignedNumber) ast.get(i)).doubleValue();
			} else {
				throw new WrongArgumentType(ast, ast.get(i), i, "Conversion into a vector of double values not possible!");
			}
		}
		return result;
	}

	public static double[][] toDoubleMatrix(IAST ast) throws WrongArgumentType {
		int[] dim = ast.isMatrix();
		double[][] result = new double[dim[0]][dim[1]];
		for (int i = 1; i <= dim[0]; i++) {
			IAST row = (IAST) ast.get(i);
			for (int j = 1; i <= dim[1]; j++) {
				if (row.get(j).isSignedNumber()) {
					result[i - 1][j - 1] = ((ISignedNumber) row.get(j)).doubleValue();
				} else {
					throw new WrongArgumentType(ast, ast.get(i), i, "Conversion into a matrix of double values not possible!");
				}
			}
		}
		return result;
	}
}
