package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$b;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Optional;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Switch;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Cos;
import static org.matheclipse.core.expression.S.Cosh;
import static org.matheclipse.core.expression.S.Cot;
import static org.matheclipse.core.expression.S.Coth;
import static org.matheclipse.core.expression.S.Csc;
import static org.matheclipse.core.expression.S.Csch;
import static org.matheclipse.core.expression.S.E;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.GCD;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.Sec;
import static org.matheclipse.core.expression.S.Sech;
import static org.matheclipse.core.expression.S.Sin;
import static org.matheclipse.core.expression.S.Sinh;
import static org.matheclipse.core.expression.S.Tan;
import static org.matheclipse.core.expression.S.Tanh;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicTrigFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfCosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfCoshQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExpnQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialTest;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfExponentialTestAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfHyperbolic;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfSinQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfSinhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTanQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTanhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTrigOfLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfCosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfCoshQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfCotQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfCothQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfSinQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfSinhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfTanQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfTanhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions30 { 
  public static IAST RULES = List( 
ISetDelayed(403,FunctionOfExponentialTest(u_,x_),
    If(FreeQ(u,x),True,If(Or(SameQ(u,x),CalculusQ(u)),False,If(And(PowerQ(u),FreeQ(Part(u,C1),x),LinearQ(Part(u,C2),x)),CompoundExpression(Set($s("§$exponflag$"),True),FunctionOfExponentialTestAux(Part(u,C1),Part(u,C2),x)),If(And(HyperbolicQ(u),LinearQ(Part(u,C1),x)),FunctionOfExponentialTestAux(E,Part(u,C1),x),If(And(PowerQ(u),FreeQ(Part(u,C1),x),SumQ(Part(u,C2))),And(FunctionOfExponentialTest(Power(Part(u,C1),First(Part(u,C2))),x),FunctionOfExponentialTest(Power(Part(u,C1),Rest(Part(u,C2))),x)),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfExponentialTest(Slot1,x)),Throw(False))),u),True)))))))),
ISetDelayed(404,FunctionOfExponentialTestAux($p("base"),$p("expon"),x_),
    If(SameQ($s("$base$"),Null),CompoundExpression(Set($s("$base$"),$s("base")),Set($s("$expon$"),$s("expon")),True),Module(list($s("tmp")),CompoundExpression(Set($s("tmp"),FullSimplify(Times(Log($s("base")),Coefficient($s("expon"),x,C1),Power(Times(Log($s("$base$")),Coefficient($s("$expon$"),x,C1)),CN1)))),If(Not(RationalQ($s("tmp"))),False,If(Or(EqQ(Coefficient($s("$expon$"),x,C0),C0),NeQ($s("tmp"),FullSimplify(Times(Log($s("base")),Coefficient($s("expon"),x,C0),Power(Times(Log($s("$base$")),Coefficient($s("$expon$"),x,C0)),CN1))))),CompoundExpression(If(And(IGtQ($s("base"),C0),IGtQ($s("$base$"),C0),Less($s("base"),$s("$base$"))),CompoundExpression(Set($s("$base$"),$s("base")),Set($s("$expon$"),$s("expon")),Set($s("tmp"),Power($s("tmp"),CN1)))),Set($s("$expon$"),Times(Coefficient($s("$expon$"),x,C1),x,Power(Denominator($s("tmp")),CN1))),If(And(Less($s("tmp"),C0),NegQ(Coefficient($s("$expon$"),x,C1))),CompoundExpression(Set($s("$expon$"),Negate($s("$expon$"))),True),True)),CompoundExpression(If(And(IGtQ($s("base"),C0),IGtQ($s("$base$"),C0),Less($s("base"),$s("$base$"))),CompoundExpression(Set($s("$base$"),$s("base")),Set($s("$expon$"),$s("expon")),Set($s("tmp"),Power($s("tmp"),CN1)))),Set($s("$expon$"),Times($s("$expon$"),Power(Denominator($s("tmp")),CN1))),If(And(Less($s("tmp"),C0),NegQ(Coefficient($s("$expon$"),x,C1))),CompoundExpression(Set($s("$expon$"),Negate($s("$expon$"))),True),True)))))))),
ISetDelayed(405,FunctionOfTrigOfLinearQ(u_,x_Symbol),
    If(MatchQ(u,Condition(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("§trig"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),n_DEFAULT)),And(FreeQ(List(a,b,c,d,e,f,m,n),x),Or(TrigQ($s("§trig")),HyperbolicQ($s("§trig")))))),True,And(Not(MemberQ(list(Null,False),FunctionOfTrig(u,Null,x))),AlgebraicTrigFunctionQ(u,x)))),
