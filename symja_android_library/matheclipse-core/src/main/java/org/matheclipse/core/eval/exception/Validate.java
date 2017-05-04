package org.matheclipse.core.eval.exception;

import java.io.IOException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Static methods to be called at the start of the built-in <code>IFunctionEvaluator#evaluate()</code> methods to verify
 * correct arguments and state.
 * 
 */
public final class Validate {
	/**
	 * Check the argument, if it's a Java {@code int} value in the range [0, Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IAST ast, int pos) {
		return checkIntType(ast, pos, 0);
	}

	/**
	 * Get the exponent <code>int</code> value of the <code>ast</code> expressions, which is identified as a
	 * <code>Power[&lt;something&gt;, exponent]</code> expression. The <code>int</code> value can be determined from an
	 * IInteger or INum expression.
	 * 
	 * @param ast
	 * @return the exponent <code>int</code> value of the <code>Power[&lt;something&gt;, exponent]</code> expression.
	 * @throws WrongArgumentType
	 */
	public static int checkPowerExponent(final IAST ast) {
		try {
			IExpr arg2 = ast.arg2();
			// the following may throw ArithmeticException
			if (arg2 instanceof IInteger) {
				return ((IInteger) arg2).toInt();
			} else if (arg2 instanceof INum) {
				return ((INum) arg2).toInt();
			}
		} catch (ArithmeticException ae) {
			//
		}
		throw new WrongArgumentType(ast, ast.arg2(), 2,
				"Trying to convert the argument into an integer exponent: " + ast.arg2());
	}

	/**
	 * Get the exponent <code>long</code> value of the <code>ast</code> expressions, which is identified as a
	 * <code>Power[&lt;something&gt;, exponent]</code> expression. The <code>long</code> value can be determined from an
	 * IInteger or INum expression.
	 * 
	 * @param ast
	 * @return the exponent <code>long</code> value of the <code>Power[&lt;something&gt;, exponent]</code> expression.
	 * @throws WrongArgumentType
	 */
	public static long checkLongPowerExponent(final IAST ast) {
		IExpr arg2 = ast.arg2();
		return checkLongType(arg2);
	}

	/**
	 * Check the argument, if it's a Java {@code long} value.
	 * 
	 * @throws WrongArgumentType
	 */
	public static long checkLongType(IExpr expr) {
		long exponent = 0;
		try {
			// the following may throw ArithmeticException
			if (expr instanceof IInteger) {
				exponent = ((IInteger) expr).toLong();
				return exponent;
			} else if (expr instanceof INum) {
				exponent = ((INum) expr).toLong();
				return exponent;
			}
		} catch (ArithmeticException ae) {
			//
		}
		throw new WrongArgumentType(expr, "Trying to convert the argument into a Java long number: " + expr);
	}

