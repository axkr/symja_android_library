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
 ArcTan(Infinity, y_):=0, 
 ArcTan(Infinity)=Pi/2,
 ArcTan(-Infinity)=-Pi/2,
 ArcTan(I*Infinity)=Pi/2,
 ArcTan(-I*Infinity)=-Pi/2,
 ArcTan(ComplexInfinity)=Indeterminate,
 (* Positive/Negative are exact for inexact numbers, Equal is not: `3.*10^-20 == 0` is True, so
    testing the quadrant with `x == 0` and `x > 0` collapsed an ordinary point close to the origin
    such as ArcTan(3.0*10^-20, 4.0*10^-20) onto the origin and answered Indeterminate. A number
    which is neither Positive nor Negative is the zero of the third branch. *)
 ArcTan(x_?RealValuedNumberQ, y_?RealValuedNumberQ) :=
   If(Positive(x), ArcTan(y/x),
      If(Negative(x), If(Negative(y), ArcTan(y/x) - Pi, ArcTan(y/x) + Pi),
         If(Positive(y), Pi/2, If(Negative(y), -Pi/2, Indeterminate)))),
 ArcTan(x_?NumberQ, y_?NumberQ) := (Pi*(2*Sqrt(x^2) - x))/(4*y)
   /; (x^2 == y^2)
 }