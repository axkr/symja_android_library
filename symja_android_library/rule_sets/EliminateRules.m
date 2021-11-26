{

{ 
eliminv,
eliminv(x_*a_^x_ * z_., x_) := ProductLog((Log(a)*x)/z)/Log(a)
  /; FreeQ({a,z},x)
},
(* Rules for the case an expression is 0. *)
{ 

elimzero,
elimzero(x_^n_.*a_.+x_^m_*b_., x_) := E^((-I*Pi+Log(a)-Log(b))/(m-n))
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x),

elimzero(x_^n_*a_.+m_^x_*b_., x_) := {-((n*ProductLog(-(((-(b/a))^(1/n)*Log(m))/n)))/Log(m)), -((n*ProductLog((((-(b/a))^(1/n)*Log(m))/n)))/Log(m))}
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x)
} 
}