package org.matheclipse.core.eval;

import java.io.ByteArrayOutputStream;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.reflection.system.NIntegrate;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.math.MathException;

public class MathUtils {
  private static Double[][] rom;

  private static double romberg(int i, int j) {
    if (j == 0) {
      return rom[i][0];
    } else if (rom[i][j] != null) {
      return rom[i][j];
    } else {
      double temp =
          (Math.pow(4, j) * romberg(i, j - 1) - romberg(i - 1, j - 1)) / (Math.pow(4, j) - 1);
      rom[i][j] = temp;
      return temp;
    }
  }

  public static double arcLength(String f, String v, String a, String b) throws MathException {
    ASTNode fun;
    String var = v;
    EvalDouble parser = new EvalDouble(true);
    parser.defineVariable(var);
    try {
      fun = parser.parse(f);
    } catch (MathException e) {
      throw e;
    }
    String integrand = "sqrt(1+(" + parser.derivative(fun, var) + ")^2)";
    return integrate(integrand, v, a, b);
  }

  /**
   * Integrate a function numerically with the LegendreGauss method.
   *
   * @param fun the function which should be integrated
   * @param v the variable
   * @param aS lower bound double value string for integration
   * @param bS upper bound double value string for integration
   * @return
   * @throws MathException
   */
  public static double integrate(String fun, String v, String aS, String bS) throws MathException {
    return integrate("LegendreGauss", fun, v, aS, bS);
  }

