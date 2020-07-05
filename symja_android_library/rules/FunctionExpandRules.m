{
 ArcCot(Sqrt(x_^2)) := (Sqrt(x^2)*ArcCot(x))/x,
 ArcSin(Sqrt(x_^2)) := (Sqrt(x^2)*ArcSin(x))/x,
 ArcTan(Sqrt(x_^2)) := (Sqrt(x^2)*ArcTan(x))/x,
 
 ChebyshevT(n_,x_) :=  Cos(n*ArcCos(x)),
 ChebyshevU(n_,x_) :=  Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)),
 
 Cos(n_Integer*ArcSin(z_)) := ChebyshevT(n, Sqrt(1 - z^2))
  /; n>0,
 
 Fibonacci(m_Integer+n_) := ((1/2)*Fibonacci(m)*LucasL(n) + (1/2)*Fibonacci(n)*LucasL(m)) 
  /; Element(n, Integers),
					
 Gamma(-1, z_) := 1/(E^z*z) + ExpIntegralEi(-z) + (1/2)*(Log(-(1/z)) - Log(-z)) + Log(z),
 Gamma(-1/2, z_) := 2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*(1 - Erf(Sqrt(z))),
 Gamma(0, z_) := -ExpIntegralEi(-z) + (1/2)*(-Log(-(1/z)) + Log(-z)) - Log(z),
 Gamma(1/2, z_) := Sqrt(Pi)*(1 - Erf(Sqrt(z))),

 GegenbauerC(n_, x_) := (2*Cos(n*ArcCos(x)))/n,
 
 HarmonicNumber(n_) := EulerGamma + PolyGamma(0, 1 + n),
 HarmonicNumber(z_, n_) := -HurwitzZeta(n, 1 + z) + Zeta(n),
 HurwitzZeta(n_Integer, a_) := ((-1)^n/(n - 1)!)*PolyGamma(n - 1, a)
  /; n>1,
  
 Hypergeometric2F1(a_, b_, b_ + n_Integer, z_) := (1-z)^(-a+n) * Sum((Pochhammer(n, k)*Pochhammer(b-a+n,k)*z^k) / (Pochhammer(b+n,k)*k!), {k, 0, -n})  
  /; n<0,
 
 LegendreQ(l_, m_, x_) := -((Pi*Csc(m*Pi)*Gamma(1+l+m)*LegendreP(l, -m, x))/(2*Gamma(1+l-m))) + (1/2)*Pi*Cot(m*Pi)*LegendreP(l, m, x),
 LogisticSigmoid(x_) := 1/(1 + E^(-x)),
 
 PolyGamma(n_Integer, 1/2) := (-1)^(n + 1)*n!*(2^(n + 1) - 1)*Zeta(n+1)  
  /; n>0,

 ProductLog(x_*Log(x_)) := Log(x) 
  /; x > 1/E,
  
 Sin(n_Integer*ArcSin(z_)) := z*ChebyshevU(n - 1, Sqrt(1 - z^2))
  /; n > 0,
 Sin(n_Integer*ArcTan(z_)) := Sum((-1)^k*Binomial(n, 2*k + 1)*z^(2*k + 1), {k, 0, Floor((n-1)/2)}) / (1 + z^2)^(n/2) 
   /; n > 0,
  
 Sinc(z_) := Sin(z) / z 
  /; z!=0,


 WhittakerM(k_, m_, z_) := (z^(1/2+m) * Hypergeometric1F1(1/2-k+m, 1+ 2*m, z))/E^(z/2),

 WhittakerW(k_, m_, z_) := (z^(1/2+m) * HypergeometricU(1/2-k+m, 1+2*m, z))/E^(z/2)

}