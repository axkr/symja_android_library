{
{

(* Sum has attribute HoldAll *)
Sum(i_, {i_Symbol,a_Integer,n_Integer}) := If(a<=n, -1/2*(-1+a-n)*(a+n), 0),
  
Sum(c_^i_, {i_Symbol,1,Infinity}) := -c*(c-1)^(-1)
  /; FreeQ(c,i) && (!NumberQ(c) || (c>(-1) && c<1)),
Sum(i_^k_, {i_Symbol,1,Infinity}) := Zeta(-k)
  /; FreeQ(k,i),
Sum(1/(i_^k_), {i_Symbol,1,Infinity}) := Zeta(k)
  /;  FreeQ(k,i), 
  
Sum(k_^(a_.*i_), {i_Symbol,1,Infinity}) := -(k^a)/(-1+(k^a))
  /; FreeQ(k,i) && a<0 && (k>1 || k<(-1)),
Sum(x_^(2*i_+1)/(2*i_+1)!, {i_Symbol,0,Infinity}) := Sinh(x)
  /;  FreeQ(x,i),
Sum((-1)^i_*x_^(2*i_+1)/(2*i_+1)!, {i_Symbol,0,Infinity}) := Sin(x)
  /;  FreeQ(x,i),    
Sum(x_^(2*i_)/(2*i_)!, {i_Symbol,0,Infinity}) := Cosh(x)
  /;  FreeQ(x,i),
Sum((-1)^i_*x_^(2*i_)/(2*i_)!, {i_Symbol,0,Infinity}) := Cos(x)
  /;  FreeQ(x,i),    
Sum((-1)^i_*x_^(2*i_+1)/(2*i_+1), {i_Symbol,0,Infinity}) := ArcTanh(x)
  /;  FreeQ(x,i),  
Sum(x_^i_/(i_!), {i_Symbol,1,Infinity}) := -1+E^x
  /;  FreeQ(x,i), 
Sum(a_./(i_!), {i_Symbol,1,Infinity}) := a*(-1+E)
  /;  FreeQ(a,i),
Sum(i_^(-1)*(-1)^(i_ - 1)*(x_-1)^i_, {i_Symbol, 1, Infinity}) := Log(x)
  /;  FreeQ(x,i), 
Sum( (-1)^(-i_)*(2*i_ + 1)^(-1), {i_Symbol, 1, Infinity}) := 1/4*(-4+Pi),
Sum(z_^k_*(k_+a_)^(s_),{k_,0,Infinity}) := HurwitzLerchPhi(z,-s,a),

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

Sum(z_^i_ * i_^(-n_), {i_Symbol,1,Infinity}) := PolyLog(n,z)
  /; FreeQ({z,n},i),
  
Sum(c_^i_, {i_Symbol,0,n_Symbol}) := (-1 + c^(1 + n))/(-1 + c)  
  /; FreeQ({c,n},i),

    
Sum(i_*c_^i_, {i_Symbol,0,n_Symbol}) := (c + c^(1 + n)*(-1 - n + c*n))/(1 - c)^2
  /;  FreeQ({c,n},i),
  
Sum(Binomial(n_, i_), {i_Symbol,0,n_Symbol}) := 2^n
  /; FreeQ(n,i),
  
Sum(i_*Binomial(n_, i_), {i_Symbol,0,n_Symbol}) := n*2^(n-1)
  /; FreeQ(n,i),

Sum(i_!, {i_Symbol,0,n_Symbol}):=
  Gamma(n+2)*(-1)^(n+1)*Subfactorial(-n-2)-Subfactorial(-1) 
  /; FreeQ(n,i) 
  
} 
}