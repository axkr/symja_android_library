{
ProductLog(0)=0,
ProductLog(-Pi/2)=I*Pi/2,
ProductLog(-1/E) = -1,
ProductLog(E) = 1,

ProductLog(-1,(-1/2)*Pi) = (-1/2)*Pi*I,
ProductLog(-1,-E^(-1)) = -1,

ProductLog(Infinity) = Infinity,
ProductLog(-Infinity) = -Infinity,
ProductLog(I*Infinity) = Infinity,
ProductLog(-I*Infinity) = Infinity,
ProductLog(ComplexInfinity) = Infinity,
ProductLog(x_) * E^ProductLog(x_) := x,

ProductLog(0,x_) := ProductLog(x),
ProductLog(n_NumberQ,0) := -Infinity/;n!=0

}