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
					temp = temp.clone();
					temp.set(0, F.Unequal);
					return temp;
				} else if (head.equals(F.Unequal)) {
					temp = temp.clone();
					temp.set(0, F.Equal);
					return temp;
				} else if (head.equals(F.Greater)) {
					temp = temp.clone();
					temp.set(0, F.LessEqual);
					return temp;
				} else if (head.equals(F.GreaterEqual)) {
					temp = temp.clone();
					temp.set(0, F.Less);
					return temp;
				} else if (head.equals(F.Less)) {
					temp = temp.clone();
					temp.set(0, F.GreaterEqual);
					return temp;
				} else if (head.equals(F.LessEqual)) {
					temp = temp.clone();
					temp.set(0, F.Greater);
					return temp;
				}
			}
		}
		return null;
	}

}