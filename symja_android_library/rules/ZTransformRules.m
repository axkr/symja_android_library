{
 
  ZTransform(a_^n_ ,n_?NotListQ,z_?NotListQ):=z/(z-a)
    /; FreeQ(a,n)&&FreeQ(a,z),
  ZTransform(a_^(f_*n_) ,n_?NotListQ,z_?NotListQ):=z/(z-a^f)
    /; FreeQ({a,f},n)&&FreeQ({a,f},z),
  ZTransform(f_./(n_+g_),n_?NotListQ,z_?NotListQ):=f*HurwitzLerchPhi(1/z, 1, g) 
    /; FreeQ({f,g},n)&&FreeQ({f,g},z),
  ZTransform((n_!)^(-1),n_?NotListQ,z_?NotListQ):=(E^(1/z))
    /; FreeQ({f,g},n)&&FreeQ({f,g},z) ,
  ZTransform(f_./(g_.*n_!),n_?NotListQ,z_?NotListQ):=(E^(1/z)*f)/g
    /; FreeQ({f,g},n)&&FreeQ({f,g},z),
  ZTransform(f_./(2*n_+1)!,n_?NotListQ,z_?NotListQ):=Sqrt(z)*f*Sinh(1/Sqrt(z))
    /; FreeQ(f,n)&&FreeQ(f,z),
  ZTransform(Cos(f_.*n_)/(n_)!,n_?NotListQ,z_?NotListQ):=E^(Cos(f)/z)*Cos(Sin(f)/z)
    /; FreeQ(f,n)&&FreeQ(f,z), 
  ZTransform(Sin(f_.*n_)/(n_)!,n_?NotListQ,z_?NotListQ):=E^(Cos(f)/z)*Sin(Sin(f)/z) 
    /; FreeQ(f,n)&&FreeQ(f,z),
  ZTransform(Cos(f_.*(n_+1))/(n_+1),n_?NotListQ,z_?NotListQ):=(1/2)*z*(-Log((E^(I*f)-1/z)/E^(I*f))-Log(1-E^(I*f)/z))  
    /; FreeQ(f,n)&&FreeQ(f,z),
  ZTransform(Sin(f_.*(n_+1))/(n_+1),n_?NotListQ,z_?NotListQ):=(-(1/2))*I*z*(Log((E^(I*f)-1/z)/E^(I*f))-Log(1 - E^(I*f)/z)) 
    /; FreeQ(f,n)&&FreeQ(f,z),

  ZTransform(Cos(f_.*n_+t_.),n_?NotListQ,z_?NotListQ):=(z*(-Cos(f-t)+z*Cos(t)))/(1+z^2-2*z*Cos(f))    
    /; FreeQ({f,t},n)&&FreeQ({f,t},z),
  ZTransform(Cosh(f_.*n_+t_.),n_?NotListQ,z_?NotListQ):=(z*(-Cosh(f-t) + z*Cosh(t)))/(1 + z^2 - 2*z*Cosh(f))     
    /; FreeQ({f,t},n)&&FreeQ({f,t},z),
    
  ZTransform(Sin(f_.*n_+t_.),n_?NotListQ,z_?NotListQ):=(z*(Sin(f-t)+z*Sin(t)))/(1 +z^2-2*z*Cos(f))  
    /; FreeQ({f,t},n)&&FreeQ({f,t},z),
  ZTransform(Sinh(f_.*n_+t_.),n_?NotListQ,z_?NotListQ):=(z*(Sinh(f-t)+ z*Sinh(t)))/(1+z^2-2*z*Cosh(f))  
    /; FreeQ({f,t},n)&&FreeQ({f,t},z)  
   
}