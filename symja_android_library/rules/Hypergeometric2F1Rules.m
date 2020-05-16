{  
Hypergeometric2F1(1/2,1,3/2,z_) := ArcTanh(Sqrt(z))/Sqrt(z),
Hypergeometric2F1(1, 2, 3/2, z_) := (Sqrt(z)*Sqrt(1 - z) + ArcSin(Sqrt(z))) / (2*(1 - z)^(3/2)*Sqrt(z)),
 
Hypergeometric2F1(3/2, 2, 5/2, a_. * z_^n_.) := -((3*(Sqrt(a)*z^(n/2) - ArcTanh(Sqrt(a)*z^(n/2)) + a*z^n*ArcTanh(Sqrt(a)*z^(n/2))))/(z^((3*n)/2)*(2*a^(3/2)*(-1 + a*z^n)))),
 
Hypergeometric2F1(m_Integer, n_IntegerQ, 2, 1) := CatalanNumber(-n) /; n<0 && m == (n+1)
}