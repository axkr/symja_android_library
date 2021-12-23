{
 FunctionRange(LogIntegral(x_), x_, y_) := True,
 FunctionRange(ExpIntegralEi(x_), x_, y_) := True,
 FunctionRange(Re(x_), x_, y_) := True,
 FunctionRange(Re(x_), x_, y_, Complexes) := Im(y)==0,
 FunctionRange(Im(x_), x_, y_) := y==0,
 FunctionRange(Im(x_), x_, y_, Complexes) := Im(y)==0
}