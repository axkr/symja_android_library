{
  D(Abs(f_),x_?NotListQ):=D(f,x)*f/Abs(f) /; Element(f, Reals),
    
  D(ExpIntegralE(g_, f_),x_?NotListQ):= -1*ExpIntegralE(-1+g,f)*D(f,x)
    /; FreeQ({g},x),

  D(JacobiCD(f_, g_),x_?NotListQ):= (-1 + g)*JacobiND(f,g)*JacobiSD(f,g)*D(f,x) + ((f*(-1 + g) + JacobiEpsilon(f,g))*JacobiND(f,g)*JacobiSD(f,g)*D(g,x))/(2*g), 
  D(JacobiCN(f_, g_),x_?NotListQ):= (-JacobiDN(f,g))*JacobiSN(f,g)*D(f,x) + (JacobiDN(f,g)*JacobiSN(f,g)*(f*(-1 + g) + JacobiEpsilon(f,g) - g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiCS(f_, g_),x_?NotListQ):= (-JacobiDS(f,g))*JacobiNS(f,g)*D(f,x) - (JacobiDS(f,g)*JacobiNS(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiDC(f_, g_),x_?NotListQ):= (1 - g)*JacobiNC(f,g)*JacobiSC(f,g)*D(f,x) + ((f*(1 - g) - JacobiEpsilon(f,g))*JacobiNC(f,g)*JacobiSC(f,g)*D(g,x))/(2*g),
  D(JacobiDN(f_, g_),x_?NotListQ):= (-g)*JacobiCN(f,g)*JacobiSN(f,g)*D(f,x) + (JacobiCN(f,g)*(f*(-1 + g) + JacobiEpsilon(f,g) - JacobiDN(f,g)*JacobiSC(f,g))*JacobiSN(f,g)*D(g,x))/(2*(1 - g)),
  D(JacobiDS(f_, g_),x_?NotListQ):= (-JacobiCS(f,g))*JacobiNS(f,g)*D(f,x) - (JacobiCS(f,g)*JacobiNS(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiDN(f,g)*JacobiSC(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiNC(f_, g_),x_?NotListQ):= JacobiDC(f,g)*JacobiSC(f,g)*D(f,x) + (JacobiDC(f,g)*JacobiSC(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiND(f_, g_),x_?NotListQ):= g*JacobiCD(f,g)*JacobiSD(f,g)*D(f,x) + (JacobiCD(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + JacobiDN(f,g)*JacobiSC(f,g))*JacobiSD(f,g)*D(g,x))/(2*(1 - g)),
  D(JacobiNS(f_, g_),x_?NotListQ):= (-JacobiCS(f,g))*JacobiDS(f,g)*D(f,x) - (JacobiCS(f,g)*JacobiDS(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiSC(f_, g_),x_?NotListQ):= JacobiDC(f,g)*JacobiNC(f,g)*D(f,x) + (JacobiDC(f,g)*JacobiNC(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiSD(f_, g_),x_?NotListQ):= JacobiCD(f,g)*JacobiND(f,g)*D(f,x) + (JacobiCD(f,g)*JacobiND(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiDN(f,g)*JacobiSC(f,g))*D(g,x))/(2*(1 - g)*g),
  D(JacobiSN(f_, g_),x_?NotListQ):= JacobiCN(f,g)*JacobiDN(f,g)*D(f,x) + (JacobiCN(f,g)*JacobiDN(f,g)*(f*(1 - g) - JacobiEpsilon(f,g) + g*JacobiCD(f,g)*JacobiSN(f,g))*D(g,x))/(2*(1 - g)*g),
    
  D(HurwitzZeta(f_,g_),x_?NotListQ):=(-f)*HurwitzZeta(1 + f, g)*D(g,x)
    /; FreeQ({f},x),
  D(Zeta(f_,g_),x_?NotListQ):=(-f)*Zeta(1+f, g)*D(g,x)
    /; FreeQ({f},x),
 
  D(WhittakerM(f_, g_, h_),x_?NotListQ) :=((1/2-f/h)*WhittakerM(f, g, h) + ((1/2+f+g)*WhittakerM(1 + f, g, h))/h)*D(h,x)
    /; FreeQ({f,g},x),
  D(WhittakerW(f_, g_, h_),x_?NotListQ) :=((1/2-f/h)*WhittakerW(f, g, h) - WhittakerW(1+f,g,h)/h)*D(h,x) 
    /; FreeQ({f,g},x),   
    
  D(InverseFunction(f_)[x_],x_) := 1/Derivative(1)[f][InverseFunction(f)[x]] 
    /; FreeQ(f,x),
  
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
  
  D(x_^a_, {x_, n_}) := If(IntegerQ(n), Pochhammer(a - n + 1, n)*x^(a - n), FactorialPower(a, n)*x^(a - n))
    /; ((IntegerQ(n) && n >= 0)||SymbolQ(n)) && FreeQ(a,x),
  D(a_^x_, {x_, n_}) := a^x*Log(a)^n
    /; ((IntegerQ(n) && n >= 0)||SymbolQ(n)) && FreeQ(a,x),
    
  D(ArcCos(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcCos(x) - 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((1/(2*k - n + 1)!)*(Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k)), {k, 0, n - 1})
    /; n >= 0, 
  D(ArcCot(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcCot(x) - 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; n >= 0, 
  D(ArcSin(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcSin(x) + 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k))/(2*k - n + 1)!, {k, 0, n - 1}) 
    /; n >= 0, 
  D(ArcTan(x_), {x_, n_Integer}) := KroneckerDelta(n)*ArcTan(x) + 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; n >= 0,
  D(ArcTan(x_), {x_, n_Symbol}) := 2^(n - 1)*Sqrt(Pi)*x^(1 - n)*HypergeometricPFQRegularized({1/2, 1, 1}, {1 - n/2, (3 - n)/2}, -x^2),

  D(Cot(x_), {x_, n_}) := Cot(x)*KroneckerDelta(n) - Csc(x)^2*KroneckerDelta(n - 1) - 
    n*Sum((((-1)^j*Binomial(n - 1, k))/(k + 1))*Sin(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1}) 
      /; SymbolQ(n), 
  D(Tan(x_), {x_, n_}) := Tan(x)*KroneckerDelta(n) + Sec(x)^2* KroneckerDelta(n - 1) + 
    n*Sum((((-1)^k*Binomial(n - 1, k))/(k + 1))*Cos(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1})
      /; SymbolQ(n), 
      
  D(Log(x_), {x_, n_Integer}) := ((-1)^(n - 1)*(n - 1)!)/x^n
    /; n >= 1, 
  D(Log(x_), {x_, n_Symbol}) := Piecewise({{((-1)^(n - 1)*(n - 1)!)/x^n, n >= 1}}, Log(x)), 

  D(HarmonicNumber(x_), {x_, n_Integer}) := (-1)^n*x^(-1 - n)*n! + EulerGamma*KroneckerDelta(n) + PolyGamma(n, x)
    /; n >= 1,
  D(HarmonicNumber(x_), {x_, n_Symbol}) := Piecewise({{(-1)^n*x^(-1 - n)*n! + EulerGamma*KroneckerDelta(n) + PolyGamma(n, x), n >= 1}}, HarmonicNumber(x)),
     
  D(ArcTan(f_, g_),x_?NotListQ):= With({d=((-g*D(f,x)+f*D(g,x))/(f^2 + g^2))},If(PossibleZeroQ(d),0,d)),
  D(JacobiAmplitude(f_, g_),x_?NotListQ) := JacobiDN(f, g)*D(f,x)+(((EllipticE(JacobiAmplitude(f, g), g) + f*(-1 + g))*JacobiDN(f, g) - g*JacobiCN(f, g)*JacobiSN(f, g))*D(g,x))/(2*(-1 + g)*g),
  
  D(AppellF1(a_,b_,c_,d_,f_,g_),x_?NotListQ) :=  (a*b*AppellF1(1+a,1+b,c,1+d,f,g)*D(f,x))/d+(a*c*AppellF1(1+a,b,1+c,1+d,f,g)*D(g,x))/d
    /; FreeQ({a,b,c,d},x),
    
  D(If(c_, f_, g_),x_?NotListQ) := With({u=D(f,x), v=D(g,x)}, If(u===v, u, If(c, u, v))),
  D(If(c_, f_),x_?NotListQ) := With({u=D(f,x)}, If(c, u)),

  D(InverseZTransform(f_,g_,h_),x_?NotListQ) := DiscreteDelta(h)*D(f,x)
    /; FreeQ({f},g)
}
