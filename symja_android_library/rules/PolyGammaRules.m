{
PolyGamma(0, -5/2) = 46/15 - EulerGamma - Log(4),
PolyGamma(0, 1) = -EulerGamma,
PolyGamma(-1, 1) = 0,
PolyGamma(-1, 0) = Infinity,
PolyGamma(1, 1/4) = Pi^2 + 8*Catalan,
PolyGamma(1, 3/4) = Pi^2 - 8*Catalan,
PolyGamma(2, 5/6) = 4*Sqrt(3)*Pi^3 - 182*Zeta(3),

PolyGamma(0, n_Integer) := HarmonicNumber(n - 1) - EulerGamma /; n>0
}