{
 BesselY(Undefined, y_):=Undefined,
 BesselY(x_, Undefined):=Undefined, 
 BesselY(-1/2,z_):=(Sqrt(2/Pi)*Sin(z))/Sqrt(z), 
 BesselY(1/2,z_):=(-Sqrt(2/Pi)*Cos(z))/Sqrt(z),
 
 BesselY(-3/2,z_):=(Sqrt(2/Pi)*(Cos(z) - Sin(z)/z))/Sqrt(z),
 BesselY(3/2,z_):=(Sqrt(2/Pi)*(-(Cos(z)/z) - Sin(z)))/Sqrt(z),
 
 BesselY(-5/2,z_):=(Sqrt(2/Pi)*(-((3*Cos(z))/z) - Sin(z) + (3*Sin(z))/z^2))/Sqrt(z),
 BesselY(5/2,z_):=(Sqrt(2/Pi)*(Cos(z) - (3*Cos(z))/z^2 - (3*Sin(z))/z))/Sqrt(z),
 
  
 BesselY(x_/;(x>0 && FractionalPart(x)==0.5 && !IntegerQ(x)),z_/;(!PossibleZeroQ(z)&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k= x-1/2},f=Cos(u)/u;While(k>0, k=k-1;f = (D(f, u)/u)); (-Sqrt(2/Pi*z) * ((-u)^(x-1/2)*f))/.u->z),
   
 BesselY(x_/;(x<0 && FractionalPart(x)==-0.5 && !IntegerQ(x)),z_/;(!PossibleZeroQ(z)&&FreeQ(z,DirectedInfinity))):=
   Module({u,f,k=-x-1/2},f=Sin(u)/u;While(k>0, k=k-1;f = (D(f, u)/u)); (Sqrt(2/Pi*z) * ((-u)^(-x-1/2)*f))/.u->z)
 
}