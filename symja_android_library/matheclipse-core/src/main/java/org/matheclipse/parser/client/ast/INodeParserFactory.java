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

/**
 * Factory for creating the ASTNodes from the parser
 * 
 */
public interface INodeParserFactory extends IParserFactory {

	/**
	 * Creates a new function with head <code>head</code> and 0 arguments.
	 * 
	 * @param head
	 *            the head of the function
	 * @return
	 */
	public FunctionNode createFunction(SymbolNode head);

	/**
	 * Creates a new function with head <code>head</code> and 1 argument.
	 * 
	 * @param head
	 *            the head of the function
	 * @param arg
	 *            the argument of the function
	 * @return
	 */
	public FunctionNode createFunction(SymbolNode head, ASTNode arg);

	/**
	 * Creates a new function with head <code>head</code> and 2 arguments.
	 * 
	 * @param head
	 *            the head of the function
	 * @param arg1
	 *            the first argument of the function
	 * @param arg2
	 *            the second argument of the function
	 * @return
	 */
	public FunctionNode createFunction(SymbolNode head, ASTNode arg1, ASTNode arg2);

	/**
	 * Creates a new function with no arguments from the given header expression .
	 * 
	 * @param headExpr
	 *            the head of the function
	 * @return
	 */
	public FunctionNode createAST(ASTNode headExpr);

	/**
	 * Create an double node from the given double value string
	 * 
	 * @param doubleString
	 *            the double string
	 * @return
	 */
	public ASTNode createDouble(String doubleString);

	/**
	 * Create an integer node from the given string
	 * 
	 * @param integerString
	 *            the integer number represented as a String
	 * @param numberFormat
	 *            the format of the number (usually 10)
	 * @return IInteger
	 */
	public IntegerNode createInteger(String integerString, int numberFormat);

	/**
	 * Create an integer node from the given value
	 * 
	 * @param integerValue
	 *            the integer number's value
	 * @return IInteger
	 */
	public IntegerNode createInteger(int integerValue);

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denominator of the fractional number
	 * @return IFraction
	 */
	public abstract FractionNode createFraction(IntegerNode numerator, IntegerNode denominator);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>_</code> or <code>x_</code>)
	 * 
	 * @param patternName
	 *            the name of the pattern
	 * @param check
	 * @return
	 */
	public PatternNode createPattern(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>__</code> or <code>x__</code>)
	 * 
	 * @param patternName
	 * @param check
	 * @return
	 */
	public PatternNode createPattern2(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node (i.e. <code>___</code> or <code>x___</code>)
	 * 
	 * @param patternName
	 * @param check
	 * @return
	 */
	public PatternNode createPattern3(SymbolNode patternName, ASTNode check);

	/**
	 * Create a pattern from the given symbol node
	 * 
	 * @param patternName
	 *            the pattern name
	 * @param check
	 *            the pattern head to check for
	 * @param optional
	 *            <code>true</code> if the pattern is optional
	 * @return
	 */
	public PatternNode createPattern(SymbolNode patternName, ASTNode check, boolean optional);

	/**
	 * Create a pattern from the given symbol node
	 * 
	 * @param patternName
	 *            the pattern name
	 * @param check
	 *            the pattern head to check for
	 * @param defaultValue
	 *            the default value for this pattern
	 * @return
	 */
	public PatternNode createPattern(SymbolNode patternName, ASTNode check, ASTNode defaultValue);

	/**
	 * Create a string node from the scanned double quoted string
	 * 
	 * @param buffer
	 *            the buffer which contains the parsed string
	 * @return
	 */
	public StringNode createString(StringBuilder buffer);

	/**
	 * Create a symbol from the scanned identifier string
	 * 
	 * @param symbolName
	 * @return
	 */
	public SymbolNode createSymbol(String symbolName);

}