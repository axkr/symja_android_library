package org.matheclipse.core.system;

import java.util.Map;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PolynomialHomogenizationNew;

public class HomogenizationTest extends AbstractTestCase {
	public HomogenizationTest() {
		super("HomogenizationTest");
	}

	public final static IBuiltInSymbol Homogenization = S.initFinalSymbol("Homogenization", ID.Zeta + 9);

	static {
		Homogenization.setEvaluator(new Homogenization());
	}

	private static class Homogenization extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isAST()) {
				VariablesSet eVar = new VariablesSet(arg1);
				PolynomialHomogenizationNew substitutions = new PolynomialHomogenizationNew(eVar.getVarList(), engine);
				IExpr temp = substitutions.replaceForward(arg1);
				Map<ISymbol, IExpr> map = substitutions.substitutedVariables();
				IASTAppendable list = F.ListAlloc(substitutions.size());
				list.appendAll(map);
				// sort for canonical expressions:
				EvalAttributes.sort(list);
				return F.List(temp, list);
			}
			return arg1;
		}

		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}
	}

	public void testHomogenization() {
		EvalEngine engine = EvalEngine.get();

		engine.resetModuleCounter4JUnit();
		check("Homogenization(Sin(x))", //
				"{hg$1,{hg$1->Sin(x)}}");

		engine.resetModuleCounter4JUnit();
		check("Homogenization(x^2+Sin(x)+Sin(x)^3)", //
				"{hg$1^2+hg$2+hg$2^3,{hg$1->x,hg$2->Sin(x)}}");

		engine.resetModuleCounter4JUnit();
		check("Homogenization((1+x^2)^(-1))", //
				"{1/(1+hg$1^2),{hg$1->x}}");

		engine.resetModuleCounter4JUnit();
		check("Homogenization(f(x)^(-1))", //
				"{1/hg$1,{hg$1->f(x)}}");
	}
}
