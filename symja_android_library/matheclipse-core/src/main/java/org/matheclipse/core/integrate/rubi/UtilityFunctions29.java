package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.Block;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Drop;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Switch;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.TrueQ;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Cosh;
import static org.matheclipse.core.expression.S.Coth;
import static org.matheclipse.core.expression.S.Csch;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.Sech;
import static org.matheclipse.core.expression.S.Sinh;
import static org.matheclipse.core.expression.S.Tanh;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CommonFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DivideDegreesOfFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponential;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialFunction;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialFunctionAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialTest;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfInverseLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfLinearSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegralFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadBase;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MonomialFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions29 { 
  public static IAST RULES = List( 
ISetDelayed(391,Star(u_,v_),
    Condition(Times(u,v),Or(Not(TrueQ($s("§$showsteps"))),EqQ(Sqr(u),C1),IntegralFreeQ(v)))),
ISetDelayed(392,Star(u_,Times(v_,w_)),
    Condition(Star(Times(u,v),w),IntegralFreeQ(v))),
ISetDelayed(393,FunctionOfLinear(u_,x_Symbol),
    With(list(Set($s("lst"),FunctionOfLinear(u,False,False,x,False))),If(Or(AtomQ($s("lst")),FalseQ(Part($s("lst"),C1)),And(SameQ(Part($s("lst"),C1),C0),SameQ(Part($s("lst"),C2),C1))),False,list(FunctionOfLinearSubst(u,Part($s("lst"),C1),Part($s("lst"),C2),x),Part($s("lst"),C1),Part($s("lst"),C2))))),
ISetDelayed(394,FunctionOfLinear(u_,a_,b_,x_,$p("flag")),
    If(FreeQ(u,x),list(a,b),If(CalculusQ(u),False,If(LinearQ(u,x),If(FalseQ(a),list(Coefficient(u,x,C0),Coefficient(u,x,C1)),With(list(Set($s("lst"),CommonFactors(list(b,Coefficient(u,x,C1))))),If(And(EqQ(Coefficient(u,x,C0),C0),Not($s("flag"))),list(C0,Part($s("lst"),C1)),If(EqQ(Subtract(Times(b,Coefficient(u,x,C0)),Times(a,Coefficient(u,x,C1))),C0),list(Times(a,Power(Part($s("lst"),C2),CN1)),Part($s("lst"),C1)),list(C0,C1))))),If(And(PowerQ(u),FreeQ(Part(u,C1),x)),FunctionOfLinear(Times(Log(Part(u,C1)),Part(u,C2)),a,b,x,False),Module(list($s("lst")),If(And(ProductQ(u),NeQ(Part(Set($s("lst"),MonomialFactor(u,x)),C1),C0)),If(And(False,IntegerQ(Part($s("lst"),C1)),Unequal(Part($s("lst"),C1),CN1),FreeQ(Part($s("lst"),C2),x)),If(And(RationalQ(LeadFactor(Part($s("lst"),C2))),Less(LeadFactor(Part($s("lst"),C2)),C0)),FunctionOfLinear(Times(DivideDegreesOfFactors(Negate(Part($s("lst"),C2)),Part($s("lst"),C1)),x),a,b,x,False),FunctionOfLinear(Times(DivideDegreesOfFactors(Part($s("lst"),C2),Part($s("lst"),C1)),x),a,b,x,False)),False),CompoundExpression(Set($s("lst"),list(a,b)),Catch(CompoundExpression(Scan(Function(CompoundExpression(Set($s("lst"),FunctionOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x,SumQ(u))),If(AtomQ($s("lst")),Throw(False)))),u),$s("lst"))))))))))),
ISetDelayed(395,FunctionOfLinearSubst(u_,a_,b_,x_),
    If(FreeQ(u,x),u,If(LinearQ(u,x),Module(list(Set($s("tmp"),Coefficient(u,x,C1))),CompoundExpression(Set($s("tmp"),If(SameQ($s("tmp"),b),C1,Times($s("tmp"),Power(b,CN1)))),Plus(Coefficient(u,x,C0),Times(CN1,a,$s("tmp")),Times($s("tmp"),x)))),If(And(PowerQ(u),FreeQ(Part(u,C1),x)),Exp(FullSimplify(FunctionOfLinearSubst(Times(Log(Part(u,C1)),Part(u,C2)),a,b,x))),Module(list($s("lst")),If(And(ProductQ(u),NeQ(Part(Set($s("lst"),MonomialFactor(u,x)),C1),C0)),If(And(RationalQ(LeadFactor(Part($s("lst"),C2))),Less(LeadFactor(Part($s("lst"),C2)),C0)),Negate(Power(FunctionOfLinearSubst(Times(DivideDegreesOfFactors(Negate(Part($s("lst"),C2)),Part($s("lst"),C1)),x),a,b,x),Part($s("lst"),C1))),Power(FunctionOfLinearSubst(Times(DivideDegreesOfFactors(Part($s("lst"),C2),Part($s("lst"),C1)),x),a,b,x),Part($s("lst"),C1))),Map(Function(FunctionOfLinearSubst(Slot1,a,b,x)),u))))))),
ISetDelayed(396,DivideDegreesOfFactors(u_,n_),
    If(ProductQ(u),Map(Function(Power(LeadBase(Slot1),Times(LeadDegree(Slot1),Power(n,CN1)))),u),Power(LeadBase(u),Times(LeadDegree(u),Power(n,CN1))))),
ISetDelayed(397,FunctionOfInverseLinear(u_,x_Symbol),
    FunctionOfInverseLinear(u,Null,x)),
ISetDelayed(398,FunctionOfInverseLinear(u_,$p("lst"),x_),
    If(FreeQ(u,x),$s("lst"),If(SameQ(u,x),False,If(QuotientOfLinearsQ(u,x),With(list(Set($s("tmp"),Drop(QuotientOfLinearsParts(u,x),C2))),If(SameQ(Part($s("tmp"),C2),C0),False,If(SameQ($s("lst"),Null),$s("tmp"),If(EqQ(Subtract(Times(Part($s("lst"),C1),Part($s("tmp"),C2)),Times(Part($s("lst"),C2),Part($s("tmp"),C1))),C0),$s("lst"),False)))),If(CalculusQ(u),False,Module(list(Set($s("tmp"),$s("lst"))),Catch(CompoundExpression(Scan(Function(If(AtomQ(Set($s("tmp"),FunctionOfInverseLinear(Slot1,$s("tmp"),x))),Throw(False))),u),$s("tmp"))))))))),
ISetDelayed(399,FunctionOfExponentialQ(u_,x_Symbol),
    Block(list(Set($s("$base$"),Null),Set($s("$expon$"),Null),Set($s("§$exponflag$"),False)),And(FunctionOfExponentialTest(u,x),$s("§$exponflag$")))),
ISetDelayed(400,FunctionOfExponential(u_,x_Symbol),
    Block(list(Set($s("$base$"),Null),Set($s("$expon$"),Null),Set($s("§$exponflag$"),False)),CompoundExpression(FunctionOfExponentialTest(u,x),Power($s("$base$"),$s("$expon$"))))),
ISetDelayed(401,FunctionOfExponentialFunction(u_,x_Symbol),
    Block(list(Set($s("$base$"),Null),Set($s("$expon$"),Null),Set($s("§$exponflag$"),False)),CompoundExpression(FunctionOfExponentialTest(u,x),SimplifyIntegrand(FunctionOfExponentialFunctionAux(u,x),x)))),
ISetDelayed(402,FunctionOfExponentialFunctionAux(u_,x_),
          If(AtomQ(u), u, If(And(PowerQ(u), FreeQ(Part(u, C1), x), LinearQ(Part(u, C2), x)),
              If(EqQ(Coefficient($s("$expon$"), x, C0), C0),
                  Times(Power(Part(u, C1), Coefficient(Part(u, C2), x, C0)), Power(x,
                      FullSimplify(Times(Log(Part(u, C1)), Coefficient(Part(u, C2), x, C1),
                          Power(Times(Log($s("$base$")), Coefficient($s("$expon$"), x, C1)),
                              CN1))))),
                  Power(x,
                      FullSimplify(
                          Times(Log(Part(u, C1)), Coefficient(Part(u, C2), x, C1),
                              Power(
                                  Times(Log($s("$base$")),
                                      Coefficient($s("$expon$"), x, C1)),
                                  CN1))))),
              If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)),
                  Module(list($s("tmp")), CompoundExpression(
                      Set($s("tmp"),
                          Power(x,
                              FullSimplify(Times(Coefficient(Part(u, C1), x, C1),
                                  Power(Times(Log($s("$base$")), Coefficient($s("$expon$"), x, C1)),
                                      CN1))))),
                      Switch(Head(u), Sinh,
                          Subtract(Times(C1D2, $s("tmp")), Power(Times(C2, $s("tmp")), CN1)), Cosh,
                          Plus(Times(C1D2, $s("tmp")), Power(Times(C2, $s("tmp")), CN1)), Tanh,
                          Times(Subtract($s("tmp"), Power($s("tmp"), CN1)),
                              Power(Plus($s("tmp"), Power($s("tmp"), CN1)), CN1)),
                          Coth,
                          Times(Plus($s("tmp"), Power($s("tmp"), CN1)),
                              Power(Subtract($s("tmp"), Power($s("tmp"), CN1)), CN1)),
                          Sech, Times(C2, Power(Plus($s("tmp"), Power($s("tmp"), CN1)), CN1)), Csch,
                          Times(C2, Power(Subtract($s("tmp"), Power($s("tmp"), CN1)), CN1))))),
                  If(And(PowerQ(u), FreeQ(Part(u, C1), x), SumQ(Part(u, C2))), Times(
                      FunctionOfExponentialFunctionAux(Power(Part(u, C1), First(Part(u, C2))), x),
                      FunctionOfExponentialFunctionAux(Power(Part(u, C1), Rest(Part(u, C2))), x)),
                      Map(Function(FunctionOfExponentialFunctionAux(Slot1, x)), u))))))
  );
}
