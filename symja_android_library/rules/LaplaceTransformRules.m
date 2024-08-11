{ 
 LaplaceTransform(a_. * E^(b_. + c_. * t_), t_, s_) := LaplaceTransform(a * E^b, t, s-c)
   /; FreeQ({b,c,s}, t), 
   
 LaplaceTransform(t_^(1/2), t_, s_) := Sqrt(Pi)/(2*s^(3/2))
   /; FreeQ(s, t),
 LaplaceTransform(Sin(a_.*t_), t_, s_) := a/(s^2+a^2)
   /; FreeQ({a,s}, t)&&FreeQ(a,s),
 LaplaceTransform(Cos(a_.*t_), t_, s_) := s/(s^2+a^2)
   /; FreeQ({a,s}, t)&&FreeQ(a,s),
 LaplaceTransform(Sinh(t_), t_, s_) := c/(s^2-1)
   /; FreeQ(s, t),
 LaplaceTransform(Cosh(t_), t_, s_) := s/(s^2-1)
   /; FreeQ(s, t),
 LaplaceTransform(Tanh(t_), t_, s_) := (1/2)*(-(2/s)-PolyGamma(0,s/4)+PolyGamma(0,(2+s)/4))
   /; FreeQ(s, t),
 LaplaceTransform(DiracDelta(a_*t_), t_, s_) := 1/Abs(a)
   /; FreeQ({a,s}, t),
 LaplaceTransform(E^t_, t_, s_) := 1/(s-1)
   /; FreeQ(s, t),
 LaplaceTransform(Log(t_), t_, s_) := -(EulerGamma+Log(s))/s
   /; FreeQ(s, t),
 LaplaceTransform(Log(t_)^2, t_, s_) := (6*EulerGamma^2 + Pi^2 + 6*Log(s)*(2*EulerGamma+Log(s)))/(6*s)
   /; FreeQ(s, t),
 LaplaceTransform(Erf(t_), t_, s_) := E^(s^2/4)*Erfc(s/2)/s
   /; FreeQ(s, t),
 LaplaceTransform(Erf(t_^(1/2)), t_, s_) := 1/(Sqrt(s+1)*s)
   /; FreeQ(s, t),
 LaplaceTransform(UnitStep(a_.*t_),t_,s_):=Which(Sign(a)==1,1/s,Sign(a)==-1,0,True,0)
   /; FreeQ({a,s}, t),
    
 LaplaceTransform(f_'(t_), t_, s_) := s*LaplaceTransform(f(t),t,s)-f(0)
   /; FreeQ(f, t), 
 LaplaceTransform(f_''(t_), t_, s_) := s^2*LaplaceTransform(f(t),t,s)-s*f(0)-f'(0)
   /; FreeQ({f,s}, t)
 }   