{
 BesselK(Undefined, y_):=Undefined,
 BesselK(x_, Undefined):=Undefined, 
 BesselK(-1/2,z_):=Sqrt(Pi/2)/(E^z*Sqrt(z)), 
 BesselK(1/2,z_):=Sqrt(Pi/2)/(E^z*Sqrt(z)),
 
 BesselK(x_/;(x>0 && IntegerQ(2*x)),z_/;(z!=0&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k= x-1/2},f=Exp(-u)/u;While(k>0, k=k-1;f = (D(f, u)/u)); (Sqrt(Pi/2*z) * ((-u)^(x-1/2)*f))/.u->z),
   
 BesselK(x_/;(x<0 && IntegerQ(2*x)),z_/;(z!=0&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k=-x-1/2},f=Exp(-u)/u;While(k>0, k=k-1;f = (D(f, u)/u)); (Sqrt(Pi/2*z) * ((-u)^(-x-1/2)*f))/.u->z)
  
}