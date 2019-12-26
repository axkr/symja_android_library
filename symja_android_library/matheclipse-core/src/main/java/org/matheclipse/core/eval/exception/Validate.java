package org.matheclipse.core.eval.exception;

import java.io.IOException;
import java.math.BigInteger;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
	 * Check the argument, if it's a Java {@code long} value.
	 * 
	 * @throws WrongArgumentType
	 */
	// private static long checkLongType(IExpr expr) {
	// if (expr instanceof IntegerSym) {
	// // IntegerSym always fits into a long number
	// return ((IntegerSym) expr).toLong();
	// }
	// try {
	// // the following may throw ArithmeticException
	// if (expr instanceof IInteger) {
	// return ((IInteger) expr).toLong();
	// } else if (expr instanceof INum) {
	// return ((INum) expr).toLong();
	// }
	// } catch (ArithmeticException ae) {
	// //
	// }
	// throw new WrongArgumentType(expr, "Trying to convert the argument into a Java long number: " + expr);
	// }

	/**
	 * Check the argument, if it's an {@code IAST} of {@code long} values in the range [{@code startValue},
	 * Long.MAX_VALUE]
	 * 
	 * @param ast
	 * @param arg
	 * @param startValue
	 * @param quiet
	 *            suppress error message output
	 * @param engine
	 * @return <code>null</code> if the conversion isn't possible
	 */
	public static long[] checkListOfLongs(IAST ast, IExpr arg, long startValue, boolean quiet, EvalEngine engine) {
		if (arg.isList()) {
			IAST list = (IAST) arg;
			long[] result = new long[list.argSize()];
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
						if (!quiet) {
							// List of Java long numbers expected in `1`.
							IOFunctions.printMessage(ast.topHead(), "listoflongs", F.List(arg), engine);
						}
						return null;
						// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						// + startValue + " - " + Long.MAX_VALUE);
					}
					result[i - 1] = longValue;
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		if (!quiet) {
			IOFunctions.printMessage(ast.topHead(), "listoflongs", F.List(arg), engine);
		}
		return null;
	}

	public static BigInteger[] checkListOfBigIntegers(IAST ast, IExpr arg, boolean nonNegative, EvalEngine engine) {
		if (arg.isList()) {
			IAST list = (IAST) arg;
			BigInteger[] result = new BigInteger[list.argSize()];

			try {
				IExpr expr;
				BigInteger longValue = BigInteger.ZERO;
				for (int i = 1; i < list.size(); i++) {
					expr = list.get(i);
					// the following may throw an ArithmeticException
					if (expr instanceof IInteger) {
						longValue = ((IInteger) expr).toBigNumerator();
					} else if (expr instanceof INum) {
						longValue = BigInteger.valueOf(((INum) expr).toLong());
					}
					// if (startValue > longValue) {
					// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
					// + startValue + " - " + Long.MAX_VALUE);
					// }
					if (nonNegative && longValue.compareTo(BigInteger.ZERO) < 0) {
						IOFunctions.printMessage(ast.topHead(), "listofbigints", F.List(arg), engine);
						return null;
						// throw new WrongArgumentType(arg,
						// "Trying to convert the given list into a list of long non-negative numbers: " + arg);
					}
					result[i - 1] = longValue;
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		IOFunctions.printMessage(ast.topHead(), "listofbigints", F.List(arg), engine);
		return null;
		// throw new WrongArgumentType(arg, "Trying to convert the given list into a list of long numbers: " + arg);
	}

	/**
	 * Check the argument, if it's an {@code IAST} of {@code int} values in the range {@code minValue} (inclusive),
	 * {@code maxValue} (inclusive).
	 * 
	 * @param ast
	 * @param arg
	 * @param minValue
	 * @param maxValue
	 * @param engine
	 * @return <code>null</code> if the conversion isn't possible
	 */
	public static int[] checkListOfInts(IAST ast, IExpr arg, int minValue, int maxValue, EvalEngine engine) {
		if (arg.isList()) {
			IAST list = (IAST) arg;
			int[] result = new int[list.argSize()];
			int intValue = 0;
			try {
				IExpr expr;
				for (int i = 1; i < list.size(); i++) {
					expr = list.get(i);
					// the following may throw an ArithmeticException
					if (expr instanceof IInteger) {
						intValue = ((IInteger) expr).toInt();
					} else if (expr instanceof INum) {
						intValue = ((INum) expr).toInt();
					}
					if (minValue > intValue || intValue > maxValue) {
						IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
						return null;
						// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
						// + minValue + " - " + maxValue);
					}
					result[i - 1] = intValue;
				}
				return result;
			} catch (RuntimeException rex) {
				//
			}
		}
		IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
		return null;
	}

	/**
	 * Check the argument, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @throws WrongArgumentType
	 */
	public static int checkIntType(IAST ast, int pos, int startValue) {
		if (ast.get(pos) instanceof IntegerSym) {
			// IntegerSym always fits into an int number
			int result = ((IntegerSym) ast.get(pos)).toInt();
			if (startValue > result) {
				// Java int value greater equal `1` expected instead of `2`.
				String str = IOFunctions.getMessage("intjava", F.List(F.ZZ(startValue), ast.get(pos)),
						EvalEngine.get());
				throw new ArgumentTypeException(str);
			}
			return result;
		}
		if (ast.get(pos).isReal()) {
			int result = ast.get(pos).toIntDefault();
			if (result == Integer.MIN_VALUE || startValue > result) {
				// Java int value greater equal `1` expected instead of `2`.
				String str = IOFunctions.getMessage("intjava", F.List(F.ZZ(startValue), ast.get(pos)),
						EvalEngine.get());
				throw new ArgumentTypeException(str);
			}
			return result;
		}
		// Java int value greater equal `1` expected instead of `2`.
		String str = IOFunctions.getMessage("intjava", F.List(F.ZZ(startValue), ast.get(pos)), EvalEngine.get());
		throw new ArgumentTypeException(str);
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
			// maximum possible level in Symja
			int result = Integer.MAX_VALUE;
			if (startValue > result) {
				// Level value greater equal `1` expected instead of `2`.
				String str = IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), F.CInfinity),
						EvalEngine.get());
				throw new ArgumentTypeException(str);
			}
			return result;
		}
		if (expr.isReal()) {
			int result = expr.toIntDefault();
			if (result == Integer.MIN_VALUE || startValue > result) {
				// Level specification value greater equal `1` expected instead of `2`.
				String str = IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), expr), EvalEngine.get());
				throw new ArgumentTypeException(str);
			}
			return result;
		}
		if (expr.isNegativeInfinity()) {
			// maximum possible level in Symja
			int result = Integer.MIN_VALUE;
			if (startValue > result) {
				// Level specification value greater equal `1` expected instead of `2`.
				String str = IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), F.CNInfinity),
						EvalEngine.get());
				throw new ArgumentTypeException(str);
			}
			return result;
		}
		// Level specification value greater equal `1` expected instead of `2`.
		String str = IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), expr), EvalEngine.get());
		throw new ArgumentTypeException(str);
	}

	/**
	 * Check the expression, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE]
	 * 
	 * @param expr
	 *            a signed number which will be converted to a Java <code>int</code> if possible, otherwise throw a
	 *            <code>WrongArgumentType</code> exception.
	 * @return <code>Integer.MIN_VALUE</code> if a <code>Java int</code> value couldn't be determined.
	 */
	public static int checkIntType(ISymbol head, IExpr expr, int startValue, EvalEngine engine) {
		int result = expr.toIntDefault();
		if (result == Integer.MIN_VALUE || startValue > result) {
			// Java int value greater equal `1` expected instead of `2`.
			IOFunctions.printMessage(head, "intjava", F.List(F.ZZ(startValue), expr), engine);
			return Integer.MIN_VALUE;
		}
		return result;
		// if (expr instanceof IntegerSym) {
		// // IntegerSym always fits into an int number
		// int result = ((IntegerSym) expr).toInt();
		// if (startValue > result) {
		// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
		// + startValue + " - " + Integer.MAX_VALUE);
		// }
		// return result;
		// }
		// if (expr.isReal()) {
		// try {
		// int result = ((ISignedNumber) expr).toInt();
		// if (startValue > result) {
		// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
		// + startValue + " - " + Integer.MAX_VALUE);
		// }
		// return result;
		// } catch (ArithmeticException ae) {
		// throw new WrongArgumentType(expr, "Trying to convert the expression into the integer range: "
		// + startValue + " - " + Integer.MAX_VALUE);
		// }
		// }
		// throw new WrongArgumentType(expr,
		// "Trying to convert the expression into the integer range: " + startValue + " - " + Integer.MAX_VALUE);
	}

	/**
	 * Check the argument, if it's a Java {@code int} value in the range [ {@code startValue}, Integer.MAX_VALUE].
	 * 
	 * @param expr
	 * @param startValue
	 * @param engine
	 * @return
	 * @throws ArgumentTypeException
	 *             if it's not a Java int value in the range.
	 */
	public static int throwIntType(IExpr expr, int startValue, EvalEngine engine) {
		int result = expr.toIntDefault();
		if (result == Integer.MIN_VALUE || startValue > result) {
			// Java int value greater equal `1` expected instead of `2`.
			String str = IOFunctions.getMessage("intjava", F.List(F.ZZ(startValue), expr), engine);
			throw new ArgumentTypeException(str);
		}
		return result;
	}

	/**
	 * Check if the argument at the given position is a <code>List()</code> (i.e. <code>{...}</code>) object.
	 * 
	 * @param position
	 *            the position which has to be a list.
	 * @param engine
	 *            the evaluation engine
	 * 
	 * @throws WrongArgumentType
	 *             if it's not a list.
	 */
	public static IAST checkListType(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isList()) {
			return (IAST) ast.get(position);
		}
		// List expected at position `1` in `2`.
		return IOFunctions.printMessage(ast.topHead(), "list", F.List(F.ZZ(position), ast), engine);
	}

	/**
	 * <p>
	 * Check if the argument at the given position is a <code>IStringX</code> string object.
	 * </p>
	 * 
	 * @param position
	 *            the position which has to be a string.
	 * @param engine
	 *            the evaluation engine
	 * @return <code>F.NIL</code>
	 */
	public static IExpr checkStringType(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position) instanceof IStringX) {
			return ast.get(position);
		}
		// String expected at position `1` in `2`
		return IOFunctions.printMessage(ast.topHead(), "string", F.List(F.ZZ(position), ast), engine);
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

				// `1` is not a valid context name.
				String str = IOFunctions.getMessage("cxt", F.List(strX), EvalEngine.get());
				throw new ArgumentTypeException(str);
				// throw new WrongArgumentType(ast, ast.get(position), position,
				// "Contextname must be prepended by a '`' character!");
			}
			return contextName;
		}
		// `1` is not a valid context name.
		String str = IOFunctions.getMessage("cxt", F.List(ast.get(position)), EvalEngine.get());
		throw new ArgumentTypeException(str);
		// throw new WrongArgumentType(ast, ast.get(position), position, "String expected!");
	}

	/**
	 * Check if the argument at the given position is a single symbol or a list of symbols.
	 * 
	 * @param position
	 *            the position which has to be a symbol or list.
	 * @param engine
	 *            the evaluation engine
	 * 
	 * @return a list of symbols defined at <code>ast.get(position)</code> or otherwise <code>F.NIL</code>
	 */
	public static IAST checkSymbolOrSymbolList(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isList()) {
			IAST listOfSymbols = (IAST) ast.get(position);
			for (int i = 1; i < listOfSymbols.size(); i++) {
				if (!Validate.checkSymbolType(listOfSymbols, i, engine).isPresent()) {
					return F.NIL;
				}
			}
			return listOfSymbols;
		} else {
			IExpr temp = Validate.checkSymbolType(ast, position, engine);
			if (temp.isPresent()) {
				return F.List(temp);
			}
		}
		return F.NIL;
	}

	/**
	 * Check if the argument at the given position is a single variable or a list of variables.
	 * 
	 * @param ast
	 * @param position
	 *            the position which has to be a variable or list of variables.
	 * @param engine
	 *            engine to print a message if the expression is no variable
	 * @return a list of symbols defined at <code>ast.get(position)</code> or <code>F.NIL</code> if not.
	 */
	public static IAST checkIsVariableOrVariableList(IAST ast, int position, EvalEngine engine) {
		IAST vars = null;
		IExpr temp = null;
		if (ast.get(position).isList()) {
			vars = (IAST) ast.get(position);
			for (int i = 1; i < vars.size(); i++) {
				temp = Validate.checkSymbolType(vars, i, engine);
				if (!temp.isPresent()) {
					return F.NIL;
				}
			}
			return vars;
		} else {
			temp = Validate.checkIsVariable(ast, position, engine);
			if (!temp.isPresent()) {
				return F.NIL;
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
	// private static ISymbol checkAssignedVariable(IExpr expr) {
	// if (expr.isSymbol() && ((ISymbol) expr).hasAssignedSymbolValue()) {
	// return (ISymbol) expr;
	// }
	// throw new WrongArgumentType(expr,
	// "Expecting assigned value for variable expression: " + expr.toString() + " !");
	// }

	/**
	 * Check if the argument at the given position is a symbol.
	 * 
	 * @param ast
	 *            the ast which should be evaluated
	 * @param position
	 *            the position which has to be a symbol.
	 * @param engine
	 *            evaluatioin engine
	 * @return <code>F.NIL</code> if the argument at the given position is not a symbol.
	 */
	public static IExpr checkSymbolType(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isSymbol()) {
			return (ISymbol) ast.get(position);
		}
		// Argument `1` at position `2` is expected to be a symbol.
		return IOFunctions.printMessage(ast.topHead(), "sym", F.List(ast.get(position), F.ZZ(position)), engine);
	}

	/**
	 * Check if the argument at the given position is a variable, i.e. a symbol which doesnt't have the
	 * <code>Constant</code> attribute set.
	 * 
	 * @param position
	 *            the position which has to be a variable.
	 * @return <code>F.NIL</code> if the argument is not a variable
	 */
	public static IExpr checkIsVariable(IAST ast, int position, EvalEngine engine) {
		if (ast.get(position).isSymbol() && !ast.get(position).isConstantAttribute()) {
			return (ISymbol) ast.get(position);
		}
		// `1` is not a valid variable.
		return IOFunctions.printMessage(ast.topHead(), "ivar", F.List(ast.get(position)), engine);
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
		throw new ArgumentTypeException("Function(AST) in left-hand-side of UpSet() or UpSetDelayed() expected!");
	}

	/**
	 * Check if the expression is an AST.
	 * 
	 * @param ast
	 *            TODO
	 * @param expr
	 * @param engine
	 * 
	 * @return <code>F.NIL</code> if the expression is no <code>IAST</code> object.
	 */
	public static IAST checkASTType(IAST ast, IExpr expr, EvalEngine engine) {
		if (expr.isAST()) {
			return (IAST) expr;
		}
		// Nonatomic expression expected.
		return IOFunctions.printMessage(ast.topHead(), "normal", F.List(), engine);
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
	public static IASTAppendable checkEquations(final IAST ast, int position) {
		IExpr expr = ast.get(position);
		if (expr.isList() || expr.isAnd()) {
			IAST listOrAndAST = (IAST) expr;
			int size = listOrAndAST.size();
			IASTAppendable termsEqualZeroList = F.ListAlloc(size);
			return termsEqualZeroList.appendArgs(size, i -> checkEquation(listOrAndAST.get(i)));
			// for (int i = 1; i < size; i++) {
			// termsEqualZeroList.append(checkEquation(listOrAndAST.get(i)));
			// }
			// return termsEqualZeroList;
		}
		return F.ListAlloc(checkEquation(expr));
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
	public static IASTAppendable checkEquationsAndInequations(final IAST ast, int position) {
		IExpr expr = ast.get(position);
		IAST eqns = null;
		IASTAppendable termsEqualZeroList;
		if (expr.isList() || expr.isAnd()) {

			// a list of equations or inequations or a boolean AND expression of
			// equations
			eqns = (IAST) expr;
			termsEqualZeroList = F.ListAlloc(eqns.size());
			for (int i = 1; i < eqns.size(); i++) {
				if (eqns.get(i).isAST2()) {
					IAST eq = (IAST) eqns.get(i);
					termsEqualZeroList.append(checkEquationAndInequation(eq));
				} else {
					// not an equation or inequation
					throw new WrongArgumentType(eqns, eqns.get(i), i, "Equation or inequation expression expected");
				}
			}
			return termsEqualZeroList;
		}
		return F.ListAlloc(checkEquationAndInequation(expr));
	}

	private static IExpr checkEquationAndInequation(IExpr eq) {
		if (eq.isEqual()) {
			IAST equal = (IAST) eq;
			IExpr subtract = EvalEngine.get().evaluate(F.Subtract(equal.arg1(), equal.arg2()));
			final IExpr[] arr = new IExpr[] { //
					subtract.isTimes() ? subtract : F.evalExpandAll(subtract), //
					F.C0 };
			return F.function(F.Equal, arr);
		}
		if (eq.isAST()) {
			IAST equal = (IAST) eq;
			IExpr head = equal.head();
			if (head.equals(F.Equal) || head.equals(F.Unequal) || head.equals(F.Greater) || head.equals(F.GreaterEqual)
					|| head.equals(F.Less) || head.equals(F.LessEqual)) {
				final IExpr[] arr = new IExpr[] { F.expandAll(equal.arg1(), true, true),
						F.expandAll(equal.arg2(), true, true) };
				return F.ast(arr, head);
			}
		} else if (eq.isTrue()) {
			return F.True;
		} else if (eq.isFalse()) {
			return F.False;
		}
		// not an equation or inequation
		throw new WrongArgumentType(eq, "Equation or inequation expression expected");
	}

	/**
	 * Check if the given expression is an equation (i.e. <code>Equal(a,b)</code>)
	 * 
	 * @param expr
	 *            the expression which should be an equation
	 * @return
	 */
	private static IExpr checkEquation(IExpr expr) {
		if (expr.isEqual()) {
			IAST equal = (IAST) expr;
			return F.evalExpandAll(F.Subtract(equal.arg1(), equal.arg2()));
		} else if (expr.isTrue()) {
			return F.True;
		} else if (expr.isFalse()) {
			return F.False;
		} else {
			// not an equation
			throw new WrongArgumentType(expr, "Equal[] expression (a==b) expected");
		}
	}

	public static void printException(final Appendable buf, final Throwable e) {
		String msg = e.getMessage();
		try {
			if (msg != null) {
				buf.append("\n" + e.getClass().getName() + ": " + msg);
			} else {
				buf.append("\n" + e.getClass().getName());
			}
		} catch (IOException e1) {
			// ignore
		}
	}

}
