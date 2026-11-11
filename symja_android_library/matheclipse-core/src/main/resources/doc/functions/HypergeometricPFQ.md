## HypergeometricPFQ

```
HypergeometricPFQ({a,...}, {b,...}, c)
```

> return the `HypergeometricPFQ` function

See:
* [Wikipedia - Generalized hypergeometric function](https://en.wikipedia.org/wiki/Generalized_hypergeometric_function)

### Examples

```
>> HypergeometricPFQ({1,2,3},{4,5},0.5)
1.18987

>> HypergeometricPFQ({1,1},{2},x)
-Log(1-x)/x
```

With no parameters the series is the exponential function:

```
>> HypergeometricPFQ({}, {}, x)
E^x
```

With two upper and no lower parameters the series diverges for every `x != 0`, unless it ends. Numerically `HypergeometricPFQ({a1, a2}, {}, x)` is the function `(-1/x)^a1*HypergeometricU(a1, 1+a1-a2, -1/x)`, which the series is the asymptotic expansion of; `FunctionExpand` gives this form:

```
>> HypergeometricPFQ({1,1},{},-1.0)
0.596347

>> N(HypergeometricPFQ({2/3,5/4},{},-100), 25)
0.07095851870980052763312791

>> FunctionExpand(HypergeometricPFQ({a,b},{},x))
(-1/x)^a*HypergeometricU(a,1+a-b,-1/x)
```

Every other series with more than one upper parameter in excess of the lower ones diverges the same way. Its numerical value is the `MeijerG` function which the series is the asymptotic expansion of. On the cut `x > 0` these functions, the one with two upper parameters included, are `ComplexInfinity`:

```
>> HypergeometricPFQ({1,1,1},{},-0.5)
0.768631

>> HypergeometricPFQ({1,2,3},{4},-2.0)
0.332948

>> HypergeometricPFQ({1,1},{},1.0)
ComplexInfinity
```






### Implementation status

* &#x1F9EA; - experimental

### Github

* [Implementation of HypergeometricPFQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/HypergeometricFunctions.java#L1649) 

* [Rule definitions of HypergeometricPFQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/HypergeometricPFQRules.m) 
