## Compile

```
Compile(list-of-arguments}, expression)
```

> compile the `expression` into a Java function, which has the arguments defined in `list-of-arguments` and return the compile result in an `CompiledFunction` expression. You have to run Symja from a Java Development Kit (JDK) to compile to Java binary code.


### Examples

Compile the expression into a `CompiledFunction` and assign it to `f`:

```
>> f=Compile({x, _Real}, E^3-Cos(Pi^2/x));

>> f(1.4567) 
19.20421
```

```
>> f = Compile({{n, _Integer}}, Module({p = Range(n),i,x,t}, Do(x = RandomInteger({1,i}); t = p[[i]]; p[[i]] = p[[x]]; p[[x]] = t,{i,n,2,-1}); p));

>> f(4)
```

### Related terms 
[CompiledFunction](CompiledFunction.md), [CompilePrint](CompilePrint.md), [OptimizeExpression](OptimizeExpression.md)