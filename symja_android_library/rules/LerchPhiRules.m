{  
  LerchPhi(-1, 1, 0) = -Log(2),
  LerchPhi(-1, 2, 1/2) = 4*Catalan,
  LerchPhi(0, 1, 0) = 0, 
  LerchPhi(1, 1, 0) = Infinity,
  LerchPhi(1, 2, 1) = 1/6*Pi^2,
  LerchPhi(2, 1, 0) = -I*Pi,
  LerchPhi(1/2-1/2*I, 2, 1) = (1+I)*PolyLog(2,1/2-1/2*I), 
  LerchPhi(z_, s_, i_Integer) := Module({n=(-i)}, (z^n)*(PolyLog(s, z)+Sum(1/(z^k*k^s), {k, 1, n})) /; i<0 ),
  LerchPhi(z_, s_, n_Integer) := (PolyLog(s,z)-Sum(z^k/k^s, {k, 1, n - 1}))/(z^n) /; n>0

  
}