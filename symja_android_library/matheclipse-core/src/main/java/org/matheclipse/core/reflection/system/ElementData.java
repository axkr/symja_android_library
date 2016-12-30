package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.NIL;

import java.util.HashMap;

import org.matheclipse.core.data.ElementData1;
import org.matheclipse.core.data.ElementData2;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *  
 */
public class ElementData extends AbstractFunctionEvaluator {

	private static java.util.Map<IExpr, IExpr> MAP_NUMBER_NAME = new HashMap<IExpr, IExpr>();

	private static java.util.Map<IExpr, IAST> MAP_NAME_DATA = new HashMap<IExpr, IAST>();

	public ElementData() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.size() == 2) {
			if (ast.arg1().isInteger()) {
				IExpr temp = MAP_NUMBER_NAME.get(ast.arg1());
				if (temp != null) {
					return temp;
				}
			}
		} else {
			if (ast.arg1() instanceof IStringX) {
				IAST temp = MAP_NAME_DATA.get(ast.arg1());
				if (temp != null) {
					if (ast.arg2().toString().equals("AtomicNumber")){
						return temp.arg1();
					}
				}
			}
		}
		return NIL;

	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		IAST[] list = ElementData1.ELEMENTS;
		for (int i = 0; i < list.length; i++) {
			MAP_NUMBER_NAME.put(list[i].arg1(), list[i].arg3());
			IAST subList = F.List();
			subList.append(list[i].arg1()); // atomic number
			MAP_NAME_DATA.put(list[i].arg2(), subList);
			MAP_NAME_DATA.put(list[i].arg3(), subList);
		}
		list = ElementData2.ELEMENTS;
		for (int i = 0; i < list.length; i++) {
			MAP_NUMBER_NAME.put(list[i].arg1(), list[i].arg3());
			IAST subList = F.List();
			subList.append(list[i].arg1()); // atomic number
			MAP_NAME_DATA.put(list[i].arg2(), subList);
			MAP_NAME_DATA.put(list[i].arg3(), subList);
		}
	}

}
