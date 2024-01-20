{
ProductLog(Undefined)=Undefined,
ProductLog(0)=0,
ProductLog(-Pi/2)=I*Pi/2,
ProductLog(-1/E) = -1,
ProductLog(E) = 1,

ProductLog(-1,(-1/2)*Pi) = (-1/2)*Pi*I,
ProductLog(-1,-E^(-1)) = -1,

ProductLog(Infinity) = Infinity,
ProductLog(-Infinity) = Infinity,
ProductLog(I*Infinity) = Infinity,
ProductLog(-I*Infinity) = Infinity,
ProductLog(ComplexInfinity) = Infinity,

ProductLog(x_*Log(x_)) := Log(x) 
  /; x>(1/E),
ProductLog(a_*Log(x_)) := -Log(x) 
  /; 0<x && x<=E && a<0 && (-x*a==1),
ProductLog(x_*E^x_) := x 
  /; x>=-1,
ProductLog(-1, x_*E^x_) := x 
  /; x<=-1,
ProductLog(Rational(k_,n_)*b_^Rational(c_,n_)*Log(b_)) :=
  Module( {a, v},
    a = N( (n*ProductLog((b^(c/n)*k*Log(b))/n))/Log(b) );
	v = Rationalize(a);
	v*Log(b)/n
      /; IntegerQ(v) && 
         v >= 1 &&
	     PossibleZeroQ( (((-b^(c/n))*k + b^(v/n)*v)*Log(b))/n ))   
}