{
 ArcCot(Sqrt(x_^2)) := (Sqrt(x^2)*ArcCot(x))/x,
 ArcSin(Sqrt(x_^2)) := (Sqrt(x^2)*ArcSin(x))/x,
 ArcTan(Sqrt(x_^2)) := (Sqrt(x^2)*ArcTan(x))/x,
 
 BetaRegularized(z_, a_, b_) := (Beta(z, a, b)*Gamma(a + b))/(Gamma(a)*Gamma(b)),
 BetaRegularized(y_, z_, a_, b_) := ((-Beta(y, a, b) + Beta(z, a, b))*Gamma(a + b))/(Gamma(a)*Gamma(b)),
  
 ChebyshevT(n_,x_) :=  Cos(n*ArcCos(x)),
 ChebyshevU(n_,x_) :=  Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)),
 
 Cos(n_Integer*ArcSin(z_)) := ChebyshevT(n, Sqrt(1 - z^2))
  /; n>0,
 
 ExpIntegralE(n_,z_) := z^(-1 + n)*Gamma(1 - n, z),
 
 Factorial(z_) := Gamma(1+z),  
 Factorial2(n_) := 2^(n/2 + (1/4)*(1 - Cos(n*Pi)))*Pi^((1/4)*(-1 + Cos(n*Pi)))*Gamma(1 + n/2),
 
 Fibonacci(m_Integer+n_) := ((1/2)*Fibonacci(m)*LucasL(n) + (1/2)*Fibonacci(n)*LucasL(m)) 
  /; Element(n, Integers),
 Fibonacci(n_+a_) := ((2/(1 + Sqrt(5)))^(-a-n)-((1/2)*(1+Sqrt(5)))^(-a-n)*Cos((a+n)*Pi))/Sqrt(5)
  /; Element(n, Integers),
  
 Gamma(-1, z_) := 1/(E^z*z) + ExpIntegralEi(-z) + (1/2)*(Log(-(1/z)) - Log(-z)) + Log(z),
 Gamma(-1/2, z_) := 2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*(1 - Erf(Sqrt(z))),
 Gamma(0, z_) := -ExpIntegralEi(-z) + (1/2)*(-Log(-(1/z)) + Log(-z)) - Log(z),
 Gamma(1/2, z_) := Sqrt(Pi)*(1 - Erf(Sqrt(z))),

 GammaRegularized(a_, z_) := Gamma(a,z) / Gamma(a),
 
 GegenbauerC(n_, x_) := (2*Cos(n*ArcCos(x)))/n,
 
 Gudermannian(z_) := Piecewise({{(1/2)*(Pi - 4*ArcCot(E^z)), Re(z)>0||(Re(z)==0&&Im(z)>=0 )}}, (1/2)*(-Pi + 4*ArcTan(E^z))), 
 
 HarmonicNumber(n_) := EulerGamma + PolyGamma(0, 1 + n),
 HarmonicNumber(z_, n_) := -HurwitzZeta(n, 1 + z) + Zeta(n),
 Haversine(z_) := (1/2)*(1-Cos(z)),
 HurwitzZeta(n_Integer, a_) := ((-1)^n/(n - 1)!)*PolyGamma(n - 1, a)
  /; n>1,
   
 HypergeometricPFQ({1/2}, {1, 1}, z_) := BesselI(0, Sqrt(z))^2,
 Hypergeometric2F1(a_, b_, b_ + n_Integer, z_) := (1-z)^(-a+n) * Sum((Pochhammer(n, k)*Pochhammer(b-a+n,k)*z^k) / (Pochhammer(b+n,k)*k!), {k, 0, -n})  
  /; n<0,
 
 InverseGudermannian(z_) := Log(Tan(Pi/4 + z/2)), 
 InverseHaversine(z_) := 2*ArcSin(Sqrt(z)),
 
 JacobiCD(f_, g_) := JacobiCN(f,g) / JacobiDN(f,g),
 JacobiDC(f_, g_) := JacobiDN(f,g) / JacobiCN(f,g),
 JacobiNC(f_, g_) := 1 / JacobiCN(f,g), 
 JacobiND(f_, g_) := 1 / JacobiDN(f,g), 
 JacobiSC(f_, g_) := JacobiSN(f,g) / JacobiCN(f,g),
 JacobiSD(f_, g_) := JacobiSN(f,g) / JacobiDN(f,g),
 
 LegendreQ(l_, m_, x_) := -((Pi*Csc(m*Pi)*Gamma(1+l+m)*LegendreP(l, -m, x))/(2*Gamma(1+l-m))) + (1/2)*Pi*Cot(m*Pi)*LegendreP(l, m, x),
 
 Log((1 + I*Sqrt(3))/2) = I*Pi/3,
  
 LogisticSigmoid(x_) := 1/(1 + E^(-x)),
 
 LogGamma(x_) := Log(Gamma(x)) 
  /; x>0,
 PolyGamma(n_Integer, 1/2) := (-1)^(n + 1)*n!*(2^(n + 1) - 1)*Zeta(n+1)  
  /; n>0,
  
 Power(Abs(x_),y_Integer) := x^y
  /; EvenQ(y) && Element(x,Reals),
  
 ProductLog(x_*Log(x_)) := Log(x) 
  /; x > 1/E,
  
 Sin(n_Integer*ArcSin(z_)) := z*ChebyshevU(n - 1, Sqrt(1 - z^2))
  /; n > 0,
 Sin(n_Integer*ArcTan(z_)) := Sum((-1)^k*Binomial(n, 2*k + 1)*z^(2*k + 1), {k, 0, Floor((n-1)/2)}) / (1 + z^2)^(n/2) 
   /; n > 0,
  
 Sinc(z_) := Sin(z) / z 
  /; z!=0,

 SphericalHarmonicY(a_,1,t_,p_) := -((a*(1 + a)*Sqrt(1 + 2*a)*E^(I*p)*Sqrt(1 - Cos(t))*Sqrt(1 + Cos(t))*Sqrt(Gamma(a))*Hypergeometric2F1(1 - a, 2 + a, 2, Sin(t/2)^2)*Sin(t))/(4*Sqrt(Pi)*Sqrt(1 - Cos(t)^2)*Sqrt(Gamma(2 + a)))),
 SphericalHarmonicY(a_,b_,t_,p_) := (Sqrt(1 + 2*a)*E^(I*b*p)*(1 + Cos(t))^(b/2)*Sqrt(Gamma(1 + a - b))*Hypergeometric2F1(-a, 1 + a, 1 - b, Sin(t/2)^2)*Sin(t)^b)/((1 - Cos(t))^(b/2)*(1 - Cos(t)^2)^(b/2)*(2*Sqrt(Pi)*Gamma(1 - b)*Sqrt(Gamma(1 + a + b)))),
    
 WhittakerM(k_, m_, z_) := (z^(1/2+m) * Hypergeometric1F1(1/2-k+m, 1+ 2*m, z))/E^(z/2),

 WhittakerW(k_, m_, z_) := (z^(1/2+m) * HypergeometricU(1/2-k+m, 1+2*m, z))/E^(z/2)

}