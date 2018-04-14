{
CDF(GammaDistribution(a_, b_)) :=
  (Piecewise({{GammaRegularized(a, 0, #/b), # > 0}}, 0)&),
CDF(GammaDistribution(a_, b_, g_, d_)) :=
  (Piecewise({{GammaRegularized(a, 0, ((# - d)/b)^g), # > d}}, 0)&),
CDF(NormalDistribution( )) :=
  ( (1/2)*Erfc(-(#/Sqrt(2))) & ),
CDF(NormalDistribution(n_, m_)) :=
  ( (1/2)*Erfc((-# + n)/(Sqrt(2)*m)) &)
} 