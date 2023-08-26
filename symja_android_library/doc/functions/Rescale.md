## Rescale

```
Rescale(list)
```

> returns `Rescale(list,{Min(list), Max(list)})`. 

```
Rescale(x,{xmin, xmax})
```

> returns `x/(xmax-xmin)-xmin/(xmax-xmin)`. 
   
```
Rescale(x,{xmin, xmax},{ymin, ymax})
```

> returns `(x*(ymax-ymin))/(xmax-xmin)-(xmin*ymax-xmax*ymin)/(xmax-xmin)`. 

### Examples

``` 
>> Rescale({a,b})
{a/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b)),b/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b))}

>> Rescale({1, 2, 3, 4, 5}, {-100, 100})
{101/200,51/100,103/200,13/25,21/40}

>> Rescale(x,{xmin, xmax},{ymin, ymax})
(x*(ymax-ymin))/(xmax-xmin)-(xmin*ymax-xmax*ymin)/(xmax-xmin)
```







### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Rescale](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/StatisticsFunctions.java#L6528) 
