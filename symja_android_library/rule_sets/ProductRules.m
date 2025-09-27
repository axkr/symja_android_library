{
{
 Product(x_Symbol,{x_,0,m_}):=0,
 Product(x_Symbol,{x_,0,m_, s_}):=0 
     /; 0<m,
 Product(x_Symbol,{x_,1,n_}) := Factorial(n),
 Product(x_Symbol,{x_,m_,n_}) := Pochhammer(m, 1+n-m) ,
 Product(x_Symbol,{y_,m_,n_}) := x^(1-m+n)
     /; FreeQ({y,m,n},x),
     
 Product(1-4*x_^2/(1-4*i_+4*i_^2),{i_Symbol,1,Infinity}) := Cos(Pi*x)
     /; FreeQ(x,i),
 Product(1-x_^2/i_^2,{i_Symbol,1,Infinity}) := Sin(Pi*x)/(Pi*x)
     /; FreeQ(x,i)
}
}  