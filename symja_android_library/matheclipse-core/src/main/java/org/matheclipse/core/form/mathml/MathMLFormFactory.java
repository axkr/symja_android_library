package org.matheclipse.core.form.mathml;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.PrefixOperator;

/**
 * Generates MathML presentation output
 * 
 */
public class MathMLFormFactory extends AbstractMathMLFormFactory {

	private final class Abs extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			// fFactory.tag(buf, "mo", "&LeftBracketingBar;");
			fFactory.tag(buf, "mo", "&#10072;");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			// fFactory.tag(buf, "mo", "&RightBracketingBar;");
			fFactory.tag(buf, "mo", "&#10072;");
			fFactory.tagEnd(buf, "mrow");

			return true;
		}
	}

	private static abstract class AbstractConverter implements IConverter {
		protected AbstractMathMLFormFactory fFactory;

		public AbstractConverter() {
		}

		/**
		 * @param factory
		 */
		@Override
		public void setFactory(final AbstractMathMLFormFactory factory) {
			fFactory = factory;
		}

	}

	private final static class Binomial extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			// "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
			if (f.size() != 3) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			fFactory.tag(buf, "mo", "(");
			fFactory.tagStart(buf, "mfrac", "linethickness=\"0\"");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false);
			fFactory.tagEnd(buf, "mfrac");
			fFactory.tag(buf, "mo", ")");
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	private final static class Ceiling extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			// LeftCeiling &#002308;
			fFactory.tag(buf, "mo", "&#x2308;");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			// RightCeiling &#x02309;
			fFactory.tag(buf, "mo", "&#x2309;");
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	private final static class D extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST2()) {
				fFactory.tagStart(buf, "mfrac");
				fFactory.tagStart(buf, "mrow");
				// &PartialD; &x02202;
				fFactory.tag(buf, "mo", "&#x2202;");

				fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
				fFactory.tagEnd(buf, "mrow");
				fFactory.tagStart(buf, "mrow");
				// &PartialD; &x02202
				fFactory.tag(buf, "mo", "&#x2202;");
				fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false);

				fFactory.tagEnd(buf, "mrow");
				fFactory.tagEnd(buf, "mfrac");
				return true;
			}
			return false;
		}
	}

	private final static class Floor extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			// &LeftFloor; &x0230A;
			fFactory.tag(buf, "mo", "&#x230A;");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			// &RightFloor; &#0230B;
			fFactory.tag(buf, "mo", "&#x230B;");
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	private final static class Function extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			// "<mrow>{0}<mo>!</mo></mrow>"
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.tag(buf, "mo", "&amp;");
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	public interface IConverter {
		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buffer
		 *            StringBuilder for MathML output
		 * @param function
		 *            the math function which should be converted to MathML
		 */
		public boolean convert(StringBuilder buffer, IAST function, int precedence);

		public void setFactory(final AbstractMathMLFormFactory factory);
	}

	private final static class Integrate extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				// &sum; &#x2211
				return iteratorStep(buf, "&#x222B;", f, 2);
			}
			return false;
		}

		public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
			if (i >= f.size()) {
				fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
				return true;
			}
			if (f.get(i).isList()) {
				IAST list = (IAST) f.get(i);
				if (list.isAST3() && list.arg1().isSymbol()) {
					ISymbol symbol = (ISymbol) list.arg1();
					fFactory.tagStart(buf, "msubsup");
					// &Integral; &#x222B;
					fFactory.tag(buf, "mo", mathSymbol);
					fFactory.convert(buf, list.arg2(), Integer.MIN_VALUE, false);
					fFactory.convert(buf, list.arg3(), Integer.MIN_VALUE, false);
					fFactory.tagEnd(buf, "msubsup");
					if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
						return false;
					}
					fFactory.tagStart(buf, "mrow");
					// &dd; &#x2146;
					fFactory.tag(buf, "mo", "&#x2146;");
					fFactory.convertSymbol(buf, symbol);
					fFactory.tagEnd(buf, "mrow");
					return true;
				}
			} else if (f.get(i).isSymbol()) {
				ISymbol symbol = (ISymbol) f.get(i);
				// &Integral; &#x222B;
				fFactory.tag(buf, "mo", mathSymbol);
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				fFactory.tagStart(buf, "mrow");
				// &dd; &#x2146;
				fFactory.tag(buf, "mo", "&#x2146;");
				fFactory.convertSymbol(buf, symbol);
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
			return false;
		}
	}

	private final static class MatrixForm extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			int[] dims = f.arg1().isMatrix();
			if (dims == null) {
				int dim = f.arg1().isVector();
				if (dim < 0) {
					return false;
				} else {
					final IAST vector = (IAST) f.arg1();
					fFactory.tagStart(buf, "mrow");
					fFactory.tag(buf, "mo", "(");
					fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

					IExpr temp;
					for (int i = 1; i < vector.size(); i++) {

						temp = vector.get(i);
						fFactory.tagStart(buf, "mtr");
						fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
						fFactory.convert(buf, temp, Integer.MIN_VALUE, false);
						fFactory.tagEnd(buf, "mtd");
						fFactory.tagEnd(buf, "mtr");
					}

					fFactory.tagEnd(buf, "mtable");
					fFactory.tag(buf, "mo", ")");
					fFactory.tagEnd(buf, "mrow");
				}
			} else {
				final IAST matrix = (IAST) f.arg1();
				fFactory.tagStart(buf, "mrow");
				fFactory.tag(buf, "mo", "(");
				fFactory.tagStart(buf, "mtable", "columnalign=\"center\"");

				IAST temp;
				for (int i = 1; i < matrix.size(); i++) {

					temp = (IAST) matrix.get(i);
					fFactory.tagStart(buf, "mtr");
					for (int j = 1; j < temp.size(); j++) {

						fFactory.tagStart(buf, "mtd", "columnalign=\"center\"");
						fFactory.convert(buf, temp.get(j), Integer.MIN_VALUE, false);
						fFactory.tagEnd(buf, "mtd");
					}
					fFactory.tagEnd(buf, "mtr");
				}

				fFactory.tagEnd(buf, "mtable");
				fFactory.tag(buf, "mo", ")");
				fFactory.tagEnd(buf, "mrow");
			}
			return true;
		}

	}

	private static class MMLFunction extends AbstractConverter {

		String fFunctionName;

		public MMLFunction(final MathMLFormFactory factory, final String functionName) {
			super();
			fFunctionName = functionName;
		}

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {

			fFactory.tagStart(buf, "mrow");
			fFactory.tag(buf, "mi", fFunctionName);
			// &af; &#x2061;
			fFactory.tag(buf, "mo", "&#x2061;");

			fFactory.tag(buf, "mo", "(");
			for (int i = 1; i < f.size(); i++) {
				fFactory.convert(buf, f.get(i), Integer.MIN_VALUE, false);
				if (i < f.argSize()) {
					fFactory.tag(buf, "mo", ",");
				}
			}
			fFactory.tag(buf, "mo", ")");
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	private static class MMLOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fFirstTag;
		protected String fOperator;

		public MMLOperator(final int precedence, final String oper) {
			this(precedence, "mrow", oper);
		}

		public MMLOperator(final int precedence, final String firstTag, final String oper) {
			fPrecedence = precedence;
			fFirstTag = firstTag;
			fOperator = oper;
		}

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			fFactory.tagStart(buf, fFirstTag);
			precedenceOpen(buf, precedence);
			for (int i = 1; i < f.size(); i++) {
				fFactory.convert(buf, f.get(i), fPrecedence, false);
				if (i < f.argSize()) {
					if (fOperator.compareTo("") != 0) {
						fFactory.tag(buf, "mo", fOperator);
					}
				}
			}
			precedenceClose(buf, precedence);
			fFactory.tagEnd(buf, fFirstTag);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				fFactory.tag(buf, "mo", ")");
				fFactory.tagEnd(buf, "mrow");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				fFactory.tagStart(buf, "mrow");
				fFactory.tag(buf, "mo", "(");
			}
		}

	}

	private final static class MMLPostfix extends AbstractConverter {

		final String fOperator;

		public MMLPostfix(final String operator) {
			super();
			fOperator = operator;
		}

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST1()) {
				fFactory.tagStart(buf, "mrow");
				fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
				fFactory.tag(buf, "mo", fOperator);
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
			return false;
		}
	}

	private final static class Not extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			// <mrow><mo>&not;</mo>{0}</mrow>
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "mrow");
			// &not; &#x00AC;
			fFactory.tag(buf, "mo", "&#x00AC;");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.tagEnd(buf, "mrow");
			return true;
		}
	}

	class Operator {
		String fOperator;

		Operator(final String oper) {
			fOperator = oper;
		}

		public void convert(final StringBuilder buf) {
			tagStart(buf, "mo");
			buf.append(fOperator);
			tagEnd(buf, "mo");
		}

		@Override
		public String toString() {
			return fOperator;
		}

	}

	private final static class Plus extends MMLOperator {

		public Plus() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "mrow", "+");
		}

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			IExpr expr;
			fFactory.tagStart(buf, fFirstTag);
			precedenceOpen(buf, precedence);
			final Times timesConverter = new Times();
			timesConverter.setFactory(fFactory);
			int size = f.argSize();
			for (int i = size; i > 0; i--) {
				expr = f.get(i);
				if ((i < size) && expr.isAST(F.Times)) {
					timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence, MathMLFormFactory.PLUS_CALL);
				} else {
					if (i < size) {
						if (expr.isReal() && expr.isNegative()) {
							fFactory.tag(buf, "mo", "-");
							expr = ((ISignedNumber) expr).negate();
						} else {
							fFactory.tag(buf, "mo", "+");
						}
					}
					fFactory.convert(buf, expr, fPrecedence, false);
				}
			}
			precedenceClose(buf, precedence);
			fFactory.tagEnd(buf, fFirstTag);
			return true;
		}

	}

	private final static class Power extends MMLOperator {

		public Power() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "msup", "");
		}

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return false;
			}
			IExpr arg1 = f.arg1();
			IExpr arg2 = f.arg2();
			IExpr exp = arg2;
			IExpr den = F.C1;
			int useMROOT = 0;
			if (arg2.isFraction()) {
				IFraction f2 = ((IFraction) arg2);
				if (f2.isPositive()) {
					exp = f2.numerator();
					if (f2.equals(F.C1D2)) {
						fFactory.tagStart(buf, "msqrt");
						useMROOT = 1;
					} else {
						den = f2.denominator();
						fFactory.tagStart(buf, "mroot");
						useMROOT = 2;
					}

				}
			}
			if (useMROOT > 0 && exp.isOne()) {
				fFactory.convert(buf, arg1, Integer.MIN_VALUE, false);
			} else {
				if (exp.isNegative()) {
					exp = exp.negate();
					fFactory.tagStart(buf, "mfrac");
					fFactory.convert(buf, F.C1, Integer.MIN_VALUE, false);
					if (exp.isOne()) {
						fFactory.convert(buf, arg1, Integer.MIN_VALUE, false);
					} else {
						convert(buf, F.Power(arg1, exp), Integer.MIN_VALUE);
					}
					fFactory.tagEnd(buf, "mfrac");
				} else {
					precedenceOpen(buf, precedence);
					fFactory.tagStart(buf, "msup");
					fFactory.convert(buf, arg1, fPrecedence, false);
					fFactory.convert(buf, exp, fPrecedence, false);
					fFactory.tagEnd(buf, "msup");
					precedenceClose(buf, precedence);
				}
			}
			if (useMROOT == 1) {
				fFactory.tagEnd(buf, "msqrt");
			} else if (useMROOT == 2) {
				fFactory.convert(buf, den, fPrecedence, false);
				fFactory.tagEnd(buf, "mroot");
			}
			return true;
		}
	}

	private final static class Product extends Sum {

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				// &Product; &#x220F;
				return iteratorStep(buf, "&#x220F;", f, 2);
			}
			return false;
		}

	}

	private final static class Rational extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 *
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return false;
			}

			// <mfrac><mi>k</mi><mn>2</mn></mfrac>
			fFactory.tagStart(buf, "mfrac");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.convert(buf, f.arg2(), Integer.MIN_VALUE, false);
			fFactory.tagEnd(buf, "mfrac");

			return true;
		}
	}

	private final static class Sqrt extends AbstractConverter {

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			fFactory.tagStart(buf, "msqrt");
			fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
			fFactory.tagEnd(buf, "msqrt");
			return true;
		}
	}

	private static class Sum extends AbstractConverter {

		public Sum() {
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				// &sum; &#x2211
				return iteratorStep(buf, "&#x2211;", f, 2);
			}
			return false;
		}

		public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
			if (i >= f.size()) {
				fFactory.convert(buf, f.arg1(), Integer.MIN_VALUE, false);
				return true;
			}
			fFactory.tagStart(buf, "mrow");
			if (f.get(i).isList()) {
				IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), EvalEngine.get());
				if (iterator.isValidVariable() && iterator.getStep().isOne()) {
					fFactory.tagStart(buf, "munderover");
					fFactory.tag(buf, "mo", mathSymbol);

					fFactory.tagStart(buf, "mrow");
					fFactory.convertSymbol(buf, iterator.getVariable());
					fFactory.tag(buf, "mo", "=");
					fFactory.convert(buf, iterator.getLowerLimit(), Integer.MIN_VALUE, false);
					fFactory.tagEnd(buf, "mrow");
					fFactory.convert(buf, iterator.getUpperLimit(), Integer.MIN_VALUE, false);
					fFactory.tagEnd(buf, "munderover");
					if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
						return false;
					}
					fFactory.tagEnd(buf, "mrow");
					return true;
				}
			} else if (f.get(i).isSymbol()) {
				ISymbol symbol = (ISymbol) f.get(i);
				fFactory.tagStart(buf, "munderover");
				fFactory.tag(buf, "mo", mathSymbol);
				fFactory.tagStart(buf, "mrow");
				fFactory.convertSymbol(buf, symbol);
				fFactory.tagEnd(buf, "mrow");
				// empty <mi> </mi>
				fFactory.tagStart(buf, "mi");
				fFactory.tagEnd(buf, "mi");

				fFactory.tagEnd(buf, "munderover");
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				fFactory.tagEnd(buf, "mrow");
				return true;
			}
			return false;
		}

	}

	private final static class Times extends MMLOperator {

		public Times() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "mrow", "&#0183;");// centerdot instead
																									// of
																									// invisibletimes:
																									// "&#8290;");
		}

		/**
		 * Converts a given function into the corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The math function which should be converted to MathML
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			return convertTimesFraction(buf, f, precedence, MathMLFormFactory.NO_PLUS_CALL);
		}

		/**
		 * Try to split a given <code>Times[...]</code> function into nominator and denominator and add the
		 * corresponding MathML output
		 * 
		 * @param buf
		 *            StringBuilder for MathML output
		 * @param f
		 *            The function which should be converted to MathML
		 * @precedence
		 * @caller
		 */
		public boolean convertTimesFraction(final StringBuilder buf, final IAST f, final int precedence,
				final boolean caller) {
			IExpr[] parts = Algebra.fractionalPartsTimesPower(f, false, true, false, false);
			if (parts == null) {
				convertTimesOperator(buf, f, precedence, caller);
				return true;
			}
			final IExpr numerator = parts[0];
			final IExpr denominator = parts[1];
			if (!denominator.isOne()) {
				// found a fraction expression
				if (caller == MathMLFormFactory.PLUS_CALL) {
					fFactory.tag(buf, "mo", "+");
				}
				fFactory.tagStart(buf, "mfrac");
				// insert numerator in buffer:
				if (numerator.isTimes()) {
					convertTimesOperator(buf, (IAST) numerator, precedence, MathMLFormFactory.NO_PLUS_CALL);
				} else {
					fFactory.convert(buf, numerator, fPrecedence, false);
				}
				if (denominator.isTimes()) {
					convertTimesOperator(buf, (IAST) denominator, precedence, MathMLFormFactory.NO_PLUS_CALL);
				} else {
					fFactory.convert(buf, denominator, Integer.MIN_VALUE, false);
				}
				fFactory.tagEnd(buf, "mfrac");
			} else {
				// if (numerator.size() <= 2) {
				if (numerator.isTimes()) {
					convertTimesOperator(buf, (IAST) numerator, precedence, caller);
				} else {
					convertTimesOperator(buf, f, precedence, caller);
				}
			}

			return true;
		}

		/**
		 * Converts a given <code>Times[...]</code> function into the corresponding MathML output.
		 * 
		 * @param buf
		 * @param timesAST
		 * @param precedence
		 * @param caller
		 * @return
		 */
		private boolean convertTimesOperator(final StringBuilder buf, final IAST timesAST, final int precedence,
				final boolean caller) {
			int size = timesAST.size();
			if (size > 1) {
				IExpr arg1 = timesAST.arg1();
				if (arg1.isMinusOne()) {
					if (size == 2) {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
						fFactory.convert(buf, arg1, fPrecedence, false);
					} else {
						if (caller == MathMLFormFactory.NO_PLUS_CALL) {
							fFactory.tagStart(buf, fFirstTag);
							fFactory.tag(buf, "mo", "-");
							if (size == 3) {
								fFactory.convert(buf, timesAST.arg2(), fPrecedence, false);
								fFactory.tagEnd(buf, fFirstTag);
								return true;
							}
							// fFactory.tagEnd(buf, fFirstTag);
							// fFactory.tagStart(buf, fFirstTag);
						} else {
							fFactory.tagStart(buf, fFirstTag);
							precedenceOpen(buf, precedence);
							fFactory.tag(buf, "mo", "-");
						}
					}
				} else if (arg1.isOne()) {
					if (size == 2) {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
						fFactory.convert(buf, arg1, fPrecedence, false);
					} else {
						if (caller == MathMLFormFactory.PLUS_CALL) {
							if (size == 3) {
								fFactory.convert(buf, timesAST.arg2(), fPrecedence, false);
								return true;
							}
							fFactory.tagStart(buf, fFirstTag);
						} else {
							fFactory.tagStart(buf, fFirstTag);
							precedenceOpen(buf, precedence);
						}
					}
				} else {
					if (caller == MathMLFormFactory.PLUS_CALL) {
						if (arg1.isReal() && arg1.isNegative()) {
							fFactory.tag(buf, "mo", "-");
							fFactory.tagStart(buf, fFirstTag);
							arg1 = ((ISignedNumber) arg1).negate();
						} else {
							fFactory.tag(buf, "mo", "+");
							fFactory.tagStart(buf, fFirstTag);
						}
					} else {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
					}
					fFactory.convert(buf, arg1, fPrecedence, false);
					if (fOperator.length() > 0) {
						fFactory.tag(buf, "mo", fOperator);
					}
				}
			}

			for (int i = 2; i < size; i++) {
				fFactory.convert(buf, timesAST.get(i), fPrecedence, false);
				if ((i < timesAST.argSize()) && (fOperator.length() > 0)) {
					fFactory.tag(buf, "mo", fOperator);
				}
			}
			precedenceClose(buf, precedence);
			fFactory.tagEnd(buf, fFirstTag);
			return true;
		}
	}

	/**
	 * The conversion wasn't called with an operator preceding the <code>IExpr</code> object.
	 */
	public final static boolean NO_PLUS_CALL = false;

	/**
	 * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code> object.
	 */
	public final static boolean PLUS_CALL = true;

	public final static HashMap<ISymbol, IConverter> CONVERTERS = new HashMap<ISymbol, IConverter>(199);

	/**
	 * Table for constant symbols
	 */
	public final static HashMap<String, Object> CONSTANT_SYMBOLS = new HashMap<String, Object>(199);

	/**
	 * Table for constant expressions
	 */
	public final static HashMap<IExpr, String> CONSTANT_EXPRS = new HashMap<IExpr, String>(199);

	/**
	 * Description of the Field
	 */
	public final static HashMap<String, AbstractConverter> OPERATORS = new HashMap<String, AbstractConverter>(199);

	private int plusPrec;

	private boolean fRelaxedSyntax;

	/**
	 * Constructor
	 */
	public MathMLFormFactory() {
		this("", null);
	}

	public MathMLFormFactory(final String tagPrefix) {
		this(tagPrefix, null);
	}

	public MathMLFormFactory(final String tagPrefix, NumberFormat numberFormat) {
		super(tagPrefix, numberFormat);
		fRelaxedSyntax = true;
		init();
	}

	@Override
	public void convert(final StringBuilder buf, final IExpr o, final int precedence, boolean isASTHead) {
		String str = CONSTANT_EXPRS.get(o);
		if (str != null) {
			buf.append(str);
			return;
		}
		if (o instanceof IAST) {
			final IAST f = ((IAST) o);
			IAST ast = f;
			IAST temp;
			if (f.topHead().hasFlatAttribute()) {
				// associative
				if ((temp = EvalAttributes.flatten(f)).isPresent()) {
					ast = temp;
				}
			}
			IExpr h = ast.head();
			if (h.isSymbol()) {
				IConverter converter = CONVERTERS.get(h);
				// IConverter converter = reflection(((ISymbol) h).getSymbolName());
				if (converter != null) {
					converter.setFactory(this);
					StringBuilder sb = new StringBuilder();
					if (converter.convert(sb, ast, precedence)) {
						buf.append(sb);
						return;
					}
				}
			}
			convertAST(buf, ast, 0);
			return;
		}
		if (convertNumber(buf, o, precedence, NO_PLUS_CALL)) {
			return;
		}
		if (o instanceof ISymbol) {
			convertSymbol(buf, (ISymbol) o);
			return;
		}
		convertString(buf, o.toString());
	}

	public void convertApcomplex(final StringBuilder buf, final Apcomplex ac, final int precedence) {
		Apfloat realPart = ac.real();
		Apfloat imaginaryPart = ac.imag();
		final boolean isImNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;

		tagStart(buf, "mrow");
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			tag(buf, "mo", "(");
		}
		tagStart(buf, "mn");
		buf.append(String.valueOf(realPart));
		tagEnd(buf, "mn");
		if (isImNegative) {
			tag(buf, "mo", "-");
			imaginaryPart = imaginaryPart.negate();
		} else {
			tag(buf, "mo", "+");
		}
		tagStart(buf, "mn");
		buf.append(String.valueOf(imaginaryPart));
		tagEnd(buf, "mn");

		// <!ENTITY InvisibleTimes "&#x2062;" >
		// <!ENTITY CenterDot "&#0183;" >
		tag(buf, "mo", "&#0183;");
		// <!ENTITY ImaginaryI "&#x2148;" >
		tag(buf, "mi", "&#x2148;");// "&#x2148;");
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	public void convertApfloat(final StringBuilder buf, final Apfloat realPart, final int precedence) {
		buf.append(String.valueOf(realPart));
	}

	public void convertArgs(final StringBuilder buf, IExpr head, final IAST function) {
		tagStart(buf, "mrow");
		if (head.isAST()) {
			// append(buf, "[");
			tag(buf, "mo", "[");
		} else if (fRelaxedSyntax) {
			// append(buf, "(");
			tag(buf, "mo", "(");
		} else {
			// append(buf, "[");
			tag(buf, "mo", "[");
		}
		final int functionSize = function.size();
		if (functionSize > 1) {
			convert(buf, function.arg1(), Integer.MIN_VALUE, false);
		}
		for (int i = 2; i < functionSize; i++) {
			tag(buf, "mo", ",");
			convert(buf, function.get(i), Integer.MIN_VALUE, false);
		}
		if (head.isAST()) {
			// append(buf, "]");
			tag(buf, "mo", "]");
		} else if (fRelaxedSyntax) {
			// append(buf, ")");
			tag(buf, "mo", ")");
		} else {
			// append(buf, "]");
			tag(buf, "mo", "]");
		}
		tagEnd(buf, "mrow");
	}

	private void convertAST(final StringBuilder buf, final IAST ast, final int precedence) {
		final IAST list = ast;
		IExpr header = list.head();
		if (!header.isSymbol()) {
			// print expressions like: f(#1, y)& [x]

			IAST[] derivStruct = list.isDerivativeAST1();
			if (derivStruct != null) {
				IAST a1Head = derivStruct[0];
				IAST headAST = derivStruct[1];
				if (a1Head.isAST1() && a1Head.arg1().isInteger() && headAST.isAST1()
						&& (headAST.arg1().isSymbol() || headAST.arg1().isAST()) && derivStruct[2] != null) {
					try {
						int n = ((IInteger) a1Head.arg1()).toInt();
						if (n == 1 || n == 2) {
							tagStart(buf, "mrow");
							IExpr symbolOrAST = headAST.arg1();
							convert(buf, symbolOrAST, Integer.MIN_VALUE, false);
							if (n == 1) {
								tag(buf, "mo", "'");
							} else if (n == 2) {
								tag(buf, "mo", "''");
							}
							convertArgs(buf, symbolOrAST, list);
							tagEnd(buf, "mrow");
							return;
						}
						if (n > 2) {
							tagStart(buf, "mrow");
							IExpr symbolOrAST = headAST.arg1();
							tagStart(buf, "msup");
							convert(buf, symbolOrAST, Integer.MIN_VALUE, false);
							tagStart(buf, "mrow");
							tag(buf, "mo", "(");
							tagStart(buf, "mn");
							buf.append(Integer.toString(n));
							tagEnd(buf, "mn");
							tag(buf, "mo", ")");
							tagEnd(buf, "mrow");
							tagEnd(buf, "msup");
							convertArgs(buf, symbolOrAST, list);
							tagEnd(buf, "mrow");
							return;
						}
					} catch (ArithmeticException ae) {

					}
				}
			}

			convert(buf, header, Integer.MIN_VALUE, false);
			convertFunctionArgs(buf, list);
			return;
		}
		ISymbol head = list.topHead();
		final org.matheclipse.parser.client.operator.Operator operator = OutputFormFactory.getOperator(head);
		if (operator != null) {
			if (operator instanceof PostfixOperator) {
				if (list.isAST1()) {
					convertPostfixOperator(buf, list, (PostfixOperator) operator, operator.getPrecedence());
					return;
				}
			} else {
				// if (convertOperator(operator, list, buf, isASTHead ? Integer.MAX_VALUE :
				// precedence, head)) {
				if (convertOperator(operator, list, buf, operator.getPrecedence(), head)) {
					return;
				}
			}
		}
		if (list instanceof ASTSeriesData) {
			if (convertSeriesData(buf, (ASTSeriesData) list, precedence)) {
				return;
			}
		}
		if (list.isList() || list instanceof ASTRealVector || list instanceof ASTRealMatrix) {
			convertList(buf, list);
			return;
		}
		if (head.equals(F.Part) && (list.size() >= 3)) {
			convertPart(buf, list);
			return;
		}

		if (head.equals(F.Slot) && (list.isAST1()) && (list.arg1() instanceof IInteger)) {
			convertSlot(buf, list);
			return;
		}
		if (head.equals(F.SlotSequence) && (list.isAST1()) && (list.arg1() instanceof IInteger)) {
			convertSlotSequence(buf, list);
			return;
		}
		if ((head.equals(F.HoldForm) || head.equals(F.Defer)) && (list.isAST1())) {
			convert(buf, list.arg1(), precedence, false);
			return;
		}
		// if (head.equals(F.SeriesData) && (list.size() == 7)) {
		// if (convertSeriesData(buf, list, precedence)) {
		// return;
		// }
		// }
		// if (list.isDirectedInfinity()) { // head.equals(F.DirectedInfinity))
		// // {
		// if (list.isAST0()) {
		// append(buf, "ComplexInfinity");
		// return;
		// }
		// if (list.isAST1()) {
		// if (list.arg1().isOne()) {
		// append(buf, "Infinity");
		// return;
		// } else if (list.arg1().isMinusOne()) {
		// if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
		// append(buf, "(");
		// }
		// append(buf, "-Infinity");
		// if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
		// append(buf, ")");
		// }
		// return;
		// } else if (list.arg1().isImaginaryUnit()) {
		// append(buf, "I*Infinity");
		// return;
		// } else if (list.arg1().isNegativeImaginaryUnit()) {
		// append(buf, "-I*Infinity");
		// return;
		// }
		// }
		// }
		// convertAST(buf, list);

		tagStart(buf, "mrow");
		convertHead(buf, ast.head());
		// &af; &#x2061;
		// tag(buf, "mo", "&#x2061;");
		tagStart(buf, "mrow");
		tag(buf, "mo", "(");
		tagStart(buf, "mrow");
		for (int i = 1; i < ast.size(); i++) {
			convert(buf, ast.get(i), Integer.MIN_VALUE, false);
			if (i < ast.argSize()) {
				tag(buf, "mo", ",");
			}
		}
		tagEnd(buf, "mrow");
		tag(buf, "mo", ")");
		tagEnd(buf, "mrow");
		tagEnd(buf, "mrow");

	}

	@Override
	public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence, boolean caller) {
		boolean isReZero = c.getRealPart().isZero();

		IRational imaginaryPart = c.getImaginaryPart();
		final boolean isImOne = imaginaryPart.isOne();
		final boolean isImNegative = imaginaryPart.isNegative();
		final boolean isImMinusOne = imaginaryPart.isMinusOne();
		if (isReZero && isImOne) {
			tagStart(buf, "mrow");
			// <!ENTITY ImaginaryI "&#x2148;"
			tag(buf, "mi", "&#x2148;");
			tagEnd(buf, "mrow");
			return;
		}
		tagStart(buf, "mrow");
		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			tag(buf, "mo", "(");
		}
		if (!isReZero) {
			convertFraction(buf, c.getRealPart(), plusPrec, caller);
			// if (isImNegative) {
			// tag(buf, "mo", "-");
			// } else {
			// tag(buf, "mo", "+");
			// }
		}

		// else {
		// if (isImNegative) {
		// tag(buf, "mo", "-");
		// }
		// }
		if (isImOne) {
			tagStart(buf, "mrow");
			if (isReZero) {
				if (caller == PLUS_CALL) {
					tag(buf, "mo", "+");
				}
				// <!ENTITY ImaginaryI "&#x2148;"
				tag(buf, "mi", "&#x2148;");
				tagEnd(buf, "mrow");
			} else {
				tag(buf, "mo", "+");
				// <!ENTITY ImaginaryI "&#x2148;"
				tag(buf, "mi", "&#x2148;");
				tagEnd(buf, "mrow");
			}
		} else if (isImMinusOne) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "-");
			// <!ENTITY ImaginaryI "&#x2148;"
			tag(buf, "mi", "&#x2148;");
			tagEnd(buf, "mrow");
		} else {
			tagStart(buf, "mrow");
			if (isImNegative) {
				imaginaryPart = imaginaryPart.negate();
			}
			if (!isReZero) {
				if (isImNegative) {
					tag(buf, "mo", "-");
				} else {
					tag(buf, "mo", "+");
				}
			} else {
				if (caller == PLUS_CALL) {
					tag(buf, "mo", "+");
				}
				if (isImNegative) {
					tag(buf, "mo", "-");
				}
			}
			convertFraction(buf, imaginaryPart, ASTNodeFactory.TIMES_PRECEDENCE, caller);
			// <!ENTITY InvisibleTimes "&#x2062;" >
			// <!ENTITY CenterDot "&#0183;" >
			tag(buf, "mo", "&#0183;");
			// <!ENTITY ImaginaryI "&#x2148;"
			tag(buf, "mi", "&#x2148;");
			tagEnd(buf, "mrow");
			// } else {
			// tag(buf, "mi", "&#x2148;");
		}
		if (!isReZero && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	@Override
	public void convertDouble(final StringBuilder buf, final INum d, final int precedence, boolean caller) {
		if (d.isZero()) {
			tagStart(buf, "mn");
			buf.append(convertDoubleToFormattedString(0.0));
			tagEnd(buf, "mn");
			return;
		}
		final boolean isNegative = d.isNegative();
		if (isNegative && (precedence > plusPrec)) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "(");
		}
		tagStart(buf, "mn");
		if (d instanceof ApfloatNum) {
			convertApfloat(buf, ((ApfloatNum) d).apfloatValue(), precedence);
		} else {
			buf.append(convertDoubleToFormattedString(d.getRealPart()));
		}
		tagEnd(buf, "mn");
		if (isNegative && (precedence > plusPrec)) {
			tag(buf, "mo", ")");
			tagEnd(buf, "mrow");
		}
	}

	@Override
	public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc, final int precedence,
			boolean caller) {
		if (dc instanceof ApcomplexNum) {
			convertApcomplex(buf, ((ApcomplexNum) dc).apcomplexValue(), precedence);
			return;
		}
		double realPart = dc.getRealPart();
		double imaginaryPart = dc.getImaginaryPart();
		final boolean isImNegative = imaginaryPart < 0;

		tagStart(buf, "mrow");
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			tag(buf, "mo", "(");
		}
		tagStart(buf, "mn");
		buf.append(convertDoubleToFormattedString(realPart));
		tagEnd(buf, "mn");
		if (isImNegative) {
			tag(buf, "mo", "-");
			imaginaryPart *= (-1);
		} else {
			tag(buf, "mo", "+");
		}
		tagStart(buf, "mn");
		buf.append(convertDoubleToFormattedString(imaginaryPart));
		tagEnd(buf, "mn");

		// <!ENTITY InvisibleTimes "&#x2062;" >
		// <!ENTITY CenterDot "&#0183;" >
		tag(buf, "mo", "&#0183;");
		// <!ENTITY ImaginaryI "&#x2148;" >
		tag(buf, "mi", "&#x2148;");// "&#x2148;");
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	public void convertFraction(final StringBuilder buf, final BigInteger n, BigInteger denominator,
			final int precedence, boolean caller) {
		boolean isInteger = denominator.compareTo(BigInteger.ONE) == 0;
		BigInteger numerator = n;
		final boolean isNegative = numerator.compareTo(BigInteger.ZERO) < 0;
		if (isNegative) {
			numerator = numerator.negate();
		}
		final int prec = isNegative ? ASTNodeFactory.PLUS_PRECEDENCE : ASTNodeFactory.TIMES_PRECEDENCE;
		tagStart(buf, "mrow");
		if (!isNegative) {
			if (caller == PLUS_CALL) {
				tag(buf, "mo", "-");
			}
		} else {
			tag(buf, "mo", "-");
		}
		if (prec < precedence) {
			tag(buf, "mo", "(");
		}

		String str = numerator.toString();
		if (!isInteger) {
			tagStart(buf, "mfrac");
			tagStart(buf, "mn");
			buf.append(str);
			tagEnd(buf, "mn");
			tagStart(buf, "mn");
			str = denominator.toString();
			buf.append(str);
			tagEnd(buf, "mn");
			tagEnd(buf, "mfrac");
		} else {
			tagStart(buf, "mn");
			buf.append(str);
			tagEnd(buf, "mn");
		}
		if (prec < precedence) {
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
		boolean isInteger = f.denominator().isOne();
		if (f.isNegative() && (precedence > plusPrec)) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "(");
		}
		if (isInteger) {
			tagStart(buf, "mn");
			buf.append(f.toBigNumerator().toString());
			tagEnd(buf, "mn");
		} else {
			tagStart(buf, "mfrac");
			tagStart(buf, "mn");
			buf.append(f.toBigNumerator().toString());
			tagEnd(buf, "mn");
			tagStart(buf, "mn");
			buf.append(f.toBigDenominator().toString());
			tagEnd(buf, "mn");
			tagEnd(buf, "mfrac");
		}
		if (f.isNegative() && (precedence > plusPrec)) {
			tag(buf, "mo", ")");
			tagEnd(buf, "mrow");
		}
	}

	@Override
	// public void convertFraction(final StringBuilder buf, final IRational frac, final int precedence) {
	// IRational f = frac;
	// boolean isNegative = f.isNegative();
	// if (isNegative && (precedence > plusPrec)) {
	// tagStart(buf, "mrow");
	// tag(buf, "mo", "(");
	// }
	// if (isNegative) {
	// tag(buf, "mo", "-");
	// f = frac.negate();
	// }
	// if (f.getDenominator().isOne() || f.getNumerator().isZero()) {
	// tagStart(buf, "mn");
	// buf.append(f.getNumerator().toString());
	// tagEnd(buf, "mn");
	// } else {
	// tagStart(buf, "mfrac");
	// tagStart(buf, "mn");
	// buf.append(f.getNumerator().toString());
	// tagEnd(buf, "mn");
	// tagStart(buf, "mn");
	// buf.append(f.getDenominator().toString());
	// tagEnd(buf, "mn");
	// tagEnd(buf, "mfrac");
	// }
	// if (isNegative && (precedence > plusPrec)) {
	// tag(buf, "mo", ")");
	// tagEnd(buf, "mrow");
	// }
	// }

	public void convertFraction(final StringBuilder buf, final IRational f, final int precedence, boolean caller) {
		convertFraction(buf, f.toBigNumerator(), f.toBigDenominator(), precedence, caller);
	}

	public void convertFunctionArgs(final StringBuilder buf, final IAST list) {
		tag(buf, "mo", "[");
		for (int i = 1; i < list.size(); i++) {
			convert(buf, list.get(i), Integer.MIN_VALUE, false);
			if (i < list.argSize()) {
				tag(buf, "mo", ",");
			}
		}
		tag(buf, "mo", "]");
	}

	@Override
	public void convertHead(final StringBuilder buf, final IExpr obj) {
		if (obj instanceof ISymbol) {
			String headStr = ((ISymbol) obj).getSymbolName();
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
				if (str != null) {
					headStr = str;
				}
			}
			tagStart(buf, "mi");
			buf.append(headStr);
			tagEnd(buf, "mi");
			// &af; &#x2061;
			tag(buf, "mo", "&#x2061;");
			return;
		}
		convert(buf, obj, Integer.MIN_VALUE, false);
	}

	public void convertInfixOperator(final StringBuilder buf, final IAST list, final InfixOperator oper,
			final int precedence) {

		if (list.isAST2()) {
			tagStart(buf, "mrow");
			if (oper.getPrecedence() < precedence) {
				// append(buf, "(");
				tag(buf, "mo", "(");
			}
			if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE && list.arg1().head().equals(list.head())) {
				// append(buf, "(");
				tag(buf, "mo", "(");
				// } else {
				// if (oper.getOperatorString() == "^") {
				// final Operator operator = getOperator(list.arg1().topHead());
				// if (operator instanceof PostfixOperator) {
				// append(buf, "(");
				// }
				// }
			}
			convert(buf, list.arg1(), oper.getPrecedence(), false);
			if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE && list.arg1().head().equals(list.head())) {
				// append(buf, ")");
				tag(buf, "mo", ")");
				// } else {
				// if (oper.getOperatorString() == "^") {
				// final Operator operator = getOperator(list.arg1().topHead());
				// if (operator instanceof PostfixOperator) {
				// append(buf, ")");
				// }
				// }
			}

			// append(buf, oper.getOperatorString());
			tag(buf, "mo", oper.getOperatorString());

			if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE && list.arg2().head().equals(list.head())) {
				// append(buf, "(");
				tag(buf, "mo", "(");
			}
			convert(buf, list.arg2(), oper.getPrecedence(), false);
			if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE && list.arg2().head().equals(list.head())) {
				// append(buf, ")");
				tag(buf, "mo", ")");
			}

			if (oper.getPrecedence() < precedence) {
				// append(buf, ")");
				tag(buf, "mo", ")");
			}
			tagEnd(buf, "mrow");
			return;
		}

		tagStart(buf, "mrow");
		if (oper.getPrecedence() < precedence) {
			// append(buf, "(");
			tag(buf, "mo", "(");
		}
		if (list.size() > 1) {
			convert(buf, list.arg1(), oper.getPrecedence(), false);
		}

		for (int i = 2; i < list.size(); i++) {
			// append(buf, oper.getOperatorString());
			tag(buf, "mo", oper.getOperatorString());
			convert(buf, list.get(i), oper.getPrecedence(), false);
		}
		if (oper.getPrecedence() < precedence) {
			// append(buf, ")");
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	@Override
	public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence, boolean caller) {
		if (i.isNegative() && (precedence > plusPrec)) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "(");
		}
		tagStart(buf, "mn");
		buf.append(i.toBigNumerator().toString());
		tagEnd(buf, "mn");
		if (i.isNegative() && (precedence > plusPrec)) {
			tag(buf, "mo", ")");
			tagEnd(buf, "mrow");
		}
	}

	public void convertList(final StringBuilder buf, final IAST list) {
		// if (list instanceof ASTRealVector) {
		// ((ASTRealVector) list).toString(buf);
		// return;
		// }
		// if (list instanceof ASTRealMatrix) {
		// ((ASTRealMatrix) list).toString(buf, fEmpty);
		// fColumnCounter = 1;
		// fEmpty = false;
		// return;
		// }
		// if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
		// }

		tagStart(buf, "mrow");
		tag(buf, "mo", "{");
		if (list.size() > 1) {
			tagStart(buf, "mrow");
			convert(buf, list.arg1(), 0, false);
			for (int i = 2; i < list.size(); i++) {
				tag(buf, "mo", ",");
				convert(buf, list.get(i), 0, false);
			}
			tagEnd(buf, "mrow");
		}
		tag(buf, "mo", "}");
		tagEnd(buf, "mrow");

	}

	public boolean convertNumber(final StringBuilder buf, final IExpr o, final int precedence, boolean caller) {
		if (o instanceof INum) {
			convertDouble(buf, (INum) o, precedence, caller);
			return true;
		}
		if (o instanceof IComplexNum) {
			convertDoubleComplex(buf, (IComplexNum) o, precedence, caller);
			return true;
		}
		if (o instanceof IInteger) {
			convertInteger(buf, (IInteger) o, precedence, caller);
			return true;
		}
		if (o instanceof IFraction) {
			convertFraction(buf, (IFraction) o, precedence, caller);
			return true;
		}
		if (o instanceof IComplex) {
			convertComplex(buf, (IComplex) o, precedence, caller);
			return true;
		}
		return false;
	}

	private boolean convertOperator(final org.matheclipse.parser.client.operator.Operator operator, final IAST list,
			final StringBuilder buf, final int precedence, ISymbol head) {
		if ((operator instanceof PrefixOperator) && (list.isAST1())) {
			convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
			return true;
		}
		if ((operator instanceof InfixOperator) && (list.size() > 2)) {
			InfixOperator infixOperator = (InfixOperator) operator;
			// if (head.equals(F.Plus)) {
			// if (fPlusReversed) {
			// convertPlusOperatorReversed(buf, list, infixOperator, precedence);
			// } else {
			// convertPlusOperator(buf, list, infixOperator, precedence);
			// }
			// return true;
			// } else
			// if (head.equals(F.Times)) {
			// convertTimesOperator(buf, list, infixOperator, precedence, NO_PLUS_CALL);
			// return true;
			// convertTimesFraction(buf, list, infixOperator, precedence, NO_PLUS_CALL);
			// return true;
			// } else if (list.isPower()) {
			// convertPowerOperator(buf, list, infixOperator, precedence);
			// return true;
			// } else
			if (list.isAST(F.Apply)) {
				if (list.size() == 3) {
					convertInfixOperator(buf, list, ASTNodeFactory.APPLY_OPERATOR, precedence);
					return true;
				}
				if (list.size() == 4 && list.get(2).equals(F.List(F.C1))) {
					convertInfixOperator(buf, list, ASTNodeFactory.APPLY_LEVEL_OPERATOR, precedence);
					return true;
				}
				return false;
			} else if (list.size() != 3 && infixOperator.getGrouping() != InfixOperator.NONE) {
				return false;
			}
			convertInfixOperator(buf, list, (InfixOperator) operator, precedence);
			return true;
		}
		if ((operator instanceof PostfixOperator) && (list.isAST1())) {
			convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
			return true;
		}
		return false;
	}

	/**
	 * This method will only be called if <code>list.isAST2()==true</code> and the head equals "Part".
	 * 
	 * @param buf
	 * @param list
	 * @throws IOException
	 */
	public void convertPart(final StringBuilder buf, final IAST list) {
		IExpr arg1 = list.arg1();
		tagStart(buf, "mrow");
		if (!(arg1 instanceof IAST)) {
			tag(buf, "mo", "(");
		}
		convert(buf, arg1, Integer.MIN_VALUE, false);
		tag(buf, "mo", "[[");

		for (int i = 2; i < list.size(); i++) {
			convert(buf, list.get(i), Integer.MIN_VALUE, false);
			if (i < list.argSize()) {
				tag(buf, "mo", ",");
			}
		}

		tag(buf, "mo", "]]");
		if (!(arg1 instanceof IAST)) {
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	public void convertPostfixOperator(final StringBuilder buf, final IAST list, final PostfixOperator oper,
			final int precedence) {
		tagStart(buf, "mrow");
		if (oper.getPrecedence() < precedence) {
			// append(buf, "(");
			tag(buf, "mo", "(");
		}
		convert(buf, list.arg1(), oper.getPrecedence(), false);
		// append(buf, oper.getOperatorString());
		tag(buf, "mo", oper.getOperatorString());
		if (oper.getPrecedence() < precedence) {
			// append(buf, ")");
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	public void convertPrefixOperator(final StringBuilder buf, final IAST list, final PrefixOperator oper,
			final int precedence) {
		tagStart(buf, "mrow");
		if (oper.getPrecedence() < precedence) {
			// append(buf, "(");
			tag(buf, "mo", "(");
		}
		// append(buf, oper.getOperatorString());
		tag(buf, "mo", oper.getOperatorString());
		convert(buf, list.arg1(), oper.getPrecedence(), false);
		if (oper.getPrecedence() < precedence) {
			// append(buf, ")");
			tag(buf, "mo", ")");
		}
		tagEnd(buf, "mrow");
	}

	/**
	 * Convert a <code>SeriesData[...]</code> expression.
	 * 
	 * @param buf
	 * @param seriesData
	 *            <code>SeriesData[x, x0, list, nmin, nmax, den]</code> expression
	 * @param precedence
	 *            the precedence of the parent expression
	 * @return <code>true</code> if the conversion was successful
	 * @throws IOException
	 */
	public boolean convertSeriesData(final StringBuilder buf, final ASTSeriesData seriesData, final int precedence) {
		int operPrecedence = ASTNodeFactory.PLUS_PRECEDENCE;
		StringBuilder tempBuffer = new StringBuilder();
		tagStart(tempBuffer, "mrow");
		if (operPrecedence < precedence) {
			tag(tempBuffer, "mo", "(");
		}
		try {
			IExpr plusArg;
			// SeriesData[x, x0, list, nmin, nmax, den]
			IExpr x = seriesData.getX();
			IExpr x0 = seriesData.getX0();
			int nMin = seriesData.getNMin();
			int nMax = seriesData.getNMax();
			int power = seriesData.getPower();
			int den = seriesData.getDenominator();
			boolean call = NO_PLUS_CALL;
			INumber exp;
			IExpr pow;
			boolean first = true;
			IExpr x0Term = x.subtract(x0);
			for (int i = nMin; i < nMax; i++) {
				IExpr coefficient = seriesData.coeff(i);
				if (!coefficient.isZero()) {
					if (!first) {
						tag(tempBuffer, "mo", "+");
					}
					exp = F.fraction(i, den).normalize();
					pow = x0Term.power(exp);

					call = convertSeriesDataArg(tempBuffer, coefficient, pow, call);

					first = false;
				}
			}
			plusArg = F.Power(F.O(x.subtract(x0)), F.fraction(power, den).normalize());
			if (!plusArg.isZero()) {
				tag(tempBuffer, "mo", "+");
				convert(tempBuffer, plusArg, Integer.MIN_VALUE, false);
				call = PLUS_CALL;
			}

		} catch (Exception ex) {
			if (Config.SHOW_STACKTRACE) {
				ex.printStackTrace();
			}
			return false;
		}
		if (operPrecedence < precedence) {
			tag(tempBuffer, "mo", ")");
		}
		tagEnd(tempBuffer, "mrow");
		buf.append(tempBuffer);
		return true;
	}

	/**
	 * Convert a factor of a <code>SeriesData</code> object.
	 * 
	 * @param buf
	 * @param coefficient
	 *            the coefficient expression of the factor
	 * @param pow
	 *            the power expression of the factor
	 * @param call
	 * @param operPrecedence
	 * @return the current call status
	 * @throws IOException
	 */
	private boolean convertSeriesDataArg(StringBuilder buf, IExpr coefficient, IExpr pow, boolean call)
			throws IOException {
		IExpr plusArg;
		if (coefficient.isZero()) {
			plusArg = F.C0;
		} else if (coefficient.isOne()) {
			plusArg = pow;
		} else {
			if (pow.isOne()) {
				plusArg = coefficient;
			} else {
				plusArg = F.binaryAST2(F.Times, coefficient, pow);
			}
		}
		if (!plusArg.isZero()) {
			convert(buf, plusArg, Integer.MIN_VALUE, false);
			call = PLUS_CALL;
		}
		return call;
	}

	public void convertSlot(final StringBuilder buf, final IAST list) {
		try {
			final int slot = ((ISignedNumber) list.arg1()).toInt();
			// append(buf, "#" + slot);
			tag(buf, "mi", "#" + slot);
		} catch (final ArithmeticException e) {
			// add message to evaluation problemReporter
		}
	}

	public void convertSlotSequence(final StringBuilder buf, final IAST list) {
		try {
			final int slotSequenceStartPosition = ((ISignedNumber) list.arg1()).toInt();
			// append(buf, "##" + slotSequenceStartPosition);
			tag(buf, "mi", "##" + slotSequenceStartPosition);
		} catch (final ArithmeticException e) {
			// add message to evaluation problemReporter
		}
	}

	@Override
	public void convertString(final StringBuilder buf, final String str) {
		String[] splittedStr = str.split("\\n");
		for (int i = 0; i < splittedStr.length; i++) {
			tagStart(buf, "mtext");
			String text = splittedStr[i].replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;");
			text = text.replaceAll("\\\"", "&quot;").replaceAll(" ", "&nbsp;");
			buf.append(text);
			tagEnd(buf, "mtext");
			buf.append("<mspace linebreak='newline' />");
		}

	}

	@Override
	public void convertSymbol(final StringBuilder buf, final ISymbol sym) {
		String headStr = sym.getSymbolName();
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
			if (str != null) {
				headStr = str;
			}
		}
		final Object convertedSymbol = CONSTANT_SYMBOLS.get(headStr);
		if (convertedSymbol == null) {
			tagStart(buf, "mi");
			buf.append(sym.toString());
			tagEnd(buf, "mi");
		} else {
			if (convertedSymbol instanceof Operator) {
				((Operator) convertedSymbol).convert(buf);
			} else {
				tagStart(buf, "mi");
				buf.append(convertedSymbol.toString());
				tagEnd(buf, "mi");
			}
		}
	}

	public void init() {
		plusPrec = ASTNodeFactory.PLUS_PRECEDENCE;// ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence();

		CONVERTERS.put(F.Sin, new MMLFunction(this, "sin"));
		CONVERTERS.put(F.Cos, new MMLFunction(this, "cos"));
		CONVERTERS.put(F.Tan, new MMLFunction(this, "tan"));
		CONVERTERS.put(F.Cot, new MMLFunction(this, "cot"));
		CONVERTERS.put(F.ArcSin, new MMLFunction(this, "arcsin"));
		CONVERTERS.put(F.ArcCos, new MMLFunction(this, "arccos"));
		CONVERTERS.put(F.ArcTan, new MMLFunction(this, "arctan"));
		CONVERTERS.put(F.ArcCot, new MMLFunction(this, "arccot"));
		CONVERTERS.put(F.ArcSinh, new MMLFunction(this, "arcsinh"));
		CONVERTERS.put(F.ArcCosh, new MMLFunction(this, "arccosh"));
		CONVERTERS.put(F.ArcTanh, new MMLFunction(this, "arctanh"));
		CONVERTERS.put(F.ArcCoth, new MMLFunction(this, "arccoth"));
		CONVERTERS.put(F.Log, new MMLFunction(this, "log"));

		// operTab.put("Sum", new MMLSum(this));
		// operTab.put("Integrate", new MMLIntegrate(this));
		// operTab.put("D", new MMLD(this));
		// operTab.put(Factorial, new MMLFactorial(this));
		// operTab.put("Binomial", new MMLBinomial(this));

		CONSTANT_SYMBOLS.put("E", "&#x2147;");
		// CONSTANT_SYMBOLS.put("I", "\u2148"); // IMaginaryI
		CONSTANT_SYMBOLS.put("HEllipsis", new Operator("&#x2026;"));// &hellip;"));
		// greek Symbols:
		// CONSTANT_SYMBOLS.put("Pi", "&#x03A0;");
		// CONSTANT_SYMBOLS.put("pi", "&#x03C0;");
		CONSTANT_SYMBOLS.put("Alpha", "&#x0391;");
		CONSTANT_SYMBOLS.put("Beta", "&#x0392;");
		CONSTANT_SYMBOLS.put("Gamma", "&#x0393;");
		CONSTANT_SYMBOLS.put("Delta", "&#x0394;");
		CONSTANT_SYMBOLS.put("Epsilon", "&#x0395;");
		CONSTANT_SYMBOLS.put("Zeta", "&#x0396;");
		CONSTANT_SYMBOLS.put("Eta", "&#x0397;");
		CONSTANT_SYMBOLS.put("Theta", "&#x0398;");
		CONSTANT_SYMBOLS.put("Iota", "&#x0399;");
		CONSTANT_SYMBOLS.put("Kappa", "&#x039A;");
		CONSTANT_SYMBOLS.put("Lambda", "&#x039B;");
		CONSTANT_SYMBOLS.put("Mu", "&#x039C;");
		CONSTANT_SYMBOLS.put("Nu", "&#x039D;");
		CONSTANT_SYMBOLS.put("Xi", "&#x039E;");
		CONSTANT_SYMBOLS.put("Omicron", "&#x039F;");
		CONSTANT_SYMBOLS.put("Rho", "&#x03A1;");
		CONSTANT_SYMBOLS.put("Sigma", "&#x03A3;");
		CONSTANT_SYMBOLS.put("Tau", "&#x03A4;");
		CONSTANT_SYMBOLS.put("Upsilon", "&#x03A5;");
		CONSTANT_SYMBOLS.put("Phi", "&#x03A6;");
		CONSTANT_SYMBOLS.put("Chi", "&#x03A7;");
		CONSTANT_SYMBOLS.put("Psi", "&#x03A8;");
		CONSTANT_SYMBOLS.put("Omega", "&#x03A9;");

		CONSTANT_SYMBOLS.put("varTheta", "&#x03D1;");

		CONSTANT_SYMBOLS.put("alpha", "&#x03B1;");
		CONSTANT_SYMBOLS.put("beta", "&#x03B2;");
		CONSTANT_SYMBOLS.put("chi", "&#x03C7;");
		CONSTANT_SYMBOLS.put("selta", "&#x03B4;");
		CONSTANT_SYMBOLS.put("epsilon", "&#x03B5;");
		CONSTANT_SYMBOLS.put("phi", "&#x03C7;");
		CONSTANT_SYMBOLS.put("gamma", "&#x03B3;");
		CONSTANT_SYMBOLS.put("eta", "&#x03B7;");
		CONSTANT_SYMBOLS.put("iota", "&#x03B9;");
		CONSTANT_SYMBOLS.put("varphi", "&#x03C6;");
		CONSTANT_SYMBOLS.put("kappa", "&#x03BA;");
		CONSTANT_SYMBOLS.put("lambda", "&#x03BB;");
		CONSTANT_SYMBOLS.put("mu", "&#x03BC;");
		CONSTANT_SYMBOLS.put("nu", "&#x03BD;");
		CONSTANT_SYMBOLS.put("omicron", "&#x03BF;");
		CONSTANT_SYMBOLS.put("theta", "&#x03B8;");
		CONSTANT_SYMBOLS.put("rho", "&#x03C1;");
		CONSTANT_SYMBOLS.put("sigma", "&#x03C3;");
		CONSTANT_SYMBOLS.put("tau", "&#x03C4;");
		CONSTANT_SYMBOLS.put("upsilon", "&#x03C5;");
		CONSTANT_SYMBOLS.put("varsigma", "&#x03C2;");
		CONSTANT_SYMBOLS.put("omega", "&#x03C9;");
		CONSTANT_SYMBOLS.put("xi", "&#x03BE;");
		CONSTANT_SYMBOLS.put("psi", "&#x03C8;");
		CONSTANT_SYMBOLS.put("zeta", "&#x03B6;");

		ENTITY_TABLE.put("&af;", "&#xE8A0;");
		ENTITY_TABLE.put("&dd;", "&#xF74C;");
		ENTITY_TABLE.put("&ImaginaryI;", "i");// "\u2148");
		ENTITY_TABLE.put("&InvisibleTimes;", "&#xE89E;");

		ENTITY_TABLE.put("&Integral;", "&#x222B;");
		ENTITY_TABLE.put("&PartialD;", "&#x2202;");
		ENTITY_TABLE.put("&Product;", "&#x220F;");

		CONSTANT_EXPRS.put(F.GoldenRatio, "<mi>&#x03C7;</mi>"); // phi
		CONSTANT_EXPRS.put(F.Pi, "<mi>&#x03C0;</mi>");
		CONSTANT_EXPRS.put(F.CInfinity, "<mi>&#x221E;</mi>"); // &infin;
		// mrow is important in mfrac element!
		CONSTANT_EXPRS.put(F.CNInfinity, "<mrow><mo>-</mo><mi>&#x221E;</mi></mrow>");
		CONSTANT_EXPRS.put(F.Catalan, "<mi>C</mi>");
		CONSTANT_EXPRS.put(F.Degree, "<mi>&#x00b0;</mi>");
		CONSTANT_EXPRS.put(F.Glaisher, "<mi>A</mi>");
		CONSTANT_EXPRS.put(F.EulerGamma, "<mi>&#x03B3;</mi>");
		CONSTANT_EXPRS.put(F.Khinchin, "<mi>K</mi>");

		CONVERTERS.put(F.Abs, new Abs());
		CONVERTERS.put(F.And, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("And").getPrecedence(), "&#x2227;"));
		CONVERTERS.put(F.Binomial, new Binomial());
		CONVERTERS.put(F.Ceiling, new Ceiling());
		CONVERTERS.put(F.CompoundExpression,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ";"));
		CONVERTERS.put(F.D, new D());
		CONVERTERS.put(F.Dot, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Dot").getPrecedence(), "."));
		CONVERTERS.put(F.Equal, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Equal").getPrecedence(), "=="));
		CONVERTERS.put(F.Factorial, new MMLPostfix("!"));
		CONVERTERS.put(F.Factorial2, new MMLPostfix("!!"));
		CONVERTERS.put(F.Floor, new Floor());
		CONVERTERS.put(F.Function, new Function());
		CONVERTERS.put(F.Greater,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), "&gt;"));
		CONVERTERS.put(F.GreaterEqual,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("GreaterEqual").getPrecedence(), "&#x2265;"));
		CONVERTERS.put(F.Integrate, new Integrate());
		CONVERTERS.put(F.Less, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Less").getPrecedence(), "&lt;"));
		CONVERTERS.put(F.LessEqual,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(), "&#x2264;"));
		CONVERTERS.put(F.MatrixForm, new MatrixForm());
		CONVERTERS.put(F.Not, new Not());
		CONVERTERS.put(F.Or, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Or").getPrecedence(), "&#x2228;"));
		CONVERTERS.put(F.Plus, new Plus());
		CONVERTERS.put(F.Power, new Power());
		CONVERTERS.put(F.Product, new Product());
		CONVERTERS.put(F.Rational, new Rational());
		CONVERTERS.put(F.Rule, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Rule").getPrecedence(), "-&gt;"));
		CONVERTERS.put(F.RuleDelayed,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(), "&#x29F4;"));
		CONVERTERS.put(F.Set, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), "="));
		CONVERTERS.put(F.SetDelayed,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("SetDelayed").getPrecedence(), ":="));
		CONVERTERS.put(F.Sqrt, new Sqrt());
		CONVERTERS.put(F.Sum, new Sum());
		CONVERTERS.put(F.Times, new Times());
		CONVERTERS.put(F.Unequal,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Unequal").getPrecedence(), "!="));
	}
}
