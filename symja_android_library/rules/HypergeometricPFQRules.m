{ 
  HypergeometricPFQ[{},{},z_] := E^(z),  
  HypergeometricPFQ[{a_},{},z_] := (1-z)^(-a),
  HypergeometricPFQ[{},{b_},z_] := z^(1/2-b/2)*BesselI(-1+b,2*Sqrt(z))*Gamma(b),
  HypergeometricPFQ({a_,b_},{c_,b_},z_) := HypergeometricPFQ({a},{c},z),
  HypergeometricPFQ({1/2, b_}, {3/2, c_}, z_) := (b/(2*b - 1))*(Sqrt(Pi/z)*Erfi(Sqrt(z)) - (Gamma(b) - Gamma(b,-z))/(-z)^b) 
     /; PossibleZeroQ(b+1-c),
  HypergeometricPFQ({1,1}, {2,2}, z_) := -(EulerGamma+Gamma(0,-z)+Log(-z))/z,
  HypergeometricPFQ({1/2}, {3/2, 3/2}, z_) := SinhIntegral(2*Sqrt(z))/(2*Sqrt(z)),
  HypergeometricPFQ({1,1}, {2,2,3/2}, z_) := (-EulerGamma+CoshIntegral(2*Sqrt(z))-Log(2*Sqrt(z)))/z,
  HypergeometricPFQ({1, 1, 1}, {2, 2}, z_) := PolyLog(2,z)/z,

 HypergeometricPFQ({1}, {1/2, c_}, z_) := 1+Sqrt(Pi)*z^(1/4*(3-2*c))*Gamma(c)*StruveL(1+1/2*(-3+2*c),2*Sqrt(z)),
 HypergeometricPFQ({1}, {3/2, c_}, z_) := 1/2*Sqrt(Pi)*z^(1/4-c/2)*Gamma(c)*StruveL(-3/2+c,2*Sqrt(z))
}