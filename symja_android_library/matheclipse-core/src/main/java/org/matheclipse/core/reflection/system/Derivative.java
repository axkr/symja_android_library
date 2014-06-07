package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.DerivativeRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 */
public class Derivative extends AbstractFunctionEvaluator implements DerivativeRules {

	/**
	 * { Derivative[ArcCos]=(-1)*(1-#^2)^(-1/2)&, Derivative[ArcCosh]=(#^2-1)^(-1/2)&, Derivative[ArcCot]=(-1)*(1+#^2)^(-1)&,
	 * Derivative[ArcCoth]=(1-#^2)^(-1)&, Derivative[ArcCsc]=-1*#^(-2)*(1-#^(-2))^(-1/2)&,
	 * Derivative[ArcCsch]=-1*Abs[#]^(-1)*(1+#^2)^(-1/2)&, Derivative[ArcSin]=(1-#^2)^(-1/2)&, Derivative[ArcSinh]=(1+#^2)^(-1/2)&,
	 * Derivative[ArcTan]=(1+#^2)^(-1)&, Derivative[ArcTanh]=(1-#^2)^(-1)&, Derivative[ArcSec]=#^(-2)*(1-#^(-2))^(-1/2)&,
	 * Derivative[ArcSech]=-1*#^(-1)*(1-#^2)^(-1/2)&, Derivative[Log]=#^(-1)&, Derivative[Cot]=(-1)*Sin[#]^(-2)&,
	 * Derivative[Coth]=(-1)*Sinh[#]^(-2)&, Derivative[Cos]=(-1)*Sin[#]&, Derivative[Cosh]=(-1)*Sinh[#]&,
	 * Derivative[Csc]=(-1)*Cot[#]*Csc[#]&, Derivative[Csch]=(-1)*Coth[#]*Csch[#]&, Derivative[Sin]=Cos[#]&,
	 * Derivative[Sinh]=Cosh[#]&, Derivative[Tan]=Cos[#]^(-2)&, Derivative[Tanh]=Cosh[#]^(-2)&, Derivative[Sec]=Sec[#]*Tan[#]&,
	 * Derivative[Sech]=(-1)*Tanh[#]*Sech[#]& }
	 */
	// final static IAST RULES = List(
	// Set(Derivative($s("ArcCos")),Function(Times(CN1,Power(Plus(C1,Times(CN1,Power(Slot1,C2))),CN1D2)))),
	// Set(Derivative($s("ArcCosh")),Function(Power(Plus(Power(Slot1,C2),Times(CN1,C1)),CN1D2))),
	// Set(Derivative($s("ArcCot")),Function(Times(CN1,Power(Plus(C1,Power(Slot1,C2)),CN1)))),
	// Set(Derivative($s("ArcCoth")),Function(Power(Plus(C1,Times(CN1,Power(Slot1,C2))),CN1))),
	// Set(Derivative($s("ArcCsc")),Function(Times(Times(CN1,Power(Slot1,integer(-2L))),Power(Plus(C1,Times(CN1,Power(Slot1,integer(-2L)))),CN1D2)))),
	// Set(Derivative($s("ArcCsch")),Function(Times(Times(CN1,Power(Abs(Slot1),CN1)),Power(Plus(C1,Power(Slot1,C2)),CN1D2)))),
	// Set(Derivative($s("ArcSin")),Function(Power(Plus(C1,Times(CN1,Power(Slot1,C2))),CN1D2))),
	// Set(Derivative($s("ArcSinh")),Function(Power(Plus(C1,Power(Slot1,C2)),CN1D2))),
	// Set(Derivative($s("ArcTan")),Function(Power(Plus(C1,Power(Slot1,C2)),CN1))),
	// Set(Derivative($s("ArcTanh")),Function(Power(Plus(C1,Times(CN1,Power(Slot1,C2))),CN1))),
	// Set(Derivative($s("ArcSec")),Function(Times(Power(Slot1,integer(-2L)),Power(Plus(C1,Times(CN1,Power(Slot1,integer(-2L)))),CN1D2)))),
	// Set(Derivative($s("ArcSech")),Function(Times(Times(CN1,Power(Slot1,CN1)),Power(Plus(C1,Times(CN1,Power(Slot1,C2))),CN1D2)))),
	// Set(Derivative($s("Log")),Function(Power(Slot1,CN1))),
	// Set(Derivative($s("Cot")),Function(Times(CN1,Power(Sin(Slot1),integer(-2L))))),
	// Set(Derivative($s("Coth")),Function(Times(CN1,Power(Sinh(Slot1),integer(-2L))))),
	// Set(Derivative($s("Cos")),Function(Times(CN1,Sin(Slot1)))),
	// Set(Derivative($s("Cosh")),Function(Times(CN1,Sinh(Slot1)))),
	// Set(Derivative($s("Csc")),Function(Times(Times(CN1,Cot(Slot1)),Csc(Slot1)))),
	// Set(Derivative($s("Csch")),Function(Times(Times(CN1,Coth(Slot1)),Csch(Slot1)))),
	// Set(Derivative($s("Sin")),Function(Cos(Slot1))),
	// Set(Derivative($s("Sinh")),Function(Cosh(Slot1))),
	// Set(Derivative($s("Tan")),Function(Power(Cos(Slot1),integer(-2L)))),
	// Set(Derivative($s("Tanh")),Function(Power(Cosh(Slot1),integer(-2L)))),
	// Set(Derivative($s("Sec")),Function(Times(Sec(Slot1),Tan(Slot1)))),
	// Set(Derivative($s("Sech")),Function(Times(Times(CN1,Tanh(Slot1)),Sech(Slot1))))
	// );

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public Derivative() {
	}

	@Override
	public IExpr evaluate(IAST ast) {
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDALL);
		super.setUp(symbol);
	}

}
