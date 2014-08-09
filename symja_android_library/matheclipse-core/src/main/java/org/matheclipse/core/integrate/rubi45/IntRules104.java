package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules104 { 
  public static IAST RULES = List( 
ISetDelayed(Int(ArcTanh(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcTanh(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x))))))),Times(b,Int(Times(x,Power(Plus(c,Times(CN1,pd),Times(c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcCoth(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcCoth(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x))))))),Times(b,Int(Times(x,Power(Plus(c,Times(CN1,pd),Times(c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcTanh(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcTanh(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x))))))),Times(b,Plus(C1,c,Times(CN1,pd)),Int(Times(x,Power(Plus(C1,c,Times(CN1,pd),Times(Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Int(Times(x,Power(Plus(C1,Times(CN1,c),pd,Times(Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcCoth(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcCoth(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x))))))),Times(b,Plus(C1,c,Times(CN1,pd)),Int(Times(x,Power(Plus(C1,c,Times(CN1,pd),Times(Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Int(Times(x,Power(Plus(C1,Times(CN1,c),pd,Times(Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(Times(ArcTanh(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(c,Times(CN1,pd),Times(c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcCoth(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(c,Times(CN1,pd),Times(c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcTanh(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Plus(C1,c,Times(CN1,pd)),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,c,Times(CN1,pd),Times(Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,Times(CN1,c),pd,Times(Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcCoth(Plus(Times(Tanh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(c,Times(pd,Tanh(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Plus(C1,c,Times(CN1,pd)),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,c,Times(CN1,pd),Times(Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,Times(CN1,c),pd,Times(Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(ArcTanh(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcTanh(Plus(c,Times(pd,Coth(Plus(a,Times(b,x))))))),Times(b,Int(Times(x,Power(Plus(c,Times(CN1,pd),Times(CN1,c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcCoth(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcCoth(Plus(c,Times(pd,Coth(Plus(a,Times(b,x))))))),Times(b,Int(Times(x,Power(Plus(c,Times(CN1,pd),Times(CN1,c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcTanh(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcTanh(Plus(c,Times(pd,Coth(Plus(a,Times(b,x))))))),Times(b,Plus(C1,c,Times(CN1,pd)),Int(Times(x,Power(Plus(C1,c,Times(CN1,pd),Times(CN1,Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Int(Times(x,Power(Plus(C1,Times(CN1,c),pd,Times(CN1,Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(ArcCoth(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Plus(Times(x,ArcCoth(Plus(c,Times(pd,Coth(Plus(a,Times(b,x))))))),Times(b,Plus(C1,c,Times(CN1,pd)),Int(Times(x,Power(Plus(C1,c,Times(CN1,pd),Times(CN1,Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Int(Times(x,Power(Plus(C1,Times(CN1,c),pd,Times(CN1,Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))))),
ISetDelayed(Int(Times(ArcTanh(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(c,Times(pd,Coth(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(c,Times(CN1,pd),Times(CN1,c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcCoth(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(c,Times(pd,Coth(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(c,Times(CN1,pd),Times(CN1,c,Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcTanh(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(c,Times(pd,Coth(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Plus(C1,c,Times(CN1,pd)),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,c,Times(CN1,pd),Times(CN1,Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,Times(CN1,c),pd,Times(CN1,Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(ArcCoth(Plus(Times(Coth(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pd_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(c,Times(pd,Coth(Plus(a,Times(b,x)))))),Power(Plus(m,C1),CN1)),Times(b,Plus(C1,c,Times(CN1,pd)),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,c,Times(CN1,pd),Times(CN1,Plus(C1,c,pd),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),Times(CN1,b,Plus(C1,Times(CN1,c),pd),Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(C1,Times(CN1,c),pd,Times(CN1,Plus(C1,Times(CN1,c),Times(CN1,pd)),Power(E,Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(Plus(c,Times(CN1,pd))),Times(CN1,C1)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(Power(Plus(Times(Cosh(v_),a_DEFAULT),Times(Sinh(v_),b_DEFAULT)),pn_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(Times(a,Power(E,Times(a,Power(b,CN1),v))),pn)),x),And(FreeQ(List(a,b,pn),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b)))))))
  );
}
