{
 Limit(x_^m_IntegerQ, x_Symbol->Infinity):= 0 /; Negative(m),
 Limit(x_^m_IntegerQ, x_Symbol->DirectedInfinity(-1)):= 0 /; Negative(m),
 Limit((1+x_^(-1))^x_, x_Symbol->Infinity)=E,
 Limit((1+a_*(x_^(-1)))^x_, x_Symbol->Infinity)=E^(a) /; FreeQ(a,x), 
 Limit(Sum(y_Symbol^(s_IntegerQ),{y_Symbol,1,x_Symbol}), x_Symbol->Infinity):=Module({v=-s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!)) /; EvenQ(s)&&s<0
}