package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcher;
import org.matheclipse.parser.client.SyntaxError;

/**
 *
 */
public abstract class AbstractArgMultiple extends AbstractArg2 {

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() > 2) {
			IAST temp = evaluateHashs(ast);
			if (temp != null) {
				return temp;
			}
		}
		if (ast.size() == 3) {
			return binaryOperator(ast.get(1), ast.get(2));
		}

		if (ast.size() > 3) {
			final ISymbol sym = ast.topHead();
			final IAST result = F.function(sym);
			IExpr tres;
			IExpr temp = ast.get(1);
			boolean evaled = false;
			int i = 2;

			while (i < ast.size()) {

				tres = binaryOperator(temp, ast.get(i));

				if (tres == null) {

					for (int j = i + 1; j < ast.size(); j++) {
						tres = binaryOperator(temp, ast.get(j));

						if (tres != null) {
							evaled = true;
							temp = tres;

							ast.remove(j);

							break;
						}
					}

					if (tres == null) {
						result.add(temp);
						if (i == ast.size() - 1) {
							result.add(ast.get(i));
						} else {
							temp = ast.get(i);
						}
						i++;
					}

				} else {
					evaled = true;
					temp = tres;

					if (i == (ast.size() - 1)) {
						result.add(temp);
					}

					i++;
				}
			}

			if (evaled) {

				if ((result.size() == 2) && ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)) {
					return result.get(1);
				}

				return result;
			}
		}

		return null;
	}

	public HashedOrderlessMatcher getHashRuleMap() {
		return null;
	}

	public IAST evaluateHashs(final IAST ast) {
		HashedOrderlessMatcher hashRuleMap = getHashRuleMap();
		if (hashRuleMap == null) {
			return null;
		}
		return hashRuleMap.evaluate(ast);
	}

	/**
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 * @param condition
	 * @see org.matheclipse.core.patternmatching.HashedOrderlessMatcher#setUpHashRule(org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr)
	 */
	public void setUpHashRule(IExpr lhs1, IExpr lhs2, IExpr rhs, IExpr condition) {
		getHashRuleMap().setUpHashRule(lhs1, lhs2, rhs, condition);
	}

	/**
	 * @param lhs1Str
	 * @param lhs2Str
	 * @param rhsStr
	 * @param conditionStr
	 * @throws SyntaxError
	 * @see org.matheclipse.core.patternmatching.HashedOrderlessMatcher#setUpHashRule(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setUpHashRule(String lhs1Str, String lhs2Str, String rhsStr, String conditionStr) throws SyntaxError {
		getHashRuleMap().setUpHashRule(lhs1Str, lhs2Str, rhsStr, conditionStr);
	}

	/**
	 * @param lhs1Str
	 * @param lhs2Str
	 * @param rhsStr
	 * @throws SyntaxError
	 * @see org.matheclipse.core.patternmatching.HashedOrderlessMatcher#setUpHashRule(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void setUpHashRule(String lhs1Str, String lhs2Str, String rhsStr) throws SyntaxError {
		getHashRuleMap().setUpHashRule(lhs1Str, lhs2Str, rhsStr);
	}

	@Override
	public IExpr binaryOperator(final IExpr o0, final IExpr o1) {
		IExpr result = null;
		if (o0 instanceof INum) {
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
		} else if (o1 instanceof INum) {
			// use specialized methods for numeric mode
			if (o0.isInteger()) {
				result = e2DblArg(F.num((IInteger) o0), (INum) o1);
			} else if (o0.isFraction()) {
				result = e2DblArg(F.num((IFraction) o0), (INum) o1);
			} else if (o0 instanceof IComplexNum) {
				result = e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).getRealPart()));
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
		} else if (o0 instanceof IFraction) {
			if (o1 instanceof IInteger) {
				return e2FraArg((IFraction) o0, F.fraction((IInteger) o1, F.C1));
			}
			if (o1 instanceof IFraction) {
				return e2FraArg((IFraction) o0, (IFraction) o1);
			}
			if (o1 instanceof IComplex) {
				return e2ComArg(F.complex((IFraction) o0), (IComplex) o1);
			}
		} else if (o0 instanceof IComplex) {
			if (o1 instanceof IInteger) {
				return eComIntArg((IComplex) o0, (IInteger) o1);
			}
			if (o1 instanceof IComplex) {
				return e2ComArg((IComplex) o0, (IComplex) o1);
			}
		}
		result = e2ObjArg(o0, o1);
		if (result != null) {
			return result;
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

	@Override
	public abstract IExpr e2IntArg(final IInteger i0, final IInteger i1);
}
