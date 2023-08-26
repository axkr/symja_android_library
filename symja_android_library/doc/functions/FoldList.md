## FoldList

```
FoldList[f, x, {a, b}]
```

> returns `{x, f[x, a], f[f[x, a], b]}`

### Examples
 
[A002110 Primorial numbers](https://oeis.org/A002110): product of first n primes.

```
>> FoldList(Times, 1, Prime(Range(20)))
{1,2,6,30,210,2310,30030,510510,9699690,223092870,6469693230,200560490130,
7420738134810,304250263527210,13082761331670030,614889782588491410,
32589158477190044730,1922760350154212639070,117288381359406970983270,
7858321551080267055879090,557940830126698960967415390}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of FoldList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L3215) 
