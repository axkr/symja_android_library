package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Drop;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.OddQ;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sow;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.Cosh;
import static org.matheclipse.core.expression.S.Coth;
import static org.matheclipse.core.expression.S.Csch;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.Sech;
import static org.matheclipse.core.expression.S.Sinh;
import static org.matheclipse.core.expression.S.Tanh;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EvenQuotientQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FindTrigFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfCoshQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfHyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfSinhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTanhQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTanhWeight;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerQuotientQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadBase;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeadFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.OddHyperbolicPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.OddQuotientQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PureFunctionOfCothQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ReapList;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemainingFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions32 { 
  public static IAST RULES = List( 
ISetDelayed(426,PureFunctionOfCothQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),EqQ(Part(u,C1),v)),SameQ(Head(u),Coth),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfCothQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(427,FunctionOfSinhQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),If(OddQuotientQ(Part(u,C1),v),Or(SameQ(Head(u),Sinh),SameQ(Head(u),Csch)),Or(SameQ(Head(u),Cosh),SameQ(Head(u),Sech))),If(And(IntegerPowerQ(u),HyperbolicQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(EvenQ(Part(u,C2)),True,FunctionOfSinhQ(Part(u,C1),v,x)),If(ProductQ(u),If(And(SameQ(Head(Part(u,C1)),Cosh),SameQ(Head(Part(u,C2)),Sinh),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),FunctionOfSinhQ(Drop(u,C2),v,x),Module(list($s("lst")),CompoundExpression(Set($s("lst"),FindTrigFactor(Sinh,Csch,u,v,False)),If(And(ListQ($s("lst")),EvenQuotientQ(Part($s("lst"),C1),v)),FunctionOfSinhQ(Times(Cosh(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Cosh,Sech,u,v,False)),If(And(ListQ($s("lst")),OddQuotientQ(Part($s("lst"),C1),v)),FunctionOfSinhQ(Times(Cosh(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Tanh,Coth,u,v,True)),If(ListQ($s("lst")),FunctionOfSinhQ(Times(Cosh(v),Part($s("lst"),C2)),v,x),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfSinhQ(Slot1,v,x)),Throw(False))),u),True)))))))))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfSinhQ(Slot1,v,x)),Throw(False))),u),True)))))))),
ISetDelayed(428,FunctionOfCoshQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),Or(SameQ(Head(u),Cosh),SameQ(Head(u),Sech)),If(And(IntegerPowerQ(u),HyperbolicQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(EvenQ(Part(u,C2)),True,FunctionOfCoshQ(Part(u,C1),v,x)),If(ProductQ(u),Module(list($s("lst")),CompoundExpression(Set($s("lst"),FindTrigFactor(Sinh,Csch,u,v,False)),If(ListQ($s("lst")),FunctionOfCoshQ(Times(Sinh(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Tanh,Coth,u,v,True)),If(ListQ($s("lst")),FunctionOfCoshQ(Times(Sinh(v),Part($s("lst"),C2)),v,x),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfCoshQ(Slot1,v,x)),Throw(False))),u),True))))))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfCoshQ(Slot1,v,x)),Throw(False))),u),True)))))))),
ISetDelayed(429,FunctionOfTanhQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),Or(SameQ(Head(u),Tanh),SameQ(Head(u),Coth),EvenQuotientQ(Part(u,C1),v)),If(And(PowerQ(u),EvenQ(Part(u,C2)),HyperbolicQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),True,If(And(PowerQ(u),EvenQ(Part(u,C2)),SumQ(Part(u,C1))),FunctionOfTanhQ(Expand(Sqr(Part(u,C1))),v,x),If(ProductQ(u),With(list(Set($s("lst"),ReapList(Scan(Function(If(Not(FunctionOfTanhQ(Slot1,v,x)),Sow(Slot1))),u)))),If(SameQ($s("lst"),List()),True,And(Equal(Length($s("lst")),C2),OddHyperbolicPowerQ(Part($s("lst"),C1),v,x),OddHyperbolicPowerQ(Part($s("lst"),C2),v,x)))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfTanhQ(Slot1,v,x)),Throw(False))),u),True))))))))),
ISetDelayed(430,OddHyperbolicPowerQ(u_,v_,x_),
    If(MemberQ(List(Sinh,Cosh,Sech,Csch),Head(u)),OddQuotientQ(Part(u,C1),v),If(PowerQ(u),And(OddQ(Part(u,C2)),OddHyperbolicPowerQ(Part(u,C1),v,x)),If(ProductQ(u),If(NeQ(FreeFactors(u,x),C1),OddHyperbolicPowerQ(NonfreeFactors(u,x),v,x),With(list(Set($s("lst"),ReapList(Scan(Function(If(Not(FunctionOfTanhQ(Slot1,v,x)),Sow(Slot1))),u)))),If(SameQ($s("lst"),List()),True,And(Equal(Length($s("lst")),C1),OddHyperbolicPowerQ(Part($s("lst"),C1),v,x))))),If(SumQ(u),Catch(CompoundExpression(Scan(Function(If(Not(OddHyperbolicPowerQ(Slot1,v,x)),Throw(False))),u),True)),False))))),
ISetDelayed(431,FunctionOfTanhWeight(u_,v_,x_),
    If(AtomQ(u),C0,If(CalculusQ(u),C0,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),If(And(SameQ(Head(u),Tanh),EqQ(Part(u,C1),v)),C1,If(And(SameQ(Head(u),Coth),EqQ(Part(u,C1),v)),CN1,C0)),If(And(PowerQ(u),EvenQ(Part(u,C2)),HyperbolicQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(Or(SameQ(Head(Part(u,C1)),Tanh),SameQ(Head(Part(u,C1)),Cosh),SameQ(Head(Part(u,C1)),Sech)),C1,CN1),If(ProductQ(u),If(Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfTanhQ(Slot1,v,x)),Throw(False))),u),True)),Apply(Plus,Map(Function(FunctionOfTanhWeight(Slot1,v,x)),Apply(List,u))),C0),Apply(Plus,Map(Function(FunctionOfTanhWeight(Slot1,v,x)),Apply(List,u))))))))),
ISetDelayed(432,FunctionOfHyperbolicQ(u_,v_,x_Symbol),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),True,Catch(CompoundExpression(Scan(Function(If(FunctionOfHyperbolicQ(Slot1,v,x),Null,Throw(False))),u),True)))))),
ISetDelayed(433,FindTrigFactor($p("func1"),$p("func2"),u_,v_,$p("flag")),
    If(SameQ(u,C1),False,If(And(Or(SameQ(Head(LeadBase(u)),$s("func1")),SameQ(Head(LeadBase(u)),$s("func2"))),OddQ(LeadDegree(u)),IntegerQuotientQ(Part(LeadBase(u),C1),v),Or($s("flag"),NeQ(Part(LeadBase(u),C1),v))),list(Part(LeadBase(u),C1),RemainingFactors(u)),With(list(Set($s("lst"),FindTrigFactor($s("func1"),$s("func2"),RemainingFactors(u),v,$s("flag")))),If(AtomQ($s("lst")),False,list(Part($s("lst"),C1),Times(LeadFactor(u),Part($s("lst"),C2)))))))),
ISetDelayed(434,IntegerQuotientQ(u_,v_),
    IntegerQ(Simplify(Times(u,Power(v,CN1))))),
ISetDelayed(435,OddQuotientQ(u_,v_),
          OddQ(Simplify(Times(u, Power(v, CN1)))))
  );
}
