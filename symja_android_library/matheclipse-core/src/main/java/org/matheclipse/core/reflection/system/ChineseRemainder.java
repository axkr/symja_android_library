package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.JASModInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;

public class ChineseRemainder extends AbstractFunctionEvaluator {

	public ChineseRemainder() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		if (ast.arg1().isList() && ast.arg1().isList()) {
			IAST list1 = (IAST) ast.arg1();
			IAST list2 = (IAST) ast.arg2();
			if (list1.size() != list2.size()) {
				return F.NIL;
			}

			for (int i = 1; i < list1.size(); i++) {
				ModLongRing modIntegerRing = JASModInteger.option2ModLongRing((IInteger) list2.get(i));
				ModLong c = new ModLong(modIntegerRing, ((IInteger) list1.get(i)).getBigNumerator());
				ModLong a = new ModLong(modIntegerRing, ((IInteger) list2.get(i)).getBigNumerator());
				ModLong r = modIntegerRing.chineseRemainder(c, c.inverse(), a);
				return F.integer(r.getVal());
			}
		}
		return F.NIL;
	}

}
