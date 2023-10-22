{ 
Hypergeometric2F1Regularized(a_, b_, c_, 0) := 1/Gamma(c), 
Hypergeometric2F1Regularized(a_, b_, a_, z) := 1/((1 - z)^b*Gamma(a)),
Hypergeometric2F1Regularized(a_, b_, b_, z_) := 1/((1-z)^a*Gamma(a)),

Hypergeometric2F1Regularized(a_, b_, c_, z_) := 
  Module({n=b-c},(1 - z)^(-a - n)*Sum((Pochhammer(-n, k)*Pochhammer(b - a - n, k)*z^k)/(Gamma(b - n + k) * k!), {k, 0, n}) /; n >= 0&&Element(n,Integers))
}