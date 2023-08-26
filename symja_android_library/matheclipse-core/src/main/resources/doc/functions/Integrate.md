## Integrate  
 
```
Integrate(f, x)
```
 
> integrates `f` with respect to `x`. The result does not contain the additive integration constant.

```
Integrate(f, {x,a,b})
```
 
> computes the definite integral of `f` with respect to `x` from `a` to `b`.

See: 
- [Wikipedia: Integral](https://en.wikipedia.org/wiki/Integral)
- [Wikipedia: Antiderivative](https://en.wikipedia.org/wiki/Antiderivative)
- [Rubi: Rule-based Integration](https://rulebasedintegration.org/)

### Examples

```
>> Integrate(x^2, x)
x^3/3

>> Integrate(Tan(x) ^ 5, x)
-Log(Cos(x))-Tan(x)^2/2+Tan(x)^4/4
```

### Related terms 
[D](D.md),[DSolve](DSolve.md), [Int](Int.md), [Limit](Limit.md), [ND](ND.md), [NIntegrate](NIntegrate.md) 
 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Integrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Integrate.java#L83) 
