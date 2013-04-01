package org.matheclipse.core.form.output;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
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
public class OutputFormFactory implements IConstantHeaders {

	private final boolean fRelaxedSyntax;

	private OutputFormFactory(final boolean relaxedSyntax) {
		fRelaxedSyntax = relaxedSyntax;
	}

	/**
	 * Get an <code>OutputFormFactory</code> for converting an internal expression
	 * to a user readable string.
	 * 
	 * @param relaxedSyntax
	 *          If <code>true</code> use paranthesis instead of square brackets
	 *          and ignore case for functions, i.e. sin() instead of Sin[]. If
	 *          <code>true</code> use single square brackets instead of double
	 *          square brackets for extracting parts of an expression, i.e.
	 *          {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
	 * @return
	 */
	public static OutputFormFactory get(final boolean relaxedSyntax) {
		return new OutputFormFactory(relaxedSyntax);
	}

	/**
	 * Get an <code>OutputFormFactory</code> for converting an internal expression
	 * to a user readable string, with <code>relaxedSyntax</code> set to false.
	 * 
	 * @return
	 * @see #get(boolean)
	 */
	public static OutputFormFactory get() {
		return get(false);
	}

	public void convertDouble(final Writer buf, final INum d, final int precedence) throws IOException {
		final boolean isNegative = d.isNegative();

		convertDoubleValue(buf, d.toString(), precedence, isNegative);
	}

