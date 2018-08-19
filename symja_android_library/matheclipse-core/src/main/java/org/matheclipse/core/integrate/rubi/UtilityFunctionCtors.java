package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.ast;
import static org.matheclipse.core.expression.F.binaryAST2;
import static org.matheclipse.core.expression.F.initFinalHiddenSymbol;
import static org.matheclipse.core.expression.F.quaternary;
import static org.matheclipse.core.expression.F.quinary;
import static org.matheclipse.core.expression.F.senary;
import static org.matheclipse.core.expression.F.ternaryAST3;
import static org.matheclipse.core.expression.F.unaryAST1;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * UtilityFunction constructors from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
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

	public final static ISymbol H = initFinalHiddenSymbol("H");
	public final static ISymbol J = initFinalHiddenSymbol("J");
	public final static ISymbol K = initFinalHiddenSymbol("K");
	public final static ISymbol L = initFinalHiddenSymbol("L");
	public final static ISymbol M = initFinalHiddenSymbol("M");
	public final static ISymbol O = initFinalHiddenSymbol("O");
	public final static ISymbol P = initFinalHiddenSymbol("P");
	public final static ISymbol Q = initFinalHiddenSymbol("Q");
	public final static ISymbol R = initFinalHiddenSymbol("R");
	public final static ISymbol S = initFinalHiddenSymbol("S");
	public final static ISymbol T = initFinalHiddenSymbol("T");
	public final static ISymbol U = initFinalHiddenSymbol("U");
	public final static ISymbol V = initFinalHiddenSymbol("V");
	public final static ISymbol W = initFinalHiddenSymbol("W");
	public final static ISymbol X = initFinalHiddenSymbol("X");
	public final static ISymbol Y = initFinalHiddenSymbol("Y");
	public final static ISymbol Z = initFinalHiddenSymbol("Z");

	public static ISymbol Dist = org.matheclipse.core.expression.F.$rubi(INTEGRATE_PREFIX + "Dist");
	public static ISymbol IntegerPowerQ = org.matheclipse.core.expression.F.$rubi(INTEGRATE_PREFIX + "IntegerPowerQ");
	public static ISymbol FractionalPowerQ = org.matheclipse.core.expression.F
			.$rubi(INTEGRATE_PREFIX + "FractionalPowerQ");

	public static ISymbol AbortRubi = org.matheclipse.core.expression.F.$rubi(INTEGRATE_PREFIX + "AbortRubi");
	public static ISymbol ReapList = org.matheclipse.core.expression.F.$rubi(INTEGRATE_PREFIX + "ReapList");

	static ISymbol FalseQ = F.$rubi(INTEGRATE_PREFIX + "FalseQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				return engine.evaluate(ast.arg1()).isFalse() ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol FractionQ = F.$rubi(INTEGRATE_PREFIX + "FractionQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.isFraction() ? F.True : F.False;
			}
			if (ast.size() > 2) {
				return ast.forAll(x -> engine.evaluate(x).isFraction(), 1) ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol IntegersQ = F.$rubi(INTEGRATE_PREFIX + "IntegersQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.isInteger() ? F.True : F.False;
			}
			if (ast.size() > 2) {
				return ast.forAll(x -> engine.evaluate(x).isInteger(), 1) ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol ComplexNumberQ = F.$rubi(INTEGRATE_PREFIX + "ComplexNumberQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.isComplex() || arg1.isComplexNumeric() ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol PowerQ = F.$rubi(INTEGRATE_PREFIX + "PowerQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.head().equals(F.Power) ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol ProductQ = F.$rubi(INTEGRATE_PREFIX + "ProductQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.head().equals(F.Times) ? F.True : F.False;
			}
			return F.False;
		}
	});

	static ISymbol SumQ = F.$rubi(INTEGRATE_PREFIX + "SumQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.head().equals(F.Plus) ? F.True : F.False;
			}
			return F.False;
		}
	});
	static ISymbol NonsumQ = F.$rubi(INTEGRATE_PREFIX + "NonsumQ", new AbstractCoreFunctionEvaluator() {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				return arg1.head().equals(F.Plus) ? F.False : F.True;
			}
			return F.False;
		}
	});

	public static IAST AbortRubi(final IExpr a0) {
		return F.headAST0(F.Abort);
	}

	public static IAST F(final IExpr a0) {
		return unaryAST1(F.FSymbol, a0);
	}

	public static IAST F(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.FSymbol, a0, a1);
	}

	public static IAST G(final IExpr a0) {
		return unaryAST1(F.GSymbol, a0);
	}

	public static IAST G(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.GSymbol, a0, a1);
	}

	public static IAST H(final IExpr a0) {
		return unaryAST1(H, a0);
	}

	public static IAST H(final IExpr a0, final IExpr a1) {
		return binaryAST2(H, a0, a1);
	}

	public static IAST H(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(H, a0, a1, a2, a3);
	}

	/**
	 * Convert to Integrate[]
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IAST Int(final IExpr a0, final IExpr a1) {
		// Integrate.setAttributes(ISymbol.CONSOLE_OUTPUT);
		return binaryAST2(Integrate, a0, a1);
	}

	public static IAST IntBinomialQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "IntBinomialQ"));
	}

	public static IAST IntHide(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IntHide"), a0, a1);
	}

	public static IAST IntLinearQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "IntLinearQ"));
	}

	public static IAST IntQuadraticQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "IntQuadraticQ"));
	}

	public static IAST Dist(final IExpr a0, final IExpr a1) {
		return binaryAST2(Dist, a0, a1);
	}

	public static IAST Dist(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Dist, a0, a1, a2);
	}

	// private static ISymbol $sDBG(final String symbolName) {
	// return $s(symbolName, false);
	// }

	public static IAST AbsorbMinusSign(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "AbsorbMinusSign"), a0);
	}

	public static IAST AbsurdNumberFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "AbsurdNumberFactors"), a0);
	}

	public static IAST AbsurdNumberGCD(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "AbsurdNumberGCD"));
	}

	public static IAST AbsurdNumberGCDList(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "AbsurdNumberGCDList"), a0, a1);
	}

	public static IAST AbsurdNumberQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "AbsurdNumberQ"), a0);
	}

	public static IAST ActivateTrig(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ActivateTrig"), a0);
	}

	public static IAST AlgebraicFunctionFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "AlgebraicFunctionFactors"), a0, a1);
	}

	public static IAST AlgebraicFunctionQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "AlgebraicFunctionQ"), a0, a1);
	}

	public static IAST AlgebraicFunctionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "AlgebraicFunctionQ"), a0, a1, a2);
	}

	public static IAST AlgebraicTrigFunctionQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "AlgebraicTrigFunctionQ"), a0, a1);
	}

	public static IAST AllNegTermQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "AllNegTermQ"), a0);
	}

	public static IAST AtomBaseQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "AtomBaseQ"), a0);
	}

	public static IAST BinomialDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "BinomialDegree"), a0, a1);
	}

	public static IAST BinomialMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "BinomialMatchQ"), a0, a1);
	}

	public static IAST BinomialParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "BinomialParts"), a0, a1);
	}

	public static IAST BinomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "BinomialQ"), a0, a1);
	}

	public static IAST BinomialQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "BinomialQ"), a0, a1, a2);
	}

	public static IAST BinomialTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "BinomialTest"), a0, a1);
	}

	public static IAST CalculusFreeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "CalculusFreeQ"), a0, a1);
	}

	public static IAST CalculusQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CalculusQ"), a0);
	}

	public static IAST CancelCommonFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "CancelCommonFactors"), a0, a1);
	}

	public static IAST CannotIntegrate(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "CannotIntegrate"), a0, a1);
	}

	public static IAST CollectReciprocals(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "CollectReciprocals"), a0, a1);
	}

	public static IAST Coeff(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Coeff"), a0, a1);
	}

	public static IAST Coeff(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "Coeff"), a0, a1, a2);
	}

	public static IAST CombineExponents(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CombineExponents"), a0);
	}

	public static IAST CommonFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CommonFactors"), a0);
	}

	public static IAST CommonNumericFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CommonNumericFactors"), a0);
	}

	public static IAST ComplexFreeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ComplexFreeQ"), a0);
	}

	public static IAST ComplexNumberQ(final IExpr a0) {
		return unaryAST1(ComplexNumberQ, a0);
	}

	public static IAST ConstantFactor(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ConstantFactor"), a0, a1);
	}

	public static IAST ContentFactor(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ContentFactor"), a0);
	}

	public static IAST ContentFactorAux(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ContentFactorAux"), a0);
	}

	public static IAST CosQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CosQ"), a0);
	}

	public static IAST CoshQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CoshQ"), a0);
	}

	public static IAST CotQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CotQ"), a0);
	}

	public static IAST CothQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CothQ"), a0);
	}

	public static IAST CscQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CscQ"), a0);
	}

	public static IAST CschQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "CschQ"), a0);
	}

	public static IAST CubicMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "CubicMatchQ"), a0, a1);
	}

	public static IAST DeactivateTrig(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "DeactivateTrig"), a0, a1);
	}

	public static IAST DeactivateTrigAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "DeactivateTrigAux"), a0, a1);
	}

	public static IAST DerivativeDivides(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "DerivativeDivides"), a0, a1, a2);
	}

	public static IAST Distrib(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Distrib"), a0, a1);
	}

	public static IAST DistributeDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "DistributeDegree"), a0, a1);
	}

	public static IAST DivideDegreesOfFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "DivideDegreesOfFactors"), a0, a1);
	}

	public static IAST Divides(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "Divides"), a0, a1, a2);
	}

	public static IAST EasyDQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "EasyDQ"), a0, a1);
	}

	public static IAST EqQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "EqQ"), a0);
	}

	public static IAST EqQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "EqQ"), a0, a1);
	}

	public static IAST EqQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "EqQ"), a0, a1, a2);
	}

	public static IAST EulerIntegrandQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "EulerIntegrandQ"), a0, a1);
	}

	public static IAST EveryQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "EveryQ"), a0, a1);
	}

	public static IAST EvenQuotientQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "EvenQuotientQ"), a0, a1);
	}

	public static IAST ExpQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ExpQ"), a0);
	}

	public static IAST ExpandAlgebraicFunction(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandAlgebraicFunction"), a0, a1);
	}

	public static IAST ExpandBinomial(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4,
			final IExpr a5) {
		return senary(F.$rubi(INTEGRATE_PREFIX + "ExpandBinomial"), a0, a1, a2, a3, a4, a5);
	}

	public static IAST ExpandCleanup(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandCleanup"), a0, a1);
	}

	public static IAST ExpandExpression(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandExpression"), a0, a1);
	}

	public static IAST ExpandIntegrand(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandIntegrand"), a0, a1);
	}

	public static IAST ExpandIntegrand(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExpandIntegrand"), a0, a1, a2);
	}

	public static IAST ExpandLinearProduct(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "ExpandLinearProduct"), a0, a1, a2, a3, a4);
	}

	public static IAST ExpandToSum(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandToSum"), a0, a1);
	}

	public static IAST ExpandToSum(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExpandToSum"), a0, a1, a2);
	}

	public static IAST ExpandTrig(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandTrig"), a0, a1);
	}

	public static IAST ExpandTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExpandTrig"), a0, a1, a2);
	}

	public static IAST ExpandTrigExpand(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4,
			final IExpr a5) {
		return senary(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigExpand"), a0, a1, a2, a3, a4, a5);
	}

	public static IAST ExpandTrigReduce(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigReduce"), a0, a1);
	}

	public static IAST ExpandTrigReduce(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigReduce"), a0, a1, a2);
	}

	public static IAST ExpandTrigReduceAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigReduceAux"), a0, a1);
	}

	public static IAST ExpandTrigToExp(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigToExp"), a0, a1);
	}

	public static IAST ExpandTrigToExp(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExpandTrigToExp"), a0, a1, a2);
	}

	public static IAST Expon(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Expon"), a0, a1);
	}

	public static IAST Expon(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "Expon"), a0, a1, a2);
	}

	public static IAST ExponentIn(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExponentIn"), a0, a1, a2);
	}

	public static IAST ExponentInAux(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ExponentInAux"), a0, a1, a2);
	}

	public static IAST FactorAbsurdNumber(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FactorAbsurdNumber"), a0);
	}

	public static IAST FactorNumericGcd(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FactorNumericGcd"), a0);
	}

	public static IAST FactorOrder(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FactorOrder"), a0, a1);
	}

	public static IAST FalseQ(final IExpr a0) {
		return unaryAST1(FalseQ, a0);
	}

	public static IAST FindTrigFactor(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "FindTrigFactor"), a0, a1, a2, a3, a4);
	}

	public static IAST FixInertTrigFunction(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FixInertTrigFunction"), a0, a1);
	}

	public static IAST FixIntRule(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FixIntRule"), a0);
	}

	public static IAST FixIntRule(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FixIntRule"), a0, a1);
	}

	public static IAST FixIntRules() {
		return F.headAST0(F.$rubi(INTEGRATE_PREFIX + "FixIntRules"));
	}

	public static IAST FixIntRules(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FixIntRules"), a0);
	}

	public static IAST FixRhsIntRule(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FixRhsIntRule"), a0, a1);
	}

	public static IAST FixSimplify(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FixSimplify"), a0);
	}

	public static IAST FracPart(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FracPart"), a0);
	}

	public static IAST FracPart(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FracPart"), a0, a1);
	}

	public static IAST FractionOrNegativeQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "FractionOrNegativeQ"));
	}

	public static IAST FractionQ(final IExpr... a) {
		return ast(a, FractionQ);
	}

	public static IAST FractionalPowerFreeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FractionalPowerFreeQ"), a0);
	}

	public static IAST FractionalPowerOfLinear(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "FractionalPowerOfLinear"), a0, a1, a2, a3);
	}

	public static IAST FractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1, final IExpr a2,
			final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "FractionalPowerOfQuotientOfLinears"), a0, a1, a2, a3);
	}

	public static IAST FractionalPowerOfSquareQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "FractionalPowerOfSquareQ"), a0);
	}

	public static IAST FractionalPowerQ(final IExpr a0) {
		return unaryAST1(FractionalPowerQ, a0);
	}

	public static IAST FractionalPowerSubexpressionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FractionalPowerSubexpressionQ"), a0, a1, a2);
	}

	public static IAST FreeFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FreeFactors"), a0, a1);
	}

	public static IAST FreeTerms(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FreeTerms"), a0, a1);
	}

	public static IAST FunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfCosQ"), a0, a1, a2);
	}

	public static IAST FunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfCoshQ"), a0, a1, a2);
	}

	public static IAST FunctionOfDensePolynomialsQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfDensePolynomialsQ"), a0, a1);
	}

	public static IAST FunctionOfExpnQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExpnQ"), a0, a1, a2);
	}

	public static IAST FunctionOfExponential(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponential"), a0, a1);
	}

	public static IAST FunctionOfExponentialFunction(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponentialFunction"), a0, a1);
	}

	public static IAST FunctionOfExponentialFunctionAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponentialFunctionAux"), a0, a1);
	}

	public static IAST FunctionOfExponentialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponentialQ"), a0, a1);
	}

	public static IAST FunctionOfExponentialTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponentialTest"), a0, a1);
	}

	public static IAST FunctionOfExponentialTestAux(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfExponentialTestAux"), a0, a1, a2);
	}

	public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfHyperbolic"), a0, a1);
	}

	public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfHyperbolic"), a0, a1, a2);
	}

	public static IAST FunctionOfHyperbolicQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfHyperbolicQ"), a0, a1, a2);
	}

	public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfInverseLinear"), a0, a1);
	}

	public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfInverseLinear"), a0, a1, a2);
	}

	public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfLinear"), a0, a1);
	}

	public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "FunctionOfLinear"), a0, a1, a2, a3, a4);
	}

	public static IAST FunctionOfLinearSubst(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "FunctionOfLinearSubst"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfLog(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfLog"), a0, a1);
	}

	public static IAST FunctionOfLog(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "FunctionOfLog"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfQ"), a0, a1, a2);
	}

	public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "FunctionOfQ"), a0, a1, a2, a3);
	}

	public static IAST FunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfSinQ"), a0, a1, a2);
	}

	public static IAST FunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfSinhQ"), a0, a1, a2);
	}

	public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic"), a0, a1);
	}

	public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic"), a0, a1, a2);
	}

	public static IAST FunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTanQ"), a0, a1, a2);
	}

	public static IAST FunctionOfTanWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTanWeight"), a0, a1, a2);
	}

	public static IAST FunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTanhQ"), a0, a1, a2);
	}

	public static IAST FunctionOfTanhWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTanhWeight"), a0, a1, a2);
	}

	public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTrig"), a0, a1);
	}

	public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTrig"), a0, a1, a2);
	}

	public static IAST FunctionOfTrigOfLinearQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTrigOfLinearQ"), a0, a1);
	}

	public static IAST FunctionOfTrigQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "FunctionOfTrigQ"), a0, a1, a2);
	}

	public static IAST GE(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GE"), a0, a1);
	}

	public static IAST GE(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "GE"), a0, a1, a2);
	}

	public static IAST GT(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GT"), a0, a1);
	}

	public static IAST GT(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "GT"), a0, a1, a2);
	}

	public static IAST GtQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GtQ"), a0, a1);
	}

	public static IAST GtQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "GtQ"), a0, a1, a2);
	}

	public static IAST GeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeQ"), a0, a1);
	}

	public static IAST GeQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "GeQ"), a0, a1, a2);
	}

	public static IAST Gcd(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "Gcd"));
	}

	public static IAST Gcd(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Gcd"), a0, a1);
	}

	public static IAST GeneralizedBinomialDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedBinomialDegree"), a0, a1);
	}

	public static IAST GeneralizedBinomialMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedBinomialMatchQ"), a0, a1);
	}

	public static IAST GeneralizedBinomialParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedBinomialParts"), a0, a1);
	}

	public static IAST GeneralizedBinomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedBinomialQ"), a0, a1);
	}

	public static IAST GeneralizedBinomialTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedBinomialTest"), a0, a1);
	}

	public static IAST GeneralizedTrinomialDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedTrinomialDegree"), a0, a1);
	}

	public static IAST GeneralizedTrinomialMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedTrinomialMatchQ"), a0, a1);
	}

	public static IAST GeneralizedTrinomialParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedTrinomialParts"), a0, a1);
	}

	public static IAST GeneralizedTrinomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedTrinomialQ"), a0, a1);
	}

	public static IAST GeneralizedTrinomialTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "GeneralizedTrinomialTest"), a0, a1);
	}

	public static IAST GensymSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "GensymSubst"), a0, a1, a2);
	}

	public static IAST HalfIntegerQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "HalfIntegerQ"), a0);
	}

	public static IAST HalfIntegerQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "HalfIntegerQ"), a0, a1);
	}

	public static IAST HeldFormQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "HeldFormQ"), a0);
	}

	public static IAST HyperbolicQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "HyperbolicQ"), a0);
	}

	public static IAST IGtQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IGtQ"), a0, a1);
	}

	public static IAST IGeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IGeQ"), a0, a1);
	}

	public static IAST ILtQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ILtQ"), a0, a1);
	}

	public static IAST ILeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ILeQ"), a0, a1);
	}

	public static IAST ImaginaryNumericQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ImaginaryNumericQ"), a0);
	}

	public static IAST ImaginaryQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "ImaginaryQ"), a0);
	}

	public static IAST IndependentQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IndependentQ"), a0, a1);
	}

	public static IAST InertReciprocalQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "InertReciprocalQ"), a0, a1);
	}

	public static IAST InertTrigFreeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "InertTrigFreeQ"), a0);
	}

	public static IAST InertTrigQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "InertTrigQ"), a0);
	}

	public static IAST InertTrigQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "InertTrigQ"), a0, a1);
	}

	public static IAST InertTrigQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "InertTrigQ"), a0, a1, a2);
	}

	public static IAST InertTrigSumQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "InertTrigSumQ"), a0, a1, a2);
	}

	public static IAST IntPart(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "IntPart"), a0);
	}

	public static IAST IntPart(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IntPart"), a0, a1);
	}

	public static IAST IntSum(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IntSum"), a0, a1);
	}

	public static IAST IntTerm(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IntTerm"), a0, a1);
	}

	public static IAST IntegerPowerQ(final IExpr a0) {
		return unaryAST1(IntegerPowerQ, a0);
	}

	public static IAST IntegerQuotientQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "IntegerQuotientQ"), a0, a1);
	}

	public static IAST IntegersQ(final IExpr... a) {
		return ast(a, IntegersQ);
	}

	public static IAST Integral(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Integral"), a0, a1);
	}

	public static IAST IntegralFreeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "IntegralFreeQ"), a0);
	}

	public static IAST InverseFunctionFreeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "InverseFunctionFreeQ"), a0, a1);
	}

	public static IAST InverseFunctionOfLinear(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "InverseFunctionOfLinear"), a0, a1);
	}

	public static IAST InverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "InverseFunctionOfQuotientOfLinears"), a0, a1);
	}

	public static IAST InverseFunctionQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "InverseFunctionQ"), a0);
	}

	public static IAST InverseHyperbolicQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "InverseHyperbolicQ"), a0);
	}

	public static IAST InverseTrigQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "InverseTrigQ"), a0);
	}

	public static IAST J(final IExpr a0) {
		return unaryAST1(J, a0);
	}

	public static IAST KernelSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "KernelSubst"), a0, a1, a2);
	}

	public static IAST KnownCotangentIntegrandQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "KnownCotangentIntegrandQ"), a0, a1);
	}

	public static IAST KnownSecantIntegrandQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "KnownSecantIntegrandQ"), a0, a1);
	}

	public static IAST KnownSineIntegrandQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "KnownSineIntegrandQ"), a0, a1);
	}

	public static IAST KnownTangentIntegrandQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "KnownTangentIntegrandQ"), a0, a1);
	}

	public static IAST KnownTrigIntegrandQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "KnownTrigIntegrandQ"), a0, a1, a2);
	}

	public static IAST LE(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LE"), a0, a1);
	}

	public static IAST LE(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "LE"), a0, a1, a2);
	}

	public static IAST LtQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LtQ"), a0, a1);
	}

	public static IAST LtQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "LtQ"), a0, a1, a2);
	}

	public static IAST LeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LeQ"), a0, a1);
	}

	public static IAST LeQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "LeQ"), a0, a1, a2);
	}

	public static IAST LT(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LT"), a0, a1);
	}

	public static IAST LT(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "LT"), a0, a1, a2);
	}

	public static IAST LeadBase(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "LeadBase"), a0);
	}

	public static IAST LeadDegree(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "LeadDegree"), a0);
	}

	public static IAST LeadFactor(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "LeadFactor"), a0);
	}

	public static IAST LeadTerm(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "LeadTerm"), a0);
	}

	public static IAST LinearMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LinearMatchQ"), a0, a1);
	}

	public static IAST LinearPairQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "LinearPairQ"), a0, a1, a2);
	}

	public static IAST LinearQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "LinearQ"), a0, a1);
	}

	public static IAST LogQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "LogQ"), a0);
	}

	public static IAST MakeAssocList(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MakeAssocList"), a0, a1);
	}

	public static IAST MakeAssocList(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "MakeAssocList"), a0, a1, a2);
	}

	public static IAST Map2(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "Map2"), a0, a1, a2);
	}

	public static IAST MapAnd(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MapAnd"), a0, a1);
	}

	public static IAST MapAnd(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "MapAnd"), a0, a1, a2);
	}

	public static IAST MapOr(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MapOr"), a0, a1);
	}

	public static IAST MergeFactor(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "MergeFactor"), a0, a1, a2);
	}

	public static IAST MergeFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MergeFactors"), a0, a1);
	}

	public static IAST MergeMonomials(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MergeMonomials"), a0, a1);
	}

	public static IAST MergeableFactorQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "MergeableFactorQ"), a0, a1, a2);
	}

	public static IAST MinimumDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MinimumDegree"), a0, a1);
	}

	public static IAST MinimumMonomialExponent(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MinimumMonomialExponent"), a0, a1);
	}

	public static IAST MonomialExponent(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MonomialExponent"), a0, a1);
	}

	public static IAST MonomialFactor(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MonomialFactor"), a0, a1);
	}

	public static IAST MonomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MonomialQ"), a0, a1);
	}

	public static IAST MonomialSumQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "MonomialSumQ"), a0, a1);
	}

	public static IAST MostMainFactorPosition(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "MostMainFactorPosition"), a0);
	}

	public static IAST NegativeCoefficientQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NegativeCoefficientQ"), a0);
	}

	public static IAST NegativeIntegerQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "NegativeIntegerQ"));
	}

	public static IAST NegativeOrZeroQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NegativeOrZeroQ"), a0);
	}

	public static IAST NegativeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NegativeQ"), a0);
	}

	public static IAST NegQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NegQ"), a0);
	}

	public static IAST NegQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NegQ"), a0, a1);
	}

	public static IAST NegSumBaseQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NegSumBaseQ"), a0);
	}

	public static IAST NeQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NeQ"), a0);
	}

	public static IAST NeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NeQ"), a0, a1);
	}

	public static IAST NiceSqrtAuxQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NiceSqrtAuxQ"), a0);
	}

	public static IAST NiceSqrtQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NiceSqrtQ"), a0);
	}

	public static IAST NonabsurdNumberFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NonabsurdNumberFactors"), a0);
	}

	public static IAST NonalgebraicFunctionFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NonalgebraicFunctionFactors"), a0, a1);
	}

	public static IAST NonfreeFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NonfreeFactors"), a0, a1);
	}

	public static IAST NonfreeTerms(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NonfreeTerms"), a0, a1);
	}

	public static IAST NonnumericFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NonnumericFactors"), a0);
	}

	public static IAST NonpolynomialTerms(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NonpolynomialTerms"), a0, a1);
	}

	public static IAST NonpositiveFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NonpositiveFactors"), a0);
	}

	public static IAST NonrationalFunctionFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NonrationalFunctionFactors"), a0, a1);
	}

	public static IAST NonsumQ(final IExpr a0) {
		return unaryAST1(NonsumQ, a0);
	}

	// public static IAST NonzeroQ(final IExpr a0) {
	// return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NonzeroQ"), a0);
	// }

	public static IAST NormalizeHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "NormalizeHyperbolic"), a0, a1, a2, a3);
	}

	public static IAST NormalizeIntegrand(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeIntegrand"), a0, a1);
	}

	public static IAST NormalizeIntegrandAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeIntegrandAux"), a0, a1);
	}

	public static IAST NormalizeIntegrandFactor(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeIntegrandFactor"), a0, a1);
	}

	public static IAST NormalizeIntegrandFactorBase(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeIntegrandFactorBase"), a0, a1);
	}

	public static IAST NormalizeLeadTermSigns(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NormalizeLeadTermSigns"), a0);
	}

	public static IAST NormalizePowerOfLinear(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizePowerOfLinear"), a0, a1);
	}

	public static IAST NormalizePseudoBinomial(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizePseudoBinomial"), a0, a1);
	}

	public static IAST NormalizeSumFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NormalizeSumFactors"), a0);
	}

	public static IAST NormalizeTogether(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NormalizeTogether"), a0);
	}

	public static IAST NormalizeTrig(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeTrig"), a0, a1);
	}

	public static IAST NormalizeTrig(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "NormalizeTrig"), a0, a1, a2, a3);
	}

	public static IAST NormalizeTrigReduce(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NormalizeTrigReduce"), a0, a1);
	}

	public static IAST NotFalseQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NotFalseQ"), a0);
	}

	public static IAST NotIntegrableQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NotIntegrableQ"), a0, a1);
	}

	public static IAST NumericFactor(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "NumericFactor"), a0);
	}

	public static IAST NthRoot(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "NthRoot"), a0, a1);
	}

	public static IAST OddHyperbolicPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "OddHyperbolicPowerQ"), a0, a1, a2);
	}

	public static IAST OddQuotientQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "OddQuotientQ"), a0, a1);
	}

	public static IAST OddTrigPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "OddTrigPowerQ"), a0, a1, a2);
	}

	public static IAST OneQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "OneQ"), a0);
	}

	public static IAST OneQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "OneQ"));
	}

	public static IAST PerfectPowerTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PerfectPowerTest"), a0, a1);
	}

	public static IAST PerfectSquareQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PerfectSquareQ"), a0);
	}

	public static IAST PiecewiseLinearQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PiecewiseLinearQ"), a0, a1);
	}

	public static IAST PiecewiseLinearQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PiecewiseLinearQ"), a0, a1, a2);
	}

	public static IAST PolyGCD(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolyGCD"), a0, a1, a2);
	}

	public static IAST PolyQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PolyQ"), a0, a1);
	}

	public static IAST PolyQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolyQ"), a0, a1, a2);
	}

	public static IAST PolynomialDivide(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolynomialDivide"), a0, a1, a2);
	}

	public static IAST PolynomialDivide(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "PolynomialDivide"), a0, a1, a2, a3);
	}

	public static IAST PolynomialInAuxQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolynomialInAuxQ"), a0, a1, a2);
	}

	public static IAST PolynomialInQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolynomialInQ"), a0, a1, a2);
	}

	public static IAST PolynomialInSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolynomialInSubst"), a0, a1, a2);
	}

	public static IAST PolynomialInSubstAux(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PolynomialInSubstAux"), a0, a1, a2);
	}

	public static IAST PolynomialTermQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PolynomialTermQ"), a0, a1);
	}

	public static IAST PolynomialTerms(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PolynomialTerms"), a0, a1);
	}

	public static IAST PosAux(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PosAux"), a0);
	}

	public static IAST PosQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PosQ"), a0);
	}

	public static IAST PositiveFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PositiveFactors"), a0);
	}

	public static IAST PositiveIntegerPowerQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PositiveIntegerPowerQ"), a0);
	}

	public static IAST PositiveIntegerQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "PositiveIntegerQ"));
	}

	public static IAST PositiveOrZeroQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PositiveOrZeroQ"), a0);
	}

	public static IAST PositiveQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "PositiveQ"), a0);
	}

	public static IAST PowerOfLinearMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PowerOfLinearMatchQ"), a0, a1);
	}

	public static IAST PowerOfLinearQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PowerOfLinearQ"), a0, a1);
	}

	public static IAST PowerQ(final IExpr a0) {
		return unaryAST1(PowerQ, a0);
	}

	public static IAST PowerVariableDegree(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "PowerVariableDegree"), a0, a1, a2, a3);
	}

	public static IAST PowerVariableExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PowerVariableExpn"), a0, a1, a2);
	}

	public static IAST PowerVariableSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PowerVariableSubst"), a0, a1, a2);
	}

	public static IAST PowerOfInertTrigSumQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PowerOfInertTrigSumQ"), a0, a1, a2);
	}

	public static IAST ProductOfLinearPowersQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ProductOfLinearPowersQ"), a0, a1);
	}

	public static IAST ProductQ(final IExpr a0) {
		return unaryAST1(ProductQ, a0);
	}

	public static IAST ProperPolyQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ProperPolyQ"), a0, a1);
	}

	public static IAST PseudoBinomialParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PseudoBinomialParts"), a0, a1);
	}

	public static IAST PseudoBinomialPairQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PseudoBinomialPairQ"), a0, a1, a2);
	}

	public static IAST PseudoBinomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "PseudoBinomialQ"), a0, a1);
	}

	public static IAST PureFunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfCosQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfCoshQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCotQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfCotQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfCothQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfCothQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfSinQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfSinhQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfTanQ"), a0, a1, a2);
	}

	public static IAST PureFunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "PureFunctionOfTanhQ"), a0, a1, a2);
	}

	public static IAST QuadraticMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuadraticMatchQ"), a0, a1);
	}

	public static IAST QuadraticQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuadraticQ"), a0, a1);
	}

	public static IAST QuotientOfLinearsMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuotientOfLinearsMatchQ"), a0, a1);
	}

	public static IAST QuotientOfLinearsP(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuotientOfLinearsP"), a0, a1);
	}

	public static IAST QuotientOfLinearsParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuotientOfLinearsParts"), a0, a1);
	}

	public static IAST QuotientOfLinearsQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuotientOfLinearsQ"), a0, a1);
	}

	public static IAST QuadraticProductQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "QuadraticProductQ"), a0, a1);
	}

	public static IAST RationalFunctionExpand(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RationalFunctionExpand"), a0, a1);
	}

	public static IAST RationalFunctionExponents(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RationalFunctionExponents"), a0, a1);
	}

	public static IAST RationalFunctionFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RationalFunctionFactors"), a0, a1);
	}

	public static IAST RationalFunctionQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RationalFunctionQ"), a0, a1);
	}

	public static IAST RationalPowerQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RationalPowerQ"), a0);
	}

	public static IAST RationalQ(final IExpr... a) {
		return ast(a, F.$rubi(INTEGRATE_PREFIX + "RationalQ"));
	}

	// public static IAST RealNumericQ(final IExpr a0) {
	// return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RealNumericQ"), a0);
	// }

	public static IAST RealQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RealQ"), a0);
	}

	public static IAST ReapList(final IExpr a0) {
		return unaryAST1(ReapList, a0);
	}

	public static IAST RectifyCotangent(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "RectifyCotangent"), a0, a1, a2, a3);
	}

	public static IAST RectifyCotangent(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "RectifyCotangent"), a0, a1, a2, a3, a4);
	}

	public static IAST RectifyTangent(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "RectifyTangent"), a0, a1, a2, a3);
	}

	public static IAST RectifyTangent(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "RectifyTangent"), a0, a1, a2, a3, a4);
	}

	public static IAST ReduceInertTrig(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "ReduceInertTrig"), a0, a1);
	}

	public static IAST ReduceInertTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "ReduceInertTrig"), a0, a1, a2);
	}

	public static IAST RemainingFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RemainingFactors"), a0);
	}

	public static IAST RemainingTerms(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RemainingTerms"), a0);
	}

	public static IAST RemoveContent(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RemoveContent"), a0, a1);
	}

	public static IAST RemoveContentAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RemoveContentAux"), a0, a1);
	}

	public static IAST Rt(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "Rt"), a0);
	}

	public static IAST Rt(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Rt"), a0, a1);
	}

	public static IAST RtAux(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "RtAux"), a0, a1);
	}

	public static IAST RuleName(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "RuleName"), a0);
	}

	public static IAST SecQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SecQ"), a0);
	}

	public static IAST SechQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SechQ"), a0);
	}

	public static IAST SignOfFactor(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SignOfFactor"), a0);
	}

	public static IAST Simp(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Simp"), a0, a1);
	}

	public static IAST SimpFixFactor(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimpFixFactor"), a0, a1);
	}

	public static IAST SimpHelp(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimpHelp"), a0, a1);
	}

	public static IAST SimplerIntegrandQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SimplerIntegrandQ"), a0, a1, a2);
	}

	public static IAST SimplerQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplerQ"), a0, a1);
	}

	public static IAST SimplerSqrtQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplerSqrtQ"), a0, a1);
	}

	public static IAST SimplifyAntiderivative(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplifyAntiderivative"), a0, a1);
	}

	public static IAST SimplifyAntiderivativeSum(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplifyAntiderivativeSum"), a0, a1);
	}

	public static IAST SimplifyIntegrand(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplifyIntegrand"), a0, a1);
	}

	public static IAST SimplifyTerm(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SimplifyTerm"), a0, a1);
	}

	public static IAST SinCosQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SinCosQ"), a0);
	}

	public static IAST SinQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SinQ"), a0);
	}

	public static IAST SinhCoshQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SinhCoshQ"), a0);
	}

	public static IAST SinhQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SinhQ"), a0);
	}

	public static IAST Smallest(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "Smallest"), a0);
	}

	public static IAST Smallest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Smallest"), a0, a1);
	}

	public static IAST SmartApart(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SmartApart"), a0, a1);
	}

	public static IAST SmartApart(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SmartApart"), a0, a1, a2);
	}

	public static IAST SmartDenominator(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SmartDenominator"), a0);
	}

	public static IAST SmartNumerator(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SmartNumerator"), a0);
	}

	public static IAST SmartSimplify(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SmartSimplify"), a0);
	}

	public static IAST SomeNegTermQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SomeNegTermQ"), a0);
	}

	public static IAST SplitFreeFactors(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SplitFreeFactors"), a0, a1);
	}

	public static IAST SplitProduct(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SplitProduct"), a0, a1);
	}

	public static IAST SplitSum(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SplitSum"), a0, a1);
	}

	public static IAST SqrtNumberQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SqrtNumberQ"), a0);
	}

	public static IAST SqrtNumberSumQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SqrtNumberSumQ"), a0);
	}

	public static IAST SqrtQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SqrtQ"), a0);
	}

	public static IAST SquareRootOfQuadraticSubst(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "SquareRootOfQuadraticSubst"), a0, a1, a2, a3);
	}

	public static IAST StopFunctionQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "StopFunctionQ"), a0);
	}

	public static IAST Subst(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Subst"), a0, a1);
	}

	public static IAST Subst(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "Subst"), a0, a1, a2);
	}

	public static IAST SubstAux(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstAux"), a0, a1, a2);
	}

	public static IAST SubstAux(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "SubstAux"), a0, a1, a2, a3);
	}

	public static IAST SubstFor(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstFor"), a0, a1, a2);
	}

	public static IAST SubstFor(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "SubstFor"), a0, a1, a2, a3);
	}

	public static IAST SubstForAux(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstForAux"), a0, a1, a2);
	}

	public static IAST SubstForExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstForExpn"), a0, a1, a2);
	}

	public static IAST SubstForFractionalPower(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "SubstForFractionalPower"), a0, a1, a2, a3, a4);
	}

	public static IAST SubstForFractionalPowerAuxQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstForFractionalPowerAuxQ"), a0, a1, a2);
	}

	public static IAST SubstForFractionalPowerOfLinear(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SubstForFractionalPowerOfLinear"), a0, a1);
	}

	public static IAST SubstForFractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SubstForFractionalPowerOfQuotientOfLinears"), a0, a1);
	}

	public static IAST SubstForFractionalPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstForFractionalPowerQ"), a0, a1, a2);
	}

	public static IAST SubstForHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "SubstForHyperbolic"), a0, a1, a2, a3, a4);
	}

	public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "SubstForInverseFunction"), a0, a1, a2);
	}

	public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(F.$rubi(INTEGRATE_PREFIX + "SubstForInverseFunction"), a0, a1, a2, a3);
	}

	public static IAST SubstForInverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SubstForInverseFunctionOfQuotientOfLinears"), a0, a1);
	}

	public static IAST SubstForTrig(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary(F.$rubi(INTEGRATE_PREFIX + "SubstForTrig"), a0, a1, a2, a3, a4);
	}

	public static IAST SumBaseQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "SumBaseQ"), a0);
	}

	public static IAST SumQ(final IExpr a0) {
		return unaryAST1(SumQ, a0);
	}

	public static IAST SumSimplerAuxQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SumSimplerAuxQ"), a0, a1);
	}

	public static IAST SumSimplerQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "SumSimplerQ"), a0, a1);
	}

	public static IAST TanQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TanQ"), a0);
	}

	public static IAST TanhQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TanhQ"), a0);
	}

	public static IAST TogetherSimplify(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TogetherSimplify"), a0);
	}

	public static IAST TrigHyperbolicFreeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrigHyperbolicFreeQ"), a0, a1);
	}

	public static IAST TrigQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigQ"), a0);
	}

	public static IAST TrigSimplify(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSimplify"), a0);
	}

	public static IAST TrigSimplifyAux(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSimplifyAux"), a0);
	}

	public static IAST TrigSimplifyQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSimplifyQ"), a0);
	}

	public static IAST TrigSimplifyRecur(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSimplifyRecur"), a0);
	}

	public static IAST TrigSquareQ(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSquareQ"), a0);
	}

	public static IAST TrinomialDegree(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrinomialDegree"), a0, a1);
	}

	public static IAST TrinomialMatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrinomialMatchQ"), a0, a1);
	}

	public static IAST TrinomialParts(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrinomialParts"), a0, a1);
	}

	public static IAST TrinomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrinomialQ"), a0, a1);
	}

	public static IAST TrinomialTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TrinomialTest"), a0, a1);
	}

	public static IAST TryPureTanSubst(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TryPureTanSubst"), a0, a1);
	}

	public static IAST TryPureTanhSubst(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TryPureTanhSubst"), a0, a1);
	}

	public static IAST TryTanhSubst(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "TryTanhSubst"), a0, a1);
	}

	public static IAST UnifyNegativeBaseFactors(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "UnifyNegativeBaseFactors"), a0);
	}

	public static IAST UnifySum(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "UnifySum"), a0, a1);
	}

	public static IAST UnifyTerm(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(F.$rubi(INTEGRATE_PREFIX + "UnifyTerm"), a0, a1, a2);
	}

	public static IAST UnifyTerms(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "UnifyTerms"), a0, a1);
	}

	// public static IAST Unintegrable(final IExpr a0, final IExpr a1) {
	// return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "Unintegrable"), a0, a1);
	// }

	public static IAST UnifyInertTrigFunction(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.$rubi(INTEGRATE_PREFIX + "UnifyInertTrigFunction"), a0, a1);
	}

	public static IAST TrigSquare(final IExpr a0) {
		return unaryAST1(F.$rubi(INTEGRATE_PREFIX + "TrigSquare"), a0);
	}

	/**
	 * <pre>
	 * w_*Dist[u_,v_,x_] :=
	 *     Dist[w*u,v,x] /;
	 *     w=!=-1
	 * </pre>
	 * 
	 * @param astTimes
	 * @return
	 */
	public static IExpr evalRubiDistTimes(IAST astTimes) {
		for (int i = 1; i < astTimes.size(); i++) {
			IExpr temp = astTimes.get(i);
			if (temp.isAST(Dist) && temp.size() == 4) {
				IAST dist = (IAST) temp;
				temp = astTimes.removeAtClone(i).getOneIdentity(F.C1);
				if (!temp.isMinusOne()) {
					// System.out.println("w_*Dist[u_,v_,x_]");
					// Dist[ temp *u,v,x]
					return Dist(F.Times(temp, dist.arg1()), dist.arg2(), dist.arg3());
				}
			}
		}
		return F.NIL;
	}

	/**
	 * Rule 1:
	 * 
	 * <pre>
	 * Dist[u_,v_,x_]+Dist[w_,v_,x_] :=
	 *     If[EqQ[u+w,0],
	 *     0,
	 *     Dist[u+w,v,x]]
	 * </pre>
	 * 
	 * Rule 2:
	 * 
	 * <pre>
	 * Dist[u_,v_,x_]-Dist[w_,v_,x_] :=
	 *     If[EqQ[u-w,0],
	 *     0,
	 *     Dist[u-w,v,x]]
	 * </pre>
	 * 
	 * @param astTimes
	 * @return
	 */
	public static IExpr evalRubiDistPlus(IAST astPlus) {
		for (int i = 1; i < astPlus.size() - 1; i++) {
			IExpr arg1 = astPlus.get(i);
			if (arg1.isAST(Dist) && arg1.size() == 4) {
				// dist1 = Dist[u_,v_,x_]
				IAST dist1 = (IAST) arg1;
				IExpr v = dist1.arg2();
				IExpr x = dist1.arg2();
				for (int j = i + 1; j < astPlus.size(); j++) {
					IExpr arg2 = astPlus.get(j);
					if (arg2.isAST(Dist) && arg2.size() == 4 && arg2.getAt(2).equals(v) && arg2.getAt(2).equals(x)) {
						// dist2=Dist[w_,v_,x_]
						IAST dist2 = (IAST) arg2;
						IASTAppendable result = astPlus.removeAtClone(j);
						result.remove(i);
						// Dist /: Dist[u_,v_,x_]+Dist[w_,v_,x_] :=
						// If[EqQ[u+w,0],
						// 0,
						// Dist[u+w,v,x]]
						IExpr p = F.Plus(dist1.arg1(), dist2.arg1());
						result.append(F.If(EqQ(p, F.C0), F.C0, Dist(p, v, x)));
						return result;
					}
					if (arg2.isTimes() && arg2.size() == 3 && arg2.first().isMinusOne() && arg2.second().isAST(Dist)) {
						// -1 * Dist[w_,v_,x_]
						IAST dist2 = (IAST) arg2.second();
						if (dist2.size() == 4 && dist2.getAt(2).equals(v) && dist2.getAt(2).equals(x)) {
							IASTAppendable result = astPlus.removeAtClone(j);
							result.remove(i);
							// Dist /: Dist[u_,v_,x_]-Dist[w_,v_,x_] :=
							// If[EqQ[u-w,0],
							// 0,
							// Dist[u-w,v,x]]
							IExpr p = F.Subtract(dist1.arg1(), dist2.arg1());
							result.append(F.If(EqQ(p, F.C0), F.C0, Dist(p, v, x)));
							return result;
						}
					}
				}
			} else if (arg1.isTimes() && arg1.size() == 3 && arg1.first().isMinusOne() && arg1.second().isAST(Dist)) {
				// -1 * Dist[w_,v_,x_]
				IAST dist1 = (IAST) arg1.second();
				IExpr v = dist1.arg2();
				IExpr x = dist1.arg2();
				for (int j = i + 1; j < astPlus.size(); j++) {
					IExpr arg2 = astPlus.get(j);
					if (arg2.isAST(Dist) && arg2.size() == 4 && arg2.getAt(2).equals(v) && arg2.getAt(2).equals(x)) {
						// dist2 = Dist[u_,v_,x_]
						IAST dist2 = (IAST) arg2;
						IASTAppendable result = astPlus.removeAtClone(j);
						result.remove(i);
						// Dist /: Dist[u_,v_,x_]-Dist[w_,v_,x_] :=
						// If[EqQ[u-w,0],
						// 0,
						// Dist[u-w,v,x]]
						IExpr p = F.Subtract(dist2.arg1(), dist1.arg1());
						result.append(F.If(EqQ(p, F.C0), F.C0, Dist(p, v, x)));
						return result;
					}
				}
			}
		}
		return F.NIL;
	}

}