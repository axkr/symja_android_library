{

{ 
eliminv,
eliminv(x_*a_^x_ * z_., x_) := ProductLog((Log(a)*x)/z)/Log(a)
  /; FreeQ({a,z},x)
},
(* Rules for the case an expression is 0 and is a Plus() expression. *)
{ 

elimzeroplus,
elimzeroplus(x_^n_.*a_.+x_^m_*b_., x_) := E^((-I*Pi+Log(a)-Log(b))/(m-n))
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x),

elimzeroplus(x_^n_*a_.+m_^x_*b_., x_) := {-((n*ProductLog(-(((-(b/a))^(1/n)*Log(m))/n)))/Log(m)), -((n*ProductLog((((-(b/a))^(1/n)*Log(m))/n)))/Log(m))}
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x),
  
elimzeroplus(y_.*x_+Sqrt(a_.+b_.*x_)+z_., x_) := {(b-2*y*z-Sqrt(b^2+4*a*y^2-4*b*y*z))/(2*y^2), (b-2*y*z+Sqrt(b^2+4*a*y^2-4*b*y*z))/(2*y^2)} 
  /; FreeQ({a,b,y,z},x),
     
elimzeroplus(y_.*x_+Sqrt(a_.+b_.*x_+c_.*x_^2)+z_., x_) := If(PossibleZeroQ(c-y^2), (-a+z^2)/(b-2*y*z), {(b-2*y*z+Sqrt(b^2-4*a*c+4*a*y^2-4*b*y*z+4*c*z^2))/(2*(-c+y^2)), (-b+2*y*z+Sqrt(b^2-4*a*c+4*a*y^2-4*b*y*z+4*c*z^2))/(2*(c-y^2))})
  /; FreeQ({a,b,c,y,z},x)
} 
}