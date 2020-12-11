## Names

```
Names(string) 
```

or

```
Names(pattern) 
```

> return the symbols from the context path matching the `string` or `pattern`.
 
 
### Examples

```
>> sysnames = Names("System`*"); 
			 
>> Select(sysnames, MemberQ(Attributes(#), OneIdentity) &) // InputForm 
{"And","Composition","Dot","GCD","Intersection","Join","Max","Min","Or","Plus","Power","StringExpression","StringJoin","TensorProduct","Times","Union","Xor"}
```