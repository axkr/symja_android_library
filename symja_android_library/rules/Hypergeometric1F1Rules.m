{  
  Hypergeometric1F1(1/2, 3/2, z_) := (Sqrt(Pi)*Erfi(Sqrt(z)))/(2*Sqrt(z)),
  Hypergeometric1F1(1, 1/2, z_) := 1+E^z*Sqrt(Pi)*Sqrt(z)*Erf(Sqrt(z)),
    
  (* fungrim "dec042" *)
  Hypergeometric1F1(a_Integer,b_,z_) := Module({n=(-a)}, Sum(FactorialPower(-n,k,-1)/FunctionExpand(FactorialPower(b,k,-1)) * (z^k / Factorial(k)), {k, 0, n}) 
     /; a<0 && !(TrueQ(Element(b, Integers) && b<=0 && b>a)) ) 
  
}