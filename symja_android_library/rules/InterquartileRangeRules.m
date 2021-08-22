{ 
InterquartileRange(BernoulliDistribution(n_)) :=
  Piecewise({{1, 1/4<n<=3/4}}, 0),
InterquartileRange(ExponentialDistribution(n_)) =
  Log(3)/n,
InterquartileRange(GumbelDistribution()) :=
  Log(Log(4)/Log(4/3)),
InterquartileRange(GumbelDistribution(n_,m_)) :=
  m*Log(Log(4)/Log(4/3)),
InterquartileRange(NormalDistribution(n_,m_)) :=
  2*Sqrt(2)*m*InverseErfc(1/2)  
}