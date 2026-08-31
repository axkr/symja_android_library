## Compile

```
Compile(list-of-arguments}, expression)
```

> compile the `expression` into a Java function, which has the arguments defined in `list-of-arguments` and return the compiled result in an `CompiledFunction` expression. 


### Examples

Compile the expression into a `CompiledFunction` and assign it to `f`:

```
>> f=Compile({{x, _Real}}, E^3-Cos(Pi^2/x));

>> f(1.4567) 
19.20421

>> f = Compile({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p));

>> f(4)
```

### Options

| option | default | |
|---|---|---|
| [RuntimeAttributes](RuntimeAttributes.md) | `{}` | attributes the compiled function is evaluated with |
| [RuntimeOptions](RuntimeOptions.md) | `Automatic` | how the compiled function behaves while it runs |
| [CompilationOptions](CompilationOptions.md) | `Automatic` | how the expression is compiled; inlines calls to other compiled functions |
| [CompilationTarget](CompilationTarget.md) | `"WVM"` | accepted and ignored |
| [Parallelization](Parallelization.md) | `Automatic` | accepted and ignored |

```
>> Compile({{x, _Real}}, x^2, RuntimeAttributes -> {Listable}, RuntimeOptions -> "Speed")[3.]
9.0
```

### Related terms 
[CompiledFunction](CompiledFunction.md), [CompilePrint](CompilePrint.md), [OptimizeExpression](OptimizeExpression.md), [CompilationOptions](CompilationOptions.md), [CompilationTarget](CompilationTarget.md), [Parallelization](Parallelization.md), [RuntimeAttributes](RuntimeAttributes.md), [RuntimeOptions](RuntimeOptions.md)






### Implementation status

* &#x2615; - supported on Java virtual machine 

### Github

* [Implementation of Compile](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/CompilerFunctions.java#L267) 