ISetDelayed(406,FunctionOfTrig(u_,x_Symbol),
    With(list(Set(v,FunctionOfTrig(ActivateTrig(u),Null,x))),If(SameQ(v,Null),False,v))),
ISetDelayed(407,FunctionOfTrig(u_,v_,x_),
    If(AtomQ(u),If(SameQ(u,x),False,v),If(And(TrigQ(u),LinearQ(Part(u,C1),x)),If(SameQ(v,Null),Part(u,C1),With(List(Set(a,Coefficient(v,x,C0)),Set(b,Coefficient(v,x,C1)),Set(c,Coefficient(Part(u,C1),x,C0)),Set(d,Coefficient(Part(u,C1),x,C1))),If(And(EqQ(Subtract(Times(a,d),Times(b,c)),C0),RationalQ(Times(b,Power(d,CN1)))),Plus(Times(a,Power(Numerator(Times(b,Power(d,CN1))),CN1)),Times(b,x,Power(Numerator(Times(b,Power(d,CN1))),CN1))),False))),If(And(HyperbolicQ(u),LinearQ(Part(u,C1),x)),If(SameQ(v,Null),Times(CI,Part(u,C1)),With(List(Set(a,Coefficient(v,x,C0)),Set(b,Coefficient(v,x,C1)),Set(c,Times(CI,Coefficient(Part(u,C1),x,C0))),Set(d,Times(CI,Coefficient(Part(u,C1),x,C1)))),If(And(EqQ(Subtract(Times(a,d),Times(b,c)),C0),RationalQ(Times(b,Power(d,CN1)))),Plus(Times(a,Power(Numerator(Times(b,Power(d,CN1))),CN1)),Times(b,x,Power(Numerator(Times(b,Power(d,CN1))),CN1))),False))),If(CalculusQ(u),False,Module(list(Set(w,v)),Catch(CompoundExpression(Scan(Function(If(FalseQ(Set(w,FunctionOfTrig(Slot1,w,x))),Throw(False))),u),w)))))))),
ISetDelayed(408,AlgebraicTrigFunctionQ(u_,x_Symbol),
    If(AtomQ(u),True,If(And(TrigQ(u),LinearQ(Part(u,C1),x)),True,If(And(HyperbolicQ(u),LinearQ(Part(u,C1),x)),True,If(And(PowerQ(u),FreeQ(Part(u,C2),x)),AlgebraicTrigFunctionQ(Part(u,C1),x),If(Or(ProductQ(u),SumQ(u)),Catch(CompoundExpression(Scan(Function(If(Not(AlgebraicTrigFunctionQ(Slot1,x)),Throw(False))),u),True)),False)))))),
ISetDelayed(409,FunctionOfHyperbolic(u_,x_Symbol),
    With(list(Set(v,FunctionOfHyperbolic(u,Null,x))),If(SameQ(v,Null),False,v))),
ISetDelayed(410,FunctionOfHyperbolic(u_,v_,x_),
    If(AtomQ(u),If(SameQ(u,x),False,v),If(And(HyperbolicQ(u),LinearQ(Part(u,C1),x)),If(SameQ(v,Null),Part(u,C1),With(List(Set(a,Coefficient(v,x,C0)),Set(b,Coefficient(v,x,C1)),Set(c,Coefficient(Part(u,C1),x,C0)),Set(d,Coefficient(Part(u,C1),x,C1))),If(And(EqQ(Subtract(Times(a,d),Times(b,c)),C0),RationalQ(Times(b,Power(d,CN1)))),Plus(Times(a,Power(Numerator(Times(b,Power(d,CN1))),CN1)),Times(b,x,Power(Numerator(Times(b,Power(d,CN1))),CN1))),False))),If(CalculusQ(u),False,Module(list(Set(w,v)),Catch(CompoundExpression(Scan(Function(If(FalseQ(Set(w,FunctionOfHyperbolic(Slot1,w,x))),Throw(False))),u),w))))))),
