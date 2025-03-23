## CompilePrint

```
CompilePrint(list-of-arguments}, expression)
```

> compile the `expression` into a Java function and return the corresponding Java source code function, which has the arguments defined in `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to Java binary code.


### Examples

Compile the expression into a `CompiledFunction` and assign it to `f`:

```
>> f=CompilePrint({x, _Real}, E^3-Cos(Pi^2/x)) 
```

```
>> f = CompilePrint({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p))
```

### Related terms 
[Compile](Compile.md), [CompiledFunction](CompiledFunction.md), [OptimizeExpression](OptimizeExpression.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of CompilePrint](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CompilerFunctions.java#L732) 
