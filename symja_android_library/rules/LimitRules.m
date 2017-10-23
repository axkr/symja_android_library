{
 Limit(x_^m_NumberQ, x_Symbol->Infinity):= Infinity /; Positive(m),
 Limit(x_^m_NumberQ, x_Symbol->Infinity):= 0 /; Negative(m),
 Limit(m_NumberQ^x_, x_Symbol->Infinity):= If(m>1, Infinity, If(m==1, 1, 0)) /; Positive(m),
 Limit(m_NumberQ^(-x_), x_Symbol->Infinity):= 0 /; m>1,
 Limit(E^x_, x_Symbol->Infinity):= Infinity,
 Limit(E^x_, x_Symbol->-Infinity):= 0,
 Limit(Log(x_), x_Symbol->0)=-Infinity, 
 Limit(Log(x_), x_Symbol->Infinity)=Infinity,  
 Limit((1+x_^(-1))^x_, x_Symbol->Infinity)=E,
 Limit((1+a_*(x_^(-1)))^x_, x_Symbol->Infinity)=E^(a) /; FreeQ(a,x),
 Limit( HarmonicNumber(y_Symbol,s_IntegerQ), x_Symbol->Infinity):=Module({v=s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!)) /; EvenQ(s)&&Positive(s),
 Limit(x_/Abs(x_), x_Symbol->0, Direction->1):= -1,
 Limit(x_/Abs(x_), x_Symbol->0, Direction->-1):= 1,
 Limit(Tan(x_), x_Symbol->Pi/2, Direction->1):= Infinity,
 Limit(Tan(x_), x_Symbol->Pi/2, Direction->-1):= -Infinity,
 Limit(Cot(x_), x_Symbol->0, Direction->1):= -Infinity,
 Limit(Cot(x_), x_Symbol->0, Direction->-1):= Infinity
}