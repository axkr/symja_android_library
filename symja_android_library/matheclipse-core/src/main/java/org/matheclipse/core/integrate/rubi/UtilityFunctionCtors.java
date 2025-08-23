package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.ast;
import static org.matheclipse.core.expression.F.function;
import static org.matheclipse.core.expression.F.quaternary;
import static org.matheclipse.core.expression.F.quinary;
import static org.matheclipse.core.expression.F.senary;
import static org.matheclipse.core.expression.S.initFinalHiddenSymbol;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.B1;
import org.matheclipse.core.expression.B2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.KryoUtil;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.esotericsoftware.kryo.Kryo;

/**
 * UtilityFunction constructors from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *
 * <p>
 * TODO a lot of functions are only placeholders at the moment.
 */
public class UtilityFunctionCtors {

  public static ISymbol INTEGRATE_TRIG_SIMPLIFY = null;
  public static ISymbol INTEGRATE_SMARTLEAFCOUNT = null;
  public static ISymbol INTEGRATE_SMARTNUMERATOR = null;
  public static ISymbol INTEGRATE_SMARTDENOMINATOR = null;
  public static ISymbol INTEGRATE_SIMP = null;
  public static ISymbol INTEGRATE_REAPLIST = null;

  public static final ISymbol H = initFinalHiddenSymbol("H");
  public static final ISymbol J = initFinalHiddenSymbol("J");
  public static final ISymbol K = initFinalHiddenSymbol("K");
  public static final ISymbol L = initFinalHiddenSymbol("L");
  public static final ISymbol M = initFinalHiddenSymbol("M");
  public static final ISymbol O = initFinalHiddenSymbol("O");
  public static final ISymbol P = initFinalHiddenSymbol("P");
  public static final ISymbol Q = initFinalHiddenSymbol("Q");
  public static final ISymbol R = initFinalHiddenSymbol("R");
  // public final static ISymbol S = initFinalHiddenSymbol("S");
  public static final ISymbol T = initFinalHiddenSymbol("T");
  public static final ISymbol U = initFinalHiddenSymbol("U");
  public static final ISymbol V = initFinalHiddenSymbol("V");
  public static final ISymbol W = initFinalHiddenSymbol("W");
  public static final ISymbol X = initFinalHiddenSymbol("X");
  public static final ISymbol Y = initFinalHiddenSymbol("Y");
  public static final ISymbol Z = initFinalHiddenSymbol("Z");

  public static ISymbol Dist = F.$rubi("Dist");

  public static ISymbol GeQ = F.$rubi("GeQ");
  public static ISymbol GtQ = F.$rubi("GtQ");
  public static ISymbol IGtQ = F.$rubi("IGtQ");
  public static ISymbol IGeQ = F.$rubi("IGeQ");
  public static ISymbol ILtQ = F.$rubi("ILtQ");
  public static ISymbol ILeQ = F.$rubi("ILeQ");

  public static ISymbol LtQ = F.$rubi("LtQ");
  public static ISymbol LeQ = F.$rubi("LeQ");
  public static ISymbol NegQ = F.$rubi("NegQ");
  public static ISymbol PolyQ = F.$rubi("PolyQ");
  public static ISymbol PosQ = F.$rubi("PosQ");

  public static ISymbol BinomialQ = F.$rubi("BinomialQ");
  public static ISymbol ExpandTrig = F.$rubi("ExpandTrig");
  public static ISymbol FixSimplify = F.$rubi("FixSimplify");
  public static ISymbol FracPart = F.$rubi("FracPart");
  public static ISymbol IntPart = F.$rubi("IntPart");
  public static ISymbol Simp = F.$rubi("Simp");
  public static ISymbol Star = F.$rubi("Star");
  public static ISymbol Unintegrable = F.$rubi("Unintegrable");

  public static ISymbol NormalizeIntegrand = F.$rubi("NormalizeIntegrand");

  public static ISymbol ReapList = org.matheclipse.core.expression.F.$rubi("ReapList");

