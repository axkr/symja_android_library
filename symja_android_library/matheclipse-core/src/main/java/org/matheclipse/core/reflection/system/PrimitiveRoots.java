package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href=
 * "http://exploringnumbertheory.wordpress.com/2013/09/09/finding-primitive-roots/">
 * Exploring Number Theory - Finding Primitive Roots</a>
 */
public class PrimitiveRoots extends AbstractTrigArg1 {

	public PrimitiveRoots() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			try {
				IInteger[] roots = ((IInteger) arg1).primitiveRoots();
				if (roots != null) {
					IAST list = F.List();
					for (int i = 0; i < roots.length; i++) {
						list.append(roots[i]);
					}
					return list;
				}
			} catch (ArithmeticException e) {
				// integer to large?
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
