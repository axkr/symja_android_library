## Begin 

```
Begin("<context-name>")
```

> start a new context definition

### Examples

``` 
>> Begin("mytest`") 

>> Context()
mytest`

>> $ContextPath
{System`,Global`}

>> End()
mytest`

>> Context()
Global`

>> $ContextPath
{System`,Global`}

```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Begin](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L107) 
