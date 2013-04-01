/*
 * Copyright 2005-2009 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.parser.client.eval.api;

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.eval.BooleanVariable;

/**
 * Evaluates a given expression (as <code>String</code> or
 * <code>ASTNode</code>) into a <code>DATA</code> object type. For example
 * the <code>ComplexEvaluator</code> class uses the <code>Complex</code>
 * class data type.
 * 
 * @param <DATA>
 * @param <DATA_VARIABLE>
 * @param <USER_DATA_TYPE>
 * 
 * @see org.matheclipse.parser.client.eval.ComplexEvaluator
 * @see org.matheclipse.parser.client.math.Complex
 */
public class ObjectEvaluator<DATA, DATA_VARIABLE, USER_DATA_TYPE> {
	protected ASTNode fNode;

	protected IASTVisitor<DATA, DATA_VARIABLE, USER_DATA_TYPE> fVisitor;

	public ObjectEvaluator(IASTVisitor<DATA, DATA_VARIABLE, USER_DATA_TYPE> visitor) {
		fVisitor = visitor;
	}

	/**
	 * Evaluate an already parsed in abstract syntax tree node into a
	 * <code>DATA</code> value.
	 * 
	 * @param node
	 *          abstract syntax tree node
	 * 
	 * @return the evaluated Complex number
	 * 
	 */
	public DATA evaluateNode(final ASTNode node) {
		return evaluateNode(node, null);
	}

	public DATA evaluateNode(ASTNode node, USER_DATA_TYPE data) {
		try {
			fVisitor.setUp(data);
			return fVisitor.evaluateNode(node);
		} finally {
			fVisitor.tearDown();
		}
	}

	/**
	 * Define a value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, DATA_VARIABLE value) {
		fVisitor.defineVariable(variableName, value);
	}

	/**
	 * Returns the data variable value to which the specified variableName is
	 * mapped, or {@code null} if this map contains no mapping for the
	 * variableName.
	 * 
	 * @param variableName
	 * @return
	 */
	public DATA_VARIABLE getVariable(String variableName) {
		return fVisitor.getVariable(variableName);
	}

	/**
	 * Define a boolean value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, BooleanVariable value) {
		fVisitor.defineVariable(variableName, value);
	}

	/**
	 * Clear all defined variables for this evaluator.
	 */
	public void clearVariables() {
		fVisitor.clearVariables();
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
		return fVisitor.optimizeFunction(functionNode);
	}

	/**
	 * Parse the given expression <code>String</code> and evaluate it to a
	 * <code>DATA</code> value.
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public DATA evaluate(String expression) {
		Parser p = new Parser();
		fNode = p.parse(expression);
		if (fNode instanceof FunctionNode) {
			fNode = fVisitor.optimizeFunction((FunctionNode) fNode);
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
	public DATA evaluate() {
		if (fNode == null) {
			throw new SyntaxError(0, 0, 0, " ", "No parser input defined", 1);
		}
		return evaluateNode(fNode);
	}

}
