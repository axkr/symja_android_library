## Prime

```
Prime(n)
```

> returns the `n`th prime number.
 
See
* [Wikipedia - Prime number](https://en.wikipedia.org/wiki/Prime_number)
* [Wikipedia - Prime number theorem](https://en.wikipedia.org/wiki/Prime_number_theorem)
* [Wikipedia - Bertrand's postulate](https://en.wikipedia.org/wiki/Bertrand's_postulate)

### Examples

Note that the first prime is 2, not 1:
 
```
>> Prime(1)
2

>> Prime(167)
991
```

When given a list of integers, a list is returned:

```
>> Prime({5, 10, 15})
{11,29,47}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Prime](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L4243) 
