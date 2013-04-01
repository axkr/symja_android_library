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

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.NumberNode;
import org.matheclipse.parser.client.ast.SymbolNode;

/**
 * Evaluate math expressions to <code>double</code> numbers.
 * 
 */
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
	 * @param callbackFunction
	 *          the callback function to set
	 */
	public void setCallbackFunction(IDoubleCallbackFunction callbackFunction) {
		fCallbackFunction = callbackFunction;
	}

	static class ArcTanFunction implements IDouble1Function, IDouble2Function {
		public double evaluate(double arg1) {
			return Math.atan(arg1);
		}

		public double evaluate(double arg1, double arg2) {
			return Math.atan2(arg1, arg2);
		}
	}

	static class LogFunction implements IDouble1Function, IDouble2Function {
		public double evaluate(double arg1) {
			return Math.log(arg1);
		}

		public double evaluate(double base, double z) {
			return Math.log(z) / Math.log(base);
		}
	}

	static class CompoundExpressionFunction implements IDoubleFunction {
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
		public double evaluate(DoubleEvaluator engine, FunctionNode function) {
			if (function.size() != 3) {
				throw new ArithmeticException("SetFunction#evaluate(DoubleEvaluator,FunctionNode) needs 2 arguments: "
						+ function.toString());
			}
			if (!(function.getNode(1) instanceof SymbolNode)) {
				throw new ArithmeticException("SetFunction#evaluate(DoubleEvaluator,FunctionNode) symbol required on the left hand side: "
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

	static class MaxFunction implements IDoubleFunction, IDouble2Function {
		public double evaluate(double arg1, double arg2) {
			return Math.max(arg1, arg2);
		}

		public double evaluate(DoubleEvaluator engine, FunctionNode function) {
			double result = Double.MIN_VALUE;
			double temp;
			int end = function.size();
			for (int i = 1; i < end; i++) {
				temp = Math.max(result, engine.evaluateNode(function.getNode(i)));
				if (temp > result) {
					result = temp;
				}
			}
			return result;
		}
	}

	static class MinFunction implements IDoubleFunction, IDouble2Function {
		public double evaluate(double arg1, double arg2) {
			return Math.min(arg1, arg2);
		}

		public double evaluate(DoubleEvaluator engine, FunctionNode function) {
			double result = Double.MAX_VALUE;
			double temp;
			int end = function.size();
			for (int i = 1; i < end; i++) {
				temp = Math.min(result, engine.evaluateNode(function.getNode(i)));
				if (temp < result) {
					result = temp;
				}
			}
			return result;
		}
	}

	static class PlusFunction implements IDoubleFunction, IDouble2Function {
		public double evaluate(double arg1, double arg2) {
			return arg1 + arg2;
		}

		public double evaluate(DoubleEvaluator engine, FunctionNode function) {
			double result = 0.0;
			for (int i = 1; i < function.size(); i++) {
				result += engine.evaluateNode(function.getNode(i));
			}
			return result;
		}
	}

	static class TimesFunction implements IDoubleFunction, IDouble2Function {
		public double evaluate(double arg1, double arg2) {
			return arg1 * arg2;
		}

		public double evaluate(DoubleEvaluator engine, FunctionNode function) {
			double result = 1.0;
			for (int i = 1; i < function.size(); i++) {
				result *= engine.evaluateNode(function.getNode(i));
			}
			return result;
		}
	}

	static {
		SYMBOL_DOUBLE_MAP = new HashMap<String, Double>();
		SYMBOL_DOUBLE_MAP.put("Degree", new Double(Math.PI / 180));
		SYMBOL_DOUBLE_MAP.put("E", new Double(Math.E));
		SYMBOL_DOUBLE_MAP.put("Pi", new Double(Math.PI));

		SYMBOL_BOOLEAN_MAP = new HashMap<String, Boolean>();
		SYMBOL_BOOLEAN_MAP.put("False", Boolean.FALSE);
		SYMBOL_BOOLEAN_MAP.put("True", Boolean.TRUE);

		FUNCTION_BOOLEAN_MAP = new HashMap<String, Object>();

		FUNCTION_BOOLEAN_MAP.put("And", new IBooleanBoolean2Function() {
			public boolean evaluate(boolean arg1, boolean arg2) {
				return arg1 && arg2;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("Not", new IBooleanBoolean1Function() {
			public boolean evaluate(boolean arg1) {
				return !arg1;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("Or", new IBooleanBoolean2Function() {
			public boolean evaluate(boolean arg1, boolean arg2) {
				return arg1 || arg2;
			}
		});

		FUNCTION_BOOLEAN_MAP.put("Equal", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return Math.abs(arg1 - arg2) < EPSILON;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("Greater", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return arg1 > arg2;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("GreaterEqual", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return arg1 >= arg2;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("Less", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return arg1 < arg2;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("LessEqual", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return arg1 <= arg2;
			}
		});
		FUNCTION_BOOLEAN_MAP.put("Unequal", new IBooleanDouble2Function() {
			public boolean evaluate(double arg1, double arg2) {
				return !(Math.abs(arg1 - arg2) < EPSILON);
			}
		});

		FUNCTION_DOUBLE_MAP = new HashMap<String, Object>();
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
		FUNCTION_DOUBLE_MAP.put("Random", new IDouble0Function() {
			public double evaluate() {
				return Math.random();
			}
		});
		//
		// Functions with 1 argument
		//
		FUNCTION_DOUBLE_MAP.put("ArcCos", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.acos(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("ArcSin", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.asin(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Ceiling", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.ceil(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Cos", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.cos(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Cosh", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.cosh(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Exp", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.exp(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Floor", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.floor(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Round", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.round(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Sign", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.signum(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Sin", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.sin(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Sinh", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.sinh(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Sqrt", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.sqrt(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Tan", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.tan(arg1);
			}
		});
		FUNCTION_DOUBLE_MAP.put("Tanh", new IDouble1Function() {
			public double evaluate(double arg1) {
				return Math.tanh(arg1);
			}
		});

		//
		// Functions with 2 arguments
		//
		FUNCTION_DOUBLE_MAP.put("Power", new IDouble2Function() {
			public double evaluate(double arg1, double arg2) {
				return Math.pow(arg1, arg2);
			}
		});
	}

	private Map<String, IDoubleValue> fVariableMap;

	private Map<String, BooleanVariable> fBooleanVariables;

	private ASTNode fNode;

	public DoubleEvaluator() {
		this(null);
	}

	public DoubleEvaluator(ASTNode node) {
		fVariableMap = new HashMap<String, IDoubleValue>();
		fBooleanVariables = new HashMap<String, BooleanVariable>();
		fNode = node;
	}

	/**
	 * Parse the given <code>expression String</code> and store the resulting
	 * ASTNode in this DoubleEvaluator
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public ASTNode parse(String expression) {
		Parser p = new Parser();
		fNode = p.parse(expression);
		if (fNode instanceof FunctionNode) {
			fNode = optimizeFunction((FunctionNode) fNode);
		}
		return fNode;
	}

	/**
	 * Parse the given <code>expression String</code> and return the resulting
	 * ASTNode
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public static ASTNode parseNode(String expression) {
		DoubleEvaluator doubleEvaluator = new DoubleEvaluator();
		return doubleEvaluator.parse(expression);
	}

	/**
	 * Parse the given <code>expression String</code> and evaluate it to a double
	 * value
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public double evaluate(String expression) {
		Parser p = new Parser();
		fNode = p.parse(expression);
		if (fNode instanceof FunctionNode) {
			fNode = optimizeFunction((FunctionNode) fNode);
		}
		return evaluateNode(fNode);
	}

	/**
	 * Reevaluate the <code>expression</code> (possibly after a new Variable
	 * assignment)
	 * 
	 * @param expression
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
	 * Evaluate an already parsed in abstract syntax tree node into a
	 * <code>double</code> number value.
	 * 
	 * @param node
	 *          abstract syntax tree node
	 * 
	 * @return the evaluated double number
	 * 
	 * @throws ArithmeticException
	 *           if the <code>node</code> cannot be evaluated.
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

		throw new ArithmeticException("EvalDouble#evaluate(ASTNode) not possible for: " + node.toString());
	}

	/**
	 * Evaluate an already parsed in <code>FunctionNode</code> into a
	 * <code>souble</code> number value.
	 * 
	 * @param functionNode
	 * @return
	 * 
	 * @throws ArithmeticException
	 *           if the <code>functionNode</code> cannot be evaluated.
	 */
	public double evaluateFunction(final FunctionNode functionNode) {
		if (functionNode.size() > 0 && functionNode.getNode(0) instanceof SymbolNode) {
			String symbol = functionNode.getNode(0).toString();
			if (symbol.equals("If")) {
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
					if (obj instanceof IDouble0Function) {
						return ((IDouble0Function) obj).evaluate();
					}
				} else if (functionNode.size() == 2) {
					if (obj instanceof IDouble1Function) {
						return ((IDouble1Function) obj).evaluate(evaluateNode(functionNode.getNode(1)));
					}
				} else if (functionNode.size() == 3) {
					if (obj instanceof IDouble2Function) {
						return ((IDouble2Function) obj).evaluate(evaluateNode(functionNode.getNode(1)), evaluateNode(functionNode.getNode(2)));
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
		}
		throw new ArithmeticException("EvalDouble#evaluateFunction(FunctionNode) not possible for: " + functionNode.toString());
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

		throw new ArithmeticException("EvalDouble#evaluateNodeLogical(ASTNode) not possible for: " + node.toString());
	}

	public boolean evaluateFunctionLogical(final FunctionNode functionNode) {
		if (functionNode.size() > 0 && functionNode.getNode(0) instanceof SymbolNode) {
			String symbol = functionNode.getNode(0).toString();
			if (functionNode.size() == 2) {
				Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
				if (obj instanceof IBooleanBoolean1Function) {
					return ((IBooleanBoolean1Function) obj).evaluate(evaluateNodeLogical(functionNode.getNode(1)));
				}
			} else if (functionNode.size() == 3) {
				Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
				if (obj instanceof IBooleanDouble2Function) {
					return ((IBooleanDouble2Function) obj).evaluate(evaluateNode(functionNode.getNode(1)),
							evaluateNode(functionNode.getNode(2)));
				} else if (obj instanceof IBooleanBoolean2Function) {
					return ((IBooleanBoolean2Function) obj).evaluate(evaluateNodeLogical(functionNode.getNode(1)),
							evaluateNodeLogical(functionNode.getNode(2)));
				}
				// } else {
				// Object obj = FUNCTION_BOOLEAN_MAP.get(symbol);
				// if (obj instanceof IBooleanDoubleFunction) {
				// return ((IBooleanDoubleFunction) obj).evaluate(this, functionNode);
				// }
			}
		}
		throw new ArithmeticException("EvalDouble#evaluateFunctionLogical(FunctionNode) not possible for: " + functionNode.toString());

	}

	/**
	 * Optimize an already parsed in <code>functionNode</code> into an
	 * <code>ASTNode</code>.
	 * 
	 * @param functionNode
	 * @return
	 * 
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
	 * Define a value for a given variable name and set the <code>value</code>.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, double value) {
		fVariableMap.put(variableName, new DoubleVariable(value));
	}

	/**
	 * Define a value for a given variable name and set the default value <code>0.0</code>.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName) {
		fVariableMap.put(variableName, new DoubleVariable(0.0));
	}

	/**
	 * Define a value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, IDoubleValue value) {
		fVariableMap.put(variableName, value);
	}

	/**
	 * Returns the double variable value to which the specified variableName is
	 * mapped, or {@code null} if this map contains no mapping for the
	 * variableName.
	 * 
	 * @param variableName
	 * @return
	 */
	public IDoubleValue getVariable(String variableName) {
		return fVariableMap.get(variableName);
	}

	/**
	 * Define a boolean value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, BooleanVariable value) {
		fBooleanVariables.put(variableName, value);
	}

	/**
	 * Clear all defined variables for this evaluator.
	 */
	public void clearVariables() {
		fVariableMap.clear();
		fBooleanVariables.clear();
	}

	/**
	 * Get the variable names from the given expression.
	 * 
	 * @param expression
	 * @param result
	 *          a set which contains the variable names
	 */
	public static void getVariables(String expression, Set<String> result) {
		Parser p = new Parser();
		ASTNode node = p.parse(expression);
		getVariables(node, result);
	}

	/**
	 * Get the variable names from the given AST node.
	 * 
	 * @param node
	 *          an already parsed AST node
	 * @param result
	 *          a set which contains the variable names
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
