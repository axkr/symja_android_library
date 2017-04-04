package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Dot;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MapThread;
import static org.matheclipse.core.expression.F.Most;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Norm;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Prepend;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.g;
import static org.matheclipse.core.expression.F.r;
import static org.matheclipse.core.expression.F.y;

import java.util.ArrayList;
import java.util.List;

import org.hipparchus.linear.BlockFieldMatrix;
import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.FieldDecompositionSolver;
import org.hipparchus.linear.FieldLUDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.commons.math.linear.FieldReducedRowEchelonForm;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.NonNegativeIntegerExpected;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Matrix;
import org.matheclipse.core.eval.util.IIndexFunction;
import org.matheclipse.core.eval.util.IndexFunctionDiagonal;
import org.matheclipse.core.eval.util.IndexTableGenerator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ExprField;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

public final class LinearAlgebra {
	
	static {
		F.ArrayDepth.setEvaluator(new ArrayDepth());
		F.BrayCurtisDistance.setEvaluator(new BrayCurtisDistance());
		F.CanberraDistance.setEvaluator(new CanberraDistance());
		F.CharacteristicPolynomial.setEvaluator(new CharacteristicPolynomial());
		F.ChessboardDistance.setEvaluator(new ChessboardDistance());
		F.ConjugateTranspose.setEvaluator(new ConjugateTranspose());
		F.CosineDistance.setEvaluator(new CosineDistance());
		F.Cross.setEvaluator(new Cross());
		F.DesignMatrix.setEvaluator(new DesignMatrix());
		F.Det.setEvaluator(new Det());
		F.DiagonalMatrix.setEvaluator(new DiagonalMatrix());
		F.Dimensions.setEvaluator(new Dimensions());
		F.Eigenvalues.setEvaluator(new Eigenvalues());
		F.Eigenvectors.setEvaluator(new Eigenvectors());
		F.EuclideanDistance.setEvaluator(new EuclideanDistance());
		F.HilbertMatrix.setEvaluator(new HilbertMatrix());
		F.IdentityMatrix.setEvaluator(new IdentityMatrix());
		F.Inner.setEvaluator(new Inner());
		F.Inverse.setEvaluator(new Inverse());
		F.JacobiMatrix.setEvaluator(new JacobiMatrix());
		F.LinearSolve.setEvaluator(new LinearSolve());
		F.LUDecomposition.setEvaluator(new LUDecomposition());
		F.ManhattanDistance.setEvaluator(new ManhattanDistance());
		F.MatrixMinimalPolynomial.setEvaluator(new MatrixMinimalPolynomial());
		F.MatrixPower.setEvaluator(new MatrixPower());
		F.MatrixRank.setEvaluator(new MatrixRank());
		F.Norm.setEvaluator(new Norm());
		F.Normalize.setEvaluator(new Normalize());
		F.NullSpace.setEvaluator(new NullSpace());
		F.PseudoInverse.setEvaluator(new PseudoInverse());
		F.QRDecomposition.setEvaluator(new QRDecomposition());
		F.RowReduce.setEvaluator(new RowReduce());
		F.SingularValueDecomposition.setEvaluator(new SingularValueDecomposition());
		F.SquaredEuclideanDistance.setEvaluator(new SquaredEuclideanDistance());
		F.Tr.setEvaluator(new Tr());
		F.Transpose.setEvaluator(new Transpose());
		F.UnitVector.setEvaluator(new UnitVector());
		F.VandermondeMatrix.setEvaluator(new VandermondeMatrix());
		F.VectorAngle.setEvaluator(new VectorAngle());
	}

