package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluate a function with 1 or 2 arguments.
 */
public abstract class AbstractArg12 extends AbstractFunctionEvaluator {

	public IExpr unaryOperator(final IExpr arg0) {
		final IExpr result = e1ObjArg(arg0);

		if (result != null) {
			return result;
		}
		// argument dispatching
		if (arg0 instanceof IAST) {
			e1FunArg((IAST) arg0);
		}
		final int hier = arg0.hierarchy();
		if (hier <= IExpr.INTEGERID) {
			if (hier <= IExpr.DOUBLECOMPLEXID) {
				if (hier == IExpr.DOUBLEID) {
					return e1DblArg((INum) arg0);
				}
				return e1DblComArg((IComplexNum) arg0);
			} else {
				return e1IntArg((IInteger) arg0);
			}
		} else {
			if (hier <= IExpr.COMPLEXID) {
				if (hier == IExpr.FRACTIONID) {
					return e1FraArg((IFraction) arg0);
				}
				return e1ComArg((IComplex) arg0);
			} else {
				if (hier == IExpr.SYMBOLID) {
					return e1SymArg((ISymbol) arg0);
				}
			}
		}

		return null;
	}

	public IExpr e1ObjArg(final IExpr o) {
		return null;
	}

	public IExpr e1DblArg(final INum d) {
		return null;
	}

	public IExpr e1DblComArg(final IComplexNum c) {
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

	public IExpr binaryOperator(final IExpr o0, final IExpr o1) {
		final IExpr result = e2ObjArg(o0, o1);
		if (result != null) {
			return result;
		}
		if (o0 instanceof INum) {
			if (o1 instanceof INum) {
				return e2DblArg((INum) o0, (INum) o1);
			}
			if (o1 instanceof IComplexNum) {
				return e2DblComArg(F.complexNum(((INum) o0).getRealPart()), (IComplexNum) o1);
			}

			return null;
		}

		if (o0 instanceof IComplexNum) {
			if (o1 instanceof INum) {
				return e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).getRealPart()));
			}
			if (o1 instanceof IComplexNum) {
				return e2DblComArg((IComplexNum) o0, (IComplexNum) o1);
			}

			return null;
		}

		if (o0 instanceof IInteger) {
			if (o1 instanceof IInteger) {
				return e2IntArg((IInteger) o0, (IInteger) o1);
			}
			if (o1 instanceof IFraction) {
				return e2FraArg(F.fraction((IInteger) o0, F.C1), (IFraction) o1);
			}
			if (o1 instanceof IComplex) {
				return e2ComArg(F.complex((IInteger) o0, F.C0), (IComplex) o1);
			}

			return null;
		}

		if (o0 instanceof IFraction) {
			if (o1 instanceof IInteger) {
				return e2FraArg((IFraction) o0, F.fraction((IInteger) o1, F.C1));
			}
			if (o1 instanceof IFraction) {
				return e2FraArg((IFraction) o0, (IFraction) o1);
			}
			if (o1 instanceof IComplex) {
				return e2ComArg(F.complex((IFraction) o0), (IComplex) o1);
			}

			return null;
		}

		if (o0 instanceof IComplex) {
			if (o1 instanceof IInteger) {
				return eComIntArg((IComplex) o0, (IInteger) o1);
			}
			if (o1 instanceof IComplex) {
				return e2ComArg((IComplex) o0, (IComplex) o1);
			}
		}

		if (o0 instanceof ISymbol) {
			if (o1 instanceof ISymbol) {
				return e2SymArg((ISymbol) o0, (ISymbol) o1);
			}
		}

		if (o0 instanceof IAST) {
			if (o1 instanceof IInteger) {
				return eFunIntArg((IAST) o0, (IInteger) o1);
			}
			if (o1 instanceof IAST) {
				return e2FunArg((IAST) o0, (IAST) o1);
			}
		}

		return null;
	}

	public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
		return null;
	}

	public IExpr e2DblArg(final INum d0, final INum d1) {
		return null;
	}

	public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
		return null;
	}

	public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
		return null;
	}

	public IExpr e2SymArg(final ISymbol s0, final ISymbol s1) {
		return null;
	}

	public IExpr e2FunArg(final IAST f0, final IAST f1) {
		return null;
	}

	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return null;
	}

	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		return null;
	}

	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return null;
	}

	public IExpr eFunIntArg(final IAST f0, final IInteger i1) {
		return null;
	}

	@Override
	public IExpr evaluate(final IAST functionList) {
		Validate.checkRange(functionList, 2, 3);
		if (functionList.size() != 3) {
			// if (functionList.size() != 2) {
			// throw new WrongNumberOfArguments(functionList, 2,
			// functionList.size()-1);
			// }
			return unaryOperator(functionList.get(1));
		}
		return binaryOperator(functionList.get(1), functionList.get(2));
	}

}