  /**
   * Integrate a function numerically.
   *
   * @param method the following methods are possible: LegendreGauss, Simpson, Romberg, Trapezoid
   * @param fun the function which should be integrated
   * @param v the variable
   * @param aS lower bound double value string for integration
   * @param bS upper bound double value string for integration
   * @return
   * @throws MathException
   */
  public static double integrate(String method, String fun, String v, String aS, String bS)
      throws MathException {
    ExprEvaluator parser = new ExprEvaluator();
    double a, b;
    try {
      a = parser.evalf(aS);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidStart) +
      // e.getMessage(), e.context);
      throw e;
    }
    try {
      // b = parser.parse(bS).getVal();
      b = parser.evalf(bS);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidEnd) +
      // e.getMessage(), e.context);
      throw e;
    }

    IExpr function = parse(fun, null);
    IExpr var = parse(v, null);
    IAST list = F.list(var, F.num(a), F.num(b));
    return NIntegrate.integrateDouble(function, var, a, b, "LegendreGauss",
        NIntegrate.DEFAULT_MAX_POINTS, NIntegrate.DEFAULT_MAX_ITERATIONS, list.rest(),
        parser.getEvalEngine());
  }

  /**
   * TODO use LegendreGauss method
   *
   * @param fun
   * @param v1
   * @param v2
   * @param bounds
   * @return
   */
  public static double integrate(String fun, String v1, String v2, String[] bounds) {
    double x1, x2;
    ASTNode y1, y2, integFun;
    String integVarX = v1;
    String integVarY = v2;
    EvalDouble integParser = new EvalDouble(true);
    integParser.defineVariable(integVarX);
    integParser.defineVariable(integVarY);
    try {
      x1 = integParser.evaluate(bounds[0]);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidStartX) +
      // e.getMessage(), e.context);
      throw e;
    }
    try {
      // x2 = integParser.parse(bounds[1]).getVal();
      x2 = integParser.evaluate(bounds[1]);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidEndX) +
      // e.getMessage(), e.context);
      throw e;
    }
    try {
      y1 = integParser.parse(bounds[2]);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidStartY) +
      // e.getMessage(), e.context);
      throw e;
    }
    try {
      y2 = integParser.parse(bounds[3]);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidEndY) +
      // e.getMessage(), e.context);
      throw e;
    }
    try {
      integFun = integParser.parse(fun);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidfunction)
      // +
      // e.getMessage(), e.context);
      throw e;
    }
    int romI = 9;
    Double[][] romX = new Double[romI][romI];
    for (int i = 0; i < romI; i++) {
      double step = (x2 - x1) / Math.pow(2, i);
      double aTemp = x1;
      rom = new Double[romI][romI];
      // integVarX.setVal(aTemp);
      integParser.defineVariable(integVarX, aTemp);
      double a = integParser.evaluateNode(y1);
      double b = integParser.evaluateNode(y2);

      for (int j = 0; j < romI; j++) {
        double stepY = (b - a) / Math.pow(2, j);
        double aTempY = a;
        // integVarY.setVal(a);
        // double valueY = integFun.getVal() / 2;
        integParser.defineVariable(integVarY, a);
        double valueY = integParser.evaluateNode(integFun) / 2;
        for (int k = 0; k < Math.pow(2, j) - 1; k++) {
          aTempY += stepY;
          // integVarY.setVal(aTempY);
          // valueY += integFun.getVal();
          integParser.defineVariable(integVarY, aTempY);
          valueY += integParser.evaluateNode(integFun);
        }
        // integVarY.setVal(b);
        // valueY += integFun.getVal() / 2;
        integParser.defineVariable(integVarY, b);
        valueY += integParser.evaluateNode(integFun) / 2;
        valueY *= stepY;
        rom[j][0] = valueY;
      }
      double valueX = romberg(romI - 1, romI - 1) / 2;

      for (int l = 0; l < Math.pow(2, i) - 1; l++) {
        rom = new Double[romI][romI];
        aTemp += step;
        // integVarX.setVal(aTemp);
        // a = y1.getVal();
        // b = y2.getVal();
        integParser.defineVariable(integVarX, aTemp);
        a = integParser.evaluateNode(y1);
        b = integParser.evaluateNode(y2);
        for (int j = 0; j < romI; j++) {
          double stepY = (b - a) / Math.pow(2, j);
          double aTempY = a;
          // integVarY.setVal(a);
          // double valueY = integFun.getVal() / 2;
          integParser.defineVariable(integVarY, a);
          double valueY = integParser.evaluateNode(integFun) / 2;
          for (int k = 0; k < Math.pow(2, j) - 1; k++) {
            aTempY += stepY;
            // integVarY.setVal(aTempY);
            // valueY += integFun.getVal();
            integParser.defineVariable(integVarY, aTempY);
            valueY += integParser.evaluateNode(integFun);
          }
          // integVarY.setVal(b);
          // valueY += integFun.getVal() / 2;
          integParser.defineVariable(integVarY, b);
          valueY += integParser.evaluateNode(integFun) / 2;
          valueY *= stepY;
          rom[j][0] = valueY;
        }
        valueX += romberg(romI - 1, romI - 1);
      }

      // integVarX.setVal(x2);
      // a = y1.getVal();
      // b = y2.getVal();
      integParser.defineVariable(integVarX, x2);
      a = integParser.evaluateNode(y1);
      b = integParser.evaluateNode(y2);
      rom = new Double[romI][romI];

      for (int j = 0; j < romI; j++) {
        double stepY = (b - a) / Math.pow(2, j);
        double aTempY = a;
        // integVarY.setVal(a);
        // double valueY = integFun.getVal() / 2;
        integParser.defineVariable(integVarY, a);
        double valueY = integParser.evaluateNode(integFun) / 2;
        for (int k = 0; k < Math.pow(2, j) - 1; k++) {
          aTempY += stepY;
          // integVarY.setVal(aTempY);
          // valueY += integFun.getVal();
          integParser.defineVariable(integVarY, aTempY);
          valueY += integParser.evaluateNode(integFun);
        }
        // integVarY.setVal(b);
        // valueY += integFun.getVal() / 2;
        integParser.defineVariable(integVarY, b);
        valueY += integParser.evaluateNode(integFun) / 2;
        valueY *= stepY;
        rom[j][0] = valueY;
      }
      valueX += romberg(romI - 1, romI - 1) / 2;
      valueX *= step;
      romX[i][0] = valueX;
    }
    rom = romX;

    return romberg(romI - 1, romI - 1);
  }

  public static double getFunctionVal(String f, double s) {
    EvalDouble dEval = new EvalDouble(true);
    return dEval.evaluate(f);
  }

  public static double getFunctionVal(String f, String v, String x) {
    EvalDouble parser = new EvalDouble(true);
    String var = v;
    ASTNode fun, val;
    parser.defineVariable(var);
    try {
      fun = parser.parse(f);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidfunction)
      // + ": "
      // + e.getMessage(), e.context);
      throw e;
    }
    try {
      val = parser.parse(x);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidNumber) +
      // ": " +
      // e.getMessage(), e.context);
      throw e;
    }
    // var.setVal(val.getVal());
    parser.defineVariable(var, parser.evaluateNode(val));
    return parser.evaluateNode(fun);
  }

  public static String getFunctionVal(String fun, String[] var, String resp, String[] vals)
      throws MathException {
    try {
      EvalDouble parParser = new EvalDouble(true);
      double values[] = new double[vals.length];
      for (int i = 0; i < vals.length; i++) {
        values[i] = parParser.evaluate(vals[i]);
      }
      String respVar = null;
      for (int i = 0; i < var.length; i++) {
        if (var[i].equals(resp)) {
          respVar = resp;
          // parParser.add(respVar);
          // respVar.setVal(values[i]);
          parParser.defineVariable(respVar, values[i]);
        } else {
          String temp = var[i];
          parParser.defineVariable(temp, values[i]);
        }
      }
      if (respVar != null) {
        try {
          ASTNode f = parParser.parse(fun);
          return parParser.evaluateNode(f) + "";
        } catch (MathException e) {
          // throw new
          // ParseError(context.getString(R.string.invalidfunction) +
          // e.getMessage(), e.context);
          throw e;
        }
      }
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidVariable),
      // new
      // ParserContext(resp, 0, null));
      throw e;
    }
    throw new SymjaMathException("MathUtils:getFunctionVal - cannot compute function values");
  }

  public static boolean isValid(String fun, String[] var) {
    EvalDouble dEval = new EvalDouble(true);
    for (String v : var) {
      dEval.defineVariable(v, new DoubleVariable(0.0));
    }
    try {
      dEval.parse(fun);
      return true;
    } catch (Exception e) {
      Errors.rethrowsInterruptException(e);

      return false;
    }
  }

  public static boolean[] isValid(String[] fun, String[] var) {
    EvalDouble dEval = new EvalDouble(true);
    for (String v : var) {
      dEval.defineVariable(v, new DoubleVariable(0.0));
    }
    boolean[] b = new boolean[fun.length];
    for (int i = 0; i < fun.length; i++) {
      try {
        dEval.parse(fun[i]);
        b[i] = true;
      } catch (Exception e) {
        Errors.rethrowsInterruptException(e);
        b[i] = false;
      }
    }
    return b;
  }

  public static String getDerivative(String fun, String[] var, String resp) {
    IExpr sym = parse(resp, null);
    if (sym instanceof ISymbol) {
      return evaluateReaplaceAll(fun, F.D(F.Slot1, sym));
    }
    return "error in MathUtils#getDerivative()";
  }

  // public static String evaluate(final String codeString, final String
  // function) {
  // String result = null;
  // // set up and direct the input and output streams
  // try {
  // // _inputStream = inputStreamFromString(codeString);
  // ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();
  //
  // // fire up the command interpreter to evaluate the source code buffer
  // WebInterpreter _commandInterpreter = new WebInterpreter(codeString,
  // _outputStream);
  // try {
  // _commandInterpreter.eval(function);
  // // extract the resulting text output from the stream
  // result = stringFromOutputStream(_outputStream);
  // } catch (Throwable t) {
  // Log.e("MathUtils",
  // String.format("evalCodeString(): UNSUPPORTED OPERATION!\n[\n%s\n]\n%s",
  // codeString, t.toString()), t);
  // result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" +
  // t.toString();
  // }
  // _outputStream.close();
  // } catch (Throwable t) {
  // Log.e("MathUtils",
  // String.format("evalCodeString(): UNSUPPORTED OPERATION!\n[\n%s\n]\n%s",
  // codeString, t.toString()), t);
  // result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" +
  // t.toString();
  // }
  //
  // return result;
  // }

  public static String getPowerSeries(String fun, String v, String cen, int iter)
      throws MathException {
    IExpr sym = parse(v, null);
    if (sym instanceof ISymbol) {
      IExpr center = parse(cen, null);
      if (center != null) {
        return evaluateReaplaceAll(fun, F.Taylor(F.Slot1, F.list(sym, center, F.ZZ(iter))));
      }
    }
    return "error in MathUtils#getPowerSeries()";
  }

  // (y-y0)=m(x-x0)
  public static String tangentLine(String f, String x, String v) throws MathException {
    ASTNode fun;
    String var = v;
    EvalDouble parser = new EvalDouble(true);
    parser.defineVariable(var);
    try {
      fun = parser.parse(f);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidfunction)
      // + ": "
      // + e.getMessage(), e.context);
      throw e;
    }
    try {
      parser.defineVariable(var, parser.evaluateNode(parser.parse(x)));
      try {
        double m = parser.evaluateNode(parser.derivative(fun, var));
        String out;
        if (m == 1) {
          out = var;
        } else if (m == -1) {
          out = "-" + var;
        } else if (m == 0) {
          out = "";
        } else {
          out = m + "*" + var;
        }
        double b = parser.evaluateNode(fun) - m * parser.evaluate(var);
        if (b > 0) {
          if (!out.equals("")) {
            out += "+" + b;
          } else {
            out += b;
          }
        } else if (b < 0) {
          b *= -1;
          out += "-" + b;
        }
        if (out.equals("")) {
          out = "0";
        }
        return out;
      } catch (MathException e) {
        // throw new
        // ParseError(context.getString(R.string.invalidfunction),
        // e.context);
        throw e;
      }
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidNumber),
      // e.context);
      throw e;
    }
  }

  public static double surfaceArea(String fun, String v1, String v2, String[] bounds)
      throws MathException {
    String integVarX = v1;
    String integVarY = v2;
    EvalDouble integParser = new EvalDouble(true);
    integParser.defineVariable(integVarX);
    integParser.defineVariable(integVarY);
    try {
      // String funX =
      // integParser.parse(fun).derivative(integVarX).toString();
      // String funY =
      // integParser.parse(fun).derivative(integVarY).toString();
      String funX = integParser.derivative(integParser.parse(fun), integVarX).toString();
      String funY = integParser.derivative(integParser.parse(fun), integVarY).toString();
      fun = "sqrt(1+(" + funX + ")^2+(" + funY + ")^2)";
      return integrate(fun, v1, v2, bounds);
    } catch (MathException e) {
      // throw new ParseError(context.getString(R.string.invalidfunction)
      // +
      // e.getMessage(), e.context);
      throw e;
    }
  }

  /**
   * Evaluate the expression in <code>codeString</code>.
   *
   * @param function <code>null</code> if you like to evaluate in symbolic mode; &quot;N&quot; if
   *        you like to evaluate in numeric mode
   * @return
   */
  public static String evaluate(final String codeString, final String function) {
    String result = null;
    // set up and direct the input and output streams
    // _inputStream = inputStreamFromString(codeString);
    try (ByteArrayOutputStream _outputStream = new ByteArrayOutputStream()) {

      // fire up the command interpreter to evaluate the source code
      // buffer
      SymjaInterpreter _commandInterpreter = new SymjaInterpreter(codeString, _outputStream);
      try {
        _commandInterpreter.eval(function);
        // extract the resulting text output from the stream
        result = _outputStream.toString("UTF-8");
      } catch (Throwable t) {
        result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" + t.toString();
      }
    } catch (Throwable t) {
      result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" + t.toString();
    }

    return result;
  }

  /**
   * Parse the <code>codeString</code> into an <code>IExpr</code> and if <code>function</code>
   * unequals <code>null</code>, replace all occurences of the slot <code>#</code> in the function
   * with the parsed expression. After that evaluate the given expression.
   *
   * @param function
   * @return
   */
  public static String evaluateReaplaceAll(final String codeString, final IAST function) {
    String result = null;
    // set up and direct the input and output streams
    try (ByteArrayOutputStream _outputStream = new ByteArrayOutputStream()) {

      // fire up the command interpreter to evaluate the source code
      // buffer
      SymjaInterpreter _commandInterpreter = new SymjaInterpreter(codeString, _outputStream);
      try {
        _commandInterpreter.evalReplaceAll(function);
        // extract the resulting text output from the stream
        result = _outputStream.toString("UTF-8");
      } catch (Throwable t) {
        result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" + t.toString();
      }
    } catch (Throwable t) {
      result = "UNSUPPORTED OPERATION!\n[\n" + codeString + "\n]\n" + t.toString();
    }

    return result;
  }

  /**
   * Parse the <code>codeString</code> into an <code>IExpr</code> and if <code>function</code>
   * unequals <code>null</code>, replace all occurences of symbol <code>x</code> in the function
   * with the parsed expression. After that evaluate the given expression.
   *
   * @param function
   * @return
   */
  public static IExpr parse(String evalStr, IAST function) {
    try {
      ExprParser p = new ExprParser(EvalEngine.get(), true);
      // throws MathException exception, if syntax isn't valid
      return p.parse(evalStr);

    } catch (MathException e1) {
      try {
        ExprParser p = new ExprParser(EvalEngine.get(), false);
        // throws MathException exception, if syntax isn't valid
        return p.parse(evalStr);
      } catch (Exception e2) {
        Errors.rethrowsInterruptException(e2);
        return null;
      }
    }
  }
}
