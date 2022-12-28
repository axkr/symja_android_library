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
