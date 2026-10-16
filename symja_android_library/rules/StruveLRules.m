{
StruveL(-1,0)=Times(2/Pi), 
StruveL(-1/2,0)=0,
StruveL(-1/2,z_):=(Sqrt(2/Pi)*Sinh(z))/Sqrt(z), 
StruveL(1/2,z_):=(Sqrt(2/Pi)*(Cosh(z)-1))/Sqrt(z),

StruveL(Undefined, y_):=Undefined,
StruveL(x_, Undefined):=Undefined 
}