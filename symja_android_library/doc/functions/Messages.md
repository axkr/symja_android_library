## Messages

```
Messages(symbol)
```

> return all messages which are asociated to `symbol`.

### Examples

```
>> a::hello:="Hello world"; a::james:="Hello `1`, Mr 00`2`!"

>> Messages(a) 
{HoldPattern(a::james):>Hello `1`, Mr 00`2`!,HoldPattern(a::hello):>Hello world}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Messages](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/IOFunctions.java#L322) 
