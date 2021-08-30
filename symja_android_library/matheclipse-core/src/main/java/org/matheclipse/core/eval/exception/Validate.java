package org.matheclipse.core.eval.exception;

import java.io.IOException;
import java.math.BigInteger;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Scanner;

/**
 * Static methods to be called at the start of the built-in <code>IFunctionEvaluator#evaluate()
 * </code> methods to verify correct arguments and state.
 */
public final class Validate {

  /** Check the argument, if it's a Java {@code int} value in the range [0, Integer.MAX_VALUE] */
  public static int checkIntType(IAST ast, int pos) {
    return checkIntType(ast, pos, 0);
  }

  /**
   * Check the argument, if it's an {@code IAST} of {@code long} values in the range [{@code
   * startValue}, Long.MAX_VALUE]
   *
   * @param ast
   * @param arg
   * @param startValue
   * @param quiet suppress error message output
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static long[] checkListOfLongs(
      IAST ast, IExpr arg, long startValue, boolean quiet, EvalEngine engine) {
    if (arg.isList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
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
            }
            result[i - 1] = longValue;
          }
          return result;
        } catch (RuntimeException rex) {
          //
        }
      }
    }
    if (!quiet) {
      // List of Java long numbers expected in `1`.
      IOFunctions.printMessage(ast.topHead(), "listoflongs", F.List(arg), engine);
    }
    return null;
  }

  /**
   * @param ast
   * @param arg
   * @param nonNegative
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static BigInteger[] checkListOfBigIntegers(
      IAST ast, IExpr arg, boolean nonNegative, EvalEngine engine) {
    if (arg.isNonEmptyList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
        BigInteger[] result = new BigInteger[list.argSize()];

        try {
          IExpr expr;
          for (int i = 1; i < list.size(); i++) {
            BigInteger longValue = null;
            expr = list.get(i);
            // the following may throw an ArithmeticException
            if (expr instanceof IInteger) {
              longValue = ((IInteger) expr).toBigNumerator();
            } else if (expr instanceof INum) {
              longValue = BigInteger.valueOf(((INum) expr).toLong());
            }
            if ((longValue == null) || (nonNegative && longValue.compareTo(BigInteger.ZERO) <= 0)) {
              // The first argument `1` of `2` should be a non-empty list of positive integers.
              IOFunctions.printMessage(ast.topHead(), "coef", F.List(arg, ast.topHead()), engine);
              return null;
            }
            result[i - 1] = longValue;
          }
          return result;
        } catch (RuntimeException rex) {
          if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
        }
      }
    }
    // The first argument `1` of `2` should be a non-empty list of positive integers.
    IOFunctions.printMessage(ast.topHead(), "coef", F.List(arg, ast.topHead()), engine);
    return null;
  }

  /**
   * Check the argument, if it's an {@code IAST} of {@code int} values in the range
   * [Integer.MIN_VALUE+1, Integer.MAX_VALUE]
   *
   * @param ast
   * @param arg the non-empty list of integer values
   * @param nonNegative chek if all values are greater or equal 0
   * @param quiet print no error message
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static int[] checkListOfInts(
      IAST ast, IExpr arg, boolean nonNegative, boolean quiet, EvalEngine engine) {
    if (arg.isNonEmptyList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
        int[] result = new int[list.argSize()];

        try {
          IExpr expr;
          for (int i = 1; i < list.size(); i++) {
            expr = list.get(i);
            int intValue = expr.toIntDefault();
            if (intValue == Integer.MIN_VALUE) {
              if (!quiet) {
                // The first argument `1` of `2` should be a non-empty list of positive integers.
                IOFunctions.printMessage(ast.topHead(), "coef", F.List(arg, ast.topHead()), engine);
              }
              return null;
            }
            if (nonNegative && intValue < 0) {
              if (!quiet) {
                // The first argument `1` of `2` should be a non-empty list of positive integers.
                IOFunctions.printMessage(ast.topHead(), "coef", F.List(arg, ast.topHead()), engine);
              }
              return null;
            }
            result[i - 1] = intValue;
          }
          return result;
        } catch (RuntimeException rex) {
          if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
          }
        }
      }
    }
    if (!quiet) {
      // The first argument `1` of `2` should be a non-empty list of positive integers.
      IOFunctions.printMessage(ast.topHead(), "coef", F.List(arg, ast.topHead()), engine);
    }
    return null;
  }

  /**
   * Check the argument, if it's an {@code IAST} of {@code int} values in the range {@code minValue}
   * (inclusive), {@code maxValue} (inclusive).
   *
   * @param ast
   * @param arg
   * @param minValue
   * @param maxValue
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static int[] checkListOfInts(
      IAST ast, IExpr arg, int minValue, int maxValue, EvalEngine engine) {
    if (arg.isList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
        int[] result = new int[list.argSize()];
        int intValue = 0;
        try {
          IExpr expr;
          for (int i = 1; i < list.size(); i++) {
            intValue = list.get(i).toIntDefault();
            if (intValue == Integer.MIN_VALUE) {
              // List of Java int numbers expected in `1`.
              IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
              return null;
            }
            if (minValue > intValue || intValue > maxValue) {
              // List of Java int numbers expected in `1`.
              IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
              return null;
            }
            result[i - 1] = intValue;
          }
          return result;
        } catch (RuntimeException rex) {
          //
        }
      }
    }
    IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
    return null;
  }

  /**
   * @param ast
   * @param arg
   * @param position
   * @param stringLength
   * @param minValue
   * @param maxValue
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static int[][] checkListOfSequenceSpec(
      IAST ast,
      IExpr arg,
      int position,
      int stringLength,
      int minValue,
      int maxValue,
      EvalEngine engine) {
    if (arg.isList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
        int[][] result = new int[list.argSize()][2];
        int intValue = 0;
        try {
          IExpr expr;
          for (int i = 1; i < list.size(); i++) {
            expr = list.get(i);
            if (expr.isList2()) {
              intValue = expr.first().toIntDefault();
              if (intValue == Integer.MIN_VALUE) {
                // Sequence specification or a list of sequence specifications expected at position
                // `1` in `2`.
                IOFunctions.printMessage(
                    ast.topHead(), "mseqs", F.List(F.ZZ(position), ast), engine);
                return null;
              }
              result[i - 1][0] = intValue;

              intValue = expr.second().toIntDefault();
              if (intValue == Integer.MIN_VALUE) {
                // Sequence specification or a list of sequence specifications expected at position
                // `1` in `2`.
                IOFunctions.printMessage(
                    ast.topHead(), "mseqs", F.List(F.ZZ(position), ast), engine);
                return null;
              }
              result[i - 1][0] = intValue;
            } else {
              intValue = expr.toIntDefault();
              if (intValue == Integer.MIN_VALUE) {
                // List of Java int numbers expected in `1`.
                IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
                return null;
              }
              if (minValue > intValue || intValue > maxValue) {
                // List of Java int numbers expected in `1`.
                IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
                return null;
              }
              if (intValue >= 0) {
                result[i - 1][0] = 1;
                result[i - 1][1] = intValue;
              } else {
                result[i - 1][0] = stringLength + intValue;
                result[i - 1][1] = stringLength;
              }
            }
          }
          return result;
        } catch (RuntimeException rex) {
          //
        }
      }
    }
    IOFunctions.printMessage(ast.topHead(), "listofints", F.List(arg), engine);
    return null;
  }

  /**
   * Get a dimension parameter. <code>arg</code> is expected to be a positive integer or a list of
   * positive integers.
   *
   * @param ast
   * @param arg
   * @param engine
   * @return <code>null</code> if the conversion isn't possible
   */
  public static int[] checkDimension(IAST ast, IExpr arg, EvalEngine engine) {
    if (arg.isInteger()) {
      int n = arg.toIntDefault();
      if (n > 0) {
        return new int[] {n};
      }
    } else if (arg.isList()) {
      IAST list = (IAST) arg;
      if (list.argSize() > 0) {
        int[] result = new int[list.argSize()];
        int intValue = 0;
        try {
          IExpr expr;
          for (int i = 1; i < list.size(); i++) {
            intValue = list.get(i).toIntDefault();
            if (intValue <= 0) {
              // The dimension parameter `1` is expected to be a positive integer or a list of
              // positive integers
              IOFunctions.printMessage(ast.topHead(), "posdim", F.List(arg), engine);
              return null;
            }
            result[i - 1] = intValue;
          }
          return result;
        } catch (RuntimeException rex) {
          //
        }
      }
    }
    // The dimension parameter `1` is expected to be a positive integer or a list of positive
    // integers
    IOFunctions.printMessage(ast.topHead(), "posdim", F.List(arg), engine);
    return null;
  }
  /**
   * Check the argument, if it's a Java {@code int} value in the range [ {@code startValue},
   * Integer.MAX_VALUE]
   */
  public static int checkIntType(IAST ast, int pos, int startValue) {
    if (ast.get(pos) instanceof IntegerSym) {
      // IntegerSym always fits into an int number
      int result = ((IntegerSym) ast.get(pos)).toInt();
      if (startValue > result) {
        // Machine-sized integer expected at position `2` in `1`.
        String str = IOFunctions.getMessage("intm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    if (ast.get(pos).isReal()) {
      int result = ast.get(pos).toIntDefault();
      if (result == Integer.MIN_VALUE || startValue > result) {
        // Machine-sized integer expected at position `2` in `1`.
        String str = IOFunctions.getMessage("intm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    // Machine-sized integer expected at position `2` in `1`.
    String str = IOFunctions.getMessage("intm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  public static int checkNonNegativeIntType(IAST ast, int pos) {
    if (ast.get(pos) instanceof IntegerSym) {
      // IntegerSym always fits into an int number
      int result = ast.get(pos).toIntDefault();
      if (result == Integer.MIN_VALUE || 0 > result) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        String str = IOFunctions.getMessage("intnm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    if (ast.get(pos).isReal()) {
      int result = ast.get(pos).toIntDefault();
      if (result == Integer.MIN_VALUE || 0 > result) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        String str = IOFunctions.getMessage("intnm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    // Non-negative machine-sized integer expected at position `2` in `1`.
    String str = IOFunctions.getMessage("intnm", F.List(ast, F.ZZ(pos)), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  public static int checkPositiveIntType(IAST ast, int pos) {
    if (ast.get(pos) instanceof IntegerSym) {
      // IntegerSym always fits into an int number
      int result = ast.get(pos).toIntDefault();
      if (result == Integer.MIN_VALUE || 0 >= result) {
        // Positive machine-sized integer expected at position `2` in `1`.
        String str =
            IOFunctions.getMessage("intpm", F.List(ast.topHead(), F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    if (ast.get(pos).isReal()) {
      int result = ast.get(pos).toIntDefault();
      if (result == Integer.MIN_VALUE || 0 >= result) {
        // Positive machine-sized integer expected at position `2` in `1`.
        String str =
            IOFunctions.getMessage("intpm", F.List(ast.topHead(), F.ZZ(pos)), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    // Positive machine-sized integer expected at position `2` in `1`.
    String str =
        IOFunctions.getMessage("intpm", F.List(ast.topHead(), F.ZZ(pos)), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  /** Check the expression, if it's a Java {@code int} value in the range [0 , Integer.MAX_VALUE] */
  public static int checkIntLevelType(IExpr expr) {
    return checkIntLevelType(expr, 0);
  }

  /**
   * Check the expression, if it's a Java {@code int} value in the range [ {@code startValue},
   * Integer.MAX_VALUE]
   *
   * @param expr a signed number which will be converted to a Java <code>int</code> if possible,
   *     otherwise throw a <code>ArgumentTypeException</code> exception.
   * @throws ArgumentTypeException
   */
  public static int checkIntLevelType(IExpr expr, int startValue) {
    if (expr.isInfinity()) {
      // maximum possible level in Symja
      int result = Integer.MAX_VALUE;
      if (startValue > result) {
        // Level value greater equal `1` expected instead of `2`.
        String str =
            IOFunctions.getMessage(
                "intlevel", F.List(F.ZZ(startValue), F.CInfinity), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    if (expr.isReal()) {
      int result = expr.toIntDefault();
      if (result == Integer.MIN_VALUE || startValue > result) {
        // Level specification value greater equal `1` expected instead of `2`.
        String str =
            IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), expr), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    if (expr.isNegativeInfinity()) {
      // maximum possible level in Symja
      int result = Integer.MIN_VALUE;
      if (startValue > result) {
        // Level specification value greater equal `1` expected instead of `2`.
        String str =
            IOFunctions.getMessage(
                "intlevel", F.List(F.ZZ(startValue), F.CNInfinity), EvalEngine.get());
        throw new ArgumentTypeException(str);
      }
      return result;
    }
    // Level specification value greater equal `1` expected instead of `2`.
    String str =
        IOFunctions.getMessage("intlevel", F.List(F.ZZ(startValue), expr), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  /**
   * Check the expression, if it's a Java {@code int} value in the range [ {@code startValue},
   * Integer.MAX_VALUE]
   *
   * @param expr a signed number which will be converted to a Java <code>int</code> if possible,
   *     otherwise return <code>Integer.MIN_VALUE</code>
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
  }

  /**
   * Check the argument, if it's a Java {@code int} value in the range [ {@code startValue},
   * Integer.MAX_VALUE].
   *
   * @param expr
   * @param startValue
   * @param engine
   * @return
   * @throws ArgumentTypeException if it's not a Java int value in the range.
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
   * Check if the argument at the given position is a <code>List()</code> (i.e. <code>{...}</code>)
   * object.
   *
   * @param position the position which has to be a list.
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> if the check failed
   */
  public static IAST checkListType(IAST ast, int position, EvalEngine engine) {
    if (ast.get(position).isList()) {
      return (IAST) ast.get(position);
    }
    // List expected at position `1` in `2`.
    return IOFunctions.printMessage(ast.topHead(), "list", F.List(F.ZZ(position), ast), engine);
  }

  /**
   * Check if the argument at the given position is a <code>IStringX</code> string object.
   *
   * @param position the position which has to be a string.
   * @param engine the evaluation engine
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
   * @param position the position which has to be a string.
   * @throws ArgumentTypeException if it's not a symbol.
   */
  public static String checkContextName(IAST ast, int position) {
    if (ast.get(position).isString()) {
      IStringX strX = (IStringX) ast.get(position);
      String contextName = strX.toString();
      if (contextName.length() > 0) {
        if (contextName.charAt(contextName.length() - 1) != '`') {
          // `1` is not a valid context name.
          String str = IOFunctions.getMessage("cxt", F.List(strX), EvalEngine.get());
          throw new ArgumentTypeException(str);
        }
        return contextName;
      }
    }
    // `1` is not a valid context name.
    String str = IOFunctions.getMessage("cxt", F.List(ast.get(position)), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  /**
   * Check if the argument at the given position is a single symbol or a list of symbols.
   *
   * @param ast
   * @param position the position which has to be a symbol or list.
   * @param engine the evaluation engine
   * @return a list of symbols defined at <code>ast.get(position)</code> or otherwise <code>F.NIL
   *     </code>
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
   * Check if the argument at the given position is a list of symbols or <code>Set</code> and <code>SefDelayed</code>
   * definitions from a local variable definition.
   *
   * @param ast
   * @param position the position which has to be a list of symbols
   * @param engine the evaluation engine
   * @return a list of symbols defined at <code>ast.get(position)</code> or otherwise <code>F.NIL
   */
  public static IAST checkLocalVariableList(IAST ast, int position, EvalEngine engine) {
    if (ast.get(position).isList()) {
      IAST listOfSymbols = (IAST) ast.get(position);
      listOfSymbols = F.flattenSequence(listOfSymbols).orElse(listOfSymbols);
      for (int i = 1; i < listOfSymbols.size(); i++) {
        IExpr arg = listOfSymbols.get(i);
        if (arg.isSymbol()) {
          continue;
        }
        if (arg.isAST(S.Set, 3) || arg.isAST(S.SetDelayed, 3)) {
          if (arg.first().isSymbol()) {
            continue;
          }
        }
        // Local variable specification `1` contains `2` which is not a symbol or an assignment to
        // a symbol.
        return IOFunctions.printMessage(
            ast.topHead(), "lvsym", F.List(ast.get(position), arg), engine);
      }
      return listOfSymbols;
    }
    // Local variable specification `1` is not a List.
    return IOFunctions.printMessage(ast.topHead(), "lvlist", F.List(ast.get(position)), engine);
  }

  /**
   * Check if the argument at the given position is a single variable or a list of variables.
   *
   * @param ast
   * @param position the position which has to be a variable or list of variables.
   * @param engine engine to print a message if the expression is no variable
   * @return a list of symbols defined at <code>ast.get(position)</code> or <code>F.NIL</code>
   *     otherwise.
   */
  public static IAST checkIsVariableOrVariableList(
      IAST ast, int position, ISymbol head, EvalEngine engine) {
    IAST vars = null;
    IExpr temp = null;
    if (ast.get(position).isList()) {
      vars = (IAST) ast.get(position);
      for (int i = 1; i < vars.size(); i++) {
        temp = Validate.checkIsVariable(vars, i, head, engine);
        if (!temp.isPresent()) {
          return F.NIL;
        }
      }
      return vars;
    } else {
      temp = Validate.checkIsVariable(ast, position, head, engine);
      if (!temp.isPresent()) {
        return F.NIL;
      }
      return F.List(temp);
    }
  }

  /**
   * Check if the argument at the given position is a symbol.
   *
   * @param ast the ast which should be evaluated
   * @param position the position which has to be a symbol.
   * @param engine evaluatioin engine
   * @return <code>F.NIL</code> if the argument at the given position is not a symbol.
   */
  public static IExpr checkSymbolType(IAST ast, int position, EvalEngine engine) {
    if (ast.get(position).isSymbol() && ast.get(position).isVariable()) {
      return ast.get(position);
    }
    // Argument `1` at position `2` is expected to be a symbol.
    return IOFunctions.printMessage(
        ast.topHead(), "sym", F.List(ast.get(position), F.ZZ(position)), engine);
  }

  /**
   * Check if the argument at the given position is a variable, i.e. a symbol which doesnt't have
   * the <code>Constant</code> attribute set.
   *
   * @param position the position which has to be a variable.
   * @return <code>F.NIL</code> if the argument is not a variable
   */
  public static IExpr checkIsVariable(IAST ast, int position, EvalEngine engine) {
    return checkIsVariable(ast, position, ast.topHead(), engine);
  }

  /**
   * Check if the argument at the given position is a variable, i.e. a symbol which doesnt't have
   * the <code>Constant</code> attribute set.
   *
   * @param ast
   * @param position
   * @param head
   * @param engine
   * @return
   */
  public static IExpr checkIsVariable(IAST ast, int position, ISymbol head, EvalEngine engine) {
    IExpr arg = ast.get(position);
    if (arg.isSymbol() && arg.isVariable()) {
      return arg;
    }
    // `1` is not a valid variable.
    return IOFunctions.printMessage(head, "ivar", F.List(arg), engine);
  }

  /**
   * Check if the argument is an AST, otherwise throw an <code>ArgumentTypeException</code> with
   * message &quot;Cannot assign to raw object `1`.&quot;
   *
   * @throws ArgumentTypeException if it's not an AST.
   */
  public static IAST checkASTUpRuleType(IExpr expr) {
    if (expr.isAST()) {
      return (IAST) expr;
    }
    // Cannot assign to raw object `1`.
    String str = IOFunctions.getMessage("setraw", F.List(expr), EvalEngine.get());
    throw new ArgumentTypeException(str);
  }

  /**
   * Check if the expression is an AST.
   *
   * @param ast TODO
   * @param position
   * @param engine
   * @return <code>F.NIL</code> if the expression is no <code>IAST</code> object.
   */
  public static IAST checkASTType(IAST ast, IExpr arg1, int position, EvalEngine engine) {
    if (arg1.isAST()) {
      return (IAST) arg1;
    }
    // Nonatomic expression expected.
    return IOFunctions.printMessage(ast.topHead(), "normal", F.List(F.ZZ(position), ast), engine);
  }

  /**
   * Check if the expression is an IAST or an IAssociation
   *
   * @param ast TODO
   * @param position
   * @param engine
   * @return <code>F.NIL</code> if the expression is no <code>IAST</code> object.
   */
  public static IAST checkASTOrAssociationType(
      IAST ast, IExpr arg1, int position, EvalEngine engine) {
    if (arg1.isASTOrAssociation()) {
      return (IAST) arg1;
    }
    // Nonatomic expression expected.
    return IOFunctions.printMessage(ast.topHead(), "normal", F.List(F.ZZ(position), ast), engine);
  }

  private Validate() {}

  /**
   * Check if the argument at the given <code>ast</code> position is an equation (i.e. <code>
   * Equal(a,b)</code>) or a list of equations or a boolean <code>And()</code> expression of
   * equations and return a list of expanded expressions, which should be equal to <code>0</code>.
   *
   * @param ast
   * @param position the position of the equations argument in the <code>ast</code> expression.
   * @return
   */
  public static IASTAppendable checkEquations(final IAST ast, int position) {
    IExpr expr = ast.get(position);
    int size = expr.size();
    IASTAppendable termsEqualNumberList = F.ListAlloc(size > 0 ? size : 1);
    if (expr.isList() || expr.isAnd()) {
      IAST listOrAndAST = (IAST) expr;
      for (int i = 1; i < size; i++) {
        checkEquation(listOrAndAST.get(i), termsEqualNumberList);
      }
      return termsEqualNumberList;
    }
    checkEquation(expr, termsEqualNumberList);
    return termsEqualNumberList;
  }

  /**
   * Check if the argument at the given <code>ast</code> position is an equation or inequation (i.e.
   * <code>Equal(a,b)</code>) or a list of equations or inequations or a boolean <code>And()</code>
   * expression of equations and return a list of expanded expressions, which should be equal to
   * <code>0</code>.
   *
   * @param ast
   * @param position the position of the equations argument in the <code>ast</code> expression.
   * @return
   */
  public static IASTAppendable checkEquationsAndInequations(final IAST ast, int position) {
    IExpr expr = ast.get(position);
    IAST eqns = null;
    IASTAppendable termsEqualZeroList;
    if (expr.isList() || expr.isAnd()) {

      // a list of equations or inequations or a boolean AND expression of equations
      eqns = (IAST) expr;
      termsEqualZeroList = F.ListAlloc(eqns.size());
      for (int i = 1; i < eqns.size(); i++) {
        IExpr arg = eqns.get(i);
        if (arg.isAST2()) {
          IAST eq = (IAST) arg;
          checkEquationAndInequation(eq, termsEqualZeroList);
        } else {
          // not an equation or inequation
          throw new ArgumentTypeException(
              "binary equation or inequation expression expected at position " + i);
        }
      }
    } else {
      termsEqualZeroList = F.ListAlloc();
      checkEquationAndInequation(expr, termsEqualZeroList);
    }
    return termsEqualZeroList;
  }

  private static void checkEquationAndInequation(IExpr eq, IASTAppendable termsEqualZeroList) {
    if (eq.isEqual()) {
      IAST equal = (IAST) eq;
      IExpr subtract = EvalEngine.get().evaluate(F.Subtract(equal.arg1(), equal.arg2()));
      if (subtract.isList()) {
        IAST list = (IAST) subtract;
        IASTAppendable result = F.ListAlloc(list.size());
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          termsEqualZeroList.append(F.Equal(arg.isTimes() ? arg : F.evalExpandAll(arg), F.C0));
        }
        return;
      }
      termsEqualZeroList.append(
          F.Equal(subtract.isTimes() ? subtract : F.evalExpandAll(subtract), F.C0));
      return;
    }
    if (eq.isAST2()) {
      IAST equal = (IAST) eq;
      IExpr head = equal.head();
      if (head.equals(S.Equal)
          || head.equals(S.Unequal)
          || head.equals(S.Greater)
          || head.equals(S.GreaterEqual)
          || head.equals(S.Less)
          || head.equals(S.LessEqual)) {
        final IExpr[] arr =
            new IExpr[] {
              F.expandAll(equal.arg1(), true, true), F.expandAll(equal.arg2(), true, true)
            };
        termsEqualZeroList.append(F.ast(arr, head));
        return;
      }
    } else if (eq.isTrue()) {
      termsEqualZeroList.append(S.True);
      return;
    } else if (eq.isFalse()) {
      termsEqualZeroList.append(S.False);
      return;
    }
    // not an equation or inequation
    throw new ArgumentTypeException(
        "binary equation or inequation expression expected instead of " + eq.toString());
  }

  /**
   * Check if the given expression is an equation (i.e. <code>Equal(a,b)</code>)
   *
   * @param expr the expression which should be an equation
   */
  private static void checkEquation(IExpr expr, IASTAppendable termsEqualNumberList) {
    if (expr.isASTSizeGE(S.Equal, 3)) {
      IAST equal = (IAST) expr;
      IExpr last = equal.last();
      for (int i = 1; i < equal.size() - 1; i++) {
        IExpr temp = F.evalExpandAll(F.Subtract(equal.get(i), last));
        termsEqualNumberList.append(temp);
      }

    } else if (expr.isTrue()) {
      termsEqualNumberList.append(S.True);
      // return S.True;
    } else if (expr.isFalse()) {
      termsEqualNumberList.append(S.False);
      // return S.False;
    } else {
      // not an equation
      throw new ArgumentTypeException(
          "Equal[] expression (a==b) expected instead of " + expr.toString());
    }
  }

  public static void printException(final Appendable buf, final Throwable e) {
    if (Config.SHOW_STACKTRACE) {
      e.printStackTrace();
    }
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

  /**
   * Test if a non-negative integer or Infinity is defined in <code>upToAST.arg1()</code>.
   *
   * @param upToAST
   * @param engine
   * @return
   */
  public static int checkUpTo(IAST upToAST, EvalEngine engine) {
    int upTo = Integer.MIN_VALUE;
    if (upToAST.arg1().isInfinity()) {
      upTo = Integer.MAX_VALUE;
    } else {
      upTo = upToAST.arg1().toIntDefault();
    }
    if (upTo < 0) {
      // Non-negative integer or Infinity expected at position `1` in `2`.
      IOFunctions.printMessage(S.UpTo, "innf", F.List(F.C1, upToAST), engine);
      return Integer.MIN_VALUE;
    }
    return upTo;
  }

  /**
   * Test if <code>expr</code> is a symbol or a string which can be converted into a symbol.
   *
   * @param expr
   * @param ast
   * @param engine
   * @return {@link F#NIL} if <code>expr</code> cannot be converted into a symbol
   * @deprecated use {@link #checkIdentifierHoldPattern(IExpr, IAST, EvalEngine)})
   */
  @Deprecated
  public static IExpr checkIdentifier(final IExpr expr, IAST ast, EvalEngine engine) {
    ISymbol sym = null;
    if (expr.isString()) {
      String identifier = expr.toString();
      if (!Scanner.isIdentifier(identifier)) {
        // Argument `1` at position `2` is expected to be a symbol.
        return IOFunctions.printMessage(ast.topHead(), "sym", F.List(expr, F.C1), engine);
      }
      sym = F.symbol(identifier, engine);
    } else if (expr.isSymbol()) {
      sym = (ISymbol) expr;
    } else {
      // Argument `1` at position `2` is expected to be a symbol.
      return IOFunctions.printMessage(ast.topHead(), "sym", F.List(expr, F.C1), engine);
    }
    return sym;
  }

  public static IExpr checkIdentifierHoldPattern(final IExpr expr, IAST ast, EvalEngine engine) {
    ISymbol sym = null;
    if (expr.isString()) {
      String identifier = expr.toString();
      if (!Scanner.isIdentifier(identifier)) {
        // Argument `1` at position `2` is expected to be a symbol.
        return IOFunctions.printMessage(ast.topHead(), "sym", F.List(expr, F.C1), engine);
      }
      int indx = identifier.lastIndexOf('`');
      if (indx > 0) {
        ContextPath contextPath = engine.getContextPath();
        Context context = contextPath.getContext(identifier.substring(0, indx + 1));
        sym = contextPath.symbol(identifier.substring(indx + 1), context, engine.isRelaxedSyntax());
      } else {
        sym = F.symbol(identifier, engine);
      }
    } else if (expr.isSymbol()) {
      sym = (ISymbol) expr;
    } else if (expr.isAST(S.HoldPattern, 2) && expr.first().isSymbol()) {
      sym = (ISymbol) expr.first();
    } else {
      // Symbol, string or HoldPattern(symbol) expected at position `2` in `1`.
      return IOFunctions.printMessage(ast.topHead(), "ssle", F.List(expr, F.C1), engine);
    }
    return sym;
  }

  /**
   * Test if <code>expr</code> is a symbol or a string which can be converted into a MessageName
   * tag.
   *
   * @param expr
   * @param ast
   * @param engine
   * @return <code>null</code> if <code>expr</code> cannot be converted into a symbol
   */
  public static String checkMessageNameTag(final IExpr expr, IAST ast, EvalEngine engine) {
    if (expr.isString() || expr.isSymbol()) {
      return expr.toString();
    } else {
      // Argument `1` at position `2` is expected to be a symbol.
      IOFunctions.printMessage(ast.topHead(), "sym", F.List(expr, F.C1), engine);
    }
    return null;
  }
}
