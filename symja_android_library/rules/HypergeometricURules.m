{  
HypergeometricU(1,m_,z_) := E^z*z^(1-m)*Gamma(-1+m, z),
HypergeometricU(1/2,1,z_) := (E^(z/2)*BesselK(0, z/2))/Sqrt(Pi),
HypergeometricU(n_Integer, b_, z_) := (-1/Pochhammer(2-b, n-1))*(-Gamma(b - 1, z)*z^(1 - b)*E^z*LaguerreL(n-1, 1-b, -z)+Sum((1/k)*LaguerreL(n-k-1, -b+k+1, -z)*LaguerreL(k-1, b-k-1, z), {k, 1, n - 1}))
  /; n>0
}