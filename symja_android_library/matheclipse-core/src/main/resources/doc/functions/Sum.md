## Sum

```
Sum(expr, {i, imin, imax})
```

> evaluates the discrete sum of `expr` with `i` ranging from `imin` to `imax`.

```
Sum(expr, {i, imax})
```

> same as `Sum(expr, {i, 1, imax})`. 
      
```
Sum(expr, {i, imin, imax, di})
```

> `i` ranges from `imin` to `imax` in steps of `di`.

```
Sum(expr, {i, {e1, e2, ...}})
```

> `i` takes each of the values `e1, e2, ...` in turn.

```
Sum(expr, {{e1, e2, ...}})
```

> adds one copy of `expr` for each element of the list, without assigning the elements to a
> variable; only the number of elements matters.

```
Sum(expr, imax)
```

> same as `Sum(expr, {i, 1, imax})` for an anonymous iteration variable, so `expr` is added
> `imax` times.

```
Sum(expr, i)
```

> the indefinite sum, an expression whose difference in `i` is `expr`.

```
Sum(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
```

> evaluates `expr` as a multiple sum, with `{i, ...}, {j, ...}, ...` being in outermost-to-innermost order.

```
Sum(expr, iterator, Method->"Polynomial" | "Geometric" | "Gosper")
```

> computes the sum with the named algorithm alone, instead of trying all of them in turn.

`Sum` finds a closed form for polynomial summands with Faulhaber's formula, for geometric and
hypergeometric summands with Gosper's algorithm, and for rational summands by splitting them into
partial fractions and summing each part with `PolyGamma` or `HarmonicNumber`. An iterator whose
step differs from `1` is reindexed onto a step of `1` before any of that is tried.


See
* [Wikipedia - Summation](https://en.wikipedia.org/wiki/Summation)
* [Wikipedia - Faulhaber's formula](https://en.wikipedia.org/wiki/Faulhaber%27s_formula)
* [Wikipedia - Gosper's algorithm](https://en.wikipedia.org/wiki/Gosper%27s_algorithm)
* [Wikipedia - Digamma function](https://en.wikipedia.org/wiki/Digamma_function)

### Examples

```
>> Sum(k, {k, 1, 10})    
55    
```

Double sum:   

```
>> Sum(i * j, {i, 1, 10}, {j, 1, 10})    
3025    

>> Table(Sum(i * j, {i, 0, n}, {j, 0, n}), {n, 0, 4})
{0,1,9,36,100}
```

Symbolic sums are evaluated: 

```
>> Sum(k, {k, 1, n})    
1/2*n*(1+n)

>> Sum(k, {k, n, 2*n})  
3/2*n*(1+n)

>> Sum(k, {k, I, I + 1})    
1+I*2   

>> Sum(1 / k ^ 2, {k, 1, n})    
HarmonicNumber(n, 2)    
```

Verify algebraic identities:   
 
```
>> Simplify(Sum(x ^ 2, {x, 1, y}) - y * (y + 1) * (2 * y + 1) / 6)   
0     
```
 
Infinite sums:  
  
```
>> Sum(1 / 2 ^ i, {i, 1, Infinity})    
1    
  
>> Sum(1 / k ^ 2, {k, 1, Infinity})    
Pi^2/6   

>> Sum(x^k*Sum(y^l,{l,0,4}),{k,0,4})    
1+y+y^2+y^3+y^4+x*(1+y+y^2+y^3+y^4)+(1+y+y^2+y^3+y^4)*x^2+(1+y+y^2+y^3+y^4)*x^3+(1+y+y^2+y^3+y^4)*x^4  

>> Sum(2^(-i), {i, 1, Infinity})    
1    
 
>> Sum(i / Log(i), {i, 1, Infinity})    
Sum(i/Log(i),{i,1,Infinity})    

>> Sum(Cos(Pi i), {i, 1, Infinity})    
Sum(Cos(i*Pi),{i,1,Infinity})  
```

Non-integer bounds:

```
>> Sum(i, {i, 1, 2.5})
3.0

>> Sum(i, {i, 1.1, 2.5})
3.2

>> Sum(k, {k, I, I+1.5})
1.0+2.0*I
```
     

Sum over the elements of a list:
```
>> Sum(k^2, {k, {2, 3, 5}})
38
```

A list in place of the whole iterator adds one copy of the summand per element:
```
>> Sum(x, {{a, b, c}})
3*x
```

A summand which is a rational function is split into partial fractions:
```
>> Sum(1/(i+a), {i, 1, n})
-PolyGamma(0,1+a)+PolyGamma(0,1+a+n)
```

```
>> Sum(1/(i^2+i*x), {i, 1, Infinity})
(EulerGamma+PolyGamma(0,1+x))/x
```

An iterator with a step:
```
>> Sum(k, {k, 1, n, 2})
(1+Floor(1/2*(-1+n)))^2
```

The summand may be a list:
```
>> Sum({i, i^2}, {i, 1, n})
{1/2*n*(1+n),n/6+n^2/2+n^3/3}
```


### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Sum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Sum.java) 

* [Rule definitions of Sum](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rule_sets/SumRules.m) 
