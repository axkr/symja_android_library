package org.matheclipse.core.form.output;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;

/**
 * Converts an internal <code>IExpr</code> into a user readable string.
 * 
 */
public class OutputFormFactory {

	private final boolean fRelaxedSyntax;
	private boolean fIgnoreNewLine = false;
	private boolean fEmpty = true;
	private int fColumnCounter;

	private OutputFormFactory(final boolean relaxedSyntax) {
		fRelaxedSyntax = relaxedSyntax;
	}

	/**
	 * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable string.
	 * 
	 * @param relaxedSyntax
	 *            If <code>true</code> use paranthesis instead of square brackets and ignore case for functions, i.e. sin() instead
	 *            of Sin[]. If <code>true</code> use single square brackets instead of double square brackets for extracting parts
	 *            of an expression, i.e. {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
	 * @return
	 */
	public static OutputFormFactory get(final boolean relaxedSyntax) {
		return new OutputFormFactory(relaxedSyntax);
	}

	/**
	 * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable string, with
	 * <code>relaxedSyntax</code> set to false.
	 * 
	 * @return
	 * @see #get(boolean)
	 */
	public static OutputFormFactory get() {
		return get(false);
	}

	public void convertDouble(final Appendable buf, final INum d, final int precedence) throws IOException {
		if (d.isZero()) {
			convertDoubleValue(buf, "0.0", precedence, false);
			return;
		}
		final boolean isNegative = d.isNegative();

		convertDoubleValue(buf, d.toString(), precedence, isNegative);
	}

