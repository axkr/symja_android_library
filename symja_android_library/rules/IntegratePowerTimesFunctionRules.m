{
 List(ArcCos,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCos(m*x)+m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n)),
 List(ArcCosh,x_,n_,m_) := (x^(1+n)*(ArcCosh(m*x)+(-m*x*Sqrt(1-m^2*x^2)*Hypergeometric2F1(1/2,1+n/2,2+n/2,m^2*x^2))/((2+n)*Sqrt(-1+m*x)*Sqrt(1+m*x))))/(1+n),
 List(ArcCot,x_,n_,m_) :=  (x^(1+n)*((2+n)*ArcCot(m*x)+m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n)),
 List(ArcCoth,x_,n_,m_) := (x^(1+n)*((2+n)*ArcCoth(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n)),
 List(ArcSin,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSin[m*x]-m*x*Hypergeometric2F1[1/2,1+n/2,2+n/2,m^2*x^2]))/((1+n)*(2+n)),
 List(ArcSinh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcSinh(m*x)-m*x*Hypergeometric2F1(1/2,1+n/2,2+n/2,-m^2*x^2)))/((1+n)*(2+n)),
 List(ArcTan,x_,n_,m_) := x^(1+n)/((1+n)*(2+n))*((2+n)*ArcTan(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,-m^2*x^2)),
 List(ArcTanh,x_,n_,m_) := (x^(1+n)*((2+n)*ArcTanh(m*x)-m*x*Hypergeometric2F1(1,1+n/2,2+n/2,m^2*x^2)))/((1+n)*(2+n))
}