{
 Limit(x_*Sqrt(2*Pi)^(x_^(-1))*(Sin(x_)/(x_!))^(x_^(-1)), x_Symbol->Infinity):= E,
 Limit(x_*((x_!)^(-1*x_^(-1))), x_Symbol->Infinity):= E,
 Limit(x_^(a_.*(x_^n_.) + b_.), x_Symbol->0):= With({r=ConditionalExpression( 1, Element(a,Reals)&&b>0&&n>0 )}, r /; r=!= Undefined),
 Limit(x_^(a_.*(x_^n_.) + b_.), x_Symbol->0):= With({r=ConditionalExpression( 0, Element(b,Reals)&&a>0&&n<0&&Cos(-n*Pi)>0&&Sin(-n*Pi)>0 )}, r /; r=!= Undefined),
 Limit(x_^(a_.*(x_^n_.) + b_.), x_Symbol->Infinity):= With({r=ConditionalExpression( Infinity, Element(b,Reals)&&a>0&&n>0 )}, r /; r=!= Undefined),
 Limit(x_^(a_.*(x_^n_.)), x_Symbol->Infinity):= With({r=ConditionalExpression( 1, Element(a,Reals)&&n<0 )}, r /; r=!= Undefined),
 Limit(x_^m_?RealNumberQ, x_Symbol->Infinity):= If(m<0, 0, Infinity),
 Limit(m_?NumberQ^x_, x_Symbol->Infinity):= If(m>1, Infinity, If(m==1, 1, 0)) /; Positive(m),
 Limit(m_?NumberQ^(-x_), x_Symbol->Infinity):= 0 /; m>1,
 Limit(E^x_, x_Symbol->Infinity):= Infinity,
 Limit(E^x_, x_Symbol->-Infinity):= 0,
 Limit(Log(x_), x_Symbol->0)=-Infinity,  
 Limit(Log(x_), x_Symbol->Infinity)=Infinity,  
 Limit(Log(x_), x_Symbol->-Infinity)=Infinity, 
 Limit((1+x_^(-1))^x_, x_Symbol->Infinity)=E,
 Limit((1+a_.*(x_^(-1)))^(b_.*x_), x_Symbol->-Infinity)=E^(a*b) /; FreeQ({a,b},x),
 Limit((1+a_.*(x_^(-1)))^(b_.*x_), x_Symbol->Infinity)=E^(a*b) /; FreeQ({a,b},x),
 Limit((1+x_)^(1/x_), x_Symbol->0)=E,
 Limit(((a_.+x_)/(b_.+x_))^(c_.+x_), x_Symbol->-Infinity)=E^(a-b) /; FreeQ({a,b,c},x),
 Limit(((a_.+x_)/(b_.+x_))^(c_.+x_), x_Symbol->Infinity)=E^(a-b) /; FreeQ({a,b,c},x),
 Limit( HarmonicNumber(y_Symbol,s_Integer), x_Symbol->Infinity):=With({v=s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!)) /; EvenQ(s)&&Positive(s),
 Limit(Tan(x_), x_Symbol->Pi/2):= Indeterminate,
 Limit(Cot(x_), x_Symbol->0):= Indeterminate,

 Limit(ArcCos(x_), x_Symbol->Infinity)=I*Infinity,  
 Limit(ArcCos(x_), x_Symbol->-Infinity)=(-I)*Infinity, 
 Limit(ArcCot(x_), x_Symbol->Infinity)=0,  
 Limit(ArcCot(x_), x_Symbol->-Infinity)=0, 
 Limit(ArcCsc(x_), x_Symbol->Infinity)=0,  
 Limit(ArcCsc(x_), x_Symbol->-Infinity)=0, 
 Limit(ArcSec(x_), x_Symbol->Infinity)=Pi/2,  
 Limit(ArcSec(x_), x_Symbol->-Infinity)=Pi/2, 
 Limit(ArcSin(x_), x_Symbol->Infinity)=(-I)*Infinity,  
 Limit(ArcSin(x_), x_Symbol->-Infinity)=I*Infinity,   
 Limit(ArcTan(x_), x_Symbol->Infinity)=Pi/2,  
 Limit(ArcTan(x_), x_Symbol->-Infinity)=-Pi/2, 

 Limit(ArcCosh(x_), x_Symbol->Infinity)=Infinity,  
 Limit(ArcCosh(x_), x_Symbol->-Infinity)=Infinity, 
 Limit(ArcCoth(x_), x_Symbol->Infinity)=0,  
 Limit(ArcCoth(x_), x_Symbol->-Infinity)=0, 
 Limit(ArcCsch(x_), x_Symbol->Infinity)=0,  
 Limit(ArcCsch(x_), x_Symbol->-Infinity)=0, 
 Limit(ArcSech(x_), x_Symbol->Infinity)=I*Pi/2, 
 Limit(ArcSech(x_), x_Symbol->-Infinity)=I*Pi/2, 
 Limit(ArcSinh(x_), x_Symbol->Infinity)=Infinity,  
 Limit(ArcSinh(x_), x_Symbol->-Infinity)=-Infinity,   
 Limit(ArcTanh(x_), x_Symbol->Infinity)=-I*Pi/2,   
 Limit(ArcTanh(x_), x_Symbol->-Infinity)=I*Pi/2, 
 
 Limit(AiryAi(x_), x_Symbol->Infinity)=0,
 Limit(AiryAi(x_), x_Symbol->-Infinity)=0,
 Limit(AiryAiPrime(x_), x_Symbol->Infinity)=0,
 Limit(AiryBi(x_), x_Symbol->Infinity)=Infinity,
 Limit(AiryBi(x_), x_Symbol->-Infinity)=0,
 Limit(AiryBiPrime(x_), x_Symbol->Infinity)=Infinity,
 Limit(AiryBiPrime(x_), x_Symbol->-Infinity)=0,
 

 Limit(BesselJ(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
 Limit(BesselJ(n_, x_), x_Symbol->-Infinity)=0
   /; FreeQ(n,x),
 Limit(BesselI(n_, x_), x_Symbol->Infinity)=Infinity
   /; FreeQ(n,x),
 Limit(BesselY(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
 Limit(BesselY(n_, x_), x_Symbol->-Infinity)=0
   /; FreeQ(n,x),
 Limit(BesselK(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
   
 Limit(SphericalBesselJ(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
 Limit(SphericalBesselJ(n_, x_), x_Symbol->-Infinity)=0
   /; FreeQ(n,x),
 Limit(SphericalBesselY(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
 Limit(SphericalBesselY(n_, x_), x_Symbol->-Infinity)=0
   /; FreeQ(n,x),
 Limit(SphericalHankelH1(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
 Limit(SphericalHankelH2(n_, x_), x_Symbol->Infinity)=0
   /; FreeQ(n,x),
   
 Limit(StruveH(n_, x_), x_Symbol->Infinity):=ConditionalExpression(Infinity, 1<n<5/2)
   /; FreeQ(n,x),
 Limit(StruveH(n_, x_), x_Symbol->-Infinity):=ConditionalExpression(0,Element(n,Integers) && n<1/2)
   /; FreeQ(n,x),
   
   
 Limit(Cosh(x_), x_Symbol->Infinity)=Infinity,  
 Limit(Cosh(x_), x_Symbol->-Infinity)=Infinity,
 Limit(Coth(x_), x_Symbol->Infinity)=1,  
 Limit(Coth(x_), x_Symbol->-Infinity)=-1,
 Limit(Csch(x_), x_Symbol->Infinity)=0,  
 Limit(Csch(x_), x_Symbol->-Infinity)=0,
 Limit(Sech(x_), x_Symbol->Infinity)=0,  
 Limit(Sech(x_), x_Symbol->-Infinity)=0,
 Limit(Sinh(x_), x_Symbol->Infinity)=Infinity,  
 Limit(Sinh(x_), x_Symbol->-Infinity)=-Infinity,
 Limit(Tanh(x_), x_Symbol->Infinity)=1,  
 Limit(Tanh(x_), x_Symbol->-Infinity)=-1,
 
  
 Limit(Gamma(x_), x_Symbol->0, Direction->1)=-Infinity,  
 Limit(Gamma(x_), x_Symbol->0, Direction->-1)=Infinity,  
 Limit(Gamma(z_, x_), x_Symbol->Infinity)=0,  
 Limit(Gamma(z_, x_), x_Symbol->0):=Gamma(z),
 
 Limit(x_/Abs(x_), x_Symbol->0, Direction->1):= -1,
 Limit(x_/Abs(x_), x_Symbol->0, Direction->-1):= 1,
 Limit(Tan(x_), x_Symbol->Pi/2, Direction->1):= Infinity,
 Limit(Tan(x_), x_Symbol->Pi/2, Direction->-1):= -Infinity,
 Limit(Cot(x_), x_Symbol->0, Direction->1):= -Infinity,
 Limit(Cot(x_), x_Symbol->0, Direction->-1):= Infinity
}