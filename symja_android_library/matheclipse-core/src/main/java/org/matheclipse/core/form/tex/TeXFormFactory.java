package org.matheclipse.core.form.tex;

import java.text.NumberFormat;
import java.util.HashMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
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

/**
 * <p>
 * Generates TeX presentation output
 * </p>
 * 
 */
public class TeXFormFactory {

	private static abstract class AbstractConverter {
		protected TeXFormFactory fFactory;

		public AbstractConverter() {
			fFactory = null;
		}

		public AbstractConverter(final TeXFormFactory factory) {
			fFactory = factory;
		}

		public abstract boolean convert(final StringBuilder buf, final IAST f, final int precedence);

		/**
		 * @param factory
		 */
		public void setFactory(final TeXFormFactory factory) {
			fFactory = factory;
		}
	}

	private static class AbstractOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fOperator;

		public AbstractOperator(final int precedence, final String oper) {
			fPrecedence = precedence;
			fOperator = oper;
		}

		public AbstractOperator(final TeXFormFactory factory, final int precedence, final String oper) {
			super(factory);
			fPrecedence = precedence;
			fOperator = oper;
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			precedenceOpen(buf, precedence);
			for (int i = 1; i < f.size(); i++) {
				fFactory.convert(buf, f.get(i), fPrecedence);
				if (i < f.argSize()) {
					if (fOperator.compareTo("") != 0) {
						buf.append(fOperator);
					}
				}
			}
			precedenceClose(buf, precedence);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\right) ");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\left( ");
			}
		}

	}

	private final static class Binomial extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			// "<mrow><mo>(</mo><mfrac linethickness=\"0\">{0}{1}</mfrac><mo>)</mo></mrow>"
			if (f.size() != 3) {
				return false;
			}
			buf.append('{');
			fFactory.convert(buf, f.arg1(), 0);
			buf.append("\\choose ");
			fFactory.convert(buf, f.arg2(), 0);
			buf.append('}');
			return true;
		}
	}

	private final static class Complex extends AbstractOperator {

		public Complex() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
		}

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return super.convert(buf, f, precedence);
			}
			precedenceOpen(buf, precedence);
			IExpr arg1 = f.arg1();
			boolean reZero = arg1.isZero();
			IExpr arg2 = f.arg2();
			boolean imZero = arg2.isZero();

			if (!reZero) {
				fFactory.convert(buf, arg1, 0);
			}
			if (!imZero) {
				if (!reZero && !arg2.isNegativeSigned()) {
					buf.append(" + ");
				}
				if (arg2.isMinusOne()) {
					buf.append(" - ");
				} else if (!arg2.isOne()) {
					fFactory.convert(buf, arg2, 0);
					buf.append("\\,"); // InvisibleTimes
				}
				buf.append("\\imag");
			}
			return true;
		}
	}

	private final static class D extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST2()) {

				buf.append("\\frac{d}{{d");
				fFactory.convert(buf, f.arg2(), 0);
				buf.append("}}");
				fFactory.convert(buf, f.arg1(), 0);

				return true;
			}
			return false;
		}
	}

	private final static class DirectedInfinity extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST1()) {
				if (f.arg1().isOne()) {
					buf.append("\\infty");
					return true;
				} else if (f.arg1().isMinusOne()) {
					buf.append("- \\infty");
					return true;
				}

				return true;
			}
			return false;
		}
	}

	private final static class HarmonicNumber extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST1()) {
				buf.append("H_");
				fFactory.convert(buf, f.arg1(), 0);
				return true;
			} else if (f.isAST2()) {
				buf.append("H_");
				fFactory.convert(buf, f.arg1(), 0);

				buf.append("^{(");
				fFactory.convert(buf, f.arg2(), 0);
				buf.append(")}");
				return true;
			}
			return false;
		}
	}

	private final static class Integrate extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				return iteratorStep(buf, "\\int", f, 2);
			}
			return false;
		}

		public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
			if (i >= f.size()) {
				buf.append(" ");
				fFactory.convert(buf, f.arg1(), 0);
				return true;
			}
			if (f.get(i).isList()) {
				IAST list = (IAST) f.get(i);
				if (list.size() == 4 && list.arg1().isSymbol()) {
					ISymbol symbol = (ISymbol) list.arg1();
					buf.append(mathSymbol);
					buf.append("_{");
					fFactory.convert(buf, list.arg2(), 0);
					buf.append("}^{");
					fFactory.convert(buf, list.arg3(), 0);
					buf.append('}');
					if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
						return false;
					}
					buf.append("\\,\\mathrm{d}");
					fFactory.convertSymbol(buf, symbol);
					return true;
				}
			} else if (f.get(i).isSymbol()) {
				ISymbol symbol = (ISymbol) f.get(i);
				buf.append(mathSymbol);
				buf.append(" ");
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				buf.append("\\,\\mathrm{d}");
				fFactory.convertSymbol(buf, symbol);
				return true;
			}
			return false;
		}
	}

	private final static class Limit extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isAST2() && f.arg2().isRuleAST()) {
				final IAST rule = (IAST) f.arg2();
				buf.append("\\lim_{");
				fFactory.convertSubExpr(buf, rule.arg1(), 0);
				buf.append("\\to ");
				fFactory.convertSubExpr(buf, rule.arg2(), 0);
				buf.append(" }\\,");
				fFactory.convertSubExpr(buf, f.arg1(), 0);

				// buf.append("\\mathop {\\lim }\\limits_{");
				// fFactory.convert(buf, rule.arg1(), 0);
				// buf.append(" \\to ");
				// fFactory.convert(buf, rule.arg2(), 0);
				// buf.append('}');
				// fFactory.convert(buf, f.arg1(), 0);
				return true;
			}
			return false;
		}
	}

	private final static class List extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST ast, final int precedence) {

			int[] dims = ast.isMatrix();
			if (dims != null) {
				// create a LaTeX matrix
				buf.append("\\left(\n\\begin{array}{");
				for (int i = 0; i < dims[1]; i++) {
					buf.append("c");
				}
				buf.append("}\n");
				if (ast.size() > 1) {
					for (int i = 1; i < ast.size(); i++) {
						IAST row = ast.getAST(i);
						for (int j = 1; j < row.size(); j++) {
							fFactory.convert(buf, row.get(j), 0);
							if (j < row.argSize()) {
								buf.append(" & ");
							}
						}
						if (i < ast.argSize()) {
							buf.append(" \\\\\n");
						} else {

							buf.append(" \n");
						}
					}
				}
				buf.append("\\end{array}\n\\right) ");
			} else if ((ast.getEvalFlags() & IAST.IS_VECTOR) == IAST.IS_VECTOR) {
				// create a LaTeX row vector
				// \begin{pmatrix} x & y \end{pmatrix}
				buf.append("\\begin{pmatrix} ");
				if (ast.size() > 1) {
					for (int j = 1; j < ast.size(); j++) {
						fFactory.convert(buf, ast.get(j), 0);
						if (j < ast.argSize()) {
							buf.append(" & ");
						}
					}
				}
				buf.append(" \\end{pmatrix} ");
			} else {
				buf.append("\\{");
				if (ast.size() > 1) {
					fFactory.convert(buf, ast.arg1(), 0);
					for (int i = 2; i < ast.size(); i++) {
						buf.append(',');
						fFactory.convert(buf, ast.get(i), 0);
					}
				}
				buf.append("\\}");
			}
			return true;
		}
	}

	private final static class MatrixForm extends AbstractConverter {

		/** {@inheritDoc} */
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
					buf.append("\\begin{pmatrix}\n");
					IExpr element;
					for (int i = 1; i < vector.size(); i++) {
						element = vector.get(i);
						buf.append(' ');
						fFactory.convert(buf, element, 0);
						buf.append(' ');
						if (i < vector.argSize()) {
							buf.append('&');
						}
					}
					buf.append("\\end{pmatrix}");
				}
			} else {
				final IAST matrix = (IAST) f.arg1();
				buf.append("\\begin{pmatrix}\n");
				IAST row;
				for (int i = 1; i < matrix.size(); i++) {
					row = (IAST) matrix.get(i);
					for (int j = 1; j < row.size(); j++) {
						buf.append(' ');
						fFactory.convert(buf, row.get(j), 0);
						buf.append(' ');
						if (j < row.argSize()) {
							buf.append('&');
						}
					}
					buf.append("\\\\\n");
				}

				buf.append("\\end{pmatrix}");
			}
			return true;
		}

	}

	private final static class TableForm extends AbstractConverter {

		/** {@inheritDoc} */
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
					buf.append("\\begin{array}{c}\n");
					IExpr element;
					for (int i = 1; i < vector.size(); i++) {
						element = vector.get(i);
						buf.append(' ');
						fFactory.convert(buf, element, 0);
						buf.append(' ');
						if (i < vector.argSize()) {
							buf.append("\\\\\n");
						}
					}
					buf.append("\\end{array}");
				}
			} else {
				final IAST matrix = (IAST) f.arg1();
				buf.append("\\begin{array}{");
				for (int i = 0; i < dims[1]; i++) {
					buf.append("c");
				}
				buf.append("}\n");
				IAST row;
				for (int i = 1; i < matrix.size(); i++) {
					row = (IAST) matrix.get(i);
					for (int j = 1; j < row.size(); j++) {
						buf.append(' ');
						fFactory.convert(buf, row.get(j), 0);
						buf.append(' ');
						if (j < row.argSize()) {
							buf.append('&');
						}
					}
					buf.append("\\\\\n");
				}

				buf.append("\\end{array}");
			}
			return true;
		}

	}

	static class Operator {
		String fOperator;

		Operator(final String oper) {
			fOperator = oper;
		}

		public void convert(final StringBuilder buf) {
			buf.append(fOperator);
		}

		@Override
		public String toString() {
			return fOperator;
		}

	}

	private final static class Plus extends AbstractOperator {

		public Plus() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			IExpr expr;
			precedenceOpen(buf, precedence);
			final Times timesConverter = new Times();
			timesConverter.setFactory(fFactory);
			for (int i = 1; i < f.size(); i++) {
				expr = f.get(i);

				if ((i > 1) && (expr instanceof IAST) && expr.isTimes()) {
					timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence, Times.PLUS_CALL);
				} else {
					if (i > 1) {
						if (expr.isNumber() && (((INumber) expr).complexSign() < 0)) {
							buf.append("-");
							expr = ((INumber) expr).negate();
						} else if (expr.isNegativeSigned()) {
						} else {
							buf.append("+");
						}
					}
					fFactory.convert(buf, expr, fPrecedence);
				}
			}
			precedenceClose(buf, precedence);
			return true;
		}

	}

	private final static class PostOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fOperator;

		public PostOperator(final int precedence, final String oper) {
			fPrecedence = precedence;
			fOperator = oper;
		}

		public PostOperator(final TeXFormFactory factory, final int precedence, final String oper) {
			super(factory);
			fPrecedence = precedence;
			fOperator = oper;
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			precedenceOpen(buf, precedence);
			fFactory.convert(buf, f.arg1(), fPrecedence);
			buf.append(fOperator);
			precedenceClose(buf, precedence);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\right) ");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\left( ");
			}
		}

	}

	/**
	 * See: <a href="http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices">Wikibooks - LaTeX/Mathematics -
	 * Powers and indices</a>
	 * 
	 */
	private final static class Power extends AbstractOperator {

		public Power() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "^");
		}

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return super.convert(buf, f, precedence);
			}
			IExpr arg1 = f.arg1();
			IExpr arg2 = f.arg2();
			if (arg2.isRationalValue(F.C1D2)) {
				buf.append("\\sqrt{");
				fFactory.convert(buf, arg1, fPrecedence);
				buf.append('}');
				return true;
			}
			if (arg2.isFraction()) {
				if (((IFraction) arg2).numerator().isOne()) {
					buf.append("\\sqrt[");
					fFactory.convert(buf, ((IFraction) arg2).denominator(), fPrecedence);
					buf.append("]{");
					fFactory.convert(buf, arg1, fPrecedence);
					buf.append('}');
					return true;
				}
			}

			precedenceOpen(buf, precedence);
			fFactory.convertSubExpr(buf, arg1, fPrecedence);
			if (fOperator.compareTo("") != 0) {
				buf.append(fOperator);
			}

			// http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
			// For powers with more than one digit, surround the power with {}.
			buf.append('{');
			fFactory.convert(buf, arg2, 0);
			buf.append('}');
			precedenceClose(buf, precedence);
			return true;
		}
	}

	private final static class PreOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fOperator;

		public PreOperator(final int precedence, final String oper) {
			fPrecedence = precedence;
			fOperator = oper;
		}

		public PreOperator(final TeXFormFactory factory, final int precedence, final String oper) {
			super(factory);
			fPrecedence = precedence;
			fOperator = oper;
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			precedenceOpen(buf, precedence);
			buf.append(fOperator);
			fFactory.convert(buf, f.arg1(), fPrecedence);
			precedenceClose(buf, precedence);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\right) ");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence > fPrecedence) {
				buf.append("\\left( ");
			}
		}

	}

	private final static class Product extends Sum {
		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				return iteratorStep(buf, "\\prod", f, 2);
			}
			return false;
		}

	}

	private final static class Rational extends AbstractOperator {

		public Rational() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "/");
		}

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return super.convert(buf, f, precedence);
			}
			precedenceOpen(buf, precedence);
			buf.append("\\frac{");
			fFactory.convert(buf, f.arg1(), fPrecedence);
			buf.append("}{");
			fFactory.convert(buf, f.arg2(), fPrecedence);
			buf.append('}');
			precedenceClose(buf, precedence);
			return true;
		}
	}

	private final static class Subscript extends AbstractConverter {
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return false;
			}
			IExpr arg1 = f.arg1();
			IExpr arg2 = f.arg2();

			fFactory.convertSubExpr(buf, arg1, 0);
			buf.append("_");
			fFactory.convertSubExpr(buf, arg2, 0);
			return true;
		}
	}

	private final static class Subsuperscript extends AbstractConverter {

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 4) {
				return false;
			}
			IExpr arg1 = f.arg1();
			IExpr arg2 = f.arg2();
			IExpr arg3 = f.arg3();

			fFactory.convert(buf, arg1, Integer.MAX_VALUE);
			buf.append("_");
			fFactory.convert(buf, arg2, Integer.MAX_VALUE);
			buf.append("^");
			fFactory.convert(buf, arg3, Integer.MAX_VALUE);
			return true;
		}
	}

	private static class Sum extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() >= 3) {
				return iteratorStep(buf, "\\sum", f, 2);
			}
			return false;
		}

		/**
		 * See <a href="http://en.wikibooks.org/wiki/LaTeX/Mathematics">Wikibooks - LaTeX/Mathematics</a>
		 * 
		 * @param buf
		 * @param mathSymbol
		 *            the symbol for Sum or Product expressions
		 * @param f
		 * @param i
		 * 
		 * @return <code>true</code> if the expression could be transformed to LaTeX
		 */
		public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
			if (i >= f.size()) {
				buf.append(" ");
				fFactory.convertSubExpr(buf, f.arg1(), 0);
				return true;
			}
			if (f.get(i).isList()) {
				IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), EvalEngine.get());
				if (iterator.isValidVariable() && iterator.getStep().isOne()) {
					buf.append(mathSymbol);
					buf.append("_{");
					fFactory.convertSubExpr(buf, iterator.getVariable(), 0);
					buf.append(" = ");
					fFactory.convertSubExpr(buf, iterator.getLowerLimit(), 0);
					buf.append("}^{");
					fFactory.convert(buf, iterator.getUpperLimit(), 0);
					buf.append('}');
					if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
						return false;
					}
					return true;
				}
			} else if (f.get(i).isSymbol()) {
				ISymbol symbol = (ISymbol) f.get(i);
				buf.append(mathSymbol);
				buf.append("_{");
				fFactory.convertSymbol(buf, symbol);
				buf.append("}");
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				return true;
			}
			return false;
		}
	}

	private final static class Superscript extends AbstractConverter {

		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 3) {
				return false;
			}
			IExpr arg1 = f.arg1();
			IExpr arg2 = f.arg2();

			fFactory.convertSubExpr(buf, arg1, 0);
			buf.append("^");
			fFactory.convertSubExpr(buf, arg2, 0);
			return true;
		}
	}

	private final static class TeXFunction extends AbstractConverter {

		String fFunctionName;

		public TeXFunction(final TeXFormFactory factory, final String functionName) {
			super(factory);
			fFunctionName = functionName;
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			buf.append('\\');
			buf.append(fFunctionName);
			buf.append('(');
			for (int i = 1; i < f.size(); i++) {
				fFactory.convert(buf, f.get(i), 0);
				if (i < f.argSize()) {
					buf.append(',');
				}
			}
			buf.append(')');
			return true;
		}
	}

	private final static class Times extends AbstractOperator {
		public final static int NO_SPECIAL_CALL = 0;

		public final static int PLUS_CALL = 1;

		public static Times CONST = new Times();

		public Times() {
			super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "\\,");
		}

		/**
		 * Converts a given function into the corresponding TeX output
		 * 
		 * @param buf
		 *            StringBuilder for TeX output
		 * @param f
		 *            The math function which should be converted to TeX
		 */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			return convertTimesFraction(buf, f, precedence, NO_SPECIAL_CALL);
		}

		/**
		 * Try to split a given <code>Times[...]</code> function into nominator and denominator and add the
		 * corresponding TeX output
		 * 
		 * @param buf
		 *            StringBuilder for TeX output
		 * @param f
		 *            The math function which should be converted to TeX
		 * @precedence
		 * @caller
		 */
		public boolean convertTimesFraction(final StringBuilder buf, final IAST f, final int precedence,
				final int caller) {
			IExpr[] parts = Algebra.fractionalPartsTimesPower(f, false, true, false, false);
			if (parts == null) {
				convertTimesOperator(buf, f, precedence, caller);
				return true;
			}
			final IExpr numerator = parts[0];
			final IExpr denominator = parts[1];
			if (!denominator.isOne()) {
				if (caller == PLUS_CALL) {
					buf.append('+');
				}
				buf.append("\\frac{");
				// insert numerator in buffer:
				if (numerator.isTimes()) {
					convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
				} else {
					fFactory.convert(buf, numerator, precedence);
				}
				buf.append("}{");
				// insert denominator in buffer:
				if (denominator.isTimes()) {
					convertTimesOperator(buf, (IAST) denominator, fPrecedence, NO_SPECIAL_CALL);
				} else {
					fFactory.convert(buf, denominator, precedence);
				}
				buf.append('}');
			} else {
				if (numerator.isTimes()) {
					convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
				} else {
					fFactory.convert(buf, numerator, precedence);
				}
			}

			return true;
		}

		private boolean convertTimesOperator(final StringBuilder buf, final IAST timesAST, final int precedence,
				final int caller) {
			int size = timesAST.size();
			IExpr arg1 = F.NIL;
			if (size > 1) {
				arg1 = timesAST.arg1();
				if (arg1.isMinusOne()) {
					if (size == 2) {
						precedenceOpen(buf, precedence);
						fFactory.convert(buf, arg1, fPrecedence);
					} else {
						if (caller == PLUS_CALL) {
							buf.append(" - ");
							if (size == 3) {
								fFactory.convert(buf, timesAST.arg2(), fPrecedence);
								return true;
							}
						} else {
							precedenceOpen(buf, precedence);
							buf.append(" - ");
						}
					}
				} else if (arg1.isOne()) {
					if (size == 2) {
						precedenceOpen(buf, precedence);
						fFactory.convert(buf, arg1, fPrecedence);
					} else {
						if (caller == PLUS_CALL) {
							if (size == 3) {
								buf.append(" + ");
								fFactory.convert(buf, timesAST.arg2(), fPrecedence);
								return true;
							}
						} else {
							precedenceOpen(buf, precedence);
						}
					}
				} else {
					if (caller == PLUS_CALL) {
						if ((arg1.isReal()) && arg1.isNegative()) {
							buf.append(" - ");
							arg1 = ((ISignedNumber) arg1).opposite();
						} else {
							buf.append(" + ");
						}
					} else {
						precedenceOpen(buf, precedence);
					}
					fFactory.convert(buf, arg1, fPrecedence);
					if (fOperator.compareTo("") != 0) {
						if (size > 2) {
							if (timesAST.arg1().isNumber() && isTeXNumberDigit(timesAST.arg2())) {
								// Issue #67, #117: if we have 2 TeX number
								// expressions we use
								// the \cdot operator see
								// http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
								buf.append("\\cdot ");
							} else {
								buf.append("\\,");
							}
						}
					}
				}
			}

			for (int i = 2; i < size; i++) {
				if (i == 2 && (arg1.isOne() || arg1.isMinusOne())) {
					fFactory.convert(buf, timesAST.get(i), precedence);
				} else {
					fFactory.convert(buf, timesAST.get(i), fPrecedence);
				}
				if ((i < timesAST.argSize()) && (fOperator.compareTo("") != 0)) {
					if (timesAST.get(1).isNumber() && isTeXNumberDigit(timesAST.get(i + 1))) {
						// Issue #67, #117: if we have 2 TeX number expressions we
						// use
						// the \cdot operator see
						// http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
						buf.append("\\cdot ");
					} else {
						buf.append("\\,");
					}
				}
			}
			precedenceClose(buf, precedence);
			return true;
		}

		/**
		 * Does the TeX Form of <code>expr</code> begin with a number digit?
		 * 
		 * @param expr
		 * @return
		 */
		private boolean isTeXNumberDigit(IExpr expr) {
			if (expr.isNumber()) {
				return true;
			}
			if (expr.isPower() && expr.base().isNumber() && !expr.exponent().isFraction()) {
				return true;
			}
			return false;
		}
	}

	private final static class UnaryFunction extends AbstractConverter {
		String pre;
		String post;

		/** constructor will be called by reflection */
		public UnaryFunction(String pre, String post) {
			this.pre = pre;
			this.post = post;
		}

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() != 2) {
				return false;
			}
			buf.append(pre);
			fFactory.convert(buf, f.arg1(), 0);
			buf.append(post);
			return true;
		}
	}

	private final static class Zeta extends AbstractConverter {
		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			fFactory.convertAST(buf, f, "zeta ");
			return true;
		}
	}

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
	public final static HashMap<String, AbstractConverter> operTab = new HashMap<String, AbstractConverter>(199);

	public final static boolean USE_IDENTIFIERS = false;

	private int plusPrec;

	protected NumberFormat fNumberFormat = null;

	/**
	 * Constructor
	 */
	public TeXFormFactory() {
		this("", null);
	}

	public TeXFormFactory(final String tagPrefix) {
		this(tagPrefix, null);
	}

	public TeXFormFactory(final String tagPrefix, NumberFormat numberFormat) {
		fNumberFormat = numberFormat;
		init();
	}

	public void convert(final StringBuilder buf, final Object o, final int precedence) {
		if (o instanceof IExpr) {
			IExpr expr = (IExpr) o;
			String str = CONSTANT_EXPRS.get(expr);
			if (str != null) {
				buf.append(str);
				return;
			}
		}
		if (o instanceof IAST) {
			final IAST f = ((IAST) o);
			IExpr h = f.head();
			if (h.isSymbol()) {
				String headStr = ((ISymbol) h).getSymbolName();
				if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
					String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
					if (str != null) {
						headStr = str;
					}
				}
				final AbstractConverter converter = operTab.get(headStr);
				if (converter != null) {
					converter.setFactory(this);
					if (converter.convert(buf, f, precedence)) {
						return;
					}
				}
			}
			convertAST(buf, f);
			return;
		}
		if (o instanceof IInteger) {
			convertInteger(buf, (IInteger) o, precedence);
			return;
		}
		if (o instanceof IFraction) {
			convertFraction(buf, (IFraction) o, precedence);
			return;
		}
		if (o instanceof INum) {
			convertDouble(buf, (INum) o, precedence);
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
		// if (o instanceof BigFraction) {
		// convertFraction(buf, (BigFraction) o, precedence);
		// return;
		// }
		convertString(buf, o.toString());
	}

	public void convertAST(StringBuilder buf, final IAST f) {
		convertHead(buf, f.head());
		buf.append("(");
		for (int i = 1; i < f.size(); i++) {
			convert(buf, f.get(i), 0);
			if (i < f.argSize()) {
				buf.append(',');
			}
		}
		buf.append(")");

	}

	public void convertAST(StringBuilder buf, final IAST f, String headString) {
		buf.append(headString);
		buf.append("(");
		for (int i = 1; i < f.size(); i++) {
			convert(buf, f.get(i), 0);
			if (i < f.argSize()) {
				buf.append(',');
			}
		}
		buf.append(")");

	}

	public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence) {
		if (c.isImaginaryUnit()) {
			buf.append("i ");
			return;
		}
		if (c.isNegativeImaginaryUnit()) {
			if (precedence > plusPrec) {
				buf.append("\\left( ");
			}
			buf.append(" - i ");
			if (precedence > plusPrec) {
				buf.append("\\right) ");
			}
			return;
		}
		if (precedence > plusPrec) {
			buf.append("\\left( ");
		}
		IRational re = c.getRealPart();
		IRational im = c.getImaginaryPart();
		if (!re.isZero()) {
			convert(buf, re, 0);
			if (im.compareInt(0) >= 0) {
				buf.append(" + ");
			} else {
				buf.append(" - ");
				im = im.negate();
			}
		}
		convert(buf, im, 0);
		buf.append("\\,"); // InvisibleTimes
		buf.append("i ");
		if (precedence > plusPrec) {
			buf.append("\\right) ");
		}
	}

	public void convertDouble(final StringBuilder buf, final INum d, final int precedence) {
		if (d.isZero()) {
			buf.append(convertDoubleToFormattedString(0.0));
			return;
		}
		final boolean isNegative = d.isNegative();
		if (isNegative && (precedence > plusPrec)) {
			buf.append("\\left( ");
		}
		buf.append(convertDoubleToFormattedString(d.getRealPart()));
		if (isNegative && (precedence > plusPrec)) {
			buf.append("\\right) ");
		}
	}

	// public void convertFraction(final StringBuilder buf, final BigFraction f, final int precedence) {
	// boolean negative = f.compareTo(BigFraction.ZERO) < 0;
	// if (negative && (precedence > plusPrec)) {
	// buf.append("\\left( ");
	// }
	// if (f.getDenominator().equals(BigInteger.ONE)) {
	// buf.append(f.getNumerator().toString());
	// } else {
	// buf.append("\\frac{");
	// buf.append(f.getNumerator().toString());
	// buf.append("}{");
	// buf.append(f.getDenominator().toString());
	// buf.append('}');
	// }
	// if (negative && (precedence > plusPrec)) {
	// buf.append("\\right) ");
	// }
	// }

	public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc, final int precedence) {
		double re = dc.getRealPart();
		double im = dc.getImaginaryPart();
		if (F.isZero(re)) {
			if (F.isNumIntValue(im, 1)) {
				buf.append("i ");
				return;
			}
			if (F.isNumIntValue(im, -1)) {
				if (precedence > plusPrec) {
					buf.append("\\left( ");
				}
				buf.append(" - i ");
				if (precedence > plusPrec) {
					buf.append("\\right) ");
				}
				return;
			}
		}
		if (precedence > plusPrec) {
			buf.append("\\left( ");
		}
		if (!F.isZero(re)) {
			buf.append(convertDoubleToFormattedString(re));
			if (im >= 0.0) {
				buf.append(" + ");
			} else {
				buf.append(" - ");
				im = -im;
			}
		}
		buf.append(convertDoubleToFormattedString(im));
		buf.append("\\,"); // InvisibleTimes
		buf.append("i ");
		if (precedence > plusPrec) {
			buf.append("\\right) ");
		}
	}

	protected String convertDoubleToFormattedString(double dValue) {
		return fNumberFormat == null ? Double.toString(dValue) : fNumberFormat.format(dValue);
	}

	public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
		if (f.isNegative() && (precedence > plusPrec)) {
			buf.append("\\left( ");
		}
		if (f.denominator().isOne()) {
			buf.append(f.numerator().toString());
		} else {
			buf.append("\\frac{");
			buf.append(f.toBigNumerator().toString());
			buf.append("}{");
			buf.append(f.toBigDenominator().toString());
			buf.append('}');
		}
		if (f.isNegative() && (precedence > plusPrec)) {
			buf.append("\\right) ");
		}
	}

	public void convertHead(final StringBuilder buf, final Object obj) {
		if (obj instanceof ISymbol) {
			String str = ((ISymbol) obj).getSymbolName();
			final Object ho = CONSTANT_SYMBOLS.get(((ISymbol) obj).getSymbolName());
			if ((ho != null) && ho.equals(AST2Expr.TRUE_STRING)) {
				buf.append('\\');
				buf.append(str);
				return;
			}

			if (str.length() == 1) {
				buf.append(str);
			} else {
				buf.append("\\text{");
				String header = str;
				if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
					str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(header);
					if (str != null) {
						header = str;
					}
				}
				buf.append(header);
				buf.append('}');
			}
			return;
		}
		convert(buf, obj, 0);
	}

	public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence) {
		if (i.isNegative() && (precedence > plusPrec)) {
			buf.append("\\left( ");
		}
		buf.append(i.toBigNumerator().toString());
		if (i.isNegative() && (precedence > plusPrec)) {
			buf.append("\\right) ");
		}
	}

	/*
	 * 
	 * '{': r'\{', '}': r'\}', '_': r'\_', '$': r'\$', '%': r'\%', '#': r'\#', '&': r'\&',7
	 */
	public void convertString(final StringBuilder buf, final String str) {
		buf.append("\\textnormal{");
		String text = str.replaceAll("\\&", "\\\\&");
		text = text.replaceAll("\\#", "\\\\#");
		text = text.replaceAll("\\%", "\\\\%");
		text = text.replaceAll("\\$", "\\\\\\$");
		text = text.replaceAll("\\_", "\\\\_");
		text = text.replaceAll("\\{", "\\\\{");
		text = text.replaceAll("\\}", "\\\\}");
		text = text.replaceAll("\\<", "\\$<\\$");
		text = text.replaceAll("\\>", "\\$>\\$");
		buf.append(text);
		buf.append("}");
	}

	private void convertSubExpr(StringBuilder buf, IExpr o, int precedence) {
		if (o.isAST()) {
			buf.append("{");
		}
		convert(buf, o, precedence);
		if (o.isAST()) {
			buf.append("}");
		}
	}

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
			buf.append(sym.getSymbolName());
		} else {
			if (convertedSymbol.equals(AST2Expr.TRUE_STRING)) {
				buf.append('\\');
				buf.append(sym.getSymbolName());
			} else {
				if (convertedSymbol instanceof Operator) {
					((Operator) convertedSymbol).convert(buf);
				} else {
					buf.append(convertedSymbol.toString());
				}
			}
		}
	}

	public void init() {
		plusPrec = ASTNodeFactory.RELAXED_STYLE_FACTORY.get("Plus").getPrecedence();
		// timesPrec =
		// ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence();
		operTab.put("Abs", new UnaryFunction("|", "|"));
		operTab.put("Binomial", new Binomial());
		operTab.put("Ceiling", new UnaryFunction(" \\left \\lceil ", " \\right \\rceil "));
		operTab.put("Complex", new Complex());
		operTab.put("CompoundExpression",
				new AbstractOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ", "));
		operTab.put("D", new D());
		operTab.put("DirectedInfinity", new DirectedInfinity());
		operTab.put("Floor", new UnaryFunction(" \\left \\lfloor ", " \\right \\rfloor "));
		operTab.put("HarmonicNumber", new HarmonicNumber());
		operTab.put("HurwitzZeta", new Zeta());
		operTab.put("Integrate", new Integrate());
		operTab.put("Limit", new Limit());
		operTab.put("List", new List());
		operTab.put("MatrixForm", new MatrixForm());
		operTab.put("TableForm", new TableForm());
		operTab.put("Plus", new Plus());
		operTab.put("Power", new Power());
		operTab.put("Product", new Product());
		operTab.put("Rational", new Rational());
		operTab.put("Sqrt", new UnaryFunction("\\sqrt{", "}"));
		operTab.put("Subscript", new Subscript());
		operTab.put("Subsuperscript", new Subsuperscript());
		operTab.put("Sum", new Sum());
		operTab.put("Superscript", new Superscript());
		operTab.put("Times", new Times());
		operTab.put("Zeta", new Zeta());

		operTab.put("Condition", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Condition").getPrecedence(), "\\text{/;}"));
		operTab.put("Unset",
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Unset").getPrecedence(), "\\text{=.}"));
		operTab.put("UpSetDelayed", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("UpSetDelayed").getPrecedence(), "\\text{^:=}"));
		operTab.put("UpSet", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("UpSet").getPrecedence(),
				"\\text{^=}"));
		operTab.put("NonCommutativeMultiply", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("NonCommutativeMultiply").getPrecedence(), "\\text{**}"));
		operTab.put("PreDecrement", new PreOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("PreDecrement").getPrecedence(), "\\text{--}"));
		operTab.put("ReplaceRepeated", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceRepeated").getPrecedence(), "\\text{//.}"));
		operTab.put("MapAll", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("MapAll").getPrecedence(),
				"\\text{//@}"));
		operTab.put("AddTo", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("AddTo").getPrecedence(),
				"\\text{+=}"));
		operTab.put("Greater",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), " > "));
		operTab.put("GreaterEqual", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("GreaterEqual").getPrecedence(), "\\geq "));
		operTab.put("SubtractFrom", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("SubtractFrom").getPrecedence(), "\\text{-=}"));
		operTab.put("Subtract",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Subtract").getPrecedence(), " - "));
		operTab.put("CompoundExpression", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ";"));
		operTab.put("DivideBy", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("DivideBy").getPrecedence(), "\\text{/=}"));
		operTab.put("StringJoin", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("StringJoin").getPrecedence(), "\\text{<>}"));
		operTab.put("UnsameQ", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("UnsameQ").getPrecedence(), "\\text{=!=}"));
		operTab.put("Decrement", new PostOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Decrement").getPrecedence(), "\\text{--}"));
		operTab.put("LessEqual", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(), "\\leq "));
		operTab.put("Colon",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Colon").getPrecedence(), "\\text{:}"));
		operTab.put("Increment", new PostOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Increment").getPrecedence(), "\\text{++}"));
		operTab.put("Alternatives", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Alternatives").getPrecedence(), "\\text{|}"));
		operTab.put("Equal",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Equal").getPrecedence(), " = "));
		operTab.put("Divide", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Divide").getPrecedence(),
				"\\text{/}"));
		operTab.put("Apply", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Apply").getPrecedence(),
				"\\text{@@}"));
		operTab.put("Set",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), "\\text{=}"));
		operTab.put("PreMinus",
				new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("PreMinus").getPrecedence(), "\\text{-}"));
		operTab.put("Map",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Map").getPrecedence(), "\\text{/@}"));
		operTab.put("SameQ", new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("SameQ").getPrecedence(),
				"\\text{===}"));
		operTab.put("Less",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Less").getPrecedence(), " < "));
		operTab.put("PreIncrement", new PreOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("PreIncrement").getPrecedence(), "\\text{++}"));
		operTab.put("Unequal", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Unequal").getPrecedence(), "\\text{!=}"));
		operTab.put("Or",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Or").getPrecedence(), " \\lor "));
		operTab.put("PrePlus",
				new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("PrePlus").getPrecedence(), "\\text{+}"));
		operTab.put("TimesBy", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("TimesBy").getPrecedence(), "\\text{*=}"));
		operTab.put("And",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("And").getPrecedence(), " \\land "));
		operTab.put("Not",
				new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Not").getPrecedence(), "\\neg "));
		operTab.put("Factorial",
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Factorial").getPrecedence(), " ! "));
		operTab.put("Factorial2",
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Factorial2").getPrecedence(), " !! "));

		operTab.put("ReplaceAll", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceAll").getPrecedence(), "\\text{/.}\\,"));
		operTab.put("ReplaceRepeated", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceRepeated").getPrecedence(), "\\text{//.}\\,"));
		operTab.put("Rule",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Rule").getPrecedence(), "\\to "));
		operTab.put("RuleDelayed", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(), ":\\to "));
		operTab.put("Set",
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), " = "));
		operTab.put("SetDelayed", new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("SetDelayed").getPrecedence(), "\\text{:=}\\,"));
		operTab.put("Sin", new TeXFunction(this, "sin"));
		operTab.put("Cos", new TeXFunction(this, "cos"));
		operTab.put("Tan", new TeXFunction(this, "tan"));
		operTab.put("Cot", new TeXFunction(this, "cot"));
		operTab.put("Sinh", new TeXFunction(this, "sinh"));
		operTab.put("Cosh", new TeXFunction(this, "cosh"));
		operTab.put("Tanh", new TeXFunction(this, "tanh"));
		operTab.put("Coth", new TeXFunction(this, "coth"));
		operTab.put("Csc", new TeXFunction(this, "csc"));
		operTab.put("Sec", new TeXFunction(this, "sec"));
		operTab.put("ArcSin", new TeXFunction(this, "arcsin"));
		operTab.put("ArcCos", new TeXFunction(this, "arccos"));
		operTab.put("ArcTan", new TeXFunction(this, "arctan"));
		operTab.put("ArcCot", new TeXFunction(this, "arccot"));
		operTab.put("ArcSinh", new TeXFunction(this, "arcsinh"));
		operTab.put("ArcCosh", new TeXFunction(this, "arccosh"));
		operTab.put("ArcTanh", new TeXFunction(this, "arctanh"));
		operTab.put("ArcCoth", new TeXFunction(this, "arccoth"));
		operTab.put("Log", new TeXFunction(this, "log"));

		CONSTANT_SYMBOLS.put("Alpha", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Beta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Chi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Delta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Epsilon", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Phi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Gamma", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Eta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Iota", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("varTheta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Kappa", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Lambda", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Mu", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Nu", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Omicron", AST2Expr.TRUE_STRING);

		CONSTANT_SYMBOLS.put("Theta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Rho", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Sigma", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Tau", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Upsilon", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Omega", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Xi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Psi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("Zeta", AST2Expr.TRUE_STRING);

		CONSTANT_SYMBOLS.put("alpha", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("beta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("chi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("selta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("epsilon", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("phi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("gamma", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("eta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("iota", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("varphi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("kappa", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("lambda", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("mu", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("nu", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("omicron", AST2Expr.TRUE_STRING);
		// see F.Pi
		// CONSTANT_SYMBOLS.put("pi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("theta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("rho", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("sigma", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("tau", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("upsilon", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("varomega", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("omega", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("xi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("psi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("zeta", AST2Expr.TRUE_STRING);

		CONSTANT_EXPRS.put(F.Catalan, "C");
		CONSTANT_EXPRS.put(F.Degree, "{}^{\\circ}");
		CONSTANT_EXPRS.put(F.Glaisher, "A");
		CONSTANT_EXPRS.put(F.GoldenRatio, "\\phi");
		CONSTANT_EXPRS.put(F.EulerGamma, "\\gamma");
		CONSTANT_EXPRS.put(F.Khinchin, "K");
		CONSTANT_EXPRS.put(F.Pi, "\\pi");
		CONSTANT_EXPRS.put(F.CInfinity, "\\infty");
		CONSTANT_EXPRS.put(F.CNInfinity, "-\\infty");
	}

}
