## Conjugate

```
Conjugate(z)
```
> returns the complex conjugate of the complex number `z`.
 
See 
* [Wikipedia - Complex number](https://en.wikipedia.org/wiki/Complex_number) 
* [Wikipedia - Complex conjugation](http://en.wikipedia.org/wiki/Complex_conjugation)

### Examples
```
>> Conjugate(3 + 4*I)
3 - 4 I
 
>> Conjugate(3)
3
 
>> Conjugate(a + b * I)
-I*Conjugate(b)+Conjugate(a)
 
>> Conjugate({{1, 2 + I*4, a + I*b}, {I}})
{{1,2-I*4,-I*Conjugate(b)+Conjugate(a)},{-I}}
 
>> {Conjugate(Pi), Conjugate(E)}
{Pi,E}
 
>> Conjugate(1.5 + 2.5*I)
1.5+I*(-2.5)
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Conjugate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Arithmetic.java#L979) 

* [Rule definitions of Conjugate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/rules/ConjugateRules.m) 
