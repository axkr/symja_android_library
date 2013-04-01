package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Inner extends AbstractFunctionEvaluator {

	public Inner() {
	}

	@Override 
	public IExpr evaluate(final IAST functionList) {
		if (functionList.size() != 5 || (functionList.get(2).isVector()<0) || (functionList.get(3).isVector()<0)) {
			return null;
		}
		IAST result = F.ast(functionList.get(4));
		IAST l2 = (IAST) functionList.get(2);
		IAST l3 = (IAST) functionList.get(3);

		if (l2.size() != l2.size()) {
			return null;
		}
		IAST temp;
		for (int i = 1; i < l2.size(); i++) {
			temp = F.ast(functionList.get(1));
			temp.add(l2.get(i));
			temp.add(l3.get(i));
			result.add(temp);
		}
		return result;
	}

}
