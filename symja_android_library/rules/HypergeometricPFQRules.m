{   
  HypergeometricPFQ({a_,b_},{c _,b_},z_) := HypergeometricPFQ({a},{c},z),
  HypergeometricPFQ({1/2, b_}, {3/2, c_}, z_) := (b/(2*b - 1))*(Sqrt(Pi/z)*Erfi(Sqrt(z)) - (Gamma(b) - Gamma(b,-z))/(-z)^b) 
     /; PossibleZeroQ(b+1-c)  
}