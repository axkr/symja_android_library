{
  D(Abs(f_),x_NotListQ):=D(f,x)*x/Abs(x) /; Element(x, Reals),
  D(ArcCos(f_),x_NotListQ):=D(f,x)*(-1)*(1-f^2)^(-1/2),
  D(ArcCosh(f_),x_NotListQ):=D(f,x)*(f^2-1)^(-1/2),
  D(ArcCot(f_),x_NotListQ):=D(f,x)*(-1)*(1+f^2)^(-1),
  D(ArcCoth(f_),x_NotListQ):=D(f,x)*(1-f^2)^(-1),
  D(ArcCsc(f_),x_NotListQ):=-D(f,x)*1*f^(-2)*(1-x^(-2))^(-1/2),
  D(ArcCsch(f_),x_NotListQ):=D(f,x)*(-1)*Abs(f)^(-1)*(1+f^2)^(-1/2),
  D(ArcSin(f_),x_NotListQ):=D(f,x)*(1-f^2)^(-1/2),
  D(ArcSinh(f_),x_NotListQ):=D(f,x)*(1+f^2)^(-1/2),
  D(ArcTan(f_),x_NotListQ):=D(f,x)*(1+f^2)^(-1),
  D(ArcTanh(f_),x_NotListQ):=D(f,x)*(1-f^2)^(-1),
  D(ArcSec(f_),x_NotListQ):=D(f,x)*x^(-2)*(1-f^(-2))^(-1/2),
  D(ArcSech(f_),x_NotListQ):=D(f,x)*(-1)*f^(-1)*(1-f^2)^(-1/2),
  D(Ceiling(f_),x_NotListQ):=D(f,x)*Piecewise({{0, f<Ceiling(f)}}, Indeterminate),
  D(Erf(f_),x_NotListQ):=D(f,x)*(2*E^(-f^(2))/Sqrt(Pi)),
  D(Erfc(f_),x_NotListQ):=D(f,x)*(-2*E^(-f^(2))/Sqrt(Pi)),
  D(Erfi(f_),x_NotListQ):=D(f,x)*(2*E^(f^(2))/Sqrt(Pi)),
  D(ExpIntegralEi(f_),x_NotListQ):=D(f,x)*E^f/f,
  D(Floor(f_),x_NotListQ):=D(f,x)*Piecewise({{0, f>Floor(f)}}, Indeterminate),
  D(FractionalPart(f_),x_NotListQ):=D(f,x)*1,
  D(FresnelC(f_),x_NotListQ):=D(f,x)*Cos((Pi*f^2)/2),
  D(FresnelS(f_),x_NotListQ):=D(f,x)*Sin((Pi*f^2)/2),
  D(Gamma(f_),x_NotListQ):=D(f,x)*Gamma(f)*PolyGamma(f),
  D(HarmonicNumber(f_),x_NotListQ):=D(f,x)*(Pi^2)/6 - HarmonicNumber(f, 2),
  D(HeavisideTheta(f_),x_NotListQ):=D(f,x)*DiracDelta(f),
  D(IntegerPart(f_),x_NotListQ):=0,
  D(InverseErf(f_),x_NotListQ):=D(f,x)*(1/2*Sqrt(Pi)*E^(InverseErf(f)^2)),
  D(Log(f_),x_NotListQ):=D(f,x)*f^(-1),
  D(LogisticSigmoid(f_),x_NotListQ):=D(f,x)*LogisticSigmoid(f)*(1-LogisticSigmoid(f)),
  D(PolyGamma(f_),x_NotListQ):=D(f,x)*PolyGamma(1,f),
  D(Cot(f_),x_NotListQ):=D(f,x)*(-1)*Csc(f)^2,
  D(Coth(f_),x_NotListQ):=D(f,x)*(-1)*Sinh(f)^(-2),
  D(Cos(f_),x_NotListQ):=D(f,x)*(-1)*Sin(f),
  D(Cosh(f_),x_NotListQ):=D(f,x)*Sinh(f),
  D(Csc(f_),x_NotListQ):=D(f,x)*(-1)*Cot(f)*Csc(f),
  D(Csch(f_),x_NotListQ):=D(f,x)*(-1)*Coth(f)*Csch(f),
  D(Round(f_),x_NotListQ):=D(f,x)*Piecewise({{0, NotElement(-(1/2)+Re(f), Integers) && NotElement(-(1/2)+Im(f), Integers)}}, Indeterminate),
  D(Sin(f_),x_NotListQ):=D(f,x)*Cos(f),
  D(Sinc(f_),x_NotListQ):=D(f,x)*-(Sin(f)/f^2)+Cos(f)/f,
  D(Sinh(f_),x_NotListQ):=D(f,x)*Cosh(f),
  D(Tan(f_),x_NotListQ):=D(f,x)*Sec(f)^2,
  D(Tanh(f_),x_NotListQ):=D(f,x)*Sech(f)^(2),
  D(Sec(f_),x_NotListQ):=D(f,x)*Sec(f)*Tan(f),
  D(Sech(f_),x_NotListQ):=D(f,x)*(-1)*Tanh(f)*Sech(f),
  D(CosIntegral(f_),x_NotListQ):=D(f,x)*(-1)*Cos(f)/f,
  D(CoshIntegral(f_),x_NotListQ):=D(f,x)*Cosh(f)/f,
  D(SinIntegral(f_),x_NotListQ):=D(f,x)*(-1)*Sinc(f),
  D(SinhIntegral(f_),x_NotListQ):=D(f,x)*Sinh(f)/f,
  
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
   
  D(Cos(x_), {x_, 2}) := -Cos(x),
  D(Cot(x_), {x_, 2}) := 2*Cot(x)*Csc(x)^2,
  D(Sin(x_), {x_, 2}) := -Sin(x),
  D(Tan(x_), {x_, 2}) := 2*Sec(x)^2*Tan(x),
  D(Csc(x_), {x_, 2}) := Csc(x)^3+Csc(x)*Cot(x)^2,
  D(Sec(x_), {x_, 2}) := Sec(x)^3+Sec(x)*Tan(x)^2,
     
  D(x_^a_, {x_, n_IntegerQ}) := Pochhammer(a - n + 1, n)*x^(a - n)
    /; n >= 0 && FreeQ(a,x),
  D(a_^x_, {x_, n_IntegerQ}) := a^x*Log(x)^n
    /; n >= 0 && FreeQ(a,x),
    
  D(ArcCos(x_), {x_, n_IntegerQ}) := KroneckerDelta(n)*ArcCos(x) - 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((1/(2*k - n + 1)!)*(Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k)), {k, 0, n - 1})
    /; n >= 0,
  D(ArcCot(x_), {x_, n_IntegerQ}) := KroneckerDelta(n)*ArcCot(x) - 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; n >= 0,
  D(ArcSin(x_), {x_, n_IntegerQ}) := KroneckerDelta(n)*ArcSin(x) + 
    ((-1)^(n - 1)/(1 - x^2)^(n - 1/2))*Sum((Pochhammer(1 - n, k)*Pochhammer(1/2, k)*2^(2*k + 1 - n)*x^(2*k + 1 - n)*(x^2 - 1)^(n - 1 - k))/(2*k - n + 1)!, {k, 0, n - 1}) 
    /; n >= 0,
  D(ArcTan(x_), {x_, n_IntegerQ}) := KroneckerDelta(n)*ArcTan(x) + 
    Sum((((-1)^k*k! * Pochhammer(2*k - n + 2, 2*(n - k) - 2))/ ((n - k - 1)! * (2*x)^(n - 2*k - 1)))*(1 + x^2)^(-k - 1), {k, 0, n - 1})
    /; n >= 0,
      
  D(Cos(x_), {x_, n_IntegerQ}) := Cos(x + (Pi*n)/2) 
    /; n >= 0,
  D(Cot(x_), {x_, n_IntegerQ}) := Cot(x)*KroneckerDelta(n) - Csc(x)^2*KroneckerDelta(n - 1) - 
    n*Sum((((-1)^j*Binomial(n - 1, k))/(k + 1))*Sin(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1}) 
      /; n >= 0,
  D(Sin(x_), {x_, n_IntegerQ}) := Sin(x + (Pi*n)/2) 
    /; n >= 0, 
  D(Tan(x_), {x_, n_IntegerQ}) := Tan(x)*KroneckerDelta(n) + Sec(x)^2* KroneckerDelta(n - 1) + 
    n*Sum((((-1)^k*Binomial(n - 1, k))/(k + 1))*Cos(x)^(-2*k - 2)*2^(n - 2*k)*Binomial(2*k, j)*(k - j)^(n - 1)*Sin((n*Pi)/2 + 2*(k - j)*x), {k, 0, n - 1}, {j, 0, k - 1})
      /; n >= 0,
  
  D(Log(x_), {x_, n_IntegerQ}) := ((-1)^(n - 1)*(n - 1)!)/x^n
    /; n >= 0,
    
  
  D(BesselJ(f_, g_),x_NotListQ):= 1/2*(BesselJ(-1+f, g)-BesselJ(1+f, g))*D(g,x)+D(f,x)*Derivative(1,0)[BesselJ][f,g],
  D(ProductLog(f_, g_),x_NotListQ):= ProductLog(f,g)*D(g,x)/(g*(1+ProductLog(f,g)))+D(f,x)*Derivative(1,0)[ProductLog][f,g] 
}