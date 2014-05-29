{
 Limit(x_^m_IntegerQ, x_Symbol->Infinity):= 0 /; Negative(m),
 Limit(x_^m_IntegerQ, x_Symbol->DirectedInfinity(-1)):= 0 /; Negative(m),
 Limit((1+x_^(-1))^x_, x_Symbol->Infinity)=E,
 Limit((1+a_*(x_^(-1)))^x_, x_Symbol->Infinity)=E^(a) /; FreeQ(a,x)  
}