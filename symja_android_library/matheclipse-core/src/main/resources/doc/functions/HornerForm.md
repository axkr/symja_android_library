## HornerForm

```
HornerForm(polynomial)
```

> Generate the horner scheme for a univariate `polynomial`. 

```
HornerForm(polynomial, x)
```

> Generate the horner scheme for a univariate `polynomial` in `x`. 

See:
* [Wikipedia - Horner scheme](http://en.wikipedia.org/wiki/Horner_scheme)
* [Rosetta Code - Horner's rule for polynomial evaluation](https://rosettacode.org/wiki/Horner%27s_rule_for_polynomial_evaluation) 
 
### Examples
```   
>> HornerForm(3+4*x+5*x^2+33*x^6+x^8)
3+x*(4+x*(5+(33+x^2)*x^4))

>> HornerForm(a+b*x+c*x^2,x)
a+x*(b+c*x)
```
    






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of HornerForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L324) 
