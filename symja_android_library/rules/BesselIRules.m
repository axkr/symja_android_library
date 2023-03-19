{
 BesselI(Undefined, y_):=Undefined,
 BesselI(x_, Undefined):=Undefined, 
 BesselI(-1/2,z_):=(Sqrt(2/Pi)*Cosh(z))/Sqrt(z), 
 BesselI(1/2,z_):=(Sqrt(2/Pi)*Sinh(z))/Sqrt(z),
 
 BesselI(x_/;(x>0 && IntegerQ(2*x)),z_/;(z!=0&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k= x-1/2},f=Sinh(u)/u;While(k>0, k=k-1;f = (-D(f, u)/u)); (Sqrt(2/Pi*z) * ((-u)^(x-1/2)*f))/.u->z),
   
 BesselI(x_/;(x<0 && IntegerQ(2*x)),z_/;(z!=0&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k=-x-1/2},f=Cosh(u)/u;While(k>0, k=k-1;f = (-D(f, u)/u)); (Sqrt(2/Pi*z) * ((-u)^(-x-1/2)*f))/.u->z),
 
 BesselI(x_, -(I)*Infinity)=0,
 BesselI(x_, I*Infinity)=0 
}