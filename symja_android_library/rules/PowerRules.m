{
 E^(1/2*I*Pi)=I,
 E^(3/2*I*Pi)=-I, 
 E^(I*Pi*n_):=(-1)^n /; Element(n,Integers),
 E^(Pi*c_Complex):=Module({r=Re(c),j=Im(c)},If(EvenQ(j),1,-1) /; r==0 && IntegerQ(j)),
 E^(x_+Pi*c_Complex):=Module({r=Re(c),j=Im(c)},If(EvenQ(j),E^x,-E^x) /; r==0 && IntegerQ(j)),
 E^(I*Infinity)=Indeterminate,
 E^(-I*Infinity)=Indeterminate,
 E^(ComplexInfinity)=Indeterminate,
 E^Log(x_):=x,
 E^(a_*Log(x_)):=x^a /; FreeQ(a,x),
 Overflow()^(-1) := Underflow(),
 Underflow()^(-1) := Overflow()
}