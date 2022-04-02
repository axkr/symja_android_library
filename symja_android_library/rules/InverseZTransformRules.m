{
  InverseZTransform(z_/(z_+a_),z_?NotListQ,n_?NotListQ):=(-a)^n
    /; FreeQ(a,n)&&FreeQ(a,z)
}