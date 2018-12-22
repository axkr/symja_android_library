package org.matheclipse.core.form.tex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.function.BiFunction;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.operator.Operator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

public class TeXParser {

	static class BinaryOperator extends Operator {
		BiFunction<IExpr, IExpr, IExpr> binaryFunction;

		public BinaryOperator(final String oper, final String functionName, final int precedence,
				BiFunction<IExpr, IExpr, IExpr> binaryFunction) {
			super(oper, functionName, precedence);
			this.binaryFunction = binaryFunction;
		}

		public IExpr createFunction(final IExpr lhs, final IExpr rhs) {
			return binaryFunction.apply(lhs, rhs);
		}
	}

	static final BinaryOperator[] BINARY_OPERATORS = {
			new BinaryOperator("=", "Equal", ExprParserFactory.EQUAL_PRECEDENCE, (lhs, rhs) -> F.Equal(lhs, rhs)), //
			new BinaryOperator("+", "Plus", ExprParserFactory.PLUS_PRECEDENCE, (lhs, rhs) -> F.Plus(lhs, rhs)), //
			new BinaryOperator("-", "Subtract", ExprParserFactory.PLUS_PRECEDENCE, (lhs, rhs) -> F.Subtract(lhs, rhs)), //
			new BinaryOperator("*", "Times", ExprParserFactory.TIMES_PRECEDENCE, (lhs, rhs) -> F.Times(lhs, rhs)), //
			new BinaryOperator("\u00d7", "Times", ExprParserFactory.TIMES_PRECEDENCE, (lhs, rhs) -> F.Times(lhs, rhs)), //
			new BinaryOperator("/", "Divide", ExprParserFactory.DIVIDE_PRECEDENCE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
			new BinaryOperator("\u00f7", "Divide", ExprParserFactory.DIVIDE_PRECEDENCE,
					(lhs, rhs) -> F.Divide(lhs, rhs)) //
	};

	private static final HashMap<String, BinaryOperator> BINARY_OPERATOR_MAP;
	EvalEngine fEngine;

	static {
		BINARY_OPERATOR_MAP = new HashMap<String, BinaryOperator>();
		for (int i = 0; i < BINARY_OPERATORS.length; i++) {
			String headStr = BINARY_OPERATORS[i].getOperatorString();
			BINARY_OPERATOR_MAP.put(headStr, BINARY_OPERATORS[i]);
		}
	}

	public TeXParser(EvalEngine engine) {
		fEngine = engine;
	}

	public IExpr toExpression(String texStr) {
		SnuggleEngine engine = new SnuggleEngine();
		SnuggleSession session = engine.createSession();

		SnuggleInput input = new SnuggleInput("$$ " + texStr + " $$");
		try {
			session.parseInput(input);

			/*
			 * Convert the results to an XML String, which in this case will be a single MathML <math>...</math>
			 * element.
			 */
			// String xmlString = session.buildXMLString();
			// System.out.println("Input " + input.getString() + " was converted to:\n" + xmlString);

			NodeList nodes = session.buildDOMSubtree();
			int[] position = new int[] { 0 };
			return getNodeList(nodes, position, null, 0);
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return F.$Aborted;
	}

	private IExpr expr(Node node) {
		int[] position = new int[] { 0 };
		String name = node.getNodeName();
		if (name.equals("mi")) {
			String text = node.getTextContent();
			if (text.length() == 1) {

				// System.out.println(toUnicodeString(text, "UTF-8"));
				char ch = text.charAt(0);
				if (ch == '\u03c0') {
					return F.Pi;
				}
			}
			return F.$s(text);
		} else if (name.equals("mo")) {
			String text = node.getTextContent();
			if (text.length() == 1) {

				// System.out.println(toUnicodeString(text, "UTF-8"));
				char ch = text.charAt(0);
				if (ch == '\u2218' || ch == '\u00B0') {
					return F.Degree;
				}
			}
			return F.$s(text);
		} else if (name.equals("mn")) {
			String text = node.getTextContent();
			return F.integer(text, 10);
		} else if (name.equals("math")) {
			return getNodeList(node.getChildNodes(), position, null, 0);
		} else if (name.equals("mfrac")) {
			return mfrac(node.getChildNodes());
		} else if (name.equals("msqrt")) {
			return msqrt(node.getChildNodes());
		} else if (name.equals("msup")) {
			return msup(node.getChildNodes());
		} else if (name.equals("mrow")) {
			return mrow(node);
		}
		// String n = node.getNodeName();
		// System.out.println(n.toString());
		NodeList list = node.getChildNodes();
		return getNodeList(list, position, null, 0);
	}

	public static String toUnicodeString(final String unicodeInput, final String inputEncoding) {
		final StringBuilder unicodeStringBuilder = new StringBuilder();
		String unicodeString = null;

		try {
			final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
			String hexValueString = null;
			int hexValueLength = 0;
			for (int i = 0; i < utf8String.length(); i++) {
				hexValueString = Integer.toHexString(utf8String.charAt(i));
				hexValueLength = hexValueString.length();
				if (hexValueLength < 4) {
					for (int j = 0; j < (4 - hexValueLength); j++) {
						hexValueString = "0" + hexValueString;
					}
				}
				unicodeStringBuilder.append("\\u");
				unicodeStringBuilder.append(hexValueString);
			}
			unicodeString = unicodeStringBuilder.toString();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return unicodeString;
	}

	private IExpr getNodeList(NodeList list, int[] position, IExpr lhs, int precedence) {
		if (list.getLength() > 2) {
			if (lhs == null) {
				Node lhsNode = list.item(position[0]++);
				lhs = expr(lhsNode);
			}
			IExpr result = lhs;
			int currPrec = 0;
			while (position[0] + 2 <= list.getLength()) {
				Node op = list.item(position[0]);
				String name = op.getNodeName();
				if (name.equals("mo")) {
					String text = op.getTextContent();
					BinaryOperator operator = BINARY_OPERATOR_MAP.get(text);
					if (operator != null) {
						currPrec = operator.getPrecedence();
						if (precedence > currPrec) {
							return result;
						}
						position[0]++;
						IExpr rhs = getNodeList(list, position, null, currPrec);
						result = operator.createFunction(result, rhs);
						continue;
					}
				}
				result = F.NIL;
				break;
			}
			if (result.isPresent() && position[0] >= list.getLength()) {
				return result;
			}
		}
		IASTAppendable ast = F.Sequence();
		for (int i = 0; i < list.getLength(); i++) {
			Node temp = list.item(i);
			// String name = temp.getNodeName();
			// if (name.equals("math")) {
			// return expr(temp);
			// }
			IExpr ex = expr(temp);
			if (i == 0 && ex.isSymbol() && list.getLength() == 2) {
				Node arg2 = list.item(1);
				if (arg2.getNodeName().equals("mfenced")) {
					IExpr args = expr(list.item(1));
					if (args.isSequence()) {
						((IASTMutable) args).set(0, ex);
						return args;
					}
					return F.unaryAST1(ex, args);
				}
			}
			ast.append(ex);
		}
		if (ast.size() == 2) {
			return ast.arg1();
		}
		if (ast.size() > 1) {
			if (ast.arg1().isBuiltInSymbol()) {

				return F.unaryAST1(ast.arg1(), ast.arg2());
			}
		}
		return ast;
	}

	private IExpr mrow(Node node) {
		NodeList list = node.getChildNodes();
		boolean isSymbol = true;
		for (int i = 0; i < list.getLength(); i++) {
			Node temp = list.item(i);
			String n = temp.getNodeName();
			if (!n.equals("mi") || !(temp instanceof ElementImpl)) {
				isSymbol = false;
				break;
			}
		}
		if (isSymbol) {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < list.getLength(); i++) {
				Node temp = list.item(i);
				buf.append(temp.getTextContent());
			}
			return F.$s(buf.toString());
		}
		int[] position = new int[] { 0 };
		return getNodeList(list, position, null, 0);
	}

	private IExpr mfrac(NodeList list) {
		IASTAppendable divide = F.TimesAlloc(2);
		if (list.getLength() > 0) {
			Node temp = list.item(0);
			divide.append(expr(temp));
			for (int i = 1; i < list.getLength(); i++) {
				temp = list.item(i);
				divide.append(F.Power(expr(temp), -1));
			}
		}
		return divide;
	}

	private IExpr msqrt(NodeList list) {
		if (list.getLength() > 0) {
			Node temp = list.item(0);
			return F.Power(expr(temp), F.C1D2);
		}
		return F.NIL;
	}

	private IExpr msup(NodeList list) {
		if (list.getLength() == 2) {
			Node arg1 = list.item(0);
			IExpr a1 = expr(arg1);
			Node arg2 = list.item(1);
			IExpr a2 = expr(arg2);
			if (a2.equals(F.Degree)) {
				return F.Times(a1, a2);
			}
			return F.Power(a1, a2);
		}
		throw new AbortException();
	}
}
