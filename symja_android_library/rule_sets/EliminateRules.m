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
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x),
  
elimzero(x_+Sqrt(a_.+b_.*x_)+z_., x_) := {(1/2)*(b-2*z-Sqrt(4*a+b^2-4*b*z)), (1/2)*(b-2*z+Sqrt(4*a+b^2-4*b*z))} 
  /; FreeQ({a,b,z},x), 
  
elimzero(x_+Sqrt(a_.+b_.*x_+c_.*x_^2)+z_., x_) := {(-b+2*z-Sqrt(4*a+b^2-4*a*c-4*b*z+4*c*z^2))/(2*(-1 + c)), (-b + 2*z + Sqrt(4*a+b^2-4*a*c-4*b*z+4*c*z^2))/(2*(-1+c))} 
  /; FreeQ({a,b,c,z},x)
} 
}