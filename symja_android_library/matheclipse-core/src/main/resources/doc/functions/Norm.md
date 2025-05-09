## Norm
```
Norm(v)
```
> returns the norm of the vector `v`

```
Norm(m, l)
```

> computes the `l`-norm of matrix `m` (currently only works for vectors!).   

```	
Norm(m)   
```

> computes the norm of matrix `m` (SVD)

```	
Norm(m, "Frobenius")   
```

> computes the Frobenius norm of matrix `m` 

See
* [Wikipedia - Norm (mathematics)](https://en.wikipedia.org/wiki/Norm_(mathematics))
* [Wikipedia - Matrix norm](https://en.wikipedia.org/wiki/Matrix_norm)

### Examples

```
>> Norm({1, 2, 3, 4}, 2)    
Sqrt(30)    

>> Norm({10, 100, 200}, 1)    
310    

>> Norm({a, b, c})
Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)    

>> Norm({-100, 2, 3, 4}, Infinity)    
100    

>> Norm(1 + I)    
Sqrt(2)    
```

The first Norm argument should be a number, vector, or matrix.  

```
>> Norm({1, {2, 3}})    
Norm({1, {2, 3}})    

>> Norm({x, y})    
Sqrt(Abs(x)^2+Abs(y)^2) 

>> Norm({x, y}, p)    
(Abs(x) ^ p + Abs(y) ^ p) ^ (1 / p)  

>> Norm({{1,2}, {3,4}})
5.46499
```

The second argument of Norm, 0, should be a symbol, Infinity, or an integer or real number not less than 1 for vector p-norms; or 1, 2, Infinity, or "Frobenius" for matrix norms. 
 
```
>> Norm({x, y}, 0)    
Norm({x, y}, 0)    
```

The second argument of Norm, 0.5, should be a symbol, Infinity, or an integer or real number not less than 1 for vector p-norms; or 1, 2, Infinity, or "Frobenius" for matrix norms. 

```
>> Norm({x, y}, 0.5)     
Norm({x, y}, 0.5)

>> Norm({})    
Norm({})

>> Norm(0)    
0    
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Norm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/LinearAlgebra.java#L4480) 
