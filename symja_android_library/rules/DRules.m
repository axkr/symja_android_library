{
  D(Integrate(f_, x_),x_?NotListQ):=f,
  D(Abs(f_),x_?NotListQ):=D(f,x)*x/Abs(x) /; Element(x, Reals),
    
  D(ExpIntegralE(g_, f_),x_?NotListQ):= -1*ExpIntegralE(-1+g,f)*D(f,x)
    /; FreeQ({g},x),

  D(JacobiAmplitude(g_, f_),x_?NotListQ):= JacobiDN(f,g)*D(f,x) + ((JacobiDN(f,g)*(f*(-1 + g) + JacobiEpsilon(f,g)) - g*JacobiCN(f,g)*JacobiSN(f,g))*D(g,x))/(2*(-1 + g)*g),
  
  D(JacobiCD(g_, f_),x_?NotListQ):= (-1 + g)*JacobiND(f,g)*JacobiSD(f,g)*D(f,x) + ((f*(-1 + g) + JacobiEpsilon(f,g))*JacobiND(f,g)*JacobiSD(f,g)*D(g,x))/(2*g), 
  D(JacobiCN(g_, f_),x_?NotListQ):= (-JacobiDN(f,g))*JacobiSN(f,g)*D(f,x) + (JacobiDN(f,g)*JacobiSN(f,g)*(f*(-1 + g) + JacobiEpsilon(f,g) - g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiDC(g_, f_),x_?NotListQ):= (1 - g)*JacobiNC(f,g)*JacobiSC(f,g)*D(f,x) + ((f*(1 - g) - JacobiEpsilon(f,g))*JacobiNC(f,g)*JacobiSC(f,g)*D(g,x))/(2*g),
  D(JacobiDN(g_, f_),x_?NotListQ):= (-g)*JacobiCN(f,g)*JacobiSN(f,g)*D(f,x) + (JacobiCN(f,g)*(f*(-1 + g) + JacobiEpsilon(f,g) - JacobiDN(f,g)*JacobiSC(f,g))*JacobiSN(f,g)*D(g,x))/(2*(1 - g)),
  D(JacobiNC(g_, f_),x_?NotListQ):= JacobiDC(f,g)*JacobiSC(f,g)*D(f,x) + (JacobiDC(f,g)*JacobiSC(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiND(g_, f_),x_?NotListQ):= g*JacobiCD(f,g)*JacobiSD(f,g)*D(f,x) + (JacobiCD(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + JacobiDN(f,g)*JacobiSC(f,g))*JacobiSD(f,g)*D(g,x))/(2*(1 - g)),
  D(JacobiSC(g_, f_),x_?NotListQ):= JacobiDC(f,g)*JacobiNC(f,g)*D(f,x) + (JacobiDC(f,g)*JacobiNC(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiSD(g_, f_),x_?NotListQ):= JacobiCD(f,g)*JacobiND(f,g)*D(f,x) + (JacobiCD(f,g)*JacobiND(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiDN(f,g)*JacobiSC(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiSN(g_, f_),x_?NotListQ):= JacobiCN(f,g)*JacobiDN(f,g)*D(f,x) + (JacobiCN(f,g)*JacobiDN(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
    
  D(Erf(g_, f_),x_?NotListQ):= (2*D(f,x))/(E^f^2*Sqrt(Pi))+(-2*D(g,x))/(E^g^2*Sqrt(Pi)),
  D(InverseErf(g_, f_),x_?NotListQ):= 1/2*E^InverseErf(g,f)^2*Sqrt(Pi)*D(f,x)+D(g,x)/E^(g^2-InverseErf(g,f)^2),
    
  D(BernoulliB(g_, f_),x_?NotListQ):= BernoulliB(-1+g, f)*g*D(f,x)
    /; FreeQ({g},x), 
  D(ChebyshevT(g_, f_),x_?NotListQ):= ChebyshevU(-1+g, f)*g*D(f,x)
    /; FreeQ({g},x),
  D(ChebyshevU(g_, f_),x_?NotListQ):= ((ChebyshevU(-1+g, f)*(-1-g) + ChebyshevU(g, f)*f*g)*D(f,x))/(-1+f^2)
    /; FreeQ({g},x), 
  D(GegenbauerC(g_, f_),x_?NotListQ):= 2*ChebyshevU(-1+g, f)*D(f,x)
    /; FreeQ({g},x),
  D(GegenbauerC(g_,h_,f_),x_?NotListQ):= 2*GegenbauerC(-1+g,1+h,f)*h*D(f,x)
    /; FreeQ({g,h},x),
    
  D(LaguerreL(g_, f_),x_?NotListQ):= -1*LaguerreL(-1+g,1,f)*D(f,x)
    /; FreeQ({g},x),
  D(LaguerreL(g_, h_, f_),x_?NotListQ):= -1*LaguerreL(-1+g,1+h,f)*c
    /; FreeQ({g,h},x), 
  D(LegendreP(g_, f_),x_?NotListQ):= ((f*(-1-g)*LegendreP(g,f)+(1+g)*LegendreP(1+g,f))*D(f,x))/(-1 + f^2)
    /; FreeQ({g},x),
  D(LegendreP(g_, h_, f_),x_?NotListQ):= ((f*(-1-g)*LegendreP(g, h, f)+(1+g-h)*LegendreP(1+g,h,f))*D(f,x))/(-1+f^2)
    /; FreeQ({g,h},x),   
  D(LegendreQ(g_, f_),x_?NotListQ):= ((f*(-1-g)*LegendreQ(g,f)+(1+g)*LegendreQ(1+g,f))*D(f,x))/(-1+f^2)
    /; FreeQ({g},x),
  D(LegendreQ(g_, h_, f_),x_?NotListQ):= ((f*(-1-g)*LegendreQ(g,h,f)+(1+g-h)*LegendreQ(1+g,h,f))*D(f,x))/(-1+f^2)
    /; FreeQ({g,h},x),
    
  D(PolyGamma(0, x_), {x_, n_}) := PolyGamma(n, x)
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n), 
  D(PolyGamma(g_, f_),x_?NotListQ):=  PolyGamma(1+g, f)*D(f,x)
    /; FreeQ({g},x),
  D(HurwitzZeta(f_,g_),x_?NotListQ):=(-f)*HurwitzZeta(1 + f, g)*D(g,x)
    /; FreeQ({f},x),
  D(Zeta(f_,g_),x_?NotListQ):=(-f)*Zeta(1+f, g)*D(g,x)
    /; FreeQ({f},x),
 
 D(Hypergeometric0F1(a_, f_), x_?NotListQ) := (Hypergeometric0F1(1+a,f)*D(f,x))/a
     /; FreeQ(a,x),
 D(Hypergeometric1F1(a_, b_, f_), x_?NotListQ) := (a*Hypergeometric1F1(1+a,1+b,f)*D(f,x))/b
    /; FreeQ({a,b},x),
  D(Hypergeometric2F1(a_, b_, c_, f_), x_?NotListQ) := (a*b*Hypergeometric2F1(1 + a, 1 + b, 1 + c, f)*D(f,x))/c
    /; FreeQ({a,b,c},x),
  D(Hypergeometric2F1(a_, b_, c_, x_), {x_,n_}) := Hypergeometric2F1(a + n, b + n, c + n, x)*(Pochhammer(a, n)*Pochhammer(b, n))/Pochhammer(c, n)
    /; FreeQ({a,b,c,n},x) && Negative(n)=!=True,
  D(Hypergeometric2F1Regularized(a_, b_, c_, f_), x_?NotListQ) := a*b*Hypergeometric2F1Regularized(1+a,1+b,1+c,f)*D(f,x)
    /; FreeQ({a,b,c},x),
  D(HypergeometricU(f_, g_, h_),x_?NotListQ) :=-f*HypergeometricU(1+f,1+g,h)*D(h,x)
    /; FreeQ({f,g},x),
  D(WhittakerM(f_, g_, h_),x_?NotListQ) :=((1/2-f/h)*WhittakerM(f, g, h) + ((1/2+f+g)*WhittakerM(1 + f, g, h))/h)*D(h,x)
    /; FreeQ({f,g},x),
  D(WhittakerW(f_, g_, h_),x_?NotListQ) :=((1/2-f/h)*WhittakerW(f, g, h) - WhittakerW(1+f,g,h)/h)*D(h,x) 
    /; FreeQ({f,g},x),   
  D(E^(y_)*(x_)^m_,{x_,n_}) := E^x*x^(m-n)*Binomial(m,n)*n!*Hypergeometric1F1(-n,1+m-n,-x)
    /; FreeQ({m,n},x) && Negative(n)=!=True && y==x,
  D(E^(y_)*(x_)^m_,{x_,n_}) := (x^(m-n)*Binomial(m,n)*n!*Hypergeometric1F1(-n,1+m-n,x))/E^x
    /; FreeQ({m,n},x) && Negative(n)=!=True && (-y)==x,
    
  D(InverseFunction(f_)[x_],x_) := 1/Derivative(1)[f][InverseFunction(f)[x]] 
    /; FreeQ(f,x),
  
  D(ArcCos(x_), {x_, 2}) := -(x/(1 - x^2)^(3/2)),
  D(ArcCot(x_), {x_, 2}) := (2*x)/(1 + x^2)^2,
  D(ArcSin(x_), {x_, 2}) := x/(1 - x^2)^(3/2),
  D(ArcTan(x_), {x_, 2}) := -((2*x)/(1 + x^2)^2),
 
  D(ArcCosh(x_), {x_, 2}) :=  -(x/((-1 + x)^(3/2)*(1 + x)^(3/2))),
  D(ArcCoth(x_), {x_, 2}) :=  (2*x)/(1 - x^2)^2,
  D(ArcSinh(x_), {x_, 2}) := -(x/(1 + x^2)^(3/2)),
  D(ArcTanh(x_), {x_, 2}) := (2*x)/(1 - x^2)^2,
  D(ArcCsc(x_), {x_, 2}) :=  (-1+2*x^2)/(Sqrt(1 - 1/x^2)*x^3*(-1+x^2)),
  D(ArcSec(x_), {x_, 2}) := (1-2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2)),
    
  D(Cot(x_), {x_, 2}) := 2*Cot(x)*Csc(x)^2,
  D(Tan(x_), {x_, 2}) := 2*Sec(x)^2*Tan(x),
  D(Csc(x_), {x_, 2}) := Csc(x)^3+Csc(x)*Cot(x)^2,
  D(Sec(x_), {x_, 2}) := Sec(x)^3+Sec(x)*Tan(x)^2,
  
  D(Cos(x_), {x_, n_}) := Cos((n*Pi)/2 + x)
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n),
  D(Sin(x_), {x_, n_}) := Sin((n*Pi)/2+x)
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n), 
    
  D(Cosh(x_), {x_, n_}) := (-I)^n*Cos((n*Pi)/2-I*x)
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n),
  D(Sinh(x_), {x_, n_}) := I*(-I)^n*Sin((n*Pi)/2-I*x) 
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n),
    
  D(x_^a_, {x_, n_}) := If(IntegerQ(n), Pochhammer(a - n + 1, n)*x^(a - n), FactorialPower(a, n)*x^(a - n))
    /; ((IntegerQ(n) && n >= 0)||SymbolQ(n)) && FreeQ(a,x),
  D(a_^x_, {x_, n_Integer}) := a^x*Log(a)^n
    /; ((IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ)) && FreeQ(a,x),
    
  D(ArcCos(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcCos(x) - 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((1/(2*k - n + 1)!)*(Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k)), {k, 0, n - 1})
    /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 
  D(ArcCot(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcCot(x) - 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 
  D(ArcSin(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcSin(x) + 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k))/(2*k - n + 1)!, {k, 0, n - 1}) 
    /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 
  D(ArcTan(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcTan(x) + 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 

  D(Cot(x_), {x_, n_Integer}) := Cot(x)*KroneckerDelta(n) - Csc(x)^2*KroneckerDelta(n - 1) - 
    n*Sum((((-1)^j*Binomial(n - 1, k))/(k + 1))*Sin(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1}) 
      /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 
 
  D(Tan(x_), {x_, n_Integer}) := Tan(x)*KroneckerDelta(n) + Sec(x)^2* KroneckerDelta(n - 1) + 
    n*Sum((((-1)^k*Binomial(n - 1, k))/(k + 1))*Cos(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1})
      /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 
  D(Log(x_), {x_, n_Integer}) := ((-1)^(n - 1)*(n - 1)!)/x^n
    /; (IntegerQ(n) && n >= 0)||FreeQ(n,_?NumberQ), 

  D(HarmonicNumber(x_), {x_, n_Integer}) := (-1)^n*x^(-1 - n)*n! + EulerGamma*KroneckerDelta(n) + PolyGamma(n, x)  
    /; (IntegerQ(n) && n >= 1)||FreeQ(n,_?NumberQ),
     
  D(ArcTan(f_, g_),x_?NotListQ):= With({d=((-g*D(f,x)+f*D(g,x))/(f^2 + g^2))},If(PossibleZeroQ(d),0,d)),
  D(BesselJ(f_, g_),x_?NotListQ):= 1/2*(BesselJ(-1+f, g)-BesselJ(1+f, g))*D(g,x)+D(f,x)*Derivative(1,0)[BesselJ][f,g],
  D(BesselY(f_, g_),x_?NotListQ):= 1/2*(BesselY(-1+f, g)-BesselY(1+f, g))*D(g,x)+D(f,x)*Derivative(1,0)[BesselY][f,g],
  
  D(CarlsonRC(f_, g_),x_?NotListQ) := 
    Piecewise({{(-CarlsonRC(f,g) + 1/Sqrt(f))/(2*(f-g)), f!=g}, {-(1/(6*f^(3/2))), f==g && (Im(g)!=0 || Re(g)>0)}}, ComplexInfinity)*D(f,x) + 
    Piecewise({{(CarlsonRC(f,g) - Sqrt(f)/g)/(2*(f-g)), f!=g}, {-(1/(3*f^(3/2))), f==g && (Im(g)!=0 || Re(g)>0)}}, ComplexInfinity)*D(g,x),
  D(CarlsonRD(f_, g_, h_),x_?NotListQ):= (-(1/6))*CarlsonRD(g,h,f)*D(f,x) - (1/6)*CarlsonRD(f,h,g)*D(g,x) - (1/6)*CarlsonRD(f,g,h)*D(h,x),
  D(CarlsonRF(f_, g_, h_),x_?NotListQ):= (-(1/6))*CarlsonRD(g,h,f)*D(f,x) - (1/6)*CarlsonRD(f,h,g)*D(g,x) - (1/6)*CarlsonRD(f,g,h)*D(h,x),
  D(CarlsonRG(f_, g_, h_),x_?NotListQ):= (1/12)*(3*CarlsonRF(f,g,h) - CarlsonRD(g,h,f)*f)*D(f,x) + (1/12)*(3*CarlsonRF(f,g,h) - CarlsonRD(f,h,g)*g)*D(g,x) + (1/12)*(3*CarlsonRF(f,g,h) - CarlsonRD(f,g,h)*h)*D(h,x), 
   
  D(PolyLog(f_, g_),x_?NotListQ) := (PolyLog(-1 + f, g)*D(g,x))/g + D(f,x)*Derivative(1, 0)[PolyLog][f, g],
  D(PolyLog(f_, g_, h_),x_?NotListQ) := (PolyLog(-1+f,g,h)*D(h,x))/h + D(g,x)*Derivative(0,1,0)[PolyLog][f,g,h] + D(f,x)*Derivative(1,0,0)[PolyLog][f,g,h],
  
  D(ProductLog(f_),x_?NotListQ) := (ProductLog(f)*D(f, x))/(f*(1 + ProductLog(f))),
  D(ProductLog(f_, g_),x_?NotListQ):= ProductLog(f,g)*D(g,x)/(g*(1+ProductLog(f,g)))+D(f,x)*Derivative(1,0)[ProductLog][f,g],
  
  D(JacobiAmplitude(f_, g_),x_?NotListQ) := JacobiDN(f, g)*D(f,x)+(((EllipticE(JacobiAmplitude(f, g), g) + f*(-1 + g))*JacobiDN(f, g) - g*JacobiCN(f, g)*JacobiSN(f, g))*D(g,x))/(2*(-1 + g)*g),
  
  D(StruveH(f_, g_),x_?NotListQ):=(1/2)*(g^f /(2^f *(Sqrt(Pi)*Gamma(3/2+f))) + StruveH(-1 + f, g)-StruveH(1 + f, g))*D(g,x) + D(f,x)*Derivative(1, 0)[StruveH][f, g],
  D(StruveL(f_, g_),x_?NotListQ):=(1/2)*(g^f /(2^f *(Sqrt(Pi)*Gamma(3/2+f))) + StruveL(-1 + f, g)+StruveL(1 + f, g))*D(g,x) + D(f,x)*Derivative(1, 0)[StruveL][f, g],
  
  D(AppellF1(a_,b_,c_,d_,f_,g_),x_?NotListQ) :=  (a*b*AppellF1(1+a,1+b,c,1+d,f,g)*D(f,x))/d+(a*c*AppellF1(1+a,b,1+c,1+d,f,g)*D(g,x))/d
    /; FreeQ({a,b,c,d},x) 
  
}