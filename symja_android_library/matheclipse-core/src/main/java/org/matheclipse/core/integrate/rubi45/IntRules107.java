package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules107 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,Plus(p,C1D2)),Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x)),And(And(And(FreeQ(List(a,b,c,d,e,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),NegativeIntegerQ(Plus(p,Negate(C1D2)))),Not(PositiveQ(d))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,Plus(p,C1D2)),Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x)),And(And(And(FreeQ(List(a,b,c,d,e,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),NegativeIntegerQ(Plus(p,Negate(C1D2)))),Not(PositiveQ(d))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Times(Sqrt(Plus(d_,Times(e_DEFAULT,x_))),Sqrt(Plus(f_,Times(g_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Times(Sqrt(Plus(Times(d,f),Times(e,g,Sqr(x)))),Power(Times(Sqrt(Plus(d,Times(e,x))),Sqrt(Plus(f,Times(g,x)))),-1),Int(Times(Power(Plus(h,Times(i,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(Times(d,f),Times(e,g,Sqr(x))),CN1D2)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,i,m,n),x),ZeroQ(Plus(Times(e,f),Times(d,g)))))),
ISetDelayed(Int(Times(Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Times(Sqrt(Plus(d_,Times(e_DEFAULT,x_))),Sqrt(Plus(f_,Times(g_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Times(Sqrt(Plus(Times(d,f),Times(e,g,Sqr(x)))),Power(Times(Sqrt(Plus(d,Times(e,x))),Sqrt(Plus(f,Times(g,x)))),-1),Int(Times(Power(Plus(h,Times(i,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(Times(d,f),Times(e,g,Sqr(x))),CN1D2)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,i,m,n),x),ZeroQ(Plus(Times(e,f),Times(d,g)))))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Times(i_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(i,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,f,g,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Times(i_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(i,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,f,g,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(Plus(h,Times(i,x)),m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),p_DEFAULT),Power(Plus(h_DEFAULT,Times(i_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(Plus(h,Times(i,x)),m),Power(Plus(d,Times(e,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Plus(n,C1)),-1)),Times(CN1,g,m,Power(Times(b,c,Sqrt(d),Plus(n,C1)),-1),Int(Times(Power(Plus(f,Times(g,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,d,e,f,g,m),x),ZeroQ(Plus(Times(Sqr(c),d),e))),PositiveQ(d)),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Sqrt(d),Plus(n,C1)),-1)),Times(g,m,Power(Times(b,c,Sqrt(d),Plus(n,C1)),-1),Int(Times(Power(Plus(f,Times(g,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,d,e,f,g,m),x),ZeroQ(Plus(Times(Sqr(c),d),e))),PositiveQ(d)),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Times(Power(Times(Power(c,Plus(m,C1)),Sqrt(d)),-1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Plus(Times(c,f),Times(g,Sin(x))),m)),x),x,ArcSin(Times(c,x)))),And(And(And(FreeQ(List(a,b,c,d,e,f,g,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),PositiveQ(d)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Times(CN1,Power(Times(Power(c,Plus(m,C1)),Sqrt(d)),-1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Plus(Times(c,f),Times(g,Cos(x))),m)),x),x,ArcCos(Times(c,x)))),And(And(And(FreeQ(List(a,b,c,d,e,f,g,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),PositiveQ(d)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Times(Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),CN1D2)),x)),And(And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),Not(PositiveQ(d))))),
ISetDelayed(Int(Times(Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Times(Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),CN1D2)),x)),And(And(FreeQ(List(a,b,c,d,e,f,g,m,n),x),ZeroQ(Plus(Times(Sqr(c),d),e))),Not(PositiveQ(d))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Times(g_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,g,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Times(g_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),-1),Int(Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x)),FreeQ(List(a,b,c,d,e,g,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(Plus(f,Times(g,x)),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Times(c_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition($(AbortRubi($s("Int")),Times(Power(Plus(f,Times(g,x)),m),Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Power(Plus(a,Times(b,ArcSin(x))),n),x),x,Plus(c,Times(d,x)))),FreeQ(List(a,b,c,d,n),x))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Power(Plus(a,Times(b,ArcCos(x))),n),x),x,Plus(c,Times(d,x)))),FreeQ(List(a,b,c,d,n),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x)))),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(a,Times(b,ArcCos(x))),n)),x),x,Plus(c,Times(d,x)))),FreeQ(List(a,b,c,d,e,f,m,n),x))),
ISetDelayed(Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(CN1,CSymbol,Power(d,-2)),Times(CSymbol,Power(d,-2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x)))),And(And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,n,p),x),ZeroQ(Plus(Times(BSymbol,Plus(C1,Negate(Sqr(c)))),Times(C2,ASymbol,c,d)))),ZeroQ(Plus(Times(C2,c,CSymbol),Times(CN1,BSymbol,d)))))),
ISetDelayed(Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(CN1,CSymbol,Power(d,-2)),Times(CSymbol,Power(d,-2),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(x))),n)),x),x,Plus(c,Times(d,x)))),And(And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,n,p),x),ZeroQ(Plus(Times(BSymbol,Plus(C1,Negate(Sqr(c)))),Times(C2,ASymbol,c,d)))),ZeroQ(Plus(Times(C2,c,CSymbol),Times(CN1,BSymbol,d)))))),
ISetDelayed(Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcSin(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,-2)),Times(CSymbol,Power(d,-2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),n)),x),x,Plus(c,Times(d,x)))),And(And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,n,p),x),ZeroQ(Plus(Times(BSymbol,Plus(C1,Negate(Sqr(c)))),Times(C2,ASymbol,c,d)))),ZeroQ(Plus(Times(C2,c,CSymbol),Times(CN1,BSymbol,d)))))),
ISetDelayed(Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,ArcCos(Plus(c_,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,-2)),Times(CSymbol,Power(d,-2),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(x))),n)),x),x,Plus(c,Times(d,x)))),And(And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,n,p),x),ZeroQ(Plus(Times(BSymbol,Plus(C1,Negate(Sqr(c)))),Times(C2,ASymbol,c,d)))),ZeroQ(Plus(Times(C2,c,CSymbol),Times(CN1,BSymbol,d))))))
  );
}
