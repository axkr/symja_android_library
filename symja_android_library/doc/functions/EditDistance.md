## EditDistance

``` 
EditDistance(a, b)
```
 
> returns the Levenshtein distance of `a` and `b`, which is defined as the minimum number of insertions, deletions and substitutions on the constituents of `a` and `b` needed to transform one into the other.

See:
* [Wikipedia - Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance)
 

### Examples

```
>> EditDistance("kitten", "kitchen")
2

>> EditDistance("abc", "ac")
1
>> EditDistance("abc", "acb")
2

>> EditDistance("azbc", "abxyc")
3
```

The `IgnoreCase` option makes `EditDistance` ignore the case of letters:

```
>> EditDistance("time", "Thyme")
3

>> EditDistance("time", "Thyme", IgnoreCase -> True)
2
```
 






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EditDistance](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StringFunctions.java#L1022) 
