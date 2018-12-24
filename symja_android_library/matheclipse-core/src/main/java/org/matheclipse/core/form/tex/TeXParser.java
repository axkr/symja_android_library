package org.matheclipse.core.form.tex;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.operator.Operator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

public class TeXParser {
	final static boolean SHOW_UNICODE = false;

	static class PrefixOperator extends Operator {
		Function<IExpr, IExpr> function;

		public PrefixOperator(final String oper, final String functionName, final int precedence,
				Function<IExpr, IExpr> function) {
			super(oper, functionName, precedence);
			this.function = function;
		}

		public IExpr createFunction(final IExpr expr) {
			return function.apply(expr);
		}
	}

	static class PostfixOperator extends Operator {
		Function<IExpr, IExpr> function;

		public PostfixOperator(final String oper, final String functionName, final int precedence,
				Function<IExpr, IExpr> function) {
			super(oper, functionName, precedence);
			this.function = function;
		}

		public IExpr createFunction(final IExpr expr) {
			return function.apply(expr);
		}
	}

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

	static final PrefixOperator[] PREFIX_OPERATORS = { new PrefixOperator("+", "Plus", 670, (x) -> x), //
			new PrefixOperator("-", "Minus", 485, (x) -> F.Negate(x)), //
			new PrefixOperator("\u00ac", "Not", 230, (x) -> F.Not(x)), //

	};

	static final PostfixOperator[] POSTFIX_OPERATORS = {
			new PostfixOperator("!", "Factorial", ExprParserFactory.FACTORIAL_PRECEDENCE, (x) -> F.Factorial(x)), //
	};

	static final BinaryOperator[] BINARY_OPERATORS = {
			new BinaryOperator("=", "Equal", ExprParserFactory.EQUAL_PRECEDENCE, (lhs, rhs) -> F.Equal(lhs, rhs)), //
			new BinaryOperator("\u2264", "LessEqual", ExprParserFactory.EQUAL_PRECEDENCE,
					(lhs, rhs) -> F.LessEqual(lhs, rhs)), //
			new BinaryOperator("\u2265", "GreaterEqual", ExprParserFactory.EQUAL_PRECEDENCE,
					(lhs, rhs) -> F.GreaterEqual(lhs, rhs)), //
			new BinaryOperator("<", "Less", ExprParserFactory.EQUAL_PRECEDENCE, (lhs, rhs) -> F.Less(lhs, rhs)), //
			new BinaryOperator(">", "Greater", ExprParserFactory.EQUAL_PRECEDENCE, (lhs, rhs) -> F.Greater(lhs, rhs)), //

			new BinaryOperator("\u2227", "And", 215, (lhs, rhs) -> F.And(lhs, rhs)), //
			new BinaryOperator("\u2228", "Or", 213, (lhs, rhs) -> F.Or(lhs, rhs)), //

			new BinaryOperator("\u21d2", "Implies", 120, (lhs, rhs) -> F.Implies(lhs, rhs)), // Rightarrow
			new BinaryOperator("\u2192", "Rule", 120, (lhs, rhs) -> F.Rule(lhs, rhs)), // rightarrow
			new BinaryOperator("\u21d4", "Equivalent", 120, (lhs, rhs) -> F.Equivalent(lhs, rhs)), // Leftrightarrow

			new BinaryOperator("+", "Plus", ExprParserFactory.PLUS_PRECEDENCE, (lhs, rhs) -> F.Plus(lhs, rhs)), //
			new BinaryOperator("-", "Subtract", ExprParserFactory.PLUS_PRECEDENCE, (lhs, rhs) -> F.Subtract(lhs, rhs)), //

			new BinaryOperator("*", "Times", ExprParserFactory.TIMES_PRECEDENCE, (lhs, rhs) -> F.Times(lhs, rhs)), //
			// x multiplication sign
			new BinaryOperator("\u00d7", "Times", ExprParserFactory.TIMES_PRECEDENCE, (lhs, rhs) -> F.Times(lhs, rhs)), //
			// InvisibleTimes
			new BinaryOperator("\u2062", "Times", ExprParserFactory.TIMES_PRECEDENCE, (lhs, rhs) -> F.Times(lhs, rhs)), //

			new BinaryOperator("/", "Divide", ExprParserFactory.DIVIDE_PRECEDENCE, (lhs, rhs) -> F.Divide(lhs, rhs)), //
			// &#xf7; Division sign
			new BinaryOperator("\u00f7", "Divide", ExprParserFactory.DIVIDE_PRECEDENCE,
					(lhs, rhs) -> F.Divide(lhs, rhs)) //
	};

