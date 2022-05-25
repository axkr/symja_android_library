{
 BesselJ(Undefined, y_):=Undefined,
 BesselJ(x_, Undefined):=Undefined, 
 BesselJ(-1/2,z_):=(Sqrt(2/Pi)*Cos(z))/Sqrt(z), 
 BesselJ(1/2,z_):=(Sqrt(2/Pi)*Sin(z))/Sqrt(z),
 BesselJ(x_, -Infinity)=0,
 BesselJ(x_, Infinity)=0 
}