package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcCosRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arccosine
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions"> Inverse_trigonometric functions</a>
 */
public class ArcCos extends AbstractTrigArg1 implements INumeric, ArcCosRules {

	/**
	 * <pre>
	 * ArcCos[1/2*2^(1/2)]=1/4*Pi,
	 *   ArcCos[1/2*3^(1/2)]=1/6*Pi,
	 *   ArcCos[1/2*(2+2^(1/2))^(1/2)]=1/8*Pi,
	 *   ArcCos[1/2*(2-2^(1/2))^(1/2)]=3/8*Pi,
	 *   ArcCos[(1+1/3*3^(1/2))*1/4*6^(1/2)]=1/12*Pi,
	 *   ArcCos[(1-1/3*3^(1/2))*1/4*6^(1/2)]=5/12*Pi,
	 *   ArcCos[-1/4+1/4*5^(1/2)]=2/5*Pi,
	 *   ArcCos[1]=0,
	 *   ArcCos[0]=1/2*Pi,
	 *   ArcCos[1/2]=1/3*Pi,
	 *   ArcCos[I]=1/2*Pi-I*ArcSinh[1],
	 *   ArcCos[DirectedInfinity[1]]=I*Infinity,
	 *   ArcCos[(5+5^(1/2))^(1/2)*1/4*2^(1/2)]=1/10*Pi,
	 *   ArcCos[(5-5^(1/2))^(1/2)*1/4*2^(1/2)]=3/10*Pi,
	 *   ArcCos[Cosh[1]]=I,
	 *   ArcCos[1/4+1/4*5^(1/2)]=1/5*Pi,
	 * </pre>
	 */
	// final static IAST RULES = List(
	// Set(ArcCos(Times(Times(C1D4, Power(C2, C1D2)), Power(Plus(Times(CN1, Power(C5, C1D2)), C5), C1D2))), Times(fraction(3L, 10L),
	// Pi)), Set(ArcCos(Times(Times(C1D4, Plus(Times(C1D3, Power(C3, C1D2)), C1)), Power(integer(6L), C1D2))), Times(fraction(
	// 1L, 12L), Pi)), Set(ArcCos(Times(Times(C1D4, Plus(Times(CN1D3, Power(C3, C1D2)), C1)), Power(integer(6L), C1D2))), Times(
	// fraction(5L, 12L), Pi)), Set(ArcCos(Times(Times(C1D4, Power(C2, C1D2)), Power(Plus(Power(C5, C1D2), C5), C1D2))), Times(
	// fraction(1L, 10L), Pi)), Set(ArcCos(Times(C1D2, Power(C2, C1D2))), Times(C1D4, Pi)), Set(ArcCos(Times(C1D2, Power(C3,
	// C1D2))), Times(fraction(1L, 6L), Pi)), Set(ArcCos(Times(C1D2, Power(Plus(Power(C2, C1D2), C2), C1D2))), Times(fraction(
	// 1L, 8L), Pi)), Set(ArcCos(Times(C1D2, Power(Plus(Times(CN1, Power(C2, C1D2)), C2), C1D2))), Times(fraction(3L, 8L), Pi)),
	// Set(ArcCos(C1), C0), Set(ArcCos(C0), Times(C1D2, Pi)), Set(ArcCos(C1D2), Times(C1D3, Pi)), Set(ArcCos(CI), Plus(Times(Times(
	// CN1, CI), ArcSinh(C1)), Times(C1D2, Pi))), Set(ArcCos(CInfinity), Times(CI, CInfinity)), Set(ArcCos(Cosh(C1)), CI), Set(
	// ArcCos(Plus(Times(C1D4, Power(C5, C1D2)), C1D4)), Times(fraction(1L, 5L), Pi)), Set(ArcCos(Plus(Times(C1D4, Power(C5,
	// C1D2)), Times(CN1, C1D4))), Times(fraction(2L, 5L), Pi)));

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcCos() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (AbstractFunctionEvaluator.isNegativeExpression(arg1)) {
			return Plus(Times(CN1, Pi), ArcCos(Times(CN1, arg1)));
		}
		return null;
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return F.num(Math.acos(arg1.getRealPart()));
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return ComplexUtils.acos(arg1);
	}

	public double evalReal(final double[] stack, final int top, final int size) {
		if (size != 1) {
			throw new UnsupportedOperationException();
		}
		return Math.acos(stack[top]);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
