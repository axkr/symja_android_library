package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.IPartialFractionGenerator;
import org.matheclipse.core.polynomials.PartialFractionGenerator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;

/**
 * Evaluate the partial fraction decomposition of a univariate polynomial
 * fraction.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Partial_fraction">Wikipedia -
 * Partial fraction decomposition</a>
 */
public class Apart extends AbstractFunctionEvaluator {
	public static final Apart CONST = new Apart();

	/**
	 * Split the expression into numerator and denominator parts, by calling the
	 * <code>Numerator[]</code> and <code>Denominator[]</code> functions
	 * 
	 * @param ast
	 * @return an array with the numerator, denominator and the evaluated
	 *         <code>Together[expr]</code>.
	 */
	public static IExpr[] getNumeratorDenominator(IAST ast) {
		IExpr[] result = new IExpr[3];
		result[2] = Together.together(ast);
		// split expr into numerator and denominator
		result[1] = F.eval(F.Denominator(result[2]));
		if (!result[1].isOne()) {
			// search roots for the numerator expression
			result[0] = F.eval(F.Numerator(result[2]));
		} else {
			result[0] = result[2];
		}
		return result;
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating
	 * positive and negative powers.
	 * 
	 * @param arg
	 * @param trig
	 *            determine the denominator by splitting up functions like
	 *            <code>Tan[9,Cot[], Csc[],...</code>
	 * @return the numerator and denominator expression or <code>null</code> if
	 *         no denominator was found.
	 */
	public static IExpr[] getFractionalParts(final IExpr arg, boolean trig) {
		IExpr[] parts = null;
		if (arg.isTimes()) {
			parts = Apart.getFractionalPartsTimes((IAST) arg, false, true, trig, true);
		} else if (arg.isPower()) {
			parts = getFractionalPartsPower((IAST) arg, trig);
			if (parts == null) {
				return null;
			}
		} else {
			if (arg.isAST()) {
				IExpr numerForm = Numerator.getTrigForm((IAST) arg, trig);
				if (numerForm.isPresent()) {
					IExpr denomForm = Denominator.getTrigForm((IAST) arg, trig);
					if (denomForm.isPresent()) {
						parts = new IExpr[2];
						parts[0] = numerForm;
						parts[1] = denomForm;
						return parts;
					}
				}
			}
		}
		return parts;
	}

	/**
	 * Return the denominator for the given <code>Power[...]</code> AST, by
	 * separating positive and negative powers.
	 * 
	 * @param powerAST
	 *            a power expression (a^b)
	 * @param trig
	 *            TODO
	 * @return the numerator and denominator expression
	 */
	public static IExpr[] getFractionalPartsPower(final IAST powerAST, boolean trig) {
		IExpr[] parts = new IExpr[2];
		parts[0] = F.C1;
		IExpr arg2 = powerAST.arg2();
		if (arg2.isSignedNumber()) {
			ISignedNumber sn = (ISignedNumber) arg2;
			if (sn.isMinusOne()) {
				parts[1] = powerAST.arg1();
				return parts;
			} else if (sn.isNegative()) {
				parts[1] = F.Power(powerAST.arg1(), sn.negate());
				return parts;
			} else {
				if (sn.isInteger() && powerAST.arg1().isAST()) {
					// positive integer
					IAST function = (IAST) powerAST.arg1();
					IExpr numerForm = Numerator.getTrigForm(function, trig);
					if (numerForm.isPresent()) {
						IExpr denomForm = Denominator.getTrigForm(function, trig);
						if (denomForm.isPresent()) {
							parts[0] = F.Power(numerForm, sn);
							parts[1] = F.Power(denomForm, sn);
							return parts;
						}
					}
				}
			}
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
		if (negExpr.isPresent()) {
			parts[1] = F.Power(powerAST.arg1(), negExpr);
			return parts;
		}
		return null;
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating
	 * positive and negative powers.
	 * 
	 * @param arg
	 * @return the numerator and denominator expression
	 */
	public static IExpr[] getFractionalPartsRational(final IExpr arg) {
		if (arg.isFraction()) {
			IFraction fr = (IFraction) arg;
			IExpr[] parts = new IExpr[2];
			parts[0] = fr.getNumerator();
			parts[1] = fr.getDenominator();
			return parts;
		}
		return getFractionalParts(arg, false);
	}

	/**
	 * Return the numerator and denominator for the given
	 * <code>Times[...]</code> AST, by separating positive and negative powers.
	 * 
	 * @param timesAST
	 *            a times expression (a*b*c....)
	 * @param splitNumeratorOne
	 *            split a fractional number into numerator and denominator, only
	 *            if the numerator is 1, if <code>true</code>, ignore
	 *            <code>splitFractionalNumbers</code> parameter.
	 * @param splitFractionalNumbers
	 *            split a fractional number into numerator and denominator
	 * @param trig
	 *            try to find a trigonometric numerator/denominator form
	 *            (Example: Csc[x] gives 1 / Sin[x])
	 * @param evalParts
	 *            evaluate the determined parts
	 * @return the numerator and denominator expression and an optional
	 *         fractional number (maybe <code>null</code>), if splitNumeratorOne
	 *         is <code>true</code>.
	 */
	public static IExpr[] getFractionalPartsTimes(final IAST timesAST, boolean splitNumeratorOne,
			boolean splitFractionalNumbers, boolean trig, boolean evalParts) {
		IExpr[] result = new IExpr[3];
		result[2] = null;
		IAST numerator = F.Times(); // new TimesOp(timesAST.size());
		IAST denominator = F.Times();// new TimesOp(timesAST.size());
		IExpr arg;
		IAST argAST;
		boolean evaled = false;
		boolean splitFractionEvaled = false;
		for (int i = 1; i < timesAST.size(); i++) {
			arg = timesAST.get(i);
			if (arg.isAST()) {
				argAST = (IAST) arg;
				if (trig && argAST.isAST1()) {
					IExpr numerForm = Numerator.getTrigForm(argAST, trig);
					if (numerForm.isPresent()) {
						IExpr denomForm = Denominator.getTrigForm(argAST, trig);
						if (denomForm.isPresent()) {
							if (!numerForm.isOne()) {
								numerator.append(numerForm);// numerator.addMerge(numerForm);
							}
							if (!denomForm.isOne()) {
								denominator.append(denomForm);// denominator.addMerge(denomForm);
							}
							evaled = true;
							continue;
						}
					}
				} else if (arg.isPower()) {
					IExpr[] parts = getFractionalPartsPower((IAST) arg, trig);
					if (parts != null) {
						if (!parts[0].isOne()) {
							numerator.append(parts[0]); // numerator.addMerge(parts[0]);
						}
						if (!parts[1].isOne()) {
							denominator.append(parts[1]);// denominator.addMerge(parts[1]);
						}
						evaled = true;
						continue;
					}
				}
			} else if (i == 1 && arg.isFraction()) {
				if (splitNumeratorOne) {
					IFraction fr = (IFraction) arg;
					if (fr.getNumerator().isOne()) {
						denominator.append(fr.getDenominator()); // denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					if (fr.getNumerator().isMinusOne()) {
						numerator.append(fr.getNumerator()); // numerator.addMerge(fr.getNumerator());
						denominator.append(fr.getDenominator());// denominator.addMerge(fr.getDenominator());
						splitFractionEvaled = true;
						continue;
					}
					result[2] = fr;
					continue;
				} else if (splitFractionalNumbers) {
					IFraction fr = (IFraction) arg;
					if (!fr.getNumerator().isOne()) {
						numerator.append(fr.getNumerator()); // numerator.addMerge(fr.getNumerator());
					}
					denominator.append(fr.getDenominator()); // denominator.addMerge(fr.getDenominator());
					evaled = true;
					continue;
				}
			}
			numerator.append(arg); // numerator.addMerge(arg);
		}
		if (evaled) {
			// result[0] = numerator.getProduct();
			// result[1] = denominator.getProduct();
			if (evalParts) {
				result[0] = F.eval(numerator);
				result[1] = F.eval(denominator);
			} else {
				result[0] = numerator.getOneIdentity(F.C1);
				result[1] = denominator.getOneIdentity(F.C1);
			}
			if (result[0].isNegative() && result[1].isPlus() && ((IAST) result[1]).isAST2()) {
				// negate numerator and denominator:
				result[0] = result[0].negate();
				result[1] = result[1].negate();
			}
			return result;
		}
		if (splitFractionEvaled) {
			result[0] = numerator.getOneIdentity(F.C1);// numerator.getProduct();
			if (!result[0].isTimes() && !result[0].isPlus()) {
				result[1] = denominator.getOneIdentity(F.C1); // denominator.getProduct();
				return result;
			}
			if (result[0].isTimes() && ((IAST) result[0]).isAST2() && ((IAST) result[0]).arg1().isMinusOne()) {
				result[1] = denominator.getOneIdentity(F.C1); // denominator.getProduct();
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial
	 * fraction decomposition of the numerator and denominator parts.
	 * 
	 * @deprecated untested at the moment
	 * @param parts
	 * @param variableList
	 * @return <code>null</code> if the partial fraction decomposition wasn't
	 *         constructed
	 */
	@Deprecated
	private static IAST partialFractionDecompositionInteger(IExpr[] parts, IAST variableList) {
		try {
			IExpr exprNumerator = F.evalExpandAll(parts[0]);
			IExpr exprDenominator = F.evalExpandAll(parts[1]);
			ASTRange r = new ASTRange(variableList, 1);
			List<IExpr> varList = r.toList();

			String[] varListStr = new String[1];
			varListStr[0] = variableList.arg1().toString();
			JASConvert<BigInteger> jas = new JASConvert<BigInteger>(varList, BigInteger.ZERO);
			GenPolynomial<BigInteger> numerator = jas.expr2JAS(exprNumerator, false);
			GenPolynomial<BigInteger> denominator = jas.expr2JAS(exprDenominator, false);

			// get factors
			FactorAbstract<BigInteger> factorAbstract = FactorFactory.getImplementation(BigInteger.ZERO);
			SortedMap<GenPolynomial<BigInteger>, Long> sfactors = factorAbstract.baseFactors(denominator);

			List<GenPolynomial<BigInteger>> D = new ArrayList<GenPolynomial<BigInteger>>(sfactors.keySet());

			SquarefreeAbstract<BigInteger> sqf = SquarefreeFactory.getImplementation(BigInteger.ZERO);
			List<List<GenPolynomial<BigInteger>>> Ai = sqf.basePartialFraction(numerator, sfactors);
			// returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
			// A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

			if (Ai.size() > 0) {
				IAST result = F.Plus();
				IExpr temp;
				if (!Ai.get(0).get(0).isZERO()) {
					temp = F.eval(jas.integerPoly2Expr(Ai.get(0).get(0)));
					if (temp.isAST()) {
						((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
					}
					result.append(temp);
				}
				for (int i = 1; i < Ai.size(); i++) {
					List<GenPolynomial<BigInteger>> list = Ai.get(i);
					long j = 0L;
					for (GenPolynomial<BigInteger> genPolynomial : list) {
						if (!genPolynomial.isZERO()) {
							temp = F.eval(F.Times(jas.integerPoly2Expr(genPolynomial),
									F.Power(jas.integerPoly2Expr(D.get(i - 1)), F.integer(j * (-1L)))));
							if (!temp.isZero()) {
								if (temp.isAST()) {
									((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
								}
								result.append(temp);
							}
						}
						j++;
					}

				}
				return result;
			}
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial
	 * fraction decomposition of the numerator and denominator parts.
	 * 
	 * @param pf
	 *            partial fraction generator
	 * @param parts
	 * @param variable
	 *            a variable
	 * @return <code>F.NIL</code> if the partial fraction decomposition wasn't
	 *         constructed
	 */
	public static IExpr partialFractionDecompositionRational(IPartialFractionGenerator pf, IExpr[] parts,
			ISymbol variable) {
		try {
			IAST variableList = F.List(variable);
			IExpr exprNumerator = F.evalExpandAll(parts[0]);
			IExpr exprDenominator = F.evalExpandAll(parts[1]);
			ASTRange r = new ASTRange(variableList, 1);
			List<IExpr> varList = r.toList();

			String[] varListStr = new String[1];
			varListStr[0] = variableList.arg1().toString();
			JASConvert<BigRational> jas = new JASConvert<BigRational>(varList, BigRational.ZERO);
			GenPolynomial<BigRational> numerator = jas.expr2JAS(exprNumerator, false);
			GenPolynomial<BigRational> denominator = jas.expr2JAS(exprDenominator, false);

			// get factors
			FactorAbstract<BigRational> factorAbstract = FactorFactory.getImplementation(BigRational.ZERO);
			SortedMap<GenPolynomial<BigRational>, Long> sfactors = factorAbstract.baseFactors(denominator);

			List<GenPolynomial<BigRational>> D = new ArrayList<GenPolynomial<BigRational>>(sfactors.keySet());

			SquarefreeAbstract<BigRational> sqf = SquarefreeFactory.getImplementation(BigRational.ZERO);
			List<List<GenPolynomial<BigRational>>> Ai = sqf.basePartialFraction(numerator, sfactors);
			// returns [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) =
			// A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).

			if (Ai.size() > 0) {
				// IAST result = F.Plus();
				pf.setJAS(jas);
				if (!Ai.get(0).get(0).isZERO()) {
					pf.addNonFractionalPart(Ai.get(0).get(0));
				}
				for (int i = 1; i < Ai.size(); i++) {
					List<GenPolynomial<BigRational>> list = Ai.get(i);
					int j = 0;
					for (GenPolynomial<BigRational> genPolynomial : list) {
						if (!genPolynomial.isZERO()) {
							GenPolynomial<BigRational> Di_1 = D.get(i - 1);
							pf.addSinglePartialFraction(genPolynomial, Di_1, j);
						}
						j++;
					}

				}
				return pf.getResult();
			}
		} catch (JASConversionException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return F.NIL;
	}

	public Apart() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		final IExpr arg1 = ast.arg1();
		IAST temp = Thread.threadLogicEquationOperators(arg1, ast, 1);
		if (temp.isPresent()) {
			return temp;
		}

		IAST variableList = null;
		if (ast.isAST2()) {
			variableList = Validate.checkSymbolOrSymbolList(ast, 2);
		} else {
			VariablesSet eVar = new VariablesSet(arg1);
			if (eVar.isSize(0)) {
				return arg1;
			}
			if (!eVar.isSize(1)) {
				// partial fraction only possible for univariate polynomials
				return F.NIL;
			}
			variableList = eVar.getVarList();
		}

		if (arg1.isTimes() || arg1.isPower()) {
			IExpr[] parts = Apart.getFractionalParts(arg1, false);
			if (parts != null) {
				return partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
						(ISymbol) variableList.arg1());
			}
		} else {
			return arg1;
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(newSymbol);
	}
}