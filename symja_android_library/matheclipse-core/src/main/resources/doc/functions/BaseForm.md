## BaseForm
 
```
BaseForm(integer, radix)
```

> prints the `integer` number in base `radix` form.

See:  
* [Wikipedia - Positional notation - Base conversion](https://en.wikipedia.org/wiki/Positional_notation#Base_conversion) 
* [Wikipedia - Hexadecimal](https://en.wikipedia.org/wiki/Hexadecimal)

### Examples 
 
In the Android interface the following output looks like 

$${\textnormal{abcdefff}}_{16}$$

```
>> BaseForm(2882400255, 16) 
Subscript("abcdefff",16) 

>> 16^^abcdefff
2882400255 
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of BaseForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L156) 
