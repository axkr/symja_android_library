package org.matheclipse.core.eval.interfaces;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluate a function with 1 or 2 arguments.
 */
public abstract class AbstractArg12 extends AbstractFunctionEvaluator {

	public IExpr unaryOperator(final IExpr arg0) {
		IExpr result = e1ObjArg(arg0);

		if (result.isPresent()) {
			return result;
		}
		// argument dispatching
		if (arg0 instanceof IAST) {
			result = e1FunArg((IAST) arg0);
			if (result.isPresent()) {
				return result;
			}
		}
		final int hier = arg0.hierarchy();
		if (hier <= IExpr.INTEGERID) {
			if (hier <= IExpr.DOUBLECOMPLEXID) {
				if (hier == IExpr.DOUBLEID) {
					if (arg0 instanceof ApfloatNum) {
						return e1ApfloatArg(((ApfloatNum) arg0).apfloatValue());
					}
					return e1DblArg((INum) arg0);
				}
				if (arg0 instanceof ApcomplexNum) {
					return e1ApcomplexArg(((ApcomplexNum) arg0).apcomplexValue());
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

		return F.NIL;
	}

	public IExpr e1ObjArg(final IExpr o) {
		return F.NIL;
	}

	public IExpr e1DblArg(final INum d) {
		return F.NIL;
	}

	public IExpr e1DblComArg(final IComplexNum c) {
		return F.NIL;
	}

	public IExpr e1ApfloatArg(Apfloat arg1) {
		return F.NIL;
	}

	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		return F.NIL;
	}

	public IExpr e1IntArg(final IInteger i) {
		return F.NIL;
	}

	public IExpr e1FraArg(final IFraction f) {
		return F.NIL;
	}

	public IExpr e1ComArg(final IComplex c) {
		return F.NIL;
	}

	public IExpr e1SymArg(final ISymbol s) {
		return F.NIL;
	}

	public IExpr e1FunArg(final IAST f) {
		return F.NIL;
	}

	public IExpr binaryOperator(final IExpr o0, final IExpr o1) {
		IExpr result = F.NIL;
		if (o0 instanceof ApcomplexNum) {
			if (o1.isNumber()) {
				result = e2ApcomplexArg((ApcomplexNum) o0,
						((INumber) o1).apcomplexNumValue(((ApcomplexNum) o0).precision()));
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof ApcomplexNum) {
			if (o0.isNumber()) {
				result = e2ApcomplexArg(((INumber) o0).apcomplexNumValue(((ApcomplexNum) o1).precision()),
						(ApcomplexNum) o1);
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o0 instanceof ComplexNum) {
			if (o1.isNumber()) {
				result = e2DblComArg((ComplexNum) o0, ((INumber) o1).complexNumValue());
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof ComplexNum) {
			if (o0.isNumber()) {
				result = e2DblComArg(((INumber) o0).complexNumValue(), (ComplexNum) o1);
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		}

		if (o0 instanceof ApfloatNum) {
			if (o1.isReal()) {
				result = e2ApfloatArg((ApfloatNum) o0,
						((ISignedNumber) o1).apfloatNumValue(((ApfloatNum) o0).precision()));
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof ApfloatNum) {
			if (o0.isReal()) {
				result = e2ApfloatArg(((ISignedNumber) o0).apfloatNumValue(((ApfloatNum) o1).precision()),
						(ApfloatNum) o1);
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o0 instanceof Num) {
			if (o1.isReal()) {
				result = e2DblArg((Num) o0, ((ISignedNumber) o1).numValue());
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		} else if (o1 instanceof Num) {
			if (o0.isReal()) {
				result = e2DblArg(((ISignedNumber) o0).numValue(), (Num) o1);
			}
			if (result.isPresent()) {
				return result;
			}
			return e2ObjArg(o0, o1);
		}

		result = e2ObjArg(o0, o1);
		if (result.isPresent()) {
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

			return F.NIL;
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

			return F.NIL;
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

		return F.NIL;
	}

	public IExpr e2ComArg(final IComplex c0, final IComplex c1) {
		return F.NIL;
	}

	public IExpr e2DblArg(final INum d0, final INum d1) {
		return F.NIL;
	}

	public IExpr e2DblComArg(final IComplexNum d0, final IComplexNum d1) {
		return F.NIL;
	}

	public IExpr e2ApfloatArg(final ApfloatNum d0, final ApfloatNum d1) {
		return F.NIL;
	}

	public IExpr e2ApcomplexArg(final ApcomplexNum c0, final ApcomplexNum c1) {
		return F.NIL;
	}

	public IExpr e2FraArg(final IFraction f0, final IFraction f1) {
		return F.NIL;
	}

	public IExpr e2SymArg(final ISymbol s0, final ISymbol s1) {
		return F.NIL;
	}

	public IExpr e2FunArg(final IAST f0, final IAST f1) {
		return F.NIL;
	}

	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		return F.NIL;
	}

	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		return F.NIL;
	}

	public IExpr eComFraArg(final IComplex c0, final IFraction i1) {
		return F.NIL;
	}

	public IExpr eComIntArg(final IComplex c0, final IInteger i1) {
		return F.NIL;
	}

	public IExpr eFunIntArg(final IAST f0, final IInteger i1) {
		return F.NIL;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);
		if (ast.size() != 3) {
			return unaryOperator(ast.arg1());
		}
		return binaryOperator(ast.arg1(), ast.arg2());
	}

}
