{

(*TODO: define other header symbol then Eliminate *)
{ 
eliminv,
eliminv(x_*a_^x_ * z_., x_) := ProductLog((Log(a)*x)/z)/Log(a)
  /; FreeQ({a,z},x)
},
(* Rules for case that expression is 0. *)
{ 
elimzero,
elimzero(x_^n_.*a_.+x_^m_*b_., x_) := E^((-I*Pi+Log(a)-Log(b))/(m-n))
  /;FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x)
} 
}