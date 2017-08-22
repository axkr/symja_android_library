package org.matheclipse.core.form.mathml;

import java.util.HashMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Presentation generator generates MathML presentation output
 * 
 */
public class MathMLFormFactory extends AbstractMathMLFormFactory {

	class Operator {
		String fOperator;

		Operator(final String oper) {
			fOperator = oper;
		}

		public void convert(final StringBuffer buf) {
			tagStart(buf, "mo");
			buf.append(fOperator);
			tagEnd(buf, "mo");
		}

		public String toString() {
			return fOperator;
		}

	}

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

	/**
	 * Constructor
	 */
	public MathMLFormFactory() {
		this("");
	}

	public MathMLFormFactory(final String tagPrefix) {
		super(tagPrefix);
		init();
	}

	public void convertDouble(final StringBuffer buf, final INum d, final int precedence) {
		if (d.isNegative() && (precedence > plusPrec)) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "(");
		}
		tagStart(buf, "mn");
		buf.append(d.toString());
		tagEnd(buf, "mn");
		if (d.isNegative() && (precedence > plusPrec)) {
			tag(buf, "mo", ")");
			tagEnd(buf, "mrow");
		}
	}

	public void convertDoubleComplex(final StringBuffer buf, final IComplexNum dc, final int precedence) {
		tagStart(buf, "mrow");
		buf.append(String.valueOf(dc.getRealPart()));
		tag(buf, "mo", "+");
		tagStart(buf, "mrow");
		buf.append(String.valueOf(dc.getImaginaryPart()));

		// <!ENTITY InvisibleTimes "&#x2062;" >
		tag(buf, "mo", "&#x2062;");
		// <!ENTITY ImaginaryI "&#x2148;" >
		tag(buf, "mi", "&ImaginaryI;");// "&#x2148;");
		tagEnd(buf, "mrow");
		tagEnd(buf, "mrow");
	}

	public void convertInteger(final StringBuffer buf, final IInteger i, final int precedence) {
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

	public void convertFraction(final StringBuffer buf, final IFraction f, final int precedence) {
		boolean isInteger = f.getDenominator().isOne();
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

	public void convertFraction(final StringBuffer buf, final IRational f, final int precedence) {
		if (f.isNegative() && (precedence > plusPrec)) {
			tagStart(buf, "mrow");
			tag(buf, "mo", "(");
		}
		if (f.getDenominator().isOne() || f.getNumerator().isZero()) {
			tagStart(buf, "mn");
			buf.append(f.getNumerator().toString());
			tagEnd(buf, "mn");
		} else {
			tagStart(buf, "mfrac");
			tagStart(buf, "mn");
			buf.append(f.getNumerator().toString());
			tagEnd(buf, "mn");
			tagStart(buf, "mn");
			buf.append(f.getDenominator().toString());
			tagEnd(buf, "mn");
			tagEnd(buf, "mfrac");
		}
		if (f.isNegative() && (precedence > plusPrec)) {
			tag(buf, "mo", ")");
			tagEnd(buf, "mrow");
		}
	}

	public void convertComplex(final StringBuffer buf, final IComplex c, final int precedence) {
		boolean isReZero = c.getRealPart().isZero();
		final boolean isImOne = c.getImaginaryPart().isOne();
		final boolean isImNegative = c.getImaginaryPart().isLessThan(F.C0);
		final boolean isImMinusOne = c.getImaginaryPart().isMinusOne();
		tagStart(buf, "mrow");
		if (!isReZero) {
			convertFraction(buf, c.getRealPart(), precedence);
			if (isImNegative) {
				tag(buf, "mo", "-");
			} else {
				tag(buf, "mo", "+");
			}
		} else {
			if (isImMinusOne) {
				tag(buf, "mo", "-");
			}
		}
		if (!isImOne && !isImMinusOne) {
			tagStart(buf, "mrow");
			convertFraction(buf, c.getImaginaryPart(), ASTNodeFactory.TIMES_PRECEDENCE);
			// <!ENTITY InvisibleTimes "&#x2062;" >
			tag(buf, "mo", "&#x2062;");
			// <!ENTITY ImaginaryI "&#x2148;"
			tag(buf, "mi", "&#x2148;");
			tagEnd(buf, "mrow");
		} else {
			tag(buf, "mi", "&#x2148;");
		}
		tagEnd(buf, "mrow");
	}

	public void convertString(final StringBuffer buf, final String str) {
		tagStart(buf, "mtext");
		buf.append(str);
		tagEnd(buf, "mtext");
	}

	public void convertSymbol(final StringBuffer buf, final ISymbol sym) {
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

	public void convertHead(final StringBuffer buf, final IExpr obj) {
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
		convert(buf, obj, 0);
	}

	public void convert(final StringBuffer buf, final IExpr o, final int precedence) {
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
				IConverter converter = CONVERTERS.get((ISymbol) h);
				// IConverter converter = reflection(((ISymbol) h).getSymbolName());
				if (converter != null) {
					converter.setFactory(this);
					StringBuffer sb = new StringBuffer();
					if (converter.convert(sb, ast, precedence)) {
						buf.append(sb);
						return;
					}
				}
			}
			convertAST(buf, ast);
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
		if (o instanceof IInteger) {
			convertInteger(buf, (IInteger) o, precedence);
			return;
		}
		if (o instanceof IFraction) {
			convertFraction(buf, (IFraction) o, precedence);
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
		convertString(buf, o.toString());
	}

	private void convertAST(final StringBuffer buf, final IAST ast) {
		tagStart(buf, "mrow");
		convertHead(buf, ast.head());
		// &af; &#x2061;
		// tag(buf, "mo", "&#x2061;");
		tagStart(buf, "mrow");
		tag(buf, "mo", "(");
		tagStart(buf, "mrow");
		for (int i = 1; i < ast.size(); i++) {
			convert(buf, ast.get(i), 0);
			if (i < ast.size() - 1) {
				tag(buf, "mo", ",");
			}
		}
		tagEnd(buf, "mrow");
		tag(buf, "mo", ")");
		tagEnd(buf, "mrow");
		tagEnd(buf, "mrow");

	}

	public void init() {
		plusPrec = ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence();

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

		CONVERTERS.put(F.Abs, new org.matheclipse.core.form.mathml.reflection.Abs());
		CONVERTERS.put(F.And, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("And").getPrecedence(), "&#x2227;"));
		CONVERTERS.put(F.Binomial, new org.matheclipse.core.form.mathml.reflection.Binomial());
		CONVERTERS.put(F.Ceiling, new org.matheclipse.core.form.mathml.reflection.Ceiling());
		CONVERTERS.put(F.CompoundExpression,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ";"));
		CONVERTERS.put(F.D, new org.matheclipse.core.form.mathml.reflection.D());
		CONVERTERS.put(F.Dot, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Dot").getPrecedence(), "."));
		CONVERTERS.put(F.Equal, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Equal").getPrecedence(), "=="));
		CONVERTERS.put(F.Factorial, new org.matheclipse.core.form.mathml.reflection.Factorial());
		CONVERTERS.put(F.Factorial2, new org.matheclipse.core.form.mathml.reflection.Factorial2());
		CONVERTERS.put(F.Floor, new org.matheclipse.core.form.mathml.reflection.Floor());
		CONVERTERS.put(F.Greater,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), "&gt;"));
		CONVERTERS.put(F.GreaterEqual,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("GreaterEqual").getPrecedence(), "&#x2265;"));
		CONVERTERS.put(F.Integrate, new org.matheclipse.core.form.mathml.reflection.Integrate());
		CONVERTERS.put(F.Less, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Less").getPrecedence(), "&lt;"));
		CONVERTERS.put(F.LessEqual,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(), "&#x2264;"));
		CONVERTERS.put(F.List, new org.matheclipse.core.form.mathml.reflection.List());
		CONVERTERS.put(F.MatrixForm, new org.matheclipse.core.form.mathml.reflection.MatrixForm());
		CONVERTERS.put(F.Not, new org.matheclipse.core.form.mathml.reflection.Not());
		CONVERTERS.put(F.Or, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Or").getPrecedence(), "&#x2228;"));
		CONVERTERS.put(F.Part, new org.matheclipse.core.form.mathml.reflection.Part());
		CONVERTERS.put(F.Plus, new org.matheclipse.core.form.mathml.reflection.Plus());
		CONVERTERS.put(F.Power, new org.matheclipse.core.form.mathml.reflection.Power());
		CONVERTERS.put(F.Product, new org.matheclipse.core.form.mathml.reflection.Product());
		CONVERTERS.put(F.Rational, new org.matheclipse.core.form.mathml.reflection.Rational());
		CONVERTERS.put(F.Rule, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Rule").getPrecedence(), "-&gt;"));
		CONVERTERS.put(F.RuleDelayed,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(), "&#x29F4;"));
		CONVERTERS.put(F.Set, new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), "="));
		CONVERTERS.put(F.SetDelayed,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("SetDelayed").getPrecedence(), ":="));
		CONVERTERS.put(F.Sqrt, new org.matheclipse.core.form.mathml.reflection.Sqrt());
		CONVERTERS.put(F.Sum, new org.matheclipse.core.form.mathml.reflection.Sum());
		CONVERTERS.put(F.Times, org.matheclipse.core.form.mathml.reflection.Times.CONST);
		CONVERTERS.put(F.Unequal,
				new MMLOperator(ASTNodeFactory.MMA_STYLE_FACTORY.get("Unequal").getPrecedence(), "!="));
	}
}
