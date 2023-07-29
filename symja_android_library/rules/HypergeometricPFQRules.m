{ 
  HypergeometricPFQ[{},{},z_] := E^(z),  
  HypergeometricPFQ[{a_},{},z_] := (1-z)^(-a),
  HypergeometricPFQ[{},{b_},z_] := z^(1/2-b/2)*BesselI(-1+b,2*Sqrt(z))*Gamma(b),
  HypergeometricPFQ({a_,b_},{c_,b_},z_) := HypergeometricPFQ({a},{c},z),
  HypergeometricPFQ({1/2, b_}, {3/2, c_}, z_) := (b/(2*b - 1))*(Sqrt(Pi/z)*Erfi(Sqrt(z)) - (Gamma(b) - Gamma(b,-z))/(-z)^b) 
     /; PossibleZeroQ(b+1-c)  
}