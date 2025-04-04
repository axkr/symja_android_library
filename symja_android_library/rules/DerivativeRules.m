{ 
Derivative(1)[AiryAi] = AiryAiPrime(#) &,
Derivative(1)[AiryAiPrime] = AiryAi(#)*# &,
Derivative(1)[AiryBi] = AiryBiPrime(#) &,
Derivative(1)[AiryBiPrime] = AiryBi(#)*# &,
Derivative(1)[ArcCos] = (-1)*(1-#^2)^(-1/2) &,
Derivative(1)[ArcCosh] = (#^2-1)^(-1/2) &,
Derivative(1)[ArcCot] = (-1)*(1+#^2)^(-1) &,
Derivative(1)[ArcCoth] = (1-#^2)^(-1) &,
Derivative(1)[ArcCsc] = -1*#^(-2)*(1-#^(-2))^(-1/2) &,
Derivative(1)[ArcCsch] = -1*Abs(#)^(-1)*(1+#^2)^(-1/2) &,
Derivative(1)[ArcSin] = (1-#^2)^(-1/2) &,
Derivative(1)[ArcSinh] = (1+#^2)^(-1/2) &,
Derivative(1)[ArcTan] = (1+#^2)^(-1) &,
Derivative(1)[ArcTanh] = (1-#^2)^(-1) &,
Derivative(1)[ArcSec] = #^(-2)*(1-#^(-2))^(-1/2) &,
Derivative(1)[ArcSech] = -1*#^(-1)*(1-#^2)^(-1/2) &,
Derivative(1)[CatalanNumber] = CatalanNumber(#)*(Log(4)+PolyGamma(1/2+#)-PolyGamma(2+#)) &,
Derivative(1)[Ceiling] = Piecewise({{0, #<Ceiling(#)}}, Indeterminate) &,
Derivative(1)[EllipticE] = (EllipticE(#)-EllipticK(#))/(2*#) &,
Derivative(1)[EllipticK] = (EllipticE(#)-EllipticK(#)*(1-#))/(2*(1-#)*#) &,
Derivative(1)[Erf] = (2*E^(-#^(2))/Sqrt(Pi)) &,
Derivative(1)[Erfc] = (-2*E^(-#^(2))/Sqrt(Pi)) &,
Derivative(1)[Erfi] = (2*E^(#^(2))/Sqrt(Pi)) &,
Derivative(1)[ExpIntegralEi] = E^#/# &,
Derivative(1)[Factorial] = Gamma(1+#)*PolyGamma(0,1+#) &,
Derivative(1)[Factorial2] = 1/2*Factorial2(#)*(Log(2)  + PolyGamma(0,1+#/2) + 1/2*Pi*Log(2/Pi)*Sin(Pi*#)) &,
Derivative(1)[Floor] = Piecewise({{0, #>Floor(#)}}, Indeterminate) &,
Derivative(1)[FractionalPart] = 1 &,
Derivative(1)[FresnelC] = Cos((Pi*#^2)/2) &,
Derivative(1)[FresnelS] = Sin((Pi*#^2)/2) &,
Derivative(1)[Gamma] = Gamma(#)*PolyGamma(0,#) &,
Derivative(1)[Gudermannian] = Sech(#) &,
Derivative(1)[HarmonicNumber] = (Pi^2)/6 - HarmonicNumber(#, 2) &,
Derivative(1)[Haversine] = (1/2)*Sin(#) &,
Derivative(1)[HeavisideLambda] = -HeavisidePi(1/2-#)+HeavisidePi(1/2+#) &,  
Derivative(1)[HeavisidePi] = -2*DiracDelta(-1+2*#)+2*DiracDelta(1+2*#) &,  
Derivative(1)[HeavisideTheta] = DiracDelta(#) &,
Derivative(1)[HyperFactorial] = Hyperfactorial(#)*(#+(1/2)*(1-Log(2*Pi)) + LogGamma(1+#)) &,
Derivative(1)[Identity] = 1 &,
Derivative(1)[IntegerPart] = 0 &,
Derivative(1)[InverseErf] = (1/2*Sqrt(Pi)*E^(InverseErf(x)^2)) &,
Derivative(1)[InverseErfc] = (-(1/2))*E^InverseErfc(#)^2*Sqrt(Pi) &,
Derivative(1)[InverseHaversine] = 1/Sqrt((1 - #)*#) &,
Derivative(1)[InverseGudermannian] = Sec(#) &,
Derivative(1)[KelvinBei] = ((2*KelvinBei(1, #) - 2*KelvinBer(1, #))/(2*Sqrt(2))) &,
Derivative(1)[KelvinBer] = ((2*KelvinBei(1, #) + 2*KelvinBer(1, #))/(2*Sqrt(2))) &,
Derivative(1)[Log] = #^(-1) &,
Derivative(1)[Log10] = 1/(Log(10)*#) &,
Derivative(1)[Log2] = 1/(Log(2)*#) &,
Derivative(1)[LogGamma] = PolyGamma(0,#) &,
Derivative(1)[LogisticSigmoid] = LogisticSigmoid(#)*(1-LogisticSigmoid(#)) &,
Derivative(1)[PolyGamma] = PolyGamma(1,#) &, 
Derivative(0,n_)[PolyGamma] := PolyGamma(n+#, #2) &
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n),
    
Derivative(1)[ProductLog] = ProductLog(#)/((1 + ProductLog(#))*#) &,
Derivative(1)[Cot] = (-1)*Csc(#)^2 &,
Derivative(1)[Coth] = (-1)*Sinh(#)^(-2) &,
Derivative(1)[Cos] = (-1)*Sin(#) &,
Derivative(1)[Cosh] = Sinh(#) &,
Derivative(1)[Csc] = (-1)*Cot(#)*Csc(#) &,
Derivative(1)[Csch] = (-1)*Coth(#)*Csch(#) &,
Derivative(1)[RealAbs] = #/RealAbs(#) &,
Derivative(1)[RealSign] = Piecewise({{0, #!=0}}, Indeterminate) &,
Derivative(1)[Round] = Piecewise({{0, NotElement(-(1/2)+Re(#), Integers) && NotElement(-(1/2)+Im(#), Integers)}}, Indeterminate) &,
Derivative(1)[Sin] = Cos(#) &,
Derivative(1)[Sinc] = (Cos(#)/#-(Sin(#)/#^2)) &,
Derivative(1)[Sinh] = Cosh(#) &,
Derivative(1)[Tan] = Sec(#)^2 &,
Derivative(1)[Tanh] = Sech(#)^(2) &,
Derivative(1)[Sec] = Sec(#)*Tan(#) &,
Derivative(1)[Sech] = (-1)*Tanh(#)*Sech(#) &,

Derivative(1)[CosIntegral] = Cos(#)/# &,
Derivative(1)[CoshIntegral] = Cosh(#)/# &,
Derivative(1)[LogIntegral] = 1/Log(#) &,
Derivative(1)[SinIntegral] = Sinc(#) &,
Derivative(1)[SinhIntegral] = Sinh(#)/# &,
Derivative(1)[UnitStep] = Piecewise({{Indeterminate, #==0}},0) &,

Derivative(n_)[Cos] := With({t=Cos(n/2*Pi+#)}, t &  
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n)), 
Derivative(n_)[Sin] := With({t=Sin(n/2*Pi+#)}, t &
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n)), 
Derivative(n_)[Cosh] := With({t=(-I)^n*Cos((n*Pi)/2 - I*#)}, t &
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n)), 
Derivative(n_)[Sinh] := With({t=I*(-I)^n*Sin((n*Pi)/2 - I*#)}, t &
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n)), 
    
Derivative(n_)[LogisticSigmoid] := KroneckerDelta(n)*LogisticSigmoid(#)-(1-KroneckerDelta(n))*PolyLog(-n,-E^#) &
    /; (IntegerQ(n) && n >= 0)||SymbolQ(n),
Derivative(n_)[Exp] = E^# &,
    
Derivative(1,0)[Binomial] = Binomial(#,#2)*(PolyGamma(0,1+#)-PolyGamma(0,1+#-#2))&,
Derivative(0,1)[Binomial] = Binomial(#,#2)*(PolyGamma(0,1+#-#2)-PolyGamma(0,1+#2))&,
Derivative(1,1)[Binomial] = Binomial(#,#2)*(PolyGamma(0,1+#)-PolyGamma(0,1+#-#2))*(PolyGamma(0,1+#-#2)-PolyGamma(0,1+#2))+Binomial(#,#2)*PolyGamma(1,1+#-#2)&,

Derivative(0,1)[BesselJ] = 1/2*(BesselJ(-1+#,#2)-BesselJ(1+#,#2)) &,
Derivative(0,1)[BesselY] = 1/2*(BesselY(-1+#,#2)-BesselY(1+#,#2)) &,
Derivative(0,1)[BesselI] = ((BesselI(-1+#, #2) + BesselI(1+#, #2))/2) &,
Derivative(0,1)[BesselK] = ((-BesselK(-1+#, #2) - BesselK(1+#, #2))/2) &,

Derivative(1,0)[CarlsonRC] = Piecewise({{(-CarlsonRC(#,#2)+1/Sqrt(#))/(2*(#-#2)), #!=#2},{-(1/(6*#^(3/2))), #==#2&&(Im(#2)!=0||Re(#2)>0)}}, ComplexInfinity) &,
Derivative(0,1)[CarlsonRC] = Piecewise({{(CarlsonRC(#,#2) - Sqrt(#)/#2)/(2*(#-#2)), #!=#2}, {-(1/(3*#^(3/2))), #==#2 && (Im(#2)!= 0||Re(#2)> 0)}},ComplexInfinity) &,

Derivative(1,0,0)[CarlsonRF] = -(1/6)*CarlsonRD(#2,#3,#) &,
Derivative(0,1,0)[CarlsonRF] = -(1/6)*CarlsonRD(#,#3,#2) &,
Derivative(0,0,1)[CarlsonRF] = -(1/6)*CarlsonRD(#,#2,#3) &,

Derivative(1,0,0)[CarlsonRG] = ((1/4)*CarlsonRF(#,#2,#3) - (1/12)*CarlsonRD(#2,#3,#)*#) &,
Derivative(0,1,0)[CarlsonRG] = ((1/4)*CarlsonRF(#,#2,#3) - (1/12)*CarlsonRD(#,#3,#2)*#2) &,
Derivative(0,0,1)[CarlsonRG] = ((1/4)*CarlsonRF(#,#2,#3) - (1/12)*CarlsonRD(#,#2,#3)*#3) &,

Derivative(0,1)[Erf] = 2/(E^(#2^2)*Sqrt(Pi))&,
Derivative(1,0)[Erf] = -2/(E^(#^2)*Sqrt(Pi))&,
Derivative(0,1)[InverseErf] = 1/2*E^InverseErf(#,#2)^2*Sqrt(Pi)&,
Derivative(1,0)[InverseErf] = E^(InverseErf[#,#2]^2-#^2)&,

Derivative(0,1)[Gamma] = (-E^(-#2))*#2^(-1+#) &,
Derivative(1,0)[Gamma] = Gamma(#, #2)*Log(#2)+MeijerG({{},{1, 1}},{{0, 0, #},{}},#2) &,
Derivative(0,0,1)[Gamma] = #3^(-1+#)/E^#3 &,
Derivative(0,1,0)[Gamma] = (-E^(-#2))*#2^(-1+#) &,
Derivative(1,0,0)[Gamma] = Gamma(#,#2)*Log(#2)-Gamma(#,#3)*Log(#3)+MeijerG({{},{1, 1}},{{0,0,#},{}},#2)-MeijerG({{},{1, 1}},{{0,0,#},{}},#3) &,
Derivative(1,0)[HarmonicNumber] = #2*(Zeta(#2+1) - HarmonicNumber(#,#2+1)) &,
Derivative(0,1)[HankelH1] = ((HankelH1(-1+#, #2) - HankelH1(1 + #, #2))/2) &,
Derivative(0,1)[HankelH2] = ((HankelH2(-1+#, #2) - HankelH2(1 + #, #2))/2) &,

Derivative[0,1][Hypergeometric0F1] = (Hypergeometric0F1(1+#,#2)/#) &,
Derivative[0,0,1][Hypergeometric1F1] = ((Hypergeometric1F1(1+#,1+#2,#3)*#)/#2) &,
Derivative[0,0,0,1][Hypergeometric2F1] = ((Hypergeometric2F1(1+#,1+#2,1+#3,#4)*#*#2)/#3) &,
Derivative[0,0,0,n_][Hypergeometric2F1] := ((Hypergeometric2F1(n+#,n+#2,n+#3,#4)*Pochhammer(#,n)*Pochhammer(#2,n))/Pochhammer(#3,n)) &
  /; IntegerQ(n)||!NumericQ(n),
Derivative[0,0,0,1][Hypergeometric2F1Regularized] = (Hypergeometric2F1Regularized(1+#,1+#2,1+#3,#4)*#*#2) &,
Derivative[0,0,0,n_][Hypergeometric2F1Regularized] := (Hypergeometric2F1Regularized(n+#,n+#2,n+#3,#4)*Pochhammer(#,n)*Pochhammer(#2,n)) &
  /; IntegerQ(n)||!NumericQ(n),
Derivative[0,0,1][HypergeometricU] = (-HypergeometricU(1+#,1+#2,#3)*#) &,
Derivative[0,0,n_][HypergeometricU] := ((-1)^n*HypergeometricU(n+#,n+#2,#3)*Pochhammer(#,n)) &
  /; IntegerQ(n)||!NumericQ(n),

Derivative(1,0)[Pochhammer] = (Pochhammer(#,#2)*(-PolyGamma(0, #) + PolyGamma(0, #+#2))) &,
Derivative(0,1)[Pochhammer] = (Pochhammer(#,#2)*PolyGamma(0, #+#2)) &,
Derivative(1,0)[Power] = #^(-1+#2)*#2 &,
Derivative(0,1)[Power] = Log(#)*#^#2  &,
Derivative(1,1)[Power] = #^(-1+#2)+Log(#)*#^(-1+#2)*#2 &,
Derivative(0,1)[PolyLog] = PolyLog(-1+#2,#)*(#^(-1)) &,
Derivative(0,0,1)[PolyLog] = PolyLog(-1+#,#2,#3) / #3 &,
Derivative(0,1)[ProductLog] = ProductLog(#, #2)/#2 * (1+ProductLog(#, #2)) &,

Derivative(0,1)[BernoulliB] = BernoulliB(-1+#, #2)*# &,
Derivative(1,0)[ChebyshevT] = (-ArcCos(#2))*ChebyshevU(-1+#, #2)*Sqrt(1 - #2^2) &,
Derivative(0,1)[ChebyshevT] = ChebyshevU(-1 + #, #2)*# &,
Derivative(1,0)[ChebyshevU] = (ArcCos(#2)*ChebyshevT(1+#, #2))/Sqrt(1 - #2^2) &,
Derivative(0,1)[ChebyshevU] = (ChebyshevU(-1 + #, #2)*(-1-#) + ChebyshevU(#, #2)*#*#2)/(-1 + #2^2) &,
Derivative(1,0)[GegenbauerC] = -((2*ChebyshevT(#, #2))/#^2) - (2*ArcCos(#2)*ChebyshevU(-1+#, #2)*Sqrt(1 - #2^2))/# &,
Derivative(0,1)[GegenbauerC] = 2*ChebyshevU(-1 + #, #2) &,
Derivative(0,0,1)[GegenbauerC] = 2*GegenbauerC(-1 + #, 1 + #2, #3)*#2 &,

Derivative(0,1)[HermiteH] = 2*HermiteH(-1+#,#2)*# &,

Derivative(0,0,0,1)[JacobiP] =  1/2*JacobiP(-1+#,1+#2,1+#3,#4)*(1+#+#2+#3) &,

Derivative(0,1)[LaguerreL] = -LaguerreL(-1 + #, 1, #2) &, 
Derivative(0,0,1)[LaguerreL] = -LaguerreL(-1 + #, 1 + #2, #3) &,
Derivative(0,1)[LegendreP] = (((-1 - #)*x*LegendreP(#, #2) + (1+#)*LegendreP(1+#, #2))/(-1 + #2^2)) &,
Derivative(0,0,1)[LegendreP] = ((LegendreP(1+#, #2, #3)*(1+#-#2) + LegendreP(#, #2, #3)*(-1-#)*#3)/(-1 + #3^2)) &,
Derivative(0,1)[LegendreQ] = ((LegendreQ(1+#, #2)*(1+#) + LegendreQ(#, #2)*(-1-#)*#2)/(-1 + #2^2)) &,
Derivative(0,0,1)[LegendreQ] = ((LegendreQ(1+#, #2, #3)*(1 + #-#2) + LegendreQ(#, #2, #3)*(-1-#)*#3)/(-1 + #3^2)) &,

Derivative(0,0,1,0)[SphericalHarmonicY] = (Cot(#3)*#2*SphericalHarmonicY(#, #2, #3, #4) + (Sqrt(Gamma(1+#-#2))*Sqrt(Gamma(2+#+#2))*SphericalHarmonicY(#, 1 + #2, #3, #4))/(E^(I*#4)*Sqrt(Gamma(#-#2))*Sqrt(Gamma(1+# + #2)))) &,
Derivative(0,0,0,1)[SphericalHarmonicY] = (I*#2*SphericalHarmonicY[#, #2, #3, #4]) &,
 
Derivative(0,1)[StruveH] = (1/2)*(StruveH(#-1, #2) - StruveH(#+1, #2) + (#2/2)^#/(Sqrt(Pi)*Gamma(#+3/2))) &,
Derivative(0,1)[StruveL] = (1/2)*(StruveL(#-1, #2) + StruveL(#+1, #2) + (#2/2)^#/(Sqrt(Pi)*Gamma(#+3/2))) &,

Derivative(1)[Zeta][0] = (-1/2)*Log(2*Pi),
Derivative(1)[Zeta][-1] = 1/12-Log(Glaisher),

Derivative(1,0,0)[LerchPhi] = (LerchPhi(1,-1+#2,#3)-LerchPhi(#,#2,#3)*#3)/# &,
Derivative(0,0,1)[LerchPhi] = (-LerchPhi(#, 1 + #2, #3))*#2 &  

}