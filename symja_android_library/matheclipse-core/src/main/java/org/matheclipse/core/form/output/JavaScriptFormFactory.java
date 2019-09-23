package org.matheclipse.core.form.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.Operator;

/**
 * Converts an internal <code>IExpr</code> into a user readable string.
 * 
 */
public class JavaScriptFormFactory extends DoubleFormFactory {
	/**
	 * If <code>true</code> the <code>Piecewise()</code> function was used in an expression, which need to do inline
	 * operators with the JavaScript ternary operator. If <code>false</code> the converter will use
	 * <code>if(...){...}</code> statements.
	 */
	public boolean INLINE_PIECEWISE = true;

	private List<String> sliderNames;

	private final static Map<ISymbol, String> FUNCTIONS_STR = new HashMap<ISymbol, String>();

	static {

		if (Config.USE_MATHCELL) {
			FUNCTIONS_STR.put(F.AiryAi, "airyAi");
			FUNCTIONS_STR.put(F.AiryBi, "airyBi");
			FUNCTIONS_STR.put(F.DirichletEta, "dirichletEta");
			FUNCTIONS_STR.put(F.HankelH1, "hankel1");
			FUNCTIONS_STR.put(F.HankelH2, "hankel2");
			FUNCTIONS_STR.put(F.InverseWeierstrassP, "inverseWeierstrassP");
			FUNCTIONS_STR.put(F.SphericalBesselJ, "sphericalBesselJ");
			FUNCTIONS_STR.put(F.SphericalBesselY, "sphericalBesselY");
			FUNCTIONS_STR.put(F.SphericalHankelH1, "sphericalHankel1");
			FUNCTIONS_STR.put(F.SphericalHankelH2, "sphericalHankel2");

			FUNCTIONS_STR.put(F.WeierstrassHalfPeriods, "weierstrassHalfPeriods");
			FUNCTIONS_STR.put(F.WeierstrassInvariants, "weierstrassInvariants");
			FUNCTIONS_STR.put(F.WeierstrassP, "weierstrassP");
			FUNCTIONS_STR.put(F.WeierstrassPPrime, "weierstrassPPrime");

			FUNCTIONS_STR.put(F.Abs, "abs");
			FUNCTIONS_STR.put(F.Arg, "arg");

			FUNCTIONS_STR.put(F.BesselJ, "besselJ");
			FUNCTIONS_STR.put(F.BesselY, "besselY");
			FUNCTIONS_STR.put(F.BesselI, "besselI");
			FUNCTIONS_STR.put(F.BesselK, "besselK");

			// FUNCTIONS_STR.put(F.Hankel1, "hankel1");
			// FUNCTIONS_STR.put(F.Hankel2, "hankel2");

			// FUNCTIONS_STR.put(F.AiryAi, "airyAi");
			// FUNCTIONS_STR.put(F.AiryBi, "airyBi");

			FUNCTIONS_STR.put(F.EllipticF, "ellipticF");
			FUNCTIONS_STR.put(F.EllipticK, "ellipticK");
			FUNCTIONS_STR.put(F.EllipticE, "ellipticE");
			FUNCTIONS_STR.put(F.EllipticPi, "ellipticPi");

			FUNCTIONS_STR.put(F.JacobiZeta, "jacobiZeta");
			FUNCTIONS_STR.put(F.Factorial, "factorial");
			FUNCTIONS_STR.put(F.Factorial2, "factorial2");
			FUNCTIONS_STR.put(F.Binomial, "binomial");
			FUNCTIONS_STR.put(F.LogGamma, "logGamma");
			FUNCTIONS_STR.put(F.Gamma, "gamma");
			FUNCTIONS_STR.put(F.Beta, "beta");
			FUNCTIONS_STR.put(F.Erf, "erf");
			FUNCTIONS_STR.put(F.Erfc, "erfc");

			FUNCTIONS_STR.put(F.Hypergeometric0F1, "hypergeometric0F1");
			FUNCTIONS_STR.put(F.Hypergeometric1F1, "hypergeometric1F1");
			// FUNCTIONS_STR.put(F.Hypergeometric2??, "hypergeometric2F0");
			FUNCTIONS_STR.put(F.Hypergeometric2F1, "hypergeometric2F1");

			FUNCTIONS_STR.put(F.Exp, "exp");

			FUNCTIONS_STR.put(F.ProductLog, "lambertW");
			FUNCTIONS_STR.put(F.Chop, "chop");
			FUNCTIONS_STR.put(F.KroneckerDelta, "kronecker");

			FUNCTIONS_STR.put(F.HermiteH, "hermite");
			FUNCTIONS_STR.put(F.LaguerreL, "laguerre");
			FUNCTIONS_STR.put(F.ChebyshevT, "chebyshevT");
			FUNCTIONS_STR.put(F.ChebyshevU, "chebyshevU");
			FUNCTIONS_STR.put(F.LegendreP, "legendreP");
			// FUNCTIONS_STR.put(F.SpheriacelHarmonic, "sphericalHarmonic");

			FUNCTIONS_STR.put(F.Sin, "sin");
			FUNCTIONS_STR.put(F.Cos, "cos");
			FUNCTIONS_STR.put(F.Tan, "tan");
			FUNCTIONS_STR.put(F.Cot, "cot");
			FUNCTIONS_STR.put(F.Sec, "sec");
			FUNCTIONS_STR.put(F.Csc, "csc");

			FUNCTIONS_STR.put(F.ArcSin, "arcsin");
			FUNCTIONS_STR.put(F.ArcCos, "arccos");
			FUNCTIONS_STR.put(F.ArcTan, "arctan");
			FUNCTIONS_STR.put(F.ArcCot, "arccot");
			FUNCTIONS_STR.put(F.ArcSec, "arcsec");
			FUNCTIONS_STR.put(F.ArcCsc, "arccsc");

			FUNCTIONS_STR.put(F.Sinh, "sinh");
			FUNCTIONS_STR.put(F.Cosh, "cosh");
			FUNCTIONS_STR.put(F.Tanh, "tanh");
			FUNCTIONS_STR.put(F.Coth, "coth");
			FUNCTIONS_STR.put(F.Sech, "sech");
			FUNCTIONS_STR.put(F.Csch, "csch");

			FUNCTIONS_STR.put(F.ArcSinh, "arcsinh");
			FUNCTIONS_STR.put(F.ArcCosh, "arccosh");
			FUNCTIONS_STR.put(F.ArcTanh, "arctanh");
			FUNCTIONS_STR.put(F.ArcCoth, "arccoth");
			FUNCTIONS_STR.put(F.ArcSech, "arcsech");
			FUNCTIONS_STR.put(F.ArcCsch, "arccsch");

			FUNCTIONS_STR.put(F.Sinc, "sinc");
			FUNCTIONS_STR.put(F.Zeta, "zeta");
			// FUNCTIONS_STR.put(F.DirichletEta, "dirichletEta");
			FUNCTIONS_STR.put(F.BernoulliB, "bernoulli");

		} else {
			FUNCTIONS_STR.put(F.Abs, "Math.abs");

			FUNCTIONS_STR.put(F.ArcCos, "Math.acos");
			FUNCTIONS_STR.put(F.ArcCosh, "Math.acosh");
			FUNCTIONS_STR.put(F.ArcSin, "Math.asin");
			FUNCTIONS_STR.put(F.ArcSinh, "Math.asinh");
			FUNCTIONS_STR.put(F.ArcTan, "Math.atan");
			FUNCTIONS_STR.put(F.ArcTanh, "Math.atanh");

			FUNCTIONS_STR.put(F.Ceiling, "Math.ceil");
			FUNCTIONS_STR.put(F.Cos, "Math.cos");
			FUNCTIONS_STR.put(F.Cosh, "Math.cosh");
			FUNCTIONS_STR.put(F.Exp, "Math.exp");
			FUNCTIONS_STR.put(F.Floor, "Math.floor");

			FUNCTIONS_STR.put(F.Log, "Math.log");
			FUNCTIONS_STR.put(F.Max, "Math.max");
			FUNCTIONS_STR.put(F.Min, "Math.min");
			// Power is handled by coding
			// FUNCTIONS_STR.put(F.Power, "Math.pow");

			FUNCTIONS_STR.put(F.Sign, "Math.sign");
			FUNCTIONS_STR.put(F.Sin, "Math.sin");
			FUNCTIONS_STR.put(F.Sinh, "Math.sinh");
			FUNCTIONS_STR.put(F.Tan, "Math.tan");
			FUNCTIONS_STR.put(F.Tanh, "Math.tanh");

		}
	}

