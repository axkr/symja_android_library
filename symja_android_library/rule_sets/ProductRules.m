{
{
 Product(x_Symbol,{x_,0,m_}):=0,
 Product(x_Symbol,{x_,0,m_, s_}):=0 
     /; 0<m,
 Product(x_Symbol,{x_,m_,n_}) := Pochhammer(m, 1+n-m) 
     /; FreeQ({m,n},x),
 Product(x_Symbol,{y_,m_,n_}) := x^(1-m+n)
     /; FreeQ({y,m,n},x)
}
}  