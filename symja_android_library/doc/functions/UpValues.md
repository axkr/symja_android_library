## UpValues 

```
UpValues(symbol)
```
> prints the up-value rules associated with `symbol`.
  
### Examples

``` 
>> u /: v(x_u) := {x}

>> UpValues(u) 
{HoldPattern(v(x_u)):>{x}}
```
