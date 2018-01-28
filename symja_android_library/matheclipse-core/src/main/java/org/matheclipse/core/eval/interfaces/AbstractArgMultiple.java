package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedPatternRules;

/**
 *
 */
public abstract class AbstractArgMultiple extends AbstractArg2 {

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {

		if (ast.isAST2()) {
			IExpr temp = binaryOperator(ast.arg1(), ast.arg2());
			if (temp.isPresent()) {
				return temp;
			}
			return evaluateHashsRepeated(ast, engine);
		}

		if (ast.size() > 3) {
			IASTAppendable tempAST = ast.copyAppendable();
			final ISymbol sym = tempAST.topHead();
			final IASTAppendable result = F.ast(sym);
			IExpr tres;
			IExpr temp = tempAST.arg1();
			boolean evaled = false;
			int i = 2;

			while (i < tempAST.size()) {

				tres = binaryOperator(temp, tempAST.get(i));

				if (!tres.isPresent()) {

					for (int j = i + 1; j < tempAST.size(); j++) {
						tres = binaryOperator(temp, tempAST.get(j));

						if (tres.isPresent()) {
							evaled = true;
							temp = tres;

							tempAST.remove(j);

							break;
						}
					}

					if (!tres.isPresent()) {
						result.append(temp);
						if (i == tempAST.argSize()) {
							result.append(tempAST.get(i));
						} else {
							temp = tempAST.get(i);
						}
						i++;
					}

				} else {
					evaled = true;
					temp = tres;

					if (i == tempAST.argSize()) {
						result.append(temp);
					}

					i++;
				}
			}

			if (evaled) {

				if ((result.isAST1()) && ((sym.getAttributes() & ISymbol.ONEIDENTITY) == ISymbol.ONEIDENTITY)) {
					return result.arg1();
				}

				return result;
			}
			if (tempAST.size() > 2) {
				return evaluateHashsRepeated(tempAST, engine);
			}
		}

		return F.NIL;
	}

	public HashedOrderlessMatcher getHashRuleMap() {
		return null;
	}

	/**
	 * Evaluate an <code>Orderless</code> AST if the
	 * <code>getHashRuleMap()</code> method returns a
	 * <code>HashedOrderlessMatcher</code>,
	 * 
	 * @param orderlessAST
	 * @return
	 * @see HashedPatternRules
	 */
	public IAST evaluateHashsRepeated(final IAST orderlessAST, EvalEngine engine) {
		HashedOrderlessMatcher hashRuleMap = getHashRuleMap();
		if (hashRuleMap == null) {
			return F.NIL;
		}
		return hashRuleMap.evaluateRepeated(orderlessAST, engine);
	}

	/**
	 * Define the rule for the <code>Orderless</code> operator <b>OP</b>.
	 * <code>OP[lhs1, lhs2, ...] := OP[rhs, ...] /; condition</code>
	 * 
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 * @param condition
	 * @see org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher#defineHashRule(org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr)
	 */
	public void defineHashRule(IExpr lhs1, IExpr lhs2, IExpr rhs, IExpr condition) {
		getHashRuleMap().defineHashRule(lhs1, lhs2, rhs, condition);
	}

	/**
	 * @param lhs1
	 * @param lhs2
	 * @param rhs
	 * @param condition
	 * @see org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher#defineHashRule(org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr,
	 *      org.matheclipse.core.interfaces.IExpr)
	 */
	public void setUpHashRule2(IExpr lhs1, IExpr lhs2, IExpr rhs, IExpr condition) {
		getHashRuleMap().definePatternHashRule(lhs1, lhs2, rhs, condition);
	}

	@Override
	public IExpr binaryOperator(final IExpr o0, final IExpr o1) {
		IExpr result = F.NIL;
		if (o0 instanceof INum) {
			// use specialized methods for numeric mode
			if (o1 instanceof INum) {
				result = e2DblArg((INum) o0, (INum) o1);
			} else if (o1.isInteger()) {
				result = e2DblArg((INum) o0, F.num((IInteger) o1));
			} else if (o1.isFraction()) {
				result = e2DblArg((INum) o0, F.num((IFraction) o1));
			} else if (o1 instanceof IComplexNum) {
				if (o0 instanceof ApfloatNum) {
					result = e2DblComArg(F.complexNum(((ApfloatNum) o0).apfloatValue()), (IComplexNum) o1);
				} else {
					result = e2DblComArg(F.complexNum(((INum) o0).getRealPart()), (IComplexNum) o1);
				}
			}
			if (result.isPresent()) {
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
				if (o1 instanceof ApfloatNum) {
					result = e2DblComArg((IComplexNum) o0, F.complexNum(((ApfloatNum) o1).apfloatValue()));
				} else {
					result = e2DblComArg((IComplexNum) o0, F.complexNum(((INum) o1).getRealPart()));
				}
			}
			if (result.isPresent()) {
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
			if (result.isPresent()) {
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
			if (result.isPresent()) {
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
		if (result.isPresent()) {
			return result;
		}

		if (o0 instanceof ISymbol) {
			if (o1 instanceof ISymbol) {
				return e2SymArg((ISymbol) o0, (ISymbol) o1);
			}
		}

		if (o0 instanceof IAST) {
			IAST a0 = (IAST) o0;
			if (o1 instanceof IInteger) {
				return eFunIntArg(a0, (IInteger) o1);
			}
			if (o1 instanceof IAST) {
				return e2FunArg(a0, (IAST) o1);
			}
		}

		return F.NIL;
	}

	@Override
	public abstract IExpr e2IntArg(final IInteger i0, final IInteger i1);
}
