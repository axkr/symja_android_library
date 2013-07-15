package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluate a function with 1 argument.
 */
public abstract class AbstractArg1 extends AbstractFunctionEvaluator {

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		// if (functionList.size() != 2) {
		// throw new WrongNumberOfArguments(functionList, 1, functionList.size()-1);
		// } else {
		final IExpr arg0 = ast.get(1);
		final IExpr result = e1ObjArg(arg0);

		if (result != null) {
			return result;
		}
		// argument dispatching
		if (arg0 instanceof IAST) {
			e1FunArg((IAST) arg0);
		}
		final int hier = ast.get(1).hierarchy();
		if (hier <= IExpr.INTEGERID) {
			if (hier <= IExpr.DOUBLECOMPLEXID) {
				if (hier == IExpr.DOUBLEID) {
					return e1DblArg((INum) ast.get(1));
				}
				return e1DblComArg((IComplexNum) ast.get(1));
			} else {
				return e1IntArg((IInteger) ast.get(1));
			}
		} else {
			if (hier <= IExpr.COMPLEXID) {
				if (hier == IExpr.FRACTIONID) {
					return e1FraArg((IFraction) ast.get(1));
				}
				return e1ComArg((IComplex) ast.get(1));
			} else {
				if (hier == IExpr.SYMBOLID) {
					return e1SymArg((ISymbol) ast.get(1));
				}
			}
		}
		// }

		return null;
	}

	public IExpr e1ObjArg(final IExpr o) {
		return null;
	}

	public IExpr e1DblArg(final INum d) {
		return null;
	}

	public IExpr e1DblComArg(final IComplexNum d) {
		return null;
	}

	public IExpr e1IntArg(final IInteger i) {
		return null;
	}

	public IExpr e1FraArg(final IFraction f) {
		return null;
	}

	public IExpr e1ComArg(final IComplex c) {
		return null;
	}

	public IExpr e1SymArg(final ISymbol s) {
		return null;
	}

	public IExpr e1FunArg(final IAST f) {
		return null;
	}

}
