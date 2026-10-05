## Integrate  
 
```
Integrate(f, x)
```
 
> integrates `f` with respect to `x`. The result does not contain the additive integration constant.

```
Integrate(f, {x,a,b})
```
 
> computes the definite integral of `f` with respect to `x` from `a` to `b`.

> Definite integrals over `{x,0,Infinity}`, `{x,-Infinity,Infinity}` or a period of a trigonometric integrand are also computed by the residue theorem, where no antiderivative exists in closed form. The conditions for convergence have to follow from the `Assumptions` option. The option `PrincipalValue -> True` asks for the Cauchy principal value.

> A definite integral which is the integral representation of a special function is returned as that function: `Beta`, `BesselK`, `AiryAi`, `HurwitzZeta`, `LerchPhi`, `Hypergeometric2F1` and the derivatives of `Gamma`. A condition for convergence which does not follow from the `Assumptions` is returned with the value in a `ConditionalExpression`.

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

>> Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/2})
ConditionalExpression(Beta(1/2*(1+a),1/2*(1+b))/2,a>-1&&b>-1)

>> Integrate(E^(-a*Cosh(x))*Cosh(k*x), {x,0,Infinity}, Assumptions->a>0)
BesselK(k,a)

>> Integrate(Cos(a*x)/Cosh(b*x)^2, {x,0,Infinity}, Assumptions->a>0&&b>0)
(a*Pi*Csch((a*Pi)/(2*b)))/(2*b^2)
```

### Related terms 
[D](D.md),[DSolve](DSolve.md), [Int](Int.md), [Limit](Limit.md), [ND](ND.md), [NIntegrate](NIntegrate.md) 
 






### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of Integrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/Integrate.java#L92) 
