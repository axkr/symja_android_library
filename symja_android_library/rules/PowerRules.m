{
 E^(3/2*I*Pi)=-I, 
 E^(Pi*c_Complex):=Module({r=Re(c),j=Im(c)},If(EvenQ(j),1,-1) /; r==0 && IntegerQ(j)),
 E^(I*Infinity)=Indeterminate,
 E^(-I*Infinity)=Indeterminate,
 E^(ComplexInfinity)=Indeterminate,
 E^Log(x_):=x,
 E^(a_*Log(x_)):=x^a /; FreeQ(a,x),
 Tan(x_)^(m_IntegerQ):=Cot(x)^(-m)/;(m<0),
 Cot(x_)^(m_IntegerQ):=Tan(x)^(-m)/;(m<0),
 Sec(x_)^(m_IntegerQ):=Cos(x)^(-m)/;(m<0),
 Cos(x_)^(m_IntegerQ):=Sec(x)^(-m)/;(m<0),
 Csc(x_)^(m_IntegerQ):=Sin(x)^(-m)/;(m<0),
 Sin(x_)^(m_IntegerQ):=Csc(x)^(-m)/;(m<0)
}