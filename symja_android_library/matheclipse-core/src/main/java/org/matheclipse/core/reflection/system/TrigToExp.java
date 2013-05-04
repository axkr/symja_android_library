package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * Exponential definitions for trigonometric functions
 * 
 * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"
 * >List of trigonometric identities - Exponential definitions</a>
 */
public class TrigToExp implements IFunctionEvaluator {

	public TrigToExp() {
	}

	class TrigToExpVisitor extends VisitorExpr {
		public TrigToExpVisitor() {
			super();
		}

		@Override
		public IExpr visit2(IExpr head, IExpr arg1) {
			IExpr temp = arg1;
			IExpr result = arg1.accept(this);
			if (result != null) {
				temp = result;
			}
			if (head.equals(Sin)) {
				return Subtract(Times(C1D2, CI, Power(E, Times(CNI, temp))), Times(C1D2, CI, Power(E, Times(CI, temp))));
			}
			if (head.equals(Cos)) {
				return Plus(Times(C1D2, Power(E, Times(CNI, temp))), Times(C1D2, Power(E, Times(CI, temp))));
			}
			if (head.equals(Tan)) {
				return Times(CI, Subtract(Power(E, Times(CNI, temp)), Power(E, Times(CI, temp))), Power(Plus(Power(E, Times(CNI, temp)),
						Power(E, Times(CI, temp))), CN1));
			}
			if (head.equals(ArcSin)) {
				return Times(CNI, Log(Plus(Sqrt(Subtract(C1, Sqr(temp))), Times(CI, temp))));
			}
			if (head.equals(ArcCos)) {
				return Plus(Times(C1D2, Pi), Times(CI, Log(Plus(Sqrt(Subtract(C1, Sqr(temp))), Times(CI, temp)))));
			}
			if (head.equals(ArcTan)) {
				return Subtract(Times(C1D2, CI, Log(Plus(C1, Times(CNI, temp)))), Times(C1D2, CI, Log(Plus(C1, Times(CI, temp)))));
			}
			if (result != null) {
				return $(head, result);
			}
			return null;
		}
	}

	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 2) {
			throw new WrongNumberOfArguments(functionList, 1, functionList.size() - 1);
		}
		TrigToExpVisitor tteVisitor = new TrigToExpVisitor();
		IExpr result = functionList.get(1).accept(tteVisitor);
		if (result != null) {
			return result;
		}
		return functionList.get(1);
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
