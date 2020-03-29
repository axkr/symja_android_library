{  
 Hypergeometric2F1(1, 2, 3/2, z_) := (Sqrt(z)*Sqrt(1 - z) + ArcSin(Sqrt(z))) / (2*(1 - z)^(3/2)*Sqrt(z)),
 Hypergeometric2F1(m_Integer, n_IntegerQ, 2, 1) := CatalanNumber(-n) /; n<0 && m == (n+1)
}