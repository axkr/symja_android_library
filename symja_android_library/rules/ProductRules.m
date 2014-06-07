{
 Product(x_Symbol,{x_,0,m_}):=0,
 Product(x_Symbol,{x_,0,m_, s_}):=0,
 Product(x_Symbol,{x_,1,m_}) := m! /; FreeQ(x,m)
}