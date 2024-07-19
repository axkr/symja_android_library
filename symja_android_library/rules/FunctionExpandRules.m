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
 
 Cos(Pi/60) = -(-1/8*Sqrt(3)*(-1+Sqrt(5))-Sqrt(1/2*(5+Sqrt(5)))/4)/Sqrt(2)-(1/8*(-1+Sqrt(5))-Sqrt(3/2*(5+Sqrt(5)))/4)/Sqrt(2),
 Cos(n_Integer*ArcSin(z_)) := ChebyshevT(n, Sqrt(1-z^2))
  /; n>0,
 Cos(n_Integer*ArcCot(z_)) := n*Sum(((-1)^k*(-1-k+n)!)/(2^(1+2*k-n)*k!*(-2*k+n)!)*((1+z^2)/z^2)^(k-n/2),{k,0,Floor(n/2)})
   /; n > 0,
  
 CosIntegral(Sqrt(z_^n_)) := CosIntegral(z^(n/2)) - Log(z^(n/2)) + Log(z^n)/2,
 CoshIntegral(Sqrt(z_^n_)) :=  CoshIntegral(z^(n/2)) - Log(z^(n/2)) + Log(z^n)/2,
 
 Erf(Power(Power(a_,b_), 1/2)) := (Sqrt(a^b)*Erf(a^(b/2)))/a^(b/2),
 Erf(Power(Power(a_,b_), -1/2)) := (a^(b/2)*Erf(a^(-b/2)))/Sqrt(a^b),
 Erfc(a_^b_) := 1-Erf(a^b),    
 Erfi(Power(Power(a_,b_), 1/2)) := (Sqrt(a^b)*Erfi(a^(b/2)))/a^(b/2),
 Erfi(Power(Power(a_,b_), -1/2)) := (a^(b/2)*Erfi(a^(-b/2)))/Sqrt(a^b),

 ExpIntegralEi(Log(z_)) := LogIntegral(z),
 
 FactorialPower(x_,n_Integer,-1) := Product(x+k,{k,0,n-1})
   /; n>0,  
 FactorialPower(x_,n_,-1) := (x^n*Gamma(1-x))/((-x)^n*Gamma(1-n-x)),
 FactorialPower(x_,n_Integer) := Product(x-+k,{k,0,n-1})
   /; n>0, 
 FactorialPower(x_,n_) := Gamma(1+x)/Gamma(1-n+x),
 FactorialPower(x_,n_,h_) := (x^n*Gamma(1+x/h))/((x/h)^n*Gamma(1-n+x/h))
   /; UnsameQ(h,0),
  
 Gamma(a_.Integer+z_)*Gamma(b_.Integer+z_)^(-1)*x_. := If(b<a, x*Product((z+i), {i,b,a-1}), x*Product((z+i), {i,a,b-1})^(-1)),
 Gamma(-1, z_) := 1/(E^z*z) + ExpIntegralEi(-z) + (1/2)*(Log(-(1/z)) - Log(-z)) + Log(z),
 Gamma(-1/2, z_) := 2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*(1 - Erf(Sqrt(z))),
 Gamma(0, z_) := -ExpIntegralEi(-z) + (1/2)*(-Log(-(1/z)) + Log(-z)) - Log(z),
 Gamma(1/2, z_) := Sqrt(Pi)*(1 - Erf(Sqrt(z))),

 GammaRegularized(a_, z_) := Gamma(a,z) / Gamma(a),
 GammaRegularized(a_, y_, z_) :=  Gamma(a,y)/Gamma(a)-Gamma(a,z)/Gamma(a),
 
 Gudermannian(z_) := Piecewise({{(1/2)*(Pi - 4*ArcCot(E^z)), Re(z)>0||(Re(z)==0&&Im(z)>=0 )}}, (1/2)*(-Pi + 4*ArcTan(E^z))), 
 
 HarmonicNumber(n_) := EulerGamma + PolyGamma(0, 1 + n),
 HarmonicNumber(z_, n_) := -HurwitzZeta(n, 1 + z) + Zeta(n),
 Haversine(z_) := (1/2)*(1-Cos(z)),
 HurwitzZeta(n_Integer, a_) := ((-1)^n/(n - 1)!)*PolyGamma(n - 1, a)
  /; n>1,
   
 HypergeometricPFQ({1/2}, {1, 1}, z_) := BesselI(0, Sqrt(z))^2,
 
 HypergeometricPFQ({}, {a_}, z_) := z^(1/2-a/2)*BesselI(-1+a,2*Sqrt(z))*Gamma(a),
 HypergeometricPFQ({a_}, {b_}, z_) := Hypergeometric1F1(a,b,z),
 HypergeometricPFQ({a_,b_}, {c_}, z_) := Hypergeometric2F1(a,b,c,z),
 
 Hypergeometric0F1(a_,z_) := z^(1/2-a/2)*BesselI(-1+a,2*Sqrt(z))*Gamma(a),
 Hypergeometric1F1(a_,1,z_) := LaguerreL(-a,z),
 Hypergeometric1F1(a_,b_,z_) := (E^(z/2)*z^(1/2-a)*BesselI(1/2*(-1+b),z/2)*Gamma(1/2+a))/4^(1/2-a)
  /; a==b/2,
 
 Hypergeometric2F1(2, b_, c_, -1/2) := (3-b)/3
  /; (5/2 - 1/2*b)==Expand(c),
 Hypergeometric2F1(a_, a_ + 1/2, c_, z_) := (2^(-1+2*a)*(1+Sqrt(1-z))^(1-2*a))/Sqrt(1-z)
  /; 2*a==c, 
 Hypergeometric2F1(a_, b_, b_ + n_Integer, z_) := (1-z)^(-a+n) * Sum((Pochhammer(n, k)*Pochhammer(b-a+n,k)*z^k) / (Pochhammer(b+n,k)*k!), {k, 0, -n})  
  /; n<0,
 Hypergeometric2F1Regularized(a_, b_, c_, z_) := Hypergeometric2F1(a,b,c,z)/Gamma(c),
  
 InverseGudermannian(z_) := Log(Tan(Pi/4 + z/2)), 
 InverseHaversine(z_) := 2*ArcSin(Sqrt(z)),
 
 
 JacobiP(n_, a_, b_, 1):= Gamma(1+a+n)/(Gamma(1+a)*Gamma(1+n)),
   
 JacobiCD(f_, g_) := JacobiCN(f,g) / JacobiDN(f,g),
 JacobiDC(f_, g_) := JacobiDN(f,g) / JacobiCN(f,g),
 JacobiNC(f_, g_) := 1 / JacobiCN(f,g), 
 JacobiND(f_, g_) := 1 / JacobiDN(f,g), 
 JacobiSC(f_, g_) := JacobiSN(f,g) / JacobiCN(f,g),
 JacobiSD(f_, g_) := JacobiSN(f,g) / JacobiDN(f,g),
 
 LegendreQ(l_, m_, x_) := -((Pi*Csc(m*Pi)*Gamma(1+l+m)*LegendreP(l, -m, x))/(2*Gamma(1+l-m))) + (1/2)*Pi*Cot(m*Pi)*LegendreP(l, m, x),
 
 Log((1 + I*Sqrt(3))/2) = I*Pi/3,
 Log(ProductLog(x_)) = x,
 Log(x_^(a_)) = a*Log(x) 
   /; (x>0 && Element(a, Reals)),
 
 LogisticSigmoid(x_) := 1/(1 + E^(-x)),
 
 LogGamma(x_) := Log(Gamma(x)) 
  /; x>0,

 PolyGamma(1,1/4) = 8*Catalan + Pi^2,
 PolyGamma(1,3/4) = -8*Catalan + Pi^2,
 PolyGamma(2,1) = (-2)*Zeta(3),
 PolyGamma(2,1/2) = -14*Zeta(3),
 PolyGamma(2,1/4) = -2*Pi^3-56*Zeta(3),
 PolyGamma(2,3/4) = 2*Pi^3-56*Zeta(3),
 PolyGamma(2,1/6) = -4*Sqrt(3)*Pi^3 - 182*Zeta(3),
 PolyGamma(2,5/6) = 4*Sqrt(3)*Pi^3 - 182*Zeta(3),
 
 PolyGamma(-2,1/2) = 1/24*5*Log(2)+1/2*3*Log(Glaisher)+Log(Pi)/4,
 PolyGamma(-2,1/4) = Catalan/(4*Pi)+1/8*9*Log(Glaisher)+1/8*(Log(2)+Log(Pi)),
 PolyGamma(-2,1) = (1/2)*(Log(2) + Log(Pi)),
 PolyGamma(-2,2) = -1+Log(2)+Log(Pi),
 PolyGamma(-3,1/2) = Log(Glaisher)/2+1/16*(Log(2)+Log(Pi))+(7*Zeta(3))/(32*Pi^2),
 PolyGamma(-3,1) = Log(Glaisher)+1/4*(Log(2)+Log(Pi)),
 PolyGamma(-3,2) = (-3/4)+Log(2)+2*Log(Glaisher)+Log(Pi),
 
 PolyGamma(n_Integer, 1/2) := (-1)^(n + 1)*n!*(2^(n + 1) - 1)*Zeta(n+1)  
  /; n>0,
  
 PolyLog(2,(3-Sqrt(5))/2) = Pi^2/15 - ArcCsch(2)^2,
 PolyLog(2,(-1+Sqrt(5))/2) = Pi^2/10 - ArcCsch(2)^2,
 PolyLog(2,(1-Sqrt(5))/2) = -(Pi^2/10) + ArcCsch(2)^2 + (1/2)*(Pi^2/15 - ArcCsch(2)^2) ,
 PolyLog(2,(-1-Sqrt(5))/2) = -(Pi^2/10) - ArcCsch(2)^2,
 
 Power(Abs(x_),y_Integer) := x^y
  /; EvenQ(y) && Element(x,Reals),
 Power(I,x_) := E^Distribute(1/2*I*Pi*x),
  
 ProductLog(x_*Log(x_)) := Log(x) 
  /; x > 1/E,
 E^ProductLog(x_) := x/ProductLog(x),
  
 Sin(Pi/60) = -(-1/8*Sqrt(3)*(-1+Sqrt(5))-Sqrt(1/2*(5+Sqrt(5)))/4)/Sqrt(2)+(1/8*(-1+Sqrt(5))-Sqrt(3/2*(5+Sqrt(5)))/4)/Sqrt(2),
 Sin(n_Integer*ArcSin(z_)) := z*ChebyshevU(n - 1, Sqrt(1 - z^2))
  /; n > 0,
 Sin(n_Integer*ArcTan(z_)) := Sum((-1)^k*Binomial(n, 2*k + 1)*z^(2*k + 1), {k, 0, Floor((n-1)/2)}) / (1 + z^2)^(n/2) 
   /; n > 0,

 Sinc(z_) := Sin(z) / z 
  /; z!=0,
 SinIntegral(Sqrt(z_^n_)) := (Sqrt(z^n)*SinIntegral(z^(n/2)))/z^(n/2), 
 SinhIntegral(Sqrt(z_^n_)) := (Sqrt(z^n)*SinhIntegral(z^(n/2)))/z^(n/2),
  
 SphericalHarmonicY(l_, 0, t_,p_) := (Sqrt(1+2*l)*LegendreP(l,Cos(t)))/(2*Sqrt(Pi)),
 SphericalHarmonicY(l_,1,t_,p_) := (-E^(I*p)*l*(1+l)*Sqrt(1+2*l)*Sqrt(Gamma(l))*Hypergeometric2F1(1-l,2+l,2,Sin(t/2)^2)*Sin(t))/(4*Sqrt(Pi)*Sqrt(Gamma(2+l))),
 SphericalHarmonicY(l_,m_,t_,p_) := (E^(I*m*p)*Sqrt(1+2*l)*Sqrt(Gamma(1+l-m))*Hypergeometric2F1(-l,1+l,1-m,Sin(t/2)^2)*Sin(t)^m)/((1-Cos(t))^m*2*Sqrt(Pi)*Gamma(1-m)*Sqrt(Gamma(1+l+m))),

 WeberE(a_,b_) := (2*b*Cos((a*Pi)/2)^2*HypergeometricPFQ({1},{3/2-a/2,3/2+a/2},-(b^2/4)))/((-1+a)*(1+a)*Pi)+(2*HypergeometricPFQ({1},{1-a/2,1+a/2},-(b^2/4))*Sin((a*Pi)/2)^2)/(a*Pi),
 WeberE(a_,b_,c_) := -((c*Cos((a*Pi)/2)*Gamma(2+b)*HypergeometricPFQ({1+b/2,3/2+b/2},{3/2,3/2-a/2+b/2,3/2+a/2+b/2},-(c^2/4)))/(2*Gamma(3/2-a/2+b/2)*Gamma(3/2+a/2+b/2)))+(Gamma(1+b)*HypergeometricPFQ({1/2+b/2,1+b/2},{1/2,1-a/2+b/2,1+a/2+b/2},-(c^2/4))*Sin((a*Pi)/2))/(Gamma(1-a/2+b/2)*Gamma(1+a/2+b/2)), 
 
 WhittakerM(k_, m_, z_) := (z^(1/2+m) * Hypergeometric1F1(1/2-k+m, 1+ 2*m, z))/E^(z/2),

 WhittakerW(k_, m_, z_) := (z^(1/2+m) * HypergeometricU(1/2-k+m, 1+2*m, z))/E^(z/2),

 Zeta(n_Integer, x_) := 1/((-1)^n*(n-1)!) * PolyGamma(n-1,x)
   /; EvenQ(n) && n>1,
 Derivative(1)[Zeta][m_Integer] := With({n=m/(-2)},  1/2 * (-1)^n * (2*n)!/(2*Pi)^(2*n)*Zeta(2*n+1)
   /; EvenQ(m) && m<(-1)),
 Derivative(1)[Zeta][2] := 1/6*Pi^2*(EulerGamma+Log(2)-12*Log(Glaisher)+Log(Pi))
}