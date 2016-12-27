package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Positive;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y;
import static org.matheclipse.core.expression.F.y_;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PlusOp;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcher;

public class Plus extends AbstractArgMultiple implements INumeric {
	/**
	 * Constructor for the singleton
	 */
	public final static Plus CONST = new Plus();

	private static HashedOrderlessMatcher ORDERLESS_MATCHER = new HashedOrderlessMatcher();

	public Plus() {

	}

	@Override
	public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
		return c0.add(c1);
	}

	@Override
	public IExpr e2DblArg(final INum d0, final INum d1) {
		return d0.add(d1);
	}

	@Override
	public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
		return d0.add(d1);
	}

	@Override
	public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
		return f0.add(f1);
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return i0.add(i1);
	}

	@Override
	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return c0.add(F.complex(i1, F.C0));
	}

	private IExpr evalNumericMode(final IAST ast) {
		INum number = F.CD0;
		int start = -1;
		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i) instanceof INum) {
				if (ast.get(i) instanceof ApfloatNum) {
					number = number.add((INum) ast.get(i));
				} else {
					number = number.add((INum) ast.get(i));
				}
			} else if (ast.get(i) instanceof IComplexNum) {
				start = i;
				break;
			} else {
				return F.NIL;
			}
		}
		if (start < 0) {
			return number;
		}
		IComplexNum complexNumber;
		if (number instanceof Num) {
			complexNumber = F.complexNum(((Num) number).doubleValue());
		} else {
			complexNumber = F.complexNum(((ApfloatNum) number).apfloatValue());
		}
		for (int i = start; i < ast.size(); i++) {
			if (ast.get(i) instanceof INum) {
				number = (INum) ast.get(i);
				if (number instanceof Num) {
					complexNumber = complexNumber.add(F.complexNum(((Num) number).doubleValue()));
				} else {
					complexNumber = complexNumber.add(F.complexNum(((ApfloatNum) number).apfloatValue()));
				}
			} else if (ast.get(i) instanceof IComplexNum) {
				complexNumber = complexNumber.add((IComplexNum) ast.get(i));
			} else {
				return F.NIL;
			}
		}
		return complexNumber;
	}


	@Override
	public double evalReal(final double[] stack, final int top, final int size) {
		double result = 0;
		for (int i = top - size + 1; i < top + 1; i++) {
			result += stack[i];
		}
		return result;
	}
	
	/**
	 * 
	 * See: <a href="http://www.cs.berkeley.edu/~fateman/papers/newsimp.pdf">
	 * Experiments in Hash-coded Algebraic Simplification</a>
	 * 
	 * @param ast
	 *            the abstract syntax tree (AST) of the form
	 *            <code>Plus(...)</code> which should be evaluated
	 * @return the evaluated object or <code>null</code>, if evaluation isn't
	 *         possible
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size == 1) {
			return F.C0;
		}
		if (size > 2) {
			PlusOp plusOp = new PlusOp(size);
			for (int i = 1; i < size; i++) {
				final IExpr temp = plusOp.plus(ast.get(i));
				if (temp.isPresent()) {
					return temp;
				}
			}
			if (plusOp.isEvaled()) {
				return plusOp.getSum();
			}
		}

		if (size > 2) {
			IExpr temp = evaluateHashs(ast);
			if (temp.isAST(F.Plus, 2)) {
				return ((IAST) temp).arg1();
			}
			return temp;
		}
		return F.NIL;
	}
	
	@Override
	public HashedOrderlessMatcher getHashRuleMap() {
		return ORDERLESS_MATCHER;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		IExpr temp = evalNumericMode(ast);
		if (temp.isPresent()) {
			return temp;
		}
		return evaluate(ast, engine);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(
				ISymbol.ONEIDENTITY | ISymbol.ORDERLESS | ISymbol.FLAT | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);

		// ORDERLESS_MATCHER.setUpHashRule("Sin[x_]^2", "Cos[x_]^2", "a");
		ORDERLESS_MATCHER.definePatternHashRule(Power(Sin(x_), C2), Power(Cos(x_), C2), C1);
		// ORDERLESS_MATCHER.setUpHashRule("a_*Sin[x_]^2", "a_*Cos[x_]^2", "a");
		ORDERLESS_MATCHER.defineHashRule(Times(a_, Power(Sin(x_), C2)), Times(a_, Power(Cos(x_), C2)), a);
		// ORDERLESS_MATCHER.setUpHashRule("ArcSin[x_]", "ArcCos[x_]", "Pi/2");
		ORDERLESS_MATCHER.defineHashRule(ArcSin(x_), ArcCos(x_), Times(C1D2, Pi));
		// ORDERLESS_MATCHER.setUpHashRule("ArcTan[x_]", "ArcCot[x_]", "Pi/2");
		ORDERLESS_MATCHER.defineHashRule(ArcTan(x_), ArcCot(x_), Times(C1D2, Pi));
		// ORDERLESS_MATCHER.setUpHashRule("ArcTan[x_]", "ArcTan[y_]", "Pi/2",
		// "Positive[x]&&(y==1/x)");
		ORDERLESS_MATCHER.defineHashRule(ArcTan(x_), ArcTan(y_), Times(C1D2, Pi),
				And(Positive(x), Equal(y, Power(x, CN1))));
		// ORDERLESS_MATCHER.setUpHashRule("-ArcTan[x_]", "-ArcTan[y_]",
		// "-Pi/2", "Positive[x]&&(y==1/x)");
		ORDERLESS_MATCHER.definePatternHashRule(Times(CN1, ArcTan(x_)), Times(CN1, ArcTan(y_)), Times(CN1D2, Pi),
				And(Positive(x), Equal(y, Power(x, CN1))));
		// ORDERLESS_MATCHER.setUpHashRule("Cosh[x_]^2", "-Sinh[x_]^2", "1");
		ORDERLESS_MATCHER.definePatternHashRule(Power(Cosh(x_), C2), Times(CN1, Power(Sinh(x_), C2)), C1);
		super.setUp(newSymbol);
	}

}
