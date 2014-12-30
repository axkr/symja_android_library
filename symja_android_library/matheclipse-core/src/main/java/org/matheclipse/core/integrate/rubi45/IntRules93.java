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
public class IntRules93 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),n),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),n),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(a,Times(CN1,b)))),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),n),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(a,b))),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),Plus(n,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Cos(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(d,C1D2,x))),CN1),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(Times(CN1,Pi,a,Power(Times(C4,b),CN1)),Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),Not(IntegerQ(n))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),Plus(n,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(d,x)))))),Power(Cos(Plus(Times(C1D2,c),Times(d,C1D2,x))),CN1),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(a,Times(CN1,b)))),Not(IntegerQ(n))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(C2,a),Plus(n,Times(CN1,C1D2))),Sqrt(Plus(a,Times(b,Cos(Plus(c,Times(d,x)))))),Power(Sin(Plus(Times(C1D2,c),Times(d,C1D2,x))),CN1),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(Times(C1D2,c),Times(d,C1D2,x))),Times(C2,n))),x)),And(And(FreeQ(List(a,b,c,d,e,f,m),x),ZeroQ(Plus(a,b))),Not(IntegerQ(n))))),
ISetDelayed(Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2)),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1)),x)),Times(CN1,b,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Plus(b,Times(a,Sin(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN2)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2)),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1)),x)),Times(CN1,b,Power(Plus(Sqr(a),Times(CN1,Sqr(b))),CN1),Int(Times(x,Plus(b,Times(a,Cos(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN2)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Times(Power(Power(C2,n),CN1),Int(Times(Power(x,m),Power(Plus(Times(CI,b),Times(C2,a,Power(E,Plus(Times(CI,c),Times(CI,d,x)))),Times(CN1,CI,b,Power(E,Times(C2,Plus(Times(CI,c),Times(CI,d,x)))))),n),Power(Power(E,Times(n,Plus(Times(CI,c),Times(CI,d,x)))),CN1)),x)),And(And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(m)),Greater(m,C0)),IntegerQ(n)),Less(n,C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Times(Power(Power(C2,n),CN1),Int(Times(Power(x,m),Power(Plus(b,Times(C2,a,Power(E,Plus(Times(CI,c),Times(CI,d,x)))),Times(b,Power(E,Times(C2,Plus(Times(CI,c),Times(CI,d,x)))))),n),Power(Power(E,Times(n,Plus(Times(CI,c),Times(CI,d,x)))),CN1)),x)),And(And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Times(CN1,Sqr(b))))),RationalQ(m)),Greater(m,C0)),IntegerQ(n)),Less(n,C0)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Times(Power(Power(C2,n),CN1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(CN1,b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x)),And(And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,b))),IntegersQ(m,n)),Greater(m,C0)),Less(n,C0)),Or(Equal(n,CN1),And(Equal(m,C1),Equal(n,CN2)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Times(Power(Power(C2,n),CN1),Int(Times(Power(x,m),Power(Plus(Times(C2,a),b,Times(b,Cos(Plus(Times(C2,c),Times(C2,d,x))))),n)),x)),And(And(And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(a,b))),IntegersQ(m,n)),Greater(m,C0)),Less(n,C0)),Or(Equal(n,CN1),And(Equal(m,C1),Equal(n,CN2))))))
  );
}
