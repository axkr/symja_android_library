{ 
 LaplaceTransform(a_ * f_, t_, s_) := a * LaplaceTransform(f, t, s)
   /; FreeQ(a, t),
 LaplaceTransform(a_ * t_ ^n_., t_, s_) := (-1)^n * D(LaplaceTransform(a, t, s), {s,n}) 
   /; IntegerQ(n) && n>0, 
 LaplaceTransform(a_. * E^(b_. + c_. * t_), t_, s_) := LaplaceTransform(a * E^b, t, s-c)
   /; FreeQ({b,c}, t), 
   
 LaplaceTransform(t_^(1/2), t_, s_) := Sqrt(Pi)/(2*s^(3/2)),
 LaplaceTransform(Sin(t_), t_, s_) := 1/(s^2+1),
 LaplaceTransform(Cos(t_), t_, s_) := s/(s^2+1),
 LaplaceTransform(Sinh(t_), t_, s_) := c/(s^2-1),
 LaplaceTransform(Cosh(t_), t_, s_) := s/(s^2-1),
 LaplaceTransform(E^t_, t_, s_) := 1/(s-1),
 LaplaceTransform(Log(t_), t_, s_) := -(EulerGamma+Log(s))/s,
 LaplaceTransform(Log(t_)^2, t_, s_) := (6*EulerGamma^2 + Pi^2 + 6 Log(s)*(2*EulerGamma+Log(s)))/(6*s),
 LaplaceTransform(Erf(t_), t_, s_) := E^(s^2/4)*Erfc(s/2)/s,
 LaplaceTransform(Erf(t_^(1/2)), t_, s_) := 1/(Sqrt(s+1)*s)
 }   