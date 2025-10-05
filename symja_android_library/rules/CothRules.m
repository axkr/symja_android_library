 {
 Coth(Undefined)=Undefined,
 Coth(0)=ComplexInfinity,
 Coth(0.0)=ComplexInfinity,
 Coth(1/4*Pi*I)=-I,
 Coth(1/2*Pi*I)=0,
 Coth(3/4*Pi*I)=I,
 Coth(Pi*I)=ComplexInfinity,
 
 Coth(ArcSinh(x_)):=Sqrt(1 + x^2)/x,
 Coth(ArcCosh(x_)):=x/(Sqrt((-1+x)/(1+x))*(1+x)),
 Coth(ArcTanh(x_)):=1/x,
 Coth(ArcCoth(x_)):=x,
 Coth(ArcSech(x_)):=1/(Sqrt((1-x)/(1+x))*(1+x)),
 Coth(ArcCsch(x_)):=Sqrt(1 + 1/x^2)*x,
 Coth(Log(x_)):=(1+x^2)/(x^2-1),
 
 
 
 Coth(Infinity)=1,
 Coth(ComplexInfinity)=Indeterminate 
}