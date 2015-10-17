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
package org.matheclipse.parser.server.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.DivideOperator;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PreMinusOperator;
import org.matheclipse.parser.client.operator.PrePlusOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;
import org.matheclipse.parser.client.operator.SubtractOperator;

/**
 * Utility for generating source codes for the <code>ASTNodeFactory's HEADER_STRINGS, OPERATOR_STRINGS, OPERATORS</code> arrays from
 * the operators.txt textfile description
 * 
 */
public class GenerateOperatorArrays {

	/**
	 * Utility for generating source codes for the <code>ASTNodeFactory's HEADER_STRINGS, OPERATOR_STRINGS, OPERATORS</code> arrays
	 * from an operator's text file description
	 * 
	 * @param args
	 *            if <code>args.length==0</code> take the default <code>/opertors.txt</code> file for generating the arrays; if
	 *            <code>args.length>=1</code> the <code>arg[0]</code> parameters should contain the complete filename of the
	 *            operator's description file
	 */
	public static void main(final String[] args) {
		InputStream operatorDefinitions = null;
		try {
			if (args.length >= 1) {
				operatorDefinitions = new FileInputStream(args[0]);
			} else {
				operatorDefinitions = GenerateOperatorArrays.class.getResourceAsStream("/operators.txt");
			}
			final HashMap operatorMap = new HashMap();
			final HashMap operatorTokenStartSet = new HashMap();
			GenerateOperatorArrays.generateOperatorTable(operatorDefinitions, operatorMap, operatorTokenStartSet);

			final Iterator i1 = operatorMap.keySet().iterator();
			System.out.println("public static final String[] HEADER_STRINGS = {");
			while (i1.hasNext()) {
				final String headStr = (String) i1.next();
				System.out.println("    \"" + headStr + "\",");
			}
			System.out.println("};");

			final Iterator i2 = operatorMap.keySet().iterator();
			System.out.println("public static final String[] OPERATOR_STRINGS = {");
			while (i2.hasNext()) {
				final String headStr = (String) i2.next();
				final Operator oper = (Operator) operatorMap.get(headStr);
				if (oper == null) {
					System.out.println("    \" null-value-in-operator-map \",");
				} else {
					System.out.println("    \"" + oper.getOperatorString() + "\",");
				}
			}
			System.out.println("};");

			final Iterator i3 = operatorMap.keySet().iterator();
			System.out.println("public static final Operator[] OPERATORS = {");
			while (i3.hasNext()) {
				final String headStr = (String) i3.next();
				final Operator oper = (Operator) operatorMap.get(headStr);
				if (oper instanceof DivideOperator) {
					final InfixOperator iOper = (DivideOperator) oper;
					String grouping = null;
					if (iOper.getGrouping() == InfixOperator.NONE) {
						grouping = "InfixOperator.NONE";
					} else if (iOper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE) {
						grouping = "InfixOperator.LEFT_ASSOCIATIVE";
					} else if (iOper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE) {
						grouping = "InfixOperator.RIGHT_ASSOCIATIVE";
					}
					System.out.println("    new DivideOperator(\"" + iOper.getOperatorString() + "\", \"" + iOper.getFunctionName()
							+ "\", " + iOper.getPrecedence() + ", " + grouping + "),");
				} else if (oper instanceof SubtractOperator) {
					final InfixOperator iOper = (SubtractOperator) oper;
					String grouping = null;
					if (iOper.getGrouping() == InfixOperator.NONE) {
						grouping = "InfixOperator.NONE";
					} else if (iOper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE) {
						grouping = "InfixOperator.LEFT_ASSOCIATIVE";
					} else if (iOper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE) {
						grouping = "InfixOperator.RIGHT_ASSOCIATIVE";
					}
					System.out.println("    new SubtractOperator(\"" + iOper.getOperatorString() + "\", \""
							+ iOper.getFunctionName() + "\", " + iOper.getPrecedence() + ", " + grouping + "),");
				} else if (oper instanceof InfixOperator) {
					final InfixOperator iOper = (InfixOperator) oper;
					String grouping = null;
					if (iOper.getGrouping() == InfixOperator.NONE) {
						grouping = "InfixOperator.NONE";
					} else if (iOper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE) {
						grouping = "InfixOperator.LEFT_ASSOCIATIVE";
					} else if (iOper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE) {
						grouping = "InfixOperator.RIGHT_ASSOCIATIVE";
					}
					System.out.println("    new InfixOperator(\"" + iOper.getOperatorString() + "\", \"" + iOper.getFunctionName()
							+ "\", " + iOper.getPrecedence() + ", " + grouping + "),");
				} else if (oper instanceof PostfixOperator) {
					System.out.println("    new PostfixOperator(\"" + oper.getOperatorString() + "\", \"" + oper.getFunctionName()
							+ "\", " + oper.getPrecedence() + "),");
				} else if (oper instanceof PreMinusOperator) {
					System.out.println("    new PreMinusOperator(\"" + oper.getOperatorString() + "\", \"" + oper.getFunctionName()
							+ "\", " + oper.getPrecedence() + "),");
				} else if (oper instanceof PrePlusOperator) {
					System.out.println("    new PrePlusOperator(\"" + oper.getOperatorString() + "\", \"" + oper.getFunctionName()
							+ "\", " + oper.getPrecedence() + "),");
				} else if (oper instanceof PrefixOperator) {
					System.out.println("    new PrefixOperator(\"" + oper.getOperatorString() + "\", \"" + oper.getFunctionName()
							+ "\", " + oper.getPrecedence() + "),");
				}

			}
			System.out.println("};");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (operatorDefinitions != null) {
				try {
					operatorDefinitions.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void generateOperatorTable(final InputStream is, final HashMap operatorMap, final HashMap operatorTokenStartSet) {
		String record = null;
		final BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

		StringTokenizer tokenizer;
		Operator oper = null;
		String typeStr;
		String operatorStr;
		String headStr;
		String precedenceStr;
		String groupingStr;
		int precedence;
		int grouping;
		try {

			while ((record = r.readLine()) != null) {
				if (record.charAt(0) == '#') {
					continue;
				}
				tokenizer = new StringTokenizer(record, ",");
				typeStr = ((String) tokenizer.nextElement()).trim();
				operatorStr = ((String) tokenizer.nextElement()).trim();
				headStr = ((String) tokenizer.nextElement()).trim();
				precedenceStr = ((String) tokenizer.nextElement()).trim();
				precedence = Integer.valueOf(precedenceStr).intValue();
				oper = null;
				if (typeStr.equalsIgnoreCase("in")) {
					try {
						groupingStr = ((String) tokenizer.nextElement()).trim();
						grouping = InfixOperator.NONE;
						if (groupingStr.equalsIgnoreCase("left")) {
							grouping = InfixOperator.LEFT_ASSOCIATIVE;
						} else if (groupingStr.equalsIgnoreCase("right")) {
							grouping = InfixOperator.RIGHT_ASSOCIATIVE;
						}
						oper = ASTNodeFactory.createInfixOperator(operatorStr, headStr, precedence, grouping);
					} catch (final NoSuchElementException nsee) {
						oper = new InfixOperator(operatorStr, headStr, precedence, InfixOperator.NONE);
					}
					// if (operatorStr.equals("*")) {
					// TIMES_OPERATOR = (InfixOperator) oper;
					// } else if (operatorStr.equals("+")) {
					// PLUS_OPERATOR = (InfixOperator) oper;
					// }
				} else if (typeStr.equalsIgnoreCase("pre")) {
					oper = ASTNodeFactory.createPrefixOperator(operatorStr, headStr, precedence);
				} else if (typeStr.equalsIgnoreCase("post")) {
					oper = ASTNodeFactory.createPostfixOperator(operatorStr, headStr, precedence);
				} else {
					throw new ParseException("Wrong operator type: " + typeStr, 0);
				}
				// System.out.println(oper);
				ASTNodeFactory.addOperator(operatorMap, operatorTokenStartSet, operatorStr, headStr, oper);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
