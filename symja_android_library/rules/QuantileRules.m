{
Quantile(NormalDistribution(m_, s_)) :=
  (ConditionalExpression(m - Sqrt(2)*s*InverseErfc(2*#1), 0 <= #1 <= 1) &)
}