	private void convertDoubleValue(final Writer buf, final String d, final int precedence, final boolean isNegative)
			throws IOException {
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write("(");
		}
		buf.write(d);
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write(")");
		}
	}

	public void convertDoubleComplex(final Writer buf, final IComplexNum dc, final int precedence) throws IOException {
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			buf.write("(");
		}
		double realPart = dc.getRealPart();
		buf.write(String.valueOf(realPart));
		buf.write("+I*");
		final boolean isNegative = dc.getImaginaryPart() < 0;
		convertDoubleValue(buf, String.valueOf(dc.getImaginaryPart()), ASTNodeFactory.TIMES_PRECEDENCE, isNegative);
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			buf.write(")");
		}
	}

	public void convertInteger(final Writer buf, final IInteger i, final int precedence) throws IOException {
		final boolean isNegative = i.isNegative();
		StringBufferWriter sbw = null;
		if (buf instanceof StringBufferWriter) {
			sbw = (StringBufferWriter) buf;
		}
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write("(");
		}
		final String str = i.getBigNumerator().toString();
		if (sbw != null && (str.length() + sbw.getColumnCounter() > 80)) {
			if (sbw.getColumnCounter() > 40) {
				sbw.newLine();
			}
			final int len = str.length();
			for (int j = 0; j < len; j += 79) {
				if (j + 79 < len) {
					sbw.write(str.substring(j, j + 79));
					sbw.write('\\');
					sbw.newLine();
				} else {
					buf.write(str.substring(j, len));
				}
			}
		} else {
			buf.write(str);
		}
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write(")");
		}
	}

	public void convertFraction(final Writer buf, final BigFraction f, final int precedence) throws IOException {
		boolean isInteger = f.getDenominator().compareTo(BigInteger.ONE) == 0;
		final boolean isNegative = f.getNumerator().compareTo(BigInteger.ZERO) < 0;
		final int prec = isNegative ? ASTNodeFactory.PLUS_PRECEDENCE : ASTNodeFactory.TIMES_PRECEDENCE;
		if (prec < precedence) {
			buf.write("(");
		}
		// buf.write(f.getNumerator().toString());
		StringBufferWriter sbw = null;
		if (buf instanceof StringBufferWriter) {
			sbw = (StringBufferWriter) buf;
		}
		String str = f.getNumerator().toString();
		if (sbw != null && (str.length() + sbw.getColumnCounter() > 80)) {
			if (sbw.getColumnCounter() > 40) {
				sbw.newLine();
			}
			final int len = str.length();
			for (int j = 0; j < len; j += 79) {
				if (j + 79 < len) {
					buf.write(str.substring(j, j + 79));
					buf.write('\\');
					sbw.newLine();
				} else {
					buf.write(str.substring(j, len));
				}
			}
		} else {
			buf.write(str);
		}
		if (!isInteger) {
			buf.write("/");
			// buf.write(f.getDenominator().toString());
			str = f.getDenominator().toString();
			if (sbw != null && (str.length() + sbw.getColumnCounter() > 80)) {
				if (sbw.getColumnCounter() > 40) {
					sbw.newLine();
				}
				final int len = str.length();
				for (int j = 0; j < len; j += 79) {
					if (j + 79 < len) {
						buf.write(str.substring(j, j + 79));
						buf.write('\\');
						sbw.newLine();
					} else {
						buf.write(str.substring(j, len));
					}
				}
			} else {
				buf.write(str);
			}
		}
		if (prec < precedence) {
			buf.write(")");
		}
	}

	public void convertComplex(final Writer buf, final IComplex c, final int precedence) throws IOException {
		boolean isReZero = c.getRealPart().compareTo(BigFraction.ZERO) == 0;
		final boolean isImOne = c.getImaginaryPart().compareTo(BigFraction.ONE) == 0;
		final boolean isImMinusOne = c.getImaginaryPart().equals(BigFraction.MINUS_ONE);
		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write("(");
		}
		if (!isReZero) {
			convertFraction(buf, c.getRealPart(), ASTNodeFactory.PLUS_PRECEDENCE);
		}
		if (isImOne) {
			if (isReZero) {
				buf.write("I");
				return;
			} else {
				buf.write("+I");
			}
		} else if (isImMinusOne) {
			buf.write("-I");
		} else {
			if (isReZero && (ASTNodeFactory.TIMES_PRECEDENCE < precedence)) {
				buf.write("(");
			}
			final BigFraction im = c.getImaginaryPart();
			if (NumberUtil.isNegative(im)) {
				buf.write("-I*");
				convertFraction(buf, c.getImaginaryPart().negate(), ASTNodeFactory.TIMES_PRECEDENCE);
			} else {
				if (isReZero) {
					buf.write("I*");
				} else {
					buf.write("+I*");
				}
				convertFraction(buf, c.getImaginaryPart(), ASTNodeFactory.TIMES_PRECEDENCE);
			}
			if (isReZero && (ASTNodeFactory.TIMES_PRECEDENCE < precedence)) {
				buf.write(")");
			}
		}

		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.write(")");
		}
	}

	public void convertString(final Writer buf, final String str) throws IOException {
		buf.write("\"");
		buf.write(str);
		buf.write("\"");
	}

	public void convertSymbol(final Writer buf, final ISymbol sym) throws IOException {
		buf.write(sym.toString());
	}

	public void convertPattern(final Writer buf, final IPattern sym) throws IOException {
		buf.write(sym.toString());
	}

	public void convertHead(final Writer buf, final IExpr obj) throws IOException {
		convert(buf, obj);
	}

	private void convertPlusOperator(final Writer buf, final IAST plusAST, final InfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			buf.write("(");
		}

		IExpr temp;
		int size = plusAST.size() - 1;
		for (int i = size; i > 0; i--) {
			temp = plusAST.get(i);

			if ((temp instanceof IAST) && (((IAST) temp).size() > 1) && ((IAST) temp).topHead().toString().equals(Times)) {
				// final int multPrec = OperatorTable.timesBin.getPrecedence();
				final String multCh = ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getOperatorString();
				boolean flag = false;
				final IAST multFun = (IAST) temp;
				IExpr temp1 = multFun.get(1);

				if ((temp1 instanceof INumber) && (((INumber) temp1).complexSign() < 0)) {
					// ((ISignedNumber) temp1).isNegative()) {
					if (((INumber) temp1).equalsInt(1)) {
						flag = true;
					} else {
						if (((INumber) temp1).equalsInt(-1)) {
							buf.write("-");
							flag = true;
						} else {
							convertNumber(buf, (INumber) temp1, oper.getPrecedence());
						}
					}
				} else {
					if (i < size) {
						buf.write(oper.getOperatorString());
					}
					convert(buf, temp1, ASTNodeFactory.TIMES_PRECEDENCE);
				}

				for (int j = 2; j < multFun.size(); j++) {
					temp1 = multFun.get(j);

					if ((j > 2) || (!flag)) {
						buf.write(multCh);
					}

					convert(buf, temp1, ASTNodeFactory.TIMES_PRECEDENCE);

				}
			} else {
				if ((temp instanceof ISignedNumber) && ((ISignedNumber) temp).isNegative()) {
					// special case negative number:
					convert(buf, temp);
				} else {
					if (i < size) {
						buf.write(oper.getOperatorString());
					}

					convert(buf, temp, ASTNodeFactory.PLUS_PRECEDENCE);

				}
			}
		}

		if (oper.getPrecedence() < precedence) {
			buf.write(")");
		}
	}

	private void convertTimesOperator(final Writer buf, final IAST list, final InfixOperator oper, final int precedence)
			throws IOException {
		boolean showOperator = true;
		int currPrecedence = oper.getPrecedence();
		if (currPrecedence < precedence) {
			buf.write("(");
		}

		if (list.size() > 1) {
			if (list.get(1).isSignedNumber() && list.size() > 2 && !list.get(2).isNumber()) {
				if (list.get(1).equals(F.CN1)) {
					buf.write("-");
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
				buf.write(oper.getOperatorString());
			} else {
				showOperator = true;
			}
			convert(buf, list.get(i), oper.getPrecedence());
		}
		if (currPrecedence < precedence) {
			buf.write(")");
		}
	}

	public void convertBinaryOperator(final Writer buf, final IAST list, final InfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			buf.write("(");
		}
		if (list.size() > 1) {
			convert(buf, list.get(1), oper.getPrecedence());
		}
		for (int i = 2; i < list.size(); i++) {
			buf.write(oper.getOperatorString());
			convert(buf, list.get(i), oper.getPrecedence());
		}
		if (oper.getPrecedence() < precedence) {
			buf.write(")");
		}
	}

	public void convertPrefixOperator(final Writer buf, final IAST list, final PrefixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			buf.write("(");
		}
		buf.write(oper.getOperatorString());
		convert(buf, list.get(1), oper.getPrecedence());
		if (oper.getPrecedence() < precedence) {
			buf.write(")");
		}
	}

	public void convertPostfixOperator(final Writer buf, final IAST list, final PostfixOperator oper, final int precedence)
			throws IOException {
		if (oper.getPrecedence() < precedence) {
			buf.write("(");
		}
		convert(buf, list.get(1), oper.getPrecedence());
		buf.write(oper.getOperatorString());
		if (oper.getPrecedence() < precedence) {
			buf.write(")");
		}
	}

	public void convert(final Writer buf, final IExpr o) throws IOException {
		convert(buf, o, Integer.MIN_VALUE);
	}

	public void convertNumber(final Writer buf, final INumber o, final int precedence) throws IOException {
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

	public void convert(final Writer buf, final IExpr o, final int precedence) throws IOException {
		if (o instanceof IAST) {
			final IAST list = (IAST) o;
			final String header = list.topHead().toString();
			final Operator operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(header);
			if (operator != null) {
				if ((operator instanceof PrefixOperator) && (list.size() == 2)) {
					convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
					return;
				}
				if ((operator instanceof InfixOperator) && (list.size() > 2)) {
					if (header.equals(Plus)) {
						convertPlusOperator(buf, list, (InfixOperator) operator, precedence);
						return;
					} else if (header.equals(Times)) {
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
			if (header.equals(Part) && (list.size() == 3)) {
				convertPart(buf, list);
				return;
			}
			if (header.equals(Slot) && (list.size() == 2) && (list.get(1) instanceof IInteger)) {
				convertSlot(buf, list);
				return;
			}
			if (header.equals(Hold) && (list.size() == 2)) {
				convert(buf, list.get(1));
				return;
			}
			if (header.equals(DirectedInfinity)) {
				if (list.size() == 1) {
					buf.write("ComplexInfinity");
					return;
				}
				if (list.size() == 2) {
					if (list.get(1).equals(F.C1)) {
						buf.write("Infinity");
						return;
					} else if (list.get(1).equals(F.CN1)) {
						if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
							buf.write("(");
						}
						buf.write("-Infinity");
						if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
							buf.write(")");
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
		if (o instanceof IPattern) {
			convertPattern(buf, (IPattern) o);
			return;
		}
		// if (o instanceof BigFraction) {
		// convertFraction(buf, (BigFraction) o, precedence);
		// }
		convertString(buf, o.toString());
	}

	public void convertSlot(final Writer buf, final IAST list) throws IOException {
		try {
			final int slot = ((IInteger) list.get(1)).toInt();
			buf.write("#" + slot);
		} catch (final ArithmeticException e) {
			// add message to evaluation problemReporter
		}
	}

	public void convertList(final Writer buf, final IAST list) throws IOException {
		if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
			if (buf instanceof StringBufferWriter) {
				StringBufferWriter sbw = (StringBufferWriter) buf;
				if (!sbw.isEmpty()) {
					sbw.newLine();
				}
			}
		}
		buf.write("{");
		final int listSize = list.size();
		if (listSize > 1) {
			convert(buf, list.get(1));
		}
		for (int i = 2; i < listSize; i++) {
			buf.write(",");
			if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
				if (buf instanceof StringBufferWriter) {
					StringBufferWriter sbw = (StringBufferWriter) buf;
					sbw.newLine();
					sbw.write(' ');
				} else {
					buf.write("\n ");
				}
			}
			convert(buf, list.get(i));
		}
		buf.write("}");
	}

	/**
	 * This method will only be called if <code>list.size() == 3</code> and the
	 * head equals "Part".
	 * 
	 * @param buf
	 * @param list
	 * @throws IOException
	 */
	public void convertPart(final Writer buf, final IAST list) throws IOException {
		IExpr arg1 = list.get(1);
		if (!(arg1 instanceof IAST)) {
			buf.write("(");
		}
		convert(buf, list.get(1));
		if (fRelaxedSyntax) {
			buf.write("[");
		} else {
			buf.write("[[");
		}
		convert(buf, list.get(2));
		if (fRelaxedSyntax) {
			buf.write("]");
		} else {
			buf.write("]]");
		}
		if (!(arg1 instanceof IAST)) {
			buf.write(")");
		}
	}

	/**
	 * Write a function into the given <code>Writer</code>.
	 * 
	 * @param buf
	 * @param function
	 * @throws IOException
	 */
	public void convertAST(final Writer buf, final IAST function) throws IOException {
		convert(buf, function.head());
		if (fRelaxedSyntax) {
			buf.write("(");
		} else {
			buf.write("[");
		}
		final int functionSize = function.size();
		if (functionSize > 1) {
			convert(buf, function.get(1));
		}
		for (int i = 2; i < functionSize; i++) {
			buf.write(",");
			convert(buf, function.get(i));
		}
		if (fRelaxedSyntax) {
			buf.write(")");
		} else {
			buf.write("]");
		}
	}
}
