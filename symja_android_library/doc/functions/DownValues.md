## DownValues 

```
DownValues(symbol)
```
> prints the down-value rules associated with `symbol`.
  
### Examples

``` 
>> f(1)=3
3

>> f(x_):=x^3

>> DownValues(f) 
{HoldPattern(f(1)):>3,HoldPattern(f(x_)):>x^3}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of DownValues](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L591) 
