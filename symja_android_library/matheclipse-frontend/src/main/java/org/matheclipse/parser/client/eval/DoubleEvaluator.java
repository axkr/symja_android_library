/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.NumberNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.math.ArithmeticMathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/** Evaluate math expressions to <code>double</code> numbers. */
public class DoubleEvaluator {

  private static final boolean DEBUG = false;

  public static double EPSILON = 1.0e-15;

  private static Map<String, Double> SYMBOL_DOUBLE_MAP;

  private static Map<String, Boolean> SYMBOL_BOOLEAN_MAP;

  private static Map<String, Object> FUNCTION_DOUBLE_MAP;

  private static Map<String, Object> FUNCTION_BOOLEAN_MAP;

  private IDoubleCallbackFunction fCallbackFunction = null;

  /**
   * A callback function for unknown function names.
   *
   * @return the callback function
   */
  public IDoubleCallbackFunction getCallbackFunction() {
    return fCallbackFunction;
  }

  /**
   * Set a callback function for unknown function names.
   *
   * @param callbackFunction the callback function to set
   */
  public void setCallbackFunction(IDoubleCallbackFunction callbackFunction) {
    fCallbackFunction = callbackFunction;
  }

  static class ArcTanFunction implements DoubleUnaryOperator, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1) {
      return Math.atan(arg1);
    }

    @Override
    public double applyAsDouble(double x, double y) {
      // the first argument of atan2 is y and the second is x.
      return Math.atan2(y, x);
    }
  }

  static class LogFunction implements DoubleUnaryOperator, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1) {
      return Math.log(arg1);
    }

    @Override
    public double applyAsDouble(double base, double z) {
      return Math.log(z) / Math.log(base);
    }
  }

  static class CompoundExpressionFunction implements IDoubleFunction {
    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      double result = Double.NaN;
      int end = function.size();
      for (int i = 1; i < end; i++) {
        result = engine.evaluateNode(function.getNode(i));
        if (DEBUG) {
          System.out.println(result);
        }
      }
      return result;
    }
  }

  static class SetFunction implements IDoubleFunction {
    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      if (function.size() != 3) {
        throw new ArithmeticMathException(
            "SetFunction#evaluate(DoubleEvaluator,FunctionNode) needs 2 arguments: "
                + function.toString());
      }
      if (!(function.getNode(1) instanceof SymbolNode)) {
        throw new ArithmeticMathException(
            "SetFunction#evaluate(DoubleEvaluator,FunctionNode) symbol required on the left hand side: "
                + function.toString());
      }
      String variableName = ((SymbolNode) function.getNode(1)).getString();
      double result = engine.evaluateNode(function.getNode(2));
      IDoubleValue dv = engine.getVariable(variableName);
      if (dv == null) {
        dv = new DoubleVariable(result);
      } else {
        dv.setValue(result);
      }
      engine.defineVariable(variableName, dv);
      return result;
    }
  }

  static class MaxFunction implements IDoubleFunction, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1, double arg2) {
      return Math.max(arg1, arg2);
    }

    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      double result = Double.NaN;
      double temp;
      int end = function.size();
      if (end > 1) {
        result = engine.evaluateNode(function.getNode(1));
        for (int i = 2; i < end; i++) {
          temp = Math.max(result, engine.evaluateNode(function.getNode(i)));
          if (temp > result) {
            result = temp;
          }
        }
      }
      return result;
    }
  }

  static class MinFunction implements IDoubleFunction, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1, double arg2) {
      return Math.min(arg1, arg2);
    }

    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      double result = Double.NaN;
      double temp;
      int end = function.size();
      if (end > 1) {
        result = engine.evaluateNode(function.getNode(1));
        for (int i = 2; i < end; i++) {
          temp = Math.min(result, engine.evaluateNode(function.getNode(i)));
          if (temp < result) {
            result = temp;
          }
        }
      }
      return result;
    }
  }

  static class PlusFunction implements IDoubleFunction, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1, double arg2) {
      return arg1 + arg2;
    }

    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      double result = 0.0;
      for (int i = 1; i < function.size(); i++) {
        result += engine.evaluateNode(function.getNode(i));
      }
      return result;
    }
  }

  static class TimesFunction implements IDoubleFunction, DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double arg1, double arg2) {
      return arg1 * arg2;
    }

    @Override
    public double evaluate(DoubleEvaluator engine, FunctionNode function) {
      double result = 1.0;
      for (int i = 1; i < function.size(); i++) {
        result *= engine.evaluateNode(function.getNode(i));
      }
      return result;
    }
  }

  static {
    SYMBOL_DOUBLE_MAP = new ConcurrentHashMap<String, Double>();
    SYMBOL_DOUBLE_MAP.put(
        "Catalan", Double.valueOf(0.91596559417721901505460351493238411077414937428167));
    SYMBOL_DOUBLE_MAP.put("Degree", Double.valueOf(Math.PI / 180));
    SYMBOL_DOUBLE_MAP.put("E", Double.valueOf(Math.E));
    SYMBOL_DOUBLE_MAP.put("Pi", Double.valueOf(Math.PI));
    SYMBOL_DOUBLE_MAP.put(
        "EulerGamma", Double.valueOf(0.57721566490153286060651209008240243104215933593992));
    SYMBOL_DOUBLE_MAP.put(
        "Glaisher", Double.valueOf(1.2824271291006226368753425688697917277676889273250));
    SYMBOL_DOUBLE_MAP.put(
        "GoldenRatio", Double.valueOf(1.6180339887498948482045868343656381177203091798058));
    SYMBOL_DOUBLE_MAP.put(
        "Khinchin", Double.valueOf(2.6854520010653064453097148354817956938203822939945));

    SYMBOL_BOOLEAN_MAP = new ConcurrentHashMap<String, Boolean>();
    SYMBOL_BOOLEAN_MAP.put("False", Boolean.FALSE);
    SYMBOL_BOOLEAN_MAP.put("True", Boolean.TRUE);

    FUNCTION_BOOLEAN_MAP = new ConcurrentHashMap<String, Object>();

    FUNCTION_BOOLEAN_MAP.put(
        "And",
        new IBooleanBoolean2Function() {
          @Override
          public boolean evaluate(boolean arg1, boolean arg2) {
            return arg1 && arg2;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "Not",
        new IBooleanBoolean1Function() {
          @Override
          public boolean evaluate(boolean arg1) {
            return !arg1;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "Or",
        new IBooleanBoolean2Function() {
          @Override
          public boolean evaluate(boolean arg1, boolean arg2) {
            return arg1 || arg2;
          }
        });

    FUNCTION_BOOLEAN_MAP.put(
        "Equal",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return Math.abs(arg1 - arg2) < EPSILON;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "Greater",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return arg1 > arg2;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "GreaterEqual",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return arg1 >= arg2;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "Less",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return arg1 < arg2;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "LessEqual",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return arg1 <= arg2;
          }
        });
    FUNCTION_BOOLEAN_MAP.put(
        "Unequal",
        new IBooleanDouble2Function() {
          @Override
          public boolean evaluate(double arg1, double arg2) {
            return !(Math.abs(arg1 - arg2) < EPSILON);
          }
        });

    FUNCTION_DOUBLE_MAP = new ConcurrentHashMap<String, Object>();
    FUNCTION_DOUBLE_MAP.put("ArcTan", new ArcTanFunction());
    FUNCTION_DOUBLE_MAP.put("CompoundExpression", new CompoundExpressionFunction());
    FUNCTION_DOUBLE_MAP.put("Set", new SetFunction());
    FUNCTION_DOUBLE_MAP.put("Log", new LogFunction());
    FUNCTION_DOUBLE_MAP.put("Max", new MaxFunction());
    FUNCTION_DOUBLE_MAP.put("Min", new MinFunction());
    FUNCTION_DOUBLE_MAP.put("Plus", new PlusFunction());
    FUNCTION_DOUBLE_MAP.put("Times", new TimesFunction());
    //
    // Functions with 0 argument
    //
    FUNCTION_DOUBLE_MAP.put(
        "Random",
        new DoubleSupplier() {
          @Override
          public double getAsDouble() {
            return ThreadLocalRandom.current().nextDouble();
          }
        });
    //
    // Functions with 1 argument
    //
    FUNCTION_DOUBLE_MAP.put(
        "Abs",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.abs(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "ArcCos",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.acos(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "ArcSin",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.asin(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Ceiling",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.ceil(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Cos",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.cos(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Cosh",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.cosh(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Exp",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.exp(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Floor",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.floor(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Round",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.round(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Sign",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.signum(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Sin",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.sin(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Sinh",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.sinh(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Sqrt",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.sqrt(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Tan",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.tan(arg1);
          }
        });
    FUNCTION_DOUBLE_MAP.put(
        "Tanh",
        new DoubleUnaryOperator() {
          @Override
          public double applyAsDouble(double arg1) {
            return Math.tanh(arg1);
          }
        });

    //
    // Functions with 2 arguments
    //
    FUNCTION_DOUBLE_MAP.put(
        "Power",
        new DoubleBinaryOperator() {
          @Override
          public double applyAsDouble(double arg1, double arg2) {
            return Math.pow(arg1, arg2);
          }
        });
  }

  private Map<String, IDoubleValue> fVariableMap;

  private Map<String, BooleanVariable> fBooleanVariables;

  private ASTNode fNode;

  protected final boolean fRelaxedSyntax;

  private final ASTNodeFactory fASTFactory;

  public DoubleEvaluator() {
    this(null, false);
  }

  public DoubleEvaluator(boolean relaxedSyntax) {
    this(null, relaxedSyntax);
  }

  public DoubleEvaluator(ASTNode node, boolean relaxedSyntax) {
    fASTFactory = new ASTNodeFactory(relaxedSyntax);
    fVariableMap = new HashMap<String, IDoubleValue>();
    fBooleanVariables = new HashMap<String, BooleanVariable>();
    fNode = node;
    fRelaxedSyntax = relaxedSyntax;
    if (fRelaxedSyntax) {
      if (SYMBOL_DOUBLE_MAP.get("pi") == null) {
        // init tables for relaxed mode
        for (Map.Entry<String, Double> entry : SYMBOL_DOUBLE_MAP.entrySet()) {
          SYMBOL_DOUBLE_MAP.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        for (Map.Entry<String, Boolean> entry : SYMBOL_BOOLEAN_MAP.entrySet()) {
          SYMBOL_BOOLEAN_MAP.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : FUNCTION_DOUBLE_MAP.entrySet()) {
          FUNCTION_DOUBLE_MAP.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : FUNCTION_BOOLEAN_MAP.entrySet()) {
          FUNCTION_BOOLEAN_MAP.put(entry.getKey().toLowerCase(), entry.getValue());
        }
      }
    }
  }

  /**
   * Parse the given <code>expression String</code> and store the resulting ASTNode in this
   * DoubleEvaluator
   *
   * @param expression
   * @return
   * @throws SyntaxError
   */
  public ASTNode parse(String expression) {
    Parser p;
    if (fRelaxedSyntax) {
      p = new Parser(ASTNodeFactory.RELAXED_STYLE_FACTORY, true);
    } else {
      p = new Parser(ASTNodeFactory.MMA_STYLE_FACTORY, false);
    }
    fNode = p.parse(expression);
    if (fNode instanceof FunctionNode) {
      fNode = optimizeFunction((FunctionNode) fNode);
    }
    return fNode;
  }

  /**
   * Parse the given <code>expression String</code> and return the resulting ASTNode
   *
   * @param expression
   * @return
   * @throws SyntaxError
   */
  public static ASTNode parseNode(String expression, boolean relaxedSyntax) {
    DoubleEvaluator doubleEvaluator = new DoubleEvaluator(relaxedSyntax);
    return doubleEvaluator.parse(expression);
  }

  /**
   * Parse the given <code>expression String</code> and evaluate it to a double value
   *
   * @param expression
   * @return
   * @throws SyntaxError
   */
  public double evaluate(String expression) {
    Parser p;
    if (fRelaxedSyntax) {
      p = new Parser(ASTNodeFactory.RELAXED_STYLE_FACTORY, true);
    } else {
      p = new Parser(ASTNodeFactory.MMA_STYLE_FACTORY, false);
    }
    fNode = p.parse(expression);
    if (fNode instanceof FunctionNode) {
      fNode = optimizeFunction((FunctionNode) fNode);
    }
    return evaluateNode(fNode);
  }

  /**
   * Reevaluate the <code>expression</code> (possibly after a new Variable assignment)
   *
   * @return
   * @throws SyntaxError
   */
  public double evaluate() {
    if (fNode == null) {
      throw new SyntaxError(0, 0, 0, " ", "No parser input defined", 1);
    }
    return evaluateNode(fNode);
  }

  /**
   * Evaluate an already parsed in abstract syntax tree node into a <code>double</code> number
   * value.
   *
   * @param node abstract syntax tree node
   * @return the evaluated double number
   * @throws ArithmeticMathException if the <code>node</code> cannot be evaluated.
   */
  public double evaluateNode(final ASTNode node) {
    if (node instanceof DoubleNode) {
      return ((DoubleNode) node).doubleValue();
    }
    if (node instanceof FunctionNode) {
      return evaluateFunction((FunctionNode) node);
    }
    if (node instanceof SymbolNode) {
      IDoubleValue v = fVariableMap.get(node.toString());
      if (v != null) {
        return v.getValue();
      }
      Double dbl = SYMBOL_DOUBLE_MAP.get(node.toString());
      if (dbl != null) {
        return dbl.doubleValue();
      }
    } else if (node instanceof NumberNode) {
      return ((NumberNode) node).doubleValue();
    }

    throw new ArithmeticMathException(
        "EvalDouble#evaluate(ASTNode) not possible for: " + node.toString());
  }

  /**
   * Evaluate an already parsed in <code>FunctionNode</code> into a <code>souble</code> number
   * value.
   *
   * @param functionNode
   * @return
   * @throws ArithmeticMathException if the <code>functionNode</code> cannot be evaluated.
   */
  public double evaluateFunction(final FunctionNode functionNode) {
    if (functionNode.size() > 0) {
      if (functionNode.getNode(0) instanceof SymbolNode) {
        String symbol = functionNode.getNode(0).toString();
        if (symbol.equals("If") || (fRelaxedSyntax && symbol.equalsIgnoreCase("if"))) {
          if (functionNode.size() == 3) {
            if (evaluateNodeLogical(functionNode.getNode(1))) {
              return evaluateNode(functionNode.getNode(2));
            }
          } else if (functionNode.size() == 4) {
            if (evaluateNodeLogical(functionNode.getNode(1))) {
              return evaluateNode(functionNode.getNode(2));
            } else {
              return evaluateNode(functionNode.getNode(3));
            }
          }
        } else {
          Object obj = FUNCTION_DOUBLE_MAP.get(symbol);
          if (obj instanceof IDoubleFunction) {
            return ((IDoubleFunction) obj).evaluate(this, functionNode);
          }
          if (functionNode.size() == 1) {
            if (obj instanceof DoubleSupplier) {
              return ((DoubleSupplier) obj).getAsDouble();
            }
          } else if (functionNode.size() == 2) {
            if (obj instanceof DoubleUnaryOperator) {
              return ((DoubleUnaryOperator) obj)
                  .applyAsDouble(evaluateNode(functionNode.getNode(1)));
            }
          } else if (functionNode.size() == 3) {
            if (obj instanceof DoubleBinaryOperator) {
              return ((DoubleBinaryOperator) obj)
                  .applyAsDouble(
                      evaluateNode(functionNode.getNode(1)), evaluateNode(functionNode.getNode(2)));
            }
          }
          if (fCallbackFunction != null) {
            double doubleArgs[] = new double[functionNode.size() - 1];
            for (int i = 0; i < doubleArgs.length; i++) {
              doubleArgs[i] = evaluateNode(functionNode.getNode(i + 1));
            }
            return fCallbackFunction.evaluate(this, functionNode, doubleArgs);
          }
        }
      } else if (functionNode.getNode(0) instanceof FunctionNode) {
        FunctionNode function = (FunctionNode) functionNode.getNode(0);
        if (fCallbackFunction != null) {
          double doubleArgs[] = new double[functionNode.size() - 1];
          for (int i = 0; i < doubleArgs.length; i++) {
            doubleArgs[i] = evaluateNode(functionNode.getNode(i + 1));
          }
          return fCallbackFunction.evaluate(this, functionNode, doubleArgs);
        }
      }
    }
    throw new ArithmeticMathException(
        "EvalDouble#evaluateFunction(FunctionNode) not possible for: " + functionNode.toString());
  }

  /**
   * Check if the given symbol is a <code>SymbolNode</code> and test if the names are equal.
   *
   * @param symbol1
   * @param symbol2Name
   * @return
   */
  public boolean isSymbol(SymbolNode symbol1, String symbol2Name) {
    if (fRelaxedSyntax) {
      return symbol1.getString().equalsIgnoreCase(symbol2Name);
    }
    return symbol1.getString().equals(symbol2Name);
  }

  /**
   * Check if the given symbol is a <code>SymbolNode</code> and test if the names are equal.
   *
   * @param symbol1
   * @param symbol2
   * @return
   */
  public boolean isSymbol(SymbolNode symbol1, SymbolNode symbol2) {
    if (fRelaxedSyntax) {
      return symbol1.getString().equalsIgnoreCase(symbol2.getString());
    }
    return symbol1.equals(symbol2);
  }

  /**
   * @param node
   * @param var
   * @return
   */
  public ASTNode derivative(final ASTNode node, String var) {
    SymbolNode sym = fASTFactory.createSymbol(var);
    return derivative(node, sym);
  }

  /**
   * TODO: add more derivation rules
   *
   * @param node
   * @param var
   * @return
   */
  public ASTNode derivative(final ASTNode node, SymbolNode var) {
    if (node.isFree(var)) {
      return new DoubleNode(0.0);
    }
    if (node instanceof FunctionNode) {
      FunctionNode f = (FunctionNode) node;
      if (f.size() > 1 && f.getNode(0) instanceof SymbolNode) {
        SymbolNode head = (SymbolNode) f.getNode(0);
        if (f.size() == 2) {
          ASTNode arg1Derived = derivative(f.getNode(1), var);
          if (isSymbol(head, "Exp")) {
            FunctionNode fun = new FunctionNode(fASTFactory.createSymbol("Exp"));
            fun.add(f.getNode(1));
            return getDerivativeResult(arg1Derived, fun);
          }
          if (isSymbol(head, "Cos")) {
            FunctionNode fun = new FunctionNode(fASTFactory.createSymbol("Times"));
            fun.add(new DoubleNode(-1.0));
            fun.add(new FunctionNode(fASTFactory.createSymbol("Cos"), f.getNode(1)));
            return getDerivativeResult(arg1Derived, fun);
          }
          if (isSymbol(head, "Sin")) {
            FunctionNode fun = new FunctionNode(fASTFactory.createSymbol("Cos"));
            fun.add(f.getNode(1));
            return getDerivativeResult(arg1Derived, fun);
          }
        } else if (f.size() == 3 && isSymbol(head, "Power")) {
          if (f.get(2).isFree(var)) { // derive x^r
            ASTNode arg1Derived = derivative(f.getNode(1), var);
            // (r-1)
            FunctionNode exponent =
                fASTFactory.createFunction(
                    fASTFactory.createSymbol("Plus"), new DoubleNode(-1.0), f.get(2));
            // r*x^(r-1)
            FunctionNode fun =
                fASTFactory.createFunction(
                    fASTFactory.createSymbol("Times"),
                    f.get(2),
                    fASTFactory.createFunction(
                        fASTFactory.createSymbol("Power"), f.get(1), exponent));
            return getDerivativeResult(arg1Derived, fun);
          }
          if (f.get(1).isFree(var)) { // derive a^x
            ASTNode arg2Derived = derivative(f.getNode(2), var);
            // log(a) * a^x
            FunctionNode fun =
                fASTFactory.createFunction(
                    fASTFactory.createSymbol("Times"),
                    fASTFactory.createFunction(fASTFactory.createSymbol("Log"), f.get(1)),
                    f);
            return getDerivativeResult(arg2Derived, fun);
          }
        } else {
          if (isSymbol(head, "Plus")) {
            FunctionNode result = new FunctionNode(f.getNode(0));
            for (int i = 1; i < f.size(); i++) {
              ASTNode deriv = derivative(f.getNode(i), var);
              if (!deriv.equals(new DoubleNode(0.0))) {
                result.add(deriv);
              }
            }
            return result;
          }
          if (isSymbol(head, "Times")) {
            FunctionNode plusResult = new FunctionNode(fASTFactory.createSymbol("Plus"));
            for (int i = 1; i < f.size(); i++) {
              FunctionNode timesResult = new FunctionNode(f.getNode(0));
              boolean valid = true;
              for (int j = 1; j < f.size(); j++) {
                if (j == i) {
                  ASTNode deriv = derivative(f.getNode(j), var);
                  if (deriv.equals(new DoubleNode(0.0))) {
                    valid = false;
                  } else {
                    timesResult.add(deriv);
                  }
                } else {
                  timesResult.add(f.getNode(j));
                }
              }
              if (valid) {
                plusResult.add(timesResult);
              }
            }
            return plusResult;
          }
        }
      }
      return new FunctionNode(new SymbolNode("D"), node, var);
      // return evaluateFunction((FunctionNode) node);
    }
    if (node instanceof SymbolNode) {
      if (isSymbol((SymbolNode) node, var)) {
        return new DoubleNode(1.0);
      }
      IDoubleValue v = fVariableMap.get(node.toString());
      if (v != null) {
        return new DoubleNode(0.0);
      }
      Double dbl = SYMBOL_DOUBLE_MAP.get(node.toString());
      if (dbl != null) {
        return new DoubleNode(0.0);
      }
      return new DoubleNode(0.0);
    } else if (node instanceof NumberNode) {
      return new DoubleNode(0.0);
    }

    throw new ArithmeticMathException(
        "EvalDouble#evaluate(ASTNode) not possible for: " + node.toString());
  }

  private ASTNode getDerivativeResult(ASTNode arg1Derived, FunctionNode fun) {
    if (!arg1Derived.equals(new DoubleNode(1.0))) {
      FunctionNode res = new FunctionNode(fASTFactory.createSymbol("Times"));
      res.add(arg1Derived);
      res.add(fun);
      return res;
    }
    return fun;
  }

  public boolean evaluateNodeLogical(final ASTNode node) {
    if (node instanceof FunctionNode) {
      return evaluateFunctionLogical((FunctionNode) node);
    }
    if (node instanceof SymbolNode) {
      BooleanVariable v = fBooleanVariables.get(node.toString());
      if (v != null) {
        return v.getValue();
      }
      Boolean boole = SYMBOL_BOOLEAN_MAP.get(node.toString());
      if (boole != null) {
        return boole.booleanValue();
      }
    }

    throw new ArithmeticMathException(
        "EvalDouble#evaluateNodeLogical(ASTNode) not possible for: " + node.toString());
  }

  public boolean evaluateFunctionLogical(final FunctionNode functionNode) {
    if (functionNode.size() > 0 && functionNode.getNode(0) instanceof SymbolNode) {
      String symbol = functionNode.getNode(0).toString();
      if (functionNode.size() == 2) {
        Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
        if (obj instanceof IBooleanBoolean1Function) {
          return ((IBooleanBoolean1Function) obj)
              .evaluate(evaluateNodeLogical(functionNode.getNode(1)));
        }
      } else if (functionNode.size() == 3) {
        Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
        if (obj instanceof IBooleanDouble2Function) {
          return ((IBooleanDouble2Function) obj)
              .evaluate(
                  evaluateNode(functionNode.getNode(1)), evaluateNode(functionNode.getNode(2)));
        } else if (obj instanceof IBooleanBoolean2Function) {
          return ((IBooleanBoolean2Function) obj)
              .evaluate(
                  evaluateNodeLogical(functionNode.getNode(1)),
                  evaluateNodeLogical(functionNode.getNode(2)));
        }
        // } else {
        // Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
        // if (obj instanceof IBooleanDoubleFunction) {
        // return ((IBooleanDoubleFunction) obj).evaluate(this,
        // functionNode);
        // }
      }
    }
    throw new ArithmeticMathException(
        "EvalDouble#evaluateFunctionLogical(FunctionNode) not possible for: "
            + functionNode.toString());
  }

  /**
   * Optimize an already parsed in <code>functionNode</code> into an <code>ASTNode</code>.
   *
   * @param functionNode
   * @return
   */
  public ASTNode optimizeFunction(final FunctionNode functionNode) {
    if (functionNode.size() > 0) {
      boolean doubleOnly = true;
      ASTNode node;
      for (int i = 1; i < functionNode.size(); i++) {
        node = functionNode.getNode(i);
        if (node instanceof NumberNode) {
          functionNode.set(i, new DoubleNode(((NumberNode) functionNode.getNode(i)).doubleValue()));
        } else if (functionNode.getNode(i) instanceof FunctionNode) {
          ASTNode optNode = optimizeFunction((FunctionNode) functionNode.getNode(i));
          if (!(optNode instanceof DoubleNode)) {
            doubleOnly = false;
          }
          functionNode.set(i, optNode);
        } else if (node instanceof SymbolNode) {
          Double dbl = SYMBOL_DOUBLE_MAP.get(node.toString());
          if (dbl != null) {
            functionNode.set(i, new DoubleNode(dbl.doubleValue()));
          } else {
            doubleOnly = false;
          }
        } else {
          doubleOnly = false;
        }
      }
      if (doubleOnly) {
        try {
          return new DoubleNode(evaluateFunction(functionNode));
        } catch (Exception e) {

        }
      }
    }
    return functionNode;
  }

  /**
   * Define a value for a given variable name.
   *
   * @param variableName
   * @param value
   */
  public void defineVariable(String variableName, double value) {
    if (fRelaxedSyntax) {
      fVariableMap.put(variableName.toLowerCase(), new DoubleVariable(value));
    } else {
      fVariableMap.put(variableName, new DoubleVariable(value));
    }
  }

  /**
   * Define a value for a given variable name.
   *
   * @param variableName
   * @param value
   */
  public void defineVariable(String variableName, IDoubleValue value) {
    if (fRelaxedSyntax) {
      fVariableMap.put(variableName.toLowerCase(), value);
    } else {
      fVariableMap.put(variableName, value);
    }
  }

  /**
   * Define a value for a given variable name.
   *
   * @param variableName
   */
  public void defineVariable(String variableName) {
    if (fRelaxedSyntax) {
      fVariableMap.put(variableName.toLowerCase(), new DoubleVariable(0.0));
    } else {
      fVariableMap.put(variableName, new DoubleVariable(0.0));
    }
  }

  /**
   * Returns the double variable value to which the specified variableName is mapped, or {@code
   * null} if this map contains no mapping for the variableName.
   *
   * @param variableName
   * @return
   */
  public IDoubleValue getVariable(String variableName) {
    if (fRelaxedSyntax) {
      return fVariableMap.get(variableName.toLowerCase());
    } else {
      return fVariableMap.get(variableName);
    }
  }

  /**
   * Define a boolean value for a given variable name.
   *
   * @param variableName
   * @param value
   */
  public void defineVariable(String variableName, BooleanVariable value) {
    if (fRelaxedSyntax) {
      fBooleanVariables.put(variableName.toLowerCase(), value);
    } else {
      fBooleanVariables.put(variableName, value);
    }
  }

  /** Clear all defined variables for this evaluator. */
  public void clearVariables() {
    fVariableMap.clear();
    fBooleanVariables.clear();
  }

  /**
   * Get the variable names from the given expression.
   *
   * @param expression
   * @param result a set which contains the variable names
   */
  public static void getVariables(String expression, Set<String> result) {
    getVariables(expression, result, true);
  }

  /**
   * Get the variable names from the given expression.
   *
   * @param expression
   * @param result a set which contains the variable names
   * @param relaxedSyntax if <code>true</code> us e function syntax like <code>sin(x)</code>
   *     otherwise use <code>Sin[x]</code>.
   */
  public static void getVariables(String expression, Set<String> result, boolean relaxedSyntax) {
    Parser p =
        new Parser(
            relaxedSyntax ? ASTNodeFactory.RELAXED_STYLE_FACTORY : ASTNodeFactory.MMA_STYLE_FACTORY,
            relaxedSyntax);
    ASTNode node = p.parse(expression);
    getVariables(node, result);
  }

  /**
   * Get the variable names from the given AST node.
   *
   * @param node an already parsed AST node
   * @param result a set which contains the variable names
   */
  public static void getVariables(final ASTNode node, Set<String> result) {
    if (node instanceof FunctionNode) {
      FunctionNode functionNode = (FunctionNode) node;
      if (functionNode.size() > 0 && functionNode.getNode(0) instanceof SymbolNode) {
        for (int i = 1; i < functionNode.size(); i++) {
          getVariables(functionNode.getNode(i), result);
        }
      }
    }
    if (node instanceof SymbolNode) {
      Object obj = SYMBOL_DOUBLE_MAP.get(node.toString());
      if (obj == null) {
        obj = SYMBOL_BOOLEAN_MAP.get(node.toString());
        if (obj == null) {
          result.add(node.toString());
        }
      }
    }
  }
}
