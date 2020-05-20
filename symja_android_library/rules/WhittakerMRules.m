{  
WhittakerM(n_,m_,0) := 0 
 /; Re(m)>(-1/2),
 WhittakerM(n_,m_,0) := ComplexInfinity 
 /; Re(m)<(-1/2) 

}