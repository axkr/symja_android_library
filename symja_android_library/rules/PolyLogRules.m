{
PolyLog(2,-1)=(-1/12)*Pi^2,
PolyLog(2,1)=(1/6)*Pi^2,
PolyLog(2,1/2)=(1/12)*Pi^2-(1/2)*Log(2)^2,
PolyLog(2,2)=Pi^2/4-Pi*I*Log(2),
PolyLog(2,I)=I*Catalan-Pi^2/48,
PolyLog(2,-I)=(-I)*Catalan-Pi^2/48,
PolyLog(2,1-I)=Pi^2/16-I*Catalan-I*Pi*(Log(2)/4),
PolyLog(2,1+I)=Pi^2/16+I*Catalan+I*Pi*(Log(2)/4),
PolyLog(3,1/2)=(1/24)*(-2*Pi^2*Log(2)+4*Log(2)^3+21*Zeta(3)),

PolyLog(i_Integer, z_) := Module({n=(-i)}, Sum(Sum((-1)^(k + 1)*Binomial(n+1, k-1)*(m-k+1)^n, {k, 1, m}) * z^m, {m, 1, n}) / (1-z)^(n+1) /; i<0 ),

PolyLog(Undefined, y_):=Undefined,
PolyLog(x_, Undefined):=Undefined,

PolyLog(-1,2,1/2)=Log(2)
}