{
Quantile(BernoulliDistribution(x_)) :=
  (ConditionalExpression(Piecewise({{1, # > 1 - x}}, 0), 0 <= # <= 1)&),
Quantile(CauchyDistribution(a_, b_)) :=
  (ConditionalExpression(Piecewise({{a+b*Tan((-(1/2)+#)*Pi),0<#<1},{-Infinity,#<= 0}},Infinity),0<=#<=1)&), 
Quantile(ErlangDistribution(k_, l_)) :=
  (ConditionalExpression(Piecewise({{InverseGammaRegularized(k, 0, #)/l, 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(ExponentialDistribution(n_)) :=
  (ConditionalExpression(Piecewise({{-(Log(1-#)/n), #<1}}, Infinity), 0<=#<= 1)&),
Quantile(FrechetDistribution(n_, m_)) :=
  (ConditionalExpression(Piecewise({{m/(-Log(#))^n^(-1), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(GammaDistribution(a_, b_)) :=
  (ConditionalExpression(Piecewise({{b*InverseGammaRegularized(a, 0, #), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(GammaDistribution(a_, b_, g_, d_)) :=
  (ConditionalExpression(Piecewise({{d + b*InverseGammaRegularized(a, 0, #)^(1/g), 0 < # < 1}, {d, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(GumbelDistribution( )) :=
  (ConditionalExpression(Piecewise({{Log(-Log(1 - #)), 0 < # < 1}, {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(GumbelDistribution(a_, b_)) :=
  ( ConditionalExpression(Piecewise({{a + b*Log(-Log(1 - #)), 0 < # < 1}, {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(LogNormalDistribution(m_, d_)) :=
  (ConditionalExpression(Piecewise({{E^(m - Sqrt(2)*d*InverseErfc(2*#)), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(NakagamiDistribution(m_, w_)) :=
  (ConditionalExpression(Piecewise({{Sqrt((w*InverseGammaRegularized(m, 0, #))/m), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(NormalDistribution(m_, s_)) :=
  (ConditionalExpression(m - Sqrt(2)*s*InverseErfc(2*#1), 0 <= #1 <= 1) &),
Quantile(StudentTDistribution(v_)) := 
  (ConditionalExpression(Piecewise({{(-Sqrt(v))*Sqrt(-1 + 1/InverseBetaRegularized(2*#, v/2, 1/2)), 0 < # < 1/2}, {0, # == 1/2}, 
     {Sqrt(v)*Sqrt(-1 + 1/InverseBetaRegularized(2*(1 - #), v/2, 1/2)), 1/2 < # < 1}, {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(StudentTDistribution(m_, s_, v_)) := 
  (ConditionalExpression(Piecewise({{m - s*Sqrt(v)*Sqrt(-1 + 1/InverseBetaRegularized(2*#, v/2, 1/2)), 0 < # < 1/2}, {m, # == 1/2}, 
     {m + s*Sqrt(v)*Sqrt(-1 + 1/InverseBetaRegularized(2*(1 - #), v/2, 1/2)), 1/2 < # < 1}, {-Infinity, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(WeibullDistribution(a_, b_)) := 
  (ConditionalExpression(
   Piecewise({{b*(-Log(1 - #))^(1/a), 0 < # < 1}, {0, # <= 0}}, Infinity), 0 <= # <= 1)&),
Quantile(WeibullDistribution(a_, b_, m_)) :=
  (ConditionalExpression(Piecewise({{m + b*(-Log(1 - #))^(1/a), 0 < # < 1}, {m, # <= 0}}, Infinity), 0 <= # <= 1)&)
}