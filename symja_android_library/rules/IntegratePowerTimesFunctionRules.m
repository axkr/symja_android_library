{
 List(ArcCos,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCos(m*x)+m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n)),
 List(ArcCosh,x_,n_,m_) := (x^(1+n)*(ArcCosh(m*x)+(-m*x*Sqrt(1-m^2*x^2)*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2))/((2+n)*Sqrt(-1+m*x)*Sqrt(1+m*x))))/(1+n),
 List(ArcCot,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCot(m*x)+m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n)),
 List(ArcCoth,x_,n_,m_) := (x^(1+n)*((2+n)*ArcCoth(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n)),
 List(ArcSin,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSin[m*x]-m*x*Hypergeometric2F1[1/2,1+n/2,2+n/2,m^2*x^2]))/((1+n)*(2+n)),
 List(ArcSinh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSinh(m*x)-m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n)),
 List(ArcTan,x_,n_,m_) := x^(1+n)/((1+n)*(2+n))*((2+n)*ArcTan(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2)),
 List(ArcTanh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcTanh(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n)),
 List(CubeRoot,x_,n_,m_) := (x^(1+n)*Surd(m*x,3))/(4/3+n),
 List(Surd,x_,n_,m_,p_Integer) := (x^(n+1)*Surd(m*x,p))/((p+1)/p+n) /; p>0,
 
 List(EllipticE,x_,n_,m_) := (Pi*x^(1+n)*HypergeometricPFQ({-1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n),
 List(EllipticE,x_,m_) := (2*((1+m*x)*EllipticE(m*x)+(-1+m*x)*EllipticK(m*x)))/(3*m),
 List(EllipticK,x_,n_,m_) := (Pi*x^(1+n)*HypergeometricPFQ({1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n),
 List(EllipticK,x_,m_) := (2*(EllipticE(m*x)+(-1+m*x)*EllipticK(m*x)))/m,
 
 List(Haversine,x_,m_) := x/2-Sin(m*x)/(2*m),
 List(Haversine,x_,n_,m_) := 1/4*x^n*((2*x)/(1+n)+(x*Gamma(1+n,-I*m*x))/(-I*m*x)^(1+n)+(x*Gamma(1+n,I*m*x))/(I*m*x)^(1+n)),
 List(InverseHaversine,x_,m_) := (Sqrt(-m*x*(-1+m*x))+(-1+2*m*x)*ArcSin(Sqrt(m*x)))/m,
 List(InverseHaversine,x_,n_,m_) := (2*x^(1+n)*((3+2*n)*ArcSin(Sqrt(m*x))-Sqrt(m*x)*Hypergeometric2F1(1/2,3/2+n,5/2+n,m*x)))/((1+n)*(3+2*n)),

 List(InverseErf,x_,m_) := -1/(E^InverseErf(m*x)^2*m*Sqrt(Pi)),
 List(InverseErfc,x_,m_) := 1/(E^InverseErfc(m*x)^2*m*Sqrt(Pi)),
 
 List(LogisticSigmoid,x_,m_) := -Log(1-LogisticSigmoid(m*x))/m
}