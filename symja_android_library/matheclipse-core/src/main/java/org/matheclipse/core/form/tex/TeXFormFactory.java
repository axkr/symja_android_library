package org.matheclipse.core.form.tex;

import java.text.NumberFormat;
import java.util.HashMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
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
 * <p>
 * PresentationGenerator generates TeX presentation output
 * </p>
 * 
 * <p>
 * In the method <code>getReflectionNamespace()</code> the package is set which is used by Java reflection to determine
 * special function implementations.
 * </p>
 */
public class TeXFormFactory extends AbstractTeXFormFactory {

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

	private int plusPrec;

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
		super(numberFormat);
		init();
	}

	@Override
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

	@Override
	public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc, final int precedence) {
		if (precedence > plusPrec) {
			buf.append("\\left( ");
		}
		// convert(buf, dc.getRealPart(), 0);
		buf.append(convertDoubleToFormattedString(dc.getRealPart()));
		buf.append(" + ");
		// convert(buf, dc.getImaginaryPart(), 0);
		buf.append(convertDoubleToFormattedString(dc.getImaginaryPart()));
		buf.append("\\,"); // InvisibleTimes
		buf.append("i ");
		if (precedence > plusPrec) {
			buf.append("\\right) ");
		}
	}

	@Override
	public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence) {
		if (i.isNegative() && (precedence > plusPrec)) {
			buf.append("\\left( ");
		}
		buf.append(i.toBigNumerator().toString());
		if (i.isNegative() && (precedence > plusPrec)) {
			buf.append("\\right) ");
		}
	}

	@Override
	public void convertFraction(final StringBuilder buf, final IFraction f, final int precedence) {
		if (f.isNegative() && (precedence > plusPrec)) {
			buf.append("\\left( ");
		}
		if (f.getDenominator().isOne()) {
			buf.append(f.getNumerator().toString());
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

	@Override
	public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence) {
		if (c.isImaginaryUnit()) {
			buf.append("i ");
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

	@Override
	public void convertString(final StringBuilder buf, final String str) {
		buf.append(str);
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

	@Override
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

	@Override
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
				final IConverter converter = operTab.get(headStr);
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

	@Override
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

	@Override
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

	public void init() {
		plusPrec = ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence();
		// timesPrec =
		// ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence();
		operTab.put("Abs", new org.matheclipse.core.form.tex.reflection.Abs());
		operTab.put("Binomial", new org.matheclipse.core.form.tex.reflection.Binomial());
		operTab.put("Ceiling", new org.matheclipse.core.form.tex.reflection.Ceiling());
		operTab.put("Complex", new org.matheclipse.core.form.tex.reflection.Complex());
		operTab.put("CompoundExpression", new org.matheclipse.core.form.tex.reflection.CompoundExpression());
		operTab.put("D", new org.matheclipse.core.form.tex.reflection.D());
		operTab.put("DirectedInfinity", new org.matheclipse.core.form.tex.reflection.DirectedInfinity());
		operTab.put("Floor", new org.matheclipse.core.form.tex.reflection.Floor());
		operTab.put("HarmonicNumber", new org.matheclipse.core.form.tex.reflection.HarmonicNumber());
		operTab.put("HurwitzZeta", new org.matheclipse.core.form.tex.reflection.HurwitzZeta());
		operTab.put("Integrate", new org.matheclipse.core.form.tex.reflection.Integrate());
		operTab.put("Limit", new org.matheclipse.core.form.tex.reflection.Limit());
		operTab.put("List", new org.matheclipse.core.form.tex.reflection.List());
		operTab.put("MatrixForm", new org.matheclipse.core.form.tex.reflection.MatrixForm());
		operTab.put("Plus", new org.matheclipse.core.form.tex.reflection.Plus());
		operTab.put("Power", new org.matheclipse.core.form.tex.reflection.Power());
		operTab.put("Product", new org.matheclipse.core.form.tex.reflection.Product());
		operTab.put("Rational", new org.matheclipse.core.form.tex.reflection.Rational());
		operTab.put("Sqrt", new org.matheclipse.core.form.tex.reflection.Sqrt());
		operTab.put("Subscript", new org.matheclipse.core.form.tex.reflection.Subscript());
		operTab.put("Subsuperscript", new org.matheclipse.core.form.tex.reflection.Subsuperscript());
		operTab.put("Sum", new org.matheclipse.core.form.tex.reflection.Sum());
		operTab.put("Superscript", new org.matheclipse.core.form.tex.reflection.Superscript());
		operTab.put("Times", new org.matheclipse.core.form.tex.reflection.Times());
		operTab.put("Zeta", new org.matheclipse.core.form.tex.reflection.Zeta());

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

	@Override
	public void convertSubExpr(StringBuilder buf, IExpr o, int precedence) {
		if (o.isAST()) {
			buf.append("{");
		}
		convert(buf, o, precedence);
		if (o.isAST()) {
			buf.append("}");
		}
	}

}
