package org.matheclipse.core.form.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.AST2Expr;
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

	public final static int USE_PURE_JS = 1;
	public final static int USE_MATHCELL = 2;
	public final static int USE_JSXGRAPH = 3;
	/**
	 * If <code>true</code> the <code>Piecewise()</code> function was used in an expression, which need to do inline
	 * operators with the JavaScript ternary operator. If <code>false</code> the converter will use
	 * <code>if(...){...}</code> statements.
	 */
	public boolean INLINE_PIECEWISE = true;

	private final int javascriptFlavor;

	private List<String> sliderNames;

	private final static Map<ISymbol, String> FUNCTIONS_STR_MATHCELL = new HashMap<ISymbol, String>();
	private final static Map<ISymbol, String> FUNCTIONS_STR_PURE_JS = new HashMap<ISymbol, String>();
	static {

		FUNCTIONS_STR_MATHCELL.put(F.AiryAi, "airyAi");
		FUNCTIONS_STR_MATHCELL.put(F.AiryBi, "airyBi");
		FUNCTIONS_STR_MATHCELL.put(F.DirichletEta, "dirichletEta");
		FUNCTIONS_STR_MATHCELL.put(F.HankelH1, "hankel1");
		FUNCTIONS_STR_MATHCELL.put(F.HankelH2, "hankel2");
		FUNCTIONS_STR_MATHCELL.put(F.InverseWeierstrassP, "inverseWeierstrassP");
		FUNCTIONS_STR_MATHCELL.put(F.SphericalBesselJ, "sphericalBesselJ");
		FUNCTIONS_STR_MATHCELL.put(F.SphericalBesselY, "sphericalBesselY");
		FUNCTIONS_STR_MATHCELL.put(F.SphericalHankelH1, "sphericalHankel1");
		FUNCTIONS_STR_MATHCELL.put(F.SphericalHankelH2, "sphericalHankel2");

		FUNCTIONS_STR_MATHCELL.put(F.WeierstrassHalfPeriods, "weierstrassHalfPeriods");
		FUNCTIONS_STR_MATHCELL.put(F.WeierstrassInvariants, "weierstrassInvariants");
		FUNCTIONS_STR_MATHCELL.put(F.WeierstrassP, "weierstrassP");
		FUNCTIONS_STR_MATHCELL.put(F.WeierstrassPPrime, "weierstrassPPrime");

		FUNCTIONS_STR_MATHCELL.put(F.Abs, "abs");
		FUNCTIONS_STR_MATHCELL.put(F.Arg, "arg");
		FUNCTIONS_STR_MATHCELL.put(F.Chop, "chop");

		FUNCTIONS_STR_MATHCELL.put(F.BesselJ, "besselJ");
		FUNCTIONS_STR_MATHCELL.put(F.BesselY, "besselY");
		FUNCTIONS_STR_MATHCELL.put(F.BesselI, "besselI");
		FUNCTIONS_STR_MATHCELL.put(F.BesselK, "besselK");
		FUNCTIONS_STR_MATHCELL.put(F.StruveH, "struveH");
		FUNCTIONS_STR_MATHCELL.put(F.StruveL, "struveL");
		
		// TODO see math.js - https://github.com/paulmasson/math
		// FUNCTIONS_STR_MATHCELL.put(F.BesselJZero, "besselJZero ");
		// FUNCTIONS_STR_MATHCELL.put(F.BesselYZero, "besselYZero ");

		// FUNCTIONS_STR_MATHCELL.put(F.Hankel1, "hankel1");
		// FUNCTIONS_STR_MATHCELL.put(F.Hankel2, "hankel2");

		// FUNCTIONS_STR_MATHCELL.put(F.AiryAi, "airyAi");
		// FUNCTIONS_STR_MATHCELL.put(F.AiryBi, "airyBi");

		FUNCTIONS_STR_MATHCELL.put(F.EllipticF, "ellipticF");
		FUNCTIONS_STR_MATHCELL.put(F.EllipticK, "ellipticK");
		FUNCTIONS_STR_MATHCELL.put(F.EllipticE, "ellipticE");
		FUNCTIONS_STR_MATHCELL.put(F.EllipticPi, "ellipticPi");
		FUNCTIONS_STR_MATHCELL.put(F.EllipticTheta, "jacobiTheta");

		FUNCTIONS_STR_MATHCELL.put(F.JacobiCN, "cn");
		FUNCTIONS_STR_MATHCELL.put(F.JacobiDN, "dn");
		FUNCTIONS_STR_MATHCELL.put(F.JacobiSN, "sn");
		FUNCTIONS_STR_MATHCELL.put(F.JacobiZeta, "jacobiZeta");
		FUNCTIONS_STR_MATHCELL.put(F.Factorial, "factorial");
		FUNCTIONS_STR_MATHCELL.put(F.Factorial2, "factorial2");
		FUNCTIONS_STR_MATHCELL.put(F.Binomial, "binomial");
		FUNCTIONS_STR_MATHCELL.put(F.LogGamma, "logGamma");
		FUNCTIONS_STR_MATHCELL.put(F.Gamma, "gamma");
		FUNCTIONS_STR_MATHCELL.put(F.Beta, "beta");
		FUNCTIONS_STR_MATHCELL.put(F.Erf, "erf");
		FUNCTIONS_STR_MATHCELL.put(F.Erfc, "erfc");

		FUNCTIONS_STR_MATHCELL.put(F.Hypergeometric0F1, "hypergeometric0F1");
		FUNCTIONS_STR_MATHCELL.put(F.Hypergeometric1F1, "hypergeometric1F1");
		// FUNCTIONS_STR_MATHCELL.put(F.Hypergeometric2??, "hypergeometric2F0");
		FUNCTIONS_STR_MATHCELL.put(F.Hypergeometric2F1, "hypergeometric2F1");
		FUNCTIONS_STR_MATHCELL.put(F.HypergeometricPFQ, "hypergeometricPFQ");
		FUNCTIONS_STR_MATHCELL.put(F.Exp, "exp");
		FUNCTIONS_STR_MATHCELL.put(F.Im, "im");
		FUNCTIONS_STR_MATHCELL.put(F.Log, "log");
		FUNCTIONS_STR_MATHCELL.put(F.Re, "re");

		FUNCTIONS_STR_MATHCELL.put(F.ProductLog, "lambertW");
		FUNCTIONS_STR_MATHCELL.put(F.Chop, "chop");
		FUNCTIONS_STR_MATHCELL.put(F.KroneckerDelta, "kronecker");

		FUNCTIONS_STR_MATHCELL.put(F.HermiteH, "hermite");
		FUNCTIONS_STR_MATHCELL.put(F.LaguerreL, "laguerre");
		FUNCTIONS_STR_MATHCELL.put(F.ChebyshevT, "chebyshevT");
		FUNCTIONS_STR_MATHCELL.put(F.ChebyshevU, "chebyshevU");
		FUNCTIONS_STR_MATHCELL.put(F.LegendreP, "legendreP");
		// FUNCTIONS_STR_MATHCELL.put(F.SpheriacelHarmonic, "sphericalHarmonic");

		FUNCTIONS_STR_MATHCELL.put(F.Sin, "sin");
		FUNCTIONS_STR_MATHCELL.put(F.Cos, "cos");
		FUNCTIONS_STR_MATHCELL.put(F.Tan, "tan");
		FUNCTIONS_STR_MATHCELL.put(F.Cot, "cot");
		FUNCTIONS_STR_MATHCELL.put(F.Sec, "sec");
		FUNCTIONS_STR_MATHCELL.put(F.Csc, "csc");

		FUNCTIONS_STR_MATHCELL.put(F.ArcSin, "arcsin");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCos, "arccos");
		FUNCTIONS_STR_MATHCELL.put(F.ArcTan, "arctan");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCot, "arccot");
		FUNCTIONS_STR_MATHCELL.put(F.ArcSec, "arcsec");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCsc, "arccsc");

		FUNCTIONS_STR_MATHCELL.put(F.Sinh, "sinh");
		FUNCTIONS_STR_MATHCELL.put(F.Cosh, "cosh");
		FUNCTIONS_STR_MATHCELL.put(F.Tanh, "tanh");
		FUNCTIONS_STR_MATHCELL.put(F.Coth, "coth");
		FUNCTIONS_STR_MATHCELL.put(F.Sech, "sech");
		FUNCTIONS_STR_MATHCELL.put(F.Csch, "csch");

		FUNCTIONS_STR_MATHCELL.put(F.ArcSinh, "arcsinh");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCosh, "arccosh");
		FUNCTIONS_STR_MATHCELL.put(F.ArcTanh, "arctanh");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCoth, "arccoth");
		FUNCTIONS_STR_MATHCELL.put(F.ArcSech, "arcsech");
		FUNCTIONS_STR_MATHCELL.put(F.ArcCsch, "arccsch");

		FUNCTIONS_STR_MATHCELL.put(F.Sinc, "sinc");
		FUNCTIONS_STR_MATHCELL.put(F.Zeta, "zeta");
		// FUNCTIONS_STR_MATHCELL.put(F.DirichletEta, "dirichletEta");
		FUNCTIONS_STR_MATHCELL.put(F.BernoulliB, "bernoulli");

		FUNCTIONS_STR_MATHCELL.put(F.Ceiling, "Math.ceil");
		FUNCTIONS_STR_MATHCELL.put(F.Floor, "Math.floor");
		FUNCTIONS_STR_MATHCELL.put(F.IntegerPart, "Math.trunc");
		FUNCTIONS_STR_MATHCELL.put(F.Max, "Math.max");
		FUNCTIONS_STR_MATHCELL.put(F.Min, "Math.min");
		FUNCTIONS_STR_MATHCELL.put(F.Round, "Math.round");
		FUNCTIONS_STR_MATHCELL.put(F.Sign, "Math.sign");

		//
		// pure JavaScript mappings
		//
		FUNCTIONS_STR_PURE_JS.put(F.Abs, "Math.abs");

		FUNCTIONS_STR_PURE_JS.put(F.ArcCos, "Math.acos");
		FUNCTIONS_STR_PURE_JS.put(F.ArcCosh, "Math.acosh");
		FUNCTIONS_STR_PURE_JS.put(F.ArcSin, "Math.asin");
		FUNCTIONS_STR_PURE_JS.put(F.ArcSinh, "Math.asinh");
		FUNCTIONS_STR_PURE_JS.put(F.ArcTan, "Math.atan");
		FUNCTIONS_STR_PURE_JS.put(F.ArcTanh, "Math.atanh");

		FUNCTIONS_STR_PURE_JS.put(F.Ceiling, "Math.ceil");
		FUNCTIONS_STR_PURE_JS.put(F.Cos, "Math.cos");
		FUNCTIONS_STR_PURE_JS.put(F.Cosh, "Math.cosh");
		FUNCTIONS_STR_PURE_JS.put(F.Exp, "Math.exp");
		FUNCTIONS_STR_PURE_JS.put(F.Floor, "Math.floor");
		FUNCTIONS_STR_PURE_JS.put(F.IntegerPart, "Math.trunc");

		FUNCTIONS_STR_PURE_JS.put(F.Log, "Math.log");
		FUNCTIONS_STR_PURE_JS.put(F.Max, "Math.max");
		FUNCTIONS_STR_PURE_JS.put(F.Min, "Math.min");
		// Power is handled by coding
		// FUNCTIONS_STR_PURE_JS.put(F.Power, "Math.pow");
		FUNCTIONS_STR_PURE_JS.put(F.Round, "Math.round");
		FUNCTIONS_STR_PURE_JS.put(F.Sign, "Math.sign");
		FUNCTIONS_STR_PURE_JS.put(F.Sin, "Math.sin");
		FUNCTIONS_STR_PURE_JS.put(F.Sinh, "Math.sinh");
		FUNCTIONS_STR_PURE_JS.put(F.Tan, "Math.tan");
		FUNCTIONS_STR_PURE_JS.put(F.Tanh, "Math.tanh");

	}

	public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
			int significantFigures) {
		this(relaxedSyntax, reversed, exponentFigures, significantFigures, USE_PURE_JS);
	}

	/**
	 * JavaScript converter constructor.
	 * 
	 * @param relaxedSyntax
	 * @param reversed
	 * @param exponentFigures
	 * @param significantFigures
	 * @param javascriptFlavor
	 */
	public JavaScriptFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
			int significantFigures, int javascriptFlavor) {
		super(relaxedSyntax, reversed, exponentFigures, significantFigures);
		this.sliderNames = new ArrayList<String>();
		this.javascriptFlavor = javascriptFlavor;
	}

	public void appendSlider(String sliderName) {
		sliderNames.add(sliderName);
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
		if (javascriptFlavor == USE_MATHCELL) {
			return FUNCTIONS_STR_MATHCELL.get(symbol);
		}
		return FUNCTIONS_STR_PURE_JS.get(symbol);
	}

	public void convertSymbol(final StringBuilder buf, final ISymbol symbol) {

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
		if (symbol == F.Indeterminate) {
			buf.append("Number.NaN");
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

	public void convertAST(final StringBuilder buf, final IAST function) {
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
				if (function.isASTSizeGE(F.Round, 3)) {
					throw new MathException("Cannot convert to JavaScript: " + function.toString());
				}
				if (function.isAST(F.ArcTan, 3)) {
					buf.append("Math.atan2");
				} else {
					buf.append(str);
				}
				convertArgs(buf, head, function);
				return;
			}
			if (javascriptFlavor == USE_MATHCELL && function.headID() < 0) {
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
		if (javascriptFlavor == USE_MATHCELL) {
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
			} else if (function.isInfinity()) {
				buf.append("Number.POSITIVE_INFINITY");
				return;
			} else if (function.isNegativeInfinity()) {
				buf.append("Number.NEGATIVE_INFINITY");
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
				if (convertPiecewise(function, buf)) {
					return;
				}
			} else if (function.head() == F.ConditionalExpression && function.size() == 3) {
				convertConditionalExpression(function, buf);
				return;
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
			} else if (function.isInfinity()) {
				buf.append("Number.POSITIVE_INFINITY");
				return;
			} else if (function.isNegativeInfinity()) {
				buf.append("Number.NEGATIVE_INFINITY");
				return;
			} else if (function.head() == F.Piecewise && function.size() > 1) {
				if (convertPiecewise(function, buf)) {
					return;
				}
			} else if (function.head() == F.ConditionalExpression && function.size() == 3) {
				convertConditionalExpression(function, buf);
				return;
			} else if (function.head() == F.Cot && function.size() == 2) {
				buf.append("(1/Math.tan(");
				convert(buf, function.arg1());
				buf.append("))");
				return;
			} else if (function.head() == F.ArcCot && function.size() == 2) {
				buf.append("((Math.PI/2.0)-Math.atan(");
				convert(buf, function.arg1());
				buf.append("))");
				return;
			}
		}

		if (function.head() == F.If && function.size() >= 3 && function.size() <= 4) {
			// use the ternary operator
			buf.append("((");
			convert(buf, function.arg1());
			buf.append(") ? (");
			convert(buf, function.arg2());
			buf.append(") : ( ");
			if (function.size() == 4) {
				convert(buf, function.arg3());
			} else {
				buf.append("Number.NaN");
			}
			buf.append(" ))");
			return;
		}
		if (function.isAST(F.Missing)) {
			// Missing value
			buf.append("Number.NaN");
			return;
		}
		if (function.headID() > 0) {
			throw new MathException("Cannot convert to JavaScript. Function head: " + function.head());
		}

		convert(buf, head);
		convertArgs(buf, head, function);
	}

	private void convertConditionalExpression(final IAST function, final StringBuilder buf) {
		IExpr arg1 = function.arg1();
		IExpr arg2 = function.arg2();
		// use the ternary operator
		buf.append("((");
		convert(buf, arg2);
		buf.append(") ? (");
		convert(buf, arg1);
		buf.append(") : ( Number.NaN ))");
	}

	private boolean convertPiecewise(final IAST function, final StringBuilder buf) {
		int[] dim = function.arg1().isMatrix();
		if (dim != null && dim[1] == 2) {
			IAST list = (IAST) function.arg1();
			if (INLINE_PIECEWISE) {
				// use the ternary operator
				int size = list.size();
				buf.append("(");
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
					buf.append(" Number.NaN ");
				}
				buf.append(" )");
				for (int i = 2; i < size; i++) {
					buf.append(" )");
				}
				buf.append(")");
				return true;
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
					buf.append(" return Number.NaN; ");
				}
				buf.append("}");
				return true;
			}
		}
		return false;
	}

	protected boolean convertOperator(final Operator operator, final IAST list, final StringBuilder buf,
			final int precedence, ISymbol head) {
		if (!super.convertOperator(operator, list, buf, precedence, head)) {
			if (javascriptFlavor == USE_MATHCELL) {
				convertAST(buf, list);
				return true;
			}
			return false;
		}

		return true;
	}

	public Operator getOperator(ISymbol head) {
		if (javascriptFlavor == USE_MATHCELL) {
			if (head.isSymbolID(ID.Equal, ID.Unequal, ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual, ID.And, ID.Or,
					ID.Not)) {
				return OutputFormFactory.getOperator(head);
				// String str = head.toString();
				// if (fRelaxedSyntax) {
				// operator = ASTNodeFactory.RELAXED_STYLE_FACTORY.get(str.toLowerCase());
				// } else {
				// operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(str);
				// }
			}
			return null;
		} else if (javascriptFlavor == USE_JSXGRAPH) {
			if (head.isSymbolID(ID.Plus, ID.Times, ID.Equal, ID.Unequal, ID.Less, ID.LessEqual, ID.Greater,
					ID.GreaterEqual, ID.And, ID.Or, ID.Not)) {
				return OutputFormFactory.getOperator(head);
				// String str = head.toString();
				// if (fRelaxedSyntax) {
				// operator = ASTNodeFactory.RELAXED_STYLE_FACTORY.get(str.toLowerCase());
				// } else {
				// operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(str);
				// }
			}
			return null;
		}
		return super.getOperator(head);
	}

	public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence, boolean caller) {
		buf.append("complex(");
		convertFraction(buf, c.getRealPart(), 0, NO_PLUS_CALL);
		buf.append(",");
		convertFraction(buf, c.getImaginaryPart(), 0, NO_PLUS_CALL);
		buf.append(")");
	}

	public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc, final int precedence,
			boolean caller) {
		buf.append("complex(");
		convertDoubleString(buf, convertDoubleToFormattedString(dc.getRealPart()), 0, false);
		buf.append(",");
		convertDoubleString(buf, convertDoubleToFormattedString(dc.getImaginaryPart()), 0, false);
		buf.append(")");
	}
}
