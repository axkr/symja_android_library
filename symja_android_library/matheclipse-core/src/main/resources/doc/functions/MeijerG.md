## MeijerG

```
MeijerG({{a(1),a(2),...,a(n)},{a(n+1),a(n+2),...,a(p)}},{{b(1),b(2),...,b(m)},{b(m+1),b(m+2),...,b(q)}}, z)
```

> return the `MeijerG` function. The G-function was introduced by Cornelis Simon Meijer (1936) as a very general function intended to include most of the known special functions as particular cases. 

```
MeijerG({{a(1),a(2),...,a(n)},{a(n+1),a(n+2),...,a(p)}},{{b(1),b(2),...,b(m)},{b(m+1),b(m+2),...,b(q)}}, z, r)
```

> return the generalized `MeijerG` function, whose integral has `z^(-s/r)` in place of `z^(-s)`.

See: 
* [Wikipedia - Meijer G-function](https://en.wikipedia.org/wiki/Meijer_G-function) 

### Notes

* The G-function evaluates automatically where Slater's theorem gives one term, or a sum of the functions `Hypergeometric0F1`, `Hypergeometric1F1` and `Hypergeometric2F1`, and to named functions like `BesselK` or `HypergeometricU`. 
* `FunctionExpand` also returns sums of general hypergeometric functions, the logarithms and `EulerGamma` terms where parameters differ by integers, and for `p == q` a `Piecewise` of the expansions inside and outside of the unit circle.
* Equal parameters of the first and the fourth list, or of the third and the second list, cancel. If a parameter of the first list minus one of the third list is a positive integer, the G-function doesn't exist.
* For numbers as arguments the function is evaluated at machine precision or at arbitrary precision. For `p == q` the series in `z` is taken inside the unit circle and on it, the series in `1/z` outside of it.

### Examples

```
>> MeijerG({{}, {}}, {{b1}, {}},z)
z^b1/E^z

>> MeijerG({{},{a2}},{{b1},{b2}},z)
(z^b1*Hypergeometric1F1Regularized(1-a2+b1,1+b1-b2,z))/Gamma(a2-b1)

>> MeijerG({{},{}},{{0,1/2},{}},-x/2,1/2)
E^x*Sqrt(Pi)

>> FunctionExpand(MeijerG({{},{3,1}},{{0,0},{}},z))
Piecewise({{-3/4+z-z^2/4-Log(z)/2,Abs(z)<=1}},0)

>> N(MeijerG({{1/2},{1/4}},{{0},{3/4}},1/2),30)
0.343518538419324924504065102099

>> N(MeijerG({{1/2},{1/4}},{{0},{3/4}},2),30)
-0.378541903398408788301500239877

>> D(MeijerG({{a1},{a2}},{{b1},{b2}},z),z)
MeijerG({{-1,-1+a1},{-1+a2}},{{-1+b1},{0,-1+b2}},z)
```

### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of MeijerG](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/MeijerG.java#L15) 
