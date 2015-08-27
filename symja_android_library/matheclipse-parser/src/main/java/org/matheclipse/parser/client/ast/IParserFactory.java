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
package org.matheclipse.parser.client.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.matheclipse.parser.client.operator.Operator;

/**
 * Factory for creating the ASTNodes from the parser
 * 
 */
public interface IParserFactory {
	/**
	 * The default set of characters, which could form an operator
	 * 
	 */
	public final static String DEFAULT_OPERATOR_CHARACTERS = ".-:=<>*+;!^|&/@?";

	/**
	 * The set of characters, which could form an operator
	 * 
	 */
	public String getOperatorCharacters();

	/**
	 * Get the identifier to operator map
	 * 
	 * @return the map which stores the Operators for a given head string like
	 *         Times, Plus, Sin,...
	 */
	public Map<String, Operator> getIdentifier2OperatorMap();

	/**
	 * Get the operator-string to possible operator-list map
	 * 
	 * @return the map which stores the operator-list for a given operator string
	 *         like *, +, ==...
	 */
	public Map<String, ArrayList<Operator>> getOperator2ListMap();

	/**
	 * Get the operator for a given identifier string like Times, Plus, Sin,...
	 * 
	 * @param identifier
	 * @return
	 */
	public Operator get(String identifier);

	/**
	 * Get the operator-list for a given operator-string
	 * 
	 * @return the operator-list for a given operator string like *, +, ==...
	 */
	public List<Operator> getOperatorList(String operatorString);

	/**
	 * Creates a new function with head <code>head</code> and 0 arguments.
	 */
	public FunctionNode createFunction(SymbolNode head);

	/**
	 * Creates a new function with head <code>head</code> and 1 argument.
	 */
	public FunctionNode createFunction(SymbolNode head, ASTNode arg0);

	/**
	 * Creates a new function with head <code>head</code> and 2 arguments.
	 */
	public FunctionNode createFunction(SymbolNode head, ASTNode arg0, ASTNode arg1);

	/**
	 * Creates a new function with no arguments from the given header expression .
	 */
	public FunctionNode createAST(ASTNode headExpr);

	/**
	 * Create an double node from the given double value string
	 * 
	 * @param doubleString
	 * @return
	 */
	public ASTNode createDouble(String doubleString);

	/**
	 * Create an integer node from the given string
	 * 
	 * @param integerString
	 *          the integer number represented as a String
	 * @param numberFormat
	 *          the format of the number (usually 10)
	 * @return IInteger
	 */
	public IntegerNode createInteger(String integerString, int numberFormat);

	/**
	 * Create an integer node from the given value
	 * 
	 * @param integerValue
	 *          the integer number's value
	 * @return IInteger
	 */
	public IntegerNode createInteger(int integerValue);

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *          numerator of the fractional number
	 * @param denominator
	 *          denominator of the fractional number
	 * @return IFraction
	 */
	public abstract FractionNode createFraction(IntegerNode numerator, IntegerNode denominator);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>_</code> or
	 * <code>x_</code>)
	 * 
	 * @param patternName
	 * @return Object
	 */
	public PatternNode createPattern(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>__</code> or
	 * <code>x__</code>)
	 * 
	 * @param patternName
	 * @return Object
	 */
	public PatternNode createPattern2(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>___</code> or
	 * <code>x___</code>)
	 * 
	 * @param patternName
	 * @return Object
	 */
	public PatternNode createPattern3(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node
	 * 
	 * @param patternName
	 * @return Object
	 */
	public PatternNode createPattern(SymbolNode patternName, ASTNode check, boolean optional);

	/**
	 * Create a string node from the scanned double quoted string
	 * 
	 * @param symbolName
	 * @return
	 */
	public StringNode createString(StringBuffer buffer);

	/**
	 * Create a symbol from the scanned identifier string
	 * 
	 * @param symbolName
	 * @return
	 */
	public SymbolNode createSymbol(String symbolName);

	/**
	 * Check if the identifier name is valid.
	 * 
	 * @param identifier
	 *          the currently parsed identifier
	 * @return <code>false</code> if the identifier is not valid (in this case the
	 *         parser creates a SyntaxError exception); otherwise return
	 *         <code>true</code>
	 */
	public boolean isValidIdentifier(String identifier);

}