	/**
	 * Check the argument, if it's an {@code IAST} of {@code long} values in the range [{@code startValue},
	 * Long.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static long[] checkListOfLongs(IExpr arg, long startValue) {
		if (arg.isList()) {
			IAST list = (IAST) arg;
			long[] result = new long[list.size() - 1];
			long longValue = 0;
			try {
				IExpr expr;
				for (int i = 1; i < list.size(); i++) {
					expr = list.get(i);
					// the following may throw an ArithmeticException
					if (expr instanceof IInteger) {
						longValue = ((IInteger) expr).toLong();
					} else if (expr instanceof INum) {
						longValue = ((INum) expr).toLong();
					}
					if (startValue > longValue) {
						throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
								+ startValue + " - " + Long.MAX_VALUE);
					}
					result[i - 1] = longValue;
				}
				return result;
			} catch (ArithmeticException ae) {
				//
			}
		}
		throw new WrongArgumentType(arg, "Trying to convert the given list into a list of long numbers: " + arg);
	}

	/**
	 * Check the argument, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IAST ast, int pos, int startValue) {
		if (ast.get(pos).isSignedNumber()) {
			try {
				int result = ((ISignedNumber) ast.get(pos)).toInt();
				if (startValue > result) {
					throw new WrongArgumentType(ast, ast.get(pos), pos,
							"Trying to convert the argument into the integer range: " + startValue + " - "
									+ Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(ast, ast.get(pos), pos,
						"Trying to convert the argument into the integer range: " + startValue + " - "
								+ Integer.MAX_VALUE);
			}
		}
		throw new WrongArgumentType(ast, ast.get(pos), pos,
				"Trying to convert the argument into the integer range: " + startValue + " - " + Integer.MAX_VALUE);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [0 , Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntLevelType(IExpr expr) {
		return checkIntLevelType(expr, 0);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @param expr
	 *            a signed number which will be converted to a Java <code>int</code> if possible, otherwise throw a
	 *            <code>WrongArgumentType</code> exception.
	 * @throws WrongArgumentType
	 */
	public static int checkIntLevelType(IExpr expr, int startValue) {
		if (expr.isInfinity()) {
			try {
				int result = Integer.MAX_VALUE;
				if (startValue > result) {
					throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
							+ startValue + " - " + Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						+ startValue + " - " + Integer.MAX_VALUE);
			}
		}
		if (expr.isSignedNumber()) {
			try {
				int result = ((ISignedNumber) expr).toInt();
				if (startValue > result) {
					throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
							+ startValue + " - " + Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						+ startValue + " - " + Integer.MAX_VALUE);
			}
		}
		if (expr.isNegativeInfinity()) {
			try {
				int result = Integer.MIN_VALUE;
				if (startValue > result) {
					throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
							+ startValue + " - " + Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						+ startValue + " - " + Integer.MAX_VALUE);
			}
		}
		throw new WrongArgumentType(expr,
				"Trying to convert the expression into the integer range: " + startValue + " - " + Integer.MAX_VALUE);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [0 , Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IExpr expr) {
		return checkIntType(expr, 0);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @param expr
	 *            a signed number which will be converted to a Java <code>int</code> if possible, otherwise throw a
	 *            <code>WrongArgumentType</code> exception.
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IExpr expr, int startValue) {
		if (expr.isSignedNumber()) {
			try {
				int result = ((ISignedNumber) expr).toInt();
				if (startValue > result) {
					throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
							+ startValue + " - " + Integer.MAX_VALUE);
				}
				return result;
			} catch (ArithmeticException ae) {
				throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						+ startValue + " - " + Integer.MAX_VALUE);
			}
		}
		throw new WrongArgumentType(expr,
				"Trying to convert the expression into the integer range: " + startValue + " - " + Integer.MAX_VALUE);
	}

	/**
	 * Check if the argument at the given position is a integer.
	 * 
	 * @param position
	 *            the position which has to be a symbol.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static IInteger checkIntegerType(IAST ast, int position) {
		if (ast.get(position).isInteger()) {
			return (IInteger) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Integer expected!");
	}

	/**
	 * If {@code ast.size() < from} throw a {@code WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *             if {@code size} is not in the range {@code from} to {@code Integer.MAX_VALUE}
	 */
	public static IAST checkRange(IAST ast, int from) {
		return checkRange(ast, from, Integer.MAX_VALUE);
	}

	/**
	 * If {@code ast.size() < from || ast.size() > to} throw a {@code WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *             if {@code size} is not in the range {@code from} to {@code to}
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
	 * If {@code ast.size() != size} throw a {@code WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *             if {@code size} unequals the list size
	 */
	public static IAST checkSize(IAST ast, int size) {
		if (ast.size() != size) {
			throw new WrongNumberOfArguments(ast, size - 1, ast.size() - 1);
		}
		return ast;
	}

	/**
	 * If {@code ast.size()-1} is not even throw a {@code WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *             if {@code ast.size()-1} is not even
	 */
	public static IAST checkEven(IAST ast) {
		if (((ast.size() - 1) & 0x0001) == 0x0001) {
			throw new WrongNumberOfArguments(1, ast, ast.size() - 1);
		}
		return ast;
	}

	/**
	 * If {@code ast.size()-1} is not odd throw a {@code WrongNumberOfArguments} exception.
	 * 
	 * @throws WrongNumberOfArguments
	 *             if {@code ast.size()-1} is not odd
	 */
	public static IAST checkOdd(IAST ast) {
		if (((ast.size() - 1) & 0x0001) == 0x0000) {
			throw new WrongNumberOfArguments(2, ast, ast.size() - 1);
		}
		return ast;
	}

	/**
	 * Check if the argument at the given position is a <code>List()</code> (i.e. <code>{...}</code>) object.
	 * 
	 * @param position
	 *            the position which has to be a list.
	 * @throws WrongArgumentType
	 *             if it's not a list.
	 */
	public static IAST checkListType(IAST ast, int position) {
		if (ast.get(position).isList()) {
			return (IAST) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "List expected!");
	}

	/**
	 * Check if the argument at the given position is a <code>IStringX</code> string object.
	 * 
	 * @param position
	 *            the position which has to be a string.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static IStringX checkStringType(IAST ast, int position) {
		if (ast.get(position) instanceof IStringX) {
			return (IStringX) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "String expected!");
	}

	/**
	 * Check if the argument at the given position is a <code>IStringX</code> string object.
	 * 
	 * @param position
	 *            the position which has to be a string.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static String checkContextName(IAST ast, int position) {

		if (ast.get(position) instanceof IStringX) {
			IStringX strX = (IStringX) ast.get(position);
			String contextName = strX.toString();
			if (contextName.charAt(contextName.length() - 1) != '`') {
				throw new WrongArgumentType(ast, ast.get(position), position,
						"Contextname must be prepended by a '`' character!");
			}
			return contextName;
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "String expected!");
	}

	/**
	 * Check if the argument at the given position is a single symbol or a list of symbols.
	 * 
	 * @param position
	 *            the position which has to be a symbol or list.
	 * @return a list of symbols defined at <code>ast.get(position)</code>.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static IAST checkSymbolOrSymbolList(IAST ast, int position) {
		IAST vars = null;
		if (ast.get(position).isList()) {
			vars = (IAST) ast.get(position);
			for (int i = 1; i < vars.size(); i++) {
				Validate.checkSymbolType(vars, i);
			}
			return vars;
		} else {
			return F.List(Validate.checkSymbolType(ast, position));
		}
	}

	/**
	 * Check if the argument at the given position is a single variable or a list of variables.
	 * 
	 * @param ast
	 * @param position
	 *            the position which has to be a variable or list of variables.
	 * @param engine
	 *            engine to print a message if the expression is no variable
	 * @return a list of symbols defined at <code>ast.get(position)</code> or <code>null</code> if not.
	 */
	public static IAST checkIsVariableOrVariableList(IAST ast, int position, EvalEngine engine) {
		IAST vars = null;
		IExpr temp = null;
		if (ast.get(position).isList()) {
			vars = (IAST) ast.get(position);
			for (int i = 1; i < vars.size(); i++) {
				temp = Validate.checkSymbolType(vars, i);
				if (temp == null) {
					return null;
				}
			}
			return vars;
		} else {
			temp = Validate.checkIsVariable(ast, position, engine);
			if (temp == null) {
				return null;
			}
			return F.List(temp);
		}
	}

	/**
	 * Check if the argument at the given position is a single symbol or a list of symbols.
	 * 
	 * @param position
	 *            the position which has to be a symbol or list.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	// public static IAST checkSymbolList(IAST ast, int position) {
	// IAST vars = null;
	// if (ast.get(position).isList()) {
	// vars = (IAST) ast.get(position);
	// for (int i = 1; i < vars.size(); i++) {
	// Validate.checkSymbolType(vars, i);
	// }
	// return vars;
	// }
	// throw new WrongArgumentType(ast, ast.get(position), position, "List of
	// symbols expected!");
	// }

	/**
	 * Check if the argument is a symbol and has an assigned value.
	 * 
	 * @param expr
	 *            the expr which has to be a symbol.
	 * @return <code>expr</code> if it's a Symbol
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static ISymbol checkAssignedVariable(IExpr expr) {
		if (expr.isSymbol() && ((ISymbol) expr).hasAssignedSymbolValue()) {
			return (ISymbol) expr;
		}
		throw new WrongArgumentType(expr,
				"Expecting assigned value for variable expression: " + expr.toString() + " !");
	}

	/**
	 * Check if the argument at the given position is a symbol.
	 * 
	 * @param position
	 *            the position which has to be a symbol.
	 * @throws WrongArgumentType
	 *             if it's not a symbol.
	 */
	public static ISymbol checkSymbolType(IAST ast, int position) {
		if (ast.get(position).isSymbol()) {
			return (ISymbol) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Symbol expected!");
	}

	/**
	 * Check if the argument at the given position is a symbol.
	 * 
	 * @param position
	 *            the position which has to be a symbol.
	 * @return <code>null</code> if the argument at the given position is not a symbol.
	 */
	public static ISymbol checkSymbolType(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isSymbol()) {
			return (ISymbol) ast.get(position);
		}
		engine.printMessage(
				ast.get(position).toString() + " is not a variable with a value, so its value cannot be changed.");
		return null;
	}

	/**
	 * Check if the argument at the given position is a variable, i.e. a symbol which doesnt't have the
	 * <code>Constant</code> set.
	 * 
	 * @param position
	 *            the position which has to be a variable.
	 * @return null if the argument is not a variable
	 */
	public static ISymbol checkIsVariable(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isSymbol() && !ast.get(position).isConstant()) {
			return (ISymbol) ast.get(position);
		}
		engine.printMessage(ast.get(position).toString() + " is not a valid variable");
		return null;
	}

	/**
	 * Check if the argument at the given position is an AST.
	 * 
	 * @param position
	 *            the position which has to be an AST.
	 * @throws WrongArgumentType
	 *             if it's not an AST.
	 */
	public static IAST checkASTUpRuleType(IExpr expr) {
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			// for (int i = 0; i < ast.size(); i++) {
			// if (!(ast.get(i) instanceof IPatternObject)) {
			// throw new WrongArgumentType(ast, ast.get(i), i,
			// "Pattern objects are not allowed in left-hand-side of UpSet[] or
			// UpSetDelayed[]!");
			// }
			// }
			return ast;
		}
		throw new WrongArgumentType(expr, "Function(AST)  in left-hand-side of UpSet[] or UpSetDelayed[] expected!");
	}

	/**
	 * Check if the argument at the given position is an AST.
	 * 
	 * @param position
	 *            the position which has to be an AST.
	 * @throws WrongArgumentType
	 *             if it's not an AST.
	 */
	public static IAST checkASTType(IAST ast, int position) {
		if (ast.get(position).isAST()) {
			return (IAST) ast.get(position);
		}
		throw new WrongArgumentType(ast, ast.get(position), position, "Function(AST) expected!");
	}

	/**
	 * Check if the expression is an AST.
	 * 
	 * @throws WrongArgumentType
	 *             if it's not an AST.
	 */
	public static IAST checkASTType(IExpr expr, EvalEngine engine) {
		if (expr.isAST()) {
			return (IAST) expr;
		}
		engine.printMessage("Nonatomic expression expected.");
		return null;
	}

	private Validate() {
	}

	/**
	 * Check if the argument at the given <code>ast</code> position is an equation (i.e. <code>Equal(a,b)</code>) or a
	 * list of equations or a boolean <code>And()</code> expression of equations and return a list of expanded
	 * expressions, which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 *            the position of the equations argument in the <code>ast</code> expression.
	 * @return
	 */
	public static IAST checkEquations(final IAST ast, int position) {
		IExpr expr = ast.get(position);
		IAST eqns = null;
		IAST eq;
		IAST termsEqualZeroList = F.List();
		if (expr.isList() || expr.isAnd()) {

			// a list of equations or a boolean AND expression of equations
			eqns = (IAST) expr;
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST(F.Equal, 3)) {
					eq = (IAST) eqns.get(i);
					termsEqualZeroList.append(F.expandAll(F.Subtract(eq.arg1(), eq.arg2()), true, true));
				} else {
					// not an equation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equal[] expression (a==b) expected");
				}
			}

		} else {
			termsEqualZeroList.append(checkEquation(expr));
		}
		return termsEqualZeroList;
	}

	/**
	 * Check if the argument at the given <code>ast</code> position is an equation or inequation (i.e.
	 * <code>Equal(a,b)</code>) or a list of equations or inequations or a boolean <code>And()</code> expression of
	 * equations and return a list of expanded expressions, which should be equal to <code>0</code>.
	 * 
	 * @param ast
	 * @param position
	 *            the position of the equations argument in the <code>ast</code> expression.
	 * @return
	 */
	public static IAST checkEquationsAndInequations(final IAST ast, int position) {
		IExpr expr = ast.get(position);
		IAST eqns = null;
		IAST termsEqualZeroList = F.List();
		if (expr.isList() || expr.isAnd()) {

			// a list of equations or inequations or a boolean AND expression of
			// equations
			eqns = (IAST) expr;
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST2()) {
					IAST eq = (IAST) eqns.get(i);
					termsEqualZeroList.append(checkEquationAndInequation(eq));
				} else {
					// not an equation or inequation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equation or inequation expression expected");
				}
			}

		} else {
			if (expr.isAST()) {
				termsEqualZeroList.append(checkEquationAndInequation((IAST) expr));
			}
		}
		return termsEqualZeroList;
	}

	private static IAST checkEquationAndInequation(IAST eq) {
		IExpr head = eq.head();
		if (head.equals(F.Equal) || head.equals(F.Unequal) || head.equals(F.Greater) || head.equals(F.GreaterEqual)
				|| head.equals(F.Less) || head.equals(F.LessEqual)) {
			final IExpr[] arr = new IExpr[] { F.expandAll(eq.arg1(), true, true), F.expandAll(eq.arg2(), true, true) };
			return F.ast(arr, head);
		} else {
			// not an equation or inequation
			throw new WrongArgumentType(eq, "Equation or inequation expression expected");
		}
	}

	/**
	 * Check if the given expression is an equation (i.e. <code>Equal(a,b)</code>)
	 * 
	 * @param expr
	 *            the expression which should be an equation
	 * @return
	 */
	public static IExpr checkEquation(IExpr expr) {
		IAST eq;
		if (expr.isAST(F.Equal, 3)) {
			eq = (IAST) expr;
			return F.evalExpandAll(F.Subtract(eq.arg1(), eq.arg2()));
		} else {
			// not an equation
			throw new WrongArgumentType(expr, "Equal[] expression (a==b) expected");
		}
	}

	public static void printException(final Appendable buf, final Throwable e) {
		String msg = e.getMessage();
		try {
			if (msg != null) {
				buf.append("\nError: " + msg);
			} else {
				buf.append("\nError: " + e.getClass().getSimpleName());
			}
		} catch (IOException e1) {
			// ignore
		}
	}

}