	private final static class ArrayDepth extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				IExpr header = list.head();
				ArrayList<Integer> dims = LinearAlgebra.getDimensions(list, header, Integer.MAX_VALUE);
				return F.integer(dims.size());
			}

			return F.C0;

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * BrayCurtisDistance of two vectors
	 */
	private final static class BrayCurtisDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() != 3) {
				throw new WrongNumberOfArguments(ast, 2, ast.size() - 1);
			}
			IExpr u = ast.arg1();
			IExpr v = ast.arg2();

			int dim1 = u.isVector();
			if (dim1 > (-1)) {
				int dim2 = v.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}

					return F.Total(F.Divide(F.Abs(F.Subtract(u, v)), F.Total(F.Abs(F.Plus(u, v)))));

				}
			}
			return F.NIL;
		}

	}

	/**
	 * CanberraDistance of two vectors
	 */
	private final static class CanberraDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 3) {
				throw new WrongNumberOfArguments(functionList, 2, functionList.size() - 1);
			}
			IExpr arg1 = functionList.arg1();
			IExpr arg2 = functionList.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}

					return F.Total(F.Divide(F.Abs(F.Subtract(arg1, arg2)), F.Plus(F.Abs(arg1), F.Abs(arg2))));

				}
			}
			return F.NIL;
		}

	}

	/**
	 * Compute the characteristic polynomial of a square matrix.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Characteristic_polynomial">
	 * Wikipedia - Characteristic polynomial</a>
	 */
	private final static class CharacteristicPolynomial extends AbstractFunctionEvaluator {

		public CharacteristicPolynomial() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			int[] dimensions = ast.arg1().isMatrix();
			if (dimensions != null && dimensions[0] == dimensions[1]) {
				// a matrix with square dimensions
				IAST matrix = (IAST) ast.arg1();
				IExpr variable = ast.arg2();
				return generateCharacteristicPolynomial(dimensions[0], matrix, variable);
			}

			return F.NIL;
		}

		/**
		 * Generate the characteristic polynomial of a square matrix.
		 * 
		 * @param dim
		 *            dimension of the square matrix
		 * @param matrix
		 *            the square matrix
		 * @param variable
		 *            the variable which should be used in the resulting
		 *            characteristic polynomial
		 * @return
		 */
		public static IAST generateCharacteristicPolynomial(int dim, IAST matrix, IExpr variable) {
			final IExpr[] valuesForIdentityMatrix = { F.C0, variable };
			return F.Det(F.Subtract(matrix, diagonalMatrix(valuesForIdentityMatrix, dim)));
		}

	}

	/**
	 * ChessboardDistance of two vectors
	 */
	private final static class ChessboardDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 3) {
				throw new WrongNumberOfArguments(functionList, 2, functionList.size() - 1);
			}
			IExpr arg1 = functionList.arg1();
			IExpr arg2 = functionList.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}
					IAST a1 = ((IAST) arg1);
					IAST a2 = ((IAST) arg2);
					IAST maxAST = F.Max();
					for (int i = 1; i < a1.size(); i++) {
						maxAST.append(F.Abs(F.Subtract(a1.get(i), a2.get(i))));
					}
					return maxAST;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Conjugate and transpose a matrix.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia:
	 * Complex conjugation</a> and
	 * <a href="http://en.wikipedia.org/wiki/Transpose">Transpose</a>
	 */
	private final static class ConjugateTranspose extends Transpose {

		public ConjugateTranspose() {

		}

		@Override
		protected IExpr transform(final IExpr expr) {
			return expr.conjugate();
		}

	}

	/**
	 * CosineDistance of two vectors
	 */
	private final static class CosineDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 3) {
				throw new WrongNumberOfArguments(functionList, 2, functionList.size() - 1);
			}
			IExpr arg1 = functionList.arg1();
			IExpr arg2 = functionList.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}

					return F.Subtract(F.C1, F.Divide(F.Dot(arg1, arg2), F.Times(F.Norm(arg1), F.Norm(arg2))));

				}
			}
			return F.NIL;
		}

	}

	/**
	 * Calculate the cross product of 2 vectors with dimension 3.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Cross_product">Wikipedia:Cross
	 * product</a>
	 */
	private final static class Cross extends AbstractFunctionEvaluator {

		public Cross() {
		}

		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);
			IExpr arg1 = ast.arg1();
			if (ast.isAST2()) {
				IExpr arg2 = ast.arg2();
				int dim1 = arg1.isVector();
				int dim2 = arg2.isVector();
				if (dim1 == 3 && dim2 == 3) {
					final IAST v1 = (IAST) arg1;
					final IAST v2 = (IAST) arg2;
					if ((v1.isAST3()) || (v2.isAST3())) {
						return List(Plus(Times(v1.arg2(), v2.arg3()), Times(CN1, v1.arg3(), v2.arg2())),
								Plus(Times(v1.arg3(), v2.arg1()), Times(CN1, v1.arg1(), v2.arg3())),
								Plus(Times(v1.arg1(), v2.arg2()), Times(CN1, v1.arg2(), v2.arg1())));
					}
				}
			} else if (ast.isAST1()) {
				int dim1 = arg1.isVector();
				if (dim1 == 2) {
					final IAST v1 = (IAST) arg1;
					return List(Negate(v1.arg2()), v1.arg1());
				}
			}
			return F.NIL;
		}

	}

	private static class DesignMatrix extends AbstractEvaluator {

		public DesignMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);
			IExpr m = ast.arg1();
			IExpr f = ast.arg2();
			IExpr x = ast.arg3();
			if (f.isList()) {
				if (x.isAtom()) {
					// DesignMatrix[m_, f_List, x_?AtomQ] :=
					// DesignMatrix[m, {f}, ConstantArray[x, Length[f]]]
					return F.DesignMatrix(m, F.List(f), F.ConstantArray(x, F.Length(f)));
				} else if (x.isList()) {
					// DesignMatrix[m_, f_List, x_List] :=
					// Prepend[MapThread[Function[{g, y, r}, g /. y -> r], {f,
					// x, Most[#]}], 1]& /@ m
					return Map(Function(Prepend(
							MapThread(Function(List(g, y, r), ReplaceAll(g, Rule(y, r))), List(f, x, Most(Slot1))),
							C1)), m);
				}
			} else {
				if (x.isAtom()) {
					// DesignMatrix[m_, f_, x_?AtomQ]': 'DesignMatrix[m, {f},
					// {x}]
					return F.DesignMatrix(m, F.List(f), F.List(x));
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Compute the determinant of a matrix
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Determinant">Determinant</a>
	 * 
	 */
	private static class Det extends AbstractMatrix1Expr {

		public Det() {
			super();
		}

		@Override
		public IExpr matrixEval(final FieldMatrix<IExpr> matrix) {
			if (matrix.getRowDimension() == 2 && matrix.getColumnDimension() == 2) {
				return determinant2x2(matrix);
			}
			if (matrix.getRowDimension() == 3 && matrix.getColumnDimension() == 3) {
				return determinant3x3(matrix);
			}
			final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
			return F.evalExpand(lu.getDeterminant());
		}

		@Override
		public IExpr realMatrixEval(RealMatrix matrix) {
			final org.hipparchus.linear.LUDecomposition lu = new org.hipparchus.linear.LUDecomposition(matrix);
			return F.num(lu.getDeterminant());
		}
	}

	/**
	 * Create a diagonal matrix from a list
	 */
	private static class DiagonalMatrix extends AbstractFunctionEvaluator {

		public DiagonalMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				IAST list = (IAST) ast.arg1();
				int m = list.size();
				IAST res = F.List();
				int offset = 0;
				if ((ast.isAST2())) {
					offset = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
				}
				for (int i = 1; i < m; i++) {
					IAST row = F.List();
					for (int j = 1; j < m; j++) {
						if (i + offset == j) {
							row.append(list.get(i));
						} else {
							row.append(F.C0);
						}
					}

					res.append(row);
				}

				return res;

			}

			return F.NIL;

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Get the dimensions of an expression
	 */
	private static class Dimensions extends AbstractFunctionEvaluator {

		public Dimensions() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			int n = Integer.MAX_VALUE;
			if (ast.isAST2() && ast.arg2().isInteger()) {
				n = Validate.checkIntType(ast, 2);
			}
			if (ast.arg1().isAST()) {
				IAST res = F.List();
				if (n > 0) {
					IAST list = (IAST) ast.arg1();
					IExpr header = list.head();
					ArrayList<Integer> dims = getDimensions(list, header, n - 1);
					for (int i = 0; i < dims.size(); i++) {
						res.append(F.integer(dims.get(i).intValue()));
					}
				}
				return res;
			}

			return F.List();

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Compute the numerical Eigenvalues of a real symmetric matrix
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace" >
	 * Wikipedia - Eigenvalue, eigenvector and eigenspace</a>
	 */
	private static class Eigenvalues extends AbstractMatrix1Expr {

		public Eigenvalues() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			FieldMatrix<IExpr> matrix;
			try {

				int[] dim = ast.arg1().isMatrix();
				if (dim != null) {
					if (dim[0] == 1 && dim[1] == 1) {
						// Eigenvalues({{a}})
						return List(ast.arg1().getAt(1).getAt(1));
					}
					if (dim[0] == 2 && dim[1] == 2) {
						matrix = Convert.list2Matrix((IAST) ast.arg1());
						if (matrix != null) {
							// Eigenvalues({{a, b}, {c, d}}) =>
							// {
							// 1/2 (a + d - Sqrt[a^2 + 4 b c - 2 a d + d^2]),
							// 1/2 (a + d + Sqrt[a^2 + 4 b c - 2 a d + d^2])
							// }
							IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
									Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
									Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)),
									Sqr(matrix.getEntry(1, 1))));
							return List(
									Times(C1D2, Plus(Negate(sqrtExpr), matrix.getEntry(0, 0), matrix.getEntry(1, 1))),
									Times(C1D2, Plus(sqrtExpr, matrix.getEntry(0, 0), matrix.getEntry(1, 1))));
						}
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

			// switch to numeric calculation
			return numericEval(ast, engine);
		}

		@Override
		public IAST realMatrixEval(RealMatrix matrix) {
			try {
				IAST list = F.List();
				EigenDecomposition ed = new EigenDecomposition(matrix);
				double[] realValues = ed.getRealEigenvalues();
				double[] imagValues = ed.getImagEigenvalues();
				for (int i = 0; i < realValues.length; i++) {
					if (F.isZero(imagValues[i])) {
						list.append(F.num(realValues[i]));
					} else {
						list.append(F.complexNum(realValues[i], imagValues[i]));
					}
				}
				return list;
			} catch (Exception ime) {
				throw new WrappedException(ime);
			}
		}

		@Override
		public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
			return F.NIL;
		}
	}

	/**
	 * Compute the numerical Eigenvectors of a real symmetric matrix
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Eigenvalue,_eigenvector_and_eigenspace" >
	 * Eigenvalue, eigenvector and eigenspace</a>
	 */
	private static class Eigenvectors extends AbstractMatrix1Expr {

		public Eigenvectors() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			FieldMatrix<IExpr> matrix;
			try {

				int[] dim = ast.arg1().isMatrix();
				if (dim != null) {
					if (dim[0] == 1 && dim[1] == 1) {
						// Eigenvectors({{a}})
						return C1;
					}
					if (dim[0] == 2 && dim[1] == 2) {
						matrix = Convert.list2Matrix((IAST) ast.arg1());
						if (matrix != null) {
							if (matrix.getEntry(1, 0).isZero()) {
								if (matrix.getEntry(0, 0).equals(matrix.getEntry(1, 1))) {
									// Eigenvectors({{a, b}, {0, a}})
									return List(List(C1, C0), List(C0, C0));
								} else {
									// Eigenvectors({{a, b}, {0, d}})
									return List(List(C1, C0), List(Divide(Negate(matrix.getEntry(0, 1)),
											Subtract(matrix.getEntry(0, 0), matrix.getEntry(1, 1))), C1));
								}
							} else {
								// Eigenvectors({{a, b}, {c, d}}) =>
								// {
								// { - (1/(2*c)) * (-a + d + Sqrt[a^2 + 4 b c -
								// 2 a
								// d +
								// d^2]), 1},
								// { - (1/(2*c)) * (-a + d - Sqrt[a^2 + 4 b c -
								// 2 a
								// d +
								// d^2]), 1}
								// }
								IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
										Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
										Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)),
										Sqr(matrix.getEntry(1, 1))));
								return List(List(
										Times(CN1D2, Power(matrix.getEntry(1, 0), CN1),
												Plus(sqrtExpr, Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))),
										C1), List(
												Times(CN1D2,
														Power(matrix.getEntry(1, 0), CN1), Plus(Negate(sqrtExpr),
																Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))),
												C1));
							}
						}
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

			// switch to numeric calculation
			return numericEval(ast, engine);
		}

		@Override
		public IAST realMatrixEval(RealMatrix matrix) {
			try {
				IAST list = F.List();
				EigenDecomposition ed = new EigenDecomposition(matrix);
				for (int i = 0; i < matrix.getColumnDimension(); i++) {
					RealVector rv = ed.getEigenvector(i);
					list.append(Convert.realVector2List(rv));
				}
				return list;
			} catch (Exception ime) {
				throw new WrappedException(ime);
			}
		}

		@Override
		public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
			return F.NIL;
		}
	}

	/**
	 * EuclidianDistance of two vectors
	 */
	private final static class EuclideanDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}
					IAST a1 = ((IAST) arg1);
					IAST a2 = ((IAST) arg2);
					IAST plusAST = F.Plus();
					for (int i = 1; i < a1.size(); i++) {
						plusAST.append(F.Sqr(F.Abs(F.Subtract(a1.get(i), a2.get(i)))));
					}
					return F.Sqrt(plusAST);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {

		}

	}

	/**
	 * Hilbert matrix, defined by A<sub>i,j</sub> = 1 / (i+j-1). See <a>
	 * href="http://en.wikipedia.org/wiki/Hilbert_matrix">Wikipedia:Hilbert
	 * matrix</a>
	 */
	private static class HilbertMatrix extends AbstractFunctionEvaluator {

		private static class HilbertFunctionDiagonal implements IIndexFunction<IExpr> {

			public HilbertFunctionDiagonal() {
			}

			@Override
			public IRational evaluate(final int[] index) {
				return F.fraction(1L, 1L + index[0] + index[1]);
			}
		}

		public HilbertMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			int rowSize = 0;
			int columnSize = 0;
			if (ast.isAST1() && ast.arg1().isInteger()) {
				rowSize = Validate.checkIntType(ast, 1);
				columnSize = rowSize;
			} else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {
				rowSize = Validate.checkIntType(ast, 1);
				columnSize = Validate.checkIntType(ast, 2);
			} else {
				return F.NIL;
			}

			final IAST resultList = F.List();
			final int[] indexArray = new int[2];
			indexArray[0] = rowSize;
			indexArray[1] = columnSize;
			final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList,
					new HilbertFunctionDiagonal());
			final IAST matrix = (IAST) generator.table();
			matrix.addEvalFlags(IAST.IS_MATRIX);
			return matrix;
		}
	}

	/**
	 * Create an identity matrix
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Identity_matrix">Wikipedia -
	 * Identity matrix</a>
	 */
	private static class IdentityMatrix extends AbstractFunctionEvaluator {

		public IdentityMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isInteger()) {
				int indx = Validate.checkIntType(ast, 1);
				final IExpr[] valueArray = { F.C0, F.C1 };
				return diagonalMatrix(valueArray, indx);
			}
			return F.NIL;
		}

	}

	private static class Inner extends AbstractFunctionEvaluator {

		private static class InnerAlgorithm {
			final IExpr f;
			final IExpr g;
			final IExpr head;
			final IAST list1;
			final IAST list2;
			int list2Dim0;

			private InnerAlgorithm(final IExpr f, final IAST list1, final IAST list2, final IExpr g) {
				this.f = f;
				this.list1 = list1;
				this.list2 = list2;
				this.g = g;
				this.head = list2.head();
			}

			private IAST inner() {
				ArrayList<Integer> list1Dimensions = getDimensions(list1, list1.head(), Integer.MAX_VALUE);
				ArrayList<Integer> list2Dimensions = getDimensions(list2, list2.head(), Integer.MAX_VALUE);
				list2Dim0 = list2Dimensions.get(0);
				return recursion(new ArrayList<Integer>(), new ArrayList<Integer>(),
						list1Dimensions.subList(0, list1Dimensions.size() - 1),
						list2Dimensions.subList(1, list2Dimensions.size()));
			}

			@SuppressWarnings("unchecked")
			private IAST recursion(ArrayList<Integer> list1Cur, ArrayList<Integer> list2Cur,
					List<Integer> list1RestDimensions, List<Integer> list2RestDimensions) {
				if (list1RestDimensions.size() > 0) {
					IAST newResult = F.ast(head);
					for (int i = 1; i < list1RestDimensions.get(0) + 1; i++) {
						ArrayList<Integer> list1CurClone = (ArrayList<Integer>) list1Cur.clone();
						list1CurClone.add(i);
						newResult.append(recursion(list1CurClone, list2Cur,
								list1RestDimensions.subList(1, list1RestDimensions.size()), list2RestDimensions));
					}
					return newResult;
				} else if (list2RestDimensions.size() > 0) {
					IAST newResult = F.ast(head);
					for (int i = 1; i < list2RestDimensions.get(0) + 1; i++) {
						ArrayList<Integer> list2CurClone = (ArrayList<Integer>) list2Cur.clone();
						list2CurClone.add(i);
						newResult.append(recursion(list1Cur, list2CurClone, list1RestDimensions,
								list2RestDimensions.subList(1, list2RestDimensions.size())));
					}
					return newResult;
				} else {
					IAST part = F.ast(g);
					for (int i = 1; i < list2Dim0 + 1; i++) {
						part.append(summand(list1Cur, list2Cur, i));
					}
					return part;
				}
			}

			@SuppressWarnings("unchecked")
			private IAST summand(ArrayList<Integer> list1Cur, ArrayList<Integer> list2Cur, final int i) {
				IAST result = F.ast(f);
				ArrayList<Integer> list1CurClone = (ArrayList<Integer>) list1Cur.clone();
				list1CurClone.add(i);
				result.append(list1.getPart(list1CurClone));
				ArrayList<Integer> list2CurClone = (ArrayList<Integer>) list2Cur.clone();
				list2CurClone.add(0, i);
				result.append(list2.getPart(list2CurClone));
				return result;
			}
		}

		public Inner() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 4, 5);

			if (ast.arg2().isAST() && ast.arg3().isAST()) {
				IExpr f = ast.arg1();
				IAST list1 = (IAST) ast.arg2();
				IAST list2 = (IAST) ast.arg3();
				IExpr g;
				if (ast.isAST3()) {
					g = F.Plus;
				} else {
					g = ast.arg4();
				}
				IExpr head2 = list2.head();
				if (!list1.head().equals(head2)) {
					return F.NIL;
				}
				InnerAlgorithm ic = new InnerAlgorithm(f, list1, list2, g);
				return ic.inner();
			}
			return F.NIL;
		}
	}

	/**
	 * Invert a matrix
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Invertible_matrix">Invertible
	 * matrix</a>
	 */
	private final static class Inverse extends AbstractMatrix1Matrix {

		@Override
		public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix) {
			return inverseMatrix(matrix);
		}

		public static FieldMatrix<IExpr> inverseMatrix(FieldMatrix<IExpr> matrix) {
			final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
			FieldDecompositionSolver<IExpr> solver = lu.getSolver();
			if (!solver.isNonSingular()) {
				EvalEngine.get().printMessage("Inverse: the matrix is singular.");
				return null;
			}
			return solver.getInverse();
		}

		@Override
		public RealMatrix realMatrixEval(RealMatrix matrix) {
			final org.hipparchus.linear.LUDecomposition lu = new org.hipparchus.linear.LUDecomposition(matrix);
			DecompositionSolver solver = lu.getSolver();
			if (!solver.isNonSingular()) {
				EvalEngine.get().printMessage("Inverse: the matrix is singular.");
				return null;
			}
			return solver.getInverse();
		}
	}

	/**
	 * Create a Jacobian matrix.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Jacobian">Jacobian</a>
	 * 
	 */
	private static class JacobiMatrix extends AbstractFunctionEvaluator {
		public JacobiMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isVector() >= 0) {
				IAST variables = F.NIL;
				if (ast.arg2().isSymbol()) {
					variables = F.List();
				} else if (ast.arg2().isVector() >= 0) {
					variables = (IAST) ast.arg2();
				}
				if (variables.isPresent()) {
					IAST vector = (IAST) ast.arg1();
					IAST jacobiMatrix = F.List();
					IAST jacobiRow = null;
					for (int i = 1; i < vector.size(); i++) {
						jacobiRow = F.List();
						for (int j = 1; j < variables.size(); j++) {
							jacobiRow.append(F.D(vector.get(i), variables.get(j)));
						}
						jacobiMatrix.append(jacobiRow);
					}
					return jacobiMatrix;
				}
			}

			return F.NIL;
		}

	}

	/**
	 * Determine <code>x</code> for Matrix <code>A</code> in the equation
	 * <code>A.x==b</code>
	 * 
	 */
	private static class LinearSolve extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isMatrix() != null && ast.arg2().isVector() >= 0) {
				try {
					FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix((IAST) ast.arg1(), (IAST) ast.arg2());
					return LinearAlgebra.rowReduced2List(augmentedMatrix, engine);
				} catch (final ClassCastException e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				} catch (final IndexOutOfBoundsException e) {
					if (Config.SHOW_STACKTRACE) {
						e.printStackTrace();
					}
				}
			}

			return F.NIL;
		}

	}

	private static class LUDecomposition extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			FieldMatrix<IExpr> matrix;
			try {
				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix);
				final FieldMatrix<IExpr> lMatrix = lu.getL();
				final FieldMatrix<IExpr> uMatrix = lu.getU();
				final int[] iArr = lu.getPivot();
				// final int permutationCount = lu.getPermutationCount();
				final IAST iList = List();
				for (int i = 0; i < iArr.length; i++) {
					// +1 because in MathEclipse the offset is +1 compared to
					// java arrays
					iList.append(F.integer(iArr[i] + 1));
				}
				final IAST result = List();
				final IAST lMatrixAST = Convert.matrix2List(lMatrix);
				final IAST uMatrixAST = Convert.matrix2List(uMatrix);
				result.append(lMatrixAST);
				result.append(uMatrixAST);
				result.append(iList);
				return result;

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

	}

	/**
	 * The Manhattan distance of two vectors.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab
	 * geometry</a>
	 */
	private final static class ManhattanDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() != 3) {
				throw new WrongNumberOfArguments(ast, 2, ast.size() - 1);
			}
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}
					IAST a1 = ((IAST) arg1);
					IAST a2 = ((IAST) arg2);
					IAST plusAST = F.Plus();
					for (int i = 1; i < a1.size(); i++) {
						plusAST.append(F.Abs(F.Subtract(a1.get(i), a2.get(i))));
					}
					return plusAST;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {

		}

	}

	/**
	 * Compute the minimal polynomial of a matrix.
	 * 
	 * See <a href=
	 * "https://en.wikipedia.org/wiki/Minimal_polynomial_(linear_algebra)">
	 * Wikipedia - Minimal polynomial (linear algebra)</a>
	 */
	private static class MatrixMinimalPolynomial extends AbstractFunctionEvaluator {

		public MatrixMinimalPolynomial() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			int[] dimensions = ast.arg1().isMatrix();
			if (dimensions != null && dimensions[0] == dimensions[1]) {
				// a matrix with square dimensions
				IAST matrix = (IAST) ast.arg1();
				IExpr variable = ast.arg2();
				ISymbol i = new Symbol("§i");
				int n = 1;
				IAST qu = F.List();
				IAST mnm = (IAST) engine
						.evaluate(F.List(F.Flatten(diagonalMatrix(new IExpr[] { F.C0, F.C1 }, dimensions[0]))));
				while (qu.size() == 1) {
					mnm.append(engine.evaluate(F.Flatten(F.MatrixPower(matrix, F.integer(n)))));
					qu = (IAST) engine.evaluate(F.NullSpace(F.Transpose(mnm)));
					n++;
				}
				return engine
						.evaluate(F.Dot(qu.arg1(), F.Table(F.Power(variable, i), F.List(i, F.C0, F.integer(--n)))));
			}

			return F.NIL;
		}

	}

	private final static class MatrixPower extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			FieldMatrix<IExpr> matrix;
			FieldMatrix<IExpr> resultMatrix;
			try {
				matrix = Convert.list2Matrix((IAST) ast.arg1());
				int p = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
				// if (p < 0) {
				// return F.NIL;
				// }
				if (p == 1) {
					((IAST) ast.arg1()).addEvalFlags(IAST.IS_MATRIX);
					return ast.arg1();
				}
				if (p == 0) {
					resultMatrix = new BlockFieldMatrix<IExpr>(ExprField.CONST, matrix.getRowDimension(),
							matrix.getColumnDimension());
					int min = matrix.getRowDimension();
					if (min > matrix.getColumnDimension()) {
						min = matrix.getColumnDimension();
					}
					for (int i = 0; i < min; i++) {
						resultMatrix.setEntry(i, i, F.C1);
					}

					return Convert.matrix2List(resultMatrix);
				}
				if (p < 0) {
					resultMatrix = Inverse.inverseMatrix(matrix);
					matrix = resultMatrix;
					p *= (-1);
				} else {
					resultMatrix = matrix;
				}
				for (int i = 1; i < p; i++) {
					resultMatrix = resultMatrix.multiply(matrix);
				}
				return Convert.matrix2List(resultMatrix);

			} catch (final ClassCastException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (final ArithmeticException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
				throw new NonNegativeIntegerExpected(ast, 2);
			} catch (final IndexOutOfBoundsException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return F.NIL;
		}
	}

	/**
	 * Compute the rank of a matrix.
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Rank_%28linear_algebra%29">Wikipedia - Rank
	 * (linear algebra)</a>.
	 */
	private final static class MatrixRank extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			FieldMatrix<IExpr> matrix;
			try {
				Validate.checkSize(ast, 2);

				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isMatrix() != null) {
					final IAST astMatrix = (IAST) arg1;
					matrix = Convert.list2Matrix(astMatrix);
					FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
					return F.integer(fmw.getMatrixRank());
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
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Norm of a given argument
	 */
	private final static class Norm extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr arg1 = ast.arg1();
			int dim = arg1.isVector();
			if (dim > (-1)) {
				if (dim == 0) {
					return F.NIL;
				}
				if (ast.isAST2()) {
					IExpr arg2 = ast.arg2();
					if (arg2.isInfinity()) {
						return ((IAST) arg1).map(F.Max, Functors.replaceAll(F.Abs(F.Null), F.Null));
					} else {
						if (arg2.isSymbol() || arg2.isSignedNumber()) {
							if (arg2.isZero()) {
								// throw new WrongArgumentType(ast, ast.get(2),
								// 2, "0 not allowed as second argument!");
								engine.printMessage("Norm: 0 not allowed as second argument!");
								return F.NIL;
							}
							if (arg2.isSignedNumber() && arg2.lessThan(F.C1).isTrue()) {
								// throw new WrongArgumentType(ast, ast.get(2),
								// 2, "Second argument is < 1!");
								engine.printMessage("Norm: Second argument is < 1!");
								return F.NIL;
							}
							return F.Power(((IAST) arg1).map(F.Plus,
									Functors.replaceAll(F.Power(F.Abs(F.Null), arg2), F.Null)), arg2.inverse());
						}
					}
					return F.NIL;
				}
				return F.Sqrt(((IAST) arg1).map(F.Plus, Functors.replaceAll(F.Sqr(F.Abs(F.Null)), F.Null)));
			}
			if (arg1.isNumber()) {
				if (ast.isAST2()) {
					return F.NIL;
				}
				// absolute Value of a number
				return ((INumber) arg1).abs();
			}
			if (arg1.isNumericFunction()) {
				if (ast.isAST2()) {
					return F.NIL;
				}
				// absolute Value
				return F.Abs(arg1);
			}
			return F.NIL;
		}

	}

	/**
	 * <code>Normalize[vector]</code> calculates the normalized
	 * <code>vector</code>.
	 */
	private final static class Normalize extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr normFunction = F.Norm;
			if (ast.isAST2()) {
				normFunction = ast.arg2();
			}
			IExpr arg1 = ast.arg1();
			if (arg1.isAST(F.List, 1) && ast.isAST1()) {
				return arg1;
			}
			IExpr norm = engine.evaluate(F.unaryAST1(normFunction, ast.arg1()));
			if (norm.isZero()) {
				return arg1;
			}
			return F.Divide(ast.arg1(), norm);
		}

	}

	/**
	 * Compute the null space of a matrix.
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29">Wikipedia -
	 * Kernel (linear algebra)</a>. <a href=
	 * "http://en.wikibooks.org/wiki/Linear_Algebra/Null_Spaces">Wikibooks -
	 * Null Spaces</a>
	 */
	private static class NullSpace extends AbstractFunctionEvaluator {

		public NullSpace() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			FieldMatrix<IExpr> matrix;
			try {
				Validate.checkSize(ast, 2);

				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
				FieldMatrix<IExpr> nullspace = fmw.getNullSpace(F.CN1);
				if (nullspace == null) {
					return F.List();
				}
				return Convert.matrix2List(nullspace);

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

	}

	/**
	 * Pseudoinverse of a matrix
	 * 
	 * See <a href=
	 * "https://en.wikipedia.org/wiki/Moore%E2%80%93Penrose_pseudoinverse">
	 * Moore-Penrose pseudoinverse</a>
	 */
	private final static class PseudoInverse extends AbstractMatrix1Matrix {

		public PseudoInverse() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return numericEval(ast, engine);
		}

		@Override
		public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix) {
			return null;
		}

		@Override
		public RealMatrix realMatrixEval(RealMatrix matrix) {
			final org.hipparchus.linear.SingularValueDecomposition lu = new org.hipparchus.linear.SingularValueDecomposition(
					matrix);
			DecompositionSolver solver = lu.getSolver();
			return solver.getInverse();
		}
	}

	private static class QRDecomposition extends AbstractMatrix1Expr {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// FieldMatrix<IExpr> matrix;
			try {

				int[] dim = ast.arg1().isMatrix();
				if (dim != null) {
					// if (dim[0] == 1 && dim[1] == 1) {
					// }
					
					// if (dim[0] == 2 && dim[1] == 2) {
					// matrix = Convert.list2Matrix((IAST) ast.arg1());
					// if (matrix != null) {
					// }
					// }

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

			// switch to numeric calculation
			return numericEval(ast, engine);
		}

		@Override
		public IAST realMatrixEval(RealMatrix matrix) {
			try {
				IAST list = F.List();
				org.hipparchus.linear.QRDecomposition ed = new org.hipparchus.linear.QRDecomposition(matrix);
				RealMatrix rMatrix = ed.getR();
				RealMatrix qMatrix = ed.getQ();
				list.append(Convert.realMatrix2List(qMatrix));
				list.append(Convert.realMatrix2List(rMatrix));
				return list;
			} catch (Exception ime) {
				throw new WrappedException(ime); 
			}
		}

		@Override
		public IExpr matrixEval(FieldMatrix<IExpr> matrix) {
			return F.NIL;
		}
	}

	/**
	 * <p>
	 * Reduce the matrix to row reduced echelon form.
	 * </p>
	 * 
	 * See:
	 * <ul>
	 * <li><a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia -
	 * Row echelon form</a></li>
	 * <li><a href="https://www.math.hmc.edu/calculus/tutorials/linearsystems/">
	 * Solving Systems of Linear Equations; Row Reduction </a></li>
	 * </ul>
	 */
	private static class RowReduce extends AbstractFunctionEvaluator {

		public RowReduce() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			FieldMatrix<IExpr> matrix;
			try {
				Validate.checkSize(ast, 2);

				final IAST list = (IAST) ast.arg1();
				matrix = Convert.list2Matrix(list);
				FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix);
				return Convert.matrix2List(fmw.getRowReducedMatrix());

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

	}

	/**
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">
	 * Wikipedia: Singular value decomposition</a>
	 */
	private final static class SingularValueDecomposition extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			RealMatrix matrix;
			try {

				matrix = ast.arg1().toRealMatrix();
				if (matrix != null) {
					final org.hipparchus.linear.SingularValueDecomposition svd = new org.hipparchus.linear.SingularValueDecomposition(
							matrix);
					final RealMatrix uMatrix = svd.getU();
					final RealMatrix sMatrix = svd.getS();
					final RealMatrix vMatrix = svd.getV();

					final IAST result = List();
					final IAST uMatrixAST = new ASTRealMatrix(uMatrix, false);
					final IAST sMatrixAST = new ASTRealMatrix(sMatrix, false);
					final IAST vMatrixAST = new ASTRealMatrix(vMatrix, false);
					result.append(uMatrixAST);
					result.append(sMatrixAST);
					result.append(vMatrixAST);
					return result;
				}

			} catch (final WrongArgumentType e) {
				// WrongArgumentType occurs in list2RealMatrix(),
				// if the matrix elements aren't pure numerical values
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

	}

	/**
	 * SquaredEuclidianDistance of two vectors
	 */
	private final static class SquaredEuclideanDistance extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST functionList, EvalEngine engine) {
			if (functionList.size() != 3) {
				throw new WrongNumberOfArguments(functionList, 2, functionList.size() - 1);
			}
			IExpr arg1 = functionList.arg1();
			IExpr arg2 = functionList.arg2();

			int dim1 = arg1.isVector();
			if (dim1 > (-1)) {
				int dim2 = arg2.isVector();
				if (dim1 == dim2) {
					if (dim1 == 0) {
						return F.C0;
					}
					IAST a1 = ((IAST) arg1);
					IAST a2 = ((IAST) arg2);
					IAST plusAST = F.Plus();
					for (int i = 1; i < a1.size(); i++) {
						plusAST.append(F.Sqr(F.Abs(F.Subtract(a1.get(i), a2.get(i)))));
					}
					return plusAST;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Trace of a matrix.<br>
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Trace_(linear_algebra)">Trace
	 * (linear algebra)</a>
	 */
	private static class Tr extends AbstractEvaluator {

		public Tr() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			// TODO generalize for tensors
			final int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				final IAST mat = (IAST) ast.arg1();
				IAST tr;
				int len = dim[0] < dim[1] ? dim[0] : dim[1];
				if (ast.size() > 2) {
					tr = F.ast(ast.arg2(), len, true);
				} else {
					tr = F.ast(F.Plus, len, true);
				}
				IAST row;
				for (int i = 1; i <= len; i++) {
					row = (IAST) mat.get(i);
					tr.set(i, row.get(i));
				}
				return tr;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Transpose a matrix.
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Transpose">Transpose</a>
	 */
	private static class Transpose extends AbstractEvaluator {

		public Transpose() {

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// TODO generalize transpose for all levels
			Validate.checkRange(ast, 2);

			final int[] dim = ast.arg1().isMatrix();
			if (dim != null) {
				final IAST originalMatrix = (IAST) ast.arg1();
				return transpose(originalMatrix, dim[0], dim[1]);
			}
			return F.NIL;
		}

		/**
		 * Transpose the given matrix.
		 * 
		 * @param matrix
		 *            the matrix which should be transposed
		 * @param rows
		 *            number of rows of the matrix
		 * @param cols
		 *            number of columns of the matrix
		 * @return
		 */
		public IAST transpose(final IAST matrix, int rows, int cols) {
			final IAST transposedMatrix = F.ast(F.List, cols, true);
			for (int i = 1; i <= cols; i++) {
				transposedMatrix.set(i, F.ast(F.List, rows, true));
			}

			IAST originalRow;
			IAST transposedResultRow;
			for (int i = 1; i <= rows; i++) {
				originalRow = (IAST) matrix.get(i);
				for (int j = 1; j <= cols; j++) {
					transposedResultRow = (IAST) transposedMatrix.get(j);
					transposedResultRow.set(i, transform(originalRow.get(j)));
				}
			}
			transposedMatrix.addEvalFlags(IAST.IS_MATRIX);
			return transposedMatrix;
		}

		protected IExpr transform(final IExpr expr) {
			return expr;
		}

	}

	/**
	 * Create a unit vector
	 * 
	 * See <a href="http://en.wikipedia.org/wiki/Unit_vector">Wikipedia - Unit
	 * vector</a>
	 */
	private final static class UnitVector extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST2()) {
				int n = Validate.checkIntType(ast, 1);
				int k = Validate.checkIntType(ast, 2);
				if (k <= n) {
					IAST vector = F.ListAlloc(n);
					for (int i = 0; i < n; i++) {
						vector.append(F.C0);
					}
					vector.set(k, F.C1);
					return vector;
				}
				return F.NIL;
			}

			if (ast.arg1().isInteger()) {
				int k = Validate.checkIntType(ast, 1);
				if (k == 1) {
					return F.List(F.C1, F.C0);
				}
				if (k == 2) {
					return F.List(F.C0, F.C1);
				}
			}
			return F.NIL;
		}

	}

	/**
	 * Vandermonde matrix, defined by A<sub>i,j</sub> = vector[i]^(j-1). See
	 * <a href="http://en.wikipedia.org/wiki/Vandermonde_matrix">Vandermonde
	 * matrix</a>
	 * 
	 */
	private static class VandermondeMatrix extends AbstractFunctionEvaluator {
		public VandermondeMatrix() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (ast.arg1().isList()) {
				final IAST lst = (IAST) ast.arg1();
				final int len0 = lst.size() - 1;

				final IAST resultList = List();
				final int[] indexArray = new int[2];
				indexArray[0] = len0;
				indexArray[1] = len0;

				final IIndexFunction<IExpr> function = new IIndexFunction<IExpr>() {
					@Override
					public IExpr evaluate(int[] index) {
						return Power(lst.get(index[0] + 1), F.integer(index[1]));
					}
				};
				final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList, function);
				final IAST matrix = (IAST) generator.table();
				matrix.addEvalFlags(IAST.IS_MATRIX);
				return matrix;
			}

			return F.NIL;
		}
	}

	/**
	 * <p>
	 * VectorAngle(arg1, arg2) calculates the angle between vectors arg1 and
	 * arg2.
	 * </p>
	 * 
	 * See: <a href=
	 * "https://en.wikipedia.org/wiki/Angle#Dot_product_and_generalisation">Wikipedia
	 * - Angle - Dot product and generalisation</a>
	 */
	private static class VectorAngle extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);
			IExpr arg1 = ast.arg1();
			IExpr arg2 = ast.arg2();

			int dim1 = arg1.isVector();
			int dim2 = arg2.isVector();
			if (dim1 > (-1) && dim2 > (-1)) {
				return ArcCos(Divide(Dot(arg1, arg2), Times(Norm(arg1), Norm(arg2))));
			}
			return F.NIL;
		}

	}

	/**
	 * <p>
	 * Use cramer's rule to solve linear equations represented by a
	 * <code>2 x 3</code> augmented matrix which represents the system
	 * <code>M.x == b</code>, where the columns of the <code>2 x 2</code> matrix
	 * <code>M</code> are augmented by the vector <code>b</code>. This method
	 * assumes that the dimensions of the matrix are already checked by the
	 * caller.
	 * </p>
	 * See: <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia
	 * Cramer's rule</a>
	 * 
	 * @param matrix
	 *            the <code>2 x 3</code> augmented matrix
	 * @param quiet
	 *            show no message if there is no solution
	 * @param engine
	 *            the evaluation engine
	 * @return a list of values which solve the equations or <code>F#NIL</code>,
	 *         if the equations have no solution.
	 */
	public static IAST cramersRule2x3(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
		IAST list = F.List();
		// a1 * b2 - b1 * a2
		IExpr denominator = determinant2x2(matrix);
		if (denominator.isZero()) {
			if (!quiet) {
				engine.printMessage("Row reduced linear equations have no solution.");
			}
			return F.NIL;
		}
		// c1 * b2 - b1 * c2
		IExpr xNumerator = F.Subtract(F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 1)),
				F.Times(matrix.getEntry(0, 1), matrix.getEntry(1, 2)));
		list.append(F.Divide(xNumerator, denominator));
		// a1 * c2 - c1*a2
		IExpr yNumerator = F.Subtract(F.Times(matrix.getEntry(0, 0), matrix.getEntry(1, 2)),
				F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 0)));
		list.append(F.Divide(yNumerator, denominator));
		return list;
	}

	/**
	 * <p>
	 * Use cramer's rule to solve linear equations represented by a
	 * <code>3 x 4</code> augmented matrix which represents the system
	 * <code>M.x == b</code>, where the columns of the <code>3 x 3</code> matrix
	 * <code>M</code> are augmented by the vector <code>b</code>. This method
	 * assumes that the dimensions of the matrix are already checked by the
	 * caller.
	 * </p>
	 * See: <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia
	 * Cramer's rule</a>
	 * 
	 * @param matrix
	 *            the <code>3 x 4</code> augmented matrix
	 * @param quiet
	 *            show no message if there is no solution
	 * @param engine
	 *            the evaluation engine
	 * @return a list of values which solve the equations or <code>F#NIL</code>,
	 *         if the equations have no solution.
	 */
	public static IAST cramersRule3x4(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
		IAST list = F.List();
		FieldMatrix<IExpr> denominatorMatrix = matrix.getSubMatrix(0, 2, 0, 2);
		IExpr denominator = determinant3x3(denominatorMatrix);
		if (denominator.isZero()) {
			if (!quiet) {
				engine.printMessage("Row reduced linear equations have no solution.");
			}
			return F.NIL;
		}

		FieldMatrix<IExpr> xMatrix = denominatorMatrix.copy();
		xMatrix.setColumn(0, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr xNumerator = determinant3x3(xMatrix);

		list.append(F.Divide(xNumerator, denominator));

		FieldMatrix<IExpr> yMatrix = denominatorMatrix.copy();
		yMatrix.setColumn(1, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr yNumerator = determinant3x3(yMatrix);

		list.append(F.Divide(yNumerator, denominator));

		FieldMatrix<IExpr> zMatrix = denominatorMatrix.copy();
		zMatrix.setColumn(2, new IExpr[] { matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3) });
		IExpr zNumerator = determinant3x3(zMatrix);

		list.append(F.Divide(zNumerator, denominator));

		return list;
	}

	/**
	 * Get the determinant of a <code>2 x 2</code> matrix. This method assumes
	 * that the dimensions of the matrix are already checked by the caller.
	 * 
	 * @param matrix
	 *            a 2x2 matrix
	 * @return
	 */
	public static IExpr determinant2x2(final FieldMatrix<IExpr> matrix) {
		// 2x2 matrix
		IExpr[] row1 = matrix.getRow(0);
		IExpr[] row2 = matrix.getRow(1);
		return F.evalExpand(row1[0].multiply(row2[1]).subtract((row1[1].multiply(row2[0]))));
	}

	/**
	 * Get the determinant of a <code>3 x 3</code> matrix. This method assumes
	 * that the dimensions of the matrix are already checked by the caller.
	 * 
	 * @param matrix
	 *            a 3x3 matrix
	 * @return
	 */
	public static IExpr determinant3x3(final FieldMatrix<IExpr> matrix) {
		// 3x3 matrix
		IExpr[] row1 = matrix.getRow(0);
		IExpr[] row2 = matrix.getRow(1);
		IExpr[] row3 = matrix.getRow(2);
		return F.evalExpand(row1[0].multiply(row2[1].multiply(row3[2]))
				.subtract((row1[0].multiply(row2[2].multiply(row3[1]))))
				.subtract((row1[1].multiply(row2[0].multiply(row3[2]))))
				.plus((row1[1].multiply(row2[2].multiply(row3[0])))).plus((row1[2].multiply(row2[0].multiply(row3[1]))))
				.subtract((row1[2].multiply(row2[1].multiply(row3[0])))));
	}

	/**
	 * Create a diagonal matrix from <code>valueArray[0]</code> (non-diagonal
	 * elements) and <code>valueArray[1]</code> (diagonal elements).
	 * 
	 * @param valueArray
	 *            2 values for non-diagonal and diagonal elemnets of the matrix.
	 * @param dimension
	 *            of the square matrix
	 * 
	 * @return
	 */
	public static IAST diagonalMatrix(final IExpr[] valueArray, int dimension) {
		final IAST resultList = F.List();
		final int[] indexArray = new int[2];
		indexArray[0] = dimension;
		indexArray[1] = dimension;
		final IndexTableGenerator generator = new IndexTableGenerator(indexArray, resultList,
				new IndexFunctionDiagonal(valueArray));
		final IAST matrix = (IAST) generator.table();
		matrix.addEvalFlags(IAST.IS_MATRIX);
		return matrix;
	}

	public static ArrayList<Integer> getDimensions(IAST ast, IExpr header, int maxLevel) {
		return getDimensions(ast, header, maxLevel, new ArrayList<Integer>());
	}

	public static ArrayList<Integer> getDimensions(IAST ast, IExpr header, int maxLevel, ArrayList<Integer> dims) {
		int size = ast.size();
		dims.add(size - 1);
		if (size > 1 && ast.arg1().isAST()) {
			IAST arg1AST = (IAST) ast.arg1();
			int arg1Size = arg1AST.size();
			if (!header.equals(arg1AST.head())) {
				return dims;
			}
			if (maxLevel > 0) {
				for (int i = 2; i < size; i++) {
					if (!ast.get(i).isAST()) {
						return dims;
					}
					if (arg1Size != ((IAST) ast.get(i)).size()) {
						return dims;
					}
				}
				getDimensions(arg1AST, header, maxLevel - 1, dims);
			}
		}
		return dims;
	}

	/**
	 * Return the solution of the given (augmented-)matrix interpreted as a
	 * system of linear equations
	 * 
	 * @param matrix
	 * @param engine
	 *            the evaluation engine
	 * @return <code>F.NIL</code> if the linear system is inconsistent and has
	 *         no solution
	 */
	public static IAST rowReduced2List(FieldMatrix<IExpr> matrix, EvalEngine engine) {

		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		if (rows == 2 && cols == 3) {
			IAST list = cramersRule2x3(matrix, false, engine);
			if (list.isPresent()) {
				return list;
			}
		} else if (rows == 3 && cols == 4) {
			IAST list = cramersRule3x4(matrix, false, engine);
			if (list.isPresent()) {
				return list;
			}
		}
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
		IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				engine.printMessage("Row reduced linear equations have no solution.");
				return F.NIL;
			}
		}
		IAST list = F.List();
		for (int j = 0; j < rows; j++) {
			list.append(F.eval(F.Together(rowReduced.getEntry(j, cols - 1))));
		}
		if (rows < cols - 1) {
			for (int i = rows; i < cols - 1; i++) {
				list.append(F.C0);
			}
		}
		return list;
	}

	/**
	 * Row reduce the given <code>(augmented-)matrix</code> and append the
	 * result as rules for the given <code>variableList</code>.
	 * 
	 * @param matrix
	 *            a (augmented-)matrix
	 * @param listOfVariables
	 *            list of variable symbols
	 * @param resultList
	 *            a list to which the rules should be appended
	 * @param engine
	 *            the evaluation engine
	 * @return resultList with the appended results as list of rules
	 */
	public static IAST rowReduced2RulesList(FieldMatrix<IExpr> matrix, IAST listOfVariables, IAST resultList,
			EvalEngine engine) {
		int rows = matrix.getRowDimension();
		int cols = matrix.getColumnDimension();
		IAST smallList = null;
		if (rows == 2 && cols == 3) {
			smallList = cramersRule2x3(matrix, true, engine);
		} else if (rows == 3 && cols == 4) {
			smallList = cramersRule3x4(matrix, true, engine);
		}
		if (smallList != null) {
			if (!smallList.isPresent()) {
				// no solution
				return F.List();
			}
			IAST list = F.List();
			IAST rule;
			for (int j = 1; j < smallList.size(); j++) {
				rule = F.Rule(listOfVariables.get(j), F.eval(smallList.get(j)));
				list.append(rule);
			}

			resultList.append(list);
			return resultList;
		}
		FieldReducedRowEchelonForm ref = new FieldReducedRowEchelonForm(matrix);
		FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
		int size = listOfVariables.size() - 1;

		IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
		IAST list = F.List();
		if (lastVarCoefficient.isZero()) {
			if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
				// no solution
				return F.List();
			}
		}
		IAST rule;
		for (int j = 1; j < rows + 1; j++) {
			if (j < size + 1) {
				IExpr diagonal = rowReduced.getEntry(j - 1, j - 1);
				if (!diagonal.isZero()) {
					IAST plus = F.Plus();
					plus.append(rowReduced.getEntry(j - 1, cols - 1));
					for (int i = j; i < cols - 1; i++) {
						if (!rowReduced.getEntry(j - 1, i).isZero()) {
							plus.append(F.Times(rowReduced.getEntry(j - 1, i).negate(), listOfVariables.get(i + 1)));
						}
					}
					rule = F.Rule(listOfVariables.get(j), F.eval(F.Together(plus.getOneIdentity(F.C0))));
					list.append(rule);
				}
			}
		}
		resultList.append(list);
		return resultList;
	}

	final static LinearAlgebra CONST = new LinearAlgebra();

	public static LinearAlgebra initialize() {
		return CONST;
	}

	private LinearAlgebra() {

	}

}
