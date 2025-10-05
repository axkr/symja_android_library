 {
 Csch(Undefined)=Undefined,
 Csch(0)=ComplexInfinity,
 Csch(1/6*Pi*I)=-2*I,
 Csch(1/4*Pi*I)=-Sqrt(2)*I,
 Csch(1/2*Pi*I)=-I,
 Csch(3/4*Pi*I)=-Sqrt(2)*I,
 Csch(5/6*Pi*I)=-2*I,  
 Csch(Pi*I)=ComplexInfinity,  
 Csch(7/6*Pi*I)=2*I, 
 Csch(5/4*Pi*I)=Sqrt(2)*I,
 Csch(3/2*Pi*I)=I,   
 Csch(7/4*Pi*I)=Sqrt(2)*I, 
 Csch(11/6*Pi*I)=2*I, 
 Csch(2*Pi*I)=ComplexInfinity,  
 
 Csch(ArcSinh(x_)):=1/x,
 Csch(ArcCosh(x_)):=1/(Sqrt((-1+x)/(1+x))*(1+x)),
 Csch(ArcTanh(x_)):=Sqrt(1-x^2)/x,
 Csch(ArcCoth(x_)):=Sqrt(1-1/x^2)*x,
 Csch(ArcSech(x_)):=x/(Sqrt((1 - x)/(1 + x))*(1 + x)),
 Csch(ArcCsch(x_)):=x,
 Csch(Log(x_)):=2*x/(-1+x^2),
 
 Csch(Infinity)=0,
 Csch(ComplexInfinity)=Indeterminate 
}