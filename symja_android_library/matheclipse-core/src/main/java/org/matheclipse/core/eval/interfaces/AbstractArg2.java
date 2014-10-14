package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluate a function with 2 arguments.
 */
public abstract class AbstractArg2 extends AbstractFunctionEvaluator {

	public IExpr binaryOperator(final IExpr o0, final IExpr o1) {
		IExpr result = null;
		if (o0 instanceof Num) {
			// use specialized methods for numeric mode
			if (o1 instanceof INum) {
				result = e2DblArg((INum) o0, (INum) o1);
			} else if (o1.isInteger()) {
				result = e2DblArg((INum) o0, F.num((IInteger) o1));
			} else if (o1.isFraction()) {
				result = e2DblArg((INum) o0, F.num((IFraction) o1));
			} else if (o1 instanceof IComplexNum) {
				result = e2DblComArg(F.complexNum(((INum) o0).getRealPart()), (IComplexNum) o1);
			}
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o0 instanceof ApfloatNum) {
			// use specialized methods for numeric mode
			if (o1 instanceof ApfloatNum) {
				result = e2ApfloatArg((ApfloatNum) o0, (ApfloatNum) o1);
			} else if (o1 instanceof Num) {
				ApfloatNum apf = ApfloatNum.valueOf(((Num) o1).doubleValue(), ((ApfloatNum) o0).precision());
				result = e2ApfloatArg((ApfloatNum) o0, apf);
			} else if (o1.isInteger()) {
				result = e2ApfloatArg((ApfloatNum) o0,
						ApfloatNum.valueOf(((IInteger) o1).getBigNumerator(), ((ApfloatNum) o0).precision()));
			} else if (o1.isFraction()) {
				result = e2ApfloatArg((ApfloatNum) o0, ApfloatNum.valueOf(((IFraction) o1).getBigNumerator(),
						((IFraction) o1).getBigDenominator(), ((ApfloatNum) o0).precision()));
			}
			// TODO implement missing classes
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof ApfloatNum) {
			// use specialized methods for numeric mode
			if (o0 instanceof Num) {
				ApfloatNum apf = ApfloatNum.valueOf(((Num) o0).doubleValue(), ((ApfloatNum) o1).precision());
				result = e2ApfloatArg(apf, (ApfloatNum) o1);
			} else if (o0.isInteger()) {
				result = e2ApfloatArg(ApfloatNum.valueOf(((IInteger) o0).getBigNumerator(), ((ApfloatNum) o1).precision()),
						(ApfloatNum) o1);
			} else if (o1.isFraction()) {
				result = e2ApfloatArg(ApfloatNum.valueOf(((IFraction) o0).getBigNumerator(), ((IFraction) o0).getBigDenominator(),
						((ApfloatNum) o1).precision()), (ApfloatNum) o1);
			}
			// TODO implement missing classes
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof Num) {
			// use specialized methods for numeric mode
			if (o0.isInteger()) {
				result = e2DblArg(F.num((IInteger) o0), (Num) o1);
			} else if (o0.isFraction()) {
				result = e2DblArg(F.num((IFraction) o0), (Num) o1);
			} else if (o0 instanceof IComplexNum) {
				result = e2DblComArg((IComplexNum) o0, F.complexNum(((Num) o1).getRealPart()));
			}
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		}

		if (o0 instanceof IComplexNum) {
			// use specialized methods for complex numeric mode
			if (o1 instanceof INum) {
				result = e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).getRealPart()));
			} else if (o1.isInteger()) {
				result = e2DblComArg((IComplexNum) o0, F.complexNum((IInteger) o1));
			} else if (o1.isFraction()) {
				result = e2DblComArg((IComplexNum) o0, F.complexNum((IFraction) o1));
			} else if (o1 instanceof IComplexNum) {
				result = e2DblComArg((IComplexNum) o0, (IComplexNum) o1);
			}
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof IComplexNum) {
			// use specialized methods for complex numeric mode
			if (o0 instanceof INum) {
				result = e2DblComArg(F.complexNum(((INum) o0).getRealPart()), (IComplexNum) o1);
			} else if (o0.isInteger()) {
				result = e2DblComArg(F.complexNum((IInteger) o0), (IComplexNum) o1);
			} else if (o0.isFraction()) {
				result = e2DblComArg(F.complexNum((IFraction) o0), (IComplexNum) o1);
			}
			if (result != null) {
				return result;
			}
			return e2ObjArg(o0, o1);
		}

		result = e2ObjArg(o0, o1);
		if (result != null) {
			return result;
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
			if (o1 instanceof IFraction) {
				return eComFraArg((IComplex) o0, (IFraction) o1);
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

	public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
		return null;
	}

	public IExpr e2DblArg(final INum d0, final INum d1) {
		return null;
	}

	public IExpr e2ApcomplexArg(final ApcomplexNum ac0, final ApcomplexNum ac1) {
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

	public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
		return null;
	}

	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return null;
	}

	public IExpr eFunIntArg(final IAST f0, final IInteger i1) {
		return null;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		return binaryOperator(ast.arg1(), ast.arg2());
	}

}
