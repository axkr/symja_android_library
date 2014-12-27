{

Sum(i_^(-1), {i_Symbol,1,n_Symbol}) := HarmonicNumber(n) 
  /; FreeQ(n,i),

Sum(i_^(k_IntegerQ), {i_Symbol,1,n_Symbol}) := HarmonicNumber(n,-k) 
  /; FreeQ(n,i)&&Negative(k),

Sum(i_^(k_), {i_Symbol,1,n_Symbol}) := HarmonicNumber(n,-k) 
  /; FreeQ(n,i)&&Head(k)==Rational,
 
Sum(c_^i_, {i_Symbol,1,n_Symbol}) := c*(c^n-1)*(c-1)^(-1)
  /; FreeQ(c,i)&&FreeQ(n,i),
  
Sum(Ceiling(Log(i_)), {i_Symbol,1,n_Symbol}):=
  ( Floor(Log(n))*E^(Floor(Log(n))+1)-(Floor(Log(n))+1)*E^Floor(Log(n))+1 ) * (E-1)^(-1) + (n-E^Floor(Log(n)))*Ceiling(Log(n))
  /; FreeQ(n,i),
  
Sum(Ceiling(Log(a_,i_)), {i_Symbol,1,n_Symbol}):=
  ( Floor(Log(a,n))*a^(Floor(Log(a,n))+1)-(Floor(Log(a,n))+1)*a^Floor(Log(a,n))+1 ) * (a-1)^(-1) + (n-a^Floor(Log(a,n)))*Ceiling(Log(a,n))
  /; FreeQ(a,i)&&FreeQ(n,i),

Sum(i_!, {i_Symbol,1,n_Symbol}):=
  Gamma(n+2)*(-1)^(n+1)*Subfactorial(-n-2)-Subfactorial(-1)-1 
  /; FreeQ(n,i),
  
Sum(Binomial(n_, i_), {i_Symbol,0,n_Symbol}) := 2^n
  /; FreeQ(n,i),
  
Sum(i_*Binomial(n_, i_), {i_Symbol,0,n_Symbol}) := n*2^(n-1)
  /; FreeQ(n,i)
  
}