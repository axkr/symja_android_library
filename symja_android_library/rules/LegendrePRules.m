 { 
   LegendreP(x_, 1) = 1,
   
   LegendreP(x_?NumericQ, 0) :=  Sqrt(Pi)/(Gamma((1 - x)/2) * Gamma(1 + x/2)),
   LegendreP(x_?IntegerQ, -1) := (-1)^x,
   LegendreP(-(1/2), 1 - 2*z_) := 2/Pi * EllipticK(z)
 }