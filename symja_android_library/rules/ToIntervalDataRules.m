 {  
  ToIntervalData(HoldPattern(Inequality(a_, s : (Less | LessEqual), x_, t : (Less | LessEqual), b_)), x_) := 
    IntervalData({a, s, t, b}),

 ToIntervalData(Less(a_, x_), x_) := IntervalData({a, Less, Less, Infinity});
 ToIntervalData(LessEqual(a_, x_), x_) := IntervalData({a, LessEqual, Less, Infinity}),
 ToIntervalData(Greater(x_, a_), x_) := IntervalData({a, Less, Less, Infinity}),
 ToIntervalData(GreaterEqual(x_, a_), x_) := IntervalData({a, LessEqual, Less, Infinity}),

 ToIntervalData(Less(x_, b_), x_) :=  IntervalData({-Infinity, Less, Less, b}),
 ToIntervalData(LessEqual(x_, b_), x_) := IntervalData({-Infinity, Less, LessEqual, b}),
 ToIntervalData(Greater(b_, x_), x_) := IntervalData({-Infinity, Less, Less, b}),
 ToIntervalData(GreaterEqual(b_, x_), x_) := IntervalData({-Infinity, Less, LessEqual, b}),

 ToIntervalData(Reals) = IntervalData({-Infinity, Less, Less, Infinity})
 }