package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.convert.JASConvert;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
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
 * Evaluate the partial fraction decomposition of a univariate polynomial fraction.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Partial_fraction">Wikipedia - Partial fraction decomposition</a>
 */
public class Apart extends AbstractFunctionEvaluator {

	public Apart() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		IAST variableList = null;
		if (ast.size() == 3) {
			variableList = Validate.checkSymbolOrSymbolList(ast, 2);
		} else {
			VariablesSet eVar = new VariablesSet(ast.arg1());
			if (!eVar.isSize(1)) {
				// partial fraction only possible for univariate polynomials
				return null;
			}
			variableList = eVar.getVarList();
		}

		final IExpr arg = ast.arg1();
		if (arg.isTimes() || arg.isPower()) {
			IExpr[] parts = Apart.getFractionalParts(ast.arg1());
			if (parts != null) {
				IAST plusResult = partialFractionDecompositionRational(new PartialFractionGenerator(), parts,
						(ISymbol) variableList.arg1());
				if (plusResult != null) {
					return plusResult.getOneIdentity(F.C0);
				}
			}
		} else {
			return ast.arg1();
		}

		return null;
	}

	/**
	 * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the numerator and
	 * denominator parts.
	 * 
	 * @deprecated untested at the moment
	 * @param parts
	 * @param variableList
	 * @return <code>null</code> if the partial fraction decomposition wasn't constructed
	 */
	public static IAST partialFractionDecompositionInteger(IExpr[] parts, IAST variableList) {
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
					result.add(temp);
				}
				for (int i = 1; i < Ai.size(); i++) {
					List<GenPolynomial<BigInteger>> list = Ai.get(i);
					long j = 0L;
					for (GenPolynomial<BigInteger> genPolynomial : list) {
						if (!genPolynomial.isZERO()) {
							temp = F.eval(F.Times(jas.integerPoly2Expr(genPolynomial),
									F.Power(jas.integerPoly2Expr(D.get(i - 1)), F.integer(j * (-1L)))));
							if (!temp.equals(F.C0)) {
								if (temp.isAST()) {
									((IAST) temp).addEvalFlags(IAST.IS_DECOMPOSED_PARTIAL_FRACTION);
								}
								result.add(temp);
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
	 * Returns an AST with head <code>Plus</code>, which contains the partial fraction decomposition of the numerator and
	 * denominator parts.
	 * 
	 * @param parts
	 * @param variableList
	 * @return <code>null</code> if the partial fraction decomposition wasn't constructed
	 */
	public static IAST partialFractionDecompositionRational(IPartialFractionGenerator pf, IExpr[] parts, ISymbol x) {
		try {
			IAST variableList = F.List(x);
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
		return null;
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating positive and negative powers.
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
		return getFractionalParts(arg);
	}

	/**
	 * Split the expression into numerator and denominator parts, by separating positive and negative powers.
	 * 
	 * @param arg
	 * @return the numerator and denominator expression or <code>null</code> if no denominator was found.
	 */
	public static IExpr[] getFractionalParts(final IExpr arg) {
		IExpr[] parts = null;
		if (arg.isTimes()) {
			parts = Apart.getFractionalPartsTimes((IAST) arg, true);
		} else if (arg.isPower()) {
			parts = new IExpr[2];
			IExpr denom = getFractionalPartsPower((IAST) arg);
			if (denom == null) {
				return null;
			} else {
				parts[0] = F.C1;
				parts[1] = denom;
			}
		} else {
			if (arg.isAST()) {
				IAST denomForm = Denominator.getDenominatorForm((IAST) arg);
				if (denomForm != null) {
					parts = new IExpr[2];
					parts[0] = F.C1;
					parts[1] = denomForm;
					return parts;
				}
			}
		}
		return parts;
	}

	/**
	 * Return the numerator and denominator for the given <code>Times[...]</code> AST, by separating positive and negative powers.
	 * 
	 * @param timesAST
	 *            a times expression (a*b*c....)
	 * @param splitFractionalNumbers
	 *            TODO
	 * @return the numerator and denominator expression
	 */
	public static IExpr[] getFractionalPartsTimes(final IAST timesAST, boolean splitFractionalNumbers) {
		IExpr[] result = new IExpr[2];

		IAST numerator = F.Times();
		IAST denominator = F.Times();
		IExpr arg;
		IAST argAST;
		boolean evaled = false;
		for (int i = 1; i < timesAST.size(); i++) {
			arg = timesAST.get(i);
			if (arg.isAST()) {
				argAST = (IAST) arg;
				if (argAST.size() == 2) {
					IAST denomForm = Denominator.getDenominatorForm(argAST);
					if (denomForm != null) {
						denominator.add(denomForm);
						evaled = true;
						continue;
					}
				} else if (arg.isPower()) {
					IExpr denom = getFractionalPartsPower((IAST) arg);
					if (denom != null) {
						denominator.add(denom);
						evaled = true;
						continue;
					}
				}
			} else if (splitFractionalNumbers && arg.isFraction()) {
				IFraction fr = (IFraction) arg;
				numerator.add(fr.getNumerator());
				denominator.add(fr.getDenominator());
				evaled = true;
				// IInteger numer = ((IRational) arg).getNumerator();
				// if (!numer.equals(F.C1)) {
				// numerator.add(numer);
				// }
				// IInteger denom = ((IRational) arg).getDenominator();
				// if (!denom.equals(F.C1)) {
				// denominator.add(denom);
				// }
				continue;
			}
			numerator.add(arg);
		}
		if (evaled) {
			result[0] = numerator.getOneIdentity(F.C1);
			result[1] = denominator.getOneIdentity(F.C1);
			return result;
		}
		return null;
	}

	/**
	 * Return the denominator for the given <code>Power[...]</code> AST, by separating positive and negative powers.
	 * 
	 * @param powerAST
	 *            a power expression (a^b)
	 * @return the numerator and denominator expression
	 */
	public static IExpr getFractionalPartsPower(final IAST powerAST) {
		IExpr arg2 = powerAST.arg2();
		if (arg2.isSignedNumber()) {
			ISignedNumber sn = (ISignedNumber) arg2;
			if (sn.isMinusOne()) {
				return powerAST.arg1();
			} else if (sn.isNegative()) {
				return F.Power(powerAST.arg1(), sn.negate());
			} else {
				if (sn.isInteger() && powerAST.arg1().isAST()) {
					// positive integer
					IAST function = (IAST) powerAST.arg1();
					IAST denomForm = Denominator.getDenominatorForm(function);
					if (denomForm != null) {
						return F.Power(denomForm, sn);
					}
				}
			}
		}
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg2);
		if (negExpr != null) {
			return F.Power(powerAST.arg1(), negExpr);
		}
		return null;
	}
}