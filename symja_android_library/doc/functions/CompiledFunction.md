## CompiledFunction

```
CompiledFunction(...)
```

> represents a binary Java coded function. 


The Wolfram Language's own serialized form of a compiled function is called through the
uncompiled `Function` it embeds:

```
>> cf = CompiledFunction({7, 7.0, 42}, {_Integer, _Real}, {{2, 0, 0}}, {{}}, {0, 1, 2, 0, 0}, {{1}}, Function({a, b}, N(a) + b), Evaluate);

>> cf(3, 0.25)
3.25
```

### Related terms 
[Compile](Compile.md), [CompilePrint](CompilePrint.md) 
 






### Implementation status

* &#x2615; - supported on Java virtual machine 

### Github

* [Implementation of CompiledFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-compile/src/main/java/org/matheclipse/compile/builtin/CompilerFunctions.java#L168) 
