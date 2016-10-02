package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MapThread;
import static org.matheclipse.core.expression.F.Most;
import static org.matheclipse.core.expression.F.Prepend;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.g;
import static org.matheclipse.core.expression.F.r;
import static org.matheclipse.core.expression.F.y;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class DesignMatrix extends AbstractEvaluator {

	public DesignMatrix() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 4);
		IExpr m = ast.arg1();
		IExpr f = ast.arg2();
		IExpr x = ast.arg3();
		if (f.isList()) {
			if (x.isAtom()) {
				// DesignMatrix[m_, f_List, x_?AtomQ] := 
				//   DesignMatrix[m, {f}, ConstantArray[x, Length[f]]]
				return F.DesignMatrix(m, F.List(f), F.ConstantArray(x, F.Length(f)));
			} else if (x.isList()) {
				// DesignMatrix[m_, f_List, x_List] :=
				//   Prepend[MapThread[Function[{g, y, r}, g /. y -> r], {f, x, Most[#]}], 1]& /@ m 
				return Map(Function(Prepend(
						MapThread(Function(List(g, y, r), ReplaceAll(g, Rule(y, r))), List(f, x, Most(Slot1))), C1)),
						m);
			}
		} else {
			if (x.isAtom()) {
				// DesignMatrix[m_, f_, x_?AtomQ]': 'DesignMatrix[m, {f}, {x}]
				return F.DesignMatrix(m, F.List(f), F.List(x));
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
