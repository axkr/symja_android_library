{
 BesselI(Undefined, y_):=Undefined,
 BesselI(x_, Undefined):=Undefined, 
 BesselI(-1/2,z_):=(Sqrt(2/Pi)*Cosh(z))/Sqrt(z), 
 BesselI(1/2,z_):=(Sqrt(2/Pi)*Sinh(z))/Sqrt(z)
}