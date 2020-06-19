{ 
Hypergeometric2F1(0, b_, c_, z_) = 1,
Hypergeometric2F1(a_, 0, c_, z_) = 1,
Hypergeometric2F1(a_, b_, c_, 0) = 1,
Hypergeometric2F1(1/2,1/2,3/2,z_) := ArcSin(Sqrt(z))/Sqrt(z), 
Hypergeometric2F1(1/2,1,3/2,z_) := ArcTanh(Sqrt(z))/Sqrt(z), 
Hypergeometric2F1(1,1,2,z_) := -(Log(1 - z)/z),
Hypergeometric2F1(1,b_,2,z_) := -((-1 + (1 - z)^b + z)/((1 - z)^b*((-1 + b)*z))),
Hypergeometric2F1(1, 2, 3/2, z_) := (Sqrt(z)*Sqrt(1 - z) + ArcSin(Sqrt(z))) / (2*(1 - z)^(3/2)*Sqrt(z)),
 
Hypergeometric2F1(3/2, 2, 5/2, a_. * z_^n_.) := -((3*(Sqrt(a)*z^(n/2) - ArcTanh(Sqrt(a)*z^(n/2)) + a*z^n*ArcTanh(Sqrt(a)*z^(n/2))))/(z^((3*n)/2)*(2*a^(3/2)*(-1 + a*z^n)))),
 
Hypergeometric2F1(m_Integer, n_Integer, 2, 1) := CatalanNumber(-n) /; n<0 && m == (n+1),
Hypergeometric2F1[a_, b_, c_, z_] := ((1-z)^(-1-a)*(z*a/(b - 1)-z*b/(b - 1)+z/(b - 1)+b/(b - 1)-1/(b - 1))) /; NumberQ(b)&&NumberQ(c)&&!(IntegerQ(c)&&c<0)&&PossibleZeroQ(b-1-c),
Hypergeometric2F1[b_, a_, c_, z_] := ((1-z)^(-1-a)*(z*a/(b - 1)-z*b/(b - 1)+z/(b - 1)+b/(b - 1)-1/(b - 1))) /; NumberQ(b)&&NumberQ(c)&&!(IntegerQ(c)&&c<0)&&PossibleZeroQ(b-1-c) 
}