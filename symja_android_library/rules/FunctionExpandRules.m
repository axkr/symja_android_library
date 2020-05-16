{
 ArcCot(Sqrt(x_^2)) := (Sqrt(x^2)*ArcCot(x))/x,
 ArcSin(Sqrt(x_^2)) := (Sqrt(x^2)*ArcSin(x))/x,
 ArcTan(Sqrt(x_^2)) := (Sqrt(x^2)*ArcTan(x))/x,
 
 Cos(n_Integer*ArcSin(z_)) := ChebyshevT(n, Sqrt(1 - z^2))
  /; n>0,
 
 Gamma(-1, z_) :=1/(E^z*z) + ExpIntegralEi(-z) + (1/2)*(Log(-(1/z)) - Log(-z)) + Log(z),
 Gamma(-1/2, z_) := 2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*(1 - Erf(Sqrt(z))),
 Gamma(0, z_) :=-ExpIntegralEi(-z) + (1/2)*(-Log(-(1/z)) + Log(-z)) - Log(z),
 Gamma(1/2, z_) := Sqrt(Pi)*(1 - Erf(Sqrt(z))),

 Hypergeometric2F1(a_, b_, b_ + n_Integer, z_) := (1-z)^(-a+n) * Sum((Pochhammer(n, k)*Pochhammer(b-a+n,k)*z^k) / (Pochhammer(b+n,k)*k!), {k, 0, -n})  
  /; n<0,
 
 PolyGamma(n_Integer, 1/2) := (-1)^(n + 1)*n!*(2^(n + 1) - 1)*Zeta(n+1)  
  /; n>0,
  
 Sinc(z_) := Sin(z) / z 
  /; z!=0
}