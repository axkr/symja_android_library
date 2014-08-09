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
public class IntRules107 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,Plus(p,C1D2)),Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x)),And(And(And(FreeQ(List(a,b,c,pd,pe,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),NegativeIntegerQ(Plus(p,Times(CN1,C1D2)))),Not(PositiveQ(pd))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,Plus(p,C1D2)),Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(x,m),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x)),And(And(And(FreeQ(List(a,b,c,pd,pe,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),NegativeIntegerQ(Plus(p,Times(CN1,C1D2)))),Not(PositiveQ(pd))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x),x),And(FreeQ(List(a,b,c,pd,pe,m,pn),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x),x),And(FreeQ(List(a,b,c,pd,pe,m,pn),x),PositiveIntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT),Power(Times(Sqrt(Plus(Times(g_DEFAULT,x_),f_)),Sqrt(Plus(Times(x_,pe_DEFAULT),pd_))),CN1)),x_Symbol),
    Condition(Times(Sqrt(Plus(Times(pd,f),Times(pe,g,Sqr(x)))),Power(Times(Sqrt(Plus(pd,Times(pe,x))),Sqrt(Plus(f,Times(g,x)))),CN1),Int(Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn),Power(Plus(Times(pd,f),Times(pe,g,Sqr(x))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn),x),ZeroQ(Plus(Times(pe,f),Times(pd,g)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT),Power(Times(Sqrt(Plus(Times(g_DEFAULT,x_),f_)),Sqrt(Plus(Times(x_,pe_DEFAULT),pd_))),CN1)),x_Symbol),
    Condition(Times(Sqrt(Plus(Times(pd,f),Times(pe,g,Sqr(x)))),Power(Times(Sqrt(Plus(pd,Times(pe,x))),Sqrt(Plus(f,Times(g,x)))),CN1),Int(Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn),Power(Plus(Times(pd,f),Times(pe,g,Sqr(x))),CN1D2)),x)),And(FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn),x),ZeroQ(Plus(Times(pe,f),Times(pd,g)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Times(x_,pi_),m_)),x_Symbol),
    Condition(Times(Power(Times(pi,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,f,g,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Times(x_,pi_),m_)),x_Symbol),
    Condition(Times(Power(Times(pi,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,f,g,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_),p_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_),p_DEFAULT),Power(Plus(Times(x_,pi_DEFAULT),h_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(h,Times(pi,x)),m),Power(Plus(pd,Times(pe,x)),p),Power(Plus(f,Times(g,x)),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,h,pi,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Plus(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(pn,C1)),Power(Times(b,c,Sqrt(pd),Plus(pn,C1)),CN1)),Times(CN1,g,m,Power(Times(b,c,Sqrt(pd),Plus(pn,C1)),CN1),Int(Times(Power(Plus(f,Times(g,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,m),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),PositiveQ(pd)),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(pn,C1)),Power(Times(b,c,Sqrt(pd),Plus(pn,C1)),CN1)),Times(g,m,Power(Times(b,c,Sqrt(pd),Plus(pn,C1)),CN1),Int(Times(Power(Plus(f,Times(g,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(pn,C1))),x))),And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,m),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),PositiveQ(pd)),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Times(Power(Times(Power(c,Plus(m,C1)),Sqrt(pd)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),pn),Power(Plus(Times(c,f),Times(g,Sin(x))),m)),x),x,ArcSin(Times(c,x)))),And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),PositiveQ(pd)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Times(CN1,Power(Times(Power(c,Plus(m,C1)),Sqrt(pd)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),pn),Power(Plus(Times(c,f),Times(g,Cos(x))),m)),x),x,ArcCos(Times(c,x)))),And(And(And(FreeQ(List(a,b,c,pd,pe,f,g,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),PositiveQ(pd)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Times(Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),CN1D2)),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,g,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),Not(PositiveQ(pd))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_),CN1D2)),x_Symbol),
    Condition(Times(Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Plus(pd,Times(pe,Sqr(x))),CN1D2),Int(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn),Power(Plus(C1,Times(CN1,Sqr(c),Sqr(x))),CN1D2)),x)),And(And(FreeQ(List(a,b,c,pd,pe,f,g,m,pn),x),ZeroQ(Plus(Times(Sqr(c),pd),pe))),Not(PositiveQ(pd))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(Times(g_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,g,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT),Power(Times(g_,x_),m_)),x_Symbol),
    Condition(Times(Power(Times(g,x),m),Power(Power(x,m),CN1),Int(Times(Power(x,m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x)),FreeQ(List(a,b,c,pd,pe,g,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(f,Times(g,x)),m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(g_DEFAULT,x_),f_DEFAULT),m_DEFAULT),Power(Plus(Times(Sqr(x_),pe_DEFAULT),pd_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(f,Times(g,x)),m),Power(Plus(pd,Times(pe,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,f,g,m,pn,p),x))),
ISetDelayed(Int(Power(Plus(Times(ArcSin(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Power(Plus(a,Times(b,ArcSin(x))),pn),x),x,Plus(c,Times(pd,x)))),FreeQ(List(a,b,c,pd,pn),x))),
ISetDelayed(Int(Power(Plus(Times(ArcCos(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Power(Plus(a,Times(b,ArcCos(x))),pn),x),x,Plus(c,Times(pd,x)))),FreeQ(List(a,b,c,pd,pn),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(Plus(Times(pd,pe),Times(CN1,c,f)),Power(pd,CN1)),Times(f,x,Power(pd,CN1))),m),Power(Plus(a,Times(b,ArcSin(x))),pn)),x),x,Plus(c,Times(pd,x)))),FreeQ(List(a,b,c,pd,pe,f,m,pn),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(Plus(Times(pd,pe),Times(CN1,c,f)),Power(pd,CN1)),Times(f,x,Power(pd,CN1))),m),Power(Plus(a,Times(b,ArcCos(x))),pn)),x),x,Plus(c,Times(pd,x)))),FreeQ(List(a,b,c,pd,pe,f,m,pn),x))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pb_DEFAULT),Times(Sqr(x_),pc_DEFAULT),pa_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(CN1,pc,Power(pd,CN2)),Times(pc,Power(pd,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),pn)),x),x,Plus(c,Times(pd,x)))),And(And(FreeQ(List(a,b,c,pd,pa,pb,pc,pn,p),x),ZeroQ(Plus(Times(pb,Plus(C1,Times(CN1,Sqr(c)))),Times(C2,pa,c,pd)))),ZeroQ(Plus(Times(C2,c,pc),Times(CN1,pb,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(x_,pb_DEFAULT),Times(Sqr(x_),pc_DEFAULT),pa_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(CN1,pc,Power(pd,CN2)),Times(pc,Power(pd,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(x))),pn)),x),x,Plus(c,Times(pd,x)))),And(And(FreeQ(List(a,b,c,pd,pa,pb,pc,pn,p),x),ZeroQ(Plus(Times(pb,Plus(C1,Times(CN1,Sqr(c)))),Times(C2,pa,c,pd)))),ZeroQ(Plus(Times(C2,c,pc),Times(CN1,pb,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcSin(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Plus(Times(x_,pb_DEFAULT),Times(Sqr(x_),pc_DEFAULT),pa_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(Plus(Times(pd,pe),Times(CN1,c,f)),Power(pd,CN1)),Times(f,x,Power(pd,CN1))),m),Power(Plus(Times(CN1,pc,Power(pd,CN2)),Times(pc,Power(pd,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(x))),pn)),x),x,Plus(c,Times(pd,x)))),And(And(FreeQ(List(a,b,c,pd,pe,f,pa,pb,pc,m,pn,p),x),ZeroQ(Plus(Times(pb,Plus(C1,Times(CN1,Sqr(c)))),Times(C2,pa,c,pd)))),ZeroQ(Plus(Times(C2,c,pc),Times(CN1,pb,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(ArcCos(Plus(Times(x_,pd_DEFAULT),c_)),b_DEFAULT),a_DEFAULT),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Plus(Times(x_,pb_DEFAULT),Times(Sqr(x_),pc_DEFAULT),pa_DEFAULT),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(Times(Plus(Times(pd,pe),Times(CN1,c,f)),Power(pd,CN1)),Times(f,x,Power(pd,CN1))),m),Power(Plus(Times(CN1,pc,Power(pd,CN2)),Times(pc,Power(pd,CN2),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(x))),pn)),x),x,Plus(c,Times(pd,x)))),And(And(FreeQ(List(a,b,c,pd,pe,f,pa,pb,pc,m,pn,p),x),ZeroQ(Plus(Times(pb,Plus(C1,Times(CN1,Sqr(c)))),Times(C2,pa,c,pd)))),ZeroQ(Plus(Times(C2,c,pc),Times(CN1,pb,pd))))))
  );
}
