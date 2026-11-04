{  
  HurwitzLerchPhi(0, 1, 1) = 1,  
  
  HurwitzLerchPhi(z_, s_, 1) := PolyLog(s,z)/z,
  HurwitzLerchPhi(z_, 0, a_) := 1/(1-z),
  HurwitzLerchPhi(0, s_, a_) := a^(-s),
  HurwitzLerchPhi(z_, 1, 1) := -Log(1-z)/z,
  HurwitzLerchPhi(-1, s_, 1) := (1-2^(1-s))*Zeta(s) /; s!=1,
  HurwitzLerchPhi(-1, s_, a_) := HurwitzZeta(s, a/2)/2^s-HurwitzZeta(s, (1+a)/2)/2^s /; s=!=1,
  HurwitzLerchPhi(0, 1, a_) := 1/a 
}