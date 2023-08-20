## MessageName

```
MessageName(symbol, msg)
```

or

```
symbol::msg
```

> `symbol::msg` identifies a message. `MessageName` is the head of message IDs of the form `symbol::tag`.

### Examples

The second parameter 'tag' is interpreted as a string.

```
>> FullForm(a::b)
MessageName(a, b)

>> FullForm(a::"b")
MessageName(a, "b")
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of MessageName](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1028) 
