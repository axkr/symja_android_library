## Commonest 

``` 
Commonest(data-values-list)
```

> the mode of a list of data values is the value that appears most often.

``` 
Commonest(data-values-list, n)
```

> return the `n` values that appears most often.

See 
* [Wikipedia - Mode (statistics)](https://en.wikipedia.org/wiki/Mode_(statistics)) 

### Examples

```
>> Commonest({1, 3, 6, 6, 6, 6, 7, 7, 12, 12, 17}) 
{6}
```

Given the list of data `{1, 1, 2, 4, 4}` the mode is not unique – the dataset may be said to be **bimodal**, while a set with more than two modes may be described as **multimodal**. 
 
```
>> Commonest({1, 1, 2, 4, 4}) 
{1,4}
```

### Related terms 
[Counts](Counts.md), [Tally](Tally.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Commonest](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L1488) 
