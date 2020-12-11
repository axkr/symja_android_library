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
