package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions31 { 
  public static IAST RULES = List( 
ISetDelayed(415,PureFunctionOfTanQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),EqQ(Part(u,C1),v)),Or(SameQ(Head(u),Tan),SameQ(Head(u),Cot)),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfTanQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(416,PureFunctionOfCotQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),EqQ(Part(u,C1),v)),SameQ(Head(u),Cot),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfCotQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(417,FunctionOfSinQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),If(OddQuotientQ(Part(u,C1),v),Or(SameQ(Head(u),Sin),SameQ(Head(u),Csc)),Or(SameQ(Head(u),Cos),SameQ(Head(u),Sec))),If(And(IntegerPowerQ(u),TrigQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(EvenQ(Part(u,C2)),True,FunctionOfSinQ(Part(u,C1),v,x)),If(ProductQ(u),If(And(SameQ(Head(Part(u,C1)),Cos),SameQ(Head(Part(u,C2)),Sin),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),FunctionOfSinQ(Drop(u,C2),v,x),Module(list($s("lst")),CompoundExpression(Set($s("lst"),FindTrigFactor(Sin,Csc,u,v,False)),If(And(ListQ($s("lst")),EvenQuotientQ(Part($s("lst"),C1),v)),FunctionOfSinQ(Times(Cos(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Cos,Sec,u,v,False)),If(And(ListQ($s("lst")),OddQuotientQ(Part($s("lst"),C1),v)),FunctionOfSinQ(Times(Cos(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Tan,Cot,u,v,True)),If(ListQ($s("lst")),FunctionOfSinQ(Times(Cos(v),Part($s("lst"),C2)),v,x),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfSinQ(Slot1,v,x)),Throw(False))),u),True)))))))))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfSinQ(Slot1,v,x)),Throw(False))),u),True)))))))),
ISetDelayed(418,FunctionOfCosQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),Or(SameQ(Head(u),Cos),SameQ(Head(u),Sec)),If(And(IntegerPowerQ(u),TrigQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(EvenQ(Part(u,C2)),True,FunctionOfCosQ(Part(u,C1),v,x)),If(ProductQ(u),Module(list($s("lst")),CompoundExpression(Set($s("lst"),FindTrigFactor(Sin,Csc,u,v,False)),If(ListQ($s("lst")),FunctionOfCosQ(Times(Sin(v),Part($s("lst"),C2)),v,x),CompoundExpression(Set($s("lst"),FindTrigFactor(Tan,Cot,u,v,True)),If(ListQ($s("lst")),FunctionOfCosQ(Times(Sin(v),Part($s("lst"),C2)),v,x),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfCosQ(Slot1,v,x)),Throw(False))),u),True))))))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfCosQ(Slot1,v,x)),Throw(False))),u),True)))))))),
ISetDelayed(419,FunctionOfTanQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),Or(SameQ(Head(u),Tan),SameQ(Head(u),Cot),EvenQuotientQ(Part(u,C1),v)),If(And(PowerQ(u),EvenQ(Part(u,C2)),TrigQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),True,If(And(PowerQ(u),EvenQ(Part(u,C2)),SumQ(Part(u,C1))),FunctionOfTanQ(Expand(Sqr(Part(u,C1))),v,x),If(ProductQ(u),Module(list(Set($s("lst"),ReapList(Scan(Function(If(Not(FunctionOfTanQ(Slot1,v,x)),Sow(Slot1))),u)))),If(SameQ($s("lst"),List()),True,And(Equal(Length($s("lst")),C2),OddTrigPowerQ(Part($s("lst"),C1),v,x),OddTrigPowerQ(Part($s("lst"),C2),v,x)))),Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfTanQ(Slot1,v,x)),Throw(False))),u),True))))))))),
ISetDelayed(420,OddTrigPowerQ(u_,v_,x_),
    If(MemberQ(List(Sin,Cos,Sec,Csc),Head(u)),OddQuotientQ(Part(u,C1),v),If(PowerQ(u),And(OddQ(Part(u,C2)),OddTrigPowerQ(Part(u,C1),v,x)),If(ProductQ(u),If(NeQ(FreeFactors(u,x),C1),OddTrigPowerQ(NonfreeFactors(u,x),v,x),Module(list(Set($s("lst"),ReapList(Scan(Function(If(Not(FunctionOfTanQ(Slot1,v,x)),Sow(Slot1))),u)))),If(SameQ($s("lst"),List()),True,And(Equal(Length($s("lst")),C1),OddTrigPowerQ(Part($s("lst"),C1),v,x))))),If(SumQ(u),Catch(CompoundExpression(Scan(Function(If(Not(OddTrigPowerQ(Slot1,v,x)),Throw(False))),u),True)),False))))),
ISetDelayed(421,FunctionOfTanWeight(u_,v_,x_),
    If(AtomQ(u),C0,If(CalculusQ(u),C0,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),If(And(SameQ(Head(u),Tan),EqQ(Part(u,C1),v)),C1,If(And(SameQ(Head(u),Cot),EqQ(Part(u,C1),v)),CN1,C0)),If(And(PowerQ(u),EvenQ(Part(u,C2)),TrigQ(Part(u,C1)),IntegerQuotientQ(Part(u,C1,C1),v)),If(Or(SameQ(Head(Part(u,C1)),Tan),SameQ(Head(Part(u,C1)),Cos),SameQ(Head(Part(u,C1)),Sec)),C1,CN1),If(ProductQ(u),If(Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfTanQ(Slot1,v,x)),Throw(False))),u),True)),Apply(Plus,Map(Function(FunctionOfTanWeight(Slot1,v,x)),Apply(List,u))),C0),Apply(Plus,Map(Function(FunctionOfTanWeight(Slot1,v,x)),Apply(List,u))))))))),
ISetDelayed(422,FunctionOfTrigQ(u_,v_,x_Symbol),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),True,Catch(CompoundExpression(Scan(Function(If(Not(FunctionOfTrigQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(423,PureFunctionOfSinhQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),EqQ(Part(u,C1),v)),Or(SameQ(Head(u),Sinh),SameQ(Head(u),Csch)),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfSinhQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(424,PureFunctionOfCoshQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),EqQ(Part(u,C1),v)),Or(SameQ(Head(u),Cosh),SameQ(Head(u),Sech)),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfCoshQ(Slot1,v,x)),Throw(False))),u),True)))))),
ISetDelayed(425,PureFunctionOfTanhQ(u_,v_,x_),
    If(AtomQ(u),UnsameQ(u,x),If(CalculusQ(u),False,If(And(HyperbolicQ(u),EqQ(Part(u,C1),v)),Or(SameQ(Head(u),Tanh),SameQ(Head(u),Coth)),Catch(CompoundExpression(Scan(Function(If(Not(PureFunctionOfTanhQ(Slot1,v,x)),Throw(False))),u),True))))))
  );
}