  static ISymbol FalseQ = F.$rubi("FalseQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        return engine.evaluate(ast.arg1()).isFalse() ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol FractionQ = F.$rubi("FractionQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.isFraction() ? S.True : S.False;
      }
      if (ast.argSize() > 1) {
        return ast.forAll(x -> engine.evaluate(x).isFraction(), 1) ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol IntegersQ = F.$rubi("IntegersQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.isInteger() ? S.True : S.False;
      }
      if (ast.argSize() > 1) {
        return ast.forAll(x -> engine.evaluate(x).isInteger(), 1) ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol ComplexNumberQ = F.$rubi("ComplexNumberQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.isComplex() || arg1.isComplexNumeric() ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol PowerQ = F.$rubi("PowerQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.head().equals(S.Power) ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol ProductQ = F.$rubi("ProductQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.head().equals(S.Times) ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol SumQ = F.$rubi("SumQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.head().equals(S.Plus) ? S.True : S.False;
      }
      return S.False;
    }
  });
  static ISymbol NonsumQ = F.$rubi("NonsumQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 1) {
        IExpr arg1 = engine.evaluate(ast.arg1());
        return arg1.head().equals(S.Plus) ? S.False : S.True;
      }
      return S.False;
    }
  });

  public static ISymbol IntegerPowerQ =
      F.$rubi("IntegerPowerQ", new AbstractCoreFunctionEvaluator() {
        @Override
        public IExpr evaluate(IAST ast, EvalEngine engine) {
          if (ast.argSize() == 1) {
            IExpr arg1 = engine.evaluate(ast.arg1());
            return arg1.isPower() && arg1.exponent().isInteger() ? S.True : S.False;
          }
          return S.False;
        }
      });

  public static ISymbol FractionalPowerQ =
      F.$rubi("FractionalPowerQ", new AbstractCoreFunctionEvaluator() {
        @Override
        public IExpr evaluate(IAST ast, EvalEngine engine) {
          if (ast.argSize() == 1) {
            IExpr arg1 = engine.evaluate(ast.arg1());
            return arg1.isPower() && arg1.exponent().isFraction() ? S.True : S.False;
          }
          return S.False;
        }
      });

  public static IAST F(final IExpr a0) {
    return F.unaryAST1(F.FSymbol, a0);
  }

  public static IAST F(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.FSymbol, a0, a1);
  }

  public static IAST G(final IExpr a0) {
    return F.unaryAST1(F.GSymbol, a0);
  }

  public static IAST G(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.GSymbol, a0, a1);
  }

  public static IAST H(final IExpr a0) {
    return F.unaryAST1(H, a0);
  }

  public static IAST H(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(H, a0, a1);
  }

  public static IAST H(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(H, a0, a1, a2, a3);
  }

  public static IAST IntBinomialQ(final IExpr... a) {
    return function(F.$rubi("IntBinomialQ"), a);
  }

  public static IAST IntHide(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("IntHide"), a0, a1);
  }

  public static IAST IntLinearQ(final IExpr... a) {
    return function(F.$rubi("IntLinearQ"), a);
  }

  public static IAST IntQuadraticQ(final IExpr... a) {
    return function(F.$rubi("IntQuadraticQ"), a);
  }

  public static IAST Dist(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(Dist, a0, a1, a2);
  }

  public static IAST AbsorbMinusSign(final IExpr a0) {
    return F.unaryAST1(F.$rubi("AbsorbMinusSign"), a0);
  }

  public static IAST AbsurdNumberFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("AbsurdNumberFactors"), a0);
  }

  public static IAST AbsurdNumberGCD(final IExpr... a) {
    return ast(a, F.$rubi("AbsurdNumberGCD"));
  }

  public static IAST AbsurdNumberGCDList(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("AbsurdNumberGCDList"), a0, a1);
  }

  public static IAST AbsurdNumberQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("AbsurdNumberQ"), a0);
  }

  public static IAST ActivateTrig(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ActivateTrig"), a0);
  }

  public static IAST AlgebraicFunctionFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("AlgebraicFunctionFactors"), a0, a1);
  }

  public static IAST AlgebraicFunctionQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("AlgebraicFunctionQ"), a0, a1);
  }

  public static IAST AlgebraicFunctionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("AlgebraicFunctionQ"), a0, a1, a2);
  }

  public static IAST AlgebraicTrigFunctionQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("AlgebraicTrigFunctionQ"), a0, a1);
  }

  public static IAST AllNegTermQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("AllNegTermQ"), a0);
  }

  public static IAST AtomBaseQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("AtomBaseQ"), a0);
  }

  public static IAST BinomialDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("BinomialDegree"), a0, a1);
  }

  public static IAST BinomialMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("BinomialMatchQ"), a0, a1);
  }

  public static IAST BinomialParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("BinomialParts"), a0, a1);
  }

  public static IAST BinomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(BinomialQ, a0, a1);
  }

  public static IAST BinomialQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(BinomialQ, a0, a1, a2);
  }

  public static IAST BinomialTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("BinomialTest"), a0, a1);
  }

  public static IAST CalculusFreeQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("CalculusFreeQ"), a0, a1);
  }

  public static IAST CalculusQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("CalculusQ"), a0);
  }

  public static IAST CancelCommonFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("CancelCommonFactors"), a0, a1);
  }

  public static IAST CannotIntegrate(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("CannotIntegrate"), a0, a1);
  }

  public static IAST CollectReciprocals(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("CollectReciprocals"), a0, a1);
  }

  public static IAST Coeff(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Coeff"), a0, a1);
  }

  public static IAST Coeff(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("Coeff"), a0, a1, a2);
  }

  public static IAST CombineExponents(final IExpr a0) {
    return F.unaryAST1(F.$rubi("CombineExponents"), a0);
  }

  public static IAST CommonFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("CommonFactors"), a0);
  }

  public static IAST CommonNumericFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("CommonNumericFactors"), a0);
  }

  public static IAST ComplexFreeQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ComplexFreeQ"), a0);
  }

  public static IAST ComplexNumberQ(final IExpr a0) {
    return F.unaryAST1(ComplexNumberQ, a0);
  }

  public static IAST ConstantFactor(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ConstantFactor"), a0, a1);
  }

  public static IAST ContentFactor(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ContentFactor"), a0);
  }

  public static IAST ContentFactorAux(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ContentFactorAux"), a0);
  }

  public static IAST CubicMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("CubicMatchQ"), a0, a1);
  }

  public static IAST DeactivateTrig(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("DeactivateTrig"), a0, a1);
  }

  public static IAST DeactivateTrigAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("DeactivateTrigAux"), a0, a1);
  }

  public static IAST DerivativeDivides(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("DerivativeDivides"), a0, a1, a2);
  }

  public static IAST Distrib(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Distrib"), a0, a1);
  }

  public static IAST DistributeDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("DistributeDegree"), a0, a1);
  }

  public static IAST DivideDegreesOfFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("DivideDegreesOfFactors"), a0, a1);
  }

  public static IAST Divides(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("Divides"), a0, a1, a2);
  }

  public static IAST EasyDQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("EasyDQ"), a0, a1);
  }

  static ISymbol EqQ = F.$rubi("EqQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 2) {
        // TODO implement Refine
        // Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True)))
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg1.equals(arg2)) {
          return S.True;
        }
        return arg1.subtract(arg2).isPossibleZero(true) ? S.True : S.False;
      }
      return S.False;
    }
  });

  static ISymbol NeQ = F.$rubi("NeQ", new AbstractCoreFunctionEvaluator() {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 2) {
        // TODO implement Refine
        // Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True)))
        IExpr arg1 = ast.arg1();
        IExpr arg2 = ast.arg2();
        if (arg1.equals(arg2)) {
          return S.False;
        }
        return arg1.subtract(arg2).isPossibleZero(true) ? S.False : S.True;
      }
      return S.True;
    }
  });

  public static final class EqQ extends B2 {
    public EqQ() {
      super();
    }

    EqQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return EqQ;
    }

    @Override
    public IASTMutable copy() {
      return new EqQ(arg1, arg2);
    }
  }

  public static IAST EqQ(final IExpr a0, final IExpr a1) {
    return new EqQ(a0, a1);
  }

  public static IAST EqQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(EqQ, a0, a1, a2);
  }

  public static IAST EulerIntegrandQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("EulerIntegrandQ"), a0, a1);
  }

  public static IAST EveryQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("EveryQ"), a0, a1);
  }

  public static IAST EvenQuotientQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("EvenQuotientQ"), a0, a1);
  }

  public static IAST ExpandAlgebraicFunction(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandAlgebraicFunction"), a0, a1);
  }

  public static IAST ExpandBinomial(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
      final IExpr a4, final IExpr a5) {
    return senary(F.$rubi("ExpandBinomial"), a0, a1, a2, a3, a4, a5);
  }

  public static IAST ExpandCleanup(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandCleanup"), a0, a1);
  }

  public static IAST ExpandExpression(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandExpression"), a0, a1);
  }

  public static IAST ExpandIntegrand(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandIntegrand"), a0, a1);
  }

  public static IAST ExpandIntegrand(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExpandIntegrand"), a0, a1, a2);
  }

  public static IAST ExpandLinearProduct(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4) {
    return quinary(F.$rubi("ExpandLinearProduct"), a0, a1, a2, a3, a4);
  }

  public static IAST ExpandToSum(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandToSum"), a0, a1);
  }

  public static IAST ExpandToSum(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExpandToSum"), a0, a1, a2);
  }

  public static IAST ExpandTrig(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(ExpandTrig, a0, a1);
  }

  public static IAST ExpandTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(ExpandTrig, a0, a1, a2);
  }

  public static IAST ExpandTrigExpand(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4, final IExpr a5) {
    return senary(F.$rubi("ExpandTrigExpand"), a0, a1, a2, a3, a4, a5);
  }

  public static IAST ExpandTrigReduce(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandTrigReduce"), a0, a1);
  }

  public static IAST ExpandTrigReduce(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExpandTrigReduce"), a0, a1, a2);
  }

  public static IAST ExpandTrigReduceAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandTrigReduceAux"), a0, a1);
  }

  public static IAST ExpandTrigToExp(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ExpandTrigToExp"), a0, a1);
  }

  public static IAST ExpandTrigToExp(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExpandTrigToExp"), a0, a1, a2);
  }

  public static IAST Expon(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Expon"), a0, a1);
  }

  public static IAST Expon(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("Expon"), a0, a1, a2);
  }

  public static IAST ExponentIn(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExponentIn"), a0, a1, a2);
  }

  public static IAST ExponentInAux(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ExponentInAux"), a0, a1, a2);
  }

  public static IAST FactorAbsurdNumber(final IExpr a0) {
    return F.unaryAST1(F.$rubi("FactorAbsurdNumber"), a0);
  }

  public static IAST FactorNumericGcd(final IExpr a0) {
    return F.unaryAST1(F.$rubi("FactorNumericGcd"), a0);
  }

  public static IAST FactorOrder(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FactorOrder"), a0, a1);
  }

  private static final class FalseQ extends B1 {
    public FalseQ() {
      super();
    }

    FalseQ(IExpr arg1) {
      super(arg1);
    }

    @Override
    public final ISymbol head() {
      return FalseQ;
    }

    @Override
    public IASTMutable copy() {
      return new FalseQ(arg1);
    }
  }

  public static IAST FalseQ(final IExpr a0) {
    return new FalseQ(a0);
  }

  public static IAST FindTrigFactor(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
      final IExpr a4) {
    return quinary(F.$rubi("FindTrigFactor"), a0, a1, a2, a3, a4);
  }

  public static IAST FixInertTrigFunction(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FixInertTrigFunction"), a0, a1);
  }

  public static IAST FixIntRule(final IExpr a0) {
    return F.unaryAST1(F.$rubi("FixIntRule"), a0);
  }

  public static IAST FixIntRule(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FixIntRule"), a0, a1);
  }

  // public static IAST FixIntRules() {
  // return F.headAST0(F.$rubi("FixIntRules"));
  // }

  // public static IAST FixIntRules(final IExpr a0) {
  // return F.unaryAST1(F.$rubi("FixIntRules"), a0);
  // }

  public static IAST FixRhsIntRule(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FixRhsIntRule"), a0, a1);
  }

  public static IAST FixSimplify(final IExpr a0) {
    return F.unaryAST1(FixSimplify, a0);
  }

  private static final class FracPart extends B1 {
    public FracPart() {
      super();
    }

    FracPart(IExpr arg1) {
      super(arg1);
    }

    @Override
    public final ISymbol head() {
      return FracPart;
    }

    @Override
    public IASTMutable copy() {
      return new FracPart(arg1);
    }
  }

  public static IAST FracPart(final IExpr a0) {
    return new FracPart(a0);
  }

  public static IAST FracPart(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(FracPart, a0, a1);
  }

  public static IAST FractionOrNegativeQ(final IExpr... a) {
    return function(F.$rubi("FractionOrNegativeQ"), a);
  }

  public static IAST FractionQ(final IExpr... a) {
    return function(FractionQ, a);
  }

  public static IAST FractionalPowerFreeQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("FractionalPowerFreeQ"), a0);
  }

  public static IAST FractionalPowerOfLinear(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("FractionalPowerOfLinear"), a0, a1, a2, a3);
  }

  public static IAST FractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1,
      final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("FractionalPowerOfQuotientOfLinears"), a0, a1, a2, a3);
  }

  public static IAST FractionalPowerOfSquareQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("FractionalPowerOfSquareQ"), a0);
  }

  public static IAST FractionalPowerQ(final IExpr a0) {
    return F.unaryAST1(FractionalPowerQ, a0);
  }

  public static IAST FractionalPowerSubexpressionQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FractionalPowerSubexpressionQ"), a0, a1, a2);
  }

  public static IAST FreeFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FreeFactors"), a0, a1);
  }

  public static IAST FreeTerms(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FreeTerms"), a0, a1);
  }

  public static IAST FunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfCosQ"), a0, a1, a2);
  }

  public static IAST FunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfCoshQ"), a0, a1, a2);
  }

  public static IAST FunctionOfDensePolynomialsQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfDensePolynomialsQ"), a0, a1);
  }

  public static IAST FunctionOfExpnQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfExpnQ"), a0, a1, a2);
  }

  public static IAST FunctionOfExponential(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfExponential"), a0, a1);
  }

  public static IAST FunctionOfExponentialFunction(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfExponentialFunction"), a0, a1);
  }

  public static IAST FunctionOfExponentialFunctionAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfExponentialFunctionAux"), a0, a1);
  }

  public static IAST FunctionOfExponentialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfExponentialQ"), a0, a1);
  }

  public static IAST FunctionOfExponentialTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfExponentialTest"), a0, a1);
  }

  public static IAST FunctionOfExponentialTestAux(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfExponentialTestAux"), a0, a1, a2);
  }

  public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfHyperbolic"), a0, a1);
  }

  public static IAST FunctionOfHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfHyperbolic"), a0, a1, a2);
  }

  public static IAST FunctionOfHyperbolicQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfHyperbolicQ"), a0, a1, a2);
  }

  public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfInverseLinear"), a0, a1);
  }

  public static IAST FunctionOfInverseLinear(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfInverseLinear"), a0, a1, a2);
  }

  public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfLinear"), a0, a1);
  }

  public static IAST FunctionOfLinear(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4) {
    return quinary(F.$rubi("FunctionOfLinear"), a0, a1, a2, a3, a4);
  }

  public static IAST FunctionOfLinearSubst(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("FunctionOfLinearSubst"), a0, a1, a2, a3);
  }

  public static IAST FunctionOfLog(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfLog"), a0, a1);
  }

  public static IAST FunctionOfLog(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("FunctionOfLog"), a0, a1, a2, a3);
  }

  public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfQ"), a0, a1, a2);
  }

  public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("FunctionOfQ"), a0, a1, a2, a3);
  }

  public static IAST FunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfSinQ"), a0, a1, a2);
  }

  public static IAST FunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfSinhQ"), a0, a1, a2);
  }

  public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfSquareRootOfQuadratic"), a0, a1);
  }

  public static IAST FunctionOfSquareRootOfQuadratic(final IExpr a0, final IExpr a1,
      final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfSquareRootOfQuadratic"), a0, a1, a2);
  }

  public static IAST FunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTanQ"), a0, a1, a2);
  }

  public static IAST FunctionOfTanWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTanWeight"), a0, a1, a2);
  }

  public static IAST FunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTanhQ"), a0, a1, a2);
  }

  public static IAST FunctionOfTanhWeight(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTanhWeight"), a0, a1, a2);
  }

  public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfTrig"), a0, a1);
  }

  public static IAST FunctionOfTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTrig"), a0, a1, a2);
  }

  public static IAST FunctionOfTrigOfLinearQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("FunctionOfTrigOfLinearQ"), a0, a1);
  }

  public static IAST FunctionOfTrigQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("FunctionOfTrigQ"), a0, a1, a2);
  }

  private static final class GtQ extends B2 {
    public GtQ() {
      super();
    }

    GtQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return GtQ;
    }

    @Override
    public IASTMutable copy() {
      return new GtQ(arg1, arg2);
    }
  }

  public static IAST GtQ(final IExpr a0, final IExpr a1) {
    return new GtQ(a0, a1);
  }

  public static IAST GtQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(GtQ, a0, a1, a2);
  }

  private static final class GeQ extends B2 {
    public GeQ() {
      super();
    }

    GeQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return GeQ;
    }

    @Override
    public IASTMutable copy() {
      return new GeQ(arg1, arg2);
    }
  }

  public static IAST GeQ(final IExpr a0, final IExpr a1) {
    return new GeQ(a0, a1);
  }

  public static IAST GeQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(GeQ, a0, a1, a2);
  }

  public static IAST GeneralizedBinomialDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedBinomialDegree"), a0, a1);
  }

  public static IAST GeneralizedBinomialMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedBinomialMatchQ"), a0, a1);
  }

  public static IAST GeneralizedBinomialParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedBinomialParts"), a0, a1);
  }

  public static IAST GeneralizedBinomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedBinomialQ"), a0, a1);
  }

  public static IAST GeneralizedBinomialTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedBinomialTest"), a0, a1);
  }

  public static IAST GeneralizedTrinomialDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedTrinomialDegree"), a0, a1);
  }

  public static IAST GeneralizedTrinomialMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedTrinomialMatchQ"), a0, a1);
  }

  public static IAST GeneralizedTrinomialParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedTrinomialParts"), a0, a1);
  }

  public static IAST GeneralizedTrinomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedTrinomialQ"), a0, a1);
  }

  public static IAST GeneralizedTrinomialTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("GeneralizedTrinomialTest"), a0, a1);
  }

  public static IAST GensymSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("GensymSubst"), a0, a1, a2);
  }

  public static IAST HalfIntegerQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("HalfIntegerQ"), a0);
  }

  public static IAST HalfIntegerQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("HalfIntegerQ"), a0, a1);
  }

  public static IAST HeldFormQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("HeldFormQ"), a0);
  }

  public static IAST HyperbolicQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("HyperbolicQ"), a0);
  }

  private static final class IGtQ extends B2 {
    public IGtQ() {
      super();
    }

    IGtQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return IGtQ;
    }

    @Override
    public IASTMutable copy() {
      return new IGtQ(arg1, arg2);
    }
  }

  public static IAST IGtQ(final IExpr a0, final IExpr a1) {
    return new IGtQ(a0, a1);
  }

  public static IAST IGeQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(IGeQ, a0, a1);
  }

  private static final class ILtQ extends B2 {
    public ILtQ() {
      super();
    }

    ILtQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return ILtQ;
    }

    @Override
    public IASTMutable copy() {
      return new ILtQ(arg1, arg2);
    }
  }

  public static IAST ILtQ(final IExpr a0, final IExpr a1) {
    return new ILtQ(a0, a1);
  }

  public static IAST ILeQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(ILeQ, a0, a1);
  }

  public static IAST ImaginaryNumericQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ImaginaryNumericQ"), a0);
  }

  public static IAST ImaginaryQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("ImaginaryQ"), a0);
  }

  public static IAST IndependentQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("IndependentQ"), a0, a1);
  }

  public static IAST InertReciprocalQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("InertReciprocalQ"), a0, a1);
  }

  public static IAST InertTrigFreeQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("InertTrigFreeQ"), a0);
  }

  public static IAST InertTrigQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("InertTrigQ"), a0);
  }

  public static IAST InertTrigQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("InertTrigQ"), a0, a1);
  }

  public static IAST InertTrigQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("InertTrigQ"), a0, a1, a2);
  }

  public static IAST InertTrigSumQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("InertTrigSumQ"), a0, a1, a2);
  }

  private static final class IntPart extends B1 {
    public IntPart() {
      super();
    }

    IntPart(IExpr arg1) {
      super(arg1);
    }

    @Override
    public final ISymbol head() {
      return IntPart;
    }

    @Override
    public IASTMutable copy() {
      return new IntPart(arg1);
    }
  }

  public static IAST IntPart(final IExpr a0) {
    return new IntPart(a0);
  }

  public static IAST IntPart(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(IntPart, a0, a1);
  }

  public static IAST IntSum(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("IntSum"), a0, a1);
  }

  public static IAST IntTerm(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("IntTerm"), a0, a1);
  }

  public static IAST IntegerPowerQ(final IExpr a0) {
    return F.unaryAST1(IntegerPowerQ, a0);
  }

  public static IAST IntegerQuotientQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("IntegerQuotientQ"), a0, a1);
  }

  public static IAST IntegersQ(final IExpr... a) {
    return function(IntegersQ, a);
  }

  public static IAST Integral(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Integral"), a0, a1);
  }

  public static IAST IntegralFreeQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("IntegralFreeQ"), a0);
  }

  public static IAST InverseFunctionFreeQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("InverseFunctionFreeQ"), a0, a1);
  }

  public static IAST InverseFunctionOfLinear(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("InverseFunctionOfLinear"), a0, a1);
  }

  public static IAST InverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("InverseFunctionOfQuotientOfLinears"), a0, a1);
  }

  public static IAST InverseFunctionQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("InverseFunctionQ"), a0);
  }

  public static IAST InverseHyperbolicQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("InverseHyperbolicQ"), a0);
  }

  public static IAST InverseTrigQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("InverseTrigQ"), a0);
  }

  public static IAST J(final IExpr a0) {
    return F.unaryAST1(J, a0);
  }

  public static IAST KernelSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("KernelSubst"), a0, a1, a2);
  }

  public static IAST KnownCotangentIntegrandQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("KnownCotangentIntegrandQ"), a0, a1);
  }

  public static IAST KnownSecantIntegrandQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("KnownSecantIntegrandQ"), a0, a1);
  }

  public static IAST KnownSineIntegrandQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("KnownSineIntegrandQ"), a0, a1);
  }

  public static IAST KnownTangentIntegrandQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("KnownTangentIntegrandQ"), a0, a1);
  }

  public static IAST KnownTrigIntegrandQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("KnownTrigIntegrandQ"), a0, a1, a2);
  }

  private static final class LtQ extends B2 {
    public LtQ() {
      super();
    }

    LtQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return LtQ;
    }

    @Override
    public IASTMutable copy() {
      return new LtQ(arg1, arg2);
    }
  }

  public static IAST LtQ(final IExpr a0, final IExpr a1) {
    return new LtQ(a0, a1);
  }

  public static IAST LtQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(LtQ, a0, a1, a2);
  }

  private static final class LeQ extends B2 {
    public LeQ() {
      super();
    }

    LeQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return LeQ;
    }

    @Override
    public IASTMutable copy() {
      return new LeQ(arg1, arg2);
    }
  }

  public static IAST LeQ(final IExpr a0, final IExpr a1) {
    return new LeQ(a0, a1);
  }

  public static IAST LeQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(LeQ, a0, a1, a2);
  }

  public static IAST LeadBase(final IExpr a0) {
    return F.unaryAST1(F.$rubi("LeadBase"), a0);
  }

  public static IAST LeadDegree(final IExpr a0) {
    return F.unaryAST1(F.$rubi("LeadDegree"), a0);
  }

  public static IAST LeadFactor(final IExpr a0) {
    return F.unaryAST1(F.$rubi("LeadFactor"), a0);
  }

  public static IAST LeadTerm(final IExpr a0) {
    return F.unaryAST1(F.$rubi("LeadTerm"), a0);
  }

  public static IAST LinearMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("LinearMatchQ"), a0, a1);
  }

  public static IAST LinearPairQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("LinearPairQ"), a0, a1, a2);
  }

  public static IAST LinearQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("LinearQ"), a0, a1);
  }

  public static IAST LogQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("LogQ"), a0);
  }

  public static IAST MakeAssocList(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MakeAssocList"), a0, a1);
  }

  public static IAST MakeAssocList(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("MakeAssocList"), a0, a1, a2);
  }

  public static IAST Map2(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("Map2"), a0, a1, a2);
  }

  public static IAST MapAnd(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MapAnd"), a0, a1);
  }

  public static IAST MapAnd(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("MapAnd"), a0, a1, a2);
  }

  public static IAST MapOr(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MapOr"), a0, a1);
  }

  public static IAST MergeFactor(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("MergeFactor"), a0, a1, a2);
  }

  public static IAST MergeFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MergeFactors"), a0, a1);
  }

  public static IAST MergeMonomials(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MergeMonomials"), a0, a1);
  }

  public static IAST MergeableFactorQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("MergeableFactorQ"), a0, a1, a2);
  }

  public static IAST MinimumDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MinimumDegree"), a0, a1);
  }

  public static IAST MinimumMonomialExponent(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MinimumMonomialExponent"), a0, a1);
  }

  public static IAST MonomialExponent(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MonomialExponent"), a0, a1);
  }

  public static IAST MonomialFactor(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MonomialFactor"), a0, a1);
  }

  public static IAST MonomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MonomialQ"), a0, a1);
  }

  public static IAST MonomialSumQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("MonomialSumQ"), a0, a1);
  }

  public static IAST MostMainFactorPosition(final IExpr a0) {
    return F.unaryAST1(F.$rubi("MostMainFactorPosition"), a0);
  }

  public static IAST NegativeCoefficientQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NegativeCoefficientQ"), a0);
  }

  public static IAST NegativeIntegerQ(final IExpr... a) {
    return function(F.$rubi("NegativeIntegerQ"), a);
  }

  public static IAST NegativeOrZeroQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NegativeOrZeroQ"), a0);
  }

  public static IAST NegativeQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NegativeQ"), a0);
  }

  public static IAST NegQ(final IExpr a0) {
    return F.unaryAST1(NegQ, a0);
  }

  public static IAST NegQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(NegQ, a0, a1);
  }

  public static IAST NegSumBaseQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NegSumBaseQ"), a0);
  }

  private static final class NeQ extends B2 {
    public NeQ() {
      super();
    }

    NeQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return NeQ;
    }

    @Override
    public IASTMutable copy() {
      return new NeQ(arg1, arg2);
    }
  }

  public static IAST NeQ(final IExpr a0, final IExpr a1) {
    return new NeQ(a0, a1);
  }

  public static IAST NiceSqrtAuxQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NiceSqrtAuxQ"), a0);
  }

  public static IAST NiceSqrtQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NiceSqrtQ"), a0);
  }

  public static IAST NonabsurdNumberFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NonabsurdNumberFactors"), a0);
  }

  public static IAST NonalgebraicFunctionFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NonalgebraicFunctionFactors"), a0, a1);
  }

  public static IAST NonfreeFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NonfreeFactors"), a0, a1);
  }

  public static IAST NonfreeTerms(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NonfreeTerms"), a0, a1);
  }

  public static IAST NonnumericFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NonnumericFactors"), a0);
  }

  public static IAST NonpolynomialTerms(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NonpolynomialTerms"), a0, a1);
  }

  public static IAST NonpositiveFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NonpositiveFactors"), a0);
  }

  public static IAST NonrationalFunctionFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NonrationalFunctionFactors"), a0, a1);
  }

  public static IAST NonsumQ(final IExpr a0) {
    return F.unaryAST1(NonsumQ, a0);
  }

  // public static IAST NonzeroQ(final IExpr a0) {
  // return F.unaryAST1(F.$rubi("NonzeroQ"), a0);
  // }

  public static IAST NormalizeHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("NormalizeHyperbolic"), a0, a1, a2, a3);
  }

  public static IAST NormalizeIntegrand(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(NormalizeIntegrand, a0, a1);
  }

  public static IAST NormalizeIntegrandAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizeIntegrandAux"), a0, a1);
  }

  public static IAST NormalizeIntegrandFactor(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizeIntegrandFactor"), a0, a1);
  }

  public static IAST NormalizeIntegrandFactorBase(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizeIntegrandFactorBase"), a0, a1);
  }

  public static IAST NormalizeLeadTermSigns(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NormalizeLeadTermSigns"), a0);
  }

  public static IAST NormalizePowerOfLinear(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizePowerOfLinear"), a0, a1);
  }

  public static IAST NormalizePseudoBinomial(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizePseudoBinomial"), a0, a1);
  }

  public static IAST NormalizeSumFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NormalizeSumFactors"), a0);
  }

  public static IAST NormalizeTogether(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NormalizeTogether"), a0);
  }

  public static IAST NormalizeTrig(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizeTrig"), a0, a1);
  }

  public static IAST NormalizeTrig(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("NormalizeTrig"), a0, a1, a2, a3);
  }

  public static IAST NormalizeTrigReduce(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NormalizeTrigReduce"), a0, a1);
  }

  // public static IAST NotFalseQ(final IExpr a0) {
  // return F.unaryAST1(F.$rubi("NotFalseQ"), a0);
  // }

  public static IAST NotIntegrableQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NotIntegrableQ"), a0, a1);
  }

  public static IAST NumericFactor(final IExpr a0) {
    return F.unaryAST1(F.$rubi("NumericFactor"), a0);
  }

  public static IAST NthRoot(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("NthRoot"), a0, a1);
  }

  public static IAST OddHyperbolicPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("OddHyperbolicPowerQ"), a0, a1, a2);
  }

  public static IAST OddQuotientQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("OddQuotientQ"), a0, a1);
  }

  public static IAST OddTrigPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("OddTrigPowerQ"), a0, a1, a2);
  }

  // public static IAST OneQ(final IExpr a0) {
  // return F.unaryAST1(F.$rubi("OneQ"), a0);
  // }
  //
  // public static IAST OneQ(final IExpr... a) {
  // return function(F.$rubi("OneQ"), a);
  // }

  public static IAST PerfectPowerTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PerfectPowerTest"), a0, a1);
  }

  public static IAST PerfectSquareQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("PerfectSquareQ"), a0);
  }

  public static IAST PiecewiseLinearQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PiecewiseLinearQ"), a0, a1);
  }

  public static IAST PiecewiseLinearQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PiecewiseLinearQ"), a0, a1, a2);
  }

  public static IAST PolyGCD(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolyGCD"), a0, a1, a2);
  }

  private static final class PolyQ extends B2 {
    public PolyQ() {
      super();
    }

    PolyQ(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return PolyQ;
    }

    @Override
    public IASTMutable copy() {
      return new PolyQ(arg1, arg2);
    }
  }

  public static IAST PolyQ(final IExpr a0, final IExpr a1) {
    return new PolyQ(a0, a1);
  }

  public static IAST PolyQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(PolyQ, a0, a1, a2);
  }

  public static IAST PolynomialDivide(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolynomialDivide"), a0, a1, a2);
  }

  public static IAST PolynomialDivide(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("PolynomialDivide"), a0, a1, a2, a3);
  }

  public static IAST PolynomialInAuxQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolynomialInAuxQ"), a0, a1, a2);
  }

  public static IAST PolynomialInQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolynomialInQ"), a0, a1, a2);
  }

  public static IAST PolynomialInSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolynomialInSubst"), a0, a1, a2);
  }

  public static IAST PolynomialInSubstAux(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PolynomialInSubstAux"), a0, a1, a2);
  }

  public static IAST PolynomialTermQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PolynomialTermQ"), a0, a1);
  }

  public static IAST PolynomialTerms(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PolynomialTerms"), a0, a1);
  }

  public static IAST PosAux(final IExpr a0) {
    return F.unaryAST1(F.$rubi("PosAux"), a0);
  }

  public static IAST PosQ(final IExpr a0) {
    return F.unaryAST1(PosQ, a0);
  }

  public static IAST PositiveFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("PositiveFactors"), a0);
  }

  public static IAST PowerOfLinearMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PowerOfLinearMatchQ"), a0, a1);
  }

  public static IAST PowerOfLinearQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PowerOfLinearQ"), a0, a1);
  }

  public static IAST PowerQ(final IExpr a0) {
    return F.unaryAST1(PowerQ, a0);
  }

  public static IAST PowerVariableDegree(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("PowerVariableDegree"), a0, a1, a2, a3);
  }

  public static IAST PowerVariableExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PowerVariableExpn"), a0, a1, a2);
  }

  public static IAST PowerVariableSubst(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PowerVariableSubst"), a0, a1, a2);
  }

  public static IAST PowerOfInertTrigSumQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PowerOfInertTrigSumQ"), a0, a1, a2);
  }

  public static IAST ProductOfLinearPowersQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ProductOfLinearPowersQ"), a0, a1);
  }

  public static IAST ProductQ(final IExpr a0) {
    return F.unaryAST1(ProductQ, a0);
  }

  public static IAST ProperPolyQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ProperPolyQ"), a0, a1);
  }

  public static IAST PseudoBinomialParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PseudoBinomialParts"), a0, a1);
  }

  public static IAST PseudoBinomialPairQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PseudoBinomialPairQ"), a0, a1, a2);
  }

  public static IAST PseudoBinomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("PseudoBinomialQ"), a0, a1);
  }

  public static IAST PureFunctionOfCosQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfCosQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfCoshQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfCoshQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfCotQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfCotQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfCothQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfCothQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfSinQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfSinQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfSinhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfSinhQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfTanQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfTanQ"), a0, a1, a2);
  }

  public static IAST PureFunctionOfTanhQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("PureFunctionOfTanhQ"), a0, a1, a2);
  }

  public static IAST QuadraticMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuadraticMatchQ"), a0, a1);
  }

  public static IAST QuadraticQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuadraticQ"), a0, a1);
  }

  public static IAST QuotientOfLinearsMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuotientOfLinearsMatchQ"), a0, a1);
  }

  public static IAST QuotientOfLinearsP(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuotientOfLinearsP"), a0, a1);
  }

  public static IAST QuotientOfLinearsParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuotientOfLinearsParts"), a0, a1);
  }

  public static IAST QuotientOfLinearsQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuotientOfLinearsQ"), a0, a1);
  }

  public static IAST QuadraticProductQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("QuadraticProductQ"), a0, a1);
  }

  public static IAST RationalFunctionExpand(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RationalFunctionExpand"), a0, a1);
  }

  public static IAST RationalFunctionExponents(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RationalFunctionExponents"), a0, a1);
  }

  public static IAST RationalFunctionFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RationalFunctionFactors"), a0, a1);
  }

  public static IAST RationalFunctionQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RationalFunctionQ"), a0, a1);
  }

  public static IAST RationalPowerQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("RationalPowerQ"), a0);
  }

  public static IAST RationalQ(final IExpr... a) {
    return function(F.$rubi("RationalQ"), a);
  }

  // public static IAST RealNumericQ(final IExpr a0) {
  // return F.unaryAST1(F.$rubi("RealNumericQ"), a0);
  // }

  public static IAST RealQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("RealQ"), a0);
  }

  public static IAST ReapList(final IExpr a0) {
    return F.unaryAST1(ReapList, a0);
  }

  public static IAST RectifyCotangent(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("RectifyCotangent"), a0, a1, a2, a3);
  }

  public static IAST RectifyCotangent(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4) {
    return quinary(F.$rubi("RectifyCotangent"), a0, a1, a2, a3, a4);
  }

  public static IAST RectifyTangent(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("RectifyTangent"), a0, a1, a2, a3);
  }

  public static IAST RectifyTangent(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
      final IExpr a4) {
    return quinary(F.$rubi("RectifyTangent"), a0, a1, a2, a3, a4);
  }

  public static IAST ReduceInertTrig(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("ReduceInertTrig"), a0, a1);
  }

  public static IAST ReduceInertTrig(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("ReduceInertTrig"), a0, a1, a2);
  }

  public static IAST RemainingFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("RemainingFactors"), a0);
  }

  public static IAST RemainingTerms(final IExpr a0) {
    return F.unaryAST1(F.$rubi("RemainingTerms"), a0);
  }

  public static IAST RemoveContent(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RemoveContent"), a0, a1);
  }

  public static IAST RemoveContentAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RemoveContentAux"), a0, a1);
  }

  public static IAST Rt(final IExpr a0) {
    return F.unaryAST1(F.$rubi("Rt"), a0);
  }

  public static IAST Rt(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Rt"), a0, a1);
  }

  public static IAST RtAux(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("RtAux"), a0, a1);
  }

  public static IAST RuleName(final IExpr a0) {
    return F.unaryAST1(F.$rubi("RuleName"), a0);
  }

  public static IAST SecQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SecQ"), a0);
  }

  public static IAST SechQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SechQ"), a0);
  }

  public static IAST SignOfFactor(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SignOfFactor"), a0);
  }

  private static final class Simp1 extends B1 {
    public Simp1() {
      super();
    }

    Simp1(IExpr arg1) {
      super(arg1);
    }

    @Override
    public final ISymbol head() {
      return Simp;
    }

    @Override
    public IASTMutable copy() {
      return new Simp1(arg1);
    }
  }

  private static final class Simp extends B2 {
    public Simp() {
      super();
    }

    Simp(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return Simp;
    }

    @Override
    public IASTMutable copy() {
      return new Simp(arg1, arg2);
    }
  }

  private static final class Star extends B2 {
    public Star() {
      super();
    }

    Star(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return Star;
    }

    @Override
    public IASTMutable copy() {
      return new Star(arg1, arg2);
    }
  }

  public static IAST Simp(final IExpr a0) {
    return new Simp1(a0);
  }

  public static IAST Simp(final IExpr a0, final IExpr a1) {
    return new Simp(a0, a1);
  }

  public static IAST SimpFixFactor(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimpFixFactor"), a0, a1);
  }

  public static IAST SimpHelp(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimpHelp"), a0, a1);
  }

  public static IAST SimplerIntegrandQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SimplerIntegrandQ"), a0, a1, a2);
  }

  public static IAST SimplerQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplerQ"), a0, a1);
  }

  public static IAST SimplerSqrtQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplerSqrtQ"), a0, a1);
  }

  public static IAST SimplifyAntiderivative(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplifyAntiderivative"), a0, a1);
  }

  public static IAST SimplifyAntiderivativeSum(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplifyAntiderivativeSum"), a0, a1);
  }

  public static IAST SimplifyIntegrand(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplifyIntegrand"), a0, a1);
  }

  public static IAST SimplifyTerm(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SimplifyTerm"), a0, a1);
  }

  public static IAST Smallest(final IExpr a0) {
    return F.unaryAST1(F.$rubi("Smallest"), a0);
  }

  public static IAST Smallest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Smallest"), a0, a1);
  }

  public static IAST SmartApart(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SmartApart"), a0, a1);
  }

  public static IAST SmartApart(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SmartApart"), a0, a1, a2);
  }

  public static IAST SmartDenominator(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SmartDenominator"), a0);
  }

  public static IAST SmartNumerator(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SmartNumerator"), a0);
  }

  public static IAST SmartSimplify(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SmartSimplify"), a0);
  }

  public static IAST SomeNegTermQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SomeNegTermQ"), a0);
  }

  public static IAST SplitFreeFactors(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SplitFreeFactors"), a0, a1);
  }

  public static IAST SplitProduct(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SplitProduct"), a0, a1);
  }

  public static IAST SplitSum(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SplitSum"), a0, a1);
  }

  public static IAST SqrtNumberQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SqrtNumberQ"), a0);
  }

  public static IAST SqrtNumberSumQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SqrtNumberSumQ"), a0);
  }

  public static IAST SquareRootOfQuadraticSubst(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("SquareRootOfQuadraticSubst"), a0, a1, a2, a3);
  }

  public static IAST Star(final IExpr a0, final IExpr a1) {
    return new Star(a0, a1);
  }

  public static IAST StopFunctionQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("StopFunctionQ"), a0);
  }

  public static IAST Subst(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("Subst"), a0, a1);
  }

  public static IAST Subst(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("Subst"), a0, a1, a2);
  }

  public static IAST SubstAux(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstAux"), a0, a1, a2);
  }

  public static IAST SubstAux(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("SubstAux"), a0, a1, a2, a3);
  }

  public static IAST SubstFor(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstFor"), a0, a1, a2);
  }

  public static IAST SubstFor(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(F.$rubi("SubstFor"), a0, a1, a2, a3);
  }

  public static IAST SubstForAux(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstForAux"), a0, a1, a2);
  }

  public static IAST SubstForExpn(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstForExpn"), a0, a1, a2);
  }

  public static IAST SubstForFractionalPower(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4) {
    return quinary(F.$rubi("SubstForFractionalPower"), a0, a1, a2, a3, a4);
  }

  public static IAST SubstForFractionalPowerAuxQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstForFractionalPowerAuxQ"), a0, a1, a2);
  }

  public static IAST SubstForFractionalPowerOfLinear(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SubstForFractionalPowerOfLinear"), a0, a1);
  }

  public static IAST SubstForFractionalPowerOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SubstForFractionalPowerOfQuotientOfLinears"), a0, a1);
  }

  public static IAST SubstForFractionalPowerQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstForFractionalPowerQ"), a0, a1, a2);
  }

  public static IAST SubstForHyperbolic(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4) {
    return quinary(F.$rubi("SubstForHyperbolic"), a0, a1, a2, a3, a4);
  }

  public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("SubstForInverseFunction"), a0, a1, a2);
  }

  public static IAST SubstForInverseFunction(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(F.$rubi("SubstForInverseFunction"), a0, a1, a2, a3);
  }

  public static IAST SubstForInverseFunctionOfQuotientOfLinears(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SubstForInverseFunctionOfQuotientOfLinears"), a0, a1);
  }

  public static IAST SubstForTrig(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
      final IExpr a4) {
    return quinary(F.$rubi("SubstForTrig"), a0, a1, a2, a3, a4);
  }

  public static IAST SumBaseQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("SumBaseQ"), a0);
  }

  public static IAST SumQ(final IExpr a0) {
    return F.unaryAST1(SumQ, a0);
  }

  public static IAST SumSimplerAuxQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SumSimplerAuxQ"), a0, a1);
  }

  public static IAST SumSimplerQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("SumSimplerQ"), a0, a1);
  }

  public static IAST TanQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TanQ"), a0);
  }

  public static IAST TanhQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TanhQ"), a0);
  }

  public static IAST TogetherSimplify(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TogetherSimplify"), a0);
  }

  public static IAST TrigHyperbolicFreeQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrigHyperbolicFreeQ"), a0, a1);
  }

  public static IAST TrigQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigQ"), a0);
  }

  public static IAST TrigSimplify(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSimplify"), a0);
  }

  public static IAST TrigSimplifyAux(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSimplifyAux"), a0);
  }

  public static IAST TrigSimplifyQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSimplifyQ"), a0);
  }

  public static IAST TrigSimplifyRecur(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSimplifyRecur"), a0);
  }

  public static IAST TrigSquareQ(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSquareQ"), a0);
  }

  public static IAST TrinomialDegree(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrinomialDegree"), a0, a1);
  }

  public static IAST TrinomialMatchQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrinomialMatchQ"), a0, a1);
  }

  public static IAST TrinomialParts(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrinomialParts"), a0, a1);
  }

  public static IAST TrinomialQ(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrinomialQ"), a0, a1);
  }

  public static IAST TrinomialTest(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TrinomialTest"), a0, a1);
  }

  public static IAST TryPureTanSubst(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TryPureTanSubst"), a0, a1);
  }

  public static IAST TryPureTanhSubst(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TryPureTanhSubst"), a0, a1);
  }

  public static IAST TryTanhSubst(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("TryTanhSubst"), a0, a1);
  }

  public static IAST UnifyNegativeBaseFactors(final IExpr a0) {
    return F.unaryAST1(F.$rubi("UnifyNegativeBaseFactors"), a0);
  }

  public static IAST UnifySum(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("UnifySum"), a0, a1);
  }

  public static IAST UnifyTerm(final IExpr a0, final IExpr a1, final IExpr a2) {
    return F.ternaryAST3(F.$rubi("UnifyTerm"), a0, a1, a2);
  }

  public static IAST UnifyTerms(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("UnifyTerms"), a0, a1);
  }

  private static final class Unintegrable extends B2 {
    public Unintegrable() {
      super();
    }

    Unintegrable(IExpr arg1, IExpr arg2) {
      super(arg1, arg2);
    }

    @Override
    public final ISymbol head() {
      return Unintegrable;
    }

    @Override
    public IASTMutable copy() {
      return new Unintegrable(arg1, arg2);
    }
  }

  public static IAST Unintegrable(final IExpr a0, final IExpr a1) {
    return new Unintegrable(a0, a1);
  }

  public static IAST UnifyInertTrigFunction(final IExpr a0, final IExpr a1) {
    return F.binaryAST2(F.$rubi("UnifyInertTrigFunction"), a0, a1);
  }

  public static IAST TrigSquare(final IExpr a0) {
    return F.unaryAST1(F.$rubi("TrigSquare"), a0);
  }

  /**
   *
   *
   * <pre>
   * w_*Dist[u_,v_,x_] :=
   *     Dist[w*u,v,x] /;
   *     w=!=-1
   * </pre>
   *
   * @param astTimes
   * @param engine TODO
   * @return
   */
  public static IExpr evalRubiDistTimes(IAST astTimes, EvalEngine engine) {
    for (int i = 1; i < astTimes.size(); i++) {
      IExpr temp = astTimes.get(i);
      if (temp.isAST(Dist) && temp.size() == 4) {
        IAST dist = engine.evalArgs((IAST) temp, ISymbol.NOATTRIBUTE, false).orElse((IAST) temp);
        temp = astTimes.splice(i).oneIdentity1();
        temp = engine.evaluate(temp);
        if (!temp.isMinusOne()) {
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
   * @param astPlus
   * @param engine TODO
   * @return
   */
  public static IExpr evalRubiDistPlus(IAST astPlus, EvalEngine engine) {
    for (int i = 1; i < astPlus.argSize(); i++) {
      IExpr arg1 = astPlus.get(i);
      if (arg1.isAST(Dist) && arg1.size() == 4) {
        // dist1 = Dist[u_,v_,x_]
        IAST dist1 = engine.evalArgs((IAST) arg1, ISymbol.NOATTRIBUTE, false).orElse((IAST) arg1); // (IAST)
        // arg1;
        IExpr v = dist1.arg2();
        IExpr x = dist1.arg3();
        for (int j = i + 1; j < astPlus.size(); j++) {
          IExpr arg2 = astPlus.get(j);
          if (arg2.isAST(Dist) && arg2.size() == 4 && arg2.getAt(2).equals(v)
              && arg2.getAt(3).equals(x)) {
            // dist2=Dist[w_,v_,x_]
            IAST dist2 =
                engine.evalArgs((IAST) arg2, ISymbol.NOATTRIBUTE, false).orElse((IAST) arg2); // (IAST)
            // arg2;
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
          if (arg2.isTimes2() && arg2.first().isMinusOne() && arg2.second().isAST(Dist)) {
            // -1 * Dist[w_,v_,x_]
            IAST dist2 = engine.evalArgs((IAST) arg2.second(), ISymbol.NOATTRIBUTE, false)
                .orElse((IAST) arg2.second()); // (IAST) arg2.second();
            if (dist2.size() == 4 && dist2.getAt(2).equals(v) && dist2.getAt(3).equals(x)) {
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
      } else if (arg1.isTimes2() && arg1.first().isMinusOne() && arg1.second().isAST(Dist)) {
        // -1 * Dist[w_,v_,x_]
        IAST dist1 = engine.evalArgs((IAST) arg1.second(), ISymbol.NOATTRIBUTE, false)
            .orElse((IAST) arg1.second()); // (IAST)
        // arg1.second();
        IExpr v = dist1.arg2();
        IExpr x = dist1.arg3();
        for (int j = i + 1; j < astPlus.size(); j++) {
          IExpr arg2 = astPlus.get(j);
          if (arg2.isAST(Dist) && arg2.size() == 4 && arg2.getAt(2).equals(v)
              && arg2.getAt(3).equals(x)) {
            // dist2 = Dist[u_,v_,x_]
            IAST dist2 =
                engine.evalArgs((IAST) arg2, ISymbol.NOATTRIBUTE, false).orElse((IAST) arg2); // (IAST)
            // arg2;
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

  public static void getRuleASTRubi45() {
    IAST init;
    init = org.matheclipse.core.integrate.rubi.IntRules0.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules1.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules2.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules3.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules4.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules5.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules6.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules7.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules8.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules9.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules10.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules11.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules12.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules13.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules14.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules15.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules16.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules17.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules18.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules19.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules20.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules21.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules22.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules23.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules24.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules25.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules26.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules27.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules28.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules29.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules30.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules31.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules32.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules33.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules34.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules35.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules36.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules37.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules38.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules39.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules40.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules41.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules42.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules43.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules44.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules45.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules46.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules47.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules48.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules49.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules50.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules51.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules52.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules53.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules54.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules55.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules56.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules57.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules58.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules59.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules60.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules61.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules62.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules63.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules64.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules65.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules66.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules67.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules68.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules69.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules70.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules71.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules72.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules73.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules74.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules75.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules76.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules77.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules78.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules79.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules80.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules81.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules82.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules83.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules84.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules85.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules86.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules87.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules88.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules89.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules90.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules91.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules92.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules93.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules94.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules95.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules96.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules97.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules98.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules99.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules100.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules101.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules102.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules103.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules104.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules105.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules106.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules107.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules108.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules109.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules110.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules111.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules112.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules113.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules114.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules115.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules116.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules117.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules118.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules119.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules120.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules121.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules122.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules123.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules124.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules125.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules126.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules127.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules128.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules129.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules130.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules131.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules132.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules133.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules134.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules135.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules136.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules137.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules138.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules139.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules140.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules141.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules142.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules143.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules144.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules145.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules146.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules147.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules148.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules149.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules150.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules151.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules152.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules153.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules154.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules155.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules156.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules157.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules158.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules159.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules160.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules161.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules162.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules163.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules164.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules165.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules166.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules167.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules168.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules169.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules170.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules171.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules172.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules173.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules174.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules175.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules176.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules177.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules178.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules179.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules180.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules181.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules182.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules183.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules184.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules185.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules186.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules187.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules188.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules189.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules190.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules191.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules192.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules193.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules194.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules195.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules196.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules197.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules198.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules199.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules200.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules201.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules202.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules203.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules204.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules205.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules206.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules207.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules208.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules209.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules210.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules211.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules212.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules213.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules214.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules215.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules216.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules217.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules218.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules219.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules220.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules221.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules222.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules223.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules224.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules225.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules226.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules227.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules228.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules229.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules230.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules231.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules232.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules233.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules234.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules235.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules236.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules237.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules238.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules239.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules240.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules241.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules242.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules243.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules244.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules245.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules246.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules247.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules248.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules249.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules250.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules251.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules252.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules253.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules254.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules255.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules256.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules257.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules258.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules259.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules260.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules261.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules262.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules263.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules264.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules265.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules266.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules267.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules268.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules269.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules270.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules271.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules272.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules273.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules274.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules275.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules276.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules277.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules278.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules279.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules280.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules281.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules282.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules283.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules284.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules285.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules286.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules287.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules288.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules289.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules290.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules291.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules292.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules293.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules294.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules295.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules296.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules297.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules298.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules299.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules300.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules301.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules302.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules303.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules304.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules305.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules306.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules307.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules308.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules309.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules310.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules311.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules312.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules313.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules314.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules315.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules316.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules317.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules318.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules319.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules320.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules321.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules322.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules323.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules324.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules325.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules326.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules327.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules328.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules329.RULES;

    init = org.matheclipse.core.integrate.rubi.IntRules330.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules331.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules332.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules333.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules334.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules335.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules336.RULES;
    init = org.matheclipse.core.integrate.rubi.IntRules337.RULES;

  }

  public static void getUtilityFunctionsRuleASTRubi45() {
    IAST ast = org.matheclipse.core.integrate.rubi.UtilityFunctions0.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions1.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions2.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions3.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions4.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions5.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions6.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions7.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions8.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions9.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions10.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions11.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions12.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions13.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions14.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions15.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions16.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions17.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions18.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions19.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions20.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions21.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions22.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions23.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions24.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions25.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions26.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions27.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions28.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions29.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions30.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions31.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions32.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions33.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions34.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions35.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions36.RULES;
    ast = org.matheclipse.core.integrate.rubi.UtilityFunctions37.RULES;
    // ast = org.matheclipse.core.integrate.rubi.UtilityFunctions38.RULES;
    // ast = org.matheclipse.core.integrate.rubi.UtilityFunctions39.RULES;

    // org.matheclipse.core.integrate.rubi.UtilityFunctions.init();
  }

  /**
   * Register classes for Kryo serializer.
   * 
   * @param kryo
   * @throws ClassNotFoundException
   */
  public static void registerKryo(Kryo kryo) throws ClassNotFoundException {
    kryo.register(EqQ.class, new KryoUtil.IASTSerializer());
    kryo.register(FalseQ.class, new KryoUtil.IASTSerializer());
    kryo.register(FracPart.class, new KryoUtil.IASTSerializer());
    kryo.register(GeQ.class, new KryoUtil.IASTSerializer());
    kryo.register(GtQ.class, new KryoUtil.IASTSerializer());
    kryo.register(IGtQ.class, new KryoUtil.IASTSerializer());
    kryo.register(ILtQ.class, new KryoUtil.IASTSerializer());
    kryo.register(IntPart.class, new KryoUtil.IASTSerializer());
    kryo.register(LeQ.class, new KryoUtil.IASTSerializer());
    kryo.register(LtQ.class, new KryoUtil.IASTSerializer());
    kryo.register(NeQ.class, new KryoUtil.IASTSerializer());
    kryo.register(PolyQ.class, new KryoUtil.IASTSerializer());
    kryo.register(Simp.class, new KryoUtil.IASTSerializer());
    kryo.register(Unintegrable.class, new KryoUtil.IASTSerializer());
  }
}
