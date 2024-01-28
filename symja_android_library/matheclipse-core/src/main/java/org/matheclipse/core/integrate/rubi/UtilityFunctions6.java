package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.Im;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.N;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.NumberQ;
import static org.matheclipse.core.expression.F.NumericQ;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Re;
import static org.matheclipse.core.expression.F.Refine;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Complex;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NiceSqrtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProperPolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TogetherSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions6 { 
  public static IAST RULES = List( 
ISetDelayed(48,PolyQ(u_,Power(x_Symbol,v_),n_),
    And(PolyQ(u,Power(x,v)),EqQ(Expon(u,Power(x,v)),n),NeQ(Coeff(u,Power(x,v),n),C0))),
ISetDelayed(49,ProperPolyQ(u_,x_Symbol),
    And(PolyQ(u,x),NeQ(Coeff(u,x,C0),C0))),
ISetDelayed(50,BinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(BinomialParts(u,x)))),
ISetDelayed(51,BinomialQ(u_,x_Symbol,n_),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x,n)),Throw(False))),u),True)),$(Function(And(ListQ(Slot1),SameQ(Part(Slot1,C3),n))),BinomialParts(u,x)))),
ISetDelayed(52,TrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(TrinomialQ(Slot1,x)),Throw(False))),u),True)),And(ListQ(TrinomialParts(u,x)),Not(QuadraticQ(u,x)),Not(MatchQ(u,Condition(Sqr(w_),BinomialQ(w,x))))))),
ISetDelayed(53,GeneralizedBinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedBinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedBinomialParts(u,x)))),
ISetDelayed(54,GeneralizedTrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedTrinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedTrinomialParts(u,x)))),
ISetDelayed(55,PosQ(u_),
    PosAux(TogetherSimplify(u))),
ISetDelayed(56,PosAux(u_),
    If(NumberQ(u),If(SameQ(Head(u),Complex),If(EqQ(Re(u),C0),PosAux(Im(u)),PosAux(Re(u))),Greater(u,C0)),If(NumericQ(u),With(list(Set(v,Simplify(Re(u)))),If(NumberQ(v),If(EqQ(v,C0),PosAux(Simplify(Im(u))),Greater(v,C0)),With(list(Set(w,N(u))),And(NumberQ(w),PosAux(w))))),With(list(Set(v,Refine(Greater(u,C0)))),If(Or(SameQ(v,True),SameQ(v,False)),v,If(PowerQ(u),If(IntegerQ(Part(u,C2)),Or(EvenQ(Part(u,C2)),PosAux(Part(u,C1))),True),If(ProductQ(u),If(PosAux(First(u)),PosAux(Rest(u)),Not(PosAux(Rest(u)))),If(SumQ(u),PosAux(First(u)),True)))))))),
ISetDelayed(57,NegQ(u_),
    And(Not(PosQ(u)),NeQ(u,C0))),
ISetDelayed(58,NiceSqrtQ(u_),
          If(RationalQ(u), Greater(u, C0), Not($($s("§fractionalpowerfactorq"), Rt(u, C2)))))
  );
}
