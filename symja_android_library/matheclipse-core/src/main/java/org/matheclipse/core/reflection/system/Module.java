package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.math.MathException;

public class Module extends AbstractFunctionEvaluator {
	public Module() {
	}

	/**
	 *
	 */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		if (ast.get(1).isList()) {
			IAST lst = (IAST) ast.get(1);
			IExpr arg2 = ast.get(2);
			final EvalEngine engine = EvalEngine.get();
			// if (arg2.isAST(F.Condition, 3)) {
			// return evalModuleCondition(lst, ((IAST) arg2).get(1), ((IAST)
			// arg2).get(2), engine, null);
			// }
			return evalModule(lst, arg2, engine);
		}

		return null;
	}

	/**
	 * Check the (possible nested) module condition in pattern matcher without
	 * evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkModuleCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (arg1.isList()) {
			IAST intializerList = (IAST) arg1;
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			// final IAST lst = (IAST) ast.get(1);
			final java.util.Map<ISymbol, ISymbol> variables = new HashMap<ISymbol, ISymbol>();

			try {
				rememberVariables(intializerList, engine, varAppend, variables);
				IExpr result = F.subst(arg2, Functors.rules(variables));
				if (result.isCondition()) {
					return Condition.checkCondition(result.getAt(1), result.getAt(2), engine);
				} else if (result.isModule()) {
					return checkModuleCondition(result.getAt(1), result.getAt(2), engine);
				}
			} finally {
				removeVariables(engine, variables);
			}
		}
		return true;
	}

	/**
	 * <code>Module[{variablesList}, rhs ]</code>
	 * 
	 * @param intializerList
	 * @param arg2
	 * @param engine
	 * @return
	 */
	private static IExpr evalModule(IAST intializerList, IExpr arg2, final EvalEngine engine) {
		final int moduleCounter = engine.incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		// final IAST lst = (IAST) ast.get(1);
		final java.util.Map<ISymbol, ISymbol> variables = new HashMap<ISymbol, ISymbol>();

		try {
			rememberVariables(intializerList, engine, varAppend, variables);
			IExpr temp = engine.evaluate(F.subst(arg2, Functors.rules(variables)));
			return temp;
		} finally {
			removeVariables(engine, variables);
		}
	}

	/**
	 * <code>Module[{variablesList}, rhs /; condition]</code>
	 * 
	 * @param variablesList
	 * @param rightHandSide
	 * @param condition
	 * @param engine
	 * @param rules
	 * @return
	 */
	// public static IExpr evalModuleCondition(IAST variablesList, IExpr
	// rightHandSide, IExpr condition, final EvalEngine engine,
	// Function<IExpr, IExpr> rules) {
	// final int moduleCounter = engine.incModuleCounter();
	// final String varAppend = "$" + moduleCounter;
	// // final IAST lst = (IAST) ast.get(1);
	// final java.util.Map<ISymbol, ISymbol> variables = new HashMap<ISymbol,
	// ISymbol>();
	//
	// try {
	// IAST substList = variablesList;
	// if (rules != null) {
	// substList = (IAST) variablesList.replaceAll(rules);
	// substList = (substList == null) ? variablesList : substList;
	// }
	// rememberVariables(substList, engine, varAppend, variables);
	// IExpr substCondition = condition;
	// if (rules != null) {
	// substCondition = (IAST) condition.replaceAll(rules);
	// substCondition = (substCondition == null) ? condition : substCondition;
	// }
	// IExpr result = substCondition.replaceAll(Functors.rules(variables));
	// if (engine.evaluate(result).isTrue()) {
	// IExpr substRHS = rightHandSide;
	// if (rules != null) {
	// substRHS = (IAST) rightHandSide.replaceAll(rules);
	// substRHS = (substRHS == null) ? rightHandSide : substRHS;
	// }
	// result = substRHS.replaceAll(Functors.rules(variables));
	// return engine.evaluate(result);
	// }
	// } finally {
	// removeVariables(engine, variables);
	// }
	// return null;
	// }

	private static void removeVariables(final EvalEngine engine, final java.util.Map<ISymbol, ISymbol> variables) {
		// remove all module variables from eval engine
		Map<String, ISymbol> variableMap = engine.getVariableMap();
		for (ISymbol symbol : variables.values()) {
			variableMap.remove(symbol.toString());
		}
	}

	private static void rememberVariables(IAST variablesList, final EvalEngine engine, final String varAppend,
			final java.util.Map<ISymbol, ISymbol> variables) {
		ISymbol oldSymbol;
		ISymbol newSymbol;
		// remember which local variables we use:
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isSymbol()) {
				oldSymbol = (ISymbol) variablesList.get(i);
				newSymbol = F.$s(oldSymbol.toString() + varAppend);
				variables.put(oldSymbol, newSymbol);
				newSymbol.pushLocalVariable();
			} else {
				if (variablesList.get(i).isAST(F.Set, 3)) {
					final IAST setFun = (IAST) variablesList.get(i);
					if (setFun.get(1).isSymbol()) {
						oldSymbol = (ISymbol) setFun.get(1);
						newSymbol = F.$s(oldSymbol.toString() + varAppend);
						variables.put(oldSymbol, newSymbol);
						IExpr rightHandSide = setFun.get(2);
						try {
							rightHandSide = engine.evaluate(rightHandSide);
						} catch (MathException me) {
							if (Config.DEBUG) {
								me.printStackTrace();
							}
						}
						newSymbol.pushLocalVariable(rightHandSide);
					}
				}
			}
		}
	}

	@Override
	public void setUp(ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
