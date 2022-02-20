## Curl

```
Curl({f1, f2}, {x1, x2})
```

```
Curl({f1, f2, f3}, {x1, x2, x3})
```

> gives the curl.
 

See:  
* [Wikipedia - Curl (mathematics)](http://en.wikipedia.org/wiki/Curl_%28mathematics%29)

### Examples
```
>> Curl({f(u,v,w),f(v,w,u),f(w,u,v),f(x)}, {u,v,w})
{-D(f(v,w,u),w)+D(f(w,u,v),v),-D(f(w,u,v),u)+D(f(u,v,w),w),-D(f(u,v,w),v)+D(f(v,w,u),u),f(x)}
```


### Github

* [Implementation of Curl](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/VectorAnalysisFunctions.java#L57) 
