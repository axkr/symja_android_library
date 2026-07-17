{
{

(* Sum has attribute HoldAll *)
Sum(i_, {i_Symbol,a_Integer,n_Integer}) := If(a<=n, -1/2*(-1+a-n)*(a+n), 0),
  
Sum(c_^(i_+a_.), {i_Symbol,1,Infinity}) := -(c^(1 + a)/(-1 + c)) 
  /; FreeQ({a,c},i) && (!NumericQ(c) || (c>(-1) && c<1)),
Sum(i_^k_, {i_Symbol,1,Infinity}) := Zeta(-k)
  /; FreeQ(k,i),
Sum(1/(i_^k_), {i_Symbol,1,Infinity}) := Zeta(k)
  /;  FreeQ(k,i), 
  
Sum(k_^(a_.*i_), {i_Symbol,1,Infinity}) := -(k^a)/(-1+(k^a))
  /; FreeQ(k,i) && a<0 && (k>1 || k<(-1)),
Sum(i_^k_*c_^(a_.*i_), {i_Symbol,1,Infinity}) := PolyLog(-k, c^a)
  /;  FreeQ({a,c,k},i) && (!NumericQ(c^a) || (c^a>(-1) && c^a<1)), 

Sum(x_^(2*i_+1)/(2*i_+1)!, {i_Symbol,0,Infinity}) := Sinh(x)
  /;  FreeQ(x,i),
Sum((-1)^i_*x_^(2*i_+1)/(2*i_+1)!, {i_Symbol,0,Infinity}) := Sin(x)
  /;  FreeQ(x,i),    
Sum(x_^(2*i_)/(2*i_)!, {i_Symbol,0,Infinity}) := Cosh(x)
  /;  FreeQ(x,i),
Sum((-1)^i_*x_^(2*i_)/(2*i_)!, {i_Symbol,0,Infinity}) := Cos(x)
  /;  FreeQ(x,i),    
Sum((-1)^i_*x_^(2*i_+1)/(2*i_+1), {i_Symbol,0,Infinity}) := ArcTanh(x)
  /;  FreeQ(x,i) && (!NumericQ(x) || (x>(-1) && x<1)),  
Sum(x_^i_/(i_!), {i_Symbol,1,Infinity}) := -1+E^x
  /;  FreeQ(x,i), 
Sum(a_./(i_!), {i_Symbol,1,Infinity}) := a*(-1+E)
  /;  FreeQ(a,i),
Sum(i_^(-1)*(-1)^(i_ - 1)*(x_-1)^i_, {i_Symbol, 1, Infinity}) := Log(x)
  /;  FreeQ(x,i) && (!NumericQ(x) || (x>0 && x<=2)), 
Sum( (-1)^(-i_)*(2*i_ + 1)^(-1), {i_Symbol, 1, Infinity}) := 1/4*(-4+Pi),
Sum(z_^k_*(k_+a_)^(s_),{k_,0,Infinity}) := HurwitzLerchPhi(z,-s,a)
  /; FreeQ({a,s,z},k) && (!NumericQ(z) || (z>(-1) && z<1)),

Sum(i_^k_Symbol, {i_Symbol,1,n_Symbol}) := HarmonicNumber(n, -k)
  /; FreeQ(k,i)&&FreeQ(n,i),
Sum(i_^c_, {i_Symbol,0,n_Symbol}) := 0^c + HarmonicNumber(n, -c)
  /;  FreeQ({c,n},i),
Sum(i_^c_, {i_Symbol,1,n_Symbol}) := HarmonicNumber(n, -c)
  /;  FreeQ({c,n},i),
  
Sum(a_. *(b_^i_), {i_Symbol,1,n_Symbol}) := (a*b*(-1 + b^k))/(-1 + b)
  /; FreeQ({a,b,n},i),
   
Sum(Ceiling(Log(i_)), {i_Symbol,1,n_Symbol}):=
  ( Floor(Log(n))*E^(Floor(Log(n))+1)-(Floor(Log(n))+1)*E^Floor(Log(n))+1 ) * (E-1)^(-1) + (n-E^Floor(Log(n)))*Ceiling(Log(n))
  /; FreeQ(n,i),
  
Sum(Ceiling(Log(i_)/Log(a_)), {i_Symbol,1,n_Symbol}):=
  ( Floor(Log(a,n))*a^(Floor(Log(a,n))+1)-(Floor(Log(a,n))+1)*a^Floor(Log(a,n))+1 ) * (a-1)^(-1) + (n-a^Floor(Log(a,n)))*Ceiling(Log(a,n))
  /; FreeQ(a,i)&&FreeQ(n,i),

  (*
  
Sum(1/(i_^2 + i_*x_), {i_Symbol,1,Infinity}) := (EulerGamma + PolyGamma(0, 1 + x))/x
  /;  FreeQ(x,i), 
  
  *)
Sum(1/Binomial(2*i_,i_), {i_Symbol,1,Infinity}) :=  (2*Pi*Sqrt(3)+9)/27,
Sum(1/i_*1/Binomial(2*i_,i_), {i_Symbol,1,Infinity}) :=  (Pi*Sqrt(3))/9,
Sum(1/i_^2*1/Binomial(2*i_,i_), {i_Symbol,1,Infinity}) :=  Zeta(2)/3, 
Sum((-1)^(i_-1)/i_, {i_Symbol,1,Infinity}) := Log(2), 

(* Fourier series of Sin(a*k)/k^s and Cos(a*k)/k^s for 0<a<2*Pi *)
Sum(Sin(a_.*k_)/k_, {k_Symbol,1,Infinity}) := 1/2*(Pi-a)
  /; FreeQ(a,k) && a>0 && a<2*Pi,
Sum(Cos(a_.*k_)/k_, {k_Symbol,1,Infinity}) := -Log(2*Sin(a/2))
  /; FreeQ(a,k) && a>0 && a<2*Pi,
Sum(Sin(a_.*k_)*k_^m_, {k_Symbol,1,Infinity}) := Im(PolyLog(-m,E^(I*a)))
  /; FreeQ({a,m},k) && m<-1 && a>0 && a<2*Pi,
Sum(Cos(a_.*k_)*k_^m_, {k_Symbol,1,Infinity}) := Re(PolyLog(-m,E^(I*a)))
  /; FreeQ({a,m},k) && m<-1 && a>0 && a<2*Pi,

(* Euler sums of harmonic numbers *)
Sum(HarmonicNumber(k_)*k_^m_, {k_Symbol,1,Infinity}) := (1-m/2)*Zeta(1-m)-1/2*Sum(Zeta(1+j)*Zeta(-m-j),{j,1,-m-2})
  /; FreeQ(m,k) && IntegerQ(m) && m<-1,
Sum(HarmonicNumber(k_,2)/k_^3, {k_Symbol,1,Infinity}) := 3*Zeta(2)*Zeta(3)-9/2*Zeta(5),
Sum(HarmonicNumber(k_)^2/k_^2, {k_Symbol,1,Infinity}) := 17*Pi^4/360,

Sum(z_^i_ * i_^(-n_), {i_Symbol,1,Infinity}) := PolyLog(n,z)
  /; FreeQ({z,n},i) && (!NumericQ(z) || (z>(-1) && z<1)),
  
Sum(Log(i_)*(i_^(-2)), {i_, 1, Infinity}) := (-(1/6))*Pi^2*(EulerGamma+Log(2)-12*Log(Glaisher)+Log(Pi)),
Sum(Log(i_)*(i_^n_Integer), {i_, 1, Infinity}) := -Derivative[1][Zeta][-n] 
  /; n<-2,
  
Sum(c_^(a_.*i_), {i_Symbol,0,n_}) := (-1+c^(a+a*n))/(-1+c^a) 
  /; FreeQ({a,c},i) && SpecialsFreeQ(n),
Sum(c_^(i_+m_.), {i_Symbol,0,n_}) := (c^m*(-1 + c^(1 + n)))/(-1 + c)  
  /; FreeQ({c,n,m},i) && SpecialsFreeQ(n),

Sum(i_*c_^i_, {i_Symbol,0,Infinity}) := c/(-1+c)^2
  /;  FreeQ(c,i) && (!NumericQ(c) || (c>(-1) && c<1)),  
Sum(i_*c_^i_, {i_Symbol,0,n_}) := (c + c^(1 + n)*(-1 - n + c*n))/(1 - c)^2
  /;  FreeQ({c,n},i) && !IntegerQ(n) && SpecialsFreeQ(n),
  
Sum(Binomial(n_, i_), {i_Symbol,0,n_}) := 2^n
  /; FreeQ(n,i),
  
Sum(i_*Binomial(n_, i_), {i_Symbol,0,n_}) := n*2^(n-1)
  /; FreeQ(n,i),

Sum(i_!, {i_Symbol,0,n_?(!ExactNumberQ(#)&&SpecialsFreeQ(#)&)}):= -Subfactorial(-1)-(-1)^n*Gamma(2+n)*Subfactorial(-2-n)
  /; FreeQ(n,i),
  
Sum(Cos(i_*c_.), {i_Symbol,0,n_}) := Csc(c/2)*Cos((c*n)/2)*Sin((1/2)*c*(1 + n))
  /; FreeQ({c,n},i) && SpecialsFreeQ(n),
Sum(Sin(i_*c_.), {i_Symbol,0,n_}) := Csc(c/2)*Sin((c*n)/2)*Sin((1/2)*c*(1 + n))   
  /; FreeQ({c,n},i) && SpecialsFreeQ(n)

} 
}