	private void convertDoubleValue(final Appendable buf, final String d, final int precedence, final boolean isNegative)
			throws IOException {
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, "(");
		}
		append(buf, d);
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, ")");
		}
	}

	public void convertDoubleComplex(final Appendable buf, final IComplexNum dc, final int precedence) throws IOException {
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			append(buf, "(");
		}
		double realPart = dc.getRealPart();
		double imaginaryPart = dc.getImaginaryPart();
		boolean realZero = F.isZero(realPart);
		boolean imaginaryZero = F.isZero(imaginaryPart);
		if (realZero && imaginaryZero) {
			convertDoubleValue(buf, "0.0", ASTNodeFactory.PLUS_PRECEDENCE, false);
		} else {
			if (!realZero) {
				append(buf, String.valueOf(realPart));
				if (!imaginaryZero) {
					append(buf, "+I*");
					final boolean isNegative = dc.getImaginaryPart() < 0;
					convertDoubleValue(buf, String.valueOf(imaginaryPart), ASTNodeFactory.TIMES_PRECEDENCE, isNegative);
				}
			} else {
				append(buf, "I*");
				final boolean isNegative = dc.getImaginaryPart() < 0;
				convertDoubleValue(buf, String.valueOf(imaginaryPart), ASTNodeFactory.TIMES_PRECEDENCE, isNegative);
			}
		}
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			append(buf, ")");
		}
	}

	public void convertInteger(final Appendable buf, final IInteger i, final int precedence) throws IOException {
		final boolean isNegative = i.isNegative();

		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, "(");
		}
		final String str = i.getBigNumerator().toString();
		if ((str.length() + getColumnCounter() > 80)) {
			if (getColumnCounter() > 40) {
				newLine(buf);
			}
			final int len = str.length();
			for (int j = 0; j < len; j += 79) {
				if (j + 79 < len) {
					append(buf, str.substring(j, j + 79));
					append(buf, '\\');
					newLine(buf);
				} else {
					append(buf, str.substring(j, len));
				}
			}
		} else {
			append(buf, str);
		}
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, ")");
		}
	}

	public void convertFraction(final Appendable buf, final BigFraction f, final int precedence) throws IOException {
		boolean isInteger = f.getDenominator().compareTo(BigInteger.ONE) == 0;
		final boolean isNegative = f.getNumerator().compareTo(BigInteger.ZERO) < 0;
		final int prec = isNegative ? ASTNodeFactory.PLUS_PRECEDENCE : ASTNodeFactory.TIMES_PRECEDENCE;
		if (prec < precedence) {
			append(buf, "(");
		}
		// append(buf, f.getNumerator().toString());

		String str = f.getNumerator().toString();
		if ((str.length() + getColumnCounter() > 80)) {
			if (getColumnCounter() > 40) {
				newLine(buf);
			}
			final int len = str.length();
			for (int j = 0; j < len; j += 79) {
				if (j + 79 < len) {
					append(buf, str.substring(j, j + 79));
					append(buf, '\\');
					newLine(buf);
				} else {
					append(buf, str.substring(j, len));
				}
			}
		} else {
			append(buf, str);
		}
		if (!isInteger) {
			append(buf, "/");
			// append(buf, f.getDenominator().toString());
			str = f.getDenominator().toString();
			if ((str.length() + getColumnCounter() > 80)) {
				if (getColumnCounter() > 40) {
					newLine(buf);
				}
				final int len = str.length();
				for (int j = 0; j < len; j += 79) {
					if (j + 79 < len) {
						append(buf, str.substring(j, j + 79));
						append(buf, '\\');
						newLine(buf);
					} else {
						append(buf, str.substring(j, len));
					}
				}
			} else {
				append(buf, str);
			}
		}
		if (prec < precedence) {
			append(buf, ")");
		}
	}

	public void convertComplex(final Appendable buf, final IComplex c, final int precedence) throws IOException {
		boolean isReZero = c.getRealPart().compareTo(BigFraction.ZERO) == 0;
		final boolean isImOne = c.getImaginaryPart().compareTo(BigFraction.ONE) == 0;
		final boolean isImMinusOne = c.getImaginaryPart().equals(BigFraction.MINUS_ONE);
		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, "(");
		}
		if (!isReZero) {
			convertFraction(buf, c.getRealPart(), ASTNodeFactory.PLUS_PRECEDENCE);
		}
		if (isImOne) {
			if (isReZero) {
				append(buf, "I");
				return;
			} else {
				append(buf, "+I");
			}
		} else if (isImMinusOne) {
			append(buf, "-I");
		} else {
			if (isReZero && (ASTNodeFactory.TIMES_PRECEDENCE < precedence)) {
				append(buf, "(");
			}
			final BigFraction im = c.getImaginaryPart();
			if (NumberUtil.isNegative(im)) {
				append(buf, "-I*");
				convertFraction(buf, c.getImaginaryPart().negate(), ASTNodeFactory.TIMES_PRECEDENCE);
			} else {
				if (isReZero) {
					append(buf, "I*");
				} else {
					append(buf, "+I*");
				}
				convertFraction(buf, c.getImaginaryPart(), ASTNodeFactory.TIMES_PRECEDENCE);
			}
			if (isReZero && (ASTNodeFactory.TIMES_PRECEDENCE < precedence)) {
				append(buf, ")");
			}
		}

		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			append(buf, ")");
		}
	}

	public void convertString(final Appendable buf, final String str) throws IOException {
		append(buf, "\"");
		append(buf, str);
		append(buf, "\"");
	}

	public void convertSymbol(final Appendable buf, final ISymbol symbol) throws IOException {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbol.getSymbolName());
			if (str != null) {
				append(buf, str);
				return;
			}
		}
		append(buf, symbol.getSymbolName());
	}

	public void convertPattern(final Appendable buf, final IPatternObject pattern) throws IOException {
		append(buf, pattern.toString());
	}

	public void convertHead(final Appendable buf, final IExpr obj) throws IOException {
		convert(buf, obj);
	}

	private void convertPlusOperator(final Appendable buf, final IAST plusAST, final InfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			append(buf, "(");
		}

		IExpr temp;
		int size = plusAST.size() - 1;
		// print Plus[] in reverse order (i.e. numbers at last)
		for (int i = size; i > 0; i--) {
			temp = plusAST.get(i);

			if (temp.isTimes()) {
				final String multCh = ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getOperatorString();
				boolean flag = false;
				final IAST multFun = (IAST) temp;
				IExpr temp1 = multFun.get(1);

				if (temp1.isNumber() && (((INumber) temp1).complexSign() < 0)) {
					if (((INumber) temp1).equalsInt(1)) {
						flag = true;
					} else {
						if (((INumber) temp1).equalsInt(-1)) {
							append(buf, "-");
							flag = true;
						} else {
							convertNumber(buf, (INumber) temp1, oper.getPrecedence());
						}
					}
				} else {
					if (i < size) {
						append(buf, oper.getOperatorString());
					}
					convert(buf, temp1, ASTNodeFactory.TIMES_PRECEDENCE);
				}

				for (int j = 2; j < multFun.size(); j++) {
					temp1 = multFun.get(j);

					if ((j > 2) || (!flag)) {
						append(buf, multCh);
					}

					convert(buf, temp1, ASTNodeFactory.TIMES_PRECEDENCE);

				}
			} else {
				if (temp.isNumber() && (((INumber) temp).complexSign() < 0)) {
					// special case negative number:
					convert(buf, temp);
				} else {
					if (i < size) {
						append(buf, oper.getOperatorString());
					}

					convert(buf, temp, ASTNodeFactory.PLUS_PRECEDENCE);

				}
			}
		}

		if (oper.getPrecedence() < precedence) {
			append(buf, ")");
		}
	}

	private void convertTimesOperator(final Appendable buf, final IAST list, final InfixOperator oper, final int precedence)
			throws IOException {
		boolean showOperator = true;
		int currPrecedence = oper.getPrecedence();
		if (currPrecedence < precedence) {
			append(buf, "(");
		}

		if (list.size() > 1) {
			if (list.get(1).isSignedNumber() && list.size() > 2 && !list.get(2).isNumber()) {
				if (list.get(1).equals(F.CN1)) {
					append(buf, "-");
					showOperator = false;
				} else {
					if (((ISignedNumber) list.get(1)).isNegative()) {
						convertNumber(buf, (INumber) list.get(1), oper.getPrecedence());
					} else {
						convert(buf, list.get(1), oper.getPrecedence());
					}
				}
			} else {
				convert(buf, list.get(1), oper.getPrecedence());
			}
		}
		for (int i = 2; i < list.size(); i++) {
			if (showOperator) {
				append(buf, oper.getOperatorString());
			} else {
				showOperator = true;
			}
			convert(buf, list.get(i), oper.getPrecedence());
		}
		if (currPrecedence < precedence) {
			append(buf, ")");
		}
	}

	public void convertBinaryOperator(final Appendable buf, final IAST list, final InfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			append(buf, "(");
		}
		if (list.size() > 1) {
			convert(buf, list.get(1), oper.getPrecedence());
		}
		for (int i = 2; i < list.size(); i++) {
			append(buf, oper.getOperatorString());
			convert(buf, list.get(i), oper.getPrecedence());
		}
		if (oper.getPrecedence() < precedence) {
			append(buf, ")");
		}
	}

	public void convertPrefixOperator(final Appendable buf, final IAST list, final PrefixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			append(buf, "(");
		}
		append(buf, oper.getOperatorString());
		convert(buf, list.get(1), oper.getPrecedence());
		if (oper.getPrecedence() < precedence) {
			append(buf, ")");
		}
	}

	public void convertPostfixOperator(final Appendable buf, final IAST list, final PostfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			append(buf, "(");
		}
		convert(buf, list.get(1), oper.getPrecedence());
		append(buf, oper.getOperatorString());
		if (oper.getPrecedence() < precedence) {
			append(buf, ")");
		}
	}

	public void convert(final Appendable buf, final IExpr o) throws IOException {
		convert(buf, o, Integer.MIN_VALUE);
	}

	public void convertNumber(final Appendable buf, final INumber o, final int precedence) throws IOException {
		if (o instanceof INum) {
			convertDouble(buf, (INum) o, precedence);
			return;
		}
		if (o instanceof IComplexNum) {
			convertDoubleComplex(buf, (IComplexNum) o, precedence);
			return;
		}
		if (o instanceof IInteger) {
			convertInteger(buf, (IInteger) o, precedence);
			return;
		}
		if (o instanceof IFraction) {
			convertFraction(buf, ((IFraction) o).getRational(), precedence);
			return;
		}
		if (o instanceof IComplex) {
			convertComplex(buf, (IComplex) o, precedence);
			return;
		}
	}

	public void convert(final Appendable buf, final IExpr o, final int precedence) throws IOException {
		if (o instanceof IAST) {
			final IAST list = (IAST) o;
			ISymbol head = list.topHead();
			String header = head.getSymbolName();
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(header);
				if (str != null) {
					header = str;
				}
			}
			final Operator operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(header);
			if (operator != null) {
				if ((operator instanceof PrefixOperator) && (list.size() == 2)) {
					convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
					return;
				}
				if ((operator instanceof InfixOperator) && (list.size() > 2)) {
					if (head.equals(F.Plus)) {
						convertPlusOperator(buf, list, (InfixOperator) operator, precedence);
						return;
					} else if (head.equals(F.Times)) {
						convertTimesOperator(buf, list, (InfixOperator) operator, precedence);
						return;
					}
					convertBinaryOperator(buf, list, (InfixOperator) operator, precedence);
					return;
				}
				if ((operator instanceof PostfixOperator) && (list.size() == 2)) {
					convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
					return;
				}
			}
			if (list.isList()) { // header.equals(List)) {
				convertList(buf, list);
				return;
			}
			if (head.equals(F.Part) && (list.size() >= 3)) {
				convertPart(buf, list);
				return;
			}
			if (head.equals(F.Slot) && (list.size() == 2) && (list.get(1) instanceof IInteger)) {
				convertSlot(buf, list);
				return;
			}
			if (head.equals(F.Hold) && (list.size() == 2)) {
				convert(buf, list.get(1));
				return;
			}
			if (list.isDirectedInfinity()) { // head.equals(F.DirectedInfinity)) {
				if (list.size() == 1) {
					append(buf, "ComplexInfinity");
					return;
				}
				if (list.size() == 2) {
					if (list.get(1).equals(F.C1)) {
						append(buf, "Infinity");
						return;
					} else if (list.get(1).equals(F.CN1)) {
						if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
							append(buf, "(");
						}
						append(buf, "-Infinity");
						if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
							append(buf, ")");
						}
						return;
					}
				}
			}
			convertAST(buf, list);
			return;
		}
		if (o instanceof ISignedNumber) {
			convertNumber(buf, (ISignedNumber) o, precedence);
			return;
		}
		if (o instanceof IComplexNum) {
			convertDoubleComplex(buf, (IComplexNum) o, precedence);
			return;
		}
		if (o instanceof IComplex) {
			convertComplex(buf, (IComplex) o, precedence);
			return;
		}
		if (o instanceof ISymbol) {
			convertSymbol(buf, (ISymbol) o);
			return;
		}
		if (o instanceof IPatternObject) {
			convertPattern(buf, (IPatternObject) o);
			return;
		}
		// if (o instanceof BigFraction) {
		// convertFraction(buf, (BigFraction) o, precedence);
		// }
		convertString(buf, o.toString());
	}

	public void convertSlot(final Appendable buf, final IAST list) throws IOException {
		try {
			final int slot = ((ISignedNumber) list.get(1)).toInt();
			append(buf, "#" + slot);
		} catch (final ArithmeticException e) {
			// add message to evaluation problemReporter
		}
	}

	public void convertList(final Appendable buf, final IAST list) throws IOException {
		if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
			if (!fEmpty) {
				newLine(buf);
			}
		}
		append(buf, "{");
		final int listSize = list.size();
		if (listSize > 1) {
			convert(buf, list.get(1));
		}
		for (int i = 2; i < listSize; i++) {
			append(buf, ",");
			if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
				newLine(buf);
				append(buf, ' ');

			}
			convert(buf, list.get(i));
		}
		append(buf, "}");
	}

	/**
	 * This method will only be called if <code>list.size() == 3</code> and the head equals "Part".
	 * 
	 * @param buf
	 * @param list
	 * @throws IOException
	 */
	public void convertPart(final Appendable buf, final IAST list) throws IOException {
		IExpr arg1 = list.get(1);
		if (!(arg1 instanceof IAST)) {
			append(buf, "(");
		}
		convert(buf, list.get(1));
		// if (fRelaxedSyntax) {
		// append(buf, "[");
		// } else {
		append(buf, "[[");
		// }
		for (int i = 2; i < list.size(); i++) {
			convert(buf, list.get(i));
			if (i < list.size() - 1) {
				append(buf, ",");
			}
		}

		// if (fRelaxedSyntax) {
		// append(buf, "]");
		// } else {
		append(buf, "]]");
		// }
		if (!(arg1 instanceof IAST)) {
			append(buf, ")");
		}
	}

	/**
	 * Write a function into the given <code>Appendable</code>.
	 * 
	 * @param buf
	 * @param function
	 * @throws IOException
	 */
	public void convertAST(final Appendable buf, final IAST function) throws IOException {
		IExpr head = function.head();
		convert(buf, function.head());
		if (head.isAST()) {
			append(buf, "[");
		} else if (fRelaxedSyntax) {
			append(buf, "(");
		} else {
			append(buf, "[");
		}
		final int functionSize = function.size();
		if (functionSize > 1) {
			convert(buf, function.get(1));
		}
		for (int i = 2; i < functionSize; i++) {
			append(buf, ",");
			convert(buf, function.get(i));
		}
		if (head.isAST()) {
			append(buf, "]");
		} else if (fRelaxedSyntax) {
			append(buf, ")");
		} else {
			append(buf, "]");
		}
	}

	/**
	 * this resets the columnCounter to offset 0
	 * 
	 */
	private void newLine(Appendable buf) throws IOException {
		if (!fIgnoreNewLine) {
			append(buf, '\n');
		}
		fColumnCounter = 0;
		fEmpty = false;
	}

	private void append(Appendable buf, String str) throws IOException {
		buf.append(str);
		fColumnCounter += str.length();
		fEmpty = false;
	}

	private void append(Appendable buf, char c) throws IOException {
		buf.append(c);
		fColumnCounter += 1;
		fEmpty = false;
	}

	/**
	 * @param ignoreNewLine
	 *            The ignoreNewLine to set.
	 */
	public void setIgnoreNewLine(final boolean ignoreNewLine) {
		fIgnoreNewLine = ignoreNewLine;
	}

	public void setEmpty(final boolean empty) {
		fEmpty = empty;
	}

	/**
	 * @return Returns the columnCounter.
	 */
	public int getColumnCounter() {
		return fColumnCounter;
	}

	/**
	 * @param columnCounter
	 *            The columnCounter to set.
	 */
	public void setColumnCounter(final int columnCounter) {
		fColumnCounter = columnCounter;
	}
}
