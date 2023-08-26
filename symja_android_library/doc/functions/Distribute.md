## Distribute  

```
Distribute(f(x1, x2, x3,...))
```

> distributes `f` over `Plus` appearing in any of the `xi`.
 
See:  
* [Wikipedia - Distributive property](http://en.wikipedia.org/wiki/Distributive_property)
 
### Examples

```
>> Distribute((a+b)*(x+y+z))
a*x+a*y+a*z+b*x+b*y+B*z
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Distribute](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Algebra.java#L1211) 
