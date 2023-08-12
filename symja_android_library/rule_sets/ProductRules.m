{
{
 Product(x_Symbol,{x_,0,m_}):=0,
 Product(x_Symbol,{x_,0,m_, s_}):=0 
     /; 0<m,
 Product(x_Symbol,{x_,1,n_}) := Factorial(n),
 Product(x_Symbol,{x_,m_,n_}) := Pochhammer(m, 1+n-m) ,
 Product(x_Symbol,{y_,m_,n_}) := x^(1-m+n)
     /; FreeQ({y,m,n},x),

 Product(Cos(x_*2^i_), {i_Symbol, 0, l_}) := Module({k=l-1}, (Csc(x)*Sin(2^k*x))/2^k /; ( !NumericQ(l) || (IntegerQ(l)&&l>0)) && FreeQ({x,l},i) )
}
}  