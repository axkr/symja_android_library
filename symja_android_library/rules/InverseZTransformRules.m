{
  InverseZTransform(z_/(z_+a_),z_?NotListQ,n_?NotListQ):=(-a)^n
    /; FreeQ(a,n)&&FreeQ(a,z),

  InverseZTransform(z_/(z_+a_)^p_Integer,z_?NotListQ,n_?NotListQ):=(-a)^(-(p-1) + n)*Binomial(n, p-1) 
    /;(p>1)&&FreeQ({a,p},n)&&FreeQ({a,p},z) 
}