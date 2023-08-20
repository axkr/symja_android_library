## In

```
In(k)
```

> gives the `k`th line of input.

### Examples

```
>> x = 1
1

>> x = x + 1
2

>> Do(In(2), {3})
    
>> x
5

>> In(-1)
5

>> Definition(In)
Attributes(In)={Listable,NHoldFirst,Protected}
In(1):=x=1
In(2):=x=x+1
In(3):=Do(In(2),{3})
In(4):=x
In(5):=In(-1)
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of In](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/In.java#L17) 
