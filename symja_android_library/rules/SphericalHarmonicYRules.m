 {  
   SphericalHarmonicY(0, 0, t_, p_) = 1/(2*Sqrt(Pi)),
   SphericalHarmonicY(1, 1, t_, p_) := (-1/2)*E^(I*p)*Sqrt(3/(2*Pi))*Sin(t),
   SphericalHarmonicY(n_, 0, 0, p_) := Sqrt(1 + 2*n)/(2*Sqrt(Pi)),
   SphericalHarmonicY(n_Integer, 0, t_, p_) := (Sqrt(1+2*n)*LegendreP(n,Cos(t)))/(2*Sqrt(Pi)),
   SphericalHarmonicY(n_Integer, m_Integer, t_, p_) := 0
     /; (n>=0 && m>n),
   SphericalHarmonicY(n_, m_, t_, p_) := 0
     /; (m==(-n-1))
 }