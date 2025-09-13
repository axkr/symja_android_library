 {  
   SphericalHarmonicY(0, 0, t_, p_) := 1/(2*Sqrt(Pi)),
   SphericalHarmonicY(1, -1, t_, p_) := ((1/2)*Sqrt(3/(2*Pi))*Sin(t))/E^(I*p), 
   SphericalHarmonicY(1, 1, t_, p_) := (-1/2)*E^(I*p)*Sqrt(3/(2*Pi))*Sin(t),
   SphericalHarmonicY(n_, 0, 0, p_) := Sqrt(1 + 2*n)/(2*Sqrt(Pi)),
   SphericalHarmonicY(2, -2, t_, p_) := ((1/4)*Sqrt(15/(2*Pi))*Sin(t)^2)/E^(2*I*p),
   SphericalHarmonicY(2, -1, t_, p_) := ((1/2)*Sqrt(15/(2*Pi))*Cos(t)*Sin(t))/E^(I*p);
   SphericalHarmonicY(2, 0, t_, p_) := (1/4)*Sqrt(5/Pi)*(-1 + 3*Cos(t)^2),
   SphericalHarmonicY(3, -3, t_, p_) := ((1/8)*Sqrt(35/Pi)*Sin(t)^3)/E^(3*I*p),
   SphericalHarmonicY(3, -2, t_, p_) := ((1/4)*Sqrt(105/(2*Pi))*Cos(t)*Sin(t)^2)/E^(2*I*p),
   SphericalHarmonicY(3, -1, t_, p_) := ((1/8)*Sqrt(21/Pi)*(-1 + 5*Cos(t)^2)*Sin(t))/E^(I*p),
   SphericalHarmonicY(3, 0, t_, p_) := (1/4)*Sqrt(7/Pi)*(-3*Cos(t) + 5*Cos(t)^3),
   SphericalHarmonicY(3, 1, t_, p_) := (-(1/8))*E^(I*p)*Sqrt(21/Pi)*(-1 + 5*Cos(t)^2)*Sin(t),
   SphericalHarmonicY(3, 2, t_, p_) := (1/4)*E^(2*I*p)*Sqrt(105/(2*Pi))*Cos(t)*Sin(t)^2,
   SphericalHarmonicY(3, 3, t_, p_) := (-(1/8))*E^(3*I*p)*Sqrt(35/Pi)*Sin(t)^3,
     
   SphericalHarmonicY(n_Integer, 0, t_, p_) := (Sqrt(1+2*n)*LegendreP(n,Cos(t)))/(2*Sqrt(Pi)),
   SphericalHarmonicY(n_Integer, m_Integer, t_, p_) := 0
     /; (n>=0 && m>n)
 }