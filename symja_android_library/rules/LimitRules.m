{
 Limit(x_^m_NumberQ, x_Symbol->Infinity):= Infinity /; Positive(m),
 Limit(x_^m_NumberQ, x_Symbol->Infinity):= 0 /; Negative(m),
 Limit(m_NumberQ^x_, x_Symbol->Infinity):= If(m>1, Infinity, If(m==1, 1, 0)) /; Positive(m),
 Limit(m_NumberQ^(-x_), x_Symbol->Infinity):= 0 /; m>1,
 Limit(Log(x_), x_Symbol->Infinity)=Infinity,
 Limit((1+x_^(-1))^x_, x_Symbol->Infinity)=E,
 Limit((1+a_*(x_^(-1)))^x_, x_Symbol->Infinity)=E^(a) /; FreeQ(a,x), 
 Limit(Sum(y_Symbol^(s_IntegerQ),{y_Symbol,1,x_Symbol}), x_Symbol->Infinity):=Module({v=-s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!)) /; EvenQ(s)&&s<0
}