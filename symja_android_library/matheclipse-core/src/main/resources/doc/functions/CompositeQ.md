## CompositeQ

```
CompositeQ(n)
```

> returns `True` if `n` is a composite integer number.   
 
For very large numbers, `CompositeQ` uses [probabilistic prime testing](https://en.wikipedia.org/wiki/Prime_number#Primality_testing_versus_primality_proving), so it might be wrong sometimes (i.e. a number might be non composite even though `CompositeQ` says it is).

See
* [Wikipedia - Prime number](https://en.wikipedia.org/wiki/Prime_number) 

### Examples

```
>> Select(Range(100), CompositeQ)
{4,6,8,9,10,12,14,15,16,18,20,21,22,24,25,26,27,28,30,32,33,34,35,36,38,39,40,42,
44,45,46,48,49,50,51,52,54,55,56,57,58,60,62,63,64,65,66,68,69,70,72,74,75,76,77,
78,80,81,82,84,85,86,87,88,90,91,92,93,94,95,96,98,99,100}
```

`CompositeQ` has attribute `Listable`: 

```
>> CompositeQ(Range(20)) 
{False,False,False,True,False,True,False,True,True,True,False,True,False,True,True,True,False,True,False,True}
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CompositeQ](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PredicateQ.java#L318) 
