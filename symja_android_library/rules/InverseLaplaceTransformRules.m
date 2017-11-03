{ 
 InverseLaplaceTransform(s_, s_, t_) := DiracDelta'(t),
 InverseLaplaceTransform(s_^(-1), s_, t_) := 1,
 InverseLaplaceTransform(s_^(n_IntegerQ), s_, t_) := t^(-n-1)/(-n-1)!
    /; n<(-1),
 InverseLaplaceTransform((s_+a_)^(-1), s_, t_) := E^(-a*t)
    /; FreeQ(a, s), 
    
 InverseLaplaceTransform((a_RealNumberQ+s_^2)^(-1), s_, t_) := 
    If(a>0, Sin(Sqrt(a)*t)/Sqrt(a), (-1+E^(2*Sqrt(-a)*t))/(E^(Sqrt(-a)*t)*(2*Sqrt(-a)))),
 InverseLaplaceTransform(s_*(s_^2+a_NumberQ)^(-1), s_, t_) := Cos(Sqrt(a)*t)
    /; a>0, 
 
 InverseLaplaceTransform((s_^2+a_^2)^(-1), s_, t_) := Sin(a*t)/a
    /; FreeQ(a, s), 
 InverseLaplaceTransform((s_^2-a_^2)^(-1), s_, t_) := (-1+E^(2*a*t))/(E^(a*t)*(2*a))
    /; FreeQ(a, s), 
 InverseLaplaceTransform(s_*(s_^2+a_^2)^(-1), s_, t_) := Cos(a*t)
    /; FreeQ(a, s)

 }   