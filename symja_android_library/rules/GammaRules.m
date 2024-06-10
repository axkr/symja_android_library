{
  Gamma(Undefined)=Undefined,
  Gamma(-(5/2)) = -((8*Sqrt(Pi))/15),
  Gamma(-(3/2)) = (4*Sqrt(Pi))/3,
  Gamma(-(1/2)) = -2*Sqrt(Pi),
  Gamma(1/2) = Sqrt(Pi),
  Gamma(3/2) = Sqrt(Pi)/2,
  Gamma(5/2) = (3*Sqrt(Pi))/4,
  Gamma(Infinity) = Infinity,
  Gamma(-Infinity) = Indeterminate,
  Gamma(I*Infinity) = 0,
  Gamma(-I*Infinity) = 0, 
  Gamma(ComplexInfinity) = Indeterminate, 

  Gamma(Undefined, y_):=Undefined,
  Gamma(x_, Undefined):=Undefined,
  Gamma(a_, Infinity):=0,
  Gamma(1/2, z_?NumericQ):=Sqrt(Pi)*Erfc(Sqrt(z)),
  Gamma(-(1/2), z_?NumericQ):=2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*Erfc(Sqrt(z)),
  Gamma(1, z_):=E^(-z),
  
  Gamma(a_, z_, 0) := Gamma(a, z) - Gamma(a) 
    /; Re(a) > 0,
  Gamma(a_, z_, Infinity) := Gamma(a, z),
  Gamma(a_, Infinity, z_) := -Gamma(a, z),
  Gamma(a_, 0, Infinity) := Gamma(a) /; Re(a) > 0,
  Gamma(a_, 0, z_) := Gamma(a) - Gamma(a, z) 
    /; Re(a) > 0,
  Gamma(a_, x_, y_) := Gamma(a, x) - Gamma(a, y)
}