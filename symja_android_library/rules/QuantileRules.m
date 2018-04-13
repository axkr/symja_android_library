{
Quantile(NormalDistribution(m_, s_)) :=
  (ConditionalExpression(m - Sqrt(2)*s*InverseErfc(2*#1), 0 <= #1 <= 1) &),
Quantile(ExponentialDistribution(n_)) :=
  (ConditionalExpression(Piecewise({{-(Log(1 - #)/n), # < 1}}, Infinity), 0 <= # <= 1)&),
Quantile(FrechetDistribution(n_, m_)) :=
  (ConditionalExpression(Piecewise({{m/(-Log(#))^n^(-1), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&)
}