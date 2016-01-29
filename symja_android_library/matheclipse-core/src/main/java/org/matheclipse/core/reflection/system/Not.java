package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Not extends AbstractArg1 {

	public Not() {
	}

	@Override
	public IExpr e1ObjArg(final IExpr o) {
		if (o.isTrue()) {
			return F.False;
		}
		if (o.isFalse()) {
			return F.True;
		}
		if (o.isAST()) {
			IAST temp = (IAST) o;
			if (o.isNot()) {
				return ((IAST) o).arg1();
			}
			if (temp.size() == 3) {
				IExpr head = temp.head();
				if (head.equals(F.Equal)) {
					return temp.apply(F.Unequal); 
				} else if (head.equals(F.Unequal)) {
					return temp.apply(F.Equal); 
				} else if (head.equals(F.Greater)) {
					return temp.apply(F.LessEqual); 
				} else if (head.equals(F.GreaterEqual)) {
					return temp.apply(F.Less); 
				} else if (head.equals(F.Less)) {
					return temp.apply(F.GreaterEqual); 
				} else if (head.equals(F.LessEqual)) {
					return temp.apply(F.Greater); 
				}
			}
		}
		return F.NIL;
	}

}