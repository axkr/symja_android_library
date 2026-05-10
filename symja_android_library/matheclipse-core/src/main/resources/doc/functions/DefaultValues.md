## DefaultValues

```
DefaultValues(symbol)
```

> `DefaultValues` returns the default values associated with the `symbol`.

### Examples

```
>> Default(tst)=42
42

>> DefaultValues(tst) 
{HoldPattern(Default(tst)):>42}

>> Default(xx1)=1;Default(xx2)=3;Default(xx3)=5;
 
>> DefaultValues /@ Names("xx*")
{{HoldPattern(Default(xx1)):>1},{HoldPattern(Default(xx3)):>5},{HoldPattern(Default(xx2)):>3}}
```