ISetDelayed(411,FunctionOfQ(v_,u_,x_Symbol,Optional($p("§pureflag"),False)),
    If(FreeQ(u,x),False,If(AtomQ(v),True,If(Not(InertTrigFreeQ(u)),FunctionOfQ(v,ActivateTrig(u),x,$s("§pureflag")),If(And(ProductQ(v),NeQ(FreeFactors(v,x),C1)),FunctionOfQ(NonfreeFactors(v,x),u,x,$s("§pureflag")),If($s("§pureflag"),Switch(Head(v),Sin,PureFunctionOfSinQ(u,Part(v,C1),x),Cos,PureFunctionOfCosQ(u,Part(v,C1),x),Tan,PureFunctionOfTanQ(u,Part(v,C1),x),Cot,PureFunctionOfCotQ(u,Part(v,C1),x),Sec,PureFunctionOfCosQ(u,Part(v,C1),x),Csc,PureFunctionOfSinQ(u,Part(v,C1),x),Sinh,PureFunctionOfSinhQ(u,Part(v,C1),x),Cosh,PureFunctionOfCoshQ(u,Part(v,C1),x),Tanh,PureFunctionOfTanhQ(u,Part(v,C1),x),Coth,PureFunctionOfCothQ(u,Part(v,C1),x),Sech,PureFunctionOfCoshQ(u,Part(v,C1),x),Csch,PureFunctionOfSinhQ(u,Part(v,C1),x),$b(),UnsameQ(FunctionOfExpnQ(u,v,x),False)),Switch(Head(v),Sin,FunctionOfSinQ(u,Part(v,C1),x),Cos,FunctionOfCosQ(u,Part(v,C1),x),Tan,FunctionOfTanQ(u,Part(v,C1),x),Cot,FunctionOfTanQ(u,Part(v,C1),x),Sec,FunctionOfCosQ(u,Part(v,C1),x),Csc,FunctionOfSinQ(u,Part(v,C1),x),Sinh,FunctionOfSinhQ(u,Part(v,C1),x),Cosh,FunctionOfCoshQ(u,Part(v,C1),x),Tanh,FunctionOfTanhQ(u,Part(v,C1),x),Coth,FunctionOfTanhQ(u,Part(v,C1),x),Sech,FunctionOfCoshQ(u,Part(v,C1),x),Csch,FunctionOfSinhQ(u,Part(v,C1),x),$b(),UnsameQ(FunctionOfExpnQ(u,v,x),False)))))))),
ISetDelayed(412,FunctionOfExpnQ(u_,v_,x_),
    If(SameQ(u,v),C1,If(AtomQ(u),If(SameQ(u,x),False,C0),If(CalculusQ(u),False,If(And(PowerQ(u),FreeQ(Part(u,C2),x)),If(EqQ(Part(u,C1),v),If(IntegerQ(Part(u,C2)),Part(u,C2),C1),If(And(PowerQ(v),FreeQ(Part(v,C2),x),EqQ(Part(u,C1),Part(v,C1))),If(RationalQ(Part(v,C2)),If(And(RationalQ(Part(u,C2)),IntegerQ(Times(Part(u,C2),Power(Part(v,C2),CN1))),Or(Greater(Part(v,C2),C0),Less(Part(u,C2),C0))),Times(Part(u,C2),Power(Part(v,C2),CN1)),False),If(IntegerQ(Simplify(Times(Part(u,C2),Power(Part(v,C2),CN1)))),Simplify(Times(Part(u,C2),Power(Part(v,C2),CN1))),False)),FunctionOfExpnQ(Part(u,C1),v,x))),If(And(ProductQ(u),NeQ(FreeFactors(u,x),C1)),FunctionOfExpnQ(NonfreeFactors(u,x),v,x),If(And(ProductQ(u),ProductQ(v)),Module(list(Set($s("deg1"),FunctionOfExpnQ(First(u),First(v),x)),$s("deg2")),If(SameQ($s("deg1"),False),False,CompoundExpression(Set($s("deg2"),FunctionOfExpnQ(Rest(u),Rest(v),x)),If(And(SameQ($s("deg1"),$s("deg2")),FreeQ(Simplify(Times(u,Power(Power(v,$s("deg1")),CN1))),x)),$s("deg1"),False)))),With(list(Set($s("lst"),Map(Function(FunctionOfExpnQ(Slot1,v,x)),Apply(List,u)))),If(MemberQ($s("lst"),False),False,Apply(GCD,$s("lst"))))))))))),
ISetDelayed(413,PureFunctionOfSinQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),EqQ(Part(u,C1),v)),Or(SameQ(Head(u),Sin),SameQ(Head(u),Csc)),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfSinQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(414,PureFunctionOfCosQ(u_,v_,x_),
          If(AtomQ(u), UnsameQ(u, x), If(CalculusQ(u), False,
              If(And(TrigQ(u), EqQ(Part(u, C1), v)), Or(SameQ(Head(u), Cos), SameQ(Head(u), Sec)),
                  Catch(CompoundExpression(
                      Scan(Function(If(Not(PureFunctionOfCosQ(Slot1, v, x)), Throw(False))), u),
                      True))))))
  );
}
