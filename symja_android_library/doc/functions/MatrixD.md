## MatrixD

```
MatrixD(f, X)
```

> gives the matrix derivative of `f` with respect to the matrix `X`.
 
See
* [Wikipedia - Matrix calculus](https://en.wikipedia.org/wiki/Matrix_calculus)
* [Internet Archive - The Matrix Cookbook](https://archive.org/details/imm3274)

### Examples

```
>> $Assumptions={Element(X, Matrices({4,4},Complexes)),Element(A, Matrices({4,4},Complexes)),Element(B, Matrices({4,4},Complexes))} 
{X∈Matrices({4,4},Complexes),A∈Matrices({4,4},Complexes),B∈Matrices({4,4},Complexes)}

>> MatrixD(Det(A.X.B), X) 
Det(A.X.B)*Inverse(Transpose(X))
```
 






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MatrixD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/MatrixD.java#L17) 

* [Rule definitions of MatrixD](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/MatrixDRules.m) 
