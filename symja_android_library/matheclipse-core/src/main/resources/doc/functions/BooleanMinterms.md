## BooleanMinterms

```
BooleanMinterms({{b1,b2,...}}, {v1,v2,...})
```

> create the disjunction of the variables `{v1,v2,...}`.
 
### Examples

``` 
>> BooleanMinterms({{1,1,1,1}}, {a, b, c,d}) 
a&&b&&c&&d 
        
>> BooleanMinterms({{True,True,True,True}}, {a, b, c,d}) 
a&&b&&c&&d 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BooleanMinterms](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/BooleanFunctions.java#L1308) 
