## FunctionURL

```
FunctionURL(built-in-symbol) 
```

> returns the GitHub URL of the `built-in-symbol` implementation in the [Symja GitHub repository](https://github.com/axkr/symja_android_library). 

### Examples

Get the GitHub URL of the `NIntegrate` function implementation:

```
>> FunctionURL(NIntegrate)
https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/NIntegrate.java#L71
```

### Github

* [Implementation of FunctionURL](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/SourceCodeFunctions.java#L62) 
