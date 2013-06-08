package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * UtilityFunction constructors from the <a
 * href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 * TODO a lot of functions are only placeholders at the moment.
 * 
 */
public class UtilityFunctionCtors {

	public final static String INTEGRATE_PREFIX = "Integrate::";
	public static ISymbol INTEGRATE_TRIG_SIMPLIFY = null;
	public static ISymbol INTEGRATE_SMARTLEAFCOUNT = null;
	public static ISymbol INTEGRATE_SMARTNUMERATOR = null;
	public static ISymbol INTEGRATE_SMARTDENOMINATOR = null;
	public static ISymbol INTEGRATE_SIMP = null;
	public static ISymbol INTEGRATE_REAPLIST = null;

	private static ISymbol $sDBG(final String symbolName) {
		return $s(symbolName, false);
		// ISymbol sym = $s(symbolName, false);
		// sym.setAttributes(ISymbol.CONSOLE_OUTPUT);
		// return sym;
	}

	public static IAST AlgebraicFunctionQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "AlgebraicFunctionQ"), a0, a1);
	}

	public static IAST BinomialQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "BinomialQ"), a0, a1);
	}

	public static IAST BinomialTest(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "BinomialTest"), a0, a1);
	}

	public static IAST CalculusFreeQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "CalculusFreeQ"), a0, a1);
	}

	public static IAST CalculusQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CalculusQ"), a0);
	}

	// public static IAST ClearDownValues(final IExpr a0) {
	// return unary($sDBG(INTEGRATE_PREFIX + "ClearDownValues"), a0);
	// }

	public static IAST CommonFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CommonFactors"), a0);
	}

	public static IAST CommonNumericFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CommonNumericFactors"), a0);
	}

	public static IAST ComplexFreeQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ComplexFreeQ"), a0);
	}

	public static IAST ConstantFactor(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ConstantFactor"), a0, a1);
	}

	public static IAST ContainsQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "ContainsQ"), a0, a1, a2);
	}

	public static IAST ContentFactor(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ContentFactor"), a0);
	}

	public static IAST CosQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CosQ"), a0);
	}

	public static IAST CoshQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CoshQ"), a0);
	}

	public static IAST CotQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CotQ"), a0);
	}

	public static IAST CothQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CothQ"), a0);
	}

	public static IAST CscQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CscQ"), a0);
	}

	public static IAST CschQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "CschQ"), a0);
	}

	public static IAST DerivativeDivides(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "DerivativeDivides"), a0, a1, a2);
	}

	public static IAST Dist(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "Dist"), a0, a1);
	}

	public static IAST DivideDegreesOfFactors(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "DivideDegreesOfFactors"), a0, a1);
	}

	public static IAST EasyDQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "EasyDQ"), a0, a1);
	}

	public static IAST EvenQuotientQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "EvenQuotientQ"), a0, a1);
	}

	public static IAST ExpQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ExpQ"), a0);
	}

	public static IAST ExpandExpression(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpandExpression"), a0, a1);
	}

	public static IAST ExpandExpressionAux(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpandExpressionAux"), a0, a1);
	}

	public static IAST ExpandImproperFraction(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpandImproperFraction"), a0, a1);
	}

	public static IAST ExpandImproperFraction(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "ExpandImproperFraction"), a0, a1, a2);
	}

	public static IAST ExpandIntegrandQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "ExpandIntegrandQ"), a0, a1, a2);
	}

	public static IAST ExpandPolynomial(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpandPolynomial"), a0, a1);
	}

	public static IAST ExpandTrigExpression(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "ExpandTrigExpression"), a0, a1, a2);
	}

	public static IAST ExpandTrigExpressionAux(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpandTrigExpressionAux"), a0, a1);
	}

	public static IAST ExpnExpand(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpnExpand"), a0, a1);
	}

	public static IAST ExpnExpandAux(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "ExpnExpandAux"), a0, a1);
	}

	public static IAST FactorOrder(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FactorOrder"), a0, a1);
	}

	public static IAST FalseQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "FalseQ"), a0);
	}

	public static IAST FindKernel(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FindKernel"), a0, a1);
	}

	public static IAST FindTrigFactor(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "FindTrigFactor"), a0, a1, a2, a3, a4);
	}

	public static IAST FractionOrNegativeQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "FractionOrNegativeQ"), a0);
	}

	public static IAST FractionQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "FractionQ"), a0);
	}

	public static IAST FractionalPowerFreeQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "FractionalPowerFreeQ"), a0);
	}

	public static IAST FractionalPowerOfLinear(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FractionalPowerOfLinear"), a0, a1, a2, a3);
	}

	public static IAST FractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FractionalPowerOfQuotientOfLinears"), a0, a1, a2, a3);
	}

	public static IAST FractionalPowerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "FractionalPowerQ"), a0);
	}

	public static IAST FunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfCosQ"), a0, a1, a2);
	}

	public static IAST FunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfCoshQ"), a0, a1, a2);
	}

	public static IAST FunctionOfDensePolynomialsQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfDensePolynomialsQ"), a0, a1);
	}

	public static IAST FunctionOfExpnQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfExpnQ"), a0, a1, a2);
	}

	public static IAST FunctionOfExponentialOfLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfExponentialOfLinear"), a0, a1);
	}

	public static IAST FunctionOfExponentialOfLinear(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "FunctionOfExponentialOfLinear"), a0, a1, a2, a3, a4);
	}

	public static IAST FunctionOfExponentialOfLinearAux(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4, final IExpr a5) {
		return senary($sDBG(INTEGRATE_PREFIX + "FunctionOfExponentialOfLinearAux"), a0, a1, a2, a3, a4, a5);
	}

	public static IAST FunctionOfExponentialOfLinearSubst(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "FunctionOfExponentialOfLinearSubst"), a0, a1, a2, a3, a4);
	}

	public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfHyperbolic"), a0, a1);
	}

	public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfHyperbolic"), a0, a1, a2);
	}

	public static IAST FunctionOfHyperbolicQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfHyperbolicQ"), a0, a1, a2);
	}

	public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfInverseLinear"), a0, a1);
	}

	public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfInverseLinear"), a0, a1, a2);
	}

	public static IAST FunctionOfKernelQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfKernelQ"), a0, a1, a2);
	}

	public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfLinear"), a0, a1);
	}

	public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "FunctionOfLinear"), a0, a1, a2, a3, a4);
	}

	public static IAST FunctionOfLinearSubst(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FunctionOfLinearSubst"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfLog(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfLog"), a0, a1);
	}

	public static IAST FunctionOfLog(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FunctionOfLog"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfPowerQ(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FunctionOfPowerQ"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfProductLog(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfProductLog"), a0, a1);
	}

	public static IAST FunctionOfProductLog(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FunctionOfProductLog"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "FunctionOfQ"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfSinQ"), a0, a1, a2);
	}

	public static IAST FunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfSinhQ"), a0, a1, a2);
	}

	public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic"), a0, a1);
	}

	public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic"), a0, a1, a2);
	}

	public static IAST FunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTanQ"), a0, a1, a2);
	}

	public static IAST FunctionOfTanWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTanWeight"), a0, a1, a2);
	}

	public static IAST FunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTanhQ"), a0, a1, a2);
	}

	public static IAST FunctionOfTanhWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTanhWeight"), a0, a1, a2);
	}

	public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "FunctionOfTrig"), a0, a1);
	}

	public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTrig"), a0, a1, a2);
	}

	public static IAST FunctionOfTrigQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "FunctionOfTrigQ"), a0, a1, a2);
	}

	public static IAST GE(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "GE"), a0, a1);
	}

	public static IAST GE(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "GE"), a0, a1, a2);
	}

	public static IAST GT(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "GT"), a0, a1);
	}

	public static IAST GT(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "GT"), a0, a1, a2);
	}

	public static IAST GoodExpansionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "GoodExpansionQ"), a0, a1, a2);
	}

	public static IAST HalfIntegerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "HalfIntegerQ"), a0);
	}

	public static IAST HyperbolicQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "HyperbolicQ"), a0);
	}

	public static IAST ImaginaryNumericQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ImaginaryNumericQ"), a0);
	}

	public static IAST ImaginaryQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ImaginaryQ"), a0);
	}

	public static IAST ImproperRationalFunctionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "ImproperRationalFunctionQ"), a0, a1, a2);
	}

	public static IAST IndentedPrint(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "IndentedPrint"), a0, a1);
	}

	public static IAST IndependentQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "IndependentQ"), a0, a1);
	}

	public static IAST IntegerPowerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "IntegerPowerQ"), a0);
	}

	public static IAST IntegerQuotientQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "IntegerQuotientQ"), a0, a1);
	}

	public static IAST InverseFunctionFreeQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "InverseFunctionFreeQ"), a0, a1);
	}

	public static IAST InverseFunctionOfLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "InverseFunctionOfLinear"), a0, a1);
	}

	public static IAST InverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "InverseFunctionOfQuotientOfLinears"), a0, a1);
	}

	public static IAST InverseFunctionQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "InverseFunctionQ"), a0);
	}

	public static IAST InverseHyperbolicQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "InverseHyperbolicQ"), a0);
	}

	public static IAST InverseTrigQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "InverseTrigQ"), a0);
	}

	public static IAST LE(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "LE"), a0, a1);
	}

	public static IAST LE(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "LE"), a0, a1, a2);
	}

	public static IAST LT(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "LT"), a0, a1);
	}

	public static IAST LT(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "LT"), a0, a1, a2);
	}

	public static IAST LeadBase(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "LeadBase"), a0);
	}

	public static IAST LeadDegree(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "LeadDegree"), a0);
	}

	public static IAST LeadFactor(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "LeadFactor"), a0);
	}

	public static IAST LeadTerm(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "LeadTerm"), a0);
	}

	public static IAST LinearQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "LinearQ"), a0, a1);
	}

	public static IAST LogQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "LogQ"), a0);
	}

	public static IAST MakeList(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MakeList"), a0, a1);
	}

	public static IAST Map2(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "Map2"), a0, a1, a2);
	}

	public static IAST MapAnd(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MapAnd"), a0, a1);
	}

	public static IAST MapAnd(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "MapAnd"), a0, a1, a2);
	}

	public static IAST MapOr(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MapOr"), a0, a1);
	}

	public static IAST MinimumDegree(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MinimumDegree"), a0, a1);
	}

	public static IAST Mods(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "Mods"), a0);
	}

	public static IAST MonomialFactor(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MonomialFactor"), a0, a1);
	}

	public static IAST MonomialQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MonomialQ"), a0, a1);
	}

	public static IAST MonomialSumQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "MonomialSumQ"), a0, a1);
	}

	public static IAST MostMainFactorPosition(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "MostMainFactorPosition"), a0);
	}

	// public static IAST MoveDownValues(final IExpr a0, final IExpr a1) {
	// return binary($sDBG(INTEGRATE_PREFIX + "MoveDownValues"), a0, a1);
	// }

	public static IAST NegQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NegQ"), a0);
	}

	public static IAST NegativeCoefficientQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NegativeCoefficientQ"), a0);
	}

	public static IAST NegativeOrZeroQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NegativeOrZeroQ"), a0);
	}

	public static IAST NegativeQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NegativeQ"), a0);
	}

	public static IAST NonnumericFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NonnumericFactors"), a0);
	}

	public static IAST NonpolynomialTerms(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "NonpolynomialTerms"), a0, a1);
	}

	public static IAST NonpositiveFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NonpositiveFactors"), a0);
	}

	public static IAST NonsumQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NonsumQ"), a0);
	}

	public static IAST NonzeroQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NonzeroQ"), a0);
	}

	public static IAST NormalForm(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "NormalForm"), a0, a1);
	}

	public static IAST NotFalseQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NotFalseQ"), a0);
	}

	public static IAST NotIntegrableQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "NotIntegrableQ"), a0, a1);
	}

	public static IAST NumericFactor(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "NumericFactor"), a0);
	}

	public static IAST OddHyperbolicPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "OddHyperbolicPowerQ"), a0, a1, a2);
	}

	public static IAST OddQuotientQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "OddQuotientQ"), a0, a1);
	}

	public static IAST OddTrigPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "OddTrigPowerQ"), a0, a1, a2);
	}

	public static IAST PerfectPowerTest(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "PerfectPowerTest"), a0, a1);
	}

	public static IAST PolynomialDivide(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PolynomialDivide"), a0, a1, a2);
	}

	public static IAST PolynomialFunctionOf(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "PolynomialFunctionOf"), a0, a1);
	}

	public static IAST PolynomialTermQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "PolynomialTermQ"), a0, a1);
	}

	public static IAST PolynomialTerms(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "PolynomialTerms"), a0, a1);
	}

	public static IAST PosQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PosQ"), a0);
	}

	public static IAST PositiveFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PositiveFactors"), a0);
	}

	public static IAST PositiveIntegerPowerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PositiveIntegerPowerQ"), a0);
	}

	public static IAST PositiveOrZeroQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PositiveOrZeroQ"), a0);
	}

	public static IAST PositiveQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PositiveQ"), a0);
	}

	public static IAST PowerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "PowerQ"), a0);
	}

	public static IAST PowerVariableDegree(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "PowerVariableDegree"), a0, a1, a2, a3);
	}

	public static IAST PowerVariableExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PowerVariableExpn"), a0, a1, a2);
	}

	public static IAST PowerVariableSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PowerVariableSubst"), a0, a1, a2);
	}

	public static IAST ProductLogQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ProductLogQ"), a0);
	}

	public static IAST ProductQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ProductQ"), a0);
	}

	public static IAST PureFunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfCosQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfCoshQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCotQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfCotQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCothQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfCothQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfSinQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfSinhQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfTanQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "PureFunctionOfTanhQ"), a0, a1, a2);
	}

	public static IAST QuadraticPolynomialQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "QuadraticPolynomialQ"), a0, a1);
	}

	public static IAST QuadraticQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "QuadraticQ"), a0, a1);
	}

	public static IAST QuotientOfLinearsParts(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "QuotientOfLinearsParts"), a0, a1);
	}

	public static IAST QuotientOfLinearsQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "QuotientOfLinearsQ"), a0, a1);
	}

	public static IAST RationalFunctionExponents(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "RationalFunctionExponents"), a0, a1);
	}

	public static IAST RationalFunctionQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "RationalFunctionQ"), a0, a1);
	}

	public static IAST RationalPowerQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RationalPowerQ"), a0);
	}

	public static IAST RationalQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RationalQ"), a0);
	}

	public static IAST RealNumericQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RealNumericQ"), a0);
	}

	public static IAST RealQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RealQ"), a0);
	}

	public static IAST ReapList(final IExpr a0) {
		if (INTEGRATE_REAPLIST == null) {
			INTEGRATE_REAPLIST = $sDBG(INTEGRATE_PREFIX + "ReapList");
			INTEGRATE_REAPLIST.setAttributes(ISymbol.HOLDFIRST);
		}
		return unary(INTEGRATE_REAPLIST, a0);
	}

	public static IAST RecognizedFormQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "RecognizedFormQ"), a0, a1);
	}

	public static IAST Regularize(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "Regularize"), a0, a1);
	}

	public static IAST RegularizeSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "RegularizeSubst"), a0, a1, a2);
	}

	public static IAST RegularizeTerm(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "RegularizeTerm"), a0, a1);
	}

	public static IAST RemainingFactors(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RemainingFactors"), a0);
	}

	public static IAST RemainingTerms(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "RemainingTerms"), a0);
	}

	public static IAST Rt(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "Rt"), a0, a1);
	}

	public static IAST SecQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SecQ"), a0);
	}

	public static IAST SechQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SechQ"), a0);
	}

	public static IAST Second(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "Second"), a0);
	}

	// public static IAST SetDownValues(final IExpr a0, final IExpr a1) {
	// return binary($sDBG(INTEGRATE_PREFIX + "SetDownValues"), a0, a1);
	// }

	public static IAST Simp(final IExpr a0) {
		if (INTEGRATE_SIMP == null) {
			INTEGRATE_SIMP = $sDBG(INTEGRATE_PREFIX + "Simp");
		}
		return unary(INTEGRATE_SIMP, a0);
	}

	public static IAST SimpAux(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SimpAux"), a0);
	}

	public static IAST SimpProduct(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SimpProduct"), a0, a1);
	}

	public static IAST SimpSum(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SimpSum"), a0, a1);
	}

	public static IAST SimplerExpressionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SimplerExpressionQ"), a0, a1, a2);
	}

	public static IAST SimplerRationalFunctionQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SimplerRationalFunctionQ"), a0, a1);
	}

	public static IAST SimplifyExpression(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SimplifyExpression"), a0, a1);
	}

	public static IAST SinCosQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SinCosQ"), a0);
	}

	public static IAST SinQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SinQ"), a0);
	}

	public static IAST SinhCoshQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SinhCoshQ"), a0);
	}

	public static IAST SinhQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SinhQ"), a0);
	}

	public static IAST Smallest(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "Smallest"), a0);
	}

	public static IAST Smallest(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "Smallest"), a0, a1);
	}

	public static IAST SmartDenominator(final IExpr a0) {
		if (INTEGRATE_SMARTDENOMINATOR == null) {
			INTEGRATE_SMARTDENOMINATOR = $sDBG(INTEGRATE_PREFIX + "SmartDenominator");
		}
		return unary(INTEGRATE_SMARTDENOMINATOR, a0);
	}

	public static IAST SmartLeafCount(final IExpr a0) {
		if (INTEGRATE_SMARTLEAFCOUNT == null) {
			INTEGRATE_SMARTLEAFCOUNT = $sDBG(INTEGRATE_PREFIX + "SmartLeafCount");
		}
		return unary(INTEGRATE_SMARTLEAFCOUNT, a0);
	}

	public static IAST SmartNumerator(final IExpr a0) {
		if (INTEGRATE_SMARTNUMERATOR == null) {
			INTEGRATE_SMARTNUMERATOR = $sDBG(INTEGRATE_PREFIX + "SmartNumerator");
		}
		return unary(INTEGRATE_SMARTNUMERATOR, a0);
	}

	public static IAST SmartTrigExpand(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SmartTrigExpand"), a0, a1);
	}

	public static IAST SplitFactorsOfTerms(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SplitFactorsOfTerms"), a0, a1);
	}

	public static IAST SplitFreeFactors(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SplitFreeFactors"), a0, a1);
	}

	public static IAST SplitFreeTerms(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SplitFreeTerms"), a0, a1);
	}

	public static IAST SplitMonomialTerms(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SplitMonomialTerms"), a0, a1);
	}

	public static IAST SqrtNumberQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SqrtNumberQ"), a0);
	}

	public static IAST SqrtNumberSumQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SqrtNumberSumQ"), a0);
	}

	public static IAST SqrtQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SqrtQ"), a0);
	}

	public static IAST SquareRootOfQuadraticSubst(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "SquareRootOfQuadraticSubst"), a0, a1, a2, a3);
	}

	public static IAST Subst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "Subst"), a0, a1, a2);
	}

	public static IAST SubstFor(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SubstFor"), a0, a1, a2);
	}

	public static IAST SubstForExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SubstForExpn"), a0, a1, a2);
	}

	public static IAST SubstForFractionalPower(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "SubstForFractionalPower"), a0, a1, a2, a3, a4);
	}

	public static IAST SubstForFractionalPowerAuxQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SubstForFractionalPowerAuxQ"), a0, a1, a2);
	}

	public static IAST SubstForFractionalPowerOfLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SubstForFractionalPowerOfLinear"), a0, a1);
	}

	public static IAST SubstForFractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SubstForFractionalPowerOfQuotientOfLinears"), a0, a1);
	}

	public static IAST SubstForFractionalPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SubstForFractionalPowerQ"), a0, a1, a2);
	}

	public static IAST SubstForHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "SubstForHyperbolic"), a0, a1, a2, a3, a4);
	}

	public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG(INTEGRATE_PREFIX + "SubstForInverseFunction"), a0, a1, a2);
	}

	public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "SubstForInverseFunction"), a0, a1, a2, a3);
	}

	public static IAST SubstForInverseFunctionOfLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SubstForInverseFunctionOfLinear"), a0, a1);
	}

	public static IAST SubstForInverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SubstForInverseFunctionOfQuotientOfLinears"), a0, a1);
	}

	public static IAST SubstForInverseLinear(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "SubstForInverseLinear"), a0, a1);
	}

	public static IAST SubstForPower(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary($sDBG(INTEGRATE_PREFIX + "SubstForPower"), a0, a1, a2, a3);
	}

	public static IAST SubstForTrig(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary($sDBG(INTEGRATE_PREFIX + "SubstForTrig"), a0, a1, a2, a3, a4);
	}

	public static IAST SubstQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SubstQ"), a0);
	}

	public static IAST SumFreeQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SumFreeQ"), a0);
	}

	public static IAST SumQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "SumQ"), a0);
	}

	public static IAST TanQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "TanQ"), a0);
	}

	public static IAST TanhQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "TanhQ"), a0);
	}

	public static IAST TrigHyperbolicFreeQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG(INTEGRATE_PREFIX + "TrigHyperbolicFreeQ"), a0, a1);
	}

	public static IAST TrigQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "TrigQ"), a0);
	}

	public static IAST TrigSimplify(final IExpr a0) {
		if (INTEGRATE_TRIG_SIMPLIFY == null) {
			INTEGRATE_TRIG_SIMPLIFY = $sDBG(INTEGRATE_PREFIX + "TrigSimplify");
		}
		return unary(INTEGRATE_TRIG_SIMPLIFY, a0);
	}

	public static IAST TrigSimplifyAux(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "TrigSimplifyAux"), a0);
	}

	public static IAST TryTrigReduceQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "TryTrigReduceQ"), a0);
	}

	public static IAST ZeroQ(final IExpr a0) {
		return unary($sDBG(INTEGRATE_PREFIX + "ZeroQ"), a0);
	}

}