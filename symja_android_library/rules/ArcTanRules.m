{ 
 ArcTan(Undefined)=Undefined,
 ArcTan(0)=0, 
 ArcTan(0,0)=Indeterminate, 
 ArcTan(2-Sqrt(3))=1/12*Pi,
 ArcTan(Sqrt(3)-2)=-1/12*Pi,
 ArcTan(Sqrt(2)-1)=1/8*Pi, 
 ArcTan(1-Sqrt(2))=-1/8*Pi, 
 ArcTan(1/Sqrt(3))=1/6*Pi,   
 ArcTan(Sqrt(5-2*Sqrt(5)))=1/5*Pi, 
 ArcTan(1)=1/4*Pi,
 ArcTan(1,1)=1/4*Pi,
 ArcTan(-1,-1)=-3/4*Pi, 
 ArcTan(Sqrt(3))=1/3*Pi,  
 ArcTan(1+Sqrt(2))=3/8*Pi, 
 ArcTan(2+Sqrt(3))=5/12*Pi,
 ArcTan(Sqrt(5-2*Sqrt(5)))=1/5*Pi,
 ArcTan(Sqrt(5+2*Sqrt(5)))=2/5*Pi,
 
 ArcTan(I)=I*Infinity,

 
 ArcTan(Undefined, y_):=Undefined,
 ArcTan(x_, Undefined):=Undefined,
 ArcTan(Infinity, y_):=0, 
 ArcTan(Infinity)=Pi/2,
 ArcTan(-Infinity)=-Pi/2,
 ArcTan(I*Infinity)=Pi/2,
 ArcTan(-I*Infinity)=-Pi/2,
 ArcTan(ComplexInfinity)=Indeterminate
 (* The two 2-argument down rules that used to stand here were deleted on purpose:
      ArcTan(x_?RealValuedNumberQ, y_?RealValuedNumberQ) := If(x == 0, ...)
      ArcTan(x_?NumberQ, y_?NumberQ) := (Pi*(2*Sqrt(x^2) - x))/(4*y) /; (x^2 == y^2)
    1) They are redundant: the built-in evaluator ExpTrigsFunctions.ArcTan#e2ObjArg computes the
       same quadrant corrected angle for every pair of numbers.
    2) They are harmful: EvalEngine tries the down rules BEFORE the built-in evaluator, so these
       rules hid the built-in completely and any fix made there was dead code.
    3) They cannot be repaired in rule syntax: both decide their case with Equal, which is
       tolerance based for inexact numbers, and there is no exact zero predicate at rule level.
       For ArcTan(3.0*10^-20, 4.0*10^-20) the first rule saw x == 0 and y == 0 and answered
       Indeterminate, the second saw x^2 == y^2 and answered 3/16*Pi - although the point
       (3*10^-20, 4*10^-20) has the very same direction as the point (3, 4).
    See ExpTrigsFunctionsTest#testArcTanTwoArgumentsTinyValues. *)
 }