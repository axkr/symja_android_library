package org.matheclipse.core.eval.exception;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state.
 * 
 */
public final class Validate {
	/**
	 * Check the argument, if it's a Java {@code int} value in the range [0,
	 * Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IAST ast, int pos) {
		return checkIntType(ast, pos, 0);
	}

	/**
	 * Check the argument, if it's a Java {@code int} value in the range [{@code
	 * startValue}, Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IAST ast, int pos, int startValue) {
		if (ast.get(pos).isSignedNumber()) {
			try {
				int result = ((ISignedNumber) ast.get(pos)).toInt();
				if (startValue > result) {
					throw new WrongArgumentType(ast, ast.get(pos), pos, "Trying to convert the argument into the integer range: "
							+ startValue + " - " + Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(ast, ast.get(pos), pos, "Trying to convert the argument into the integer range: " + startValue
						+ " - " + Integer.MAX_VALUE);
			}
		}
		throw new WrongArgumentType(ast, ast.get(pos), pos, "Trying to convert the argument into the integer range: " + startValue
				+ " - " + Integer.MAX_VALUE);
	}

	public static int checkIntType(IExpr expr) {
		return checkIntType(expr, 0);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [
	 * {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IExpr expr, int startValue) {
		if (expr.isSignedNumber()) {
			try {
				int result = ((ISignedNumber) expr).toInt();
				if (startValue > result) {
					throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: " + startValue + " - "
							+ Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: " + startValue + " - "
						+ Integer.MAX_VALUE);
			}
		}
		throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: " + startValue + " - "
				+ Integer.MAX_VALUE);
	}

	/**
	 * Check if the argument at the given position is a integer.
	 * 
	 * @param position
	 *          the position which has to be a symbol.
	 * @throws WrongArgumentType
	 *           if it's not a symbol.
	 */
	public static IInteger checkIntegerType(IAST ast, int position) {
		if (ast.get(position).isInteger()) {
			return (IInteger) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Integer expected!");
	}

	/**
	 * If {@code ast.size() < from} throw a {@code WrongNumberOfArguments}
	 * exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *           if {@code size} is not in the range {@code from} to {@code
	 *           Integer.MAX_VALUE}
	 */
	public static IAST checkRange(IAST ast, int from) {
		return checkRange(ast, from, Integer.MAX_VALUE);
	}

	/**
	 * If {@code ast.size() < from || ast.size() > to} throw a {@code
	 * WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *           if {@code size} is not in the range {@code from} to {@code to}
	 */
	public static IAST checkRange(IAST ast, int from, int to) {
		if (ast.size() < from) {
			throw new WrongNumberOfArguments(ast, from - 1, ast.size() - 1);
		}
		if (ast.size() > to) {
			throw new WrongNumberOfArguments(ast, to - 1, ast.size() - 1);
		}
		return ast;
	}

	/**
	 * If {@code ast.size() != size} throw a {@code WrongNumberOfArguments}
	 * exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *           if {@code size} unequals the list size
	 */
	public static IAST checkSize(IAST ast, int size) {
		if (ast.size() != size) {
			throw new WrongNumberOfArguments(ast, size - 1, ast.size() - 1);
		}
		return ast;
	}

	/**
	 * Check if the argument at the given position is a single symbol or a list of
	 * symbols.
	 * 
	 * @param position
	 *          the position which has to be a symbol or list.
	 * @throws WrongArgumentType
	 *           if it's not a symbol.
	 */
	public static IAST checkSymbolOrSymbolList(IAST ast, int position) {
		IAST vars = null;
		if (ast.get(position).isList()) {
			vars = (IAST) ast.get(position);
			for (int i = 1; i < vars.size(); i++) {
				Validate.checkSymbolType(vars, i);
			}
		} else {
			vars = F.List(Validate.checkSymbolType(ast, position));
		}
		return vars;
	}

	/**
	 * Check if the argument at the given position is a symbol.
	 * 
	 * @param position
	 *          the position which has to be a symbol.
	 * @throws WrongArgumentType
	 *           if it's not a symbol.
	 */
	public static ISymbol checkSymbolType(IAST ast, int position) {
		if (ast.get(position).isSymbol()) {
			return (ISymbol) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Symbol expected!");
	}

	/**
	 * Check if the argument at the given position is an AST.
	 * 
	 * @param position
	 *          the position which has to be an AST.
	 * @throws WrongArgumentType
	 *           if it's not an AST.
	 */
	public static IAST checkASTType(IAST ast, int position) {
		if (ast.get(position).isAST()) {
			return (IAST) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Function(AST) expected!");
	}

	private Validate() {
	}

}
