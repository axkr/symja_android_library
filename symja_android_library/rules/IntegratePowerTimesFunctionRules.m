{
 f2(ArcCos,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCos(m*x)+m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
 f3(ArcCos,x_,n_,m_,p_) :=  (x^(1+n)*((1+n+p)*ArcCos(m*x^p)+m*p*x^p*Hypergeometric2F1(1/2,(1+n+p)/(2*p),(1+n+3*p)/(2*p),m^2*x^(2*p))))/((1+n)*(1+n+p))   
   /; n!=(-1)&&(n+p)!=(-1),

 f2(ArcCosh,x_,n_,m_) := (x^(1+n)*(ArcCosh(m*x)+(-m*x*Sqrt(1-m^2*x^2)*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2))/((2+n)*Sqrt(-1+m*x)*Sqrt(1+m*x))))/(1+n)
   /; n!=(-1)&&n!=(-2),
 f3(ArcCosh,x_,n_,m_,p_) := (x^(1+n)*(ArcCosh(m*x^p)+(-m*p*x^p*Sqrt(1-m^2*x^(2*p))*Hypergeometric2F1(1/2,(1+n+p)/(2*p),(1+n+3*p)/(2*p),m^2*x^(2*p)))/((1+n+p)*Sqrt(-1+m*x^p)*Sqrt(1+m*x^p))))/(1+n) 
   /; n!=(-1)&&(n+p)!=(-1),
   
 f2(ArcCot,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCot(m*x)+m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
 f2(ArcCoth,x_,n_,m_) := (x^(1+n)*((2+n)*ArcCoth(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
 
 f2(ArcSin,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSin(m*x)-m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
 f3(ArcSin,x_,n_,m_,p_) := (x^(1+n)*((1+n+p)*ArcSin(m*x^p)-m*p*x^p*Hypergeometric2F1(1/2,(1+n+p)/(2*p),(1+n+3*p)/(2*p),m^2*x^(2*p))))/((1+n)*(1+n+p)) 
    /; n!=(-1)&&(n+p)!=(-1),
    
 f2(ArcSinh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSinh(m*x)-m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
 f3(ArcSinh,x_,n_,m_,p_) := (x^(1 + n)*((1 + n + p)*ArcSinh(m*x^p)-m*p*x^p*Hypergeometric2F1(1/2, (1 + n + p)/(2*p), (1 + n + 3*p)/(2*p),(-m^2)*x^(2*p))))/((1 + n)*(1 + n + p)) 
     /; n!=(-1)&&(n+p)!=(-1),
     
 f2(ArcTan,x_,n_,m_) := x^(1+n)/((1+n)*(2+n))*((2+n)*ArcTan(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2))   
   /; n!=(-1)&&n!=(-2),
 f2(ArcTanh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcTanh(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n))
   /; n!=(-1)&&n!=(-2),
   
 f2(CubeRoot,x_,n_,m_) := (x^(1+n)*Surd(m*x,3))/(4/3+n)
   /; n!=(-4/3),
 
 f1(EllipticE,x_,m_) := (2*((1+m*x)*EllipticE(m*x)+(-1+m*x)*EllipticK(m*x)))/(3*m),
 f2(EllipticE,x_,n_,m_) := (Pi*x^(1+n)*HypergeometricPFQ({-1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n),
 f3(EllipticE,x_,n_,m_,p_) := (Pi*x^(1+n)*Gamma((1+n)/p)*HypergeometricPFQRegularized({-1/2,1/2,(1+n)/p},{1,(1+n+p)/p},m*x^p))/(2*p),

 f1(EllipticK,x_,m_) := (2*(EllipticE(m*x)+(-1+m*x)*EllipticK(m*x)))/m,
 f2(EllipticK,x_,n_,m_) := (Pi*x^(1+n)*HypergeometricPFQ({1/2,1/2,1+n},{1,2+n},m*x))/(2+2*n),
 f3(EllipticK,x_,n_,m_,p_) := (Pi*x^(1+n)*Gamma((1+n)/p)*HypergeometricPFQRegularized({1/2,1/2,(1+n)/p},{1,(1+n+p)/p},m*x^p))/(2*p),
 
 f1(Haversine,x_,m_) := x/2-Sin(m*x)/(2*m),
 f2(Haversine,x_,n_,m_) := 1/4*x^n*((2*x)/(1+n)+(x*Gamma(1+n,-I*m*x))/(-I*m*x)^(1+n)+(x*Gamma(1+n,I*m*x))/(I*m*x)^(1+n)),
 f3(Haversine,x_,n_,m_,p_) := (x^(1+n)*(2*p*(m^2*x^(2*p))^((1+n)/p)+(1+n)*(I*m*x^p)^((1+n)/p)*Gamma((1+n)/p,-I*m*x^p)+(1+n)*(-I*m*x^p)^((1+n)/p)*Gamma((1+n)/p,I*m*x^p)))/((m^2*x^(2*p))^((1+n)/p)*4*(1+n)*p),
 
 f1(InverseHaversine,x_,m_) := (Sqrt(-m*x*(-1+m*x))+(-1+2*m*x)*ArcSin(Sqrt(m*x)))/m,
 f2(InverseHaversine,x_,n_,m_) := (2*x^(1+n)*((3+2*n)*ArcSin(Sqrt(m*x))-Sqrt(m*x)*Hypergeometric2F1(1/2,3/2+n,5/2+n,m*x)))/((1+n)*(3+2*n)) ,
 f3(InverseHaversine,x_,n_,m_,p_) := (2*x^(1+n)*((2+2*n+p)*ArcSin(Sqrt(m*x^p))-p*Sqrt(m*x^p)*Hypergeometric2F1(1/2,(2+2*n+p)/(2*p),3/2+(1+n)/p,m*x^p)))/((1+n)*(2+2*n+p)),
    
 f1(InverseErf,x_,m_) := -1/(E^InverseErf(m*x)^2*m*Sqrt(Pi)),
 f1(InverseErfc,x_,m_) := 1/(E^InverseErfc(m*x)^2*m*Sqrt(Pi)),
 
 f1(LogisticSigmoid,x_,m_) := -Log(1-LogisticSigmoid(m*x))/m
   /; m!=0,
 
 f4(Surd,x_,n_,m_,p_Integer) := (x^(n+1)*Surd(m*x,p))/((p+1)/p+n) 
   /; p>0
}