	private static final HashMap<String, IExpr> UNICODE_OPERATOR_MAP;
	private static final HashMap<String, BinaryOperator> BINARY_OPERATOR_MAP;
	private static final HashMap<String, PrefixOperator> PREFIX_OPERATOR_MAP;
	private static final HashMap<String, PostfixOperator> POSTFIX_OPERATOR_MAP;
	static {
		UNICODE_OPERATOR_MAP = new HashMap<String, IExpr>();
		UNICODE_OPERATOR_MAP.put("\u2218", F.Degree);
		UNICODE_OPERATOR_MAP.put("\u00B0", F.Degree);
		UNICODE_OPERATOR_MAP.put("\u222b", F.Integrate);
		UNICODE_OPERATOR_MAP.put("\u2211", F.Sum);
		UNICODE_OPERATOR_MAP.put("\u220f", F.Product);
		UNICODE_OPERATOR_MAP.put("\u03c0", F.Pi);
		UNICODE_OPERATOR_MAP.put("\u221e", F.CInfinity);
		UNICODE_OPERATOR_MAP.put("\u2148", F.CI); // double-struck italic letter i
		UNICODE_OPERATOR_MAP.put("\u2149", F.CI); // double-struck italic letter j
		UNICODE_OPERATOR_MAP.put("\u2107", F.E); // euler's constant

		BINARY_OPERATOR_MAP = new HashMap<String, BinaryOperator>();
		for (int i = 0; i < BINARY_OPERATORS.length; i++) {
			String headStr = BINARY_OPERATORS[i].getOperatorString();
			BINARY_OPERATOR_MAP.put(headStr, BINARY_OPERATORS[i]);
		}
		PREFIX_OPERATOR_MAP = new HashMap<String, PrefixOperator>();
		for (int i = 0; i < PREFIX_OPERATORS.length; i++) {
			String headStr = PREFIX_OPERATORS[i].getOperatorString();
			PREFIX_OPERATOR_MAP.put(headStr, PREFIX_OPERATORS[i]);
		}
		POSTFIX_OPERATOR_MAP = new HashMap<String, PostfixOperator>();
		for (int i = 0; i < POSTFIX_OPERATORS.length; i++) {
			String headStr = POSTFIX_OPERATORS[i].getOperatorString();
			POSTFIX_OPERATOR_MAP.put(headStr, POSTFIX_OPERATORS[i]);
		}
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

	int counter = 0;
	EvalEngine fEngine;

	public TeXParser(EvalEngine engine) {
		fEngine = engine;
	}

	private IExpr toExpr(Node node) {
		int[] position = new int[] { 0 };
		String name = node.getNodeName();
		if (name.equals("mi")) {
			return mi(node);
		} else if (name.equals("mo")) {
			return mo(node);
		} else if (name.equals("mn")) {
			return mn(node);
		} else if (name.equals("math")) {
			return getNodeList(node.getChildNodes(), position, null, 0);
		} else if (name.equals("mfrac")) {
			return mfrac(node.getChildNodes());
		} else if (name.equals("msqrt")) {
			return msqrt(node.getChildNodes());
		} else if (name.equals("msup")) {
			return msup(node.getChildNodes());
		} else if (name.equals("msubsup")) {
			return msubsup(node.getChildNodes(), null, position, 0);
		} else if (name.equals("munderover")) {
			return munderover(node.getChildNodes(), null, position, 0);
		} else if (name.equals("mrow")) {
			return mrow(node);
		}
		// String n = node.getNodeName();
		// System.out.println(n.toString());
		NodeList list = node.getChildNodes();
		return getNodeList(list, position, null, 0);
	}

	private IExpr toHeadExpr(Node node, NodeList parentList, int[] position, int precedence) {
		String name = node.getNodeName();
		if (name.equals("mi")) {
			return mi(node);
		} else if (name.equals("mo")) {
			return mo(node);
		} else if (name.equals("mn")) {
			return mn(node);
		} else if (name.equals("math")) {
			return getNodeList(node.getChildNodes(), position, null, 0);
		} else if (name.equals("mfrac")) {
			return mfrac(node.getChildNodes());
		} else if (name.equals("msqrt")) {
			return msqrt(node.getChildNodes());
		} else if (name.equals("msup")) {
			return msup(node.getChildNodes());
		} else if (name.equals("msubsup")) {
			return msubsup(node.getChildNodes(), parentList, position, precedence);
		} else if (name.equals("munderover")) {
			return munderover(node.getChildNodes(), parentList, position, precedence);
		} else if (name.equals("mrow")) {
			return mrow(node);
		}
		// String n = node.getNodeName();
		// System.out.println(n.toString());
		NodeList list = node.getChildNodes();
		return getNodeList(list, position, null, 0);
	}

	private IExpr getNodeList(NodeList list, int[] position, IExpr lhs, int precedence) {
		if (list.getLength() > 1) {
			if (lhs == null) {
				Node lhsNode = list.item(position[0]++);
				String name = lhsNode.getNodeName();
				if (name.equals("mo")) {
					String text = lhsNode.getTextContent();
					PrefixOperator operator = PREFIX_OPERATOR_MAP.get(text);
					if (operator != null) {
						int currPrec = operator.getPrecedence();
						IExpr x = getNodeList(list, position, null, currPrec);
						lhs = operator.createFunction(x);
					}
				}
				if (lhs == null) {
					lhs = toHeadExpr(lhsNode, list, position, precedence);
					if (position[0] >= list.getLength()) {
						return lhs;
					}
				}

				if (lhs.isSymbol() && position[0] < list.getLength()) {
					Node arg2 = list.item(position[0]);
					if (arg2.getNodeName().equals("mfenced")) {
						position[0]++;
						int[] position2 = new int[] { 0 };
						IExpr args = getNodeList(arg2.getChildNodes(), position2, null, 0);
						if (args.isSequence()) {
							((IASTMutable) args).set(0, lhs);
							return args;
						}
						return F.unaryAST1(lhs, args);
					}
				}
			}
			IExpr result = lhs;
			int currPrec = 0;
			while (position[0] < list.getLength()) {
				Node op = list.item(position[0]);
				String name = op.getNodeName();
				if (name.equals("mo")) {
					String text = op.getTextContent();
					if (SHOW_UNICODE) {
						System.out.println("mo: " + text + " - " + toUnicodeString(text, "UTF-8"));
					}
					BinaryOperator binaryOperator = BINARY_OPERATOR_MAP.get(text);
					if (binaryOperator != null) {
						currPrec = binaryOperator.getPrecedence();
						if (precedence > currPrec) {
							return result;
						}
						position[0]++;
						IExpr rhs = getNodeList(list, position, null, currPrec);
						result = binaryOperator.createFunction(result, rhs);
						continue;
					} else {
						PostfixOperator postfixOperator = POSTFIX_OPERATOR_MAP.get(text);
						if (postfixOperator != null) {
							currPrec = postfixOperator.getPrecedence();
							result = postfixOperator.createFunction(lhs);
							position[0]++;
							continue;
						}
					}
					throw new AbortException();
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
			IExpr ex = toExpr(temp);
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

	private IExpr mfrac(NodeList list) {
		IASTAppendable divide = F.TimesAlloc(2);
		if (list.getLength() > 0) {
			Node temp = list.item(0);
			divide.append(toExpr(temp));
			for (int i = 1; i < list.getLength(); i++) {
				temp = list.item(i);
				divide.append(F.Power(toExpr(temp), -1));
			}
		}
		return divide;
	}

	private IExpr mi(Node node) {
		String text = node.getTextContent();
		if (text.length() == 1) {
			if (SHOW_UNICODE) {
				System.out.println("mi: " + text + " - " + toUnicodeString(text, "UTF-8"));
			}
			IExpr x = UNICODE_OPERATOR_MAP.get(text);
			if (x != null) {
				return x;
			}
		}
		return F.$s(text);
	}

	private IExpr mn(Node node) {
		try {
			String text = node.getTextContent();
			if (text.contains(".") || text.contains("E")) {
				return F.num(text);
			}
			return F.integer(text, 10);
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		}
		throw new AbortException();
	}

	private IExpr mo(Node node) {
		String text = node.getTextContent();
		if (text.length() == 1) {
			if (SHOW_UNICODE) {
				System.out.println("mo: " + text + " - " + toUnicodeString(text, "UTF-8"));
			}
			IExpr x = UNICODE_OPERATOR_MAP.get(text);
			if (x != null) {
				return x;
			}
		}
		return F.$s(text);
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

	private IExpr msqrt(NodeList list) {
		if (list.getLength() > 0) {
			Node temp = list.item(0);
			return F.Power(toExpr(temp), F.C1D2);
		}
		return F.NIL;
	}

	private IExpr msubsup(NodeList list, NodeList parentList, int[] position, int precedence) {
		// \\int_0^\\infty a dx
		if (list.getLength() > 0) {
			Node arg0 = list.item(0);
			IExpr head = toExpr(arg0);
			if (head.isBuiltInSymbol()) {
				ISymbol sym = F.Dummy("msubsup$" + counter++);
				IExpr arg2 = sym;
				if (list.getLength() >= 2) {
					Node arg1 = list.item(1);
					IExpr a1 = toExpr(arg1);
					if (list.getLength() == 3) {
						IExpr a2 = toExpr(list.item(2));
						arg2 = F.List(sym, a1, a2);
					} else if (list.getLength() == 2) {
						arg2 = F.List(sym, a1);
					}
				}

				while (position[0] < parentList.getLength()) {
					if (head.equals(F.Integrate)) {
						IExpr arg1 = toExpr(parentList.item(position[0]++));
						IExpr d = toExpr(parentList.item(position[0]++));
						if (d.toString().equals("d")) {
							IExpr x = toExpr(parentList.item(position[0]++));
							arg1 = F.subs(arg1, sym, x);
							arg2 = F.subs(arg2, sym, x);
							return F.binaryAST2(head, arg1, arg2);
						}
					} else {
						IExpr arg1 = getNodeList(parentList, position, null, Integer.MAX_VALUE);
						return F.binaryAST2(head, arg1, arg2);
					}
				}
			}
		}
		throw new AbortException();
	}

	private IExpr munderover(NodeList list, NodeList parentList, int[] position, int precedence) {
		// \\int_0^\\infty a dx
		if (list.getLength() > 0) {
			Node arg0 = list.item(0);
			IExpr head = toExpr(arg0);
			if (head.isBuiltInSymbol()) {
				ISymbol sym = F.Dummy("munderover$" + counter++);
				IExpr arg2 = sym;
				if (list.getLength() >= 2) {
					Node arg1 = list.item(1);
					IExpr a1 = toExpr(arg1);
					if (a1.isEqual() && a1.first().isSymbol()) {
						sym = (ISymbol) a1.first();
						arg2 = sym;
						a1 = a1.second();
					}
					if (list.getLength() == 3) {
						IExpr a2 = toExpr(list.item(2));
						arg2 = F.List(sym, a1, a2);
					} else if (list.getLength() == 2) {
						arg2 = F.List(sym, a1);
					}
				}

				if (position[0] < parentList.getLength()) {
					IExpr arg1 = getNodeList(parentList, position, null, Integer.MAX_VALUE);
					return F.binaryAST2(head, arg1, arg2);
				}
			}
		}
		throw new AbortException();
	}

	private IExpr msup(NodeList list) {
		if (list.getLength() == 2) {
			Node arg1 = list.item(0);
			IExpr a1 = toExpr(arg1);
			Node arg2 = list.item(1);
			IExpr a2 = toExpr(arg2);
			if (a1.isBuiltInSymbol() && a2.isMinusOne()) {
				IExpr value = F.UNARY_INVERSE_FUNCTIONS.get(a1);
				if (value != null) {
					// typically Sin^(-1) -> ArcSin or similar...
					return value;
				}
			}
			if (a2.equals(F.Degree)) {
				// case \operatorname { sin } 30 ^ { \circ } ==> Sin(30*Degree)
				return F.Times(a1, a2);
			}
			return F.Power(a1, a2);
		}
		throw new AbortException();
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

}
