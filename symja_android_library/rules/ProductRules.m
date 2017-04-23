{
 Product(x_Symbol,{x_,0,m_}):=0,
 Product(x_Symbol,{x_,0,m_, s_}):=0,
 Product(x_Symbol,{x_,min_,max_}) := Pochhammer(min, 1+max-min) 
     /; FreeQ(x,min) && FreeQ(x,max)
}  