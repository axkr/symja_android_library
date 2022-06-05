## BooleanFunction

```
BooleanFunction(n, number-of-variables)
```

> create the `n`-th boolean function containing the `number-of-variables`. The `i`-th variable is represented by the `i`-th slot.

```
BooleanFunction({{b11, b12,...}->True,{b21, b22,...}->True,...}, {v1,v2,...})
```

> create the boolean function by defining all possible combinations `{bx1, bx2,...}` for obtaining `True` for the variable names `{v1, v2,...}`
 
### Examples

``` 
>> f=BooleanFunction(42,3); 
 
>> f(True,False,True) 
True

>> f(True,False,False) 
False 

>> BooleanConvert(f, "DNF") 
(!#1&&#3)||(!#2&&#3)&

>> BooleanConvert(BooleanFunction({{False, False} -> False,{False, True} -> False,{True, False} -> False,{True, True} -> True})) 
x&&y

>> BooleanConvert(BooleanFunction({{False, False} -> False,{False, True} -> True,{True, False} -> True,{True, True} -> True}, {x,y})) 
x||y
        
```
