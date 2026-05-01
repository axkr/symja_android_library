{ 
  InverseZTransform(z_/(z_+a_),z_?NotListQ,n_?NotListQ):=(-a)^n
    /; FreeQ(a,n)&&FreeQ(a,z),

  InverseZTransform(z_/(z_+a_)^p_Integer,z_?NotListQ,n_?NotListQ):=(-a)^(-(p-1) + n)*Binomial(n, p-1) 
    /;(p>1)&&FreeQ({a,p},n)&&FreeQ({a,p},z),

  InverseZTransform(E^(a_./z_), z_?NotListQ, n_?NotListQ) := a^n/Gamma(n+1)
    /; FreeQ(a,z) && FreeQ(a,n),

  InverseZTransform(E^(a_./z_^2), z_?NotListQ, n_?NotListQ) := (a^(n/2)*(1+(-1)^n))/(2*Gamma(1+n/2))
    /; FreeQ(a,z) && FreeQ(a,n),

  InverseZTransform(z_^m_Integer * E^(a_./z_), z_?NotListQ, n_?NotListQ) := a^(n+m)/Gamma(n+m+1)
    /; FreeQ(a,z) && FreeQ(a,n) && m < 0,

  InverseZTransform(z_^m_Integer * E^(a_./z_^2), z_?NotListQ, n_?NotListQ) := (a^((n+m)/2)*(1+(-1)^(n+m)))/(2*Gamma(1+(n+m)/2))
    /; FreeQ(a,z) && FreeQ(a,n) && m < 0
}