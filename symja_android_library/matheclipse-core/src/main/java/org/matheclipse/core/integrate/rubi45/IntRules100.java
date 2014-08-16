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
public class IntRules100 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),pn),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Cosh(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),pn),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Cosh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(a,Times(CN1,b)))),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(CN2,a),pn),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Sinh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(a,b))),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),Plus(pn,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Sinh(Plus(c,Times(pd,x)))))),Power(Cosh(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(pd,C1D2,x))),CN1),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Cosh(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(pn))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),Plus(pn,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x)))))),Power(Cosh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),CN1),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Cosh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(a,Times(CN1,b)))),Not(IntegerQ(pn))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(CN2,a),Plus(pn,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x)))))),Power(Sinh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),CN1),Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Sinh(Plus(Times(C1D2,c),Times(pd,C1D2,x))),Times(C2,pn))),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),ZeroQ(Plus(a,b))),Not(IntegerQ(pn))))),
ISetDelayed(Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN2)),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(x,Power(Plus(a,Times(b,Sinh(Plus(c,Times(pd,x))))),CN1)),x)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(x,Plus(b,Times(CN1,a,Sinh(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(pd,x))))),CN2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),CN2)),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Power(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x))))),CN1)),x)),Times(CN1,b,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Plus(b,Times(a,Cosh(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x))))),CN2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_)),x_Symbol),
    Condition(Times(Power(Power(C2,pn),CN1),Int(Times(Power(x,m),Power(Plus(Times(CN1,b),Times(C2,a,Power(E,Plus(c,Times(pd,x)))),Times(b,Power(E,Times(C2,Plus(c,Times(pd,x)))))),pn),Power(Power(E,Times(pn,Plus(c,Times(pd,x)))),CN1)),x)),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(m)),Greater(m,C0)),IntegerQ(pn)),Less(pn,C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_)),x_Symbol),
    Condition(Times(Power(Power(C2,pn),CN1),Int(Times(Power(x,m),Power(Plus(b,Times(C2,a,Power(E,Plus(c,Times(pd,x)))),Times(b,Power(E,Times(C2,Plus(c,Times(pd,x)))))),pn),Power(Power(E,Times(pn,Plus(c,Times(pd,x)))),CN1)),x)),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(m)),Greater(m,C0)),IntegerQ(pn)),Less(pn,C0)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,C1D2,Sinh(Plus(Times(C2,c),Times(C2,pd,x))))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,m,pn),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sinh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),pn_)),x_Symbol),
    Condition(Times(Power(Power(C2,pn),CN1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),Times(CN1,b),Times(b,Cosh(Plus(Times(C2,c),Times(C2,pd,x))))),pn)),x)),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,Times(CN1,b)))),IntegersQ(m,pn)),Greater(m,C0)),Less(pn,C0)),Or(Equal(pn,CN1),And(Equal(m,C1),Equal(pn,CN2)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Cosh(Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),pn_)),x_Symbol),
    Condition(Times(Power(Power(C2,pn),CN1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cosh(Plus(Times(C2,c),Times(C2,pd,x))))),pn)),x)),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,b))),IntegersQ(m,pn)),Greater(m,C0)),Less(pn,C0)),Or(Equal(pn,CN1),And(Equal(m,C1),Equal(pn,CN2))))))
  );
}
