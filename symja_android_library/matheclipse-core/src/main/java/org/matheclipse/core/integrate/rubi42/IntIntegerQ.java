package org.matheclipse.core.integrate.rubi42;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the first argument is an integer;
 * <code>False</code> otherwise
 */
public class IntIntegerQ extends AbstractFunctionEvaluator {

	public IntIntegerQ() {
	}

	// IntegerQ[u_+m_*(n_+v_)] :=
	// RationalQ[m] && RationalQ[n] && IntegerQ[m*n] && IntegerQ[u+m*v]
	//
	// IntegerQ[u_List] :=
	// MapAnd[IntegerQ,u]
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		if (ast.get(1).isList()) {
			IAST list1 = (IAST) ast.get(1);
			for (int i = 1; i < list1.size(); i++) {
				if (!ast.get(i).isInteger()) {
					return F.False;
				}
			}

			return F.True;
		}
		return F.bool(ast.get(1).isInteger());
	}

	@Override
	public void setUp(final ISymbol symbol) {
		// symbol.setAttributes(ISymbol.LISTABLE);
	}

}
