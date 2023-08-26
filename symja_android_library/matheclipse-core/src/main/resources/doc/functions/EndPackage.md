## EndPackage 

```
EndPackage( )
```

> end a package definition

### Examples

``` 
>> BeginPackage("Test`")

>> $ContextPath
{Test`,System`}

>> EndPackage( )

>> $ContextPath
{Test`,System`,Global`}
```






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of EndPackage](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java#L476) 
