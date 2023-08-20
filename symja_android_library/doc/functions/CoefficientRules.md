## CoefficientRules

```
CoefficientRules(polynomial, list-of-variables)
```

> get the list of coefficient rules of a  `polynomial`.
 

See:  
* [Wikipedia - Coefficient](http://en.wikipedia.org/wiki/Coefficient)

### Examples

```
CoefficientRules(x^3+3*x^2*y+3*x*y^2+y^3, {x,y}) 
>> {{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1} 
```

Non integer or negative exponents returns only one rule:

```
>> CoefficientRules(7*y^w, {y,z}) 
{{0,0}->7*y^w} 
 
>> CoefficientRules(c*x^(-2)+a+b*x,x) 
{{0}->a+c/x^2+b*x}
```

### Related terms

[Coefficient](Coefficient.md), [CoefficientList](CoefficientList.md), [Exponent](Exponent.md), [MonomialList](MonomialList.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CoefficientRules](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PolynomialFunctions.java#L317) 
