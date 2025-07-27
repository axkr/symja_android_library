{
{
  SeriesCoefficient(Fibonacci(x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{-((((-I)*Pi - ArcCsch(2))^n + (I*Pi - ArcCsch(2))^n-2*ArcCsch(2)^n)/(2*Sqrt(5)*n!)), n >= 1}}, 0)
    /; FreeQ(n,x), 
    
  SeriesCoefficient(HarmonicNumber(x_),{x_Symbol, a_, n_?NotListQ}) := Piecewise({{HarmonicNumber(a), n == 0}, {(-1)^(1 + n)*Zeta(1 + n, 1 + a), n >= 1}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x), 
    
  SeriesCoefficient(BernoulliB(m_, x_),{x_Symbol, a_, n_?NotListQ}) :=  Piecewise({{(BernoulliB(m - n, a)*Pochhammer(1 + m - n, n))/n!, n >= 0 && m >= n}}, 0)
    /; FreeQ({a,m,n},x), 
    
  SeriesCoefficient(x_/(a_^x_ - 1), {x_Symbol, 0, n_?NotListQ})  := Piecewise({{(BernoulliB(n)*Log(a)^(-1+n))/n!, n >= 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x), 
    
  SeriesCoefficient(Cos(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I^n*(1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Cos(x_),{x_Symbol, Pi/2, n_?NotListQ}):=Piecewise({{-((I*I^n*(-1 + (-1)^n))/(2*n!)), n >= 0}}, 0) 
    /; FreeQ(n,x), 
    
  SeriesCoefficient(Sin(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I*I^n*(-1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
   SeriesCoefficient(Sin(x_),{x_Symbol, Pi/2, n_?NotListQ}):=Piecewise({{(I^n*(1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(Tan(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I^(1 + n)*2^n*(-1 + (-1)^n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!, n >= 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Tan(x_),{x_Symbol, Pi/2, n_?NotListQ}):=Piecewise({{-1, n == -1}, {(I^(1 + n)*2^n*(-1 + (-1)^n)*BernoulliB(1 + n))/(1 + n)!, n >= 0}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(Cot(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1, n == -1}, {-((I*(2*I)^n*(-1 + (-1)^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Cot(x_),{x_Symbol, Pi/2, n_?NotListQ}):=Piecewise({{-((I*(2*I)^n*(-1 + (-1)^n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!), n >= 1}}, 0)
    /; FreeQ(n,x),

  SeriesCoefficient(Cosh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1/n!, Mod(n, 2) == 0 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Coth(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1, n == -1}, {(2^(1 + n)*BernoulliB(1 + n))/(1 + n)!, n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sinh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1/n!, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Tanh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(2^(1 + n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!, Mod(n, 2) == 1 && n >= 1}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(ArcCos(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{Pi/2, n == 0}, {-(Pochhammer(1/2, (1/2)*(-1 + n))/(n*((1/2)*(-1 + n))!)), n > 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcCot(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{I^(1 + n)/n, n > 0 && Mod(n, 2) == 1}, {Pi/2, n == 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcSin(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{Pochhammer(1/2, (1/2)*(-1 + n))/(n*((1/2)*(-1 + n))!), Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I*((-I)^n-I^n))/(2*n), n > 0}}, 0)  
    /; FreeQ(n,x),
    
  SeriesCoefficient(ArcCosh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I*Pi)/2, n == 0}, {-((I*Pochhammer(1/2, (1/2)*(-1 + n)))/(n*((1/2)*(-1 + n))!)), n >= 1 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcSinh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I^(-1 + n)*Pochhammer(1/2, (1/2)*(-1 + n)))/(n*((1/2)*(-1 + n))!), Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcTanh(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1/n, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),    
     
     
  SeriesCoefficient(Csc(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1, n == -1}, {-((2*I*I^n*(-1 + 2^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sec(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{(I^n*EulerE(n))/n!, Mod(n, 2) == 0 && n >= 0}}, 0) 
    /; FreeQ(n,x),    
     
  SeriesCoefficient(Csch(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{1, n == -1}, {-((2*(-1 + 2^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sech(x_),{x_Symbol, 0, n_?NotListQ}):=Piecewise({{EulerE(n)/n!, n >= 0}}, 0)
    /; FreeQ(n,x),     
    
  SeriesCoefficient(Cos(x_),{x_Symbol, a_, n_?NotListQ}):=Piecewise({{Cos(a + (n*Pi)/2)/n!, n >= 0}}, 0) 
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(Sin(x_),{x_Symbol, a_, n_?NotListQ}):=Piecewise({{Sin(a + (n*Pi)/2)/n!, n >= 0}}, 0) 
    /; FreeQ(a,x)&&FreeQ(n,x),    
  SeriesCoefficient(Cosh(x_),{x_Symbol, a_, n_?NotListQ}):=Piecewise({{Cosh(a)/n!, Mod(n, 2) == 0 && n >= 0}, {Sinh(a)/n!, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(Sinh(x_),{x_Symbol, a_, n_?NotListQ}):=Piecewise({{Cosh(a)/n!, Mod(n, 2) == 1 && n >= 0}, {Sinh(a)/n!, Mod(n, 2) == 0 && n >= 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),        
    
  SeriesCoefficient(ArcCot(x_),{x_Symbol, a_, n_?NotListQ}) := Piecewise({{(I*((-I - a)^(-n) - (I - a)^(-n)))/(2*n), n > 0}, {(1/2)*I*(Log((-I + a)/a) - Log((I + a)/a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, a_, n_?NotListQ}) := Piecewise({{-((I*((-I - a)^(-n) - (I - a)^(-n)))/(2*n)), n>0}, {ArcTan(a), n==0}}, 0) 
   /; FreeQ(a,x)&&FreeQ(n,x), 
    
  SeriesCoefficient(ArcCoth(x_),{x_Symbol, a_, n_?NotListQ}) := Piecewise({{(-(-1 - a)^(-n) + (1 - a)^(-n))/(2*n), n > 0}, {(1/2)*(Log(1 + 1/a) - Log((-1 + a)/a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(ArcTanh(x_),{x_Symbol, a_, n_?NotListQ}) := Piecewise({{((-1)^n*((-1 + a)^(-n) - (1 + a)^(-n)))/(2*n), n >= 1}, {(1/2)*(-Log(1 - a) + Log(1 + a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),   
   
   
  SeriesCoefficient(ArcCos(x_),{x_Symbol, a_, 1}):= -(1/Sqrt(1 - a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcCot(x_),{x_Symbol, a_, 1}):= -(1/(1 + a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcSin(x_),{x_Symbol, a_, 1}):= 1/Sqrt(1 - a^2)
    /; FreeQ(a,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, a_, 1}):= 1/(1 + a^2)
    /; FreeQ(a,x),
  SeriesCoefficient(ArcCsc(x_),{x_Symbol, a_, 1}):= -(1/(Sqrt(1 - 1/a^2)*a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcSec(x_),{x_Symbol, a_, 1}):= 1/(Sqrt(1-1/a^2)*a^2)
    /; FreeQ(a,x),
          
  SeriesCoefficient(Log(b_.+c_.*x_),{x_Symbol, a_, n_?NotListQ}):=
    If(c===1,Piecewise({{(-1)^(1 + n)/((a + b)^n*n), n >= 1}, {Log(a + b), n == 0}}, 0),Piecewise({{-((-(c/(b + a*c)))^n/n), n > 0}, {Log(b + a*c), n == 0}}, 0))
      /; FreeQ({a,b,c,n},x),
    
  SeriesCoefficient(ProductLog(x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{(-n)^(-1 + n)/n!, n >= 1}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(PolyGamma(x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{-1, n == -1}, {-EulerGamma, n == 0}, {(-1)^(1 + n)*Zeta(1 + n), n >= 1}}, 0) 
    /; FreeQ(n,x),
    
  SeriesCoefficient(PolyLog(k_, x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{n^(-k), n >= 1}}, 0)
    /; FreeQ(k,x) && FreeQ(n,x), 
    
  SeriesCoefficient(ChebyshevT(k_, x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{((-(1/2))^n*Sqrt(Pi)*Gamma(1/2 + n)*Pochhammer(-k, n)*Pochhammer(k, n))/(n!*Gamma((1/2)*(1 - k + n))*Gamma((1/2)*(1 + k + n))*Pochhammer(1/2, n)), n >= 0}}, 0)
    /; FreeQ(k,x) && FreeQ(n,x),  
    
  SeriesCoefficient(ChebyshevU(k_, x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{((-(1/2))^n*Sqrt(Pi)*Gamma(3/2 + n)*Pochhammer(-k, n)*Pochhammer(2 + k, n))/(n!*Gamma((1/2)*(1 - k + n))*Gamma((1/2)*(3 + k + n))*Pochhammer(3/2, n)), n >= 0}}, 0)
    /; FreeQ(k,x) && FreeQ(n,x),
 
  SeriesCoefficient(EllipticE(x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{-((Gamma(-(1/2) + n)*Gamma(1/2 + n))/(4*Gamma(1+n)^2)), n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(EllipticK(x_),{x_Symbol, 0, n_?NotListQ}) := Piecewise({{Gamma(1/2+n)^2/(2*Gamma(1+n)^2), n >= 0}}, 0)
    /; FreeQ(n,x)
}
} 