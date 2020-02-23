package org.matheclipse.core.form.tex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
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
import org.matheclipse.core.trie.Tries;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * <p>
 * Generates TeX presentation output
 * </p>
 * 
 */
public class TeXFormFactory {
	/**
	 * The conversion wasn't called with an operator preceding the <code>IExpr</code> object.
	 */
	public final static boolean NO_PLUS_CALL = false;

	/**
	 * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code> object.
	 */
	public final static boolean PLUS_CALL = true;

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
				fFactory.convertInternal(buf, f.get(i), fPrecedence);
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
			if (f.size() != 3) {
				return false;
			}
			buf.append('{');
			fFactory.convertInternal(buf, f.arg1(), 0);
			buf.append("\\choose ");
			fFactory.convertInternal(buf, f.arg2(), 0);
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
				fFactory.convertInternal(buf, arg1, 0);
			}
			if (!imZero) {
				if (!reZero && !arg2.isNegativeSigned()) {
					buf.append(" + ");
				}
				if (arg2.isMinusOne()) {
					buf.append(" - ");
				} else if (!arg2.isOne()) {
					fFactory.convertInternal(buf, arg2, 0);
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
				fFactory.convertInternal(buf, f.arg2(), 0);
				buf.append("}}");
				fFactory.convertInternal(buf, f.arg1(), 0);

				return true;
			}
			return false;
		}
	}

	private final static class DirectedInfinity extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.isComplexInfinity()) {
				buf.append("ComplexInfinity");
				return true;
			} else if (f.isAST1()) {
				if (f.arg1().isOne()) {
					buf.append("\\infty");
					return true;
				} else if (f.arg1().isMinusOne()) {
					buf.append("- \\infty");
					return true;
				}
			}
			return false;
		}
	}

	private final static class HoldForm extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() == 2) {
				fFactory.convertInternal(buf, f.arg1(), 0);
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
				fFactory.convertInternal(buf, f.arg1(), 0);
				return true;
			} else if (f.isAST2()) {
				buf.append("H_");
				fFactory.convertInternal(buf, f.arg1(), 0);

				buf.append("^{(");
				fFactory.convertInternal(buf, f.arg2(), 0);
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
				fFactory.convertInternal(buf, f.arg1(), 0);
				return true;
			}
			if (f.get(i).isList()) {
				IAST list = (IAST) f.get(i);
				if (list.size() == 4 && list.arg1().isSymbol()) {
					ISymbol symbol = (ISymbol) list.arg1();
					buf.append(mathSymbol);
					buf.append("_{");
					fFactory.convertInternal(buf, list.arg2(), 0);
					buf.append("}^{");
					fFactory.convertInternal(buf, list.arg3(), 0);
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

			if ((ast.getEvalFlags() & IAST.OUTPUT_MULTILINE) == IAST.OUTPUT_MULTILINE) {
				if (convertMultiline(buf, ast)) {
					return true;
				}
			}
			if ((ast instanceof ASTRealMatrix) || //
					(ast.getEvalFlags() & IAST.IS_MATRIX) == IAST.IS_MATRIX) {
				int[] dims = ast.isMatrix();
				if (dims != null) {
					// create a LaTeX matrix

					// problem with KaTeX?
					// buf.append("\\left(\n\\begin{array}{");
					// for (int i = 0; i < dims[1]; i++) {
					// buf.append("c");
					// }
					// buf.append("}\n");
					// if (ast.size() > 1) {
					// for (int i = 1; i < ast.size(); i++) {
					// IAST row = ast.getAST(i);
					// for (int j = 1; j < row.size(); j++) {
					// fFactory.convert(buf, row.get(j), 0);
					// if (j < row.argSize()) {
					// buf.append(" & ");
					// }
					// }
					// if (i < ast.argSize()) {
					// buf.append(" \\\\\n");
					// } else {
					//
					// buf.append(" \n");
					// }
					// }
					// }
					// buf.append("\\end{array}\n\\right) ");

					final IAST matrix = ast;
					buf.append("\\begin{pmatrix}\n");
					IAST row;
					for (int i = 1; i < matrix.size(); i++) {
						row = (IAST) matrix.get(i);
						for (int j = 1; j < row.size(); j++) {
							buf.append(' ');
							fFactory.convertInternal(buf, row.get(j), 0);
							buf.append(' ');
							if (j < row.argSize()) {
								buf.append('&');
							}
						}
						buf.append("\\\\\n");
					}

					buf.append("\\end{pmatrix}");
					return true;
				}
			}

			if ((ast.getEvalFlags() & IAST.IS_VECTOR) == IAST.IS_VECTOR) {
				// create a LaTeX row vector
				// \begin{pmatrix} x & y \end{pmatrix}
				buf.append("\\begin{pmatrix} ");
				if (ast.size() > 1) {
					for (int j = 1; j < ast.size(); j++) {
						fFactory.convertInternal(buf, ast.get(j), 0);
						if (j < ast.argSize()) {
							buf.append(" & ");
						}
					}
				}
				buf.append(" \\end{pmatrix} ");
			} else {
				buf.append("\\{");
				if (ast.size() > 1) {
					fFactory.convertInternal(buf, ast.arg1(), 0);
					for (int i = 2; i < ast.size(); i++) {
						buf.append(',');
						fFactory.convertInternal(buf, ast.get(i), 0);
					}
				}
				buf.append("\\}");
			}
			return true;
		}

		private boolean convertMultiline(final StringBuilder buf, final IAST list) {

			buf.append("\\begin{array}{c}\n");
			IExpr element;
			for (int i = 1; i < list.size(); i++) {
				element = list.get(i);
				buf.append(' ');
				fFactory.convertInternal(buf, element, 0);
				buf.append(' ');
				if (i < list.argSize()) {
					buf.append("\\\\\n");
				}
			}
			buf.append("\n\\end{array}");

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
						fFactory.convertInternal(buf, element, 0);
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
						fFactory.convertInternal(buf, row.get(j), 0);
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
						fFactory.convertInternal(buf, element, 0);
						buf.append(' ');
						if (i < vector.argSize()) {
							buf.append("\\\\\n");
						}
					}
					buf.append("\n\\end{array}");
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
						fFactory.convertInternal(buf, row.get(j), 0);
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

	private final static class Part extends AbstractConverter {

		/** {@inheritDoc} */
		@Override
		public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
			if (f.size() > 2) {
				fFactory.convertHead(buf, f.arg1());
				buf.append("[[");
				int argSize = f.argSize();
				for (int i = 2; i <= argSize; i++) {
					fFactory.convertInternal(buf, f.get(i), 0);
					if (i < argSize) {
						buf.append(",");
					}
				}
				buf.append("]]");
				return true;
			}
			return false;
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
					fFactory.convertInternal(buf, expr, fPrecedence);
				}
			}
			precedenceClose(buf, precedence);
			return true;
		}

	}

	private final static class PostOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fOperator;

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
			fFactory.convertInternal(buf, f.arg1(), fPrecedence);
			buf.append(fOperator);
			precedenceClose(buf, precedence);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence >= fPrecedence) {
				buf.append("\\right) ");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence >= fPrecedence) {
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
			if (arg2.isNumEqualRational(F.C1D2)) {
				buf.append("\\sqrt{");
				fFactory.convertInternal(buf, arg1, fPrecedence);
				buf.append('}');
				return true;
			}
			if (arg2.isFraction()) {
				if (((IFraction) arg2).numerator().isOne()) {
					buf.append("\\sqrt[");
					fFactory.convertInternal(buf, ((IFraction) arg2).denominator(), fPrecedence);
					buf.append("]{");
					fFactory.convertInternal(buf, arg1, fPrecedence);
					buf.append('}');
					return true;
				}
			}

			precedenceOpen(buf, precedence);

			// http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
			// For powers with more than one digit, surround the power with {}.
			buf.append('{');
			fFactory.convertInternal(buf, arg1, fPrecedence);
			buf.append('}');
			if (fOperator.compareTo("") != 0) {
				buf.append(fOperator);
			}

			buf.append('{');
			fFactory.convertInternal(buf, arg2, 0);
			buf.append('}');
			precedenceClose(buf, precedence);
			return true;
		}
	}

	private final static class PreOperator extends AbstractConverter {
		protected int fPrecedence;
		protected String fOperator;

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
			fFactory.convertInternal(buf, f.arg1(), fPrecedence);
			precedenceClose(buf, precedence);
			return true;
		}

		public void precedenceClose(final StringBuilder buf, final int precedence) {
			if (precedence >= fPrecedence) {
				buf.append("\\right) ");
			}
		}

		public void precedenceOpen(final StringBuilder buf, final int precedence) {
			if (precedence >= fPrecedence) {
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
			fFactory.convertInternal(buf, f.arg1(), fPrecedence);
			buf.append("}{");
			fFactory.convertInternal(buf, f.arg2(), fPrecedence);
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

			// http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
			// For powers with more than one digit, surround the power with {}.
			buf.append('{');
			fFactory.convertInternal(buf, arg1, precedence);
			buf.append('}');
			buf.append("_");

			buf.append('{');
			fFactory.convertInternal(buf, arg2, precedence);
			buf.append('}');
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

			// http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
			// For powers with more than one digit, surround the power with {}.
			buf.append('{');
			fFactory.convertInternal(buf, arg1, Integer.MAX_VALUE);
			buf.append('}');
			buf.append("_");

			buf.append('{');
			fFactory.convertInternal(buf, arg2, Integer.MAX_VALUE);
			buf.append('}');
			buf.append("^");

			buf.append('{');
			fFactory.convertInternal(buf, arg3, Integer.MAX_VALUE);
			buf.append('}');
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
				try {
					IIterator<IExpr> iterator = Iterator.create((IAST) f.get(i), i, EvalEngine.get());
					if (iterator.isValidVariable() && iterator.getStep().isOne()) {
						buf.append(mathSymbol);
						buf.append("_{");
						fFactory.convertSubExpr(buf, iterator.getVariable(), 0);
						buf.append(" = ");
						fFactory.convertSubExpr(buf, iterator.getLowerLimit(), 0);
						buf.append("}^{");
						fFactory.convertInternal(buf, iterator.getUpperLimit(), 0);
						buf.append('}');
						if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
							return false;
						}
						return true;
					}
				} catch (ArgumentTypeException ate) {
					// see Ietrator definition
				}
				return false;
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

			// http://en.wikibooks.org/wiki/LaTeX/Mathematics#Powers_and_indices
			// For powers with more than one digit, surround the power with {}.
			buf.append('{');
			fFactory.convertInternal(buf, arg1, 0);
			buf.append('}');

			buf.append("^");
			buf.append('{');
			fFactory.convertInternal(buf, arg2, 0);
			buf.append('}');
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
				fFactory.convertInternal(buf, f.get(i), 0);
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

		// public static Times CONST = new Times();

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
			IExpr[] parts = Algebra.fractionalPartsTimesPower(f, false, true, false, false, false, false);
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
					fFactory.convertInternal(buf, numerator, precedence);
				}
				buf.append("}{");
				// insert denominator in buffer:
				if (denominator.isTimes()) {
					convertTimesOperator(buf, (IAST) denominator, fPrecedence, NO_SPECIAL_CALL);
				} else {
					fFactory.convertInternal(buf, denominator, precedence);
				}
				buf.append('}');
			} else {
				if (numerator.isTimes()) {
					convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
				} else {
					fFactory.convertInternal(buf, numerator, precedence);
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
						fFactory.convertInternal(buf, arg1, fPrecedence);
					} else {
						if (caller == PLUS_CALL) {
							buf.append(" - ");
							if (size == 3) {
								fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence);
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
						fFactory.convertInternal(buf, arg1, fPrecedence);
					} else {
						if (caller == PLUS_CALL) {
							if (size == 3) {
								buf.append(" + ");
								fFactory.convertInternal(buf, timesAST.arg2(), fPrecedence);
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
					fFactory.convertInternal(buf, arg1, fPrecedence);
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
					fFactory.convertInternal(buf, timesAST.get(i), precedence);
				} else {
					fFactory.convertInternal(buf, timesAST.get(i), fPrecedence);
				}
				if ((i < timesAST.argSize()) && (fOperator.compareTo("") != 0)) {
					if (timesAST.arg1().isNumber() && isTeXNumberDigit(timesAST.get(i + 1))) {
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
			fFactory.convertInternal(buf, f.arg1(), 0);
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
	public final static Map<String, Object> CONSTANT_SYMBOLS = Tries.forStrings();

	/**
	 * Table for constant expressions
	 */
	public final static HashMap<IExpr, String> CONSTANT_EXPRS = new HashMap<IExpr, String>(199);

	/**
	 * Description of the Field
	 */
	public final static HashMap<ISymbol, AbstractConverter> operTab = new HashMap<ISymbol, AbstractConverter>(199);

	public final static boolean USE_IDENTIFIERS = false;

	private int plusPrec;

	// protected NumberFormat fNumberFormat = null;
	private int fExponentFigures;
	private int fSignificantFigures;

	/**
	 * Constructor
	 */
	public TeXFormFactory() {
		this(-1, -1);
	}

	/**
	 * 
	 * @param exponentFigures
	 * @param significantFigures
	 */
	public TeXFormFactory(int exponentFigures, int significantFigures) {
		fExponentFigures = exponentFigures;
		fSignificantFigures = significantFigures;
		init();
	}

	public void convertApcomplex(final StringBuilder buf, final Apcomplex dc, final int precedence, boolean caller) {
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			if (caller == PLUS_CALL) {
				buf.append(" + ");
				caller = false;
			}
			buf.append("\\left( ");
		}
		Apfloat realPart = dc.real();
		Apfloat imaginaryPart = dc.imag();
		boolean realZero = realPart.equals(Apcomplex.ZERO);
		boolean imaginaryZero = imaginaryPart.equals(Apcomplex.ZERO);
		if (realZero && imaginaryZero) {
			convertDoubleString(buf, "0.0", ASTNodeFactory.PLUS_PRECEDENCE, false);
		} else {
			if (!realZero) {
				buf.append(convertApfloat(realPart));
				if (!imaginaryZero) {
					buf.append(" + ");
					final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
					convertDoubleString(buf, convertApfloat(imaginaryPart), ASTNodeFactory.TIMES_PRECEDENCE,
							isNegative);
					buf.append("\\,"); // InvisibleTimes
					buf.append("i ");
				}
			} else {
				if (caller == PLUS_CALL) {
					buf.append("+");
					caller = false;
				}

				final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
				convertDoubleString(buf, convertApfloat(imaginaryPart), ASTNodeFactory.TIMES_PRECEDENCE, isNegative);
				buf.append("\\,"); // InvisibleTimes
				buf.append("i ");
			}
		}
		if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
			buf.append("\\right) ");
		}
	}

	public static String convertApfloat(Apfloat num) {
		String str = num.toString();
		int index = str.indexOf('e');
		if (index > 0) {
			String exponentStr = str.substring(index + 1);
			String result = str.substring(0, index);
			return result + "*10^" + exponentStr;
		}
		return str;
	}

	public boolean convert(final StringBuilder buf, final IExpr o, final int precedence) {
		try {
			convertInternal(buf, o, precedence);
			if (buf.length() >= Config.MAX_OUTPUT_SIZE) {
				return false;
			}
			return true;
		} catch (RuntimeException rex) {
			if (Config.SHOW_STACKTRACE) {
				rex.printStackTrace();
			}
		} catch (OutOfMemoryError oome) {
		}
		return false;
	}

	private void convertInternal(final StringBuilder buf, final Object o, final int precedence) {
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
				final AbstractConverter converter = operTab.get(((ISymbol) h));
				if (converter != null) {
					converter.setFactory(this);
					if (converter.convert(buf, f, precedence)) {
						return;
					}
				}
			}
			convertAST(buf, f, precedence);
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
			if (o instanceof ApcomplexNum) {
				convertApcomplex(buf, ((ApcomplexNum) o).apcomplexValue(), precedence, NO_PLUS_CALL);
				return;
			}
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

	public void convertAST(StringBuilder buf, final IAST f, int precedence) {

		int functionID = ((ISymbol) f.head()).ordinal();
		if (functionID > ID.UNKNOWN) {
			switch (functionID) {
			case ID.Inequality:
				if (f.size() > 3 && convertInequality(buf, f, precedence)) {
					return;
				}
				break;
			case ID.Interval:
				if (f.size() > 1 && f.first().isASTSizeGE(F.List, 2)) {
					IAST interval = IntervalSym.normalize(f);
					buf.append("Interval(");
					for (int i = 1; i < interval.size(); i++) {
						buf.append("\\{");

						IAST subList = (IAST) interval.get(i);
						IExpr min = subList.arg1();
						IExpr max = subList.arg2();
						if (min instanceof INum) {
							convertDoubleString(buf, convertDoubleToFormattedString(min.evalDouble()), 0, false);
						} else {
							convertInternal(buf, min, 0);
						}
						buf.append(",");
						if (max instanceof INum) {
							convertDoubleString(buf, convertDoubleToFormattedString(max.evalDouble()), 0, false);
						} else {
							convertInternal(buf, max, 0);
						}
						buf.append("\\}");
						if (i < interval.size() - 1) {
							buf.append(",");
						}
					}
					buf.append(")");
					return;
				}
				break;
			}
		}

		if (f.isAssociation()) {
			convertAssociation(buf, (IAssociation) f, 0);
			return;
		}
		convertHead(buf, f.head());
		buf.append("(");
		for (int i = 1; i < f.size(); i++) {
			convertInternal(buf, f.get(i), 0);
			if (i < f.argSize()) {
				buf.append(',');
			}
		}
		buf.append(")");

	}

	/**
	 * Convert an association to TeX. <br />
	 * <code>&lt;|a -> x, b -> y, c -> z|&gt;</code> gives <br />
	 * <code>\langle|a\to x,b\to y,c\to z|\rangle</code>
	 * 
	 * @param buf
	 * @param assoc
	 * @param precedence
	 * @return
	 */
	public boolean convertAssociation(final StringBuilder buf, final IAssociation assoc, final int precedence) {
		IAST ast = assoc.normal();
		buf.append("\\langle|");
		if (ast.size() > 1) {
			convertInternal(buf, ast.arg1(), 0);
			for (int i = 2; i < ast.size(); i++) {
				buf.append(',');
				convertInternal(buf, ast.get(i), 0);
			}
		}
		buf.append("|\\rangle");

		return true;
	}

	private boolean convertInequality(final StringBuilder buf, final IAST inequality, final int precedence) {
		int operPrecedence = ASTNodeFactory.EQUAL_PRECEDENCE;
		StringBuilder tempBuffer = new StringBuilder();

		if (operPrecedence < precedence) {
			tempBuffer.append("(");
		}

		final int listSize = inequality.size();
		int i = 1;
		while (i < listSize) {
			convertInternal(tempBuffer, inequality.get(i++), 0);
			if (i == listSize) {
				if (operPrecedence < precedence) {
					tempBuffer.append(")");
				}
				buf.append(tempBuffer);
				return true;
			}
			IExpr head = inequality.get(i++);
			if (head.isBuiltInSymbol()) {
				int id = ((IBuiltInSymbol) head).ordinal();
				switch (id) {
				case ID.Equal:
					tempBuffer.append(" == ");
					break;
				case ID.Greater:
					tempBuffer.append(" > ");
					break;
				case ID.GreaterEqual:
					tempBuffer.append("\\geq ");
					break;
				case ID.Less:
					tempBuffer.append(" < ");
					break;
				case ID.LessEqual:
					tempBuffer.append("\\leq ");
					break;
				case ID.Unequal:
					tempBuffer.append("\\neq ");
					break;
				default:
					return false;
				}
			} else {
				return false;
			}
		}
		if (operPrecedence < precedence) {
			tempBuffer.append(")");
		}
		buf.append(tempBuffer);
		return true;
	}

	public void convertAST(StringBuilder buf, final IAST f, String headString) {
		buf.append(headString);
		buf.append("(");
		for (int i = 1; i < f.size(); i++) {
			convertInternal(buf, f.get(i), 0);
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
			convertInternal(buf, re, 0);
			if (im.compareInt(0) >= 0) {
				buf.append(" + ");
			} else {
				buf.append(" - ");
				im = im.negate();
			}
		}
		convertInternal(buf, im, 0);
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
		if (d instanceof Num) {
			convertDoubleString(buf, convertDoubleToFormattedString(d.getRealPart()), precedence, isNegative);
		} else {
			convertDoubleString(buf, convertApfloat(((INum) d).apfloatValue(d.precision())), precedence, isNegative);
		}
	}

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

	private void convertDoubleString(final StringBuilder buf, final String d, final int precedence,
			final boolean isNegative) {
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.append("\\left( ");
		}
		buf.append(d);
		if (isNegative && (ASTNodeFactory.PLUS_PRECEDENCE < precedence)) {
			buf.append("\\right) ");
		}
	}

	protected String convertDoubleToFormattedString(double dValue) {
		if (fSignificantFigures > 0) {
			try {
				StringBuilder buf = new StringBuilder();
				DoubleToMMA.doubleToMMA((Appendable) buf, dValue, fExponentFigures, fSignificantFigures, true);
				return buf.toString();
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
		}
		return Double.toString(dValue);
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

	public void convertHead(final StringBuilder buf, final IExpr obj) {
		if (obj instanceof ISymbol) {
			String str = ((ISymbol) obj).getSymbolName();
			final Object ho = CONSTANT_SYMBOLS.get(((ISymbol) obj).getSymbolName());
			if ((ho != null) && ho.equals(AST2Expr.TRUE_STRING)) {
				buf.append('\\');
				buf.append(str);
				return;
			}

			convertHeader(buf, str);
			return;
		}
		convertInternal(buf, obj, 0);
	}

	private void convertHeader(final StringBuilder buf, String str) {
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
		convertInternal(buf, o, precedence);
		if (o.isAST()) {
			buf.append("}");
		}
	}

	public void convertSymbol(final StringBuilder buf, final ISymbol sym) {
		Context context = sym.getContext();
		if (context == Context.DUMMY) {
			buf.append(sym.getSymbolName());
			return;
		}
		String headStr = sym.getSymbolName();
		if (headStr.length() == 1) {
			String c = Characters.unicodeName(headStr);
			if (c != null) {
				final Object convertedSymbol = CONSTANT_SYMBOLS.get(c);
				if (convertedSymbol != null) {
					convertConstantSymbol(buf, sym, convertedSymbol);
					return;
				}
			}
		}
		if (context.equals(Context.SYSTEM) || context.isGlobal()) {
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
				String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headStr);
				if (str != null) {
					headStr = str;
				}
			}
			final Object convertedSymbol = CONSTANT_SYMBOLS.get(headStr);
			if (convertedSymbol == null) {
				buf.append(headStr);
				return;
			} else {
				convertConstantSymbol(buf, sym, convertedSymbol);
				return;
			}
		}

		if (EvalEngine.get().getContextPath().contains(context)) {
			buf.append(sym.getSymbolName());
		} else {
			buf.append(context.toString() + sym.getSymbolName());
		}
	}

	private void convertConstantSymbol(final StringBuilder buf, final ISymbol sym, final Object convertedSymbol) {
		if (convertedSymbol.equals(AST2Expr.TRUE_STRING)) {
			buf.append('\\');
			buf.append(sym.getSymbolName());
			return;
		}
		if (convertedSymbol instanceof Operator) {
			((Operator) convertedSymbol).convert(buf);
			return;
		}
		buf.append(convertedSymbol.toString());
		return;
	}

	public void init() {
		plusPrec = ASTNodeFactory.RELAXED_STYLE_FACTORY.get("Plus").getPrecedence();
		// timesPrec =
		// ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence();
		operTab.put(F.Abs, new UnaryFunction("|", "|"));
		operTab.put(F.Binomial, new Binomial());
		operTab.put(F.Ceiling, new UnaryFunction(" \\left \\lceil ", " \\right \\rceil "));
		operTab.put(F.Complex, new Complex());
		operTab.put(F.CompoundExpression,
				new AbstractOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ", "));
		operTab.put(F.D, new D());
		operTab.put(F.Defer, new HoldForm());
		operTab.put(F.DirectedInfinity, new DirectedInfinity());
		operTab.put(F.Floor, new UnaryFunction(" \\left \\lfloor ", " \\right \\rfloor "));
		operTab.put(F.Function, new UnaryFunction("", "\\&"));
		operTab.put(F.HarmonicNumber, new HarmonicNumber());
		operTab.put(F.HoldForm, new HoldForm());
		operTab.put(F.HurwitzZeta, new Zeta());
		operTab.put(F.Integrate, new Integrate());
		operTab.put(F.Limit, new Limit());
		operTab.put(F.List, new List());
		operTab.put(F.$RealMatrix, new List());
		operTab.put(F.$RealVector, new List());
		operTab.put(F.MatrixForm, new MatrixForm());
		operTab.put(F.TableForm, new TableForm());
		operTab.put(F.Part, new Part());
		operTab.put(F.Plus, new Plus());
		operTab.put(F.Power, new Power());
		operTab.put(F.Product, new Product());
		operTab.put(F.Rational, new Rational());
		operTab.put(F.Slot, new UnaryFunction("\\text{$\\#$", "}"));
		operTab.put(F.SlotSequence, new UnaryFunction("\\text{$\\#\\#$", "}"));
		operTab.put(F.Sqrt, new UnaryFunction("\\sqrt{", "}"));
		operTab.put(F.Subscript, new Subscript());
		operTab.put(F.Subsuperscript, new Subsuperscript());
		operTab.put(F.Sum, new Sum());
		operTab.put(F.Superscript, new Superscript());
		operTab.put(F.Times, new Times());
		operTab.put(F.Zeta, new Zeta());

		operTab.put(F.Condition, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Condition").getPrecedence(), "\\text{/;}"));
		operTab.put(F.Unset,
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Unset").getPrecedence(), "\\text{=.}"));
		operTab.put(F.UpSetDelayed, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("UpSetDelayed").getPrecedence(), "\\text{^:=}"));
		operTab.put(F.UpSet, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("UpSet").getPrecedence(),
				"\\text{^=}"));
		operTab.put(F.NonCommutativeMultiply, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("NonCommutativeMultiply").getPrecedence(), "\\text{**}"));
		operTab.put(F.PreDecrement, new PreOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("PreDecrement").getPrecedence(), "\\text{--}"));
		operTab.put(F.ReplaceRepeated, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceRepeated").getPrecedence(), "\\text{//.}"));
		operTab.put(F.MapAll, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("MapAll").getPrecedence(),
				"\\text{//@}"));
		operTab.put(F.AddTo, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("AddTo").getPrecedence(),
				"\\text{+=}"));
		operTab.put(F.Greater,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), " > "));
		operTab.put(F.GreaterEqual, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("GreaterEqual").getPrecedence(), "\\geq "));
		operTab.put(F.SubtractFrom, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("SubtractFrom").getPrecedence(), "\\text{-=}"));
		operTab.put(F.Subtract,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Subtract").getPrecedence(), " - "));
		operTab.put(F.CompoundExpression, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ";"));
		operTab.put(F.DivideBy, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("DivideBy").getPrecedence(), "\\text{/=}"));
		operTab.put(F.StringJoin, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("StringJoin").getPrecedence(), "\\text{<>}"));
		operTab.put(F.UnsameQ, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("UnsameQ").getPrecedence(), "\\text{=!=}"));
		operTab.put(F.Decrement, new PostOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Decrement").getPrecedence(), "\\text{--}"));
		operTab.put(F.LessEqual, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(), "\\leq "));
		operTab.put(F.Colon,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Colon").getPrecedence(), "\\text{:}"));
		operTab.put(F.Increment, new PostOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Increment").getPrecedence(), "\\text{++}"));
		operTab.put(F.Alternatives, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("Alternatives").getPrecedence(), "\\text{|}"));
		operTab.put(F.Equal,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Equal").getPrecedence(), " == "));
		operTab.put(F.DirectedEdge, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("DirectedEdge").getPrecedence(), "\\to "));
		operTab.put(F.Divide, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Divide").getPrecedence(),
				"\\text{/}"));
		operTab.put(F.Apply, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Apply").getPrecedence(),
				"\\text{@@}"));
		operTab.put(F.Set,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), " = "));
		// operTab.put(F.Minus,
		// new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Minus").getPrecedence(), "\\text{-}"));
		operTab.put(F.Map,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Map").getPrecedence(), "\\text{/@}"));
		operTab.put(F.SameQ, new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("SameQ").getPrecedence(),
				"\\text{===}"));
		operTab.put(F.Less,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Less").getPrecedence(), " < "));
		operTab.put(F.PreIncrement, new PreOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("PreIncrement").getPrecedence(), "\\text{++}"));
		operTab.put(F.Unequal,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Unequal").getPrecedence(), "\\neq "));
		operTab.put(F.Or,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Or").getPrecedence(), " \\lor "));
		// operTab.put(F.PrePlus,
		// new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("PrePlus").getPrecedence(), "\\text{+}"));
		operTab.put(F.TimesBy, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("TimesBy").getPrecedence(), "\\text{*=}"));
		operTab.put(F.And,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("And").getPrecedence(), " \\land "));
		operTab.put(F.Not,
				new PreOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Not").getPrecedence(), "\\neg "));
		operTab.put(F.Factorial,
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Factorial").getPrecedence(), " ! "));
		operTab.put(F.Factorial2,
				new PostOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Factorial2").getPrecedence(), " !! "));

		operTab.put(F.ReplaceAll, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceAll").getPrecedence(), "\\text{/.}\\,"));
		operTab.put(F.ReplaceRepeated, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceRepeated").getPrecedence(), "\\text{//.}\\,"));
		operTab.put(F.Rule,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Rule").getPrecedence(), "\\to "));
		operTab.put(F.RuleDelayed, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(), ":\\to "));
		operTab.put(F.Set,
				new AbstractOperator(this, ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), " = "));
		operTab.put(F.SetDelayed, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("SetDelayed").getPrecedence(), "\\text{:=}\\,"));
		operTab.put(F.UndirectedEdge, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("UndirectedEdge").getPrecedence(), "\\leftrightarrow "));
		operTab.put(F.TwoWayRule, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("TwoWayRule").getPrecedence(), "\\leftrightarrow "));
		operTab.put(F.CenterDot, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("CenterDot").getPrecedence(), "\\cdot "));
		operTab.put(F.CircleDot, new AbstractOperator(this,
				ASTNodeFactory.MMA_STYLE_FACTORY.get("CircleDot").getPrecedence(), "\\odot "));

		operTab.put(F.Sin, new TeXFunction(this, "sin "));
		operTab.put(F.Cos, new TeXFunction(this, "cos "));
		operTab.put(F.Tan, new TeXFunction(this, "tan "));
		operTab.put(F.Cot, new TeXFunction(this, "cot "));
		operTab.put(F.Sinh, new TeXFunction(this, "sinh "));
		operTab.put(F.Cosh, new TeXFunction(this, "cosh "));
		operTab.put(F.Tanh, new TeXFunction(this, "tanh "));
		operTab.put(F.Coth, new TeXFunction(this, "coth "));
		operTab.put(F.Csc, new TeXFunction(this, "csc "));
		operTab.put(F.Sec, new TeXFunction(this, "sec "));
		operTab.put(F.ArcSin, new TeXFunction(this, "arcsin "));
		operTab.put(F.ArcCos, new TeXFunction(this, "arccos "));
		operTab.put(F.ArcTan, new TeXFunction(this, "arctan "));
		operTab.put(F.ArcCot, new TeXFunction(this, "arccot "));
		operTab.put(F.ArcSinh, new TeXFunction(this, "arcsinh "));
		operTab.put(F.ArcCosh, new TeXFunction(this, "arccosh "));
		operTab.put(F.ArcTanh, new TeXFunction(this, "arctanh "));
		operTab.put(F.ArcCoth, new TeXFunction(this, "arccoth "));
		operTab.put(F.Log, new TeXFunction(this, "log "));

		CONSTANT_SYMBOLS.put("Alpha", "\\alpha");
		CONSTANT_SYMBOLS.put("Beta", "\\beta");
		CONSTANT_SYMBOLS.put("Chi", "\\chi");
		CONSTANT_SYMBOLS.put("Delta", "\\delta");
		CONSTANT_SYMBOLS.put("Epsilon", "\\epsilon");
		CONSTANT_SYMBOLS.put("Phi", "\\phi");
		CONSTANT_SYMBOLS.put("Gamma", "\\gamma");
		CONSTANT_SYMBOLS.put("Eta", "\\eta");
		CONSTANT_SYMBOLS.put("Iota", "\\iota");
		CONSTANT_SYMBOLS.put("Kappa", "\\kappa");
		CONSTANT_SYMBOLS.put("Lambda", "\\lambda");
		CONSTANT_SYMBOLS.put("Mu", "\\mu");
		CONSTANT_SYMBOLS.put("Nu", "\\nu");
		CONSTANT_SYMBOLS.put("Omicron", "\\omicron");
		CONSTANT_SYMBOLS.put("Theta", "\\theta");
		CONSTANT_SYMBOLS.put("Rho", "\\rho");
		CONSTANT_SYMBOLS.put("Sigma", "\\sigma");
		CONSTANT_SYMBOLS.put("Tau", "\\tau");
		CONSTANT_SYMBOLS.put("Upsilon", "\\upsilon");
		CONSTANT_SYMBOLS.put("Omega", "\\omega");
		CONSTANT_SYMBOLS.put("Xi", "\\xi");
		CONSTANT_SYMBOLS.put("Psi", "\\psi");
		CONSTANT_SYMBOLS.put("Zeta", "\\zeta");

		CONSTANT_SYMBOLS.put("alpha", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("beta", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("chi", AST2Expr.TRUE_STRING);
		CONSTANT_SYMBOLS.put("delta", AST2Expr.TRUE_STRING);
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
		CONSTANT_EXPRS.put(F.E, "e");
		CONSTANT_EXPRS.put(F.Glaisher, "A");
		CONSTANT_EXPRS.put(F.GoldenRatio, "\\phi");
		CONSTANT_EXPRS.put(F.EulerGamma, "\\gamma");
		CONSTANT_EXPRS.put(F.Khinchin, "K");
		CONSTANT_EXPRS.put(F.Pi, "\\pi");
		CONSTANT_EXPRS.put(F.CInfinity, "\\infty");
		CONSTANT_EXPRS.put(F.CNInfinity, "-\\infty");
	}

}
