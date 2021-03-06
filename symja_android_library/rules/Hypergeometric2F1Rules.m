{ 
Hypergeometric2F1(0, b_, c_, z_) = 1,
Hypergeometric2F1(a_, 0, c_, z_) = 1,
Hypergeometric2F1(a_, b_, c_, 0) = 1,

Hypergeometric2F1(1/2,1/2,3/2,z_) := ArcSin(Sqrt(z))/Sqrt(z), 

Hypergeometric2F1(1/2,1,-5/2,z_) := (5 - 7*z*(3 + 5*(-1 + z)*z))/ (5*(-1 + z)^4), 
Hypergeometric2F1(1/2,1,-3/2,z_) := (-3 + 5*(2 - 3*z)*z)/(3*(-1 + z)^3), 
Hypergeometric2F1(1/2,1,-1/2,z_) := (1 - 3*z)/(-1 + z)^2, 
Hypergeometric2F1(1/2,1,1/2,z_) := 1/(1 - z),
Hypergeometric2F1(1/2,1,1,z_) := 1/Sqrt(1 - z), 
Hypergeometric2F1(1/2,1,3/2,z_) := ArcTanh(Sqrt(z))/Sqrt(z), 
Hypergeometric2F1(1/2,1,2,z_) := (2 - 2*Sqrt(1 - z))/z, 
Hypergeometric2F1(1/2,1,5/2,z_) :=  (3*(Sqrt(z) + (-1 + z)*ArcTanh(Sqrt(z))))/(2*z^(3/2)), 
Hypergeometric2F1(1/2,1,3,z_) := -((4*(2 - 2*Sqrt(1-z) + (-3 + 2*Sqrt(1-z))*z))/(3*z^2)), 
Hypergeometric2F1(1/2,1,4,z_) := (2*(-8*(1 - z)^(5/2) + 15*z^2 - 20*z + 8)) / (5*z^3), 
Hypergeometric2F1(1/2,1,5,z_) :=  -((1/(35*z^4))*(8*(-16*(-1 + Sqrt(1-z)) + z*(8*(-7 + 6*Sqrt(1-z)) + z*(70 - 48*Sqrt(1-z) + (-35 + 16*Sqrt(1-z))*z))))), 

Hypergeometric2F1(1/2,2,-11/2,z_) := (231 + z*(-1890 + 13*z*(525 + 11*z*(-100 + 3*z*(45 + 7*z*(-6 + 5*z))))))/ (231*(-1 + z)^8), 
Hypergeometric2F1(1/2,2,-9/2,z_) := (-63 + 13*z*(35 + 11*z*(-10 + 3*z*(6 + 7*(-1 + z)*z))))/(63*(-1 + z)^7), 
Hypergeometric2F1(1/2,2,-7/2,z_) := (35 + 11*z*(-20 + 3*z*(18 + 7*z*(-4 + 5*z))))/(35*(-1 + z)^6), 
Hypergeometric2F1(1/2,2,-5/2,z_) := (-5 + 3*z*(9 + 7*z*(-3 + 5*z)))/ (5*(-1 + z)^5), 
Hypergeometric2F1(1/2,2,-3/2,z_) := (3 + 7*z*(-2 + 5*z))/(3*(-1 + z)^4), 
Hypergeometric2F1(1/2,2,-1/2,z_) := (-1 + 5*z)/(-1 + z)^3, 
Hypergeometric2F1(1/2,2,1/2,z_) := 1/(-1 + z)^2, 
Hypergeometric2F1(1/2,2,1,z_) := (2 - z)/(2*(1 - z)^(3/2)), 
Hypergeometric2F1(1/2,2,3/2,z_) := (1/2)*(1/(1 - z) + ArcTanh(Sqrt(z))/Sqrt(z)), 
Hypergeometric2F1(1/2,2,2,z_) := 1/Sqrt(1 - z), 
Hypergeometric2F1(1/2,2,5/2,z_) := -(3/(4*z)) + (3*(1 + z)*ArcTanh(Sqrt(z)))/(4*z^(3/2)), 
Hypergeometric2F1(1/2,2,3,z_) := (4*(2 - (2 + z)*Sqrt(1 - z)))/(3*z^2), 
Hypergeometric2F1(1/2,2,7/2,z_) :=  (15*((-(-3 + z))*Sqrt(z) + (-3 + 2*z + z^2)*ArcTanh(Sqrt(z))))/(16*z^(5/2)), 
Hypergeometric2F1(1/2,2,4,z_) :=  (8*(-4 + 4*Sqrt(1 - z) + (5 - 3*Sqrt(1 - z))*z - Sqrt(1 - z)*z^2))/(5*z^3),
Hypergeometric2F1(1/2,2,9/2,z_) :=  (35*(Sqrt(z)*(15 + z*(22 + 3*z)) + 3*(-5 + z)*(1 + z)^2*ArcTan(Sqrt(z))))/ (96*z^(7/2)), 
Hypergeometric2F1(1/2,2,5,z_) := -((16*(24*(-1 + Sqrt(1 - z)) + z*(56 - 44*Sqrt(1 - z) + z*(-35 + 16*Sqrt(1 - z) + 4*Sqrt(1 - z)*z))))/(35*z^4)), 
Hypergeometric2F1(1/2,2,11/2,z_) :=  (21*(Sqrt(z)*(105 + z*(-265 + (191 - 15*z)*z)) + 15*(-1 + z)^3*(7 + z)*ArcTanh(Sqrt(z))))/(256*z^(9/2)), 

Hypergeometric2F1(1,3/2,-5/2,z_) := (-5 + 7*z*(4 + 5*z (-2 + z*(4 + z))))/ (5*(-1 + z)^5), 
Hypergeometric2F1(1,3/2,-3/2,z_) := (1 + 5*z (-1 + z*(3 + z)))/(-1 + z)^4, 
Hypergeometric2F1(1,3/2,-1/2,z_) := (-1 + 6*z + 3*z^2)/(-1 + z)^3, 
Hypergeometric2F1(1,3/2,1/2,z_) := (1 + z)/(-1 + z)^2,
Hypergeometric2F1(1,3/2,1,z_) := 1/(1 - z)^(3/2),
Hypergeometric2F1(1,3/2,3/2,z_) := (2*(1 - Sqrt(1 - z)))/(z*Sqrt(1 - z)),
Hypergeometric2F1(1,3/2,2,z_) := 1/(1 - z)^(3/2),
Hypergeometric2F1(1,3/2,5/2,z_) := -(3/z) + (3*ArcTanh(Sqrt(z)))/z^(3/2), 

Hypergeometric2F1(1,1,2,z_) := -(Log(1 - z)/z),
Hypergeometric2F1(1,b_,2,z_) := -((-1 + (1 - z)^b + z)/((1 - z)^b*((-1 + b)*z))),
Hypergeometric2F1(1, 2, 3/2, z_) := (Sqrt(z)*Sqrt(1 - z) + ArcSin(Sqrt(z))) / (2*(1 - z)^(3/2)*Sqrt(z)),
 
Hypergeometric2F1(3/2, 2, 5/2, a_. * z_^n_.) := -((3*(Sqrt(a)*z^(n/2) - ArcTanh(Sqrt(a)*z^(n/2)) + a*z^n*ArcTanh(Sqrt(a)*z^(n/2))))/(z^((3*n)/2)*(2*a^(3/2)*(-1 + a*z^n)))),
 
Hypergeometric2F1(m_Integer, n_Integer, 2, 1) := CatalanNumber(-n) /; n<0 && m == (n+1),
Hypergeometric2F1[a_, b_, c_, z_] := ((1-z)^(-1-a)*(z*a/(b - 1)-z*b/(b - 1)+z/(b - 1)+b/(b - 1)-1/(b - 1))) /; NumberQ(b)&&NumberQ(c)&&!(IntegerQ(c)&&c<0)&&PossibleZeroQ(b-1-c),
Hypergeometric2F1[b_, a_, c_, z_] := ((1-z)^(-1-a)*(z*a/(b - 1)-z*b/(b - 1)+z/(b - 1)+b/(b - 1)-1/(b - 1))) /; NumberQ(b)&&NumberQ(c)&&!(IntegerQ(c)&&c<0)&&PossibleZeroQ(b-1-c) 
}