{ 
 ArcTan(Undefined)=Undefined,
 ArcTan(0)=0, 
 ArcTan(0,0)=Indeterminate, 
 ArcTan(2-Sqrt(3))=1/12*Pi,
 ArcTan(Sqrt(3)-2)=-1/12*Pi,
 ArcTan(Sqrt(2)-1)=1/8*Pi, 
 ArcTan(1-Sqrt(2))=-1/8*Pi, 
 ArcTan(1/Sqrt(3))=1/6*Pi,   
 ArcTan(Sqrt(5-2*Sqrt(5)))=1/5*Pi, 
 ArcTan(1)=1/4*Pi,
 ArcTan(1,1)=1/4*Pi,
 ArcTan(-1,-1)=-3/4*Pi, 
 ArcTan(Sqrt(3))=1/3*Pi,  
 ArcTan(1+Sqrt(2))=3/8*Pi, 
 ArcTan(2+Sqrt(3))=5/12*Pi,
 ArcTan(Sqrt(5-2*Sqrt(5)))=1/5*Pi,
 ArcTan(Sqrt(5+2*Sqrt(5)))=2/5*Pi,
 
 ArcTan(I)=I*Infinity,

 
 ArcTan(Undefined, y_):=Undefined,
 ArcTan(x_, Undefined):=Undefined,
 ArcTan(Infinity, y_)=0, 
 ArcTan(Infinity)=Pi/2,
 ArcTan(-Infinity)=-Pi/2,
 ArcTan(I*Infinity)=Pi/2,
 ArcTan(-I*Infinity)=-Pi/2,
 ArcTan(ComplexInfinity)=Indeterminate,
 ArcTan(x_?RealValuedNumberQ, y_?RealValuedNumberQ) :=  
   If(x == 0, If(y == 0, Indeterminate, If(y > 0, Pi/2, -Pi/2)), If(x > 0,
        ArcTan(y/x), If(y >= 0, ArcTan(y/x) + Pi, ArcTan(y/x) - Pi))),
 ArcTan(x_?NumberQ, y_?NumberQ) := (Pi*(2*Sqrt(x^2) - x))/(4*y) 
   /; (x^2 == y^2)
 }