{
  Gamma(-(5/2)) = -((8*Sqrt(Pi))/15),
  Gamma(-(3/2)) = (4*Sqrt(Pi))/3,
  Gamma(-(1/2)) = -2*Sqrt(Pi),
  Gamma(1/2) = Sqrt(Pi),
  Gamma(3/2) = Sqrt(Pi)/2,
  Gamma(5/2) = (3*Sqrt(Pi))/4,
  
  Gamma(a_, 0):=ComplexInfinity
   /; Re(a)<0,
  Gamma(a_, 0):=Gamma(a)
   /; Re(a)>0,
  Gamma(a_, -1):=E*Subfactorial(a - 1),
  Gamma(a_, Infinity):=0,
  Gamma(1/2, z_):=Sqrt(Pi)*Erfc(Sqrt(z)),
  Gamma(-(1/2), z_):=2/(E^z*Sqrt(z)) - 2*Sqrt(Pi)*Erfc(Sqrt(z)),
  Gamma(1, z_):=E^(-z)
}