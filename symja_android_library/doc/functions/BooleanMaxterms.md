## BooleaMaxterms

```
BooleaMaxterms({{b1,b2,...}}, {v1,v2,...})
```

> create the conjunction of the variables `{v1,v2,...}`.
 
### Examples

``` 
>> BooleanMaxterms({{1,1,1,1}}, {a, b, c,d}) 
a||b||c||d

>> BooleanMaxterms({{True,True,True,True}}, {a, b, c,d}) 
a||b||c||d
```
