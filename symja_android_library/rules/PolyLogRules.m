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

(* PolyLog(i_Integer, z_) for i<0 is SpecialFunctions.PolyLog#polyLogNegativeIntegerOrder: the same
   Eulerian numbers over (1-z)^(n+1), reached by their recurrence rather than by summing the
   explicit formula for each of them as an expression - which did not finish at i=-128. *)

PolyLog(Undefined, y_):=Undefined,
PolyLog(x_, Undefined):=Undefined,

PolyLog(-1,2,1/2)=Log(2)
}