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

import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.NumberNode;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;
import org.matheclipse.parser.client.eval.ComplexNode;
import org.matheclipse.parser.client.eval.DoubleNode;

/**
 * Abstract AST visitor with empty default method implementations.
 * 
 * @param <USER_DATA_TYPE> 
 */
public abstract class AbstractASTVisitor<DATA, DATA_VARIABLE, USER_DATA_TYPE> implements IASTVisitor<DATA, DATA_VARIABLE, USER_DATA_TYPE> {

	public void setUp(USER_DATA_TYPE data) {
	}

	public void tearDown() {
	}

	public DATA visit(ComplexNode node) {
		return null;
	}

	public DATA visit(DoubleNode node) {
		return null;
	}

	public DATA visit(FloatNode node) {
		return null;
	}

	public DATA visit(FractionNode node) {
		return null;
	}

	public DATA visit(IntegerNode node) {
		return null;
	}

	public DATA visit(PatternNode node) {
		return null;
	}

	public DATA visit(StringNode node) {
		return null;
	}

	public DATA visit(SymbolNode node) {
		return null;
	}

	/**
	 * Evaluate an already parsed in abstract syntax tree node (ASTNode) into a
	 * <code>DATA</code> value.
	 * 
	 * @param node
	 *          abstract syntax tree node
	 * 
	 * @return the evaluated value
	 * 
	 */
	public DATA evaluateNode(ASTNode node) {
		if (node instanceof DoubleNode) {
			return visit((DoubleNode) node);
		}
		if (node instanceof ComplexNode) {
			return visit((ComplexNode) node);
		}
		if (node instanceof FunctionNode) {
			return visit((FunctionNode) node);
		}
		if (node instanceof NumberNode) {
			if (node instanceof FloatNode) {
				return visit((FloatNode) node);
			}
			if (node instanceof FractionNode) {
				return visit((FractionNode) node);
			}
			if (node instanceof IntegerNode) {
				return visit((IntegerNode) node);
			}
		}
		if (node instanceof PatternNode) {
			return visit((PatternNode) node);
		}
		if (node instanceof StringNode) {
			return visit((StringNode) node);
		}
		if (node instanceof SymbolNode) {
			return visit((SymbolNode) node);
		}
		return null;
	}

}
