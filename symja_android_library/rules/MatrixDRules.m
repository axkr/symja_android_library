{
(* Rule 40 of https://archive.org/details/imm3274 *)
MatrixD(Inverse(x_),x_?(TensorRank(x)==2)) := (-Inverse(x)).MatrixD(a, x).Inverse(x),

(* Rule 44 *)
MatrixD(Transpose(x_),x_?(TensorRank(x)==2)) := Transpose(MatrixD(x, x)),

(* Rule 49 *)
MatrixD(Det(x_),x_?(TensorRank(x)==2)) := Det(x)*Transpose(Inverse(x)),

(* Rule 51 *)
MatrixD(Det(a_?(TensorRank(x)==2) . x_ . b_?(TensorRank(x)==2)), x_?(TensorRank(x)==2)) := Det(a.x.b)*Transpose(Inverse(x)),

(* Rule 52 x square, invertible *)
MatrixD(Det(Transpose(x_) . a_?(TensorRank(x)==2) . x_), x_?(TensorRank(x)==2)) := 2*Det(Transpose(x).a.x)*Inverse(Transpose(x)),

(* Rule 55 *)
MatrixD(Log(Det(Transpose(x_).x_)), x_?(TensorRank(x)==2)) := 2*Inverse(Transpose(x)),

(* Rule 57 *)
MatrixD(Log(Det(x_)), x_?(TensorRank(x)==2)) := Inverse(Transpose(x)),

(* Rule 58 *)
MatrixD(Det(MatrixPower(x_,k_)), x_?(TensorRank(x)==2)) := k*Det(MatrixPower(x,k))*Inverse(Transpose(x)),

(* Rule 60 *)
MatrixD(Inverse(x_),x_?(TensorRank(x)==2)) := (-Inverse(x)) . $SingleEntryMatrix . Inverse(x),

(* Rule 62 *)
MatrixD(Det(Inverse(x_)),x_?(TensorRank(x)==2)) := (-Det(Inverse(x)))*Transpose(Inverse(x))
  
} 