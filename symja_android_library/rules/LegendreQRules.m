 {  
   LegendreQ(x_, 1) = ComplexInfinity,
   LegendreQ(x_, -1) = ComplexInfinity,
   LegendreQ(x_IntegerQ, z_) := ComplexInfinity 
     /; x<0,
   LegendreQ(0, z_) := (1/2)*Log(1 + z) - (1/2)*Log(1 - z),
   LegendreQ(1, z_) := -1+z*( - (1/2)*Log(1 - z) + (1/2)*Log(1 + z)),
   LegendreQ(n_IntegerQ, z_) := (1/2)*(Log(1+z)-Log(1-z))*LegendreP(n, z) - Sum(((2*n-4*k-1)/((2*k + 1)*(n - k)))*LegendreP(n-2*k-1, z), {k, 0, Floor((n - 1)/2)}) 
     /;  n >= 0,
   LegendreQ(-(1/2), 2*z_ - 1) := EllipticK(z)
 }