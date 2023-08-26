## PrependTo

```
PrependTo(s, item)
```

> prepend `item` to value of `s` and sets `s` to the result.

### Examples

Assign `s` to a list  

```
>> s = {1, 2, 4, 9}    
{1,2,4,9}   
```

Add a new value at the beginning of the list:   

```
>> PrependTo(s, 0)    
{0,1,2,4,9}   
```
 
The value assigned to `s` has changed:     

```
>> s    
{0,1,2,4,9}
```
 
`PrependTo` works with a head other than `List`:

```
>> y = f(a, b, c)    
>> PrependTo(y, x)    
f(x,a,b,c)  
 
>> y    
f(x,a,b,c) 
```

{a, b} is not a variable with a value, so its value cannot be changed. 

```
>> PrependTo({a, b}, 1)    
PrependTo({a,b},1)   
```

a is not a variable with a value, so its value cannot be changed.

```
>> PrependTo(a, b)     
PrependTo(a,b)

>> x = 1 + 2    
3
```

Nonatomic expression expected at position `1` in `PrependTo`

```
>> PrependTo(x, {3, 4})      
PrependTo(x,{3,4})    
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of PrependTo](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L5331) 
