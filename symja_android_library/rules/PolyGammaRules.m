{
PolyGamma(0, -5/2) = 46/15 - EulerGamma - Log(4),
PolyGamma(0, 1) = -EulerGamma,
PolyGamma(-1, 1) = 0,
PolyGamma(-1, 0) = Infinity,
PolyGamma(1, 1) = (1/6) * Pi^2,
PolyGamma(1, 1/2) = (1/2)*Pi^2,
PolyGamma(3, 1/2) = Pi^4,

PolyGamma(0, n_Integer) := HarmonicNumber(n - 1) - EulerGamma 
  /; n>0,
PolyGamma(Undefined, y_):=Undefined,
PolyGamma(x_, Undefined):=Undefined
}