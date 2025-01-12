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

elimzeroplus(x_^n_*a_.+m_^x_*b_., x_) := {-((n*ProductLog(-(((-(b/a))^(1/n)*Log(m))/n)))/Log(m)), 
                                          -((n*ProductLog((((-(b/a))^(1/n)*Log(m))/n)))/Log(m))}
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x), 
  
elimzeroplus(w_.*Sqrt(a_.+b_.*x_)+z_., x_) := Expand((-a)*w^2 + z^2)/(b*w^2)
  /; FreeQ({a,b,w,z},x),
  
elimzeroplus(x_^a_.*Log(b_.*x_)+c_., x_) := (-((a*c)/ProductLog(((-a)*c)/(1/b)^a)))^(1/a)
  /; FreeQ({a,b,c},x),
  
elimzeroplus(y_.*x_+w_.*Sqrt(a_.+b_.*x_)+z_., x_) := {(b*w^2-2*y*z-w*Sqrt(b^2*w^2+4*a*y^2-4*b*y*z))/(2*y^2), 
                                                      (b*w^2-2*y*z+w*Sqrt(b^2*w^2+4*a*y^2-4*b*y*z))/(2*y^2)}  
  /; FreeQ({a,b,w,y,z},x)&&!PossibleZeroQ(y),
        
elimzeroplus(y_.*x_+w_.*Sqrt(a_.+b_.*x_+c_.*x_^2)+z_., x_) := 
        If(PossibleZeroQ(c*w^2-y^2), Expand(-w^2*a+z^2)/(w^2*b-2*y*z), 
             {((-b)*w^2 + 2*y*z - Sqrt(b^2*w^4 - 4*a*c*w^4 + 4*a*w^2*y^2 - 4*b*w^2*y*z + 4*c*w^2*z^2))/(2*(c*w^2 - y^2)), 
              ((-b)*w^2 + 2*y*z + Sqrt(b^2*w^4 - 4*a*c*w^4 + 4*a*w^2*y^2 - 4*b*w^2*y*z + 4*c*w^2*z^2))/(2*(c*w^2 - y^2))})
  /; FreeQ({a,b,c,w,y,z},x)
} 
}