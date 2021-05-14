{   
  JacobiSC(EllipticK(m_)/2, m_) := 1/(1-m)^(1/4),
  JacobiSC((I*EllipticK(1+l_))/2, m_) := I/Sqrt(1 + Sqrt(m))  
    /; (-l==m)
}