package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

public interface LimitRules {
	final public static IAST RULES = List(
SetDelayed(Limit(Power(x_,$p(m,IntegerQ)),Rule(x_Symbol,CInfinity)),
    Condition(C0,Negative(m))),
SetDelayed(Limit(Power(x_,$p(m,IntegerQ)),Rule(x_Symbol,CNInfinity)),
    Condition(C0,Negative(m))),
Set(Limit(Power(Plus(C1,Power(x_,CN1)),x_),Rule(x_Symbol,CInfinity)),
    E),
Set(Limit(Power(Plus(C1,Times(Power(x_,CN1),a_)),x_),Rule(x_Symbol,CInfinity)),
    Power(E,a)),
SetDelayed(Limit(Sum(Power(y_Symbol,$p(s,IntegerQ)),List(y_Symbol,C1,x_Symbol)),Rule(x_Symbol,CInfinity)),
    Condition(Module(List(Set(v,Times(C1D2,CN1,s))),Times(Power(Times(C2,Pi),Times(C2,v)),Power(CN1,Plus(v,C1)),BernoulliB(Times(C2,v)),Power(Times(C2,Factorial(Times(C2,v))),CN1))),And(EvenQ(s),Less(s,C0))))
	);
}