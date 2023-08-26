## Partition

```
Partition(list, n)
```

> partitions `list` into sublists of length `n`.

``` 
Partition(list, n, d)
```

> partitions `list` into sublists of length `n` which overlap `d` indices.

See
* [Wikipedia - Partition of a set](https://en.wikipedia.org/wiki/Partition_of_a_set)

### Examples

``` 
>> Partition({a, b, c, d, e, f}, 2)
{{a,b},{c,d},{e,f}}
 
>> Partition({a, b, c, d, e, f}, 3, 1)
{{a,b,c},{b,c,d},{c,d,e},{d,e,f}} 
 
>> Partition({a, b, c, d, e}, 2)
{{a,b},{c,d}}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Partition](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1475) 
