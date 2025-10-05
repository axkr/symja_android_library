 {
 Tanh(Undefined)=Undefined,
 Tanh(0)=0,
 Tanh(1/4*Pi*I)=I,
 Tanh(1/3*Pi*I)=Sqrt(3)*I,
 Tanh(1/2*Pi*I)=ComplexInfinity, 
 Tanh(2/3*Pi*I)=-Sqrt(3)*I,
 Tanh(3/4*Pi*I)=-I,
 Tanh(5/6*Pi*I)=-I/Sqrt(3),
 Tanh(Pi*I)=0,
  
 Tanh(ArcSinh(x_)):=x/Sqrt(1 + x^2),
 Tanh(ArcCosh(x_)):=((1+x)*Sqrt((-1+x)/(1+x)))/x,
 Tanh(ArcTanh(x_)):=x,
 Tanh(ArcCoth(x_)):=1/x,
 Tanh(ArcSech(x_)):=Sqrt((1-x)/(1+x))*(1+x),
 Tanh(ArcCsch(x_)):=1/(Sqrt(1 + 1/x^2)*x),
 Tanh(Log(x_)):=(x^2-1)/(1+x^2), 
 
 Tanh(Infinity)=1, 
 Tanh(ComplexInfinity)=Indeterminate 
}