## GoldbachList

```
GoldbachList(even-number)
```

> return the list of Goldbach prime pairs for the `even-number`

```
GoldbachList(even-number, max-results)
```

> return `max-results` number of pairs. 

See
* [Wikipedia - Goldbach's_conjecture](https://en.wikipedia.org/wiki/Goldbach%27s_conjecture) 
 
### Examples

``` 
>> GoldbachList(64)
{{3,61},{5,59},{11,53},{17,47},{23,41}} 

>> GoldbachList(1000000000000000000,1)
{{11,999999999999999989}}
```

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of GoldbachList](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/NumberTheory.java#L3295) 
