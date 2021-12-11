## FullForm

```
FullForm(expression) 
```

> shows the internal representation of the given `expression`.

### Examples

FullForm shows the difference in the internal expression representation for the `Times` operator:

```  
>> FullForm(x(x+1))
x(Plus(1, x))

>> FullForm(x*(x+1))
Times(x, Plus(1, x))
```

### Related terms 
[LeafCount](LeafCount.md) 

### Github

* [Implementation of FullForm](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/OutputFunctions.java#L149) 
