{
  ZTransform(a_^n_ ,n_?NotListQ,z_?NotListQ):=z/(z-a)
    /; FreeQ(a,n)&&FreeQ(a,z)
}