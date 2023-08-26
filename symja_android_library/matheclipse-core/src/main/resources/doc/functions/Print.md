## Print

```
Print(expr)
```

> print the `expr` to the default output stream and return `Null`.
  

### Examples

``` 
>> Do(Print(i0);If(i0>4,Return(toobig)), {i0,1,10}) 
```

prints these numbers

```
1
2
3
4
5
```

and returns

```
toobig 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Print](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IOFunctions.java#L421) 