	public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
			int significantFigures) {
		super(relaxedSyntax, reversed, exponentFigures, significantFigures);
	}

	public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
			int significantFigures, List<String> sliderNames) {
		super(relaxedSyntax, reversed, exponentFigures, significantFigures);
		this.sliderNames = sliderNames;
	}

	/**
	 * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user readable string.
	 * 
	 * @param relaxedSyntax
	 *            If <code>true</code> use paranthesis instead of square brackets and ignore case for functions, i.e.
	 *            sin() instead of Sin[]. If <code>true</code> use single square brackets instead of double square
	 *            brackets for extracting parts of an expression, i.e. {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
	 * @return
	 */
	public static JavaScriptFormFactory get(final boolean relaxedSyntax) {
		return get(relaxedSyntax, false);
	}

	/**
	 * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user readable string.
	 * 
	 * @param relaxedSyntax
	 *            if <code>true</code> use paranthesis instead of square brackets and ignore case for functions, i.e.
	 *            sin() instead of Sin[]. If <code>true</code> use single square brackets instead of double square
	 *            brackets for extracting parts of an expression, i.e. {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
	 * @param plusReversed
	 *            if <code>true</code> the arguments of the <code>Plus()</code> function will be printed in reversed
	 *            order
	 * @return
	 */
	public static JavaScriptFormFactory get(final boolean relaxedSyntax, final boolean plusReversed) {
		return get(relaxedSyntax, plusReversed, -1, -1);
	}

	/**
	 * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user readable string.
	 * 
	 * @param relaxedSyntax
	 *            if <code>true</code> use paranthesis instead of square brackets and ignore case for functions, i.e.
	 *            sin() instead of Sin[]. If <code>true</code> use single square brackets instead of double square
	 *            brackets for extracting parts of an expression, i.e. {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
	 * @param plusReversed
	 *            if <code>true</code> the arguments of the <code>Plus()</code> function will be printed in reversed
	 *            order
	 * @param exponentFigures
	 * @param significantFigures
	 * @return
	 */
	public static JavaScriptFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
			int exponentFigures, int significantFigures) {
		return new JavaScriptFormFactory(relaxedSyntax, plusReversed, exponentFigures, significantFigures);
	}

	public String functionHead(ISymbol symbol) {
		return FUNCTIONS_STR.get(symbol);
	}

	public void convertSymbol(final Appendable buf, final ISymbol symbol) throws IOException {

		if (symbol.isBuiltInSymbol()) {
			String str = functionHead((ISymbol) symbol);
			if (str != null) {
				buf.append(str);
				return;
			}
		}
		if (sliderNames != null && sliderNames.contains(symbol.toString())) {
			buf.append(symbol.toString() + ".Value()");
			return;
		}
		super.convertSymbol(buf, symbol);
	}

	/**
	 * Get an <code>JavaScriptFormFactory</code> for converting an internal expression to a user readable string, with
	 * <code>relaxedSyntax</code> set to false.
	 * 
	 * @return
	 * @see #get(boolean)
	 */
	public static JavaScriptFormFactory get() {
		return get(false);
	}

	public void convertAST(final Appendable buf, final IAST function) throws IOException {
		if (function.isNumericFunction()) {
			try {
				double value = EvalEngine.get().evalDouble(function);
				buf.append("(" + value + ")");
				return;
			} catch (RuntimeException rex) {
				//
			}
		}
		IExpr head = function.head();
		if (head.isSymbol()) {
			String str = functionHead((ISymbol) head);
			if (str != null) {
				if (function.isAST(F.ArcTan, 3)) {
					buf.append("Math.atan2");
				} else {
					buf.append(str);
				}
				convertArgs(buf, head, function);
				return;
			}
			if (Config.USE_MATHCELL && function.headID() < 0) {
				// avoid generating JavaScript eval(head) here
				buf.append("(window[");
				convert(buf, head);
				buf.append("](");
				convertArgs(buf, head, function);
				buf.append("))");
				return;
			}
		}
		if (function.isList()) {
			// interpret List() as javascript array
			buf.append("[");
			for (int i = 1; i < function.size(); i++) {
				convert(buf, function.get(i));
				if (i < function.size() - 1) {
					buf.append(",");
				}
			}
			buf.append("]");
			return;
		}
		if (Config.USE_MATHCELL) {
			if (function.isPlus() || function.isTimes()) {
				if (function.size() >= 3) {
					for (int i = 1; i < function.size() - 1; i++) {
						if (function.isPlus()) {
							buf.append("add(");
						} else {
							buf.append("mul(");
						}
					}
					convert(buf, function.arg1());
					buf.append(",");
					for (int i = 2; i < function.size(); i++) {
						convert(buf, function.get(i));
						buf.append(")");
						if (i < function.size() - 1) {
							buf.append(",");
						}
					}
					return;
				}
			} else if (function.isPower()) {
				IExpr base = function.base();
				IExpr exponent = function.exponent();
				if (exponent.isMinusOne()) {
					buf.append("(1.0/");
					convert(buf, base);
					buf.append(")");
					return;
				}
				if (exponent.isNumEqualRational(F.C1D2)) {
					buf.append("sqrt(");
					convert(buf, base);
					buf.append(")");
					return;
				}
				buf.append("pow");
				convertArgs(buf, head, function);
				return;
			} else if (function.head() == F.Log) {
				if (function.isAST1()) {
					IExpr arg1 = function.first();
					buf.append("log(");
					convert(buf, arg1);
					buf.append(", Math.E)");
					return;
				} else if (function.isAST2()) {
					IExpr arg1 = function.first();
					IExpr arg2 = function.second();
					buf.append("log(");
					convert(buf, arg1);
					buf.append(", ");
					convert(buf, arg2);
					buf.append(")");
					return;
				}
			} else if (function.head() == F.Piecewise && function.size() > 1) {
				int[] dim = function.arg1().isMatrix();
				if (dim != null && dim[1] == 2) {
					IAST list = (IAST) function.arg1();
					if (INLINE_PIECEWISE) {
						// use the ternary operator
						int size = list.size();
						for (int i = 1; i < size; i++) {
							IAST row = (IAST) list.get(i);
							if (i > 1) {
								buf.append("(");
							}
							buf.append("(");
							convert(buf, row.second());
							buf.append(") ? ");
							convert(buf, row.first());
							buf.append(" : ");
						}
						buf.append("( ");
						if (function.isAST2()) {
							convert(buf, function.second());
						} else {
							buf.append(" NaN ");
						}
						buf.append(" )");
						for (int i = 2; i < size; i++) {
							buf.append(" )");
						}
						return;
					} else {
						// use if... statements
						for (int i = 1; i < list.size(); i++) {
							IAST row = (IAST) list.get(i);
							if (i == 1) {
								buf.append("if (");
								convert(buf, row.second());
								buf.append(") {");
							} else {
								buf.append(" else if (");
								convert(buf, row.second());
								buf.append(") {");
							}
							buf.append(" return ");
							convert(buf, row.first());
							buf.append("}");
						}
						buf.append(" else {");
						if (function.isAST2()) {
							convert(buf, function.second());
						} else {
							buf.append(" return NaN; ");
						}
						buf.append("}");
						return;
					}
				}
			}
			if (function.headID() > 0) {
				throw new MathException("illegal JavaScript arg");
			}
		} else {
			if (function.isPower()) {
				IExpr base = function.base();
				IExpr exponent = function.exponent();
				if (exponent.isMinusOne()) {
					buf.append("(1.0/");
					convert(buf, base);
					buf.append(")");
					return;
				}
				if (exponent.isNumEqualRational(F.C1D2)) {
					buf.append("Math.sqrt(");
					convert(buf, base);
					buf.append(")");
					return;
				}
				if (exponent.isNumEqualRational(F.C1D3)) {
					buf.append("Math.cbrt(");
					convert(buf, base);
					buf.append(")");
					return;
				}
				buf.append("Math.pow");
				convertArgs(buf, head, function);
				return;
			}
		}
		if (function.isInfinity()) {
			buf.append("Number.POSITIVE_INFINITY");
			return;
		}
		if (function.isNegativeInfinity()) {
			buf.append("Number.NEGATIVE_INFINITY");
			return;
		}
		convert(buf, head);
		convertArgs(buf, head, function);
	}

	protected boolean convertOperator(final Operator operator, final IAST list, final Appendable buf,
			final int precedence, ISymbol head) throws IOException {
		if (!super.convertOperator(operator, list, buf, precedence, head)) {
			if (Config.USE_MATHCELL) {
				convertAST(buf, list);
				return true;
			}
			return false;
		}

		return true;
	}

	public Operator getOperator(ISymbol head) {
		if (Config.USE_MATHCELL) {
			Operator operator = null;
			if (head.isSymbolID(ID.Equal, ID.Unequal, ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual, ID.And, ID.Or,
					ID.Not)) {
				String str = head.toString();
				operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(str);
			}
			return operator;
		} else if (Config.USE_JSXGRAPH) {
			Operator operator = null;
			if (head.isSymbolID(ID.Plus, ID.Times, ID.Equal, ID.Unequal, ID.Less, ID.LessEqual, ID.Greater,
					ID.GreaterEqual, ID.And, ID.Or, ID.Not)) {
				String str = head.toString();
				operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(str);
			}
			return operator;
		}
		return super.getOperator(head);
	}

	public void convertComplex(final Appendable buf, final IComplex c, final int precedence, boolean caller)
			throws IOException {
		buf.append("complex(");
		convertFraction(buf, c.getRealPart(), 0, NO_PLUS_CALL);
		buf.append(",");
		convertFraction(buf, c.getImaginaryPart(), 0, NO_PLUS_CALL);
		buf.append(")");
	}

	public void convertDoubleComplex(final Appendable buf, final IComplexNum dc, final int precedence, boolean caller)
			throws IOException {
		buf.append("complex(");
		convertDoubleString(buf, convertDoubleToFormattedString(dc.getRealPart()), 0, false);
		buf.append(",");
		convertDoubleString(buf, convertDoubleToFormattedString(dc.getImaginaryPart()), 0, false);
		buf.append(")");
	}
}
