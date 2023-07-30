{ 
Hypergeometric2F1Regularized(a_, b_, c_, 0) := 1/Gamma(c), 
Hypergeometric2F1Regularized(a_, b_, b_, x_) := 1/((1-x)^a*Gamma(a))
 
}