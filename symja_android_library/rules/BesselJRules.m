{
 BesselJ(Undefined, y_):=Undefined,
 BesselJ(x_, Undefined):=Undefined, 
 BesselJ(-1/2,z_):=(Sqrt(2/Pi)*Cos(z))/Sqrt(z), 
 BesselJ(1/2,z_):=(Sqrt(2/Pi)*Sin(z))/Sqrt(z),
 
 BesselJ(x_/;(x>0 && FractionalPart(x)==0.5 && !IntegerQ(x)),z_/;(!PossibleZeroQ(z)&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k= x-1/2},f=Sin(u)/u;While(k>0, k=k-1;f = (D(f, u)/u)); (Sqrt(2/Pi*z) * ((-u)^(x-1/2)*f))/.u->z),
 
 BesselJ(x_/;(x<0 && FractionalPart(x)==(-0.5) && !IntegerQ(x)),z_/;(!PossibleZeroQ(z)&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k=-x-1/2},f=Cos(u)/u;While(k>0, k=k-1;f = (-D(f, u)/u)); (Sqrt(2/Pi*z) * ((-u)^(-x-1/2)*f))/.u->z),
 
 BesselJ(x_, -Infinity)=0,
 BesselJ(x_, Infinity)=0 
}