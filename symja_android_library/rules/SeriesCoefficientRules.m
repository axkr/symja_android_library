{
  SeriesCoefficient(Cos(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I^n*(1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Cos(x_),{x_Symbol, Pi/2, n_NotListQ}):=Piecewise({{-((I*I^n*(-1 + (-1)^n))/(2*n!)), n >= 0}}, 0) 
    /; FreeQ(n,x), 
    
  SeriesCoefficient(Sin(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I*I^n*(-1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
   SeriesCoefficient(Sin(x_),{x_Symbol, Pi/2, n_NotListQ}):=Piecewise({{(I^n*(1 + (-1)^n))/(2*n!), n >= 0}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(Tan(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I^(1 + n)*2^n*(-1 + (-1)^n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!, n >= 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Tan(x_),{x_Symbol, Pi/2, n_NotListQ}):=Piecewise({{-1, n == -1}, {(I^(1 + n)*2^n*(-1 + (-1)^n)*BernoulliB(1 + n))/(1 + n)!, n >= 0}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(Cot(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1, n == -1}, {-((I*(2*I)^n*(-1 + (-1)^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Cot(x_),{x_Symbol, Pi/2, n_NotListQ}):=Piecewise({{-((I*(2*I)^n*(-1 + (-1)^n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!), n >= 1}}, 0)
    /; FreeQ(n,x),

  SeriesCoefficient(Cosh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1/n!, Mod(n, 2) == 0 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Coth(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1, n == -1}, {(2^(1 + n)*BernoulliB(1 + n))/(1 + n)!, n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sinh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1/n!, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Tanh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(2^(1 + n)*(-1 + 2^(1 + n))*BernoulliB(1 + n))/(1 + n)!, Mod(n, 2) == 1 && n >= 1}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(ArcCos(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{Pi/2, n == 0}, {-(Pochhammer(1/2, (1/2)*(-1 + n))/(n*((1/2)*(-1 + n))!)), n > 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcCot(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{I^(1 + n)/n, n > 0 && Mod(n, 2) == 1}, {Pi/2, n == 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcSin(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{Pochhammer(1/2, (1/2)*(-1 + n))/(n*((1/2)*(-1 + n))!), Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{I^(-1 + n)/n, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
    
  SeriesCoefficient(ArcCosh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I*Pi)/2, n == 0}, {-((I*Pochhammer(1/2, (1/2)*(-1 + n)))/(n*((1/2)*(-1 + n))!)), n >= 1 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcSinh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I^(-1 + n)*Pochhammer(1/2, (1/2)*(-1 + n)))/(n*((1/2)*(-1 + n))!), Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(ArcTanh(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1/n, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(n,x),    
     
     
  SeriesCoefficient(Csc(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1, n == -1}, {-((2*I*I^n*(-1 + 2^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sec(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{(I^n*EulerE(n))/n!, Mod(n, 2) == 0 && n >= 0}}, 0) 
    /; FreeQ(n,x),    
     
  SeriesCoefficient(Csch(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{1, n == -1}, {-((2*(-1 + 2^n)*BernoulliB(1 + n))/(1 + n)!), n >= 0 && Mod(n, 2) == 1}}, 0)
    /; FreeQ(n,x),
  SeriesCoefficient(Sech(x_),{x_Symbol, 0, n_NotListQ}):=Piecewise({{EulerE(n)/n!, n >= 0}}, 0)
    /; FreeQ(n,x),     
    
  SeriesCoefficient(Cos(x_),{x_Symbol, a_, n_NotListQ}):=Piecewise({{Cos(a + (n*Pi)/2)/n!, n >= 0}}, 0) 
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(Sin(x_),{x_Symbol, a_, n_NotListQ}):=Piecewise({{Sin(a + (n*Pi)/2)/n!, n >= 0}}, 0) 
    /; FreeQ(a,x)&&FreeQ(n,x),    
  SeriesCoefficient(Cosh(x_),{x_Symbol, a_, n_NotListQ}):=Piecewise({{Cosh(a)/n!, Mod(n, 2) == 0 && n >= 0}, {Sinh(a)/n!, Mod(n, 2) == 1 && n >= 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(Sinh(x_),{x_Symbol, a_, n_NotListQ}):=Piecewise({{Cosh(a)/n!, Mod(n, 2) == 1 && n >= 0}, {Sinh(a)/n!, Mod(n, 2) == 0 && n >= 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),        
    
  SeriesCoefficient(ArcCot(x_),{x_Symbol, a_, n_NotListQ}) := Piecewise({{(I*((-I - a)^(-n) - (I - a)^(-n)))/(2*n), n > 0}, {(1/2)*I*(Log((-I + a)/a) - Log((I + a)/a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, a_, n_NotListQ}) := Piecewise({{-((I*((-I - a)^(-n) - (I - a)^(-n)))/(2*n)), n > 0}, {(1/2)*I*(Log(1 - I*a) - Log(1 + I*a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x), 
    
  SeriesCoefficient(ArcCoth(x_),{x_Symbol, a_, n_NotListQ}) := Piecewise({{(-(-1 - a)^(-n) + (1 - a)^(-n))/(2*n), n > 0}, {(1/2)*(Log(1 + 1/a) - Log((-1 + a)/a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),
  SeriesCoefficient(ArcTanh(x_),{x_Symbol, a_, n_NotListQ}) := Piecewise({{((-1)^n*((-1 + a)^(-n) - (1 + a)^(-n)))/(2*n), n >= 1}, {(1/2)*(-Log(1 - a) + Log(1 + a)), n == 0}}, 0)
    /; FreeQ(a,x)&&FreeQ(n,x),   
   
   
  SeriesCoefficient(ArcCos(x_),{x_Symbol, a_, 1}):= -(1/Sqrt(1 - a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcCot(x_),{x_Symbol, a_, 1}):= -(1/(1 + a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcSin(x_),{x_Symbol, a_, 1}):= 1/Sqrt(1 - a^2)
    /; FreeQ(a,x),
  SeriesCoefficient(ArcTan(x_),{x_Symbol, a_, 1}):=1/(1 + a^2)
    /; FreeQ(a,x),
  SeriesCoefficient(ArcCsc(x_),{x_Symbol, a_, 1}):= -(1/(Sqrt(1 - 1/a^2)*a^2))
    /; FreeQ(a,x),
  SeriesCoefficient(ArcSec(x_),{x_Symbol, a_, 1}):= 1/(Sqrt(1-1/a^2)*a^2)
    /; FreeQ(a,x),
        
  SeriesCoefficient(Log(x_),{x_Symbol, a_, n_NotListQ}):=Piecewise({{(-1)^(1 + n)/(a^n*n), n >= 1}, {Log(a), n == 0}}, 0)
    /; FreeQ(a,x) && a!=0 && FreeQ(n,x),
  SeriesCoefficient(b_^x_,{x_Symbol, a_, n_NotListQ}):=Piecewise({{(b^a*Log(b)^n)/n!, n >= 0}}, 0)
    /; FreeQ(b,x) && FreeQ(a,x) && FreeQ(n,x)  
     
} 