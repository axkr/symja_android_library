{
 Abs(x_^n_Integer) := (Im(x)^2+Re(x)^2)^(n/2)
   /; EvenQ(n),
 ArcCos(x_^(-1)) := ArcSec(x),
 ArcSec(x_^(-1)) := ArcCos(x),
 ArcCot(Sqrt(x_^2)) := (Sqrt(x^2)*ArcCot(x))/x,
 ArcCot(x_^(-1)) := ArcTan(x),
 ArcTan(Sqrt(x_^2)) := (Sqrt(x^2)*ArcTan(x))/x,
 ArcTan(x_^(-1)) := ArcCot(x),
 ArcCsc(Sqrt(x_^2)) := (Sqrt(x^2)*ArcCsc(x))/x ,
 ArcCsc(x_^(-1)) := ArcSin(x),
 ArcSin(Sqrt(x_^2)) := (Sqrt(x^2)*ArcSin(x))/x,
 ArcSin(x_^(-1)) := ArcCsc(x),
 
 BetaRegularized(z_, a_, b_) := (Beta(z, a, b)*Gamma(a + b))/(Gamma(a)*Gamma(b)),
 BetaRegularized(y_, z_, a_, b_) := ((-Beta(y, a, b) + Beta(z, a, b))*Gamma(a + b))/(Gamma(a)*Gamma(b)),
  
 CatalanNumber(n_) := (2^(2*n)*Gamma(1/2+n))/(Sqrt(Pi)*Gamma(2+n)),
 ChebyshevT(n_,x_) :=  Cos(n*ArcCos(x)),
 ChebyshevU(n_,x_) :=  Sin((1 + n)*ArcCos(x))/(Sqrt(1 - x)*Sqrt(1 + x)),
 
 Cos(n_Integer*ArcSin(z_)) := ChebyshevT(n, Sqrt(1-z^2))
  /; n>0,
 
 CosIntegral(Sqrt(z_^n_)) := CosIntegral(z^(n/2)) - Log(z^(n/2)) + Log(z^n)/2,
 CoshIntegral(Sqrt(z_^n_)) :=  CoshIntegral(z^(n/2)) - Log(z^(n/2)) + Log(z^n)/2,
 
 ExpIntegralE(n_,z_) := z^(-1 + n)*Gamma(1 - n, z),
 ExpIntegralEi(Log(z_)) := LogIntegral(z),
 
 Factorial(z_) := Gamma(1+z),  
 Factorial2(n_) := 2^(n/2 + (1/4)*(1 - Cos(n*Pi)))*Pi^((1/4)*(-1 + Cos(n*Pi)))*Gamma(1 + n/2),
 
 Fibonacci(m_Integer+n_) := ((1/2)*Fibonacci(m)*LucasL(n) + (1/2)*Fibonacci(n)*LucasL(m)) 
  /; Element(n, Integers),
 Fibonacci(n_+a_) := ((2/(1 + Sqrt(5)))^(-a-n)-((1/2)*(1+Sqrt(5)))^(-a-n)*Cos((a+n)*Pi))/Sqrt(5)
  /; Element(n, Integers),
  
 Gamma(a_.Integer+z_)*Gamma(b_.Integer+z_)^(-1) := If(b<a, Product((z+i), {i,b,a-1}), Product((z+i), {i,a,b-1})^(-1)),
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
 Hypergeometric2F1(2, b_, c_, -1/2) := (3-b)/3
  /; (5/2 - 1/2*b)==Expand(c), 
 Hypergeometric2F1(a_, a_ + 1/2, c_, z_) := (2^(-1+2*a)*(1+Sqrt(1-z))^(1-2*a))/Sqrt(1-z)
  /; 2*a==c, 
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
 Log(ProductLog(x_)) = x,
 
 LogisticSigmoid(x_) := 1/(1 + E^(-x)),
 
 LogGamma(x_) := Log(Gamma(x)) 
  /; x>0,
 PolyGamma(n_Integer, 1/2) := (-1)^(n + 1)*n!*(2^(n + 1) - 1)*Zeta(n+1)  
  /; n>0,
  
 Power(Abs(x_),y_Integer) := x^y
  /; EvenQ(y) && Element(x,Reals),
  
 ProductLog(x_*Log(x_)) := Log(x) 
  /; x > 1/E,
 E^ProductLog(x_) := x/ProductLog(x),
  
  
 Sin(n_Integer*ArcSin(z_)) := z*ChebyshevU(n - 1, Sqrt(1 - z^2))
  /; n > 0,
 Sin(n_Integer*ArcTan(z_)) := Sum((-1)^k*Binomial(n, 2*k + 1)*z^(2*k + 1), {k, 0, Floor((n-1)/2)}) / (1 + z^2)^(n/2) 
   /; n > 0,

 Sinc(z_) := Sin(z) / z 
  /; z!=0,
 SinIntegral(Sqrt(z_^n_)) := (Sqrt(z^n)*SinIntegral(z^(n/2)))/z^(n/2), 
 SinhIntegral(Sqrt(z_^n_)) := (Sqrt(z^n)*SinhIntegral(z^(n/2)))/z^(n/2),
 
 SphericalBesselJ(a_,b_) := (Sqrt(Pi/2)*BesselJ((1/2)*(1 + 2*a), b))/Sqrt(b),
 SphericalBesselY(a_,b_) := (Sqrt(Pi/2)*BesselY((1/2)*(1 + 2*a), b))/Sqrt(b),
 
 SphericalHankelH1(a_,b_) := (Sqrt(Pi/2)*BesselJ((1/2)*(1 + 2*a), b))/Sqrt(b) + (I*Sqrt(Pi/2)*BesselY((1/2)*(1 + 2*a), b))/Sqrt(b), 
 SphericalHankelH2(a_,b_) := (Sqrt(Pi/2)*BesselJ((1/2)*(1 + 2*a), b))/Sqrt(b) - (I*Sqrt(Pi/2)*BesselY((1/2)*(1 + 2*a), b))/Sqrt(b),

 SphericalHarmonicY(a_,1,t_,p_) := -((a*(1 + a)*Sqrt(1 + 2*a)*E^(I*p)*Sqrt(1 - Cos(t))*Sqrt(1 + Cos(t))*Sqrt(Gamma(a))*Hypergeometric2F1(1 - a, 2 + a, 2, Sin(t/2)^2)*Sin(t))/(4*Sqrt(Pi)*Sqrt(1 - Cos(t)^2)*Sqrt(Gamma(2 + a)))),
 SphericalHarmonicY(a_,b_,t_,p_) := (Sqrt(1 + 2*a)*E^(I*b*p)*(1 + Cos(t))^(b/2)*Sqrt(Gamma(1 + a - b))*Hypergeometric2F1(-a, 1 + a, 1 - b, Sin(t/2)^2)*Sin(t)^b)/((1 - Cos(t))^(b/2)*(1 - Cos(t)^2)^(b/2)*(2*Sqrt(Pi)*Gamma(1 - b)*Sqrt(Gamma(1 + a + b)))),
 
 WeberE(a_,b_) := (2*b*Cos((a*Pi)/2)^2*HypergeometricPFQ({1},{3/2-a/2,3/2+a/2},-(b^2/4)))/((-1+a)*(1+a)*Pi)+(2*HypergeometricPFQ({1},{1-a/2,1+a/2},-(b^2/4))*Sin((a*Pi)/2)^2)/(a*Pi),
 WeberE(a_,b_,c_) := -((c*Cos((a*Pi)/2)*Gamma(2+b)*HypergeometricPFQ({1+b/2,3/2+b/2},{3/2,3/2-a/2+b/2,3/2+a/2+b/2},-(c^2/4)))/(2*Gamma(3/2-a/2+b/2)*Gamma(3/2+a/2+b/2)))+(Gamma(1+b)*HypergeometricPFQ({1/2+b/2,1+b/2},{1/2,1-a/2+b/2,1+a/2+b/2},-(c^2/4))*Sin((a*Pi)/2))/(Gamma(1-a/2+b/2)*Gamma(1+a/2+b/2)), 
 
 WhittakerM(k_, m_, z_) := (z^(1/2+m) * Hypergeometric1F1(1/2-k+m, 1+ 2*m, z))/E^(z/2),

 WhittakerW(k_, m_, z_) := (z^(1/2+m) * HypergeometricU(1/2-k+m, 1+2*m, z))/E^(z/2),

 Zeta(n_Integer, x_) := 1/((-1)^n*(n-1)!) * PolyGamma(n-1,x)
   /; EvenQ(n) && n>1
}