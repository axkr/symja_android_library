{
PDF(DiscreteUniformDistribution({a_, b_})) :=
  ( Piecewise({{1/(1 - a + b), a <= # <= b}}, 0) & ),
PDF(GammaDistribution(a_, b_)) :=
  ( Piecewise({{#^(-1 + a)/(b^a*E^(#/b)*Gamma(a)), # > 0}}, 0) & ),
PDF(GammaDistribution(a_, b_, g_, d_)) :=
  ( Piecewise( {{(((# - d)/b)^(-1 + a*g)*g)/(E^((# - d)/b)^g*(b*Gamma(a))), # > d}}, 0) & ),
PDF(NormalDistribution( )) :=
  ( 1/(E^(#^2/2)*Sqrt(2*Pi)) & ),
PDF(NormalDistribution(n_, m_)) :=
  ( 1/(E^((# - n)^2/(2*m^2))*(m*Sqrt(2*Pi))) & )
}