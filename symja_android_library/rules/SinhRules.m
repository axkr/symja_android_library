 {
 Sinh(Undefined)=Undefined,
 Sinh(0)=0,
 Sinh(1/6*Pi*I)=1/2*I,
 Sinh(1/4*Pi*I)=1/2*Sqrt(2)*I,
 Sinh(1/3*Pi*I)=1/2*Sqrt(3)*I,
 Sinh(1/2*Pi*I)=I,
 Sinh(2/3*Pi*I)=1/2*Sqrt(3)*I,
 Sinh(3/4*Pi*I)=1/2*Sqrt(2)*I,
 Sinh(5/6*Pi*I)=1/2*I,
 Sinh(Pi*I)=0,
 Sinh(7/6*Pi*I)=-1/2*I, 
 Sinh(5/4*Pi*I)=-1/2*Sqrt(2)*I, 
 Sinh(4/3*Pi*I)=-1/2*Sqrt(3)*I, 
 Sinh(3/2*Pi*I)=-I, 
 Sinh(5/3*Pi*I)=-1/2*Sqrt(3)*I,
 Sinh(7/4*Pi*I)=-1/2*Sqrt(2)*I,
 Sinh(11/6*Pi*I)=-1/2*I,
 Sinh(2*Pi*I)=0,
 
 Sinh(Pi/2*I+x_):=I*Cosh(x),
 Sinh(Complex(0,n_Integer)*Pi+x_):=(-1)^n*Sinh(x),
 
 Sinh(ArcSinh(x_)):=x,
 Sinh(ArcCosh(x_)):=Sqrt((-1+x)/(1+x))*(1+x),
 Sinh(ArcTanh(x_)):=x/Sqrt(1-x^2),
 Sinh(ArcCoth(x_)):=1/(Sqrt(1-1/x^2)*x),
 Sinh(ArcSech(x_)):=((1+x)*Sqrt((1-x)/(1+x)))/x,
 Sinh(ArcCsch(x_)):=1/x,
 Sinh(Log(x_)):=(1/2)*x-(1/2)/x,
 
 Sinh(Infinity)=Infinity,
 Sinh(ComplexInfinity)=Indeterminate
}