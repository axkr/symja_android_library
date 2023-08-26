## Message

```
Message(symbol::msg, expr1, expr2, ...)
```

> displays the specified message, replacing placeholders in the message text with the corresponding expressions.

### Examples

```
>> a::b = "Hello world!"
Hello world!
    
>> Message(a::b)
a: Hello world!
    
>> a::c := "Hello `1`, Mr 00`2`!"
    
>> Message(a::c, "you", 3 + 4)
a: Hello you, Mr 007!  
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Message](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IOFunctions.java#L289) 
